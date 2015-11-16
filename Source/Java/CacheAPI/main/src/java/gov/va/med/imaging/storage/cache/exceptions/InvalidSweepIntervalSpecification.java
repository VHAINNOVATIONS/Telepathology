package gov.va.med.imaging.storage.cache.exceptions;

public class InvalidSweepIntervalSpecification 
extends InvalidSweepSpecification
{
	private static final long serialVersionUID = 7209897062789985369L;

	public InvalidSweepIntervalSpecification()
	{
	}

	public InvalidSweepIntervalSpecification(String message, Throwable cause)
	{
		super(message, cause);
	}

	public InvalidSweepIntervalSpecification(String message)
	{
		super(message);
	}

	public InvalidSweepIntervalSpecification(Throwable cause)
	{
		super(cause);
	}

}
