package gov.va.med.imaging.storage.cache;

import gov.va.med.imaging.storage.cache.exceptions.CacheException;

import java.util.Iterator;

/**
 * Implemented by an MutableNamedObject that is a Set of Instance.
 * @see Group
 * 
 * NOTE: this interface defines methods that generally follow the Set methods though the method
 * naming is different.  This is because InstanceSet and GroupSet may both be implemented by a 
 * single class and the naming would get confused otherwise.
 * 
 * NOTE: the word "delete" is used to mean specifically remove from a Collection.  The word
 * "remove" is used to mean specifically remove a persistent copy of an Instance or Group.
 * 
 * @author VHAISWBECKEC
 */
public interface InstanceSet 
{
	public Iterator<? extends Instance> getInstances() 
	throws CacheException;

	public Instance getOrCreateChildInstance(String key)		// not recursive
	throws CacheException;

	public Instance getChildInstance(String key) 				// not recursive
	throws CacheException;

	public void deleteChildInstance(String key, boolean forceDelete) 	// not recursive
	throws CacheException;
	
	public void deleteAllChildInstances(boolean forceDelete)						// not recursive
	throws CacheException;
}
