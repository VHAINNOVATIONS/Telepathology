/**
 * 
 */
package gov.va.med.imaging.router.commands.documents;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.annotations.routerfacade.RouterCommandExecution;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.DocumentFilter;
import gov.va.med.imaging.exchange.business.documents.DocumentSet;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import java.util.List;

/**
 * A command to get a List of Study instances:
 * 1.) from a single Site
 * 2.) related to a single patient
 * 3.) meeting the criteria of the given StudyFilter instance
 * 
 * @author vhaiswbeckec
 *
 */
@RouterCommandExecution(asynchronous=true, distributable=true)
public class GetDocumentSetListBySiteNumberCommandImpl 
extends AbstractDocumentSetCommandImpl<List<DocumentSet>> 
{
	private static final long serialVersionUID = 5473568262532356886L;
	
	private final RoutingToken routingToken;
	private final DocumentFilter filter;
	
	/**
	 * @param router
	 * @param asynchronousMethodProcessor
	 */
	public GetDocumentSetListBySiteNumberCommandImpl(
		RoutingToken routingToken,
		DocumentFilter filter)
	{
		super();
		this.routingToken = routingToken;
		this.filter = filter;
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AsynchronousCommandProcessor#parameterToString()
	 */
	@Override
	protected String parameterToString()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(this.getSiteNumber());
		sb.append(',');
		sb.append(this.getFilter() == null ? "<null>" : this.getFilter().toString());
		
		return sb.toString();
	}

	@Override
	public RoutingToken getRoutingToken()
	{
		return this.routingToken;
	}

	public DocumentFilter getFilter()
	{
		return this.filter;
	}

	public String getSiteNumber()
	{
		return getRoutingToken().getRepositoryUniqueId();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AsynchronousCommandProcessor#callInTransactionContext()
	 */
	@Override
	public List<DocumentSet> callSynchronouslyInTransactionContext()
	throws MethodException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		transactionContext.setServicedSource(getRoutingToken().toRoutingTokenString());
		
		List<DocumentSet> documentSets = this.getPatientDocumentSetList(getRoutingToken(), filter);
		return documentSets;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.filter == null) ? 0 : this.filter.hashCode());
		result = prime * result + ((this.routingToken == null) ? 0 : this.routingToken.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		final GetDocumentSetListBySiteNumberCommandImpl other = (GetDocumentSetListBySiteNumberCommandImpl) obj;
		if (this.filter == null)
		{
			if (other.filter != null)
				return false;
		}
		else if (!this.filter.equals(other.filter))
			return false;
		if (this.routingToken == null)
		{
			if (other.routingToken != null)
				return false;
		}
		else if (!this.routingToken.equals(other.routingToken))
			return false;
		return true;
	}


}
