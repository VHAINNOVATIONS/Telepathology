/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jan 15, 2010
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
package gov.va.med.imaging.health;

/**
 * @author vhaiswwerfej
 *
 */
public class VixServerHealthProperties 
{
	public final static String VIX_SERVER_HEALTH_VIX_CONFIG_DIR_KEY = "VixConfigDir";
	public final static String VIX_SERVER_HEALTH_VIX_CACHE_DIR_KEY = "VixCacheDir";
	public final static String VIX_SERVER_HEALTH_VIX_SYSTEM_DRIVE = "VixSystemDrive";
	public final static String VIX_SERVER_HEALTH_VIX_HOSTNAME = "VixHostname";
	
	public final static String VIX_SERVER_HEALTH_VIX_VERSION = "VixVersion";
	
	public final static String VIX_SERVER_HEALTH_VIX_ROOT_DRIVE_KEY = "VixRootDrive";
	public final static String VIX_SERVER_HEALTH_VIX_ROOT_DRIVE_USABLE_SPACE = "VixRootDriveUsableSpace";
	public final static String VIX_SERVER_HEALTH_VIX_ROOT_DRIVE_TOTAL_SPACE = "VixRootDriveTotalSpace";
	public final static String VIX_SERVER_HEALTH_VIX_TOMCAT_DIR = "VixTomcatDir";
	public final static String VIX_SERVER_HEALTH_VIX_TOMCAT_LOGS_DIR = "VixTomcatLogsDir";	
	public final static String VIX_SERVER_HEALTH_VIX_TOMCAT_LOGS_DIR_SIZE = "VixTomcatLogsDirSize";
	public final static String VIX_SERVER_HEALTH_VIX_TOMCAT_LOGS_DIR_ROOT_DRIVE_AVAILABLE = "VixTomcatLogsDirRootDriveAvailable";
	public final static String VIX_SERVER_HEALTH_VIX_TOMCAT_LOGS_DIR_ROOT_DRIVE_TOTAL = "VixTomcatLogsDirRootDriveTotal";
	public final static String VIX_SERVER_HEALTH_VIX_CACHE_DIR_SIZE = "VixCacheDirSize";
	public final static String VIX_SERVER_HEALTH_VIX_CACHE_DIR_ROOT_DRIVE_AVAILABLE = "VixCacheDirRootDriveAvailable";
	public final static String VIX_SERVER_HEALTH_VIX_CACHE_DIR_ROOT_DRIVE_TOTAL = "VixCacheDirRootDriveTotal";
	public final static String VIX_SERVER_HEALTH_VIX_TRANSACTIONLOGS_DIR_SIZE = "VixTransactionLogsDirSize";
	public final static String VIX_SERVER_HEALTH_VIX_TRANSACTIONLOGS_DIR = "VixTransactionLogsDir";
	public final static String VIX_SERVER_HEALTH_VIX_TRANSACTIONLOGS_DIR_ROOT_DRIVE_AVAILABLE = "VixTransactionLogsDirRootDriveAvailable";
	public final static String VIX_SERVER_HEALTH_VIX_TRANSACTIONLOGS_DIR_ROOT_DRIVE_TOTAL = "VixTransactionLogsDirRootDriveTotal";
	
	public final static String VIX_SERVER_HEALTH_VIX_JMX_CACHE_URI = "VixJMXCacheUri";
	public final static String VIX_SERVER_HEALTH_VIX_JMX_CACHE_ENABLED = "VixJMXCacheEnabled";
	public final static String VIX_SERVER_HEALTH_VIX_JMX_CACHE_OPERATIONS_INITIATED = "VixJMXCacheOperationsInitiated";
	public final static String VIX_SERVER_HEALTH_VIX_JMX_CACHE_OPERATIONS_SUCCESSFUL = "VixJMXCacheOperationsSuccessful";
	public final static String VIX_SERVER_HEALTH_VIX_JMX_CACHE_OPERATIONS_ERROR = "VixJMXCacheOperationsError";
	public final static String VIX_SERVER_HEALTH_VIX_JMX_CACHE_OPERATIONS_INSTANCE_NOT_FOUND = "VixJMXCacheOperationsInstanceNotFound";
	
