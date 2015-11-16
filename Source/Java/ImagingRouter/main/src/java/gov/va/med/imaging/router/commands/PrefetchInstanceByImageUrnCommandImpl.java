/**
 * 
 */
package gov.va.med.imaging.router.commands;

import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.channels.ByteStreamPump;
import gov.va.med.imaging.channels.ChecksumValue;
import gov.va.med.imaging.channels.exceptions.ChecksumFormatException;
import gov.va.med.imaging.core.annotations.routerfacade.RouterCommandExecution;
import gov.va.med.imaging.core.interfaces.ImageMetadataNotification;
import gov.va.med.imaging.core.interfaces.exceptions.ChecksumComparisonFailedException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNotFoundException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.ImageFormatQualityList;
import gov.va.med.imaging.exchange.business.ImageStreamResponse;
import gov.va.med.imaging.exchange.business.util.ExchangeUtil;
import gov.va.med.imaging.exchange.enums.ImageFormat;
import gov.va.med.imaging.exchange.enums.ImageQuality;
import gov.va.med.imaging.exchange.storage.cache.ImmutableInstance;
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
 * This command gets an image from a remote data source and then caches it locally.
 * This command does not log image access because a prefetch does not imply that an image has bee
 * viewed.
 * 
 * This command allows both asynchronous execution and distributed execution.
 * 
 * @author vhaiswbeckec
 *
 */
