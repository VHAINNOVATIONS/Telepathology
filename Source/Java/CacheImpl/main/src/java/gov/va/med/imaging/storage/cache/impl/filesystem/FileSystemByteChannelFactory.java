/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl.filesystem;


import gov.va.med.imaging.channels.ChecksumFactory;
import gov.va.med.imaging.storage.cache.*;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import gov.va.med.imaging.storage.cache.exceptions.PersistenceIOException;
import gov.va.med.imaging.storage.cache.impl.AbstractByteChannelFactory;
import gov.va.med.imaging.storage.cache.memento.ByteChannelFactoryMemento;

import java.io.File;
import java.io.IOException;
import java.util.zip.Checksum;

import javax.management.DynamicMBean;

import org.apache.log4j.Logger;

/**
 * @author VHAISWBECKEC
 *
 * A factory class that provides some management of instance file channels.
 * 
 */
public class FileSystemByteChannelFactory
extends AbstractByteChannelFactory<File>
implements InstanceByteChannelFactory<File>, DynamicMBean, CacheLifecycleListener
{
	public static final long defaultMaxChannelOpenDuration = 300000L;	// default to 5 minutes (for remote jukeboxes)
	public static final long defaultSweepTime = 10000L;
	public static final boolean defaultTraceChannelInstantiation = true;
	public static final String defaultChecksumAlgorithmName = "Adler32";
	
	private Logger log = Logger.getLogger(this.getClass());
	
	// ===================================================================================================================
	// Factory Methods
	// ===================================================================================================================
	public static FileSystemByteChannelFactory create()
	{
		return new FileSystemByteChannelFactory(defaultMaxChannelOpenDuration, defaultSweepTime, defaultChecksumAlgorithmName);
	}
	
	public static FileSystemByteChannelFactory create(ByteChannelFactoryMemento memento)
	{
		return new FileSystemByteChannelFactory(memento);
	}
	
	public static FileSystemByteChannelFactory create(Long maxChannelOpenDuration, Long sweepTime, String checksumClassname)
	{
		return new FileSystemByteChannelFactory(maxChannelOpenDuration, sweepTime, checksumClassname);
	}
	
	/**
	 * 
	 * @param memento
	 */
	private FileSystemByteChannelFactory(ByteChannelFactoryMemento memento)
	{
		this(
			memento == null ? defaultMaxChannelOpenDuration : memento.getMaxChannelOpenDuration(), 
			memento == null ? defaultSweepTime : memento.getSweepTime(),
			memento == null ? null : memento.getChecksumAlgorithmName()
		);
	}
	
	/**
	 * 
	 * @param maxChannelOpenDuration - the maximum time a channel is allowed to be open
	 * @param sweepTime - the delay in the background thread that looks for open channels
	 */
	private FileSystemByteChannelFactory(Long maxChannelOpenDuration, Long sweepTime, String checksumClassname)
	{
		super(maxChannelOpenDuration, sweepTime, checksumClassname);
	}


	// ==================================================================================================
	// InstanceByteChannelFactory Implementation
	// Business Methods
	// ==================================================================================================
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.filesystem.InstanceByteChannelFactory#getInstanceReadableByteChannel(java.io.File)
	 */
	public InstanceReadableByteChannel getInstanceReadableByteChannel(File instanceFile, InstanceByteChannelListener timeoutListener)
	throws PersistenceIOException, CacheException
	{
		try
		{
			Checksum checksum = null;
			try
			{
				checksum = ChecksumFactory.getFactory().get(getChecksumClassname());
			} 
			catch(Exception x)
			{
				log.error("Unable to create checksum instance of class '" + getChecksumClassname() + "'.");
				checksum = null;
			}
			InstanceReadableByteChannelImpl readable = new InstanceReadableByteChannelImpl(this, instanceFile, checksum);
			if(timeoutListener != null)
				this.putReadableChannel(readable, timeoutListener);
				
			return readable;
		} 
		catch (IOException ioX)
		{
			throw new PersistenceIOException(ioX);
		}
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.filesystem.InstanceByteChannelFactory#getInstanceWritableByteChannel(java.io.File)
	 */
	public InstanceWritableByteChannel getInstanceWritableByteChannel(File instanceFile, InstanceByteChannelListener timeoutListener) 
	throws PersistenceIOException, CacheException
	{
		try
		{
			Checksum checksum = null;
			try
			{
				checksum = ChecksumFactory.getFactory().get(getChecksumClassname());
			} 
			catch(Exception x)
			{
				log.error("Unable to create checksum instance of class '" + getChecksumClassname() + "'.");
				checksum = null;
			}
			
			InstanceWritableByteChannelImpl writable = new InstanceWritableByteChannelImpl( this, instanceFile, checksum );

			if(timeoutListener != null)
				putWritableChannel(writable, timeoutListener);
			
			return writable;
		} 
		catch (IOException ioX)
		{
			throw new PersistenceIOException(ioX);
		}
	}
	
	protected InstanceReadableByteChannel getOpenReadableByteChannel(File instanceFile)
	{
		if(instanceFile == null)
			return null;
		
		synchronized(getOpenReadChannels())
		{
			for(InstanceReadableByteChannel channel: getOpenReadChannels().keySet() )
			{
				InstanceReadableByteChannelImpl fileChannel = (InstanceReadableByteChannelImpl)channel; 
				File channelFile = fileChannel.getFile();
				
				if(instanceFile.equals(channelFile))
					return channel;
			}
		}
		
		return null;
	}
	
	protected InstanceWritableByteChannel getOpenWritableByteChannel(File instanceFile)
	{
		if(instanceFile == null)
			return null;
		
		synchronized(getOpenWriteChannels())
		{
			for(InstanceWritableByteChannel channel: getOpenWriteChannels().keySet() )
			{
				InstanceWritableByteChannelImpl fileChannel = (InstanceWritableByteChannelImpl)channel; 
				File channelFile = fileChannel.getFile();
				
				if(instanceFile.equals(channelFile))
					return channel;
			}
		}
		
		return null;
	}
	
}
