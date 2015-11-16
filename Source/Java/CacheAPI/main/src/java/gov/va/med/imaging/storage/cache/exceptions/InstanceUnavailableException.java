package gov.va.med.imaging.storage.cache.exceptions;

/**
 * An instance exists in the cache but the persistent image is inaccessible.  The operation that resulted in this exception 
 * may be retried, though the existence of the persistent instance image may not be relied on.
 * This error will only occur in really odd circumstances, like when the eviction thread has deleted
 * an Instance after another thread has a reference to it or when a writing thread calls error() and removes the
 * persistent image.
 * 
 * @see gov.va.med.imaging.storage.cache.exceptions.InstanceInaccessibleException 
 * 
 * @author VHAISWBECKEC
 *
 */
public class InstanceUnavailableException 
extends TransientCacheException
{
	private static final long serialVersionUID = 3239977796584018148L;

	public final static String defaultRetryMessage =
		"The operation that resulted in this exception may be retried, though the existence of the persistent instance image may not be relied on.";
	
	public final static String defaultCauseMessage =
		"This error should only occur in really odd circumstances, like when the eviction thread has deleted \n" +
		"an Instance after another thread has a reference to it or when a writing thread calls error() and removes the \n" +
		"persistent image.";

	public final static String defaultMessage = 
		"An instance exists in the cache but the persistent image is unavailable.\n" + defaultRetryMessage + "\n" + defaultCauseMessage;
	
	public final static String defaultFileDoesNotExistMessage = 
		"An instance exists in the cache but the persistent image (file) does not exist.\n" + defaultRetryMessage + "\n" + defaultCauseMessage;
	
	public final static String defaultInstanceInvalidMessage = 
		"An instance exists in the cache but it has been marked invalid, probably due to an error while writing.\n" + defaultRetryMessage + "\n" + defaultCauseMessage;
	
	public InstanceUnavailableException()
	{
		super(defaultMessage);
	}

	public InstanceUnavailableException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public InstanceUnavailableException(String instanceIdentifier)
	{
		super("Instance '" + instanceIdentifier + "'-" + defaultMessage);
	}

	public InstanceUnavailableException(Throwable cause)
	{
		super(defaultMessage, cause);
	}

	/**
	 * Using this constructor allows the creator to indicate if the
	 * image exists in persistent storage or not.
	 * 
	 * @param persistentImageExists 
	 * - if true then the cause is an instance marked invalid
	 * - if false then the cause is an image file does not exist
	 */
	public InstanceUnavailableException(boolean persistentImageExists)
	{
		super(persistentImageExists ? defaultInstanceInvalidMessage : defaultFileDoesNotExistMessage);
	}
	
	public InstanceUnavailableException(String instanceIdentifier, boolean persistentImageExists)
	{
		super( "Instance '" + instanceIdentifier + "'-" + 
			(persistentImageExists ? defaultInstanceInvalidMessage : defaultFileDoesNotExistMessage) 
		);
	}
}
