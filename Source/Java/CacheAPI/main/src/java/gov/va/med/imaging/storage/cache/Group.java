package gov.va.med.imaging.storage.cache;

import java.util.Iterator;
import java.util.regex.Pattern;

import gov.va.med.imaging.storage.cache.events.GroupLifecycleListener;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;

/**
 * 
 * i.e. A Group may contain other Group instances, which contain other Group instances,
 * which eventually contain Instance.	
 * 
 */
public interface Group
extends MutableNamedObject, GroupAndInstanceAncestor, GroupSet, InstanceSet
{
	// A group name must be 1 to 64 chars, start with a letter and contain letters, numbers, dashes and underscores
	public static Pattern NamePattern = Pattern.compile( "[a-zA-Z][a-zA-Z0-9-_]{0,63}" );
	
	/**
	 * Return some human readable identifier for this group.
	 * This must be unique within the parent group, not necessarily across all groups.
	 * @return
	 */
	@Override
	public String getName();
	
	public java.util.Date getLastAccessed() 
	throws CacheException;

	public long getSize()
	throws CacheException;

	// ===========================================================================
	// Methods that recursively walk down the Region/Group/Instance graph are 
	// defined in GroupAndInstanceAncestor
	// ===========================================================================
	
	// ===========================================================================
	// Methods operating on the child Group of this Group are defined in GroupSet
	// ===========================================================================
	
	// =====================================================================================
	// Methods operating on child Instance are defined in InstanceSet
	// =====================================================================================
	
	// =====================================================================================
	// Eviction Methods
	// =====================================================================================
	public int evaluateAndEvictChildGroups(EvictionJudge<Group> judge)
	throws CacheException;
	
	// ======================================================================================================
	// Listener Management
	// ======================================================================================================
	public abstract void registerListener(GroupLifecycleListener listener);
	public abstract void unregisterListener(GroupLifecycleListener listener);
	
}
