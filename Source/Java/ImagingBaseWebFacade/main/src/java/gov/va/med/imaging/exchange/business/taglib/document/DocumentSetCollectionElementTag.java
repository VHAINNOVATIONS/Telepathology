/**
 * 
 */
package gov.va.med.imaging.exchange.business.taglib.document;

import gov.va.med.imaging.exchange.business.documents.DocumentSet;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * @author vhaiswbeckec
 *
 */
public class DocumentSetCollectionElementTag
extends AbstractDocumentSetTag
{
	private static final long serialVersionUID = 1L;

	protected AbstractDocumentSetCollectionTag getParentTag()
	{
		return (AbstractDocumentSetCollectionTag)TagSupport.findAncestorWithClass(this, AbstractDocumentSetCollectionTag.class);
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.taglib.AbstractBusinessObjectTag#getBusinessObject()
	 */
	@Override
	public DocumentSet getBusinessObject() 
	throws JspException
	{
		return getParentTag().getCurrent();
	}

}
