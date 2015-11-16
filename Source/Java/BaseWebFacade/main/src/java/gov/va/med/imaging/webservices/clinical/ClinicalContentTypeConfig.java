/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Oct 10, 2008
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
package gov.va.med.imaging.webservices.clinical;

import gov.va.med.imaging.exchange.enums.ImageQuality;
import gov.va.med.imaging.exchange.enums.VistaImageType;

/**
 * Defines a configuration for the content type of a specific image type, quality and interface
 * version. 
 * 
 * @author VHAISWWERFEJ
 *
 */
public class ClinicalContentTypeConfig 
{
	
	private VistaImageType imageType;
	private ImageQuality imageQuality;
	private String contentType;
	
	public ClinicalContentTypeConfig()
	{
		super();
		imageType = null;
		imageQuality = null;
		contentType = "";
	}	
	
	public ClinicalContentTypeConfig(VistaImageType imageType,
			ImageQuality imageQuality) 
	{
		super();
		this.imageType = imageType;
		this.imageQuality = imageQuality;
		this.contentType = "";
	}
	
	public ClinicalContentTypeConfig(VistaImageType imageType,
			ImageQuality imageQuality, String contentType) 
	{
		super();
		this.imageType = imageType;
		this.imageQuality = imageQuality;
		this.contentType = contentType;
	}

	/**
	 * @return the imageType
	 */
	public VistaImageType getImageType() {
		return imageType;
	}
	/**
	 * @param imageType the imageType to set
	 */
	public void setImageType(VistaImageType imageType) {
		this.imageType = imageType;
	}
	/**
	 * @return the imageQuality
	 */
	public ImageQuality getImageQuality() {
		return imageQuality;
	}
	/**
	 * @param imageQuality the imageQuality to set
	 */
	public void setImageQuality(ImageQuality imageQuality) {
		this.imageQuality = imageQuality;
	}

	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}
