package gov.va.med.imaging.core.interfaces.exceptions;

/**
 * The parent class of all exceptions thrown by DataSourceServices
 * when doing connections.
 * 
 * @author VHAISWBECKEC
 *
 */
public class ConnectionException 
extends Exception
{
	private static final long serialVersionUID = 1L;

	public ConnectionException()
	{
	}

	public ConnectionException(String message)
	{
		super(message);
	}

	public ConnectionException(Throwable cause)
	{
		super(cause);
	}

	public ConnectionException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
