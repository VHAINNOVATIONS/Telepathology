/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl;

import gov.va.med.imaging.storage.cache.EvictionJudge;
import gov.va.med.imaging.storage.cache.Group;
import gov.va.med.imaging.storage.cache.InstanceByteChannelFactory;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;

import java.lang.ref.SoftReference;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

/**
 * @author VHAISWBECKEC
 * 
 * An abstract class that represents a Set of Group instances in a cache implementation
 * that persistent stores cache data.  Both Group and Region implementations have
 * sets of Group instances.  This class makes the management of those instances easier.
 *
 * Known Derivations:
 * @see gov.va.med.imaging.storage.cache.impl.filesystem.FileSystemGroupSet
 */
public abstract class PersistentGroupSet
extends PersistentSet<Group>
{
	private static final long serialVersionUID = 1L;
	private Logger log = Logger.getLogger(this.getClass());

	// ============================================================================================================================================
	// Constructors
	// ============================================================================================================================================
	protected PersistentGroupSet(
		InstanceByteChannelFactory byteChannelFactory,
		int secondsReadWaitsForWriteCompletion,
		boolean setModificationTimeOnRead)
	{
		super(byteChannelFactory, secondsReadWaitsForWriteCompletion, setModificationTimeOnRead);
	}
	
	// =============================================================================================================================
	// The eviction group as determined by the given eviction judge
	// =============================================================================================================================
	/**
	 * Return the Set of Group instances that are evictable according to the
	 * given EvictionJudge.
	 * NOTE: this is not a recursive method.  The descendant groups, that may be
	 * evictable, are not included in this list.
	 * 
	 * @param judge
	 * @return
	 */
	public Set<? extends Group> internalEvictableGroups(EvictionJudge<Group> judge)
	{
		Set<Group> evictableGroups = new HashSet<Group>();
		
		for(SoftReference<? extends Group> childGroupRef : this)
		{
			Group childGroup = childGroupRef.get();
			
			// the child group may no longer be referenced
			if(childGroup != null)
			{
				try
				{
					if( judge.isEvictable(childGroup) )
					{
						log.info("Queueing group '" + childGroup.getName() + " for eviction.");
						evictableGroups.add(childGroup);
					}
				} 
				catch (CacheException e)
				{
					log.error("CacheException evaluating eviction criteria for group '" + childGroup.toString() + "', which may have to be manually deleted.", e);
				}
			}
		}
		
		return evictableGroups;
	}
}
