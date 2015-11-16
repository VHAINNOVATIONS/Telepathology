/**
 * 
 */
package gov.va.med.imaging.router.commands;

import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.channels.ByteStreamPump;
import gov.va.med.imaging.channels.CompositeIOException;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNearLineException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNotFoundException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.storage.cache.ImmutableInstance;
import gov.va.med.imaging.router.facade.ImagingContext;
import gov.va.med.imaging.storage.cache.InstanceWritableByteChannel;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import gov.va.med.imaging.storage.cache.exceptions.InstanceInaccessibleException;
import gov.va.med.imaging.storage.cache.exceptions.SimultaneousWriteException;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;

/**
 * @author vhaiswbeckec
 *
 */
public class GetPatientIdentificationImageCommandImpl 
extends AbstractImagingCommandImpl<InputStream> 
{
	private static final long serialVersionUID = 1797497988357747778L;
	private final PatientIdentifier patientIdentifier;
	private final RoutingToken routingToken;
	
	/**
	 * @param commandContext
	 */
	public GetPatientIdentificationImageCommandImpl(
		PatientIdentifier patientIdentifier, 
		RoutingToken routingToken)
	{
		super();
		this.patientIdentifier = patientIdentifier;
		this.routingToken = routingToken;
	}

	public PatientIdentifier getPatientIdentifier()
	{
		return patientIdentifier;
	}

	public RoutingToken getRoutingToken()
	{
		return this.routingToken;
	}

	public String getSiteNumber()
	{
		return getRoutingToken().getRepositoryUniqueId();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#parameterToString()
	 */
	@Override
	protected String parameterToString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(getPatientIdentifier());
		sb.append(", ");
		sb.append(getSiteNumber());
		
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.patientIdentifier == null) ? 0 : this.patientIdentifier.hashCode());
		result = prime * result
				+ ((this.routingToken == null) ? 0 : this.routingToken.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		final GetPatientIdentificationImageCommandImpl other = (GetPatientIdentificationImageCommandImpl) obj;
		if (this.patientIdentifier == null)
		{
			if (other.patientIdentifier != null)
				return false;
		} else if (!this.patientIdentifier.equals(other.patientIdentifier))
			return false;
		if (this.routingToken == null)
		{
			if (other.routingToken != null)
				return false;
		} else if (!this.routingToken.equals(other.routingToken))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#callSynchronouslyInTransactionContext()
	 */
	@Override
	public InputStream callSynchronouslyInTransactionContext()
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();

		getLogger().info( "getPatientIdentificationImage - Transaction ID [" + transactionContext.getTransactionId() + "] from site [" + routingToken.toString() + "] for patient [" + patientIdentifier + "].");
		

		transactionContext.setServicedSource(routingToken.toRoutingTokenString());
		// if caching is enabled we will try to use the cache
		// cacheThisInstance indicates both that we write to and read from the cache for this instance
		boolean cacheThisInstance = patientIdentifier != null  &&  routingToken.getRepositoryUniqueId() != null  && getCommandContext().isCachingEnabled();

		// if the Image URN was successfully parsed and caching is enabled
		// try to retrieve the instance from the cache
		if( cacheThisInstance ) 
		{
			getLogger().info("Patient ID image '" + patientIdentifier + "' from site '" + routingToken.toString() + "' caching enabled.");
			try
			{
				InputStream cacheStream =
						CommonImageCacheFunctions.streamPatientPhotoImageFromCache(getCommandContext(), routingToken.getRepositoryUniqueId(), patientIdentifier);
					if(cacheStream != null)
					{
						transactionContext.setItemCached(Boolean.TRUE);
						getLogger().info("Patient ID image '" + patientIdentifier + "' from site '" + routingToken.toString() + "' found in the cache and returned stream.");					
	
						return cacheStream;
						// new ImageMetadata(imageUrn, response.imageFormat, null, response.bytesReturnedFromDataSource, response.bytesReturnedFromDataSource);
					}
				
				getLogger().info("Did not get patient ('" + patientIdentifier + "') photo image from cache");
			}
			catch(CompositeIOException cioX) 
			{
				// if we know that no bytes have been written then we we can continue
				// otherwise we have to stop here and throw an error 
				if( cioX.isBytesWrittenKnown() && cioX.getBytesWritten() == 0 || cioX.getBytesWritten() == -1 )
				{
					getLogger().warn(
						"IO Exception when reading from cache, continuing with direct data source stream." + 
						cioX.getBytesWritten() + 
						" bytes were indicated to have been written." +
						"Caused by : [" + cioX.getMessage() +
						"] at " + getClass().getName() + ".callSynchronouslyInTransactionContext()"
					);					
					return streamFromDataSource();
				}
				else
				{
					// exception occurred, we can't continue because the image may be partially written
					getLogger().error(cioX);
					throw new MethodException(
						"IO Exception when reading from cache, cannot continue because " + cioX.getBytesWritten() + 
						" bytes were known to have been written, continuing could result in corrupted image. " +
						"Caused by : [" + cioX.getMessage() +
						"] at " + getClass().getName() + ".callSynchronouslyInTransactionContext()"
					);
				}
			}
			catch(IOException ioX)
			{
				// exception occurred, we can't continue because the image may be partially written
				getLogger().error(ioX);
				throw new MethodException(
					"IO Exception when reading from cache, cannot continue because some bytes may be written, " + 
					"continuing could result in corrupted image. " +
					"Caused by : [" + ioX.getMessage() +
					"] at " + getClass().getName() + ".callSynchronouslyInTransactionContext()"
				);
			}

			// if we get here then caching is enabled but the instance was not found in the cache
			// we try to grab the writable byte channel as soon as possible to lock other threads from writing to
			// it
			transactionContext.setItemCached(Boolean.FALSE);
			ImmutableInstance instance = null;
			InstanceWritableByteChannel instanceWritableChannel = null;
			OutputStream cacheOutStream = null;

			
			try
			{
				InputStream inputStream = streamFromDataSource();
				if(inputStream == null)
				{
					getLogger().info("Received null response from data source, indicates patient '" + 
							getPatientIdentifier() + "' does not have a photo ID at site " + 
							getRoutingToken().toRoutingTokenString() + ", returning null.");
					return null; // indicates no photo ID for this patient at the site
				}
				getLogger().info("Received response from data source, putting into cache");
				// set the data source image format and image quality here 
				// since it is now in the cache.
				// JMW 10/6/2008
				// moved this here, if we get the image from the DS, need to use the format/quality from the DS to put/get the image from the cache
				// only clear these values if there is a cache exception (error writing to the cache)
				
				getLogger().debug("Attempting to create cache instance for photo image");
				instance = getCommandContext().getIntraEnterpriseCacheCache().createPatientPhotoId(getSiteNumber(), patientIdentifier);

				instanceWritableChannel = instance.getWritableChannel();
				cacheOutStream = Channels.newOutputStream(instanceWritableChannel);
				
				if(cacheOutStream != null)
				{
					getLogger().info("Pumping stream into cache");
					ByteStreamPump pump = ByteStreamPump.getByteStreamPump(ByteStreamPump.TRANSFER_TYPE.NetworkToNetwork);
					// if the cacheStream is null the ByteStreamPump will ignore it
					int bytesReturned = pump.xfer(inputStream, cacheOutStream);
					getLogger().info("Wrote '" + bytesReturned + "' bytes into cache for patient photo image.");					
					
				}// not really sure what to do in the alternative here...
				//TODO: move to finally!
				if(inputStream != null)
				{
					// close the input stream
					inputStream.close();
				}				
				cacheOutStream.close();				
			}
			catch(InstanceInaccessibleException iaX)
			{
				// special exception handling, another thread is requesting to write to the instance
				// just before we did.  Try once again to read from the cache, our thread will be held until
				// the write is complete
				try
				{
					getLogger().warn("InstanceInaccessibleException caused by patient [" + patientIdentifier + ":" + routingToken.toString() + "] photo", iaX);
					
					getLogger().debug("Finding photo ID cached instance using format from data source response.");
					InputStream cacheResponse = CommonImageCacheFunctions.streamPatientPhotoImageFromCache(getCommandContext(), 
						getSiteNumber(), patientIdentifier);
					if(cacheResponse != null)
					{
						getLogger().debug("Found photo ID instance in cache from data source response.");
						return cacheResponse;
					}
					getLogger().info("Did not get patient '" + patientIdentifier + "' photo from cache");
				}
				catch(CompositeIOException cioX) 
				{
					// if we know that no bytes have been written then we we can continue
					// otherwise we have to stop here and throw an error 
					if( cioX.isBytesWrittenKnown() && cioX.getBytesWritten() == 0 || cioX.getBytesWritten() == -1 )
					{
						getLogger().warn(
							"IO Exception when reading from cache, continuing with direct data source stream." + 
							cioX.getBytesWritten() + 
							" bytes were indicated to have been written." +
							"Caused by : [" + cioX.getMessage() +
							"] at " + getClass().getName() + ".callSynchronouslyInTransactionContext()"
						);					
						return streamFromDataSource();
					}
					else
					{
						// exception occurred, we can't continue because the image may be partially written
						getLogger().error(cioX);
						throw new MethodException(
							"IO Exception when reading from cache, cannot continue because " + cioX.getBytesWritten() + 
							" bytes were known to have been written, continuing could result in corrupted image. " +
							"Caused by : [" + cioX.getMessage() +
							"] at " + getClass().getName() + ".callSynchronouslyInTransactionContext()"
						);
					}
				}
				catch(IOException ioX)
				{
					// exception occured, we can't continue because the image may be partially written
					getLogger().error(ioX);
					throw new MethodException(
						"IO Exception when reading from cache, cannot continue because some bytes may be written, continuing could result in corrupted image." +
						"Caused by : [" + ioX.getMessage() +
						"] at " + getClass().getName() + ".callSynchronouslyInTransactionContext() in InstanceInaccessibleException handler"
					);
				}
			}
			catch(SimultaneousWriteException swX)
			{
				getLogger().warn("SimultaneousWriteException caused by patient '" + patientIdentifier + "' photo image.", swX);
				// JMW 10/3/2008
				// occurs if 2 threads are attempting to write to the cache at the same time,
				// this thread will try to get the image from the cache which should cause this 
				// thread to wait for the other thread to complete before getting the image
				try
				{
					InputStream cacheResponse = CommonImageCacheFunctions.streamPatientPhotoImageFromCache(getCommandContext(), 
						getSiteNumber(), patientIdentifier);
					if(cacheResponse != null)
					{
						getLogger().debug("Found photo ID instance in cache from data source response.");
						return cacheResponse;
					}
					getLogger().info("Did not get patient '" + patientIdentifier + "' photo from cache");
					
				}
				catch(IOException ioX)
				{
					// exception occurred, we can't continue because the image may be partially written
					getLogger().error(ioX);
					throw new MethodException(
						"IO Exception when reading from cache, cannot continue because some bytes may be written, continuing could result in corrupted image." +
						"Caused by : [" + ioX.getMessage() +
						"] at " + getClass().getName() + ".callSynchronouslyInTransactionContext() in SimultaneousWriteException handler"
					);
				}
			}
			catch(CacheException cX)
			{
				// any kind of cache exceptions should be logged, but the image must still be retreived from the DoD
				// from here on if cacheOutStream is not null we'll write to it 
				getLogger().error(cX);
				instance = null;
				instanceWritableChannel= null;
				cacheOutStream = null;
			}
			catch(IOException ioX)
			{
				cacheOutStream = null;
				try{instanceWritableChannel.error();}catch(Throwable t){}
				getLogger().error(ioX);
			} 
			catch (ImageNotFoundException e)
            {
				//return null;
				throw e;
            }
			catch(ImageNearLineException inlX)
			{
				//scheduleRequestOfNearlineImage();
				throw inlX;
			}
			finally
			{
				// the instance absolutely positively must be closed
				if((instanceWritableChannel != null) && (instanceWritableChannel.isOpen()))
				{
					getLogger().error("Cache instance writable byte channel being closed with error on unknown exception");
					try{instanceWritableChannel.error();}catch(Throwable t){}
				}
			}

			// the image is now in the cache, the streams and channels are closed
			// now try to stream from the cache
			try
			{
				InputStream cacheResponse = CommonImageCacheFunctions.streamPatientPhotoImageFromCache(getCommandContext(), 
					getSiteNumber(), patientIdentifier);
				if(cacheResponse != null)
				{
					getLogger().debug("Found photo ID instance in cache from data source response.");
					return cacheResponse;
				}
				getLogger().info("Did not get patient '" + patientIdentifier + "' photo from cache");
			}
			catch(IOException ioX)
			{
				// exception occured, we can't continue because the image may be partially written
				getLogger().error(ioX);
				throw new MethodException(
					"IO Exception when reading from cache, cannot continue because some bytes may be written, continuing could result in corrupted image." +
					"Caused by : [" + ioX.getMessage() +
					"] at " + getClass().getName() + ".callSynchronouslyInTransactionContext() streaming from cache."
				);
			}
		}
		
		// caching is disabled or unusable for this instance
		// stream directly from cache
		return streamFromDataSource();		
	}
	
	private InputStream streamFromDataSource()
	throws MethodException, ConnectionException
	{
		getLogger().info("Patient photo image from site [" + routingToken.toString() + "] for patient [" + patientIdentifier + "] caching disabled, getting image from source.");
		
		InputStream patientIdImageStream = 
			ImagingContext.getRouter().getPatientIdentificationImage(
				getRoutingToken(), 
				getPatientIdentifier());
		
		getLogger().info( ((patientIdImageStream == null) ? "Did not find ID image" : "Found ID image") + 
				" for patient '" + patientIdentifier + "'.");
		TransactionContext transactionContext = TransactionContextFactory.get();
		transactionContext.setItemCached(Boolean.FALSE);
		return patientIdImageStream;
	}
}
