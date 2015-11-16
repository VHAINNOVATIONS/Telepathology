
package gov.va.med.imaging.storage.cache.impl;

import gov.va.med.imaging.storage.cache.EvictionJudge;
import gov.va.med.imaging.storage.cache.Group;
import gov.va.med.imaging.storage.cache.Instance;
import gov.va.med.imaging.storage.cache.InstanceByteChannelFactory;
import gov.va.med.imaging.storage.cache.events.GroupLifecycleEvent;
import gov.va.med.imaging.storage.cache.events.GroupLifecycleListener;
import gov.va.med.imaging.storage.cache.events.LifecycleEvent;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import gov.va.med.imaging.storage.cache.exceptions.RegionNotInitializedException;

import java.util.*;

import org.apache.log4j.Logger;

/**
 * An implementation of common Group methods that are not specific to the
 * backing persistence mechanism.
 * 
 */
public abstract class PersistentGroup 
extends RecursiveGroupAndInstanceParent
implements Group, Comparable<PersistentGroup>
{
	private Logger log = Logger.getLogger(this.getClass());
	private InstanceByteChannelFactory instanceFactoryChannel = null;
	
	// ==========================================================================================================================
	// Constructors 
	// ==========================================================================================================================
	protected PersistentGroup(InstanceByteChannelFactory instanceFactoryChannel)
	{
		this.instanceFactoryChannel = instanceFactoryChannel;
	}
	
	@Override
	public abstract String getName();
	
	// ==========================================================================================================================
	// 
	// ==========================================================================================================================
	
	public InstanceByteChannelFactory getInstanceFactoryChannel()
	{
		return this.instanceFactoryChannel;
	}
	
	/**
	 * A default implementation that will iterate through member Instance, get the last
	 * access time from each and return the max.
	 */
	@Override
	public Date getLastAccessed() 
	throws CacheException
	{
		Date max = new Date(0L);
		
		for( Iterator<? extends Instance> instanceIter=getInstances(); instanceIter.hasNext(); )
		{
			Instance instance = instanceIter.next();
			if(instance != null)
			{
				Date dateInstanceLastAccessed = instance.getLastAccessed(); 
				if( max.before(dateInstanceLastAccessed) )
					max = instance.getLastAccessed();
			}
		}
		return max;
	}

	/**
	 * A default implementation that will iterate through member Instance, get the 
	 * size from each and return the total.
	 */
	@Override
	public long getSize() 
	throws CacheException
	{
		long total = 0L;
		
		for( Iterator<? extends Instance> instanceIter=getInstances(); instanceIter.hasNext(); )
		{
			Instance instance = instanceIter.next();
			total += instance.getSize();
		}
		
		for( Iterator<? extends Group> groupIter=getGroups(); groupIter.hasNext(); )
		{
			Group group = groupIter.next();
			total += group.getSize();
		}
		
		return total;
	}

	protected abstract PersistentGroupSet getPersistentGroupSet()
	throws RegionNotInitializedException;
	
	/**
	 * @param minLastAccessMilli
	 * @throws CacheException 
	 */
	@Override
	public int evaluateAndEvictChildGroups(EvictionJudge<Group> judge) 
	throws CacheException
	{
		
		int totalEvictions = 0;
		
		// first recursively tell the progeny to evict their evictable children
		for(Iterator<Group> iter=getPersistentGroupSet().hardReferenceIterator(); iter.hasNext(); )
		{
			Group childGroup  = iter.next();
			if(childGroup == null)
				continue;
			
			try
			{
				totalEvictions += childGroup.evaluateAndEvictChildGroups(judge);
			}
			catch(ConcurrentModificationException cmX)
			{
				log.info("Concurrent modification exception occurred while evicting" + childGroup.getName() + ".  Some groups may not have been evicted but will be on subsequent sweeps.  Don't worry about it.");
			}
		}
		
		// now find the child groups of this Group that may be evicted
		Set<? extends Group> deadGroups = getPersistentGroupSet().evictableChildren(judge);
		
		if(deadGroups != null)
			for(Group deadGroup:deadGroups)
			{
				log.info("Group '" + deadGroup.getName() + " queued for eviction is being deleted.");
				getPersistentGroupSet().deleteChild(deadGroup, false);
				++totalEvictions;
			}
		
		return totalEvictions;
	}

	/**
	 * 
	 */
	@Override
	public int compareTo(PersistentGroup o)
	{
		return this.getName().compareTo(o.getName());
	}
	
	// ======================================================================================================
	// Listener Management
	// ======================================================================================================
	private List<GroupLifecycleListener> listeners = new ArrayList<GroupLifecycleListener>();
	@Override
	public void registerListener(GroupLifecycleListener listener)
	{
		listeners.add(listener);
	}
	
	@Override
	public void unregisterListener(GroupLifecycleListener listener)
	{
		listeners.remove(listener);
	}
	
	protected void notifyListeners(LifecycleEvent event)
	{
		GroupLifecycleEvent lifecycleEvent = new GroupLifecycleEvent(event, getName());
		for(GroupLifecycleListener listener : listeners)
			listener.notify(lifecycleEvent);
	}
}
