package gov.va.med.imaging.storage.cache.exceptions;

/**
 * Thrown when a cache operation was attempted before the cache
 * was started (received START event) or after it was stopped (received
 * STOP event).
 * 
 * @author VHAISWBECKEC
 *
 */
public class CacheStateException 
extends CacheException
{
	private static final long serialVersionUID = 2691389570871147052L;
	public final static String defaultMessage = 
		"An operation on the cache has been requested and the cache is not in a state to execut it." + 
		"Usually this means that the cache has not been initialized or is shutting down.";
	

	public CacheStateException()
	{
		super(defaultMessage);
	}

	public CacheStateException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public CacheStateException(String message)
	{
		super(message);
	}

	public CacheStateException(Throwable cause)
	{
		super(defaultMessage, cause);
	}

}
