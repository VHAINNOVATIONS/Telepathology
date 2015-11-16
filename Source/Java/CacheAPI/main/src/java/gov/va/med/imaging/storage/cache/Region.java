package gov.va.med.imaging.storage.cache;

import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import gov.va.med.imaging.storage.cache.exceptions.UnknownEvictionStrategyException;
import gov.va.med.imaging.storage.cache.memento.RegionMemento;

import java.util.regex.Pattern;


/**
 * A Region is the first level of collection in a Cache.
 * A Region collects groups and instances that are to be managed similarly.
 * A Region has associated EvictionStrategy instances that determine when
 * Group and Instance are removed from the cache.
 * 
 */
public interface Region
extends CacheLifecycleListener, MutableNamedObject, GroupAndInstanceAncestor, GroupSet
{
	// A region name must be 1 to 64 chars, start with a letter and contain letters, numbers, dashes and underscores
	public static Pattern NamePattern = Pattern.compile( "[a-zA-Z][a-zA-Z0-9-_]{0,63}" );
	public static final int SIZE_CALCULATION_RETRIES = 5;
	
	/**
	 * Initialization provides the Region the opportunity to 
	 * either initialization itself, or if it throws an exception,
	 * to halt the initialization of the entire cache.
	 * 
	 * @return
	 */
	public abstract Boolean isInitialized();
	public abstract void setInitialized(Boolean initialized)
	throws CacheException;
	
	/**
	 * Notify the region instance that it is starting or stopping.
	 * Depending on the cache implementation, it may be critical that it be
	 * notified of start and stop events.
	 * @param event
	 */
	@Override
	public abstract void cacheLifecycleEvent(CacheLifecycleEvent event);

	/**
	 * Report the available space (in bytes) for the storage backing this region.
	 * 
	 * @return
	 */
	public long getFreeSpace();
	
	/**
	 * Report the total space (in bytes) for the storage backing this region.
	 * 
	 * @return
	 */
	public long getTotalSpace();
	
	/**
	 * Return a "best effort" estimate of the size of all elements in the Region.
	 * This method is defined intentionally to NOT allow any exceptions.  Implementations
	 * are expected to soldier on and return a best guess with the prevailing conditions.
	 * In particular, groups added/deleted/modified while calculating the used space may be
	 * ignored for purposes of this method.
	 * @return
	 */
	public long getUsedSpace();
	
	// ===========================================================================
	// Methods that recursively walk down the Region/Group/Instance graph are 
	// defined in GroupAndInstanceAncestor
	// ===========================================================================
	
	// ===============================================================================
	// Eviction Strategy Methods
	// ===============================================================================
	/**
	 * @param evictionStrategy
	 * @throws UnknownEvictionStrategyException 
	 */
	public abstract void setEvictionStrategyNames(String[] evictionStrategyName) 
	throws UnknownEvictionStrategyException;
	
	/**
	 * 
	 * @return
	 */
	public abstract String[] getEvictionStrategyNames();
	
	/**
	 * 
	 * @return
	 */
	public abstract EvictionStrategy[] getEvictionStrategies();
	
	/**
	 * @return
	 */
	public abstract RegionMemento createMemento();
}
