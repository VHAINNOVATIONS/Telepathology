/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 2, 2009
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

import gov.va.med.imaging.health.configuration.EnvironmentVariableHealthConfigurationEntry;
import gov.va.med.imaging.health.configuration.JMXHealthConfigurationEntry;
import gov.va.med.imaging.health.configuration.VixHealthConfigurationLoader;
import gov.va.med.imaging.health.parser.VixServerHealthXmlParser;
import gov.va.med.imaging.monitorederrors.MonitoredError;
import gov.va.med.imaging.monitorederrors.MonitoredErrors;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

/**
 * 
 * VixServerHealth reports the health of the VIX by examining and reporting the status of the VIX.
 * @author vhaiswwerfej
 *
 */
public class VixServerHealth 
{	
	private final Map<String, String> vixServerHealthProperties = 
		new HashMap<String, String>();
	
	private final VixHealthConfigurationLoader vixHealthLoader;
	
	private VixServerHealth()
	{
		super();
		vixHealthLoader = VixHealthConfigurationLoader.getVixHealthConfigurationLoader();
	}
	
	/**
	 * Get an instance of the VIX Server health.
	 * @return
	 */
	public static VixServerHealth getVixServerHealth(VixServerHealthSource [] vixServerHealthSources)
	{
		VixServerHealth vixServerHealth = new VixServerHealth();
		vixServerHealth.determineHealth(vixServerHealthSources);
		return vixServerHealth;
	}
	
