/**
 * 
 */
package gov.va.med.imaging.storage.cache.exceptions;

/**
 * @author VHAISWBECKEC
 *
 */
public class CacheInternalException 
extends CacheException
{
	private static final long serialVersionUID = 5288810329586910974L;
	
	public final static String defaultMessage = "A CacheInternalException indicates an implementation problem.";
	/**
	 * 
	 */
	public CacheInternalException()
	{
		this(defaultMessage);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CacheInternalException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public CacheInternalException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public CacheInternalException(Throwable cause)
	{
		super(defaultMessage, cause);
	}

}
