/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 1, 2008
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
package gov.va.med.imaging.exchange.business;

import gov.va.med.MediaType;
import gov.va.med.imaging.exchange.enums.ImageFormat;
import gov.va.med.imaging.exchange.enums.ImageQuality;
import gov.va.med.imaging.exchange.storage.DataSourceImageInputStream;
import gov.va.med.imaging.exchange.storage.DataSourceInputStream;
import gov.va.med.imaging.exchange.storage.exceptions.CannotCalculateChecksumException;

/**
 * Represents the response from streaming an image. Contains the input stream and the 
 * checksum for the image (if provided)
 * 
 * @author VHAISWWERFEJ
 *
 */
public class ImageStreamResponse 
{
	protected final DataSourceImageInputStream imageStream;
	protected DataSourceInputStream txtStream = null;
	private ImageQuality imageQuality;
	private MediaType mediaType;
	
	/**
	 * Create the ImageStreamResponse with a known image stream
	 * @param imageStream
	 */
	public ImageStreamResponse(DataSourceImageInputStream imageStream)
	{
		this(imageStream, null);
	}
	
	public ImageStreamResponse(DataSourceImageInputStream imageStream, ImageQuality imageQuality)
	{
		this(imageStream, null, imageQuality);
	}
	
	public ImageStreamResponse(DataSourceImageInputStream imageStream, 
			DataSourceInputStream txtStream, ImageQuality imageQuality)
	{
		this.imageStream = imageStream;
		this.imageQuality = imageQuality;
		this.txtStream = txtStream;
	}

	public DataSourceImageInputStream getImageStream() 
	{
		return imageStream;
	}

	public DataSourceInputStream getTxtStream() 
	{
		return txtStream;
	}

	public void setTxtStream(DataSourceInputStream txtStream) 
	{
		this.txtStream = txtStream;
	}

	/**
	 * Convenience method for calculating the text file checksum, if there is an error then null is returned
	 * @return
	 */
	public String getTxtChecksum() 
	{
		try
		{
			return txtStream == null || txtStream.getCalculatedChecksum() == null ?
				null :txtStream.getCalculatedChecksum().toString();
		}
		catch(CannotCalculateChecksumException cccX)
		{
			return null;
		}		
	}
	
	/**
	 * Convenience method for getting the provided image checksum.  If none was provided null is returned.
	 * @return
	 */
	public String getProvidedImageChecksum()
	{
		if(imageStream != null)
		{
			if(imageStream.isChecksumProvided())
				return imageStream.getProvidedChecksum().toString();
		}
		return null;
	}

	/**
	 * Convenience method to get the image format of the image.  If the image stream is null, null is returned
	 * @return the imageFormat
	 */
	public ImageFormat getImageFormat() 
	{
		if(imageStream != null)
			return imageStream.getImageFormat();
		return null;
	}
	
	/**
	 * Convenience method to get the size of the image.  If the image stream is null, 0 is returned.
	 * @return
	 */
	public int getImageSize()
	{
		if(imageStream != null)
			return imageStream.getSize();
		return 0;
	}

	public ImageQuality getImageQuality() {
		return imageQuality;
	}

	public void setImageQuality(ImageQuality imageQuality) {
		this.imageQuality = imageQuality;
	}

	/**
	 * @return the mediaType
	 */
	public MediaType getMediaType()
	{
		return this.mediaType;
	}

	/**
	 * @param mediaType the mediaType to set
	 */
	public void setMediaType(String mediaType)
	{
		this.mediaType = MediaType.lookup(mediaType);
	}
	public void setMediaType(MediaType mediaType)
	{
		this.mediaType = mediaType;
	}
}
