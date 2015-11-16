/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 22, 2011
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswwerfej
  Description: 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+

 */
package gov.va.med.imaging.router.commands.documents;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.imaging.channels.ByteStreamPump;
import gov.va.med.imaging.core.annotations.routerfacade.RouterCommandExecution;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.documents.DocumentRetrieveResult;
import gov.va.med.imaging.exchange.storage.cache.ImmutableInstance;
import gov.va.med.imaging.exchange.storage.cache.RealizedCache;
import gov.va.med.imaging.storage.cache.InstanceWritableByteChannel;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * This is the asynchronous command that does the work of prefetching a document from a GAI
 * 
 * @author vhaiswwerfej
 *
 */
@RouterCommandExecution(asynchronous=true, distributable=false)
public class PrefetchDocumentAsyncCommandImpl
extends AbstractDocumentCommandImpl<Boolean>
{
	private static final long serialVersionUID = -953511334348751894L;

	public PrefetchDocumentAsyncCommandImpl(GlobalArtifactIdentifier documentIdentifier)
	{
		super(documentIdentifier);
	}

	@Override
	public Boolean callSynchronouslyInTransactionContext()
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();

		getLogger().info("RouterImpl.prefetchDocument(" + getDocumentIdentifier().toString() + "), transaction '" + transactionContext.getTransactionId() + "'");
		
		// use this documentId to query the DOD
		String homeCommunityId = getDocumentIdentifier().getHomeCommunityId();
		getLogger().info("Getting " + 
			"[homeCommunityID=" + homeCommunityId + 
			"] [repositoryID=" + getDocumentIdentifier().getRepositoryUniqueId() + 
			"] [documentID=" + getDocumentIdentifier().getDocumentUniqueId() +  "].");

		transactionContext.setServicedSource(getDocumentIdentifier().toRoutingTokenString());
		
		// if caching disabled, don't bother doing anything
		if(!getCommandContext().isCachingEnabled())
			return false;
		
		RealizedCache cache = getCache();
		if(cache != null)
		{
			try
			{
				ImmutableInstance instance = 
					cache.getDocumentContent(getDocumentIdentifier());
				if(instance != null)
				{
					// getting an instance does not open it, don't need to close it
					getLogger().info("Document '" + getDocumentIdentifier().toString() + "' found in cache.");
					transactionContext.setItemCached(true);
					return true;
				}
			}
			catch(CacheException cX)
			{
				getLogger().error("CacheException reading '" + getDocumentIdentifier().toString() + "' from cache, " + cX.getMessage());
			}
			// image not in the cache, try to get from data source and put into cache
			getLogger().info("Document '" + getDocumentIdentifier().toString() + "' NOT found in cache, getting from datasource.");
			transactionContext.setItemCached(Boolean.FALSE);
			DocumentRetrieveResult documentRetrieveResult = streamFromDataSource();
			try
			{
				ImmutableInstance documentInstance = 
					cache.createDocumentContent( getDocumentIdentifier() );
				InstanceWritableByteChannel instanceWritableChannel = documentInstance.getWritableChannel();
				OutputStream cacheOutStream = Channels.newOutputStream(instanceWritableChannel);							
				int bytesReturned = 0;
				if(cacheOutStream != null)
				{
					InputStream imageStream = documentRetrieveResult.getDocumentStream();
					ByteStreamPump pump = ByteStreamPump.getByteStreamPump(ByteStreamPump.TRANSFER_TYPE.NetworkToNetwork);
					// if the cacheStream is null the ByteStreamPump will ignore it
					try
					{
						bytesReturned = pump.xfer(imageStream, cacheOutStream);
					}
					catch(IOException ioX)
					{
						getLogger().error("IOException pumping document into cache, " + ioX.getMessage());
					}
					finally
					{
						if(imageStream != null)
						{
							try
							{
								imageStream.close();
							}
							catch(Exception x) {}							
						}
						if(cacheOutStream != null)
						{
							try
							{
								cacheOutStream.close();							
							}
							catch(Exception x) {}
						}
					}
					getLogger().info("Wrote '" + bytesReturned + "' bytes to cache for document '" + getDocumentIdentifier().toString() + "'.");
					return true;
				}// not really sure what to do in the alternative here...				
			}
			catch(CacheException cX)
			{
				getLogger().error("CacheException writing to cache for gai '" + getDocumentIdentifier().toString() + "', " + cX.getMessage());
				throw new MethodException(cX);
			}
		}
		// there is no cache available, no point continuing
		
		return false;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((documentIdentifier == null) ? 0 : documentIdentifier.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		final PrefetchDocumentAsyncCommandImpl other = (PrefetchDocumentAsyncCommandImpl) obj;
		if (documentIdentifier == null)
		{
			if (other.documentIdentifier != null)
				return false;
		}
		else if (!documentIdentifier.equals(other.documentIdentifier))
			return false;
		return true;
	}

	@Override
	protected String parameterToString()
	{
		return getDocumentIdentifier().toString();
	}

}
