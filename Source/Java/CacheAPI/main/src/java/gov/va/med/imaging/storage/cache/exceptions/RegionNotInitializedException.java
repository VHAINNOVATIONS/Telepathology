/**
 * 
 */
package gov.va.med.imaging.storage.cache.exceptions;

/**
 * @author VHAISWBECKEC
 * Thrown when a method is called on a Region that requires that the Region
 * be initialized.  The implementations of Region may throw this on most any
 * method. 
 */
public class RegionNotInitializedException 
extends CacheStateException
{
	private static final long serialVersionUID = -6223321558753527355L;

	/**
	 * 
	 */
	public RegionNotInitializedException()
	{
	}

	/**
	 * @param message
	 * @param cause
	 */
	public RegionNotInitializedException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public RegionNotInitializedException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public RegionNotInitializedException(Throwable cause)
	{
		super(cause);
	}

}
