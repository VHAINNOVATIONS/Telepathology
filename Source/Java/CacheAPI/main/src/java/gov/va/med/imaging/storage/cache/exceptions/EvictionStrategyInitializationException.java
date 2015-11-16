/**
 * 
 */
package gov.va.med.imaging.storage.cache.exceptions;

import gov.va.med.imaging.storage.cache.memento.EvictionStrategyMemento;

/**
 * @author VHAISWBECKEC
 *
 * An exception thrown during cache initialization.  If this exception occurs during
 * the startup sequence then it should be considered fatal, the cache will not be in an 
 * operable state after throwing this exception.  If this exception occurs when creating or
 * adding new eviction strategies using the management interface then the eviction strategy
 * is not usable, will not be persisted, but the cache should keep running.
 * 
 * Any method that takes a memento to initialize an object may throw an InitializationException
 * if the memento is the wrong type.
 */
public class EvictionStrategyInitializationException 
extends CacheException
{
	private static final long serialVersionUID = -4735250274450620302L;

	/**
	 * 
	 */
	public EvictionStrategyInitializationException()
	{
	}

	/**
	 * @param message
	 * @param cause
	 */
	public EvictionStrategyInitializationException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public EvictionStrategyInitializationException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public EvictionStrategyInitializationException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Throw an exception using this constructor when the given memento type is not one that the 
	 * specific derivation expects.
	 * 
	 * @param actualMementoClass
	 * @param expectedMementoClass
	 */
	public EvictionStrategyInitializationException(Class<? extends EvictionStrategyMemento> actualMementoClass, Class<? extends EvictionStrategyMemento> expectedMementoClass)
	{
		this("Unknown memento class '" + actualMementoClass.getName() + "', expected memento class '" + expectedMementoClass.getName() + "'.");
	}
}
