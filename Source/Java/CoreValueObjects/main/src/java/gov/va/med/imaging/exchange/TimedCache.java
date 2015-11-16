/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Dec 26, 2006
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
  Description: 
  	Base timed cache. Holds cached items and implements the TimerTask.run() event to purge cache items after a desired amount of time.

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
package gov.va.med.imaging.exchange;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import gov.va.med.imaging.ReadWriteLockCollections;
import gov.va.med.imaging.core.interfaces.ITimedCache;

/**
 * Base timed cache. Holds cached items and implements the TimerTask.run() event to purge cache items after a desired amount of time.
 * 
 * @author VHAISWWERFEJ
 *
 */
public class TimedCache<U extends BaseTimedCacheValueItem> extends TimerTask{
	
	protected Map<Object, U> map = null;
	protected long retentionPeriod;
	private String cachedObjectType;
	
	private Logger logger = Logger.getLogger(getClass());

	public TimedCache(String cachedObjectType) {
		super();
		this.cachedObjectType = cachedObjectType;
		retentionPeriod = 1000 * 60 * 15; // 15 minutes
		try {
			map = new ConcurrentHashMap<Object, U>();
		}
		catch(Exception eX) {
			map = null;
			eX.printStackTrace();
		}		
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.broker.interfaces.ITimedCache#purgeExpiredCacheItems()
	 */
	public void purgeExpiredCacheItems() {
		long expiredTime = System.currentTimeMillis() - retentionPeriod;
		
		Collection<U> cacheItems = map.values();
		logger.debug(cachedObjectType + " cache has [" + cacheItems.size() + "] items");
		Iterator<U> cacheIter = cacheItems.iterator();
		while(cacheIter.hasNext()) {
			U item = cacheIter.next();
			if(item.getRefreshTime() < expiredTime) {
				cacheIter.remove();
			}
		}	
	}

	/* (non-Javadoc)
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run() {
		logger.debug(cachedObjectType + ".run() [" + cachedObjectType + "], purging expired cache items");
		purgeExpiredCacheItems();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.broker.interfaces.ITimedCache#getItem(java.lang.Object)
	 */
	public U getItem(Object key) {
		return map.get(key);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.broker.interfaces.ITimedCache#updateItem(gov.va.med.imaging.exchange.broker.BaseTimedCacheValueItem)
	 */
	public void updateItem(U object) {
		object.updateRefreshTime();
		map.put(object.getKey(), object);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.broker.interfaces.ITimedCache#updateItems(gov.va.med.imaging.exchange.broker.BaseTimedCacheValueItem[])
	 */
	public void updateItems(U[] objects) {
		for(int i = 0; i < objects.length; i++) {
			updateItem(objects[i]);
		}		
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.broker.interfaces.ITimedCache#setRetentionPeriod(long)
	 */
	public void setRetentionPeriod(long period) {
		retentionPeriod = period;
	}
}
