/**
 * 
 */
package gov.va.med.imaging.storage.cache;

import gov.va.med.imaging.storage.cache.Cache;
import gov.va.med.imaging.storage.cache.EvictionStrategy;
import gov.va.med.imaging.storage.cache.Region;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import gov.va.med.imaging.storage.cache.memento.EvictionStrategyMemento;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.management.MBeanException;

/**
 * @author VHAISWBECKEC
 *
 */
public interface CacheManager
extends javax.naming.Referenceable
{

	public abstract Iterable<Cache> getKnownCaches();

	public abstract Cache createCache(String name, URI locationUri, String prototypeName) throws MBeanException, CacheException, URISyntaxException,
			IOException;

	public abstract Cache getCache(String cacheName) throws FileNotFoundException, IOException, MBeanException, CacheException;

	// ============================================================================================	
	// Basic Cache management methods made available here so that tests can get a running cache
	// ============================================================================================
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.filesystem.CacheManager#initialize()
	 */
	public abstract String initialize(Cache cache);

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.filesystem.CacheManager#enable()
	 */
	public abstract String enable(Cache cache);

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.filesystem.CacheManager#disable()
	 */
	public abstract String disable(Cache cache);

	public abstract void store(Cache cache) throws IOException;

	public abstract void storeAll() throws IOException;

	public abstract void delete(Cache cache);

	/**
	 * Create an EvictionStrategy in the given Cache instance using the type of the
	 * given EvictionStrategyMemento to determine the EvictionStrategy and 
	 * the properties in the given EvictionStrategyMemento to initialize the created
	 * EvictionStrategy.  Add the eviction strategy to the given cache.
	 * 
	 * @param cache
	 * @param memento
	 * @return
	 * @throws CacheException
	 */
	public abstract EvictionStrategy createEvictionStrategy(Cache cache, EvictionStrategyMemento memento) throws CacheException;

	public abstract Region createRegion(Cache cache, String regionName, String[] evictionStrategyNames) throws CacheException;

	/**
	 * Returns true if this instance has received a server start event and has not 
	 * received a server stop event.
	 * @return
	 */
	boolean isServerRunning();
}