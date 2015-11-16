/**
 * 
 */
package gov.va.med.imaging.storage.cache.exceptions;

/**
 * @author VHAISWBECKEC
 *
 * PersistenceException derived classes are wrapper classes around persistence mechanism specific
 * exceptions (e.g. IOException and its derivations).
 * In general, these are not recoverable errors and may indicate a problem with access to the
 * peristent storage (e.g. user does not have access to the file server).
 */
public abstract class PersistenceException 
extends CacheException
{

	/**
	 * 
	 */
	public PersistenceException()
	{
	}

	/**
	 * @param message
	 */
	public PersistenceException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public PersistenceException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public PersistenceException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
