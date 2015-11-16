/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl;

import gov.va.med.imaging.storage.cache.Cache;
import gov.va.med.imaging.storage.cache.EvictionStrategy;
import gov.va.med.imaging.storage.cache.EvictionTimer;
import gov.va.med.imaging.storage.cache.Region;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import gov.va.med.imaging.storage.cache.exceptions.CacheInitializationException;
import gov.va.med.imaging.storage.cache.exceptions.InitializationException;
import gov.va.med.imaging.storage.cache.exceptions.InvalidSweepSpecification;
import gov.va.med.imaging.storage.cache.impl.eviction.EvictionStrategyFactory;
import gov.va.med.imaging.storage.cache.memento.*;
import gov.va.med.imaging.storage.cache.timer.EvictionTimerImpl;

import java.beans.XMLDecoder;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;

import javax.management.MBeanException;

import org.apache.log4j.Logger;

/**
 * @author VHAISWBECKEC
 * 
 * A factory that create Cache instances, the type of which is based on one of:
 * 1.) a memento type
 * 2.) a location URL
 * 
 */
public class CacheFactory
{
	// the create mode is passed in when creating a new cache from scratch (i.e. not from a memento)
	// the CreateMode is passed as a String (i.e. CREATE.toString()) rather than the enum value
	// so that application specific configurators may be specified 
	public enum CreateMode
	{
		CREATE,							// configure with no eviction strategies and no regions
		CREATE_TEST, 					// configure with test regions and short term evictions
		CREATE_TEST_NO_EVICTION 		// configure with test regions and no eviction
	};
	
	private static CacheFactory singleton;
	// a map from the persistence protocol to the cache and cache configurator that supports the protocol
	private static ProtocolCacheImplementationMap protocolCacheImplementationMap;
	private Logger logger = Logger.getLogger(this.getClass());
	
	static
	{
		// a map from the persistence protocol to the cache and cache configurator that supports the protocol
		protocolCacheImplementationMap = new ProtocolCacheImplementationMap();
		
		protocolCacheImplementationMap.put(
				gov.va.med.imaging.storage.cache.impl.filesystem.FileSystemCache.protocol, 
				gov.va.med.imaging.storage.cache.impl.filesystem.FileSystemCache.class, 
				gov.va.med.imaging.storage.cache.impl.filesystem.FileSystemCacheConfigurator.class,
				gov.va.med.imaging.storage.cache.impl.filesystem.memento.FileSystemCacheMemento.class);
				
//		protocolCacheImplementationMap.put(
//				gov.va.med.imaging.storage.cache.impl.jcifs.JcifsCache.protocol, 
//				gov.va.med.imaging.storage.cache.impl.jcifs.JcifsCache.class, 
//				gov.va.med.imaging.storage.cache.impl.jcifs.JcifsCacheConfigurator.class,
//				gov.va.med.imaging.storage.cache.impl.jcifs.memento.JcifsCacheMemento.class);
//		
//		protocolCacheImplementationMap.put(
//				gov.va.med.imaging.storage.cache.impl.memory.MemoryCache.protocol, 
//				gov.va.med.imaging.storage.cache.impl.memory.MemoryCache.class, 
//				gov.va.med.imaging.storage.cache.impl.memory.MemoryCacheConfigurator.class,
//				gov.va.med.imaging.storage.cache.impl.memory.memento.MemoryCacheMemento.class);
	};
	
	public static synchronized CacheFactory getSingleton()
	{
		if(singleton == null)
			singleton = new CacheFactory();
		
		return singleton;
	}
	
	/**
	 * 
	 */
	private CacheFactory()
	{
		
	}

	/**
	 * Use this method to create a cache instance from a cache memento instance.
	 * 
	 * @param memento
	 * @return
	 * @throws CacheInitializationException
	 */
	public Cache createCache(CacheMemento memento) 
	throws CacheInitializationException
	{
		Class<? extends Cache> cacheClass = protocolCacheImplementationMap.getCacheClass(memento);
		
		Cache cache = createCacheInstance(memento, cacheClass);
		//configurator = createConfiguratorInstance(protocol, configuratorClass, configurator);
		
		return cache;
	}
	
