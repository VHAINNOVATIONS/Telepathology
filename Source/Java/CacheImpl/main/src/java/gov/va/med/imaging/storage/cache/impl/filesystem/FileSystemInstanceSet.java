package gov.va.med.imaging.storage.cache.impl.filesystem;

import gov.va.med.imaging.storage.cache.Instance;
import gov.va.med.imaging.storage.cache.InstanceByteChannelFactory;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import gov.va.med.imaging.storage.cache.impl.PersistentInstanceSet;

import java.io.File;
import java.io.FileFilter;
import java.lang.ref.SoftReference;
import java.util.Iterator;

/**
 * This class encapsulates the collection of child instances in a FileSystemCacheGroup.
 * It is the responsibility of this class to ensure that the persistent (filesystem)
 * and the transient (memory) views of the Instances in a group are consistent.
 * It is a requirement that this class NOT keep references to child instances that 
 * would prevent garbage collection of instances that are no longer referenced outside
 * the cache (i.e. if the application does not have a reference then the cache should not
 * prevent garbage collection).
 * This class should be the sole modifier of the instances referenced within.
 * 
 * @author VHAISWBECKEC
 *
 */
class FileSystemInstanceSet
extends PersistentInstanceSet
{
	private static final long serialVersionUID = -346790233288183129L;
	private File rootDirectory = null;			// the directory in which all of our instances reside in persistent storage
	
	/**
	 * 
	 * @param rootDirectory
	 * @param byteChannelFactory
	 * @param secondsReadWaitsForWriteCompletion
	 * @param setModificationTimeOnRead
	 */
	FileSystemInstanceSet(
		File rootDirectory, 
		InstanceByteChannelFactory byteChannelFactory,
		int secondsReadWaitsForWriteCompletion,
		boolean setModificationTimeOnRead
	)
	{
		super(byteChannelFactory, secondsReadWaitsForWriteCompletion, setModificationTimeOnRead);
		this.rootDirectory = rootDirectory;
	}
	
	/**
	 * 
	 * @return
	 */
	public File getRootDirectory()
	{
		return rootDirectory;
	}
	
	/**
	 * 
	 */
	@Override
	protected Instance getOrCreate(String name, boolean create) 
	throws CacheException
	{
		File childInstanceFile = new File(getRootDirectory(), name);
		Instance child = create ? 
			FileSystemInstance.getOrCreateInstance(childInstanceFile, getByteChannelFactory(), getSecondsReadWaitsForWriteCompletion(), isSetModificationTimeOnRead()) :
			FileSystemInstance.getInstance(childInstanceFile, getByteChannelFactory(), getSecondsReadWaitsForWriteCompletion(), isSetModificationTimeOnRead());
		
		return child;
	}
	
	/**
	 * Assure that the internal representation of child instances matches
	 * what is in the file system. 
	 */
	@Override
	protected void internalSynchronizeChildren()
	throws CacheException
	{
		getLogger().info("ENTERING internalSynchronizeChildren(), " + this.size() + " child references.");
		// prune unused references
		pruneNullSoftReferences();
		getLogger().info("WITHIN internalSynchronizeChildren(), " + this.size() + " child references.");

		// get a list of all the child files
		for( File childFile : getChildFiles() )
		{
			String instanceName = childFile.getName();		// the file name and the Instance name are the same
			Instance childInstance = getTransient(instanceName);		// get an existing reference by name
			if(childInstance == null)
			{
				//getLogger().info("WITHIN internalSynchronizeChildren() BEFORE getChild(), " + this.size() + " child references.");
				//childInstance = getChild(instanceName, false);
				//getLogger().info("WITHIN internalSynchronizeChildren() AFTER getChild(), " + this.size() + " child references.");
				
				File instanceFile = new File(this.getRootDirectory(), instanceName);
				childInstance =  FileSystemInstance.getInstance(instanceFile, getByteChannelFactory(), getSecondsReadWaitsForWriteCompletion(), isSetModificationTimeOnRead());
				SoftReference<FileSystemInstance> instanceRef = new SoftReference<FileSystemInstance>( (FileSystemInstance)childInstance);
				add( instanceRef );
				
				getLogger().info("WITHIN internalSynchronizeChildren(), added reference to '" + instanceName + "'");
			}
		}
		getLogger().info("EXITING internalSynchronizeChildren(), " + this.size() + " child references.");
	}

	/**
	 * Get all of the files in our directory.
	 * All files (not directories) are considered child instances of this group unless they 
	 * are checksum files (i.e. end in ".checksum").
	 * 
	 * @return
	 */
	private File[] getChildFiles()
	{
		
		File[] childFiles = getRootDirectory().listFiles(new FileFilter()
		{
			public boolean accept(File instanceFile)
			{
				return 
					! instanceFile.isDirectory() 
					&& ! instanceFile.getName().endsWith(FileSystemInstance.checksumFileExtension)
					&& instanceFile.exists();
			}
		} );

		// return an empty array even if the group directory does not exist
		return childFiles == null ? new File[]{} : childFiles;
	}
}
