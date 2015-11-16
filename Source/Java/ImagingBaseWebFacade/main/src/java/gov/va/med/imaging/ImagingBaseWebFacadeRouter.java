/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 9, 2012
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswbeckec
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
package gov.va.med.imaging;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.access.TransactionLogWriter;
import gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterInterface;
import gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterInterfaceCommandTester;
import gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterMethod;
import gov.va.med.imaging.core.interfaces.ImageMetadataNotification;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNotFoundException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.ImageAccessLogEvent;
import gov.va.med.imaging.exchange.business.ArtifactResults;
import gov.va.med.imaging.exchange.business.DocumentFilter;
import gov.va.med.imaging.exchange.business.ImageFormatQualityList;
import gov.va.med.imaging.exchange.business.ImageMetadata;
import gov.va.med.imaging.exchange.business.PassthroughInputMethod;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.exchange.business.StudySetResult;
import gov.va.med.imaging.exchange.business.documents.DocumentRetrieveResult;
import gov.va.med.imaging.exchange.business.documents.DocumentSet;
import gov.va.med.imaging.exchange.business.documents.DocumentSetResult;
import gov.va.med.imaging.exchange.enums.DatasourceProtocol;
import gov.va.med.imaging.exchange.enums.ImageQuality;
import gov.va.med.imaging.exchange.enums.SiteConnectivityStatus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

/**
 * @author VHAISWWERFEJ
 *
 */
