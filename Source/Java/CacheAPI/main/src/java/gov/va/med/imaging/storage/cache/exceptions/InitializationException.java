package gov.va.med.imaging.storage.cache.exceptions;

public class InitializationException 
extends CacheException
{
	private static final long serialVersionUID = 6855933654971585515L;

	public InitializationException()
	{
	}

	public InitializationException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public InitializationException(String message)
	{
		super(message);
	}

	public InitializationException(Throwable cause)
	{
		super(cause);
	}

}
