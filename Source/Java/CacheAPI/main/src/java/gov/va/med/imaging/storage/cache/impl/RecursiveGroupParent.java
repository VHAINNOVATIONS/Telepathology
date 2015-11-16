package gov.va.med.imaging.storage.cache.impl;

import gov.va.med.imaging.storage.cache.Group;
import gov.va.med.imaging.storage.cache.GroupAndInstanceAncestor;
import gov.va.med.imaging.storage.cache.GroupSet;
import gov.va.med.imaging.storage.cache.Instance;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import gov.va.med.imaging.storage.cache.exceptions.InstanceInaccessibleException;

import org.apache.log4j.Logger;

/**
 * A superclass of Group and Region implementations.
 * 
 * This class implements the recursive Group handling of Group and Region instances.
 * Any class that can be a parent of a set of Group instances should derive from this
 * class.
 * A class that can be a parent of both Group and Instance instances should derive
 * from PersistentGroupInstanceParent.
 * 
 * @author vhaiswbeckec
 *
 */
public abstract class RecursiveGroupParent
implements GroupAndInstanceAncestor, GroupSet
{
	private Logger log = Logger.getLogger(this.getClass());
	
	protected Logger getLogger(){return log;}
	
	/**
	 * Get or create an Instance.  The groupName is an ordered array if the
	 * instances ancestor groups, starting from the progeny of this
	 * group.
	 */
	public Instance getOrCreateInstance(String[] groupName, String key) 
	throws CacheException
	{
		return getOrCreateInstance(groupName, key, true); 
	}

	/**
	 * 
	 * @param groupName
	 * @param key
	 * @return
	 * @throws CacheException
	 */
	public Instance getInstance(String[] groupName, String key) 
	throws CacheException
	{
		return getOrCreateInstance(groupName, key, false); 
	}

	/**
	 * Pass through the call to delete an Instance.  
	 * The groupName is an ordered array of the instances ancestor groups, starting from the progeny of this group.
	 * 
	 * NOTE: an instance of RecursiveGroupParent should not be a direct superclass of a
	 * class that contains an Instance Set.  Instance Set implementation should be derived from
	 * RecursiveGroupAndInstanceParent, which overrides this method with one that can delete and Instance
	 * that is a child.
	 */
	public void deleteInstance(String[] groupName, String key, boolean forceDelete) 
	throws CacheException
	{
		Group childGroup = getChildGroup(groupName[0]);
		
		// if the instance ancestor groups do not exist, just return
		if(childGroup == null)
		{
			log.info("Unable to delete instance '" + key + "', ancestor group '" + groupName[0] + "'does not exist");
			return;
		}
		
		String[] progenyGroupNames = new String[groupName.length-1];
		System.arraycopy(groupName, 1, progenyGroupNames, 0, progenyGroupNames.length);
		childGroup.deleteInstance( progenyGroupNames, key, forceDelete);
		return;
	}
	
	/**
	 * NOTE: this method will NOT create an Instance instance.
	 * The derived class RecursiveGroupAndInstanceParent is called
	 * directly, if it needs to recurse through offspring Groups then
	 * it calls this method to do so.
	 * 
	 * @param groupName
	 * @param key
	 * @param allowCreate
	 * @return
	 * @throws CacheException
	 */
	Instance getOrCreateInstance(String[] groupName, String key, boolean allowCreate) 
	throws CacheException
	{
		Group childGroup = allowCreate ?
			getOrCreateChildGroup(groupName[0]) :
			getChildGroup(groupName[0]);
		
		if(childGroup == null)
		{
			if(allowCreate)
				log.error("Unable to create child group '" + groupName[0] + "'.");
			return null;
		}
		
		if(groupName.length == 1)					// shortcut if childGroup is the parent of the Instance
			return allowCreate ? childGroup.getOrCreateChildInstance(key) : childGroup.getChildInstance(key);
		
		String[] progenyGroupNames = new String[groupName.length-1];
		System.arraycopy(groupName, 1, progenyGroupNames, 0, progenyGroupNames.length);
		return allowCreate ? 
			childGroup.getOrCreateInstance( progenyGroupNames, key) :
			childGroup.getInstance( progenyGroupNames, key);
	}

	/*
	 * ==================================================================================
	 * The following methods implement the recursive functions in the Group interface.
	 * These functions delegate the child level (i.e. directly beneath this group) to
	 * concrete class methods.
	 * ==================================================================================
	 */
	public Group getOrCreateGroup(String[] groupName) 
	throws CacheException
	{
		if(groupName == null || groupName.length == 0)
			return null;

		Group childGroup = getOrCreateChildGroup(groupName[0]);
		
		String[] progenyGroupNames = new String[groupName.length-1];
		System.arraycopy(groupName, 1, progenyGroupNames, 0, progenyGroupNames.length);
		return groupName.length == 0 ? childGroup : childGroup.getOrCreateGroup( progenyGroupNames);
	}

	/**
	 * 
	 */
	public Group getGroup(String[] groupName) 
	throws CacheException
	{
		if(groupName == null || groupName.length == 0 || groupName[0] == null)
			return null;
		
		Group childGroup = getChildGroup(groupName[0]);
		
		if(childGroup == null)
			throw new InstanceInaccessibleException("Group '" + groupName[0] + "' not found.");
		
		int progenyGroupLength = groupName.length-1;
		if(progenyGroupLength > 0)
		{
			String[] progenyGroupNames = new String[progenyGroupLength];
			System.arraycopy(groupName, 1, progenyGroupNames, 0, progenyGroupNames.length);
			return childGroup.getGroup( progenyGroupNames );
		}
		else
			return childGroup;
	}
	
	/**
	 * Delete a Group.  The groupName is an ordered array of the
	 * instances ancestor groups, starting from the progeny of this
	 * group.
	 * NOTE: this is a recursive function.  The group name specifies a path
	 * through an object graph of Group instances.  This method follows that path,
	 * removing the group name that it currently resides on. 
	 */
	public void deleteGroup(String[] groupName, boolean forceDelete) 
	throws CacheException
	{
		Group childGroup = this.getChildGroup(groupName[0]);
		
		// if the group is a child of ours then delete it
		if(groupName.length == 1)
		{
			if(childGroup != null)
				this.deleteChildGroup(childGroup, forceDelete);
		}
		else
		{
			String[] progenyGroupNames = new String[groupName.length-1];
			System.arraycopy(groupName, 1, progenyGroupNames, 0, progenyGroupNames.length);
			childGroup.deleteGroup(progenyGroupNames, forceDelete);
		}
		
		return;
	}
	
}
