/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 1, 2008
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

import gov.va.med.imaging.exchange.enums.ImageFormat;

import java.util.ArrayList;


/**
 * This array list contains all of the formats the specified image format can be converted
 * into. These define rules to be used to determine if a given image format can be converted
 * into another format desired by the requestor. 
 * 
 * @author VHAISWWERFEJ
 *
 */
public class ImageFormatAllowableConversionList
extends ArrayList<ImageFormat>
{
	private static final long serialVersionUID = -3476929222406143637L;
	
	// cannot be final because used by XmlEncoder/XmlDecoder
	private ImageFormat imageFormat;
	private boolean canCompress;
	
	public ImageFormatAllowableConversionList()
	{
		super();
	}
	
	/**
	 * @param imageFormat The image format creating rules for.
	 * @param canCompress Determines if this image format can be compressed into a 
	 * compressed version of itself
	 */
	public ImageFormatAllowableConversionList(ImageFormat imageFormat, boolean canCompress)
	{
		this.imageFormat = imageFormat;
		this.canCompress = canCompress;
	}

	/**
	 * Return the internal image format the rules are defined for.
	 * @return
	 */
	public ImageFormat getImageFormat() 
	{
		return imageFormat;
	}	
	
	/**
	 * @param imageFormat the imageFormat to set
	 */
	public void setImageFormat(ImageFormat imageFormat) {
		this.imageFormat = imageFormat;
	}

	/**
	 * Determines if the current image format can be converted into the specified target format
	 * @param targetFormat
	 * @return
	 */
	public boolean isFormatConversionAllowed(ImageFormat targetFormat)
	{
		for(ImageFormat format : this)
		{
			if(format == targetFormat)
				return true;
		}
		return false;
	}

	/**
	 * Returns the canCompress value for this image format. This determines if a format can 
	 * be made into a compressed version of itself. For example a TGA cannot be compressed
	 * into a smaller TGA.
	 * @return
	 */
	public boolean isCanCompress() {
		return canCompress;
	}	

	/**
	 * @param canCompress the canCompress to set
	 */
	public void setCanCompress(boolean canCompress) {
		this.canCompress = canCompress;
	}

	@Override
	public String toString() 
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("Format [" + this.imageFormat + "] can be converted to:\n");
		for(ImageFormat format : this)
		{
			buffer.append("\t" + format + "\n");
		}
		return buffer.toString();
	}	
}