@RouterCommandExecution(asynchronous=true, distributable=true)
public class PrefetchInstanceByImageUrnCommandImpl 
extends AbstractImageCommandImpl<Void>
{
	private static final long serialVersionUID = 1969186619792673657L;
	
	private final ImageURN imageUrn;
	private final ImageMetadataNotification imageMetadataNotification;
	private final ImageFormatQualityList imageFormatQualityList;

	/**
	 * 
	 * @param imageUrn
	 * @param imageFormatQualityList
	 */
	public PrefetchInstanceByImageUrnCommandImpl(
		ImageURN imageUrn,
		ImageFormatQualityList imageFormatQualityList)
	{
		this(imageUrn, imageFormatQualityList, (ImageMetadataNotification)null);
	}
	
	/**
	 * 
	 * @param commandContext - the context available to the command
	 * @param imageUrn - the universal identifier of the image
	 * @param outStream - the Output Stream where the image text will be available
	 * @param metadataCallback - the listener to be notified when metadata is available
	 * @param imageFormatQualityList - a list of acceptable format quality values
	 * @param logAccess - whether to log access or not
	 */
	public PrefetchInstanceByImageUrnCommandImpl(
		ImageURN imageUrn,
		ImageFormatQualityList imageFormatQualityList,
		ImageMetadataNotification imageMetadataNotification)
	{
		super();
		this.imageUrn = imageUrn;
		this.imageMetadataNotification = imageMetadataNotification;
		this.imageFormatQualityList = imageFormatQualityList;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#parameterToString()
	 */
	@Override
	protected String parameterToString()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(getImageUrn());
		sb.append(',');
		sb.append(getMetadataCallback() == null ? "<null callback>" : getMetadataCallback().toString());
		sb.append(',');
		sb.append(getRequestedFormatQuality() == null ? "<null image format>" : getRequestedFormatQuality().toString());
		
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.router.commands.GetInstanceByImageUrnCommand#getImageUrn()
	 */
	public ImageURN getImageUrn()
	{
		return this.imageUrn;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.router.commands.GetInstanceByImageUrnCommand#getMetadataCallback()
	 */
	public ImageMetadataNotification getMetadataCallback()
	{
		return this.imageMetadataNotification;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.router.commands.GetInstanceByImageUrnCommand#getRequestedFormatQuality()
	 */
	public ImageFormatQualityList getRequestedFormatQuality()
	{
		return this.imageFormatQualityList;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((this.imageFormatQualityList == null) ? 0
						: this.imageFormatQualityList.hashCode());
		result = prime
				* result
				+ ((this.imageMetadataNotification == null) ? 0
						: this.imageMetadataNotification.hashCode());
		result = prime * result
				+ ((this.imageUrn == null) ? 0 : this.imageUrn.hashCode());
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
		final PrefetchInstanceByImageUrnCommandImpl other = (PrefetchInstanceByImageUrnCommandImpl) obj;
		if (this.imageFormatQualityList == null)
		{
			if (other.imageFormatQualityList != null)
				return false;
		} else if (!this.imageFormatQualityList
				.equals(other.imageFormatQualityList))
			return false;
		if (this.imageMetadataNotification == null)
		{
			if (other.imageMetadataNotification != null)
				return false;
		} else if (!this.imageMetadataNotification
				.equals(other.imageMetadataNotification))
			return false;
		if (this.imageUrn == null)
		{
			if (other.imageUrn != null)
				return false;
		} else if (!this.imageUrn.equals(other.imageUrn))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#callInTransactionContext()
	 */
	@Override
	public java.lang.Void callSynchronouslyInTransactionContext()
	throws MethodException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();

		getLogger().info("RouterImpl.prefetchInstanceByImageURN(" + imageUrn.toString() + ", " + getRequestedFormatQuality().getAcceptString(true, true) + ") + '" + transactionContext.getTransactionId() + "'");
		
		// use this imageId to query the DOD
		String siteNumber = imageUrn.getOriginatingSiteId();
		transactionContext.setServicedSource(imageUrn.toRoutingTokenString());
		// if caching is enabled we will try to use the cache
		// cacheThisInstance indicates both that we write to and read from the cache for this instance
		
		// if the Image URN was successfully parsed and caching is enabled
		// try to retrieve the instance from the cache
		if(! isInstanceInCache(imageUrn, getRequestedFormatQuality() ))
		{
			// the instance was not found in the cache
			// we try to grab the writable byte channel as soon as possible to lock other threads from writing to
			// it
			getLogger().info("Did not find image '" + getImageUrn().toString() + "' in cache, will attempt to get from datasource.");
			transactionContext.setItemCached(Boolean.FALSE);
			ImmutableInstance instance = null;
			InstanceWritableByteChannel instanceWritableChannel = null;
			OutputStream cacheOutStream = null;
			
			// JMW 9/5/08 - if the image comes back from the datasource, use the properties of the
			// image (format/quality) from the datasource to find it in the cache
			ImageFormat dataSourceImageFormat = null;
			ImageQuality dataSourceImageQuality = null;
			
			try
			{
				ImageStreamResponse datasourceResponse = streamImageFromDataSource(imageUrn, getRequestedFormatQuality());
				ImageFormat imgFormat = datasourceResponse.getImageFormat();
				getLogger().info("Received response from data source, putting into cache");
				// set the data source image format and image quality here 
				// since it is now in the cache.
				// JMW 10/6/2008
				// moved this here, if we get the image from the DS, need to use the format/quality from the DS to put/get the image from the cache
				// only clear these values if there is a cache exception (error writing to the cache)
				dataSourceImageFormat = imgFormat;
				dataSourceImageQuality = datasourceResponse.getImageQuality();
				getLogger().debug("Attempting to create cache instance with format ["  + dataSourceImageFormat + "] and quality [" + dataSourceImageQuality + "]");
				if(ExchangeUtil.isSiteDOD(siteNumber))
				{
					instance = getCommandContext().getExtraEnterpriseCache().createImage(imageUrn, dataSourceImageQuality.name(), dataSourceImageFormat.getMimeWithEnclosedMime());
				}
				else
				{
					instance = getCommandContext().getIntraEnterpriseCacheCache().createImage(imageUrn, dataSourceImageQuality.name(), dataSourceImageFormat.getMimeWithEnclosedMime());
				}

				instanceWritableChannel = instance.getWritableChannel();
				cacheOutStream = Channels.newOutputStream(instanceWritableChannel);
				InputStream imageStream = datasourceResponse.getImageStream().getInputStream();
				
				int bytes = 0;
				if(cacheOutStream != null)
				{
					getLogger().info("Pumping stream into cache");
					ByteStreamPump pump = ByteStreamPump.getByteStreamPump(ByteStreamPump.TRANSFER_TYPE.NetworkToNetwork);
					// if the cacheStream is null the ByteStreamPump will ignore it
					bytes = pump.xfer(imageStream, cacheOutStream);
					
					
				}// not really sure what to do in the alternative here...
				
				if(imageStream != null)
				{
					// close the input stream
					imageStream.close();
				}
				cacheOutStream.close();
				
				assertCacheAndDatasourceChecksumEquality(instance,datasourceResponse);
				if(getMetadataCallback() != null)
					getMetadataCallback().imageMetadata(datasourceResponse.getProvidedImageChecksum(), 
							dataSourceImageFormat, bytes, dataSourceImageQuality);
			}
			catch(InstanceInaccessibleException iaX)
			{
				// special exception handling, another thread is requesting to write to the instance
				// just before we did.
				// Since the function of this command is to prefetch an image, and we now know that some other thread
				// is getting the image, just return.
				getLogger().info("InstanceInaccessibleException for image [" + imageUrn.toString() + "], prefetch is completing on the assumption tha the other thread will cache the image");
			}
			catch(SimultaneousWriteException swX)
			{
				getLogger().info("SimultaneousWriteException for image [" + imageUrn.toString() + "], prefetch is completing on the assumption tha the other thread will cache the image");
			}
			catch(CacheException cX)
			{
				// any kind of cache exceptions should be logged, but the image must still be retrieved from the data source
				// from here on if cacheOutStream is not null we'll write to it 
				getLogger().error(cX);
				instance = null;
				instanceWritableChannel= null;
				cacheOutStream = null;
				dataSourceImageFormat = null;
				dataSourceImageQuality = null;
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
			finally
			{
				// the instance absolutely positively must be closed
				if((instanceWritableChannel != null) && (instanceWritableChannel.isOpen()))
				{
					getLogger().error("Cache instance writable byte channel being closed with error on unknown exception");
					try{instanceWritableChannel.error();}catch(Throwable t){}
				}
			}
		}
		else
		{
			transactionContext.setItemCached(true);
			getLogger().info("Found image '" + getImageUrn().toString() + "' in cache, prefetch complete.");
		}
		
		return (java.lang.Void)null;
	}

	/**
	 * 
	 * @param instance
	 * @param datasourceResponse
	 * @throws ChecksumComparisonFailedException
	 */
	private void assertCacheAndDatasourceChecksumEquality(
		ImmutableInstance instance, 
		ImageStreamResponse datasourceResponse)
	throws ChecksumComparisonFailedException
	{
		String responseChecksum = datasourceResponse.getProvidedImageChecksum();
		boolean noResponseChecksum = false;
		if(responseChecksum != null)
		{
			noResponseChecksum = (responseChecksum.equals("ok") || responseChecksum.equals("not ok"));
		}					
		String cacheChecksum = instance.getChecksumValue();
		if(responseChecksum != null && cacheChecksum != null)
		{
			try
			{
				ChecksumValue responseCV;
				if (noResponseChecksum) 
					responseCV = new ChecksumValue("");
				else 
					responseCV = new ChecksumValue(responseChecksum);
				ChecksumValue cacheCV = new ChecksumValue(cacheChecksum);

				if (noResponseChecksum) 
				{
					if (responseChecksum.equals("ok"))
						getLogger().info("Checksum for inStream '" + imageUrn + "' equals to data source cheksum.");
					else // "not ok"
					{
						getLogger().info("Checksum for inStream '" + imageUrn + "' IS Not Equal to data source checksum.");
						throw new ChecksumComparisonFailedException(imageUrn);
					}
				}
				else if (responseCV.getAlgorithm().equals(cacheCV.getAlgorithm())) 
				{
					if (! responseCV.equals(cacheCV) )
					{
						getLogger().error("Checksums for instance '" + imageUrn + "' ARE NOT EQUAL.");
						throw new ChecksumComparisonFailedException(imageUrn);
					}
					else
						getLogger().info("Checksums for instance '" + imageUrn + "' are equal.");
				}
				else
					getLogger().warn("Checksums not compared for instance '" + imageUrn + 
							"' because response algorithm is '" + responseCV.getAlgorithm() + 
							"' and cache algorithm is '" + cacheCV.getAlgorithm() + "'.");
			} 
			catch (ChecksumFormatException x)
			{
				getLogger().error("Invalidly formatted checksum value, either response header checksum '" + responseChecksum + 
						"' or cache calculated checksum '" + cacheChecksum + "'");
				throw new ChecksumComparisonFailedException(imageUrn);
			}
		}
	}
}
