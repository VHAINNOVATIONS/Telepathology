package gov.va.med.imaging.storage.cache.exceptions;

public class InvalidRegionNameException 
extends InvalidNameException
{
	private static final long serialVersionUID = 4918547795121448600L;
	public final static String defaultMessage = 
		"The given region name does not follow the region name specification (alpha-numeric, dashes and underscores only)";

	public InvalidRegionNameException()
	{
		super(defaultMessage);
	}

	public InvalidRegionNameException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public InvalidRegionNameException(String message)
	{
		super(message);
	}

	public InvalidRegionNameException(Throwable cause)
	{
		super(defaultMessage, cause);
	}

}
