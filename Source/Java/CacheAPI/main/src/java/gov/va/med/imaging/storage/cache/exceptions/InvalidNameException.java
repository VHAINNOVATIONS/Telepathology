package gov.va.med.imaging.storage.cache.exceptions;

public abstract class InvalidNameException 
extends CacheException
{
	public InvalidNameException()
	{
	}

	public InvalidNameException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public InvalidNameException(String message)
	{
		super(message);
	}

	public InvalidNameException(Throwable cause)
	{
		super(cause);
	}

}
