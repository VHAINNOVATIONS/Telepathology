/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 4, 2009
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswwerfej
  Description: 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+

 */
package gov.va.med.imaging.federation.translator;

import gov.va.med.PatientIdentifier;
import gov.va.med.imaging.core.interfaces.exceptions.InsufficientPatientSensitivityException;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.ImageAccessLogEvent;
import gov.va.med.imaging.exchange.ImageAccessLogEvent.ImageAccessLogEventType;
import gov.va.med.imaging.exchange.business.Image;
import gov.va.med.imaging.exchange.business.Patient;
import gov.va.med.imaging.exchange.business.PatientSensitiveValue;
import gov.va.med.imaging.exchange.business.Series;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.exchange.business.Patient.PatientSex;
import gov.va.med.imaging.exchange.enums.ObjectOrigin;
import gov.va.med.imaging.exchange.enums.PatientSensitivityLevel;
import gov.va.med.imaging.exchange.enums.StudyDeletedImageState;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author vhaiswwerfej
 *
 */
public class FederationDatasourceTranslatorV2 
extends AbstractFederationDatasourceTranslator
{
	public SortedSet<Study> transformStudies(gov.va.med.imaging.federation.webservices.types.v2.StudiesType studiesType,
		StudyFilter studyFilter, String patientIcn, StudyLoadLevel studyLoadLevel)
	throws InsufficientPatientSensitivityException
	{
		if(studiesType.getError() != null)
		{
			getLogger().info("Study result contains error message, converting into InsufficientPatientSensitivityException");
			// should look at studiesType.getError().getStudiesError() to determine if actually sensitivity error type, for now always will be
			gov.va.med.imaging.federation.webservices.types.v2.FederationStudiesErrorMessageType errorMessageType = 
				studiesType.getError();
			PatientSensitivityLevel sensitiveLevel = PatientSensitivityLevel.getPatientSensitivityLevel(errorMessageType.getErrorCode().intValue());
			PatientSensitiveValue sensitiveValue = new PatientSensitiveValue(sensitiveLevel, errorMessageType.getErrorMessage());
			InsufficientPatientSensitivityException ipsX = 
				InsufficientPatientSensitivityException.createInsufficientPatientSensitivityException(sensitiveValue, 
						PatientIdentifier.icnPatientIdentifier(patientIcn), studyFilter.getMaximumAllowedLevel());
			throw ipsX;
		}
		TransactionContext transactionContext = TransactionContextFactory.get();
		getLogger().info("Transaction [" + transactionContext.getTransactionId() + "] returned " + 
				(studiesType.getStudy() == null ? 0 : studiesType.getStudy().length) + 
				" studies");
		return transformStudies(studiesType.getStudy(), studyFilter, studyLoadLevel);
	}
	
	private SortedSet<Study> transformStudies(gov.va.med.imaging.federation.webservices.types.v2.FederationStudyType [] studies,
			StudyFilter studyFilter, StudyLoadLevel studyLoadLevel)
	{
		SortedSet<Study> result = new TreeSet<Study>();	
		if(studies == null)
			return result;
		for(int i = 0; i < studies.length; i++)
		{
			gov.va.med.imaging.federation.webservices.types.v2.FederationStudyType studyType = studies[i];
			boolean useStudy = true;
			// JMW 6/1/2010 - need to translate first to get the proper URN to determine if allowed to include in result
			Study study = transformStudy(studyType, studyLoadLevel);
			if(studyFilter.isStudyIenSpecified())
			{
				if(!studyFilter.isAllowableStudyId(study.getGlobalArtifactIdentifier()))
				{
					useStudy = false;
				}
			}
			if(useStudy)
			{
				result.add(study);
			}
		}		
		// JMW 1/5/2009 - if the study IEN is not specified then the results need to be filtered.
		// call the pre and post filter since the pre filter cannot run on the Federation Datasource before calling the DS
		// both need to be run depending on the creator - might have different functionality on the filter functions
		if(!studyFilter.isStudyIenSpecified())
		{
			studyFilter.preFilter(result);
			studyFilter.postFilter(result);
		}
		return result;
	}
	
	public Study transformStudy(gov.va.med.imaging.federation.webservices.types.v2.FederationStudyType studyType, 
			StudyLoadLevel studyLoadLevel)
	{
		Study study = null;
		String studyId = studyType.getStudyId();
		ObjectOrigin studyOrigin = transformObjectOrigin(studyType.getObjectOrigin());
		try
		{
			study = Study.create(studyOrigin, studyType.getSiteNumber(), studyId, 
					PatientIdentifier.icnPatientIdentifier(studyType.getPatientIcn()), 
					StudyLoadLevel.FULL, StudyDeletedImageState.cannotIncludeDeletedImages);
		}
		catch (URNFormatException x)
		{
			getLogger().error("Unable to create a Study from the given key elements");
		}
		study.setDescription(studyType.getDescription() == null ? "" : studyType.getDescription());
		study.setStudyUid(studyType.getDicomUid());
		study.setImageCount(studyType.getImageCount());
		//study.setPatientIcn(studyType.getPatientIcn());
		if (studyType.getPatientName() == null)
		{
			studyType.setPatientName("");
		}
		study.setPatientName(studyType.getPatientName().replaceAll("\\^", " "));
		
		study.setProcedureDate(convertDICOMDateToDate(studyType.getProcedureDate()));
		study.setProcedure(studyType.getProcedureDescription() == null ? "" : studyType.getProcedureDescription());
		study.setRadiologyReport((studyType.getRadiologyReport() == null ? "" : studyType.getRadiologyReport()));
		
		study.setSiteName(studyType.getSiteName() == null ? "" : studyType.getSiteName());
		study.setSpecialty(studyType.getSpecialtyDescription() == null ? "" : studyType.getSpecialtyDescription());

		study.setOrigin(studyType.getOrigin());
		study.setSiteAbbr(studyType.getSiteAbbreviation());
		if(studyType.getStudyModalities() != null)
		{			
			String[] modalities = studyType.getStudyModalities().getModality();
			if(modalities != null)
			{
				for(int i = 0; i < modalities.length; i++)
				{
					study.addModality(modalities[i]);
				}
			}
		}
		
		// Display specific fields
		study.setNoteTitle(studyType.getNoteTitle());
		study.setImagePackage(studyType.getImagePackage());
		study.setImageType(studyType.getImageType());
		study.setEvent(studyType.getEvent());
		study.setOrigin(studyType.getOrigin());
		study.setImagePackage(studyType.getImagePackage());
		study.setStudyClass(studyType.getStudyClass());
		study.setCaptureDate(studyType.getCaptureDate());
		study.setCaptureBy(studyType.getCapturedBy());
		study.setRpcResponseMsg(studyType.getRpcResponseMsg());
		study.setErrorMessage(studyType.getErrorMessage() == null ? "" : studyType.getErrorMessage());
		Image firstImage = null;
		
		firstImage = transformImage(studyType.getFirstImage());
		
		if(studyType.getComponentSeries() != null)
		{
			gov.va.med.imaging.federation.webservices.types.v2.FederationStudyTypeComponentSeries serieses = studyType.getComponentSeries();
			gov.va.med.imaging.federation.webservices.types.v2.FederationSeriesType []series = serieses.getSeries();
			if(series != null)
			{			
				for(int i = 0; i < series.length; i++)
				{				
					Series ser = transformSeries(series[i]);
					study.addSeries(ser);
					if(firstImage == null)
					{
						Iterator<Image> iter = ser.iterator();
						if(iter.hasNext())
							firstImage = iter.next();
					}
				}
			}
		}
		
		
		if(firstImage != null)
		{
			study.setFirstImage(firstImage);
			study.setFirstImageIen(firstImage.getIen());
		}
		
		return study;
	}
	
	private Series transformSeries(gov.va.med.imaging.federation.webservices.types.v2.FederationSeriesType seriesType)
	{
		if(seriesType == null)
			return null;
		Series series = new Series();
		series.setSeriesIen(seriesType.getSeriesId());
		series.setSeriesNumber(seriesType.getDicomSeriesNumber() + "");
		series.setSeriesUid(seriesType.getDicomUid() == null ? "" : seriesType.getDicomUid());
		series.setObjectOrigin(transformObjectOrigin(seriesType.getObjectOrigin()));
		series.setModality(seriesType.getSeriesModality());
		
		gov.va.med.imaging.federation.webservices.types.v2.FederationInstanceType [] instances = seriesType.getComponentInstances().getInstance();
		if(instances != null) {
			for(int i = 0; i < instances.length; i++) {
				Image image = transformImage(instances[i]);//, series, studyType);
				series.addImage(image);
			}
		}		
		return series;
	}
	
	private Image transformImage(gov.va.med.imaging.federation.webservices.types.v2.FederationInstanceType instanceType)
	{
		if(instanceType == null)
			return null;		
		Image image = null;
		try
		{
			image = Image.create(
				instanceType.getSiteNumber(), 
				instanceType.getImageId(), 
				instanceType.getGroupId() == null ? instanceType.getStudyId() : instanceType.getGroupId(),
				PatientIdentifier.icnPatientIdentifier(instanceType.getPatientIcn()), 
				instanceType.getImageModality() 
			);
		}
		catch (URNFormatException x)
		{
			getLogger().error("Unable to create an Image instance from the given key elements.");
			return null;
		}
		
		image.setImageNumber(instanceType.getImageNumber() == null ? "" : instanceType.getImageNumber() + "");
		//image.setIen(instanceType.getImageId());
		image.setImageUid(instanceType.getDicomUid() == null ? "" : instanceType.getDicomUid());
		image.setDescription(instanceType.getDescription() == null ? "" : instanceType.getDescription());
		
		image.setPatientName(instanceType.getPatientName() == null ? "" : instanceType.getPatientName().replaceAll("\\^", " "));
		image.setProcedureDate(convertDICOMDateToDate(instanceType.getProcedureDate()));
		image.setProcedure(instanceType.getProcedure() == null ? "" : instanceType.getProcedure());
		image.setSiteAbbr(instanceType.getSiteAbbr());
		image.setObjectOrigin(transformObjectOrigin(instanceType.getObjectOrigin()));
		//image.setSiteNumber(instanceType.getSiteNumber());
		image.setFullLocation(instanceType.getFullLocation());
		image.setFullFilename(instanceType.getFullImageFilename());
		image.setAbsLocation(instanceType.getAbsLocation());
		image.setAbsFilename(instanceType.getAbsImageFilename());
		image.setDicomImageNumberForDisplay(instanceType.getDicomImageNumberForDisplay() == null ? "" : instanceType.getDicomImageNumberForDisplay() + "");
		image.setDicomSequenceNumberForDisplay(instanceType.getDicomSequenceNumberForDisplay() == null ? "" : instanceType.getDicomSequenceNumberForDisplay() + "");
		image.setImgType(instanceType.getImageType().intValue());
		image.setImageClass(instanceType.getImageClass());
		image.setBigFilename(instanceType.getBigImageFilename());
		image.setQaMessage(instanceType.getQaMessage());
		image.setErrorMessage(instanceType.getErrorMessage() == null ? "" : instanceType.getErrorMessage());
		return image;
	}
	
	public gov.va.med.imaging.federation.webservices.types.v2.FederationFilterTypeOrigin transformOrigin(
			String filterOrigin)
	{
		if("VA".equalsIgnoreCase(filterOrigin))
		{
			return gov.va.med.imaging.federation.webservices.types.v2.FederationFilterTypeOrigin.value2;
		}
		else if("DOD".equalsIgnoreCase(filterOrigin))
		{
			return gov.va.med.imaging.federation.webservices.types.v2.FederationFilterTypeOrigin.value4;
		}
		else if("NON-VA".equalsIgnoreCase(filterOrigin))
		{
			return gov.va.med.imaging.federation.webservices.types.v2.FederationFilterTypeOrigin.value3;
		}
		else if("FEE".equalsIgnoreCase(filterOrigin))
		{
			return gov.va.med.imaging.federation.webservices.types.v2.FederationFilterTypeOrigin.value5;
		}
		else
		{
			return gov.va.med.imaging.federation.webservices.types.v2.FederationFilterTypeOrigin.value1;
		}
	}
	
	public gov.va.med.imaging.federation.webservices.types.v2.FederationStudyLoadLevelType transformStudyLoadLevel(
		StudyLoadLevel studyLoadLevel)
	{
		if(studyLoadLevel == StudyLoadLevel.STUDY_ONLY)
			return gov.va.med.imaging.federation.webservices.types.v2.FederationStudyLoadLevelType.STUDY_ONLY;
		if(studyLoadLevel == StudyLoadLevel.STUDY_AND_REPORT)
			return gov.va.med.imaging.federation.webservices.types.v2.FederationStudyLoadLevelType.STUDY_AND_REPORT;
		return gov.va.med.imaging.federation.webservices.types.v2.FederationStudyLoadLevelType.FULL;			
	}
	
	private ObjectOrigin transformObjectOrigin(
			gov.va.med.imaging.federation.webservices.types.v2.ObjectOriginType objectOriginType)
	{
		if(gov.va.med.imaging.federation.webservices.types.v2.ObjectOriginType._DOD.equals(objectOriginType.getValue()))
		{
			return ObjectOrigin.DOD;
		}
		return ObjectOrigin.VA;
	}
	
	public SortedSet<Patient> transformPatients(gov.va.med.imaging.federation.webservices.types.v2.PatientType [] patients)
	{
		if(patients == null)
			return null;
		SortedSet<Patient> result = new TreeSet<Patient>();		
		for(int i = 0; i < patients.length; i++)
		{
			try
			{
				Patient patient = transformPatient(patients[i]);
				if(patient != null)
				{
					result.add(patient);	
				}
			}
			catch(ParseException pX)
			{
				getLogger().error("Parse Exception converting patient", pX);
			}
		}		
		return result;
	}
	
	private Patient transformPatient(gov.va.med.imaging.federation.webservices.types.v2.PatientType patientType)
	throws ParseException
	{
		Patient patient = null;
		if(patientType == null)
			return patient;
		patient = new Patient(patientType.getPatientName(), patientType.getPatientIcn(), 
				patientType.getVeteranStatus(), 
				transformPatientSex(patientType.getPatientSex()), 
				transformPatientDOB(patientType.getPatientDob()),
				null, null, null);
		return patient;
	}
	
	private PatientSex transformPatientSex(gov.va.med.imaging.federation.webservices.types.v2.FederationPatientSexType patientSexType)
	{
		if(gov.va.med.imaging.federation.webservices.types.v2.FederationPatientSexType._FEMALE.equals(patientSexType)){
			return PatientSex.Female;
		}
		else if(gov.va.med.imaging.federation.webservices.types.v2.FederationPatientSexType._MALE.equals(patientSexType))
		{
			return PatientSex.Male;
		}
		return PatientSex.Unknown;
	}
	
	private Date transformPatientDOB(String patientTypeDob)
	throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
		return sdf.parse(patientTypeDob);
	}
	
	public PatientSensitiveValue transformPatientSensitiveValue(
			gov.va.med.imaging.federation.webservices.types.v2.PatientSensitiveCheckResponseType responseType)
	{
		if(responseType == null)
			return null;
		String msg = responseType.getWarningMessage();
		PatientSensitivityLevel level = transformPatientSensitiveLevel(responseType.getPatientSensitivityLevel());
		
		return new PatientSensitiveValue(level, msg);
	}
	
	private PatientSensitivityLevel transformPatientSensitiveLevel(
			gov.va.med.imaging.federation.webservices.types.v2.PatientSensitivityLevelType sensitiveLevelType)
	{
		if(sensitiveLevelType == gov.va.med.imaging.federation.webservices.types.v2.PatientSensitivityLevelType.DISPLAY_WARNING)
		{
			return PatientSensitivityLevel.DISPLAY_WARNING;
		}
	
		else if(sensitiveLevelType == gov.va.med.imaging.federation.webservices.types.v2.PatientSensitivityLevelType.ACCESS_DENIED)
		{
			return PatientSensitivityLevel.ACCESS_DENIED;
		}
		else if(sensitiveLevelType == gov.va.med.imaging.federation.webservices.types.v2.PatientSensitivityLevelType.DISPLAY_WARNING_CANNOT_CONTINUE)
		{
			return PatientSensitivityLevel.DISPLAY_WARNING_CANNOT_CONTINUE;
		}
		else if(sensitiveLevelType == gov.va.med.imaging.federation.webservices.types.v2.PatientSensitivityLevelType.DISPLAY_WARNING_REQUIRE_OK)
		{
			return PatientSensitivityLevel.DISPLAY_WARNING_REQUIRE_OK;
		}
		else if(sensitiveLevelType == gov.va.med.imaging.federation.webservices.types.v2.PatientSensitivityLevelType.NO_ACTION_REQUIRED)
		{
			return PatientSensitivityLevel.NO_ACTION_REQUIRED;
		}
		else if(sensitiveLevelType == gov.va.med.imaging.federation.webservices.types.v2.PatientSensitivityLevelType.RPC_FAILURE)
		{
			return PatientSensitivityLevel.DATASOURCE_FAILURE;
		}
		return null;
	}
	
	public gov.va.med.imaging.federation.webservices.types.v2.FederationImageAccessLogEventType transformLogEvent(
			ImageAccessLogEvent logEvent)
	{
		gov.va.med.imaging.federation.webservices.types.v2.FederationImageAccessLogEventType result = 
			new gov.va.med.imaging.federation.webservices.types.v2.FederationImageAccessLogEventType();
		result.setImageId(logEvent.getImageIen());
		result.setPatientIcn(logEvent.getPatientIcn());
		result.setReason(logEvent.getReasonCode());
		result.setSiteNumber(logEvent.getSiteNumber());
		result.setEventType(transformLogEventType(logEvent.getEventType()));
		result.setUserSiteNumber(logEvent.getUserSiteNumber());
		return result;
	}
	
	private gov.va.med.imaging.federation.webservices.types.v2.FederationImageAccessLogEventTypeEventType transformLogEventType(
			ImageAccessLogEventType eventType)
	{
		if(eventType == ImageAccessLogEventType.IMAGE_COPY)
		{
			return gov.va.med.imaging.federation.webservices.types.v2.FederationImageAccessLogEventTypeEventType.IMAGE_COPY;
		}
		else if(eventType == ImageAccessLogEventType.IMAGE_PRINT)
		{
			return gov.va.med.imaging.federation.webservices.types.v2.FederationImageAccessLogEventTypeEventType.IMAGE_PRINT;
		}
		else if(eventType == ImageAccessLogEventType.PATIENT_ID_MISMATCH)
		{
			return gov.va.med.imaging.federation.webservices.types.v2.FederationImageAccessLogEventTypeEventType.PATIENT_ID_MISMATCH;
		}
		else if(eventType == ImageAccessLogEventType.RESTRICTED_ACCESS)
		{
			return gov.va.med.imaging.federation.webservices.types.v2.FederationImageAccessLogEventTypeEventType.RESTRICTED_ACCESS;
		}
		return gov.va.med.imaging.federation.webservices.types.v2.FederationImageAccessLogEventTypeEventType.IMAGE_ACCESS;			
	}

}
