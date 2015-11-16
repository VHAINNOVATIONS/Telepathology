package gov.va.med.imaging.storage.cache.exceptions;

public class UnknownEvictionStrategyException 
extends CacheInitializationException
{
	private static final long serialVersionUID = -6157420574441364703L;

	public UnknownEvictionStrategyException()
	{
	}

	public UnknownEvictionStrategyException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public UnknownEvictionStrategyException(String message)
	{
		super(message);
	}

	public UnknownEvictionStrategyException(Throwable cause)
	{
		super(cause);
	}

}
