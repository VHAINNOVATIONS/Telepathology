/**
 * 
 */
package gov.va.med.imaging.storage.cache.exceptions;

/**
 * @author vhaiswbeckec
 *
 */
public class StorageConnectionException 
extends CacheException
{
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public StorageConnectionException()
	{
	}

	/**
	 * @param message
	 * @param cause
	 */
	public StorageConnectionException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public StorageConnectionException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public StorageConnectionException(Throwable cause)
	{
		super(cause);
	}

}
