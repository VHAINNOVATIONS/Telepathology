package gov.va.med.imaging.storage.cache;

import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import gov.va.med.imaging.storage.cache.exceptions.CacheStateException;
import gov.va.med.imaging.storage.cache.exceptions.RegionInitializationException;
import gov.va.med.imaging.storage.cache.memento.CacheMemento;
import gov.va.med.imaging.storage.cache.memento.RegionMemento;

import java.net.URI;
import java.util.Collection;

/**
 * The encapsulating interface of the metadata and image cache.
 * The general contract is thus:
 *  A START event must be received before any cache access methods are called.
 *  getOrCreateInstance() will either succeed, returning an Instance instance, or will throw an exception.
 *  getInstance() will return either a an Instance instance or null if the Instance was not found.  
 *  getInstance() throws exceptions in error conditions only, returns null to indicate that an Instance does not exist.
 *  A STOP event should be received to properly shutdown the cache
 *  No acche access methods should be called after a STOP event is received 
 *  
 * READ THIS !!! =============================================================================
 * Cache realizations must also implement the following factory methods:
 * 1.) public static Cache create(String name, URI locationUri)
 *     throws CacheException
 * 2.) public static Cache create(CacheMemento memento)
 *     throws CacheException
 * READ THIS !!! =============================================================================
 */
