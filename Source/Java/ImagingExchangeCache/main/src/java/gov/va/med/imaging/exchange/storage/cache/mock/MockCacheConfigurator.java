package gov.va.med.imaging.exchange.storage.cache.mock;

import gov.va.med.imaging.storage.cache.exceptions.CacheException;

/**
 * 
 * @author VHAISWBECKEC
 *
 */
public class MockCacheConfigurator
{
	private static CacheException createImageCacheException;
	private static CacheException createStudyCacheException;
	private static CacheException getImageCacheException;
	private static CacheException getStudyCacheException;
	
	public static void clearAll()
	{
		setCreateImageCacheException(null);
		setCreateStudyCacheException(null);
		setGetImageCacheException(null);
		setGetStudyCacheException(null);
	}
	
	static CacheException getCreateImageCacheException()
    {
    	return createImageCacheException;
    }

	public static void setCreateImageCacheException(CacheException createImageCacheException)
    {
    	MockCacheConfigurator.createImageCacheException = createImageCacheException;
    }

	static CacheException getCreateStudyCacheException()
    {
    	return createStudyCacheException;
    }

	public static void setCreateStudyCacheException(CacheException createStudyCacheException)
    {
    	MockCacheConfigurator.createStudyCacheException = createStudyCacheException;
    }

	static CacheException getGetImageCacheException()
    {
    	return getImageCacheException;
    }

	public static void setGetImageCacheException(CacheException getImageCacheException)
    {
    	MockCacheConfigurator.getImageCacheException = getImageCacheException;
    }

	static CacheException getGetStudyCacheException()
    {
    	return getStudyCacheException;
    }

	public static void setGetStudyCacheException(CacheException getStudyCacheException)
    {
    	MockCacheConfigurator.getStudyCacheException = getStudyCacheException;
    }
}
