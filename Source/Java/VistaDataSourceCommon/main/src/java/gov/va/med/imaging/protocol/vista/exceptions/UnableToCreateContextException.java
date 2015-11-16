package gov.va.med.imaging.protocol.vista.exceptions;


/**
 * An internal exception that the VistaConnection or VistaStudyGraphDataSource
 * may throw but which must be trapped and retrapped before exiting the public interface.
 * 
 * @author VHAISWBECKEC
 *
 */
public class UnableToCreateContextException 
extends VistaConnectionException
{
	private static final long serialVersionUID = 1L;

	public UnableToCreateContextException()
	{
	}

	public UnableToCreateContextException(String message)
	{
		super(message);
	}

	public UnableToCreateContextException(Throwable cause)
	{
		super(cause);
	}

	public UnableToCreateContextException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