	public final static String VIX_SERVER_HEALTH_VIX_JVM_UPTIME = "VixJVMUptime";
	public final static String VIX_SERVER_HEALTH_VIX_JVM_STARTTIME = "VixJVMStartTime";
	
	public final static String VIX_SERVER_HEALTH_REALM_JMX_VISTA_PORT = "RealmVistaPort";
	public final static String VIX_SERVER_HEALTH_REALM_JMX_VISTA_SERVER = "RealmVistaServer";
	
	public final static String VIX_SERVER_HEALTH_BUFFER_POOL_MANAGER_POOL_COUNT = "BufferPoolCount";
	public final static String VIX_SERVER_HEALTH_BUFFER_POOL_MANAGER_HIGHEST_OVERLOAD_REQUEST = "BufferPoolHighestOverloadRequest";
	public final static String VIX_SERVER_HEALTH_BUFFER_POOL_MANAGER_OVERLOAD_REQUEST_COUNT = "BufferPoolOverloadRequestCount";
	public final static String VIX_SERVER_HEALTH_BUFFER_POOL_MANAGER_TOTAL_REQUEST_COUNT = "BufferPoolTotalRequestCount";
	public final static String VIX_SERVER_HEALTH_BUFFER_POOL_MANAGER_TOTAL_BUFER_SIZE_USE = "BufferPoolTotalBufferSizeUse"; 
	
	public final static String VIX_SERVER_HEALTH_CATALINA_THREAD_POOL_COUNT = "CatalinaThreadPoolThreadCount";
	public final static String VIX_SERVER_HEALTH_CATALINA_THREAD_POOL_THREADS_BUSY = "CatalinaThreadPoolThreadsBusy";
	public final static String VIX_SERVER_HEALTH_CATALINA_THREAD_POOL_MAX_THREADS = "CatalinaThreadPoolMaxThreads";
	
	public final static String VIX_SERVER_HEALTH_CATALINA_REQUEST_PROCESSING_TIME = "CatalinaRequestProcessingTime";
	
	public final static String VIX_SERVER_HEALTH_TRANSACTION_LOG_STATISTICS_TRANSACTIONS_WRITTEN = "TransactionLogStatisticsTransactionsWritten";
	public final static String VIX_SERVER_HEALTH_TRANSACTION_LOG_STATISTICS_TRANSACTIONS_QUERIED = "TransactionLogStatisticsTransactionsQueried";
	public final static String VIX_SERVER_HEALTH_TRANSACTION_LOG_STATISTICS_TRANSACTIONS_PURGED = "TransactionLogStatisticsTransactionsPurged";
	public final static String VIX_SERVER_HEALTH_TRANSACTION_LOG_STATISTICS_TRANSACTION_READ_ERRORS = "TransactionLogStatisticsTransactionReadErrors";
	public final static String VIX_SERVER_HEALTH_TRANSACTION_LOG_STATISTICS_TRANSACTION_WRITE_ERRORS = "TransactionLogStatisticsTransactionWriteErrors";
	public final static String VIX_SERVER_HEALTH_TRANSACTION_LOG_STATISTICS_TRANSACTION_ERRORS = "TransactionLogStatisticsTransactionErrors";

