/**
 * 
 */
package gov.va.med.imaging.storage.cache;

import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import gov.va.med.imaging.storage.cache.impl.CacheFactory;

import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.TestCase;

/**
 * @author VHAISWBECKEC
 *
 */
public abstract class AbstractCacheFactoryTest 
extends TestCase
{
	protected abstract URI getCacheUri() 
	throws URISyntaxException;
	
	protected abstract String getPrototypeName();

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() 
	throws Exception
	{
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() 
	throws Exception
	{
		super.tearDown();
	}

	/**
	 * Test method for {@link gov.va.med.imaging.storage.cache.impl.memory.MemoryCache#create(java.lang.String, java.net.URI, gov.va.med.imaging.storage.cache.EvictionTimer)}.
	 * @throws URISyntaxException 
	 * @throws CacheException 
	 */
	public void testSimpleCreate() 
	throws URISyntaxException, CacheException
	{
		CacheFactory cacheFactory = CacheFactory.getSingleton();
		Cache cache = null;
		
		cache = cacheFactory.createCache(this.getName() + "-SimpleCreate", getCacheUri(), (String)null);
		
		assertNotNull(cache);
	}
}