@FacadeRouterInterface(extendsClassName="gov.va.med.imaging.BaseWebFacadeRouterImpl")
@FacadeRouterInterfaceCommandTester
public interface ImagingBaseWebFacadeRouter 
extends gov.va.med.imaging.BaseWebFacadeRouter
{
	
	/**
	 * Checks to see if the ViX can communicate with the specified site
	 * @param siteNumber The site number to communicate with
	 * @return The status of the site
	 */
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetSiteConnectivityStatusCommand")
	public abstract SiteConnectivityStatus isSiteAvailable(RoutingToken routingToken)
	throws MethodException, ConnectionException;
	
	/**
	 * Given a site number, patient ID and an optional filter, find all of the Study
	 * instances for that patient, at that Site and matching the filter criteria.
	 * 
	 * @param siteNumber
	 * @param patientId
	 * @param filter
	 * @return
	 * @throws MethodException
	 */
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetStudyListBySiteCommand")
	public abstract List<Study> getPatientStudyList(
		RoutingToken routingToken, 
		PatientIdentifier patientIdentifier, 
		StudyFilter filter)
	throws MethodException, ConnectionException;
	
	/**
	 * Given a site number, patient ID and an optional filter, find all of the Study
	 * instances for that patient (fully loaded), at that Site and matching the filter criteria.
	 * 
	 * @param siteNumber
	 * @param patientId
	 * @param filter
	 * @return
	 * @throws MethodException
	 */
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetStudySetResultBySiteCommand")
	public abstract StudySetResult getPatientStudySet(
		RoutingToken routingToken, 
		PatientIdentifier patientIdentifier, 
		StudyFilter filter)
	throws MethodException, ConnectionException;
	
	/**
	 * Given a study identifier, return a single study for the studyUrn specified.
	 * 
	 * @param studyUrn
	 * @return
	 * @throws MethodException
	 */
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetStudyCommand")
	public abstract Study getPatientStudy(StudyURN studyUrn)
	throws MethodException, ConnectionException;
	
	
	/**
	 * Given a study identifier, return a single study for the studyUrn specified.
	 * 
	 * @param studyUrn
	 * @return
	 * @throws MethodException
	 */	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetStudyCommand")
	public abstract Study getPatientStudyWithDeletedImages(StudyURN studyUrn, boolean includeDeletedImages)
	throws MethodException, ConnectionException;
	

	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetStudyCommand")
	public abstract Study getPatientStudy(BhieStudyURN studyUrn)
	throws MethodException, ConnectionException;
	
	
	/**
	 * An image access logging method.  This synchronous command calls an async command to do the actual work
	 *  
	 * @param event
	 * @throws MethodException 
	 */
	@FacadeRouterMethod(asynchronous=false, commandClassName="PostImageAccessEventCommand")
	public abstract void logImageAccessEvent(ImageAccessLogEvent event)
	throws MethodException, ConnectionException;
	
	/**
	 * Call an async command to post an image access event
	 * @param event
	 */
	@FacadeRouterMethod(asynchronous=true, commandClassName="PostImageAccessEventRetryableCommand")
	public abstract void logImageAccessEventRetryable(ImageAccessLogEvent event);
	
	/**
	 * Retrieves image information about an image in the form of a newline terminated string
	 * @param imageUrn
	 * @return - image information or a null if the image is not found
	 * @throws MethodException
	 */
	@FacadeRouterMethod(asynchronous=false)
	public abstract String getImageInformation(AbstractImagingURN imagingUrn)
	throws MethodException, ConnectionException;
	
	/**
	 * Retrieves image information about an image in the form of a newline terminated string
	 * @param imageUrn
	 * @param includeDeletedImages
	 * @return - image information or a null if the image is not found
	 * @throws MethodException
	 */
	@FacadeRouterMethod(asynchronous=false)
	public abstract String getImageInformation(AbstractImagingURN imagingUrn, boolean includeDeletedImages)
	throws MethodException, ConnectionException;

	/**
	 * 
	 * @param imageUrn
	 * @return
	 * @throws MethodException
	 */
	@FacadeRouterMethod(asynchronous=false)
	public abstract String getImageSystemGlobalNode(AbstractImagingURN imagingUrn)
	throws MethodException, ConnectionException;

	/**
	 * 
	 * @param imageURN
	 * @param flags
	 * @return
	 * @throws IOException
	 * @throws ImageNotFoundException
	 * @throws MethodException
	 */
	@FacadeRouterMethod(asynchronous=false)
	public abstract String getImageDevFields(AbstractImagingURN imagingUrn, String flags)
	throws MethodException, ConnectionException;
	
	/**
	 * Given an ImageURN (a globally unique identifier of an image) return the
	 * number of bytes written to the output stream. The metadata callback is called
	 * before writing to the stream
	 * 
	 * @param imageUrn
	 * @param requestedFormatQuality
	 * @param outStream
	 * @param metadataCallback
	 * @return
	 * @throws IOException
	 * @throws MethodException
	 */
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetInstanceByImageUrnCommand")
	public abstract Long getInstanceByImageURN(
			ImageURN imageUrn, 
			ImageFormatQualityList requestedFormatQuality, 
			OutputStream outStream,
			ImageMetadataNotification metadataCallback)
	throws MethodException, ConnectionException;

	/**
	 * 
	 * @param siteNumber
	 * @param patientIcn
	 * @param studyFilter
	 * @param imageFormatList
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	@FacadeRouterMethod(asynchronous=true)
	public abstract void prefetchPatientStudyList(
		RoutingToken routingToken, 
		PatientIdentifier patientIdentifier, 
		StudyFilter studyFilter, 
		ImageFormatQualityList imageFormatList);
	
	/**
	 * 
	 * @param imageUrn
	 * @param outStream
	 * @param metadataNotification
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetImageTextCommand")
	public abstract int getTxtFileByImageURN(
		ImageURN imageUrn, 
		OutputStream outStream, 
		ImageMetadataNotification metadataNotification)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, isChildCommand=true, commandClassName="GetImageTextCommand")
	public abstract int getTxtFileByImageURNAsChild(
		ImageURN imageUrn, 
		OutputStream outStream, 
		ImageMetadataNotification metadataNotification)
	throws MethodException, ConnectionException;
	
	/**
	 * 
	 * @param patientIcn
	 * @param siteNumber
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	@FacadeRouterMethod(asynchronous=false)
	public abstract InputStream getPatientIdentificationImage(
		PatientIdentifier patientIdentifier,
		RoutingToken routingToken)
	throws MethodException, ConnectionException;	
   
   /**
	 * Given an ImageURN (a globally unique identifier of an image) return the
	 * number of bytes written to the output stream. The metadata callback is called
	 * before writing to the stream
	 * 
	 * @param imageUrn
	 * @param requestedFormatQuality
	 * @param outStream
	 * @param metadataCallback
	 * @return
	 * @throws IOException
	 * @throws MethodException
	 */
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetExamInstanceByImageUrnCommand")
	public abstract Long getExamInstanceByImageUrn(
			ImageURN imageUrn, 
			ImageMetadataNotification metadataCallback,
			OutputStream outStream,
			ImageFormatQualityList requestedFormatQuality)
	throws MethodException, ConnectionException;

	/**
	 * Given an ImageURN (a globally unique identifier of an image) cache the image and then
	 * return the number of bytes in the image.
	 * 
	 * 
	 * @param imageUrn
	 * @param requestedFormatQuality
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	@FacadeRouterMethod(asynchronous=false, commandClassName="HeadInstanceByImageUrnCommand")
	public abstract ImageMetadata headInstanceByImageUrn(
		ImageURN imageUrn, 
		ImageFormatQualityList requestedFormatQuality)
	throws MethodException, ConnectionException;
	
	/**
	 * Another version of headInstanceByImageUrn with more parameters.
	 * @param imageUrn
	 * @param imageFormatQualityList
	 * @param outStream
	 * @param forceDatasourceAccess
	 * @param forceSizeCalculation
	 * @param allowCaching
	 * @return
	 */
	@FacadeRouterMethod(asynchronous=false, commandClassName="HeadInstanceByImageUrnCommand")
	public abstract ImageMetadata headInstanceByImageUrnVerbose(
		ImageURN imageUrn, 
		ImageFormatQualityList imageFormatQualityList,
		OutputStream outStream,
		boolean forceDatasourceAccess,
		boolean forceSizeCalculation,
		boolean allowCaching)
	throws MethodException, ConnectionException;
	
	/**
	 * Given a site number, patient ID and filter, find all of the shallow studies for that patient.
	 * Shallow studies do not contain images or the radiology report.
	 * 
	 * @param siteNumber
	 * @param patientId
	 * @param filter
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetShallowStudyListBySiteNumberCommand")
	public abstract List<Study> getPatientShallowStudyList(
		RoutingToken routingToken, 
		PatientIdentifier patientIdentifier, 
		StudyFilter filter)
	throws MethodException, ConnectionException;
	
	/**
	 * Given a site number, patient ID and filter, find all of the shallow studies for that patient.
	 * Shallow studies do not contain images or the radiology report.
	 * 
	 * @param siteNumber
	 * @param patientId
	 * @param filter
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetShallowStudySetResultBySiteNumberCommand")
	public abstract StudySetResult getPatientShallowStudySetResult(
		RoutingToken routingToken, 
		PatientIdentifier patientIdentifier, 
		StudyFilter filter)
	throws MethodException, ConnectionException;
	
	/**
	 * Given a site number, patient ID and filter, find all of the shallow studies with reports
	 * for that patient. Shallow studies do not contain images but these include reports for each 
	 * study.
	 * 
	 * @param siteNumber
	 * @param patientId
	 * @param filter
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetShallowStudyListWithReportBySiteNumberCommand")
	public abstract List<Study> getPatientShallowStudyWithReportList(
		RoutingToken routingToken, 
		PatientIdentifier patientIdentifier, 
		StudyFilter filter)
	throws MethodException, ConnectionException;
	
	/**
	 * Given a site number, patient ID and filter, find all of the shallow studies with reports
	 * for that patient. Shallow studies do not contain images but these include reports for each 
	 * study.
	 * 
	 * @param siteNumber
	 * @param patientId
	 * @param filter
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetShallowStudySetWithReportBySiteNumberCommand")
	public abstract StudySetResult getPatientShallowStudySetWithReport(
		RoutingToken routingToken, 
		PatientIdentifier patientIdentifier, 
		StudyFilter filter)
	throws MethodException, ConnectionException;

	/**
	 * 
	 * @param documentFilter
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetDocumentSetListCommand")
	public abstract List<DocumentSet> getDocumentSetList(RoutingToken routingToken, DocumentFilter documentFilter)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetDocumentSetResultForPatientCommand")
	public abstract DocumentSetResult getDocumentSetResult(RoutingToken routingToken, DocumentFilter documentFilter)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetDocumentSetResultBySiteNumberCommand")
	public abstract DocumentSetResult getDocumentSetResultFromSite(RoutingToken routingToken, DocumentFilter filter)
	throws MethodException, ConnectionException;

	/**
	 * @param siteNumber
	 * @param documentFilter
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetDocumentSetListBySiteNumberCommand")
	public abstract List<DocumentSet> getDocumentSetListBySiteNumber(RoutingToken routingToken, DocumentFilter documentFilter)
	throws MethodException, ConnectionException;
	
	/**
	 * 
	 * @param homeCommunityId
	 * @param siteNumber
	 * @param documentUrn
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	//@FacadeRouterMethod()
	//public abstract DocumentRetrieveResult getDocument(DocumentURN documentUrn)
	//throws MethodException, ConnectionException;

	@FacadeRouterMethod()
	public abstract DocumentRetrieveResult getDocument(GlobalArtifactIdentifier documentIdentifier)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod()
	public abstract DocumentRetrieveResult getDocument(GlobalArtifactIdentifier documentIdentifier,
			ImageMetadataNotification imageMetadataNotification)
	throws MethodException, ConnectionException;

	/**
	 * Get a List of Transaction Log records.
	 * This is a composite command-
	 * 
	 * getTransactionLogEntryList (null, null, null, null, null, null, null, null, null, null, null, null) -
	 * maps to SPI getAllLogEntries ().
	 * 
	 * getTransactionLogEntryList (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, null, null) -
	 * maps to SPI getLogEntries (startDate, endDate, imageQuality, use, modality, datasourceProtocol, errorMessage, imageUrn, transactionId, forward).
	 * 
	 * getTransactionLogEntryList (null, null, null, null, null, null, null, null, null, null, ?, ?) -
	 * maps to SPI getLogEntries (fieldName, fieldValue).
	 * 
	 * @return the List of TransactionLog records meeting the query criteria, if any.
	 * 
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	/*
	@FacadeRouterMethod(asynchronous = false, commandClassName = "GetTransactionLogEntryListCommand")
	public abstract List<TransactionLogEntry> getTransactionLogEntryList(
		Date startDate, Date endDate, ImageQuality imageQuality,
		String user, String modality,
		DatasourceProtocol datasourceProtocol, String errorMessage,
		String imageUrn, String transactionId, Boolean forward,
		String fieldName, String fieldValue, Integer startIndex,
			Integer endIndex) 
	throws MethodException, ConnectionException;
	*/
	
	@FacadeRouterMethod(asynchronous = false, commandClassName = "GetTransactionLogEntriesCommand")
	public abstract void getTransactionLogEntries(
			TransactionLogWriter transactionLogWriter,
			Date               startDate,
			Date               endDate, 
			ImageQuality       imageQuality, 
			String             user, 
			String             modality, 
			DatasourceProtocol datasourceProtocol,
			String             errorMessage,
			String             imageUrn,
			String             transactionId, 
			Boolean            forward,
			Integer            startIndex,
			Integer            endIndex) 
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetExamTextFileByImageUrnCommand")
	public abstract Integer getExamTextFileByImageUrn(
		ImageURN imageUrn, 
		ImageMetadataNotification metadataCallback,
		OutputStream outStream)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, isChildCommand=true, commandClassName="GetExamTextFileByImageUrnCommand")
	public abstract Integer getExamTextFileByImageUrnAsChild(
		ImageURN imageUrn, 
		ImageMetadataNotification metadataCallback,
		OutputStream outStream)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetStudyListWithImagesCommand")
	public abstract List<Study> getPatientStudyWithImagesList(
		RoutingToken routingToken, 
		PatientIdentifier patientIdentifier, 
		StudyFilter filter)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetStudySetResultWithImagesBySiteNumberCommand")
	public abstract StudySetResult getPatientStudySetResultWithImages(
		RoutingToken routingToken, 
		PatientIdentifier patientIdentifier, 
		StudyFilter filter)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="PostPassthroughMethodCommand")
	public abstract String postPassthroughMethod(
		RoutingToken routingTokenr, 
		PassthroughInputMethod inputMethod)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetExamInstanceByImageUrnNotCachedCommand")
	public abstract Long getExamInstanceByImageUrnNotFromCache(
			ImageURN imageUrn, 
			ImageMetadataNotification metadataCallback,
			OutputStream outStream,
			ImageFormatQualityList requestedFormatQuality)
	throws MethodException, ConnectionException;
	
	/**
	 * Given an ImageURN (a globally unique identifier of an image) return the
	 * number of bytes written to the output stream only if the image is in the
	 * cache.  If the image is not in the cache, an exception will be thrown.
	 * The metadata callback is called before writing to the stream
	 * 
	 * @param imageUrn
	 * @param requestedFormatQuality
	 * @param outStream
	 * @param metadataCallback
	 * @return
	 * @throws IOException
	 * @throws MethodException
	 */
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetExamInstanceFromCacheByImageUrnCommand")
	public abstract Long getExamInstanceFromCacheByImageUrn(
			ImageURN imageUrn, 
			ImageMetadataNotification metadataCallback,
			OutputStream outStream,
			ImageFormatQualityList requestedFormatQuality)
	throws MethodException, ConnectionException;
	
	/**
	 * Get studies with images for the patient from all sites
	 * @param patientId
	 * @param studyFilter
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetStudySetResultWithImagesForPatientCommand")
	public abstract StudySetResult getStudySetResultWithImagesForPatient(PatientIdentifier patientIdentifier, StudyFilter studyFilter)
	throws MethodException, ConnectionException;
	
	/**
	 * Get fully loaded studies for the patient from all sites
	 * @param patientId
	 * @param studyFilter
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetStudySetResultForPatientCommand")
	public abstract StudySetResult getStudySetResultForPatient(PatientIdentifier patientIdentifier, StudyFilter studyFilter)
	throws MethodException, ConnectionException;
	
	/**
	 * Get studies with reports for the patient from all sites
	 * @param patientId
	 * @param studyFilter
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetStudySetResultWithReportForPatientCommand")
	public abstract StudySetResult getStudySetResultWithReportsForPatient(PatientIdentifier patientIdentifier, StudyFilter studyFilter)
	throws MethodException, ConnectionException;
	
	/**
	 * Get studies with no images or reports for patient from all sites
	 * @param patientId
	 * @param studyFilter
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetShallowStudySetResultForPatientCommand")
	public abstract StudySetResult getShallowStudySetResultForPatient(PatientIdentifier patientIdentifier, StudyFilter studyFilter)
	throws MethodException, ConnectionException;
	
	/**
	 * Get the artifact results (study level only) for a patient from a specific site
	 * @param routingToken
	 * @param patientId
	 * @param filter
	 * @param includeRadiology
	 * @param includeDocuments
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetStudyOnlyArtifactResultsBySiteNumberCommand")
	public abstract ArtifactResults getShallowArtifactResultsForPatientFromSite(RoutingToken routingToken,
		PatientIdentifier patientIdentifier, 
		StudyFilter filter, 
		boolean includeRadiology, 
		boolean includeDocuments)
	throws MethodException, ConnectionException;
	
	/**
	 * Get the artifact results (study and report) for a patient from a specific site
	 * @param routingToken
	 * @param patientId
	 * @param filter
	 * @param includeRadiology
	 * @param includeDocuments
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetStudyWithReportArtifactResultsBySiteNumberCommand")
	public abstract ArtifactResults getStudyWithReportArtifactResultsForPatientFromSite(RoutingToken routingToken,
		PatientIdentifier patientIdentifier, 
		StudyFilter filter, 
		boolean includeRadiology, 
		boolean includeDocuments)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetDocumentStreamCommand")
	public abstract Long getDocumentStreamed(GlobalArtifactIdentifier documentIdentifier, OutputStream outStream,
			ImageMetadataNotification imageMetadataNotification)
	throws MethodException, ConnectionException;

}
