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

import java.io.IOException;
import java.lang.reflect.Method;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.IterationTag;
import javax.servlet.jsp.tagext.Tag;

/**
 * @author VHAISWBECKEC
 *
 */
public class EnumIteratorTag 
extends BodyTagSupport
{
	private static final long serialVersionUID = 1L;
	private String enumClassName;
	private Class<?> enumClass;
	private Enum<?>[] values;
	private int index;
	private boolean prependNullElement = false;
	private boolean appendAllElement = false;
	private String[] prependElements = null;
	private String[] appendElements = null;

	private final static String NONE_ELEMENT_NAME = "unselected";
	private final static String NONE_ELEMENT_TOSTRING = "-unselected-";
	private final static int NONE_ELEMENT_ORDINAL = -1;
	private final static String ALL_ELEMENT_NAME = "all";
	private final static String ALL_ELEMENT_TOSTRING = "-all-";
	private final static int ALL_ELEMENT_ORDINAL = Integer.MAX_VALUE;
	
	public String getEnumClassName()
	{
		return enumClassName;
	}
	
	public String getEnumClassSimpleName()
	{
		return enumClass == null ? null : enumClass.getSimpleName();
	}
	
	public void setEnumClassName(String className) 
	throws JspException
	{
		this.enumClassName = className;
	}

	public boolean isPrependNullElement()
    {
    	return prependNullElement;
    }

	public void setPrependNullElement(boolean prependNullElement)
    {
    	this.prependNullElement = prependNullElement;
    }

	public boolean isAppendAllElement()
    {
    	return appendAllElement;
    }

	public void setAppendAllElement(boolean appendAllElement)
    {
    	this.appendAllElement = appendAllElement;
    }

	public String[] getPrependElements()
	{
		return this.prependElements;
	}

	public String[] getAppendElements()
	{
		return this.appendElements;
	}

	public void setPrependElements(String[] prependElements)
	{
		this.prependElements = prependElements;
	}

	public void setAppendElements(String[] appendElements)
	{
		this.appendElements = appendElements;
	}

	@Override
    public int doStartTag() 
	throws JspException
    {
		if(getEnumClassName() == null || getEnumClassName().length() == 0)
		{
			try{pageContext.getOut().write("The enumeration class name is required but was not provided.");}
			catch(IOException ioX){throw new JspException(ioX);}
			return Tag.SKIP_BODY;
		}
		try
        {
	        enumClass = Class.forName(getEnumClassName());
	        Method valuesMethod = enumClass.getDeclaredMethod("values", (Class<?>[])null);
	        values = (Enum<?>[])valuesMethod.invoke(null, (Object[])null );
	        index = isPrependNullElement() ? NONE_ELEMENT_ORDINAL : 0;
	        
	        return values == null || values.length == 0 ? Tag.SKIP_BODY : Tag.EVAL_BODY_INCLUDE;
        } 
		catch (Exception e)
        {
        	throw new JspException("EnumIteratorTag.enumClassName('" + getEnumClassName() + "'): " + e.getClass().getSimpleName() + " -> " + e.getMessage() );
        } 
    }
	
	public String getElementToString()
	{
		return index < 0 ? NONE_ELEMENT_TOSTRING : 
			index >= values.length ? ALL_ELEMENT_TOSTRING : 
			values[index].toString();
	}
	
	public String getElementName()
	{
		return index < 0 ? NONE_ELEMENT_NAME : 
			index >= values.length ? ALL_ELEMENT_NAME : 
			values[index].name();
	}
	
	public int getElementOrdinal()
	{
		return index < 0 ? NONE_ELEMENT_ORDINAL : 
			index >= values.length ? ALL_ELEMENT_ORDINAL : 
			values[index].ordinal();
	}
	
	@Override
    public int doAfterBody() 
	throws JspException
    {
		++index;
		return isAppendAllElement() ? 
			index <= values.length ? IterationTag.EVAL_BODY_AGAIN : Tag.SKIP_BODY :
			index < values.length ? IterationTag.EVAL_BODY_AGAIN : Tag.SKIP_BODY;
    }

	
}
