/**
 * 
 */
package gov.va.med.imaging.exchange.business.taglib.artifactsource;

import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import java.io.IOException;
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
public abstract class AbstractResolvedArtifactSourceCollectionTag
extends BodyTagSupport
{
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(this.getClass());
	private String emptyResultMessage = null;
	
	private Collection<ResolvedArtifactSource> resolvedArtifactSources;
	private Iterator<ResolvedArtifactSource> resolvedArtifactSourceIterator;
	private ResolvedArtifactSource currentResolvedArtifactSource;
	
	/**
	 * Override this method XOR getResolvedArtifactSourcesIterator()
	 * in derived classes.
	 * 
	 * @return
	 * @throws JspException
	 */
	protected Collection<ResolvedArtifactSource> getResolvedArtifactSources()
	throws JspException
	{
		return null;
	}

	/**
	 * Override this method XOR getResolvedArtifactSources()
	 * in derived classes.
	 * 
	 * @return
	 * @throws JspException
	 */
	protected Iterator<ResolvedArtifactSource> getResolvedArtifactSourcesIterator()
	throws JspException
	{
		this.resolvedArtifactSources = getResolvedArtifactSources();
		return resolvedArtifactSources == null ? null : resolvedArtifactSources.iterator();
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
		resolvedArtifactSourceIterator = getResolvedArtifactSourcesIterator();
		if(resolvedArtifactSourceIterator == null || !resolvedArtifactSourceIterator.hasNext())
		{
			currentResolvedArtifactSource = null;
	    	if(getEmptyResultMessage() != null)
	    		try{pageContext.getOut().write(getEmptyResultMessage());}
	    		catch(IOException ioX){logger.error("Unable to write empty result set message.");}
	    		
			return BodyTag.SKIP_BODY;
		}
		else
		{
			currentResolvedArtifactSource = resolvedArtifactSourceIterator.next();
			return BodyTag.EVAL_BODY_INCLUDE;
		}
	}

	/**
	 * 
	 * @return
	 */
	public ResolvedArtifactSource getCurrentResolvedArtifactSource()
	{
		return currentResolvedArtifactSource;
	}
	
	@Override
    public int doAfterBody() 
	throws JspException
    {
		if(resolvedArtifactSourceIterator.hasNext())
		{
			currentResolvedArtifactSource = resolvedArtifactSourceIterator.next();
			return IterationTag.EVAL_BODY_AGAIN;
		}
		else
		{
			currentResolvedArtifactSource = null;
			return IterationTag.SKIP_BODY;
		}
    }
}
