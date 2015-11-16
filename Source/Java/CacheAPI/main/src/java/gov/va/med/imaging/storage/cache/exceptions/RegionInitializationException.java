package gov.va.med.imaging.storage.cache.exceptions;

import gov.va.med.imaging.storage.cache.memento.RegionMemento;

/**
 * @author VHAISWBECKEC
 *
 * An exception thrown during cache initialization.  If this exception occurs during
 * the startup sequence then it should be considered fatal, the cache will not be in an 
 * operable state after throwing this exception.  If this exception occurs when creating or
 * adding new regions using the management interface then the region
 * is not usable, will not be persisted, but the cache should keep running.
 */
public class RegionInitializationException 
extends InitializationException
{
	private static final long serialVersionUID = 1L;

	public RegionInitializationException()
	{
		super();
	}

	public RegionInitializationException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public RegionInitializationException(String message)
	{
		super(message);
	}

	public RegionInitializationException(Throwable cause)
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
	public RegionInitializationException(Class<? extends RegionMemento> actualMementoClass, Class<? extends RegionMemento> expectedMementoClass)
	{
		this("Unknown memento class '" + actualMementoClass.getName() + "', expected memento class '" + expectedMementoClass.getName() + "'.");
	}

}
