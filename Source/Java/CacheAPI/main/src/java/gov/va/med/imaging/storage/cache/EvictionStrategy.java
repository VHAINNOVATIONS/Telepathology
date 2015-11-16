package gov.va.med.imaging.storage.cache;

import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import gov.va.med.imaging.storage.cache.memento.EvictionStrategyMemento;

/**
 * 
 * 
 */
public interface EvictionStrategy
{
	/**
	 * <p>
	 * Does ...
	 * </p>
	 * 
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * <p>
	 * Does ...
	 * </p>
	 * 
	 * 
	 * @param region
	 */
	public void addRegion(gov.va.med.imaging.storage.cache.Region region);

	/**
	 * <p>
	 * Does ...
	 * </p>
	 * 
	 * 
	 * @param region
	 */
	public void removeRegion(gov.va.med.imaging.storage.cache.Region region);
	
	/**
	 * Return a String array of the managed region names.
	 * This is intended for monitoring only.
	 */
	public abstract String[] getRegionNames();
	
	/**
	 * Once initialized an EvictionStrategy should never be uninitialized.
	 * An exception throw in setInitialized() will result in an initialization failure
	 * of the enclosing Cache.
	 */
	public abstract boolean isInitialized();
	
	public abstract void setInitialized(boolean initialized)
	throws CacheException;
	
	/**
	 * Create and return a serializable representation of the state of the EvictionStrategy.
	 * The instance returned here must be usable in a static create() method in the EvictionStrategy
	 * such that the newly created EvictionStrategy is functionally identical to the source of the
	 * EvictionStrategyMemento.
	 * The EvictionStrategyMemento does NOT include the managed Regions.
	 * @return
	 */
	public EvictionStrategyMemento createMemento();
}
