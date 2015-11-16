/**
 * 
 */
package gov.va.med.imaging.storage.cache;

import gov.va.med.imaging.storage.cache.exceptions.CacheException;

/**
 * A MutableNamedObject is any object in the cache that has a name and that may be 
 * modified and deleted.  Examples include Region, Group and Instance.
 * 
 * @author VHAISWBECKEC
 *
 */
public interface MutableNamedObject
{
	/**
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 *
	 */
	public void delete(boolean forceDelete)
	throws CacheException;
}
