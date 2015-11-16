/**
 * 
 */
package gov.va.med.imaging.exchange.business.taglib.artifactsource;

import gov.va.med.imaging.artifactsource.ArtifactSource;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
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
 * @author vhaiswbeckec
 *
 */
public abstract class AbstractArtifactSourceCollectionTag
extends BodyTagSupport
{
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(this.getClass());
	private String emptyResultMessage = null;
	
	private Collection<ResolvedArtifactSource> artifactSources;
	private Iterator<ResolvedArtifactSource> artifactSourceIterator;
	private ResolvedArtifactSource currentResolvedArtifactSource;
	private ArtifactSource currentArtifactSource;
	
	protected abstract Collection<ResolvedArtifactSource> getArtifactSources()
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
			artifactSources = getArtifactSources();
        } 
		catch (MissingRequiredArgumentException e)
        {
			try{pageContext.getOut().write(e.getMessage());} 
			catch (IOException ioX){throw new JspException(ioX);}
			
			return BodyTag.SKIP_BODY;
        }
		
		if(artifactSources == null || artifactSources.size() < 1)
		{
	    	if(getEmptyResultMessage() != null)
	    		try{pageContext.getOut().write(getEmptyResultMessage());}
	    		catch(IOException ioX){logger.error("Unable to write empty result set message.");}
	    		
			return BodyTag.SKIP_BODY;
		}
		
		artifactSourceIterator = artifactSources.iterator();
		currentResolvedArtifactSource = artifactSourceIterator.next();
		currentArtifactSource = currentResolvedArtifactSource.getArtifactSource();
		
		return BodyTag.EVAL_BODY_INCLUDE;
	}

	/**
	 * 
	 * @return
	 */
	public ArtifactSource getCurrentArtifactSource()
	{
		return currentArtifactSource;
	}
	
	public ResolvedArtifactSource getCurrentResolvedArtifactSource()
	{
		return currentResolvedArtifactSource;
	}
	
	@Override
    public int doAfterBody() 
	throws JspException
    {
		if(artifactSourceIterator.hasNext())
		{
			currentResolvedArtifactSource = artifactSourceIterator.next();
			currentArtifactSource = currentResolvedArtifactSource.getArtifactSource();
			return IterationTag.EVAL_BODY_AGAIN;
		}
		else
		{
			currentResolvedArtifactSource = null;
			currentArtifactSource = null;
			return IterationTag.SKIP_BODY;
		}
    }
}
