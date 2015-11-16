/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 7, 2011
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
package gov.va.med.imaging.router.facade;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.AbstractImagingURN;
import gov.va.med.imaging.CprsIdentifier;
import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.StudyURN;
import gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterDataSourceMethod;
import gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterInterface;
import gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterInterfaceCommandTester;
import gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterMethod;
import gov.va.med.imaging.core.interfaces.FacadeRouter;
import gov.va.med.imaging.core.interfaces.ImageMetadataNotification;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.router.AsynchronousCommandResultListener;
import gov.va.med.imaging.core.interfaces.router.CumulativeCommandStatistics;
import gov.va.med.imaging.exchange.ImageAccessLogEvent;
import gov.va.med.imaging.exchange.ImagingLogEvent;
import gov.va.med.imaging.exchange.business.ArtifactResults;
import gov.va.med.imaging.exchange.business.DocumentFilter;
import gov.va.med.imaging.exchange.business.Image;
import gov.va.med.imaging.exchange.business.ImageFormatQualityList;
import gov.va.med.imaging.exchange.business.ImageStreamResponse;
import gov.va.med.imaging.exchange.business.Patient;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.exchange.business.StudySetResult;
import gov.va.med.imaging.exchange.business.annotations.ImageAnnotation;
import gov.va.med.imaging.exchange.business.annotations.ImageAnnotationDetails;
import gov.va.med.imaging.exchange.business.annotations.ImageAnnotationSource;
import gov.va.med.imaging.exchange.business.documents.DocumentRetrieveResult;
import gov.va.med.imaging.exchange.business.documents.DocumentSetResult;
import gov.va.med.imaging.exchange.business.vistarad.Exam;
import gov.va.med.imaging.exchange.business.vistarad.ExamImage;
import gov.va.med.imaging.exchange.business.vistarad.ExamImages;
import gov.va.med.imaging.exchange.business.vistarad.ExamListResult;
import gov.va.med.imaging.exchange.business.vistarad.PatientEnterpriseExams;
import gov.va.med.imaging.exchange.business.vistarad.PatientRegistration;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.exchange.storage.DataSourceInputStream;

/**
 * @author VHAISWWERFEJ
 *
 */
