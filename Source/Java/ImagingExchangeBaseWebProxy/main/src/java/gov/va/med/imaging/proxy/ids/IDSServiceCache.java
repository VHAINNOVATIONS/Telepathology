/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 7, 2008
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
package gov.va.med.imaging.proxy.ids;

import java.util.Set;

import gov.va.med.imaging.exchange.BaseTimedCache;
import gov.va.med.imaging.exchange.BaseTimedCacheValueItem;
import gov.va.med.imaging.exchange.TaskScheduler;

/**
 * Cache to hold instances of the IDS service for a specified amount of time, 
 * then they are purged
 * 
 * @author VHAISWWERFEJ
 *
 */
public class IDSServiceCache 
{
	private final static long IDS_SERVICE_CACHE_TIMER_REFRESH = 1000 * 60 * 15; // 15 minutes
	private final static long IDS_SERVICE_SITE_OFFLINE_REFRESH = 1000 * 60 * 10; // 10 minutes
	
	private BaseTimedCache<String, IDSServiceCacheItem> serviceCache = null;
	private BaseTimedCache<String, IDSSiteOfflineCacheItem> siteOfflineCache = null;
	private BaseTimedCache<String, IDSSiteVersionUnavailableCacheItem> siteVersionUnavailableCache = null;
	
	public IDSServiceCache()
	{
		serviceCache = new BaseTimedCache<String, IDSServiceCacheItem>(IDSServiceCache.class.toString());
		TaskScheduler.getTaskScheduler().schedule(serviceCache, IDS_SERVICE_CACHE_TIMER_REFRESH, 
				IDS_SERVICE_CACHE_TIMER_REFRESH);
		
		siteOfflineCache = new BaseTimedCache<String, IDSSiteOfflineCacheItem>(IDSServiceCache.class.toString());
		siteOfflineCache.setRetentionPeriod(IDS_SERVICE_SITE_OFFLINE_REFRESH);
		TaskScheduler.getTaskScheduler().schedule(siteOfflineCache, IDS_SERVICE_SITE_OFFLINE_REFRESH, 
				IDS_SERVICE_SITE_OFFLINE_REFRESH);
		
		siteVersionUnavailableCache = new BaseTimedCache<String, IDSSiteVersionUnavailableCacheItem>(IDSServiceCache.class.toString());
		siteVersionUnavailableCache.setRetentionPeriod(IDS_SERVICE_SITE_OFFLINE_REFRESH);
		TaskScheduler.getTaskScheduler().schedule(siteVersionUnavailableCache, IDS_SERVICE_SITE_OFFLINE_REFRESH, 
				IDS_SERVICE_SITE_OFFLINE_REFRESH);
	}
	
	public boolean isSiteVersionUnavailable(String siteNumber, String version, String applicationName)
	{
		IDSSiteVersionUnavailableCacheItem item = null;
		synchronized(siteVersionUnavailableCache)
		{
			String key = createSiteVersionUnavailableKey(siteNumber, version, applicationName);
			item = (IDSSiteVersionUnavailableCacheItem)siteVersionUnavailableCache.getItem(key);
		}
		if(item != null)
			return true;
		return false;
	}
	
	public void setSiteVersionUnavailable(String siteNumber, String version, String applicationName)
	{
		IDSSiteVersionUnavailableCacheItem item = 
			new IDSSiteVersionUnavailableCacheItem(siteNumber, version, applicationName);
		synchronized (siteVersionUnavailableCache)
		{
			siteVersionUnavailableCache.updateItem(item);
		}
	}
	
	public boolean isSiteOffline(String siteNumber)
	{
		IDSSiteOfflineCacheItem item = null;
		synchronized(siteOfflineCache)
		{
			item = (IDSSiteOfflineCacheItem)siteOfflineCache.getItem(siteNumber);
		}
		if(item != null)
			return true;
		return false;
	}
	
	public void setSiteOffline(String siteNumber)
	{
		IDSSiteOfflineCacheItem item = new IDSSiteOfflineCacheItem(siteNumber);
		synchronized(siteOfflineCache)
		{
			siteOfflineCache.updateItem(item);
		}
	}
	
	/**
	 * Return an IDS service instance from the cache 
	 * @param siteNumber The site number of the service looking up
	 * @param applicationType The application type to find
	 * @param version The version of the service to find
	 * @return The IDS Service instance from the cache or null if none was found that match the input
	 */
	public IDSService getCachedService(String siteNumber, String applicationType, String version)
	{
		synchronized(serviceCache)
		{
			if(serviceCache == null)
				return null;
			String key = siteNumber + "_" + applicationType + "_" + version;
			IDSServiceCacheItem item = (IDSServiceCacheItem) serviceCache.getItem(key);
			if(item == null)
				return null;
			return item.service;
		}
	}
	
	/**
	 * Cache an IDS service instance
	 * @param siteNumber The site number for where the service came from
	 * @param service The service to cache
	 */
	public void cacheService(String siteNumber, IDSService service)
	{
		synchronized(serviceCache)
		{
			IDSServiceCacheItem item = new IDSServiceCacheItem(siteNumber, service);
			serviceCache.updateItem(item);
		}
	}
	
	/**
	 * Cache a set of IDS services
	 * @param siteNumber The site number for where all of the services came from
	 * @param services The services to cache
	 */
	public void cacheServices(String siteNumber, Set<IDSService> services)
	{
		for(IDSService service : services)
		{
			cacheService(siteNumber, service);
		}
	}
	
	/**
	 * Instances to hold the IDS Services with the proper key so the cache can find them
	 * @author VHAISWWERFEJ
	 *
	 */
	class IDSServiceCacheItem extends BaseTimedCacheValueItem
	{
		private String siteNumber;
		private IDSService service;

		/**
		 * Create a IDS Service cache item
		 * @param siteNumber
		 * @param service
		 */
		public IDSServiceCacheItem(String siteNumber, IDSService service) 
		{
			super();
			this.siteNumber = siteNumber;
			this.service = service;
		}

		@Override
		public Object getKey() 
		{
			return siteNumber + "_" + service.getApplicationType() + "_" + service.getVersion();
		}
		
	}
	
	class IDSSiteOfflineCacheItem 
	extends BaseTimedCacheValueItem
	{
		private String siteNumber;
		
		public IDSSiteOfflineCacheItem(String siteNumber)
		{
			super();
			this.siteNumber = siteNumber;
		}

		@Override
		public Object getKey()
		{
			return siteNumber;
		}
	}
	
	class IDSSiteVersionUnavailableCacheItem
	extends BaseTimedCacheValueItem
	{
		private final String siteNumber;
		private final String version;
		private final String applicationName;
		
		public IDSSiteVersionUnavailableCacheItem(String siteNumber, String version, String applicationName)
		{
			this.siteNumber = siteNumber;
			this.version = version;
			this.applicationName = applicationName;
		}

		@Override
		public Object getKey()
		{
			return createSiteVersionUnavailableKey(siteNumber, version, applicationName);
		}
		
	}
	
	static String createSiteVersionUnavailableKey(String siteNumber, String version, String applicationName)
	{
		return siteNumber + "_" + version + "_" + applicationName;
	}
}
