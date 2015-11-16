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

import gov.va.med.imaging.javalogs.JavaLogFile;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Abstract Java Log property tag
 * 
 * @author vhaiswwerfej
 *
 */
public abstract class AbstractJavaLogPropertyTag 
extends BodyTagSupport 
{
	protected AbstractJavaLogTag getParentJavaLogTag()
	{
		return (AbstractJavaLogTag)TagSupport.findAncestorWithClass(this, AbstractJavaLogTag.class);
	}

	/**
	 * 
	 * @return
	 * @throws JspException
	 */
	protected JavaLogFile getFile() 
	throws JspException
	{
		AbstractJavaLogTag javaLogTag = getParentJavaLogTag();
		if(javaLogTag == null)
			throw new JspException("A Java Log Property tag does not have an ancestor Java Log tag.");
		
		JavaLogFile file = javaLogTag.getFile();
		
		if(file == null)
			throw new JspException("A Java Log Property tag was unable to get the filename from its parent tag.");
		
		return file;
	}
	
	protected Writer getWriter() 
	throws IOException
	{
		return pageContext.getOut();
	}
	
	public abstract String getElementValue() 
	throws JspException;

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
