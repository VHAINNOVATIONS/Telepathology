/**
 * 
 */
package gov.va.med.imaging.exchange.storage.cache;

import java.io.Serializable;
import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.business.documents.Document;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;

/**
 * The interface to get/retrieve cache objects.
 * The cache is "realized" because it is mapped to one or more Cache
 * regions that have associated storage and eviction rules.
 * 
 * @author vhaiswbeckec
 *
 */
public interface RealizedCache
{
	/**
	 * This method will use the Study's global artifact identifier to
	 * build the cache groups.
	 * 
	 * @param study
	 * @throws CacheException
	 */
	public void createStudy(Study study) 
	throws CacheException;
	
	/**
	 * Find the Study instance in the cache, using the global artifact identifier (the
	 * study URN).
	 * 
	 * @param gai
	 * @return
	 * @throws CacheException
	 */
	public Study getStudy(GlobalArtifactIdentifier gai) 
	throws CacheException;
	
	/**
	 * @param homeCommunityId
	 * @param siteNumber
	 * @param documentUrn
	 * @param document
	 */
	public void createDocumentMetadata(GlobalArtifactIdentifier gaid, Document document)
	throws CacheException;

	/**
	 * @param homeCommunityId
	 * @param siteNumber
	 * @param documentId
	 * @return
	 * @throws CacheException
	 */
	public Document getDocumentMetadata(GlobalArtifactIdentifier gaid) 
	throws CacheException;

	/**
	 * @param homeCommunityId
	 * @param siteNumber
	 * @param documentId
	 * @return
	 * @throws CacheException
	 */
	public ImmutableInstance getDocumentContent(GlobalArtifactIdentifier gaid)
	throws CacheException;

	/**
	 * @param homeCommunityId
	 * @param siteNumber
	 * @param documentId
	 * @param inStream
	 * @throws CacheException
	 */
	public ImmutableInstance createDocumentContent( GlobalArtifactIdentifier gaid )
	throws CacheException;

	/**
	 * 
	 * @param <T>
	 * @param gai
	 * @param object
	 * @throws CacheException
	 */
	public <T extends Serializable> void create(GlobalArtifactIdentifier gai, T object) 
	throws CacheException;

	/**
	 * 
	 * @param <T>
	 * @param expectedClass
	 * @param gai
	 * @return
	 * @throws CacheException
	 */
	public <T extends Serializable> T get(Class<T> expectedClass, GlobalArtifactIdentifier gai) 
	throws CacheException;
}
