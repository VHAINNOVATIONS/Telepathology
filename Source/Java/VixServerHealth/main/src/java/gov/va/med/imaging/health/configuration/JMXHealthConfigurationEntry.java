/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 4, 2009
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
package gov.va.med.imaging.health.configuration;

import gov.va.med.imaging.ImagingMBean;
import gov.va.med.imaging.health.VixServerHealthProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Entry that contains information about a value in JMX to be contained in the VIX Health report.
 * 
 * @author vhaiswwerfej
 *
 */
public class JMXHealthConfigurationEntry 
implements Serializable
{
	
	private static final long serialVersionUID = -8637072763622246821L;
	private final static Logger logger = Logger.getLogger(JMXHealthConfigurationEntry.class);
	
	private String mBeanDomain;
	private String vixHealthKey;
	private String jmxAttributeName;
	private Hashtable<String, String> objectNameKeys = 
		new Hashtable<String, String>();
	private boolean queryList;
	private boolean enabled;
	
	public JMXHealthConfigurationEntry()
	{
		mBeanDomain = "";
		vixHealthKey = "";
		jmxAttributeName = "";
		queryList = false;
		enabled = true;
	}
	
	public JMXHealthConfigurationEntry(String mBeanDomain, String vixHealthKey, 
			String jmxAttribute, Hashtable<String, String> objectNameKeys)
	{
		this(mBeanDomain, vixHealthKey, jmxAttribute, objectNameKeys, false, true);
	}
	
	public JMXHealthConfigurationEntry(String mBeanDomain, String vixHealthKey, 
			String jmxAttribute, Hashtable<String, String> objectNameKeys, boolean queryList, boolean enabled)
	{
		this.mBeanDomain = mBeanDomain;
		this.jmxAttributeName = jmxAttribute;
		this.objectNameKeys = objectNameKeys;
		this.vixHealthKey = vixHealthKey;
		this.queryList = queryList;
		this.enabled = enabled;
	}

	/**
	 * @return the mBeanDomain
	 */
	public String getMBeanDomain() {
		return mBeanDomain;
	}

	/**
	 * @param beanDomain the mBeanDomain to set
	 */
	public void setMBeanDomain(String beanDomain) {
		mBeanDomain = beanDomain;
	}

	/**
	 * @return the vixHealthKey
	 */
	public String getVixHealthKey() {
		return vixHealthKey;
	}

	/**
	 * @param vixHealthKey the vixHealthKey to set
	 */
	public void setVixHealthKey(String vixHealthKey) {
		this.vixHealthKey = vixHealthKey;
	}

	/**
	 * @return the jmxAttributeName
	 */
	public String getJmxAttributeName() {
		return jmxAttributeName;
	}

	/**
	 * @param jmxAttributeName the jmxAttributeName to set
	 */
	public void setJmxAttributeName(String jmxAttributeName) {
		this.jmxAttributeName = jmxAttributeName;
	}

	/**
	 * @return the objectNameKeys
	 */
	public Hashtable<String, String> getObjectNameKeys() {
		return objectNameKeys;
	}

	/**
	 * @param objectNameKeys the objectNameKeys to set
	 */
	public void setObjectNameKeys(Hashtable<String, String> objectNameKeys) {
		this.objectNameKeys = objectNameKeys;
	}
	
	/**
	 * Determines if the JMX health entry represents a query to get multiple entries from JMX
	 * 
	 * @return the queryList
	 */
	public boolean isQueryList() {
		return queryList;
	}

	/**
	 * @param queryList the queryList to set
	 */
	public void setQueryList(boolean queryList) {
		this.queryList = queryList;
	}
	
	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	
	
	private final static String VIX_REALM_MBEAN_DOMAIN_NAME = "gov.va.med.imaging.tomcat.vistarealm";
	private final static String JAVA_JVM_MBEAN_DOMAIN_NAME = "java.lang";
	private final static String CATALINA_MBEAN_DOMAIN_NAME= "Catalina";
	
	/**
	 * Get the default list of entries to load from JMX
	 * @param realmSiteNumber
	 * @return
	 */
	public static List<JMXHealthConfigurationEntry> getDefaultJmxHealthConfigurationEntries(String realmSiteNumber,
			boolean listThreadPoolData, boolean listThreadProcessingTime, boolean includeAwiv, boolean includeHdig)
	{
		List<JMXHealthConfigurationEntry> entries = new ArrayList<JMXHealthConfigurationEntry>();
		
		logger.info("Loading default JMX configuration");
		Hashtable<String, String> vixCacheJMXKeys = new Hashtable<String, String>();
		vixCacheJMXKeys.put("type", "Cache");
		vixCacheJMXKeys.put("name", "ImagingExchangeCache");
		
		entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
				VixServerHealthProperties.VIX_SERVER_HEALTH_VIX_JMX_CACHE_URI, "locationUri", vixCacheJMXKeys));
		entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
				VixServerHealthProperties.VIX_SERVER_HEALTH_VIX_JMX_CACHE_ENABLED, "enabled", vixCacheJMXKeys));
		entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
				VixServerHealthProperties.VIX_SERVER_HEALTH_VIX_JMX_CACHE_OPERATIONS_INITIATED, "getOperationInitiatedCount", vixCacheJMXKeys));
		entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
				VixServerHealthProperties.VIX_SERVER_HEALTH_VIX_JMX_CACHE_OPERATIONS_ERROR, "getOperationErrorCount", vixCacheJMXKeys));
		entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
				VixServerHealthProperties.VIX_SERVER_HEALTH_VIX_JMX_CACHE_OPERATIONS_INSTANCE_NOT_FOUND, "getOperationInstanceNotFoundCount", vixCacheJMXKeys));
		entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
				VixServerHealthProperties.VIX_SERVER_HEALTH_VIX_JMX_CACHE_OPERATIONS_SUCCESSFUL, "getOperationSuccessfulCount", vixCacheJMXKeys));
		
		if(realmSiteNumber != null)
		{
			logger.info("Realm '" + realmSiteNumber + "' specified, adding entries to include in health monitor results");
			Hashtable<String, String> vixRealmJMXKeys = new Hashtable<String, String>();
			vixRealmJMXKeys.put("realm", realmSiteNumber);
			
			entries.add(new JMXHealthConfigurationEntry(VIX_REALM_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_SERVER_HEALTH_REALM_JMX_VISTA_PORT, "VistaPort", vixRealmJMXKeys));
			entries.add(new JMXHealthConfigurationEntry(VIX_REALM_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_SERVER_HEALTH_REALM_JMX_VISTA_SERVER, "VistaServer", vixRealmJMXKeys));		
		}
		
		Hashtable<String, String> vixJvmKeys = new Hashtable<String, String>();
		vixJvmKeys.put("type", "Runtime");
		entries.add(new JMXHealthConfigurationEntry(JAVA_JVM_MBEAN_DOMAIN_NAME,
				VixServerHealthProperties.VIX_SERVER_HEALTH_VIX_JVM_UPTIME, "Uptime", vixJvmKeys));
		entries.add(new JMXHealthConfigurationEntry(JAVA_JVM_MBEAN_DOMAIN_NAME,
				VixServerHealthProperties.VIX_SERVER_HEALTH_VIX_JVM_STARTTIME, "StartTime", vixJvmKeys));
		
		/*
		 // JMW 10/25/2010 P104 - buffer pool no longer used, exclude from results
		Hashtable<String, String> vixBufferPoolKeys = new Hashtable<String, String>();
		vixBufferPoolKeys.put("type", "ByteBufferPoolManager");
		vixBufferPoolKeys.put("name", "Manager");
		entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME,
				VixServerHealthProperties.VIX_SERVER_HEALTH_BUFFER_POOL_MANAGER_POOL_COUNT, "BufferPoolCount", vixBufferPoolKeys));
		entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME,
				VixServerHealthProperties.VIX_SERVER_HEALTH_BUFFER_POOL_MANAGER_HIGHEST_OVERLOAD_REQUEST, "HighestOverloadedBufferSizeRequest", vixBufferPoolKeys));
		entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME,
				VixServerHealthProperties.VIX_SERVER_HEALTH_BUFFER_POOL_MANAGER_OVERLOAD_REQUEST_COUNT, "OverloadedBufferSizeRequests", vixBufferPoolKeys));
		entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME,
				VixServerHealthProperties.VIX_SERVER_HEALTH_BUFFER_POOL_MANAGER_TOTAL_REQUEST_COUNT, "TotalRequestBufferCount", vixBufferPoolKeys));
		entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME,
				VixServerHealthProperties.VIX_SERVER_HEALTH_BUFFER_POOL_MANAGER_TOTAL_BUFER_SIZE_USE, "TotalBufferSizeUse", vixBufferPoolKeys));
				*/

		Hashtable<String, String> requestProcessorKeys = new Hashtable<String, String>();
		requestProcessorKeys.put("type", "RequestProcessor");
		requestProcessorKeys.put("worker", "*");
		requestProcessorKeys.put("name", "*"); 
		entries.add(new JMXHealthConfigurationEntry(CATALINA_MBEAN_DOMAIN_NAME,
				VixServerHealthProperties.VIX_SERVER_HEALTH_CATALINA_REQUEST_PROCESSING_TIME, "requestProcessingTime", 
				requestProcessorKeys, true, listThreadProcessingTime));
		
		Hashtable<String, String> threadPoolKeys = new Hashtable<String, String>();
		threadPoolKeys.put("type", "ThreadPool");
		threadPoolKeys.put("name", "*"); 
		entries.add(new JMXHealthConfigurationEntry(CATALINA_MBEAN_DOMAIN_NAME,
				VixServerHealthProperties.VIX_SERVER_HEALTH_CATALINA_THREAD_POOL_COUNT, "currentThreadCount",
				threadPoolKeys, true, true));
		entries.add(new JMXHealthConfigurationEntry(CATALINA_MBEAN_DOMAIN_NAME,
				VixServerHealthProperties.VIX_SERVER_HEALTH_CATALINA_THREAD_POOL_MAX_THREADS, "maxThreads",
				threadPoolKeys, true, true));
		entries.add(new JMXHealthConfigurationEntry(CATALINA_MBEAN_DOMAIN_NAME,
				VixServerHealthProperties.VIX_SERVER_HEALTH_CATALINA_THREAD_POOL_THREADS_BUSY, "currentThreadsBusy",
				threadPoolKeys, true, listThreadPoolData));
		
		
		// always including Exchange to support v1 on site VIX
		Hashtable<String, String> exchangeServletKeys = new Hashtable<String, String>();
		exchangeServletKeys.put("j2eeType", "Servlet");
		exchangeServletKeys.put("name", "*");
		exchangeServletKeys.put("WebModule", "*ImagingExchangeWebApp");
		exchangeServletKeys.put("J2EEApplication", "none");
		exchangeServletKeys.put("J2EEServer", "none");
		
		entries.add(new JMXHealthConfigurationEntry(CATALINA_MBEAN_DOMAIN_NAME,
				VixServerHealthProperties.VIX_SERVER_HEALTH_CATALINA_EXCHANGE_REQUEST_COUNT, "requestCount",
				exchangeServletKeys, true, true));
		
		Hashtable<String, String> federationServletKeys = new Hashtable<String, String>();
		federationServletKeys.put("j2eeType", "Servlet");
		federationServletKeys.put("name", "*");
		federationServletKeys.put("WebModule", "*FederationWebApp");
		federationServletKeys.put("J2EEApplication", "none");
		federationServletKeys.put("J2EEServer", "none");
		
		entries.add(new JMXHealthConfigurationEntry(CATALINA_MBEAN_DOMAIN_NAME,
				VixServerHealthProperties.VIX_SERVER_HEALTH_CATALINA_FEDERATION_REQUEST_COUNT, "requestCount",
				federationServletKeys, true, true));
					
		
		if(includeHdig)
		{

			Hashtable<String, String> vixActivityKeys = new Hashtable<String, String>();
			vixActivityKeys.put( "type", "DicomServiceStats" );
			vixActivityKeys.put( "name", "DICOMActivity");
			//vixActivityKeys.put("number", "*");
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_SERVICE_ACCOUNT_VALID, "ServiceAccountValid", 
					vixActivityKeys, true, true));
			
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_TOTAL_RAD_OBJECTS_TO_CORRECT, "TotalRADObjectsToCorrectForHDIG", 
					vixActivityKeys, true, true));

			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_TOTAL_CON_OBJECTS_TO_CORRECT, "TotalCONObjectsToCorrectForHDIG", 
					vixActivityKeys, true, true));

			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_TOTAL_DICOM_CORRECT_WORK_ITEMS, "DicomCorrectWorkItemCount", 
					vixActivityKeys, true, true));
			
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_TOTAL_NETWORK_IMPORT_WORK_ITEMS, "NetworkImportWorkItemCount", 
					vixActivityKeys, true, true));

			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_TOTAL_STAGED_MEDIA_WORK_ITEMS, "StagedMediaWorkItemCount", 
					vixActivityKeys, true, true));

			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_TOTAL_FAILED_WORK_ITEMS, "FailedWorkItemCount", 
					vixActivityKeys, true, true));
			
			Hashtable<String, String> dcfLicenseKeys = new Hashtable<String, String>();
			dcfLicenseKeys.put( "type", "DCFLicenseInfo" );
			dcfLicenseKeys.put( "name", "License");
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_DCF_VERSION, "DCFVersion", 
					dcfLicenseKeys));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_DCF_LICENSE_EXPIRATION_DATE, "DCFLicenseExpirationDate", 
					dcfLicenseKeys));
			
			
			Hashtable<String, String> listeningPortKeys = new Hashtable<String, String>();
			listeningPortKeys.put( "type", "DicomServiceStats" );
			listeningPortKeys.put( "name", "Port");
			listeningPortKeys.put("number", "*");
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_LISTENING_PORT, "PortName", 
					listeningPortKeys, true, true));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_LISTENING_PORT_CURRENT_STATUS, "CurrentStatus", 
					listeningPortKeys, true, true));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_LISTENING_PORT_LIVE_SINCE, "ListeningSince", 
					listeningPortKeys, true, true));
	
			Hashtable<String, String> inboundAssociationKeys = new Hashtable<String, String>();
			inboundAssociationKeys.put( "type", "DicomServiceStats" );
			inboundAssociationKeys.put( "name", "InboundAssociations");
			inboundAssociationKeys.put("number", "*");
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_INBOUND_ASSOCIATION_AETITLE, "AeTitle", 
					inboundAssociationKeys, true, true));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_INBOUND_ASSOCIATION_IP_ADDRESS, "IpAddress", 
					inboundAssociationKeys, true, true));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_INBOUND_ASSOCIATION_LAST_ACCESS_TIMESTAMP, "TimeStampOfLastActivity", 
					inboundAssociationKeys, true, true));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_INBOUND_ASSOCIATION_TOTAL_ACCEPTED_ASSOCIATIONS, "TotalAcceptedAssociations", 
					inboundAssociationKeys, true, true));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_INBOUND_ASSOCIATION_TOTAL_REJECTED_ASSOCIATIONS, "TotalRejectedAssociations", 
					inboundAssociationKeys, true, true));

			Hashtable<String, String> dimseKeys = new Hashtable<String, String>();
			dimseKeys.put( "type", "DicomServiceStats" );
			dimseKeys.put( "name", "InboundDimseMessages");
			dimseKeys.put("number", "*");
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_INBOUND_DIMSE_AETITLE, "AeTitle", 
					dimseKeys, true, true));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_INBOUND_DIMSE_SERVICE_NAME, "DimseServiceName", 
					dimseKeys, true, true));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_INBOUND_DIMSE_TOTAL_PROCESSED_MESSAGES, "TotalProcessedDimseMessages", 
					dimseKeys, true, true));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_INBOUND_DIMSE_TOTAL_REJECTED_MESSAGES, "TotalRejectedDimseMessages", 
					dimseKeys, true, true));

			Hashtable<String, String> storageSCPKeys = new Hashtable<String, String>();
			storageSCPKeys.put( "type", "DicomServiceStats" );
			storageSCPKeys.put( "name", "InboundObjects");
			storageSCPKeys.put("number", "*");
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_INBOUND_OBJECTS_AETITLE, "AeTitle", 
					storageSCPKeys, true, true));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_INBOUND_OBJECTS_TOTAL_OBJECTS_PROCESSED, "TotalObjectsProcessed", 
					storageSCPKeys, true, true));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_INBOUND_OBJECTS_TOTAL_OBJECTS_REJECTED, "TotalObjectsRejected", 
					storageSCPKeys, true, true));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_INBOUND_OBJECTS_TOTAL_OBJECTS_PASSED_TO_LEGACY_DGW, "TotalObjectsPassedToLegacyGW", 
					storageSCPKeys, true, true));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_INBOUND_OBJECTS_TOTAL_OBJECTS_PASSED_TO_HDIG_DATA_STRUCTURE, "TotalObjectsPassedToHDIGDataStructure", 
					storageSCPKeys, true, true));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_INBOUND_OBJECTS_TOTAL_DUPLICATE_OBJECTS, "TotalDuplicateObjects", 
					storageSCPKeys, true, true));
	
			Hashtable<String, String> modalityDeviceKeys = new Hashtable<String, String>();
			modalityDeviceKeys.put( "type", "DicomServiceStats" );
			modalityDeviceKeys.put( "name", "InboundModalityDevices");
			modalityDeviceKeys.put("number", "*");
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_INBOUND_MODALITY_DEVICE_MANUFACTURER, "Manufacturer", 
					modalityDeviceKeys, true, true));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_INBOUND_MODALITY_DEVICE_MODEL, "Model", 
					modalityDeviceKeys, true, true));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_INBOUND_MODALITY_DEVICE_TOTAL_OBJECTS_PROCESSED, "TotalDicomObjectsProcessed", 
					modalityDeviceKeys, true, true));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_INBOUND_MODALITY_DEVICE_TOTAL_OBJECTS_REJECTED, "TotalDicomObjectsRejected", 
					modalityDeviceKeys, true, true));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_INBOUND_MODALITY_DEVICE_TOTAL_OBJECTS_WITH_IOD_VIOLATIONS, "TotalDicomObjectsWithIODViolations", 
					modalityDeviceKeys, true, true));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_INBOUND_MODALTIY_DEVICE_TOTAL_DUPLICATE_INSTANCE_UIDS, "TotalDuplicateInstanceUIDs", 
					modalityDeviceKeys, true, true));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_INBOUND_MODALITY_DEVICE_TOTAL_DUPLICATE_OBJECTS, "TotalDuplicateObjects", 
					modalityDeviceKeys, true, true));
			
			Hashtable<String, String> sopClassKeys = new Hashtable<String, String>();
			sopClassKeys.put( "type", "DicomServiceStats" );
			sopClassKeys.put( "name", "InboundSOPClasses");
			sopClassKeys.put("number", "*");
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_INBOUND_SOP_CLASS, "ModalityCode", 
					sopClassKeys, true, true));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_INBOUND_SOP_CLASS_TOTAL_OBJECTS_PROCESSED, "TotalDicomObjectsProcessed", 
					sopClassKeys, true, true));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_INBOUND_SOP_CLASS_TOTAL_OBJECTS_REJECTED, "TotalDicomObjectsRejected", 
					sopClassKeys, true, true));
			
			Hashtable<String, String> storageSCUKeys = new Hashtable<String, String>();
			storageSCUKeys.put( "type", "DicomServiceStats" );
			storageSCUKeys.put( "name", "OutboundObjects");
			storageSCUKeys.put("number", "*");
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_OUTBOUND_OBJECTS_AETITLE, "AeTitle", 
					storageSCUKeys, true, true));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_OUTBOUND_OBJECTS_TOTAL_OBJECTS_TRANSMITTED_TO_SCP, "TotalObjectsTransmittedToSCP", 
					storageSCUKeys, true, true));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_OUTBOUND_OBJECTS_TOTAL_OBJECTS_REJECTED_BY_SCP, "TotalObjectsRejectedBySCP", 
					storageSCUKeys, true, true));

			Hashtable<String, String> outboundAssociationKeys = new Hashtable<String, String>();
			outboundAssociationKeys.put( "type", "DicomServiceStats" );
			outboundAssociationKeys.put( "name", "OutboundAssociations");
			outboundAssociationKeys.put("number", "*");
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_OUTBOUND_ASSOCIATION_AETITLE, "AeTitle", 
					outboundAssociationKeys, true, true));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_OUTBOUND_ASSOCIATION_TOTAL_ACCEPTED_ASSOCIATIONS, "TotalAcceptedAssociations", 
					outboundAssociationKeys, true, true));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_OUTBOUND_ASSOCIATION_TOTAL_REJECTED_ASSOCIATIONS, "TotalRejectedAssociations", 
					outboundAssociationKeys, true, true));

		}
		else
		{
			// include site service if not HDIG
			
			// there are many possible entries for the site service (for all possible URL options)
			Hashtable<String, String> siteServiceServletKeys = new Hashtable<String, String>();
			siteServiceServletKeys.put("j2eeType", "Servlet");
			siteServiceServletKeys.put("name", "*");
			siteServiceServletKeys.put("WebModule", "*siteservicewebapp");
			siteServiceServletKeys.put("J2EEApplication", "none");
			siteServiceServletKeys.put("J2EEServer", "none");
			entries.add(new JMXHealthConfigurationEntry(CATALINA_MBEAN_DOMAIN_NAME,
					VixServerHealthProperties.VIX_SERVER_HEALTH_CATALINA_SITE_SERVICE_REQUEST_COUNT + "_1", "requestCount",
					siteServiceServletKeys, true, true));
			
			siteServiceServletKeys = new Hashtable<String, String>();
			siteServiceServletKeys.put("j2eeType", "Servlet");
			siteServiceServletKeys.put("name", "*");
			siteServiceServletKeys.put("WebModule", "*VistAWebSvcs"); // capital A
			siteServiceServletKeys.put("J2EEApplication", "none");
			siteServiceServletKeys.put("J2EEServer", "none");
			entries.add(new JMXHealthConfigurationEntry(CATALINA_MBEAN_DOMAIN_NAME,
					VixServerHealthProperties.VIX_SERVER_HEALTH_CATALINA_SITE_SERVICE_REQUEST_COUNT + "_2", "requestCount",
					siteServiceServletKeys, true, true));
			
			siteServiceServletKeys = new Hashtable<String, String>();
			siteServiceServletKeys.put("j2eeType", "Servlet");
			siteServiceServletKeys.put("name", "*");
			siteServiceServletKeys.put("WebModule", "*VistaWebSvcs"); // lowercase A
			siteServiceServletKeys.put("J2EEApplication", "none");
			siteServiceServletKeys.put("J2EEServer", "none");
			entries.add(new JMXHealthConfigurationEntry(CATALINA_MBEAN_DOMAIN_NAME,
					VixServerHealthProperties.VIX_SERVER_HEALTH_CATALINA_SITE_SERVICE_REQUEST_COUNT + "_3", "requestCount",
					siteServiceServletKeys, true, true));
			
			siteServiceServletKeys = new Hashtable<String, String>();
			siteServiceServletKeys.put("j2eeType", "Servlet");
			siteServiceServletKeys.put("name", "*");
			siteServiceServletKeys.put("WebModule", "*vistawebsvcs"); // all lowercase
			siteServiceServletKeys.put("J2EEApplication", "none");
			siteServiceServletKeys.put("J2EEServer", "none");
			entries.add(new JMXHealthConfigurationEntry(CATALINA_MBEAN_DOMAIN_NAME,
					VixServerHealthProperties.VIX_SERVER_HEALTH_CATALINA_SITE_SERVICE_REQUEST_COUNT + "_4", "requestCount",
					siteServiceServletKeys, true, true));

			//Moved code here to avoid problems with HDIG.
			Hashtable<String, String> transactionLogKeys = new Hashtable<String, String>();
			transactionLogKeys.put("type", "TransactionLogDatabase");
			transactionLogKeys.put("name", "Statistics");
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME,
				VixServerHealthProperties.VIX_SERVER_HEALTH_TRANSACTION_LOG_STATISTICS_TRANSACTIONS_WRITTEN, "TransactionsWritten", transactionLogKeys));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME,
				VixServerHealthProperties.VIX_SERVER_HEALTH_TRANSACTION_LOG_STATISTICS_TRANSACTIONS_QUERIED, "TransactionsQueried", transactionLogKeys));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME,
				VixServerHealthProperties.VIX_SERVER_HEALTH_TRANSACTION_LOG_STATISTICS_TRANSACTIONS_PURGED, "TransactionsPurged", transactionLogKeys));		
			// JMW 2/1/2012 - include error properties from the transaction log
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME,
					VixServerHealthProperties.VIX_SERVER_HEALTH_TRANSACTION_LOG_STATISTICS_TRANSACTION_WRITE_ERRORS, "TransactionWriteErrors", transactionLogKeys));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME,
					VixServerHealthProperties.VIX_SERVER_HEALTH_TRANSACTION_LOG_STATISTICS_TRANSACTION_READ_ERRORS, "TransactionReadErrors", transactionLogKeys));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_SERVER_HEALTH_TRANSACTION_LOG_STATISTICS_TRANSACTION_ERRORS, "TransactionErrors", transactionLogKeys));
		}
		
		if(includeAwiv)
		{
			// CVIX only values			
			Hashtable<String, String> awivServletKeys = new Hashtable<String, String>();
			awivServletKeys.put("j2eeType", "Servlet");
			awivServletKeys.put("name", "*");
			awivServletKeys.put("WebModule", "*AWIVWebApp");
			awivServletKeys.put("J2EEApplication", "none");
			awivServletKeys.put("J2EEServer", "none");
			
			entries.add(new JMXHealthConfigurationEntry(CATALINA_MBEAN_DOMAIN_NAME,
					VixServerHealthProperties.VIX_SERVER_HEALTH_CATALINA_AWIV_REQUEST_COUNT, "requestCount",
					awivServletKeys, true, true));
			
			Hashtable<String, String> xcaServletKeys = new Hashtable<String, String>();
			xcaServletKeys.put("j2eeType", "Servlet");
			xcaServletKeys.put("name", "*");
			xcaServletKeys.put("WebModule", "*XCARespondingGateway");
			xcaServletKeys.put("J2EEApplication", "none");
			xcaServletKeys.put("J2EEServer", "none");
			entries.add(new JMXHealthConfigurationEntry(CATALINA_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_SERVER_HEALTH_CATALINA_XCA_REQUEST_COUNT, "requestCount",
					xcaServletKeys, true, true));
			
			Hashtable<String, String> dodRequestKeys = new Hashtable<String, String>();
			dodRequestKeys.put("name", "Statistics");
			dodRequestKeys.put("type", "DODRequests");
			
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME,
					VixServerHealthProperties.VIX_SERVER_TOTAL_DOD_PATIENT_ARTIFACT_REQUESTS, 
					"TotalDodPatientArtifactRequests", dodRequestKeys));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME,
					VixServerHealthProperties.VIX_SERVER_TOTAL_DOD_EXAM_REQUESTS, 
					"TotalDodExamRequests", dodRequestKeys));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME,
					VixServerHealthProperties.VIX_SERVER_NON_CORRELATED_DOD_PATIENT_ARTIFACT_REQUESTS, 
					"NonCorrelatedDodPatientArtifactRequests", dodRequestKeys));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME,
					VixServerHealthProperties.VIX_SERVER_NON_CORRELATED_DOD_EXAM_REQUESTS, 
					"NonCorrelatedDodExamRequests", dodRequestKeys));
		}
		else
		{
			// Site VIX only values
			Hashtable<String, String> clinicalDisplayServletKeys = new Hashtable<String, String>();
			clinicalDisplayServletKeys.put("j2eeType", "Servlet");
			clinicalDisplayServletKeys.put("name", "*");
			clinicalDisplayServletKeys.put("WebModule", "*ClinicalDisplayWebApp");
			clinicalDisplayServletKeys.put("J2EEApplication", "none");
			clinicalDisplayServletKeys.put("J2EEServer", "none");		
			entries.add(new JMXHealthConfigurationEntry(CATALINA_MBEAN_DOMAIN_NAME,
					VixServerHealthProperties.VIX_SERVER_HEALTH_CATALINA_CLINICAL_DISPLAY_REQUEST_COUNT, "requestCount",
					clinicalDisplayServletKeys, true, true));
			
			Hashtable<String, String> vistaRadServletKeys = new Hashtable<String, String>();
			vistaRadServletKeys.put("j2eeType", "Servlet");
			vistaRadServletKeys.put("name", "*");
			vistaRadServletKeys.put("WebModule", "*VistaRadWebApp");
			vistaRadServletKeys.put("J2EEApplication", "none");
			vistaRadServletKeys.put("J2EEServer", "none");		
			entries.add(new JMXHealthConfigurationEntry(CATALINA_MBEAN_DOMAIN_NAME,
					VixServerHealthProperties.VIX_SERVER_HEALTH_CATALINA_VISTARAD_REQUEST_COUNT, "requestCount",
					vistaRadServletKeys, true, true));
			
			// JMW 4/20/2012 - properties for ROI processing
			Hashtable<String, String> roiPeriodicProcessingKeys = new Hashtable<String, String>();
			roiPeriodicProcessingKeys.put("type", "ROIProcessingCommands" );
			roiPeriodicProcessingKeys.put("name", "Statistics" );
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_SERVER_HEALTH_ROI_PERIODIC_PROCESSING_ENABLED,
					"RoiPeriodicProcessing", roiPeriodicProcessingKeys));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_SERVER_HEALTH_ROI_PERIODIC_PROCESSING_ERROR,
					"RoiPeriodicProcessingError", roiPeriodicProcessingKeys));			
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_SERVER_HEALTH_ROI_COMPLETED_ITEM_PURGE_ENABLED,
					"RoiCompletedItemsPurgeProcessing", roiPeriodicProcessingKeys));
			
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_SERVER_HEALTH_ROI_DISCLOSURE_REQUESTS,
					"RoiDisclosureRequests", roiPeriodicProcessingKeys));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_SERVER_HEALTH_ROI_DISCLOSURE_PROCESSING_ERRORS,
					"RoiDisclosureProcessingErrors", roiPeriodicProcessingKeys));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_SERVER_HEALTH_ROI_DISCLOSURES_COMPLETED,
					"RoiDisclosuresCompleted", roiPeriodicProcessingKeys));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_SERVER_HEALTH_ROI_STUDIES_SENT_TO_EXPORT_QUEUE,
					"RoiStudiesSentToExportQueue", roiPeriodicProcessingKeys));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_SERVER_HEALTH_ROI_DISCLOSURES_CANCELLED,
					"RoiDisclosuresCancelled", roiPeriodicProcessingKeys));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_SERVER_HEALTH_ROI_PROCESS_WORK_ITEMS_IMMEDIATELY,
					"RoiProcessWorkItemImmediately", roiPeriodicProcessingKeys));
			
			// JMW 2/1/2013 - additional properties related to ROI
			Hashtable<String, String> imageGearProcessingKeys = new Hashtable<String, String>();
			imageGearProcessingKeys.put("type", "ImageGearDataSourceProvider" );
			imageGearProcessingKeys.put("name", "Statistics" );
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_SERVER_HEALTH_IMAGE_GEAR_BURN_ANNOTATION_FAILURES,
					"BurnAnnotationFailures", imageGearProcessingKeys));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_SERVER_HEALTH_IMAGE_GEAR_BURN_ANNOTATION_REQUESTS,
					"BurnAnnotationRequests", imageGearProcessingKeys));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_SERVER_HEALTH_IMAGE_GEAR_BURN_ANNOTATION_SUCCESS,
					"BurnAnnotationSuccess", imageGearProcessingKeys));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_SERVER_HEALTH_IMAGE_GEAR_DISCLOSURE_FAILURES,
					"DisclosureWriteFailures", imageGearProcessingKeys));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_SERVER_HEALTH_IMAGE_GEAR_DISCLOSURE_REQUESTS,
					"DisclosureWriteRequests", imageGearProcessingKeys));
			entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, 
					VixServerHealthProperties.VIX_SERVER_HEALTH_IMAGE_GEAR_DISCLOSURE_SUCCESS,
					"DisclosureWriteSuccess", imageGearProcessingKeys));
			
		}
		
		Hashtable<String, String> siteServiceKeys = new Hashtable<String, String>();
		siteServiceKeys.put("name", "Configuration");
		siteServiceKeys.put("type", "SiteService");
		
		entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME,
				VixServerHealthProperties.VIX_SITE_SERVICE_URL, "SiteServiceUrl", siteServiceKeys));
		entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME,
				VixServerHealthProperties.VIX_SITE_SERVICE_LAST_UPDATED, "LastUpdate", siteServiceKeys));
		entries.add(new JMXHealthConfigurationEntry(ImagingMBean.VIX_MBEAN_DOMAIN_NAME,
				VixServerHealthProperties.VIX_SITE_SERVICE_SOURCE_VERSION, "SiteServiceDataSourceVersion", siteServiceKeys));
		
		
		
		// Operating System and Server Information (for VIX/CVIX/HDIG)
		Hashtable<String, String> operatingSystemKeys = new Hashtable<String, String>();
		operatingSystemKeys.put("type", "OperatingSystem");		
		entries.add(new JMXHealthConfigurationEntry(JAVA_JVM_MBEAN_DOMAIN_NAME,
				VixServerHealthProperties.VIX_SERVER_AVAILABLE_PROCESSORS, "AvailableProcessors", operatingSystemKeys));
		entries.add(new JMXHealthConfigurationEntry(JAVA_JVM_MBEAN_DOMAIN_NAME,
				VixServerHealthProperties.VIX_SERVER_OPERATING_SYSTEM_ARCHITECTURE, "Arch", operatingSystemKeys));
		entries.add(new JMXHealthConfigurationEntry(JAVA_JVM_MBEAN_DOMAIN_NAME,
				VixServerHealthProperties.VIX_SERVER_OPERATING_SYSTEM_NAME, "Name", operatingSystemKeys));
		entries.add(new JMXHealthConfigurationEntry(JAVA_JVM_MBEAN_DOMAIN_NAME,
				VixServerHealthProperties.VIX_SERVER_OPERATING_SYSTEM_VERSION, "Version", operatingSystemKeys));
		entries.add(new JMXHealthConfigurationEntry(JAVA_JVM_MBEAN_DOMAIN_NAME,
				VixServerHealthProperties.VIX_SERVER_COMMITTED_VIRTUAL_MEMORY_SIZE, 
				"CommittedVirtualMemorySize", operatingSystemKeys));		
		entries.add(new JMXHealthConfigurationEntry(JAVA_JVM_MBEAN_DOMAIN_NAME,
				VixServerHealthProperties.VIX_SERVER_FREE_PHYSICAL_MEMORY_SIZE, 
				"FreePhysicalMemorySize", operatingSystemKeys));
		entries.add(new JMXHealthConfigurationEntry(JAVA_JVM_MBEAN_DOMAIN_NAME,
				VixServerHealthProperties.VIX_SERVER_TOTAL_PHYSICAL_MEMORY_SIZE, 
				"TotalPhysicalMemorySize", operatingSystemKeys));
		entries.add(new JMXHealthConfigurationEntry(JAVA_JVM_MBEAN_DOMAIN_NAME,
				VixServerHealthProperties.VIX_SERVER_FREE_SWAP_SPACE_SIZE, 
				"FreeSwapSpaceSize", operatingSystemKeys));
		entries.add(new JMXHealthConfigurationEntry(JAVA_JVM_MBEAN_DOMAIN_NAME,
				VixServerHealthProperties.VIX_SERVER_TOTAL_SWAP_SPACE_SIZE, 
				"TotalSwapSpaceSize", operatingSystemKeys));
		
		Hashtable<String, String> threadingKeys = new Hashtable<String, String>();
		threadingKeys.put("type", "Threading");
		entries.add(new JMXHealthConfigurationEntry(JAVA_JVM_MBEAN_DOMAIN_NAME,
				VixServerHealthProperties.VIX_SERVER_THREAD_COUNT,
				"ThreadCount", threadingKeys));
		
		// bytes through each interface
		Hashtable<String, String> globalRequestProcessorKeys = new Hashtable<String, String>();
		globalRequestProcessorKeys.put("type", "GlobalRequestProcessor");
		globalRequestProcessorKeys.put("name", "*");
		entries.add(new JMXHealthConfigurationEntry(CATALINA_MBEAN_DOMAIN_NAME,
				VixServerHealthProperties.VIX_SERVER_HEALTH_CATALINA_GLOBAL_REQUEST_PROCESSING_BYTES_RECEIVED, 
				"bytesReceived", globalRequestProcessorKeys, true, true));
		entries.add(new JMXHealthConfigurationEntry(CATALINA_MBEAN_DOMAIN_NAME,
				VixServerHealthProperties.VIX_SERVER_HEALTH_CATALINA_GLOBAL_REQUEST_PROCESSING_BYTES_SENT, 
				"bytesSent", globalRequestProcessorKeys, true, true));
		entries.add(new JMXHealthConfigurationEntry(CATALINA_MBEAN_DOMAIN_NAME,
				VixServerHealthProperties.VIX_SERVER_HEALTH_CATALINA_GLOBAL_REQUEST_PROCESSING_PROCESSING_TIME, 
				"processingTime", globalRequestProcessorKeys, true, true));
		entries.add(new JMXHealthConfigurationEntry(CATALINA_MBEAN_DOMAIN_NAME,
				VixServerHealthProperties.VIX_SERVER_HEALTH_CATALINA_GLOBAL_REQUEST_PROCESSING_REQUEST_COUNT, 
				"requestCount", globalRequestProcessorKeys, true, true));
		
		
		return entries;
	}
}
