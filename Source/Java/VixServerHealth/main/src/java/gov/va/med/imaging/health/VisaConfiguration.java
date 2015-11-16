/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 6, 2012
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
package gov.va.med.imaging.health;

import gov.va.med.imaging.NullOutputStream;
import gov.va.med.imaging.channels.ByteStreamPump;
import gov.va.med.imaging.channels.ChecksumValue;
import gov.va.med.imaging.channels.AbstractBytePump.TRANSFER_TYPE;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

import org.apache.log4j.Logger;

/**
 * @author VHAISWWERFEJ
 *
 */
public class VisaConfiguration
{
	private final List<VisaConfigurationProperty> visaConfigurationProperties =
		new ArrayList<VisaConfigurationProperty>();
	private final static Logger logger = Logger.getLogger(VisaConfiguration.class);
	
	private VisaConfiguration()
	{
		super();
	}
	
	public static VisaConfiguration getVisaConfiguration(VisaConfigurationType visaConfigurationType, boolean calculateChecksums)
	{
		VisaConfiguration visaConfiguration = new VisaConfiguration();
		visaConfiguration.determineConfiguration(visaConfigurationType, 
				calculateChecksums);
		return visaConfiguration;
	}
	
	public List<VisaConfigurationProperty> getVisaConfigurationProperties()
	{
		return visaConfigurationProperties;
	}

	public String toXml()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<VisaConfiguration>");
		
		for(VisaConfigurationProperty vp : visaConfigurationProperties)
		{
			sb.append("<Property name=\"" + vp.getName() + "\" modified=\"" + vp.getModifiedFormatted() + "\" size=\"" + vp.getSize() + "\" checksum=\"" + (vp.getChecksum() == null ? "" : vp.getChecksum()) + "\" />");
		}
		
		sb.append("</VisaConfiguration>");
		return sb.toString();
	}
	
	private void determineConfiguration(VisaConfigurationType visaConfigurationType, boolean calculateChecksums)
	{
		File libDir = null;
		switch(visaConfigurationType)
		{
			case jreLibExt:
				libDir = getJreLibExtDirectory();
				break;
			default:
				libDir = getLibDirectory();
				break;
		}
		if(libDir != null)
		{
			if(libDir.exists() && libDir.isDirectory() && libDir.canRead())
			{			
				File [] fileList = libDir.listFiles();
				for(File file : fileList)
				{
					String checksum = null;
					if(calculateChecksums)
						checksum = calculateChecksum(file);
					visaConfigurationProperties.add(
							new VisaConfigurationProperty(file.getName(), new Date(file.lastModified()), 
									file.length(), checksum));
				}
			
			}
			else
			{
				logger.error("Error reading lib directory '" + libDir.getAbsolutePath() + "'.");
			}
		}
	}
	
	private String calculateChecksum(File file)
	{
		CheckedInputStream checkedStream = null;
		NullOutputStream nullOutStream = null;
		try
		{
			checkedStream = 
				new CheckedInputStream(new FileInputStream(file), new Adler32());
			nullOutStream = new NullOutputStream();
			ByteStreamPump pump = ByteStreamPump.getByteStreamPump(TRANSFER_TYPE.NetworkToByteArray);
			pump.xfer(checkedStream, nullOutStream);
			ChecksumValue checksumValue = new ChecksumValue(checkedStream.getChecksum());
			return checksumValue.toString();
		}
		catch(Exception ex)
		{
			logger.warn("Error calculating checksum for file '" + file.getAbsolutePath() + "', " + ex.getMessage());
			return null;
		}
		finally
		{
			if(checkedStream != null)
			{
				try {checkedStream.close();} catch(Exception ex) {}
			}
			if(nullOutStream != null)
			{
				try {nullOutStream.close();} catch(Exception ex) {}
			}
		}
	}
	
	private static File jreLibExtDirectory = null;
	private synchronized static File getJreLibExtDirectory()
	{
		if(jreLibExtDirectory == null)
		{
			String javaHome = getJavaHome();
			jreLibExtDirectory = new File(javaHome + File.separatorChar + "lib" + File.separatorChar + "ext");			
		}
		return jreLibExtDirectory;
	}
	
	
	
	public static String getJavaHome()
	{
		MBeanServer server = java.lang.management.ManagementFactory.getPlatformMBeanServer();
		Hashtable<String, String> objectNameKeys = new Hashtable<String, String>();
		objectNameKeys.put("type", "Runtime");
		try
		{
			ObjectName objectName = new ObjectName("java.lang", objectNameKeys);
			Object value = server.getAttribute(objectName, "SystemProperties");
			//TabularDataSupport tds = (TabularDataSupport)value;
			TabularData tds = (TabularData)value;
			
			for(Object v : tds.values())
			{
				CompositeData cd = (CompositeData)v;
				
				Object key = cd.get("key");
				Object val = cd.get("value");
				if("java.home".equals(key))
					return val.toString();
				/*
				
				if(cd.containsKey("java.home"))
				{
					System.out.println("Found java home: " + cd.get("java.home"));
				}
				else
				{
					System.out.println(cd.getClass().getName() + ": " +   cd.toString());
					for(Object rv : cd.values())
					{
						System.out.println("\t" + rv.toString());
					}
				}*/
			}
			
			return null;
		} 
		catch (MalformedObjectNameException e)
		{
			e.printStackTrace();
		} catch (NullPointerException e)
		{
			e.printStackTrace();
		} catch (AttributeNotFoundException e)
		{
			e.printStackTrace();
		} catch (InstanceNotFoundException e)
		{
			e.printStackTrace();
		} catch (MBeanException e)
		{
			e.printStackTrace();
		} catch (ReflectionException e)
		{
			e.printStackTrace();
		}
		return null;
		
	}
	
	private final static String catalina_home_env = "catalina_home";
	private static File libDirectory = null;
	private synchronized static File getLibDirectory()
	{
		if(libDirectory == null)
		{
			String tomcatDir = System.getenv(catalina_home_env);
			if(tomcatDir == null)
			{
				logger.warn("Did not find '" + catalina_home_env + "' environment variable.");
				return null;
			}
			libDirectory = new File(tomcatDir + File.separatorChar + "lib");
			logger.debug("Found lib directory '" + libDirectory.getAbsolutePath() + "'.");
		}
		return libDirectory;
	}

}
