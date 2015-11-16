package gov.va.med.imaging.core.router.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import gov.va.med.OID;
import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.RoutingTokenImpl;
import gov.va.med.WellKnownOID;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.router.AsynchronousCommandResult;
import gov.va.med.imaging.core.interfaces.router.AsynchronousCommandResultListener;
import gov.va.med.imaging.core.interfaces.router.CumulativeCommandStatistics;
import gov.va.med.imaging.core.router.AbstractCommandImpl;
import gov.va.med.imaging.core.router.commands.configuration.CommandConfiguration;
import gov.va.med.imaging.core.router.facade.InternalContext;
import gov.va.med.imaging.exceptions.OIDFormatException;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * This abstract cumulative command returns statistics about a result and the result so it can be determined if
 * the result is a partial result or not
 * @author vhaiswwerfej
 *
 * @param <R>
 */
public abstract class AbstractCumulativeStatisticsCommandImpl<R>
extends AbstractCommandImpl<CumulativeCommandStatistics<R>>
{
	private static final long serialVersionUID = 6800220806232954169L;

	private static final long MAX_WAIT_TIME = 120000L;  // Wait 120 seconds for asynchronous results to be returned.
	
	private CountDownLatch countdownLatch;
	private CumulativeCommandStatistics<R> cumulativeCommandStatistics = 
		new CumulativeCommandStatistics<R>();
	
	private final boolean requireVixAtRemoteSites;
	
	public AbstractCumulativeStatisticsCommandImpl(boolean requireVixAtRemoteSites)
	{
		this.requireVixAtRemoteSites = requireVixAtRemoteSites;
	}
	
	/**
	 * Derived classes must make the patient ID available so that the
	 * treating facilities list may be obtained.
	 * 
	 * @return
	 */
	protected abstract PatientIdentifier getPatientIdentifier();
	
	/**
	 * Determines if a child command should be called for the specified routingToken. 
	 * @param routingToken 
	 * @return True if the command should be called for this token, false otherwise
	 */
	protected abstract boolean shouldCallChildCommandForRoutingToken(RoutingToken routingToken);
	
	/**
	 * Derived class must implement this method to create child commands.
	 * 
	 * @param siteNumber
	 * @return
	 */
	//protected abstract Command<List> createCommand(SiteNumber siteNumber);
	
	/**
	 * Derived class must implement this method to call child commands asynchronously.
	 * @param siteNumber
	 * @param listener
	 */
	protected abstract void callChildCommandAsync(
		RoutingToken routingToken, 
		AsynchronousCommandResultListener listener);
	
	/**
	 * KLUDGE!! 
	 * This is necessary because when calling the RPC to get the treating sites against station 200
	 * you need to use the CPRS context (because station 200 does not have Imaging installed),
	 * then when you call the RPC to get the actual data, you need to use MAG WINDOWS 
	 * this solution should be revised and improved!
	 * 
	 */
	protected abstract void setInitialImagingSecurityContext();
	
	/**
	 * KLUDGE!! 
	 * This is necessary because when calling the RPC to get the treating sites against station 200
	 * you need to use the CPRS context (because station 200 does not have Imaging installed),
	 * then when you call the RPC to get the actual data, you need to use MAG WINDOWS 
	 * this solution should be revised and improved!
	 * 
	 */
	protected abstract void setSecondaryImagingSecurityContext();
	
	/**
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#callSynchronouslyInTransactionContext()
	 */
	@Override
	public CumulativeCommandStatistics<R> callSynchronouslyInTransactionContext()
	throws MethodException, ConnectionException
	{
		// GetTreatingSitesCommand needs a Site Number.
		TransactionContext transactionContext = TransactionContextFactory.get ();
		
		// if a derived class overrides getRoutingToken() then that will be used.
		// by default getRoutingToken() delegates to getLocalRealmRadiologyRoutingToken()
		RoutingToken localRoutingToken = getRoutingToken(); // getLocalRealmRadiologyRoutingToken();
		
		transactionContext.setServicedSource (localRoutingToken.toRoutingTokenString());
		setInitialImagingSecurityContext();		

		// if the routing token repository is a wildcard then get the list of
		// routing tokens within the community where the patient has been seen
		Set<RoutingToken> repositories = null;
		boolean useExtantGateway = false;
		if( RoutingToken.ROUTING_WILDCARD.equals(localRoutingToken.getRepositoryUniqueId()) )
		{
			getLogger().info("Local routing token repository Id is wildcard");
			OID homeCommunityOid = null;
			try
			{
				homeCommunityOid = OID.create(getRoutingToken().getHomeCommunityId());
				useExtantGateway = getCommandContext().getSiteResolver().isRepositoryGatewayExtant(homeCommunityOid);
				// if there is a gateway that can query from the entire home community on our
				// behalf then use it, else get the repositories within the community that have
				// data on this patient and query each one individually
				if(useExtantGateway)
				{
					getLogger().info("Using extantGateway, setting repositories to 1 and creating for '" + homeCommunityOid.toString() + "'.");
					repositories = new HashSet<RoutingToken>(1);
					try{ repositories.add(RoutingTokenImpl.create(homeCommunityOid.toString())); }
					catch (RoutingTokenFormatException x) {throw new MethodException("Failed to build a wildcard routing token from OID '" + homeCommunityOid.toString() + "'.", x);}
				}
				else
				{
					getLogger().info("Finding repositories with patient data from routing token '" +localRoutingToken + "'.");
					repositories = getRepositoriesWithPatientData(localRoutingToken);
				}
			}
			catch (OIDFormatException x)
			{
				getLogger().warn(
					"Error determining availabality of gateway to '" + getRoutingToken().getHomeCommunityId() +
					"' because that is not a valid OID.  Assuming no gateway is available and continuing.");
			}
		}
		else
		{
			getLogger().info("Routing token is not wildcard, creating repository list of size 1 for local routing token '" +localRoutingToken + ".");
			repositories = new HashSet<RoutingToken>(1);
			repositories.add(localRoutingToken);
		}
		
		// Exception or no Sites found?  Bail.
		if(repositories == null || repositories.size() < 1) 
		{
			getLogger().info("no repositories, not doing anything");
			return cumulativeCommandStatistics;
		}
		
		StringBuilder sbMsg = new StringBuilder();
		sbMsg.append("Patient has data in the following repositories:");
		for(RoutingToken rt : repositories)
			sbMsg.append("\t" + rt.toString());
		getLogger().info(sbMsg);
		
		setSecondaryImagingSecurityContext();
		
		// this holds the list of tokens for sites that have been filtered (so excluding site 200)
		List<RoutingToken> filteredRoutingTokens = new ArrayList<RoutingToken>();
		String excludedSiteMsg = "";
		for(RoutingToken siteRoutingToken : repositories)
		{
			if(shouldCallChildCommandForRoutingToken(siteRoutingToken))
				filteredRoutingTokens.add(siteRoutingToken);
			else
				excludedSiteMsg += siteRoutingToken.toRoutingTokenString() + ", ";
		}
		transactionContext.addDebugInformation("Not retrieving data from sites [" + excludedSiteMsg + "]");
		
		
		int childCommandsCount = filteredRoutingTokens.size();
		this.countdownLatch = new CountDownLatch(childCommandsCount);
		getLogger().info("Calling child async commands for '" + filteredRoutingTokens.size() + "' repositories.");
		// Setup a GetStudyListBySiteNumberCommand command for each Site found.
		// Add the asynch result listener waiter to each command.
		for(RoutingToken siteRoutingToken : filteredRoutingTokens)
			callChildCommandAsync(siteRoutingToken, new CumulativeCommandChildListener<R>(this, siteRoutingToken));
		
		long timeout = getAsynchronousCommandWaitTimeout();
		// Wait for results to be accumulated
		try
		{
			countdownLatch.await(timeout, 
					TimeUnit.MILLISECONDS);
		}
		catch (InterruptedException x)
		{
			String msg = "'" + this.getClass().getSimpleName() + "' interrupted waiting for child commands to complete, abandoning command execution.";
			getLogger().warn(msg);
			throw new MethodException(msg);
		}
		
		long orphaned = countdownLatch.getCount();		// should be zero
		getLogger().debug(
			childCommandsCount + " Asynchronous Commands: " + 
			cumulativeCommandStatistics.getChildGetSuccessCount() + " Completed OK, " + 
			
			cumulativeCommandStatistics.getChildGetErrorCount() + " Failed, " + 
			orphaned + " Orphaned.");
		if(orphaned > 0)
		{
			getLogger().warn("Asynchronous commands are complete, " + orphaned + "' command(s) were orphaned, likely means they took longer than '" + timeout + "' ms");
			
			if(orphaned >= childCommandsCount)
			{
				// no child commands returned, throw exception
				throw new MethodException("Did not receive a response from any sites");
			}
			// if here then at least 1 site failed to respond			
			// could add an error for each site that didn't respond, but for now just adding a single error
			cumulativeCommandStatistics.addError(null, 
					new ConnectionException("Did not receive a response from '" + orphaned + "' sites"));			
		}
		getLogger().info("Returning '" + (cumulativeCommandStatistics.getCumulativeResults() == null ? 0 : cumulativeCommandStatistics.getCumulativeResults().size()) + "' cumulative results.");
		
		return cumulativeCommandStatistics;
	}

	public boolean isRequireVixAtRemoteSites()
	{
		return requireVixAtRemoteSites;
	}

	/**
	 * A genericized form of getTreatingFacilityList.
	 * 
	 * @param localRoutingToken
	 * @param patientId
	 * @return
	 * @throws ConnectionException 
	 * @throws MethodException 
	 */
	private Set<RoutingToken> getRepositoriesWithPatientData(RoutingToken localRoutingToken) 
	throws MethodException, ConnectionException
	{
		Set<RoutingToken> result = new HashSet<RoutingToken>();

		if( WellKnownOID.VA_DOCUMENT.isApplicable(localRoutingToken.getHomeCommunityId()) ||
			WellKnownOID.VA_RADIOLOGY_IMAGE.isApplicable(localRoutingToken.getHomeCommunityId()) )
		{
			PatientIdentifier patientIdentifier = getPatientIdentifier();
			getLogger().info("Finding treating sites for patient '" + patientIdentifier + "'.");
			
			if(patientIdentifier.getPatientIdentifierType().isLocal())
				throw new MethodException("Cannot use local patient identifier to retrieve treating sites information");
			
			List<ResolvedArtifactSource> artifactSourceList = 
				InternalContext.getRouter().getTreatingSites(getLocalRealmRadiologyRoutingToken(), patientIdentifier);
			
			if(artifactSourceList == null || artifactSourceList.size() == 0)
				return result;
			
			for(ResolvedArtifactSource artifactSource : artifactSourceList)
			{
				if(isRequireVixAtRemoteSites())
				{
					if(artifactSource.getArtifactSource() instanceof Site)
					{
						Site site = (Site)artifactSource.getArtifactSource();
						if(site.hasAcceleratorServer())
						{
							getLogger().debug("Site '" + site.getSiteNumber() + "' has a VIX, including in result.");
							result.add( artifactSource.getArtifactSource().createRoutingToken() );
						}
						else
						{
							getLogger().info("Site '" + site.getSiteNumber() + "' does not have a VIX, not including in result.");
						}
					}	
					else
					{
						getLogger().info("ArtifactSource '" + artifactSource.getArtifactSource().toString() + "' is not a Site, cannot determine if source has a VIX, not including in result.");
					}
				}
				else
				{
					result.add( artifactSource.getArtifactSource().createRoutingToken() );
				}
			}
		}
		getLogger().info("Returning '" + result.size() + "' repositories for patient");
		return result;
	}
	
	private long getAsynchronousCommandWaitTimeout()
	{
		long timeout = 
			CommandConfiguration.getCommandConfiguration().getAsynchronousCommandWaiterTimeoutMs();
		if(timeout <= 0)
		{
			timeout = MAX_WAIT_TIME;
		}
		return timeout;
	}

	/**
	 * 
	 * @author vhaiswbeckec
	 *
	 * @param <S>
	 */
	class CumulativeCommandChildListener<S>
	implements AsynchronousCommandResultListener<S>
	{
		private AbstractCumulativeStatisticsCommandImpl<S> parentCommand;
		private final RoutingToken routingToken;
		
		CumulativeCommandChildListener(AbstractCumulativeStatisticsCommandImpl<S> parentCommand, 
				RoutingToken routingToken)
		{
			this.parentCommand = parentCommand;
			this.routingToken = routingToken;
		}
		
		@Override
		public void commandComplete(AsynchronousCommandResult<S> result)
		{
			String transactionId = TransactionContextFactory.get().getTransactionId();
			if(result.isSuccess())
			{
				//getLogger().info("child command adding " + 
				//	(result.getResult() == null ? 0 : ((List<S>)result.getResult()).size()) +
				//	" results.");
				getLogger().info("Child command adding " + 
						(result.getResult() == null ? "null" : "not null") + 
						" results from site '" + routingToken.toRoutingTokenString() + "' for transaction Id [" + transactionId + "].");
				parentCommand.cumulativeCommandStatistics.incrementChildGetSuccessCount();
				if(result.getResult() != null)
					parentCommand.cumulativeCommandStatistics.addToCumulativeResults(result.getResult());
			}
			if(result.isError())
			{
				getLogger().info("child command results in error, adding no results from site [" + routingToken.toRoutingTokenString() + "] for transaction Id [" + transactionId + "].");
				parentCommand.cumulativeCommandStatistics.addError(this.routingToken, result.getThrowable());
				parentCommand.cumulativeCommandStatistics.incrementChildGetErrorCount();
			}
			// do this last so that the results are on the cumulative list
			// before the parent command continues
			parentCommand.countdownLatch.countDown();
		}
	}
}
