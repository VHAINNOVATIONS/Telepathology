/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 4, 2008
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
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
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.ImageAccessLogEvent;
import gov.va.med.imaging.exchange.ImageAccessLogEvent.ImageAccessLogEventType;
import gov.va.med.imaging.exchange.business.Image;
import gov.va.med.imaging.exchange.business.Patient;
import gov.va.med.imaging.exchange.business.Series;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.exchange.business.Patient.PatientSex;
import gov.va.med.imaging.exchange.enums.ObjectOrigin;
import gov.va.med.imaging.exchange.enums.StudyDeletedImageState;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.federation.webservices.types.FederationFilterTypeOrigin;
import gov.va.med.imaging.federation.webservices.types.FederationImageAccessLogEventType;
import gov.va.med.imaging.federation.webservices.types.FederationImageAccessLogEventTypeEventType;
import gov.va.med.imaging.federation.webservices.types.ObjectOriginType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

/**
 * @author VHAISWWERFEJ
 *
 */
public class FederationDatasourceTranslator 
extends AbstractFederationDatasourceTranslator
{
	private final static Logger logger = Logger.getLogger(FederationDatasourceTranslator.class);
	
	public FederationDatasourceTranslator()
	{
		super();
	}
	
	public SortedSet<Study> transformStudies(gov.va.med.imaging.federation.webservices.types.FederationStudyType [] studies,
			StudyFilter studyFilter)
	{
		SortedSet<Study> result = new TreeSet<Study>();	
		if(studies == null)
			return result;
		for(int i = 0; i < studies.length; i++)
		{
			gov.va.med.imaging.federation.webservices.types.FederationStudyType studyType = studies[i];
			boolean useStudy = true;
			// JMW 6/1/2010 - need to translate first to get the proper URN to determine if allowed to include in result
			Study study = transformStudy(studyType);
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
	
	public Study transformStudy(gov.va.med.imaging.federation.webservices.types.FederationStudyType studyType)
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
			getLogger().error("Unable to create a Study from the given key elements", x);
		}
		study.setDescription(studyType.getDescription() == null ? "" : studyType.getDescription());
		study.setStudyUid(studyType.getDicomUid());
		study.setImageCount(studyType.getImageCount());
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
		
		if(studyType.getComponentSeries() != null)
		{
			gov.va.med.imaging.federation.webservices.types.FederationStudyTypeComponentSeries serieses = studyType.getComponentSeries();
			gov.va.med.imaging.federation.webservices.types.FederationSeriesType []series = serieses.getSeries();
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
		
		if(firstImage != null)
		{
			study.setFirstImage(firstImage);
			study.setFirstImageIen(firstImage.getIen());
		}
		
		return study;
	}
	
	private Series transformSeries(gov.va.med.imaging.federation.webservices.types.FederationSeriesType seriesType)
	{
		if(seriesType == null)
			return null;
		Series series = new Series();
		series.setSeriesIen(seriesType.getSeriesId());
		series.setSeriesNumber(seriesType.getDicomSeriesNumber() + "");
		series.setSeriesUid(seriesType.getDicomUid() == null ? "" : seriesType.getDicomUid());
		series.setObjectOrigin(transformObjectOrigin(seriesType.getObjectOrigin()));
		series.setModality(seriesType.getSeriesModality());
		
		gov.va.med.imaging.federation.webservices.types.FederationInstanceType [] instances = seriesType.getComponentInstances().getInstance();
		if(instances != null) {
			for(int i = 0; i < instances.length; i++) {
				Image image = transformImage(instances[i]);//, series, studyType);
				series.addImage(image);
			}
		}		
		return series;
	}
	
	private Image transformImage(gov.va.med.imaging.federation.webservices.types.FederationInstanceType instanceType)
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
			logger.error("Unable to create an Image instance from the given key elements.");
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
	
	public ObjectOrigin transformObjectOrigin(
			gov.va.med.imaging.federation.webservices.types.ObjectOriginType objectOriginType)
	{
		if(ObjectOriginType._DOD.equals(objectOriginType.getValue()))
		{
			return ObjectOrigin.DOD;
		}
		return ObjectOrigin.VA;
	}

	public FederationFilterTypeOrigin transformOrigin(String filterOrigin)
	{
		if("VA".equalsIgnoreCase(filterOrigin))
		{
			return FederationFilterTypeOrigin.value2;
		}
		else if("DOD".equalsIgnoreCase(filterOrigin))
		{
			return FederationFilterTypeOrigin.value4;
		}
		else if("NON-VA".equalsIgnoreCase(filterOrigin))
		{
			return FederationFilterTypeOrigin.value3;
		}
		else if("FEE".equalsIgnoreCase(filterOrigin))
		{
			return FederationFilterTypeOrigin.value5;
		}
		else
		{
			return FederationFilterTypeOrigin.value1;
		}
	}
	
	public FederationImageAccessLogEventType transformLogEvent(ImageAccessLogEvent logEvent)
	{
		FederationImageAccessLogEventType result = new FederationImageAccessLogEventType();
		result.setImageId(logEvent.getImageIen());
		result.setPatientIcn(logEvent.getPatientIcn());
		result.setReason(logEvent.getReasonCode());
		result.setSiteNumber(logEvent.getSiteNumber());
		result.setEventType(transformLogEventType(logEvent.getEventType()));
		result.setUserSiteNumber(logEvent.getUserSiteNumber());
		return result;
	}
	
	public FederationImageAccessLogEventTypeEventType transformLogEventType(ImageAccessLogEventType eventType)
	{
		if(eventType == ImageAccessLogEventType.IMAGE_COPY)
		{
			return FederationImageAccessLogEventTypeEventType.IMAGE_COPY;
		}
		else if(eventType == ImageAccessLogEventType.IMAGE_PRINT)
		{
			return FederationImageAccessLogEventTypeEventType.IMAGE_PRINT;
		}
		else if(eventType == ImageAccessLogEventType.PATIENT_ID_MISMATCH)
		{
			return FederationImageAccessLogEventTypeEventType.PATIENT_ID_MISMATCH;
		}
		return FederationImageAccessLogEventTypeEventType.IMAGE_ACCESS;			
	}
	
	public SortedSet<Patient> transformPatients(gov.va.med.imaging.federation.webservices.types.PatientType [] patients)
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
				logger.error("Parse Exception converting patient", pX);
			}
		}		
		return result;
	}
	
	public Patient transformPatient(gov.va.med.imaging.federation.webservices.types.PatientType patientType)
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
	
	private PatientSex transformPatientSex(gov.va.med.imaging.federation.webservices.types.FederationPatientSexType patientSexType)
	{
		if(gov.va.med.imaging.federation.webservices.types.FederationPatientSexType._FEMALE.equals(patientSexType)){
			return PatientSex.Female;
		}
		else if(gov.va.med.imaging.federation.webservices.types.FederationPatientSexType._MALE.equals(patientSexType))
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
}
