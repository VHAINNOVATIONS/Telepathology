
package gov.va.med.imaging.storage.cache.impl;

import gov.va.med.imaging.StackTraceAnalyzer;
import gov.va.med.imaging.channels.ChecksumValue;
import gov.va.med.imaging.storage.cache.Instance;
import gov.va.med.imaging.storage.cache.InstanceByteChannelFactory;
import gov.va.med.imaging.storage.cache.InstanceByteChannelListener;
import gov.va.med.imaging.storage.cache.InstanceReadableByteChannel;
import gov.va.med.imaging.storage.cache.InstanceWritableByteChannel;
import gov.va.med.imaging.storage.cache.events.InstanceLifecycleEvent;
import gov.va.med.imaging.storage.cache.events.InstanceLifecycleListener;
import gov.va.med.imaging.storage.cache.events.LifecycleEvent;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import gov.va.med.imaging.storage.cache.exceptions.InstanceInaccessibleException;
import gov.va.med.imaging.storage.cache.exceptions.InstanceInitializationException;
import gov.va.med.imaging.storage.cache.exceptions.InstanceUnavailableException;
import gov.va.med.imaging.storage.cache.exceptions.PersistenceException;
import gov.va.med.imaging.storage.cache.exceptions.PersistenceIOException;
import gov.va.med.imaging.storage.cache.exceptions.SimultaneousWriteException;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.Checksum;

import org.apache.log4j.Logger;

/**
 * An abstract super-class over all persistent Instance implementations.
 * This class provides generic functionality that is independent of the
 * persistence mechanism. 
 * In particular this class manages the synchronization of multiple
 * threads accessing a single Instance.
 * This class allows 
 * 1.) zero-or-one writable channels open on it at one time
 * 2.) zero-to-many readable channels open at one time
 * 3.) zero readable channels if the writable channel exists
 * 4.) zero writable channels if a readable channel exists
 * 
 * If the writable channel is open then opening a readable channel will wait 
 * a limited time for the writable channel to close.
 */
