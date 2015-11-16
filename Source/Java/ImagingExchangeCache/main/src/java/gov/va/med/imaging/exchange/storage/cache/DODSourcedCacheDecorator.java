package gov.va.med.imaging.exchange.storage.cache;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.business.documents.Document;
import gov.va.med.imaging.storage.cache.Cache;
import gov.va.med.imaging.storage.cache.Instance;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import org.apache.log4j.Logger;

/**
 * This class translates DOD semantics (opaque identifiers) into cache semantics (groups and instance keys).
 * 
 */
public class DODSourcedCacheDecorator 
extends AbstractCacheDecorator
implements DODSourcedCache
{
	private Logger logger = Logger.getLogger(this.getClass());
	private gov.va.med.imaging.storage.cache.Cache wrappedCache;
	private String metadataRegionName = null;
	private String imageRegionName = null;
	
	public DODSourcedCacheDecorator(
		Cache wrappedCache,
		String metadataRegionName, 
		String imageRegionName)
	{
		if(wrappedCache == null)
			throw new IllegalArgumentException(getClass().getSimpleName() + " was passed a null wrapped cache parameter.");
		if(metadataRegionName == null)
			throw new IllegalArgumentException(getClass().getSimpleName() + " was passed a null metadata region name parameter.");
		if(imageRegionName == null)
			throw new IllegalArgumentException(getClass().getSimpleName() + " was passed a null image region name parameter.");
		
		this.wrappedCache = wrappedCache;
		this.metadataRegionName = metadataRegionName;
		this.imageRegionName = imageRegionName;
		
		logger.info("DODSourcedCacheDecorator <ctor> backed with cache '" + wrappedCache.getName() + "'.");
	}
	
	@Override
	protected Cache getWrappedCache()
	{
		return wrappedCache;
	}
	
	@Override
	protected String getImageRegionName()
	{
		return this.imageRegionName;
	}

	@Override
	protected String getMetadataRegionName()
	{
		return this.metadataRegionName;
	}

	/**
	 * 
	 */
	@Override
	public void createStudy(Study study) 
	throws CacheException
	{
		GlobalArtifactIdentifier gaid = study.getGlobalArtifactIdentifier();
		String[] groupKeys = AbstractCacheDecorator.createExternalInstanceGroupKeys(gaid, true);
		String instanceKey = AbstractCacheDecorator.createStudyKey(gaid);
		
		createStudy(groupKeys, instanceKey, study);
	}

	/**
	 * 
	 */
	@Override
	public Study getStudy(GlobalArtifactIdentifier gaid) 
	throws CacheException
	{
		String[] groupKeys = AbstractCacheDecorator.createExternalInstanceGroupKeys(gaid, true);
		String instanceKey = AbstractCacheDecorator.createStudyKey(gaid);
		
		return getStudy(groupKeys, instanceKey);
	}
	
	// =======================================================================================
	// Document Caching
	// =======================================================================================
	@Override
	public void createDocumentMetadata( GlobalArtifactIdentifier gaid, Document document) 
	throws CacheException
	{
		String[] groupKeys = AbstractCacheDecorator.createExternalInstanceGroupKeys(gaid, true);
		String instanceKey = AbstractCacheDecorator.createDocumentMetadataKey(gaid);
		
		createDocumentMetadata(groupKeys, instanceKey, document);
	}
	
	@Override
	public Document getDocumentMetadata( GlobalArtifactIdentifier gaid ) 
	throws CacheException
	{
		String[] groupKeys = AbstractCacheDecorator.createExternalInstanceGroupKeys(gaid, true);
		String instanceKey = AbstractCacheDecorator.createDocumentMetadataKey(gaid);
		
		return getDocumentMetadata(groupKeys, instanceKey);
	}
	
	@Override
	public ImmutableInstance createDocumentContent(GlobalArtifactIdentifier gaid) 
	throws CacheException
	{
		String[] groupKeys = AbstractCacheDecorator.createExternalInstanceGroupKeys(gaid, false);
		String instanceKey = AbstractCacheDecorator.createDocumentKey(gaid);
		
		Instance imageContent = createImage(groupKeys, instanceKey);
		return imageContent == null ? null : new ImmutableInstance(imageContent);
	}

	@Override
	public ImmutableInstance getDocumentContent(GlobalArtifactIdentifier gaid) 
	throws CacheException
	{
		String[] groupKeys = AbstractCacheDecorator.createExternalInstanceGroupKeys(gaid, false);
		String instanceKey = AbstractCacheDecorator.createDocumentKey(gaid);
		
		Instance imageContent = getImage(groupKeys, instanceKey);
		return imageContent == null ? null : new ImmutableInstance(imageContent);
	}

	@Override
	public ImmutableInstance createImage(
		GlobalArtifactIdentifier gaid,
		String quality, 
		String mimeType) 
	throws CacheException
	{
		String[] groups = AbstractCacheDecorator.createExternalInstanceGroupKeys(gaid);
		String instanceKey = AbstractCacheDecorator.createExternalImageKey(gaid, quality, mimeType);
		
		return new ImmutableInstance( createImage(groups, instanceKey) );
	}

	@Override
	public ImmutableInstance getImage(
		GlobalArtifactIdentifier gaid,
		String quality, 
		String mimeType) 
	throws CacheException
	{
		String[] groups = AbstractCacheDecorator.createExternalInstanceGroupKeys(gaid);
		String instanceKey = AbstractCacheDecorator.createExternalImageKey(gaid, quality, mimeType);
		
		Instance instance = getImage(groups, instanceKey);
		if(instance == null)
			return null;
		return new ImmutableInstance(instance);
	}

}
