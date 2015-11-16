package gov.va.med.imaging.storage.cache.exceptions;

/**
 * InstanceInaccessibleException is thrown when an Instance exists in cache but is not available for access. 
 * This probably means that a writer has not closed the channel yet and we have either time out waiting for it or
 * have been interrupted while waiting for it.
 * 
 * @see gov.va.med.imaging.storage.cache.exceptions.InstanceUnavailableException
 * 
 * @author VHAISWBECKEC
 * 
 * This class needs to be renamed to, maybe, InstanceWritableChannelOpenException
 *
 */
public class InstanceInaccessibleException 
extends TransientCacheException
{
	private static final long serialVersionUID = -1859055142704500911L;
	public final static String defaultMessage = 
		"InstanceInaccessibleException is thrown when an Instance exists in cache but is not available for access. \n" +  
		"This probably means that a writer has not closed the channel yet and we have either time out waiting for it or \n" +
		"have been interrupted while waiting for it.";

	public InstanceInaccessibleException(String regionName, String[] groups, String instance)
	{
		super(createInstanceMessage(regionName, groups, instance));
	}
	
	public InstanceInaccessibleException(String regionName, String[] groups, String instance, Throwable cause)
	{
		super(createInstanceMessage(regionName, groups, instance), cause);
	}
	
	public InstanceInaccessibleException()
	{
		super(defaultMessage);
	}

	public InstanceInaccessibleException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public InstanceInaccessibleException(String message)
	{
		super(message + " - " + defaultMessage);
	}

	public InstanceInaccessibleException(Throwable cause)
	{
		super(cause.getMessage());
	}

	/**
	 * @param name
	 * @param l
	 */
	public InstanceInaccessibleException(String name, long waitTime, int secondsToWait)
	{
		super(name + "-" + defaultMessage + " Waited for " + waitTime + " milliseconds.  Instance is configured to wait " + secondsToWait + " seconds.");
	}

}
