/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 16, 2011
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
package gov.va.med.imaging.exchange.business.annotations;

/**
 * Describes the source application which created the annotation.
 * @author VHAISWWERFEJ
 *
 */
public enum ImageAnnotationSource
{
	/**
	 * Created by the VistA Imaging Clinical Display client
	 */
	clinicalDisplay("CLINICAL_DISPLAY"),
	/**
	 * Created by the VistA Imaging VistARad application
	 */
	vistaRad("VISTARAD"),
	/**
	 * Created by the VistA Imaging Clinical Capture application
	 */
	clinicalCapture("CLINICAL_CAPTURE");
	
	final String encodedValue;
	
	ImageAnnotationSource(String encodedValue)
	{
		this.encodedValue = encodedValue;
	}

	/**
	 * Return the encoded value
	 * @return
	 */
	public String getEncodedValue()
	{
		return encodedValue;
	}

	/**
	 * Create the enumeration from the encoded value
	 * @param encodedValue
	 * @return
	 */
	public static ImageAnnotationSource getFromEncodedValue(String encodedValue)
	{
		for(ImageAnnotationSource source : ImageAnnotationSource.values())
		{
			if(source.encodedValue.equals(encodedValue))
			{
				return source;
			}
		}
		return null;
	}
}
