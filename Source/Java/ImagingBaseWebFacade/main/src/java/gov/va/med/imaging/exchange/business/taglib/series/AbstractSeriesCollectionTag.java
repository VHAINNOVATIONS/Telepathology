/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Jan 22, 2008
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
package gov.va.med.imaging.exchange.business.taglib.series;

import gov.va.med.imaging.exchange.business.Series;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.IterationTag;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

/**
 * This class must be subclassed with something that will provide the
 * Collection of Series instances to display.
 * 
 * @author VHAISWBECKEC
 *
 */
public abstract class AbstractSeriesCollectionTag 
extends BodyTagSupport
{
	private static final long serialVersionUID = 1L;
	
	private Logger logger = Logger.getLogger(this.getClass());
	private String emptyResultMessage = null;
	
	/**
     * @return the studyList
     */
    protected abstract Collection<Series> getSeriesCollection()
    throws JspException;

	/**
	 * The message to show if the series list is empty
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
	
	/**
     * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
     */
    @Override
    public int doStartTag() 
    throws JspException
    {
    	Collection<Series> seriesList = getSeriesCollection();
    	
    	if(seriesList != null && seriesList.size() > 0)
    	{
    		seriesIterator = seriesList.iterator();
    	    return Tag.EVAL_BODY_INCLUDE;
    	}
    	
    	if(getEmptyResultMessage() != null)
    		try{pageContext.getOut().write(getEmptyResultMessage());}
    		catch(IOException ioX){logger.error("Unable to write empty result set message.");}
    		
    	return Tag.SKIP_BODY;
    }

    private Iterator<Series> seriesIterator;
    
    Iterator<Series> getSeriesIterator()
    {
    	return seriesIterator;
    }

	@Override
    public int doAfterBody() 
	throws JspException
    {
	    return getSeriesIterator().hasNext() ? IterationTag.EVAL_BODY_AGAIN : IterationTag.SKIP_BODY;
    }

    
	/**
     * @see javax.servlet.jsp.tagext.TagSupport#doEndTag()
     */
    @Override
    public int doEndTag() 
    throws JspException
    {
	    return Tag.EVAL_PAGE;
    }

}
