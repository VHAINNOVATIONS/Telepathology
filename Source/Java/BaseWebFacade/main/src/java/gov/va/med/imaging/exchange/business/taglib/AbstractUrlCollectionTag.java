/**
 * 
 */
package gov.va.med.imaging.exchange.business.taglib;

import gov.va.med.imaging.exchange.business.taglib.exceptions.MissingRequiredArgumentException;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.IterationTag;
import org.apache.log4j.Logger;

/**
 * @author vhaiswbeckec
 *
 */
public abstract class AbstractUrlCollectionTag
extends BodyTagSupport
{
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(this.getClass());
	private String emptyResultMessage = null;
	
	private Collection<URL> urls;
	private Iterator<URL> urlIterator;
	private URL currentUrl;
	
	/**
	 * Either this method XOR getUrlIterator() must be overridden by derived
	 * classes.  By default, this method returns null.  If this method is overridden
	 * then getUrlIterator() will call this method to get the URL collection.  If
	 * getUrlIterator() is overridden then this method will not be called.
	 * 
	 * @return
	 * @throws JspException
	 * @throws MissingRequiredArgumentException
	 */
	protected Collection<URL> getUrls()
	throws JspException
	{
		return null;
	}

	/**
	 * Either this method XOR getUrls() must be overridden by derived classes.
	 * 
	 * @return
	 * @throws JspException
	 */
	protected Iterator<URL> getUrlIterator()
	throws JspException
	{
		urls = getUrls();
		return urls == null ? null : urls.iterator();
	}
	
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
		urlIterator = getUrlIterator();
		if(urlIterator.hasNext())
		{
			currentUrl = urlIterator.next();
			return BodyTag.EVAL_BODY_INCLUDE;
		}
		else
		{
	    	if(getEmptyResultMessage() != null)
	    		try{pageContext.getOut().write(getEmptyResultMessage());}
	    		catch(IOException ioX){logger.error("Unable to write empty result set message.");}
	    		
			currentUrl = null;
			return BodyTag.SKIP_BODY;
		}
	}

	/**
	 * 
	 * @return
	 */
	public URL getCurrentUrl()
	{
		return currentUrl;
	}
	
	@Override
    public int doAfterBody() 
	throws JspException
    {
		if(urlIterator.hasNext())
		{
			currentUrl = urlIterator.next();
			return IterationTag.EVAL_BODY_AGAIN;
		}
		else
		{
			currentUrl = null;
			return IterationTag.SKIP_BODY;
		}
    }

}
