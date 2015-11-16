package gov.va.med.imaging.storage.cache.exceptions;

public abstract class CacheException 
extends Exception
{
	public final static String defaultMessage = 
		"A generic cache exception has occured." + 
		"Actually this message should never be seen, something more specific (and useful) should take its place";

	protected static String createInstanceMessage(String regionName, String[] groups, String instance)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append( regionName + ":" );
		for(String group:groups)
			sb.append(group + ".");
		sb.append(instance);
		
		return sb.toString();
	}
	
	
	public CacheException()
	{
		super(defaultMessage);
	}

	public CacheException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public CacheException(String message)
	{
		super(message);
	}

	public CacheException(Throwable cause)
	{
		super(defaultMessage, cause);
	}
}
