/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl.eviction;

import gov.va.med.imaging.GUID;
import gov.va.med.imaging.storage.cache.AbstractCacheTest;
import gov.va.med.imaging.storage.cache.Cache;
import gov.va.med.imaging.storage.cache.EvictionStrategy;
import gov.va.med.imaging.storage.cache.Instance;
import gov.va.med.imaging.storage.cache.InstanceWritableByteChannel;
import gov.va.med.imaging.storage.cache.Region;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import gov.va.med.imaging.storage.cache.impl.CacheManagerImpl;
import gov.va.med.imaging.storage.cache.impl.filesystem.FileSystemCache;
import gov.va.med.server.ServerLifecycleEvent;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

/**
 * @author VHAISWBECKEC
 *
 * This test creates an in-memory cache with a single region and an eviction strategy
 * that deletes groups when free space is too low.
 */
public class TestLimitedSpaceStorageThresholdEviction 
extends TestCase
{
	private static final long KILO = 1024;
	private static final long MEGA = KILO * KILO;
	private static final long GIGA = MEGA * KILO;
	private static final long TERA = GIGA * KILO;
	
	private static final String REGION_NAME = "very-small";
	private static final String EVICTION_STRATEGY_NAME = "20K-limit";

	private static final int instanceDataLength = 1024;
	
	private Cache cache = null;

	private static byte[] sampleData = null;
	private Logger logger = Logger.getLogger(this.getClass());

	// create some list of bytes we can use for data
	// the data should be repeatable so tests are consistent
	static
	{
		sampleData = new byte[instanceDataLength];
		for(int index=0; index<instanceDataLength; ++index)
			sampleData[index] = (byte)(index % 256);
	}
	
	protected static byte[] getSampleData()
	{
		return sampleData;
	}
	
	protected URI getCacheUri() 
	throws URISyntaxException
	{
		return new URI( FileSystemCache.protocol + "://" + "vixtests/" + this.getName() );
	}

	/**
	 * @return the cache
	 */
	public Cache getCache()
	{
		return this.cache;
	}

	private void setCache(Cache cache)
	{
		this.cache = cache;
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() 
	throws Exception
	{
		AbstractCacheTest.initializeLogging(this.getName());
		Logger.getRootLogger().info("Starting unit test '" + this.getName() + "' =======================================================");
		
		CacheManagerImpl cacheManager = CacheManagerImpl.getSingleton();
		Cache cache = cacheManager.createCache(this.getName(), getCacheUri(), (String)null);

		EvictionStrategy evictionStrategy = StorageThresholdEvictionStrategy.create(
				EVICTION_STRATEGY_NAME, true, cache.getEvictionTimer(), 
				KILO, 5 * KILO, 20 * KILO, 
				1000L, 5000L);
		cache.addEvictionStrategy(evictionStrategy);
		Region region = cache.createRegion(REGION_NAME, new String[]{EVICTION_STRATEGY_NAME});
		cache.addRegion(region);
		
		setCache( cache );
		
		getCache().setInitialized(true);
		getCache().setEnabled(true);
		cacheManager.serverLifecycleEvent(new ServerLifecycleEvent(ServerLifecycleEvent.EventType.START));
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		CacheManagerImpl cacheManager = CacheManagerImpl.getSingleton();
		
		cacheManager.serverLifecycleEvent(new ServerLifecycleEvent(ServerLifecycleEvent.EventType.STOP));
		
		super.tearDown();
	}

	/**
	 * Simply test that the cache regions's freespace property is functional.
	 * @throws CacheException 
	 * @throws IOException 
	 */
	public void testCacheFreespace() 
	throws CacheException, IOException
	{
		int iterations = 64;
		String[] imageIds = new String[iterations];
		
		for(int index=0; index < iterations; ++index)
		{
			imageIds[index] = (new GUID()).toShortString();
			createAndPopulateInstance( imageIds[index] );
			
			System.out.println( "Wrote instance [" + imageIds[index] + "]." );
			try{Thread.sleep(1000L);}catch(InterruptedException iX){}		// give the eviction thread a chance to keep up
		}
		
	}

	/**
	 * Seems odd but there was a problem if the eviction strategy had nothing to do
	 * so this test just makes it run a couple of times with nothing to do.
	 * 
	 * @throws CacheException
	 * @throws IOException
	 */
	public void testCacheFreespaceNoActivity() 
	throws CacheException, IOException
	{
		int iterations = 64;
		
		for(int index=0; index < iterations; ++index)
		{
			System.out.println( "Did nothing, available region free space is " + getCache().getRegion(REGION_NAME).getFreeSpace() + " bytes." );
			try{Thread.sleep(1000L);}catch(InterruptedException iX){}		// give the eviction thread a chance to keep up
		}
		
	}
	
	private void createAndPopulateInstance(String instanceId) 
	throws CacheException, IOException
	{
		String[] path = new String[]{instanceId};
		Instance instance = getCache().getOrCreateInstance(REGION_NAME, path, instanceId);
		
		InstanceWritableByteChannel writeChannel = instance.getWritableChannel();
		
		java.nio.ByteBuffer src = ByteBuffer.wrap( getSampleData() );
		
		writeChannel.write(src);

		writeChannel.close();
	}
}
