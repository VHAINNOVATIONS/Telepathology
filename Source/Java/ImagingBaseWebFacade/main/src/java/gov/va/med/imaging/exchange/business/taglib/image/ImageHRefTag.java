/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Feb 4, 2008
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
package gov.va.med.imaging.exchange.business.taglib.image;

import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.business.Image;
import gov.va.med.imaging.exchange.enums.ImageFormat;
import gov.va.med.imaging.exchange.enums.ImageQuality;

import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;

/**
 * This tag will generate a String that can be used as an href to an image.
 * It needs, by default, only the application path (servlet mapping ) of the WAI servlet.
 * The image specification is determined by the surrounding AbstractImageTag.
 * The quality and accept type may be set using tag properties, or if left blank
 * will be defaulted.
 * 
 * @author VHAISWBECKEC
 */
public class ImageHRefTag 
extends AbstractImagePropertyTag
{
	private static final long serialVersionUID = 1L;
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	private final static String imageUrnParameterKey = "<imageUrn>";
	private final static String imageQualityParameterKey = "<imageQuality>";
	private final static String contentTypeParameterKey = "<contentType>";

	private final static String defaultContextBase = "";
	private final static String defaultQueryStringPattern = 
		"imageUrn=" + imageUrnParameterKey + 
		"&imageQuality=" + imageQualityParameterKey + 
		"&contentType=" + contentTypeParameterKey;
	
	private String contextBase = defaultContextBase;
	private String applicationPath;
	private String queryStringPattern = defaultQueryStringPattern;
	private ImageQuality imageQuality = ImageQuality.THUMBNAIL;
	private ImageFormat contentType = ImageFormat.JPEG;
	
	/**
	 * Set/Get the context portion of the URL.  This property is optional
	 * and, if not specified will default to a zero-length string.
	 * 
	 * @return
	 */
	public String getContextBase()
    {
    	return contextBase == null || contextBase.length() == 0 ? defaultContextBase : contextBase;
    }
	public void setContextBase(String contextBase)
    {
    	this.contextBase = contextBase;
    }

	/**
	 * This property must be specified.
	 * The portion of the URL that specifies the path within the application.
	 * The contextPath should be used to specify the host, port and application name if
	 * the path is external to the application (else left blank).
	 * @return
	 */
	public String getApplicationPath()
    {
    	return applicationPath;
    }
	public void setApplicationPath(String applicationPath)
    {
    	this.applicationPath = applicationPath;
    }

	/**
	 * The href is formed by substituting the enclosing image tags identifying information
	 * into the UrlRegex string.  The regex should include the following tags where the values
	 * are to be substituted:
	 * <imageUrn>
	 * <imageQuality>
	 * <contentType>
	 * 
	 * If this property is not specified then the following values is used: 
	 * "imageUrn=<imageUrn>&imageQuality=<imageQuality>&contentType=<contentType>;
	 * 
	 * @return
	 */
	public String getQueryStringPattern()
    {
    	return queryStringPattern == null || queryStringPattern.length() == 0 ? defaultQueryStringPattern : queryStringPattern;
    }
	public void setQueryStringPattern(String urlRegex)
    {
    	this.queryStringPattern = urlRegex;
    }

	/**
	 * Specifies the image quality desired in the response stream.
	 * Must be one of:
	 * "THUMBNAIL"
	 * "REFERENCE"
	 * "DIAGNOSTIC"
	 * "DIAGNOSTICUNCOMPRESSED"
	 * or an integer number from 1 to 100
	 * 
	 * @return
	 */
	public String getImageQuality()
    {
    	return imageQuality.name();	// 16Sep2008 CTB name() changed from toString()
    }
	public void setImageQuality(String imageQualityExternalForm)
    {
		try
		{
			this.imageQuality = ImageQuality.valueOf(imageQualityExternalForm);
		}
		catch(IllegalArgumentException iaX)
		{
			try
            {
	            int qualityQValue = Integer.parseInt(imageQualityExternalForm);
	            this.imageQuality = ImageQuality.getImageQuality(qualityQValue);
            } 
			catch (NumberFormatException e)
            {
				logger.warn(
					"Image Quality '" + imageQualityExternalForm + 
					"' is neither a valid ImageQuality or number between 1 and 100, defaulting to DIAGNOSTIC.");
				this.imageQuality = ImageQuality.DIAGNOSTIC;
            }
		}
    }
	/**
	 * 
	 * @return
	 */
	public String getContentType()
    {
    	return contentType.toString();
    }
	
	public void setContentType(String contentTypeExternalForm)
    {
    	try
        {
	        this.contentType = ImageFormat.valueOf(contentTypeExternalForm);
        } 
    	catch (IllegalArgumentException iaX)
        {
			logger.warn(
				"Content Type '" + contentTypeExternalForm + 
				"' is not a valid ImageFormat, defaulting to DICOMJPEG2000.");
	        this.contentType = ImageFormat.DICOMJPEG2000;
        }
    }
	/**
	 * @see gov.va.med.imaging.exchange.business.taglib.image.AbstractImagePropertyTag#getElementValue()
	 */
	@Override
	protected String getElementValue() 
	throws JspException
	{
		String queryString = getQueryStringPattern();
		
		Image image = this.getImage();
		String imageUrnExternal = null;
		imageUrnExternal = image.getImageUrn().toString();
		queryString = queryString.replace(imageUrnParameterKey, imageUrnExternal);
		queryString = queryString.replace(imageQualityParameterKey, Integer.toString(imageQuality.getCanonical()) );
		queryString = queryString.replace(contentTypeParameterKey, contentType.getMime());
		
		StringBuilder sb = new StringBuilder();
		if(getContextBase() != null && getContextBase().length() > 0)
			sb.append(getContextBase());
		if(sb.length() == 0 || '/' != sb.charAt(sb.length()-1))
			sb.append("/");
		sb.append(getApplicationPath());
		sb.append("?");
		sb.append(queryString);
		
		return sb.toString();
	}

}
