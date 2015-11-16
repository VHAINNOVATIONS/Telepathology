/**
 * 
 */
package gov.va.med.imaging.core.interfaces.router.exceptions;

/**
 * @author vhaiswbeckec
 * 
 * The superclass of all exceptions thrown when instantiating a 
 * router (or facade-specific router).
 *
 */
public class RouterInstantiationException 
extends Exception 
{
	private static final long serialVersionUID = -542020510186693260L;

	/**
	 * 
	 */
	public RouterInstantiationException() 
	{
		super();
	}

	/**
	 * @param message
	 */
	public RouterInstantiationException(String message) 
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public RouterInstantiationException(Throwable cause) 
	{
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public RouterInstantiationException(String message, Throwable cause) 
	{
		super(message, cause);
	}

}
