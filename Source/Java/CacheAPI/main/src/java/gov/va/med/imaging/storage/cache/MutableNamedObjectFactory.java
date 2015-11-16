/**
 * 
 */
package gov.va.med.imaging.storage.cache;

import gov.va.med.imaging.storage.cache.exceptions.CacheException;

/**
 * @author VHAISWBECKEC
 *
 */
public abstract class MutableNamedObjectFactory<T extends MutableNamedObject>
{
	public abstract T create(String name)
	throws CacheException;
}
