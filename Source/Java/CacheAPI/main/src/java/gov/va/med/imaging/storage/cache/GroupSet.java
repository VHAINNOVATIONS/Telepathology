package gov.va.med.imaging.storage.cache;

import gov.va.med.imaging.storage.cache.exceptions.CacheException;

import java.util.Iterator;

/**
 * Implemented by a MutableNamedObject that is a Set of Group (Group and Region).
 * @see Group
 * @see Region
 * @author VHAISWBECKEC
 *
 * NOTE: this interface defines methods that generally follow the Set methods though the method
 * naming is different.  This is because InstanceSet and GroupSet may both be implemented by a 
 * single class and the naming would get confused otherwise.
 * 
 * NOTE: the word "delete" is used to mean specifically remove from a Collection.  The word
 * "remove" is used to mean specifically remove a persistent copy of an Instance or Group.
 */
public interface GroupSet 
{
	public Iterator<? extends Group> getGroups() 
	throws CacheException;

	public Group getOrCreateChildGroup(String group)
	throws CacheException;

	public Group getChildGroup(String group) 
	throws CacheException;
	
	public void deleteChildGroup(Group childGroup, boolean forceDelete)
	throws CacheException;

	public void deleteAllChildGroups(boolean forceDelete)
	throws CacheException;

	// ============================================================================
	// Eviction Related Methods
	// ============================================================================
	/**
	 * @param judge
	 * @return
	 * @throws CacheException
	 */
	public int evaluateAndEvictChildGroups(EvictionJudge<Group> judge)
	throws CacheException;
}
