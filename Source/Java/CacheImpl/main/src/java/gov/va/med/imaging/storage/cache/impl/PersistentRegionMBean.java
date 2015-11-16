/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl;

import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import gov.va.med.imaging.storage.cache.exceptions.UnknownEvictionStrategyException;

/**
 * The MBean interface definition.  In general, since the PersistentRegion is an abstract class,
 * this interface is not direclty referenced but is derived from for Region implementations that derive from 
 * PersistentRegion.
 * 
 * @author VHAISWBECKEC
 *
 */
public interface PersistentRegionMBean
{
	/**
	 * Get the name of the Region.
	 * 
	 * @return
	 */
	public abstract String getName();

	/**
	 * Getting and setting the eviction strategy is done by name rather than by 
	 * reference to be consistent with setting parameters from the memento.
	 */
	public abstract String[] getEvictionStrategyNames();

	public abstract void setEvictionStrategyNames(String[] evictionStrategyNames) throws UnknownEvictionStrategyException;

	/**
	 * 
	 * @return
	 */
	public abstract int getSecondsReadWaitsForWriteCompletion();

	public abstract void setSecondsReadWaitsForWriteCompletion(int secondsReadWaitsForWriteCompletion);

	/**
	 * 
	 * @return
	 */
	public abstract boolean isSetModificationTimeOnRead();

	public abstract void setSetModificationTimeOnRead(boolean setModificationTimeOnRead);

	/**
	 * The default handling of the initialized flag simply sets and return the
	 * Boolean value.  Derived classes that need to do initialization should
	 * override these methods.
	 * Note that the initialized flag is a latch, once it becomes true it stays true.
	 */
	public abstract Boolean isInitialized();

	public abstract void setInitialized(Boolean initialized) throws CacheException;

}