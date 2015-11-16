package gov.va.med.imaging.storage.cache.impl;

import gov.va.med.imaging.storage.cache.Cache;
import gov.va.med.imaging.storage.cache.CacheLifecycleEvent;
import gov.va.med.imaging.storage.cache.CacheManager;
import gov.va.med.imaging.storage.cache.CacheStructureChangeListener;
import gov.va.med.imaging.storage.cache.EvictionStrategy;
import gov.va.med.imaging.storage.cache.EvictionTimer;
import gov.va.med.imaging.storage.cache.Region;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import gov.va.med.imaging.storage.cache.exceptions.CacheInitializationException;
import gov.va.med.imaging.storage.cache.exceptions.CacheStateException;
import gov.va.med.imaging.storage.cache.impl.eviction.EvictionStrategyFactory;
import gov.va.med.imaging.storage.cache.impl.filesystem.FileSystemCache;
import gov.va.med.imaging.storage.cache.impl.jmx.AbstractCacheMBean;
import gov.va.med.imaging.storage.cache.memento.EvictionStrategyMemento;
import gov.va.med.server.CacheResourceReferenceFactory;
import gov.va.med.server.ServerLifecycleEvent;
import gov.va.med.server.ServerLifecycleListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenMBeanAttributeInfo;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import javax.management.openmbean.OpenMBeanConstructorInfo;
import javax.management.openmbean.OpenMBeanConstructorInfoSupport;
import javax.management.openmbean.OpenMBeanInfoSupport;
import javax.management.openmbean.OpenMBeanOperationInfo;
import javax.management.openmbean.OpenMBeanOperationInfoSupport;
import javax.management.openmbean.OpenMBeanParameterInfo;
import javax.management.openmbean.OpenMBeanParameterInfoSupport;
import javax.management.openmbean.SimpleType;
import javax.naming.NamingException;
import javax.naming.Reference;

import org.apache.log4j.Logger;

/**
 * Cache instances must be created through this class, not directly.  
 * This class is the interface for the cache lifecycle and also for the management and monitoring of the 
 * lifecycle and parameter persistence methods of a Cache instance.
 * 
 * This class also is responsible for storing and loading the FileSystemCache state and for
 * restoring the state of the cache when it is recreated.
 * 
 * The CacheManagerImpl singleton may manage a number of cache instances, each identified
 * by name.  This class (CacheFactory) uses the name as identified in the resource
 * declaration as the cache name it needs from CacheManagerImpl.
 * 
 * The CacheManagerImpl manages the lifecycle and the configuration of the Cache
 * regardless of whether an MBeanServer is available.  The CacheManagerImpl must be
 * instantiated and it must be used for Cache configuration, not direct
 * Cache access.
 * 
 * @author VHAISWBECKEC
 *
 */
