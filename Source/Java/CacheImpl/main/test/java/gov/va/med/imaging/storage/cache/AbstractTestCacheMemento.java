/**
 * 
 */
package gov.va.med.imaging.storage.cache;

import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import gov.va.med.imaging.storage.cache.impl.CacheManagerImpl;
import gov.va.med.imaging.storage.cache.impl.eviction.LastAccessedEvictionStrategy;
import gov.va.med.imaging.storage.cache.impl.eviction.LastAccessedEvictionStrategyMemento;
import gov.va.med.imaging.storage.cache.impl.eviction.StorageThresholdEvictionStrategy;
import gov.va.med.imaging.storage.cache.impl.eviction.StorageThresholdEvictionStrategyMemento;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.RuntimeOperationsException;

import junit.framework.TestCase;

/**
 * @author VHAISWBECKEC
 *
 */
public abstract class AbstractTestCacheMemento 
extends TestCase
{
	private CacheManagerImpl cacheManager;
	
	protected void setUp() throws Exception
	{
		super.setUp();
		cacheManager = CacheManagerImpl.getSingleton();
	}
	
	protected CacheManagerImpl getCacheManager()
	{
		return cacheManager;
	}
	
	protected abstract URI getCacheUri() throws URISyntaxException;
	
	protected abstract void validateCacheRealizationClass(Cache cache);
	
	protected abstract String getCacheName();	
	
	public void testMemento() 
	throws MBeanException, CacheException, RuntimeOperationsException, InstanceNotFoundException
	{
		Cache cache = null;
		try
		{
			cache = getCacheManager().createCache(getCacheName(), getCacheUri(), (String)null );
		} 
		catch (URISyntaxException x)
		{
			x.printStackTrace();
			fail(x.getMessage());
		} 
		catch (IOException x)
		{
			x.printStackTrace();
			fail(x.getMessage());
		}
		validateCacheRealizationClass(cache);

		LastAccessedEvictionStrategyMemento memento0 = new LastAccessedEvictionStrategyMemento();
		memento0.setName("lastAccessedEvictionStrategy");
		memento0.setMaximumTimeSinceLastAccess(10000L);
		memento0.setInitialized(true);
		getCacheManager().createEvictionStrategy( cache, memento0 );

		StorageThresholdEvictionStrategyMemento memento3 = new StorageThresholdEvictionStrategyMemento(); 
		memento3.setName("freeSpaceEvictionStrategy");
		memento3.setMinFreeSpaceThreshold(100000000L);
		memento3.setTargetFreeSpaceThreshold(1000000000L);
		memento3.setDelay(1000L);
		memento3.setInterval(10000L);
		memento3.setInitialized(true);
		getCacheManager().createEvictionStrategy( cache, memento3 );

		getCacheManager().createRegion( cache, "region0", new String[]{"lastAccessedEvictionStrategy"} );
		getCacheManager().createRegion( cache, "region1", new String[]{"lastAccessedEvictionStrategy"} );
		getCacheManager().createRegion( cache, "region2", new String[]{"freeSpaceEvictionStrategy"} );
		getCacheManager().createRegion( cache, "region3", new String[]{"freeSpaceEvictionStrategy"} );
		
		try
		{
			getCacheManager().store(cache);
		} 
		catch (IOException x)
		{
			x.printStackTrace();
			fail(x.getMessage());
		}
		
		Cache resurrectedCache = null;
		try
		{
			resurrectedCache = 	getCacheManager().getCache(getCacheName());
		} 
		catch (FileNotFoundException x)
		{
			x.printStackTrace();
			fail(x.getMessage());
		} 
		catch (IOException x)
		{
			x.printStackTrace();
			fail(x.getMessage());
		}
		
		assertEquals(getCacheName(), resurrectedCache.getName());
		assertEquals(2, resurrectedCache.getEvictionStrategies().size());
		assertEquals(4, resurrectedCache.getRegions().size());

		assertNotNull( resurrectedCache.getEvictionStrategy("lastAccessedEvictionStrategy") );
		assertNotNull( resurrectedCache.getEvictionStrategy("freeSpaceEvictionStrategy") );

		Region region = null;
		EvictionStrategy[] evictionStrategies = null;
		
		region = resurrectedCache.getRegion("region0");
		assertNotNull( region );
		evictionStrategies = region.getEvictionStrategies();
		assertNotNull( evictionStrategies );
		assertEquals(1, evictionStrategies.length);
		assertTrue(evictionStrategies[0] instanceof LastAccessedEvictionStrategy);
		assertEquals( "lastAccessedEvictionStrategy", evictionStrategies[0].getName() );
		
		region = resurrectedCache.getRegion("region1");
		evictionStrategies = region.getEvictionStrategies();
		assertNotNull( region );
		assertNotNull( evictionStrategies );
		assertEquals(1, evictionStrategies.length);
		assertTrue(evictionStrategies[0] instanceof LastAccessedEvictionStrategy);
		assertEquals( "lastAccessedEvictionStrategy", evictionStrategies[0].getName() );
		
		region = resurrectedCache.getRegion("region2");
		evictionStrategies = region.getEvictionStrategies();
		assertNotNull( region );
		assertNotNull( evictionStrategies );
		assertEquals(1, evictionStrategies.length);
		assertTrue(evictionStrategies[0] instanceof StorageThresholdEvictionStrategy);
		assertEquals( "freeSpaceEvictionStrategy", evictionStrategies[0].getName() );
		
		region = resurrectedCache.getRegion("region3");
		evictionStrategies = region.getEvictionStrategies();
		assertNotNull( region );
		assertNotNull( evictionStrategies );
		assertEquals(1, evictionStrategies.length);
		assertTrue(evictionStrategies[0] instanceof StorageThresholdEvictionStrategy);
		assertEquals( "freeSpaceEvictionStrategy", evictionStrategies[0].getName() );

		getCacheManager().delete(cache);
	}

}
