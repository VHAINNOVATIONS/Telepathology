package gov.va.med.imaging.exceptions;

/**
 * The base class for any kind of imaging URN format exception.
 * 
 * @author VHAISWBECKEC
 *
 */
public class URNFormatException 
extends Exception
{
	private static final long serialVersionUID = 6271193731031546478L;

	public URNFormatException()
	{
		super();
	}

	public URNFormatException(String message)
	{
		super(message);
	}

	public URNFormatException(Throwable cause)
	{
		super(cause);
	}

	public URNFormatException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