	public final static String VIX_SERVER_HEALTH_CATALINA_CLINICAL_DISPLAY_REQUEST_COUNT = "CatalinaClinicalDisplayRequestCount";
	public final static String VIX_SERVER_HEALTH_CATALINA_VISTARAD_REQUEST_COUNT = "CatalinaVistaRadRequestCount";
	public final static String VIX_SERVER_HEALTH_CATALINA_EXCHANGE_REQUEST_COUNT = "CatalinaExchangeRequestCount";
	public final static String VIX_SERVER_HEALTH_CATALINA_FEDERATION_REQUEST_COUNT = "CatalinaFederationRequestCount";
	public final static String VIX_SERVER_HEALTH_CATALINA_AWIV_REQUEST_COUNT = "CatalinaAWIVRequestCount";
	public final static String VIX_SERVER_HEALTH_CATALINA_XCA_REQUEST_COUNT = "CatalinaXCARequestCount";
	public final static String VIX_SERVER_HEALTH_CATALINA_SITE_SERVICE_REQUEST_COUNT = "CatalinaSiteServiceRequestCount";


	public final static String VIX_DCF_VERSION = "DCFVersion";
	public final static String VIX_DCF_LICENSE_EXPIRATION_DATE = "DCFLicenseExpirationDate";

	public final static String VIX_SERVICE_ACCOUNT_VALID = "ServiceAccountValid";
	public final static String VIX_TOTAL_RAD_OBJECTS_TO_CORRECT = "TotalRADObjectsInDicomCorrect";
	public final static String VIX_TOTAL_CON_OBJECTS_TO_CORRECT = "TotalCONObjectsInDicomCorrect";
	public final static String VIX_TOTAL_DICOM_CORRECT_WORK_ITEMS = "DicomCorrectWorkItemCount";
	public final static String VIX_TOTAL_NETWORK_IMPORT_WORK_ITEMS = "NetworkImportWorkItemCount";
	public final static String VIX_TOTAL_STAGED_MEDIA_WORK_ITEMS = "StagedMediaWorkItemCount";
	public final static String VIX_TOTAL_FAILED_WORK_ITEMS = "FailedWorkItemCount";
	
	public final static String VIX_LISTENING_PORT = "PortName";
	public final static String VIX_LISTENING_PORT_CURRENT_STATUS = "CurrentStatus";
	public final static String VIX_LISTENING_PORT_LIVE_SINCE = "ListeningSince";

	public final static String VIX_INBOUND_ASSOCIATION_AETITLE = "AETitle";
	public final static String VIX_INBOUND_ASSOCIATION_IP_ADDRESS = "IPAddress";
	public final static String VIX_INBOUND_ASSOCIATION_LAST_ACCESS_TIMESTAMP = "LastAccess";
	public final static String VIX_INBOUND_ASSOCIATION_TOTAL_ACCEPTED_ASSOCIATIONS = "TotalAcceptedAssociations";
	public final static String VIX_INBOUND_ASSOCIATION_TOTAL_REJECTED_ASSOCIATIONS = "TotalRejectedAssociations";

	public final static String VIX_INBOUND_DIMSE_AETITLE = "AETitle";
	public final static String VIX_INBOUND_DIMSE_SERVICE_NAME = "DimseServiceName";
	public final static String VIX_INBOUND_DIMSE_TOTAL_PROCESSED_MESSAGES = "TotalProcessedDimseMessages";
	public final static String VIX_INBOUND_DIMSE_TOTAL_REJECTED_MESSAGES = "TotalFailedDimseMessages";

	public final static String VIX_INBOUND_OBJECTS_AETITLE = "AETitle";
	public final static String VIX_INBOUND_OBJECTS_TOTAL_OBJECTS_PROCESSED = "TotalObjectsProcessed";
	public final static String VIX_INBOUND_OBJECTS_TOTAL_OBJECTS_REJECTED = "TotalObjectsRejected";
	public final static String VIX_INBOUND_OBJECTS_TOTAL_OBJECTS_PASSED_TO_LEGACY_DGW = "TotalObjectsPassedToLegacyGW";
	public final static String VIX_INBOUND_OBJECTS_TOTAL_OBJECTS_PASSED_TO_HDIG_DATA_STRUCTURE = "TotalObjectsPassedToHDIGDataStructure";
	public final static String VIX_INBOUND_OBJECTS_TOTAL_DUPLICATE_OBJECTS = "TotalDuplicateObjects";

