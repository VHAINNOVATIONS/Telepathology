/**
 * 
 */
package gov.va.med.imaging.router.commands.documents;

import gov.va.med.PatientIdentifier;
import gov.va.med.PatientIdentifierType;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.router.AsynchronousCommandResultListener;
import gov.va.med.imaging.core.router.commands.AbstractCumulativeCommandImpl;
import gov.va.med.imaging.exchange.business.DocumentFilter;
import gov.va.med.imaging.exchange.business.documents.DocumentSet;
import gov.va.med.imaging.exchange.enums.ImagingSecurityContextType;
import gov.va.med.imaging.router.facade.ImagingContext;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * A command to get a List of DocumentSet instances:
 * 1.) from all Sites
 * 2.) related to a single patient
 * 3.) meeting the criteria of the given DocumentFilter instance
 * 
 * @author vhaiswbeckec
 *
 */
public class GetDocumentSetListCommandImpl 
extends AbstractCumulativeCommandImpl<DocumentSet> 
{
	private static final long serialVersionUID = 5473568262532356886L;
	
	private final RoutingToken routingToken;
	private final DocumentFilter filter;
	
	/**
	 * @param router
	 * @param asynchronousMethodProcessor
	 */
	public GetDocumentSetListCommandImpl(RoutingToken routingToken, DocumentFilter filter)
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
		
		sb.append(this.getPatientIdentifier());
		sb.append(',');
		sb.append(this.getFilter() == null ? "<null>" : this.getFilter().toString());
		
		return sb.toString();
	}

	public RoutingToken getRoutingToken()
	{
		return this.routingToken;
	}

	public DocumentFilter getFilter()
	{
		return this.filter;
	}

	@Override
	public PatientIdentifier getPatientIdentifier()
	{
		return new PatientIdentifier(getFilter().getPatientId(), PatientIdentifierType.icn);
	}
	
	/*
	@Override
	protected Command<List> createCommand(SiteNumber siteNumber)
	{
		Command<List> command = getCommandContext().getCommandFactory().createCollectionCommand( 
			  java.util.List.class, 
			  gov.va.med.imaging.exchange.business.documents.DocumentSet.class, 
			  "GetDocumentSetListBySiteNumberCommand", 
			  new Object[]{siteNumber, filter} 
		);
		
		return command;
	}*/

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.commands.AbstractCumulativeCommandImpl#callAsyncCommand(gov.va.med.imaging.SiteNumber, gov.va.med.imaging.core.interfaces.router.AsynchronousCommandResultListener)
	 */
	@Override
	protected void callChildCommandAsync(
		RoutingToken routingToken,
		AsynchronousCommandResultListener listener) 
	{	
		try
		{
			ImagingContext.getRouter().getDocumentSetListBySiteNumber(
				routingToken, 
				filter, listener);
		}
		catch (MethodException x)
		{
			getLogger().error("Exception caught in child command - ", x);
		}
		catch (ConnectionException x)
		{
			getLogger().error("Exception caught in child command - ", x);
		}	
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.commands.AbstractCumulativeCommandImpl#setInitialImagingSecurityContext()
	 */
	@Override
	protected void setInitialImagingSecurityContext() 
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		transactionContext.setImagingSecurityContextType(ImagingSecurityContextType.CPRS_CONTEXT.toString());
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.commands.AbstractCumulativeCommandImpl#setSecondaryImagingSecurityContext()
	 */
	@Override
	protected void setSecondaryImagingSecurityContext() 
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		transactionContext.setImagingSecurityContextType(ImagingSecurityContextType.MAG_WINDOWS.toString());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.filter == null) ? 0 : this.filter.hashCode());
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
		final GetDocumentSetListCommandImpl other = (GetDocumentSetListCommandImpl) obj;
		if (this.filter == null)
		{
			if (other.filter != null)
				return false;
		} else if (!this.filter.equals(other.filter))
			return false;
		return true;
	}
}
