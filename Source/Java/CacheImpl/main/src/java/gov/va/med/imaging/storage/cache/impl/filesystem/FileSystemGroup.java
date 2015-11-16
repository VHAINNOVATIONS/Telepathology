package gov.va.med.imaging.storage.cache.impl.filesystem;

import gov.va.med.imaging.storage.cache.Group;
import gov.va.med.imaging.storage.cache.Instance;
import gov.va.med.imaging.storage.cache.InstanceByteChannelFactory;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import gov.va.med.imaging.storage.cache.exceptions.GroupDoesNotExistException;
import gov.va.med.imaging.storage.cache.exceptions.PersistenceIOException;
import gov.va.med.imaging.storage.cache.impl.PersistentGroup;
import gov.va.med.imaging.storage.cache.impl.PersistentGroupSet;
import gov.va.med.imaging.storage.cache.impl.PersistentInstanceSet;

import java.io.File;
import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * 
 * 
 */
public class FileSystemGroup 
extends PersistentGroup
{
	private Logger log = Logger.getLogger(this.getClass());
	private File groupDirectory = null;
	private FileSystemInstanceSet childInstances = null;
	private FileSystemGroupSet childGroups = null;
	
	/**
	 * 
	 * @param groupDirectory
	 * @param instanceFactoryChannel
	 * @return
	 * @throws CacheException
	 */
	public static FileSystemGroup getOrCreate(
		File groupDirectory, 
		InstanceByteChannelFactory<File> instanceFactoryChannel,
		int secondsReadWaitsForWriteCompletion,
		boolean setModificationTimeOnRead)
	throws CacheException
	{
		if(! groupDirectory.exists())
		{
			Logger.getLogger(FileSystemGroup.class).debug("Creating persistence for group '" + groupDirectory.getAbsolutePath() + "'.");
			groupDirectory.mkdirs();
		}
		
		return new FileSystemGroup(groupDirectory, instanceFactoryChannel, secondsReadWaitsForWriteCompletion, setModificationTimeOnRead);
	}
	
	/**
	 * 
	 * @param groupDirectory
	 * @param instanceFactoryChannel
	 * @return
	 * @throws CacheException
	 */
	public static FileSystemGroup get(
		File groupDirectory, 
		InstanceByteChannelFactory<File> instanceFactoryChannel,
		int secondsReadWaitsForWriteCompletion,
		boolean setModificationTimeOnRead)
	throws CacheException
	{
		if(! groupDirectory.exists())
			return null;
		
		return new FileSystemGroup(groupDirectory, instanceFactoryChannel, secondsReadWaitsForWriteCompletion, setModificationTimeOnRead);
	}
	
	/**
	 * 
	 * @param groupDirectory
	 * @param createIfNotExist
	 * @param instanceFactoryChannel
	 * @throws CacheException
	 */
	FileSystemGroup(
		File groupDirectory, 
		InstanceByteChannelFactory<File> instanceFactoryChannel,
		int secondsReadWaitsForWriteCompletion,
		boolean setModificationTimeOnRead
	)
	throws CacheException
	{
		super(instanceFactoryChannel);
		this.groupDirectory = groupDirectory;
		if(! groupDirectory.exists())
			throw new GroupDoesNotExistException();
		childInstances = new FileSystemInstanceSet(groupDirectory, instanceFactoryChannel, secondsReadWaitsForWriteCompletion, setModificationTimeOnRead);
		childGroups = new FileSystemGroupSet(groupDirectory, instanceFactoryChannel, secondsReadWaitsForWriteCompletion, setModificationTimeOnRead);
	}

	// ======================================================================================================
	// Persistence type specific abstract method overrides
	// This is the stuff that makes a group a file system persisted group
	// ======================================================================================================
	
	protected PersistentInstanceSet getPersistentInstanceSet()
	{
		return childInstances;
	}
	
	@Override
	protected PersistentGroupSet getPersistentGroupSet()
	{
		return childGroups;
	}

	@Override
	public String getName()
	{
		return getGroupDirectory().getName();
	}

	protected File getGroupDirectory()
	{
		return groupDirectory;
	}
	
	/**
	 * The last accessed date is the last modified of the directory or of
	 * any file in the directory, otherwise the directory gets deleted
	 * right after it was created.
	 */
	@Override
	public Date getLastAccessed() 
	throws CacheException
	{
		long dirModified = getGroupDirectory().lastModified();
		Date instancesLastAccessed = super.getLastAccessed();
		
		return instancesLastAccessed.getTime() > dirModified ? 
				instancesLastAccessed : new Date(dirModified);
	}	
	
	// ======================================================================================================
	// Child Instance Management
	// ======================================================================================================
	/**
	 * It is important that any operation that creates child groups or instances be synchronized
	 * using the childInstancesLock to assure that there is exactly one representation of the group or instance.
	 * 
	 * @param key
	 * @return
	 * @throws CacheException
	 */
	@Override
	public Instance getOrCreateChildInstance(String key)
	throws CacheException
	{
		return childInstances.getChild(key, true);
	}
	
	@Override
	public Instance getChildInstance(String key) 
	throws CacheException
	{
		return childInstances.getChild(key, false);
	}

	@Override
	public void deleteChildInstance(String key, boolean forceDelete) 
	throws CacheException
	{
		deleteChildInstance(getChildInstance(key), forceDelete);
	}

	/**
	 * 
	 */
	public void deleteChildInstance(Instance childInstance, boolean forceDelete)
	throws CacheException
	{
		if(childInstance != null)
			childInstances.deleteChild(childInstance, forceDelete);
	}
	
	
	@Override
	public Iterator<? extends Instance> getInstances() 
	throws CacheException
	{
		return childInstances.hardReferenceIterator();
	}

	@Override
	public void deleteAllChildInstances(boolean forceDelete) 
	throws CacheException
	{
		childInstances.deleteAll(forceDelete);
	}


	// ======================================================================================================
	// Child Group Management
	// ======================================================================================================
	/**
	 * 
	 */
	@Override
	public Group getChildGroup(String groupName) 
	throws CacheException
	{
		return childGroups.getChild(groupName, false);
	}

	/**
	 * 
	 */
	@Override
	public Group getOrCreateChildGroup(String groupName) 
	throws CacheException
	{
		return childGroups.getChild(groupName, true);
	}
	
	/**
	 * Remove a child group.
	 * @throws CacheException 
	 */
	@Override
	public void deleteChildGroup(Group childGroup, boolean forceDelete) 
	throws CacheException
	{
		childGroups.deleteChild(childGroup, forceDelete);
	}

	@Override
	public void deleteAllChildGroups(boolean forceDelete) 
	throws CacheException
	{
		childGroups.deleteAll(forceDelete);
	}

	/**
	 * All subdirectories of ourselves are child groups.
	 * @throws CacheException 
	 */
	@Override
	public Iterator<? extends Group> getGroups() 
	throws CacheException
	{
		return childGroups.hardReferenceIterator();
	}
	
	/**
	 * Remove ourselves, along with all children
	 * @throws CacheException 
	 */
	@Override
	public void delete(boolean forceDelete) 
	throws CacheException
	{
		log.info("Group '" + this.getName() + "' is removing itself...");
		deleteAllChildGroups(forceDelete);
		deleteAllChildInstances(forceDelete);
		
		// remove the persistent group
		long deleteTimeout = System.currentTimeMillis() + 5000l;
		
		File groupDirectory = getGroupDirectory();
		// retry the delete
		while( !groupDirectory.delete() && System.currentTimeMillis() < deleteTimeout )
			try {Thread.sleep(1000l);} 
			catch (InterruptedException e) {log.error("Interrupted while waiting for file deletion."); break;}
		if(groupDirectory.exists())
			throw new PersistenceIOException("Failed to delete group directory '" + groupDirectory.getPath() + "'.");
		
		log.info("Group '" + this.getName() + "' is removed.");
	}

	public int compareTo(FileSystemGroup that)
	{
		if(this.getGroupDirectory().equals(that.getGroupDirectory()))
			return 0;
		return this.getGroupDirectory().getAbsolutePath().compareTo(that.getGroupDirectory().getAbsolutePath());
	}


	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof FileSystemGroup)
		{
			FileSystemGroup that = (FileSystemGroup)obj;
			return this.getGroupDirectory().equals(that.getGroupDirectory());
		}
		return false;
	}


	@Override
	public String toString()
	{
		return this.getClass().getSimpleName() + " - " + this.getGroupDirectory().getAbsolutePath();
	}

	/**
	 * Clear all ancestors of this group
	 */
	public void clear()
	{
	}
	
}
