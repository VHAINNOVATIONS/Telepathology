/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr 21, 2009
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswlouthj
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
package gov.va.med.imaging.router.commands.vistarad;

import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.WellKnownOID;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.router.AsynchronousCommandResult;
import gov.va.med.imaging.core.interfaces.router.AsynchronousCommandResultListener;
import gov.va.med.imaging.core.interfaces.router.CoreArtifactResultError;
import gov.va.med.imaging.core.router.facade.InternalContext;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.vistarad.ExamSite;
import gov.va.med.imaging.exchange.business.vistarad.PatientEnterpriseExams;
import gov.va.med.imaging.exchange.enums.ArtifactResultStatus;
import gov.va.med.imaging.router.facade.ImagingContext;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * A command to get a List of Study instances:
 * 1.) from all Sites
 * 2.) related to a single patient
 * 
 * @author vhaiswlouthj
 *
 */
public class GetPatientEnterpriseExamsCommandImpl 
extends AbstractExamCommandImpl<PatientEnterpriseExams> 
{
	private static final long serialVersionUID = 1L;
	
	private static final int MAX_WAIT_SECONDS = 120;  // Wait 120 seconds for asynchronous results to be returned.
	
	private final RoutingToken routingToken;
	private final String patientIcn; 
	private final boolean fullyLoadExams;
	
	/**
	 * @param router
	 * @param asynchronousMethodProcessor
	 */
	public GetPatientEnterpriseExamsCommandImpl(RoutingToken routingToken, String patientIcn, Boolean fullyLoadExams)
	{
		super();
		this.routingToken = routingToken;
		this.patientIcn = patientIcn;
		this.fullyLoadExams = fullyLoadExams;
	}

	public RoutingToken getRoutingToken()
	{
		return this.routingToken;
	}

	public String getPatientIcn()
	{
		return this.patientIcn;
	}

	public boolean isFullyLoadExams()
	{
		return this.fullyLoadExams;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.patientIcn == null) ? 0 : this.patientIcn.hashCode());
		result = prime * result + (new Boolean(this.fullyLoadExams).hashCode());
		return result;
	}

	@Override
	protected boolean areClassSpecificFieldsEqual(Object obj)
	{
		// Perform cast for subsequent tests
		final GetPatientEnterpriseExamsCommandImpl other = (GetPatientEnterpriseExamsCommandImpl) obj;
		
		boolean allEqual = true;
		allEqual = allEqual && areFieldsEqual(this.patientIcn, other.patientIcn);
		allEqual = allEqual && areFieldsEqual(this.fullyLoadExams, other.fullyLoadExams);
		
		return allEqual;

	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AsynchronousCommandProcessor#parameterToString()
	 */
	@Override
	protected String parameterToString()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(this.getPatientId());
		
		return sb.toString();
	}

	public String getPatientId()
	{
		return getPatientIcn();
	}

	/**
	 * @see gov.va.med.imaging.core.router.AsynchronousCommandProcessor#callInTransactionContext()
	 */
	@Override
	public PatientEnterpriseExams callSynchronouslyInTransactionContext ()
	throws MethodException, ConnectionException
	{

		
		// GetTreatingSitesCommand needs a Site Number.
		TransactionContext transactionContext = TransactionContextFactory.get ();
		String siteNumber = transactionContext.getSiteNumber();
		transactionContext.setServicedSource (getRoutingToken().toRoutingTokenString());
		
		// Try to get the info from the cache
		PatientEnterpriseExams patientEnterpriseExams = getPatientEnterpriseExamsFromCache(patientIcn);

		// If the PatientEnterpriseExams was not in the cache, fetch the data and cache it
		if (patientEnterpriseExams == null)
		{
			patientEnterpriseExams = fetchAndCachePatientEnterpriseExams(siteNumber);
			transactionContext.setItemCached(false);
		}
		else
		{
			transactionContext.setItemCached(true);
		}
		
		return patientEnterpriseExams;
	}

	/**
	 * 
	 * @param siteNumber
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	private PatientEnterpriseExams fetchAndCachePatientEnterpriseExams(String siteNumber) throws MethodException, ConnectionException
	{

		PatientEnterpriseExams patientEnterpriseExams;
		List<RoutingToken> siteList = getTreatingSitesList(siteNumber);
		

		// If no treating sites were found, just create an empty PatientEnterpriseExams instance
		if (siteList == null || siteList.isEmpty ())
		{
			patientEnterpriseExams = new PatientEnterpriseExams(patientIcn);
		}
		else
		{
			// Setup an asynch result listener.
			CountDownLatch doneSignal = new CountDownLatch(siteList.size());
			ExamSiteWaiter examSiteWaiter = new ExamSiteWaiter(siteList, doneSignal);
			
			// Setup a GetExamListBySiteNumberCommand command for each Site found.
			// Add the asynch result listener waiter to each command.
			for (int i = 0; i < siteList.size (); i++)
			{
				Site site = (Site)siteList.get(i);
				
				if(fullyLoadExams)
				{
					ImagingContext.getRouter().getFullyLoadedExamSite(getRoutingToken(), patientIcn, 
							false, false, examSiteWaiter);
				}
				else
				{
					ImagingContext.getRouter().getExamSiteBySiteNumber(getRoutingToken(), patientIcn, 
							false, false, examSiteWaiter);
				}
			}		
			
			// Wait for results to be accumulated by the examSiteWaiter.
			try
			{
				doneSignal.await(MAX_WAIT_SECONDS, TimeUnit.SECONDS);
			}
			catch (InterruptedException e) { 
				getLogger().warn("Unexpected InterruptedException, ignoring ....");
			}
			
		    // Mark the waiter as timed out, so no other
		    // results get written during final processing
		    examSiteWaiter.setTimedOut();
			
			// Package results and return to caller.
			patientEnterpriseExams = new PatientEnterpriseExams(patientIcn);
			patientEnterpriseExams.getExamSites().putAll(examSiteWaiter.getExamSiteMap());
			
			// Cache the PatientEnterpriseExams
			cachePatientEnterpriseExams(patientEnterpriseExams);
		}
		return patientEnterpriseExams;
	}

	@SuppressWarnings("unchecked")
	private List<RoutingToken> getTreatingSitesList(String siteNumber) 
	throws MethodException, ConnectionException
	{
		List<ResolvedArtifactSource> artifactSources = 
			InternalContext.getRouter().getTreatingSites(getRoutingToken(), PatientIdentifier.icnPatientIdentifier(patientIcn));
		
		// The list may contain DOD sites, so strip those out before returning
		List<RoutingToken> nonDODSites = new ArrayList<RoutingToken>();
		
		for (ResolvedArtifactSource artifactSource : artifactSources)
		{
			//boolean vaDocumentSite = WellKnownOID.VA_DOCUMENT.isApplicable(artifactSource.getHomeCommunityId());
			boolean vaRadiologySite = WellKnownOID.VA_RADIOLOGY_IMAGE.isApplicable(artifactSource.getArtifactSource().getHomeCommunityId());
			if(vaRadiologySite)
				nonDODSites.add(artifactSource.getArtifactSource().createRoutingToken());
		}
		return nonDODSites;
	}
	
	/**
	 * This class aggregates the ExamSites returned by the asynchronous 
	 * GetExamSiteBySiteNumber commands.
	 * 
     * @author vhaiswlouthj
     */
	class ExamSiteWaiter implements AsynchronousCommandResultListener
    {
	    private boolean timedOut = false;
	    private CountDownLatch doneSignal;
	    private HashMap<RoutingToken, ExamSite> examSiteMap;
	    private int numCommandsSucceeded;
	    private int numCommandsFailed;
		private String lnSep = System.getProperty("line.separator");

	    public HashMap<RoutingToken, ExamSite> getExamSiteMap()
	    {
	        return examSiteMap;
	    }
	    
		public void setTimedOut()
	    {
	    	timedOut = true;
	    	
	    	// Log success/failure/orphaned results
			int numCommandsOrphaned = examSiteMap.size() - numCommandsSucceeded - numCommandsFailed;
			getLogger().debug (examSiteMap.size() + " Asynchronous Commands: " + numCommandsSucceeded + " Completed OK, "
			  + numCommandsFailed + " Failed, " + numCommandsOrphaned + " Orphaned.");

	    }

	    public ExamSiteWaiter(List<RoutingToken> treatingSites, CountDownLatch doneSignal)
	    {
	    	this.doneSignal = doneSignal;
	    	
	    	examSiteMap = new HashMap<RoutingToken, ExamSite>();
	    	// Create a default entry in the HashMap for each of the treating facilities.
	    	// Those ExamSites that are not replaced by results of asynch commands will remain 
	    	// in a status of "UNINITIALIZED". This guarantees that there is at least an 
	    	// uninitialized entry for every treating facility.
	    	for (RoutingToken site : treatingSites)
	    	{
	    		String siteName = routingToken.toString();
	    		try
	    		{
	    			siteName = VistaRadCommandCommon.getResolvedSiteName(routingToken, getCommandContext());
	    		}
	    		catch(MethodException mX)
	    		{
	    			getLogger().warn("MethodException getting site name for routing token '" + routingToken.toString() + "'.");
	    		}
	    		ExamSite examSite = new ExamSite(getRoutingToken(), ArtifactResultStatus.fullResult, siteName);
	    		examSiteMap.put(site, examSite);
	    	}
	    }

	    /**
	     * This method receives the result of an GetExamSiteBySiteNumberCommand
	     * and replaces its default ExamSite in the map, as long as the parent command
	     * has not timed out.
	     */
	    public void commandComplete(AsynchronousCommandResult result) 
	    {
	    	// Get the command
			GetExamSiteBySiteNumberCommandImpl cmd = (GetExamSiteBySiteNumberCommandImpl) result.getCommand();

			// Retry - no effect here.
			if (result.isRetryRequested())
			{
				return;
			}

			// Orphaned Command?
			if (timedOut)
			{
				getLogger().warn("Orphaned Command Arrived Late.  Details: " + lnSep + cmd.toString() + lnSep + result.toString());
				return;
			}

			// Command failed - cross command off the waiting list.
			if (result.isError())
			{
				numCommandsFailed++;
				getLogger().warn("Failed to get ExamSite for Patient ID " + patientIcn + ", Site Number " + cmd.getRoutingToken() + "." + lnSep + "Details: " + result.getException());
				
				// Create an "error" ExamSite and put it in the map
				String siteName = routingToken.toString();
	    		try
	    		{
	    			siteName = VistaRadCommandCommon.getResolvedSiteName(routingToken, getCommandContext());
	    		}
	    		catch(MethodException mX)
	    		{
	    			getLogger().warn("MethodException getting site name for routing token '" + routingToken.toString() + "'.");
	    		}
				ExamSite examSite = new ExamSite(cmd.getRoutingToken(), ArtifactResultStatus.errorResult, 
						siteName);				
				examSite.addArtifactResultError(CoreArtifactResultError.createFromException(routingToken, 
						result.getException()));							
	            examSiteMap.put(getRoutingToken(), examSite);
	            doneSignal.countDown();
	            
				return;
			}

			// Save this command's result, if applicable.
			numCommandsSucceeded++;
			if (result.getResult() != null)
			{
				// Overwrite the uninitialized ExamSite in the map.
	            ExamSite examSite = (ExamSite)result.getResult();
	            examSiteMap.put(examSite.getRoutingToken(), examSite);
	            doneSignal.countDown();
			}
	    }

    } 

}
