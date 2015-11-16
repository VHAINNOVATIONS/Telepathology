/**
 * 
 */
package gov.va.med.imaging.storage.cache.exceptions;

import gov.va.med.imaging.storage.cache.memento.CacheMemento;

/**
 * @author VHAISWBECKEC
 *
 * An exception thrown during cache initialization, these exceptions should be considered fatal, the
 * cache will not be in an operable state after throwing this exception.
 * The CacheInitializationException indicates a failure in initializing the cache itself, regions and 
 * eviction strategies should throw RegionInitializationException and EvictionStrtegyInitializationException
 * respectively, each of which are derivations of CacheException.
 */
public class CacheInitializationException 
extends InitializationException
{
	private static final long serialVersionUID = 8881701735937618207L;

	/**
	 * 
	 */
	public CacheInitializationException()
	{
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CacheInitializationException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public CacheInitializationException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public CacheInitializationException(Throwable cause)
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
	public CacheInitializationException(Class<? extends CacheMemento> actualMementoClass, Class<? extends CacheMemento> expectedMementoClass)
	{
		this("Unknown memento class '" + actualMementoClass.getName() + "', expected memento class '" + expectedMementoClass.getName() + "'.");
	}
}