public abstract class PersistentInstance 
implements Instance, Comparable<Instance>, InstanceByteChannelListener
{
	public final static int defaultsecondsReadWaitsForWriteCompletion = 60;			// in seconds !
	public final static int defaultSecondsWriteWaitsForReadCompletion = 60;			// in seconds !
	public final static int defaultSecondsRemoveWaitsForWriteCompletion = 5;		// in seconds !
	public final static int defaultSecondsRemoveWaitsForReadCompletion = 5;		// in seconds !
	
	public final static boolean defaultSetModificationTimeOnRead = true;
	
	protected Logger log = Logger.getLogger(this.getClass());
	
	private InstanceByteChannelFactory instanceChannelFactory = null;
	private boolean setModificationTimeOnRead = defaultSetModificationTimeOnRead ;
	private int secondsReadWaitsForWriteCompletion = defaultsecondsReadWaitsForWriteCompletion;
	private int secondsWriteWaitsForReadCompletion = defaultSecondsWriteWaitsForReadCompletion;
	
	// There is a bit of trickiness in the checksum handling
	// so that they value is loaded from persistent storage only when it is 
	// needed.  If checksumLoaded is false then we MAY need to load it from persistent
	// storage.
	private boolean checksumLoaded = false;
	private ChecksumValue checksumValue = null;

	protected abstract boolean persistentChecksumExists()
	throws IOException;
	
	protected abstract InputStream openChecksumInputStream()
	throws IOException;
	
	protected abstract OutputStream openChecksumOutputStream()
	throws IOException;

	protected abstract void removeChecksumPersistent() 
	throws PersistenceIOException;

	// by default an instance is always valid 
	// it becomes invalid if it fails to be written completely after which it should cease to exist
	// BUT, it takes some time to clean up after the failed write, that is the time frame that the
	// valid flag is false
	private boolean valid = true;
	
	// channelSemaphore is a semaphore that protects against opening a channel while
	// another thread is in the process of acquiring or closing a channel.
	// The existence of writableChannel and the size of readableChannels acts as a 
	// semaphore to determine whether a channel may be created.
	// The channelSemaphore just protects against simultaneous changes to readableChannels
	// and writableChannel.
	// NOTE: while this operates similarly to a ReadWriteLock, that is not applicable because 
	// the Thread closing a channel may not be the same Thread that opened it.  Specifically,
	// a read or write timeout (determined by a monitor thread) may close the channel.
	private Object channelSemaphore = new Object();

	// Set to true when a thread is trying to acquire a read channel.
	// Prohibits new read channels from being created until the writing
	// thread acquires the write channel (after which the read channels 
	// have to wait for writing thread to close the write channel
	// This value must not be set outside of a channelSemaphore synchronized
	// block.
	private boolean delayReadChannelRequests = false;

	// each Instance may have one writable channel open at a time
	// This value must not be set outside of a channelSemaphore synchronized
	// block.
	private InstanceWritableByteChannel writableChannel = null;
	
	// an Instance may have multiple readable channels open simultaneously
	// This value must not be set outside of a channelSemaphore synchronized
	// block.
	private Set<InstanceReadableByteChannel> readableChannels = new HashSet<InstanceReadableByteChannel>();
	
	// =============================================================================================================
	
	/**
	 * 
	 * @param instanceFile
	 * @param createIfNotExist
	 * @param instanceFactoryChannel
	 * @throws CacheException
	 */
	protected PersistentInstance(
		InstanceByteChannelFactory instanceFactoryChannel,
		int secondsReadWaitsForWriteCompletion,
		boolean setModificationTimeOnRead
	)
	throws CacheException
	{
		this.instanceChannelFactory = instanceFactoryChannel;
		this.secondsReadWaitsForWriteCompletion = secondsReadWaitsForWriteCompletion;
		this.setModificationTimeOnRead = setModificationTimeOnRead;
	}

	// =============================================================================================================
	public InstanceByteChannelFactory getInstanceChannelFactory()
	{
		return this.instanceChannelFactory;
	}

	public int getSecondsWriteWaitsForReadCompletion()
	{
		return this.secondsWriteWaitsForReadCompletion;
	}
	
	public int getSecondsReadWaitsForWriteCompletion()
	{
		return this.secondsReadWaitsForWriteCompletion;
	}

	public boolean isSetModificationTimeOnRead()
	{
		return this.setModificationTimeOnRead;
	}
	
	public boolean isValid()
	{
		return valid;
	}
	
	protected void setValid(boolean valid)
	{
		this.valid = valid;
	}

	/**
	 * Return a String in the standardized checksum format.
	 * This method may return null if the checksum was not calculated or
	 * if the instance closed with an error.
	 */
	public String getChecksumValue()
	{
		String value = null;
		
		// special case that allows callers to get a checksum
		// while it is writing ... well usually after it is
		// written but before it is closed.
		if(writableChannel != null && writableChannel.isOpen())
		{
			Checksum checksum = writableChannel.getChecksum();
			if(checksum != null)
			{
				ChecksumValue cv = new ChecksumValue(checksum);
				value = cv.toString();
			}
		}
		else
		{
			if(! isChecksumLoaded())
			{
				this.checksumValue = loadChecksum();
				setChecksumLoaded();
			}
			if( this.checksumValue != null)
				value = this.checksumValue.toString();
		}
		return value;
	}
	
	private void setChecksum(ChecksumValue cv)
	{
		this.checksumValue = cv;
		if(!isChecksumLoaded())
		{
			storeChecksum(cv);
			setChecksumLoaded();
		}
	}
	
	private boolean isChecksumLoaded()
	{
		return checksumLoaded;
	}
	private void setChecksumLoaded()
	{
		this.checksumLoaded = true;
	}

	// =============================================================================================================
	// The abstract methods that must be implemented to bind this to the persistence system
	// =============================================================================================================
	/**
	 * Creates a readable byte channel on the persistent storage.  Implementations should create
	 * the channel assuming that all checking for concurrent modification has been done.
	 * 
	 * @return
	 * @throws IOException
	 * @throws CacheException
	 */
	protected abstract InstanceReadableByteChannel createInstanceReadableByteChannel() 
	throws PersistenceIOException, CacheException;

	/**
	 * Creates a writable byte channel on the persistent storage.  Implementations should create
	 * the channel assuming that all checking for concurrent modification has been done.
	 * 
	 * @return
	 * @throws IOException
	 * @throws CacheException
	 */
	protected abstract InstanceWritableByteChannel createInstanceWritableByteChannel() 
	throws PersistenceIOException, CacheException;

	/**
	 * Indicates if the Instance has a persistent representation.
	 * 
	 * @return
	 * @throws PersistenceException 
	 */
	public abstract boolean isPersistent() 
	throws PersistenceException;
	
	/**
	 * Creates a persistent representation of the Instance of none exists.
	 * 
	 * @throws IOException
	 */
	protected abstract void createPersistent() 
	throws PersistenceIOException;
	
	/**
	 * Set the last modified time of the persistent representation to the given date.
	 * 
	 * @param date
	 */
	protected abstract void setLastModified(long date)
	throws PersistenceIOException;

	/**
	 * Remove the persistent representation of the Instance
	 * This method must either succeed or throw an exception.
	 */
	protected abstract void removePersistent()
	throws PersistenceIOException;
	
	// =================================================================================================================================
	public InstanceWritableByteChannel getWritableChannel()
	throws CacheException
	{
		return getWritableChannel(true);
	}
	
	public InstanceWritableByteChannel getWritableChannelNoWait()
	throws CacheException
	{
		return getWritableChannel(false);
	}
	
	/**
	 * Get a writable channel if none exists and if no readable channels exist, 
	 * else throw an exception.
	 * If the wait parameter is true then wait for the writable readable channels
	 * to close, else return null immediately.
	 */
	private InstanceWritableByteChannel getWritableChannel(boolean wait)
	throws CacheException
	{
		log.debug("getWritableChannel() - acquiring channelSemaphore");
		
		// protect against changes to the channels while we're creating our channel
		synchronized(channelSemaphore)
		{
			log.debug("getWritableChannel() - acquired channelSemaphore");
			
			// set delayReadChannelRequests to true so that requests for new
			// read channels will be held until the write channel has been
			// acquired (after which the read channels will be held waiting for
			// the write channel to close
			delayReadChannelRequests = true;
			log.debug("getWritableChannel() - delayReadChannelRequests set");

			try
			{
				// wait for all readable channels to complete
				if(wait)
					waitForReadableChannels();
	
				// we've waited for any writable channel to close, if we get here it should be null
				// if not then throw an exception
				if(writableChannel != null)
				{
					if(wait)
					{
						log.debug("getWritableChannel() - writable channel already exists and write did not complete in alloted time");
						throw new SimultaneousWriteException(this.getName());
					}
					else
					{
						log.debug("getWritableChannel() - writable channel exists and this call is NOT waiting, returning null");
						return null;
					}
				}
				
				// if there are currently open readable channels then throw an exception
				if(readableChannels.size() > 0)
				{
					if(wait)
					{
						log.debug("getWritableChannel() - readable channels exist after wait, instance inaccessible as long as they exist");
						throw new InstanceInaccessibleException(this.getName());
					}
					else
					{
						log.debug("getWritableChannel(wait = false) - readable channels exist, instance inaccessible as long as they exist, returning null");
						return null;
					}
				}
				
				// if the file does not exist then create it
				if(! isPersistent())
				{
					log.debug("FileSystemCacheInstance" + this.hashCode() + " instance (" + getName() + ") does not exist, creating ...");
					try
					{
						createPersistent();
					} 
					catch (PersistenceIOException e)
					{
						// if we cannot create the persistent instance we cannot go on
						log.error(e);
						throw new InstanceInitializationException(getName());
					}
					log.debug("FileSystemCacheInstance" + this.hashCode() + " instance (" + getName() + ") created.");
				}
				
				// as of here we know that the file exists and that no one else is writing to
				// it or reading from it, we can safely create the writable channel
				try
				{
					log.debug("FileSystemCacheInstance" + this.hashCode() + " instance (" + getName() + ") acquiring writable channel...");
					writableChannel = createInstanceWritableByteChannel();
					log.debug("FileSystemCacheInstance" + this.hashCode() + " instance (" + getName() + ") acquired writable channel.");
					
					//setLastModified( System.currentTimeMillis() );
					log.debug("FileSystemCacheInstance" + this.hashCode() + " instance (" + getName() + ") last modified date set.");
				} 
				catch (PersistenceIOException e)
				{
					if(writableChannel != null)
						try{writableChannel.error();}
						catch(Exception x){}
					writableChannel = null;
					log.error(e);
					throw new InstanceInaccessibleException(e);
				}
			} 
			// this is real useful for debug but must allow exceptions to pass through for real
			//catch(Throwable x)
			//{
			//	x.printStackTrace();
			//}
			finally
			{
				// make absolutely sure that we reset this, else
				// no one will be able to read the instance
				delayReadChannelRequests = false;
				log.debug("getWritableChannel() - delayReadChannelRequests reset");
				
				// notify any threads that may be waiting for the channel semaphore 
				channelSemaphore.notifyAll();
			}
			
			log.debug("getWritableChannel() - releasing channelSemaphore");
		}
		return writableChannel;
	}

	/**
	 * Get a readable channel if no writable byte channel exists, 
	 * else wait for the write to complete (or timeout)
	 * if timed out then throw an exception
	 */
	public InstanceReadableByteChannel getReadableChannel()
	throws CacheException
	{
		return getReadableChannel(true);
	}
	
	/**
	 * Get a readable channel if no writable byte channel exists, 
	 * else return null.
	 */
	public InstanceReadableByteChannel getReadableChannelNoWait()
	throws CacheException
	{
		return getReadableChannel(false);
	}
	
	private InstanceReadableByteChannel getReadableChannel(boolean wait)
	throws CacheException
	{
		InstanceReadableByteChannel result = null;
		// first, block any changes to the write channel or readChannels
		// from other threads
		log.debug("getReadableChannel() - acquiring channelSemaphore for '" + this.getName() + "'");
		synchronized(channelSemaphore)
		{
			// this is a quick check for persistence and validity but it is NOT a reliable check
			// because the write channel may be in process of writing, which may fail, which may mark the Instance as
			// invalid and remove the file.
			if( ! isValid() )
			{
				log.warn("Instance is not valid, operation should be retried for instance '" + this.getName() + "'");
				throw new InstanceInaccessibleException("Instance is marked invalid (write failed).  Wait and try again.");
			}
			
			log.debug("getReadableChannel() - acquired channelSemaphore for instance '" + this.getName() + "'");
			
			// wait for:
			// 1.) the writable channel to open if such a request has been made  
			// 2.) an open writable channel to close
			long delay = 0L;
			if(wait)
				delay = waitForWritableChannel();
			
			// if the writable channel is not null (i.e. it exists) then we can't do a read and we've already
			// waited for the defined time.  Error out and let the client decide if it wants to retry.
			if(writableChannel != null)
			{
				if(wait)
				{
					log.debug("getReadableChannel(wait = true) - waited " + delay + " milliseconds, but writable channel still exists, instance '" + this.getName() + "' is inaccessible.");
					throw new InstanceInaccessibleException( getName(), delay, getSecondsReadWaitsForWriteCompletion() );
				}
				else
				{
					log.debug("getReadableChannel(wait = false), writable channel exists, instance '" + this.getName() + "' is inaccessible.");
					return null;
				}
			}
			// as of here we know that the write channel does not exist and it can't be created until
			// we release the write channel semaphore (i.e. we can count on its state or non-existence).
			
			// if the instance state has become invalid (write failed) or if the file doesn't exist then error out.
			// NOTE: this check MUST be done after we've waited for the write, else it may not exist
			// because it is just now being written to.
			if(!isPersistent())
			{
				log.warn("getReadableChannel() - persistent copy of Instance '" + this.getName() + "' does not exist.");
				log.warn( StackTraceAnalyzer.currentStackAnalyzer().toString() );
				throw new InstanceUnavailableException(getName(), false);
			}
			if(!isValid())
			{
				log.warn("getReadableChannel() - persistent copy of Instance '" + this.getName() + "' does not exist for instance.");
				log.warn( StackTraceAnalyzer.currentStackAnalyzer().toString() );
				throw new InstanceUnavailableException(getName(), true);
			}
			
			try
			{
				result = createInstanceReadableByteChannel();
				readableChannels.add(result);
				if(isSetModificationTimeOnRead())
					setLastModified(System.currentTimeMillis());
			} 
			catch (PersistenceIOException e)
			{
				String message = "Unable to create readable byte channel on instance '" + getName() + "'." + "Exception is :" + e.getMessage();
				// occassionally we'll get an IOException (Access is denied) when another thread has just notifies us and we were waiting
				// for a read.  It seems that the file does not get genuinely closed, locks released, etc. until sometime after the code that
				// should do so
				log.error(message);
				throw new InstanceInaccessibleException(message);
			}
			
			log.debug("getReadableChannel() - releasing channelSemaphore for instance '" + this.getName() + "'");
			
			// notify any threads that may be waiting for the channel semaphore 
			channelSemaphore.notifyAll();
		}  // release the write channel semaphore here, we've safely added ourselves to the list of readable
		   // channels so the write channel cannot write while this channel is open
		
		return result;
	}
	
	/**
	 * The channelSemaphore lock MUST be acquired before calling this
	 * method (i.e. synchronized(channelSemaphore) )
	 * 
	 * @throws InstanceInaccessibleException
	 */
	private long waitForWritableChannel() 
	throws InstanceInaccessibleException, IllegalMonitorStateException
	{
		// check the status of the write channel
		long startWait = System.currentTimeMillis();
		long now = startWait;
		
		// if the writable channel is open then
		// if secondsReadWaitsForWriteCompletion
		// > 0 then wait that many seconds for the write to complete
		// <= 0 do not wait for the write to complete
		while(
				(writableChannel != null || delayReadChannelRequests) &&
				(getSecondsReadWaitsForWriteCompletion() > 0 &&
				 now < (startWait + (getSecondsReadWaitsForWriteCompletion() * 1000)) ) 
		)
		{
			// if the writable channel exists then we wait for
			// some defined period for it to be released.  A timeout
			// means the instance is in the cache but not currently accessible.
			try
			{
				log.debug("getReadableChannel() - waiting for channelSemaphore for instance '" + this.getName() + "'");
				// the wait() releases the channelSemaphore, hence no deadlock
				channelSemaphore.wait(getSecondsReadWaitsForWriteCompletion() * 1000);
			}
			catch (InterruptedException iX)
			{
				String message = "Interrupted when waiting for read lock for Instance '" + getName() + "'";
				log.warn(message);
				throw new InstanceInaccessibleException(message, iX);
			}
			
			now = System.currentTimeMillis();
		}
		
		return now - startWait;
	}
	
	/**
	 * The channelSemaphore lock MUST be acquired before calling this
	 * method (i.e. synchronized(channelSemaphore) )
	 * 
	 * Returns the total time waited.
	 * 
	 * @throws InstanceInaccessibleException
	 */
	private long waitForReadableChannels() 
	throws InstanceInaccessibleException, IllegalMonitorStateException
	{
		// check the status of the write channel
		long startWait = System.currentTimeMillis();
		long now = startWait;
		
		// if any readable channels are open then
		// if secondsReadWaitsForWriteCompletion
		// > 0 then wait that many seconds for the write to complete
		// <= 0 do not wait for the write to complete
		while(
				readableChannels.size() > 0 &&
				(getSecondsWriteWaitsForReadCompletion() > 0 &&
				 now < (startWait + (getSecondsWriteWaitsForReadCompletion() * 1000)) ) 
		)
		{
			// if readable channels exist then we wait for
			// some defined period for it to be released.  A timeout
			// means the instance is in the cache but not currently accessible.
			try
			{
				log.debug("waitForReadableChannels() - waiting for channelSemaphore for instance '" + this.getName() + "'");
				// the wait() releases the channelSemaphore, hence no deadlock
				channelSemaphore.wait(getSecondsWriteWaitsForReadCompletion() * 1000);
			}
			catch (InterruptedException iX)
			{
				String message = "Interrupted when waiting for write lock for Instance '" + getName() + "'";
				log.warn(message);
				throw new InstanceInaccessibleException(message, iX);
			}
			
			now = System.currentTimeMillis();
		}
		
		return now - startWait;
	}	
	
	@Override
	public void delete(boolean forceDelete) 
	throws SimultaneousWriteException, PersistenceIOException
	{
		if(forceDelete)
			forciblyDelete();
		else
			politeDelete();
	}
	
	/**
	 * Remove the persistent copy of the instance.
	 * If any channels are open then this will fail.
	 * This method will either succeed or will throw an exception.
	 * @throws SimultaneousWriteException 
	 * @throws PersistenceIOException 
	 */
	private void politeDelete() 
	throws SimultaneousWriteException, PersistenceIOException
	{
		log.debug("remove() - acquiring channelSemaphore for instance '" + this.getName() + "'");
		synchronized (channelSemaphore)
		{
			log.debug("remove() - acquired channelSemaphore for instance '" + this.getName() + "'");
			if(writableChannel != null)
			{
				String message = null;
				try 
				{
					waitForWritableChannel();
					if(writableChannel != null)
					{
						String testClients = getOpenChannelClients("[[a-zA-Z_0-9]+\\.]*Test[a-zA-Z_0-9]*");

						message = getName() 
							+ ": attempt to remove with " 
							+ readableChannels.size() 
							+ " open writable channel for instance '" 
							+ this.getName() 
							+ "'. Waited without success."
							+ (testClients != null ? testClients : "");
					}
				} 
				catch (InstanceInaccessibleException e) 
				{
					String testClients = getOpenChannelClients("[[a-zA-Z_0-9]+\\.]*Test[a-zA-Z_0-9]*");

					message = getName() 
						+ ": attempt to remove with an open writable channel for instance '" 
						+ this.getName() 
						+ "', interrupted while waiting. "
						+ (testClients != null ? testClients : "");
				}
				
				if(message != null)
				{
					log.debug(message);
					throw new SimultaneousWriteException(message);
				}
			}
			
			if(readableChannels.size() > 0)
			{
				String message = null;
				
				try 
				{
					waitForReadableChannels();
					
					if(readableChannels.size() > 0)
					{
						String testClients = getOpenChannelClients("[[a-zA-Z_0-9]+\\.]*Test[a-zA-Z_0-9]*");

						message = getName() 
							+ ": attempt to remove with " 
							+ readableChannels.size() 
							+ " open readable channels for instance '" 
							+ this.getName() 
							+ "'. Waited without success."
							+ (testClients != null ? testClients : "");
					}
				} 
				catch (InstanceInaccessibleException e) 
				{
					String testClients = getOpenChannelClients("[[a-zA-Z_0-9]+\\.]*Test[a-zA-Z_0-9]*");

					message = getName() 
						+ ": attempt to remove with " 
						+ readableChannels.size() 
						+ " open readable channels for instance '" 
						+ this.getName() 
						+ "', interrupted while waiting. "
						+ (testClients != null ? testClients : "");
				}

				if(message != null)
				{
					log.debug(message);
					throw new SimultaneousWriteException(message);
				}
			}
			try
			{
				removePersistent();
			} 
			catch (PersistenceIOException x)
			{
				log.error(x);
				throw x;
			}
			log.debug("remove() - releasing channelSemaphore");
			
			// notify any threads that may be waiting for the channel semaphore 
			channelSemaphore.notifyAll();
		}
		
		return;
	}
	
	/**
	 * If any channels are open then this will close them, forcing IOExceptions on other threads
	 * if they have open channels.
	 * @throws PersistenceIOException 
	 */
	private void forciblyDelete() 
	throws PersistenceIOException 
	{
		synchronized (channelSemaphore)
		{
			log.debug("forciblyDelete() - acquired channelSemaphore for instance '" + this.getName() + "'");
			if(writableChannel != null)
			{
				try 
				{
					log.debug("forciblyDelete() - closing writable channel for instance '" + this.getName() + "'");
					writableChannel.close();
				} 
				catch (IOException ioX) 
				{
					log.error(ioX);
					throw new PersistenceIOException(ioX);
				}
			}
			if(readableChannels.size() > 0)
			{
				for(InstanceReadableByteChannel readableChannel : readableChannels)
					try 
					{
						log.debug("forciblyDelete() - closing readable channel for instance '" + this.getName() + "'");
						readableChannel.close();
					} 
					catch (IOException ioX) 
					{
						log.error(ioX);
						throw new PersistenceIOException(ioX);
					}
			}
			
			try
			{
				log.debug("forciblyDelete() - removing persistence for instance '" + this.getName() + "'");
				removePersistent();
				log.debug("forciblyDelete() - removed persistence for instance '" + this.getName() + "'");
			} 
			catch (PersistenceIOException x)
			{
				log.error(x);
				throw x;
			}
			
			// notify any threads that may be waiting for the channel semaphore 
			channelSemaphore.notifyAll();
		}
	}
	
	/**
	 * String testClients = getOpenChannelClients("[[a-zA-Z_0-9]+\\.]*Test[a-zA-Z_0-9]*");
	 * 
	 * @param classNameRegex
	 * @return
	 */
	private String getOpenChannelClients(String classNameRegex)
	{
		StringBuilder sb = new StringBuilder();
		
		synchronized (channelSemaphore) 
		{
			StringBuilder readerSB = new StringBuilder();
			for( InstanceReadableByteChannel channel : readableChannels )
			{
				StackTraceElement[] stackTrace = channel.getInstantiatingStackTrace();
				StackTraceAnalyzer sta = new StackTraceAnalyzer( stackTrace );
				StackTraceElement testCaller = sta.getFirstElementInClassPattern("[[a-zA-Z_0-9]+\\.]*Test[a-zA-Z_0-9]*");
				if(readerSB.length() > 0)
					readerSB.append(',');
				readerSB.append( testCaller.getClassName() + "." + testCaller.getMethodName() );
			}
			if(readerSB.length() > 0)
			{
				sb.append("Readers: ");
				sb.append(readerSB);
			}
			
			if(writableChannel != null)
			{
				StackTraceElement[] stackTrace = writableChannel.getInstantiatingStackTrace();
				StackTraceAnalyzer sta = new StackTraceAnalyzer( stackTrace );
				StackTraceElement testCaller = sta.getFirstElementInClassPattern("[[a-zA-Z_0-9]+\\.]*Test[a-zA-Z_0-9]*");
				
				sb.append("Writer: ");
				sb.append(testCaller.getClassName() + "." + testCaller.getMethodName());
			}
			
			// notify any threads that may be waiting for the channel semaphore 
			channelSemaphore.notifyAll();
		}
		
		return sb.toString();
	}
	
	// ===========================================================================================================
	// InstanceByteChannelTimeoutListener
	// ===========================================================================================================
	
	/**
	 * Notification that a writable byte channel has been closed.  This is 
	 * the 'happy' path.  The listeners may allow read channels to open
	 * on the source (file).
	 * Calling this method is taken as an indication that the cache object is completely
	 * written and therefore the checksum is fully calculated (if it was caclculated at all).
	 *  
	 */
	public void writeChannelClose(InstanceWritableByteChannel closedWritableChannel)
	{
		log.debug("Instance '" + getName() + "' writable channel was closed");
		writableChannelClosed(closedWritableChannel, false);
	}
	
	/**
	 * Notification that a readable byte channel has been closed.  This is 
	 * the 'happy' path.
	 */
	public void readChannelClose(InstanceReadableByteChannel closedReadableChannel)
	{
		log.debug("Instance '" + getName() + "' readable channel was closed");
		readableChannelClosed(closedReadableChannel);
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.filesystem.InstanceByteChannelTimeoutListener#writeChannelIdleTimeout(gov.va.med.imaging.storage.cache.InstanceWritableByteChannel)
	 */
	public void writeChannelIdleTimeout(InstanceWritableByteChannel closedWritableChannel)
	{
		log.warn("Writable channel inactivity on Instance '" +  getName() +"'.");
		writableChannelClosed(closedWritableChannel, true);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.filesystem.InstanceByteChannelTimeoutListener#readChannelIdleTimeout(gov.va.med.imaging.storage.cache.InstanceReadableByteChannel)
	 */
	public void readChannelIdleTimeout(InstanceReadableByteChannel closedReadableChannel)
	{
		log.warn("Readable channel inactivity on Instance '" +  getName() +"'.");
		readableChannelClosed(closedReadableChannel);
	}
	
	/**
	 * 
	 * @param closedWritableChannel
	 * @param errorClose - if true then the channel was closed with an error and:
	 *   the file will not exist
	 *   the content of the file would be suspect if it did exist (which is why we delete it).
	 * in any case, if the write closed with an error then this instance should cease to exist
	 */
	private void writableChannelClosed(InstanceWritableByteChannel closedWritableChannel, boolean errorClose)
	{
		// We must acquire and hold the channelSemaphore here because there may be
		// readable channels waiting for the write to complete.  When we release the semaphore
		// then the readable channels will check to see if they can read the file.
		log.debug("writableChannelClosed() - acquiring channelSemaphore");
		synchronized(channelSemaphore)
		{
			log.debug("writableChannelClosed() - acquired channelSemaphore");
			
			// is this really our writable channel
			// and did we previously think it was open
			if( closedWritableChannel != null && closedWritableChannel.equals(writableChannel) )
			{
				// the instance is valid if the close is not an error close
				// else the instance is invalid and reads should fail
				// note that a write may be retried if the instance is invalid
				setValid(!errorClose);
				
				// save our checksum if this is a valid Instance
				if(isValid())
				{
					// Sanity check, the file should exist at this time, if not we can't calc a checksum.
					// Actually we got bigger problems than that, so log a big error
					try
					{
						if( isPersistent() )
						{
							Checksum checksum = closedWritableChannel.getChecksum();
							if(checksum != null)
							{
								ChecksumValue cv = new ChecksumValue(checksum.getClass().getSimpleName(), checksum.getValue());
								log.info("Instance '" + this.getName() + "' checksum value is '" + cv.toString() + "'");
								setChecksum(cv);
							}
						}
						else
							log.error("Instance '" + getName() + "' has been closed without error but is NOT PERSISTENT.");
					} 
					catch (PersistenceException x)
					{
						log.error("Instance '" + getName() + "' has been closed without error but failed when determining filesystem persistence.");
					}
				}
				
				writableChannel = null;
				channelSemaphore.notifyAll();		// notify anyone waiting for the channel semaphore
													// this should just be threads waiting to open a readable channel
				log.debug("writableChannelClosed(), instance is " + (isValid() ? "" : "NOT") + " valid - notified all channelSemaphore waiters ");
			}
			else
			{
				log.error(getName() + " was informed of a writable channel closure but it was not our channel.  This is a serious error and should never be seen.");
			}
			
			log.debug("writableChannelClosed() - releasing channelSemaphore");
		}
	}

	private void readableChannelClosed(InstanceReadableByteChannel closedReadableChannel)
	{
		log.debug("readableChannelClosed() - acquiring channelSemaphore");
		synchronized(channelSemaphore)
		{
			log.debug("readableChannelClosed() - acquired channelSemaphore and removing read channel from open list.");
			if( ! readableChannels.remove(closedReadableChannel) )
				log.warn(getName() + " was informed of a readable channel closure but it was not our channel.  This is an error and should never be seen.");
			log.debug("readableChannelClosed() - releasing channelSemaphore after removing read channel from open list.");
			
			channelSemaphore.notifyAll();		// notify anyone waiting for the channel semaphore, this probably not necessary
												// because the threads waiting will timeout and wakeup anyway but this will be 
												// a might quicker.
		}
	}
	
	
	// ===============================================================================================================
	// Checksum persistence handling
	// ===============================================================================================================
	/**
	 * 
	 */
	protected ChecksumValue loadChecksum()
	{
		ChecksumValue result = null;
		
		log.debug("Loading checksum for instance '" + this.getName() + "'.");
		ObjectInputStream objectIn = null;
		
		try
		{
			InputStream inStream = openChecksumInputStream();
			if(inStream != null)
			{
				objectIn = new ObjectInputStream(inStream);
				try 
				{
					result = (ChecksumValue)objectIn.readObject();
					log.debug("Checksum restored for instance '" + this.getName() + "'.");
				} 
				// If the checksum class has changed since it was written
				// then delete the checksum file and return null
				catch (IncompatibleClassChangeError iccError) 
				{
					removeChecksumPersistent();
					log.debug("Checksum written as incompatible class  '" + this.getName() + "', deleting.");
				}
			}
			else
				log.debug("Checksum does not exist for instance '" + this.getName() + "'.");
		} 
		catch (Throwable x)
		{
			log.error(x);
			x.printStackTrace();
		}
		finally
		{
			try{objectIn.close();}
			catch(Throwable t){}
		}
		
		return result;
	}
	
	/**
	 * 
	 *
	 */
	private void storeChecksum(ChecksumValue checksum)
	{
		if(checksum != null)
		{
			log.debug("Storing checksum for instance '" + this.getName() + "'.");
			ObjectOutputStream objectOut = null;
			
			try
			{
				OutputStream outStream = openChecksumOutputStream();
				if(outStream != null)
				{
					objectOut = new ObjectOutputStream(outStream);
						
					objectOut.writeObject(checksum);
				}
			} 
			catch (Throwable x)
			{
				log.error(x);
				x.printStackTrace();
			}
			finally
			{
				try{objectOut.close();}
				catch(Throwable t){}
			}
		}
	}
	
	// ======================================================================================================
	// Listener Management
	// ======================================================================================================
	private List<InstanceLifecycleListener> listeners = new ArrayList<InstanceLifecycleListener>();
	public void registerListener(InstanceLifecycleListener listener)
	{
		listeners.add(listener);
	}
	
	public void unregisterListener(InstanceLifecycleListener listener)
	{
		listeners.remove(listener);
	}
	
	protected void notifyListeners(LifecycleEvent event)
	{
		InstanceLifecycleEvent lifecycleEvent = new InstanceLifecycleEvent(event, getName());
		for(InstanceLifecycleListener listener : listeners)
			listener.notify(lifecycleEvent);
	}
}
