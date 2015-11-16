package gov.va.med.imaging.storage.cache;

import gov.va.med.imaging.storage.cache.exceptions.CacheStateException;

/**
 * The interface to be implemented by classes that are interested in lifecycle events
 * of the cache (start and stop). The Cache and Region interface derive from this interface,
 * hence Region and Cache implementations must understand lifecycle events.
 * Cache implementations should forward lifecycle messages to its constituent Regions.
 * 
 * @author VHAISWBECKEC
 *
 * @see Region
 * @see Cache
 */
public interface CacheLifecycleListener
{
	/**
	 * Notify the cache instance that it is starting or stopping.
	 * Depending on the cache implementation, it may be critical that it be
	 * notified of start and stop events.
	 * 
	 * @param event either START or STOP
	 * @throws CacheStateException if the listener is not in a state that it may be 
	 * started ot stopped.
	 */
	public void cacheLifecycleEvent(CacheLifecycleEvent event)
	throws CacheStateException;
}
