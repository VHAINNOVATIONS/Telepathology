/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: November, 2009
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswlouthj
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

/**
 * Base timed cache. Holds cached items and implements the TimerTask.run() event to purge cache items after a desired amount of time.
 * 
 * @author VHAISWWERFEJ
 *
 */
public class TimedCacheFactory 
{
	
	private static Map<Object, TimedCache> mapOfCaches = new ConcurrentHashMap<Object, TimedCache>();
	
	private Logger logger = Logger.getLogger(getClass());
	
	public static <T extends BaseTimedCacheValueItem> TimedCache<T> getTimedCache(String storedObjectName)
	{
		if (mapOfCaches.containsKey(storedObjectName))
		{
			return mapOfCaches.get(storedObjectName);
		}
		else
		{
			TimedCache<T> newCache = new TimedCache<T>(storedObjectName);
			mapOfCaches.put(storedObjectName, newCache);
			return newCache;
		}
	}
	
	public static <T extends BaseTimedCacheValueItem> TimedCache<T> purgeTimedCache(String storedObjectName)
	{
		if (mapOfCaches.containsKey(storedObjectName))
		{
			mapOfCaches.remove(storedObjectName);
		}
		TimedCache<T> newCache = new TimedCache<T>(storedObjectName);
		mapOfCaches.put(storedObjectName, newCache);
		return newCache;
	}

}
