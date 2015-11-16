/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 17, 2009
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
package gov.va.med.imaging.federation.webservices.translation.v3;

import gov.va.med.*;
import gov.va.med.exceptions.GlobalArtifactIdentifierFormatException;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.DateUtil;
import gov.va.med.imaging.DicomDateFormat;
import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.exchange.RoutingTokenHelper;
import gov.va.med.imaging.StudyURN;
import gov.va.med.imaging.artifactsource.ArtifactSource;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.InsufficientPatientSensitivityException;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.ImageAccessLogEvent;
import gov.va.med.imaging.exchange.ImageAccessLogEvent.ImageAccessLogEventType;
import gov.va.med.imaging.exchange.business.*;
import gov.va.med.imaging.exchange.business.Patient.PatientSex;
import gov.va.med.imaging.exchange.business.vistarad.ActiveExam;
import gov.va.med.imaging.exchange.business.vistarad.ActiveExams;
import gov.va.med.imaging.exchange.business.vistarad.Exam;
import gov.va.med.imaging.exchange.business.vistarad.ExamImage;
import gov.va.med.imaging.exchange.business.vistarad.ExamImages;
import gov.va.med.imaging.exchange.business.vistarad.ExamSite;
import gov.va.med.imaging.exchange.business.vistarad.PatientRegistration;
import gov.va.med.imaging.exchange.enums.ArtifactResultStatus;
import gov.va.med.imaging.exchange.enums.ObjectOrigin;
import gov.va.med.imaging.exchange.enums.PatientSensitivityLevel;
import gov.va.med.imaging.exchange.enums.StudyDeletedImageState;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.exchange.enums.vistarad.ExamStatus;
import gov.va.med.imaging.exchange.translation.AbstractTranslator;
import gov.va.med.imaging.exchange.translation.TranslationMethod;
import gov.va.med.imaging.exchange.translation.exceptions.TranslationException;
import gov.va.med.imaging.exchange.utility.Base32ConversionUtility;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.vista.StringUtils;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author vhaiswwerfej
 *
 */
