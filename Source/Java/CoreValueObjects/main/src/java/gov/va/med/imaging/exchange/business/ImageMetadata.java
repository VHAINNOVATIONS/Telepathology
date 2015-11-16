/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Mar 12, 2008
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author VHAISWBECKEC
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */
package gov.va.med.imaging.exchange.business;

import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.exchange.enums.ImageFormat;

import java.io.Serializable;

/**
 * A value object containing information about an image.
 * Was deprecated, resurrected to support HEAD method.
 * 
 * @author VHAISWBECKEC
 *
 */
public class ImageMetadata
implements Serializable
{
	private static final long serialVersionUID = 1L;
	private final ImageURN imageUrn;
	private final ImageFormat imageFormat;
	private final String checksum;
	private final long size;
	private final long bytesTransferred;

	/**
	 * Build an instance where the image is a composite containing one or more
	 * images (e.g. application/dicom wrapping image/j2k).
	 * 
	 * @param mimeType
	 * @param embeddedMimeTypes
	 * @param checksum
	 * @param size
	 * @param bytesTransferred
	 */
	public ImageMetadata(
		ImageURN imageUrn, 
		ImageFormat imageFormat, 
		String checksum, 
		long size, 
		long bytesTransferred)
    {
	    super();
	    this.imageUrn = imageUrn;
	    this.imageFormat = imageFormat;
	    this.checksum = checksum;
	    this.size = size;
	    this.bytesTransferred = bytesTransferred;
    }

	/**
	 * THe ImageURN of the image that this metadata applies to
	 * @return
	 */
	protected ImageURN getImageUrn()
    {
    	return imageUrn;
    }

	public ImageFormat getImageFormat() {
		return imageFormat;
	}

	/**
	 * A checksum in String form, including the hash algorithm.
	 * @return
	 */
	public String getChecksum()
    {
    	return checksum;
    }

	/**
	 * The number of bytes in the Image.  May differ from bytesTransferred
	 * if there is padding  in transport but will probably be the same.
	 * @return
	 */
	public long getSize()
    {
    	return size;
    }

	/**
	 * During a request for an Image, the value in this property will
	 * indicate how many bytes were transferred over a Stream,
	 * @return
	 */
	public long getBytesTransferred()
    {
    	return bytesTransferred;
    }
}
