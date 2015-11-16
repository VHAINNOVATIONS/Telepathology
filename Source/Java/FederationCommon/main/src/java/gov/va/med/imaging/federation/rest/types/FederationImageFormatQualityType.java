/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 22, 2011
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
package gov.va.med.imaging.federation.rest.types;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author vhaiswwerfej
 *
 */
@XmlRootElement
public class FederationImageFormatQualityType
{
	// these fields are not the enumeration, this is intentional to provide flexibility and to not restrict
	// the possible enumeration types (don't want to redefine them here)
	private String imageFormat;
	private int imageQuality;
	
	public FederationImageFormatQualityType()
	{
		super();
	}

	public String getImageFormat()
	{
		return imageFormat;
	}

	public void setImageFormat(String imageFormat)
	{
		this.imageFormat = imageFormat;
	}

	public int getImageQuality()
	{
		return imageQuality;
	}

	public void setImageQuality(int imageQuality)
	{
		this.imageQuality = imageQuality;
	}

}
