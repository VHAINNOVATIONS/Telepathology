package gov.va.med.imaging.datasource;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.imaging.DocumentURN;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.annotations.SPI;
import gov.va.med.imaging.exchange.business.ImageStreamResponse;

/**
 * 
 * @author VHAISWBECKEC
 *
 */
@SPI(description="The service provider interface for document (artifact) access")
public interface DocumentDataSourceSpi 
extends VersionableDataSourceSpi
{
	/**
	 * Retrieves a document from the data source
	 * @param documentUrn The unique identifier for the document
	 * @return An InputStream to the document or null if the Document does not exist.
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract ImageStreamResponse getDocument(DocumentURN documentUrn)
	throws MethodException, ConnectionException;
	
	/**
	 * This method might be deprecated/removed soon, better to use the other method that requires a DocumentURN
	 * @param homeCommunityUid
	 * @param repositoryUniqueId
	 * @param documentId
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract ImageStreamResponse getDocument(GlobalArtifactIdentifier gai)
	throws MethodException, ConnectionException;
	
}
