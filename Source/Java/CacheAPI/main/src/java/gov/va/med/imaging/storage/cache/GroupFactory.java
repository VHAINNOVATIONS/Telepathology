/**
 * 
 */
package gov.va.med.imaging.storage.cache;

import gov.va.med.imaging.storage.cache.Group;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;

/**
 * @author VHAISWBECKEC
 *
 */
public abstract class GroupFactory
extends MutableNamedObjectFactory<Group>
{
	@Override
	public abstract Group create(String name)
	throws CacheException;
}