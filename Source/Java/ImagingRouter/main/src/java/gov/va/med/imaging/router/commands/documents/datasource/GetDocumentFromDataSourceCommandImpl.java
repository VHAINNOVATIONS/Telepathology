/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Oct 15, 2009
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
package gov.va.med.imaging.router.commands.documents.datasource;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.DocumentURN;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl;
import gov.va.med.imaging.datasource.DocumentDataSourceSpi;
import gov.va.med.imaging.exchange.business.ImageStreamResponse;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * @author vhaiswwerfej
 *
 */
public class GetDocumentFromDataSourceCommandImpl 
extends AbstractDataSourceCommandImpl<ImageStreamResponse, DocumentDataSourceSpi>
{
	private static final long serialVersionUID = -229786486988608152L;
	private static final String SPI_METHOD_NAME = "getDocument";	
	
	private final DocumentURN documentUrn;
	private final GlobalArtifactIdentifier documentIdentifier;
	
	/**
	 * Create a GetDocumentFromDataSourceCommandImpl instance using a DocumentURN
	 * 
	 * @param documentUrn
	 */
	public GetDocumentFromDataSourceCommandImpl(DocumentURN documentUrn)
	{
		this.documentIdentifier = documentUrn;
		this.documentUrn = documentUrn;
	}

	/**
	 * Create a GetDocumentFromDataSourceCommandImpl instance using a generic GlobalArtifactIdentifier
	 * 
	 * @param documentIdentifier
	 */
	public GetDocumentFromDataSourceCommandImpl(GlobalArtifactIdentifier documentIdentifier)
	{
		this.documentIdentifier = documentIdentifier;
		this.documentUrn = null;
	}

	/**
	 * @return the documentUrn
	 */
	public DocumentURN getDocumentUrn() 
	{
		return this.documentUrn;
	}

	/**
	 * @return the documentIdentifier
	 */
	public GlobalArtifactIdentifier getDocumentIdentifier()
	{
		return this.documentIdentifier;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getCommandResult(gov.va.med.imaging.datasource.VersionableDataSourceSpi)
	 */
	@Override
	protected ImageStreamResponse getCommandResult(DocumentDataSourceSpi spi)
	throws ConnectionException, MethodException 
	{
		return 
		getDocumentUrn() == null ? 
			spi.getDocument(getDocumentIdentifier()) :
			spi.getDocument(getDocumentUrn());
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiClass()
	 */
	@Override
	protected Class<DocumentDataSourceSpi> getSpiClass() 
	{
		return DocumentDataSourceSpi.class;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSiteNumber()
	 */
	@Override
	protected String getSiteNumber() 
	{
		return 
			getDocumentUrn() == null ? 
			getDocumentIdentifier().getRepositoryUniqueId() : 
			getDocumentUrn().getOriginatingSiteId();
	}

	@Override
	public RoutingToken getRoutingToken()
	{
		return
			getDocumentUrn() == null ? getDocumentIdentifier() : getDocumentUrn();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiMethodName()
	 */
	@Override
	protected String getSpiMethodName() 
	{
		return SPI_METHOD_NAME;
	}

	@Override
	protected Object[] getSpiMethodParameters()
	{
		return getDocumentUrn() == null ?
			new Object[]{getDocumentIdentifier()} :
			new Object[]{getDocumentUrn()};
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes()
	{
		return getDocumentUrn() == null ? 
			new Class<?>[]{GlobalArtifactIdentifier.class} :
			new Class<?>[]{DocumentURN.class};
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#parameterToString()
	 */
	@Override
	protected String parameterToString() 
	{
		return getDocumentUrn() == null ? getDocumentIdentifier().toString() : getDocumentUrn().toString();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#postProcessResult(java.lang.Object)
	 */
	@Override
	protected ImageStreamResponse postProcessResult(ImageStreamResponse result) 
	{
		TransactionContextFactory.get().setDataSourceEntriesReturned( Integer.valueOf(result == null ? 0 : 1) );
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((this.documentIdentifier == null) ? 0 : this.documentIdentifier.hashCode());
		result = prime * result + ((this.documentUrn == null) ? 0 : this.documentUrn.hashCode());
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
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		GetDocumentFromDataSourceCommandImpl other = (GetDocumentFromDataSourceCommandImpl) obj;
		if (this.documentIdentifier == null)
		{
			if (other.documentIdentifier != null)
				return false;
		}
		else if (!this.documentIdentifier.equals(other.documentIdentifier))
			return false;
		if (this.documentUrn == null)
		{
			if (other.documentUrn != null)
				return false;
		}
		else if (!this.documentUrn.equals(other.documentUrn))
			return false;
		return true;
	}
}
