/**
 * 
 */
package gov.va.med.imaging.router.commands.documents;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.WellKnownOID;
import gov.va.med.imaging.channels.ChecksumValue;
import gov.va.med.imaging.core.annotations.routerfacade.RouterCommandExecution;
import gov.va.med.imaging.core.interfaces.ImageMetadataNotification;
import gov.va.med.imaging.core.interfaces.exceptions.*;
import gov.va.med.imaging.exchange.FileTypeIdentifierStream;
import gov.va.med.imaging.exchange.business.ImageStreamResponse;
import gov.va.med.imaging.exchange.business.documents.DocumentRetrieveResult;
import gov.va.med.imaging.exchange.enums.VistaImageType;
import gov.va.med.imaging.exchange.storage.cache.RealizedCache;
import gov.va.med.imaging.exchange.storage.cache.ImmutableInstance;
import gov.va.med.imaging.router.commands.AbstractImageCommandImpl;
import gov.va.med.imaging.router.facade.ImagingContext;
import gov.va.med.imaging.storage.cache.InstanceReadableByteChannel;
import gov.va.med.imaging.storage.cache.InstanceWritableByteChannel;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;

/**
 * Get a document and return an InputStream to read it from.
 * 
 * @author vhaiswbeckec
 *
 */
@RouterCommandExecution(asynchronous=false, distributable=false)
public class GetDocumentCommandImpl
extends AbstractImageCommandImpl<DocumentRetrieveResult>
{
	private static final long serialVersionUID = 1L;
	private final GlobalArtifactIdentifier documentIdentifier;
	private final ImageMetadataNotification imageMetadataNotification;
	
	/**
	 * @param repositoryId
	 * @param imageUrn
	 */
	public GetDocumentCommandImpl(GlobalArtifactIdentifier documentIdentifier)
	{
		this(documentIdentifier, null);
	}
	
	public GetDocumentCommandImpl(GlobalArtifactIdentifier documentIdentifier, 
			ImageMetadataNotification imageMetadataNotification)
	{
		super();
		this.documentIdentifier = documentIdentifier;
		this.imageMetadataNotification = imageMetadataNotification;
	}

	public GlobalArtifactIdentifier getDocumentIdentifier()
	{
		return this.documentIdentifier;
	}

	public ImageMetadataNotification getImageMetadataNotification()
	{
		return imageMetadataNotification;
	}

	@Override
	protected String parameterToString()
	{
		return getDocumentIdentifier().toString();
	}

	@Override
	public DocumentRetrieveResult callSynchronouslyInTransactionContext()
	throws MethodException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();

		getLogger().info("RouterImpl.getDocument(" + getDocumentIdentifier().toString() + ")");
		
		// use this documentId to query the DOD
		String homeCommunityId = getDocumentIdentifier().getHomeCommunityId();
		getLogger().info("Getting " + 
			"[homeCommunityID=" + homeCommunityId + 
			"] [repositoryID=" + getDocumentIdentifier().getRepositoryUniqueId() + 
			"] [documentID=" + getDocumentIdentifier().getDocumentUniqueId() +  "].");

		transactionContext.setServicedSource(getDocumentIdentifier().toRoutingTokenString());

		// if caching is enabled we will try to use the cache
		// cacheThisInstance indicates both that we write to and read from the cache for this instance
		boolean cacheThisInstance = getDocumentIdentifier() != null && getCommandContext().isCachingEnabled();
		
		RealizedCache cache = null; 
		if(cacheThisInstance)
		{
			if((WellKnownOID.VA_DOCUMENT.isApplicable(homeCommunityId)) 
					|| (WellKnownOID.VA_RADIOLOGY_IMAGE.isApplicable(homeCommunityId)))
			{
				cache = getCommandContext().getIntraEnterpriseCacheCache();
			}
			else
			{
				cache = getCommandContext().getExtraEnterpriseCache();
			}
		}
		// if the Image URN was successfully parsed and caching is enabled
		// try to retrieve the instance from the cache
		DocumentRetrieveResult res = null;
		if((cacheThisInstance) && (cache != null)) 
		{
			getLogger().info("Document '" + getDocumentIdentifier().toString() + "' caching enabled.");
			try
			{
				ImmutableInstance documentInstance = cache.getDocumentContent(
					getDocumentIdentifier()
				);
				
				// if the document was found in the cache, stream from there
				if(documentInstance != null)
				{
					getLogger().info("Document '" + getDocumentIdentifier().toString() + "' found in cache, streaming from there.");
					transactionContext.setItemCached(Boolean.TRUE);
					InstanceReadableByteChannel documentChannel = documentInstance.getReadableChannel();
					if(getImageMetadataNotification() != null)
					{
						ChecksumValue cv = null;
						if(documentChannel.getChecksum() != null)
						{
							cv = new ChecksumValue(documentChannel.getChecksum());	
						}											
						getImageMetadataNotification().imageMetadata(cv == null ? null : cv.toString(), 
								null, 0, null);
					}
					
					InputStream inStream = Channels.newInputStream(documentChannel);
					FileTypeIdentifierStream ftis = new FileTypeIdentifierStream(inStream);
					res = new DocumentRetrieveResult( getDocumentIdentifier(), ftis, (VistaImageType)null );
					
				}
				
				// the document was not found in the cache, stream from the data source while writing to
				// the cache simultaneously
				else
				{
					getLogger().info("Document '" + getDocumentIdentifier().toString() + "' NOT found in cache, getting from datasource.");
					transactionContext.setItemCached(Boolean.FALSE);
					// JMW 12/9/2010 P104 - get the image from the data source so that we don't create a 
					// cache instance before we've gotten the image.  If the image doesn't exist, we don't
					// want to get the cache instance since it will be an empty file in the cache when it 
					// should not exist.
					// No reason to put the streamFromDataSource() into a try-catch because we want the
					// exceptions that might occur to be thrown
					res = streamFromDataSource();
					// if we got here, then the image was returned (no exception) - create the cache instance
					// and stream the image
					documentInstance = cache.createDocumentContent( getDocumentIdentifier() );
					// getting the writable byte channel locks the instance for exclusive write by this thread
					InstanceWritableByteChannel documentWriteChannel = documentInstance.getWritableChannel();
					res = CachingDocumentRetrieveResult.create(res, documentWriteChannel);
				}
			}
			catch (CacheException x)
			{
				getLogger().error("Exception occured in getting instance from cache, streaming from data source.", x);
				res = streamFromDataSource();
			}
		}
		else
		{
			// Just return the stream straight from the datasource
			getLogger().info("Document '" + getDocumentIdentifier() + "' caching disabled, streaming from data source.");
			transactionContext.setItemCached(Boolean.FALSE);
			res = streamFromDataSource();
		}
		return res;
	}

	/**
	 * 
	 * @param bytesReturned
	 * @return
	 * @throws MethodException
	 * @throws ImageConversionException
	 * @throws ImageNotFoundException
	 * @throws ImageNearLineException
	 */
	private DocumentRetrieveResult streamFromDataSource()
	throws MethodException, ImageConversionException, ImageNotFoundException, ImageNearLineException
	{
		getLogger().info("Document '" + getDocumentIdentifier().toString() + "' getting document from source.");
		TransactionContextFactory.get().setItemCached(Boolean.FALSE);
		ImageStreamResponse streamResponse = null;
		try
		{
			streamResponse = streamDocumentFromDataSource();
			FileTypeIdentifierStream ftis = new FileTypeIdentifierStream(streamResponse.getImageStream().getInputStream());
			if((getImageMetadataNotification() != null) && (streamResponse != null))
			{
				getImageMetadataNotification().imageMetadata(streamResponse.getProvidedImageChecksum(), 
						streamResponse.getImageFormat(), streamResponse.getImageSize(), 
						streamResponse.getImageQuality());
			}
			return new DocumentRetrieveResult(getDocumentIdentifier(), ftis, null);
		}
		catch (ConnectionException cX)
		{
			getLogger().error(cX);
			throw new MethodException(cX);
		} 
		catch (ImageNotFoundException e)
        {
			throw e;
        }
	}
	
	/**
	 * 
	 * @param imageUrn
	 *            Unique identifier of the image to request from the data source
	 * @param qualityValue
	 * @param contentType
	 * @param outStream
	 * @return a String value indicating the checksum and checksum algorithm
	 * @see gov.va.med.imaging.ChecksumValue returns "ok" or "not ok" if
	 *      checksum was calculated for data source stream before compression
	 * @throws MethodException
	 * @throws IOException
	 * @throws ImageNearLineException
	 */
	private ImageStreamResponse streamDocumentFromDataSource() 
	throws MethodException, ImageConversionException, ConnectionException, ImageNearLineException, ImageNotFoundException
	{
		//StreamImageFromCacheResponse response = new StreamImageFromCacheResponse();
		//SizedInputStream sizedStream = null;
		//InputStream inStream = null;
		ImageStreamResponse documentResponse = null;
		//String documentId = documentUrn.getDocumentId();
		
		documentResponse = ImagingContext.getRouter().getDocumentFromDataSource(getDocumentIdentifier());
		if(documentResponse == null)
			throw new ImageNotFoundException("Document [" + getDocumentIdentifier().toString() + "] not found");

		if(documentResponse.getImageStream() == null)
			throw new MethodException("No input stream returned from data source for document [" + getDocumentIdentifier().toString() + "].");
		//inStream = sizedStream.getInStream();
		
		if(!documentResponse.getImageStream().isReadable())
			throw new MethodException("No input stream returned from data source for document [" + getDocumentIdentifier().toString() + "].");
		// at this point the image has been returned		
		
		//CommonImageCacheFunctions.cacheTXTFile(getCommandContext(), documentUrn, documentResponse.getTxtStream(), false);
		
		//ImageFormat curImgFormat = documentResponse.getImageFormat();
		//getLogger().info("Image returned from datasource in format [" + curImgFormat + "]");
		return documentResponse;		
		
	}

	// ================================================================================================
	// Eclipse Generated Code
	// ================================================================================================

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.documentIdentifier == null) ? 0 : this.documentIdentifier.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		GetDocumentCommandImpl other = (GetDocumentCommandImpl) obj;
		if (this.documentIdentifier == null)
		{
			if (other.documentIdentifier != null)
				return false;
		}
		else if (!this.documentIdentifier.equals(other.documentIdentifier))
			return false;
		return true;
	}

}
