package gov.va.med.imaging.storage.cache;

import gov.va.med.imaging.storage.cache.events.InstanceLifecycleListener;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;

/**
 * 
 * 
 */
public interface Instance
extends MutableNamedObject
{
	/**
	 * Get the name of this Instance.  
	 * The name must be unique within the parent Group.
	 */
	@Override
	public String getName();
	
	/**
	 * Return the media type of the cache item on a "best-effort"
	 * basis.  This method may return if the type is not known or
	 * if the resources required to get the media type are prohibitively
	 * expensive (i.e. don't do a data file open/read to get the media type)
	 * 
	 * @return
	 */
	public String getMediaType();
	
	/**
	 * Get a readable byte channel, suitable for reading the contents of this
	 * Instance.  Wait for a read channel if the write channel is currently 
	 * open.
	 * 
	 * @return
	 * @throws CacheException
	 */
	public InstanceReadableByteChannel getReadableChannel() 
	throws CacheException;

	/**
	 * Get a readable byte channel, suitable for reading the contents of this
	 * Instance.  Do not wait for a read channel if the write channel is currently 
	 * open, return null immediately.
	 * 
	 * @return
	 * @throws CacheException
	 */
	public InstanceReadableByteChannel getReadableChannelNoWait() 
	throws CacheException;
	
	/**
	 * Get a writable byte channel, suitable for writing the contents of this
	 * Instance.  Wait for the write channel if any read channels are currently 
	 * open.  Throw a SimultaneousWriteException if the write channel is open.
	 * 
	 * @return
	 * @throws CacheException
	 */
	public InstanceWritableByteChannel getWritableChannel()
	throws CacheException;
	
	/**
	 * Get a writable byte channel, suitable for writing the contents of this
	 * Instance.  Do not wait for the write channel if any read channels, return 
	 * null immediately.
	 * 
	 * @return
	 * @throws CacheException
	 */
	public InstanceWritableByteChannel getWritableChannelNoWait()
	throws CacheException;
	
	/**
	 * Get the last date that this Instance was accessed
	 * @return
	 * @throws CacheException
	 */
	public java.util.Date getLastAccessed()
	throws CacheException;
	
	/**
	 * Get the size of this Instance.
	 * @return
	 * @throws CacheException
	 */
	public long getSize()
	throws CacheException;
	
	/**
	 * If true then a persistent copy of this Instance exists
	 * @return
	 * @throws CacheException
	 */
	public boolean isPersistent()
	throws CacheException;
	
	/**
	 * Get the calculated checksum value with the calculation in a String form.
	 * The Strting format should be as follows:
	 * {<algorithm>}<value>
	 * where:
	 * <algorithm> is the name of the checksum algorithm used (Adler32 or CRC32)
	 * <value> is the checksum value as a decimal integer
	 * e.g.
	 * {Adler32}655321
	 * 
	 * @return
	 */
	public String getChecksumValue();
	
	// ======================================================================================================
	// Listener Management
	// ======================================================================================================
	public abstract void registerListener(InstanceLifecycleListener listener);
	public abstract void unregisterListener(InstanceLifecycleListener listener);
	
}
