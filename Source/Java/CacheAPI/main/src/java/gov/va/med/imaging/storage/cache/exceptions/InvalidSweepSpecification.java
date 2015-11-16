package gov.va.med.imaging.storage.cache.exceptions;


public class InvalidSweepSpecification 
extends CacheException
{
	private static final long serialVersionUID = 1831500884291930355L;

	public InvalidSweepSpecification()
	{
	}

	public InvalidSweepSpecification(String message, Throwable cause)
	{
		super(message, cause);
	}

	public InvalidSweepSpecification(String message)
	{
		super(message);
	}

	public InvalidSweepSpecification(Throwable cause)
	{
		super(cause);
	}

}
