/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 25, 2010
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

import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.router.AsynchronousCommandResultListener;
import gov.va.med.imaging.core.router.commands.AbstractCumulativeStatisticsCommandImpl;
import gov.va.med.imaging.core.router.commands.configuration.CommandConfiguration;
import gov.va.med.imaging.exchange.business.DocumentFilter;
import gov.va.med.imaging.exchange.business.documents.DocumentSetResult;
import gov.va.med.imaging.exchange.enums.ImagingSecurityContextType;
import gov.va.med.imaging.router.facade.ImagingContext;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * @author vhaiswwerfej
 *
 */
public class GetCumulativeStatisticsDocumentSetResultCommandImpl
extends AbstractCumulativeStatisticsCommandImpl<DocumentSetResult>
{	
	private static final long serialVersionUID = -6074860798025395183L;
	
	private final RoutingToken routingToken;
	private final DocumentFilter filter;
	
	public GetCumulativeStatisticsDocumentSetResultCommandImpl(RoutingToken routingToken, DocumentFilter filter)
	{
		super(false);
		this.routingToken = routingToken;
		this.filter = filter;
	}
	
	@Override
	public RoutingToken getRoutingToken()
	{
		return routingToken;
	}

	public DocumentFilter getFilter()
	{
		return filter;
	}

	@Override
	protected void callChildCommandAsync(RoutingToken routingToken,
			AsynchronousCommandResultListener listener)
	{
		ImagingContext.getRouter().getDocumentSetResultBySiteNumber(
				routingToken, 
				filter, listener);
	}

	@Override
	protected boolean shouldCallChildCommandForRoutingToken(
			RoutingToken routingToken)
	{
		if(getFilter() != null && !getFilter().isSiteAllowed(routingToken.getRepositoryUniqueId()))
		{
			getLogger().info("Site number [" + routingToken.getRepositoryUniqueId() + "] is excluded in the DocumentFilter, not loading document list from this site");
			return false;	
		}
		return true;
	}

	@Override
	protected PatientIdentifier getPatientIdentifier()
	{
		return PatientIdentifier.icnPatientIdentifier(getFilter().getPatientId());
	}

	@Override
	protected void setInitialImagingSecurityContext()
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		if(CommandConfiguration.getCommandConfiguration().isUseCprsContextToGetPatientTreatingFacilitiyList())
		{		
			transactionContext.setImagingSecurityContextType(ImagingSecurityContextType.CPRS_CONTEXT.toString());
		}
		else
		{
			transactionContext.setImagingSecurityContextType(ImagingSecurityContextType.MAG_WINDOWS.toString());
		}
	}

	@Override
	protected void setSecondaryImagingSecurityContext()
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		transactionContext.setImagingSecurityContextType(ImagingSecurityContextType.MAG_WINDOWS.toString());
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
	
	public String getSiteNumber()
	{
		return getRoutingToken().getRepositoryUniqueId();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		final GetCumulativeStatisticsDocumentSetResultCommandImpl other = (GetCumulativeStatisticsDocumentSetResultCommandImpl) obj;
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

	@Override
	protected String parameterToString()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(this.getSiteNumber());
		sb.append(',');
		sb.append(this.getFilter() == null ? "<null>" : this.getFilter().toString());
		
		return sb.toString();
	}

}
