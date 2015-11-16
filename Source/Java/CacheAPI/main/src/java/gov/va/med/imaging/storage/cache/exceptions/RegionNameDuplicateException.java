package gov.va.med.imaging.storage.cache.exceptions;


public class RegionNameDuplicateException 
extends CacheException 
{
	private static final long serialVersionUID = -4203477749210506357L;

	public RegionNameDuplicateException()
	{
	}

	public RegionNameDuplicateException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public RegionNameDuplicateException(String message)
	{
		super(message);
	}

	public RegionNameDuplicateException(Throwable cause)
	{
		super(cause);
	}

}
