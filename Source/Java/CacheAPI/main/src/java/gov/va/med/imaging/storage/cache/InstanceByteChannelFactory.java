package gov.va.med.imaging.storage.cache;

import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import gov.va.med.imaging.storage.cache.exceptions.PersistenceIOException;
import gov.va.med.imaging.storage.cache.memento.ByteChannelFactoryMemento;

/**
 * The interface for the byte channel factory.  The factory is responsible for creating
 * readable and writable byte channels and then monitoring their usage.  If the channels are
 * not used for a defined period then the factory must notify the registered listener.
 *  
 * @author VHAISWBECKEC
 *
 */
public interface InstanceByteChannelFactory<T>
{
	// ========================================================================================================================================
	// Business Methods
	// ========================================================================================================================================
	
	/**
	 * 
	 * @param instanceIdentifier
	 * @param timeoutListener
	 * @return
	 * @throws PersistenceIOException
	 * @throws CacheException
	 */
	public abstract InstanceReadableByteChannel getInstanceReadableByteChannel(T instanceIdentifier, InstanceByteChannelListener timeoutListener) 
	throws PersistenceIOException, CacheException;

	/**
	 * 
	 * @param instanceIdentifier
	 * @param timeoutListener
	 * @return
	 * @throws PersistenceIOException
	 * @throws CacheException
	 */
	public abstract InstanceWritableByteChannel getInstanceWritableByteChannel(T instanceIdentifier, InstanceByteChannelListener timeoutListener) 
	throws PersistenceIOException, CacheException;

	// ========================================================================================================================================
	// Behavioral Modification Methods
	// ========================================================================================================================================
	public abstract long getMaxChannelOpenDuration();
	public abstract void setMaxChannelOpenDuration(long max);
	
	public abstract long getSweepTime();
	public abstract void setSweepTime(long sweep);
	
	/**
	 * If traceChannelInstantiation is set then the factory will record
	 * the stack trace when a channel is instantiated and report the stack
	 * trace when the channel is closed due to a timeout. 
	 * 
	 * @return
	 */
	public abstract boolean isTraceChannelInstantiation();
	public void setTraceChannelInstantiation(boolean traceChannelInstantiation);
	
	// ========================================================================================================================================
	// Statistics Gathering Methods
	// ========================================================================================================================================
	public abstract int getCurrentlyOpenReadableByteChannels();
	public abstract int getCurrentlyOpenWritableByteChannels();

	// ========================================================================================================================================
	// State persistence and restoration methods
	// ========================================================================================================================================
	public abstract ByteChannelFactoryMemento createMemento();
	public abstract void restoreMemento(ByteChannelFactoryMemento memento);
	
}