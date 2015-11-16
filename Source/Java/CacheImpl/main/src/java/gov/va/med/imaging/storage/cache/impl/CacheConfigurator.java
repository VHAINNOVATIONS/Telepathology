/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl;

import java.util.Map;

/**
 * @author VHAISWBECKEC
 *
 */
public interface CacheConfigurator
{

	/**
	 * The eviction timer is always initialized the same.
	 * 
	 * @return
	 */
	public abstract Map<Long, String> getEvictionTimerSweepIntervalMap();

}