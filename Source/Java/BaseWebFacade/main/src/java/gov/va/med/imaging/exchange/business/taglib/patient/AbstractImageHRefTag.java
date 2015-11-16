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
package gov.va.med.imaging.exchange.business.taglib.patient;

import gov.va.med.imaging.exchange.business.Patient;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;

/**
 * This tag will generate a String that can be used as an href to an image.
 * It needs, by default, only the application path (servlet mapping ) of the WAI servlet.
 * The image specification is determined by the surrounding AbstractPatientTag.
 * The quality and accept type may be set using tag properties, or if left blank
 * will be defaulted.
 * Derived classes of this tag specify the image quality (Thumbnail, Reference, and Diagnostic).
 * 
 * @author VHAISWBECKEC
 */
public abstract class AbstractImageHRefTag 
extends AbstractPatientPropertyTag
{
	// a derived class may specify this key
	// in the pathInfoPattern
	protected final static String patientIcnParameterKey = "[patientIcn]";

	protected abstract String getDefaultPathInfoPattern();
	
	private String host = null;
	private String context = null;
	private String pathInfoPattern = getDefaultPathInfoPattern();
	private String protocolOverride;
	private String targetSite;
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	/**
	 * Set/Get the host portion of the URL.  This property is optional
	 * and, if not specified will default to a null, and will produce relative
	 * references.
	 * 
	 * @return
	 */
	public String getHost()
    {
    	return host;
    }
	public void setHost(String host)
    {
    	this.host = host;
    }
	
	/**
	 * Set/Get the context portion of the URL.  This property is optional
	 * and, if not specified will default to a null, and will produce relative
	 * references.
	 * 
	 * The value returned from here must be consistent with the getContext() 
	 * method of HttpServletRequest.
	 * 
	 * @return
	 */
	public String getContext()
    {
    	return context == null ? 
    			null : 
    			context.length() == 0 ? "/" :
    				context.charAt(0) == '/' ?  
    				context :
    				("/" + context);
    }
	public void setContextBase(String contextBase)
    {
    	this.context = contextBase;
    }

	/**
	 * The href is formed by substituting the enclosing image tags identifying information
	 * into the UrlRegex string.  The regex should include the following tags where the values
	 * are to be substituted:
	 * <imageUrn>
	 * 
	 * If this property is not specified then the following values is used: 
	 * "<imageUrn>"
	 * 
	 * @return
	 */
	public String getPathInfoPattern()
    {
    	return pathInfoPattern == null || pathInfoPattern.length() == 0 ? getDefaultPathInfoPattern() : pathInfoPattern;
    }
	public void setPathInfoPattern(String urlRegex)
    {
    	this.pathInfoPattern = urlRegex;
    }

	/**
     * @return the protocolOverride
     */
    public String getProtocolOverride()
    {
    	return protocolOverride;
    }
    
	/**
     * @param protocolOverride the protocolOverride to set
     */
    public void setProtocolOverride(String protocolOverride)
    {
    	this.protocolOverride = protocolOverride;
    }
    
	/**
     * @return the targetSite
     */
    public String getTargetSite()
    {
    	return targetSite;
    }
    
	/**
     * @param targetSite the targetSite to set
     */
    public void setTargetSite(String targetSite)
    {
    	this.targetSite = targetSite;
    }
    
	/**
	 * @see gov.va.med.imaging.exchange.business.taglib.image.AbstractImagePropertyTag#getElementValue()
	 */
	@Override
	protected String getElementValue() 
	throws JspException
	{
		String requestContext = null;
		try
        {
	        ServletRequest servletRequest = this.pageContext.getRequest();
	        HttpServletRequest req = (HttpServletRequest)servletRequest;
	        requestContext = req.getContextPath();
        } 
		catch (ClassCastException e1)
        {
			logger.warn("Unable to cast request to HttpServletRequest, tag library expects to be running over HTTP, continuing ...");
        } 
		
		String pathInfo = buildPathInfo();
		
		// build the path to the image servlet
		StringBuilder sb = new StringBuilder();
		
		// if the host is specified then append the host name 
		if(getHost() != null )
			sb.append(getHost());
		
		// if the context base is specified then use that value
		// else use this request's context
		if(getContext() != null )
			sb.append(getContext());
		// append a '/' if one is not there already and a context was specified
		if(sb.length() != 0 && '/' != sb.charAt(sb.length()-1))
			sb.append("/");
		
		// always append the path info
		sb.append(pathInfo);

		// if the protocol override AND the target site are provided then
		// tack them onto the URL as query parameters
		if(getProtocolOverride() != null && getProtocolOverride().length() > 0 && 
			getTargetSite() != null && getTargetSite().length() > 0	)
		{
			sb.append("?");
			sb.append("protocolOverride=");
			sb.append(getProtocolOverride());
			sb.append("&");
			sb.append("targetSite=");
			sb.append(getTargetSite());
		}
		
		return sb.toString();
	}
	
	/**
	 * Build the pathInfo portion of the image URL using the pattern
	 * in getPathInfo and the image URN or the patient ICN values from the
	 * ancestor Image element.
	 * 
     * @return
     * @throws JspException
     */
    private String buildPathInfo() 
    throws JspException
    {
	    String pathInfo = getPathInfoPattern();
		
		Patient patient = this.getPatient();
		
		if(pathInfo.contains(patientIcnParameterKey))
		{
			String patientIcnExternal = null;
			
			patientIcnExternal = patient.getPatientIcn();
			pathInfo = pathInfo.replace(patientIcnParameterKey, patientIcnExternal);
		}
		
	    return pathInfo;
    }

}
