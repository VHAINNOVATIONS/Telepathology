/**
 * 
 */
package gov.va.med.imaging.storage.cache.exceptions;

/**
 * @author VHAISWBECKEC
 *
 */
public class UnknownCacheException 
extends CacheInitializationException
{
	private static final long serialVersionUID = 9170802568798266942L;
	public final static String defaultMessage = "The given cache name is not known to the cache manager";

	public UnknownCacheException()
	{
		this(defaultMessage);
	}

	public UnknownCacheException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public UnknownCacheException(String message)
	{
		super(message);
	}

	public UnknownCacheException(Throwable cause)
	{
		this(defaultMessage, cause);
	}
	
	
}
