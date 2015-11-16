package gov.va.med.imaging.storage.cache.exceptions;


/**
 * This class indicates that a Region realization is not compatible with
 * a Cache realization.  This should only occur if a Region is created
 * outside of the Cache's createRegion() method.
 * 
 * @author VHAISWBECKEC
 *
 */
public class IncompatibleRegionException 
extends CacheException
{
	private static final long serialVersionUID = 681823486801830238L;

	public IncompatibleRegionException()
	{
		super();
	}

	public IncompatibleRegionException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public IncompatibleRegionException(String message)
	{
		super(message);
	}

	public IncompatibleRegionException(Throwable cause)
	{
		super(cause);
	}

	public IncompatibleRegionException(Class<?> cacheClass, Class<?> requiredRegionClass)
	{
		super(
			"Regions added to a cache implementation of type " + cacheClass.getName() + 
			"' must be of type " + requiredRegionClass.getName() + "'."
		);
		
	}
}
