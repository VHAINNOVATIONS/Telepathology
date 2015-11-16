/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Dec 16, 2009
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
package gov.va.med.imaging.exchange.business.taglib.javalog;

import gov.va.med.imaging.exchange.business.taglib.exceptions.MissingRequiredArgumentException;
import gov.va.med.imaging.javalogs.JavaLogFile;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.IterationTag;

import org.apache.log4j.Logger;

/**
 * Abstract collection tag to read the list of Java Log Files on the VIX
 * 
 * @author vhaiswwerfej
 *
 */
public abstract class AbstractJavaLogCollectionTag 
extends BodyTagSupport 
{
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(this.getClass());
	private String emptyResultMessage = null;
	
	private Collection<JavaLogFile> files;
	private Iterator<JavaLogFile> fileIterator;
	private JavaLogFile currentFile;
	
	protected abstract Collection<JavaLogFile> getFiles()
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
			files = getFiles();
        } 
		catch (MissingRequiredArgumentException e)
        {
			try{pageContext.getOut().write(e.getMessage());} 
			catch (IOException ioX){throw new JspException(ioX);}
			
			return BodyTag.SKIP_BODY;
        }
		
		if(files == null || files.size() < 1)
		{
	    	if(getEmptyResultMessage() != null)
	    		try{pageContext.getOut().write(getEmptyResultMessage());}
	    		catch(IOException ioX){logger.error("Unable to write empty result set message.");}
	    		
			return BodyTag.SKIP_BODY;
		}
		
		fileIterator = files.iterator();
		currentFile = fileIterator.next();
		return BodyTag.EVAL_BODY_INCLUDE;
	}
	
	/**
	 * @return the currentFile
	 */
	public JavaLogFile getCurrentFile() {
		return currentFile;
	}

	@Override
    public int doAfterBody() 
	throws JspException
    {
		if(fileIterator.hasNext())
		{
			currentFile = fileIterator.next();
			return IterationTag.EVAL_BODY_AGAIN;
		}
		else
		{
			currentFile = null;
			return IterationTag.SKIP_BODY;
		}
    }
}
