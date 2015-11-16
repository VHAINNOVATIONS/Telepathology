package gov.va.med.imaging.storage.cache.exceptions;

public class InvalidGroupNameException 
extends InvalidNameException
{
	private static final long serialVersionUID = 4918547795121448600L;

	public InvalidGroupNameException()
	{
	}

	public InvalidGroupNameException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public InvalidGroupNameException(String message)
	{
		super(message);
	}

	public InvalidGroupNameException(Throwable cause)
	{
		super(cause);
	}

}
