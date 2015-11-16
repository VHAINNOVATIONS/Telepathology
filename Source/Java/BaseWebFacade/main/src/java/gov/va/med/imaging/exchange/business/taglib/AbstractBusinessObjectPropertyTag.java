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
package gov.va.med.imaging.exchange.business.taglib;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.text.DateFormat;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * The parent class of tags that display business object properties.
 * Derivations of this class MUST reside within an AbstractBusinessObjectTag
 * element.
 * T is the type of the business object (Image, Study, Document, etc ...)
 * P is the type of the parent (surrounding) tag that this tag gets its context 
 * business object from.
 * e.g.
 * <series:SeriesImageCollection>
 * <image:ImageCollectionElement>	- a derivation of AbstractBusinessObjectTag
 * <image:ImageDescription /> - a derivation of AbstractBusinessObjectPropertyTag
 * 
 * @author VHAISWBECKEC
 *
 */
public abstract class AbstractBusinessObjectPropertyTag<T, P extends AbstractBusinessObjectTag<T>> 
extends AbstractApplicationContextTagSupport
{
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public static <T extends Tag> T findAncestorWithClass(Tag tag, Class<T> clazz)
	{
		return (T)TagSupport.findAncestorWithClass(tag, clazz);		
	}
	
	private final Class<T> businessObjectType;
	private final Method[] fieldGetters;
	
	protected AbstractBusinessObjectPropertyTag(Class<T> businessObjectType)
	{
		this(businessObjectType, (Method)null);
	}
	
	protected AbstractBusinessObjectPropertyTag(Class<T> businessObjectType, String... fieldNames) 
	throws SecurityException, NoSuchMethodException
	{
		this.businessObjectType = businessObjectType;
		if(fieldNames != null && fieldNames.length > 0)
		{
			fieldGetters = new Method[fieldNames.length];
			int fieldNameIndex = 0;
			for(String fieldName : fieldNames)
				if(fieldName != null && fieldName.length() > 0)
				{
					String getterMethodName = 
						"get" + Character.toUpperCase(fieldName.charAt(0)) + 
						(fieldName.length() > 1 ? fieldName.substring(1) : "");
					this.fieldGetters[fieldNameIndex++] = businessObjectType.getDeclaredMethod(getterMethodName, new Class<?>[]{});
				}
				else
					this.fieldGetters[fieldNameIndex++] = null;
		}
		else
			fieldGetters = null;
	}
	
	protected AbstractBusinessObjectPropertyTag(Class<T> businessObjectType, Method fieldGetter)
	{
		this.businessObjectType = businessObjectType;
		this.fieldGetters = new Method[1];
		this.fieldGetters[0] = fieldGetter;
	}
	
	@SuppressWarnings("unchecked")
	private Class<T> getBusinessObjectType()
	{
		if(businessObjectType != null)
			return businessObjectType;
		else
		{
			String typeMsg = "<Error Getting Business Object>";
			try
			{
				T businessObject = getBusinessObject();
				typeMsg = businessObject == null ? "<null business object>" : businessObject.getClass().getName();
				return (Class<T>)( businessObject == null ? null : businessObject.getClass() );
			}
			catch (ClassCastException ccX)
			{
				getLogger().error(
					"PANIC: business object to be displayed is not what is expected.  " +
					"Expected type is Class<T>, type is actually '" + typeMsg + "'." +
					"Following NullPointerExceptions may be tracable to this error.", 
					ccX);
				return null;
			}
			catch (Throwable t)
			{
				getLogger().error(
					"Error getting business object to be displayed.  " +
					"Expected type is Class<T>, type is actually '" + typeMsg + "'." +
					"A following NullPointerException may be tracable to this error.", 
					t);
				return null;
			}
		}
	}
	
	public Method getFieldGetter()
	{
		return this.fieldGetters[0];
	}

	public Method[] getFieldGetters()
	{
		return this.fieldGetters;
	}
	
	public Method getFieldGetters(int index)
	{
		return this.fieldGetters[index];
	}
	
	protected T getBusinessObject()
	throws JspException
	{
		return (T)( getParentTag().getBusinessObject() );
	}
	
	@SuppressWarnings("unchecked")
	private P getParentTag()
	throws JspException
	{
		try
        {
			AbstractBusinessObjectTag<T> parentTag = null;
			for(
				parentTag = (AbstractBusinessObjectTag)TagSupport.findAncestorWithClass(this, AbstractBusinessObjectTag.class);
				parentTag != null && ! businessObjectType.equals(parentTag.getBusinessObjectType());
				parentTag = (AbstractBusinessObjectTag)TagSupport.findAncestorWithClass(parentTag, AbstractBusinessObjectTag.class) );
			
			return (P)parentTag;
        } 
		catch (ClassCastException e)
        {
			throw new JspException("Parent tag of this '" + this.getClass().getName() + "' must present business object of type '" + getBusinessObjectType().getName() + "'.");
        }
	}

	protected DateFormat getDateFormat()
	{
		return DateFormat.getDateInstance();
	}
	
	protected Writer getWriter() 
	throws IOException
	{
		return pageContext.getOut();
	}
	
	/**
	 * Derived classes may return the value of their element by overriding
	 * this method.
	 * By default, this method will make a string representation of the field to be
	 * displayed using the toString() method of the field getter.
	 * 
	 * @return
	 * @throws JspException
	 */
	protected String getElementValue()
	throws JspException
	{
		if(getFieldGetter() == null)
			throw new JspException("This instance of '" + this.getClass().getSimpleName() + 
				"' must specify either a field name, getter method or override getElementValue() and it does not.");
		else
		{
			Object fieldValue;
			try
			{
				for(Method fieldGetter : this.getFieldGetters())
				{
					fieldValue = fieldGetter.invoke( getBusinessObject(), new Object[]{} );
					if(fieldValue != null && fieldValue.toString().length() > 0)
						return fieldValue.toString();
				}
				return "";
			}
			catch (Exception x)
			{
				throw new JspException(x);
			}
		}
	}

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
