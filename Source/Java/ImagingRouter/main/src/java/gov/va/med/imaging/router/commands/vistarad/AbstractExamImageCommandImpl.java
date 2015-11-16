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
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNearLineException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNotFoundException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.ImageFormatQualityList;
import gov.va.med.imaging.exchange.business.ImageStreamResponse;
import gov.va.med.imaging.exchange.business.vistarad.ExamImage;
import gov.va.med.imaging.exchange.enums.ImageFormat;
import gov.va.med.imaging.exchange.storage.DataSourceInputStream;
import gov.va.med.imaging.router.commands.CommonImageCacheFunctions;
import gov.va.med.imaging.router.facade.ImagingContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author vhaiswwerfej
 *
 */
public abstract class AbstractExamImageCommandImpl<R extends Object> 
extends AbstractExamCommandImpl<R>
{
	private static final long serialVersionUID = -864205235890281633L;

	public AbstractExamImageCommandImpl()
	{
		super();
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
	throws MethodException, ConnectionException, IOException
	{
		ImageStreamResponse imageResponse = null;
		
		String imageId = imageUrn.getImageId();
		getLogger().info("Requesting exam image [" + imageId + "] from data source.");
		
		// try to get exam image metadata from cache
		ExamImage image = getExamImageFromCache(imageUrn);		
		if(image != null)
			imageResponse = ImagingContext.getRouter().getExamInstanceFromDataSource(image, requestFormatQualityList);
		else
			imageResponse = ImagingContext.getRouter().getExamInstanceFromDataSource(imageUrn, requestFormatQualityList);
		
		if(imageResponse == null)
			throw new ImageNotFoundException("Image [" + imageId + "] not found");

		if(imageResponse.getImageStream() == null)
			throw new MethodException("No input stream returned from data source for image [" + imageId + "].");
		//inStream = sizedStream.getInStream();
		
		if(!imageResponse.getImageStream().isReadable())
			throw new MethodException("No input stream returned from data source for image [" + imageId + "].");
		
		CommonImageCacheFunctions.cacheTXTFile(getCommandContext(), imageUrn, imageResponse.getTxtStream(), false);
		ImageFormat curImgFormat = imageResponse.getImageFormat();
		getLogger().info("Image returned from datasource in format [" + curImgFormat + "]");
		return imageResponse;		
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
	protected int streamTXTFileFromDataSource(
			ImageURN imageUrn,
			OutputStream outStream) 
	throws MethodException, ConnectionException, 
	IOException
	{
		DataSourceInputStream imageResponse = null;
		InputStream txtStream = null;
		
		try
		{
			String imageId = imageUrn.getImageId();
			getLogger().info("Requesting txt file for exam image [" + imageId + "] from data source.");
			
			// try to get exam image metadata from cache
			ExamImage image = getExamImageFromCache(imageUrn);		
			if(image != null)
				imageResponse = ImagingContext.getRouter().getExamTextFileFromDataSource(image);
			else
				imageResponse = ImagingContext.getRouter().getExamTextFileFromDataSource(imageUrn);
			
			if(imageResponse == null)
				throw new MethodException("No input stream returned from data source for TXT file [" + imageId + "].");
			
			if(!imageResponse.isReadable())
				throw new MethodException("Unreadable input stream returned from data source for TXT file [" + imageId + "].");
			txtStream = imageResponse.getInputStream();
	
			// write the input stream to the output stream, which could be the destination output stream or
			// the cache output stream (in this method we don't know or care).
			ByteStreamPump pump = ByteStreamPump.getByteStreamPump(ByteStreamPump.TRANSFER_TYPE.NetworkToNetwork);
			// if the cacheStream is null the ByteStreamPump will ignore it
			return pump.xfer(txtStream, outStream);	
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
