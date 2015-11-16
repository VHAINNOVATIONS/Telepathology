package gov.va.med.imaging.storage.cache.exceptions;

public class CacheInterruptedException 
extends CacheException
{
	private static final long serialVersionUID = 6734169682884263186L;
	public final static String defaultMessage = 
		"A cache thread was interrupted while waiting for another cache thread to finish a dependency operation." + 
		"The cache does not interrupt its own threads so this indicates an external interrupt, perhaps the server shutting down.";
	
	public CacheInterruptedException()
	{
		super(defaultMessage);
	}

	public CacheInterruptedException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public CacheInterruptedException(String message)
	{
		super(message);
	}

	public CacheInterruptedException(Throwable cause)
	{
		super(defaultMessage, cause);
	}

}
