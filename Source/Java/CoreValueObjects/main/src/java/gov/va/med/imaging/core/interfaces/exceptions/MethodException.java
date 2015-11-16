package gov.va.med.imaging.core.interfaces.exceptions;

/**
 * The parent class of all exceptions thrown by DataSourceServices
 * when doing business method calls.  In general this class
 * should only be created directly if the source of the error
 * cannot be differentiated with respect to local and remote applications.
 * Preferentially use MethodRemoteException and MethodLocalException or their
 * derivations.
 * 
 * @author VHAISWBECKEC
 *
 */
public class MethodException
extends Exception
{
	private static final long serialVersionUID = 1L;

	public MethodException()
    {
	    super();
    }

	public MethodException(String message, Throwable cause)
    {
	    super(message, cause);
    }

	public MethodException(String message)
    {
	    super(message);
    }

	public MethodException(Throwable cause)
    {
	    super(cause);
    }

}
