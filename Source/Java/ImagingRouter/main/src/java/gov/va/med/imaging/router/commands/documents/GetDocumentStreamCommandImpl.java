/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Nov 8, 2010
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

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.imaging.channels.ByteStreamPump;
import gov.va.med.imaging.core.interfaces.ImageMetadataNotification;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractCommandImpl;
import gov.va.med.imaging.exchange.business.documents.DocumentRetrieveResult;
import gov.va.med.imaging.exchange.enums.ImageQuality;
import gov.va.med.imaging.router.facade.ImagingContext;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * @author vhaiswwerfej
 *
 */
public class GetDocumentStreamCommandImpl
extends AbstractCommandImpl<Long>
{
	private static final long serialVersionUID = -7022537982457346030L;
	
	private final GlobalArtifactIdentifier documentIdentifier;
	private final OutputStream outStream;
	private final ImageMetadataNotification imageMetadataNotification;

	public GetDocumentStreamCommandImpl(GlobalArtifactIdentifier documentIdentifier, OutputStream outStream,
			ImageMetadataNotification imageMetadataNotification)
	{
		this.documentIdentifier = documentIdentifier;
		this.outStream = outStream;
		this.imageMetadataNotification = imageMetadataNotification;
	}

	@Override
	public Long callSynchronouslyInTransactionContext() 
	throws MethodException, ConnectionException
	{
		int bytesReturned = 0;
		getLogger().info("RouterImpl.getDocumentStream(" + getDocumentIdentifier().toString() + ")");
		TransactionContext transactionContext = TransactionContextFactory.get();
		transactionContext.setServicedSource(getDocumentIdentifier().toRoutingTokenString());
		DocumentRetrieveResult documentRetrieveResult = 
			ImagingContext.getRouter().getDocument(getDocumentIdentifier());
		
		if(imageMetadataNotification != null)
		{
			imageMetadataNotification.imageMetadata(null, 
					documentRetrieveResult.getDocumentStream().getImageFormat(), -1, 
					ImageQuality.DIAGNOSTICUNCOMPRESSED);
		}
		
		ByteStreamPump pump = ByteStreamPump.getByteStreamPump();
		InputStream inputStream = documentRetrieveResult.getDocumentStream();
		try
		{
			bytesReturned = pump.xfer(inputStream, getOutStream());
		}
		catch(IOException ioX)
		{
			throw new ConnectionException("IOException pumping document to output stream, " + ioX.getMessage(), ioX);
		}
		finally
		{
			// must close input stream or causes problems in the cache
			try
			{
				if(inputStream != null)
					inputStream.close();
			}
			catch(IOException ioX)
			{
				getLogger().warn("IOException caught when closing document stream '" + getDocumentIdentifier().toString() + ", " + ioX.getMessage());
			}
		}
		
		return new Long(bytesReturned);
	}	

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((documentIdentifier == null) ? 0 : documentIdentifier
						.hashCode());
		result = prime * result
				+ ((outStream == null) ? 0 : outStream.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		final GetDocumentStreamCommandImpl other = (GetDocumentStreamCommandImpl) obj;
		if (documentIdentifier == null)
		{
			if (other.documentIdentifier != null)
				return false;
		}
		else if (!documentIdentifier.equals(other.documentIdentifier))
			return false;
		if (outStream == null)
		{
			if (other.outStream != null)
				return false;
		}
		else if (!outStream.equals(other.outStream))
			return false;
		return true;
	}

	@Override
	protected String parameterToString()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(getDocumentIdentifier());
		sb.append(',');
		sb.append(getOutStream() == null ? "<null out stream>" : getOutStream());
		
		return sb.toString();
	}

	public GlobalArtifactIdentifier getDocumentIdentifier()
	{
		return documentIdentifier;
	}

	public OutputStream getOutStream()
	{
		return outStream;
	}

	public ImageMetadataNotification getImageMetadataNotification()
	{
		return imageMetadataNotification;
	}
}