public class CacheManagerImpl
extends AbstractCacheMBean
implements ServerLifecycleListener, CacheStructureChangeListener, CacheManager
{
	private static CacheManagerImpl singleton;		// the single instance of this class
	
	private Logger logger = Logger.getLogger(this.getClass());
	private boolean serverRunning = false;				// we may delay starting the managed caches so we set this when we get the start
	private KnownCacheList knownCaches;					// A list of all the caches that this manager knows about, this class keeps the
														// configurations in the config directory consistent with the transient
														// list of caches.
	
	public static final String defaultConfigurationDirectoryName = "/vix";
	public static final String cacheConfigurationSubdirectoryName = "cache-config";
	
	private Cache activeCache;		// used in interactive management, not used in normal operation
	
	/**
	 * This class is a singleton because the cache instances may be shared across multiple web apps 
	 * but each cache must behave with synchronicity with respect to its name as the primary key.
	 *   
	 * @return
	 * @throws MBeanException
	 * @throws CacheException 
	 */
	public static synchronized CacheManagerImpl getSingleton() 
	throws MBeanException, CacheException
	{
		if(singleton == null)
			singleton = new CacheManagerImpl();
		
		return singleton;
	}

	/**
	 * @throws MBeanException 
	 * @throws CacheInitializationException 
	 * 
	 *
	 */
	private CacheManagerImpl() 
	throws CacheException, MBeanException 
	{
		knownCaches = new KnownCacheList(getConfigurationDirectory());
		
		registerCacheManagerMBean();		// register ourselves as an MBean
		// register the known caches so that they are manageable
		for(Cache cache : knownCaches)
			registerCacheMBeans(cache);
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.ICacheManager#getKnownCaches()
	 */
	@Override
	public KnownCacheList getKnownCaches()
	{
		return this.knownCaches;
	}
	
	public Cache getActiveCache()
	{
		return activeCache;
	}
	
	public void setActiveCache(Cache activeCache)
	{
		this.activeCache = activeCache;
	}

	/**
	 * Returns true if this instance has received a server start event and has not 
	 * received a server stop event.
	 * @return
	 */
	@Override
	public boolean isServerRunning()
	{
		return serverRunning;
	}

	public Cache createCache(String name, URI locationUri) 
	throws MBeanException, CacheException, URISyntaxException, IOException
	{
		return createCache(name, locationUri, (String)null);
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.ICacheManager#createCache(java.lang.String, java.net.URI, java.lang.String)
	 */
	@Override
	public Cache createCache(String name, URI locationUri, String prototypeName) 
	throws MBeanException, CacheException, URISyntaxException, IOException
	{
		Cache cache = getKnownCaches().create(name, locationUri, prototypeName);
		
		// register the newly created cache so that it is manageable
		registerCacheMBeans(cache);
		
		return cache;
	}

	public Cache createCache(String name, URI locationUri, InputStream prototype) 
	throws MBeanException, CacheException, URISyntaxException, IOException
	{
		Cache cache = getKnownCaches().create(name, locationUri, prototype);
		
		// register the newly created cache so that it is manageable
		registerCacheMBeans(cache);
		
		return cache;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.ICacheManager#getCache(java.lang.String)
	 */
	@Override
	public Cache getCache(String cacheName) 
	throws FileNotFoundException, IOException, MBeanException, CacheException
	{
		Cache cache = knownCaches.get(cacheName);
		return cache;
	}
	
	// ============================================================================================	
	// Basic Cache management methods made available here so that tests can get a running cache
	// ============================================================================================
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.filesystem.CacheManager#initialize()
	 */
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.ICacheManager#initialize(gov.va.med.imaging.storage.cache.Cache)
	 */
	@Override
	public String initialize(Cache cache)
	{
		try
		{
			if(! cache.isInitialized())
			{
				cache.setInitialized(Boolean.TRUE);
				if(serverRunning)
					cache.cacheLifecycleEvent(CacheLifecycleEvent.START);
			}
			else
				return "Cache was already initialized";
		} 
		catch (CacheException cX)
		{
			logger.error("Error initializing cache.", cX);
			return cX.getMessage();
		}
		return "Cache Initialized";
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.filesystem.CacheManager#enable()
	 */
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.ICacheManager#enable(gov.va.med.imaging.storage.cache.Cache)
	 */
	@Override
	public String enable(Cache cache)
	{
		try
		{
			cache.setEnabled(Boolean.TRUE);
		} 
		catch (CacheException cX)
		{
			logger.error("Error enabling cache '" + cache.getName() + "'.", cX);
			return cX.getMessage();
		}
		return "Cache Enabled";
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.filesystem.CacheManager#disable()
	 */
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.ICacheManager#disable(gov.va.med.imaging.storage.cache.Cache)
	 */
	@Override
	public String disable(Cache cache)
	{
		try
		{
			cache.setEnabled(Boolean.FALSE);
		} 
		catch (CacheException cX)
		{
			logger.error("Error disabling cache '" + cache.getName() + "'.", cX);
			return cX.getMessage();
		}
		return "Cache Enabled";
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.ICacheManager#store(gov.va.med.imaging.storage.cache.Cache)
	 */
	@Override
	public void store(Cache cache) 
	throws IOException
	{
		getKnownCaches().store(cache);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.ICacheManager#storeAll()
	 */
	@Override
	public void storeAll() 
	throws IOException
	{
		getKnownCaches().storeAll();
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.ICacheManager#delete(gov.va.med.imaging.storage.cache.Cache)
	 */
	@Override
	public void delete(Cache cache)
	{
		// disable the cache to stop new requests
		disable(cache);
		unregisterCacheMBeans(cache);
		
		String cacheName = cache.getName();
		cache = null;		// drop the reference
		getKnownCaches().remove(cacheName);
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.ICacheManager#createEvictionStrategy(gov.va.med.imaging.storage.cache.Cache, gov.va.med.imaging.storage.cache.memento.EvictionStrategyMemento)
	 */
	@Override
	public EvictionStrategy createEvictionStrategy(Cache cache, EvictionStrategyMemento memento)
	throws CacheException
	{
		if( getKnownCaches().isKnownCache(cache) )
		{
			EvictionStrategyFactory factory = EvictionStrategyFactory.getSingleton();
			EvictionTimer timer = cache.getEvictionTimer();
			EvictionStrategy strategy = factory.createEvictionStrategy(memento, timer);
			
			if(strategy != null)
				cache.addEvictionStrategy(strategy);
			
			try
			{
				registerEvictionStrategyMBean(cache, strategy);
			} 
			catch (Exception x)
			{
				logger.warn(x);
			}
			
			return strategy;
		}
		
		return null;
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.ICacheManager#createRegion(gov.va.med.imaging.storage.cache.Cache, java.lang.String, java.lang.String[])
	 */
	@Override
	public Region createRegion(Cache cache, String regionName, String[] evictionStrategyNames)
	throws CacheException
	{
		if(getKnownCaches().isKnownCache(cache) )
		{
			Region region = cache.createRegion(regionName, evictionStrategyNames);
			
			cache.addRegion(region);
			
			try
			{
				registerRegionMBean(cache, region);
			} 
			catch (Exception x)
			{
				logger.warn(x);
			}
			
			return region;
		}
		
		return null;
	}
	
	// ========================================================================================================================
	// 
	// ========================================================================================================================
	
	private final static String cacheManagerMBeanObjectName = "VistaImaging.ViX:type=CacheManagerImpl,name=CacheManagerImpl";
	private final static String cacheMBeanObjectNamePrefix = "VistaImaging.ViX:type=Cache,name=";
	private final static String byteChannelMBeanObjectNamePrefix = "VistaImaging.ViX:type=CacheByteChannelFactory,name=";
	private final static String evictionStrategyMBeanObjectNamePrefix = "VistaImaging.ViX:type=CacheEvictionStrategy,name=";
	private final static String regionMBeanObjectNamePrefix = "VistaImaging.ViX:type=CacheRegion,name=";
	
	private String createCacheMBeanObjectName(Cache cache)
	{return cacheMBeanObjectNamePrefix + cache.getName(); }
	
	private String createByteChannelMBeanObjectName(Cache cache)
	{return byteChannelMBeanObjectNamePrefix + cache.getName(); }
	
	private String createRegionMBeanObjectName(Cache cache, Region region)
	{return regionMBeanObjectNamePrefix + cache.getName() + "." + region.getName(); }
	
	private String createEvictionStrategyMBeanObjectName(Cache cache, EvictionStrategy evictionStrategy)
	{return evictionStrategyMBeanObjectNamePrefix + cache.getName() + "." + evictionStrategy.getName(); }
	
	/**
	 * Register the cache manager
	 * 
	 * @param cacheName
	 */
	public void registerCacheManagerMBean()
	{
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		if(mbs != null)
		{
			try
			{
				mbs.registerMBean(this, new ObjectName(cacheManagerMBeanObjectName));
			} 
			catch (InstanceAlreadyExistsException iaeX)
			{
				logger.warn("MBean instance '" + cacheManagerMBeanObjectName + "' already exists, registration is being ignored");
			}
			catch (Exception x)
			{
				logger.warn("Unable to register Cache with JMX, management and monitoring will not be available", x);
			}
		}
	}
	
	private void registerCacheMBeans(Cache cache)
	{
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		
		if(mbs != null)
		{
			try
			{
				if( cache instanceof DynamicMBean)
				{
					String cacheMBeanName = createCacheMBeanObjectName(cache);
					logger.info("Registering cache '" + cacheMBeanName + "'.");
					
					try
					{
						mbs.registerMBean(cache, new ObjectName(cacheMBeanName));
					}
					catch(InstanceAlreadyExistsException iaeX)
					{
						logger.warn("MBean instance '" + cacheMBeanName + "' already exists, attempt to re-register is being ignored.");
					}
				}
				
				logger.info("Registering " + cache.getEvictionStrategies().size() + " eviction strategies for cache '" + cache.getName() + "'.");
				for(EvictionStrategy evictionStrategy: cache.getEvictionStrategies())
					registerEvictionStrategyMBean(cache, evictionStrategy);
		
				logger.info("Registering " + cache.getRegions().size() + " regions for cache '" + cache.getName() + "'.");
				for(Region region : cache.getRegions())
					registerRegionMBean(cache, region);
		
				if( cache.getInstanceByteChannelFactory() instanceof DynamicMBean)
				{
					String byteChannelMBeanName = createByteChannelMBeanObjectName(cache);
					logger.info("Registering byte channel '" + byteChannelMBeanName + "'.");
					
					try
					{
						mbs.registerMBean(cache.getInstanceByteChannelFactory(), new ObjectName(byteChannelMBeanName));
					} 
					catch (InstanceAlreadyExistsException iaeX)
					{
						logger.warn("MBean instance '" + byteChannelMBeanName + "' already exists, registration is being ignored");
					}
						
				}
			}
			catch (Exception x)
			{
				logger.warn("Unable to register Cache with JMX, management and monitoring will not be available", x);
			}
		}
	}
	
	private void registerEvictionStrategyMBean(Cache cache, EvictionStrategy evictionStrategy) 
	throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException, NullPointerException
	{
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		
		if(mbs != null)
		{
			String evictionStrategyObjectName = createEvictionStrategyMBeanObjectName(cache, evictionStrategy);
			logger.info("Registering eviction strategy '" + evictionStrategyObjectName + "'.");
			try
			{
				mbs.registerMBean( evictionStrategy, new ObjectName(evictionStrategyObjectName) );
			} 
			catch (InstanceAlreadyExistsException iaeX)
			{
				logger.warn("MBean instance '" + evictionStrategyObjectName + "' already exists, registration is being ignored");
			}
		}
	}
	
	private void registerRegionMBean(Cache cache, Region region) 
	throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException, NullPointerException
	{
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		
		if( mbs != null )
		{
			String regionObjectName = createRegionMBeanObjectName(cache, region);
			logger.info("Registering region '" + regionObjectName + "'.");
			
			try
			{
				mbs.registerMBean( region, new ObjectName(regionObjectName) );
			} 
			catch (InstanceAlreadyExistsException iaeX)
			{
				logger.warn("MBean instance '" + regionObjectName + "' already exists, registration is being ignored.");
			}
		}
	}
	
	private void unregisterCacheMBeans(Cache cache)
	{
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		
		if(mbs != null)
		{
			try
			{
				if( cache instanceof DynamicMBean)
				{
					String cacheMBeanName = createCacheMBeanObjectName(cache);
					try{mbs.unregisterMBean(new ObjectName(cacheMBeanName));}
					catch(InstanceNotFoundException infX){logger.warn(infX);}		// if the MBean is not registered then don't worry 'bout it
				}
				for(EvictionStrategy evictionStrategy: cache.getEvictionStrategies())
					try{unregisterEvictionStrategyMBean(cache, evictionStrategy);}
					catch(InstanceNotFoundException infX){logger.warn(infX);}		// if the MBean is not registered then don't worry 'bout it
		
				for(Region region : cache.getRegions())
					try{unregisterRegionMBean(cache, region);}
					catch(InstanceNotFoundException infX){logger.warn(infX);}		// if the MBean is not registered then don't worry 'bout it
		
				if( cache.getInstanceByteChannelFactory() instanceof DynamicMBean)
				{
					String byteChannelObjectName = createByteChannelMBeanObjectName(cache);
					try{mbs.unregisterMBean(new ObjectName(byteChannelObjectName));}
					catch(InstanceNotFoundException infX){logger.warn(infX);}		// if the MBean is not registered then don't worry 'bout it
				}
			}
			catch (Exception x)
			{
				logger.warn("Unable to unregister Cache with JMX, management and monitoring for new instances may not be available", x);
			}
		}
	}
	private void unregisterEvictionStrategyMBean(Cache cache, EvictionStrategy evictionStrategy) 
	throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException, NullPointerException, InstanceNotFoundException
	{
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		
		if(mbs != null && evictionStrategy instanceof DynamicMBean)
		{
			String evictionStrategyObjectName = createEvictionStrategyMBeanObjectName(cache, evictionStrategy);
			logger.info("Unregistering eviction strategy '" + evictionStrategyObjectName + "'.");
			
			mbs.unregisterMBean( new ObjectName(evictionStrategyObjectName) );
		}
	}
	
	private void unregisterRegionMBean(Cache cache, Region region) 
	throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException, NullPointerException, InstanceNotFoundException
	{
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		
		if( mbs != null && region instanceof DynamicMBean)
		{
			String regionObjectName = createRegionMBeanObjectName(cache, region);
			logger.info("Unregistering region '" + regionObjectName + "'.");
			
			mbs.unregisterMBean( new ObjectName(regionObjectName) );
		}
	}
	
	// ============================================================================================	
	// CacheLifecycleListener Implementation
	// These are messages from the app server, abstracted by a platform specific class
	// to our semantics
	// ============================================================================================
	
	
	/**
     * @see gov.va.med.server.ServerLifecycleListener#serverLifecycleEvent(gov.va.med.server.ServerLifecycleEvent)
     * Translate the server lifecycle messages to the cache lifecycle messages, removing the dependency
     * that the cache even be in a server environment.
     * This replaces the CacheLifecycleEvent handling in V-One.
     */
    @Override
    public void serverLifecycleEvent(ServerLifecycleEvent event)
    {
		boolean previousServerRunning = serverRunning;
		
		CacheLifecycleEvent cacheLifecycleEvent = null;
		
		if(event.getEventType().equals(ServerLifecycleEvent.EventType.START))
		{
			serverRunning = true;
			cacheLifecycleEvent = CacheLifecycleEvent.START;
		}
		if(event.getEventType().equals(ServerLifecycleEvent.EventType.STOP))
		{
			serverRunning = false;
			cacheLifecycleEvent = CacheLifecycleEvent.STOP;
		}
		
		// if this represents an actual server running state change then pass
		// it on to the cache instances
		if(previousServerRunning != serverRunning)
		{
			for( Cache cache : getKnownCaches() )
				// if the cache is initialized, pass this on to the cache
				if(cache.isInitialized())
				{
					try
					{
						cache.cacheLifecycleEvent(cacheLifecycleEvent);
					} 
					catch (CacheStateException x)
					{
						logger.error(x);
					}
				}
		}
    }

	// ============================================================================================	
	// DynamicMBean Implementation
	// ============================================================================================
	@Override
	public MBeanInfo getMBeanInfo() 
	{
		try
		{
			// the MBeanInfo must be regenerated because the state of some
			// operations may change
			return createMBeanInfo();
		} 
		catch (OpenDataException x)
		{
			logger.error(x);
			return null;
		}
	}
	
	private OpenMBeanInfoSupport createMBeanInfo() 
	throws OpenDataException
	{
     	return new OpenMBeanInfoSupport(
     			FileSystemCache.class.getName(), 
     			"Cache Management (initializing, enabling, storing)", 
     			createMBeanAttributeInfo(), 
     			createMBeanConstructorInfo(), 
	 			createMBeanOperationInfo(), 
	 			createMBeanNotificationInfo()
	 		);
	}

	/**
	 * 
	 * @param cache
	 * @throws OpenDataException 
	 */
	private OpenMBeanAttributeInfo[] createMBeanAttributeInfo() 
	throws OpenDataException
	{
		List<OpenMBeanAttributeInfo> attributes = new ArrayList<OpenMBeanAttributeInfo>();

		attributes.add(
			new OpenMBeanAttributeInfoSupport("knownCacheNames", "A comma seperated list of known cache names", SimpleType.STRING, true, false, false)
		);
		
		return attributes.toArray(new OpenMBeanAttributeInfo[attributes.size()]);
	}

	private OpenMBeanConstructorInfo[] createMBeanConstructorInfo()
	{
     	return new OpenMBeanConstructorInfoSupport[]
     	{
     			
     	};
	}
	
	private MBeanNotificationInfo[] createMBeanNotificationInfo()
	{
		return new MBeanNotificationInfo[]
		{
				
		};
	}

	private final static String initializePrefix = "initialize-";
	private final static String enablePrefix = "enable-";
	private final static String disablePrefix = "disable-";
	private final static String storeOperation = "store";
	private final static String storeAllOperation = "storeAll";
	private final static String createOperation = "createCache";
	
	/**
	 * The operations are pulled from the core cache and the member regions.
	 * @param cache 
	 *
	 */
	private OpenMBeanOperationInfo[] createMBeanOperationInfo()
	{
		List<OpenMBeanOperationInfo> operations = new ArrayList<OpenMBeanOperationInfo>();
		
		for(Cache cache : getKnownCaches())
		{
			if(cache != null && !cache.isInitialized())
				operations.add(
		     		new OpenMBeanOperationInfoSupport(initializePrefix + cache.getName(), 
		     				"Initialize the cache (root directory must be set first) \n" +
		     				"This action is ignored if the cache is initialized.\n" + 
		     				"If the cache has been started with a valid configuration state available \n" +
		     				"it will start in an initialized state.  Changes to configuration will then require a restart of the server.", 
		     				new OpenMBeanParameterInfo[]{}, 
		     				SimpleType.VOID, MBeanOperationInfo.ACTION)
		     	);
	
			if(cache != null && cache.isInitialized() && !cache.isEnabled() )
				operations.add(
		     		new OpenMBeanOperationInfoSupport(enablePrefix + cache.getName(), 
		     				"Enable the cache (cache must be initialized)", 
		     				new OpenMBeanParameterInfo[]{}, 
		     				SimpleType.VOID, MBeanOperationInfo.ACTION)
		     	);
			
			else if(cache != null && cache.isEnabled())
				operations.add(
		     		new OpenMBeanOperationInfoSupport(disablePrefix + cache.getName(), 
		     				"Disable the cache (cache must be initialized and enabled)", 
		     				new OpenMBeanParameterInfo[]{}, 
		     				SimpleType.VOID, MBeanOperationInfo.ACTION)
		     	);
		}
		
		operations.add(
	     		new OpenMBeanOperationInfoSupport(storeOperation, 
	     				"Store the named cache configuration to persistent storage", 
	     				new OpenMBeanParameterInfo[]{new OpenMBeanParameterInfoSupport("name", "the bname of the cache to save configuration of", SimpleType.STRING)}, 
	     				SimpleType.VOID, MBeanOperationInfo.ACTION)
	     	);
		
		operations.add(
	     		new OpenMBeanOperationInfoSupport(storeAllOperation, 
	     				"Store all current cache configuration to persistent storage", 
	     				new OpenMBeanParameterInfo[]{}, 
	     				SimpleType.VOID, MBeanOperationInfo.ACTION)
	     	);
		
		operations.add(
     		new OpenMBeanOperationInfoSupport(createOperation, 
     				"Create a new cache at the specified location.", 
 				new OpenMBeanParameterInfo[]
 				{
 					new OpenMBeanParameterInfoSupport("cacheName", "The name of the cache (and the root of the configuration file name)", SimpleType.STRING),
 					new OpenMBeanParameterInfoSupport("cacheLocation", "The URI of the cache location (e.g. 'file:///vix/cache' or 'smb://server/cacheroot')", SimpleType.STRING),
 					new OpenMBeanParameterInfoSupport("prototypeName", "The name of the prototype or blank(e.g. 'VixPrototype', 'TestWithEvictionPrototype')", SimpleType.STRING)
 				}, 
 				SimpleType.STRING, 
 				MBeanOperationInfo.ACTION)
     	);
		
		return operations.toArray(new OpenMBeanOperationInfoSupport[operations.size()]);
	}

	@Override
	public Object getAttribute(String attribute) 
	throws AttributeNotFoundException, MBeanException, ReflectionException
	{
		if("knownCacheNames".equals(attribute))
		{
			StringBuilder sb = new StringBuilder();
			for(Cache cache : getKnownCaches())
			{
				if(sb.length() > 0)
					sb.append(",");
				sb.append(cache.getName());
			}
			
			return sb.toString();
		}
		else
			return super.getAttribute(attribute);
	}

	@Override
	public void setAttribute(Attribute attribute) 
	throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException
	{
		super.setAttribute(attribute);
	}

	@Override
	public Object invoke(String actionName, Object[] params, String[] signature) 
	throws MBeanException, ReflectionException
	{
		
		if(actionName.startsWith(initializePrefix))
		{
			String cacheName = actionName.substring(initializePrefix.length());
			logger.info("Processing request to initialize cache '" + cacheName + "'.");
			return initialize(getKnownCaches().get(cacheName));
		}
		else if(actionName.startsWith(enablePrefix))
		{
			String cacheName = actionName.substring(initializePrefix.length());
			logger.info("Processing request to enable cache '" + cacheName + "'.");
			return enable(getKnownCaches().get(cacheName));
		}
		else if(actionName.startsWith(disablePrefix))
		{
			String cacheName = actionName.substring(initializePrefix.length());
			logger.info("Processing request to disable cache '" + cacheName + "'.");
			return disable(getKnownCaches().get(cacheName));
		}
		else if(storeOperation.equals(actionName))
		{
			String cacheName = (String)params[0];
			try
			{
				Cache cache = getCache(cacheName);
				logger.info("Processing request to store configuration of '" + cacheName + "'.");
				store(cache);
			}
			catch (Exception x)
			{
				logger.error("Error storing configuration of '" + cacheName + "'.");
				throw new MBeanException(x);
			}
			return "Cache configuration stored.";
		}
		else if(storeAllOperation.equals(actionName))
		{
			try
			{
				logger.info("Processing request to store configuration of all known caches.");
				storeAll();
			}
			catch (Exception x)
			{
				logger.error("Error storing configuration to persistent storage.", x);
				throw new MBeanException(x);
			}
			return "All cache configurations stored.";
		}
		else if(createOperation.equals(actionName) && 
				signature.length == 3 && 
				params[0] instanceof String && 
				params[1] instanceof String && 
				params[2] instanceof String )
		{
			String cacheName = (String)params[0];
			String cacheLocation = (String)params[1];
			String prototypeName = (String)params[2];
			
			try
			{
				logger.info("Processing request to create cache '" + cacheName + "' at '" + cacheLocation + "' as '" + prototypeName + "'." );
				getKnownCaches().create(cacheName, new URI(cacheLocation), prototypeName);
			} 
			catch (Exception x)
			{
				throw new MBeanException(x);
			}
			return "Cache '" + cacheName + "' created at '" + cacheLocation + "' as '" + prototypeName + "'.";
		}
		
		return super.invoke(actionName, params, signature);
	}

	// ==================================================================================================================================
	
	/**
	 * Return a refererence to the configuration directory, creating
	 * directories as necessary to assure it exists before returning.
	 */
	private File getConfigurationDirectory()
	{
		String rootConfigDirName = System.getenv("vixconfig");
		if(rootConfigDirName == null)
			rootConfigDirName = defaultConfigurationDirectoryName;
		
		File rootConfigDir = new File(rootConfigDirName);
		if( ! rootConfigDir.exists() )
			rootConfigDir.mkdirs();

		// the cache configuration is in a subdirectory of the configuration directory
		File cacheConfigDir = new File(rootConfigDir, cacheConfigurationSubdirectoryName);
		if( ! cacheConfigDir.exists() )
			cacheConfigDir.mkdirs();
		
		
		return cacheConfigDir;
	}

	// ===================================================================================================
	// interface CacheStructureChangeListener realization
	// ===================================================================================================
	@Override
	public void cacheStructureChanged(Cache cache)
	{
		unregisterCacheMBeans(cache);
		registerCacheMBeans(cache);
	}
	
	@Override
	public void evictionStrategyAdded(Cache cache, EvictionStrategy newEvictionStrategy)
	{
		try
		{
			registerEvictionStrategyMBean(cache, newEvictionStrategy);
		} 
		catch (Exception x)
		{
			logger.warn(x);
		}
	}

	@Override
	public void evictionStrategyRemoved(Cache cache, EvictionStrategy oldEvictionStrategy)
	{
		try
		{
			unregisterEvictionStrategyMBean(cache, oldEvictionStrategy);
		} 
		catch (Exception x)
		{
			logger.warn(x);
		}
	}

	@Override
	public void regionAdded(Cache cache, Region newRegion)
	{
		try
		{
			registerRegionMBean(cache, newRegion);
		} 
		catch (Exception x)
		{
			logger.warn(x);
		}
	}

	@Override
	public void regionRemoved(Cache cache, Region oldRegion)
	{
		try
		{
			unregisterRegionMBean(cache, oldRegion);
		} 
		catch (Exception x)
		{
			logger.warn(x);
		}
	}

	@Override
	public Reference getReference() 
	throws NamingException
	{
		return new Reference(this.getClass().getName(), CacheResourceReferenceFactory.class.getName(), null);
	}
}
