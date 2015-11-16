package gov.va.med.imaging.storage.cache.exceptions;

import gov.va.med.imaging.storage.cache.Messages;

public class GroupDoesNotExistException 
extends CacheException
{
	private static final long serialVersionUID = 6085338776034331995L;

	private static String makeMessage(String regionName, String group)
	{
		return Messages.getString("GroupDoesNotExistException.0") + group + 
			Messages.getString("GroupDoesNotExistException.1") + regionName + 
			Messages.getString("GroupDoesNotExistException.2"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	public GroupDoesNotExistException(String regionName, String group)
	{
		super(makeMessage(regionName, group));
	}
	
	public GroupDoesNotExistException(String regionName, String group, Throwable cause)
	{
		super(makeMessage(regionName, group), cause);
	}
	
	public GroupDoesNotExistException()
	{
	}

	public GroupDoesNotExistException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public GroupDoesNotExistException(String message)
	{
		super(message);
	}

	public GroupDoesNotExistException(Throwable cause)
	{
		super(cause);
	}

}
