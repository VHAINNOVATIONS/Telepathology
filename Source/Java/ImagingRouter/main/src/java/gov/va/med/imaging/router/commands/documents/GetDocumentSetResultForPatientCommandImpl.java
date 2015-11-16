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

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.router.CumulativeCommandRoutingTokenException;
import gov.va.med.imaging.core.interfaces.router.CumulativeCommandStatistics;
import gov.va.med.imaging.core.router.AbstractCommandImpl;
import gov.va.med.imaging.exchange.business.ArtifactResultError;
import gov.va.med.imaging.exchange.business.DocumentFilter;
import gov.va.med.imaging.exchange.business.documents.DocumentSet;
import gov.va.med.imaging.exchange.business.documents.DocumentSetResult;
import gov.va.med.imaging.exchange.enums.ArtifactResultStatus;
import gov.va.med.imaging.router.facade.ImagingContext;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * @author vhaiswwerfej
 *
 */
public class GetDocumentSetResultForPatientCommandImpl
extends AbstractCommandImpl<DocumentSetResult>
{
	private static final long serialVersionUID = -3014131677787524358L;
	
	private final RoutingToken routingToken;
	private final DocumentFilter documentFilter;
	
	public GetDocumentSetResultForPatientCommandImpl(RoutingToken routingToken, DocumentFilter documentFilter)
	{
		super();
		this.routingToken = routingToken;
		this.documentFilter = documentFilter;
	}

	@Override
	public DocumentSetResult callSynchronouslyInTransactionContext()
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		transactionContext.setServicedSource(getRoutingToken().toRoutingTokenString());
		
		CumulativeCommandStatistics<DocumentSetResult> cumulativeCommandStatistics = 
			ImagingContext.getRouter().getCumulativeStatisticsDocumentSetResultForPatient(
				getRoutingToken(), 
				getDocumentFilter()
			);
		SortedSet<DocumentSet> fullResults = new TreeSet<DocumentSet>();
		List<ArtifactResultError> errors = new ArrayList<ArtifactResultError>();
		boolean partialResults = false;
		for(DocumentSetResult result : cumulativeCommandStatistics.getCumulativeResults())
		{
			fullResults.addAll(result.getArtifacts());
			if(result.isPartialResult())
				partialResults = true;
			if(result.getArtifactResultErrors() != null)
				errors.addAll(result.getArtifactResultErrors());
		}
		if(cumulativeCommandStatistics.getChildGetErrorCount() > 0)
			partialResults = true;
		
		// if the data sources were queried by the VIX then their exceptions were trapped by the VIX and not part of the result of the above command
		for(CumulativeCommandRoutingTokenException t : cumulativeCommandStatistics.getErrors())
		{
			errors.add(t.toArtifactResultError());
		}
		
		return DocumentSetResult.create(fullResults, 
				(partialResults == true ? ArtifactResultStatus.partialResult : ArtifactResultStatus.fullResult), 
				errors);		
	}
	
	@Override
	public boolean equals(Object obj)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected String parameterToString()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(this.getRoutingToken().toString());
		sb.append(',');
		sb.append(this.getDocumentFilter() == null ? "<null>" : this.getDocumentFilter().toString());
		
		return sb.toString();
	}

	@Override
	public RoutingToken getRoutingToken()
	{
		return this.routingToken;
	}

	public DocumentFilter getDocumentFilter()
	{
		return documentFilter;
	}

}