	public final static String VIX_INBOUND_MODALITY_DEVICE_MANUFACTURER = "Manufacturer";
	public final static String VIX_INBOUND_MODALITY_DEVICE_MODEL = "Model";
	public final static String VIX_INBOUND_MODALITY_DEVICE_TOTAL_OBJECTS_PROCESSED = "TotalObjectsProcessed";
	public final static String VIX_INBOUND_MODALITY_DEVICE_TOTAL_OBJECTS_REJECTED = "TotalObjectRejected";
	public final static String VIX_INBOUND_MODALTIY_DEVICE_TOTAL_DUPLICATE_INSTANCE_UIDS = "TotalDuplicateInstanceUIDs";
	public final static String VIX_INBOUND_MODALITY_DEVICE_TOTAL_OBJECTS_WITH_IOD_VIOLATIONS = "TotalObjectsWithIODViolations";
	public final static String VIX_INBOUND_MODALITY_DEVICE_TOTAL_DUPLICATE_OBJECTS = "TotalDuplicateObjects";
	
	public final static String VIX_INBOUND_SOP_CLASS = "SOPClass";
	public final static String VIX_INBOUND_SOP_CLASS_TOTAL_OBJECTS_PROCESSED = "TotalObjectsProcessed";
	public final static String VIX_INBOUND_SOP_CLASS_TOTAL_OBJECTS_REJECTED = "TotalObjectsRejected";
	
	public final static String VIX_OUTBOUND_OBJECTS_AETITLE = "AETitle";
	public final static String VIX_OUTBOUND_OBJECTS_TOTAL_OBJECTS_TRANSMITTED_TO_SCP = "TotalObjectsTranmittedToSCP";
	public final static String VIX_OUTBOUND_OBJECTS_TOTAL_OBJECTS_REJECTED_BY_SCP = "TotalObjectsRejectedBySCP";
	
	public final static String VIX_OUTBOUND_ASSOCIATION_AETITLE = "AETitle";
	public final static String VIX_OUTBOUND_ASSOCIATION_TOTAL_ACCEPTED_ASSOCIATIONS = "TotalAcceptedAssociations";
	public final static String VIX_OUTBOUND_ASSOCIATION_TOTAL_REJECTED_ASSOCIATIONS = "TotalRejectedAssociations";
		
	public final static String VIX_SITE_SERVICE_URL = "SiteServiceUrl";
	public final static String VIX_SITE_SERVICE_LAST_UPDATED = "SiteServiceLastUpdate";
	public final static String VIX_SITE_SERVICE_SOURCE_VERSION = "SiteServiceSourceVersion";
	
	public final static String VIX_SERVER_OPERATING_SYSTEM_NAME = "OSName";
	public final static String VIX_SERVER_OPERATING_SYSTEM_ARCHITECTURE = "OSArchitecture";
	public final static String VIX_SERVER_OPERATING_SYSTEM_VERSION = "OSVersion";
	public final static String VIX_SERVER_AVAILABLE_PROCESSORS = "SystemAvailableProcessors";
	public final static String VIX_SERVER_COMMITTED_VIRTUAL_MEMORY_SIZE = "CommittedVirtualMemorySize";
	public final static String VIX_SERVER_FREE_PHYSICAL_MEMORY_SIZE = "FreePhysicalMemorySize";
	public final static String VIX_SERVER_TOTAL_PHYSICAL_MEMORY_SIZE = "TotalPhysicalMemorySize";
	public final static String VIX_SERVER_FREE_SWAP_SPACE_SIZE = "FreeSwapSpaceSize";
	public final static String VIX_SERVER_TOTAL_SWAP_SPACE_SIZE = "TotalSwapSpaceSize";
	public final static String VIX_SERVER_THREAD_COUNT = "JavaThreadCount";

