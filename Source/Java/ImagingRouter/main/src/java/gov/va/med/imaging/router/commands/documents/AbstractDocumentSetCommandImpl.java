/**
 * 
 */
package gov.va.med.imaging.router.commands.documents;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.DocumentSetURN;
import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.StudyURN;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.business.DocumentFilter;
import gov.va.med.imaging.exchange.business.Image;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.business.Series;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.business.documents.Document;
import gov.va.med.imaging.exchange.business.documents.DocumentSet;
import gov.va.med.imaging.exchange.business.documents.DocumentSetResult;
import gov.va.med.imaging.exchange.business.util.ExchangeUtil;
import gov.va.med.imaging.router.commands.AbstractImagingCommandImpl;
import gov.va.med.imaging.router.facade.ImagingContext;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

/**
 * An abstract superclass of DocumentSet-related commands, grouped because there is significant
 * overlap in the commands that are contained here.
 * 
 * @author vhaiswbeckec
 *
 */
public abstract class AbstractDocumentSetCommandImpl<R extends Object> 
extends AbstractImagingCommandImpl<R>
{
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(this.getClass());

	/**
	 * @param commandContext - the context available to the command
	 */
	public AbstractDocumentSetCommandImpl()
	{
		super();
	}

	/**
	 * 
	 * @param siteNumber
	 * @param patientId
	 * @param filter
	 * @return
	 * @throws MethodException
	 */
	protected Logger getLogger()
	{
		return logger;
	}
	
	protected DocumentSetResult getPatientDocumentSetResult(RoutingToken routingToken, DocumentFilter filter)
	throws MethodException
	{
		if(filter != null && !filter.isSiteAllowed(routingToken.getRepositoryUniqueId()))
		{
			getLogger().info("Site number [" + routingToken.getRepositoryUniqueId() + "] is excluded in the StudyFilter, not loading study list from this site");
			return DocumentSetResult.createFullResult(new TreeSet<DocumentSet>());			
		}
		
		filter.setSiteNumber(routingToken.getRepositoryUniqueId());
		try
		{
			DocumentSetResult documentSetResult = ImagingContext.getRouter().getPatientDocumentSets(routingToken, 
					filter.getPatientId(), filter);
			getLogger().info("Got " + (documentSetResult == null ? "null" : documentSetResult.getArtifactSize()) + " patient '" + filter.getPatientId() + "' document groups.");
			if(documentSetResult != null)
			{
				// update for consolidated site support
				documentSetResult = CommonDocumentFunctions.updateConsolidatedSitesInDocumentSetResult(documentSetResult, getCommandContext());
			}
			Set<DocumentSet> documentSets = documentSetResult == null ? null : documentSetResult.getArtifacts();
			
			// a null Study Set indicates no studies meet the search criteria
			if(documentSets != null)
			{
				//cacheDocumentSetList(resolvedSite, documentSets);
				//TODO: cache document sets				
			}
			return documentSetResult;
		}
		catch(ConnectionException cX)
		{
			throw new MethodException(cX);
		}
	}
	
	/**
	 * 
	 * @param siteNumber
	 * @param patientId
	 * @param filter
	 * @return
	 * @throws MethodException
	 */
	protected List<DocumentSet> getPatientDocumentSetList(RoutingToken routingToken, DocumentFilter filter)
	throws MethodException
	{
		DocumentSetResult documentSetResult = getPatientDocumentSetResult(routingToken, filter);
		Set<DocumentSet> documentSets = documentSetResult == null ? null : documentSetResult.getArtifacts();
		if(documentSets != null)
		{
			// do not cache the results here, done above already
			return new ArrayList<DocumentSet>(documentSets);
		}
		else
			return new ArrayList<DocumentSet>(0);	
	}

	/**
	 * @param resolvedSite
	 * @param documentSetList
	 */
	private void cacheDocumentSetList(ResolvedSite resolvedSite, Collection<DocumentSet> documentSetList)
	{
		if(getCommandContext().isCachingEnabled()) 
		{
			getLogger().info("Caching [" + documentSetList.size() + "] document sets.");
			for(DocumentSet documentSet : documentSetList) 
			{
				getLogger().info("Caching [" + documentSet.size() + "] documents.");
				for(Document document : documentSet)
				{
					GlobalArtifactIdentifier documentId = document.getGlobalArtifactIdentifier();
					try
					{
						if(ExchangeUtil.isSiteDOD(resolvedSite.getSite()))
						{
							getCommandContext().getExtraEnterpriseCache().createDocumentMetadata(
								documentId, 
								document
							);
						}
						else
						{
							getCommandContext().getIntraEnterpriseCacheCache().createDocumentMetadata(
								documentId, 
								document
							);
						}
					}
					catch (CacheException x)
					{
						logger.error("Error caching document metadata.", x);
					}
					//catch (URNFormatException x)
					//{
					//	logger.error("Error caching document metadata.", x);
					//}
				}
			}
		}
	}

	/**
	 * 
	 * @param studyUrn
	 * @return
	 * @throws MethodException
	 */
	protected DocumentSet getPatientDocumentSet(DocumentSetURN documentSetUrn)
	throws MethodException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		String groupId = documentSetUrn.getGroupId();
		String siteNumber = documentSetUrn.getOriginatingSiteId();
		String patientIcn = documentSetUrn.getPatientId();

		DocumentSet documentSet = null;
		try
		{
			documentSet = getDocumentSetFromCache(documentSetUrn);
			if(documentSet == null)
			{
				DocumentFilter filter = new DocumentFilter(groupId);
				List<DocumentSet> documentSets = getPatientDocumentSetList(documentSetUrn, filter);
	
				documentSet = documentSets.get(0);
				getLogger().info("Found document set [" + documentSetUrn.toString() + "] from data source");
				transactionContext.setItemCached(Boolean.FALSE);
			}
			else
			{
				getLogger().info("Found document set [" + documentSetUrn.toString() + "] in cache");
				transactionContext.setServicedSource(documentSetUrn.toRoutingTokenString());
				transactionContext.setItemCached(Boolean.TRUE);
			}
		}
		catch(MethodException mX)
		{
			throw mX;
		}
		
		//return extractImagesFromStudy(study);
		return documentSet;
	}
	
	/**
	 * @param documentSetUrn
	 * @return
	 */
	private DocumentSet getDocumentSetFromCache(DocumentSetURN documentSetUrn)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Cache a list of Study instances into the appropriate cache.
	 * 
	 * @param resolvedSite
	 * @param studyList
	 */
	public void cacheStudyList(ResolvedSite resolvedSite, List<Study> studyList)
	{
		if(getCommandContext().isCachingEnabled()) 
		{
			getLogger().info("Caching [" + studyList.size() + "] studies.");
			for(int i = 0; i < studyList.size(); i++) 
			{
				Study study = studyList.get(i);					
				try 
				{
					if(ExchangeUtil.isSiteDOD(resolvedSite.getSite()))
					{
						getCommandContext().getExtraEnterpriseCache().createStudy(study);
					}
					else
					{
						StudyURN studyUrn = study.getStudyUrn();// StudyURN.create(study.getSiteNumber(), study.getStudyIen(), study.getPatientIcn());
						getCommandContext().getIntraEnterpriseCacheCache().createStudy(study);
					}
				}
				catch(CacheException cX) 
				{
					getLogger().warn(cX);
				}
				/*
				catch(URNFormatException iurnfX)
				{
					getLogger().warn(iurnfX);
				}*/
			}
		}
	}

	/**
	 * 
	 * @param studyUrn
	 * @return
	 */
	protected Study getStudyFromCache(StudyURN studyUrn)
	{
		Study study = null;
		if(getCommandContext().isCachingEnabled()) {
			try {
				study = 
					studyUrn.isOriginVA() ? 
					getCommandContext().getExtraEnterpriseCache().getStudy(studyUrn) :
					getCommandContext().getIntraEnterpriseCacheCache().getStudy(studyUrn);
			}
			catch(CacheException cX) {
				getLogger().warn("Unable to get study from cache", cX);
			}
		}
		return study;
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
			for(Image image : ser)
				images.add(image);
		
		return images;
	}
	
	/**
     * @param imageUrn
     * @return
     */
    protected Image findImageInCachedStudyGraph(ImageURN imageUrn)
    {
	    Image image = null;
		try 
		{
			StudyURN studyUrn = imageUrn.getParentStudyURN();
			
			Study study = getStudyFromCache(studyUrn);
			if(study != null)
			{
				List<Image> images = extractImagesFromStudy(study);	
				int i = 0;
				boolean found = false;
				while((!found) && (i < images.size()))
				{
					Image img = images.get(i);
					if(img.getIen().equals(imageUrn.getInstanceId()))
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
