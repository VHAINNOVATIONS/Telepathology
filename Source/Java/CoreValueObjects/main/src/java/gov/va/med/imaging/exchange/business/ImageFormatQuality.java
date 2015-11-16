/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr 16, 2008
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

import java.io.Serializable;
import gov.va.med.imaging.exchange.enums.ImageFormat;
import gov.va.med.imaging.exchange.enums.ImageQuality;

/**
 * An ImageFormatQuality is a specific image format and the quality that represents an image
 * 
 * @author VHAISWWERFEJ
 *
 */
public class ImageFormatQuality
implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private final ImageFormat imageFormat;
	private final ImageQuality imageQuality;
	
	public ImageFormatQuality(ImageFormat format, ImageQuality quality)
	{
		this.imageFormat = format;
		this.imageQuality = quality;
	}

	public ImageFormat getImageFormat() {
		return imageFormat;
	}

	public ImageQuality getImageQuality() {
		return imageQuality;
	}

	@Override
    public int hashCode()
    {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((imageFormat == null) ? 0 : imageFormat.hashCode());
	    result = prime * result + ((imageQuality == null) ? 0 : imageQuality.hashCode());
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
	    final ImageFormatQuality other = (ImageFormatQuality) obj;
	    if (imageFormat == null)
	    {
		    if (other.imageFormat != null)
			    return false;
	    } else if (!imageFormat.equals(other.imageFormat))
		    return false;
	    if (imageQuality == null)
	    {
		    if (other.imageQuality != null)
			    return false;
	    } else if (!imageQuality.equals(other.imageQuality))
		    return false;
	    return true;
    }

	
}
