/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 17, 2010
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
package gov.va.med.imaging.proxy.ids.configuration;

import gov.va.med.imaging.facade.configuration.AbstractBaseFacadeConfiguration;
import gov.va.med.imaging.facade.configuration.FacadeConfigurationFactory;
import gov.va.med.imaging.facade.configuration.exceptions.CannotLoadConfigurationException;

/**
 * @author vhaiswwerfej
 *
 */
public class IDSProxyConfiguration 
extends AbstractBaseFacadeConfiguration
{
	private String idsProtocol = null;
	private String idsApplicationPath = null;
	private String idsServicePath = null;
	private Integer idsConnectionTimeoutMs = null;
	private Integer idsResponseTimeoutMs = null;
	
	public IDSProxyConfiguration()
	{
		super();
	}
	
	private final static String defaultIdsProtocol = "http";
	private final static String defaultIdsApplicationPath = "IDSWebApp";
	private final static String defaultIdsServicePath = "VersionsService";
	private final static int defaultIdsConnectionTimeoutMs = 10000; // 10 seconds
	private final static int defaultIdsResponseTimeoutMs = 30000; // 30 seconds
	
	public synchronized static IDSProxyConfiguration getIdsProxyConfiguration()
	{
		try
		{
			return FacadeConfigurationFactory.getConfigurationFactory().getConfiguration(
					IDSProxyConfiguration.class);
		}
		catch(CannotLoadConfigurationException clcX)
		{
			return null;
		}
	}
	

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.facade.configuration.AbstractBaseFacadeConfiguration#loadDefaultConfiguration()
	 */
	@Override
	public AbstractBaseFacadeConfiguration loadDefaultConfiguration()
	{
		this.idsApplicationPath = defaultIdsApplicationPath;
		this.idsProtocol = defaultIdsProtocol;
		this.idsServicePath = defaultIdsServicePath;
		this.idsConnectionTimeoutMs = defaultIdsConnectionTimeoutMs;
		this.idsResponseTimeoutMs = defaultIdsResponseTimeoutMs;
		return this;
	}

	/**
	 * @return the idsProtocol
	 */
	public String getIdsProtocol()
	{
		return idsProtocol;
	}
	
	public String getIdsProtocolWithDefault()
	{
		if(idsProtocol == null)
			return defaultIdsProtocol;
		return idsProtocol;			
	}

	/**
	 * @param idsProtocol the idsProtocol to set
	 */
	public void setIdsProtocol(String idsProtocol)
	{
		this.idsProtocol = idsProtocol;
	}

	/**
	 * @return the idsApplicationPath
	 */
	public String getIdsApplicationPath()
	{
		return idsApplicationPath;
	}

	public String getIdsApplicationPathWithDefault()
	{
		if(idsApplicationPath == null)
			return defaultIdsApplicationPath;
		return idsApplicationPath;
	}

	/**
	 * @param idsApplicationPath the idsApplicationPath to set
	 */
	public void setIdsApplicationPath(String idsApplicationPath)
	{
		this.idsApplicationPath = idsApplicationPath;
	}

	/**
	 * @return the idsServicePath
	 */
	public String getIdsServicePath()
	{
		return idsServicePath;
	}

	public String getIdsServicePathWithDefault()
	{
		if(idsServicePath == null)
			return defaultIdsServicePath;
		return idsServicePath;
	}

	/**
	 * @param idsServicePath the idsServicePath to set
	 */
	public void setIdsServicePath(String idsServicePath)
	{
		this.idsServicePath = idsServicePath;
	}

	
	/**
	 * @return the idsConnectionTimeoutMs
	 */
	public Integer getIdsConnectionTimeoutMs()
	{
		return idsConnectionTimeoutMs;
	}


	/**
	 * @param idsConnectionTimeoutMs the idsConnectionTimeoutMs to set
	 */
	public void setIdsConnectionTimeoutMs(Integer idsConnectionTimeoutMs)
	{
		this.idsConnectionTimeoutMs = idsConnectionTimeoutMs;
	}


	/**
	 * @return the idsResponseTimeoutMs
	 */
	public Integer getIdsResponseTimeoutMs()
	{
		return idsResponseTimeoutMs;
	}


	/**
	 * @param idsResponseTimeoutMs the idsResponseTimeoutMs to set
	 */
	public void setIdsResponseTimeoutMs(Integer idsResponseTimeoutMs)
	{
		this.idsResponseTimeoutMs = idsResponseTimeoutMs;
	}


	public int getIdsResponseTimeoutMsWithDefault()
	{
		if(idsResponseTimeoutMs == null)
			return defaultIdsResponseTimeoutMs;
		return idsResponseTimeoutMs;
	}
	
	public int getIdsConnectionTimeoutMsWithDefault()
	{
		if(idsConnectionTimeoutMs == null)
			return defaultIdsConnectionTimeoutMs;
		return idsConnectionTimeoutMs;
	}

	public static void main(String [] args)
	{
		IDSProxyConfiguration config = getIdsProxyConfiguration();
		
		int connectionTimeout = 0;
		int responseTimeout = 0;
		
		for(int i = 0; i < args.length; i++)
		{
			if("-connectiontimeout".equalsIgnoreCase(args[i]))
			{
				connectionTimeout = Integer.parseInt(args[++i]);
			}
			else if("-responsetimeout".equals(args[i]))
			{
				responseTimeout = Integer.parseInt(args[++i]);
			}
		}
		if(connectionTimeout > 0)
			config.setIdsConnectionTimeoutMs(connectionTimeout);
		if(responseTimeout > 0)
			config.setIdsResponseTimeoutMs(responseTimeout);
		config.storeConfiguration();
	}
}
