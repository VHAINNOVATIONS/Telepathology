package gov.va.med.imaging.storage.cache.impl.filesystem;

import gov.va.med.imaging.storage.cache.Group;
import gov.va.med.imaging.storage.cache.InstanceByteChannelFactory;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import gov.va.med.imaging.storage.cache.impl.PersistentGroupSet;

import java.io.File;
import java.io.FileFilter;
import java.lang.ref.SoftReference;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * This class encapsulates the collection of child groups in a FileSystemCacheGroup and in
 * a FileSystemCacheRegion.
 * It is the responsibility of this class to ensure that the persistent (filesystem)
 * and the transient (memory) views of the Groups in a group are consistent.
 * It is a requirement that this class NOT keep references to child groups that 
 * would prevent garbage collection of groups that are no longer referenced outside
 * the cache (i.e. if the application does not have a reference then the cache should not
 * prevent garbage collection).
 * This class should be the sole modifier of the groups referenced within.
 * 
 * @author VHAISWBECKEC
 *
 */
class FileSystemGroupSet
extends PersistentGroupSet
{
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private Logger log = Logger.getLogger(this.getClass());
	private File rootDirectory = null;			// the directory in which all of our instances reside in persistent storage
	
	/**
	 * 
	 * @param rootDirectory
	 * @param byteChannelFactory
	 * @param secondsReadWaitsForWriteCompletion
	 * @param setModificationTimeOnRead
	 */
	FileSystemGroupSet(
		File rootDirectory, 
		InstanceByteChannelFactory byteChannelFactory,
		int secondsReadWaitsForWriteCompletion,
		boolean setModificationTimeOnRead)
	{
		super(byteChannelFactory, secondsReadWaitsForWriteCompletion, setModificationTimeOnRead);
		if(rootDirectory == null)
			throw new IllegalArgumentException("RootDirectory must be a valid directory.");
		this.rootDirectory = rootDirectory;
	}
	
	public File getRootDirectory()
	{
		return rootDirectory;
	}
	
	/**
	 * Override this to reduce type check warnings
	 */
	@Override
	@SuppressWarnings("unchecked")
	public InstanceByteChannelFactory<File> getByteChannelFactory()
	{
		return super.getByteChannelFactory();
	}
	
	/**
	 * Get or create a Group mapped to persistent storage.
	 * 
	 * @param name - the group name to get or create
	 * @param create - true if the groups should be created if it does not exist\
	 */
	@Override
	protected Group getOrCreate(String name, boolean create) 
	throws CacheException
	{
		File childGroupDir = new File(getRootDirectory(), name);
		FileSystemGroup child = create ? 
				FileSystemGroup.getOrCreate(childGroupDir, getByteChannelFactory(), getSecondsReadWaitsForWriteCompletion(), isSetModificationTimeOnRead()) :
				FileSystemGroup.get(childGroupDir, getByteChannelFactory(), getSecondsReadWaitsForWriteCompletion(), isSetModificationTimeOnRead());
		
		return child;
	}
	
	/**
	 * Assure that the internal represenation of child instances matches
	 * what is in the persistent storage (file system). 
	 */
	protected void internalSynchronizeChildren()
	throws CacheException
	{
		pruneNullSoftReferences();

		// get a list of all the child files
		for( File childDir : getChildDirectories() )
		{
			String name = childDir.getName();		// the file name and the Instance name are the same
			Group childGroup = getTransient(name);		// get an existing reference by name
			if(childGroup == null)
			{
				//childGroup = getChild(name, false);
				File groupDir = new File(this.getRootDirectory(), name);
				childGroup =  FileSystemGroup.get(groupDir, getByteChannelFactory(), getSecondsReadWaitsForWriteCompletion(), isSetModificationTimeOnRead());
				SoftReference<FileSystemGroup> groupRef = new SoftReference<FileSystemGroup>( (FileSystemGroup)childGroup);
				add( groupRef );
			}
		}
	}

	/**
	 * Get all of the files in our directory.
	 * All files (not directories) are considered child instances of this group.
	 * 
	 * @return
	 */
	private File[] getChildDirectories()
	{
		File[] childFiles = getRootDirectory().listFiles(new FileFilter()
		{
			public boolean accept(File pathname)
			{
				return pathname.isDirectory();
			}
		} );

		// never return null, return an empty array even if the root directory does not exist
		return childFiles == null ? new File[]{} : childFiles;
	}
}
