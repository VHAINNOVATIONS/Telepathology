/**
 * 
 */
package gov.va.med.imaging.router.commands;

import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.StudyURN;
import gov.va.med.imaging.channels.ByteStreamPump;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageConversionException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNearLineException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNotFoundException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.router.Command;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.ImageAccessLogEvent;
import gov.va.med.imaging.exchange.business.Image;
import gov.va.med.imaging.exchange.business.ImageFormatQuality;
import gov.va.med.imaging.exchange.business.ImageFormatQualityList;
import gov.va.med.imaging.exchange.business.ImageStreamResponse;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.business.util.ExchangeUtil;
import gov.va.med.imaging.exchange.enums.ImageFormat;
import gov.va.med.imaging.exchange.enums.ImageQuality;
import gov.va.med.imaging.exchange.storage.DataSourceInputStream;
import gov.va.med.imaging.router.facade.ImagingContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * An abstract superclass of Study-related commands, grouped because there is significant
 * overlap in the Study commands that is contained here.
 * 
 * @author vhaiswbeckec
 *
 */
public abstract class AbstractImageCommandImpl<R extends Object> 
extends AbstractStudyCommandImpl<R>
{
	private static final long serialVersionUID = 6423391034697600460L;

	/**
	 * @param commandContext - the context available to the command
	 */
	public AbstractImageCommandImpl()
	{
		super();
	}

	/**
	 * This method should be called by any command that accesses an Image.
	 * Externally, in ClinicalDisplay only for now, an ImageAccessEvent may also 
	 * be created and posted by creating an instance of the PostImageAccessEvent
	 * implementation and submitting it for processing.
	 * 
	 * @param imageUrn
	 * @param imageQuality
	 * @throws MethodException
	 */
	protected void logImageAccessEvent(ImageURN imageUrn, ImageQuality imageQuality) 
	throws MethodException
	{
		// we don't log access to thumbnail images
		if(imageQuality == ImageQuality.THUMBNAIL)
		{
			getLogger().debug( "logImageAccess - Transaction ID [" + TransactionContextFactory.get().getTransactionId() + "], accessed thumbnail image [" + imageUrn.toString() + "], skipping logging." );
		}
		else
		{
			getLogger().info("RouterImpl.logImageAccess(" + imageUrn.toString() + ") by [" + TransactionContextFactory.get().getTransactionId() + "].");
			String siteNumber = imageUrn.getOriginatingSiteId();
			
			boolean dodImage = false;		
			if(ExchangeUtil.isSiteDOD(siteNumber))
				dodImage = true;
	
			// use originating site number here
			/*
			ImageAccessLogEvent logEvent = new ImageAccessLogEvent(imageId, "",
					imageUrn.getPatientIcn(), imageUrn.getOriginatingSiteId(), System.currentTimeMillis(), 
					"", ImageAccessLogEventType.IMAGE_ACCESS, dodImage);
			
			logImageAccessEvent(logEvent);
			*/
			getLogger().fatal("Should not be doing logging from within the image command");
		}
	}

	/**
	 * This method (or the above version) should be called by any command that 
	 * accesses an Image.
	 *  
	 * @param event
	 * @throws MethodException
	 */
	protected void logImageAccessEvent(ImageAccessLogEvent event)
	throws MethodException
	{
		Command<java.lang.Void> command = (Command<java.lang.Void>)  
			getCommandContext().getCommandFactory().createCommand(java.lang.Void.class, 
					"PostImageAccessEventCommand", null, 
					new Class<?>[]{ImageAccessLogEvent.class}, new Object[]{event});
		getCommandContext().getRouter().doAsynchronously(command);
	}

	/**
	 * 
	 * @param imageUrn
	 *            Unique identifier of the image to request from the data source
	 * @param qualityValue
	 * @param contentType
	 * @param outStream
	 * @return a String value indicating the checksum and checksum algorithm
	 * @see gov.va.med.imaging.ChecksumValue returns "ok" or "not ok" if
	 *      checksum was calculated for data source stream before compression
	 * @throws MethodException
	 * @throws IOException
	 * @throws ImageNearLineException
	 */
	protected ImageStreamResponse streamImageFromDataSource(
			ImageURN imageUrn, 
			ImageFormatQualityList requestFormatQualityList) 
	throws MethodException, ImageConversionException, IOException, 
	ImageNearLineException, ImageNotFoundException
	{
		//StreamImageFromCacheResponse response = new StreamImageFromCacheResponse();
		//SizedInputStream sizedStream = null;
		//InputStream inStream = null;
		ImageStreamResponse imageResponse = null;
		
		String imageId = imageUrn.getImageId();
		
		getLogger().info(
				"Requesting image [" + imageId + "] of contentType [" + requestFormatQualityList.getAcceptString(true) + "]."
		);
		
		// if the parent Study is in the cache get the Image instance from there,
		// else we'll use the ImageURN later
		Image image = findImageInCachedStudyGraph(imageUrn);
		
		try
		{
			if(image != null)
			{
				imageResponse = ImagingContext.getRouter().getInstanceByImage(image, requestFormatQualityList);
				if((image.getAlienSiteNumber() != null) && (image.getAlienSiteNumber().length() > 0))
				{
					getLogger().info("Image contains alien site number, updating serviced source");
					TransactionContextFactory.get().setServicedSource(imageUrn.toRoutingTokenString() + "(" + image.getAlienSiteNumber() + ")");
				}
			}
			else
				imageResponse = ImagingContext.getRouter().getInstanceByImageUrn(imageUrn, requestFormatQualityList);
			
			if(imageResponse == null)
				throw new ImageNotFoundException("Image [" + imageId + "] not found");
	
			if(imageResponse.getImageStream() == null)
				throw new MethodException("No input stream returned from data source for image [" + imageId + "].");
			//inStream = sizedStream.getInStream();
			
			if(!imageResponse.getImageStream().isReadable())
				throw new MethodException("No input stream returned from data source for image [" + imageId + "].");
			// at this point the image has been returned
			CommonImageCacheFunctions.cacheTXTFile(getCommandContext(), imageUrn, imageResponse.getTxtStream(), false);
			
			ImageFormat curImgFormat = imageResponse.getImageFormat();
			getLogger().info("Image returned from datasource in format [" + curImgFormat + "]");
			return imageResponse;	
		}
		catch(ConnectionException cX)
		{
			throw new IOException(cX);
		}
	}	
	
	/**
	 * A method that simply makes the existence of the instance known to a derived command.
	 * 
	 * @param imageUrn
	 * @param imageQuality
	 * @param targetFormat
	 * @return
	 */
	protected boolean isInstanceInCache(
		ImageURN imageUrn, 
		ImageFormatQualityList requestAcceptList)
	{
		boolean acceptableImageInCache = false;
		
		for(ImageFormatQuality requestQuality : requestAcceptList)
		{
			acceptableImageInCache |= (CommonImageCacheFunctions.getImmutableInstance(getCommandContext(), imageUrn, requestQuality.getImageQuality(), requestQuality.getImageFormat().getMimeWithEnclosedMime()) != null);
			if(acceptableImageInCache)
				break;
		}
		
		
		return acceptableImageInCache;
	}

	/**
	 * 
	 * @param imageUrn
	 * @param outStream
	 * @return
	 * @throws MethodException
	 * @throws IOException
	 * @throws ImageNearLineException
	 * @throws ImageNotFoundException
	 */
	public int streamTXTFileFromDataSource(
			ImageURN imageUrn,
			OutputStream outStream) 
	throws MethodException, IOException, ImageNearLineException, ImageNotFoundException
	{
		InputStream txtStream = null;
		DataSourceInputStream imageResponse = null;
		String imageId = imageUrn.getImageId();
		String siteNumber = imageUrn.getOriginatingSiteId();
		getLogger().info("Requesting txt file [" + imageId + "] from site '" + siteNumber + "'.");
		
		Image image = null;
		try 
		{
			StudyURN studyUrn = imageUrn.getParentStudyURN();
			
			Study study = getStudyFromCache(studyUrn);
			if(study != null)
			{
				List<Image> images = extractImagesFromStudy(study);	
				int i = 0;
				boolean found = false;
				while((!found) && (i < images.size()))
				{
					Image img = images.get(i);
					if(img.getIen().equals(imageUrn.getImageId()))
					{
						image = img;
						found = true;
					}
					i++;
				}
			}
		}
		catch(URNFormatException iurnfX)
		{
			getLogger().error(iurnfX);
		}
		try
		{
			if(image != null)
			{
				imageResponse = ImagingContext.getRouter().getInstanceTextFileByImage(image);
			}
			else
			{
				imageResponse = ImagingContext.getRouter().getInstanceTextFileByImageUrn(imageUrn);
			}				
			//sizedStream = imageResponse.getInputStream();
			getLogger().info(
					"TXT file [" + imageId + "] " + (imageResponse==null ? "not found" : "found") + "."
			);
	
			
			
			if(imageResponse == null)
				throw new ImageNotFoundException("No input stream returned from data source for TXT file [" + imageId + "].");
			//inStream = sizedStream.getInStream();
			if(!imageResponse.isReadable())
				throw new MethodException("Unreadable input stream returned from data source for TXT file [" + imageId + "].");
			
	
			// write the input stream to the output stream, which could be the destination output stream or
			// the cache output stream (in this method we don't know or care).
			ByteStreamPump pump = ByteStreamPump.getByteStreamPump(ByteStreamPump.TRANSFER_TYPE.NetworkToNetwork);
			// if the cacheStream is null the ByteStreamPump will ignore it
			txtStream = imageResponse.getInputStream();
			return pump.xfer(txtStream, outStream);
		}
		catch(ConnectionException cX)
		{
			throw new IOException(cX);
		}
		finally
		{
			if(txtStream != null)
			{
				try
				{
					txtStream.close();					
				}
				catch(Exception ex) {getLogger().warn(ex); }
			}
		}
	}
}
