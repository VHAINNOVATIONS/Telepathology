package gov.va.med.imaging.storage.cache.exceptions;

import gov.va.med.imaging.storage.cache.Messages;

public class RegionDoesNotExistException 
extends CacheException
{
	private static final long serialVersionUID = 805476869215996877L;

	private static String makeMessage(String regionName)
	{
		return Messages.getString("RegionDoesNotExistException.0") + regionName + 
			Messages.getString("RegionDoesNotExistException.1"); 
	}
	
	public RegionDoesNotExistException(String regionName, boolean msgIsRegion)		// boolean msgIsRegion differentiates from msg
	{
		super(makeMessage(regionName));
	}
	
	public RegionDoesNotExistException(String regionName, Throwable cause, boolean msgIsRegion)
	{
		super(makeMessage(regionName), cause);
	}
	
	public RegionDoesNotExistException()
	{
		super();
	}

	public RegionDoesNotExistException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public RegionDoesNotExistException(String message)
	{
		super(message);
	}

	public RegionDoesNotExistException(Throwable cause)
	{
		super(cause);
	}

}
