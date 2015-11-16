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
public class DocumentUrnTag
extends AbstractBusinessObjectPropertyTag<Document, AbstractDocumentTag>
{
	private static final long serialVersionUID = 1L;

	public DocumentUrnTag() 
	throws SecurityException, NoSuchMethodException 
	{
		super(Document.class, "documentUrn");
	}
}
