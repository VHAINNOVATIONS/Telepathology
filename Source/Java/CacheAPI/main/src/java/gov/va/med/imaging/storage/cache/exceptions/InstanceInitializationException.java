package gov.va.med.imaging.storage.cache.exceptions;

import java.io.File;

public class InstanceInitializationException 
extends CacheException
{
	private static final long serialVersionUID = 932548495854043369L;

	private static String makeMessage(File instanceFile)
	{
		return instanceFile == null ?
				"Failed to create Instance file, file is null" :
				("Failed to create Instance file (" + instanceFile.getAbsolutePath() + ")");
	}

	public InstanceInitializationException()
	{
	}

	public InstanceInitializationException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public InstanceInitializationException(String message)
	{
		super(message);
	}

	public InstanceInitializationException(Throwable cause)
	{
		super(cause);
	}

	public InstanceInitializationException(File instanceFile)
	{
		this(makeMessage(instanceFile));
	}

}