	/**
	 * Convert the inputStream which represents the VIX Server health in XML into a VixServerHealth object
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public static VixServerHealth getVixServerHealthFromXml(InputStream inputStream)
	throws IOException
	{
		VixServerHealthXmlParser parser = new VixServerHealthXmlParser();
		VixServerHealth health = new VixServerHealth();
		health.vixServerHealthProperties.putAll(parser.parse(inputStream));		
		return health;
	}
	
	/**
	 * Convert the VIX server health values to simple XML.
	 * @return
	 */
	public String toXml()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<VixServerHealth>");
		for(String key : vixServerHealthProperties.keySet())
		{
			sb.append("<Property name=\"" + key + "\" value=\"" + vixServerHealthProperties.get(key) + "\" />");
		}
		sb.append("</VixServerHealth>");
		return sb.toString();
	}	
	
	/**
	 * @return the vixServerHealthProperties
	 */
	public Map<String, String> getVixServerHealthProperties() {
		return vixServerHealthProperties;
	}

	/**
	 * Add an external VIX server health property to the health record.
	 * This is necessary if the value cannot be determined internally to the VixServerHealth class
	 * but must be done elsewhere (such as in the Router).
	 * 
	 * @param propertyName
	 * @param value
	 */
	public void addVixServerHealthProperty(String propertyName, String value)
	{
		addProperty(propertyName, value);
	}		
	
	private final static Logger logger = Logger.getLogger(VixServerHealth.class);
	
	private void determineHealth(VixServerHealthSource [] vixServerHealthSources)
	{						
		
		if(isSourceIncluded(vixServerHealthSources, VixServerHealthSource.jmx))
		{
			logger.info("Loading JMX health values");
			insertVIXJMXValues();
			logger.info("Loaded JMX health values");
		}
		if(isSourceIncluded(vixServerHealthSources, VixServerHealthSource.environment_variables))
		{
			logger.info("Loading Environment variable values");
			insertEnvironmentVariableValues();
			logger.info("Loaded Environment variable values");
		}
		if(isSourceIncluded(vixServerHealthSources, VixServerHealthSource.custom_transactionLog))
		{
			logger.info("Loading custom transaction log health values.");
			insertTransactionLogValues();
			logger.info("Loaded custom transaction log health values");
		}
		if(isSourceIncluded(vixServerHealthSources, VixServerHealthSource.custom_vixCache))
		{
			logger.info("Loading custom VIX Cache health values.");
			insertVixCacheValues();
			logger.info("Loaded custom VIX Cache health values");
		}
		if(isSourceIncluded(vixServerHealthSources, VixServerHealthSource.custom_tomcatLogs))
		{
			logger.info("Loading custom Tomcat Log health values.");
			insertTomcatLogsValues();
			logger.info("Loaded custom Tomcat Log health values");
		}
		if(isSourceIncluded(vixServerHealthSources, VixServerHealthSource.monitoredError))
		{
			logger.info("Loading monitored errors health values.");
			insertMonitoredErrorsValues();
			logger.info("Loaded monitored errors health values");
		}
	}
	
	private boolean isSourceIncluded(VixServerHealthSource [] vixServerHealthSources, VixServerHealthSource source)
	{
		if(vixServerHealthSources == null)
			return false;
		for(VixServerHealthSource s : vixServerHealthSources)
		{
			if(s == source)
				return true;
		}
		return false;
	}
	
	private void insertTransactionLogValues()
	{
		try
		{
			String vixConfigDir = System.getenv("vixconfig");
			File transactionLogsDir = new File(vixConfigDir + File.separatorChar + "logs");
			//Added If statement to avoid problems with HDIG.
			if(transactionLogsDir.exists()){
				long transactionLogsDirSize = FileUtils.sizeOfDirectory(transactionLogsDir);
				addProperty(VixServerHealthProperties.VIX_SERVER_HEALTH_VIX_TRANSACTIONLOGS_DIR_SIZE, transactionLogsDirSize + "");		
				addProperty(VixServerHealthProperties.VIX_SERVER_HEALTH_VIX_TRANSACTIONLOGS_DIR, transactionLogsDir.getAbsolutePath());
				addProperty(VixServerHealthProperties.VIX_SERVER_HEALTH_VIX_TRANSACTIONLOGS_DIR_ROOT_DRIVE_AVAILABLE, transactionLogsDir.getUsableSpace() + "");			
				addProperty(VixServerHealthProperties.VIX_SERVER_HEALTH_VIX_TRANSACTIONLOGS_DIR_ROOT_DRIVE_TOTAL, transactionLogsDir.getTotalSpace() + "");
			}
		}
		catch(Exception ex)
		{
			logger.error("Error loading transaction log values, " + ex.getMessage());
		}
	}
	
	private void insertVixCacheValues()
	{
		try
		{
			String vixCacheDir =  System.getenv("vixcache");
			
			File vCache = new File(vixCacheDir);
			addProperty(VixServerHealthProperties.VIX_SERVER_HEALTH_VIX_CACHE_DIR_ROOT_DRIVE_AVAILABLE, vCache.getUsableSpace() + "");			
			addProperty(VixServerHealthProperties.VIX_SERVER_HEALTH_VIX_CACHE_DIR_ROOT_DRIVE_TOTAL, vCache.getTotalSpace() + "");
			
			long tomcatCacheDirSize = FileUtils.sizeOfDirectory(new File(vixCacheDir));
			addProperty(VixServerHealthProperties.VIX_SERVER_HEALTH_VIX_CACHE_DIR_SIZE, tomcatCacheDirSize + "");
		}
		catch(Exception ex)
		{
			logger.error("Error loading VIX cache values, " + ex.getMessage());
		}
	}
	
	private void insertTomcatLogsValues()
	{
		try
		{
			String tomcatDirectory = System.getenv("CATALINA_HOME");
			if((tomcatDirectory != null) && (tomcatDirectory.length() > 0))
			{
				addProperty(VixServerHealthProperties.VIX_SERVER_HEALTH_VIX_TOMCAT_DIR, tomcatDirectory);
				
				File tomcatDrive = new File(tomcatDirectory);		
				addProperty(VixServerHealthProperties.VIX_SERVER_HEALTH_VIX_TOMCAT_LOGS_DIR_ROOT_DRIVE_AVAILABLE, tomcatDrive.getUsableSpace() + "");
				
				addProperty(VixServerHealthProperties.VIX_SERVER_HEALTH_VIX_TOMCAT_LOGS_DIR_ROOT_DRIVE_TOTAL, tomcatDrive.getTotalSpace() + "");
				
				String tomcatLogsDir = tomcatDirectory + "\\logs";
				addProperty(VixServerHealthProperties.VIX_SERVER_HEALTH_VIX_TOMCAT_LOGS_DIR, tomcatLogsDir);
				
				long tomcatLogsDirSize = FileUtils.sizeOfDirectory(new File(tomcatLogsDir));
				addProperty(VixServerHealthProperties.VIX_SERVER_HEALTH_VIX_TOMCAT_LOGS_DIR_SIZE, tomcatLogsDirSize + "");
			}
			else
			{
				logger.warn("Cannot find environment variable 'CATALINA_HOME', cannot include Tomcat log directory information.");
			}			
		}
		catch(Exception eX)
		{
			logger.error("Error loading Tomcat logs values, " + eX.getMessage());
		}
	}
	
	private void insertMonitoredErrorsValues()
	{
		try
		{
			List<MonitoredError> monitoredErrors = MonitoredErrors.getMonitoredErrors();
			for(MonitoredError monitoredError : monitoredErrors)
			{
				String value = monitoredError.getCount() + "_";
				if(monitoredError.getLastOccurrence() != null)
					value += monitoredError.getLastOccurrence().getTime();				
				addProperty("MonitoredError_" + encodeString(monitoredError.getErrorMessageContains()),
						value);
			}
		}
		catch(Exception ex)
		{
			logger.warn("Error loading monitored errors, " + ex.getMessage());
		}
	}	
	
	//private final static String utf8 = "UTF-8"; 
	private String encodeString(String value)
	//throws UnsupportedEncodingException
	{
		return StringEscapeUtils.escapeXml(value);
	}
	
	private void addProperty(String key, String value)
	{
		vixServerHealthProperties.put(key, value);
	}
	
	private void insertEnvironmentVariableValues()
	{
		List<EnvironmentVariableHealthConfigurationEntry> environmentVariableEntries = 
			vixHealthLoader.getVixHealthConfiguration().getEnvironmentVariableHealthConfiguration();
		for(EnvironmentVariableHealthConfigurationEntry environmentVariableEntry : environmentVariableEntries)
		{
			addEnvironmentVariableValue(environmentVariableEntry);
		}
		
		String hostname = "UNKNOWN";
		try{
			hostname = InetAddress.getLocalHost().getHostName();
		}
		catch(UnknownHostException uhX){
			logger.error("Error retrieving Hostname. "+uhX.getMessage());
		}
		//NOTE - If VHAMASTER appears, this is because the hostname is set as a property in VixHealthConfig.xml file.
		addProperty(VixServerHealthProperties.VIX_SERVER_HEALTH_VIX_HOSTNAME, hostname + "");
	}
	
	private void addEnvironmentVariableValue(EnvironmentVariableHealthConfigurationEntry entry)
	{
		String value = System.getenv(entry.getEnvironmentVariableName());
		addProperty(entry.getVixHealthKey(), value);
	}
	
	private void insertVIXJMXValues()
	{
		MBeanServer server = java.lang.management.ManagementFactory.getPlatformMBeanServer();
		List<JMXHealthConfigurationEntry> jmxEntries = 
			vixHealthLoader.getVixHealthConfiguration().getJmxHealthConfiguration();
		for(JMXHealthConfigurationEntry jmxEntry : jmxEntries)
		{
			addJmxAttributeValue(jmxEntry, server);
		}
		
		/*
		if(vixHealthLoader.getVixHealthConfiguration().getListThreadPoolData() != null && 
				vixHealthLoader.getVixHealthConfiguration().getListThreadPoolData())		
		{
			addThreadPoolData(server);	
		}		
		
		if(vixHealthLoader.getVixHealthConfiguration().getListThreadProcessingTime() != null && 
				vixHealthLoader.getVixHealthConfiguration().getListThreadProcessingTime())	
		{
			addThreadProcessingTimes(server);
		}*/
	}
	
	/*
	private void addThreadPoolData(MBeanServer server)
	{
		logger.info("Loading thread pool data");
		Hashtable<String, String> threadPoolKeys = new Hashtable<String, String>();
		threadPoolKeys.put("type", "ThreadPool");
		threadPoolKeys.put("name", "*"); 
		ObjectName objectName = null;
		try
		{
			objectName = new ObjectName("Catalina", threadPoolKeys);
		}
		catch(MalformedObjectNameException monX)
		{
			logger.error("Error creating objectName for 'Catalina'", monX);
		}
		if(objectName != null)
		{		
			Set<ObjectInstance> instances = server.queryMBeans(objectName, objectName);
			logger.debug("Found '" + instances.size() + "' MBean thread pool instances.");
			for(ObjectInstance instance : instances)
			{
				String name = instance.getObjectName().getKeyProperty("name");				
				
				String value = getJmxAttribute(server, instance.getObjectName(), "currentThreadCount");
				if(value != null)
				{
					addProperty("CatalinaThreadPoolThreadCount_" + name, value);
				}
				value = getJmxAttribute(server, instance.getObjectName(), "currentThreadsBusy");
				if(value != null)
				{
					addProperty("CatalinaThreadPoolThreadsBusy_" + name, value);
				}
				value = getJmxAttribute(server, instance.getObjectName(), "maxThreads");
				if(value != null)
				{
					addProperty("CatalinaThreadPoolMaxThreads_" + name, value);
				}
			}
		}
	}
	
	private void addThreadProcessingTimes(MBeanServer server)
	{
		logger.info("Loading thread processing time");
		Hashtable<String, String> requestProcessorKeys = new Hashtable<String, String>();
		requestProcessorKeys.put("type", "RequestProcessor");
		requestProcessorKeys.put("worker", "*");
		requestProcessorKeys.put("name", "*"); 
		ObjectName objectName = null;
		try
		{
			objectName = new ObjectName("Catalina", requestProcessorKeys);
		}
		catch(MalformedObjectNameException monX)
		{
			logger.error("Error creating objectName for 'Catalina'", monX);
		}
		if(objectName != null)
		{		
			Set<ObjectInstance> instances = server.queryMBeans(objectName, objectName);
			logger.debug("Found '" + instances.size() + "' MBean thread instances.");
			for(ObjectInstance instance : instances)
			{
				String name = instance.getObjectName().getKeyProperty("name");
				String worker = instance.getObjectName().getKeyProperty("worker");
				
				String value = getJmxAttribute(server, instance.getObjectName(), "requestProcessingTime");
				if(value != null)
				{
					addProperty("CatalinaRequestProcessingTime_" + worker + "_" + name, value);
				}
			}
		}
	}*/
	
	private String getJmxAttribute(MBeanServer server, ObjectName objectName, String jmxAttribute)
	{
		try
		{
			Object attr = server.getAttribute(objectName, jmxAttribute);
			if(attr != null)
			{
				return attr.toString();
			}	
			return null;
		}
		catch(InstanceNotFoundException infX)
		{
			logger.error("Instance not found Exception: " + infX.getMessage());
			return null;
		}
		catch(MBeanException mbX)
		{
			logger.error("MBean Exception: " + mbX.getMessage());
			return null;
		}
		catch(ReflectionException rX)
		{
			logger.error("Reflection Exception: " + rX.getMessage());
			return null;
		}
		catch(AttributeNotFoundException anfX)
		{
			logger.error("Attribute not found Exception: " + anfX.getMessage());
			return null;
		}
	}
	
	private void getJmxAttributes(MBeanServer server, JMXHealthConfigurationEntry entry)
	{
		ObjectName objectName = null;
		try
		{
			objectName = new ObjectName(entry.getMBeanDomain(), entry.getObjectNameKeys());
		}
		catch(MalformedObjectNameException monX)
		{
			logger.error("Error creating objectName for '" + entry.getMBeanDomain() + "'", monX);
		}
		if(objectName != null)
		{		
			Set<ObjectInstance> instances = server.queryMBeans(objectName, objectName);
			logger.debug("Found '" + instances.size() + "' MBean thread instances.");
			for(ObjectInstance instance : instances)
			{
				StringBuilder nameKey = new StringBuilder();
				
				for(String key : entry.getObjectNameKeys().keySet())
				{
					String name = instance.getObjectName().getKeyProperty(key);
					nameKey.append("_");
					nameKey.append(name);
				}
				String value = getJmxAttribute(server, instance.getObjectName(), entry.getJmxAttributeName());
				if(value != null)
				{
					addProperty(entry.getVixHealthKey() + nameKey.toString(), value);
				}
			}
		}
	}
	
	private String getJmxAttribute(String mbeanDomain, Hashtable<String, String> objectNameKeys, 
			String jmxAttributeName, MBeanServer server)
	{
		try
		{
			ObjectName objectName = 
				new ObjectName(mbeanDomain, objectNameKeys);
			Object attribute = server.getAttribute(
					objectName, jmxAttributeName);
			if(attribute == null)
				return "";
			return attribute.toString();
						
		}
		catch(MalformedObjectNameException monX)
		{
			logger.error("Malformed Object name Exception: " + monX.getMessage());
			return null;
		}
		catch(MBeanException mbX)
		{
			logger.error("MBean Exception: " + mbX.getMessage());
			return null;
		}
		catch(InstanceNotFoundException infX)
		{
			logger.error("Instance not found Exception: " + infX.getMessage());
			return null;
		}
		catch(AttributeNotFoundException anfX)
		{
			logger.error("Attribute not found Exception: " + anfX.getMessage());
			return null;
		}
		catch(ReflectionException rX)
		{
			logger.error("Reflection Exception: " + rX.getMessage());
			return null;
		}
	}
	
	private void addJmxAttributeValue(JMXHealthConfigurationEntry jmxConfig, MBeanServer server)
	{
		if(jmxConfig.isEnabled())
		{
			if(jmxConfig.isQueryList())
			{
				getJmxAttributes(server, jmxConfig);
			}
			else
			{
				String attributeValue = getJmxAttribute(jmxConfig.getMBeanDomain(), 
						jmxConfig.getObjectNameKeys(), jmxConfig.getJmxAttributeName(), server);
				if(attributeValue != null)
				{
					addProperty(jmxConfig.getVixHealthKey(), attributeValue);
				}
			}
		}
	}	
}

