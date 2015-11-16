/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 20, 2010
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
package gov.va.med.imaging.vista.storage;

import gov.va.med.imaging.exchange.BaseTimedCache;
import gov.va.med.imaging.exchange.BaseTimedCacheValueItem;
import gov.va.med.imaging.exchange.TaskScheduler;

import org.apache.log4j.Logger;

/**
 * Caches in memory the port that can be used to communicate with an imaging server
 * 
 * @author vhaiswwerfej
 *
 */
public class SmbConnectionInformationManager
{
	private final static Logger logger = Logger.getLogger(SmbConnectionInformationManager.class);
	
	private BaseTimedCache<String, SmbConnectionInformationCacheValueItem> cache = null;
	private final static long SMB_CONNECTION_INFORMATION_CACHE_TIMER_REFRESH = 1000 * 60 * 15; // 15 minutes
	private final static long SMB_CONNECTION_INFORMATION_CACHE_RETENTION_PERIOD = 1000 * 60 * 60; // 60 minutes
	
	public SmbConnectionInformationManager()
	{
		try
		{
			cache = new BaseTimedCache<String, SmbConnectionInformationCacheValueItem>(SmbConnectionInformationManager.class.toString());
			cache.setRetentionPeriod(SMB_CONNECTION_INFORMATION_CACHE_RETENTION_PERIOD);
			TaskScheduler.getTaskScheduler().schedule(cache, SMB_CONNECTION_INFORMATION_CACHE_TIMER_REFRESH, 
					SMB_CONNECTION_INFORMATION_CACHE_TIMER_REFRESH);
		}
		catch(Exception ex)
		{
			logger.error("Error creating cache, will not be able to cache SMB Connection information", ex);
		}
	}
	
	private static SmbConnectionInformationManager smbConnectionInformationManager = null;
	
	public synchronized static SmbConnectionInformationManager getSmbConnectionInformationManager()
	{
		if(smbConnectionInformationManager == null)
		{
			smbConnectionInformationManager = new SmbConnectionInformationManager();
		}
		return smbConnectionInformationManager;
	}
	
	public int getSuccessfulPort(String server, int defaultPort)
	{
		if(cache == null)
			return defaultPort;
		if(server == null)
			return defaultPort;
		SmbConnectionInformationCacheValueItem item = null;
		logger.debug("Searching SmbConnectionInformation cache for successful port for server '" + server + "'.");
		synchronized(cache)
		{
			item = (SmbConnectionInformationCacheValueItem)cache.getItem(server.toLowerCase()); 
		}
		if(item == null)
		{
			logger.debug("Did not find successful port for server '" + server + "', returning default port '" + defaultPort + "'.");
			return defaultPort;
		}
		logger.debug("Found successful port for server '" + server + "', returning port '" + item.getSuccessfulPort() + "'.");
		return item.getSuccessfulPort();
	}
	
	public void updateSuccessfulPort(String server, int port)
	{
		if(cache == null)
			return;
		if(server == null)
			return;
		logger.debug("Updating the known succsesful port for server '" + server + "' to '" + port + "'.");
		SmbConnectionInformationCacheValueItem item = new SmbConnectionInformationCacheValueItem(server, port);
		synchronized(cache)
		{
			cache.updateItem(item);
		}
	}	
	
	class SmbConnectionInformationCacheValueItem 
	extends BaseTimedCacheValueItem
	{
		final String server;
		final int successfulPort;
		
		SmbConnectionInformationCacheValueItem(String server, int successfulPort)
		{
			this.server = server;
			this.successfulPort = successfulPort;
		}

		@Override
		public Object getKey()
		{
			return getServer().toLowerCase().toString();
		}

		public String getServer()
		{
			return server;
		}

		public int getSuccessfulPort()
		{
			return successfulPort;
		}		
	}

}