	/**
	 * Use this factory method to create a new uninitialized Cache.
	 * 
	 * @param protocol
	 * @param name
	 * @return
	 * @throws MBeanException
	 * @throws CacheException
	 */
	public Cache createCache(String cacheName, URI locationUri, String prototypeName) 
	throws CacheException
	{
		String protocol = locationUri.getScheme();
		String location = locationUri.getPath();
		
		logger.info(getClass().getSimpleName() + ".createCache ('" + locationUri.toString() + ")");
		logger.info(getClass().getSimpleName() + ".createCache protocol ='" + protocol + "', location='" + location + "'.");
		
		Class<? extends Cache> cacheClass = getImplementingCacheClass(locationUri);
		Class<? extends CacheConfigurator> cacheConfiguratorClass = getImplementingCacheConfiguratorClass(locationUri);

		logger.info(getClass().getSimpleName() + ".createCache cacheClass='" + cacheClass.getName() + "'" );

		Cache cache = createCacheInstance(cacheName, cacheClass, cacheConfiguratorClass, locationUri, null);
		//configurator = createConfiguratorInstance(protocol, configuratorClass, configurator);
		
		if(prototypeName != null)
			configureCache(cache, prototypeName);
		
		return cache;
	}

	/**
	 * Create a cache, given a prototype in the given input stream.
	 * 
	 * @param cacheName
	 * @param locationUri
	 * @param prototype
	 * @return
	 * @throws CacheException
	 */
	public Cache createCache(String cacheName, URI locationUri, InputStream prototype) 
	throws CacheException
	{
		String protocol = locationUri.getScheme();
		String location = locationUri.getPath();
		
		logger.info(getClass().getSimpleName() + ".createCache ('" + locationUri.toString() + ")");
		logger.info(getClass().getSimpleName() + ".createCache protocol ='" + protocol + "', location='" + location + "'.");
		
		Class<? extends Cache> cacheClass = getImplementingCacheClass(locationUri);
		Class<? extends CacheConfigurator> cacheConfiguratorClass = getImplementingCacheConfiguratorClass(locationUri);

		logger.info(getClass().getSimpleName() + ".createCache cacheClass='" + cacheClass.getName() + "'" );

		Cache cache = createCacheInstance(cacheName, cacheClass, cacheConfiguratorClass, locationUri, null);
		//configurator = createConfiguratorInstance(protocol, configuratorClass, configurator);
		
		if(prototype != null)
			configureCache(cache, prototype);
		
		return cache;
	}
	
	/**
	 * This method takes an unconfigured cache and runs the given configuration 
	 * strategy on it.  The configuration strategy name is a resource name of an XMLEncoded 
	 * CacheConfigurationStrategyMemento instance.
	 * 
	 * @param cache
	 * @param prototypeName
	 * @throws CacheInitializationException 
	 */
	private void configureCache(Cache cache, String prototypeName) 
	throws CacheException
	{
		logger.info("Configuring cache '" + cache.getName() + "' as '" + prototypeName + "'.");
		
		// look for the prototype first from the root and then in the prototype dir
		InputStream inStream = getClass().getClassLoader().getResourceAsStream(prototypeName);
		if(inStream == null)
		{
			String standardizedName = "prototype/" + prototypeName + ".xml";
			inStream = getClass().getClassLoader().getResourceAsStream(standardizedName);
			if(inStream == null)
				throw new CacheInitializationException("Unable to access resource '" + prototypeName + "' to configure cache, also looked in '" + standardizedName + "'.");
		}
		configureCache(cache, inStream);
	}
	
	/**
	 * Takes an unconfigured cache and applies the configuration from the input stream.
	 * 
	 * @param cache
	 * @param prototype
	 * @throws CacheException
	 */
	private void configureCache(Cache cache, InputStream prototype) 
	throws CacheException
	{
		logger.info("Configuring cache '" + cache.getName() + "' as from prototype input stream.");
		
		if(prototype == null)
			throw new CacheInitializationException("Unable to access resource prototype input stream to configure cache");
		
		XMLDecoder decoder = new XMLDecoder(prototype);
		CacheConfigurationMemento memento = (CacheConfigurationMemento)decoder.readObject();
		decoder.close();
		
		configureCache(cache, memento);
	}
	