public class Translator 
extends AbstractTranslator 
{
	private final static String federationWebserviceShortDateFormat = "MM/dd/yyyy";
	private static Map<PatientSex, gov.va.med.imaging.federation.webservices.types.v3.FederationPatientSexType> sexMap;
	private static Map<PassthroughParameterType, gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterTypeType> parameterTypeMap;
	private static Map<PatientSensitivityLevel, gov.va.med.imaging.federation.webservices.types.v3.PatientSensitivityLevelType> patientSensitivityMap;
	private static Map<StudyLoadLevel, gov.va.med.imaging.federation.webservices.types.v3.FederationStudyLoadLevelType> loadLevelMap;
	private static Map<ImageAccessLogEventType, gov.va.med.imaging.federation.webservices.types.v3.FederationImageAccessLogEventTypeEventType> imageAccessMap;
	private static Map<ExamStatus, gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamStatusType> examStatusMap;
	
	static
	{
		sexMap = new HashMap<PatientSex, gov.va.med.imaging.federation.webservices.types.v3.FederationPatientSexType>();
		sexMap.put( PatientSex.Female, gov.va.med.imaging.federation.webservices.types.v3.FederationPatientSexType.FEMALE );
		sexMap.put( PatientSex.Male, gov.va.med.imaging.federation.webservices.types.v3.FederationPatientSexType.MALE );
		sexMap.put( PatientSex.Unknown, gov.va.med.imaging.federation.webservices.types.v3.FederationPatientSexType.UNKNOWN );

		parameterTypeMap = new HashMap<PassthroughParameterType, gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterTypeType>();
		parameterTypeMap.put(PassthroughParameterType.literal, gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterTypeType.LITERAL);
		parameterTypeMap.put(PassthroughParameterType.list, gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterTypeType.LIST);
		parameterTypeMap.put(PassthroughParameterType.reference, gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterTypeType.REFERENCE);

		patientSensitivityMap = new HashMap<PatientSensitivityLevel, gov.va.med.imaging.federation.webservices.types.v3.PatientSensitivityLevelType>();
		patientSensitivityMap.put(PatientSensitivityLevel.DISPLAY_WARNING, gov.va.med.imaging.federation.webservices.types.v3.PatientSensitivityLevelType.DISPLAY_WARNING);
		patientSensitivityMap.put(PatientSensitivityLevel.ACCESS_DENIED, gov.va.med.imaging.federation.webservices.types.v3.PatientSensitivityLevelType.ACCESS_DENIED);
		patientSensitivityMap.put(PatientSensitivityLevel.DISPLAY_WARNING_CANNOT_CONTINUE, gov.va.med.imaging.federation.webservices.types.v3.PatientSensitivityLevelType.DISPLAY_WARNING_CANNOT_CONTINUE);
		patientSensitivityMap.put(PatientSensitivityLevel.DISPLAY_WARNING_REQUIRE_OK, gov.va.med.imaging.federation.webservices.types.v3.PatientSensitivityLevelType.DISPLAY_WARNING_REQUIRE_OK);
		patientSensitivityMap.put(PatientSensitivityLevel.NO_ACTION_REQUIRED, gov.va.med.imaging.federation.webservices.types.v3.PatientSensitivityLevelType.NO_ACTION_REQUIRED);
		patientSensitivityMap.put(PatientSensitivityLevel.DATASOURCE_FAILURE, gov.va.med.imaging.federation.webservices.types.v3.PatientSensitivityLevelType.RPC_FAILURE);
		
		loadLevelMap = new HashMap<StudyLoadLevel,gov.va.med.imaging.federation.webservices.types.v3.FederationStudyLoadLevelType>();
		loadLevelMap.put(StudyLoadLevel.FULL, gov.va.med.imaging.federation.webservices.types.v3.FederationStudyLoadLevelType.FULL);
		loadLevelMap.put(StudyLoadLevel.STUDY_AND_REPORT, gov.va.med.imaging.federation.webservices.types.v3.FederationStudyLoadLevelType.STUDY_AND_REPORT);
		loadLevelMap.put(StudyLoadLevel.STUDY_AND_IMAGES, gov.va.med.imaging.federation.webservices.types.v3.FederationStudyLoadLevelType.STUDY_AND_IMAGES_NO_REPORT);
		loadLevelMap.put(StudyLoadLevel.STUDY_ONLY, gov.va.med.imaging.federation.webservices.types.v3.FederationStudyLoadLevelType.STUDY_ONLY);

		imageAccessMap = new HashMap<ImageAccessLogEventType, gov.va.med.imaging.federation.webservices.types.v3.FederationImageAccessLogEventTypeEventType>();
		imageAccessMap.put(ImageAccessLogEventType.IMAGE_COPY, gov.va.med.imaging.federation.webservices.types.v3.FederationImageAccessLogEventTypeEventType.IMAGE_COPY);
		imageAccessMap.put(ImageAccessLogEventType.IMAGE_PRINT, gov.va.med.imaging.federation.webservices.types.v3.FederationImageAccessLogEventTypeEventType.IMAGE_PRINT); 
		imageAccessMap.put(ImageAccessLogEventType.PATIENT_ID_MISMATCH, gov.va.med.imaging.federation.webservices.types.v3.FederationImageAccessLogEventTypeEventType.PATIENT_ID_MISMATCH); 
		imageAccessMap.put(ImageAccessLogEventType.RESTRICTED_ACCESS, gov.va.med.imaging.federation.webservices.types.v3.FederationImageAccessLogEventTypeEventType.RESTRICTED_ACCESS); 
		imageAccessMap.put(ImageAccessLogEventType.IMAGE_ACCESS,  gov.va.med.imaging.federation.webservices.types.v3.FederationImageAccessLogEventTypeEventType.IMAGE_ACCESS);
		
		examStatusMap = new HashMap<ExamStatus, gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamStatusType>();
		examStatusMap.put(ExamStatus.INTERPRETED, gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamStatusType.INTERPRETED);
		examStatusMap.put(ExamStatus.NOT_INTERPRETED, gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamStatusType.NOT_INTERPRETED);
	}
	
	// ================================================================================================
	// Map Based Translations
	// ================================================================================================
	public static gov.va.med.imaging.federation.webservices.types.v3.FederationPatientSexType translate(
		PatientSex patientSex)
	{
		for( Entry<PatientSex, gov.va.med.imaging.federation.webservices.types.v3.FederationPatientSexType> entry : Translator.sexMap.entrySet() )
			if( entry.getKey() == patientSex )
				return entry.getValue();
		
		return gov.va.med.imaging.federation.webservices.types.v3.FederationPatientSexType.UNKNOWN;
	}
	
	public static PatientSex translate(
		gov.va.med.imaging.federation.webservices.types.v3.FederationPatientSexType sexType)
	{
		for( Entry<PatientSex, gov.va.med.imaging.federation.webservices.types.v3.FederationPatientSexType> entry : Translator.sexMap.entrySet() )
			if( entry.getValue().equals(sexType) )
				return entry.getKey();
		
		return PatientSex.Unknown;
	}

	// passthrough parameter types
	public static gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterTypeType translate(
		PassthroughParameterType parameterType)
	{		
		for( Entry<PassthroughParameterType, gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterTypeType> entry : Translator.parameterTypeMap.entrySet() )
			if( entry.getKey() == parameterType )
				return entry.getValue();
		
		return gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterTypeType.LITERAL;
	}	
	
	public static PassthroughParameterType translate(
		gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterTypeType parameterTypeType)
	{		
		for( Entry<PassthroughParameterType, gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterTypeType> entry : Translator.parameterTypeMap.entrySet() )
			if( entry.getValue() == parameterTypeType )
				return entry.getKey();
		
		return PassthroughParameterType.literal;
	}	

	// patient sensitivity levels
	public static gov.va.med.imaging.federation.webservices.types.v3.PatientSensitivityLevelType translate(
		PatientSensitivityLevel sensitivityLevel)
	{		
		for( Entry<PatientSensitivityLevel, gov.va.med.imaging.federation.webservices.types.v3.PatientSensitivityLevelType> entry : Translator.patientSensitivityMap.entrySet() )
			if( entry.getKey() == sensitivityLevel )
				return entry.getValue();
		
		return null;
	}	
	
	public static PatientSensitivityLevel translate(
		gov.va.med.imaging.federation.webservices.types.v3.PatientSensitivityLevelType sensitivityLevel)
	{		
		for( Entry<PatientSensitivityLevel, gov.va.med.imaging.federation.webservices.types.v3.PatientSensitivityLevelType> entry : Translator.patientSensitivityMap.entrySet() )
			if( entry.getValue() == sensitivityLevel )
				return entry.getKey();
		
		return null;
	}	
	
	// study load level
	public static gov.va.med.imaging.federation.webservices.types.v3.FederationStudyLoadLevelType translate(
		StudyLoadLevel loadLevel)
	{		
		for( Entry<StudyLoadLevel, gov.va.med.imaging.federation.webservices.types.v3.FederationStudyLoadLevelType> entry : Translator.loadLevelMap.entrySet() )
			if( entry.getKey() == loadLevel )
				return entry.getValue();
		
		return gov.va.med.imaging.federation.webservices.types.v3.FederationStudyLoadLevelType.FULL;
	}	
	
	public static StudyLoadLevel translate(
		gov.va.med.imaging.federation.webservices.types.v3.FederationStudyLoadLevelType loadLevelType)
	{		
		for( Entry<StudyLoadLevel, gov.va.med.imaging.federation.webservices.types.v3.FederationStudyLoadLevelType> entry : Translator.loadLevelMap.entrySet() )
			if( entry.getValue() == loadLevelType )
				return entry.getKey();
		
		return StudyLoadLevel.FULL;
	}	
	
	// Image Access Map
	public static gov.va.med.imaging.federation.webservices.types.v3.FederationImageAccessLogEventTypeEventType translate(ImageAccessLogEventType eventType) 
	{
		for( Entry<ImageAccessLogEventType, gov.va.med.imaging.federation.webservices.types.v3.FederationImageAccessLogEventTypeEventType> entry : Translator.imageAccessMap.entrySet() )
			if( entry.getKey() == eventType )
				return entry.getValue();
		
		return gov.va.med.imaging.federation.webservices.types.v3.FederationImageAccessLogEventTypeEventType.IMAGE_ACCESS;
	}
	
	public static ImageAccessLogEventType translate(gov.va.med.imaging.federation.webservices.types.v3.FederationImageAccessLogEventTypeEventType eventType) 
	{
		for( Entry<ImageAccessLogEventType, gov.va.med.imaging.federation.webservices.types.v3.FederationImageAccessLogEventTypeEventType> entry : Translator.imageAccessMap.entrySet() )
			if( entry.getValue() == eventType )
				return entry.getKey();
		
		return ImageAccessLogEventType.IMAGE_ACCESS;
	}
	
	// Exam Status Map
	public static gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamStatusType translate(
		ExamStatus examStatus)
	{
		for( Entry<ExamStatus, gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamStatusType> entry : Translator.examStatusMap.entrySet() )
			if( entry.getKey() == examStatus )
				return entry.getValue();
		
		return gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamStatusType.INTERPRETED;
	}
	
	
	public static ExamStatus translate(
		gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamStatusType statusType)
	{
		for( Entry<ExamStatus, gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamStatusType> entry : Translator.examStatusMap.entrySet() )
			if( entry.getValue() == statusType )
				return entry.getKey();
		
		return ExamStatus.INTERPRETED;
	}
	
	// ================================================================================================
	// 
	// ================================================================================================
	
	// be careful about re-using SimpleDateFormat instances because they are not thread-safe 
	protected static DateFormat getFederationWebserviceShortDateFormat()
	{
		return new SimpleDateFormat(federationWebserviceShortDateFormat);
	}
	
	public static Date translateDate(String federationDate)
	throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
		return sdf.parse(federationDate);
	}

	public static String translate(Date dob)
    throws ParseException
    {
    	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
    	return sdf.format(dob);
    }
	
	// ================================================================================================
	// 
	// ================================================================================================
	
	/**
	 * 
	 * @param sites
	 * @return
	 * @throws TranslationException
	 */
	public static String[] translateResolvedArtifactSourceCollection(Collection<ResolvedArtifactSource> sites)
	throws TranslationException
	{
		if(sites == null)
			return null;
		String[] siteNumbers = new String[sites.size()];
		int index = 0;
		for(ResolvedArtifactSource resolvedSite : sites)
		{
			ArtifactSource site = resolvedSite.getArtifactSource();
			siteNumbers[index++] = site instanceof Site ? ((Site)site).getSiteNumber() : null;
		}
		
		return siteNumbers;
	}
	
	public static List<String> translateSites(String[] sites)
	throws TranslationException
	{
		List<String> siteList = new ArrayList<String>();
		if(sites == null)
		{
			// JMW 5/26/2010 the service might return a null array which should be converted
			// to an empty list result			
			return siteList;
		}
		for(String site : sites)
			siteList.add(site);
		
		return siteList;
	}
	
	/**
	 * 
	 * @param ipsX
	 * @return
	 */
	public static gov.va.med.imaging.federation.webservices.types.v3.StudiesType translate(
		InsufficientPatientSensitivityException ipsX)
	{
		gov.va.med.imaging.federation.webservices.types.v3.StudiesType result = 
			new gov.va.med.imaging.federation.webservices.types.v3.StudiesType();
		
		gov.va.med.imaging.federation.webservices.types.v3.FederationStudiesErrorMessageType errorType = 
			new gov.va.med.imaging.federation.webservices.types.v3.FederationStudiesErrorMessageType();
		errorType.setErrorCode(BigInteger.valueOf(ipsX.getSensitiveValue().getSensitiveLevel().getCode()));
		errorType.setErrorMessage(ipsX.getSensitiveValue().getWarningMessage());
		errorType.setStudiesError(gov.va.med.imaging.federation.webservices.types.v3.FederationStudiesErrorType.INSUFFICIENT_SENSITIVE_LEVEL);
		result.setError(errorType);
		
		return result;
	}
	
	/**
	 * 
	 * @param studies
	 * @return
	 * @throws TranslationException
	 */
	public static gov.va.med.imaging.federation.webservices.types.v3.StudiesType translate(
		List<Study> studies)
	throws TranslationException
	{
		gov.va.med.imaging.federation.webservices.types.v3.StudiesType result = 
			new gov.va.med.imaging.federation.webservices.types.v3.StudiesType();
		
		gov.va.med.imaging.federation.webservices.types.v3.FederationStudyType[] federationStudies =
			AbstractTranslator.translate(gov.va.med.imaging.federation.webservices.types.v3.FederationStudyType[].class, studies);
		
		result.setStudy(federationStudies);
		
		return result;		
	}
	
	/**
	 * 
	 * @param studies
	 * @return
	 * @throws TranslationException
	 */
	public static gov.va.med.imaging.federation.webservices.types.v3.FederationStudyType[] translateIntoFederationStudyType(
		List<Study> studies)
	throws TranslationException
	{
		if(studies == null || studies.size() == 0)
			return null;
		gov.va.med.imaging.federation.webservices.types.v3.FederationStudyType[] result = 
			new gov.va.med.imaging.federation.webservices.types.v3.FederationStudyType[studies.size()];
		
		
		for(int i = 0; i < studies.size(); i++)
		{
			gov.va.med.imaging.federation.webservices.types.v3.FederationStudyType federationStudy =
				AbstractTranslator.translate(gov.va.med.imaging.federation.webservices.types.v3.FederationStudyType.class, studies.get(i));
			
			result[i] = federationStudy;
		}
		return result;
	}
	
	/**
	 * 
	 * @param study
	 * @return
	 * @throws ParseException
	 * @throws TranslationException
	 */
	public static gov.va.med.imaging.federation.webservices.types.v3.FederationStudyType translate(Study study)
	throws ParseException, TranslationException
	{
		if(study == null)
			return null;
		gov.va.med.imaging.federation.webservices.types.v3.FederationStudyType result = 
			new gov.va.med.imaging.federation.webservices.types.v3.FederationStudyType();
		gov.va.med.imaging.federation.webservices.types.v3.FederationStudyLoadLevelType loadLevel = 
			AbstractTranslator.translate(
				gov.va.med.imaging.federation.webservices.types.v3.FederationStudyLoadLevelType.class, 
				study.getStudyLoadLevel());
		result.setStudyLoadLevel( loadLevel );
		result.setDescription(study.getDescription());
		
		result.setEvent(study.getEvent());
		result.setImageCount(study.getImageCount());
		result.setImagePackage(study.getImagePackage());
		result.setImageType(study.getImageType());
		result.setNoteTitle(study.getNoteTitle());
		result.setOrigin(study.getOrigin());
		result.setPatientIcn(study.getPatientId());
		result.setPatientName(study.getPatientName());
		result.setProcedureDescription(study.getProcedure());
		result.setRadiologyReport(study.getRadiologyReport());
		result.setSiteNumber(study.getSiteNumber());
		result.setSiteName(study.getSiteName());
		result.setSiteAbbreviation(study.getSiteAbbr());
		result.setSpecialtyDescription(study.getSpecialty());
		result.setProcedureDate(translateToDicom(study.getProcedureDate()));
		
		result.setStudyPackage(study.getImagePackage());
		result.setStudyClass(study.getStudyClass() == null ? "" : study.getStudyClass());
		result.setStudyType(study.getImageType());
		result.setCaptureDate(study.getCaptureDate());
		result.setCapturedBy(study.getCaptureBy());	
		result.setRpcResponseMsg(study.getRpcResponseMsg());
		result.setErrorMessage(study.getErrorMessage() == null ? "" : study.getErrorMessage());
		// return null for the UID instead of the empty string to be consistent with the WSDL - DKB
		if (study.getStudyUid() != null && study.getStudyUid().trim().length() > 0)
			result.setDicomUid(study.getStudyUid());
		else
			result.setDicomUid(null);
		
		result.setStudyId( Base32ConversionUtility.base32Encode(study.getStudyIen()) );		// CTB 29March2010 add Base32
		
		gov.va.med.imaging.federation.webservices.types.v3.FederationStudyTypeComponentSeries seriesType =
			AbstractTranslator.translate(
				gov.va.med.imaging.federation.webservices.types.v3.FederationStudyTypeComponentSeries.class, 
				study.getSeries()
			);
		result.setComponentSeries(seriesType);
		result.setSeriesCount(seriesType != null ? seriesType.getSeries().length : 0);
		// set the description string here as a separate operation so that the translators
		// can remain symmetric
		for(gov.va.med.imaging.federation.webservices.types.v3.FederationSeriesType series: seriesType.getSeries())
			series.setDescription( study.getDescription() );
		
		if(study.getFirstImage() == null)
			throw new TranslationException("Study.firstImage is null, translation of study '" + study.getStudyUrn() + "' cannot continue.");
		
		gov.va.med.imaging.federation.webservices.types.v3.FederationInstanceType firstImage = 
			AbstractTranslator.translate(gov.va.med.imaging.federation.webservices.types.v3.FederationInstanceType.class, 
			study.getFirstImage()
		);
		result.setFirstImage(firstImage);
		result.setFirstImageIen(firstImage.getImageId());
		
		gov.va.med.imaging.federation.webservices.types.v3.ObjectOriginType originType =
			AbstractTranslator.translate(
				gov.va.med.imaging.federation.webservices.types.v3.ObjectOriginType.class, 
				study.getObjectOrigin()
		);
		result.setObjectOrigin(originType);
		
		gov.va.med.imaging.federation.webservices.types.v3.FederationStudyTypeStudyModalities modalitiesType =
			AbstractTranslator.translate(
				gov.va.med.imaging.federation.webservices.types.v3.FederationStudyTypeStudyModalities.class, 
				study.getModalities()
		);
			
		result.setStudyModalities( modalitiesType );
		
		return result;
	}
	
	/**
	 * @param modalities
	 * @return
	 */
	public static gov.va.med.imaging.federation.webservices.types.v3.FederationStudyTypeStudyModalities translateModalityCollection(
		Collection<String> modalities)
	{
		String[] modalityArray = modalities.toArray( new String[modalities.size()] );	

		gov.va.med.imaging.federation.webservices.types.v3.FederationStudyTypeStudyModalities result = 
			new gov.va.med.imaging.federation.webservices.types.v3.FederationStudyTypeStudyModalities(modalityArray);
		return result;
	}

	/**
	 * Transform a clinical display webservice FilterType to an internal Filter instance.
	 * @throws GlobalArtifactIdentifierFormatException 
	 * 
	 */
	public static StudyFilter translate(
		gov.va.med.imaging.federation.webservices.types.v3.FederationFilterType filterType,
		int authorizedSensitiveLevel, String requestingSiteNumber, String patientIcn) 
	throws GlobalArtifactIdentifierFormatException 
	{
		StudyFilter filter = new StudyFilter();
		filter.setMaximumAllowedLevel(PatientSensitivityLevel.getPatientSensitivityLevel(authorizedSensitiveLevel));
		if(filterType != null) 
		{
			DateFormat df = getFederationWebserviceShortDateFormat();
			
			Date fromDate = null;
			try
			{
				fromDate = filterType.getFromDate() == null  || filterType.getFromDate().length() == 0 ? null : df.parse(filterType.getFromDate());
			} 
			catch (ParseException x)
			{
				AbstractTranslator.getLogger().error("ParseException converting webservice format string from-date '" +  filterType.getFromDate() + "' to internal Date", x);
				fromDate = null;
			}
			
			Date toDate = null;
			try
			{
				toDate = filterType.getToDate() == null || filterType.getToDate().length() == 0 ? null : df.parse(filterType.getToDate());
			} 
			catch (ParseException x)
			{
				getLogger().error("ParseException converting webservice format string to-date '" +  filterType.getToDate() + "' to internal Date", x);
				fromDate = null;
			}
			
			// some business rules for the filter dates
			if (fromDate != null && toDate == null)
			{
				// default toDate to today
				toDate = new Date();
			}
			else if (fromDate == null && toDate != null)
			{
				// default to unfiltered
				toDate = null;
			}
			
			filter.setFromDate(fromDate);
			filter.setToDate(toDate);
			
			filter.setStudy_class(filterType.get_class() == null ? "" : filterType.get_class());
			filter.setStudy_event(filterType.getEvent() == null ? "" : filterType.getEvent());
			filter.setStudy_package(filterType.get_package() == null ? "" : filterType.get_package());
			filter.setStudy_specialty(filterType.getSpecialty() == null ? "" : filterType.getSpecialty());
			filter.setStudy_type(filterType.getTypes() == null ? "" : filterType.getTypes());
			String requestedStudyIdAsString = filterType.getStudyId();
			if((requestedStudyIdAsString != null) && (requestedStudyIdAsString.length() > 0))
			{
				// the study Id in the filter from Federation version 3 is only the study ID, not the entire URN
				// need to create a valid URN for this to work now
				try
				{
					// this might create a BhieStudyURN based on the requesting site number
					URN studyUrn = StudyURNFactory.create(requestingSiteNumber, 
							Base32ConversionUtility.base32Decode(requestedStudyIdAsString), patientIcn, StudyURN.class);
					if(studyUrn instanceof GlobalArtifactIdentifier)
						filter.setStudyId( (GlobalArtifactIdentifier)studyUrn );					
				}
				catch (URNFormatException x)
				{
					throw new GlobalArtifactIdentifierFormatException("'" + requestedStudyIdAsString + "'" +  
						" cannot be transformed into a GlobalArtifactIdentifier realization."
					);
				}
			}
			else
				filter.setStudyId(null);
			
			if(filterType.getOrigin() == null) {
				filter.setOrigin("");
			}
			else {
				if("UNSPECIFIED".equals(filterType.getOrigin().getValue())) {
					filter.setOrigin("");
				}
				else {
					filter.setOrigin(filterType.getOrigin().getValue());
				}
			}
			// don't have a study id used here
		}
		return filter;
	}

	@TranslationMethod(unmatchedMethod=true)
	public static gov.va.med.imaging.federation.webservices.types.v3.FederationFilterType translate(StudyFilter studyFilter)
	{
		if(studyFilter == null)
			return new gov.va.med.imaging.federation.webservices.types.v3.FederationFilterType();

		return new gov.va.med.imaging.federation.webservices.types.v3.FederationFilterType(
			studyFilter.getStudy_package(), 
			studyFilter.getStudy_class(), 
			studyFilter.getStudy_type(), 
			studyFilter.getStudy_event(), 
			studyFilter.getStudy_specialty(), 
			studyFilter.getFromDate() == null ? null : DateUtil.getShortDateFormat().format(studyFilter.getFromDate()), 
				studyFilter.getToDate() == null ? null : DateUtil.getShortDateFormat().format(studyFilter.getToDate()),
			translateOrigin(studyFilter.getOrigin()),				
					// translator.transformOrigin(filter.getOrigin()),
			studyFilter.getStudyId() == null ? null : studyFilter.getStudyId().toString() 
		);
	}
	
	public static gov.va.med.imaging.federation.webservices.types.v3.FederationFilterTypeOrigin translateOrigin(String origin)
	{
		return origin == null || origin.length() == 0 ? 
				gov.va.med.imaging.federation.webservices.types.v3.FederationFilterTypeOrigin.fromValue("UNSPECIFIED") :
					gov.va.med.imaging.federation.webservices.types.v3.FederationFilterTypeOrigin.fromValue(origin);
	}
	
	public static String translateOrigin(gov.va.med.imaging.federation.webservices.types.v3.FederationFilterTypeOrigin filterTypeOrigin)
	{
		return filterTypeOrigin == null || "UNSPECIFIED".equals(filterTypeOrigin.getValue()) ? 
			"" : filterTypeOrigin.getValue();
	}
	
	public static gov.va.med.imaging.federation.webservices.types.v3.PatientSensitiveCheckResponseType translate(PatientSensitiveValue sensitiveValue) 
	throws TranslationException
	{
		gov.va.med.imaging.federation.webservices.types.v3.PatientSensitiveCheckResponseType result = 
			new gov.va.med.imaging.federation.webservices.types.v3.PatientSensitiveCheckResponseType();
		result.setWarningMessage(sensitiveValue.getWarningMessage());
		gov.va.med.imaging.federation.webservices.types.v3.PatientSensitivityLevelType sensitivityLevelType =
			AbstractTranslator.translate(gov.va.med.imaging.federation.webservices.types.v3.PatientSensitivityLevelType.class, 
			sensitiveValue.getSensitiveLevel()
		);
		result.setPatientSensitivityLevel(sensitivityLevelType);
		
		return result;
	}
	
	public static ImageAccessLogEvent translate(
			gov.va.med.imaging.federation.webservices.types.v3.FederationImageAccessLogEventType logEventType) 
	throws URNFormatException, TranslationException 
	{
		if(logEventType == null)
			return null;

		ImageAccessLogEventType imageAccessLogEventType =
			AbstractTranslator.translate(ImageAccessLogEventType.class, 
			logEventType.getEventType()
		);

		ImageAccessLogEvent result = 
			new ImageAccessLogEvent(Base32ConversionUtility.base32Decode(logEventType.getImageId()), 
					"", logEventType.getPatientIcn(), 
					logEventType.getSiteNumber(), System.currentTimeMillis(), 
					logEventType.getReason(), "",
					imageAccessLogEventType, logEventType.getUserSiteNumber());
		
		return result;
	}
	
	/**
	 * @param origin
	 */
	public static String translate(gov.va.med.imaging.federation.webservices.types.v3.FederationFilterTypeOrigin origin)
	{
		return origin.getValue();
	}
	
	public static gov.va.med.imaging.federation.webservices.types.v3.PatientType[] translate(Collection<Patient> patients) 
	throws TranslationException
	{
		if(patients == null)
			return null;
		gov.va.med.imaging.federation.webservices.types.v3.PatientType [] result = 
			new gov.va.med.imaging.federation.webservices.types.v3.PatientType[patients.size()];
		int index = 0;
		for(Patient patient : patients)
			result[index++] = AbstractTranslator.translate(
				gov.va.med.imaging.federation.webservices.types.v3.PatientType.class, 
				patient
			);
			
		return result;
	}
	
	public static gov.va.med.imaging.federation.webservices.types.v3.PatientType translate(Patient patient)
	throws ParseException, TranslationException
	{
		if(patient == null)
			return null;
		gov.va.med.imaging.federation.webservices.types.v3.PatientType result = 
			new gov.va.med.imaging.federation.webservices.types.v3.PatientType();
		result.setPatientDob(translate(patient.getDob()));
		result.setPatientIcn(patient.getPatientIcn());
		result.setPatientName(patient.getPatientName());
		gov.va.med.imaging.federation.webservices.types.v3.FederationPatientSexType sexType =
			translate( patient.getPatientSex() );
		result.setPatientSex(sexType);
		result.setVeteranStatus(patient.getVeteranStatus());
		return result;
	}
	
	/**
	 * 
	 * @param examSite
	 * @return
	 * @throws TranslationException
	 */
	public static gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamType[] translate(
		ExamSite examSite) 
	throws TranslationException
	{
		int examsCount = (examSite == null ? 0 : examSite.size());
		gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamType [] result = 
			new gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamType[examsCount];
		if(examsCount == 0)
			return result;
		
		int i = 0;
		for(Exam exam : examSite)
			result[i++] =
				AbstractTranslator.translate(
					gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamType.class, 
					exam
				);
		
		return result;
	}
	
	/**
	 * @param routingToken
	 * @param exams
	 * @return
	 * @throws URNFormatException
	 */
	public static ExamSite translate(
		gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamType [] exams)
	throws URNFormatException
	{
		if(exams == null || exams.length == 0)
			return null;
		
		RoutingToken rt = null;
		try
		{
			rt = RoutingTokenHelper.createSiteAppropriateRoutingToken(exams[0].getSiteNumber());
		}
		catch (RoutingTokenFormatException x)
		{
			throw new URNFormatException(x);
		}
		ExamSite result = new ExamSite(rt, ArtifactResultStatus.fullResult, exams[0].getSiteName());
		for(int index=0; index < exams.length; ++index)
			result.add( translate(exams[index]) );
		
		return result;
	}
	
	/**
	 * 
	 * @param exams
	 * @return
	 * @throws URNFormatException
	 */
	//public static List<Exam> translateExams(gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamType [] exams)
	//throws URNFormatException
	//{
	//	if(exams == null)
	//	{
	//		getLogger().warn("Received null exams, returning empty array of exams.");
	//		return new ArrayList<Exam>(0);
	//	}
	//	List<Exam> result = new ArrayList<Exam>(exams.length);
	//	
	//	for(gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamType exam : exams)
	//	{
	//		result.add(translate(exam));
	//	}		
	//	return result;
	//}
	
	/**
	 * 
	 * @param exam
	 * @return
	 * @throws TranslationException
	 */
	public static gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamType translate(
		Exam exam) 
	throws TranslationException
	{
		gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamType result =
			new gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamType();
		
		result.setCptCode(exam.getCptCode());
		result.setExamId( Base32ConversionUtility.base32Encode(exam.getExamId()) );		// CTB 29Mar2010 added Base32
		result.setExamStatus( translate(exam.getExamStatus()) );
		result.setModality(exam.getModality());
		result.setPatientIcn(exam.getPatientIcn());
		result.setRadiologyReport(exam.getExamReport());
		result.setRawHeader1(exam.getRawHeaderLine1());
		result.setRawHeader2(exam.getRawHeaderLine2());
		result.setRawValue(exam.getRawOutput());
		result.setRequisitionReport(exam.getExamRequisitionReport());
		result.setSiteAbbr(exam.getSiteAbbr());
		result.setSiteName(exam.getSiteName());
		result.setSiteNumber(exam.getSiteNumber());
		result.setPresentationState(exam.getPresentationStateData());
		
		if(exam.getImages() == null)
		{
			// this will happen if the examSite does not have images at all
			result.setExamImages(null);
		}
		else
		{
			// if the images were attempted to load, they might be on jukebox so not considered fully loaded, but stll need to call translate so the header is set
			gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImagesType examImages = 
				AbstractTranslator.translate(
					gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImagesType.class, 
					exam.getImages()
				);
			result.setExamImages(new gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImagesResponseType(examImages));
		}		
		return result;
	}
	
	/*
	public gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImageType [] transformExamImages(ExamImages images)
	{
		gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImageType [] examImages =
			new gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImageType[images.size()];
		
		int count = 0;
		for(String key : images.keySet())
		{
			ExamImage examImage = images.get(key);
			examImages[count] = transformExamImage(examImage);
			count++;
		}
		return examImages;
	}*/
	
	/**
	 * @throws TranslationException 
	 * 
	 */
	public static gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImagesType translate(
		ExamImages images) 
	throws TranslationException
	{
		gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImagesType result = 
			new gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImagesType();
		
		gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImageType [] examImages =
			new gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImageType[images.size()];
		
		int count = 0;
		for(ExamImage examImage : images)
			examImages[count++] = AbstractTranslator.translate(
				gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImageType.class, 
				examImage
			);

		result.setExamImages(examImages);
		result.setRawHeader(images.getRawHeader());
		
		return result;
	}
	
	public static gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImageType translate(ExamImage examImage)
	{
		gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImageType result =
			new gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImageType();
		
		result.setBigImageFilename(examImage.getDiagnosticFilePath());
		result.setExamId( Base32ConversionUtility.base32Encode(examImage.getExamId()) );		// CTB 29Mar2010 added Base32
		result.setImageId( Base32ConversionUtility.base32Encode(examImage.getImageId()) );		// CTB 29Mar2010 added Base32
		result.setPatientIcn(examImage.getPatientIcn());
		result.setSiteNumber(examImage.getSiteNumber());
		
		return result;
	}
	
	/*
	public gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImageType [] transformExamToImages(Exam exam)
	{
		int examImageCount = (exam == null ? 0 : exam.getImageCount());
		gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImageType [] result = 
			new gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImageType[examImageCount];
		
		if(examImageCount == 0)
			return result;
		
		exam.get
		
		return result;		
	}*/
	
	public static gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadActiveExamsType translate(
		ActiveExams activeExams) 
	throws TranslationException
	{
		gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadActiveExamsType result = 
			new gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadActiveExamsType();
		result.setRawHeader1(activeExams.getRawHeader1());
		result.setRawHeader2(activeExams.getRawHeader2());
		result.setSiteNumber(activeExams.getSiteNumber());
		
		gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadActiveExamType [] activeExamArray = 
			new gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadActiveExamType[activeExams.size()];

		for(int i = 0; i < activeExams.size(); i++)		
			activeExamArray[i] = AbstractTranslator.translate(
				gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadActiveExamType.class, 
				activeExams.get(i)
			);
		
		result.setActiveExams(activeExamArray);
		
		return result;
	}
	
	public static gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadActiveExamType translate(
		ActiveExam activeExam)
	{
		gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadActiveExamType result = 
			new gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadActiveExamType();
		
		result.setExamId( Base32ConversionUtility.base32Encode(activeExam.getExamId()) );		// CTB 29Mar2010 added Base32
		result.setPatientIcn(activeExam.getPatientIcn());
		result.setRawValue(activeExam.getRawValue());
		result.setSiteNumber(activeExam.getSiteNumber());
		
		return result;
	}

	/**
	 * 
	 * @param patientRegistration
	 * @return
	 */
	public static gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadPatientRegistrationType translate(
		PatientRegistration patientRegistration)
	{
		gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadPatientRegistrationType result =
			new gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadPatientRegistrationType();
		result.setCptCode(patientRegistration.getCptCode());
		result.setPatientIcn(patientRegistration.getPatientIcn());
		return result;
	}	
	
	/**
	 * 
	 * @param seriesSet
	 * @param seriesDescription
	 * @return
	 * @throws ParseException
	 */
	public static gov.va.med.imaging.federation.webservices.types.v3.FederationStudyTypeComponentSeries translate(
		Set<Series> seriesSet) 
	throws ParseException
	{
		if(seriesSet == null)// || seriesSet.size() == 0)
			return null;
		ArrayList<gov.va.med.imaging.federation.webservices.types.v3.FederationSeriesType> serieses = 
			new ArrayList<gov.va.med.imaging.federation.webservices.types.v3.FederationSeriesType>();
		
		for(Series series : seriesSet)
		{
			// Filter series with no images from the result set - DKB
			if(series.getImageCount() > 0)
				serieses.add(translate(series));
		}
		
		gov.va.med.imaging.federation.webservices.types.v3.FederationSeriesType[] seriesArray = 
			serieses.toArray(new gov.va.med.imaging.federation.webservices.types.v3.FederationSeriesType[serieses.size()]);
		gov.va.med.imaging.federation.webservices.types.v3.FederationStudyTypeComponentSeries componentSeries =
			new gov.va.med.imaging.federation.webservices.types.v3.FederationStudyTypeComponentSeries();
		
		componentSeries.setSeries(seriesArray);
		
		return componentSeries;
	}
	
	/**
	 * 
	 * @param series
	 * @param seriesDescription
	 * @return
	 * @throws ParseException
	 */
	public static gov.va.med.imaging.federation.webservices.types.v3.FederationSeriesType translate(
		Series series) 
	throws ParseException
	{
		if(series == null)
			return null;
		
		gov.va.med.imaging.federation.webservices.types.v3.FederationSeriesType result = 
			new gov.va.med.imaging.federation.webservices.types.v3.FederationSeriesType();
		gov.va.med.imaging.federation.webservices.types.v3.FederationInstanceType[] instances = 
			new gov.va.med.imaging.federation.webservices.types.v3.FederationInstanceType[series.getImageCount()];

		int index=0;
		for(Image image : series)
			instances[index++] = translate(image);
	
		gov.va.med.imaging.federation.webservices.types.v3.FederationSeriesTypeComponentInstances seriesInstances = 
			new gov.va.med.imaging.federation.webservices.types.v3.FederationSeriesTypeComponentInstances(instances);

		//TODO: retrieve series through VistA if possible (available in DICOM txt files)
		//result.setDescription(seriesDescription);

		// return null for the UID instead of the empty string to be consistent with the WSDL - DKB
		if (series.getSeriesUid() != null && series.getSeriesUid().trim().length() > 0)
		{
			result.setDicomUid(series.getSeriesUid());
		}
		
		if(!"".equals(series.getSeriesNumber())) {
			int serNum = Integer.parseInt(series.getSeriesNumber());
			result.setDicomSeriesNumber(serNum);
		}
		result.setSeriesId(series.getSeriesIen());
		result.setImageCount(instances.length);
		result.setComponentInstances(seriesInstances);
		result.setSeriesModality(series.getModality());
		result.setObjectOrigin( translate(series.getObjectOrigin()) );
		
		return result;
	}
	
	public static gov.va.med.imaging.federation.webservices.types.v3.ObjectOriginType translate(ObjectOrigin origin)
	{
		if(origin == ObjectOrigin.VA)
		{
			return gov.va.med.imaging.federation.webservices.types.v3.ObjectOriginType.VA;
		}
		else if(origin == ObjectOrigin.DOD)
		{
			return gov.va.med.imaging.federation.webservices.types.v3.ObjectOriginType.DOD;
		}
		else
			return gov.va.med.imaging.federation.webservices.types.v3.ObjectOriginType.OTHER;		
	}
	
	public static gov.va.med.imaging.federation.webservices.types.v3.FederationInstanceType translate(Image image) 
	throws ParseException
	{
		if(image == null)
			return null;
		
		gov.va.med.imaging.federation.webservices.types.v3.FederationInstanceType instanceType = 
			new gov.va.med.imaging.federation.webservices.types.v3.FederationInstanceType();
		
		instanceType.setImageId( Base32ConversionUtility.base32Encode(image.getIen()) );		// CTB 29Mar2010 added Base32
		
		// Exchange fields
		// return null for the UID instead of the empty string to be consistent with the WSDL - DKB
		if (image.getImageUid()!= null && image.getImageUid().trim().length() > 0)
		{
			instanceType.setDicomUid(image.getImageUid().trim());
		}
		
		
		if (image.getImageNumber() != null && image.getImageNumber().trim().length() > 0)
		{
			try
			{
				Integer imageNumber = new Integer(image.getImageNumber());
				instanceType.setImageNumber(imageNumber);
			}
			catch (NumberFormatException ex)
			{
				// not a number - return null
				instanceType.setImageNumber(null);
			}
		}
		else
		{
			instanceType.setImageNumber(null);
		}
		
		// Clinical Display fields
		instanceType.setDescription(image.getDescription());
		instanceType.setDicomImageNumberForDisplay(image.getDicomImageNumberForDisplay());
		instanceType.setDicomSequenceNumberForDisplay(image.getDicomSequenceNumberForDisplay());
		instanceType.setPatientIcn(image.getPatientId());
		instanceType.setPatientName(image.getPatientName());
		instanceType.setProcedure(image.getProcedure());
		if(image.getProcedureDate() == null)
		{
			getLogger().warn("Setting null procedure date for image");
			instanceType.setProcedureDate("");
		}
		else 
		{
			/*
			// if the hour and minute are not 0, then likely they contain real values for hour and minute (not 00:00)
			// this leaves open the possibility of invalid data, if the real date was at 00:00 then this would not show that time.
			// we would then omit data, not show invalid data
			if((image.getProcedureDate().getHours() > 0) && (image.getProcedureDate().getMinutes() > 0))
			{
				instanceType.setProcedureDate(getFederationWebserviceLongDateFormat().format(image.getProcedureDate()));
			}
			else
			{
				instanceType.setProcedureDate(getFederationWebserviceShortDateFormat().format(image.getProcedureDate()));
			}
			*/
			instanceType.setProcedureDate(translateToDicom(image.getProcedureDate()));
		}
		instanceType.setSiteNumber(image.getSiteNumber());
		instanceType.setSiteAbbr(image.getSiteAbbr());
		instanceType.setImageClass(image.getImageClass());
		instanceType.setAbsLocation(image.getAbsLocation());
		instanceType.setFullLocation(image.getFullLocation());
		
		instanceType.setQaMessage(image.getQaMessage());
		instanceType.setImageType(BigInteger.valueOf(image.getImgType()));
		instanceType.setFullImageFilename(image.getFullFilename());
		instanceType.setAbsImageFilename(image.getAbsFilename());
		instanceType.setBigImageFilename(image.getBigFilename());

		instanceType.setStudyId( Base32ConversionUtility.base32Encode(image.getStudyIen()) );		// CTB 29Mar2010 added Base32
		instanceType.setGroupId( Base32ConversionUtility.base32Encode(image.getStudyIen()) );	// CTB 29Mar2010 added Base32
		/*
		if(image.getGroupIen() == null)
		{
			instanceType.setGroupId(null);
		}
		else
		{
			instanceType.setGroupId( Base32ConversionUtility.base32Encode(image.getStudyIen()) );	// CTB 29Mar2010 added Base32
		}*/
		//instanceType.setStudyId(image.getStudyIen());		
		//instanceType.setGroupId(image.getGroupIen());
		
		instanceType.setImageModality(image.getImageModality());
		instanceType.setObjectOrigin( translate(image.getObjectOrigin()) );
		instanceType.setErrorMessage(image.getErrorMessage() == null ? "" : image.getErrorMessage());
		return instanceType;
	}
	
	public static String translateToDicom(Date procedureDate) 
	throws ParseException
	{
		String procedureDateStringAsDicom = "";
		if(procedureDate != null)
		{
			DateFormat dicomDateFormat = new DicomDateFormat();
			procedureDateStringAsDicom = dicomDateFormat.format(procedureDate);
		}
		return procedureDateStringAsDicom;
	}
	
	public static Date translateFromDicom(String date) 
	throws ParseException
	{
		DateFormat dicomDateFormat = new DicomDateFormat();
		return dicomDateFormat.parse(date);
	}
	
	public static PassthroughInputMethod translate(String methodName,
		gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterType [] parameters)
	{
		if(methodName == null)
			return null;
		PassthroughInputMethod result = new PassthroughInputMethod(methodName);
		if(parameters != null)
			for(gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterType parameter : parameters)
				result.getParameters().add(translate(parameter));
		
		return result;
	}
	
	/**
	 * 
	 * @param parameter
	 * @return
	 */
	public static PassthroughParameter translate(
			gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterType parameter)
	{
		PassthroughParameter result = new PassthroughParameter();
		result.setIndex(parameter.getParameterIndex().intValue());
		result.setParameterType(transformParameterType(parameter.getParameterType()));
		gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterValueType parameterValue = 
			parameter.getParameterValue();
		if(parameterValue == null)
		{
			result.setValue(null);
			result.setMultipleValues(null);
		}
		else
		{
			String value = parameterValue.getValue();
			result.setValue(value);
			
			gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterMultipleType multipleValues = 
				parameterValue.getMultipleValue();
			String[] values = multipleValues == null ? null : multipleValues.getMultipleValue();
			result.setMultipleValues(values);
		}
		
		return result;
	}
	
	public static PassthroughParameterType transformParameterType(
		gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterTypeType parameterType)
	{
		PassthroughParameterType result = PassthroughParameterType.literal;
		
		if(parameterType == gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterTypeType.LIST)
			result = PassthroughParameterType.list;
		else if(parameterType == gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterTypeType.REFERENCE)
			result = PassthroughParameterType.reference;
		
		return result;
	}
	
	// =========================================================================================================
	// =========================================================================================================
	
	public static PatientRegistration translate(
		gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadPatientRegistrationType patientRegistration)
	{
		PatientRegistration result = new PatientRegistration();
		result.setCptCode(patientRegistration.getCptCode());
		result.setPatientIcn(patientRegistration.getPatientIcn());
		return result;
	}
	
	public static Exam translate(gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamType exam)
	throws URNFormatException
	{
		Exam result = Exam.create(
			exam.getSiteNumber(), 
			Base32ConversionUtility.base32Decode(exam.getExamId()),	// CTB 29March2010 add Base32 
			exam.getPatientIcn());
		result.setCptCode(exam.getCptCode());				// CTB 29Mar2010, null was transformed to blank
		result.setExamReport(exam.getRadiologyReport()); // want it null if it is null
		result.setExamRequisitionReport(exam.getRequisitionReport());// want it null if it is null
		result.setExamStatus(translate(exam.getExamStatus()));		
		
		// this indicates the images were not included (shallow exam)
		if(exam.getExamImages() == null)
			result.setImages(null);
		else
			result.setImages(translate(exam.getExamImages()));
		result.setPresentationStateData(exam.getPresentationState()); // don't check for null, want null if null value
		result.setModality(exam.getModality());
		result.setPatientName(exam.getPatientName());
		result.setRawHeaderLine1(exam.getRawHeader1());
		result.setRawHeaderLine2(exam.getRawHeader2());
		result.setRawOutput(exam.getRawValue());
		result.setSiteAbbr(exam.getSiteAbbr());
		result.setSiteName(exam.getSiteName());
		return result;
	}
	
	private static ExamImages translate(gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImagesResponseType examImages)
	throws URNFormatException
	{		
		// if the getExamImages() is null then this indicates 0 images for the exam, not that they were not included (shallow)
		if(examImages.getExamImages() == null)
			// return empty hashmap for images in exam
			return new ExamImages("", false);
		
		return translate(examImages.getExamImages());			
	}
	
	public static ExamImages translate(gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImagesType examImages)
	throws URNFormatException
	{
		ExamImages result = new ExamImages(examImages.getRawHeader(), false);
		
		if(examImages.getExamImages() != null)
		{		
			for(gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImageType examImage : examImages.getExamImages())
			{
				ExamImage img = translate(examImage);
				if(img != null)
					result.add(img);
			}
		}
		return result;	
	}
	
	public static ExamImage translate(gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImageType examImage)
	throws URNFormatException
	{
		if(examImage == null)
			return null;
		ExamImage result = ExamImage.create(
			examImage.getSiteNumber(), 
			Base32ConversionUtility.base32Decode(examImage.getImageId()),		// CTB 29March2010 add Base32 
			Base32ConversionUtility.base32Decode(examImage.getExamId()), 		// CTB 29March2010 add Base32
			examImage.getPatientIcn());
		result.setDiagnosticFilePath(examImage.getBigImageFilename());
		result.setPatientName("");
		return result;
	}
	
	public static ActiveExams translate(gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadActiveExamsType activeExams)
	{
		String rawHeader1 = (activeExams.getRawHeader1() == null ? "" : activeExams.getRawHeader1());
		String rawHeader2 = (activeExams.getRawHeader2() == null ? "" : activeExams.getRawHeader2());
		
		ActiveExams result = new ActiveExams(
			activeExams.getSiteNumber(), 
			rawHeader1, 
			rawHeader2);
		// if there are no active exams returned from the web service call, the getActiveExams appears to 
		// turn into null, so need to check for null
		if(activeExams.getActiveExams() != null)
			for(gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadActiveExamType activeExam : activeExams.getActiveExams())
				result.add(translate(activeExam));
		
		return result;
	}
	
	public static ActiveExam translate(gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadActiveExamType activeExam)
	{
		// the examId in the ActiveExam is not a URN but an ID
		ActiveExam result = new ActiveExam(
			activeExam.getSiteNumber(), 
			Base32ConversionUtility.base32Decode(activeExam.getExamId()),		// CTB 29March2010 add Base32 
			activeExam.getPatientIcn());
		result.setRawValue(activeExam.getRawValue());		
		return result;
	}
	
	public static ObjectOrigin translate(
		gov.va.med.imaging.federation.webservices.types.v3.ObjectOriginType objectOriginType)
	{
		return 
			gov.va.med.imaging.federation.webservices.types.v3.ObjectOriginType._DOD.equals(objectOriginType.getValue()) ?
				ObjectOrigin.DOD :
				ObjectOrigin.VA;
	}
	
	public static gov.va.med.imaging.federation.webservices.types.v3.FederationFilterTypeOrigin translateObjectOrigin(
		String filterOrigin)
	{
		if("VA".equalsIgnoreCase(filterOrigin))
			return gov.va.med.imaging.federation.webservices.types.v3.FederationFilterTypeOrigin.value2;
		else if("DOD".equalsIgnoreCase(filterOrigin))
			return gov.va.med.imaging.federation.webservices.types.v3.FederationFilterTypeOrigin.value4;
		else if("NON-VA".equalsIgnoreCase(filterOrigin))
			return gov.va.med.imaging.federation.webservices.types.v3.FederationFilterTypeOrigin.value3;
		else if("FEE".equalsIgnoreCase(filterOrigin))
			return gov.va.med.imaging.federation.webservices.types.v3.FederationFilterTypeOrigin.value5;
		else
			return gov.va.med.imaging.federation.webservices.types.v3.FederationFilterTypeOrigin.value1;
	}
	
	@TranslationMethod(unmatchedMethod=true)
	public static SortedSet<Study> translate(
		gov.va.med.imaging.federation.webservices.types.v3.StudiesType studiesType,
		StudyFilter studyFilter, 
		String patientIcn)
	throws InsufficientPatientSensitivityException, TranslationException
	{
		if(studiesType.getError() != null)
		{
			getLogger().info("Study result contains error message, converting into InsufficientPatientSensitivityException");
			// should look at studiesType.getError().getStudiesError() to determine if actually sensitivity error type, for now always will be
			gov.va.med.imaging.federation.webservices.types.v3.FederationStudiesErrorMessageType errorMessageType = 
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
		return translate(studiesType.getStudy(), studyFilter);
	}
	
	@TranslationMethod(unmatchedMethod=true)
	public static SortedSet<Study> translate(
		gov.va.med.imaging.federation.webservices.types.v3.FederationStudyType [] studies,
		StudyFilter studyFilter) 
	throws TranslationException
	{
		SortedSet<Study> result = new TreeSet<Study>();	
		if(studies == null)
			return result;
		for(int i = 0; i < studies.length; i++)
		{
			gov.va.med.imaging.federation.webservices.types.v3.FederationStudyType studyType = studies[i];
			// CTB 29March2010
			// either the study filter has no study ID specified or the study IDs match
			// the study ID from the federation call will be base 32 encoded
			
			// JMW 6/1/2010 - need to translate first to get the proper URN to determine if allowed to include in result
			Study study = translate(studyType);
			boolean useStudy = 
				!studyFilter.isStudyIenSpecified() ||
				studyFilter.isAllowableStudyId( study.getGlobalArtifactIdentifier() );//Base32ConversionUtility.base32Decode(studyType.getStudyId()) );
			if(useStudy)
				result.add(study);
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
	
	public static Study translate(gov.va.med.imaging.federation.webservices.types.v3.FederationStudyType studyType) 
	throws TranslationException
	{
		Study study = null;
		String studyId = Base32ConversionUtility.base32Decode( studyType.getStudyId() );	// CTB 29March2010 add Base32
		ObjectOrigin studyOrigin = translate(studyType.getObjectOrigin());
		StudyLoadLevel studyLoadLevel = translate(studyType.getStudyLoadLevel());
		try
		{
			study = Study.create(studyOrigin, studyType.getSiteNumber(), 
					studyId, PatientIdentifier.icnPatientIdentifier(studyType.getPatientIcn()), 
					studyLoadLevel, 
					StudyDeletedImageState.cannotIncludeDeletedImages);
		}
		catch (URNFormatException x)
		{
			getLogger().error("Unable to create a Study from the given key elements", x);
			throw new TranslationException("Unable to create a Study from the given key elements", x);
		}
		if(study == null)
			throw new TranslationException(
				"Failed to create Study instance from " + 
				studyOrigin + "," + studyType.getSiteNumber() + ", " + studyId + 
				"," + studyType.getPatientIcn() + "," + studyLoadLevel
			);
		study.setDescription(studyType.getDescription() == null ? "" : studyType.getDescription());
		study.setStudyUid(studyType.getDicomUid());
		study.setImageCount(studyType.getImageCount());
		//study.setPatientIcn(studyType.getPatientIcn());
		if (studyType.getPatientName() == null)
		{
			studyType.setPatientName("");
		}
		study.setPatientName(studyType.getPatientName().replaceAll("\\^", " "));
		
		try
		{
			study.setProcedureDate( translateFromDicom(studyType.getProcedureDate()) );
		}
		catch (ParseException x)
		{
			throw new TranslationException(x);
		}
		study.setProcedure(studyType.getProcedureDescription() == null ? "" : studyType.getProcedureDescription());
		//study.setRadiologyReport((studyType.getRadiologyReport() == null ? "" : studyType.getRadiologyReport()));
		// JMW 10/12/2010 - check the study load level to see if the report should have been included. If not then
		// don't use the report value because it might be an empty string which doesn't always mean the report is there
		// this is a bit sad but necessary for some reason
		if(studyLoadLevel.isIncludeReport())
			study.setRadiologyReport(studyType.getRadiologyReport()); // allow study to be null
		
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
		
		firstImage = translate(studyType.getFirstImage());
		
		if(studyType.getComponentSeries() != null)
		{
			gov.va.med.imaging.federation.webservices.types.v3.FederationStudyTypeComponentSeries serieses = studyType.getComponentSeries();
			gov.va.med.imaging.federation.webservices.types.v3.FederationSeriesType []series = serieses.getSeries();
			if(series != null)
			{			
				for(int i = 0; i < series.length; i++)
				{				
					Series ser = translate(series[i]);
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
	
	public static Series translate(gov.va.med.imaging.federation.webservices.types.v3.FederationSeriesType seriesType) 
	throws TranslationException
	{
		if(seriesType == null)
			return null;
		Series series = new Series();
		series.setSeriesIen(seriesType.getSeriesId());
		series.setSeriesNumber(seriesType.getDicomSeriesNumber() + "");
		series.setSeriesUid(seriesType.getDicomUid() == null ? "" : seriesType.getDicomUid());
		series.setObjectOrigin(translate(seriesType.getObjectOrigin()));
		series.setModality(seriesType.getSeriesModality());
		
		gov.va.med.imaging.federation.webservices.types.v3.FederationInstanceType [] instances = 
			seriesType.getComponentInstances().getInstance();
		if(instances != null) {
			for(int i = 0; i < instances.length; i++) {
				Image image = translate(instances[i]);//, series, studyType);
				series.addImage(image);
			}
		}		
		return series;
	}
	
	public static Image translate(gov.va.med.imaging.federation.webservices.types.v3.FederationInstanceType instanceType) 
	throws TranslationException
	{
		if(instanceType == null)
			return null;		
		Image image = null;
		try
		{
			String studyId = instanceType.getStudyId();
			if((instanceType.getGroupId() != null) && (instanceType.getGroupId().length() > 0))
			{
				studyId = Base32ConversionUtility.base32Decode(instanceType.getGroupId());
			}
			
			image = Image.create(
				instanceType.getSiteNumber(), 
				Base32ConversionUtility.base32Decode(instanceType.getImageId()), 		// CTB 29March2010 add Base32
				studyId,
				PatientIdentifier.icnPatientIdentifier(instanceType.getPatientIcn()), 
				instanceType.getImageModality() 
			);
		}
		catch (URNFormatException x)
		{
			getLogger().error("Unable to create an Image instance from the given key elements.", x);
			return null;
		}
		
		image.setImageNumber(instanceType.getImageNumber() == null ? "" : instanceType.getImageNumber() + "");
		//image.setIen(instanceType.getImageId());
		image.setImageUid(instanceType.getDicomUid() == null ? "" : instanceType.getDicomUid());
		image.setDescription(instanceType.getDescription() == null ? "" : instanceType.getDescription());
		image.setPatientName(instanceType.getPatientName() == null ? "" : instanceType.getPatientName().replaceAll("\\^", " "));
		if(instanceType.getProcedureDate() == null || instanceType.getProcedureDate().trim().length() == 0)
			image.setProcedureDate( null );
		else
			try
			{
				image.setProcedureDate( translateFromDicom(instanceType.getProcedureDate()) );
			}
			catch (ParseException x)
			{
				throw new TranslationException(x);
			}
		image.setProcedure(instanceType.getProcedure() == null ? "" : instanceType.getProcedure());
		image.setSiteAbbr(instanceType.getSiteAbbr());
		image.setObjectOrigin(translate(instanceType.getObjectOrigin()));
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
	
	public static gov.va.med.imaging.federation.webservices.types.v3.FederationImageAccessLogEventType translate(
			ImageAccessLogEvent logEvent)
	{
		gov.va.med.imaging.federation.webservices.types.v3.FederationImageAccessLogEventType result = 
			new gov.va.med.imaging.federation.webservices.types.v3.FederationImageAccessLogEventType();
		result.setImageId(Base32ConversionUtility.base32Encode(logEvent.getImageIen()));
		result.setPatientIcn(logEvent.getPatientIcn());
		result.setReason(logEvent.getReasonCode());
		result.setSiteNumber(logEvent.getSiteNumber());
		result.setEventType(translate(logEvent.getEventType()));
		result.setUserSiteNumber(logEvent.getUserSiteNumber());
		return result;
	}

	/**
	 * 
	 * @param patients
	 * @return
	 */
	public static SortedSet<Patient> translate(gov.va.med.imaging.federation.webservices.types.v3.PatientType [] patients)
	{
		if(patients == null)
			return null;
		SortedSet<Patient> result = new TreeSet<Patient>();		
		for(int i = 0; i < patients.length; i++)
		{
			try
			{
				Patient patient = translate(patients[i]);
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
	
	public static Patient translate(gov.va.med.imaging.federation.webservices.types.v3.PatientType patientType)
	throws ParseException
	{
		Patient patient = null;
		if(patientType == null)
			return patient;
		
		patient = new Patient(
			patientType.getPatientName(), 
			patientType.getPatientIcn(), 
			patientType.getVeteranStatus(), 
			translate(patientType.getPatientSex()), 
			translateDate(patientType.getPatientDob()),
			null,
			null,
			null
		);
		return patient;
	}
	
	public static PatientSensitiveValue translate(
			gov.va.med.imaging.federation.webservices.types.v3.PatientSensitiveCheckResponseType responseType)
	{
		if(responseType == null)
			return null;
		String msg = responseType.getWarningMessage();
		PatientSensitivityLevel level = translate(responseType.getPatientSensitivityLevel());
		
		return new PatientSensitiveValue(level, msg);
	}
	
	public static gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterType[] translate(
		SortedSet<PassthroughParameter> parameters)
	{
		if(parameters == null)
			return null;
		gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterType[] result =
			new gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterType[parameters.size()];
		
		int count = 0;
		for(PassthroughParameter parameter : parameters)
		{
			result[count] = translate(parameter);
			count++;
		}
		
		return result;
	}

	/**
	 * 
	 * @param parameter
	 * @return
	 */
	public static gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterType translate(PassthroughParameter parameter)
	{
		gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterType result = 
			new gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterType();
		result.setParameterIndex(BigInteger.valueOf(parameter.getIndex()));
		
		result.setParameterType(translate(parameter.getParameterType()));
		
		gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterValueType value = 
			new gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterValueType();
		
		// ok if null
		value.setValue(parameter.getValue());
		value.setMultipleValue(translate(parameter.getMultipleValues()));
		result.setParameterValue(value);		
		
		return result;
	}
	
	/**
	 * 
	 * @param multiples
	 * @return
	 */
	public static gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterMultipleType translate(
		String [] multiples)
	{
		if(multiples == null)
			return null;
		gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterMultipleType result = 
			new gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterMultipleType();
		
		result.setMultipleValue(multiples);
		
		return result;
	}
	
	/**
	 * The Exam image input parameter when logging access to an exam image contains the URN in the 3rd ^ piece.
	 * For communicating with a Patch 83 VIX this URN piece must be base32 encoded/decoded
	 * @param inputParameter Query string containing Base32 encoded image URN in third piece 
	 * @return
	 */
	public static String translateDecodeExamImageAccessInputParameter(String inputParameter)
	{
		// the 3rd piece of the string is the image URN, it must be base32 decoded to receive from a P83 VIX
		String decodedInputParameter = "";
		String prefix = "";
		String pieces [] = StringUtils.Split(inputParameter, StringUtils.CARET);
		for(int i = 0; i < pieces.length; i++)
		{
			if(i == 2)
			{
				try
				{
					ImageURN imageUrn = URNFactory.create(pieces[i], SERIALIZATION_FORMAT.PATCH83_VFTP, ImageURN.class);
					decodedInputParameter += prefix + imageUrn.toString();
				}
				catch(URNFormatException urnfX)
				{
					getLogger().warn("Error converting string '" + pieces[i] + "' into ImageURN, cannot continue", urnfX);
					return null;
				}
			}
			else
			{
				decodedInputParameter += prefix + pieces[i];
			}
			prefix = StringUtils.CARET;
		}
		return decodedInputParameter;
	}
	
	/**
	 * The Exam image input parameter when logging access to an exam image contains the URN in the 3rd ^ piece.
	 * For communicating with a Patch 83 VIX this URN piece must be base32 encoded/decoded
	 * @param inputParameter Query string containing image URN in third piece which must be base 32 encoded
	 * @return
	 */
	public static String translateEncodeExamImageAccessInputParameter(String inputParameter)
	{
		// the 3rd piece of the string is the image URN, it must be base32 encoded to pass to a P83 VIX
		String encodedInputParameter = "";
		String prefix = "";
		String pieces [] = StringUtils.Split(inputParameter, StringUtils.CARET);
		for(int i = 0; i < pieces.length; i++)
		{
			if(i == 2)
			{
				try
				{
					ImageURN imageUrn = URNFactory.create(pieces[i], ImageURN.class);
					// encode as VFTP here ?
					encodedInputParameter += prefix + imageUrn.toString(SERIALIZATION_FORMAT.PATCH83_VFTP);
				}
				catch(URNFormatException urnfX)
				{
					getLogger().warn("Error converting string '" + pieces[i] + "' into ImageURN, cannot continue", urnfX);
					return null;
				}
			}
			else
			{
				encodedInputParameter += prefix + pieces[i];
			}
			prefix = StringUtils.CARET;
		}
		return encodedInputParameter;
	}
	
	/**
	 * This method converts a list of studies that match a progress note/radiology consult into a single study containing all of the images
	 * @param study
	 * @return
	 */
	public static gov.va.med.imaging.federation.webservices.types.v3.FederationStudyType translateToStudy(List<Study> studies)
	throws ParseException, TranslationException
	{
		gov.va.med.imaging.federation.webservices.types.v3.FederationStudyType result = 
			new gov.va.med.imaging.federation.webservices.types.v3.FederationStudyType();
		
		if(studies == null)
			return result;
		Study study = studies.get(0);
		int totalImageCount = 0;
		int totalSeriesCount = 0;
		totalImageCount = study.getImageCount();
		totalSeriesCount = study.getSeriesCount();
		result = translate(study);
		List<gov.va.med.imaging.federation.webservices.types.v3.FederationSeriesType> series = null;
		for(int i = 1; i < studies.size(); i++)
		{
			if(series == null)
			{
				series = new ArrayList<gov.va.med.imaging.federation.webservices.types.v3.FederationSeriesType>();
				if(result.getComponentSeries() != null)
				{
					for(gov.va.med.imaging.federation.webservices.types.v3.FederationSeriesType ser : result.getComponentSeries().getSeries())
					{
						series.add(ser);						
					}
				}				
			}
			
			Study currentStudy = studies.get(i);
			totalImageCount += currentStudy.getImageCount();
			totalSeriesCount += currentStudy.getSeriesCount();
			gov.va.med.imaging.federation.webservices.types.v3.FederationStudyType studyType = translate(currentStudy);
			if(studyType.getComponentSeries() != null)
			{
				for(gov.va.med.imaging.federation.webservices.types.v3.FederationSeriesType ser : studyType.getComponentSeries().getSeries())
				{
					series.add(ser);						
				}
			}
			
		}
		if(series != null)
		{
			gov.va.med.imaging.federation.webservices.types.v3.FederationStudyTypeComponentSeries federationStudyTypeComponentSeries = 
				new gov.va.med.imaging.federation.webservices.types.v3.FederationStudyTypeComponentSeries(
						series.toArray(new gov.va.med.imaging.federation.webservices.types.v3.FederationSeriesType[series.size()]));
			result.setComponentSeries(federationStudyTypeComponentSeries);
		}
		result.setSeriesCount(totalSeriesCount);
		result.setImageCount(totalImageCount);
		
		return result;		
	}
}
