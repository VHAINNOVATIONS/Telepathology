package gov.va.med.imaging.storage.cache.exceptions;

public class SimultaneousWriteException 
extends CacheException
{
	private static final long serialVersionUID = -6297213885922714932L;
	public final static String defaultMessage = 
		"Two cache threads are attempting to open the same cache instance for writing." + 
		"This thread should cease its source read and open the cache copy for read." + 
		"The open operation on the read will be properly synchronized by the cache.";
	
	public SimultaneousWriteException()
	{
		super(defaultMessage);
	}

	public SimultaneousWriteException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public SimultaneousWriteException(String instanceName)
	{
		super(instanceName + defaultMessage);
	}

	public SimultaneousWriteException(Throwable cause)
	{
		super(defaultMessage, cause);
	}

}
