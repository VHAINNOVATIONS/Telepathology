/**
 * 
 */
package gov.va.med.imaging.router.commands;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.BhieStudyURN;
import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.StudyURN;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.business.Image;
import gov.va.med.imaging.exchange.business.Series;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.exchange.business.StudySetResult;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.router.facade.ImagingContext;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * An abstract superclass of Study-related commands, grouped because there is significant
 * overlap in the Study commands that is contained here.
 * 
 * @author vhaiswbeckec
 *
 */
public abstract class AbstractStudyCommandImpl<R extends Object> 
extends AbstractImagingCommandImpl<R>
{
	private static final long serialVersionUID = -4942954920091003627L;

	/**
	 * @param commandContext - the context available to the command
	 */
	public AbstractStudyCommandImpl()
	{
		super();
	}
	
	protected List<Study> getPatientStudyList(
			RoutingToken routingToken, 
			PatientIdentifier patientIdentifier, 
			StudyFilter filter, 
			StudyLoadLevel studyLoadLevel)
		throws MethodException
	{
		StudySetResult studySet = getPatientStudySetResult(routingToken, 
				patientIdentifier, filter, studyLoadLevel);
		if((studySet == null) || (studySet.getArtifacts() == null))
			return new ArrayList<Study>();
		SortedSet<Study> studies = studySet.getArtifacts();
		List<Study> studyList = new ArrayList<Study>(studies == null ? 0 : studies.size());
		studyList.addAll(studies);
		return studyList;
	}

	/**
	 * 
	 * @param siteNumber
	 * @param patientId
	 * @param filter
	 * @return
	 * @throws MethodException
	 */
	protected StudySetResult getPatientStudySetResult(
		RoutingToken routingToken, 
		PatientIdentifier patientIdentifier, 
		StudyFilter filter, 
		StudyLoadLevel studyLoadLevel)
	throws MethodException
	{
		
		if(filter != null && !filter.isSiteAllowed(routingToken.getRepositoryUniqueId()))
		{
			getLogger().info("Site number [" + routingToken.toString() + "] is excluded in the StudyFilter, not loading study list from this site");
			return StudySetResult.createFullResult(new TreeSet<Study>());			
		}
		
		try
		{
			StudySetResult studySet = ImagingContext.getRouter().getStudySet(
				routingToken, 
				patientIdentifier, 
				filter, 
				studyLoadLevel);				
			getLogger().info("Got " + ((studySet == null || studySet.getArtifacts() == null || studySet.getArtifacts().size() == 0) ? "no" : studySet.getArtifacts().size()) + 
					" patient '" + patientIdentifier + "' studies.");
			
			if(studySet == null)
				return StudySetResult.createFullResult(null);
			if(studySet.getArtifacts() == null)
				return studySet;
			//Set<Study> studies = studySet.getArtifacts();
			
			// need to check to see if the studies or the images contain consolidated site numbers and need to be "fixed" up
			studySet = CommonStudyFunctions.updateConsolidatedSitesInStudySetResult(studySet, 
					getCommandContext());
			
			Set<Study> studies = studySet.getArtifacts();
			
			// 1/24/2011 - decided we won't kick off the async request, just allow the the request to be made
			// to the secondary VIX and the secondary VIX will just get the image without having the metadata 			
				
			// regardless of the studyLoadLevel, try to cache the studies
			// if this returns false then 1 or more study was not fully loaded
			// studies from DoD should always be fully loaded, so this should always return true for DoD
			if(!CommonStudyCacheFunctions.cacheStudyList(getCommandContext(), 
					routingToken.getRepositoryUniqueId(), studies))
			{
				// we don't kick of an async request for the local site because if the request was to a remote site
				// through Federation, the async request will come from the original site, not the local site.
				// so site 1 calls site 2 for a shallow study list.  Site 1 will kick off the async request for the
				// full graph and call site 2. We don't want site 2 making the same async request since that will
				// just duplicate the work.
				if(!routingToken.getRepositoryUniqueId().equals(getCommandContext().getRouter().getAppConfiguration().getLocalSiteNumber()))
				{
					if(studies.size() > 0)
					{
						if(!studyLoadLevel.isFullyLoaded())
						{
							getLogger().info("At least 1 study was not cached because it was not fully loaded, requesting study graph again async fully loaded");
							// if this study data is from a remote site
							// create an asynch request to re-request this patient studies with the full data
							startAsyncFullStudyGraphRequest(routingToken, patientIdentifier, filter);
						}
						else
						{
							getLogger().info("At least 1 study was not cached because it was not fully loaded, but current request was for fully loaded studies, not trying again.");
						}
					}
					else
					{
						getLogger().info("No studies were found for patient '" + patientIdentifier + "' from site '" + routingToken.toString() + "', not getting fully loaded studies.");
					}
				}
				else
				{
					getLogger().info("At least 1 study was not cached because it was not fully loaded, but request for local site - not submitting request for full graph");
				}
			}						
			return studySet;			
		}
		catch(ConnectionException cX)
		{
			throw new MethodConnectionException(cX);
		}
	}
	
	private void startAsyncFullStudyGraphRequest(RoutingToken routingToken, PatientIdentifier patientIdentifier, 
			StudyFilter filter)
	{
		ImagingContext.getRouter().getStudyList(routingToken, patientIdentifier, filter);			
	}
	
	protected Study getPatientStudy(
			GlobalArtifactIdentifier artifactId, 
			boolean includeDeletedImages)
	throws MethodException
	{
		// by default use old behavior to allow data from cache if it is there
		return getPatientStudy(artifactId, includeDeletedImages, true);
	}

	/**
	 * 
	 * @param studyUrn
	 * @return
	 * @throws MethodException
	 */
	protected Study getPatientStudy(
		GlobalArtifactIdentifier artifactId, 
		boolean includeDeletedImages,
		boolean allowedFromCache)
	throws MethodException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		PatientIdentifier patientIdentifier = artifactId instanceof StudyURN ? 
			((StudyURN)artifactId).getThePatientIdentifier() :
			artifactId instanceof BhieStudyURN ?
				((BhieStudyURN)artifactId).getThePatientIdentifier() :
				null;

		Study study = null;
		try
		{
			// JMW 5/7/2012 P130 - use option to not get study metadata from cache if always want latest and greatest
			if(allowedFromCache)
				study = getStudyFromCache(artifactId);
			
			if(study != null)
			{
				// JMW 9/3/2010 - p104, updated 9/23/2010
				// if a study was found in the cache and the user requested the study include
				// deleted images, check to see if the study can include deleted images and if it 
				// does in fact contain deleted images
				// if not, set the study = null to get the study again from the data source
				if(includeDeletedImages && study.getStudyDeletedImageState().isCanIncludeDeletedImages() && !study.getStudyDeletedImageState().isDeletedImagesLoaded())
				{
					getLogger().info("Found study '" + study.toString() + "' in cache but does not contain deleted images which user requested and the study can potentially contain deleted images, will not use study from cache.");
					study = null;
				}
			}
			if(study == null)
			{
				StudyFilter filter = new StudyFilter(artifactId);
				filter.setIncludeDeleted(includeDeletedImages);
				List<Study> studies = getPatientStudyList(artifactId, patientIdentifier, filter, StudyLoadLevel.FULL);
	
				if(studies.size() > 0)
				{
					study = studies.get(0);
					getLogger().info("Found study [" + artifactId.toString() + "] from data source");
					transactionContext.setItemCached(Boolean.FALSE);
				}
				else
					getLogger().info("Unable to find study [" + artifactId.toString() + "] from data source");
			}
			else
			{
				getLogger().info("Found study [" + artifactId.toString() + "] in cache");
				transactionContext.setServicedSource(artifactId.toRoutingTokenString());
				transactionContext.setItemCached(Boolean.TRUE);
			}
		}
		catch(MethodException mX)
		{
			throw mX;
		}
		
		//return extractImagesFromStudy(study);
		return study;
	}	
	
	/**
	 * 
	 * @param studyUrn
	 * @return
	 */
	protected Study getStudyFromCache(GlobalArtifactIdentifier gaid)
	{
		return CommonStudyCacheFunctions.getStudyFromCache(getCommandContext(), gaid);
	}
	
	/**
	 * 
	 * @param study
	 * @return
	 */
	protected List<Image> extractImagesFromStudy(Study study)
	{
		List<Image> images = new ArrayList<Image>(study.getImageCount());
		for(Series ser : study.getSeries())
		{
			for(Image image : ser)
			{
				images.add(image);
			}
		}
		return images;
	}
	
	/**
     * @param imageUrn
     * @return
     */
    protected Image findImageInCachedStudyGraph(ImageURN imageUrn)
    {
	    Image image = null;
	    
	    getLogger().info("ImageURN is '" + (imageUrn == null ? "<null>" : imageUrn.toString()) + "'.");
	    if(imageUrn == null)
	    	return null;
	    
		try 
		{
			StudyURN studyUrn = null;
			// The StudyURN create() method will determine whether to create an instance of itself
			// or create one of its derivative classes.
			// CTB 03Jun2010
			//if(imageUrn instanceof BhieImageURN)
			//	studyUrn = BhieStudyURN.create(imageUrn.getOriginatingSiteId(), imageUrn.getStudyId(), imageUrn.getPatientIcn());
			//else
			studyUrn = imageUrn.getParentStudyURN();
		    getLogger().info("StudyURN is '" + (studyUrn == null ? "<null>" : studyUrn.toString()) + "'.");
			
			Study study = getStudyFromCache(studyUrn);
			if(study != null)
			{
				// JMW 7/13/2009
				// if the imageId is the same as the study Id, this indicates the user is requesting the 
				// first image in a single image study where the RPC gave the imageId as the studyId
				// in this case we just want to return the 'FirstImage' in the study.
				// In most cases the imageId requested won't be the study Id but if the site does
				// not have Patch 83 this will happen on single image groups and this allows the
				// cache to be used.
				// If the studyId is the imageId, it won't find the image in the study
				if(imageUrn.getImageId().equals(imageUrn.getStudyId()))
				{
					return study.getFirstImage();
				}				
				List<Image> images = extractImagesFromStudy(study);	
				int i = 0;
				boolean found = false;
				while((!found) && (i < images.size()))
				{
					Image img = images.get(i);
					if(img.getIen().equals(imageUrn.getImageId()))
					{
						image = img;
						found = true;
					}
					i++;
				}
			}
		}
		catch(URNFormatException iurnfX)
		{
			getLogger().error(iurnfX);
		}
		
	    return image;
    }
}
