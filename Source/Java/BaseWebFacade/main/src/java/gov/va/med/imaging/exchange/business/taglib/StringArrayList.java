/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Apr 3, 2008
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
package gov.va.med.imaging.exchange.business.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * @author VHAISWBECKEC
 *
 */
public abstract class StringArrayList 
extends BodyTagSupport
{
	private int index;
	protected abstract String[] getListValues();
	private boolean prependNullElement = false;
	
	public boolean isPrependNullElement()
    {
    	return prependNullElement;
    }

	public void setPrependNullElement(boolean prependNullElement)
    {
    	this.prependNullElement = prependNullElement;
    }

	@Override
    public int doStartTag() 
	throws JspException
    {
		index = 0;
	    return 
	    	getListValues() == null ? BodyTag.SKIP_BODY :
	    	getListValues().length < 1 ? BodyTag.SKIP_BODY :
	    	BodyTag.EVAL_BODY_INCLUDE;
    }

	@Override
    public int doAfterBody() 
	throws JspException
    {
		++index;
    	return index < getListValues().length ? 
    		BodyTag.EVAL_BODY_AGAIN :
	    	BodyTag.SKIP_BODY;
    }
	
	/**
	 * 
	 * @return
	 */
	String getCurrentListValue()
	{
		return getListValues()[index];
	}
}
