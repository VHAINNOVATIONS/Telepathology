/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 6, 2009
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
package gov.va.med.imaging.url.vista.image;

import gov.va.med.imaging.exchange.BaseTimedCache;
import gov.va.med.imaging.exchange.BaseTimedCacheValueItem;
import gov.va.med.imaging.exchange.TaskScheduler;

import org.apache.log4j.Logger;

/**
 * Cache manager for VistaRad site credentials, uses timed cache to keep items for only a 
 * certain amount of time so they do not get old.
 * 
 * @author vhaiswwerfej
 *
 */
public class VistaRadSiteCredentialsCacheManager 
{
private final static long VISTA_RAD_SITE_CREDENTIALS_CACHE_TIMER_REFRESH = 1000 * 60 * 15; // 15 minutes
	
	private static Logger logger = Logger.getLogger(VistaRadSiteCredentialsCacheManager.class);
	
	private BaseTimedCache<String, VistaRadSiteCredentialsCacheItem> cache;
	
	public VistaRadSiteCredentialsCacheManager() 
	{
		logger.info("VistaRadSiteCredentialsCacheManager() created");
		try {
			cache = 
				new BaseTimedCache<String, VistaRadSiteCredentialsCacheItem>(VistaRadSiteCredentialsCacheManager.class.toString());
			TaskScheduler.getTaskScheduler().schedule(cache, 
					VISTA_RAD_SITE_CREDENTIALS_CACHE_TIMER_REFRESH, VISTA_RAD_SITE_CREDENTIALS_CACHE_TIMER_REFRESH);
		}
		catch(Exception eX) {
			logger.error("Error creating image credentials map", eX);
		}
	}
	
	public VistaRadSiteCredentials getSiteCredentials(String siteNumber)
	{
		if(siteNumber == null)
			return null;
		
		VistaRadSiteCredentialsCacheItem cacheItem = (VistaRadSiteCredentialsCacheItem)cache.getItem(siteNumber);
		cacheItem.updateRefreshTime(); // if gotten from cache, then update its last access time to now
		if(cacheItem == null)
			return null;
		return cacheItem.credentials;
	}
	
	public void updateSiteCredentials(VistaRadSiteCredentials siteCredentials)
	{
		if(cache == null)
			return;
		if(siteCredentials == null)
			return;
		VistaRadSiteCredentialsCacheItem cacheItem = new VistaRadSiteCredentialsCacheItem(siteCredentials);
		logger.debug("Putting vistaRad site credentials for site [" + siteCredentials.getSiteNumber() + "] into cache");
		cache.updateItem(cacheItem);
	}
	
	/**
	 * Purge expired items from the cache. An expired item is purged if it has not been added/updated for longer than the retention period
	 */
	public void purgeExpiredCacheItems() 
	{
		logger.debug("Checking for old entries in the NetworkLocation cache");
		if(cache == null)
			return;
		cache.purgeExpiredCacheItems();
	}
	
	class VistaRadSiteCredentialsCacheItem 
	extends BaseTimedCacheValueItem 
	{
		VistaRadSiteCredentials credentials;
		long refreshedTime;
		
		public VistaRadSiteCredentialsCacheItem(VistaRadSiteCredentials credentials)
		{
			this.credentials = credentials;
		}

		@Override
		public String toString() 
		{
			return credentials.toString();
		}

		@Override
		public Object getKey() 
		{
			return credentials.getSiteNumber();
		}
	}
}
