/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl.filesystem;

import gov.va.med.imaging.storage.cache.Group;
import gov.va.med.imaging.storage.cache.GroupFactory;
import gov.va.med.imaging.storage.cache.InstanceFactory;
import gov.va.med.imaging.storage.cache.events.GroupLifecycleListener;

/**
 * THIS CLASS IS NOT USED YET
 * 
 * @author VHAISWBECKEC
 *
 */
public class FileSystemGroupFactory
extends GroupFactory
{
	private final GroupLifecycleListener groupLifecycleListener;
	private final InstanceFactory instanceFactory; 

	// ==================================================================================================================
	// 
	// ==================================================================================================================
	
	FileSystemGroupFactory(
		GroupLifecycleListener groupLifecycleListener, 
		InstanceFactory instanceFactory)
	{
		this.groupLifecycleListener = groupLifecycleListener;
		this.instanceFactory = instanceFactory;
	}
	
	/**
	 * @return the groupLifecycleListener
	 */
	public GroupLifecycleListener getGroupLifecycleListener()
	{
		return groupLifecycleListener;
	}

	
	/**
	 * @return the instanceFactory
	 */
	public InstanceFactory getInstanceFactory()
	{
		return this.instanceFactory;
	}

	/**
	 * @see gov.va.med.imaging.storage.cache.impl.memory.GroupFactory#createGroup(java.lang.String)
	 */
	public Group create(String name)
	{
		return null;
	}
}