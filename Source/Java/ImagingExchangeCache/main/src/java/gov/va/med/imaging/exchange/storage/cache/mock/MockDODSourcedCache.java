package gov.va.med.imaging.exchange.storage.cache.mock;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.imaging.exchange.storage.cache.DODSourcedCache;
import gov.va.med.imaging.exchange.storage.cache.ImmutableInstance;
import gov.va.med.imaging.storage.cache.Instance;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;

/**
 * 
 * @author VHAISWBECKEC
 *
 */
public class MockDODSourcedCache
extends AbstractMockCacheDecorator
implements DODSourcedCache
{
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.storage.cache.DODSourcedCache#createImage(gov.va.med.GlobalArtifactIdentifier, java.lang.String, java.lang.String)
	 */
	@Override
	public ImmutableInstance createImage(GlobalArtifactIdentifier gaid, String quality, String mimeType)
		throws CacheException
	{
		if( MockCacheConfigurator.getCreateImageCacheException() != null)
			throw MockCacheConfigurator.getCreateImageCacheException();

		Instance instance = new MockInstance();
		ImmutableInstance immutableInstance = new ImmutableInstance(instance);
		return immutableInstance;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.storage.cache.DODSourcedCache#getImage(gov.va.med.GlobalArtifactIdentifier, java.lang.String, java.lang.String)
	 */
	@Override
	public ImmutableInstance getImage(GlobalArtifactIdentifier gaid, String quality, String mimeType)
		throws CacheException
	{
		if( MockCacheConfigurator.getGetImageCacheException() != null)
			throw MockCacheConfigurator.getGetImageCacheException();
		
		Instance instance = new MockInstance();
		ImmutableInstance immutableInstance = new ImmutableInstance(instance);
		return immutableInstance;
	}

}
