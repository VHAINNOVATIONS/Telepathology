/**
 * 
 */
package gov.va.med.imaging.router.commands.documents;

import gov.va.med.imaging.DocumentURN;
import gov.va.med.imaging.channels.WriteChannelSiphoningInputStream;
import gov.va.med.imaging.exchange.FileTypeIdentifierStream;
import gov.va.med.imaging.exchange.business.documents.DocumentRetrieveResult;
import gov.va.med.imaging.exchange.enums.VistaImageType;
import gov.va.med.imaging.storage.cache.InstanceWritableByteChannel;
import org.apache.log4j.Logger;

/**
 * A derivation of a DocumentRetrieveResult that will write to a
 * document write channel (usually to a cache instance) as the enclosed
 * stream is read from.
 * 
 * @author vhaiswbeckec
 *
 */
public class CachingDocumentRetrieveResult
extends DocumentRetrieveResult
{

	/**
	 * @param res
	 * @param documentWriteChannel 
	 * @return
	 */
	public static DocumentRetrieveResult create(DocumentRetrieveResult res, InstanceWritableByteChannel documentWriteChannel)
	{
		WriteChannelSiphoningInputStream siphonedInputStream = 
			new WriteChannelSiphoningInputStream(res.getDocumentStream(), documentWriteChannel);
		
		FileTypeIdentifierStream ftis = new FileTypeIdentifierStream(siphonedInputStream);
		DocumentRetrieveResult siphoned = 
			new DocumentRetrieveResult( res.getDocumentIdentifier(), ftis, res.getVistaImageType() );
		
		return siphoned;
	}

	/**
	 * @param documentUrn
	 * @param documentStream
	 * @param vistaImageType
	 */
	private CachingDocumentRetrieveResult(DocumentURN documentUrn, FileTypeIdentifierStream documentStream, VistaImageType vistaImageType)
	{
		super(documentUrn, documentStream, vistaImageType);
	}
	
	/**
	 * @return
	 */
	public Logger getLogger()
	{
		return Logger.getLogger(this.getClass());
	}

}