public interface Cache
extends CacheLifecycleListener
{
	
	public abstract String getName();
	
	/**
	 * The protocol used to communicate with the persistence mechanism and the root of the persistence hierarchy.
	 * The locationUri has the persistence root contaqined within it.
	 * The major difference, and the reason that both accessor methods are in this interface, is that the
	 * location URI is what is passed to the constructor of the cache and the getLocationUri must
	 * return the exact URI passed in.  The persistence root is parsed out of that.
	 * 
	 * @return
	 */
	public abstract URI getLocationUri();
	
	/**
	 * A convenience method to pull the path from the URI
	 * 
	 * @return
	 */
	public abstract String getLocationPath();
	
	/**
	 * A convenience method to get the protocol (scheme) from the URI 
	 * @return
	 */
	public abstract String getLocationProtocol();
	
	/**
	 * Initialization provides the Region the opportunity to 
	 * either initialization itself, or if it throws an exception,
	 * to halt the initialization of the entire cache.
	 * 
	 * @return
	 */
	public abstract Boolean isInitialized();
	public abstract void setInitialized(Boolean initialized)
	throws CacheException;
	
	/**
	 * Set and get the enabled state of the cache.
	 * A disabled cache should return null for any getInstance() or getOrCreateInstance() method.
	 * 
	 * @return
	 */
	public abstract Boolean isEnabled();

	public void setEnabled(Boolean enabled) 
	throws CacheException;
	
	// ===================================================================================================================
	// Cache Instances
	// ===================================================================================================================
	
	/**
	 * <p>
	 * Get an existing instance from the cache if it exists or create a new Instance in this Cache instance.
	 * This method must throw a CacheStateException if it is called
	 * before the START event has been received or after the STOP event 
	 * has been received.
	 * </p>
	 * 
	 * 
	 * @param regionName
	 * @param group
	 * @param key
	 * @return
	 * @throws CacheException 
	 */
	public gov.va.med.imaging.storage.cache.Instance getOrCreateInstance(
		String regionName,
		String[] group, 
		String key)
	throws CacheException;

	/**
	 * <p>
	 * Get an existing Instance in the Cache instance.
	 * This method must throw a CacheStateException if it is called
	 * before the START event has been received or after the STOP event 
	 * has been received.
	 * </p>
	 * 
	 * 
	 * @param regionName
	 * @param group
	 * @param key
	 * @return
	 * @throws CacheException 
	 */
	public gov.va.med.imaging.storage.cache.Instance getInstance(
		String regionName,
		String[] group, 
		String key) 
	throws CacheException;

	/**
	 * <p>
	 * Delete an existing Instance in the Cache instance.
	 * This method must throw a CacheStateException if it is called
	 * before the START event has been received or after the STOP event 
	 * has been received.
	 * If "forceDelete" is set then delete an existing Group in the Cache instance regardless
	 * of whether it is being accessed by other threads.  Other threads will get
	 * an IO exception if they have channels open.
	 * </p>
	 * 
	 * @param regionName
	 * @param group
	 * @param key
	 * @throws CacheException
	 */
	public void deleteInstance(
		String regionName,
		String[] group, 
		String key,
		boolean forceDelete) 
	throws CacheException;

	/**
	 * <p>
	 * Get an existing group from the cache if it exists or create a new Group in this Cache instance.
	 * This method must throw a CacheStateException if it is called
	 * before the START event has been received or after the STOP event 
	 * has been received.
	 * </p>
	 * 
	 * 
	 * @param regionName
	 * @param group
	 * @return
	 * @throws CacheException 
	 */
	public gov.va.med.imaging.storage.cache.Group getOrCreateGroup(String regionName, String[] group)
	throws CacheException;
	
	/**
	 * <p>
	 * Get an existing Group in the Cache instance.
	 * This method must throw a CacheStateException if it is called
	 * before the START event has been received or after the STOP event 
	 * has been received.
	 * </p>
	 * 
	 * 
	 * @param regionName
	 * @param group
	 * @param key
	 * @return
	 * @throws CacheException 
	 */
	public abstract Group getGroup(String regionName, String[] groupsName) 
	throws CacheException;
	
	/**
	 * <p>
	 * Delete an existing group in the Cache instance.
	 * This method must throw a CacheStateException if it is called
	 * before the START event has been received or after the STOP event 
	 * has been received.
	 * If "forceDelete" is set then delete an existing Group in the Cache instance regardless
	 * of whether it is being accessed by other threads.  Other threads will get
	 * an IO exception if they have channels open.
	 * </p>
	 * 
	 * @param regionName
	 * @param group
	 * @throws CacheException
	 */
	public void deleteGroup(
		String regionName,
		String[] group,
		boolean forceDelete) 
	throws CacheException;

	/**
	 * Completely clear the cache. The regions, eviction strategies stay but the content is deleted.
	 * Note that there may be some instances left in the cache if they were being accessed when the
	 * clear() was called.
	 * 
	 * @throws CacheException
	 */
	public abstract void clear() 
	throws CacheException;
	
	
	// ===================================================================================================================
	// Region Management
	// ===================================================================================================================

	/**
	 * Create a Region compatible with the Cache realization. 
	 * 
	 * @param name
	 * @return
	 */
	public Region createRegion(String name, String[] evictionStrategyNames)
	throws RegionInitializationException;
	
	/**
	 * Create a Region compatible with the Cache realization and configured with the
	 * given memento.
	 * 
	 * @param name
	 * @return
	 */
	public Region createRegion(RegionMemento regionMemento)
	throws RegionInitializationException;
	
	public abstract void addRegion(Region region)
	throws CacheException;
	
	public abstract void addRegions(Collection<? extends Region> regions)
	throws CacheException;
	
	/**
	 * Get a collection of the regions managed by this cache.
	 * The collection may be (and should be) unmodifiable.
	 *  
	 * @return
	 */
	public abstract Collection<? extends Region> getRegions();
	
	/**
	 * @param string
	 * @return
	 */
	public abstract Region getRegion(String string);

	// ===================================================================================================================
	// Eviction Timer Management
	// ===================================================================================================================
	/**
	 * Return a reference to the eviction timer
	 * 
	 * @param name
	 * @return
	 */
	public abstract EvictionTimer getEvictionTimer();
	
	// ===================================================================================================================
	// Eviction Strategy Management
	// ===================================================================================================================
	
	/**
	 * Add an eviction strategy to those known by the cache
	 * @return
	 */
	public abstract void addEvictionStrategy(EvictionStrategy evictionStrategy)
	throws CacheStateException;
	
	
	/**
	 * Add a list of eviction strategies to those known by the cache
	 * @return
	 */
	public abstract void addEvictionStrategies(Collection<? extends EvictionStrategy> evictionStrategies)
	throws CacheStateException;
	
	/**
	 * Get a collection of all of the eviction strategies known to this cache
	 * @return
	 */
	public abstract Collection<? extends EvictionStrategy> getEvictionStrategies();
	
	/**
	 * Return a reference to a registered eviction strategy.
	 * 
	 * @param name
	 * @return
	 */
	public abstract EvictionStrategy getEvictionStrategy(String name);
	
	// ===================================================================================================================
	// Byte Channel Factory Management
	// ===================================================================================================================
	
	/**
	 * The cache has a InstanceByteChannelFactory instance associated to it which
	 * is used to create and monitor byte channels.
	 * 
	 * @return
	 */
	public abstract InstanceByteChannelFactory<?> getInstanceByteChannelFactory();
	
	// ===================================================================================================================
	// Persistent State Management
	// ===================================================================================================================
	
	/**
	 * 
	 * @return
	 */
	public abstract CacheMemento createMemento();

	// ===================================================================================================================
	// Structure Change Listeners, notified when regions or eviction strategies are added or removed
	// ===================================================================================================================
	public void registerCacheStructureChangeListener(CacheStructureChangeListener listener);
	public void unregisterCacheStructureChangeListener(CacheStructureChangeListener listener);
}
