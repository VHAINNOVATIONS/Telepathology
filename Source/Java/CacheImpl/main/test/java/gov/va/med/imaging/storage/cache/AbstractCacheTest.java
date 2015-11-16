package gov.va.med.imaging.storage.cache;

import gov.va.med.GenericDataGenerator;
import gov.va.med.imaging.storage.cache.impl.CacheManagerImpl;
import gov.va.med.server.ServerLifecycleEvent;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.TestCase;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public abstract class AbstractCacheTest 
extends TestCase
{
	private static final int testDataLength = 3967;
	private static byte[] sampleData = null;
	private static GenericDataGenerator dataGenerator = new GenericDataGenerator();
	
	/**
	 * 
	 */
	public static void initializeLogging(String name)
	{
		initializeLogging(name, Level.DEBUG);
	}
	
	public static void initializeLogging(String name, Level level)
	{
		Appender appender = null;
		
		Logger.getRootLogger().setLevel(level);
		Layout layout = new org.apache.log4j.PatternLayout("%d{DATE} %5p [%t] (%F:%L) - %m%n");
		try
		{
			appender = new org.apache.log4j.FileAppender(layout, name + ".junit.result", false);
		} 
		catch (IOException x)
		{
			appender = new org.apache.log4j.ConsoleAppender(layout);
		}
		Logger.getRootLogger().addAppender(appender);

		Appender consoleAppender = new org.apache.log4j.ConsoleAppender(layout);
		Logger.getRootLogger().addAppender(consoleAppender);
		
		// turn down clustering messages
		//Logger.getLogger("gov.va.med.imaging.cluster").setLevel(Level.WARN);
	}

	/**
	 * Get a reference to a singleton of sample data.
	 * This data remains constant (unless something else mucks with it)
	 * for the duration of a test.
	 * 
	 * @return
	 */
	public static synchronized byte[] getSampleData() {
		if(sampleData == null)
			sampleData = dataGenerator.createRandomByteArray(testDataLength);
		return sampleData;
	}

	/**
	 * Create some random sample data of the requested size.
	 * This data will differ each time it is called, unlike getSampleData().
	 * @return
	 */
	public static byte[] createSampleData(int sampleDataLength) {
		return dataGenerator.createRandomByteArray(sampleDataLength);
	}

	public static CacheItemPath createRandomCacheItemPath(CacheItemPath rootPath, int groupDepth)
	{
		CacheItemPath path = rootPath;
		for(int groupIndex = 0; groupIndex < groupDepth; ++groupIndex )
			path = path.createChildPath(dataGenerator.createRandomString("[a-z]{1,32}"), false);
		path = path.createChildInstancePath(dataGenerator.createRandomString("[a-z]{1,32}"));
		
		return path;
	}
	
	// ==============================================================================================================
	// 
	// ==============================================================================================================
	
	private Cache cache = null;
	private CacheManagerImpl cacheManager = null;
	private String localhostName;

	// by default, create a cache with the same name as the unit test
	protected String getCacheName()
	{
		return this.getName();
	}
	
	protected abstract URI getCacheUri()
	throws URISyntaxException;
	
	protected abstract String getPrototypeName();
	
	protected void setUp() 
	throws Exception
	{
		super.setUp();
		
		initializeLogging(this.getName(), Level.WARN);
		Logger.getRootLogger().info("Starting unit test '" + this.getName() + "' =======================================================");
		
//		jcifs.Config.setProperty( "jcifs.util.loglevel", "4" );
//		jcifs.Config.setProperty( "jcifs.resolveOrder", "DNS" );
//		
		InetAddress localhostAddress = InetAddress.getLocalHost();
		localhostName = localhostAddress.getHostName();
		
		cacheManager = CacheManagerImpl.getSingleton();
		cache = cacheManager.createCache(getCacheName(), getCacheUri(), getPrototypeName());
		
		cache.registerCacheStructureChangeListener(new LoggingCacheStructureChangeListener());
		
		cache.setInitialized(true);
		cache.setEnabled(true);
		cacheManager.serverLifecycleEvent(new ServerLifecycleEvent(ServerLifecycleEvent.EventType.START));
	}

	public String getLocalhostName()
	{
		return this.localhostName;
	}

	protected void tearDown() throws Exception
	{
		cacheManager.disable(cache);
		cache.cacheLifecycleEvent(CacheLifecycleEvent.STOP);
		cacheManager.delete(cache);

		Logger.getRootLogger().info("Stopping unit test '" + this.getName() + "' =======================================================");
		super.tearDown();
	}

	protected Cache getCache()
	{
		return cache;
	}
	
	
	class LoggingCacheStructureChangeListener
	implements CacheStructureChangeListener
	{
		private Logger logger = Logger.getLogger(LoggingCacheStructureChangeListener.class);
		
		@Override
		public void cacheStructureChanged(Cache cache) 
		{
			logger.info(cache.getName() + " cacheStructureChange()");
		}

		@Override
		public void regionAdded(Cache cache, Region newRegion) 
		{
			logger.info(cache.getName() + " regionAdded()" + newRegion.getName());
		}

		@Override
		public void regionRemoved(Cache cache, Region oldRegion) 
		{
			logger.info(cache.getName() + " regionRemoved()" + oldRegion.getName());
		}

		@Override
		public void evictionStrategyAdded(Cache cache, EvictionStrategy newEvictionStrategy) 
		{
			logger.info(cache.getName() + " evictionStrategyAdded()" + newEvictionStrategy.getName());
		}

		@Override
		public void evictionStrategyRemoved(Cache cache, EvictionStrategy oldEvictionStrategy) 
		{
			logger.info(cache.getName() + " evictionStrategyRemoved()" + oldEvictionStrategy.getName());
		}
		
	}
	
	/**
	 * Create a random string that is usable as a group or instance name
	 * @return
	 */
	private final static char[] ALLOWABLE_CHARS = new char[]
	{'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
	 'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
	 '1','2','3','4','5','6','7','8','9','0'};
	
	protected String makeRandomName()
	{
		int allowableCharsLength = ALLOWABLE_CHARS.length;
		
		int length = (int)(Math.random() * 20.0) + 1;
		StringBuilder sb = new StringBuilder();
		for(int index=0; index < length; ++index)
			sb.append(ALLOWABLE_CHARS[(int)(Math.random() * (double)allowableCharsLength)]);
		return sb.toString();
	}

}
