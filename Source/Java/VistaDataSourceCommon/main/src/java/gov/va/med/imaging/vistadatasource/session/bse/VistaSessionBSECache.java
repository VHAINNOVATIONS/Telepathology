/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Dec 18, 2009
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
package gov.va.med.imaging.vistadatasource.session.bse;

import org.apache.log4j.Logger;

import gov.va.med.imaging.exchange.BaseTimedCache;
import gov.va.med.imaging.exchange.TaskScheduler;

import gov.va.med.imaging.exchange.BaseTimedCacheValueItem;

/**
 * This cache contains the BSE connection status of remote sites.  The purpose is to cache the status of a BSE connection to 
 * a remote site to prevent the VIX from continuously attempting to connect to remote VA sites with BSE when the remote
 * site does not have BSE installed.  
 * 
 * @author vhaiswwerfej
 *
 */
public class VistaSessionBSECache 
{
	private final static long bseCacheRefreshTime = 1000 * 60 * 5; // 5 minutes (how often the cache is checked)
	private final static long bseCacheRetentionTime = 1000 * 60 * 10; // 10 minutes (how long things can live in the cache)
	private final static Logger logger = Logger.getLogger(VistaSessionBSECache.class);
	
	private static VistaSessionBSECache vistaSessionBseCache = null;
	
	public synchronized static VistaSessionBSECache getVistaSessionBseCache()
	{
		if(vistaSessionBseCache == null)
		{
			vistaSessionBseCache = new VistaSessionBSECache();
		}
		return vistaSessionBseCache;
	}
	
	private BaseTimedCache<String, BSECacheValueItem> cache = null;
	
	public VistaSessionBSECache() 
	{
		logger.info("VistaSessionBSECache() created");
		try 
		{
			cache = new BaseTimedCache<String, BSECacheValueItem>(VistaSessionBSECache.class.toString());
			cache.setRetentionPeriod(bseCacheRetentionTime);
			TaskScheduler.getTaskScheduler().schedule(cache, bseCacheRefreshTime, bseCacheRefreshTime);
		}
		catch(Exception eX) 
		{
			logger.error("Error creating VistaSessionBSECache", eX);
		}
	}
	
	/**
	 * Get the status of a BSE connection to the specified Site.  Will always return a value even if the site is not in
	 * the cache.
	 * 
	 * @param siteNumber
	 * @return
	 */
	public VistaSiteBseStatus getSiteStatus(String siteNumber)
	{
		BSECacheValueItem item = null;
		logger.debug("Finding the BSE site status for site '" + siteNumber + "'");
		synchronized (cache) 
		{
			item = (BSECacheValueItem)cache.getItem(siteNumber);	
		}		
		if(item == null)		
		{
			logger.debug("Got null BSE cache item for site '" + siteNumber + "'");
			return VistaSiteBseStatus.bseUnknown;
		}
		else
		{
			logger.debug("Got '" + item.bseStatus + "' BSE status from cache for site '" + siteNumber + "'");
			return item.bseStatus;
		}
	}
	
	/**
	 * Update the status of the specified site.  
	 * 
	 * <br>
	 * <b>Special Note</b>: If the site already exists in the cache and the status has not changed, the item in the 
	 * cache will not be updated.  This is to prevent the "touch" value of the item in the cache from updated.  If a site
	 * is being connected to and the BSE status doesn't change, we want it to expire from the cache rather than always
	 * reusing the cache.  This is to prevent data from living in the cache too long, we want to retry every so often in case
	 * something changes.
	 * 
	 * @param siteNumber
	 * @param status
	 */
	public void updateSiteStatus(String siteNumber, VistaSiteBseStatus status)
	{
		BSECacheValueItem item = new BSECacheValueItem(siteNumber, status);
		logger.debug("Updating the site status for site '" + siteNumber + "', new status is '" + status + "'.");
		synchronized(cache)
		{
			BSECacheValueItem oldItem = (BSECacheValueItem)cache.getItem(siteNumber);
			if(oldItem == null)
			{
				// not already in cache, need to add
				logger.debug("Site '" + siteNumber + "' not in cache, adding to cache");
				cache.updateItem(item);
			}
			else
			{
				if(oldItem.getBseStatus() !=  status)
				{
					// site status has changed, want to update
					logger.debug("Site '" + siteNumber + "' already in cache but with different status, updating item with new status");
					cache.updateItem(item);
				}
				else
				{
					logger.debug("Site '" + siteNumber + "' already in cache with same status, not updating");
				}
			}
			// if site already in cache and status not changed, do not update in cache.
		}
	}
	
	class BSECacheValueItem extends BaseTimedCacheValueItem
	{		
		private final String siteNumber;
		private final VistaSiteBseStatus bseStatus;
		
		public BSECacheValueItem(String siteNumber, VistaSiteBseStatus status)
		{
			this.siteNumber = siteNumber;
			this.bseStatus = status;
		}

		/* (non-Javadoc)
		 * @see gov.va.med.imaging.exchange.BaseTimedCacheValueItem#getKey()
		 */
		@Override
		public Object getKey() 
		{
			return this.siteNumber;
		}

		/**
		 * @return the siteNumber
		 */
		public String getSiteNumber() {
			return siteNumber;
		}

		/**
		 * @return the bseStatus
		 */
		public VistaSiteBseStatus getBseStatus() {
			return bseStatus;
		}	
	}
}
