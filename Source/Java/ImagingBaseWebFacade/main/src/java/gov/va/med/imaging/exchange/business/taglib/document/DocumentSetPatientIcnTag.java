/**
 * 
 */
package gov.va.med.imaging.exchange.business.taglib.document;

import gov.va.med.imaging.exchange.business.documents.DocumentSet;
import gov.va.med.imaging.exchange.business.taglib.AbstractBusinessObjectPropertyTag;

/**
 * @author vhaiswbeckec
 *
 */
public class DocumentSetPatientIcnTag
extends AbstractBusinessObjectPropertyTag<DocumentSet, AbstractDocumentSetTag>
{
	private static final long serialVersionUID = 1L;

	public DocumentSetPatientIcnTag() 
	throws SecurityException, NoSuchMethodException 
	{
		super(DocumentSet.class, "patientIcn");
	}
}
