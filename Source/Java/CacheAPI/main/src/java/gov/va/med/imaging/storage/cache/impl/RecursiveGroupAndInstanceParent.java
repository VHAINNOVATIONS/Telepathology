/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl;

import java.util.logging.Logger;

import sun.security.action.GetLongAction;

import gov.va.med.imaging.storage.cache.Group;
import gov.va.med.imaging.storage.cache.GroupAndInstanceAncestor;
import gov.va.med.imaging.storage.cache.GroupSet;
import gov.va.med.imaging.storage.cache.Instance;
import gov.va.med.imaging.storage.cache.InstanceSet;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;

/**
 * @author VHAISWBECKEC
 *
 * This class adds recursive group name handling for instances as well as groups.
 * Any class that can be a parent of a set of Group and Instance instances should derive from this
 * class.
*/
public abstract class RecursiveGroupAndInstanceParent 
extends RecursiveGroupParent
implements GroupAndInstanceAncestor, GroupSet, InstanceSet
{
	/**
	 * 
	 */
	public RecursiveGroupAndInstanceParent()
	{
	}

	// ========================================================================================================
	// Abstract methods bind this class to a persistence mechanism
	// ========================================================================================================
	
	/**
	 * Get or create an Instance.  The groupName is an ordered array if the
	 * instances ancestor groups, starting from the progeny of this
	 * group.
	 */
	@Override
	public Instance getOrCreateInstance(String[] groupName, String key) 
	throws CacheException
	{
		return getOrCreateInstance(groupName, key, true); 
	}

	/**
	 * Get an Instance.  The groupName is an ordered array if the
	 * instances ancestor groups, starting from the progeny of this
	 * group.
	 */
	@Override
	public Instance getInstance(String[] groupName, String key) 
	throws CacheException
	{
		return getOrCreateInstance(groupName, key, false); 
	}
	
	/**
	 * 
	 * @param groupName
	 * @param key
	 * @param allowCreate
	 * @return
	 * @throws CacheException
	 */
	@Override
	public Instance getOrCreateInstance(String[] groupName, String key, boolean allowCreate) 
	throws CacheException
	{
		if(groupName == null || groupName.length == 0)
			return allowCreate ?
				getOrCreateChildInstance(key):
				getChildInstance(key);

		return super.getOrCreateInstance(groupName, key, allowCreate);
	}
	
	/**
	 * Delete an Instance.  The groupName is an ordered array of the
	 * instances ancestor groups, starting from the progeny of this
	 * group.
	 * NOTE: this is a recursive function.  The group name specifies a path
	 * through an object graph of Group instances.  This method follows that path,
	 * removing the group name that it currently resides on. 
	 */
	public void deleteInstance(String[] groupName, String key, boolean forceDelete) 
	throws CacheException
	{
		if(groupName == null || groupName.length == 0)
		{
			deleteChildInstance(key, forceDelete);
			return;
		}
		
		super.deleteInstance(groupName, key, forceDelete);
	}
}