	public final static String VIX_SERVER_HEALTH_CATALINA_GLOBAL_REQUEST_PROCESSING_BYTES_RECEIVED = "CatalinaGlobalRequestProcessingBytesReceived";
	public final static String VIX_SERVER_HEALTH_CATALINA_GLOBAL_REQUEST_PROCESSING_BYTES_SENT = "CatalinaGlobalRequestProcessingBytesSent";
	public final static String VIX_SERVER_HEALTH_CATALINA_GLOBAL_REQUEST_PROCESSING_REQUEST_COUNT = "CatalinaGlobalRequestProcessingRequestCount";
	public final static String VIX_SERVER_HEALTH_CATALINA_GLOBAL_REQUEST_PROCESSING_PROCESSING_TIME = "CatalinaGlobalRequestProcessingProcessingTime";
	
	public final static String VIX_SERVER_TOTAL_DOD_PATIENT_ARTIFACT_REQUESTS = "TotalDoDPatientArtifactRequests";
	public final static String VIX_SERVER_TOTAL_DOD_EXAM_REQUESTS = "TotalDoDExamRequests";
	public final static String VIX_SERVER_NON_CORRELATED_DOD_PATIENT_ARTIFACT_REQUESTS = "NonCorrelatedDoDPatientArtifactRequests";
	public final static String VIX_SERVER_NON_CORRELATED_DOD_EXAM_REQUESTS = "NonCorrelatedDoDExamRequests";
	
	public final static String VIX_SERVER_HEALTH_ROI_PERIODIC_PROCESSING_ENABLED = "ROIPeriodicProcessingEnabled";
	public final static String VIX_SERVER_HEALTH_ROI_PERIODIC_PROCESSING_ERROR = "ROIPeriodicProcessingError";
	public final static String VIX_SERVER_HEALTH_ROI_COMPLETED_ITEM_PURGE_ENABLED = "ROICompletedItemPurgeEnabled";
	public final static String VIX_SERVER_HEALTH_ROI_DISCLOSURE_REQUESTS = "ROIDisclosureRequests";
	public final static String VIX_SERVER_HEALTH_ROI_DISCLOSURE_PROCESSING_ERRORS = "ROIDisclosureProcessingErrors";
	public final static String VIX_SERVER_HEALTH_ROI_DISCLOSURES_COMPLETED = "ROIDisclosuresCompleted";
	public final static String VIX_SERVER_HEALTH_ROI_STUDIES_SENT_TO_EXPORT_QUEUE = "ROIStudiesSentToExportQueue";
	public final static String VIX_SERVER_HEALTH_ROI_DISCLOSURES_CANCELLED = "ROIDisclosuresCancelled";
	public final static String VIX_SERVER_HEALTH_ROI_PROCESS_WORK_ITEMS_IMMEDIATELY = "ROIProcessWorkItemsImmediately";
	
	public final static String VIX_SERVER_HEALTH_IMAGE_GEAR_BURN_ANNOTATION_FAILURES = "ImageGearBurnAnnotationFailures";
	public final static String VIX_SERVER_HEALTH_IMAGE_GEAR_BURN_ANNOTATION_REQUESTS = "ImageGearBurnAnnotationRequests";
	public final static String VIX_SERVER_HEALTH_IMAGE_GEAR_BURN_ANNOTATION_SUCCESS = "ImageGearBurnAnnotationSuccess";
	public final static String VIX_SERVER_HEALTH_IMAGE_GEAR_DISCLOSURE_FAILURES = "ImageGearDisclosureFailures";
	public final static String VIX_SERVER_HEALTH_IMAGE_GEAR_DISCLOSURE_REQUESTS = "ImageGearDisclosureRequests";
	public final static String VIX_SERVER_HEALTH_IMAGE_GEAR_DISCLOSURE_SUCCESS = "ImageGearDisclosureSuccess";
	
}
