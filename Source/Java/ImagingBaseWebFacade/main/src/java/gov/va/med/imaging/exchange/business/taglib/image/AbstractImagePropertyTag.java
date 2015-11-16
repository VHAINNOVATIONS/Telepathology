/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Jan 30, 2008
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

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

/**
 * The parent class of tags that display Study properties.
 * Derivations of this class MUST reside within a StudyPresentationTag
 * element.
 * 
 * @author VHAISWBECKEC
 *
 */
public abstract class AbstractImagePropertyTag 
extends TagSupport
{
	private final Logger logger = Logger.getLogger(this.getClass());
	
	protected DateFormat getDateFormat()
	{
		return DateFormat.getDateInstance();
	}
	
	private AbstractImageTag getParentImageTag()
	throws JspException
	{
		try
        {
			return (AbstractImageTag)TagSupport.findAncestorWithClass(this, AbstractImageTag.class);
        } 
		catch (ClassCastException e)
        {
			throw new JspException("Parent tag of AbstractImagePropertyTag must be of type AbstractImageTag");
        }
	}

	/**
     * @return the logger
     */
    protected Logger getLogger()
    {
    	return logger;
    }

	protected Image getImage()
	throws JspException
	{
		return getParentImageTag().getImage();
	}
	
	protected Writer getWriter() 
	throws IOException
	{
		return pageContext.getOut();
	}
	
	/**
	 * Derived classes should return the value of their element by implementing
	 * this method.
	 * 
	 * @return
	 * @throws JspException
	 */
	protected abstract String getElementValue()
	throws JspException;

	/**
     * @see javax.servlet.jsp.tagext.TagSupport#doEndTag()
     */
    @Override
    public int doEndTag() 
    throws JspException
    {
    	try
        {
	        getWriter().write(getElementValue());
        } 
    	catch (IOException e)
        {
    		throw new JspException(e);
        }
    	
    	return Tag.EVAL_PAGE;
    }
	
	
}
