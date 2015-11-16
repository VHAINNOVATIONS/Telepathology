/**
 * 
 */
package gov.va.med.imaging.exchange.business.taglib.document;

import gov.va.med.imaging.exchange.business.documents.Document;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * @author vhaiswbeckec
 *
 */
public class DocumentCollectionElementTag
extends AbstractDocumentTag
{
	private static final long serialVersionUID = 1L;

	protected AbstractDocumentCollectionTag getParentTag()
	{
		return (AbstractDocumentCollectionTag)TagSupport.findAncestorWithClass(this, AbstractDocumentCollectionTag.class);
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.taglib.AbstractBusinessObjectTag#getBusinessObject()
	 */
	@Override
	public Document getBusinessObject() 
	throws JspException
	{
		return getParentTag().getCurrent();
	}

}
