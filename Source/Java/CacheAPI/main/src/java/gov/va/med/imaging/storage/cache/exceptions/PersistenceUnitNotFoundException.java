/**
 * 
 */
package gov.va.med.imaging.storage.cache.exceptions;


/**
 * @author VHAISWBECKEC
 *
 */
public class PersistenceUnitNotFoundException 
extends PersistenceException
{
	private static final long serialVersionUID = 5029899696894032027L;

	/**
	 * 
	 */
	public PersistenceUnitNotFoundException()
	{
	}

	/**
	 * @param message
	 */
	public PersistenceUnitNotFoundException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public PersistenceUnitNotFoundException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public PersistenceUnitNotFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
