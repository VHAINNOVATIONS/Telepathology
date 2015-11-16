/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Oct 1, 2010
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

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.annotations.routerfacade.RouterCommandExecution;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.DocumentFilter;
import gov.va.med.imaging.exchange.business.documents.DocumentSetResult;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * @author vhaiswwerfej
 *
 */
@RouterCommandExecution(asynchronous=true, distributable=true)
public class GetDocumentSetResultBySiteNumberCommandImpl
extends AbstractDocumentSetCommandImpl<DocumentSetResult> 
{
	private static final long serialVersionUID = 1492504808906563032L;
	
	private final RoutingToken routingToken;
	private final DocumentFilter filter;
	
	public GetDocumentSetResultBySiteNumberCommandImpl(RoutingToken routingToken, DocumentFilter filter)
	{
		super();
		this.routingToken = routingToken;
		this.filter = filter;
	}

	@Override
	public DocumentSetResult callSynchronouslyInTransactionContext()
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		transactionContext.setServicedSource(getRoutingToken().toRoutingTokenString());
		return this.getPatientDocumentSetResult(getRoutingToken(), getFilter());		
	}	

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((filter == null) ? 0 : filter.hashCode());
		result = prime * result
				+ ((routingToken == null) ? 0 : routingToken.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		final GetDocumentSetResultBySiteNumberCommandImpl other = (GetDocumentSetResultBySiteNumberCommandImpl) obj;
		if (filter == null)
		{
			if (other.filter != null)
				return false;
		}
		else if (!filter.equals(other.filter))
			return false;
		if (routingToken == null)
		{
			if (other.routingToken != null)
				return false;
		}
		else if (!routingToken.equals(other.routingToken))
			return false;
		return true;
	}

	@Override
	protected String parameterToString()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(this.getSiteNumber());
		sb.append(',');
		sb.append(this.getFilter() == null ? "<null>" : this.getFilter().toString());
		
		return sb.toString();
	}

	public RoutingToken getRoutingToken()
	{
		return routingToken;
	}

	public DocumentFilter getFilter()
	{
		return filter;
	}
	
	public String getSiteNumber()
	{
		return getRoutingToken().getRepositoryUniqueId();
	}

}
