/**
 * 
 */
package gov.va.med.imaging.exchange.business.taglib.document;

import gov.va.med.imaging.exchange.business.documents.Document;
import java.util.Collection;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * @author vhaiswbeckec
 *
 */
public class DocumentSetDocumentCollectionTag
extends AbstractDocumentCollectionTag
{
	private static final long serialVersionUID = 1L;

	protected AbstractDocumentSetTag getParentTag()
	{
		return (AbstractDocumentSetTag)TagSupport.findAncestorWithClass(this, AbstractDocumentSetTag.class);
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.taglib.AbstractBusinessObjectCollectionTag#getCollection()
	 */
	@Override
	protected Collection<Document> getCollection() 
	throws JspException
	{
		return getParentTag().getBusinessObject();
	}

}
