package gov.va.med.imaging.storage.cache.impl.filesystem;

import gov.va.med.imaging.storage.cache.Instance;
import gov.va.med.imaging.storage.cache.InstanceByteChannelFactory;
import gov.va.med.imaging.storage.cache.InstanceReadableByteChannel;
import gov.va.med.imaging.storage.cache.InstanceWritableByteChannel;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import gov.va.med.imaging.storage.cache.exceptions.InstanceInaccessibleException;
import gov.va.med.imaging.storage.cache.exceptions.PersistenceIOException;
import gov.va.med.imaging.storage.cache.impl.PersistentInstance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * This class is the binding from an abstract PersistentInstance to a 
 * file system implementation.
 *  
 */
public class FileSystemInstance 
extends PersistentInstance
implements Comparable<Instance>
{
	public final static String checksumFileExtension = ".checksum";
	private File instanceFile = null;

	// =======================================================================================================================================
	/**
	 * @param instanceFile
	 * @param instanceFactoryChannel
	 * @return
	 * @throws CacheException
	 */
	public static FileSystemInstance getInstance(
		File instanceFile, 
		InstanceByteChannelFactory<File> instanceFactoryChannel,
		int secondsReadWaitsForWriteCompletion,
		boolean setModificationTimeOnRead
	) 
	throws CacheException
	{
		if(instanceFile.exists())
			return new FileSystemInstance(instanceFile, false, instanceFactoryChannel, secondsReadWaitsForWriteCompletion, setModificationTimeOnRead);
		
		Logger.getLogger(FileSystemInstance.class).info("Request to get() an non-existent instance '" + instanceFile.getPath() + "'.");
		return null;
	}
	
	/**
	 * 
	 * @param instanceFile
	 * @param instanceFactoryChannel
	 * @return
	 * @throws CacheException
	 */
	public static FileSystemInstance getOrCreateInstance(
		File instanceFile, 
		InstanceByteChannelFactory<File> instanceFactoryChannel,
		int secondsReadWaitsForWriteCompletion,
		boolean setModificationTimeOnRead
	) 
	throws CacheException
	{
		return new FileSystemInstance(instanceFile, true, instanceFactoryChannel, secondsReadWaitsForWriteCompletion, setModificationTimeOnRead);
	}
	
	// =======================================================================================================================================
	/**
	 * @param instanceFile
	 * @param createIfNotExist
	 * @param instanceFactoryChannel
	 * @throws CacheException
	 */
	private FileSystemInstance(
		File instanceFile, 
		boolean createIfNotExist, 
		InstanceByteChannelFactory<File> instanceFactoryChannel,
		int secondsReadWaitsForWriteCompletion,
		boolean setModificationTimeOnRead
	)
	throws CacheException
	{
		super(instanceFactoryChannel, secondsReadWaitsForWriteCompletion, setModificationTimeOnRead);
		this.instanceFile = instanceFile;
		if(!instanceFile.exists() && !createIfNotExist )
			throw new InstanceInaccessibleException();
		
		// load the checksum value if it exists
		loadChecksum();
	}

	/**
	 * 
	 */
	public String getName()
	{
		return instanceFile.getName();
	}
	
	// ================================================================================================================================
	// The type specific methods to tie this class to the persistent storage
	// ================================================================================================================================
	@SuppressWarnings("unchecked")
	@Override
	public InstanceByteChannelFactory<File> getInstanceChannelFactory()
	{
		return super.getInstanceChannelFactory();
	}

	protected InstanceReadableByteChannel createInstanceReadableByteChannel() 
	throws PersistenceIOException, CacheException
	{
		return getInstanceChannelFactory().getInstanceReadableByteChannel(this.getFile(), this);
	}

	protected InstanceWritableByteChannel createInstanceWritableByteChannel() 
	throws PersistenceIOException, CacheException
	{
		return getInstanceChannelFactory().getInstanceWritableByteChannel(this.getFile(), this);
	}
	
	/**
	 * Return true if the file exists in the filesystem
	 */
	public boolean isPersistent()
	{
		return getFile().exists();
	}
	
	/**
	 * Create the file in the filesystem.
	 * Once this is run the isPersistent() method must return true.
	 */
	protected void createPersistent() 
	throws PersistenceIOException
	{
		try
		{
			File parentDir = getFile().getParentFile();
			if (!parentDir.exists())
				parentDir.mkdirs();
			getFile().createNewFile();
		}
		catch (IOException ioX)
		{
			try
			{
				throw new PersistenceIOException("Error creating new file '" + getFile().getCanonicalPath() + "'.", ioX);
			} 
			catch (IOException x)
			{
				throw new PersistenceIOException("Error creating new file '" + getName() + "'.", ioX);
			}
		}
	}
	
	@Override
	protected void removePersistent()
	throws PersistenceIOException
	{
		long deleteTimeout = System.currentTimeMillis() + 5000l;
		
		// retry the delete
		while( !getFile().delete() && System.currentTimeMillis() < deleteTimeout )
			try {Thread.sleep(1000l);} 
			catch (InterruptedException e) {log.error("Interrupted while waiting for file deletion."); break;}
		if(getFile().exists())
			throw new PersistenceIOException("Failed to delete data file '" + getFile().getPath() + "'.");
		
		try 
		{
			if( persistentChecksumExists() )
				removeChecksumPersistent();
		} 
		catch (IOException ioX) 
		{
			log.warn("IOException occured when deleting checksum file.", ioX);
			throw new PersistenceIOException(ioX);
		}
		
		return;
	}
	
	@Override
	protected boolean persistentChecksumExists() 
	throws IOException 
	{
		return getChecksumFile().exists();
	}

	@Override
	protected void setLastModified(long date)
	{
		instanceFile.setLastModified(date);
	}
	
	
	public File getFile()
	{
		return instanceFile;
	}
	
	public Date getLastAccessed()
	{
		return new Date(instanceFile.lastModified());
	}

	public long getSize()
	{
		return instanceFile.length();
	}
	
	public String getMediaType()
	{
		return null;
	}

	public int compareTo(Instance o)
	{
		if(o instanceof FileSystemInstance)
		{
			FileSystemInstance that = (FileSystemInstance)o;
			if( this.instanceFile.equals(that.instanceFile) )
				return 0;
			try
			{
				return this.instanceFile.getCanonicalPath().compareTo(that.instanceFile.getCanonicalPath());
			} 
			catch (IOException e)
			{
				return -1;
			}
		}
		return -1;
	}

	// ================================================================================================================================
	// The type specific methods to tie this class to the persistent storage for storing checksum
	// 
	// NOTE: the checksum file and filename are managed as singletons within an instance.
	// This prevents multiple copies of the File being created.
	// ================================================================================================================================
	private Object checksumFileSynch = new Object(); 
	private String checksumFilename = null;
	private File checksumFile = null;
	private String createChecksumFileName()
	{
		synchronized(checksumFileSynch)
		{
			if( checksumFilename == null )
				checksumFilename = instanceFile.getAbsolutePath() + checksumFileExtension;
		}
		return checksumFilename;
	}

	private File getChecksumFile()
	{
		synchronized(checksumFileSynch)
		{
			if(checksumFile == null)
				checksumFile = new File(createChecksumFileName());
		}
		
		return checksumFile;
	}
	// ================================================================================================================
	
	@Override
	protected InputStream openChecksumInputStream()
	throws IOException
	{
		File checksumFile = getChecksumFile();
		if( ! checksumFile.exists() )
			return null;
		return new FileInputStream( checksumFile );
	}

	@Override
	protected OutputStream openChecksumOutputStream()
	throws IOException
	{
		File checksumFile = getChecksumFile();
		return new FileOutputStream( checksumFile );
	}

	/**
	 * This method MUST either success or throw an exception.
	 * @throws PersistenceIOException 
	 */
	@Override
	protected void removeChecksumPersistent() 
	throws PersistenceIOException
	{
		File checksumFile = getChecksumFile();
		long deleteTimeout = System.currentTimeMillis() + 5000l;
		
		// retry the delete
		while( !checksumFile.delete() && System.currentTimeMillis() < deleteTimeout )
			try {Thread.sleep(1000l);} 
			catch (InterruptedException e) {log.error("Interrupted while waiting for checksum file deletion."); break;}
		if(checksumFile.exists())
			throw new PersistenceIOException("Failed to delete checksum file '" + checksumFile.getAbsolutePath() + "'.");
	}
}
