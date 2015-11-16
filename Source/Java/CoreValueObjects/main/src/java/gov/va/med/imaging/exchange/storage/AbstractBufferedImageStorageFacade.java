/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 30, 2008
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
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
package gov.va.med.imaging.exchange.storage;

import gov.va.med.imaging.core.interfaces.ImageStorageFacade;
import gov.va.med.imaging.core.interfaces.StorageCredentials;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageConversionException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNearLineException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNotFoundException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.ImageFormatQualityList;
import gov.va.med.imaging.exchange.enums.ImageQuality;
import gov.va.med.imaging.exchange.enums.StorageProximity;

import org.apache.log4j.Logger;

/**
 * Abstract storage facade that uses buffer pool lists and buffers the image data in memory to make
 * re-using the data more efficient.
 * 
 * @author VHAISWWERFEJ
 *
 */
public abstract class AbstractBufferedImageStorageFacade 
implements ImageStorageFacade 
{
	private final static Logger logger = Logger.getLogger(AbstractBufferedImageStorageFacade.class);

	// Member variables	
	protected ByteBufferBackedImageInputStream imageBuffer = null;
	protected ByteBufferBackedInputStream txtBuffer = null;
	protected ImageQuality imageQuality = null;

	/**
	 * Abstract function to retrieve an open a ImageStreamResponse for an image that might include
	 * the TXT file
	 * 
	 * @param imageIdentifier
	 * @param imageCredentials
	 * @param imageProximity
	 * @param requestFormatQualityList
	 * @return
	 * @throws ImageNearLineException
	 * @throws ImageNotFoundException
	 * @throws ConnectionException
	 */
	protected abstract ByteBufferBackedImageStreamResponse openImageStreamInternal(String imageIdentifier,
		StorageCredentials imageCredentials,
		StorageProximity imageProximity,
		ImageFormatQualityList requestFormatQualityList)
	throws ImageNearLineException, ImageNotFoundException,
		ConnectionException, ImageConversionException, MethodException;
	
	/**
	 * Abstract function to retrieve an open sized input stream to a TXT file
	 * @param imageIdentifier
	 * @param imageCredentials
	 * @param imageProximity
	 * @return
	 * @throws ImageNearLineException
	 * @throws ImageNotFoundException
	 * @throws ConnectionException
	 */
	protected abstract ByteBufferBackedInputStream openTXTStreamInternal(String imageIdentifier,
		StorageCredentials imageCredentials, StorageProximity imageProximity)
	throws ImageNearLineException, ImageNotFoundException,
		ConnectionException, MethodException;

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.ImageStorageFacade#openImageStream(java.lang.String, gov.va.med.imaging.core.interfaces.StorageCredentials, gov.va.med.imaging.exchange.enums.StorageProximity, gov.va.med.imaging.exchange.business.ImageFormatQualityList)
	 */
	@Override
	public ByteBufferBackedImageStreamResponse openImageStream(String imageIdentifier,
		StorageCredentials imageCredentials,
		StorageProximity imageProximity,
		ImageFormatQualityList requestFormatQualityList)
	throws ImageNearLineException, ImageNotFoundException,
		ConnectionException, ImageConversionException, MethodException
	{
		if(imageBuffer == null)
		{
			logger.info("Image buffer is null, retrieving Image Stream from storage device");
			ByteBufferBackedImageStreamResponse response = openImageStreamInternal(imageIdentifier, 
				imageCredentials, imageProximity, requestFormatQualityList);
			imageQuality = response.getImageQuality();
			imageBuffer = response.getImageStream();
			if((txtBuffer == null) && (response.getTxtStream() != null))
			{
				txtBuffer = response.getTxtStream();
			}
		}
		else
		{
			logger.info("Image already buffered, using buffer data in response");
		}
		ByteBufferBackedImageStreamResponse response = new ByteBufferBackedImageStreamResponse(imageBuffer);
		response.setImageQuality(imageQuality);
		
		if(txtBuffer != null)
		{
			response.setTxtStream(txtBuffer);
		}
		return response;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.ImageStorageFacade#openTXTStream(java.lang.String, gov.va.med.imaging.core.interfaces.StorageCredentials, gov.va.med.imaging.exchange.enums.StorageProximity)
	 */
	@Override
	public ByteBufferBackedInputStream openTXTStream(String imageIdentifier,
		StorageCredentials imageCredentials, StorageProximity imageProximity)
	throws ImageNearLineException, ImageNotFoundException,
		ConnectionException, MethodException
	{
		if(txtBuffer == null)
		{
			logger.info("Txt buffer is null, retrieving TXT Stream from storage device");
			txtBuffer = openTXTStreamInternal(imageIdentifier, 
				imageCredentials, imageProximity);			
		}
		else
		{
			logger.info("TXT File already buffered, using buffer data in response");
		}
		return txtBuffer;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.ImageStorageFacade#clearBuffers()
	 */
	@Override
	public void clearBuffers() 
	{
		clearImageBuffer();
		clearTxtBuffer();
	}
	
	private void clearImageBuffer()
	{
		if(imageBuffer != null)
		{
			// JMW 3/8/2010 - fix for corrupted image problem - buffer never goes back into pool, will always create a new buffer when needed
			//getBufferPool().releaseBuffer(imageBuffer.getBuffer());
			imageBuffer = null;
		}
		imageQuality = null;
	}
	
	private void clearTxtBuffer()
	{
		if(txtBuffer != null)
		{
			// JMW 3/8/2010 - fix for corrupted image problem - buffer never goes back into pool, will always create a new buffer when needed
			//getBufferPool().releaseBuffer(txtBuffer.getBuffer());
			txtBuffer = null;
		}
	}
}
