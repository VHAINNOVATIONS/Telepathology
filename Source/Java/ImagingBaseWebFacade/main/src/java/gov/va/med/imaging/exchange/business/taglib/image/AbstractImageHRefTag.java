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

import gov.va.med.imaging.exchange.business.Image;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;

/**
 * This tag will generate a String that can be used as an href to an image.
 * It needs, by default, only the application path (servlet mapping ) of the WAI servlet.
 * The image specification is determined by the surrounding AbstractImageTag.
 * The quality and accept type may be set using tag properties, or if left blank
 * will be defaulted.
 * Derived classes of this tag specify the image quality (Thumbnail, Reference, and Diagnostic).
 * 
 * @author VHAISWBECKEC
 */
public abstract class AbstractImageHRefTag 
extends AbstractImagePropertyTag
{
	// a derived class may specify either, or both, of these keys
	// in the pathInfoPattern
	protected final static String imageUrnParameterKey = "[imageUrn]";
	protected final static String patientIcnParameterKey = "[patientIcn]";

	protected abstract String getDefaultPathInfoPattern();
	
	private String host = null;
	private String context = null;
	private String pathInfoPattern = getDefaultPathInfoPattern();
	private String protocolOverride;
	private String targetSite;
	private boolean includeProtocolInUrl = false;
	private boolean includeHostInUrl = false;
	private boolean includeContextInUrl = false;
	
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
	 * The context MUST start with a '/' character and must not end with a
	 * '/'.  This is consistent with the behavior of request.getContext() 
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
	public void setContext(String contextBase)
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
	 * If set then tack "http://" on the front of the URL.
     * @return the includeProtocolInUrl
     */
    public boolean isIncludeProtocolInUrl()
    {
    	return includeProtocolInUrl;
    }
    public void setIncludeProtocolInUrl(boolean includeProtocolInUrl)
    {
    	this.includeProtocolInUrl = includeProtocolInUrl;
    }
	/**
     * @return the includeHostInUrl
     */
    public boolean isIncludeHostInUrl()
    {
    	return includeHostInUrl;
    }
    public void setIncludeHostInUrl(boolean includeHostInUrl)
    {
    	this.includeHostInUrl = includeHostInUrl;
    }
	/**
	 * If generateCompletePath is set then the returned URL will be fully
	 * defined.
	 * 
     * @return the generateCompletePath
     */
    public boolean isIncludeContextInUrl()
    {
    	return includeContextInUrl;
    }
    public void setIncludeContextInUrl(boolean includeContextInUrl)
    {
    	this.includeContextInUrl = includeContextInUrl;
    }
    
	/**
     * @return the protocolOverride
     */
    public String getProtocolOverride()
    {
    	return protocolOverride;
    }
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
		String requestHost = null;
		int requestPort	=0;
		String requestContext = null;
		String protocol = null;
		
		try
        {
	        ServletRequest servletRequest = this.pageContext.getRequest();
	        requestPort = servletRequest.getLocalPort();
	        protocol = servletRequest.getProtocol();		// e.g. HTTP/1.1
	        if(protocol.indexOf('/') > 0)
	        	protocol = protocol.substring(0, protocol.indexOf('/'));
	        protocol = protocol.toLowerCase();
	        
	        HttpServletRequest req = (HttpServletRequest)servletRequest;
	        requestContext = req.getContextPath();
	        requestHost = req.getLocalName();
        } 
		catch (ClassCastException e1)
        {
			logger.warn("Unable to cast request to HttpServletRequest, tag library expects to be running over HTTP, continuing ...");
        } 
		
		// URL (in J2EE terms) consists of:
		// <protocol>://<host>:<port>/<context>/<path-info>/<additional-path-info>
		// the context determines the web app
		// the path-info was matched to the servlet mapping
		// additional=path-info is what is left over
		
		// the path info is the portion of the URL to the right of the context, not including the query string
		String pathInfo = buildPathInfo();
		
		// build the path to the image servlet
		StringBuilder sb = new StringBuilder();
		
		if(isIncludeProtocolInUrl())
		{
			sb.append(protocol);
			sb.append("://");
		}
		
		// if the host is explicitly specified then append that host name
		// else if IncludeHostInUrl is set, append the host name of the request
		// else do nothing and the URL will be relative
		if(getHost() != null )
		{
			sb.append(getHost());
		}
		else if(isIncludeHostInUrl())
		{
			sb.append(requestHost);
			sb.append(":");
			sb.append(requestPort);
		}
		
		// if the context base is specified then use that value
		// else if IncludeContextInUrl is set use the request context
		// else use this request's context
		if(getContext() != null )
			sb.append(getContext());
		else if(isIncludeContextInUrl())
			sb.append(requestContext);
		
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
		
		Image image = this.getImage();
		
		if(pathInfo.contains(imageUrnParameterKey))
		{
			String imageUrnExternal = null;
			imageUrnExternal = image.getImageUrn().toString();
			pathInfo = pathInfo.replace(imageUrnParameterKey, imageUrnExternal);
		}
		
		if(pathInfo.contains(patientIcnParameterKey))
		{
			String patientIcnExternal = null;
			
			patientIcnExternal = image.getPatientId();
			pathInfo = pathInfo.replace(patientIcnParameterKey, patientIcnExternal);
		}
		
	    return pathInfo;
    }

}
