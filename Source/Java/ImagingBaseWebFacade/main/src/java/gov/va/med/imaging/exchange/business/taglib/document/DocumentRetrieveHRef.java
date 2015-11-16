/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Oct 26, 2010
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author vhaiswbeckec
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

package gov.va.med.imaging.exchange.business.taglib.document;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.OctetSequenceEscaping;
import gov.va.med.imaging.exchange.business.documents.Document;
import gov.va.med.imaging.exchange.business.taglib.AbstractBusinessObjectPropertyTag;
import javax.servlet.jsp.JspException;

/**
 * @author vhaiswbeckec
 *
 */
public class DocumentRetrieveHRef
extends AbstractBusinessObjectPropertyTag<Document, AbstractDocumentTag>
{
	private static final long serialVersionUID = 1L;
	private final static String DEFAULT_DOCUMENT_PATH = "document";

	private String documentPath = DEFAULT_DOCUMENT_PATH;
	private OctetSequenceEscaping rfc2141EscapeEngine;
	
	public DocumentRetrieveHRef() 
	throws SecurityException, NoSuchMethodException 
	{
		super(Document.class, "globalArtifactIdentifier");
		rfc2141EscapeEngine = OctetSequenceEscaping.createRFC2141EscapeEngine();
	}

	/**
	 * @return the documentPath
	 */
	public String getDocumentPath()
	{
		return this.documentPath;
	}

	/**
	 * @param documentPath the documentPath to set
	 */
	public void setDocumentPath(String documentPath)
	{
		this.documentPath = documentPath;
	}

	/**
	 * @see gov.va.med.imaging.exchange.business.taglib.AbstractBusinessObjectPropertyTag#getElementValue()
	 */
	@Override
	protected String getElementValue() 
	throws JspException
	{
		Document document = getBusinessObject();
		GlobalArtifactIdentifier gai = document.getGlobalArtifactIdentifier();
		
		String docPathInfo = 
			getDocumentPath() + "/" +
			rfc2141EscapeEngine.escapeIllegalCharacters(gai.getHomeCommunityId()) + "/" + 
			rfc2141EscapeEngine.escapeIllegalCharacters(gai.getRepositoryUniqueId()) + "/" + 
			rfc2141EscapeEngine.escapeIllegalCharacters(gai.getDocumentUniqueId());
		
		return docPathInfo;
	}

}
