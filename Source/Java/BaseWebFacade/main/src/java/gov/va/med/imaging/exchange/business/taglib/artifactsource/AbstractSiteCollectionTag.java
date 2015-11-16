/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Mar 18, 2008
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
package gov.va.med.imaging.exchange.business.taglib.artifactsource;

import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.taglib.exceptions.MissingRequiredArgumentException;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.IterationTag;

import org.apache.log4j.Logger;

/**
 * An abstract base class for a collection of Site instances.
 * Derived class must implement the getSites() method, which
 * is called once per use of this tag (on doStartTag()).
 * Child tags, displaying the Site instances, should call the
 * getCurrentSite() method to get the site to display.
 * 
 * @author VHAISWBECKEC
 *
 */
public abstract class AbstractSiteCollectionTag
extends BodyTagSupport
{
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(this.getClass());
	private String emptyResultMessage = null;
	
	private Collection<Site> sites;
	private Iterator<Site> siteIterator;
	private Site currentSite;
	
	protected abstract Collection<Site> getSites()
	throws JspException, MissingRequiredArgumentException;
	
	/**
	 * The message to show if the site list is empty
	 * @return
	 */
	public String getEmptyResultMessage()
    {
    	return emptyResultMessage;
    }

	public void setEmptyResultMessage(String emptyResultMessage)
    {
    	this.emptyResultMessage = emptyResultMessage;
    }
	
	// ==============================================================================
	// JSP Tag Lifecycle Events
	// ==============================================================================

	/**
	 * Create and expose the current RequestContext. Delegates to
	 * {@link #doStartTagInternal()} for actual work.
	 * 
	 * @see #REQUEST_CONTEXT_PAGE_ATTRIBUTE
	 * @see org.springframework.web.servlet.support.JspAwareRequestContext
	 */
	public final int doStartTag() 
	throws JspException
	{
		try
        {
	        sites = getSites();
        } 
		catch (MissingRequiredArgumentException e)
        {
			try{pageContext.getOut().write(e.getMessage());} 
			catch (IOException ioX){throw new JspException(ioX);}
			
			return BodyTag.SKIP_BODY;
        }
		
		if(sites == null || sites.size() < 1)
		{
	    	if(getEmptyResultMessage() != null)
	    		try{pageContext.getOut().write(getEmptyResultMessage());}
	    		catch(IOException ioX){logger.error("Unable to write empty result set message.");}
	    		
			return BodyTag.SKIP_BODY;
		}
		
		siteIterator = sites.iterator();
		currentSite = siteIterator.next();
		return BodyTag.EVAL_BODY_INCLUDE;
	}

	/**
	 * 
	 * @return
	 */
	public Site getCurrentSite()
	{
		return currentSite;
	}
	
	@Override
    public int doAfterBody() 
	throws JspException
    {
		if(siteIterator.hasNext())
		{
			currentSite = siteIterator.next();
			return IterationTag.EVAL_BODY_AGAIN;
		}
		else
		{
			currentSite = null;
			return IterationTag.SKIP_BODY;
		}
    }
}
