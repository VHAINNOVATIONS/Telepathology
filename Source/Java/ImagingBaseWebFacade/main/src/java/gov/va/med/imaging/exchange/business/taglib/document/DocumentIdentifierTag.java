/**
 * 
 */
package gov.va.med.imaging.exchange.business.taglib.document;

import gov.va.med.imaging.exchange.business.documents.Document;
import gov.va.med.imaging.exchange.business.taglib.AbstractBusinessObjectPropertyTag;

/**
 * @author vhaiswbeckec
 *
 */
public class DocumentIdentifierTag
extends AbstractBusinessObjectPropertyTag<Document, AbstractDocumentTag>
{
	private static final long serialVersionUID = 1L;

	public DocumentIdentifierTag() 
	throws SecurityException, NoSuchMethodException 
	{
		super(Document.class, "identifier");
	}
}
