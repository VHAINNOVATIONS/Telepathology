/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 17, 2009
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
package gov.va.med.imaging.conversion.enums;

/**
 * This enumeration determines the method the ImageConversionUtility uses to see what type of formats are allowed to be
 * returned among the requested format list. When presented with an image in a format, the ImageConversionUtility must
 * determine if that format is allowed to be returned to the requester. In all cases, the format must be in the
 * requesters requested format list but in certain cases the first (or next) format in the list must be satisfied and
 * in other cases any format in the list must be satisfied. This enumerations indicate to the ImageConversionUtility
 * how that decision is to be made. 
 * 
 * 
 * @author vhaiswwerfej
 *
 */
public enum ImageConversionSatisfaction 
{
	SATISFY_TOP_REQUEST("This indicates the user wants the first image format specified if possible, then moving down the list in order attempting to satisfy the next top requested format"),
	SATISFY_ANY_REQUEST("Indicates if the current image format is anywhere in the allowed list, it can be used regardless of placement"),
	SATISFY_ALLOWED_COMPRESSION("Indicates if the image is compressed and in the allowed list (anywhere), it can be returned");
	
	private final String description;
	
	ImageConversionSatisfaction(String description)
	{
		this.description = description;
	}

	/**
	 * @return the description of the satisfaction level
	 */
	public String getDescription() 
	{
		return description;
	}
}
