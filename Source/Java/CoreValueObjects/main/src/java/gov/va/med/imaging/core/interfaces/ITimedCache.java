/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Dec 26, 2006
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
  Description: 
  	Timed cache interface. Used to cache information but only for a specified amount of time. After the time has ellapsed and item not 
 	touched in that amount of time, the item is removed from the cache.

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
package gov.va.med.imaging.core.interfaces;

import gov.va.med.imaging.exchange.BaseTimedCacheValueItem;

/**
 * Timed cache interface. Used to cache information but only for a specified amount of time. After the time has ellapsed and item not 
 * 	touched in that amount of time, the item is removed from the cache.
 * 
 * @author VHAISWWERFEJ
 *
 */
public interface ITimedCache {

	/**
	 * Removes expired cache items from the cache. A cache item is expired if it has existed longer than the retention 
	 * 	period allows and has not been updated in that amount of time
	 * 
	 */
	public abstract void purgeExpiredCacheItems();
	
	/**
	 * Retrieves an item from the cache
	 * 
	 * @param key The key to search for an item
	 * @return The item from the cache or null if it was not found in the cache
	 */
	public abstract BaseTimedCacheValueItem getItem(Object key);
	
	/**
	 * Adds or updates an item in the cache. If the item already exists in the cache the time for that item is set to the current time (to extend the retention time).
	 * 
	 * @param object Item to add or update in the cache
	 */
	public abstract void updateItem(BaseTimedCacheValueItem object);
	
	/**
	 * Adds or updates multiple objects at a time.
	 * 
	 * @param objects Multiple objects to update at a single time
	 */
	public abstract void updateItems(BaseTimedCacheValueItem[] objects);
	
	/**
	 * 	Set the tmount of time for items to exist in the cache (in milliseconds).  
	 * 
	 * @param period Amount of time in ms.
	 */
	public abstract void setRetentionPeriod(long period);
}
