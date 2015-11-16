/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl.eviction;

/**
 * The interface defining the properties available to the JMX Server.
 * 
 * @author VHAISWBECKEC
 *
 */
public interface LastAccessedEvictionStrategyMBean
extends SimpleEvictionStrategyMBean
{
	public abstract long getMaximumAge();
	public abstract int getEvictedLastSweep();
	public abstract String getLastRunDate();

}