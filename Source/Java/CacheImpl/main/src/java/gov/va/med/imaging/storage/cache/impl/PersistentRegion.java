package gov.va.med.imaging.storage.cache.impl;

import gov.va.med.imaging.storage.cache.*;
import gov.va.med.imaging.storage.cache.exceptions.*;
import gov.va.med.imaging.storage.cache.impl.memento.PersistentRegionMemento;
import gov.va.med.imaging.storage.cache.memento.RegionMemento;

import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

/**
 * @author vhaiswbeckec
 *
 * A Region is identified by a name, which must be unique within the Cache in which it is 
 * a member.  The name may be used as a file or directory name and therefore is constrained
 * to upper and lowers case letters and numbers, dashes and underscores.
 * 
 * This class adds abstract persistence mechanisms to a RecursiveGroupParent class. 
 *
 */
public abstract class PersistentRegion 
extends RecursiveGroupParent
implements Region, PersistentRegionMBean
{
	private Logger log = Logger.getLogger(this.getClass());
	
	public final static int MinSecondsReadWaitsForWriteCompletion = 0;
	public final static int DefaultSecondsReadWaitsForWriteCompletion = 30;
	public final static int MaxSecondsReadWaitsForWriteCompletion = 60;

	private final Cache parentCache;
	
	private String name = null;
	private boolean running = false;
	private EvictionStrategy[] evictionStrategies = null;
	private Boolean initialized = Boolean.FALSE;
	private int secondsReadWaitsForWriteCompletion; 
	private boolean setModificationTimeOnRead; 

	// ==============================================================================================================================
	// Constructors
	// ==============================================================================================================================
	public PersistentRegion(Cache parentCache, String name) 
	throws InitializationException
	{
		this(parentCache, name, null, DefaultSecondsReadWaitsForWriteCompletion, false);
	}
	
	public PersistentRegion(Cache parentCache, String name, String[] evictionStrategyNames) 
	throws InitializationException
	{
		this(parentCache, name, evictionStrategyNames, DefaultSecondsReadWaitsForWriteCompletion, false);
	}
	
	/**
	 * 
	 * @param name
	 * @param instanceFactoryChannel
	 * @param evictionStrategyName
	 * @param secondsReadWaitsForWriteCompletion
	 * @param setModificationTimeOnRead
	 * @throws InitializationException
	 */
	public PersistentRegion(
			Cache parentCache, 
			String name, 
			String[] evictionStrategyNames,
			int secondsReadWaitsForWriteCompletion, 
			boolean setModificationTimeOnRead) 
	throws InitializationException
	{
		if(parentCache == null)
			throw new InitializationException("Parent cache was not specified in region constructor.");
		if(name == null)
			throw new InitializationException("Region name was null in region constructor.");
		
		this.parentCache = parentCache;
		
		Matcher nameMatcher = NamePattern.matcher(name);
		if( ! nameMatcher.matches() )
			throw new InitializationException("The region name '" + name + "' is not permitted.  Region names must \n" + 
				"1.) start with a letter\n" +
				"2.) include only letters, numbers, dashes and underscores\n" +
				"3.) between 1 and 64 characters total."
			);
		this.name = name;
		setSecondsReadWaitsForWriteCompletion( secondsReadWaitsForWriteCompletion );
		setSetModificationTimeOnRead( setModificationTimeOnRead );
		
		// set the eviction strategy last because once it is set the eviction thread will
		// start to scan the region
		if(evictionStrategyNames != null && evictionStrategyNames.length > 0)
		{
			EvictionStrategy[] evictionStrategies = new EvictionStrategy[evictionStrategyNames.length];
			
			for(int evictionStrategyIndex=0; evictionStrategyIndex < evictionStrategyNames.length; ++evictionStrategyIndex)
				evictionStrategies[evictionStrategyIndex] = parentCache.getEvictionStrategy(evictionStrategyNames[evictionStrategyIndex]);
			setEvictionStrategies( evictionStrategies );
		}
	}
	
	// ==============================================================================================================================
	// Property Accessors
	// ==============================================================================================================================
	
	public Cache getParentCache()
	{
		return parentCache;
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.PersistentRegionMBean#getName()
	 */
	public String getName()
	{
		return this.name;
	}

	public InstanceByteChannelFactory<?> getInstanceFactoryChannel()
	{
		return getParentCache().getInstanceByteChannelFactory();
	}

	/**
	 * @see gov.va.med.imaging.storage.cache.impl.PersistentRegionMBean#getEvictionStrategyNames()
	 */
	public String[] getEvictionStrategyNames()
	{
		if(evictionStrategies == null)
			return null;
		
		String[] evictionStrategyNames = new String[evictionStrategies.length];
		int evictionStrategyIndex = 0;
		
		for(EvictionStrategy evictionStrategy : evictionStrategies)
			evictionStrategyNames[evictionStrategyIndex++] = evictionStrategy.getName();
		
		return evictionStrategyNames;
	}
	
	/**
	 * @see gov.va.med.imaging.storage.cache.impl.PersistentRegionMBean#setEvictionStrategyNames(java.lang.String[])
	 */
	public void setEvictionStrategyNames(String[] evictionStrategyNames)
	throws UnknownEvictionStrategyException
	{
		if(evictionStrategyNames != null)
		{
			EvictionStrategy[] evictionStrategies = new EvictionStrategy[evictionStrategyNames.length];
			
			int evictionStrategyIndex = 0;
			for(String evictionStrategyName : evictionStrategyNames)
			{
				evictionStrategies[evictionStrategyIndex++] = getParentCache().getEvictionStrategy(evictionStrategyName);
			
				if(evictionStrategies[evictionStrategyIndex-1] == null)
					throw new UnknownEvictionStrategyException("EvictionStrategy '" + evictionStrategyNames + "' is not known to this cache instance.");
			}
			
			setEvictionStrategies( evictionStrategies );
		}
	}
	
	/**
	 * Use the String based eviction strategy name methods externally to this class.
	 * 
	 * @param evictionStrategy
	 */
	private void setEvictionStrategies(EvictionStrategy[] newEvictionStrategies)
	{
		// remove the old eviction strategies if there are any
		if(this.evictionStrategies != null)
		{
			for(EvictionStrategy evictionStrategy : evictionStrategies)
				evictionStrategy.removeRegion(this);
		}
		
		this.evictionStrategies = newEvictionStrategies;
		
		if(newEvictionStrategies != null && newEvictionStrategies.length != 0)
		{
			for(EvictionStrategy evictionStrategy : evictionStrategies)
				evictionStrategy.addRegion(this);
		}
	}

	public EvictionStrategy[] getEvictionStrategies()
	{
		return this.evictionStrategies;
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.PersistentRegionMBean#getSecondsReadWaitsForWriteCompletion()
	 */
	public int getSecondsReadWaitsForWriteCompletion()
	{
		return this.secondsReadWaitsForWriteCompletion;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.PersistentRegionMBean#setSecondsReadWaitsForWriteCompletion(int)
	 */
	public void setSecondsReadWaitsForWriteCompletion(int secondsReadWaitsForWriteCompletion)
	{
		log.debug("Requested to set seconds to wait for write completion to " + secondsReadWaitsForWriteCompletion);
		secondsReadWaitsForWriteCompletion = Math.min(MaxSecondsReadWaitsForWriteCompletion, secondsReadWaitsForWriteCompletion);
		secondsReadWaitsForWriteCompletion = Math.max(MinSecondsReadWaitsForWriteCompletion, secondsReadWaitsForWriteCompletion);
		
		log.debug("Actually set seconds to wait for write completion to " + secondsReadWaitsForWriteCompletion);
		this.secondsReadWaitsForWriteCompletion = secondsReadWaitsForWriteCompletion;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.PersistentRegionMBean#isSetModificationTimeOnRead()
	 */
	public boolean isSetModificationTimeOnRead()
	{
		return this.setModificationTimeOnRead;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.PersistentRegionMBean#setSetModificationTimeOnRead(boolean)
	 */
	public void setSetModificationTimeOnRead(boolean setModificationTimeOnRead)
	{
		this.setModificationTimeOnRead = setModificationTimeOnRead;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.PersistentRegionMBean#isInitialized()
	 */
	public Boolean isInitialized()
	{
		return this.initialized;
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.PersistentRegionMBean#setInitialized(java.lang.Boolean)
	 */
	public final void setInitialized(Boolean initialized)
	throws CacheException
	{
		if( isInitialized() )
			return;				// ignore multiple initialize calls
		
		initialize();
		
		this.initialized = true;
	}
	
	/**
	 * Derived classes may implement this method for initialization.
	 * Overriding this method is preferred over overriding setInitialized()
	 */
	protected void initialize()
	throws RegionInitializationException
	{
	}

	/**
	 * Return a "best effort" estimate of the size of all elements in the Region.
	 * 
	 * @see Region#getUsedSpace()
	 */
	@Override
	public long getUsedSpace() 
	{
		long cumulativeSize = 0L;
		
		Set<Group> groups = new HashSet<Group>(); 
		try
		{
			// try up to SIZE_CALCULATION_RETRIES times to get the region's size
			for(int retries=Region.SIZE_CALCULATION_RETRIES; retries > 0; retries--)
			{
				try
				{
					for(Iterator<? extends Group> groupIter=getGroups(); groupIter.hasNext(); )
						groups.add(groupIter.next());
					log.info("Successfully got Region '" + getName() + "' child groups for used space calculation.");
					break;
				}
				catch (ConcurrentModificationException cmX)
				{
					log.warn("ConcurrentModificationException while getting Region '" + getName() + "' child groups, retrying ....");
				}
			}
			
			for(Group group : groups )
				cumulativeSize += group.getSize();
		}
		catch (CacheException x)
		{
			x.printStackTrace();
		}
		
		return cumulativeSize;
	}

	// ==============================================================================================================================
	// Region Lifecycle Listener
	// ==============================================================================================================================
	/**
	 * Notify the cache instance that it is starting or stopping.
	 * Depending on the cache implementation, it may be critical that it be
	 * notified of start and stop events.
	 * @param event
	 */
	public void cacheLifecycleEvent(CacheLifecycleEvent event)
	{
		if( event.equals(CacheLifecycleEvent.START) )
		{
			this.start();
			running = true;
		}
		else if( event.equals(CacheLifecycleEvent.STOP) )
		{
			this.stop();
			running = false;
		}
	}
	
	public void start()
	{
		log.info(this.getClass().getName() + " instance '" + this.getName() + "' starting.");
	}

	public void stop()
	{
		log.info(this.getClass().getName() + " instance '" + this.getName() + "' stopping.");
	}
	
	public boolean isRunning()
	{
		return running;
	}
	
	protected abstract PersistentGroupSet getPersistentGroupSet()
	throws RegionNotInitializedException;
	
	/**
	 * 
	 * @param judge - an EvictionJudge instance that will determine whether a Group may be evicted
	 */
	public int evaluateAndEvictChildGroups(EvictionJudge<Group> judge) 
	throws CacheException
	{
		int totalEvictions = 0;

		// if we have not been initialized then don't do anything
		if( isInitialized() )
		{
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
					log.warn("Concurrent modification exception occurred while evicting '" + childGroup.getName() + "'.  Some groups may not have been evicted but will be on subsequent sweeps.  Don't worry about it.");
				}
			}
			
			// now find the child groups of this Region that may be evicted
			Set<? extends Group> deadGroups = getPersistentGroupSet().evictableChildren(judge);
			
			if(deadGroups != null)
			{
				for (Group deadGroup : deadGroups)
				{
					log.info("Group '" + deadGroup.getName() + " queued for eviction is being deleted.");
					try
					{
						getPersistentGroupSet().deleteChild(deadGroup, false);
						++totalEvictions;
					} 
					catch (SimultaneousWriteException swX)
					{
						log.info("Unable to evict entirety of group '" + deadGroup.getName() + "', at least one instance is still open.  " + 
								"The cache will clean itself up when the byte channel factory closes the instance."
						);
					}
				}
			}
		}
		
		return totalEvictions;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(getName());
		sb.append(" : ");
		sb.append(getClass().getName());
		
		return sb.toString();
	}
	
	
	// =========================================================================================================
	// Memento management
	// =========================================================================================================
	protected void populateParameters(RegionMemento regionMemento)
	{
		regionMemento.setName(getName());
		regionMemento.setEvictionStrategyNames(getEvictionStrategyNames());
		if(regionMemento instanceof PersistentRegionMemento)
		{
			((PersistentRegionMemento)regionMemento).setSecondsReadWaitsForWriteCompletion(getSecondsReadWaitsForWriteCompletion());
			((PersistentRegionMemento)regionMemento).setSetModificationTimeOnRead(isSetModificationTimeOnRead());
		}
	}
	
	public void setParameters(RegionMemento memento) 
	throws UnknownEvictionStrategyException
	{
		this.name = memento.getName();
		this.setEvictionStrategyNames(memento.getEvictionStrategyNames());
		
		if(memento instanceof PersistentRegionMemento)
		{
			PersistentRegionMemento fsrMemento = (PersistentRegionMemento)memento;
			
			this.setSecondsReadWaitsForWriteCompletion(fsrMemento.getSecondsReadWaitsForWriteCompletion());
			this.setSetModificationTimeOnRead(fsrMemento.isSetModificationTimeOnRead());
		}
	}
	
	/**
	 * Create a serializable and persistable state representation
	 * @return
	 */
	public PersistentRegionMemento createMemento()
	{
		PersistentRegionMemento regionMemento = new PersistentRegionMemento();
		populateParameters(regionMemento);
		return regionMemento;
	}
}
