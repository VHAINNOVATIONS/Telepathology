/**
 * 
 */
package gov.va.med.imaging.exchange.business.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

/**
 * @author vhaiswbeckec
 *
 */
public abstract class AbstractBusinessObjectTag<T>
extends AbstractApplicationContextTagSupport
{
	private static final long serialVersionUID = 1L;

	/**
     * @return the image
     */
    public abstract T getBusinessObject()
    throws JspException;

    /**
     * 
     * @return
     * @throws JspException
     */
    @SuppressWarnings("unchecked")
	public Class<T> getBusinessObjectType() 
    throws JspException
    {
    	try
		{
			return (Class<T>)( getBusinessObject().getClass() );
		}
		catch (Throwable x)
		{
			getLogger().error("PANIC: Business Object Type is not of the expected type, expect null pointer exceptions!");
			return null;
		}
    }
    
	/**
     * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
     */
    @Override
    public int doStartTag() 
    throws JspException
    {
    	if(getBusinessObject() != null)
    		return Tag.EVAL_BODY_INCLUDE;
    	else
    		return Tag.SKIP_BODY;
    }
}
