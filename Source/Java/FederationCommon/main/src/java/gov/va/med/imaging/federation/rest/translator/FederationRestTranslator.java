/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 15, 2010
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
package gov.va.med.imaging.federation.rest.translator;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.GlobalArtifactIdentifierFactory;

import gov.va.med.HealthSummaryURN;
import gov.va.med.MediaType;
import gov.va.med.OID;
import gov.va.med.PatientArtifactIdentifierImpl;
import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.RoutingTokenImpl;
import gov.va.med.SERIALIZATION_FORMAT;
import gov.va.med.URN;
import gov.va.med.URNFactory;
import gov.va.med.exceptions.GlobalArtifactIdentifierFormatException;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.AbstractImagingURN;
import gov.va.med.imaging.CprsIdentifier;
import gov.va.med.imaging.ImageAnnotationURN;
import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.StudyURN;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.channels.ChecksumValue;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;

import gov.va.med.imaging.exceptions.OIDFormatException;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.ImageAccessLogEvent;
import gov.va.med.imaging.exchange.ImagingLogEvent;
import gov.va.med.imaging.exchange.ProcedureFilter;
import gov.va.med.imaging.exchange.ImageAccessLogEvent.ImageAccessLogEventType;
import gov.va.med.imaging.exchange.business.ArtifactResultError;
import gov.va.med.imaging.exchange.business.ArtifactResults;
import gov.va.med.imaging.exchange.business.Division;
import gov.va.med.imaging.exchange.business.DocumentFilter;
import gov.va.med.imaging.exchange.business.ElectronicSignatureResult;
import gov.va.med.imaging.exchange.business.HealthSummaryType;
import gov.va.med.imaging.exchange.business.Image;
import gov.va.med.imaging.exchange.business.ImageAccessReason;
import gov.va.med.imaging.exchange.business.ImageFormatQuality;
import gov.va.med.imaging.exchange.business.ImageFormatQualityList;
import gov.va.med.imaging.exchange.business.PassthroughInputMethod;
import gov.va.med.imaging.exchange.business.PassthroughParameter;
import gov.va.med.imaging.exchange.business.PassthroughParameterType;
import gov.va.med.imaging.exchange.business.Patient;
import gov.va.med.imaging.exchange.business.PatientMeansTestResult;
import gov.va.med.imaging.exchange.business.PatientSensitiveValue;
import gov.va.med.imaging.exchange.business.Series;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.exchange.business.StudySetResult;
import gov.va.med.imaging.exchange.business.User;
import gov.va.med.imaging.exchange.business.UserInformation;
import gov.va.med.imaging.exchange.business.Patient.PatientSex;
import gov.va.med.imaging.exchange.business.annotations.ImageAnnotation;
import gov.va.med.imaging.exchange.business.annotations.ImageAnnotationDetails;
import gov.va.med.imaging.exchange.business.annotations.ImageAnnotationSource;
import gov.va.med.imaging.exchange.business.annotations.ImageAnnotationUser;
import gov.va.med.imaging.exchange.business.documents.Document;
import gov.va.med.imaging.exchange.business.documents.DocumentSet;
import gov.va.med.imaging.exchange.business.documents.DocumentSetResult;
import gov.va.med.imaging.exchange.business.vistarad.ActiveExam;
import gov.va.med.imaging.exchange.business.vistarad.ActiveExams;
import gov.va.med.imaging.exchange.business.vistarad.Exam;
import gov.va.med.imaging.exchange.business.vistarad.ExamImage;
import gov.va.med.imaging.exchange.business.vistarad.ExamImages;
import gov.va.med.imaging.exchange.business.vistarad.ExamListResult;
import gov.va.med.imaging.exchange.business.vistarad.ExamSite;
import gov.va.med.imaging.exchange.business.vistarad.PatientRegistration;
import gov.va.med.imaging.exchange.enums.*;
import gov.va.med.imaging.exchange.enums.vistarad.ExamStatus;
import gov.va.med.imaging.exchange.translation.AbstractTranslator;
import gov.va.med.imaging.exchange.translation.exceptions.TranslationException;
import gov.va.med.imaging.federation.FederationArtifactResultError;
import gov.va.med.imaging.federation.FederationUser;
import gov.va.med.imaging.federation.rest.types.FederationActiveExamType;
import gov.va.med.imaging.federation.rest.types.FederationActiveExamsType;
import gov.va.med.imaging.federation.rest.types.FederationArtifactResultErrorCodeType;
import gov.va.med.imaging.federation.rest.types.FederationArtifactResultErrorSeverityType;
import gov.va.med.imaging.federation.rest.types.FederationArtifactResultErrorType;
import gov.va.med.imaging.federation.rest.types.FederationArtifactResultStatusType;
import gov.va.med.imaging.federation.rest.types.FederationArtifactResultsType;
import gov.va.med.imaging.federation.rest.types.FederationChecksumType;
import gov.va.med.imaging.federation.rest.types.FederationCprsIdentifierType;
import gov.va.med.imaging.federation.rest.types.FederationDivisionType;
import gov.va.med.imaging.federation.rest.types.FederationDocumentFilterType;
import gov.va.med.imaging.federation.rest.types.FederationDocumentSetResultType;
import gov.va.med.imaging.federation.rest.types.FederationDocumentSetType;
import gov.va.med.imaging.federation.rest.types.FederationDocumentType;
import gov.va.med.imaging.federation.rest.types.FederationElectronicSignatureResultType;
import gov.va.med.imaging.federation.rest.types.FederationExamImageType;
import gov.va.med.imaging.federation.rest.types.FederationExamImagesType;
import gov.va.med.imaging.federation.rest.types.FederationExamResultType;
import gov.va.med.imaging.federation.rest.types.FederationExamStatusType;
import gov.va.med.imaging.federation.rest.types.FederationExamType;
import gov.va.med.imaging.federation.rest.types.FederationFilterType;

import gov.va.med.imaging.federation.rest.types.FederationHealthSummaryType;
import gov.va.med.imaging.federation.rest.types.FederationImageAccessLogEventType;
import gov.va.med.imaging.federation.rest.types.FederationImageAccessLogEventTypeType;
import gov.va.med.imaging.federation.rest.types.FederationImageAccessReasonType;
import gov.va.med.imaging.federation.rest.types.FederationImageAccessReasonTypeHolderType;
import gov.va.med.imaging.federation.rest.types.FederationImageAccessReasonTypeType;
import gov.va.med.imaging.federation.rest.types.FederationImageAnnotationDetailsType;
import gov.va.med.imaging.federation.rest.types.FederationImageAnnotationSourceType;
import gov.va.med.imaging.federation.rest.types.FederationImageAnnotationType;
import gov.va.med.imaging.federation.rest.types.FederationImageAnnotationUserType;
import gov.va.med.imaging.federation.rest.types.FederationImageFormatQualitiesType;
import gov.va.med.imaging.federation.rest.types.FederationImageFormatQualityType;
import gov.va.med.imaging.federation.rest.types.FederationImageType;
import gov.va.med.imaging.federation.rest.types.FederationImagingLogEventType;
import gov.va.med.imaging.federation.rest.types.FederationMediaType;
import gov.va.med.imaging.federation.rest.types.FederationObjectStatusType;
import gov.va.med.imaging.federation.rest.types.FederationPatientMeansTestResultType;
import gov.va.med.imaging.federation.rest.types.FederationPatientRegistrationType;
import gov.va.med.imaging.federation.rest.types.FederationPatientSensitiveType;
import gov.va.med.imaging.federation.rest.types.FederationPatientSensitivityLevelType;
import gov.va.med.imaging.federation.rest.types.FederationPatientSexType;
import gov.va.med.imaging.federation.rest.types.FederationPatientType;
import gov.va.med.imaging.federation.rest.types.FederationRemoteMethodParameterType;
import gov.va.med.imaging.federation.rest.types.FederationRemoteMethodParameterTypeType;
import gov.va.med.imaging.federation.rest.types.FederationRemoteMethodParameterValueType;
import gov.va.med.imaging.federation.rest.types.FederationRemoteMethodType;
import gov.va.med.imaging.federation.rest.types.FederationSeriesType;
import gov.va.med.imaging.federation.rest.types.FederationStringArrayType;
import gov.va.med.imaging.federation.rest.types.FederationStudyDeletedImageStateType;
import gov.va.med.imaging.federation.rest.types.FederationStudyLoadLevelType;
import gov.va.med.imaging.federation.rest.types.FederationStudyResultType;
import gov.va.med.imaging.federation.rest.types.FederationStudyType;
import gov.va.med.imaging.federation.rest.types.FederationUserInformationType;
import gov.va.med.imaging.federation.rest.types.FederationUserType;

/**
 * @author vhaiswwerfej
 *
 */
