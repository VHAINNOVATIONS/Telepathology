/**
 * 
 */
package gov.va.med.imaging.exchange.business.documents;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.imaging.DocumentURN;
import gov.va.med.imaging.exchange.FileTypeIdentifierStream;
import gov.va.med.imaging.exchange.enums.VistaImageType;

/**
 * @author vhaiswbeckec
 *
 */
public class DocumentRetrieveResult
{
	private final GlobalArtifactIdentifier documentIdentifier;
	private final VistaImageType vistaImageType;
	private final FileTypeIdentifierStream documentStream;
	private final Exception exception;

	/**
	 * @param documentStream
	 */
	public DocumentRetrieveResult(GlobalArtifactIdentifier documentIdentifier, FileTypeIdentifierStream documentStream, VistaImageType vistaImageType)
	{
		super();
		this.documentIdentifier = documentIdentifier;
		this.documentStream = documentStream;
		this.vistaImageType = vistaImageType;
		this.exception = null;
	}

	/**
	 * @param exception
	 */
	public DocumentRetrieveResult(GlobalArtifactIdentifier documentIdentifier, Exception exception)
	{
		super();
		this.documentIdentifier = documentIdentifier;
		this.documentStream = null;
		this.vistaImageType = null;
		this.exception = exception;
	}

	public GlobalArtifactIdentifier getDocumentIdentifier()
	{
		return this.documentIdentifier;
	}

	public FileTypeIdentifierStream getDocumentStream()
	{
		return this.documentStream;
	}

	public VistaImageType getVistaImageType()
	{
		return this.vistaImageType;
	}

	public Exception getException()
	{
		return this.exception;
	}
	
	public boolean isSuccessful()
	{
		return getException() == null;
	}
}
