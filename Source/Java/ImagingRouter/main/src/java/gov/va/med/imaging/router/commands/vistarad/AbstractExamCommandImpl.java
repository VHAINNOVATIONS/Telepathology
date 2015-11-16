/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: 
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
import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.StudyURN;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.PatientNotFoundException;
import gov.va.med.imaging.core.interfaces.router.CoreArtifactResultError;
import gov.va.med.imaging.core.router.commands.configuration.CommandConfiguration;
import gov.va.med.imaging.core.router.facade.InternalContext;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.util.ExchangeUtil;
import gov.va.med.imaging.exchange.business.vistarad.*;
import gov.va.med.imaging.exchange.enums.ArtifactResultStatus;
import gov.va.med.imaging.router.commands.AbstractImagingCommandImpl;
import gov.va.med.imaging.router.commands.mbean.DODRequests;
import gov.va.med.imaging.router.facade.ImagingContext;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

import java.util.List;

/**
 * An abstract superclass of Exam-related commands, grouped because there is significant
 * overlap in the Exam commands that is contained here.
 * 
 * @author vhaiswlouthj
 *
 */
public abstract class AbstractExamCommandImpl<R extends Object> 
extends AbstractImagingCommandImpl<R>
{
	private static final long serialVersionUID = 1L;

	/**
	 * @param commandContext - the context available to the command
	 */
	public AbstractExamCommandImpl()
	{
		super();
	}

	/**
	 * This method attempts to retrieve the PatientEnterpriseExams instance
	 * from the cache for the patient with the specified patientIcn. If the 
	 * PatientEnterpriseExams is not found in the cache, this method will return
	 * null.
	 * 
	 * This method automatically spawns an asynchronous command to get 
	 * the PatientEnterpriseExams if it is not already cached. 
	 * 
	 * @return
	 */
	protected PatientEnterpriseExams getPatientEnterpriseExamsFromCache(String patientIcn)
	{
		PatientEnterpriseExams patientEnterpriseExams= null;
		if(getCommandContext().isCachingEnabled()) {
			try 
			{
				patientEnterpriseExams = getCommandContext().getIntraEnterpriseCacheCache().getPatientEnterpriseExams(patientIcn);
			}
			catch(CacheException cX) {
				getLogger().warn("Unable to get patient enterprise exams from cache", cX);
			}
			
			// If we didn't find the exams 
//			if (patientEnterpriseExams == null)
//			{
//				Command<PatientEnterpriseExams> cmd = 
//					  getCommandContext().getCommandFactory().createCommand(
//							  gov.va.med.imaging.exchange.business.vistarad.PatientEnterpriseExams.class,
//							  "GetPatientEnterpriseExamsCommand", 
//							  new Object[]{patientIcn, new Boolean(true)} 
//							  );
//					cmd.setPriority(ScheduledPriorityQueueElement.Priority.NORMAL.ordinal());
//					getCommandContext().getRouter().doAsynchronously(cmd);
//
//			}
		}
		return patientEnterpriseExams;
	}
	
	/**
	 * Cache a list of Exam instances into the appropriate cache.
	 * 
	 * @param resolvedSite
	 * @param examList
	 */
	protected void cachePatientEnterpriseExams(PatientEnterpriseExams patientEnterpriseExams)
	{
		if(getCommandContext().isCachingEnabled()) 
		{
			getLogger().info("Caching [" + patientEnterpriseExams.getExamSites().size() + "] exam sites.");
			
			try
			{
				getCommandContext().getIntraEnterpriseCacheCache().createPatientEnterpriseExams(patientEnterpriseExams);
			}
			catch (CacheException cX)
			{
				getLogger().warn(cX);
			}
		}
	}
	
	/**
	 * 
	 * @param siteNumber
	 * @param patientIcn
	 * @param fullyLoadExams
	 * @param forceRefresh Indicates the data should not come from the cache
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException 
	 */
	protected ExamSite getExamSite(
		RoutingToken routingToken, 
		String patientIcn, 
		boolean fullyLoadExams, 
		boolean forceRefresh,
		boolean forceImagesFromJb)
	throws MethodException, ConnectionException
	{
		getLogger().info("Finding exam site for site '" + routingToken + "', patient '" + 
				patientIcn + ", fullyLoaded=" + fullyLoadExams + ", forceRefresh=" + forceRefresh + ".");
		ExamSite examSite = null;
		if(!forceRefresh)
		{
			examSite = getExamSiteFromCache(routingToken, patientIcn);
		}
		TransactionContext transactionContext = TransactionContextFactory.get();
		// If the ExamSite was not cached, or if was not cached as ONLINE, retrieve it directly
		// and attempt to cache the updated results.
		// or if the exam site was cached but not fully loaded on a fully loaded request, then get from data source
		if ((examSite == null) || 
				(examSite.getArtifactResultStatus() != ArtifactResultStatus.fullResult) || 
				((!examSite.isExamsFullyLoaded()) && fullyLoadExams))
		{
			getLogger().info("Did not find examSite for site '" + routingToken + "' and patient '" + patientIcn + "' in cache (or cached instance was not usable).");
			transactionContext.setItemCached(false);
			examSite = getExamSiteFromDataSource(routingToken, patientIcn, fullyLoadExams, 
					forceRefresh, forceImagesFromJb);
			// determine if we should async to get fully loaded data
			// JMW 2/18/2010 - doesn't matter what the request was for, if the examSite we got is not fully loaded, get fully data (if appropriate)
			// if requesting from DoD, always getting fully loaded data from data source so don't need to async reload
			
			// 7/14/2010 JMW P104
			// now always cache the metadata from VistARad (see note in cacheExamSite)
			// cacheExamSite no longer returns boolean property indicating success.  The determination of local/remote
			// should be done here only to determine if the async call for full loaded data should be done.
			// only want to async for fully loaded data if:
			// 1) data requested was not fully loaded (regardless of result)
			// 2) data requested is from remote site, don't async to populate local data since a remote site will likely kick that off anyway
			
			cacheExamSite(routingToken, patientIcn, examSite); // put exam site into cache (even if from local site)
			
			// if the result is not fully loaded
			if(!examSite.isExamsFullyLoaded())
			{
				// if the exam site is for a remote site
				if(!routingToken.getRepositoryUniqueId().equals(getCommandContext().getRouter().getAppConfiguration().getLocalSiteNumber()))
				{
					// not local site
					// if the patient has 1 or more exams, then get the fully loaded exams - otherwise what's the point?
					if(examSite.size() > 0)
					{				
						// if the request was for fully loaded exams, then we wouldn't want to repeat the request
						// since it will likely get the same result
						// so only do async request if current request is not fully loaded and if examSite was not fully loaded
						if(!fullyLoadExams)  
						{
							getLogger().info("Exams not fully loaded, async requesting exams for patient '" + patientIcn + "' from site '" + routingToken  + "'.");
							// non fully loaded exam site was cached, kick off async method to get fully loaded ExamSite,
							// it will automatically get put into the cache.
							// JMW 3/30/2011 - for the async request to get the exams, do want to force a refresh
							// and do NOT want to move images from JB to HD
							ImagingContext.getRouter().getFullyLoadedExamSite(routingToken, 
									patientIcn, true, false);
						}
						else
						{
							getLogger().info("Exams not fully loaded but current request was for fully loaded, not trying again.");
						}
					}
					else
					{
						getLogger().info("patient '" + patientIcn + "' has 0 exams from site '" + routingToken + "', not getting fully loaded exams.");
					}
				}
			}
		}
		else
		{
			getLogger().info("Got examSite for site '" + routingToken + "' and patient '" + patientIcn + "' in cache, returning.");
			transactionContext.setItemCached(true);
		}
		
		return examSite;
	}


	/**
	 * Attempt to get an ExamSite from the cache. This method returns null if the 
	 * PatientEnterpriseExams is not cached, or if the ExamSite is not found in the 
	 * cached PatientEnterpriseExams.
	 * 
	 * @param siteNumber
	 * @param patientIcn
	 * @return
	 */
	protected ExamSite getExamSiteFromCache(RoutingToken routingToken, String patientIcn)
	{
		ExamSite examSite = null;
		getLogger().info("Searching cache for exam site from site '" + routingToken + "' for patient '" + patientIcn + "'.");
		if(getCommandContext().isCachingEnabled()) {
			try 
			{
				examSite = getCommandContext().getIntraEnterpriseCacheCache().getExamSite(routingToken, patientIcn);

			}
			catch(CacheException cX) {
				getLogger().warn("Unable to get exam site from cache", cX);
			}
			
		}
		return examSite;		
	}
	
	private Exam getExamFromDataSource(StudyURN studyUrn)
	throws MethodException
	{
		try
		{
			Exam exam = ImagingContext.getRouter().getExamFromDataSource(studyUrn);
			return exam;
		}
		catch(ConnectionException cX)
		{
			return null;
		}
	}
	
	// why is ExamSite an input parameter for this method?
	private ExamSite getExamSiteFromDataSource(
		RoutingToken routingToken, 
		String patientIcn, 
		boolean fullyLoadExams,
		boolean forceRefresh,
		boolean forceImagesFromJb) 
	throws MethodException, MethodConnectionException
	{
		List<Exam> exams = null;
		ExamListResult examList = null;
		
		String siteName = VistaRadCommandCommon.getResolvedSiteName(routingToken, getCommandContext());
		
		DODRequests dodRequests = DODRequests.getRoiCommandsStatistics();
		dodRequests.incrementTotalDodExamRequests();
		
		boolean isSiteDod = ExchangeUtil.isSiteDOD(getRoutingToken().getRepositoryUniqueId());
		
		// this should only be true on the CVIX
		if(isSiteDod && CommandConfiguration.getCommandConfiguration().isEnsurePatientSeenAtDoD())
		{
			// 
			try
			{
				getLogger().info("Ensuring patient '" + patientIcn + "' has been seen at the DoD by looking up treating sites.");
				InternalContext.getRouter().getTreatingSitesFromDataSource(getRoutingToken(), 
						PatientIdentifier.icnPatientIdentifier(patientIcn), true);
			}
			catch(PatientNotFoundException pnfX)
			{
				dodRequests.incrementNonCorrelatedDodExamRequests();
				// if we get a patient not found exception from station 200 that means the patient has not been seen there and isn't really correlated with the DoD
				getLogger().warn("Patient '" + patientIcn + "' not found, indicates patient not really seen in the DoD, returning empty result set.");
				return new ExamSite(getRoutingToken(), ArtifactResultStatus.fullResult, siteName);
			}
			catch(Exception ex)
			{
				// just in case some other crazy exception occurs (with communicating with VistA or something) this ensures an ExamSite object is returned
				ExamSite examSite = 
					new ExamSite(routingToken, ArtifactResultStatus.errorResult, siteName);
				examSite.addArtifactResultError(CoreArtifactResultError.createFromException(routingToken, ex));			
			
				getLogger().error("Error getting exam site from " + routingToken.toRoutingTokenString(), ex);
				return examSite;
			}
		}	
		
		try
		{
			getLogger().info("Requesting exams for site '" + routingToken.toString() + "' for patient '" + patientIcn + "' from data source, fully loaded=" + fullyLoadExams);
			examList = ImagingContext.getRouter().getExamsForPatientFromDataSource(routingToken, 
				patientIcn, fullyLoadExams, forceRefresh, forceImagesFromJb);
			// JMW 9/24/2010 - for now, we are 
			exams = examList.getArtifacts();
		}
		catch(Exception x)
		{
			// this is a bit odd since the command will always throw a MethodException, not really sure if we should handle
			// it this way, but always want to return an ExamSite object, even one containing an error message so this 
			// should do that.
			
			// If we get here then we were unable to connect to a data source to satisfy the request.
			// Create an exam site with a status of ERROR, set the error message, and return it
			// setting the artifactResultStatus to full even though it really isn't since we got an exception
			ExamSite examSite = 
				new ExamSite(routingToken, ArtifactResultStatus.errorResult, siteName);
			examSite.addArtifactResultError(CoreArtifactResultError.createFromException(routingToken, x));			
		
			getLogger().error("Error getting exam site from " + routingToken.toRoutingTokenString(), x);
			return examSite;
		}
		
		getLogger().info("Got " + ((exams == null || exams.size() == 0) ? "no" : exams.size()) + " patient '" + patientIcn + "' exams.");


		// We got a response from the datasource. Create an ExamSite with a status
		// of ONLINE
		
		ExamSite examSite = new ExamSite(routingToken, 
				examList.getArtifactResultStatus(), siteName);
		if(examList.getArtifactResultErrors() != null)
		{
			examSite.setArtifactResultErrors(examList.getArtifactResultErrors());
		}

		// If the exam list is not null, it means there were exams at the site.
		// Add them to the ExamSite
		if (exams != null)
		{
			for (Exam exam : exams)
			{
				if(exam.isImagesIncluded())
					updateExamImagesConsolidatedSite(exam.getImages());
				examSite.add(exam);
			}
		}
		return examSite;		
	}
	
	private void cacheExamIfPossible(RoutingToken routingToken, Exam exam)
	{
		if(getCommandContext().isCachingEnabled()) 
		{			
			if(exam != null)
			{
				ExamSite examSite = getExamSiteFromCache(routingToken, exam.getPatientIcn());
				if(examSite != null)
				{				
					getLogger().info("Found exam site '" + examSite.getRoutingToken() + "' in cache, updated exam in cache.");
					try
					{
						examSite.addOrUpdateExam(exam);
						getCommandContext().getIntraEnterpriseCacheCache().createExamSite(
							routingToken, 
							exam.getPatientIcn(), 
							examSite);
					}
					catch (CacheException cX)
					{
						getLogger().warn("CacheException caching exam with exam site '" + exam.getSiteNumber() + "', " + cX.getMessage(), cX);
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param patientIcn
	 * @param examSite
	 * @return True if the examSite *should* be cached, false if the exam site should not be cached. This indicates if a subsequent async command should be used to fully populate the cache
	 * @throws ConnectionException 
	 * @throws MethodException 
	 */
	private void cacheExamSite(RoutingToken routingToken, String patientIcn, ExamSite examSite) 
	throws MethodException, ConnectionException
	{
		if(getCommandContext().isCachingEnabled()) 
		{
			//InternalRouter internalRouter = InternalContext.getRouter();
			
			// JMW 9/18/2009 P90
			// if the exam site is from the local site then we do not want to cache it.
			// This is because the exam site should not be coming from the local site because VRad should only
			// be querying for data from remote sites. If the VIX is getting an ExamSite from the local site that
			// indicates it is for a request from a remote VIX, that remote VIX should cache the data but this
			// local VIX should not in case the remote VIX needs to do a force refresh of the data, it will
			// be sure to get it from the data source and not this local site cache.
			
			// JMW 7/14/2010 P104
			// the VIX ALWAYS caches metadata for VistARad now.  This is necessary so the VIX can efficiently
			// request images from the local site.  IF a remote site asks the local site for an image, the metadata
			// needs to be cached so the VIX can get the UNC path for the image with fewer RPC calls
			// the forceRefresh parameter is now passed to the datasource still allowing a refresh to properly occur
			
			try
			{
				getLogger().info("Caching Exam Site [" + routingToken.getRepositoryUniqueId() + "] for patient '" + patientIcn + "'.");
				getCommandContext().getIntraEnterpriseCacheCache().createExamSite(
					routingToken, 
					patientIcn, 
					examSite);
			}
			catch (CacheException cX)
			{
				getLogger().warn(cX);
			}			
		}
	}
	
	/**
	 * Tries to get an exam from the cache, does not access a data source
	 * @param studyUrn
	 * @return
	 * @throws URNFormatException 
	 */
	protected Exam getExamFromCache(StudyURN studyUrn) 
	throws URNFormatException
	{
		String patientIcn = studyUrn.getPatientId();
		
		// StudyURN is being used as the RoutingToken
		ExamSite examSite = this.getExamSiteFromCache(studyUrn, patientIcn);
		if((examSite == null) || (examSite.getArtifactResultStatus() != ArtifactResultStatus.fullResult))
			return null;
		//return examSite.getExams().get(examId);
		return examSite.getByStudyUrn(studyUrn.toString());
	}
	
	protected ExamImage getExamImageFromCache(ImageURN imageUrn)	
	{
		getLogger().info("searching for exam image in cache '" + imageUrn.toString() + "'.");
		ExamImage image = null;
		try
		{
			StudyURN studyUrn = imageUrn.getParentStudyURN();
			// ImageURN is a routing token realization
			Exam exam = getExamFromCache(studyUrn);
			if((exam != null) && (exam.isImagesIncluded()))
			{				
				getLogger().info("found exam image exam in cache, exam is fully loaded - returning exam image.");
				return exam.getImages().get(imageUrn.toString());
			}
		}
		catch(URNFormatException iurnfX)
		{
			getLogger().error(iurnfX);
		}
		getLogger().info("did not find exam image exam in cache, returning null.");
		return image;
	}

	/**
	 * 
	 * @param studyUrn
	 * @return
	 * @throws MethodException
	 */
	protected Exam getFullyLoadedExam(StudyURN studyUrn)
	throws MethodException
	{
		getLogger().info("Finding fully loaded exam '" + studyUrn + "'.");
		Exam exam = null;
		try
		{
			exam = getExamFromCache(studyUrn);
		}
		catch (URNFormatException x)
		{
			throw new MethodException(x);
		}
		
		if((exam != null) && (exam.isLoaded()))
		{
			getLogger().info("Found exam in cache and is fully loaded, returning.");
			TransactionContextFactory.get().setItemCached(true);
		}
		else
		{
			getLogger().info("Exam not in cache or not fully loaded, requesting exam from data source.");
			TransactionContextFactory.get().setItemCached(false);
			
			exam = getExamFromDataSource(studyUrn);			
			if(exam.isImagesIncluded())
			{
				updateExamImagesConsolidatedSite(exam.getImages());
			}
			// there might be an ExamSite in the cache, if so update the Exam entry in the cache.
			// I'm not sure if want to cache this but I can't think of a reason not to cache it...
			
			// StudyURN implements RoutingToken
			cacheExamIfPossible(studyUrn, exam);
		}
		
		/*
		String siteNumber = studyUrn.getOriginatingSiteId();
		String patientIcn = studyUrn.getPatientId();
		// Get the ExamSite, requesting a fully populated exam if the exam is not currently in the cache
		ExamSite examSite = getExamSite(siteNumber, patientIcn, true, false);		
		if (examSite != null)
		{			
			// Get the exam from the examSite
			exam = examSite.getExams().get(studyUrn.toString());
			
			if (exam != null && !exam.isLoaded())
			{				
				getLogger().info("Exam found but not fully loaded, loading exam data.");
				// setting this to false, it might have been set to true if the ExamSite was in the cache
				// but since the data in the exam is not in the cache, set this to false here.
				TransactionContextFactory.get().setItemCached(false);
				
				exam = getExamFromDataSource(studyUrn);
				
				
				//exam.setExamReport(getExamReport(studyUrn));
				//exam.setExamRequisitionReport(getExamRequisitionReport(studyUrn));
				//exam.setImages(getExamImages(studyUrn));
				
				// if we got here then the ExamSite has been cached but the exam was not fully loaded
				// I'm not sure if want to cache this but I can't think of a reason not to cache it... 
				
				cacheExamIfPossible(exam);
			}
		}*/
		
		return exam;
	}
	
	protected String getExamReport(StudyURN studyUrn)
	throws MethodException
	{
		getLogger().info("Retrieving exam report for exam '" + studyUrn.toString() + "'");
		try
		{
			String report = ImagingContext.getRouter().getExamReportFromDataSource(studyUrn);			
			return report;
		}
		catch(ConnectionException cX)
		{
			return null;
		}
	}

	protected String getExamRequisitionReport(StudyURN studyUrn)
	throws MethodException
	{
		getLogger().info("Retrieving exam requisition report for exam '" + studyUrn.toString() + "'");
		try
		{
			String report = ImagingContext.getRouter().getExamRequisitionReportFromDataSource(studyUrn);
			return report;
		}
		catch(ConnectionException cX)
		{
			return null;
		}
	}

	
	protected ExamImages getExamImagesFromDataSource(StudyURN studyUrn)
	throws MethodException, ConnectionException
	{
		getLogger().info("Retrieving exam images for exam '" + studyUrn.toString() + "' from data source.");
		ExamImages result = ImagingContext.getRouter().getExamImagesForExamFromDataSource(studyUrn);
		
		//TODO: update the result ExamImages object to look for consolidated site entries in the images, update each image object as needed
		
		if((result != null) && (result.containsConsolidatedSite()))
		{
			updateExamImagesConsolidatedSite(result);
		}
		return result;
	}
	
	protected void updateExamImagesConsolidatedSite(ExamImages examImages)
	{
		if(examImages != null)
		{
			String consolidatedSiteNumber = examImages.getConsolidatedSiteNumber();
			if(consolidatedSiteNumber != null)
			{
				// make sure the consolidated site exists in the site service
				Site site = getConsolidatedSite(consolidatedSiteNumber);
				if(site != null)
				{				
					for(int i = 0; i < examImages.size(); i++)
					{
						ExamImage examImage = examImages.get(i);
						ExamImage newExamImage = examImage.cloneWithConsolidatedSiteNumber(consolidatedSiteNumber);
						if(newExamImage != null) // just in case - should never happen
						{
							// replace the current exam image with the new one
							examImages.set(i, newExamImage);
						}
					}				
				}
			}
		}
	}
	
	private Site getConsolidatedSite(String consolidatedSiteNumber)
	{
		try
		{
			return getCommandContext().getSiteResolver().getSite(consolidatedSiteNumber);					
		}
		catch(ConnectionException cX)
		{
			getLogger().warn("ConnectionException finding resolved site for consolidated site '" + consolidatedSiteNumber + ", " + cX.getMessage(), cX);
		}
		catch(MethodException mX)
		{
			getLogger().warn("MethodException finding resolved site for consolidated site '" + consolidatedSiteNumber + ", " + mX.getMessage(), mX);			
		}
		return null;
	}

	@Override
	public boolean equals(Object obj)
	{
		// Check objectEquivalence
		if (this == obj)
		{
			return true;
		}
		
		// Check that classes match
		if (getClass() != obj.getClass())
		{
			return false;
		}
		
		return areClassSpecificFieldsEqual(obj);
		
	}

	protected abstract boolean areClassSpecificFieldsEqual(Object obj);

	public boolean areFieldsEqual(Object field1, Object field2)
	{
		// Check the study URN
		if (field1 == null)
		{
			if (field2 != null)
			{
				return false;
			}
		} 
		else if (!field1.equals(field2))
		{
			return false;
		}
		
		return true;
	}
	

}
