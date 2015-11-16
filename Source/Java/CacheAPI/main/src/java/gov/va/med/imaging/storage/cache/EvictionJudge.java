package gov.va.med.imaging.storage.cache;

import gov.va.med.imaging.storage.cache.exceptions.CacheException;

/**
 * A simple interface, implemnted by something in the eviction strategy,
 * that determines whether a Group is evictable or not.
 * If this returns true then the Region should evict the group, and
 * if false then it must not evict the Group.
 * 
 * @author vhaiswbeckec
 *
 */
public interface EvictionJudge<T extends MutableNamedObject>
{
	// may throw CacheException because it may look at Group properties
	public boolean isEvictable(T evictableInstance) 
	throws CacheException;
}
