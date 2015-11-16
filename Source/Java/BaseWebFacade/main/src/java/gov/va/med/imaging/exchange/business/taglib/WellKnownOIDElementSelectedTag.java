/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Jun 5, 2008
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

import gov.va.med.WellKnownOID;
import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * @author VHAISWBECKEC
 *
 */
public class WellKnownOIDElementSelectedTag 
extends AbstractApplicationContextTagSupport
{
	private static final long serialVersionUID = 1L;

	private String compareValue; 
	private String trueValue;
	private String falseValue;

	/**
	 * @return the compareValue
	 */
	public String getCompareValue()
	{
		return this.compareValue;
	}

	/**
	 * @return the trueValue
	 */
	public String getTrueValue()
	{
		return this.trueValue;
	}

	/**
	 * @return the falseValue
	 */
	public String getFalseValue()
	{
		return this.falseValue;
	}

	/**
	 * @param compareValue the compareValue to set
	 */
	public void setCompareValue(String compareValue)
	{
		this.compareValue = compareValue;
	}

	/**
	 * @param trueValue the trueValue to set
	 */
	public void setTrueValue(String trueValue)
	{
		this.trueValue = trueValue;
	}

	/**
	 * @param falseValue the falseValue to set
	 */
	public void setFalseValue(String falseValue)
	{
		this.falseValue = falseValue;
	}

	/**
	 * 
	 * @return
	 * @throws JspException
	 */
	protected WellKnownOIDIteratorTag getParentEnumIteratorTag()
	throws JspException
	{
		try
        {
			return (WellKnownOIDIteratorTag)TagSupport.findAncestorWithClass(this, WellKnownOIDIteratorTag.class);
        } 
		catch (ClassCastException e)
        {
			throw new JspException("Parent tag of any derivation of AbstractEnumIteratorElementTag must be of type EnumIteratorTag");
        }
	}
	
	/**
	 * 
	 * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
	 */
	@Override
    public int doStartTag() 
	throws JspException
    {
		try
        {
			WellKnownOID current = getParentEnumIteratorTag().getCurrentOID();
			if( current != null && getCompareValue().equals(current.getCanonicalValue().toString()) )
				pageContext.getOut().write(getTrueValue());
			else
				pageContext.getOut().write(getFalseValue());
        } 
		catch (IOException e)
        {
			throw new JspException(e);
        }
	    return Tag.EVAL_BODY_INCLUDE;
    }
}
