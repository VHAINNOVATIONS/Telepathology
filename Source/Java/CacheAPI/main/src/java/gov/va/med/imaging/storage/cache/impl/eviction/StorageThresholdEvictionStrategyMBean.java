/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl.eviction;

/**
 * @author VHAISWBECKEC
 *
 */
public interface StorageThresholdEvictionStrategyMBean
{

	public abstract long getDelay();

	public abstract long getInterval();

	/**
	 * Get the minimum free space threshold.  Once the free space falls below
	 * this level the eviction strategy will evict the LRU files until the
	 * free space is greater than targetFreeSpaceThreshold.
	 * minFreeSpaceThreshold can be changed at runtime, but the
	 * change will not be effective until the next sweep.
	 * minFreeSpaceThreshold must be less than or equal to targetFreeSpaceThreshold.
	 * It is recommended that minFreeSpaceThreshold be significantly less than 
	 * targetFreeSpaceThreshold to avoid many sweep invocations (which require
	 * traversing the files in the regions).
	 * @return
	 */
	public abstract long getMinFreeSpaceThreshold();

	/**
	 * @see #getMinFreeSpaceThreshold()
	 */
	public abstract void setMinFreeSpaceThreshold(long minFreeSpaceThreshold);

	/**
	 * @see #getMinFreeSpaceThreshold()
	 * @return
	 */
	public abstract long getTargetFreeSpaceThreshold();

	/**
	 * @see #getMinFreeSpaceThreshold()
	 */
	public abstract void setTargetFreeSpaceThreshold(long targetFreeSpaceThreshold);

	/**
	 * 
	 * @return
	 */
	public abstract long getMaxUsedSpaceThreshold();

	/**
	 * 
	 * @param maxUsedSpaceThreshold
	 */
	public abstract void setMaxUsedSpaceThreshold(long maxUsedSpaceThreshold);
	
}