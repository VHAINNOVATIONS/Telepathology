/**
 * 
 */
package gov.va.med.imaging.storage.cache;

import gov.va.med.imaging.storage.cache.exceptions.CacheException;

/**
 * @author VHAISWBECKEC
 *
 */
public abstract class InstanceFactory
extends MutableNamedObjectFactory<Instance>
{
	@Override
	public abstract Instance create(String name)
	throws CacheException;
}
