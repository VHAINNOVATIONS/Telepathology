package gov.va.med.imaging.exchange.storage.cache;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;

/**
 * 
 * 
 */
public interface DODSourcedCache
extends RealizedCache
{
	/**
	 * 
	 * @param gaid
	 * @param quality
	 * @param mimeType
	 * @return
	 * @throws CacheException
	 */
	public ImmutableInstance createImage(
		GlobalArtifactIdentifier gaid, 
		String quality, 
		String mimeType)
	throws CacheException;

	/**
	 * 
	 * @param gaid
	 * @param quality
	 * @param mimeType
	 * @return
	 * @throws CacheException
	 */
	public ImmutableInstance getImage(
		GlobalArtifactIdentifier gaid,
		String quality, 
		String mimeType)
	throws CacheException;
	

}