public class FederationRestTranslator
extends AbstractTranslator
{
	private static Map<StudyLoadLevel, FederationStudyLoadLevelType> loadLevelMap;
	private static Map<PassthroughParameterType, FederationRemoteMethodParameterTypeType> parameterTypeMap;
	private static Map<ExamStatus, FederationExamStatusType> examStatusMap;
	private static Map<ImageAccessLogEventType, FederationImageAccessLogEventTypeType> imageAccessMap;
	private static Map<PatientSensitivityLevel, FederationPatientSensitivityLevelType> patientSensitiveLevelMap;
	private static Map<PatientSex, FederationPatientSexType> patientSexMap;
	private static Map<ObjectStatus, FederationObjectStatusType> objectStatusMap;
	private static Map<StudyDeletedImageState, FederationStudyDeletedImageStateType> studyDeletedImageStateMap;
	private static Map<ArtifactResultStatus, FederationArtifactResultStatusType> artifactResultStatusMap;
	private static Map<MediaType, FederationMediaType> mediaTypeMap;
	private static Map<ArtifactResultErrorCode, FederationArtifactResultErrorCodeType> artifactResultErrorCodeMap;
	private static Map<ArtifactResultErrorSeverity, FederationArtifactResultErrorSeverityType> artifactResultErrorSeverityMap;
	private static Map<ImageAnnotationSource, FederationImageAnnotationSourceType> imageAnnotationSourceMap;
	private static Map<ImageAccessReasonType, FederationImageAccessReasonTypeType> imageAccessReasonTypesMap;
	
	static
	{
		loadLevelMap = new HashMap<StudyLoadLevel, FederationStudyLoadLevelType>();
		loadLevelMap.put(StudyLoadLevel.FULL, FederationStudyLoadLevelType.FULL);
		loadLevelMap.put(StudyLoadLevel.STUDY_AND_REPORT, FederationStudyLoadLevelType.STUDY_AND_REPORT);
		loadLevelMap.put(StudyLoadLevel.STUDY_AND_IMAGES, FederationStudyLoadLevelType.STUDY_AND_IMAGES);
		loadLevelMap.put(StudyLoadLevel.STUDY_ONLY, FederationStudyLoadLevelType.STUDY_ONLY);
		
		parameterTypeMap = new HashMap<PassthroughParameterType, FederationRemoteMethodParameterTypeType>();
		parameterTypeMap.put(PassthroughParameterType.literal, FederationRemoteMethodParameterTypeType.LITERAL);
		parameterTypeMap.put(PassthroughParameterType.list, FederationRemoteMethodParameterTypeType.LIST);
		parameterTypeMap.put(PassthroughParameterType.reference, FederationRemoteMethodParameterTypeType.REFERENCE);
		
		examStatusMap = new HashMap<ExamStatus, FederationExamStatusType>();
		examStatusMap.put(ExamStatus.INTERPRETED, FederationExamStatusType.INTERPRETED);
		examStatusMap.put(ExamStatus.NOT_INTERPRETED, FederationExamStatusType.NOT_INTERPRETED);
		
		imageAccessMap = new HashMap<ImageAccessLogEventType, FederationImageAccessLogEventTypeType>();
		imageAccessMap.put(ImageAccessLogEventType.IMAGE_COPY, FederationImageAccessLogEventTypeType.IMAGE_COPY);
		imageAccessMap.put(ImageAccessLogEventType.IMAGE_PRINT, FederationImageAccessLogEventTypeType.IMAGE_PRINT); 
		imageAccessMap.put(ImageAccessLogEventType.PATIENT_ID_MISMATCH, FederationImageAccessLogEventTypeType.PATIENT_ID_MISMATCH); 
		imageAccessMap.put(ImageAccessLogEventType.RESTRICTED_ACCESS, FederationImageAccessLogEventTypeType.RESTRICTED_ACCESS); 
		imageAccessMap.put(ImageAccessLogEventType.IMAGE_ACCESS,  FederationImageAccessLogEventTypeType.IMAGE_ACCESS);
		
		patientSensitiveLevelMap = new HashMap<PatientSensitivityLevel, FederationPatientSensitivityLevelType>();
		patientSensitiveLevelMap.put(PatientSensitivityLevel.ACCESS_DENIED, FederationPatientSensitivityLevelType.ACCESS_DENIED);
		patientSensitiveLevelMap.put(PatientSensitivityLevel.DATASOURCE_FAILURE, FederationPatientSensitivityLevelType.DATASOURCE_FAILURE);
		patientSensitiveLevelMap.put(PatientSensitivityLevel.DISPLAY_WARNING, FederationPatientSensitivityLevelType.DISPLAY_WARNING);
		patientSensitiveLevelMap.put(PatientSensitivityLevel.DISPLAY_WARNING_CANNOT_CONTINUE, 
				FederationPatientSensitivityLevelType.DISPLAY_WARNING_CANNOT_CONTINUE);
		patientSensitiveLevelMap.put(PatientSensitivityLevel.DISPLAY_WARNING_REQUIRE_OK, FederationPatientSensitivityLevelType.DISPLAY_WARNING_REQUIRE_OK);
		patientSensitiveLevelMap.put(PatientSensitivityLevel.NO_ACTION_REQUIRED, FederationPatientSensitivityLevelType.NO_ACTION_REQUIRED);
		
		patientSexMap = new HashMap<PatientSex, FederationPatientSexType>();
		patientSexMap.put(PatientSex.Male, FederationPatientSexType.Male);
		patientSexMap.put(PatientSex.Female, FederationPatientSexType.Female);
		patientSexMap.put(PatientSex.Unknown, FederationPatientSexType.Unknown);
		
		objectStatusMap = new HashMap<ObjectStatus, FederationObjectStatusType>();
		objectStatusMap.put(ObjectStatus.CONTROLLED, FederationObjectStatusType.CONTROLLED);
		objectStatusMap.put(ObjectStatus.DELETED, FederationObjectStatusType.DELETED);
		objectStatusMap.put(ObjectStatus.IMAGE_GROUP, FederationObjectStatusType.IMAGE_GROUP);
		objectStatusMap.put(ObjectStatus.IN_PROGRESS, FederationObjectStatusType.IN_PROGRESS);
		objectStatusMap.put(ObjectStatus.NEEDS_REFRESH, FederationObjectStatusType.NEEDS_REFRESH);
		objectStatusMap.put(ObjectStatus.NEEDS_REVIEW, FederationObjectStatusType.NEEDS_REVIEW);
		objectStatusMap.put(ObjectStatus.NO_STATUS, FederationObjectStatusType.NO_STATUS);
		objectStatusMap.put(ObjectStatus.QA_REVIEWED, FederationObjectStatusType.QA_REVIEWED);
		objectStatusMap.put(ObjectStatus.QUESTIONABLE_INTEGRITY, FederationObjectStatusType.QUESTIONABLE_INTEGRITY);
		objectStatusMap.put(ObjectStatus.RAD_EXAM_STATUS_BLOCK, FederationObjectStatusType.RAD_EXAM_STATUS_BLOCK);
		objectStatusMap.put(ObjectStatus.TIU_AUTHORIZATION_BLOCK, FederationObjectStatusType.TIU_AUTHORIZATION_BLOCK);
		objectStatusMap.put(ObjectStatus.UNKNOWN, FederationObjectStatusType.UNKNOWN);
		objectStatusMap.put(ObjectStatus.VIEWABLE, FederationObjectStatusType.VIEWABLE);
		
		studyDeletedImageStateMap = new HashMap<StudyDeletedImageState, FederationStudyDeletedImageStateType>();
		studyDeletedImageStateMap.put(StudyDeletedImageState.cannotIncludeDeletedImages, FederationStudyDeletedImageStateType.cannotIncludeDeletedImages);
		studyDeletedImageStateMap.put(StudyDeletedImageState.doesNotIncludeDeletedImages, FederationStudyDeletedImageStateType.doesNotIncludeDeletedImages);
		studyDeletedImageStateMap.put(StudyDeletedImageState.includesDeletedImages, FederationStudyDeletedImageStateType.includesDeletedImages);
		
		artifactResultStatusMap = new HashMap<ArtifactResultStatus, FederationArtifactResultStatusType>();
		artifactResultStatusMap.put(ArtifactResultStatus.fullResult, FederationArtifactResultStatusType.fullResult);
		artifactResultStatusMap.put(ArtifactResultStatus.partialResult, FederationArtifactResultStatusType.partialResult);
		artifactResultStatusMap.put(ArtifactResultStatus.errorResult, FederationArtifactResultStatusType.errorResult);
		
		mediaTypeMap = new HashMap<MediaType, FederationMediaType>();
		mediaTypeMap.put(MediaType.APPLICATION_DICOM, FederationMediaType.APPLICATION_DICOM);
		mediaTypeMap.put(MediaType.APPLICATION_DOC, FederationMediaType.APPLICATION_DOC);
		mediaTypeMap.put(MediaType.APPLICATION_OCTETSTREAM, FederationMediaType.APPLICATION_OCTETSTREAM);
		mediaTypeMap.put(MediaType.APPLICATION_PDF, FederationMediaType.APPLICATION_PDF);
		mediaTypeMap.put(MediaType.APPLICATION_EXCEL, FederationMediaType.APPLICATION_EXCEL);
		mediaTypeMap.put(MediaType.APPLICATION_PPT, FederationMediaType.APPLICATION_PPT);
		mediaTypeMap.put(MediaType.APPLICATION_RTF, FederationMediaType.APPLICATION_RTF);
		mediaTypeMap.put(MediaType.APPLICATION_DOCX, FederationMediaType.APPLICATION_DOCX);
		mediaTypeMap.put(MediaType.AUDIO_MP4, FederationMediaType.AUDIO_MP4);
		mediaTypeMap.put(MediaType.AUDIO_MPEG, FederationMediaType.AUDIO_MPEG);
		mediaTypeMap.put(MediaType.AUDIO_WAV, FederationMediaType.AUDIO_WAV);
		mediaTypeMap.put(MediaType.IMAGE_BMP, FederationMediaType.IMAGE_BMP);
		mediaTypeMap.put(MediaType.IMAGE_XBMP, FederationMediaType.IMAGE_XBMP);
		mediaTypeMap.put(MediaType.IMAGE_J2K, FederationMediaType.IMAGE_J2K);
		mediaTypeMap.put(MediaType.IMAGE_JP2, FederationMediaType.IMAGE_JP2);
		mediaTypeMap.put(MediaType.IMAGE_JPEG, FederationMediaType.IMAGE_JPEG);
		mediaTypeMap.put(MediaType.IMAGE_PNG, FederationMediaType.IMAGE_PNG);
		mediaTypeMap.put(MediaType.IMAGE_TGA, FederationMediaType.IMAGE_TGA);
		mediaTypeMap.put(MediaType.IMAGE_TIFF, FederationMediaType.IMAGE_TIFF);
		mediaTypeMap.put(MediaType.MULTIPART_FORM_DATA, FederationMediaType.MULTIPART_FORM_DATA);
		mediaTypeMap.put(MediaType.MULTIPART_MIXED, FederationMediaType.MULTIPART_MIXED);
		mediaTypeMap.put(MediaType.TEXT_CSS, FederationMediaType.TEXT_CSS);
		mediaTypeMap.put(MediaType.TEXT_CSV, FederationMediaType.TEXT_CSV);
		mediaTypeMap.put(MediaType.TEXT_ENRICHED, FederationMediaType.TEXT_ENRICHED);
		mediaTypeMap.put(MediaType.TEXT_HTML, FederationMediaType.TEXT_HTML);
		mediaTypeMap.put(MediaType.TEXT_PLAIN, FederationMediaType.TEXT_PLAIN);
		mediaTypeMap.put(MediaType.TEXT_RTF, FederationMediaType.TEXT_RTF);
		mediaTypeMap.put(MediaType.TEXT_TSV, FederationMediaType.TEXT_TSV);
		mediaTypeMap.put(MediaType.TEXT_URI_LIST, FederationMediaType.TEXT_URI_LIST);
		mediaTypeMap.put(MediaType.TEXT_XML, FederationMediaType.TEXT_XML);
		mediaTypeMap.put(MediaType.TEXT_XML_EXTERNAL_PARSED_ENTITY, FederationMediaType.TEXT_XML_EXTERNAL_PARSED_ENTITY);		
		mediaTypeMap.put(MediaType.VIDEO_AVI, FederationMediaType.VIDEO_AVI);
		mediaTypeMap.put(MediaType.VIDEO_BMPEG, FederationMediaType.VIDEO_BMPEG);
		mediaTypeMap.put(MediaType.VIDEO_JPEG, FederationMediaType.VIDEO_JPEG);
		mediaTypeMap.put(MediaType.VIDEO_JPEG2000, FederationMediaType.VIDEO_JPEG2000);
		mediaTypeMap.put(MediaType.VIDEO_MP4, FederationMediaType.VIDEO_MP4);
		mediaTypeMap.put(MediaType.VIDEO_MPEG, FederationMediaType.VIDEO_MPEG);
		mediaTypeMap.put(MediaType.VIDEO_MPEG4_GENERIC, FederationMediaType.VIDEO_MPEG4_GENERIC);
		mediaTypeMap.put(MediaType.VIDEO_OGG, FederationMediaType.VIDEO_OGG);
		mediaTypeMap.put(MediaType.VIDEO_QUICKTIME, FederationMediaType.VIDEO_QUICKTIME);
		
		artifactResultErrorCodeMap = new HashMap<ArtifactResultErrorCode, FederationArtifactResultErrorCodeType>();
		artifactResultErrorCodeMap.put(ArtifactResultErrorCode.authorizationException, FederationArtifactResultErrorCodeType.authorizationException);
		artifactResultErrorCodeMap.put(ArtifactResultErrorCode.internalException, FederationArtifactResultErrorCodeType.internalException);
		artifactResultErrorCodeMap.put(ArtifactResultErrorCode.invalidRequestException, FederationArtifactResultErrorCodeType.invalidRequestException);
		artifactResultErrorCodeMap.put(ArtifactResultErrorCode.timeoutException, FederationArtifactResultErrorCodeType.timeoutException);
		artifactResultErrorCodeMap.put(ArtifactResultErrorCode.unknownPatientId, FederationArtifactResultErrorCodeType.unknownPatientId);
		
		artifactResultErrorSeverityMap = new HashMap<ArtifactResultErrorSeverity, FederationArtifactResultErrorSeverityType>();
		artifactResultErrorSeverityMap.put(ArtifactResultErrorSeverity.error, FederationArtifactResultErrorSeverityType.error);
		artifactResultErrorSeverityMap.put(ArtifactResultErrorSeverity.warning, FederationArtifactResultErrorSeverityType.warning);
		
		imageAnnotationSourceMap = new HashMap<ImageAnnotationSource, FederationImageAnnotationSourceType>();
		imageAnnotationSourceMap.put(ImageAnnotationSource.clinicalDisplay, FederationImageAnnotationSourceType.clinicalDisplay);
		imageAnnotationSourceMap.put(ImageAnnotationSource.vistaRad, FederationImageAnnotationSourceType.vistaRad);
		imageAnnotationSourceMap.put(ImageAnnotationSource.clinicalCapture, FederationImageAnnotationSourceType.clinicalCapture);
























		
		imageAccessReasonTypesMap = new HashMap<ImageAccessReasonType, FederationImageAccessReasonTypeType>();
		imageAccessReasonTypesMap.put(ImageAccessReasonType.changingImageStatus, FederationImageAccessReasonTypeType.changingImageStatus);
		imageAccessReasonTypesMap.put(ImageAccessReasonType.copyImage, FederationImageAccessReasonTypeType.copyImage);
		imageAccessReasonTypesMap.put(ImageAccessReasonType.deleteImage, FederationImageAccessReasonTypeType.deleteImage);
		imageAccessReasonTypesMap.put(ImageAccessReasonType.printImage, FederationImageAccessReasonTypeType.printImage);















	}
	
	public static FederationCprsIdentifierType translate(CprsIdentifier cprsIdentifier)
	{
		FederationCprsIdentifierType result =
			new FederationCprsIdentifierType();
		
		result.setCprsIdentifier(cprsIdentifier.getCprsIdentifier());
		
		return result;
	}
	
	public static ImageAccessLogEvent translate(FederationImageAccessLogEventType logEventType) 
	{
		if(logEventType == null)
			return null;

		ImageAccessLogEventType imageAccessLogEventType = 
			translate(logEventType.getEventType());

		ImageAccessLogEvent result = 
			new ImageAccessLogEvent(logEventType.getImageId(), "", logEventType.getPatientIcn(), 
					logEventType.getSiteNumber(), System.currentTimeMillis(), 
					logEventType.getReasonCode(), logEventType.getReasonDescription(),
					imageAccessLogEventType, logEventType.getUserSiteNumber());
		
		return result;
	}
	
	public static FederationImageAccessLogEventType translate(
			ImageAccessLogEvent logEvent)
	{
		FederationImageAccessLogEventType result = new FederationImageAccessLogEventType();
		result.setImageId(logEvent.getImageIen());
		result.setPatientIcn(logEvent.getPatientIcn());
		result.setReasonCode(logEvent.getReasonCode());
		result.setReasonDescription(logEvent.getReasonDescription());
		result.setSiteNumber(logEvent.getSiteNumber());
		result.setEventType(translate(logEvent.getEventType()));
		result.setUserSiteNumber(logEvent.getUserSiteNumber());
		return result;
	}
	
	public static ImageAccessLogEventType translate(FederationImageAccessLogEventTypeType eventType) 
	{
		for( Entry<ImageAccessLogEventType, FederationImageAccessLogEventTypeType> entry : FederationRestTranslator.imageAccessMap.entrySet() )
			if( entry.getValue() == eventType )
				return entry.getKey();
		
		return ImageAccessLogEventType.IMAGE_ACCESS;
	}
	
	public static FederationImageAccessLogEventTypeType translate(ImageAccessLogEventType eventType) 
	{
		for( Entry<ImageAccessLogEventType, FederationImageAccessLogEventTypeType> entry : FederationRestTranslator.imageAccessMap.entrySet() )
			if( entry.getKey() == eventType )
				return entry.getValue();
		
		return FederationImageAccessLogEventTypeType.IMAGE_ACCESS;
	}
	
	public static String [] translateStringArray(FederationStringArrayType values)
	{
		if(values == null)
			return null;
		// Jersey seems to convert empty string arrays into null responses
		// when requesting treating sites for a patient, the result is expected to be an empty array if no sites for the patient
		if(values.getValues() == null)
			return new String[0];
		return values.getValues();
	}
	
	public static FederationStringArrayType translateStringList(List<String> values)
	{
		if(values == null)
			return null;
		FederationStringArrayType result = new FederationStringArrayType();
		String [] array = new String[values.size()];
		for(int i = 0; i < values.size(); i++)
		{
			array[i] = values.get(i);
		}
		result.setValues(array);
		
		return result;
	}
	
	public static FederationStringArrayType translateStringArray(String [] values)
	{
		FederationStringArrayType result = new FederationStringArrayType();
		result.setValues(values);
		
		return result;
	}
	
	public static PatientRegistration translate(
		FederationPatientRegistrationType patientRegistration)
	{
		PatientRegistration result = new PatientRegistration();
		result.setCptCode(patientRegistration.getCptCode());
		result.setPatientIcn(patientRegistration.getPatientIcn());
		return result;
	}
	
	public static FederationPatientRegistrationType translate(
		PatientRegistration patientRegistration)
	{
		FederationPatientRegistrationType result = new FederationPatientRegistrationType();
		result.setCptCode(patientRegistration.getCptCode());
		result.setPatientIcn(patientRegistration.getPatientIcn());
		return result;
	}	
	
	public static ExamListResult translate(FederationExamResultType examResult)
	throws URNFormatException
	{
		if(examResult == null)
		{
			getLogger().error("Null FederationExamResultType, returning empty ExamListResult");
			return ExamListResult.createFullResult(null);
		}
		List<Exam> exams = translate(examResult.getExams());
		ArtifactResultStatus artifactResultStatus = translate(examResult.getArtifactResultStatus());
		List<ArtifactResultError> artifactResultErrors = translate(examResult.getErrors());		
		return ExamListResult.create(exams, artifactResultStatus, artifactResultErrors);
	}
	
	private static List<Exam> translate(FederationExamType [] exams)
	throws URNFormatException
	{
		if(exams == null)
		{
			getLogger().warn("Received null exams, returning empty array of exams.");
			return new ArrayList<Exam>(0);
		}
		List<Exam> result = new ArrayList<Exam>(exams.length);
		
		for(FederationExamType exam : exams)
		{
			result.add(translate(exam));
		}		
		return result;
	}
	
	public static FederationExamResultType translate(
		ExamSite examSite) 
	throws TranslationException
	{
		FederationExamResultType result = new FederationExamResultType();
		if(examSite == null)
			return result;
		FederationExamType [] exams = new FederationExamType[examSite.size()];
		
		int i = 0;
		for(Exam exam : examSite)
			exams[i++] = translate(exam);
		
		result.setExams(exams);
		result.setArtifactResultStatus(translate(examSite.getArtifactResultStatus()));
		result.setErrors(translateArtifactResultErrorList(examSite.getArtifactResultErrors()));
		return result;
	}
	
	public static ActiveExam translate(FederationActiveExamType activeExam)
	{
		ActiveExam result = new ActiveExam(
			activeExam.getSiteNumber(), 
			activeExam.getExamId(), 
			activeExam.getPatientIcn());
		result.setRawValue(activeExam.getRawValue());		
		return result;
	}
	
	public static ActiveExams translate(FederationActiveExamsType activeExams)
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
		{
			for(FederationActiveExamType activeExam : activeExams.getActiveExams())
				result.add(translate(activeExam));
		}
		
		return result;
	}
	
	public static FederationActiveExamType translate(
			ActiveExam activeExam)
	{
		FederationActiveExamType result = new FederationActiveExamType();
		
		result.setExamId(activeExam.getExamId());
		result.setPatientIcn(activeExam.getPatientIcn());
		result.setRawValue(activeExam.getRawValue());
		result.setSiteNumber(activeExam.getSiteNumber());
		
		return result;
	}
	
	public static FederationActiveExamsType translate(
			ActiveExams activeExams) 
	{
		FederationActiveExamsType result = new FederationActiveExamsType();
		result.setRawHeader1(activeExams.getRawHeader1());
		result.setRawHeader2(activeExams.getRawHeader2());
		result.setSiteNumber(activeExams.getSiteNumber());
		
		FederationActiveExamType [] activeExamArray = new FederationActiveExamType[activeExams.size()];
		int i = 0;
		for(ActiveExam activeExam : activeExams)
		{
			activeExamArray[i] = translate(activeExam);
			i++;
		}
		result.setActiveExams(activeExamArray);
		
		return result;
	}
	
	public static ExamImage translate(FederationExamImageType examImage)
	throws URNFormatException
	{
		if(examImage == null)
			return null;
		ExamImage result = ExamImage.create(
			examImage.getSiteNumber(), 
			examImage.getImageId(), 
			examImage.getExamId(),
			examImage.getPatientIcn());
		result.setDiagnosticFilePath(examImage.getBigImageFilename());
		result.setPatientName("");
		result.setAlienSiteNumber(examImage.getAlienSiteNumber());
		return result;
	}
	
	public static ExamImages translate(FederationExamImagesType examImages)
	throws URNFormatException
	{
		ExamImages result = new ExamImages(examImages.getRawHeader(), false);		
		
		if(examImages.getImages() != null)
		{		
			for(FederationExamImageType examImage : examImages.getImages())
			{
				ExamImage img = translate(examImage);
				if(img != null)
					result.add(img);
			}
		}
		return result;	
	}
	
	public static Exam translate(FederationExamType exam)
	throws URNFormatException
	{
		Exam result = Exam.create(
			exam.getSiteNumber(), 
			exam.getExamId(), 
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
	
	public static FederationExamType translate(Exam exam) 
	{
		FederationExamType result = new FederationExamType();
		
		result.setCptCode(exam.getCptCode());
		result.setExamId(exam.getExamId());
		result.setExamStatus( translate(exam.getExamStatus()) );
		result.setModality(exam.getModality());
		result.setPatientIcn(exam.getPatientIcn());
		result.setPatientName(exam.getPatientName());
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
			FederationExamImagesType examImages = translate(exam.getImages());
			result.setExamImages(examImages);
		}		
		return result;
	}
	
	public static FederationExamImageType translate(ExamImage examImage)
	{
		FederationExamImageType result = new FederationExamImageType();
		
		result.setBigImageFilename(examImage.getDiagnosticFilePath());
		result.setExamId(examImage.getExamId());
		result.setImageId(examImage.getImageId()) ;
		result.setPatientIcn(examImage.getPatientIcn());
		result.setSiteNumber(examImage.getSiteNumber());
		result.setAlienSiteNumber(examImage.getAlienSiteNumber());
		
		return result;
	}
	
	public static FederationExamImagesType translate(ExamImages examImages)
	{
		FederationExamImagesType result = new FederationExamImagesType();
		result.setRawHeader(examImages.getRawHeader());
		FederationExamImageType [] images = new FederationExamImageType[examImages.size()];
		int i = 0;
		for(ExamImage image : examImages)
		{
			images[i] = translate(image);
			i++;
		}
		result.setImages(images);
		
		return result;
	}
	
	public static FederationExamStatusType translate(
			ExamStatus examStatus)
		{
			for( Entry<ExamStatus, FederationExamStatusType> entry : FederationRestTranslator.examStatusMap.entrySet() )
				if( entry.getKey() == examStatus )
					return entry.getValue();
			
			return FederationExamStatusType.INTERPRETED;
		}
		
		
		public static ExamStatus translate(
				FederationExamStatusType statusType)
		{
			for( Entry<ExamStatus, FederationExamStatusType> entry : FederationRestTranslator.examStatusMap.entrySet() )
				if( entry.getValue() == statusType )
					return entry.getKey();
			
			return ExamStatus.INTERPRETED;
		}
	
	public static FederationRemoteMethodType translate(PassthroughInputMethod inputMethod, String imagingSecurityContext)
	{
		SortedSet<PassthroughParameter> parameters = inputMethod.getParameters();
		FederationRemoteMethodType result = new FederationRemoteMethodType();
		result.setImagingSecurityContext(imagingSecurityContext);
		result.setMethodName(inputMethod.getMethodName());
		FederationRemoteMethodParameterType [] parameterTypes = new FederationRemoteMethodParameterType[parameters.size()];
		int i = 0;
		for(PassthroughParameter parameter : parameters)
		{
			parameterTypes[i] = translate(parameter);
			i++;
		}
		result.setParameters(parameterTypes);
		return result;
	}
	
	public static PassthroughInputMethod translate(FederationRemoteMethodType remoteMethodType)
	{
		PassthroughInputMethod result = new PassthroughInputMethod(remoteMethodType.getMethodName());
		
		if(remoteMethodType.getParameters() != null)
			for(FederationRemoteMethodParameterType parameter : remoteMethodType.getParameters())
				result.getParameters().add(translate(parameter));
				
		return result;
	}
	
	public static FederationRemoteMethodParameterType translate(PassthroughParameter parameter)
	{
		FederationRemoteMethodParameterType result = 
			new FederationRemoteMethodParameterType();
		result.setParameterIndex(parameter.getIndex());
		
		result.setParameterType(translate(parameter.getParameterType()));
		
		FederationRemoteMethodParameterValueType value = 
			new FederationRemoteMethodParameterValueType();
		
		// ok if null
		value.setValue(parameter.getValue());
		value.setMultipleValues(parameter.getMultipleValues());
		result.setParameterValue(value);		
		
		return result;
	}
	
	public static FederationRemoteMethodParameterTypeType translate(
			PassthroughParameterType parameterType)
	{		
		for( Entry<PassthroughParameterType, FederationRemoteMethodParameterTypeType> entry : FederationRestTranslator.parameterTypeMap.entrySet() )
			if( entry.getKey() == parameterType )
				return entry.getValue();
		
		return FederationRemoteMethodParameterTypeType.LITERAL;
	}	
	
	/*
	public static PassthroughInputMethod translate(String methodName, FederationRemoteMethodParameterType [] parameters)
	{
		if(methodName == null)
			return null;
		PassthroughInputMethod result = new PassthroughInputMethod(methodName);
		if(parameters != null)
			for(FederationRemoteMethodParameterType parameter : parameters)
				result.getParameters().add(translate(parameter));
		
		return result;
	}*/	
	
	public static PassthroughParameter translate(FederationRemoteMethodParameterType parameter)
	{
		PassthroughParameter result = new PassthroughParameter();
		result.setIndex(parameter.getParameterIndex());
		
		result.setParameterType(translate(parameter.getParameterType()));
		FederationRemoteMethodParameterValueType parameterValue = parameter.getParameterValue();
		if(parameterValue == null)
		{
			result.setValue(null);
			result.setMultipleValues(null);
		}
		else
		{
			String value = parameterValue.getValue();
			result.setValue(value);
			String [] multipleValues = parameterValue.getMultipleValues();
			result.setMultipleValues(multipleValues);
		}
		
		return result;
	}
	
	public static PassthroughParameterType translate(FederationRemoteMethodParameterTypeType parameterType)
	{
		PassthroughParameterType result = PassthroughParameterType.literal;
		if(parameterType == FederationRemoteMethodParameterTypeType.LIST)
			result = PassthroughParameterType.list;
		else if(parameterType == FederationRemoteMethodParameterTypeType.REFERENCE)
			result = PassthroughParameterType.reference;
		return result;
	}
	
	public static DocumentFilter translate(FederationDocumentFilterType filterType)
	throws TranslationException
	{
		DocumentFilter result = new DocumentFilter(filterType.getPatientId(), filterType.getClassCode(), 
				filterType.getPracticeSettingCode(), filterType.getCreationTimeFrom(), filterType.getCreationTimeTo(),
				filterType.getServiceStartTimeFrom(), filterType.getServiceStartTimeTo(), 
				filterType.getServiceStopTimeFrom(), filterType.getServiceStopTimeTo(), filterType.getHealthcareFacilityTypeCode(),
				filterType.getEventCodes(), filterType.getConfidentialityCodes(), filterType.getAuthor(), filterType.getFormatCode(),
				filterType.getEntryStatus(), filterType.getMaxResultsCount(), filterType.getSiteNumber(),
				filterType.isUseAlternatePatientId()
		);
		
		result.setStudy_class(filterType.get_class());
		result.setStudy_package(filterType.get_package());
		result.setStudy_event(filterType.getEvent());
		result.setFromDate(filterType.getFromDate());
		result.setIncludeDeleted(filterType.isIncludeDeletedImages());
		result.setOrigin(filterType.getOrigin());
		result.setStudy_specialty(filterType.getSpecialty());
		if((filterType.getStudyId() != null) && (filterType.getStudyId().length() > 0))
		{
			try
			{
				URN gai = URNFactory.create(filterType.getStudyId());
				if(gai instanceof GlobalArtifactIdentifier)
					result.setStudyId((GlobalArtifactIdentifier)gai);
			}
			catch (URNFormatException x)
			{
				throw new TranslationException(filterType.getStudyId() + 
					" cannot be transformed into a GlobalArtifactIdentifier realization."
				);
			}
		}
		result.setToDate(filterType.getToDate());
		result.setStudy_type(filterType.getTypes());
		
		return result;
	}
	
	public static FederationDocumentFilterType translate(DocumentFilter filter)
	{
		FederationDocumentFilterType documentFilterType = new FederationDocumentFilterType();
		documentFilterType.set_class(filter.getStudy_class());
		documentFilterType.set_package(filter.getStudy_package());
		documentFilterType.setEvent(filter.getStudy_event());
		documentFilterType.setFromDate(filter.getFromDate());
		documentFilterType.setIncludeDeletedImages(filter.isIncludeDeleted());
		documentFilterType.setOrigin(filter.getOrigin());
		documentFilterType.setSpecialty(filter.getStudy_specialty());
		documentFilterType.setStudyId(filter.getStudyId() == null ? "" : filter.getStudyId().toString());
		documentFilterType.setToDate(filter.getToDate());
		documentFilterType.setTypes(filter.getStudy_type());
		
		documentFilterType.setPatientId(filter.getPatientId());
		documentFilterType.setClassCode(filter.getClassCode());
		documentFilterType.setPracticeSettingCode(filter.getPracticeSettingCode());
		documentFilterType.setCreationTimeFrom(filter.getCreationTimeFrom());
		documentFilterType.setCreationTimeTo(filter.getCreationTimeTo());
		documentFilterType.setServiceStartTimeFrom(filter.getServiceStartTimeFrom());
		documentFilterType.setServiceStartTimeTo(filter.getServiceStartTimeTo());
		documentFilterType.setServiceStopTimeFrom(filter.getServiceStopTimeFrom());
		documentFilterType.setServiceStopTimeTo(filter.getServiceStopTimeTo());
		documentFilterType.setHealthcareFacilityTypeCode(filter.getHealthcareFacilityTypeCode());
		documentFilterType.setEventCodes(filter.getEventCodes());
		documentFilterType.setConfidentialityCodes(filter.getConfidentialityCodes());
		documentFilterType.setAuthor(filter.getAuthor());
		documentFilterType.setFormatCode(filter.getFormatCode());
		documentFilterType.setEntryStatus(filter.getEntryStatus());
		documentFilterType.setMaxResultsCount(filter.getMaximumResults());
		documentFilterType.setSiteNumber(filter.getSiteNumber());
		documentFilterType.setUseAlternatePatientId(filter.isUseAlternatePatientId());
		
		return documentFilterType;
	}
	
	public static FederationFilterType translate(StudyFilter filterType)
	{
		FederationFilterType filter = new FederationFilterType();
		if(filterType != null)
		{
			filter.setFromDate(filterType.getFromDate());
			filter.setToDate(filterType.getToDate());
			filter.set_class(filterType.getStudy_class()== null ? "" : filterType.getStudy_class());
			filter.setEvent(filterType.getStudy_event() == null ? "" : filterType.getStudy_event());
			filter.set_package(filterType.getStudy_package() == null ? "" : filterType.getStudy_package());
			filter.setSpecialty(filterType.getStudy_specialty() == null ? "" : filterType.getStudy_specialty());
			filter.setTypes(filterType.getStudy_type() == null ? "" : filterType.getStudy_type());
			GlobalArtifactIdentifier studyIdentifier = filterType.getStudyId();
			if(studyIdentifier != null)
			{
				filter.setStudyId(studyIdentifier.toString());
			}
			else
				filter.setStudyId(null);
			filter.setOrigin(filterType.getOrigin() == null ? "" : filterType.getOrigin());
			filter.setIncludeDeletedImages(filterType.isIncludeDeleted());
		}
		return filter;
	}
	
	public static StudyFilter translate(FederationFilterType filterType,
			int authorizedSensitiveLevel, boolean filterProcedures)
	throws GlobalArtifactIdentifierFormatException
	{
		StudyFilter filter = null;
		if(filterProcedures)
			filter = new ProcedureFilter(ProcedureFilterMatchMode.existInProcedureList);
		else
			filter = new StudyFilter();		
		filter.setMaximumAllowedLevel(PatientSensitivityLevel.getPatientSensitivityLevel(authorizedSensitiveLevel));
		if(filterType != null)
		{
			filter.setFromDate(filterType.getFromDate());
			filter.setToDate(filterType.getToDate());
			filter.setStudy_class(filterType.get_class() == null ? "" : filterType.get_class());
			filter.setStudy_event(filterType.getEvent() == null ? "" : filterType.getEvent());
			filter.setStudy_package(filterType.get_package() == null ? "" : filterType.get_package());
			filter.setStudy_specialty(filterType.getSpecialty() == null ? "" : filterType.getSpecialty());
			filter.setStudy_type(filterType.getTypes() == null ? "" : filterType.getTypes());
			String requestedStudyIdAsString = filterType.getStudyId();
			if(requestedStudyIdAsString != null)
			{
				try
				{
					URN studyUrn = URNFactory.create(requestedStudyIdAsString);
					if(studyUrn instanceof GlobalArtifactIdentifier)
						filter.setStudyId( (GlobalArtifactIdentifier)studyUrn );
				}
				catch (URNFormatException x)
				{
					throw new GlobalArtifactIdentifierFormatException(requestedStudyIdAsString + 
						" cannot be transformed into a GlobalArtifactIdentifier realization."
					);
				}
			}
			else
				filter.setStudyId(null);
			filter.setOrigin(filterType.getOrigin() == null ? "" : filterType.getOrigin());
			filter.setIncludeDeleted(filterType.isIncludeDeletedImages());
		}
		return filter;
	}
	
	public static SortedSet<Study> translate(FederationStudyType [] studies)
	throws TranslationException
	{
		return translate(studies, null);
	}
	
	public static FederationDocumentSetResultType translate(DocumentSetResult documentSetResult)
	{
		if(documentSetResult == null)
			return null;
		FederationDocumentSetResultType result = new FederationDocumentSetResultType();
		
		FederationDocumentSetType[] documentSets = translate(documentSetResult.getArtifacts());
		
		FederationArtifactResultStatusType artifactResultStatus = 
			translate(documentSetResult.getArtifactResultStatus());
		
		result.setDocumentSets(documentSets);
		result.setArtifactResultStatus(artifactResultStatus);
		result.setErrors(translateArtifactResultErrorList(documentSetResult.getArtifactResultErrors()));
		
		return result;
	}
	
	private static FederationDocumentSetType[] translate(SortedSet<DocumentSet> documentSets)
	{
		if(documentSets == null)
			return null;
		FederationDocumentSetType[] result = new FederationDocumentSetType[documentSets.size()];
		int i = 0;
		for(DocumentSet documentSet : documentSets)
		{
			result[i] = translate(documentSet);
			i++;
		}
		
		return result;
	}
	
	private static FederationDocumentSetType translate(DocumentSet documentSet)
	{
		if(documentSet == null)
			return null;
		FederationDocumentSetType result = new FederationDocumentSetType();
		result.setAcquisitionDate(documentSet.getAcquisitionDate());
		result.setAlienSiteNumber(documentSet.getAlienSiteNumber());
		result.setClinicalType(documentSet.getClinicalType());		
		result.setErrorMessage(documentSet.getErrorMessage());
		result.setFirstImageIen(documentSet.getFirstImageIen());
		result.setHomeCommunityId(documentSet.getHomeCommunityId().toString());
		result.setIdentifier(documentSet.getIdentifier());
		result.setPatientIcn(documentSet.getPatientIcn());
		result.setPatientName(documentSet.getPatientName());
		result.setProcedureDate(documentSet.getProcedureDate());
		result.setRepositoryId(documentSet.getRepositoryId());
		result.setRpcResponseMsg(documentSet.getRpcResponseMsg());
		result.setSiteAbbr(documentSet.getSiteAbbr());
		result.setSiteName(documentSet.getSiteName());
		result.setConsolidatedSiteNumber(documentSet.getConsolidatedSiteNumber());
		
		FederationDocumentType [] documents = new FederationDocumentType[documentSet.size()];
		int i = 0; 
		for(Document document : documentSet)
		{
			documents[i] = translate(document);
			i++;
		}
		result.setDocuments(documents);
		
		return result;
	}
	
	private static FederationDocumentType translate(Document document)
	{
		if(document == null)
			return null;
		
		FederationDocumentType result = new FederationDocumentType();
		result.setChecksum(translate(document.getChecksumValue()));
		result.setClinicalType(document.getClinicalType());
		result.setConfidentialityCode(document.getConfidentialityCode());
		result.setContentLenth(document.getContentLength());
		result.setCreationDate(document.getCreationDate());
		result.setDescription(document.getDescription());
		result.setDocumentSetIen(document.getDocumentSetIen());
		result.setIdentifier(document.getGlobalArtifactIdentifier().toString());
		result.setLanguageCode(document.getLanguageCode());
		result.setMediaType(translate(document.getMediaType()));
		result.setName(document.getName());
		result.setVistaImageType(document.getVistaImageType());
		result.setConsolidatedSiteNumber(document.getConsolidatedSiteNumber());
		return result;
	}
	
	public static DocumentSetResult translate(FederationDocumentSetResultType documentSetResultType)
	throws TranslationException
	{
		if(documentSetResultType == null)
		{
			getLogger().error("Cannot translate null FederationDocumentSetResultType");
			return DocumentSetResult.createFullResult(null);
		}
		SortedSet<DocumentSet> documentSets = translate(documentSetResultType.getDocumentSets());
		ArtifactResultStatus artifactResultStatus = translate(documentSetResultType.getArtifactResultStatus());
		List<ArtifactResultError> artifactResultErrors = translate(documentSetResultType.getErrors());		
		return DocumentSetResult.create(documentSets, artifactResultStatus, artifactResultErrors);
	}
	
	private static SortedSet<DocumentSet> translate(FederationDocumentSetType [] documentSets)
	throws TranslationException
	{
		SortedSet<DocumentSet> result = new TreeSet<DocumentSet>();
		if(documentSets == null)
			return result;
		for(FederationDocumentSetType documentSet : documentSets)
		{
			result.add(translate(documentSet));
		}
		return result;
	}
	
	private static DocumentSet translate(FederationDocumentSetType documentSet)
	throws TranslationException
	{
		try
		{
			DocumentSet result = new DocumentSet(OID.create(documentSet.getHomeCommunityId()), 
					documentSet.getRepositoryId(), documentSet.getIdentifier());
			
			result.setAcquisitionDate(documentSet.getAcquisitionDate());
			result.setAlienSiteNumber(documentSet.getAlienSiteNumber());
			result.setClinicalType(documentSet.getClinicalType());
			result.setErrorMessage(documentSet.getErrorMessage());
			result.setFirstImageIen(documentSet.getFirstImageIen());
			result.setPatientIcn(documentSet.getPatientIcn());
			result.setPatientName(documentSet.getPatientName());
			result.setProcedureDate(documentSet.getProcedureDate());
			result.setRpcResponseMsg(documentSet.getRpcResponseMsg());
			result.setSiteAbbr(documentSet.getSiteAbbr());
			result.setSiteName(documentSet.getSiteName());
			result.setConsolidatedSiteNumber(documentSet.getConsolidatedSiteNumber());
			for(FederationDocumentType document : documentSet.getDocuments())
			{
				result.add(translate(document, result));
			}
			return result;
		}
		catch(OIDFormatException oidfX)
		{
			throw new TranslationException("Unable to create OID from Id '" + documentSet.getHomeCommunityId() + "', " + oidfX.getMessage(), oidfX);
		}
	}
	
	private static Document translate(FederationDocumentType document, DocumentSet documentSet)
	throws TranslationException
	{
		try
		{
			@SuppressWarnings("unchecked")
			GlobalArtifactIdentifier gai = 
				GlobalArtifactIdentifierFactory.create(document.getIdentifier(), GlobalArtifactIdentifier.class);
			if(gai instanceof ImageURN)
			{
				// the identifier is a URN containing the patient ICN already in it so don't do anything
				// to it
			}
			else
			{
				// JMW 12/10/2010 P104
				// The patient ICN must come from the documentSet, not the document since it is not
				// consistently set - the patientICN comes from the URN of the document, if its not a URN
				// then the ICN will be null from the Document object. 
				gai = PatientArtifactIdentifierImpl.create(gai, documentSet.getPatientIcn());
			}

			
			ChecksumValue checksum = translate(document.getChecksum());
			Document result = new Document(documentSet, gai, document.getCreationDate(), 
					document.getVistaImageType(), document.getClinicalType(), document.getContentLenth(), checksum);
			result.setConfidentialityCode(document.getConfidentialityCode());
			result.setDescription(document.getDescription());
			result.setLanguageCode(document.getLanguageCode());
			result.setMediaType(translate(document.getMediaType()));
			result.setName(document.getName());
			result.setConsolidatedSiteNumber(document.getConsolidatedSiteNumber());
			return result;
		}
		catch(GlobalArtifactIdentifierFormatException urnfX)
		{
			throw new TranslationException("Unable to create GlobalArtifactIdentifier from identifier '" + document.getIdentifier() + "', " + urnfX.getMessage(), urnfX);
		}
		catch(URNFormatException urnfX)
		{
			throw new TranslationException("URNFormatException, unable to create PatientArtifactIdentifier from identifier '" + document.getIdentifier() + "' and ICN '" + documentSet.getPatientIcn() + "', " + urnfX.getMessage(), urnfX);
		}
	}
	
	public static MediaType translate(FederationMediaType mediaType)
	{
		for( Entry<MediaType, FederationMediaType> entry : FederationRestTranslator.mediaTypeMap.entrySet() )
			if( entry.getValue() == mediaType )
				return entry.getKey();
		
		return MediaType.APPLICATION_OCTETSTREAM;
	}
	
	public static FederationMediaType translate(MediaType mediaType)
	{
		for( Entry<MediaType, FederationMediaType> entry : FederationRestTranslator.mediaTypeMap.entrySet() )
			if( entry.getKey() == mediaType )
				return entry.getValue();
		
		return FederationMediaType.APPLICATION_OCTETSTREAM;
	}
	
	private static ChecksumValue translate(FederationChecksumType checksum)
	{
		if(checksum == null)
			return null;
		return new ChecksumValue(checksum.getAlgorithm(), checksum.getValue());
	}
	
	private static FederationChecksumType translate(ChecksumValue checksum)
	{
		if(checksum == null)
			return null;
		FederationChecksumType result = new FederationChecksumType();
		result.setAlgorithm(checksum.getAlgorithm());
		result.setValue(checksum.getValue());
		return result;
	}
	
	public static FederationArtifactResultsType translate(ArtifactResults artifactResults)
	throws TranslationException
	{
		if(artifactResults == null)
			return null;
		FederationArtifactResultsType result = new FederationArtifactResultsType();
		
		result.setDocumentSetResult(translate(artifactResults.getDocumentSetResult()));
		result.setStudySetResult(translate(artifactResults.getStudySetResult()));
		
		return result;		
	}
	
	public static ArtifactResults translate(FederationArtifactResultsType artifactResultsType, StudyFilter studyFilter)
	throws TranslationException
	{
		if(artifactResultsType == null)
			return null;
		StudySetResult studySetResult = null;
		if(artifactResultsType.getStudySetResult() != null)
		{
			studySetResult = translate(artifactResultsType.getStudySetResult(), studyFilter);
		}
		DocumentSetResult documentSetResult = null;
		if(artifactResultsType.getDocumentSetResult() != null)
		{
			documentSetResult = translate(artifactResultsType.getDocumentSetResult());
		}
		return ArtifactResults.create(studySetResult, documentSetResult);
	}
	
	public static StudySetResult translate(FederationStudyResultType studyResultType, StudyFilter studyFilter)
	throws TranslationException
	{
		if(studyResultType == null)
		{
			getLogger().error("Cannot translate null FederationStudyResultType");
			return StudySetResult.createFullResult(null);
		}
		SortedSet<Study> studies = translate(studyResultType.getStudies(), studyFilter);
		ArtifactResultStatus artifactResultStatus = translate(studyResultType.getArtifactResultStatus());
		List<ArtifactResultError> artifactResultErrors = translate(studyResultType.getErrors());		
		
		return StudySetResult.create(studies, artifactResultStatus, artifactResultErrors);		
	}	
	
	private static SortedSet<Study> translate(FederationStudyType [] studies, StudyFilter studyFilter)
	throws TranslationException
	{
		SortedSet<Study> result = new TreeSet<Study>();	
		if(studies == null)
			return result;
		for(int i = 0; i < studies.length; i++)
		{
			FederationStudyType studyType = studies[i];
			// CTB 29March2010
			// either the study filter has no study ID specified or the study IDs match
			// the study ID from the federation call will be base 32 encoded
			
			// JMW 6/1/2010 - need to translate first to get the proper URN to determine if allowed to include in result
			Study study = translate(studyType);
			boolean useStudy = true;
			if(studyFilter != null)
			{
				useStudy = !studyFilter.isStudyIenSpecified() ||
					studyFilter.isAllowableStudyId( study.getGlobalArtifactIdentifier()) ||
						studyFilter.isAllowableStudyId(study.getAlternateArtifactIdentifier());						
			}
			if(useStudy)
				result.add(study);
		}		
		// JMW 1/5/2009 - if the study IEN is not specified then the results need to be filtered.
		// call the pre and post filter since the pre filter cannot run on the Federation Datasource before calling the DS
		// both need to be run depending on the creator - might have different functionality on the filter functions
		if((studyFilter != null) && (!studyFilter.isStudyIenSpecified()))
		{
			studyFilter.preFilter(result);
			studyFilter.postFilter(result);
		}
		return result;
	}
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	public static Study translate(FederationStudyType studyType) 
	throws TranslationException
	{
		Study study = null;
		String studyId = studyType.getStudyId();
		StudyLoadLevel studyLoadLevel = translate(studyType.getStudyLoadLevel());
		try
		{
			study = Study.create(ObjectOrigin.UNKNOWN, studyType.getSiteNumber(), 
					studyId, PatientIdentifier.icnPatientIdentifier(studyType.getPatientIcn()), 
					studyLoadLevel, 
					translate(studyType.getStudyDeletedImageState()));
		}
		catch (URNFormatException x)
		{
			getLogger().error("Unable to create a Study from the given key elements");
			throw new TranslationException("Unable to create a Study from the given key elements");
		}
		if((studyType.getAlternateArtifactId() != null) && (studyType.getAlternateArtifactId().length() > 0))
		{
			try
			{
				study.setAlternateArtifactIdentifier(GlobalArtifactIdentifierFactory.create(studyType.getAlternateArtifactId(), StudyURN.class));
			}
			catch(GlobalArtifactIdentifierFormatException gaifX)
			{
				getLogger().error("GlobalArtifactIdentifierFormatException creating alternate artifact ID for study from value '" + studyType.getAlternateArtifactId() + "', " + gaifX.getMessage(), gaifX);
			}
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
		
		study.setProcedureDate( studyType.getProcedureDate() );
		study.setProcedure(studyType.getProcedureDescription() == null ? "" : studyType.getProcedureDescription());
		//study.setRadiologyReport((studyType.getRadiologyReport() == null ? "" : studyType.getRadiologyReport()));
		// JMW 10/12/2010 - check the study load level to see if the report should have been included. If not then
		// don't use the report value because it might be an empty string which doesn't always mean the report is there
		// this is a bit sad but necessary for some reason
		if(studyLoadLevel.isIncludeReport())
			study.setRadiologyReport(studyType.getRadiologyReport()); // even if its null, thats ok, means study not loaded
		
		study.setSiteName(studyType.getSiteName() == null ? "" : studyType.getSiteName());
		study.setSpecialty(studyType.getSpecialtyDescription() == null ? "" : studyType.getSpecialtyDescription());

		study.setOrigin(studyType.getOrigin());
		study.setSiteAbbr(studyType.getSiteAbbreviation());
		if(studyType.getStudyModalities() != null)
		{			
			String[] modalities = studyType.getStudyModalities();
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
		study.setDocumentDate(studyType.getDocumentDate());
		study.setSensitive(studyType.isSensitive());
		study.setStudyStatus(translate(studyType.getStudyStatus()));
		study.setStudyViewStatus(translate(studyType.getStudyViewStatus()));
		study.setCptCode(studyType.getCptCode());
		study.setConsolidatedSiteNumber(studyType.getConsolidatedSiteNumber());
		study.setAlienSiteNumber(studyType.getAlienSiteNumber());
		study.setStudyImagesHaveAnnotations(studyType.isStudyImagesHaveAnnotations());
		Image firstImage = null;
		
		firstImage = translate(studyType.getFirstImage());
		if(studyType.getSeries() != null)
		{
			for(FederationSeriesType series : studyType.getSeries())
			{
				Series ser = translate(series);
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
	
	public static ObjectStatus translate(FederationObjectStatusType objectStatus)
	{
		for(Entry<ObjectStatus, FederationObjectStatusType> entry : objectStatusMap.entrySet())
		{
			if(entry.getValue() == objectStatus)
				return entry.getKey();
		}
		return ObjectStatus.UNKNOWN;
	}
	
	public static FederationObjectStatusType translate(ObjectStatus objectStatus)
	{
		for(Entry<ObjectStatus, FederationObjectStatusType> entry : objectStatusMap.entrySet())
		{
			if(entry.getKey() == objectStatus)
				return entry.getValue();
		}
		return FederationObjectStatusType.UNKNOWN;
	}
	
	public static FederationStudyResultType translate(StudySetResult studySet)
	throws TranslationException
	{
		if(studySet == null)
			return null;
		
		FederationStudyResultType result = new FederationStudyResultType();
		result.setStudies(translate(studySet.getArtifacts()));
		result.setArtifactResultStatus(translate(studySet.getArtifactResultStatus()));
		result.setErrors(translateArtifactResultErrorList(studySet.getArtifactResultErrors()));
		return result;		
	}
	
	private static List<ArtifactResultError> translate(FederationArtifactResultErrorType [] errors)
	{
		if(errors == null)
			return null;
		
		List<ArtifactResultError> result = new ArrayList<ArtifactResultError>(errors.length);
		for(FederationArtifactResultErrorType error : errors)
		{
			result.add(translate(error));
		}
		
		return result;
	}
	
	private static ArtifactResultError translate(FederationArtifactResultErrorType error)
	{
		String context = error.getCodeContext();
		String location = error.getLocation();
		ArtifactResultErrorCode errorCode = translate(error.getErrorCode());
		ArtifactResultErrorSeverity severity = translate(error.getSeverity());
		return new FederationArtifactResultError(context, location, errorCode, severity);
	}
	
	private static FederationArtifactResultErrorType [] translateArtifactResultErrorList(List<ArtifactResultError> errors)
	{
		if(errors == null)
			return null;
		
		FederationArtifactResultErrorType [] result = new FederationArtifactResultErrorType [errors.size()];
		int i = 0;
		for(ArtifactResultError error : errors)
		{
			result[i] = translate(error);
			i++;
		}
		
		return result;
	}
	
	private static FederationArtifactResultErrorType translate(ArtifactResultError error)
	{
		FederationArtifactResultErrorType result = new FederationArtifactResultErrorType();
		
		result.setCodeContext(error.getCodeContext());
		result.setLocation(error.getLocation());
		result.setErrorCode(translate(error.getErrorCode()));
		result.setSeverity(translate(error.getSeverity()));
		return result;
	}
	
	public static FederationArtifactResultErrorSeverityType translate(ArtifactResultErrorSeverity artifactResultErrorSeverity)
	{
		for(Entry<ArtifactResultErrorSeverity, FederationArtifactResultErrorSeverityType> entry : FederationRestTranslator.artifactResultErrorSeverityMap.entrySet())
		{
			if(entry.getKey() == artifactResultErrorSeverity)
			{
				return entry.getValue();
			}
		}
		return FederationArtifactResultErrorSeverityType.error;
	}
	
	public static ArtifactResultErrorSeverity translate(FederationArtifactResultErrorSeverityType artifactResultErrorSeverityType)
	{
		for(Entry<ArtifactResultErrorSeverity, FederationArtifactResultErrorSeverityType> entry : FederationRestTranslator.artifactResultErrorSeverityMap.entrySet())
		{
			if(entry.getValue() == artifactResultErrorSeverityType)
			{
				return entry.getKey();
			}
		}
		return ArtifactResultErrorSeverity.error;
	}
	
	public static FederationArtifactResultErrorCodeType translate(ArtifactResultErrorCode artifactResultErrorCode)
	{
		for(Entry<ArtifactResultErrorCode, FederationArtifactResultErrorCodeType> entry : FederationRestTranslator.artifactResultErrorCodeMap.entrySet())
		{
			if(entry.getKey() == artifactResultErrorCode)
			{
				return entry.getValue();
			}
		}
		return FederationArtifactResultErrorCodeType.internalException;
	}
	
	public static ArtifactResultErrorCode translate(FederationArtifactResultErrorCodeType artifactResultErrorCodeType)
	{
		for(Entry<ArtifactResultErrorCode, FederationArtifactResultErrorCodeType> entry : FederationRestTranslator.artifactResultErrorCodeMap.entrySet())
		{
			if(entry.getValue() == artifactResultErrorCodeType)
			{
				return entry.getKey();
			}
		}
		return ArtifactResultErrorCode.internalException;
	}	
	
	public static FederationStudyType [] translate(Collection<Study> studies)
	throws TranslationException
	{
		if(studies == null)
			return null;
		
		FederationStudyType [] result = new FederationStudyType [studies.size()];
		int i = 0;
		for(Study study : studies)
		{
			result[i] = translate(study);
			i++;
		}
		
		return result;
	}
	
	public static FederationStudyType translate(Study study)
	throws TranslationException
	{
		if(study == null)
			return null;
		FederationStudyType result = 
			new FederationStudyType();
		FederationStudyLoadLevelType loadLevel = translate(study.getStudyLoadLevel());
		result.setStudyLoadLevel( loadLevel );
		result.setStudyDeletedImageState(translate(study.getStudyDeletedImageState()));
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
		result.setProcedureDate(study.getProcedureDate());
		
		result.setStudyPackage(study.getImagePackage());
		result.setStudyClass(study.getStudyClass() == null ? "" : study.getStudyClass());
		result.setStudyType(study.getImageType());
		result.setCaptureDate(study.getCaptureDate());
		result.setCapturedBy(study.getCaptureBy());	
		result.setRpcResponseMsg(study.getRpcResponseMsg());
		
		result.setDocumentDate(study.getDocumentDate());
		result.setSensitive(study.isSensitive());
		result.setStudyStatus(translate(study.getStudyStatus()));
		result.setStudyViewStatus(translate(study.getStudyViewStatus()));
		result.setCptCode(study.getCptCode());
		result.setConsolidatedSiteNumber(study.getConsolidatedSiteNumber());
		result.setAlienSiteNumber(study.getAlienSiteNumber());
		if(study.getAlternateArtifactIdentifier() != null)
		{
			result.setAlternateArtifactId(study.getAlternateArtifactIdentifier().toString());
		}
		
		result.setErrorMessage(study.getErrorMessage() == null ? "" : study.getErrorMessage());
		// return null for the UID instead of the empty string to be consistent with the WSDL - DKB
		if (study.getStudyUid() != null && study.getStudyUid().trim().length() > 0)
			result.setDicomUid(study.getStudyUid());
		else
			result.setDicomUid(null);
		
		result.setStudyId( study.getStudyIen() );
		
		if(study.getSeries() != null)
		{
			FederationSeriesType [] seriesSet = new FederationSeriesType[study.getSeriesCount()]; 
			int i = 0;
			for(Series series : study)
			{
				seriesSet[i] = translate(series);
				i++;
			}
			result.setSeries(seriesSet);
		}
		
		if(study.getFirstImage() == null)
			throw new TranslationException("Study.firstImage is null, translation of study '" + study.getStudyUrn() + "' cannot continue.");
		
		FederationImageType firstImage = translate(study.getFirstImage());
		result.setFirstImage(firstImage);
		result.setFirstImageIen(firstImage.getImageId());
		
		if(study.getModalities() != null)
			result.setStudyModalities(study.getModalities().toArray(new String [study.getModalities().size()]));
		result.setStudyImagesHaveAnnotations(study.isStudyImagesHaveAnnotations());
		return result;
	}
	
	public static FederationSeriesType [] translate(
			Set<Series> seriesSet) 
		throws ParseException
		{
			if(seriesSet == null)// || seriesSet.size() == 0)
				return null;
			
			FederationSeriesType [] result = new FederationSeriesType[seriesSet.size()];
			int i = 0;
			for(Series series : seriesSet)
			{
				result[i] = translate(series);
				i++;
			}
			
			return result;
		}
	
	public static Series translate(FederationSeriesType series)
	{
		if(series == null)
			return null;
		
		Series result = 
			new Series();

		// JMW 2/12/2013 - if the images are empty then they might come across as a null array
		// check for null to avoid a NPE
		FederationImageType [] images = series.getImages();
		if(images != null)
		{
			for(FederationImageType image : images)
				result.addImage(translate(image));
		}

		result.setSeriesUid(series.getSeriesUid());
		result.setSeriesNumber(series.getSeriesNumber());	
		result.setSeriesIen(series.getSeriesIen());
		result.setModality(series.getModality());
		
		return result;
	}
	
	public static FederationSeriesType translate(Series series)
	{
		if(series == null)
			return null;
		
		FederationSeriesType result = 
			new FederationSeriesType();
		FederationImageType [] instances = 
			new FederationImageType[series.getImageCount()];

		int index=0;
		for(Image image : series)
			instances[index++] = translate(image);
		result.setImages(instances);

		result.setSeriesUid(series.getSeriesUid());
		result.setSeriesNumber(series.getSeriesNumber());	
		result.setSeriesIen(series.getSeriesIen());
		result.setModality(series.getModality());
		
		return result;
	}
	
	public static RoutingToken translateRoutingToken(String serializedRoutingToken)
	throws RoutingTokenFormatException
	{
		return RoutingTokenImpl.parse(serializedRoutingToken);
	}
	
	public static StudyLoadLevel translate(
			FederationStudyLoadLevelType loadLevelType)
		{		
			for( Entry<StudyLoadLevel, FederationStudyLoadLevelType> entry : FederationRestTranslator.loadLevelMap.entrySet() )
				if( entry.getValue() == loadLevelType )
					return entry.getKey();
			
			return StudyLoadLevel.FULL;
		}	
	
	public static FederationStudyLoadLevelType translate(
			StudyLoadLevel loadLevel)
		{		
			for( Entry<StudyLoadLevel, FederationStudyLoadLevelType> entry : FederationRestTranslator.loadLevelMap.entrySet() )
				if( entry.getKey() == loadLevel )
					return entry.getValue();
			
			return FederationStudyLoadLevelType.FULL;
		}	
	
	public static Image translate(FederationImageType instanceType) 
	//throws TranslationException
	{
		if(instanceType == null)
			return null;		
		Image image = null;
		try
		{
			image = Image.create(
				instanceType.getSiteNumber(), 
				instanceType.getImageId(), 		
				instanceType.getStudyId(),
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
		image.setImageUid(instanceType.getDicomUid() == null ? "" : instanceType.getDicomUid());
		image.setDescription(instanceType.getDescription() == null ? "" : instanceType.getDescription());
		//image.setPatientICN(instanceType.getPatientIcn());
		image.setPatientName(instanceType.getPatientName() == null ? "" : instanceType.getPatientName().replaceAll("\\^", " "));
		image.setProcedureDate(instanceType.getProcedureDate() );
		image.setProcedure(instanceType.getProcedure() == null ? "" : instanceType.getProcedure());
		image.setSiteAbbr(instanceType.getSiteAbbr());
		image.setFullLocation(instanceType.getFullLocation());
		image.setFullFilename(instanceType.getFullImageFilename());
		image.setAbsLocation(instanceType.getAbsLocation());
		image.setAbsFilename(instanceType.getAbsImageFilename());
		image.setDicomImageNumberForDisplay(instanceType.getDicomImageNumberForDisplay() == null ? "" : instanceType.getDicomImageNumberForDisplay() + "");
		image.setDicomSequenceNumberForDisplay(instanceType.getDicomSequenceNumberForDisplay() == null ? "" : instanceType.getDicomSequenceNumberForDisplay() + "");
		image.setImgType(instanceType.getImageType());
		image.setImageClass(instanceType.getImageClass());
		image.setBigFilename(instanceType.getBigImageFilename());
		image.setQaMessage(instanceType.getQaMessage());
		//image.setImageModality(instanceType.getImageModality() == null ? "" : instanceType.getImageModality());
		image.setErrorMessage(instanceType.getErrorMessage() == null ? "" : instanceType.getErrorMessage());
		image.setAlienSiteNumber(instanceType.getAlienSiteNumber());
		image.setConsolidatedSiteNumber(instanceType.getConsolidatedSiteNumber());
		image.setImageAnnotationStatus(instanceType.getImageAnnotationStatus());
		image.setImageAnnotationStatusDescription(instanceType.getImageAnnotationStatusDescription());
		image.setAssociatedNoteResulted(instanceType.getAssociatedNoteResulted());
		image.setImagePackage(instanceType.getImagePackage());
		image.setImageHasAnnotations(instanceType.isImageHasAnnotations());
		
		image.setCaptureDate(instanceType.getCaptureDate());
		image.setDocumentDate(instanceType.getDocumentDate());
		image.setSensitive(instanceType.isSensitive());
		image.setImageViewStatus(translate(instanceType.getImageViewStatus()));
		image.setImageStatus(translate(instanceType.getImageStatus()));
		
		
		return image;
	}
	
	public static FederationImageType translate(Image image)
	{
		if(image == null)
			return null;
		FederationImageType result = new FederationImageType();
		result.setImageId( image.getIen() );		
		
		// Exchange fields
		// return null for the UID instead of the empty string to be consistent with the WSDL - DKB
		if (image.getImageUid()!= null && image.getImageUid().trim().length() > 0)
		{
			result.setDicomUid(image.getImageUid().trim());
		}
		
		
		if (image.getImageNumber() != null && image.getImageNumber().trim().length() > 0)
		{
			try
			{
				Integer imageNumber = new Integer(image.getImageNumber());
				result.setImageNumber(imageNumber);
			}
			catch (NumberFormatException ex)
			{
				// not a number - return null
				result.setImageNumber(null);
			}
		}
		else
		{
			result.setImageNumber(null);
		}
		
		// Clinical Display fields
		result.setDescription(image.getDescription());
		result.setDicomImageNumberForDisplay(image.getDicomImageNumberForDisplay());
		result.setDicomSequenceNumberForDisplay(image.getDicomSequenceNumberForDisplay());
		result.setPatientIcn(image.getPatientId());
		result.setPatientName(image.getPatientName());
		result.setProcedure(image.getProcedure());
		if(image.getProcedureDate() == null)
		{
			getLogger().warn("Setting null procedure date for image");
			result.setProcedureDate(null);
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
			result.setProcedureDate(image.getProcedureDate());
		}
		result.setSiteNumber(image.getSiteNumber());
		result.setSiteAbbr(image.getSiteAbbr());
		result.setImageClass(image.getImageClass());
		result.setAbsLocation(image.getAbsLocation());
		result.setFullLocation(image.getFullLocation());
		
		result.setQaMessage(image.getQaMessage());
		result.setImageType(image.getImgType());
		result.setFullImageFilename(image.getFullFilename());
		result.setAbsImageFilename(image.getAbsFilename());
		result.setBigImageFilename(image.getBigFilename());

		result.setStudyId( image.getStudyIen() );
		result.setImageModality(image.getImageModality());
		result.setErrorMessage(image.getErrorMessage() == null ? "" : image.getErrorMessage());
		result.setConsolidatedSiteNumber(image.getConsolidatedSiteNumber());
		result.setAlienSiteNumber(image.getAlienSiteNumber());
		result.setAssociatedNoteResulted(image.getAssociatedNoteResulted());
		result.setImageAnnotationStatus(image.getImageAnnotationStatus());
		result.setImageAnnotationStatusDescription(image.getImageAnnotationStatusDescription());
		result.setImageHasAnnotations(image.isImageHasAnnotations());
		result.setImagePackage(image.getImagePackage());
		
		result.setCaptureDate(image.getCaptureDate());
		result.setDocumentDate(image.getDocumentDate());
		result.setSensitive(image.isSensitive());
		result.setImageViewStatus(translate(image.getImageViewStatus()));
		result.setImageStatus(translate(image.getImageStatus()));
		
		return result;
		
	}
	
	public static FederationPatientSensitiveType translate(
			PatientSensitiveValue patientSensitiveValue)
	{
		FederationPatientSensitiveType result = new FederationPatientSensitiveType();
		result.setWarningMessage(patientSensitiveValue.getWarningMessage());
		result.setSensitiveLevel(translate(patientSensitiveValue.getSensitiveLevel()));
		return result;
	}
	
	public static List<String> translate(String [] sites)
	{
		List<String> result = new ArrayList<String>(sites.length);
		for(String site : sites)
		{
			result.add(site);
		}
		return result;
	}
	
	public static String [] translateResolvedArtifactSourceList(List<ResolvedArtifactSource> sites)
	{
		String [] result = new String[sites.size()];
		for(int i = 0; i < sites.size(); i++)
		{
			result[i] = sites.get(i).getArtifactSource().getRepositoryId();
		}
		return result;
	}
	
	public static PatientSensitiveValue translate(
			FederationPatientSensitiveType patientSensitiveValue)
	{
		return new PatientSensitiveValue(translate(patientSensitiveValue.getSensitiveLevel()), 
				patientSensitiveValue.getWarningMessage());
	}
	
	private static FederationPatientSensitivityLevelType translate(
			PatientSensitivityLevel patientSensitivityLevel)
	{
		for( Entry<PatientSensitivityLevel, FederationPatientSensitivityLevelType> entry : FederationRestTranslator.patientSensitiveLevelMap.entrySet() )
			if( entry.getKey() == patientSensitivityLevel )
				return entry.getValue();
		
		return FederationPatientSensitivityLevelType.NO_ACTION_REQUIRED;
	}
	
	private static PatientSensitivityLevel translate(
			FederationPatientSensitivityLevelType patientSensitivityLevel)
	{
		for( Entry<PatientSensitivityLevel, FederationPatientSensitivityLevelType> entry : FederationRestTranslator.patientSensitiveLevelMap.entrySet() )
			if( entry.getValue() == patientSensitivityLevel )
				return entry.getKey();
		
		return PatientSensitivityLevel.NO_ACTION_REQUIRED;
	}

	public static FederationPatientType[] translatePatientList(List<Patient> patients)
	{
		FederationPatientType[] result = new FederationPatientType[patients.size()];
		for(int i = 0; i < patients.size(); i++)
		{
			result[i] = translate(patients.get(i));
		}
		return result;
	}
	
	public static List<Patient> translate(FederationPatientType [] patients)
	{
		List<Patient> result = new ArrayList<Patient>(patients.length);
		for(FederationPatientType patient : patients)
		{
			result.add(translate(patient));
		}
		return result;
	}
	
	public static SortedSet<Patient> translateToSet(FederationPatientType [] patients)
	{
		SortedSet<Patient> result = new TreeSet<Patient>();
		for(FederationPatientType patient : patients)
		{
			result.add(translate(patient));
		}
		return result;
	}
	
	public static FederationPatientType translate(Patient patient)
	{
		return new FederationPatientType(
				patient.getPatientName(),
				patient.getPatientIcn(),
				patient.getVeteranStatus(),
				translate(patient.getPatientSex()),
				patient.getDob(),
				patient.getSsn(),
				patient.getSensitive()
			);
	}
	
	public static Patient translate(FederationPatientType patient)
	{
		return new Patient(
				patient.getPatientName(),
				patient.getPatientIcn(),
				patient.getVeteranStatus(),
				translate(patient.getPatientSex()),
				patient.getDob(), 
				patient.getSsn(),
				null,
				patient.getSensitive()
			);
	}
	
	public static FederationPatientSexType translate(PatientSex patientSex)
	{
		for(Entry<PatientSex, FederationPatientSexType> entry : FederationRestTranslator.patientSexMap.entrySet())
		{
			if(entry.getKey() == patientSex)
				return entry.getValue();
		}

		return FederationPatientSexType.Unknown;
	}
	
	public static PatientSex translate(FederationPatientSexType patientSex)
	{
		for(Entry<PatientSex, FederationPatientSexType> entry : FederationRestTranslator.patientSexMap.entrySet())
		{
			if(entry.getValue() == patientSex)
				return entry.getKey();
		}
		return PatientSex.Unknown;
	}
	
	public static StudyDeletedImageState translate(FederationStudyDeletedImageStateType studyDeletedImageState)
	{
		for(Entry<StudyDeletedImageState, FederationStudyDeletedImageStateType> entry : FederationRestTranslator.studyDeletedImageStateMap.entrySet())
		{
			if(entry.getValue() == studyDeletedImageState)
				return entry.getKey();				
		}
		return StudyDeletedImageState.cannotIncludeDeletedImages;
	}
	
	public static FederationStudyDeletedImageStateType translate(StudyDeletedImageState studyDeletedImageState)
	{
		for(Entry<StudyDeletedImageState, FederationStudyDeletedImageStateType> entry : FederationRestTranslator.studyDeletedImageStateMap.entrySet())
		{
			if(entry.getKey() == studyDeletedImageState)
				return entry.getValue();				
		}
		return FederationStudyDeletedImageStateType.cannotIncludeDeletedImages;
	}
	
	public static ArtifactResultStatus translate(FederationArtifactResultStatusType artifactResultStatus)
	{
		for(Entry<ArtifactResultStatus, FederationArtifactResultStatusType> entry : FederationRestTranslator.artifactResultStatusMap.entrySet())
		{
			if(entry.getValue() == artifactResultStatus)
				return entry.getKey();
		}
		return ArtifactResultStatus.fullResult;
	}
	
	public static FederationArtifactResultStatusType translate(ArtifactResultStatus artifactResultStatus)
	{
		for(Entry<ArtifactResultStatus, FederationArtifactResultStatusType> entry : FederationRestTranslator.artifactResultStatusMap.entrySet())
		{
			if(entry.getKey() == artifactResultStatus)
				return entry.getValue();
		}
		return FederationArtifactResultStatusType.fullResult;
	}
	
	public static FederationImageFormatQualitiesType translate(ImageFormatQualityList imageFormatQualityList)
	{
		if(imageFormatQualityList == null)
			return null;
		FederationImageFormatQualitiesType result = new FederationImageFormatQualitiesType();
		List<FederationImageFormatQualityType> qualities = new ArrayList<FederationImageFormatQualityType>();
		for(ImageFormatQuality imageFormatQuality : imageFormatQualityList)
		{
			FederationImageFormatQualityType qualityType = translate(imageFormatQuality);
			if(qualityType != null)
				qualities.add(qualityType);
		}
		result.setImageFormatQualities(qualities.toArray(new FederationImageFormatQualityType[qualities.size()]));
		return result;
	}
	
	private static FederationImageFormatQualityType translate(ImageFormatQuality imageFormatQuality)
	{
		if(imageFormatQuality == null)
			return null;
		FederationImageFormatQualityType result = new FederationImageFormatQualityType();
		result.setImageFormat(imageFormatQuality.getImageFormat().name());
		result.setImageQuality(imageFormatQuality.getImageQuality().getCanonical());
		return result;
	}
	
	public static ImageFormatQualityList translate(FederationImageFormatQualitiesType imageFormatQualitiesType)
	{
		if(imageFormatQualitiesType == null)
			return null;
		ImageFormatQualityList result = new ImageFormatQualityList();
		if(imageFormatQualitiesType.getImageFormatQualities() != null)
		{
			for(FederationImageFormatQualityType imageFormatQualityType : imageFormatQualitiesType.getImageFormatQualities())
			{
				ImageFormatQuality imageFormatQuality = translate(imageFormatQualityType);
				if(imageFormatQuality != null)
					result.add(imageFormatQuality);
			}
		}
		
		return result;
	}
	
	private static ImageFormatQuality translate(FederationImageFormatQualityType imageFormatQualityType)
	{
		if(imageFormatQualityType == null)
			return null;
		ImageFormat imageFormat = ImageFormat.valueOf(imageFormatQualityType.getImageFormat());
		
		ImageQuality imageQuality = ImageQuality.getImageQuality(imageFormatQualityType.getImageQuality());
		if((imageFormat == null) || (imageQuality == null))
			return null;
		
		return new ImageFormatQuality(imageFormat, imageQuality);
	}
	
	public static List<ImageAnnotation> translate(FederationImageAnnotationType [] imageAnnotations)
	throws URNFormatException
	{
		if(imageAnnotations == null)
			return null;
		List<ImageAnnotation> result = new ArrayList<ImageAnnotation>();
		
		for(FederationImageAnnotationType imageAnnotation : imageAnnotations)
		{
			result.add(translate(imageAnnotation));
		}
		
		return result;
	}
	
	public static ImageAnnotation translate(FederationImageAnnotationType imageAnnotation)
	throws URNFormatException
	{
		AbstractImagingURN imagingUrn = 
			URNFactory.create(imageAnnotation.getImagingUrn(), AbstractImagingURN.class);
		ImageAnnotationURN imageAnnotationUrn = URNFactory.create(imageAnnotation.getImageAnnotationUrn(), ImageAnnotationURN.class);
		ImageAnnotationSource imageAnnotationSource = translate(imageAnnotation.getAnnotationSource());
		ImageAnnotationUser imageAnnotationUser = translate(imageAnnotation.getAnnotationSavedByUser());
		ImageAnnotation result = new ImageAnnotation(imagingUrn, imageAnnotationUrn, 
				imageAnnotationUser, imageAnnotation.getAnnotationSavedDate(),
				imageAnnotationSource, imageAnnotation.isSavedAfterResult(), 
				imageAnnotation.getAnnotationVersion(), 
				imageAnnotation.isAnnotationDeleted());
		return result;
	}
	
	private static ImageAnnotationUser translate(FederationImageAnnotationUserType imageAnnotationUser)
	{
		return new ImageAnnotationUser(imageAnnotationUser.getUserId(), imageAnnotationUser.getName(), imageAnnotationUser.getService());
	}
	
	public static ImageAnnotationSource translate(FederationImageAnnotationSourceType imageAnnotationSource)
	{
		for(Entry<ImageAnnotationSource, FederationImageAnnotationSourceType> entry : FederationRestTranslator.imageAnnotationSourceMap.entrySet())
		{
			if(entry.getValue() == imageAnnotationSource)
			{
				return entry.getKey();
			}
		}
		return ImageAnnotationSource.clinicalDisplay;
	}
	
	public static FederationImageAnnotationSourceType translate(ImageAnnotationSource imageAnnotationSource)
	{
		for(Entry<ImageAnnotationSource, FederationImageAnnotationSourceType> entry : FederationRestTranslator.imageAnnotationSourceMap.entrySet())
		{
			if(entry.getKey() == imageAnnotationSource)
			{
				return entry.getValue();
			}
		}
		return FederationImageAnnotationSourceType.clinicalDisplay;
	}
	
	public static ImageAnnotationDetails translate(FederationImageAnnotationDetailsType imageAnnotationDetails)
	throws URNFormatException
	{
		if(imageAnnotationDetails == null)
			return null;
		ImageAnnotation imageAnnotation = translate(imageAnnotationDetails.getImageAnnotation());
		return new ImageAnnotationDetails(imageAnnotation, imageAnnotationDetails.getAnnotationXml());
	}
	
	public static FederationImageAnnotationType[] translate(List<ImageAnnotation> imageAnnotations)
	{
		if(imageAnnotations == null)
			return null;
		FederationImageAnnotationType [] result = 
			new FederationImageAnnotationType[imageAnnotations.size()];
		
		for(int i = 0; i < imageAnnotations.size(); i++)
		{
			result[i] = translate(imageAnnotations.get(i));
		}
		
		return result;
	}
	
	public static FederationImageAnnotationType translate(ImageAnnotation imageAnnotation)
	{
		FederationImageAnnotationType result = 
			new FederationImageAnnotationType();
		
		result.setAnnotationSavedByUser(translate(imageAnnotation.getAnnotationSavedByUser()));
		result.setAnnotationSavedDate(imageAnnotation.getAnnotationSavedDate());
		result.setAnnotationSource(translate(imageAnnotation.getAnnotationSource()));
		result.setAnnotationVersion(imageAnnotation.getAnnotationVersion());
		result.setImageAnnotationUrn(imageAnnotation.getAnnotationUrn().toString());
		result.setImagingUrn(imageAnnotation.getImagingUrn().toString());
		result.setSavedAfterResult(imageAnnotation.isSavedAfterResult());
		result.setAnnotationDeleted(imageAnnotation.isAnnotationDeleted());
		
		return result;
	}
	
	private static FederationImageAnnotationUserType translate(ImageAnnotationUser imageAnnotationUser)
	{
		FederationImageAnnotationUserType result = 
			new FederationImageAnnotationUserType();
		
		result.setName(imageAnnotationUser.getName());
		result.setService(imageAnnotationUser.getService());
		result.setUserId(imageAnnotationUser.getUserId());
		
		return result;
	}
	
	public static FederationImageAnnotationDetailsType translate(ImageAnnotationDetails imageAnnotationDetails)
	{
		if(imageAnnotationDetails == null)
			return null;
		FederationImageAnnotationDetailsType result = 
			new FederationImageAnnotationDetailsType();
		
		result.setAnnotationXml(imageAnnotationDetails.getAnnotationXml());
		result.setImageAnnotation(translate(imageAnnotationDetails.getImageAnnotation()));
		
		return result;
	}
	
	public static List<String> translate(FederationStringArrayType values)
	{
		if(values == null)
			return null;
		// Jersey seems to convert empty string arrays into null responses
		// when requesting treating sites for a patient, the result is expected to be an empty array if no sites for the patient
		if(values.getValues() == null)
			return new ArrayList<String>(0);
		List<String> result = new ArrayList<String>(values.getValues().length);
		for(String value : values.getValues())
		{
			result.add(value);
		}
		return result;		
	}	
	
	public static List<Division> translate(FederationDivisionType [] divisions)
	{
		if(divisions == null)
			return null;
		List<Division> result = new ArrayList<Division>();
		
		for(FederationDivisionType division : divisions)
		{
			result.add(new Division(division.getDivisionIen(), division.getDivisionName(), division.getDivisionCode()));
		}
		
		return result;
	}
	
	public static FederationDivisionType [] translateDivisionList(List<Division> divisions)
	{
		if(divisions == null)
			return null;
		FederationDivisionType [] result = new FederationDivisionType[divisions.size()];
		for(int i = 0; i < divisions.size(); i++)
		{
			result[i] = translate(divisions.get(i));
		}
		
		return result;
	}
	
	private static FederationDivisionType translate(Division division)
	{
		FederationDivisionType result = new FederationDivisionType();
		
		result.setDivisionCode(division.getDivisionCode());
		result.setDivisionIen(division.getDivisionIen());
		result.setDivisionName(division.getDivisionName());
		
		return result;
	}
	
	public static UserInformation translate(FederationUserInformationType userInformation)
	{
		if(userInformation == null)
			return null;
		
		List<String> keys = translate(userInformation.getKeys());
		User user = translate(userInformation.getUser());
		return new UserInformation(user, keys, userInformation.isCanUserAnnotate());
	}
	
	private static User translate(FederationUserType user)
	{
		if(user == null)
			return null;
		return new FederationUser(user.getUserId(), user.getName(), user.getTitle(), user.getService());
	}
	
	public static FederationUserInformationType translate(UserInformation userInformation)
	{
		if(userInformation == null)
			return null;
		
		FederationUserInformationType result = new FederationUserInformationType();
		result.setKeys(translateStringList(userInformation.getKeys()));
		result.setUser(translate(userInformation.getUser()));
		result.setCanUserAnnotate(userInformation.isUserCanAnnotate());
		
		return result;
	}
	
	private static FederationUserType translate(User user)
	{
		if(user == null)
			return null;
		FederationUserType result = new FederationUserType();
		result.setName(user.getName());
		result.setService(user.getService());
		result.setTitle(user.getTitle());
		result.setUserId(user.getUserId());
		return result;
	}
	
	public static FederationPatientMeansTestResultType translate(PatientMeansTestResult patientMeansTestResult)
	{
		if(patientMeansTestResult == null)
			return null;
		return new FederationPatientMeansTestResultType(patientMeansTestResult.getCode(), 
				patientMeansTestResult.getMessage());
	}
	
	public static PatientMeansTestResult translate(FederationPatientMeansTestResultType patientMeansTestResult)
	{
		if(patientMeansTestResult == null)
			return null;
		return new PatientMeansTestResult(patientMeansTestResult.getCode(), 
				patientMeansTestResult.getMessage());
	}
	
	public static ElectronicSignatureResult translate(FederationElectronicSignatureResultType electronicSignature)
	{
		if(electronicSignature == null)
			return null;
		return new ElectronicSignatureResult(electronicSignature.isSuccess(), 
				electronicSignature.getMessage());
	}
	
	public static FederationElectronicSignatureResultType translate(ElectronicSignatureResult electronicSignature)
	{
		if(electronicSignature == null)
			return null;
		return new FederationElectronicSignatureResultType(electronicSignature.isSuccess(), 
				electronicSignature.getMessage());
	}
	
	public static FederationImageAccessReasonTypeHolderType translateReasonTypesToHolder(List<ImageAccessReasonType> reasonTypes)
	{
		if(reasonTypes == null)
			return new FederationImageAccessReasonTypeHolderType();
		return new FederationImageAccessReasonTypeHolderType(translateReasonTypes(reasonTypes));
	}
	
	private static FederationImageAccessReasonTypeType[] translateReasonTypes(List<ImageAccessReasonType> reasonTypes)
	{
		if(reasonTypes == null)
			return null;
		
		FederationImageAccessReasonTypeType [] result = new FederationImageAccessReasonTypeType [reasonTypes.size()];
		for(int i = 0; i < reasonTypes.size(); i++)
		{
			result[i] = translate(reasonTypes.get(i));
		}		
		return result;
	}
	
	public static List<ImageAccessReasonType> translate(FederationImageAccessReasonTypeHolderType reasonTypesHolder)
	{
		List<ImageAccessReasonType> result = new ArrayList<ImageAccessReasonType>();
		if(reasonTypesHolder != null)
		{			
			result.addAll(translate(reasonTypesHolder.getReasons()));
		}
		
		return result;
	}
	
	private static List<ImageAccessReasonType> translate(FederationImageAccessReasonTypeType [] reasonTypes)
	{
		if(reasonTypes == null)
			return null;
		List<ImageAccessReasonType> result = new ArrayList<ImageAccessReasonType>();
		for(FederationImageAccessReasonTypeType reasonType : reasonTypes)
		{
			result.add(translate(reasonType));
		}
		
		return result;
	}
	
	private static FederationImageAccessReasonTypeType translate(ImageAccessReasonType reasonType)
	{
		for(Entry<ImageAccessReasonType, FederationImageAccessReasonTypeType> entry : imageAccessReasonTypesMap.entrySet())
		{
			if(entry.getKey() == reasonType)
				return entry.getValue();			
		}
		return null;
	}
	
	private static ImageAccessReasonType translate(FederationImageAccessReasonTypeType reasonType)
	{
		for(Entry<ImageAccessReasonType, FederationImageAccessReasonTypeType> entry : imageAccessReasonTypesMap.entrySet())
		{
			if(entry.getValue() == reasonType)
				return entry.getKey();			
		}
		return null;
	}
	
	public static List<ImageAccessReason> translate( FederationImageAccessReasonType [] reasons)
	throws RoutingTokenFormatException
	{
		if(reasons == null)
			return null;
		List<ImageAccessReason> result = new ArrayList<ImageAccessReason>();
		for(FederationImageAccessReasonType reason : reasons)
		{
			result.add(translate(reason));
		}
		
		return result;
	}
	
	public static FederationImageAccessReasonType [] translateImageAccessReasons(List<ImageAccessReason> reasons)
	{
		if(reasons == null)
			return null;
		FederationImageAccessReasonType [] result = new FederationImageAccessReasonType [reasons.size()];
		for(int i = 0; i < reasons.size(); i++)
		{
			result[i] = translate(reasons.get(i));
		}
		return result;
	}
	
	private static FederationImageAccessReasonType translate(ImageAccessReason reason)
	{
		return new FederationImageAccessReasonType(reason.getRoutingToken().toRoutingTokenString(), 
				reason.getReasonCode(), reason.getDescription(), translateReasonTypes(reason.getReasonTypes()), 
				reason.getGlobalReasonCode());
	}
	
	private static ImageAccessReason translate(FederationImageAccessReasonType reason) 
	throws RoutingTokenFormatException
	{
		return new ImageAccessReason(translateRoutingToken(reason.getRoutingTokenString()),
				reason.getReasonCode(), reason.getDescription(), translate(reason.getReasonTypes()), 
				reason.getGlobalReasonCode());
	}
	
	public static FederationImagingLogEventType translate(ImagingLogEvent imagingLogEvent)
	{
		return new FederationImagingLogEventType(imagingLogEvent.getRoutingTokenToLogTo().toRoutingTokenString(),
				imagingLogEvent.getImagingUrn() == null ? null : imagingLogEvent.getImagingUrn().toStringCDTP(),
				imagingLogEvent.getPatientIcn(), imagingLogEvent.getAccessType(), imagingLogEvent.getUserInterface(), 
				imagingLogEvent.getImageCount(), imagingLogEvent.getAdditionalData());
	}
	
	public static ImagingLogEvent translate(FederationImagingLogEventType imagingLogEvent)
	throws TranslationException
	{
		try
		{
			RoutingToken routingToken = 
				translateRoutingToken(imagingLogEvent.getRoutingTokenToLogToString());
			AbstractImagingURN imagingUrn = null;
			String imagingUrnString = imagingLogEvent.getImagingUrnString();
			if(imagingUrnString != null && imagingUrnString.length() > 0)
			{
				imagingUrn = 
					URNFactory.create(imagingUrnString, SERIALIZATION_FORMAT.CDTP, AbstractImagingURN.class);
				
			}
			return new ImagingLogEvent(routingToken, imagingUrn, imagingLogEvent.getPatientIcn(), 
					imagingLogEvent.getAccessType(), imagingLogEvent.getUserInterface(), 
					imagingLogEvent.getImageCount(), imagingLogEvent.getAdditionalData());
			
		} 
		catch (RoutingTokenFormatException rtfX)
		{
			throw new TranslationException(rtfX);
		}
		catch(URNFormatException urnfX)
		{
			throw new TranslationException(urnfX);
		}
	}
	
	public static FederationHealthSummaryType [] translateHealthSummaries(List<HealthSummaryType> healthSummaries)
	{
		if(healthSummaries == null)
			return null;
		FederationHealthSummaryType [] result = new FederationHealthSummaryType[healthSummaries.size()];
		for(int i = 0; i < healthSummaries.size(); i++)
		{
			result[i] = translate(healthSummaries.get(i));
		}
		return result;
	}
	
	private static FederationHealthSummaryType translate(HealthSummaryType healthSummary)
	{
		return new FederationHealthSummaryType(healthSummary.getHealthSummaryUrn().toString(), healthSummary.getName());
	}
	
	public static List<HealthSummaryType> translate(FederationHealthSummaryType [] healthSummaries)
	throws MethodException
	{
		if(healthSummaries == null)
			return null;
		
		List<HealthSummaryType> result = new ArrayList<HealthSummaryType>();
		for(FederationHealthSummaryType healthSummary : healthSummaries)
		{
			result.add(translate(healthSummary));
		}
		return result;
	}
	
	private static HealthSummaryType translate(FederationHealthSummaryType healthSummary)
	throws MethodException
	{
		try
		{
			HealthSummaryURN healthSummaryUrn = URNFactory.create(healthSummary.getHealthSummaryId(), HealthSummaryURN.class);
			return new HealthSummaryType(healthSummaryUrn, healthSummary.getName());
		}
		catch(URNFormatException urnfX)
		{
			throw new MethodException(urnfX);
		}
	}
}