	/**
	 * 
	 * @param cache
	 * @param memento
	 * @throws CacheException 
	 */
	private void configureCache(Cache cache, CacheConfigurationMemento memento) 
	throws CacheException
	{
		EvictionStrategyFactory evictionStrategyFactory = EvictionStrategyFactory.getSingleton();
		// create and add the eviction strategies
		if(memento.getEvictionStrategyMementoes() != null)
		{
			for(EvictionStrategyMemento evictionStrategyMemento : memento.getEvictionStrategyMementoes())
			{
				logger.info("Creating eviction strategy '" + evictionStrategyMemento.getName() + "'...");
				EvictionStrategy evictionStrategy = evictionStrategyFactory.createEvictionStrategy(evictionStrategyMemento, cache.getEvictionTimer()); 
				logger.info("Eviction strategy '" + evictionStrategyMemento.getName() + "' created.");
				cache.addEvictionStrategy(evictionStrategy);
				logger.info("Eviction strategy '" + evictionStrategyMemento.getName() + "' added to cache '" + cache.getName() + "'.");
			}
		}
		if(memento.getRegionMementoes() != null)
		{
			for(RegionMemento regionMemento : memento.getRegionMementoes())
			{
				logger.info("Creating region '" + regionMemento.getName() + "'...");
				Region region = cache.createRegion(regionMemento);
				logger.info("Region '" + regionMemento.getName() + "' created.");
				cache.addRegion(region);
				logger.info("Region '" + regionMemento.getName() + "' added to cache '" + cache.getName() + "'.");
			}
		}
	}

	private Class<? extends Cache> getImplementingCacheClass(URI locationUri) 
	throws CacheInitializationException
	{
		String protocol = locationUri.getScheme();
		
		Class<? extends Cache> cacheClass = protocolCacheImplementationMap.getCacheClass(protocol);

		if(cacheClass == null)
			throw new CacheInitializationException("Unable to find a Cache implementation that supports the '" + protocol + "' protocol.");

		return cacheClass;
	}

	private Class<? extends CacheConfigurator> getImplementingCacheConfiguratorClass(URI locationUri) 
	throws CacheInitializationException
	{
		String protocol = locationUri.getScheme();
		
		Class<? extends CacheConfigurator> configuratorClass = protocolCacheImplementationMap.getConfiguratorClass(protocol);

		if(configuratorClass == null)
			throw new CacheInitializationException("Unable to find a CacheConfigurator implementation that supports the '" + protocol + "' protocol.");

		return configuratorClass;
	}
	
	/**
	 * @param protocol
	 * @param cacheName
	 * @param cacheClass
	 * @param evictionTimer
	 * @throws CacheInitializationException
	 */
	private Cache createCacheInstance(
			String cacheName,
			Class<? extends Cache> cacheClass,
			Class<? extends CacheConfigurator> cacheConfiguratorClass,
			URI locationUri,
			EvictionTimer evictionTimer) 
	throws CacheInitializationException
	{
		try
		{
			if(evictionTimer == null && cacheConfiguratorClass != null)
				evictionTimer = createDefaultEvictionTimerImpl(cacheConfiguratorClass);
			
			Method factoryMethod = cacheClass.getMethod("create", new Class[]{String.class, URI.class, EvictionTimer.class});
			// assure that the factory method actually returns an instance of the cache class
			if( ! cacheClass.isAssignableFrom(factoryMethod.getReturnType()) )
				throw new CacheInitializationException(
						"Error creating the cache realization '" + cacheClass.getName() + "." + 
						"Assure that the factory method 'create(String, URI, EvictionTimer)' exists, is accessible and returns an instance of '" + cacheClass.getName() + "'.");
			
			return (Cache)factoryMethod.invoke(null, new Object[]{cacheName, locationUri, evictionTimer});
		} 
		catch (SecurityException x)
		{
			logger.error(x);
			throw new CacheInitializationException(
					"SecurityException creating the cache realization '" + cacheClass.getName() + "." + 
					"Assure that the factory method 'create(String, URI, EvictionTimer)' exists and is accessible");
		} 
		catch (IllegalArgumentException x)
		{
			logger.error(x);
			throw new CacheInitializationException(
					"IllegalArgumentException creating the cache realization '" + cacheClass.getName() + "." + 
					"Assure that the factory method 'create(String, URI, EvictionTimer)' exists and is accessible");
		} 
		catch (NoSuchMethodException x)
		{
			logger.error(x);
			throw new CacheInitializationException(
					"NoSuchMethodException creating the cache realization '" + cacheClass.getName() + "." + 
					"Assure that the factory method 'create(String, URI, EvictionTimer)' exists and is accessible");
		} 
		catch (IllegalAccessException x)
		{
			logger.error(x);
			throw new CacheInitializationException(
					"IllegalArgumentException creating the cache realization '" + cacheClass.getName() + "." + 
					"Assure that the factory method 'create(String, URI, EvictionTimer)' exists and is accessible");
		} 
		catch (InvocationTargetException x)
		{
			logger.error(x);
			throw new CacheInitializationException(
					"IllegalArgumentException creating the cache realization '" + cacheClass.getName() + "." + 
					"Assure that the factory method 'create(String, URI, EvictionTimer)' exists and is accessible");
		} 
		catch (InstantiationException x)
		{
			logger.error(x);
			throw new CacheInitializationException(
					"IllegalArgumentException creating the cache realization '" + cacheClass.getName() + "." + 
					"Assure that the factory method 'create(String, URI, EvictionTimer)' exists and is accessible");
		}
	}
	
