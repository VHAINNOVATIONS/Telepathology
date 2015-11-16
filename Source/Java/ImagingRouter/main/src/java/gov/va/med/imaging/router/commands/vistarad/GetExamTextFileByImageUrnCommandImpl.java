/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 5, 2009
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswwerfej
  Description: 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+

 */
package gov.va.med.imaging.router.commands.vistarad;

import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.core.StreamImageFromCacheResponse;
import gov.va.med.imaging.core.annotations.routerfacade.RouterCommandExecution;
import gov.va.med.imaging.core.interfaces.ImageMetadataNotification;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.ImageFormatQuality;
import gov.va.med.imaging.exchange.business.ImageFormatQualityList;
import gov.va.med.imaging.exchange.business.util.ExchangeUtil;
import gov.va.med.imaging.exchange.enums.ImageFormat;
import gov.va.med.imaging.exchange.enums.ImageQuality;
import gov.va.med.imaging.exchange.storage.cache.ImmutableInstance;
import gov.va.med.imaging.router.commands.CommonImageCacheFunctions;
import gov.va.med.imaging.storage.cache.InstanceWritableByteChannel;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import gov.va.med.imaging.storage.cache.exceptions.InstanceInaccessibleException;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;

/**
 * @author vhaiswwerfej
 *
 */
