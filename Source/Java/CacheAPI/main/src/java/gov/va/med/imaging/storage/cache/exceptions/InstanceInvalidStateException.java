package gov.va.med.imaging.storage.cache.exceptions;


public class InstanceInvalidStateException 
extends CacheException
{
	private static final long serialVersionUID = -5934852341471546273L;

	public InstanceInvalidStateException()
	{
		super();
	}

	public InstanceInvalidStateException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public InstanceInvalidStateException(String message)
	{
		super(message);
	}

	public InstanceInvalidStateException(Throwable cause)
	{
		super(cause);
	}

}
