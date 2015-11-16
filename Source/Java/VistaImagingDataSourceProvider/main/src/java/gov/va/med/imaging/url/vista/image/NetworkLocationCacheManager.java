/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Nov 28, 2006
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
  Description: Network Locations cache manager. Maintains the cache of network Location credentials and expires old credentials after 15 minutes. 
 				This ensures credentials are not held for a long period of time.

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

import org.apache.log4j.Logger;

import gov.va.med.imaging.exchange.BaseTimedCache;
import gov.va.med.imaging.exchange.BaseTimedCacheValueItem;
import gov.va.med.imaging.exchange.TaskScheduler;
import gov.va.med.imaging.exchange.business.Image;
import gov.va.med.imaging.protocol.vista.VistaImagingTranslator;

/**
 * Network Locations cache manager. Maintains the cache of network Location credentials and expires old credentials after 15 minutes. 
 * 	This ensures credentials are not held for a long period of time.
 * 
 * @author VHAISWWERFEJ
 *
 */
public class NetworkLocationCacheManager  {

	private final static long NETWORK_LOCATION_CACHE_TIMER_REFRESH = 1000 * 60 * 15; // 15 minutes
	
	private static Logger logger = Logger.getLogger(NetworkLocationCacheManager.class);
	
	private BaseTimedCache<String, ImagingSiteCredentialsCacheItem> cache;
	
	/**
	 * In the ViX, this class is created by Spring.
	 * This constructor was made public to allow a test client to access it
	 * without Spring.  This class should be treated as a singleton, both 
	 * within Spring and in non-Spring applications.  Construction is 
	 * left to an external class though.
	 */
	public NetworkLocationCacheManager() 
	{
		logger.info("NetworkLocationCacheManager() created");
		try {
			cache = new BaseTimedCache<String, ImagingSiteCredentialsCacheItem>(NetworkLocationCacheManager.class.toString());
			TaskScheduler.getTaskScheduler().schedule(cache, NETWORK_LOCATION_CACHE_TIMER_REFRESH, NETWORK_LOCATION_CACHE_TIMER_REFRESH);
		}
		catch(Exception eX) {
			logger.error("Error creating image credentials map", eX);
		}
	}

	/**
	 * Retrieves network location information from the cache based on an image
	 * 
	 * @param image Image object searching for the network location information for
	 * @return The network locations for the image or null
	 */
	public ImagingStorageCredentials getNetworkLocations(Image image, String siteNumber) 
	{
		if(image == null)
			return null;
		if(cache == null)
			return null;
		String networkLocation = VistaImagingTranslator.extractServerShare(image);
		
		logger.info("Searching for cached imaging site credentials for site '" + siteNumber + "'.");
		
		ImagingSiteCredentialsCacheItem item = (ImagingSiteCredentialsCacheItem)cache.getItem(siteNumber);
		if(item == null)
			return null;
		logger.info("Found cached imaging site credentials for site '" + siteNumber + "'.");
		return item.imagingSiteCredentials.getStorageCredentials(networkLocation);
	}
	
	public ImagingStorageCredentials getNetworkLocation(String filename, String siteNumber)
	{
		if(filename == null)
			return null;
		if(cache == null)
			return null;
		String networkLocation = VistaImagingTranslator.extractServerShare(filename);
		
		logger.info("Searching for cached imaging site credentials for site '" + siteNumber + "'.");
		
		ImagingSiteCredentialsCacheItem item = (ImagingSiteCredentialsCacheItem)cache.getItem(siteNumber);
		if(item == null)
			return null;
		return item.imagingSiteCredentials.getStorageCredentials(networkLocation);
	}
	
	public void updateImagingSiteCredentials(ImagingSiteCredentials imagingSiteCredentials)
	{
		if(imagingSiteCredentials == null)
			return;
		if(cache == null)
			return; // could try to make it here
		ImagingSiteCredentialsCacheItem cachedItem = new ImagingSiteCredentialsCacheItem();
		cachedItem.imagingSiteCredentials = imagingSiteCredentials;
		logger.debug("Putting Imaging Site Credentials for site '" + imagingSiteCredentials.getSiteNumber() + "' into cache");
		cache.updateItem(cachedItem);
	}

	/**
	 * Updates multiple network locations in the cache. This resets the refreshTime for the network location if it is already in the cache 
	 * 	to the current time
	 * 
	 * @param networkLocations Updates the network locations in the cache
	 */
	/*
	public void updateNetworkLocations(List<NetworkLocation> networkLocations) {
		if(networkLocations == null)
			return;
		if(cache == null)
			return; // could try to make it here
		//long now = System.currentTimeMillis();
		for(int i = 0; i < networkLocations.size(); i++) {
			NetworkLocation networkLocation = networkLocations.get(i);
			NetworkLocationCacheItem cacheItem = new NetworkLocationCacheItem();
			cacheItem.networkLocation = networkLocation;
			logger.debug("Putting network location [" + cacheItem.getKey() + "] into cache");
			cache.updateItem(cacheItem);			
		}
	}*/
	
	/**
	 * Purge expired items from the cache. An expired item is purged if it has not been added/updated for longer than the retention period
	 */
	public void purgeExpiredCacheItems() {
		logger.debug("Checking for old entries in the NetworkLocation cache");
		if(cache == null)
			return;
		cache.purgeExpiredCacheItems();
	}
	
	/**
	 * Wrapper for networkLocation object that includes a time when the item was last added/updated in the cache
	 * 
	 * @author VHAISWWERFEJ
	 *
	 */
	class ImagingSiteCredentialsCacheItem 
	extends BaseTimedCacheValueItem 
	{
		ImagingSiteCredentials imagingSiteCredentials;
		long refreshedTime;

		@Override
		public String toString() 
		{
			return imagingSiteCredentials.getSiteNumber();
		}

		@Override
		public Object getKey() {
			return imagingSiteCredentials.getSiteNumber();
		}
	}
	
	/**
	 * Set the retention period in milliseconds.  This does not change how often the timer event occurs to purge the expired items.
	 * 
	 * @param networkLocationRetentionPeriod The retention period in ms
	 */
	public void setNetworkLocationRetentionPeriod(
			long networkLocationRetentionPeriod) 
	{
		cache.setRetentionPeriod(networkLocationRetentionPeriod);
	}
	
	/*
	private static String createKey(String siteNumber, String networkLocationPath)
	{
		return (siteNumber + "_" + networkLocationPath).toUpperCase();
	}*/

}
