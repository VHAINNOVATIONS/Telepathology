/**
 * 
 */
package gov.va.med.imaging.storage.cache;

import gov.va.med.imaging.storage.cache.exceptions.CacheException;

import java.util.HashSet;
import java.util.Set;

/**
 * @author VHAISWBECKEC
 *
 */
public class MutableNamedObjectSet<T extends MutableNamedObject>
extends HashSet<T>
implements Set<T>
{
	private static final long serialVersionUID = 1L;
	private final MutableNamedObjectFactory<T> factory;

	public MutableNamedObjectSet(MutableNamedObjectFactory<T> factory)
	{
		super();
		this.factory = factory;
	}

	public MutableNamedObjectFactory<T> getFactory()
	{
		return factory;
	}
	
	/**
	 * @param childGroupName
	 */
	public T getByName(String name)
	{
		if(name == null)
			return null;
		
		for(T mno : this)
			if( name.equals(mno.getName()) )
				return mno;
		
		return null;
	}

	/**
	 * 
	 * @param name
	 * @return
	 * @throws CacheException 
	 */
	public T getOrCreateByName(String name) 
	throws CacheException
	{
		T mno = getByName(name);
		if(mno == null)
		{
			mno = getFactory().create(name);
			this.add(mno);
		}
		
		return mno;
	}
}
