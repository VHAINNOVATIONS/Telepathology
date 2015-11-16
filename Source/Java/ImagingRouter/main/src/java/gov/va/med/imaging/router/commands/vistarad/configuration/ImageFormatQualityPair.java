/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 10, 2010
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
package gov.va.med.imaging.router.commands.vistarad.configuration;

import gov.va.med.imaging.exchange.enums.ImageFormat;
import gov.va.med.imaging.exchange.enums.ImageQuality;

/**
 * This is a rediculous and stupid object which is the same thing as the ImageFormatQuality object.
 * The difference is the parameters here are not final.  This object is only used by the XmlEncoder 
 * which cannot have final parameters. This object SHOULD NOT be used outside of the VistaRadCommandConfiguration.
 * 
 * @author vhaiswwerfej
 *
 */
public class ImageFormatQualityPair
{
	private ImageFormat imageFormat;
	private ImageQuality imageQuality;
	
	public ImageFormatQualityPair()
	{
		super();
	}
	
	public ImageFormatQualityPair(ImageFormat imageFormat,
		ImageQuality imageQuality)
	{
		super();
		this.imageFormat = imageFormat;
		this.imageQuality = imageQuality;
	}

	/**
	 * @return the imageFormat
	 */
	public ImageFormat getImageFormat()
	{
		return imageFormat;
	}
	/**
	 * @param imageFormat the imageFormat to set
	 */
	public void setImageFormat(ImageFormat imageFormat)
	{
		this.imageFormat = imageFormat;
	}
	/**
	 * @return the imageQuality
	 */
	public ImageQuality getImageQuality()
	{
		return imageQuality;
	}
	/**
	 * @param imageQuality the imageQuality to set
	 */
	public void setImageQuality(ImageQuality imageQuality)
	{
		this.imageQuality = imageQuality;
	}
}
