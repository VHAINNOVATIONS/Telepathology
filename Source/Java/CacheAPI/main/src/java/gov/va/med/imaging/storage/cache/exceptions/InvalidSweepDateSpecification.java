package gov.va.med.imaging.storage.cache.exceptions;

public class InvalidSweepDateSpecification 
extends InvalidSweepSpecification
{
	private static final long serialVersionUID = 6513717738801693177L;

	public InvalidSweepDateSpecification()
	{
	}

	public InvalidSweepDateSpecification(String message, Throwable cause)
	{
		super(message, cause);
	}

	public InvalidSweepDateSpecification(String message)
	{
		super(message);
	}

	public InvalidSweepDateSpecification(Throwable cause)
	{
		super(cause);
	}

}
