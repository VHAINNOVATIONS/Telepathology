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

import gov.va.med.MessagesResourceBundle;
import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.NullOutputStream;
import gov.va.med.imaging.channels.ByteStreamPump;
import gov.va.med.imaging.channels.ChecksumValue;
import gov.va.med.imaging.channels.CompositeIOException;
import gov.va.med.imaging.channels.WritableByteChannelSpreaderPump;
import gov.va.med.imaging.channels.exceptions.ChecksumFormatException;
import gov.va.med.imaging.core.CommandContextImpl;
import gov.va.med.imaging.core.StreamImageFromCacheResponse;
import gov.va.med.imaging.core.annotations.routerfacade.RouterCommandExecution;
import gov.va.med.imaging.core.interfaces.ImageMetadataNotification;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageConversionException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNearLineException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNotFoundException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.ImageFormatQualityList;
import gov.va.med.imaging.exchange.business.ImageMetadata;
import gov.va.med.imaging.exchange.business.ImageStreamResponse;
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
 */
@RouterCommandExecution(asynchronous=false, distributable=true)
public class HeadInstanceByImageUrnCommandImpl 
extends AbstractExamImageCommandImpl<ImageMetadata>
//extends AbstractCommandImpl<ImageMetadata>
{
	private static final long serialVersionUID = 1L;
	
	private final ImageURN imageUrn;
	private final ImageFormatQualityList imageFormatQualityList;
	private final OutputStream outStream;
	private final boolean forceDatasourceAccess; 					// false if the image metadata returned can come from the cache (if available)
	private final boolean forceSizeCalculation;						// if true then force acquisition of the size even if that means waiting for the whole image
	private final boolean allowCaching;								// if true then the image will be cached, if it is not already
	
	private static String[][] messages = new String[][]
	{
		new String[]{"ExceptionWithNoBytesWritten", "Exception when reading from cache, continuing with direct data source stream. {0} bytes were indicated to have been written. Caused by : [{1}] at {2}.callSynchronouslyInTransactionContext()"},
		new String[]{"ExceptionWithBytesWritten", "Exception when reading from cache, cannot continue because {0} bytes were known to have been written, continuing could result in corrupted image. Caused by: [{1}] at {2}.callSynchronouslyInTransactionContext()"},
		new String[]{"ExceptionWithUnknownBytesWritten", "Exception when reading from cache, cannot continue because some bytes may have been written, continuing could result in corrupted image. Caused by: [{0}] at {1}.callSynchronouslyInTransactionContext()"},
	};
	private MessagesResourceBundle messageFormatter = new MessagesResourceBundle(messages);
	
	/**
	 * Create an instance that will return the image metadata, cache the image,
	 * but not stream the image.  This is used to satisfy a HEAD request.
	 * 
	 * @param imageUrn
	 * @param imageMetadataNotification
	 * @param imageFormatQualityList
	 */
	public HeadInstanceByImageUrnCommandImpl(
		ImageURN imageUrn, 
		ImageFormatQualityList imageFormatQualityList)
	{
		this(imageUrn, imageFormatQualityList, (OutputStream)null, false, false, true);
	}

	/**
	 * 
	 * @param imageUrn - the URN of the image to retrieve.
	 * @param imageFormatQualityList - the list of acceptable image formats/qualities.
	 * @param forceDatasourceAccess - if true, forces retrieval of the file from the data source.
	 * @param forceSizeCalculation - if true, forces a wait for all of the data if the image cannot be found in the cache otherwise return the size if the image is in the cache
	 * @param allowCaching - if true, allows the image to be cached.
	 */
	public HeadInstanceByImageUrnCommandImpl(
		ImageURN imageUrn, 
		ImageFormatQualityList imageFormatQualityList,
		OutputStream outStream,
		boolean forceDatasourceAccess,
		boolean forceSizeCalculation,
		boolean allowCaching)
	{
		this.imageUrn = imageUrn;
		this.imageFormatQualityList = imageFormatQualityList;
		this.outStream = outStream;
		this.forceDatasourceAccess = forceDatasourceAccess;
		this.forceSizeCalculation = forceSizeCalculation;
		this.allowCaching = allowCaching;
	}
	
	public ImageURN getImageUrn() {return imageUrn;}

	public ImageFormatQualityList getImageFormatQualityList() {return imageFormatQualityList;}
	
	public OutputStream getOutputStream(){ return this.outStream;}

	public boolean isForceDatasourceAccess(){return this.forceDatasourceAccess;}

	public boolean isForceSizeCalculation() {return forceSizeCalculation;}

	public boolean isAllowCaching() {return allowCaching;}

	/**
	 * Encapsulates the rules as to when we try to read from the cache.
	 * Whether we cache includes whether the IDs exist, whether caching is enabled and whether this command instance
	 * explicitly disabled cache access.
	 * 
	 * @return
	 */
	public boolean isAttemptReadFromCache()
	{
		return
			imageUrn.getStudyId() != null 
			&& imageUrn.getImageId() != null 
			&& getCommandContext().isCachingEnabled() 
			&& isAllowCaching()
			&& !isForceDatasourceAccess(); 
	}

	/**
	 * Encapsulates the rules as to when we try to write to the cache.
	 * 
	 * @return
	 */
	public boolean isAttemptWriteToCache()
	{
		return
			imageUrn.getStudyId() != null 
			&& imageUrn.getImageId() != null 
			&& getCommandContext().isCachingEnabled() 
			&& isAllowCaching();
	}
	
	/**
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#callSynchronouslyInTransactionContext()
	 */
	@Override
	public ImageMetadata callSynchronouslyInTransactionContext() 
	throws MethodException, ConnectionException 
	{
		try
		{
			try
			{
				return streamImage(getOutputStream(), isAttemptReadFromCache(), isAttemptWriteToCache());
			}
			// special exception handling, another thread is requesting to write to the instance
			// just before we did.  Try once again to read from the cache, our thread will be held until
			// the write is complete
			catch(InstanceInaccessibleException iiX)
			{
				getLogger().warn("InstanceInaccessibleException caught getting image [" + imageUrn.toString() + "], operation will be retried.");
				return streamImage((OutputStream)null, true, isAttemptWriteToCache());
			}
			catch(SimultaneousWriteException swX)
			{
				getLogger().warn("SimultaneousWriteException caught getting image [" + imageUrn.toString() + "], operation will be retried.");
				return streamImage((OutputStream)null, true, isAttemptWriteToCache());
			}
			catch(CacheException cX)
			{
				getLogger().warn("Generic CacheException caught getting image [" + imageUrn.toString() + "], operation will be retried.");
				return streamImage((OutputStream)null, false, false);
			}
			catch(CompositeIOException cioX)
			{
				getLogger().warn("CompositeIOException caught getting image [" + imageUrn.toString() + "], operation will be retried.");
				return streamImage((OutputStream)null, false, false);
			}
		}
		catch(ImageNearLineException inlX)
		{
			scheduleRequestOfNearlineImage();
			throw inlX;
		}
		catch(Exception ioX)
		{
			getLogger().error(ioX);
			throw new MethodException(
				ioX.getClass().getSimpleName() 
				+ " when reading from cache, cannot continue because some bytes may be written, continuing could result in corrupted image." 
				+ "Caused by : [" + ioX.getMessage() 
				+"]."
			);
		}
	}

	/**
	 * This is the method that does the real work.
	 * It is usually called once per command execution but may be called (repeatedly) to implement
	 * retries.
	 * 
	 * @return
	 * @throws ConnectionException 
	 * @throws MethodException 
	 * @throws ImageNotFoundException 
	 * @throws ImageConversionException 
	 * @throws ImageNearLineException 
	 * @throws CacheException 
	 * @throws IOException 
	 * @throws InstanceInaccessibleException 
	 */
	private ImageMetadata streamImage(OutputStream outStream, boolean allowCacheRead, boolean allowCacheWrite) 
	throws ImageNearLineException, ImageConversionException, ImageNotFoundException, 
	       MethodException, ConnectionException, InstanceInaccessibleException, IOException, CacheException
	{
		ImageMetadata result = null;
		TransactionContext transactionContext = TransactionContextFactory.get();
		transactionContext.setServicedSource(imageUrn.toRoutingTokenString());
		
		// try to retrieve the instance from the cache unless some criteria is not met
		if( allowCacheRead ) 
		{
			try
			{
				if(CommonImageCacheFunctions.isImageCached(getCommandContext(), getImageUrn(), getImageFormatQualityList()))
					result = streamImageFromCache(outStream);
				else
					getLogger().info("Image [" + imageUrn.toString() + "] does not exist in cache, getting metadata from data source.");
			}
			catch(IOException ioX)
			{
				// exception occurred, we can't continue because the image may be partially written
				getLogger().error(ioX);
				String msg = messageFormatter.formatMessage("ExceptionWithUnknownBytesWritten", ioX.getMessage(), getClass().getName());

				throw new MethodException(msg);
			}
		}

		// if the instance was not found in the cache
		if(result == null)
		{
			// if writing to the cache is allowed then we try to grab a writable byte channel
			// to lock other threads from writing to it
			transactionContext.setItemCached(Boolean.FALSE);
			
			result = streamImageFromDatasource(outStream, allowCacheWrite);
		}	

		// caching is disabled or unusable for this instance
		// stream directly from cache
		if(result == null)
			result = streamImageFromDatasource(outStream, false);
		
		return result;
	}
	
	/**
	 * Stream the image from the data source into the cache.
	 * 
	 * @param outStream - if this is provided then the image will be streamed here as well as into the cache
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 * @throws IOException
	 * @throws CacheException
	 */
	private ImageMetadata streamImageFromDatasource(OutputStream outStream, boolean writeToCache)
	throws MethodException, ConnectionException, IOException, InstanceInaccessibleException, CacheException 
	{
		ImmutableInstance instance = null;
		InstanceWritableByteChannel instanceWritableChannel = null;
		OutputStream cacheOutStream = null;
		ImageFormat dataSourceImageFormat = null;
		ImageQuality dataSourceImageQuality = null;
		long bytesReturned = 0L;
		String responseChecksum = null;
		
		try
		{
			ImageStreamResponse datasourceResponse = streamImageFromDataSource(getImageUrn(), getImageFormatQualityList());
			ImageFormat imgFormat = datasourceResponse.getImageFormat();
			getLogger().info("Received response from data source, putting into cache");
			// set the data source image format and image quality here 
			// since it is now in the cache.
			// JMW 10/6/2008
			// moved this here, if we get the image from the DS, need to use the format/quality from the DS to put/get the image from the cache
			// only clear these values if there is a cache exception (error writing to the cache)
			dataSourceImageFormat = imgFormat;
			dataSourceImageQuality = datasourceResponse.getImageQuality();
			
			if(writeToCache)
			{
				getLogger().debug("Attempting to create cache instance with format ["  + dataSourceImageFormat + "] and quality [" + dataSourceImageQuality + "]");
				
				if(!imageUrn.isOriginVA())
					instance = getCommandContext().getExtraEnterpriseCache().createImage(imageUrn, dataSourceImageQuality.name(), dataSourceImageFormat.getMimeWithEnclosedMime());
				else
					instance = getCommandContext().getIntraEnterpriseCacheCache().createImage(imageUrn, dataSourceImageQuality.name(), dataSourceImageFormat.getMimeWithEnclosedMime());
		
				instanceWritableChannel = instance.getWritableChannel();
				cacheOutStream = Channels.newOutputStream(instanceWritableChannel);
			}
			
			InputStream imageStream = datasourceResponse.getImageStream().getInputStream();
			
			if(outStream != null && cacheOutStream != null)
			{
				getLogger().info("Pumping stream into cache and output stream simultaneously.");
				WritableByteChannelSpreaderPump pump = 
					new WritableByteChannelSpreaderPump(imageStream, new OutputStream[]{outStream, cacheOutStream});
				
				bytesReturned = pump.copy();
			}
			else if(outStream != null)
			{
				getLogger().info("Pumping stream into output stream only.");
				ByteStreamPump pump = ByteStreamPump.getByteStreamPump(ByteStreamPump.TRANSFER_TYPE.NetworkToNetwork);
				// if the cacheStream is null the ByteStreamPump will ignore it
				bytesReturned = pump.xfer(imageStream, outStream);
			}
			else if(cacheOutStream != null)
			{
				getLogger().info("Pumping stream into cache only.");
				ByteStreamPump pump = ByteStreamPump.getByteStreamPump(ByteStreamPump.TRANSFER_TYPE.NetworkToNetwork);
				// if the cacheStream is null the ByteStreamPump will ignore it
				bytesReturned = pump.xfer(imageStream, cacheOutStream);
			}
			else
			{
				getLogger().info("Pumping stream into bit bucket.");
				// nobody wants the data, just return the metadata
				ByteStreamPump pump = ByteStreamPump.getByteStreamPump(ByteStreamPump.TRANSFER_TYPE.NetworkToNetwork);
				bytesReturned = pump.xfer(imageStream, new NullOutputStream());
			}
			
			// close the input stream
			if(imageStream != null)
				try{imageStream.close();}catch(IOException ioX){}
			
			// get the checksum before closing the stream, else it won't be available
			responseChecksum = datasourceResponse.getProvidedImageChecksum();
		}
		finally
		{
			// close the output stream to the cache
			if(cacheOutStream != null)
				try{cacheOutStream.close();}catch(IOException ioX){}
		}
		
		validateAndLogChecksum(instance, responseChecksum);
		return new ImageMetadata(getImageUrn(), dataSourceImageFormat, responseChecksum, bytesReturned, bytesReturned);
	}

	/**
	 * 
	 * @param transactionContext
	 * @return the length of the image in bytes or -1 if unable to satisfy the request from the cache
	 * @throws IOException
	 */
	private ImageMetadata streamImageFromCache(OutputStream outStream) 
	throws IOException 
	{
		PrivateImageMetadataNotification notification = new PrivateImageMetadataNotification();

		// no data requested, just metadata
		if(outStream == null)
		{
			getLogger().info("Image '" + getImageUrn().toString() + "' is already cached, not returning image data.");
			CommonImageCacheFunctions.populateImageMetadata(
				getCommandContext(), getImageUrn(), getImageFormatQualityList(), notification
			);
		}
		else
		{
			StreamImageFromCacheResponse response = CommonImageCacheFunctions.streamImageFromCache(
				getCommandContext(), getImageUrn(), getImageFormatQualityList(), outStream, notification);
			
			if((response != null) && (response.getBytesReturnedFromDataSource() > 0))
			{
				getLogger().info("Image '" + getImageUrn().toString() + "' found in the cache and streamed to the destination.");
			}
		}
		
		return notification.getImageMetadata();
	}

	/**
	 * 
	 * @param instance
	 * @param responseChecksum
	 */
	private void validateAndLogChecksum(
		ImmutableInstance instance,
		String responseChecksum) 
	{
		boolean noResponseChecksum = false;
		if(responseChecksum != null)
			noResponseChecksum = (responseChecksum.equals("ok") || responseChecksum.equals("not ok"));
		
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

	/**
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

	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((imageFormatQualityList == null) ? 0
						: imageFormatQualityList.hashCode());
		result = prime * result
				+ ((imageUrn == null) ? 0 : imageUrn.hashCode());
		result = prime
				* result
				+ ((messageFormatter == null) ? 0 : messageFormatter.hashCode());
		result = prime * result
				+ ((outStream == null) ? 0 : outStream.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return areClassSpecificFieldsEqual(obj);
	}

	@Override
	protected boolean areClassSpecificFieldsEqual(Object obj) 
	{
		HeadInstanceByImageUrnCommandImpl other = (HeadInstanceByImageUrnCommandImpl) obj;
		if (imageFormatQualityList == null) {
			if (other.imageFormatQualityList != null)
				return false;
		} else if (!imageFormatQualityList.equals(other.imageFormatQualityList))
			return false;
		if (imageUrn == null) {
			if (other.imageUrn != null)
				return false;
		} else if (!imageUrn.equals(other.imageUrn))
			return false;
		if (messageFormatter == null) {
			if (other.messageFormatter != null)
				return false;
		} else if (!messageFormatter.equals(other.messageFormatter))
			return false;
		if (outStream == null) {
			if (other.outStream != null)
				return false;
		} else if (!outStream.equals(other.outStream))
			return false;
		return true;
	}

	private class PrivateImageMetadataNotification
	implements ImageMetadataNotification
	{
		ImageMetadata result = null;
		
		@Override
		public void imageMetadata(String checksum, ImageFormat imageFormat, int fileSize, ImageQuality imageQuality) 
		{
			result = new ImageMetadata(getImageUrn(), imageFormat, checksum, fileSize, 0L);
		}
		
		public ImageMetadata getImageMetadata(){return result;}
		
		public void setBytesTransferred(long bytesTransferred)
		{
			if(result != null)
				result = new ImageMetadata(getImageUrn(), result.getImageFormat(), result.getChecksum(), result.getSize(), bytesTransferred);
			else
				result = new ImageMetadata(getImageUrn(), null, null, 0L, bytesTransferred);
		}
	};
	
}
