/**
 * 
 */
package gov.va.med.imaging.core.router.commands;

import gov.va.med.OID;
import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.router.AsynchronousCommandResult;
import gov.va.med.imaging.core.interfaces.router.AsynchronousCommandResultListener;
import gov.va.med.imaging.core.router.AbstractCommandImpl;
import gov.va.med.imaging.core.router.facade.InternalContext;
import gov.va.med.imaging.exceptions.OIDFormatException;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * For commands that get a cumulative list of results from all
 * treating facilities, this command provides the basic framework
 * and handles the asynchronous child commands.
 * 
 * @author vhaiswbeckec
 *
 */
public abstract class AbstractCumulativeCommandImpl<R>
extends AbstractCommandImpl<List<R>>
{
	private static final long serialVersionUID = -7660250657781105527L;

	private static final long MAX_WAIT_TIME = 120000L;  // Wait 120 seconds for asynchronous results to be returned.
	
	private CountDownLatch countdownLatch;
	private List<R> cumulativeResults = Collections.synchronizedList(new ArrayList<R>());
	private int childGetSuccessCount = 0;
	private int childGetErrorCount = 0;
	

	/**
	 * Derived classes must make the patient ID available so that the
	 * treating facilities list may be obtained.
	 * 
	 * @return
	 */
	protected abstract PatientIdentifier getPatientIdentifier();
	
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
		AsynchronousCommandResultListener<?> listener);
	
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
	public List<R> callSynchronouslyInTransactionContext()
	throws MethodException, ConnectionException
	{
		// GetTreatingSitesCommand needs a Site Number.
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		// if the routing token include a wildcard for the repository then
		// create multiple child commands to service each repository within the
		// community if we can determine the repositories.
		boolean useExtantGateway = false;
		String homeCommunityId = getRoutingToken().getHomeCommunityId();
		if( RoutingToken.ROUTING_WILDCARD.equals(getRoutingToken().getRepositoryUniqueId()) )
		{
			OID homeCommunityOid = null;
			try
			{
				homeCommunityOid = OID.create(homeCommunityId);
				useExtantGateway = getCommandContext().getSiteResolver().isRepositoryGatewayExtant(homeCommunityOid);
			}
			catch (OIDFormatException x)
			{
				getLogger().warn(
					"Error determining availabality of gateway to '" + getRoutingToken().getHomeCommunityId() +
					"' because that is not a valid OID.  Assuming no gateway is available and continuing.");
			}
		}

		// if there is a gateway that will service the entire request for us then create a single
		// child transaction and call that with the given routing token
		// else determine the repositories to call and create multiple child processes for each
		if(useExtantGateway)
			return createAndExecuteGatewayCommand(transactionContext, homeCommunityId);
		else
			return createAndExecuteRepositoryCommands(transactionContext, homeCommunityId);
	}

	/**
	 * 
	 * @param transactionContext
	 * @param homeCommunityId
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	private List<R> createAndExecuteRepositoryCommands(
		TransactionContext transactionContext, 
		String homeCommunityId)
	throws MethodException, ConnectionException
	{
		RoutingToken localRoutingToken = getTreatingFacilityRepositoryRoutingToken(homeCommunityId);
		transactionContext.setServicedSource( localRoutingToken.toRoutingTokenString() );
		setInitialImagingSecurityContext();		
		
		// Try to get all the Sites for this Patient ID.
		List<ResolvedArtifactSource> artifactSourceList = 
			InternalContext.getRouter().getTreatingSites(localRoutingToken, getPatientIdentifier());

		// Exception or no Sites found?  Bail.
		if (artifactSourceList == null || artifactSourceList.isEmpty ()) 
			return new ArrayList<R> (0);
		
		StringBuilder sbMsg = new StringBuilder();
		sbMsg.append("Patient has been seen at the following sites:");
		for(ResolvedArtifactSource site : artifactSourceList)
			sbMsg.append("\t" + site.toString());
		getLogger().info(sbMsg);
		
		this.countdownLatch = new CountDownLatch(artifactSourceList.size());
		setSecondaryImagingSecurityContext();
		int childCommandsCount = artifactSourceList.size();
		// Setup a GetStudyListBySiteNumberCommand command for each Site found.
		// Add the asynch result listener waiter to each command.
		for(ResolvedArtifactSource artifactSource : artifactSourceList)
		{
			RoutingToken siteRoutingToken = artifactSource.getArtifactSource().createRoutingToken();
			
			/*
			Command<List> childCommand = createCommand(siteNumber);
			childCommand.addListener (
				new CumulativeCommandChildListener<R>(this)
			);
			childCommand.setPriority(ScheduledPriorityQueueElement.Priority.HIGH.ordinal());
			// Send each command to the black box for asynchronous execution.
			getCommandContext().getRouter().doAsynchronously( childCommand );
			*/
			callChildCommandAsync(siteRoutingToken, new CumulativeCommandChildListener<R>(this));
		}		
		
		// Wait for results to be accumulated
		try
		{
			countdownLatch.await(MAX_WAIT_TIME, TimeUnit.MILLISECONDS);
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
			childGetSuccessCount + " Completed OK, " + 
			childGetErrorCount + " Failed, " + 
			orphaned + " Orphaned.");
		getLogger().info("Returning '" + (cumulativeResults == null ? 0 : cumulativeResults.size()) + "' cumulative results.");
		
		return cumulativeResults;
	}

	/**
	 * 
	 * @param transactionContext
	 * @param homeCommunityId
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	private List<R> createAndExecuteGatewayCommand(
		TransactionContext transactionContext, 
		String homeCommunityId)
	throws MethodException, ConnectionException
	{
		setInitialImagingSecurityContext();		
		
		StringBuilder sbMsg = new StringBuilder();
		sbMsg.append("Cumulative command delegated to gateway serving '" + getRoutingToken().toString() + "'.");
		getLogger().info(sbMsg);
		
		this.countdownLatch = new CountDownLatch(1);
		setSecondaryImagingSecurityContext();
		callChildCommandAsync(getRoutingToken(), new CumulativeCommandChildListener<R>(this));
		
		// Wait for results to be accumulated
		try
		{
			countdownLatch.await(MAX_WAIT_TIME, TimeUnit.MILLISECONDS);
		}
		catch (InterruptedException x)
		{
			String msg = "'" + this.getClass().getSimpleName() + "' interrupted waiting for child commands to complete, abandoning command execution.";
			getLogger().warn(msg);
			throw new MethodException(msg);
		}
		
		long orphaned = countdownLatch.getCount();		// should be zero
		getLogger().debug(
			"1 Asynchronous Command: " + 
			childGetSuccessCount + " Completed OK, " + 
			childGetErrorCount + " Failed, " + 
			orphaned + " Orphaned.");
		getLogger().info("Returning '" + (cumulativeResults == null ? 0 : cumulativeResults.size()) + "' cumulative results.");
		
		return cumulativeResults;
	}
	
	/**
	 * 
	 * @author vhaiswbeckec
	 *
	 * @param <S>
	 */
	class CumulativeCommandChildListener<S>
	implements AsynchronousCommandResultListener<List<S>>
	{
		private AbstractCumulativeCommandImpl<S> parentCommand;
		CumulativeCommandChildListener(AbstractCumulativeCommandImpl<S> parentCommand)
		{
			this.parentCommand = parentCommand;
		}
		
		@SuppressWarnings("synthetic-access")
		@Override
		public void commandComplete(AsynchronousCommandResult<List<S>> result)
		{
			if(result.isSuccess())
			{
				getLogger().info("child command adding " + 
					(result.getResult() == null ? 0 : ((List<S>)result.getResult()).size()) +
					" results.");
				++parentCommand.childGetSuccessCount;
				if((List<S>)result.getResult() != null)
					parentCommand.cumulativeResults.addAll( (List<S>)result.getResult() );
			}
			if(result.isError())
			{
				getLogger().info("child command results in error, adding no results.");
				++parentCommand.childGetErrorCount;
			}
			// do this last so that the results are on the cumulative list
			// before the parent command continues
			parentCommand.countdownLatch.countDown();
		}
	}

}
