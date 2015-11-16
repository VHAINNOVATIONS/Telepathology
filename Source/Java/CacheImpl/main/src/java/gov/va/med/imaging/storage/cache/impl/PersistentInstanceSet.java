/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl;

import gov.va.med.imaging.storage.cache.Instance;
import gov.va.med.imaging.storage.cache.InstanceByteChannelFactory;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;

import java.lang.ref.SoftReference;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * @author VHAISWBECKEC
 *
 * An abstract class that represents a Set of Instance instances in a cache implementation
 * that persistent stores cache data.  Group implementations have
 * sets of Instance instances.  
 * This class makes the management of those instances easier.
 *
 * Known Derivations:
 * @see gov.va.med.imaging.storage.cache.impl.filesystem.FileSystemInstanceSet
 * 
 * Derivations must implement the following methods:
 * 
 * protected abstract Instance getOrCreate(String name, boolean create) 
 * throws CacheException;
 * 
 * protected abstract void synchronizeChildren()
 * throws CacheException;
 */
public abstract class PersistentInstanceSet 
extends PersistentSet<Instance>
{
	private Logger log = Logger.getLogger(this.getClass());
	
	protected PersistentInstanceSet(
		InstanceByteChannelFactory byteChannelFactory, 
		int secondsReadWaitsForWriteCompletion, 
		boolean setModificationTimeOnRead)
	{
		super(byteChannelFactory, secondsReadWaitsForWriteCompletion, setModificationTimeOnRead);
	}

	protected Logger getLogger(){return this.log;}
	
	// =========================================================================================================
	// Abstract overrides of methods in the parent templated abstract class, PersistentSet.
	// Having these just makes the derived classes a bit more clear in their derivation.
	// These methods are not strictly necessary as they just override the templated methods in
	// the superclass.
	// =========================================================================================================
	
	@Override
	protected abstract Instance getOrCreate(String name, boolean create) 
	throws CacheException;
	
	@Override
	protected abstract void internalSynchronizeChildren()
	throws CacheException;
}
