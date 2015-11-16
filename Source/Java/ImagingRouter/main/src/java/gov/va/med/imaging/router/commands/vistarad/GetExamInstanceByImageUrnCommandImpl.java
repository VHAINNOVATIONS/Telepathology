/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr 29, 2009
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
import gov.va.med.imaging.channels.ByteStreamPump;
import gov.va.med.imaging.channels.ChecksumValue;
import gov.va.med.imaging.channels.CompositeIOException;
import gov.va.med.imaging.channels.exceptions.ChecksumFormatException;
import gov.va.med.imaging.core.StreamImageFromCacheResponse;
import gov.va.med.imaging.core.annotations.routerfacade.RouterCommandExecution;
import gov.va.med.imaging.core.interfaces.ImageMetadataNotification;
import gov.va.med.imaging.core.interfaces.exceptions.*;
import gov.va.med.imaging.exchange.business.ImageFormatQualityList;
import gov.va.med.imaging.exchange.business.ImageStreamResponse;
import gov.va.med.imaging.exchange.business.util.ExchangeUtil;
import gov.va.med.imaging.exchange.enums.ImageFormat;
import gov.va.med.imaging.exchange.enums.ImageQuality;
import gov.va.med.imaging.exchange.storage.cache.ImmutableInstance;
import gov.va.med.imaging.router.commands.CommonImageCacheFunctions;
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
 * Command to request a VistARad image by a given ImageURN. The cacheOnly parameter
 * allows this command to ensure the image is in the cache but does not actually return
 * the image. If the image is in the cache then this command stops after checking the cache.
 * 
 * @author vhaiswwerfej
 *
 */