	/**
	 * @param protocol
	 * @param cacheName
	 * @param cacheClass
	 * @param evictionTimer
	 * @throws CacheInitializationException
	 */
	private Cache createCacheInstance(
			CacheMemento cacheMemento,
			Class<? extends Cache> cacheClass) 
	throws CacheInitializationException
	{
		logger.info("Creating cache '" + cacheMemento.getName() + "' from memento as type '" + cacheClass.getName() + "'.");
		try
		{
			Method factoryMethod = cacheClass.getMethod("create", new Class[]{CacheMemento.class});
			// assure that the factory method actuall returns an instance of the cache class
			if( ! cacheClass.isAssignableFrom(factoryMethod.getReturnType()) )
				throw new CacheInitializationException(
						"Error creating the cache realization '" + cacheClass.getName() + "." + 
						"Assure that the factory method 'create(String, URI, EvictionTimer)' exists, is accessible and returns an instance of '" + cacheClass.getName() + "'.");
			
			return (Cache)factoryMethod.invoke(null, new Object[]{cacheMemento});
		} 
		catch (SecurityException x)
		{
			logger.error(x);
			throw new CacheInitializationException(
					"SecurityException creating the cache realization '" + cacheClass.getName() + "." + 
					"Assure that the factory method 'create(CacheMemento)' exists and is accessible");
		} 
		catch (IllegalArgumentException x)
		{
			logger.error(x);
			throw new CacheInitializationException(
					"IllegalArgumentException creating the cache realization '" + cacheClass.getName() + "." + 
					"Assure that the factory method 'create(CacheMemento)' exists and is accessible");
		} 
		catch (NoSuchMethodException x)
		{
			logger.error(x);
			throw new CacheInitializationException(
					"NoSuchMethodException creating the cache realization '" + cacheClass.getName() + "." + 
					"Assure that the factory method 'create(CacheMemento)' exists and is accessible");
		} 
		catch (IllegalAccessException x)
		{
			logger.error(x);
			throw new CacheInitializationException(
					"IllegalAccessException creating the cache realization '" + cacheClass.getName() + "." + 
					"Assure that the factory method 'create(CacheMemento)' exists and is accessible");
		} 
		catch (InvocationTargetException x)
		{
			x.printStackTrace();
			if(x.getCause() != null)
				x.getCause().printStackTrace();
			logger.error(x);
			logger.error("CAUSED BY ...");
			logger.error(x.getCause());
			throw new CacheInitializationException(
					"InvocationTargetException creating the cache realization '" + cacheClass.getName() + ". \n" +
					"Underlying exception is '" + x.getCause().getMessage() + "'. \n" +
					"Assure that the factory method 'create(CacheMemento)' exists and is accessible");
		}
	}
	
	// ===========================================================================================================================
	// Eviction Timer
	// ===========================================================================================================================

	/**
	 * 
	 * @return
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public EvictionTimerImpl createDefaultEvictionTimerImpl(Class<? extends CacheConfigurator> configuratorClass) 
	throws InstantiationException, IllegalAccessException
	{
		CacheConfigurator configurator = configuratorClass.newInstance();
		try
		{
			return EvictionTimerImpl.create(configurator.getEvictionTimerSweepIntervalMap());
		} 
		catch (InitializationException iX)
		{
			logger.error(iX);
			return null;
		} 
		catch (InvalidSweepSpecification issX)
		{
			logger.error(issX);
			return null;
		}
	}

	public EvictionTimerImpl createEvictionTimerImpl(EvictionTimerImplMemento memento)
	{
		try
		{
			return EvictionTimerImpl.create( memento.getSweepIntervalMap() );
		} 
		catch (InitializationException iX)
		{
			logger.error(iX);
			return null;
		} 
		catch (InvalidSweepSpecification issX)
		{
			logger.error(issX);
			return null;
		}
	}
}
