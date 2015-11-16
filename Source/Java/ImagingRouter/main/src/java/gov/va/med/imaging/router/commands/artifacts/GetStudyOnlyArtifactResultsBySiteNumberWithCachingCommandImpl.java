/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jan 20, 2012
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
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
package gov.va.med.imaging.router.commands.artifacts;

import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.ArtifactResults;
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.exchange.business.documents.DocumentSetResult;
import gov.va.med.imaging.router.commands.documents.DocumentSetResultCache;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * This command extends GetStudyOnlyArtifactResultsBySiteNumberCommandImpl. This command handles in memory
 * caching of DocumentSetResult objects.  It will optionally check the cache for DocumentSetResult objects 
 * before calling the parent commands and it will ALWAYS cache DocumentSetResult information.
 * 
 * @author VHAISWWERFEJ
 *
 */
public class GetStudyOnlyArtifactResultsBySiteNumberWithCachingCommandImpl
extends GetStudyOnlyArtifactResultsBySiteNumberCommandImpl
{
	private static final long serialVersionUID = 6855358318928412052L;

	private final boolean canGetFromCache;	
	
	public GetStudyOnlyArtifactResultsBySiteNumberWithCachingCommandImpl(RoutingToken routingToken,
			PatientIdentifier patientIdentifier, 
			StudyFilter filter, 
			boolean includeRadiology, 
			boolean includeDocuments, boolean canGetFromCache)
	{
		super(routingToken, patientIdentifier, filter, includeRadiology, includeDocuments);
		this.canGetFromCache = canGetFromCache;
	}
	
	public GetStudyOnlyArtifactResultsBySiteNumberWithCachingCommandImpl(RoutingToken routingToken,
			PatientIdentifier patientIdentifier, 
			StudyFilter filter, 
			boolean includeRadiology, 
			boolean includeDocuments)
	{
		this(routingToken, patientIdentifier, filter, includeRadiology, includeDocuments, false);
	}

	/**
	 * @return the canGetFromCache
	 */
	public boolean isCanGetFromCache()
	{
		return canGetFromCache;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.router.commands.artifacts.AbstractArtifactResultsBySiteNumberCommandImpl#callSynchronouslyInTransactionContext()
	 */
	@Override
	public ArtifactResults callSynchronouslyInTransactionContext()
	throws MethodException, ConnectionException
	{		
		TransactionContext transactionContext = TransactionContextFactory.get();
		if(isCanGetFromCache())
		{
			DocumentSetResult result = 
				DocumentSetResultCache.getCachedDocumentSetResult(getRoutingToken(), getPatientIdentifier());
			
			if(result != null)
			{
				getLogger().info("Retrieved cached DocumentSetResult for patient '" + getPatientIdentifier() + "' from site '" + getRoutingToken().toRoutingTokenString() + "'.");
				transactionContext.setItemCached(true);
				return ArtifactResults.createDocumentSetResult(result);
			}
			else
			{
				getLogger().debug("Did not get DocumentSetResult from cache for patient '" + getPatientIdentifier() + "' from site '" + getRoutingToken().toRoutingTokenString() + "'.");
				transactionContext.setItemCached(false);
			}
		}
		
		// only caching the DocumentSet data
		ArtifactResults result = super.callSynchronouslyInTransactionContext();
		// cache the data
		if(result != null && result.getDocumentSetResult() != null)
		{
			DocumentSetResultCache.cacheDocumentSetResult(getRoutingToken(), 
					getPatientIdentifier(), result.getDocumentSetResult());
		}
		return result;
	}

}
