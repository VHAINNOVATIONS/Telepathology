/**
 * 
 */
package gov.va.med.imaging.storage.cache.exceptions;

/**
 * A subclass of CacheException for exceptions that may be transient.
 * When an exception of this type is thrown the client may reasonably retry the operation
 * with some expectation of success.
 * By implication, cache exceptions that are not derived from this class should not be
 * retriead as the result will be the same.
 * 
 * @author VHAISWBECKEC
 *
 */
public class TransientCacheException 
extends CacheException
{
	private static final long serialVersionUID = -1661188055942877780L;

	/**
	 * 
	 */
	public TransientCacheException()
	{
	}

	/**
	 * @param message
	 * @param cause
	 */
	public TransientCacheException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public TransientCacheException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public TransientCacheException(Throwable cause)
	{
		super(cause);
	}

}
