package gov.va.med.imaging.storage.cache.impl;

import gov.va.med.imaging.storage.cache.*;
import gov.va.med.imaging.storage.cache.exceptions.*;
import gov.va.med.imaging.storage.cache.impl.jmx.AbstractCacheMBean;
import gov.va.med.imaging.storage.cache.memento.CacheMemento;
import gov.va.med.imaging.storage.cache.memento.EvictionTimerImplMemento;
import gov.va.med.imaging.storage.cache.memento.RegionMemento;
import gov.va.med.imaging.storage.cache.timer.EvictionTimerImpl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import javax.management.*;
import javax.management.openmbean.*;

import org.apache.log4j.Logger;

/**
 * 
 * 
 */
public abstract class AbstractCacheImpl 
extends AbstractCacheMBean
implements Cache
{
	private Logger logger = Logger.getLogger(this.getClass());
	
	private String name = null;			// the name of the cache, mostly used for management naming
	private URI locationUri; 			// the root of the persistence
	private String locationUriScheme;
	private String locationUriPath;
	protected final EvictionTimer defaultEvictionTimer;
	
	private boolean enabled = false; // if false then cache calls will no-op
	private boolean initialized = false; // an internal flag, set only when initialization is complete
	protected boolean running = false;
	
	protected long getOperationCount = 0L;
	protected long getOperationSuccessfulCount = 0L;
	protected long getOperationErrorCount = 0L;
	protected long getOperationInstanceNotFoundCount = 0L;
	
	protected long deleteOperationCount = 0L;
	protected long deleteOperationSuccessfulCount = 0L;
	protected long deleteOperationErrorCount = 0L;
	protected long deleteOperationInstanceNotFoundCount = 0L;
	
	protected long getOrCreateOperationCount = 0L;
	protected long getOrCreateOperationSuccessfulCount = 0L;
	protected long getOrCreateOperationErrorCount = 0L;
	
	protected long operationCountResetDate = System.currentTimeMillis();

	public final static int defaultSecondsReadWaitsForWriteCompletion = 60;
	public final static boolean defaultSetModificationTimeOnRead = true;

	//================================================================================================
	// Constructors
	//================================================================================================
	
	/**
	 * Create a cache instance using the parameters from the memento.
	 * NOTE: this constructor does not set the initialized or enabled flags
	 * because derived classes may need other parameters set first.
	 * 
	 * @param name
	 * @throws CacheInitializationException 
	 */
	protected AbstractCacheImpl(CacheMemento memento) 
	throws CacheInitializationException
	{
		logger.info("constructing cache from memento '" + memento.getName() + "'");
		this.name = memento.getName();
		logger.info("constructing cache from memento '" + memento.getName() + "', setting cache URI to '" + memento.getLocationUri() + "'");
		try
		{
			URI mementoUri = new URI(memento.getLocationUri());
			setLocationUri( mementoUri );
		} 
		catch (URISyntaxException x)
		{
			String message = "URI '" + memento.getLocationUri() + "' is in an invalid format, cache will not initialize.";
			logger.error(message);
			throw new CacheInitializationException(message);
		}
		
		try
		{
			if(memento.getEvictionTimerMemento() instanceof EvictionTimerImplMemento)
				this.defaultEvictionTimer = EvictionTimerImpl.create( (EvictionTimerImplMemento)memento.getEvictionTimerMemento() );
			else
				this.defaultEvictionTimer = null;
		} 
		catch (InitializationException x)
		{
			String message = "Unable to initialize the eviction timer";
			logger.error(message);
			throw new CacheInitializationException(message);
		} 
		catch (InvalidSweepSpecification x)
		{
			String message = "Unable to initialize the eviction timer, invalid sweep specification";
			logger.error(message);
			throw new CacheInitializationException(message);
		}
	}
	
	protected AbstractCacheImpl(String name, URI locationUri, EvictionTimer defaultEvictionTimer, InstanceByteChannelFactory<?> byteChannelFactory) 
	throws CacheInitializationException
	{
		this.name = name;
		setLocationUri(locationUri);
		
		try
		{
			this.defaultEvictionTimer = defaultEvictionTimer != null ? defaultEvictionTimer : EvictionTimerImpl.create(EvictionTimerImplMemento.createDefault());
		} 
		catch (InitializationException x)
		{
			String message = "Unable to initialize the eviction timer";
			logger.error(message);
			throw new CacheInitializationException(message);
		} 
		catch (InvalidSweepSpecification x)
		{
			String message = "Unable to initialize the eviction timer, invalid sweep specification";
			logger.error(message);
			throw new CacheInitializationException(message);
		}
			
		try
		{
			setInstanceByteChannelFactory(byteChannelFactory);
		} 
		catch (CacheStateException x)
		{
			logger.error(x);		// won't happen unless the cache code defaults to initialized
		}
	}
	
	@Override
	public abstract CacheMemento createMemento();

	//================================================================================================
	// Core Properties having to do with name, default eviction timer
	// and the externally available lifecycle methods (initialization 
	// and enablement)
	//================================================================================================
	
	@Override
	public String getName()
	{
		return name;
	}
	
	@Override
	public URI getLocationUri()
	{
		return locationUri;
	}
	
	/**
	 * Set the location URI and parse the portions of it that must exist
	 * for all cache derivations.
	 * 
	 * @param locationUri
	 * @throws CacheInitializationException 
	 */
	private void setLocationUri(URI locationUri) 
	throws CacheInitializationException
	{
		this.locationUri = locationUri;
		this.locationUriScheme = locationUri.getScheme();
		
		this.locationUriPath = extractPath(locationUri);
		
		// call the derivation specific parsing
		parseLocationUri(locationUri);
	}

	/**
	 * Allow derived classes to override this so that they may do some non-standard
	 * URI interpretation.
	 * 
	 * @param locationUri
	 */
	protected String extractPath(URI locationUri)
	{
		return locationUri.getPath();
	}
	
	/**
	 * The parseLocationUri may be overriden by derived classes so that they
	 * may extract the implementation specific URI content.  This default
	 * implementation does nothing.
	 * Overridding methods may assume that the following methods will return 
	 * valid (location URI derived) values:
	 * getLocationProtocol()
	 * getLocationPath()   
	 * 
	 * @param locationUri
	 * @throws CacheInitializationException 
	 */
	protected void parseLocationUri(URI locationUri) 
	throws CacheInitializationException
	{
	}
	
	@Override
	public String getLocationProtocol()
	{
		return locationUriScheme;
	}
	
	@Override
	public String getLocationPath()
	{
		return locationUriPath;
	}
	
	@Override
	public EvictionTimer getEvictionTimer()
	{
		return this.defaultEvictionTimer;
	}

	@Override
	public Boolean isEnabled()
	{
		return this.enabled;
	}

	@Override
	public void setEnabled(Boolean enabled) 
	throws CacheException
	{
		// if the request is to enable the cache but it has not been initialized
		if(enabled && ! isInitialized() )
			throw new CacheStateException("Illegal attempt to enable an un-initialized " + getClass().getSimpleName() + " instance.");
		
		this.enabled = enabled;
	}
	
	/**
	 * Derived class should override this method if there are requirements
	 * for initialization (such as regions defined, eviction strategies defined, etc...).
	 * Once an instance is initialized it stays initialized.
	 * 
	 * @param initialized
	 * @throws CacheException 
	 */
	@Override
	public final void setInitialized(Boolean initialized) 
	throws CacheException
	{
		if(isInitialized() && initialized == Boolean.TRUE)
			return;
		
		if(! isInitialized() && initialized == Boolean.FALSE)
			return;
		
		internalInitialize();		// do the type specific initialization
		
		for(EvictionStrategy evictionStrategy : getEvictionStrategies())
			evictionStrategy.setInitialized(initialized);
		
		for(Region region : getRegions())
			region.setInitialized(initialized);
		
		resetOperationCounters();
		this.initialized = initialized;
	}
	
	/**
	 * Derived classes may override this method to do type-specific initialization.
	 * A derived class may rely on the following when this method is called:
	 * 1.) The cache is not currently initialized (i.e. isInitialized() returns false)
	 * 
	 * This (abstract) class will set the initialized flags on the eviction strategies and
	 * the regions after this method is called.  The eviction strategies and regions are
	 * initialized in a similar manner.
	 * 
	 * Any exceptions throw in initialization will leave the cache initialization in an unknown state.
	 * 
	 * @throws InitializationException
	 * @throws CacheStateException
	 */
	protected void internalInitialize() 
	throws InitializationException, CacheStateException
	{
		// do nothing and do not require implementation by derived classes.
	}
	
	/**
	 * @return
	 */
	@Override
	public Boolean isInitialized()
	{
		return this.initialized;
	}
	

	/**
	 * @return True if the cache has received the START event and not received a STOP event.
	 */
	public Boolean isRunning()
	{
		return running;
	}

	//================================================================================================
	// Region Management
	//================================================================================================
	// nothing magic about 8, we just know that we'll usually create 4 regions
	// and the HashMap will want some extra space
	private Set<Region> regions = new HashSet<Region>(8);

	/**
	 * Required implementation for derived classes.
	 * The type declaration is left to the derived classes so that they may
	 * specify specific types.
	 */
	@Override
	public java.util.Collection<? extends Region> getRegions()
	{
		return regions;
	}
	
	/**
	 * Silently returns if the region is the correct Region-derivation for this
	 * cache, otherwise throws an exception.
	 * Having this allows this abstract class to manage regions more fully.
	 * @param region
	 * @throws IncompatibleRegionException
	 */
	protected abstract void validateRegionType(Region region)
	throws IncompatibleRegionException;
	
	/**
	 * Create a Region-derivation that is compatible with the Cache derivation.
	 */
	@Override
	public abstract Region createRegion(String name, String[] evictionStrategyNames)
	throws RegionInitializationException;
	
	/**
	 * Create a Region-derivation that is compatible with the Cache derivation
	 * using the memento for the parameters.
	 */
	@Override
	public abstract Region createRegion(RegionMemento regionMemento)
	throws RegionInitializationException;

	/**
	 * 
	 * @param region
	 * @throws CacheException
	 */
	@Override
	public void addRegion(Region region)
	throws CacheException
	{
		if(isInitialized())
			throw new CacheStateException("Illegal attempt to add a region on an initialized FileSystemCache instance");

		validateRegionType(region);
		
		if( getRegion(region.getName()) != null)
			throw new InvalidRegionNameException("Attempt to add duplicate region name '" + region.getName() + "' is denied.");
		
		regions.add(region);

		notifyListenersOfNewRegion(region);
	}
	
	/**
	 * 
	 * @param regions
	 * @throws CacheException
	 */
	@Override
	public void addRegions(Collection<? extends Region> regions)
	throws CacheException
	{
		for(Region region:regions)
			addRegion(region);
		
		notifyListenersOfStructureChange();
	}
	
	
	/**
	 * Remove all of the region definitions
	 * 
	 * @throws CacheStateException
	 */
	public void clearRegions() 
	throws CacheStateException
	{
		if(isInitialized())
			throw new CacheStateException("Illegal attempt to clear regions on an initialized FileSystemCache instance");
		
		getRegions().clear();
		
		notifyListenersOfStructureChange();
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	@Override
	public Region getRegion(String name)
	{
		if(name == null)
			return null;
		
		for(Region region:getRegions())
			if( name.equals(region.getName()) )
				return region;
		return null;
	}

	/**
	 * 
	 * @param regionName
	 * @param evictionStrategyName
	 * @throws CacheException
	 */
	void setRegionEvictionStrategy(String regionName, String[] evictionStrategyNames)
	throws CacheException
	{
		Region region = getRegion(regionName); 
		if(region == null)
			throw new InvalidRegionNameException("Region '" + regionName + "' is not knwon to this cache instance.");
		region.setEvictionStrategyNames(evictionStrategyNames);
	}
	
	//=========================================================================================
	// Instance access methods
	//=========================================================================================
	/**
	 * 
	 */
	@Override
	public Instance getOrCreateInstance(String regionName, String[] group, String key) 
	throws CacheException
	{
		if (!isEnabled())
			return null;

		incrementGetOrCreateOperationInitiated();
		Region region = getRegion(regionName);
		if (region == null)
		{
			incrementGetOrCreateOperationError();
			throw new RegionDoesNotExistException( "createInstance call passed unknown regionName - '" + regionName + "'");
		}
		try
		{
			Instance instance = region.getOrCreateInstance(group, key);
			incrementGetOrCreateOperationSuccessful();
			return instance;
		} 
		catch (CacheException e)
		{
			incrementGetOrCreateOperationError();
			throw e;
		}
	}

	/**
	 * 
	 */
	@Override
	public Instance getInstance(String regionName, String[] group, String key)
	throws CacheException
	{
		if (!isEnabled())
			return null;

		incrementGetOperationInitiated();
		Region region = getRegion(regionName);
		if (region == null)
		{
			incrementGetOperationError();
			throw new RegionDoesNotExistException( "createInstance call passed unknown regionName - '" + regionName + "'");
		}

		try
		{
			Instance instance = region.getInstance(group, key);
			if (instance == null)
				incrementGetOperationInstanceNotFound();
			else
				incrementGetOperationSuccessful();
			return instance;
		} 
		catch (CacheException e)
		{
			incrementGetOperationError();
			throw e;
		}
	}

	/**
	 * 
	 */
	@Override
	public void deleteInstance(String regionName, String[] group, String key, boolean forceDelete)
	throws CacheException
	{
		if (!isEnabled().booleanValue())
			return;

		incrementDeleteOperationInitiated();
		Region region = getRegion(regionName);
		if (region == null)
		{
			incrementDeleteOperationError();
			throw new RegionDoesNotExistException( "deleteInstance call passed unknown regionName - '" + regionName + "'");
		}

		try
		{
			region.deleteInstance(group, key, forceDelete);
			incrementDeleteOperationSuccessful();
			return;
		} 
		catch (CacheException e)
		{
			incrementDeleteOperationError();
			throw e;
		}
	}

	@Override
	public Group getOrCreateGroup(String regionName, String[] groups)
	throws CacheException 
	{
		if (!isEnabled())
			return null;

		incrementGetOrCreateOperationInitiated();
		Region region = getRegion(regionName);
		if (region == null)
		{
			incrementGetOrCreateOperationError();
			throw new RegionDoesNotExistException( "createInstance call passed unknown regionName - '" + regionName + "'");
		}
		try
		{
			Group group = region.getOrCreateGroup(groups);
			incrementGetOrCreateOperationSuccessful();
			return group;
		} 
		catch (CacheException e)
		{
			incrementGetOrCreateOperationError();
			throw e;
		}
	}

	/**
	 * 
	 */
	@Override
	public Group getGroup(String regionName, String[] groups) 
	throws CacheException 
	{
		if (!isEnabled())
			return null;
		
		if (!isEnabled())
			return null;

		incrementGetOperationInitiated();
		Region region = getRegion(regionName);
		if (region == null)
		{
			incrementGetOperationError();
			throw new RegionDoesNotExistException( "createInstance call passed unknown regionName - '" + regionName + "'");
		}

		try
		{
			Group group = region.getGroup(groups);
			if (group == null)
				incrementGetOperationInstanceNotFound();
			else
				incrementGetOperationSuccessful();
			return group;
		} 
		catch (CacheException e)
		{
			incrementGetOperationError();
			throw e;
		}
	}

	/**
	 * 
	 */
	@Override
	public void deleteGroup(String regionName, String[] group, boolean forceDelete) 
	throws CacheException
	{
		if (!isEnabled().booleanValue())
			return;

		incrementDeleteOperationInitiated();
		Region region = getRegion(regionName);
		if (region == null)
		{
			incrementDeleteOperationError();
			throw new RegionDoesNotExistException( "deleteGoup call passed unknown regionName - '" + regionName + "'");
		}

		try
		{
			region.deleteGroup(group, forceDelete);
			incrementDeleteOperationSuccessful();
			return;
		} 
		catch (CacheException e)
		{
			incrementDeleteOperationError();
			throw e;
		}
	}

	//================================================================================================
	// Statistics gathering an publication
	//================================================================================================
	public Long getOperationCountResetDate()
	{
		return operationCountResetDate;
	}
	
	// GET operations counter accessors
	public Long getGetOperationInitiatedCount()
	{
		return getOperationCount;
	}
	public Long getGetOperationSuccessfulCount()
	{
		return getOperationSuccessfulCount;
	}
	public Long getGetOperationErrorCount()
	{
		return getOperationErrorCount;
	}
	public Long getGetOperationInstanceNotFoundCount()
	{
		return getOperationInstanceNotFoundCount;
	}
	
	// protected methods to increment GET operation counters, called by derived classes 
	protected void incrementGetOperationInitiated()
	{
		++getOperationCount;
	}
	protected void incrementGetOperationSuccessful()
	{
		++getOperationSuccessfulCount;
	}
	protected void incrementGetOperationError()
	{
		++getOperationErrorCount;
	}
	protected void incrementGetOperationInstanceNotFound()
	{
		++getOperationInstanceNotFoundCount;
	}

	// protected methods to increment DELETE operation counters, called by derived classes 
	protected void incrementDeleteOperationInitiated()
	{
		++deleteOperationCount;
	}
	protected void incrementDeleteOperationSuccessful()
	{
		++deleteOperationSuccessfulCount;
	}
	protected void incrementDeleteOperationError()
	{
		++deleteOperationErrorCount;
	}
	protected void incrementDeleteOperationInstanceNotFound()
	{
		++deleteOperationInstanceNotFoundCount;
	}
	
	// GET or CREATE operations counter accessors
	public Long getGetOrCreateOperationInitiatedCount()
	{
		return getOrCreateOperationCount;
	}
	public Long getGetOrCreateOperationSuccessfulCount()
	{
		return getOperationSuccessfulCount;
	}
	public Long getGetOrCreateOperationErrorCount()
	{
		return getOperationErrorCount;
	}
	
	// protected methods to increment GET OR CREATE operation counters, called by derived classes 
	protected void incrementGetOrCreateOperationInitiated()
	{
		++getOrCreateOperationCount;
	}
	protected void incrementGetOrCreateOperationSuccessful()
	{
		++getOrCreateOperationSuccessfulCount;
	}
	protected void incrementGetOrCreateOperationError()
	{
		++getOrCreateOperationErrorCount;
	}
	
	/**
	 * Reset the monitoring counters 
	 *
	 */
	public Long resetOperationCounters()
	{
		getOperationCount = 0L;
		getOperationSuccessfulCount = 0L;
		getOperationErrorCount = 0L;
		getOperationInstanceNotFoundCount = 0L;
		
		deleteOperationCount = 0L;
		deleteOperationSuccessfulCount = 0L;
		deleteOperationErrorCount = 0L;
		deleteOperationInstanceNotFoundCount = 0L;
		
		getOrCreateOperationCount = 0L;
		getOrCreateOperationSuccessfulCount = 0L;
		getOrCreateOperationErrorCount = 0L;
		
		long previousResetDate = operationCountResetDate;
		operationCountResetDate = System.currentTimeMillis();
		
		return previousResetDate;
	}
	
	// ===============================================================================
	// Cache lifecycle implementation
	// ===============================================================================
	/**
	 * Override this method in derived classes to get notification of when the
	 * cache should be started according to the server lifecycle
	 */
	protected void start()
	{
		System.out.println(this.getClass().getName() + " starting.");
	}

	/**
	 * Override this method in derived classes to get notification of when the
	 * cache should be stopped according to the server lifecycle
	 */
	protected void stop()
	{
		System.out.println(this.getClass().getName() + " stopping.");
	}
	
	@Override
	public void cacheLifecycleEvent(CacheLifecycleEvent event)
	{
		if(!isInitialized())
		{
			logger.warn("START message received when cache is not initialized, cache must be manually started after initialization is complete.");
			return;
		}
		
		if( event.equals(CacheLifecycleEvent.START) )
		{
			this.start();
			notifyRegionsCacheLifecycleEvent(event);
			notifyByteChannelFactoryCacheLifecycleEvent(event);
			running = true;
		}
		else if( event.equals(CacheLifecycleEvent.STOP) )
		{
			this.stop();
			defaultEvictionTimer.cancel();
			notifyRegionsCacheLifecycleEvent(event);
			notifyByteChannelFactoryCacheLifecycleEvent(event);
			running = false;
		}
	}
	
	private void notifyByteChannelFactoryCacheLifecycleEvent(CacheLifecycleEvent event)
	{
		try
		{
			if( getInstanceByteChannelFactory() instanceof CacheLifecycleListener )
				((CacheLifecycleListener)getInstanceByteChannelFactory()).cacheLifecycleEvent(event);
		}
		catch(CacheStateException csX)
		{
			logger.error("Unable to start ByteChannelFactory for cache '" + getName() + "'.");
		}
	}
	
	private void notifyRegionsCacheLifecycleEvent(CacheLifecycleEvent event)
	{
		for(Region region: getRegions())
		{
			try
			{
				if( region instanceof CacheLifecycleListener )
					((CacheLifecycleListener)region).cacheLifecycleEvent(event);
			}
			catch(CacheStateException csX)
			{
				logger.error("Unable to start Region for cache '" + getName() + "'.");
			}
		}
	}
	
	/**
	 * By default, clearing the cache is the same as clearing all of the regions in the cache
	 * @throws CacheException 
	 */
	@Override
	public void clear() 
	throws CacheException
	{
		for(Region region: getRegions())
			region.deleteAllChildGroups(false);
	}
	
	// ===========================================================================================================
	// Instance Byte Channel Factory Management Methods
	// ===========================================================================================================
	private InstanceByteChannelFactory<?> byteChannelFactory = null;
	
	/**
	 * Return the factory instance that is used to create all instance byte
	 * channels. The factory does some management of the byte channel instances
	 * lifecycle, i.e. it watches for unclosed channels.
	 * 
	 * @return
	 */
	@Override
	public InstanceByteChannelFactory<?> getInstanceByteChannelFactory()
	{
		return byteChannelFactory;
	}
	
	protected void setInstanceByteChannelFactory(InstanceByteChannelFactory byteChannelFactory) 
	throws CacheStateException
	{
		if(isInitialized())
			throw new CacheStateException("Illegal attempt to change the InstanceByteChannelFactory after cache initialization");
		this.byteChannelFactory = byteChannelFactory;
	}
	
	// ===========================================================================================================
	// Eviction Strategy Management Methods
	// ===========================================================================================================
	private Set<EvictionStrategy> evictionStrategies = new HashSet<EvictionStrategy>();
	
	@Override
	public void addEvictionStrategies(Collection<? extends EvictionStrategy> evictionStrategies) 
	throws CacheStateException
	{
		for(EvictionStrategy evictionStrategy: evictionStrategies)
			addEvictionStrategy(evictionStrategy);
		
		notifyListenersOfStructureChange();
	}
	
	/**
	 * 
	 * @param evictionStrategy
	 * @throws CacheStateException 
	 */
	@Override
	public void addEvictionStrategy(EvictionStrategy evictionStrategy) 
	throws CacheStateException
	{
		if(isInitialized())
			throw new CacheStateException("Illegal attempt to add an eviction strategy on an initialized FileSystemCache instance");
		
		evictionStrategies.add(evictionStrategy);
		
		notifyListenersOfNewEvictionStrategy(evictionStrategy);
	}

	/**
	 * 
	 * @throws CacheStateException
	 */
	public void clearEvictionStrategies()
	throws CacheStateException
	{
		if(isInitialized())
			throw new CacheStateException("Illegal attempt to clear eviction strategies on an initialized FileSystemCache instance");
		evictionStrategies.clear();
		
		notifyListenersOfStructureChange();
	}
	
	/**
	 * Return a non-modifiable collection of the eviction strategies known to this cache
	 * @return
	 */
	@Override
	public Collection<EvictionStrategy> getEvictionStrategies()
	{
		return java.util.Collections.unmodifiableCollection(evictionStrategies);
	}
	
	/**
	 * Find an eviction strategy within those known to the cache with the given name.
	 * Names ARE case sensitive.
	 * 
	 * @param name
	 * @return An eviction strategy with the name given or null if none found
	 */
	@Override
	public EvictionStrategy getEvictionStrategy(String name)
	{
		if(name == null)
			return null;
		
		for(EvictionStrategy evictionStrategy: evictionStrategies)
			if( name.equals(evictionStrategy.getName()) )
					return evictionStrategy;
			
		return null;
	}
	
	/**
	 * A toString() that will build a reasonable human readable
	 * identifier.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(getName());
		sb.append("(");
		sb.append(getLocationUri().toString());
		sb.append(")");
		
		return sb.toString();
	}

	// ============================================================================================	
	// DynamicMBean Implementation
	// ============================================================================================
	private OpenMBeanInfoSupport mBeanInfo = null;
	@Override
	public synchronized MBeanInfo getMBeanInfo() 
	{
		if(mBeanInfo == null)
			try
			{
				mBeanInfo = createMBeanInfo();
			} 
			catch (OpenDataException x)
			{
				logger.error("Error creating MBeanInfo, management and monitoring of cache will not be available.", x);
			}
		
		return mBeanInfo;
	}
	
	private OpenMBeanInfoSupport createMBeanInfo() 
	throws OpenDataException
	{
     	return new OpenMBeanInfoSupport(
     			getClass().getName(), 
     			getClass().getSimpleName() +  " implementation", 
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

		// create the core cache attributes
		attributes.add(
			new OpenMBeanAttributeInfoSupport("initialized",  
					"The cache is initialized when it has established or located its persistence structure.\n" +
					"The cache must be initialized to be enabled.\n" +  
					"The cache must be initialized to be running.",
					SimpleType.BOOLEAN, 
					true, false, true)
		);
		attributes.add( 
			new OpenMBeanAttributeInfoSupport("enabled",  
					"The cache may be enabled and disabled at runtime.\n" +
					"When disabled it will return nulls for all instance access calls.\n" + 
					"Use the enable and disable actions to change the enabled state.\n" +
					"The cache does not have to be enabled to be initialized.\n" +  
					"The cache does not have to be enabled to be running.\n", 
					SimpleType.BOOLEAN, 
					true, false, true)
		);
		attributes.add( 
			new OpenMBeanAttributeInfoSupport("running",  
					"The cache is running when it has received a start message and has not received a stop message.\n" +
					"When the cache is running it will run its eviction threads and periodically clear itself of old data.\n" +
					"The cache must be initialized to be running.\n" +
					"The cache does not have to be enabled to be running.",
					SimpleType.BOOLEAN, 
					true, false, true)
		);
		
		attributes.add( 
			new OpenMBeanAttributeInfoSupport("locationUri", 
					"The root directory of the cache.\n",
					SimpleType.STRING, 
					true, false, false)
		);
					
		attributes.add( 
			new OpenMBeanAttributeInfoSupport("getOperationInitiatedCount", "The number of GET operations initiated", SimpleType.LONG, true, false, false)
		);
		
		attributes.add( 
			new OpenMBeanAttributeInfoSupport("getOperationSuccessfulCount", "The number of successful GET operations.", SimpleType.LONG, true, false, false)
		);
		
		attributes.add( 
			new OpenMBeanAttributeInfoSupport("getOperationErrorCount", "The number of GET operations that resulted in an error.", SimpleType.LONG, true, false, false)
		);
		
		attributes.add( 
			new OpenMBeanAttributeInfoSupport("getOperationInstanceNotFoundCount", "The number of GET operations that resulted in an instance not found", SimpleType.LONG, true, false, false)
		);
			
		attributes.add( 
			new OpenMBeanAttributeInfoSupport("getOrCreateOperationInitiatedCount", "The number of GET-OR-CREATE operations initiated", SimpleType.LONG, true, false, false)
		);
		
		attributes.add( 
			new OpenMBeanAttributeInfoSupport("getOrCreateOperationSuccessfulCount", "The number of successful GET-OR-CREATE operations.", SimpleType.LONG, true, false, false)
		);
		
		attributes.add( 
			new OpenMBeanAttributeInfoSupport("getOrCreateOperationErrorCount", "The number of GET-OR-CREATE operations that resulted in an error.", SimpleType.LONG, true, false, false)
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

	
	/**
	 * The operations are pulled from the core cache and the member regions.
	 * @param cache 
	 *
	 */
	private OpenMBeanOperationInfo[] createMBeanOperationInfo()
	{
		List<OpenMBeanOperationInfo> operations = new ArrayList<OpenMBeanOperationInfo>();

		operations.add(
     		new OpenMBeanOperationInfoSupport("resetOperationCounters", "Reset the monitor counters", 
     				new OpenMBeanParameterInfo[]{}, 
     				SimpleType.STRING, MBeanOperationInfo.ACTION)
     	);

		
		return operations.toArray(new OpenMBeanOperationInfoSupport[operations.size()]);
	}

	@Override
	public Object getAttribute(String attribute) 
	throws AttributeNotFoundException, MBeanException, ReflectionException
	{
		if(attribute == null)
			throw new AttributeNotFoundException("<null> attribute name not allowed.");
		
		if( "locationUri".equals(attribute) )
			getLocationUri().toString();
		return super.getAttribute(attribute);
	}

	@Override
	public void setAttribute(Attribute attribute) 
	throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException
	{
		if(attribute == null || attribute.getName() == null)
			throw new AttributeNotFoundException("<null> attribute name not allowed.");
		
		super.setAttribute(attribute);
	}

	@Override
	public Object invoke(String actionName, Object[] params, String[] signature) 
	throws MBeanException, ReflectionException
	{
		if("resetOperationCounters".equals(actionName))
		{
			Long lastResetDate = resetOperationCounters();
			return getDateFormat().format(lastResetDate);
		}
		
		return super.invoke(actionName, params, signature);
	}
	
	
	// ====================================================================================================
	// CacheStructureChangeListener Realization
	// ====================================================================================================
	private List<CacheStructureChangeListener> changeListenerList = new ArrayList<CacheStructureChangeListener>();
	
	@Override
	public void registerCacheStructureChangeListener(CacheStructureChangeListener listener)
	{
		changeListenerList.add(listener);
	}

	@Override
	public void unregisterCacheStructureChangeListener(CacheStructureChangeListener listener)
	{
		changeListenerList.remove(listener);		
	}

	protected void notifyListenersOfStructureChange()
	{
		synchronized(changeListenerList)
		{
			for(CacheStructureChangeListener listener : changeListenerList)
				listener.cacheStructureChanged(this);
		}
	}
	
	protected void notifyListenersOfNewRegion(Region region)
	{
		synchronized(changeListenerList)
		{
			for(CacheStructureChangeListener listener : changeListenerList)
				listener.regionAdded(this, region);
		}
	}
	
	protected void notifyListenersOfNewEvictionStrategy(EvictionStrategy evictionStrategy)
	{
		synchronized(changeListenerList)
		{
			for(CacheStructureChangeListener listener : changeListenerList)
				listener.evictionStrategyAdded(this, evictionStrategy);
		}
	}
	
	protected void notifyListenersOfRemovedRegion(Region region)
	{
		synchronized(changeListenerList)
		{
			for(CacheStructureChangeListener listener : changeListenerList)
				listener.regionRemoved(this, region);
		}
	}
	
	protected void notifyListenersOfRemovedEvictionStrategy(EvictionStrategy evictionStrategy)
	{
		synchronized(changeListenerList)
		{
			for(CacheStructureChangeListener listener : changeListenerList)
				listener.evictionStrategyRemoved(this, evictionStrategy);
		}
	}
}
