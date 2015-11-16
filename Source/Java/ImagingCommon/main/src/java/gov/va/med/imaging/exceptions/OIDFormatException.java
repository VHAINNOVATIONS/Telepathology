/**
 * 
 */
package gov.va.med.imaging.exceptions;

/**
 * @author vhaiswbeckec
 *
 */
public class OIDFormatException
extends Exception
{
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public OIDFormatException()
	{
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public OIDFormatException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public OIDFormatException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public OIDFormatException(Throwable cause)
	{
		super(cause);
	}

}
