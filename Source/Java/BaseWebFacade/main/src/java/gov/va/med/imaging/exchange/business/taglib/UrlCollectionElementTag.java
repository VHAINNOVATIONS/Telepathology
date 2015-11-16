/**
 * 
 */
package gov.va.med.imaging.exchange.business.taglib;

import java.net.URL;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * @author vhaiswbeckec
 *
 */
public class UrlCollectionElementTag
extends AbstractUrlTag
{
	private static final long serialVersionUID = 1L;

	private AbstractUrlCollectionTag getParentCollectionTag()
	{
		return (AbstractUrlCollectionTag)TagSupport.findAncestorWithClass(this, AbstractUrlCollectionTag.class);
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.taglib.AbstractUrlTag#getUrl()
	 */
	@Override
	public URL getUrl() 
	throws JspException
	{
		AbstractUrlCollectionTag parentCollectionTag = getParentCollectionTag();
		
		if(parentCollectionTag == null)
			throw new JspException(
				"Tags of class '" + this.getClass().getName() + 
				"' must have an ancestor of type '" + AbstractUrlCollectionTag.class.getName() + "'.");
		return parentCollectionTag.getCurrentUrl();
	}
}
