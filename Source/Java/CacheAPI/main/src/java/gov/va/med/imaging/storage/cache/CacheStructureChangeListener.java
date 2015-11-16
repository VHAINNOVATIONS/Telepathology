/**
 * 
 */
package gov.va.med.imaging.storage.cache;

/**
 * @author VHAISWBECKEC
 *
 */
public interface CacheStructureChangeListener
{
	/**
	 * Called when multiple structure changes have occured, like in initialization
	 * @param cache
	 */
	public void cacheStructureChanged(Cache cache);
	
	/**
	 * Called when a new region is added to the cache
	 * 
	 * @param cache
	 * @param newRegion
	 */
	public void regionAdded(Cache cache, Region newRegion);
	
	/**
	 * Called when a region is removed from the cache
	 * 
	 * @param cache
	 * @param oldRegion
	 */
	public void regionRemoved(Cache cache, Region oldRegion);
	
	/**
	 * Called when a new eviction strategy is added to the cache.
	 * 
	 * @param cache
	 * @param newEvictionStrategy
	 */
	public void evictionStrategyAdded(Cache cache, EvictionStrategy newEvictionStrategy);
	
	/**
	 * Called when an eviction strategy is removed from the cache
	 * 
	 * @param cache
	 * @param oldEvictionStrategy
	 */
	public void evictionStrategyRemoved(Cache cache, EvictionStrategy oldEvictionStrategy);
}