@RouterCommandExecution(asynchronous=true, distributable=true)
public class GetExamTextFileByImageUrnCommandImpl 
extends AbstractExamImageCommandImpl<Integer>
{
	private static final long serialVersionUID = -4581736548969027211L;
	
	private final ImageURN imageUrn;
	private final ImageMetadataNotification imageMetadataNotification;
	private final OutputStream outStream;
	private final boolean cacheOnly;
	
	public GetExamTextFileByImageUrnCommandImpl(ImageURN imageUrn, 
			ImageMetadataNotification imageMetadataNotification, OutputStream outStream)
	{
		this.imageMetadataNotification = imageMetadataNotification;
		this.imageUrn = imageUrn;
		this.outStream = outStream;
		this.cacheOnly = false;
	}
	
	public GetExamTextFileByImageUrnCommandImpl(ImageURN imageUrn)
	{
		this.imageMetadataNotification = null;
		this.imageUrn = imageUrn;
		this.outStream = null;
		this.cacheOnly = true;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.commands.vistarad.AbstractExamCommandImpl#areClassSpecificFieldsEqual(java.lang.Object)
	 */
	@Override
	protected boolean areClassSpecificFieldsEqual(Object obj) 
	{
		// Perform cast for subsequent tests
		final GetExamTextFileByImageUrnCommandImpl other = (GetExamTextFileByImageUrnCommandImpl) obj;
		
		boolean allEqual = true;
		allEqual = allEqual && areFieldsEqual(this.imageUrn, other.imageUrn);
		allEqual = allEqual && areFieldsEqual(this.imageMetadataNotification, other.imageMetadataNotification);
		allEqual = allEqual && areFieldsEqual(this.cacheOnly, other.cacheOnly);
		allEqual = allEqual && areFieldsEqual(this.outStream, other.outStream);
		
		return allEqual;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#callSynchronouslyInTransactionContext()
	 */
	@Override
	public Integer callSynchronouslyInTransactionContext() 
	throws MethodException, ConnectionException 
	{
		int bytesReturned=0;
		ImageQuality txtQuality = ImageQuality.REFERENCE;
		ImageFormat txtFormat = ImageFormat.TEXT_DICOM;
		ImageFormatQualityList qualityList = new ImageFormatQualityList();
		qualityList.add(new ImageFormatQuality(txtFormat, txtQuality));
		TransactionContext transactionContext = TransactionContextFactory.get();

		getLogger().info("RouterImpl.getExamTxtFileByImageURN(" + imageUrn.toString() + ")");
		if((outStream == null) && (!isCacheOnly()))
		{
			throw new MethodException("Outputstream is null");
		}
		
		if(isCacheOnly())
			getLogger().info("Request to cache TXT file only, no return");
		
		// use this imageId to query the DOD
		String imageId = imageUrn.getImageId();
		String studyId = imageUrn.getStudyId();
		String siteNumber = imageUrn.getOriginatingSiteId();

		transactionContext.setServicedSource(imageUrn.toRoutingTokenString());

		// if caching is enabled we will try to use the cache
		// cacheThisInstance indicates both that we write to and read from the cache for this instance
		boolean cacheThisInstance = studyId != null  &&  imageId != null  && getCommandContext().isCachingEnabled();

		// if the Image URN was successfully parsed and caching is enabled
		// try to retrieve the instance from the cache
		if( cacheThisInstance ) 
		{
			getLogger().info("Image Text file '" + imageUrn.toString() + "' caching enabled");
			try
			{
				if(isCacheOnly())
				{
					if(CommonImageCacheFunctions.isImageCached(getCommandContext(), imageUrn, qualityList))
					{
						getLogger().info("Image text file '" + imageUrn.toString() + "' is already cached, not returning any data.");
						return 0;
					}
				}
				else
				{
					StreamImageFromCacheResponse response = 
						CommonImageCacheFunctions.streamImageFromCache(getCommandContext(),
								imageUrn, qualityList, outStream, getImageMetadataNotification());
					if(response != null)
					{
						bytesReturned = response.getBytesReturnedFromDataSource();
						if( bytesReturned > 0 )
						{
							transactionContext.setItemCached(Boolean.TRUE);
							getLogger().info("TXT file '" + imageUrn.toString() + "' found in the cache and streamed to the destination.");
							return bytesReturned;
						}
					}
				}
				getLogger().info("Did not get txt file [" + imageUrn.toString() + "] from cache");
			}
			catch(IOException ioX)
			{
				// exception occured, we can't continue because the image may be partially written
				getLogger().error(ioX);
				throw new MethodException(
					"IO Exception when reading from cache, cannot continue because some bytes may be written, continuing could result in corrupted image." +
					"Caused by : [" + ioX.getMessage() +
					"] at " + getClass().getName() + ".callSynchronouslyInTransactionContext()."
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
				if(ExchangeUtil.isSiteDOD(siteNumber))
				{
					instance = getCommandContext().getExtraEnterpriseCache().createImage(imageUrn, txtQuality.name(), txtFormat.getMime());
				}
				else
				{
					instance = getCommandContext().getIntraEnterpriseCacheCache().createImage(imageUrn, txtQuality.name(), txtFormat.getMime());
				}

				instanceWritableChannel = instance.getWritableChannel();
				cacheOutStream = Channels.newOutputStream(instanceWritableChannel);
			}
			catch(InstanceInaccessibleException iaX)
			{
				// special exception handling, another thread is requesting to write to the instance
				// just before we did.  Try once again to read from the cache, our thread will be held until
				// the write is complete
				try
				{
					if(isCacheOnly())
					{
						if(CommonImageCacheFunctions.isImageCached(getCommandContext(), imageUrn, qualityList))
						{
							getLogger().info("Image text file '" + imageUrn.toString() + "' is already cached, not returning any data.");
							return 0;
						}
					}
					else
					{
						StreamImageFromCacheResponse response = CommonImageCacheFunctions.streamImageFromCache(getCommandContext(),
								imageUrn, qualityList, outStream, getImageMetadataNotification());
						if(response != null)
						{
							bytesReturned = response.getBytesReturnedFromDataSource();
							if( bytesReturned > 0 )
							{
								getLogger().info("TXT File '" + imageUrn.toString() + "' found in the cache and streamed to the destination.");
								return bytesReturned;
							}
						}
					}
					getLogger().info("Did not get txt file [" + imageUrn.toString() + "] from cache");
				}
				catch(IOException ioX)
				{
					// exception occured, we can't continue because the image may be partially written
					getLogger().error(ioX);
					throw new MethodException(
						"IO Exception when reading from cache, cannot continue because some bytes may be written, continuing could result in corrupted image." +
						"Caused by : [" + ioX.getMessage() +
						"] at " + getClass().getName() + ".callSynchronouslyInTransactionContext() InstanceInaccessibleException handler."
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

			// okay, we've got a stream to the cache instance, get the image from the DoD and write to it
			if(cacheOutStream != null)
			{
				try
				{
					bytesReturned = streamTXTFileFromDataSource(imageUrn, cacheOutStream); 
					cacheOutStream.close();
					
					if(isCacheOnly())
					{
						getLogger().info("Image text file '" + imageUrn.toString() + "' is now cached, not returning any data.");
						return bytesReturned;
					}
					// the image is now in the cache, the streams and channels are closed
					// now try to stream from the cache
					try
					{
						StreamImageFromCacheResponse response = CommonImageCacheFunctions.streamImageFromCache(getCommandContext(),
								imageUrn, qualityList, outStream, getImageMetadataNotification());
						if(response != null)
						{
							bytesReturned = response.getBytesReturnedFromDataSource();
							if( bytesReturned > 0 )
							{
								getLogger().info("TXT file '" + imageUrn.toString() + "' found in the cache and streamed to the destination.");
								return bytesReturned;
							}
						}
						getLogger().info("Did not get txt file [" + imageUrn.toString() + "] from cache");
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
				catch(IOException ioX)
				{
					cacheOutStream = null;
					try{instanceWritableChannel.error();}catch(Throwable t){}
					getLogger().error(ioX);
				}
				finally
				{
					// the instance absolutely positively must be closed
					if(instanceWritableChannel.isOpen())
					{
						getLogger().error("Cache instance writable byte channel being closed with error on unknown exception");
						try{instanceWritableChannel.error();}catch(Throwable t){}
					}
				}
			}
		}

		// disabling caching also makes checking checksums problematic because the cache
		// is where we do the checksum calculation
		else
		{
			if(isCacheOnly())
			{
				getLogger().info("Caching is disabled, cannot cache image text file '" + imageUrn.toString() + "'.");
				return 0;
			}
			else
			{
				getLogger().info("Image text file '" + imageUrn.toString() + "' caching disabled, getting image from source.");
	
				try
				{
					bytesReturned = streamTXTFileFromDataSource(imageUrn, outStream);
				} 
				catch (IOException ioX)
				{
					getLogger().error(ioX);
					throw new MethodException(ioX);
				}
			}
		}
		
		return bytesReturned;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#parameterToString()
	 */
	@Override
	protected String parameterToString() 
	{
		StringBuilder sb = new StringBuilder();		
		sb.append(imageUrn.toString());
		return sb.toString();
	}

	/**
	 * @return the imageUrn
	 */
	public ImageURN getImageUrn() 
	{
		return imageUrn;
	}

	/**
	 * @return the imageMetadataNotification
	 */
	public ImageMetadataNotification getImageMetadataNotification() 
	{
		return imageMetadataNotification;
	}

	/**
	 * @return the outStream
	 */
	public OutputStream getOutStream() 
	{
		return outStream;
	}

	/**
	 * @return the cacheOnly
	 */
	public boolean isCacheOnly() {
		return cacheOnly;
	}
	
	
}