@RouterCommandExecution(asynchronous=true, distributable=true)
public class GetExamInstanceByImageUrnCommandImpl 
extends AbstractExamImageCommandImpl<Long>
{
	private static final long serialVersionUID = 3172105556588833757L;
	private final ImageURN imageUrn;
	private final ImageMetadataNotification imageMetadataNotification;
	private final OutputStream outStream;
	private final ImageFormatQualityList imageFormatQualityList;
	private final boolean cacheOnly; // determines if the image should be cached but not returned
	private final boolean responseAllowedFromCache; // determines if the image returned can come from the cache or not
	
	/**
	 * This constructor is meant to be called from a descending class, not meant to be called elsewhere.
	 * This allows the responseAllowedFromCache parameter to be set
	 * 
	 * @param imageUrn
	 * @param imageMetadataNotification
	 * @param outStream
	 * @param imageFormatQualityList
	 * @param responseAllowedFromCache
	 */
	protected GetExamInstanceByImageUrnCommandImpl(ImageURN imageUrn, 
			ImageMetadataNotification imageMetadataNotification,
		OutputStream outStream, ImageFormatQualityList imageFormatQualityList, 
		boolean responseAllowedFromCache)
	{
		this.imageUrn = imageUrn;
		this.imageFormatQualityList = imageFormatQualityList;
		this.imageMetadataNotification = imageMetadataNotification;
		this.outStream = outStream;
		this.cacheOnly = false;
		this.responseAllowedFromCache = responseAllowedFromCache;
	}
	
	public GetExamInstanceByImageUrnCommandImpl(ImageURN imageUrn, 
			ImageMetadataNotification imageMetadataNotification,
		OutputStream outStream, ImageFormatQualityList imageFormatQualityList)
	{
		this.imageUrn = imageUrn;
		this.imageFormatQualityList = imageFormatQualityList;
		this.imageMetadataNotification = imageMetadataNotification;
		this.outStream = outStream;
		this.cacheOnly = false;
		this.responseAllowedFromCache = true;
	}
	
	public GetExamInstanceByImageUrnCommandImpl(ImageURN imageUrn, 
			ImageFormatQualityList imageFormatQualityList)
	{
		this.imageUrn = imageUrn;
		this.imageFormatQualityList = imageFormatQualityList;
		this.imageMetadataNotification = null;
		this.outStream = null;
		this.cacheOnly = true;
		this.responseAllowedFromCache = true;
	}
	
	/**
	 * @return the responseAllowedFromCache
	 */
	public boolean isResponseAllowedFromCache()
	{
		return responseAllowedFromCache;
	}

	/**
	 * @return the cacheOnly
	 */
	public boolean isCacheOnly() {
		return cacheOnly;
	}

	/**
	 * @return the imageUrn
	 */
	public ImageURN getImageUrn() {
		return imageUrn;
	}

	/**
	 * @return the imageMetadataNotification
	 */
	public ImageMetadataNotification getImageMetadataNotification() {
		return imageMetadataNotification;
	}

	/**
	 * @return the outStream
	 */
	public OutputStream getOutStream() {
		return outStream;
	}

	/**
	 * @return the imageFormatQualityList
	 */
	public ImageFormatQualityList getImageFormatQualityList() {
		return imageFormatQualityList;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.commands.vistarad.AbstractExamCommandImpl#areClassSpecificFieldsEqual(java.lang.Object)
	 */
	@Override
	protected boolean areClassSpecificFieldsEqual(Object obj) 
	{
		// Perform cast for subsequent tests
		final GetExamInstanceByImageUrnCommandImpl other = (GetExamInstanceByImageUrnCommandImpl) obj;
		
		boolean allEqual = true;
		allEqual = allEqual && areFieldsEqual(this.imageUrn, other.imageUrn);
		allEqual = allEqual && areFieldsEqual(this.outStream, other.outStream);
		allEqual = allEqual && areFieldsEqual(this.imageFormatQualityList, other.imageFormatQualityList);
		allEqual = allEqual && areFieldsEqual(this.imageMetadataNotification, other.imageMetadataNotification);
		
		return allEqual;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#callSynchronouslyInTransactionContext()
	 */
	@Override
	public Long callSynchronouslyInTransactionContext() 
	throws MethodException, ConnectionException 
	{
		int bytesReturned=0;
		TransactionContext transactionContext = TransactionContextFactory.get();

		getLogger().info("RouterImpl.getInstanceByImageURN(" + imageUrn.toString() + ", " + getImageFormatQualityList().getAcceptString(true, true) + ")");
		if((outStream == null) && (!cacheOnly))
		{
			throw new MethodException("Outputstream is null");
		}
		
		if(cacheOnly)
			getLogger().info("requested to cache image only, will not return image stream");
		
		if(!isResponseAllowedFromCache())
		{
			getLogger().info("Requested to get image from data source, will not get image from cache (will put image into cache)");					
		}
		
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
			getLogger().info("Image '" + imageUrn.toString() + "' caching enabled.");
			try
			{
				if(isResponseAllowedFromCache())
				{
					if(isCacheOnly())
					{
						if(CommonImageCacheFunctions.isImageCached(getCommandContext(), imageUrn, getImageFormatQualityList()))
						{
							getLogger().info("Image '" + imageUrn.toString() + "' is already cached, not returning any data.");
							return new Long(0);
						}
					}
					else
					{				
						StreamImageFromCacheResponse response = 
							CommonImageCacheFunctions.streamImageFromCache(getCommandContext(), imageUrn, 
								getImageFormatQualityList(), outStream, getImageMetadataNotification());
						if((response != null) && (response.getBytesReturnedFromDataSource() > 0))
						{
							transactionContext.setItemCached(Boolean.TRUE);
							getLogger().info("Image '" + imageUrn.toString() + "' found in the cache and streamed to the destination.");					
		
							return new Long(response.getBytesReturnedFromDataSource());
							// new ImageMetadata(imageUrn, response.imageFormat, null, response.bytesReturnedFromDataSource, response.bytesReturnedFromDataSource);
						}
					}
				}
				getLogger().info("Did not get image [" + imageUrn.toString() + "] from cache");
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
			
			// JMW 9/5/08 - if the image comes back from the datasource, use the properties of the
			// image (format/quality) from the datasource to find it in the cache
			ImageFormat dataSourceImageFormat = null;
			ImageQuality dataSourceImageQuality = null;
			
			try
			{
				ImageStreamResponse datasourceResponse = streamImageFromDataSource(imageUrn, getImageFormatQualityList());
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
				
				if(cacheOutStream != null)
				{
					getLogger().info("Pumping stream into cache");
					ByteStreamPump pump = ByteStreamPump.getByteStreamPump(ByteStreamPump.TRANSFER_TYPE.NetworkToNetwork);
					// if the cacheStream is null the ByteStreamPump will ignore it
					bytesReturned = pump.xfer(imageStream, cacheOutStream);
					
					
				}// not really sure what to do in the alternative here...
				
				if(imageStream != null)
				{
					// close the input stream
					imageStream.close();
				}
				String responseChecksum = datasourceResponse.getProvidedImageChecksum();
				cacheOutStream.close();
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
						if (noResponseChecksum) responseCV = new ChecksumValue("");
						else responseCV = new ChecksumValue(responseChecksum);
						ChecksumValue cacheCV = new ChecksumValue(cacheChecksum);

						if (noResponseChecksum) {

							if (responseChecksum.equals("ok"))
								getLogger().info("Checksum for inStream '" + imageUrn + "' equals to data source cheksum.");
							else // "not ok"
								getLogger().info("Checksum for inStream '" + imageUrn + "' IS Not Equal to data source cheksum.");
						}
						else if (responseCV.getAlgorithm().equals(cacheCV.getAlgorithm())) {

							if (! responseCV.equals(cacheCV) )
								getLogger().error("Checksums for instance '" + imageUrn + "' ARE NOT EQUAL.");
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
					}
				}
			}
			catch(InstanceInaccessibleException iaX)
			{
				// special exception handling, another thread is requesting to write to the instance
				// just before we did.  Try once again to read from the cache, our thread will be held until
				// the write is complete
				try
				{
					getLogger().warn("InstanceInaccessibleException caused by image [" + imageUrn.toString() + "]", iaX);
					
					if(isCacheOnly())
					{
						if(CommonImageCacheFunctions.isImageCached(getCommandContext(), imageUrn, getImageFormatQualityList()))
						{
							getLogger().info("Image '" + imageUrn.toString() + "' is already cached, not returning any data.");
							return new Long(0);
						}
					}
					else
					{					
						StreamImageFromCacheResponse response = null;
						// if the image was from the datasource, the dataSourceImageFormat and dataSourceImageQuality from the data source
						// should be set, get that instance from the cache (since the quality of the image cached might be higher
						// than the image requested). This prevents putting an item into the cache and then never retrieving it.
						if((dataSourceImageFormat != null) &&
							(dataSourceImageQuality != null))
						{
							getLogger().debug("Finding cached instance using format [" + dataSourceImageFormat + 
								"] and quality [" + dataSourceImageQuality + "] from data source response.");
							int bytes = CommonImageCacheFunctions.streamImageFromCache(getCommandContext(), 
									imageUrn, dataSourceImageFormat, dataSourceImageQuality, outStream, getImageMetadataNotification());
							if(bytes > 0)
							{
								getLogger().debug("Found instance in cache using quality and format from data source response.");
								response = new StreamImageFromCacheResponse(null, bytes, dataSourceImageFormat, dataSourceImageQuality);
							} 
						}
						else
						{				
							response = CommonImageCacheFunctions.streamImageFromCache(getCommandContext(),
									imageUrn, getImageFormatQualityList(), outStream, getImageMetadataNotification());
						}
						
						if(response != null)
						{
							bytesReturned = response.getBytesReturnedFromDataSource();
							if( bytesReturned > 0 )
							{
								getLogger().info("Image '" + imageUrn.toString() + "' found in the cache and streamed to the destination.");
								return new Long(bytesReturned); //new ImageMetadata(imageUrn, response.imageFormat, null, bytesReturned, bytesReturned);
							}
						}
					}
					getLogger().info("Did not get image [" + imageUrn.toString() + "] from cache");
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
				getLogger().warn("SimultaneousWriteException caused by image [" + imageUrn.toString() + "]", swX);
				// JMW 10/3/2008
				// occurs if 2 threads are attempting to write to the cache at the same time,
				// this thread will try to get the image from the cache which should cause this 
				// thread to wait for the other thread to complete before getting the image
				try
				{
					if(isCacheOnly())
					{
						if(CommonImageCacheFunctions.isImageCached(getCommandContext(), imageUrn, getImageFormatQualityList()))
						{
							getLogger().info("Image '" + imageUrn.toString() + "' is already cached, not returning any data.");
							return new Long(0);
						}
					}
					else
					{
						StreamImageFromCacheResponse response = null;
						// if the image was from the datasource, the dataSourceImageFormat and dataSourceImageQuality from the data source
						// should be set, get that instance from the cache (since the quality of the image cached might be higher
						// than the image requested). This prevents putting an item into the cache and then never retrieving it.
						if((dataSourceImageFormat != null) &&
							(dataSourceImageQuality != null))
						{
							getLogger().debug("Finding cached instance using format [" + dataSourceImageFormat + 
								"] and quality [" + dataSourceImageQuality + "] from data source response.");
							int bytes = CommonImageCacheFunctions.streamImageFromCache(getCommandContext(),
									imageUrn, dataSourceImageFormat, dataSourceImageQuality, outStream, getImageMetadataNotification());
							if(bytes > 0)
							{
								getLogger().debug("Found instance in cache using quality and format from data source response.");
								response = new StreamImageFromCacheResponse(null, bytes, dataSourceImageFormat, dataSourceImageQuality);
							} 
						}
						else
						{				
							response = CommonImageCacheFunctions.streamImageFromCache(getCommandContext(),
									imageUrn, getImageFormatQualityList(), outStream, getImageMetadataNotification());
						}
						if(response != null)
						{
							bytesReturned = response.getBytesReturnedFromDataSource();
							if( bytesReturned > 0 )
							{
								getLogger().info("Image '" + imageUrn.toString() + "' found in the cache and streamed to the destination.");
								return new Long(bytesReturned); //new ImageMetadata(imageUrn, response.imageFormat, null, bytesReturned, bytesReturned);
							}
						}
						getLogger().info("Did not get image [" + imageUrn.toString() + "] from cache");
					}
					
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
			catch(ImageNearLineException inlX)
			{
				scheduleRequestOfNearlineImage();
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
				if(isCacheOnly())
				{
					getLogger().info("Image '" + imageUrn.toString() + "' is now cached, not returning any data.");
					return new Long(bytesReturned);
				}
				else
				{
					StreamImageFromCacheResponse response = null;
					// if the image was from the datasource, the dataSourceImageFormat and dataSourceImageQuality from the data source
					// should be set, get that instance from the cache (since the quality of the image cached might be higher
					// than the image requested). This prevents putting an item into the cache and then never retrieving it.
					if((dataSourceImageFormat != null) &&
						(dataSourceImageQuality != null))
					{
						getLogger().debug("Finding cached instance using format [" + dataSourceImageFormat + 
							"] and quality [" + dataSourceImageQuality + "] from data source response.");
						int bytes = CommonImageCacheFunctions.streamImageFromCache(getCommandContext(), 
								imageUrn, dataSourceImageFormat, dataSourceImageQuality, outStream, getImageMetadataNotification());
						if(bytes > 0)
						{
							getLogger().debug("Found instance in cache using quality and format from data source response.");
							response = new StreamImageFromCacheResponse(null, 
									bytes, dataSourceImageFormat, dataSourceImageQuality);
						} 
					}
					else
					{				
							response = CommonImageCacheFunctions.streamImageFromCache(getCommandContext(),
								imageUrn, getImageFormatQualityList(), outStream, getImageMetadataNotification());
					}
					// if the response is null, then didn't get anything from the cache (shouldn't happen)
					if(response != null)
					{
						bytesReturned = response.getBytesReturnedFromDataSource();
						if( bytesReturned > 0 )
						{
							getLogger().info("Image '" + imageUrn.toString() + "' found in the cache and streamed to the destination.");
							return new Long(bytesReturned);// new ImageMetadata(imageUrn, response.imageFormat, null, bytesReturned, bytesReturned);
						}
					}
					getLogger().info("Did not get image [" + imageUrn.toString() + "] from cache");
				}
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

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#parameterToString()
	 */
	@Override
	protected String parameterToString() 
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(this.getImageUrn());
		sb.append(',');
		sb.append(this.getImageFormatQualityList());
		
		return sb.toString();
	}
	
	/**
	 * Stream the image from the data source and put it into the output stream. This method DOES NOT use the
	 * cache so it should only be called after cache attempts have failed.
	 * 
	 * 
	 * @param bytesReturned
	 * @return
	 * @throws MethodException
	 * @throws ImageConversionException
	 * @throws ImageNotFoundException
	 * @throws ImageNearLineException
	 */
	private Long streamFromDataSource()
	throws MethodException, ConnectionException, ImageConversionException,
	ImageNotFoundException, ImageNearLineException
	{
		// if only trying to get the image into the cache, don't do anything here since this method only
		// puts the image response into the response output stream, not the cache.
		if(isCacheOnly())
		{
			getLogger().info("Image '" + imageUrn.toString() + "' caching disabled, will not get image from DS without cache");
			return new Long(0);
		}
		else
		{
			getLogger().info("Image '" + imageUrn.toString() + "' caching disabled, getting image from source.");
	
			InputStream imageStream = null;
			try
			{
				ImageStreamResponse streamResponse = streamImageFromDataSource(imageUrn, getImageFormatQualityList());
				if(getImageMetadataNotification() != null)
				{
					getImageMetadataNotification().imageMetadata(streamResponse.getProvidedImageChecksum(), 
						streamResponse.getImageFormat(), 0, streamResponse.getImageQuality());
				}
				getLogger().info("Pumping response to client (bypassing cache)");
				ByteStreamPump pump = ByteStreamPump.getByteStreamPump(ByteStreamPump.TRANSFER_TYPE.NetworkToNetwork);
				// if the cacheStream is null the ByteStreamPump will ignore it
				imageStream = streamResponse.getImageStream().getInputStream();
				long bytesReturned = pump.xfer(imageStream, outStream);					
				return new Long(bytesReturned);
			} 
			catch (IOException ioX)
			{
				getLogger().error(ioX);
				throw new MethodException(ioX);
			} 
			catch (ImageNotFoundException e)
	        {
				throw e;
	        }
			catch(ImageNearLineException inlX)
			{
				scheduleRequestOfNearlineImage(); 
				throw inlX;
			}
			finally
			{
				if(imageStream != null)
				{
					try{imageStream.close();}
					catch(IOException ioX){getLogger().warn(ioX);}
				}
			}
		}
	}

	/**
	 * Schedule a subsequent request of an image after a near-line exception has occurred.  A near-line exception
	 * implies the image is not currently available but will be shortly. Calling this function creates a new 
	 * prefetch request for the image and schedules it to execute after 1 minute at which point the hope is the
	 * image is available from the datasource. Because a prefetch command is used, this will only be attempted
	 * once.
	 */
	private void scheduleRequestOfNearlineImage()
	{
		try
		{
			//getLogger().info("Image '" + getImageUrn().toString() + "' returned nearline, requesting image again in " + prefetchImageRetryDelayMinutes + " minute");
			getLogger().info("Exam Image '" + getImageUrn().toString() + "' returned nearline, requesting image again in 1 minute");
			ImagingContext.getRouter().prefetchExamInstanceByImageUrnDelayOneMinute(getImageUrn(), 
				getImageFormatQualityList());
			/*
			Command<java.lang.Void> cmd = 
				getCommandContext().getCommandFactory().createCommand(null, 
					"PrefetchInstanceByImageUrnCommand", new Object[] {getImageUrn(), getRequestedFormatQuality()});
			Calendar now = Calendar.getInstance();
			now.add(Calendar.MINUTE, prefetchImageRetryDelayMinutes); // set to request again in 1 minute
			cmd.setAccessibilityDate(now.getTime());
			cmd.setPriority(ScheduledPriorityQueueElement.Priority.NORMAL.ordinal());
			getCommandContext().getRouter().doAsynchronously(cmd);
			*/
		}
		catch(Exception ex)
		{
			// just in case...
			getLogger().error("Error scheduling request of nearline image", ex);
		}
	}
}
