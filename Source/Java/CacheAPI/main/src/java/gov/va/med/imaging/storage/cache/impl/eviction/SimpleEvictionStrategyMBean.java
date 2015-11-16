/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl.eviction;

import gov.va.med.imaging.storage.cache.exceptions.CacheException;

/**
 * @author VHAISWBECKEC
 *
 */
public interface SimpleEvictionStrategyMBean
{

	/**
	 * 
	 * @return
	 */
	public abstract String[] getRegionNames();

	/**
	 * The default handling of the initialized flag simply sets and return the
	 * Boolean value.  Derived classes that need to do initialization should
	 * override these methods.
	 */
	public abstract boolean isInitialized();
	public abstract void setInitialized(boolean initialized) 
	throws CacheException;

}