@FacadeRouterInterface
@FacadeRouterInterfaceCommandTester
public interface ImagingRouter
extends FacadeRouter
{
	@FacadeRouterMethod(asynchronous=false, isChildCommand=true, commandClassName="PostImageAccessEventFromDataSourceCommand")
	public abstract void postImageAccessEvent(RoutingToken routingToken, ImageAccessLogEvent event)
	throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=true, isChildCommand=true, commandClassName="PostImageAccessEventRetryableCommand")
	public abstract void postImageAccessEventRetryable(ImageAccessLogEvent event);
	
	@FacadeRouterMethod(asynchronous=true, isChildCommand=true, commandClassName="PrefetchInstanceByImageUrnCommand")
	public abstract void prefetchInstanceByImageUrn(ImageURN imageUrn,
			ImageFormatQualityList imageFormatQualityList);
			
	@FacadeRouterMethod(asynchronous=true, isChildCommand=true, commandClassName="PrefetchInstanceByImageUrnCommand", priority=1, delay=60000)
	public abstract void prefetchInstanceByImageUrnDelayOneMinute(ImageURN imageUrn,
			ImageFormatQualityList imageFormatQualityList);
	
	@FacadeRouterMethod(asynchronous=true, isChildCommand=true, commandClassName="PrefetchExamInstanceByImageUrnCommand", priority=1, delay=60000)
	public abstract void prefetchExamInstanceByImageUrnDelayOneMinute(ImageURN imageUrn,
			ImageFormatQualityList imageFormatQualityList);
	
	@FacadeRouterMethod(asynchronous=true, isChildCommand=true, commandClassName="GetDocumentSetListBySiteNumberCommand", asynchronousCommandResultListenerParameterName="listener", priority=2)
	public abstract void getDocumentSetListBySiteNumber(RoutingToken routingToken, DocumentFilter filter, AsynchronousCommandResultListener<?> listener)
	throws MethodException, ConnectionException;
	
	

	@FacadeRouterMethod(asynchronous=false, isChildCommand=true, commandClassName="GetStudiesByCprsIdentifierFromDataSourceCommand")
	public abstract List<Study> getStudyFromCprsIdentifier(RoutingToken routingToken, String patientIcn, CprsIdentifier cprsIdentifier)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, isChildCommand=true)
	public abstract PatientEnterpriseExams getPatientEnterpriseExams(RoutingToken routingToken, String patientIcn, Boolean fullyLoadExams)
	throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=false, isChildCommand=true)
	public abstract PatientRegistration getPatientRegistration(RoutingToken routingToken)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, isChildCommand=true)
	public abstract String[] getRelevantPriorCptCodes(RoutingToken routingToken, String cptCode)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=true, isChildCommand=true, commandClassName="PrefetchExamImagesAsyncCommand")
	public abstract void prefetchExamImages(StudyURN studyUrn)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, isChildCommand=true)
	public abstract void getExamInstanceByImageUrn(ImageURN imageUrn, ImageFormatQualityList imageFormatQualityList)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, isChildCommand=true, commandClassName="GetExamTextFileByImageUrnCommand")
	public abstract Integer getExamTextFileByImageUrn(ImageURN imageUrn)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, isChildCommand=true, commandClassName="GetExamTextFileByImageUrnCommand")
	public abstract Integer getExamTextFileByImageUrn(ImageURN imageUrn, ImageMetadataNotification imageMetadataNotification, OutputStream outStream)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=true, isChildCommand=true, commandClassName="GetFullyLoadedExamSiteBySiteNumberCommand")
	public abstract void getFullyLoadedExamSite(RoutingToken routingToken, String patientIcn, boolean forceRefresh,
			boolean forceImagesFromJb);
	
	@FacadeRouterMethod(asynchronous=true, isChildCommand=true, commandClassName="GetFullyLoadedExamSiteBySiteNumberCommand", asynchronousCommandResultListenerParameterName="listener", priority=2)
	public abstract void getFullyLoadedExamSite(RoutingToken routingToken, String patientIcn, boolean forceRefresh,
			boolean forceImagesFromJb, AsynchronousCommandResultListener listener);
	
	@FacadeRouterMethod(asynchronous=true, isChildCommand=true, commandClassName="GetExamSiteBySiteNumberCommand", asynchronousCommandResultListenerParameterName="listener", priority=2)
	public abstract void getExamSiteBySiteNumber(RoutingToken routingToken, String patientIcn, 
			Boolean forceRefresh, Boolean forceImagesFromJb, AsynchronousCommandResultListener listener);
	
	@FacadeRouterMethod(asynchronous=false, isChildCommand=true, commandClassName="GetExamTextFileByImageUrnFromDataSourceCommand")
	public abstract DataSourceInputStream getExamTextFileFromDataSource(ImageURN imageUrn)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, isChildCommand=true, commandClassName="GetExamTextFileByExamImageFromDataSourceCommand")
	public abstract DataSourceInputStream getExamTextFileFromDataSource(ExamImage examImage)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, isChildCommand=true, commandClassName="GetExamInstanceByExamImageFromDataSourceCommand")
	public abstract ImageStreamResponse getExamInstanceFromDataSource(ExamImage image, ImageFormatQualityList requestFormatQuality)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, isChildCommand=true, commandClassName="GetExamInstanceByImageUrnFromDataSourceCommand")
	public abstract ImageStreamResponse getExamInstanceFromDataSource(ImageURN imageUrn, ImageFormatQualityList requestFormatQuality)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, isChildCommand=true, commandClassName="GetExamReportFromDataSourceCommand")
	public abstract String getExamReportFromDataSource(StudyURN studyUrn)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, isChildCommand=true, commandClassName="GetExamRequisitionReportFromDataSourceCommand")
	public abstract String getExamRequisitionReportFromDataSource(StudyURN studyUrn)
	throws MethodException, ConnectionException;
		
	@FacadeRouterMethod(asynchronous=false, isChildCommand=true, commandClassName="GetExamsForPatientFromDataSourceCommand")
	public abstract ExamListResult getExamsForPatientFromDataSource(RoutingToken routingToken, String patientIcn, boolean fullyLoadExams, 
			boolean forceRefresh, boolean forceImagesFromJb)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, isChildCommand=true, commandClassName="GetExamImagesForExamFromDataSourceCommand")
	public abstract ExamImages getExamImagesForExamFromDataSource(StudyURN studyUrn)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, isChildCommand=true, commandClassName="GetExamFromDataSourceCommand")
	public abstract Exam getExamFromDataSource(StudyURN studyUrn)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, isChildCommand=true, commandClassName="GetPatientDocumentSetsDataSourceCommand")
	public abstract DocumentSetResult getPatientDocumentSets(RoutingToken routingToken, String patientIcn, DocumentFilter filter)
	throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=false, isChildCommand=true, commandClassName="GetDocumentFromDataSourceCommand")
	public abstract ImageStreamResponse getDocumentFromDataSource(GlobalArtifactIdentifier documentIdentifier)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, isChildCommand=true, commandClassName="GetInstanceByImageUrnFromDataSourceCommand")
	public abstract ImageStreamResponse getInstanceByImageUrn(ImageURN imageUrn, ImageFormatQualityList requestFormatQualityList)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, isChildCommand=true, commandClassName="GetInstanceByImageFromDataSourceCommand")
	public abstract ImageStreamResponse getInstanceByImage(Image image, ImageFormatQualityList requestFormatQualityList)
	throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=false, isChildCommand=true, commandClassName="GetInstanceTextFileByImageFromDataSourceCommand")
	public abstract DataSourceInputStream getInstanceTextFileByImage(Image image)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, isChildCommand=true, commandClassName="GetInstanceTextFileByImageUrnFromDataSourceCommand")
	public abstract DataSourceInputStream getInstanceTextFileByImageUrn(ImageURN imageUrn)
	throws MethodException, ConnectionException;
		
	@FacadeRouterMethod(asynchronous=true, isChildCommand=true, commandClassName="GetStudyListBySiteCommand")
	public abstract void getStudyList(RoutingToken routingToken, PatientIdentifier patientIdentifier, StudyFilter filter);
		
	@FacadeRouterMethod(asynchronous=false, isChildCommand=true, commandClassName="GetStudySetBySiteNumberFromDataSourceCommand")
	public abstract StudySetResult getStudySet(RoutingToken routingToken, PatientIdentifier patientIdentifier, 
			StudyFilter filter, StudyLoadLevel studyLoadLevel)
	throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=false, isChildCommand=true, commandClassName="GetPatientIdentificationImageFromDataSourceCommand")
	public abstract InputStream getPatientIdentificationImage(RoutingToken routingToken, PatientIdentifier patientIdentifier)
	throws MethodException, ConnectionException;	
	
	@FacadeRouterMethod(asynchronous=true, commandClassName="GetStudySetResultWithImagesBySiteNumberCommand", isChildCommand=true, asynchronousCommandResultListenerParameterName="listener")
	public void getStudySetResultWithImagesBySiteNumber(RoutingToken routingToken, PatientIdentifier patientIdentifier, StudyFilter filter, AsynchronousCommandResultListener listener);
	
	@FacadeRouterMethod(asynchronous=true, commandClassName="GetShallowStudySetWithReportBySiteNumberCommand", isChildCommand=true, asynchronousCommandResultListenerParameterName="listener")	
	public void getStudySetResultWithReportsBySiteNumber(RoutingToken routingToken, PatientIdentifier patientIdentifier, 
			StudyFilter filter, AsynchronousCommandResultListener listener); 
	
	@FacadeRouterMethod(asynchronous=true, commandClassName="GetShallowStudySetResultBySiteNumberCommand", isChildCommand=true, asynchronousCommandResultListenerParameterName="listener")	
	public void getShallowStudySetResultBySiteNumber(RoutingToken routingToken, PatientIdentifier patientIdentifier, 
			StudyFilter filter, AsynchronousCommandResultListener listener);
	
	/**
	 * Get fully loaded studies
	 * @param routingToken
	 * @param patientId
	 * @param filter
	 * @param listener
	 */
	@FacadeRouterMethod(asynchronous=true, commandClassName="GetStudySetResultBySiteCommand", isChildCommand=true, asynchronousCommandResultListenerParameterName="listener")	
	public void getStudySetResultBySiteNumber(RoutingToken routingToken, PatientIdentifier patientIdentifier, 
			StudyFilter filter, AsynchronousCommandResultListener listener);
	
	

	/**
	 * This method returns CumulativeCommandStatistics<StudySetResult> but isn't defined that way because it confuses the CoreRouterAnnotationProcessor
	 * @param patientId
	 * @param studyFilter
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetCumulativeStatisticsStudySetResultForPatientCommand", isChildCommand=true)
	public abstract CumulativeCommandStatistics getCumulativeStatisticsStudySetResultForPatient(PatientIdentifier patientIdentifier, StudyFilter studyFilter, StudyLoadLevel studyLoadLevel)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=true, isChildCommand=true, commandClassName="GetDocumentSetResultBySiteNumberCommand", asynchronousCommandResultListenerParameterName="listener", priority=2)
	public abstract void getDocumentSetResultBySiteNumber(RoutingToken routingToken, DocumentFilter filter, AsynchronousCommandResultListener<?> listener);
	
	/**
	 * This method returns CumulativeCommandStatistics<DocumentSetResult> but isn't defined that way because it confuses the CoreRouterAnnotationProcessor
	 * @param patientId
	 * @param studyFilter
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetCumulativeStatisticsDocumentSetResultCommand", isChildCommand=true)
	public abstract CumulativeCommandStatistics getCumulativeStatisticsDocumentSetResultForPatient(RoutingToken routingToken, DocumentFilter documentFilter)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetFullyLoadedArtifactResultsBySiteNumberDataSourceCommand", isChildCommand=true)
	public abstract ArtifactResults getFullyLoadedPatientArtifactResultsFromSite(RoutingToken routingToken, 
			PatientIdentifier patientIdentifier, 
			StudyFilter filter, 
			boolean includeRadiology,
			boolean includeDocuments)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetStudyOnlyArtifactResultsBySiteNumberDataSourceCommand", isChildCommand=true)
	public abstract ArtifactResults getStudyOnlyPatientArtifactResultsFromSite(RoutingToken routingToken, 
			PatientIdentifier patientIdentifier, 
			StudyFilter filter, 
			boolean includeRadiology,
			boolean includeDocuments)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=true, commandClassName="GetStudyOnlyArtifactResultsBySiteNumberCommand", 
			isChildCommand=true, asynchronousCommandResultListenerParameterName="listener")
	public abstract void getStudyOnlyPatientArtifactResultsFromSiteAsync(RoutingToken routingToken,
			PatientIdentifier patientIdentifier, 
			StudyFilter filter, 
			boolean includeRadiology, 
			boolean includeDocuments,
			AsynchronousCommandResultListener listener);
	
	@FacadeRouterMethod(asynchronous=true, commandClassName="GetStudyWithImagesArtifactResultsBySiteNumberCommand", 
			isChildCommand=true, asynchronousCommandResultListenerParameterName="listener")
	public abstract void getStudyWithImagesPatientArtifactResultsFromSiteAsync(RoutingToken routingToken,
			PatientIdentifier patientIdentifier, 
			StudyFilter filter, 
			boolean includeRadiology, 
			boolean includeDocuments,
			AsynchronousCommandResultListener listener);
	
	@FacadeRouterMethod(asynchronous=true, commandClassName="GetStudyWithReportArtifactResultsBySiteNumberCommand", 
			isChildCommand=true, asynchronousCommandResultListenerParameterName="listener")
	public abstract void getStudyWithReportPatientArtifactResultsFromSiteAsync(RoutingToken routingToken,
			PatientIdentifier patientIdentifier, 
			StudyFilter filter, 
			boolean includeRadiology, 
			boolean includeDocuments,
			AsynchronousCommandResultListener listener);
	
	@FacadeRouterMethod(asynchronous=true, commandClassName="GetFullyLoadedArtifactResultsBySiteNumberCommand", 
			isChildCommand=true, asynchronousCommandResultListenerParameterName="listener")
	public abstract void getPatientArtifactResultsFromSiteAsync(RoutingToken routingToken,
			PatientIdentifier patientIdentifier, 
			StudyFilter filter, 
			boolean includeRadiology, 
			boolean includeDocuments,
			AsynchronousCommandResultListener listener);
		
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetStudyWithImagesArtifactResultsBySiteNumberDataSourceCommand", isChildCommand=true)
	public abstract ArtifactResults getStudyWithImagesPatientArtifactResultsFromSite(RoutingToken routingToken, 
			PatientIdentifier patientIdentifier, 
			StudyFilter filter, 
			boolean includeRadiology,
			boolean includeDocuments)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetStudyWithReportArtifactResultsBySiteNumberDataSourceCommand", isChildCommand=true)
	public abstract ArtifactResults getStudyWithReportPatientArtifactResultsFromSite(RoutingToken routingToken, 
			PatientIdentifier patientIdentifier, 
			StudyFilter filter, 
			boolean includeRadiology,
			boolean includeDocuments)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, isChildCommand=true, commandClassName="GetDocumentCommand")
	public abstract DocumentRetrieveResult getDocument(GlobalArtifactIdentifier artifactIdentifier)
	throws ConnectionException, MethodException;	
	
	@FacadeRouterMethod(asynchronous=true, isChildCommand=true, commandClassName="PrefetchPatientStudiesAsyncCommand")
	public abstract void prefetchPatientStudies(RoutingToken routingToken, PatientIdentifier patientIdentifier, 
			StudyFilter filter, StudyLoadLevel studyLoadLevel);
	
	
	@FacadeRouterMethod(asynchronous=true, isChildCommand=true, commandClassName="GetExamInstanceByImageUrnCommand")
	public abstract void getExamInstanceByImageUrnAsync(ImageURN imageUrn, ImageFormatQualityList imageFormatQualityList);
	
	@FacadeRouterMethod(asynchronous=true, isChildCommand=true, commandClassName="GetExamTextFileByImageUrnCommand")
	public abstract void getExamTextFileByImageUrnAsync(ImageURN imageUrn);

	@FacadeRouterMethod(asynchronous=true, isChildCommand=true, commandClassName="PrefetchDocumentAsyncCommand")
	public abstract void prefetchDocument(GlobalArtifactIdentifier gai);

	@FacadeRouterMethod(asynchronous=false, isChildCommand=true, commandClassName="PostImageAnnotationDetailsDataSourceCommand")
	@FacadeRouterDataSourceMethod(commandClassName="PostImageAnnotationDetailsDataSourceCommand", commandPackage="gov.va.med.imaging.router.commands.annotations.datasource",
			dataSourceSpi="ImageAnnotationDataSourceSpi", methodName="storeImageAnnotationDetails", routingTokenParameterName="imagingUrn")
	public abstract ImageAnnotation postImageAnnotationDetails(AbstractImagingURN imagingUrn, String annotationDetails,
			String annotationVersion, ImageAnnotationSource annotationSource)
	throws ConnectionException, MethodException;
	
	@FacadeRouterMethod(asynchronous=false, isChildCommand=true, commandClassName="PostImagingLogEventDataSourceCommand")
	@FacadeRouterDataSourceMethod(commandClassName="PostImagingLogEventDataSourceCommand", commandPackage="gov.va.med.imaging.router.commands.datasource",
			dataSourceSpi="ImageAccessLoggingSpi", methodName="LogImagingLogEvent", routingTokenParameterName="routingToken",
			spiParameterNames="imagingLogEvent")
	public abstract void postImagingLogEvent(RoutingToken routingToken, ImagingLogEvent imagingLogEvent)
	throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=true, isChildCommand=true, commandClassName="PostImagingLogEventRetryableCommand")
	public abstract void postImagingLogEventRetryable(ImagingLogEvent imagingLogEvent);
	
	@FacadeRouterMethod(asynchronous=false, isChildCommand=true, commandClassName="GetMostRecentImageAnnotationDetailsCommand")
	public abstract ImageAnnotationDetails getMostRecentImageAnnotationDetails(AbstractImagingURN imagingUrn)
	throws ConnectionException, MethodException;
	
	/**
	 * Prefetch an instance synchronously - puts the image into the cache. If there is an error an exception is thrown
	 * @param imageUrn
	 * @param imageFormatQualityList
	 * @param imageMetadataNotification
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	@FacadeRouterMethod(asynchronous=false, isChildCommand=true, commandClassName="PrefetchInstanceByImageUrnCommand")
	public abstract void prefetchInstanceByImageUrnSync(ImageURN imageUrn,
			ImageFormatQualityList imageFormatQualityList, ImageMetadataNotification imageMetadataNotification)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, isChildCommand=true, commandClassName="GetPatientInformationCommand")
	public abstract Patient getPatientInformation(RoutingToken routingToken, PatientIdentifier patientIdentifier)
	throws MethodException, ConnectionException;
	
	@SuppressWarnings("rawtypes")
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetCumulativeStatisticsArtifactResultsForPatientCommand", isChildCommand=true)
	public abstract CumulativeCommandStatistics getCumulativeStatisticsArtifactResultsForPatient(
			RoutingToken patientTreatingSiteRoutingToken,
			PatientIdentifier patientIdentifier, 
			StudyFilter studyFilter, 
			boolean includeRadiology,
			boolean includeDocuments,
			StudyLoadLevel studyLoadLevel)
	throws MethodException, ConnectionException;
}
