package gov.va.med.imaging.storage.cache.impl.filesystem;

import gov.va.med.imaging.storage.cache.Group;
import gov.va.med.imaging.storage.cache.exceptions.*;
import gov.va.med.imaging.storage.cache.impl.PersistentGroupSet;
import gov.va.med.imaging.storage.cache.impl.PersistentRegion;
import gov.va.med.imaging.storage.cache.impl.memento.PersistentRegionMemento;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * The File System based implementation of a cache region.
 * 
 */
public class FileSystemRegion
extends PersistentRegion
implements FileSystemRegionMBean
{
	private Logger log = Logger.getLogger(this.getClass());
	private File regionDirectory = null;
	private FileSystemGroupSet childGroups = null;

	// ======================================================================================================
	// Factory Methods
	// ======================================================================================================
	
	/**
	 * Create a FileSystemCacheRegion, restoring the state from a memento (as much as possible)
	 * 
	 * @param memento
	 * @param instanceFactoryChannel
	 * @param evictionStrategy
	 * @return
	 * @throws CacheInitializationException 
	 */
	public static FileSystemRegion create(
		FileSystemCache parentCache,
		PersistentRegionMemento memento) 
	throws RegionInitializationException
	{
		return create(
			parentCache, 
			memento.getName(),
			memento.getEvictionStrategyNames(),
			memento.getSecondsReadWaitsForWriteCompletion(), 
			memento.isSetModificationTimeOnRead());
	}
	
	/**
	 * Create a FileSysytemRegion instance
	 * 
	 * @param cacheRootDirectory
	 * @param name
	 * @param instanceFactoryChannel
	 * @param evictionStrategy
	 * @param secondsReadWaitsForWriteCompletion
	 * @param setModificationTimeOnRead
	 * @return
	 */
	public static FileSystemRegion create(
		FileSystemCache parentCache,
		String name,
		String[] evictionStrategyNames, 
		int secondsReadWaitsForWriteCompletion, 
		boolean setModificationTimeOnRead )
	throws RegionInitializationException
	{
		try
		{
			return new FileSystemRegion(
					parentCache, 
					name, 
					evictionStrategyNames, 
					secondsReadWaitsForWriteCompletion, 
					setModificationTimeOnRead);
		} 
		catch (CacheException x)
		{
			Logger.getLogger(FileSystemRegion.class).error(x);
			throw new RegionInitializationException(x);
		}
	}

	// ======================================================================================================
	// Constructors
	// ======================================================================================================
	
	/**
	 * 
	 * @param cacheRootDirectory
	 * @param name
	 * @param instanceFactoryChannel
	 * @param evictionStrategy
	 * @param secondsReadWaitsForWriteCompletion
	 * @param setModificationTimeOnRead
	 * @throws CacheException
	 */
	private FileSystemRegion(
			FileSystemCache parentCache,
			String name, 
			String[] evictionStrategyNames, 
			int secondsReadWaitsForWriteCompletion, 
			boolean setModificationTimeOnRead) 
	throws CacheException
	{
		super(parentCache, name, evictionStrategyNames, secondsReadWaitsForWriteCompletion, setModificationTimeOnRead);
	}
	
	/**
	 * A type-converted accessor of the parent cache
	 * @return
	 */
	private FileSystemCache getParentFileSystemCache()
	{
		return (FileSystemCache)getParentCache();
	}

	// ======================================================================================================
	// Core Accessors
	// ======================================================================================================
	
	/**
	 * The cacheRootDirectory must be set before
	 * the Region is initialized, else an exception will be thrown
	 * @throws CacheStateException 
	 */
	public File getCacheRootDirectory() 
	throws CacheStateException
	{
		return getParentFileSystemCache().getRootDirectory();
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.filesystem.FileSystemRegionMBean#getRegionDirectory()
	 */
	@Override
	public File getRegionDirectory()
	throws RegionNotInitializedException
	{
		if(! isInitialized())
			throw new RegionNotInitializedException("Region Directory is not set until initialization is complete");
		return regionDirectory;
	}
	
	/**
	 * @see gov.va.med.imaging.storage.cache.impl.filesystem.FileSystemRegionMBean#getFreeSpace()
	 */
	@Override
	public long getFreeSpace()
	{
		return getDiskStatistic("getFreeSpace");
	}
	
	/**
	 * @see gov.va.med.imaging.storage.cache.Region#getTotalSpace()
	 */
	@Override
	public long getTotalSpace()
	{
		return getDiskStatistic("getTotalSpace");
	}
	
	private long getDiskStatistic(String methodName)
	{
		try
		{
			File regionDirectory = getRegionDirectory();

			// Do the method call using reflection so that this will still compile under
			// JDK 1.5.
			//return regionDirectory.getFreeSpace();
			Method freeSpaceGetter = regionDirectory.getClass().getMethod(methodName, (Class[])null);
			Long freeSpace = (Long)freeSpaceGetter.invoke(regionDirectory, (Object[])null);
			return freeSpace.longValue();
		} 
		catch (RegionNotInitializedException x)
		{
			log.error("Attempt to call '" + methodName + "' before region has been initialized", x);
		} 
		catch (SecurityException x)
		{
			log.error("Attempt to call '" + methodName + "' failed.  Space threshold evictions will not work!", x);
		} 
		catch (NoSuchMethodException x)
		{
			log.warn("Attempt to call '" + methodName + "' from JRE previous to 1.6 is being ignored.  Space threshold evictions will not work!", x);
		} 
		catch (IllegalArgumentException x)
		{
			log.error("Attempt to call '" + methodName + "' failed.  Space threshold evictions will not work!", x);
		} 
		catch (IllegalAccessException x)
		{
			log.error("Attempt to call '" + methodName + "' failed.  Space threshold evictions will not work!", x);
		} 
		catch (InvocationTargetException x)
		{
			log.error("Attempt to call '" + methodName + "' failed.  Space threshold evictions will not work!", x);
		}
		
		return -1L;
	}

	@Override
	protected PersistentGroupSet getPersistentGroupSet() 
	throws RegionNotInitializedException
	{
		if(! isInitialized().booleanValue())
			throw new RegionNotInitializedException("Region Directory is not set until initialization is complete");
		return childGroups;
	}
	
	/**
	 * Regions cannot be removed, so throw an error if someone tries.
	 */
	@Override
	public void delete(boolean forceDelete) 
	throws CacheException
	{
		throw new CacheInternalException("Illegal attempt to remove a Region.");
	}

	/**
	 * 
	 */
	@Override
	public void initialize() 
	throws RegionInitializationException
	{
		log.debug("'" + this.getName() + "' initializing...");
		
		try
		{
			File cacheRoot = getParentFileSystemCache().getRootDirectory();
			
			if(cacheRoot == null)
				throw new RegionInitializationException("Cache root directory must be set before initializing FileSystemCacheRegion instance.");
			
			this.regionDirectory = new File(cacheRoot, this.getName());
			
			if(! regionDirectory.exists())
			{
				log.debug("'" + this.getName() + "' initializing - directory does not exist, creating...");
				try
				{
					regionDirectory.mkdirs();
				} 
				catch (RuntimeException rX)
				{
					log.error(rX);
					throw new RegionInitializationException("Group directory '" + regionDirectory.getAbsolutePath() + "' did not exist and could not be created.");
				}
			}
			log.debug("'" + this.getName() + "' initializing - directory exists");
			
			this.childGroups = new FileSystemGroupSet(regionDirectory, getInstanceFactoryChannel(), getSecondsReadWaitsForWriteCompletion(), isSetModificationTimeOnRead());
		} 
		catch (CacheStateException x)
		{
			log.error(x);
			throw new RegionInitializationException(
					"Cache state exception occurred initializing region '" + getName() + 
					"'.  The occurence of this exception is an implementation error!", 
					x);
		}
		
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
}
