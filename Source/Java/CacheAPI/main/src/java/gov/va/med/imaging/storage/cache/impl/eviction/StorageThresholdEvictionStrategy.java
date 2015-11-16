/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl.eviction;

import gov.va.med.imaging.storage.cache.*;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import gov.va.med.imaging.storage.cache.impl.GroupEvictionCandidateVisitor;
import gov.va.med.imaging.storage.cache.impl.GroupPath;
import gov.va.med.imaging.storage.cache.impl.TargetSizeGroupPathSet;

import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.*;

import org.apache.log4j.Logger;

/**
 * An eviction strategy realization that uses a minimum free space and/or maximum used space 
 * to determine when to evict groups.
 * 
 * This class will evict groups so that the total of the used spaces is the lesser of
 * the maxUsedSpaceThreshold - minFreeSpaceThreshold or the total 
 * available space - minFreeSpaceThreshold.
 * If either threshold is exceeded then this eviction strategy will attempt to evict
 * groups such that the resulting total size will be near the targetFreeSpaceThreshold.
 * 
 * The parameters are restricted in value as follows:
 * 1.) minFreeSpaceThreshold <= targetFreeSpaceThreshold
 * 2.) maxUsedSpaceThreshold <= available space 
 *     (determined at runtime, the value of maxUsedSpaceThreshold is reduced to available space if greater)
 * 
 * This eviction strategy runs periodically, on each iteration it:
 * 1.) For each region 
 *     Checks if the free space has fallen below a minimum
 * 1b.) If the free space has fallen below a minimum or then 
 *      Starts a task on a thread pool (a Region Sweep Task)
 *      If the maximum used space property is greater than 0 then the collective size
 *      of the managed regions is determined using the same thread
 * 2.) Each Region Sweep Task creates a set of eviction candidates that are the LRU groups 
 *     whose collective size falls just below the target size.
 * 3.) Starts, yet another, thread (the Evictor Task) that aggregates the set of eviction candidates 
 *     from each region such that they are collectively the LRU of all the regions (whose total size falls 
 *     just below the target size) and then removes the groups in the (collective) eviction candidate set.
 * 
 * NOTE: there is a problem here.  This will work fine as long as all of the Regions that are managed
 * by this EvictionStrategy are persisted on the same device.  
 * It is permissable to have multiple instances of this EvictionStrategy, one for each device, then 
 * each Region on that device can use the same instance of this eviction strategy.
 * 
 * @author VHAISWBECKEC
 *
 */
public class StorageThresholdEvictionStrategy
extends PeriodicSweepEvictionStrategy
implements EvictionStrategy, StorageThresholdEvictionStrategyMBean
{
	public final static long minimumDelay = 0;		// immediate
	public final static long minimumInterval = 1000;	// 1 second, which is really short except for transient caches
	
	public final static String delayPropertyKey = "delay";
	public final static String intervalPropertyKey = "interval";
	public final static String minFreeSpacePropertyKey = "minimumFreeSpace";
	public final static String targetFreeSpacePropertyKey = "targetFreeSpace";
	public final static String maxUsedSpacePropertyKey = "maximumUsedSpace";

	// ==========================================================================================================================
	// 
	// ==========================================================================================================================
	
	/**
	 * Required Factory Method
	 */
	static StorageThresholdEvictionStrategy create(Properties prop, EvictionTimer timer)
	throws CacheException 
	{
		String name = (String)prop.get(SimpleEvictionStrategy.namePropertyKey);
		boolean initialized = ((Boolean)prop.get(SimpleEvictionStrategy.initializedPropertyKey)).booleanValue();
		long delay = ((Long)prop.get(delayPropertyKey)).longValue();
		long interval = ((Long)prop.get(intervalPropertyKey)).longValue();
		long minFreeSpaceThreshold = prop.get(minFreeSpacePropertyKey) == null ?
			-1L : 
			((Long)prop.get(minFreeSpacePropertyKey)).longValue();
		long targetFreeSpaceThreshold = prop.get(targetFreeSpacePropertyKey) == null ? 
			-1L : 
			((Long)prop.get(targetFreeSpacePropertyKey)).longValue();
		long maxUsedSpaceThreshold = prop.get(maxUsedSpacePropertyKey) == null ?
			-1 : 
			((Long)prop.get(maxUsedSpacePropertyKey)).longValue();
		
		return new StorageThresholdEvictionStrategy( 
				name, initialized, timer, 
				minFreeSpaceThreshold, targetFreeSpaceThreshold, maxUsedSpaceThreshold, 
				delay, interval );
	}
	
	/**
	 * @param memento
	 * @param timer
	 * @return
	 */
	static EvictionStrategy create(StorageThresholdEvictionStrategyMemento memento, EvictionTimer timer)
	throws CacheException 
	{
		return new StorageThresholdEvictionStrategy(timer, memento);
	}

	/**
	 * 
	 * @param name
	 * @param initialized
	 * @param evictionTimer
	 * @param minFreeSpaceThreshold
	 * @param targetFreeSpaceThreshold
	 * @param maxUsedSpaceThreshold
	 * @param delay
	 * @param interval
	 * @return
	 * @throws CacheException
	 */
	static EvictionStrategy create(
		String name, boolean initialized, 
		EvictionTimer evictionTimer, 
		long minFreeSpaceThreshold, long targetFreeSpaceThreshold, long maxUsedSpaceThreshold,
		long delay, long interval) 
	throws CacheException
	{
		return new StorageThresholdEvictionStrategy(
				name, initialized, 
				evictionTimer, 
				minFreeSpaceThreshold, targetFreeSpaceThreshold, maxUsedSpaceThreshold,
				delay, interval);
	}
	
	// ==========================================================================================================================
	// 
	// ==========================================================================================================================
	
	private final long delay;
	private final long interval;

	private long minFreeSpaceThreshold;
	private long targetFreeSpaceThreshold;
	private long maxUsedSpaceThreshold;

	private SweepStatistics lastSweepStatistics = new SweepStatistics(System.currentTimeMillis(), 0);
	
	private Logger logger = Logger.getLogger(this.getClass());

	private StorageThresholdEvictionStrategy(
			EvictionTimer evictionTimer,
			StorageThresholdEvictionStrategyMemento memento
	) 
	throws CacheException 
	{
		this(
			memento.getName(),
			memento.isInitialized(), 
			evictionTimer,
			memento.getMinFreeSpaceThreshold(),
			memento.getTargetFreeSpaceThreshold(),
			memento.getMaxUsedSpaceThreshold(),
			memento.getDelay(),
			memento.getInterval()
		);
	}
	
	private StorageThresholdEvictionStrategy(
			String name, 
			boolean initialized,
			EvictionTimer evictionTimer, 
			long minFreeSpaceThreshold, 
			long targetFreeSpaceThreshold,
			long maxUsedSpaceThreshold,
			long delay, 
			long interval) 
	throws CacheException
	{
		super(name, evictionTimer);
		
		// it looks like we're setting minFreeSpaceThreshold twice in the following code
		// and we are but ...
		// the call to setMinFreeSpaceThreshold will invoke bounds checking that is not done by simply
		// setting the field.  setTargetFreeSpaceThreshold relies on the value of 
		// minFreeSpaceThreshold, so that must be set before calling setTargetFreeSpaceThreshold.
		this.minFreeSpaceThreshold = minFreeSpaceThreshold;
		setTargetFreeSpaceThreshold(targetFreeSpaceThreshold);
		setMinFreeSpaceThreshold(minFreeSpaceThreshold);
		setMaxUsedSpaceThreshold(maxUsedSpaceThreshold);
		
		this.delay = Math.max(minimumDelay, delay);
		this.interval = Math.max(minimumInterval, interval);
		setInitialized(initialized);
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.eviction.StorageThresholdEvictionStrategyMBean#getDelay()
	 */
	public long getDelay()
	{
		return this.delay;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.eviction.StorageThresholdEvictionStrategyMBean#getInterval()
	 */
	public long getInterval()
	{
		return this.interval;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.eviction.StorageThresholdEvictionStrategyMBean#getMinFreeSpaceThreshold()
	 */
	public long getMinFreeSpaceThreshold()
	{
		return this.minFreeSpaceThreshold;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.eviction.StorageThresholdEvictionStrategyMBean#setMinFreeSpaceThreshold(long)
	 */
	public void setMinFreeSpaceThreshold(long minFreeSpaceThreshold)
	{
		// minFreeSpaceThreshold must be positive
		minFreeSpaceThreshold = Math.max(minFreeSpaceThreshold, 0L);
		// minFreeSpaceThreshold must be less than or equal to the targetFreeSpaceThreshold
		minFreeSpaceThreshold = Math.min(minFreeSpaceThreshold, getTargetFreeSpaceThreshold());
		
		this.minFreeSpaceThreshold = minFreeSpaceThreshold;
		logger.info("Setting minimum free space threshold to " + this.minFreeSpaceThreshold);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.eviction.StorageThresholdEvictionStrategyMBean#getTargetFreeSpaceThreshold()
	 */
	public long getTargetFreeSpaceThreshold()
	{
		return this.targetFreeSpaceThreshold;
	}

	public long getMaxUsedSpaceThreshold()
	{
		return this.maxUsedSpaceThreshold;
	}

	public void setMaxUsedSpaceThreshold(long maxUsedSpaceThreshold)
	{
		this.maxUsedSpaceThreshold = maxUsedSpaceThreshold;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.eviction.StorageThresholdEvictionStrategyMBean#setTargetFreeSpaceThreshold(long)
	 */
	public void setTargetFreeSpaceThreshold(long targetFreeSpaceThreshold)
	{
		// targetFreeSpaceThreshold must be positive
		targetFreeSpaceThreshold = Math.max(targetFreeSpaceThreshold, 0L);
		// targetFreeSpaceThreshold must be greater than or equal to the minFreeSpaceThreshold
		targetFreeSpaceThreshold = Math.max(targetFreeSpaceThreshold, getMinFreeSpaceThreshold());
		
		this.targetFreeSpaceThreshold = targetFreeSpaceThreshold;
		logger.info("Setting target free space threshold to " + this.targetFreeSpaceThreshold);
	}

	public StorageThresholdEvictionStrategyMemento getMemento()
	{
		return new StorageThresholdEvictionStrategyMemento(
			getName(), isInitialized(), 
			getMinFreeSpaceThreshold(), getTargetFreeSpaceThreshold(), getMaxUsedSpaceThreshold(), 
			getDelay(), getInterval()
		);
	}
	
	// ===================================================================================================================
	// The actual sweep implementation, called periodically
	// ===================================================================================================================
	@Override
	public void sweep()
	{
		// a list, one element per region, each element consisting of a list of groups to evict.
		// building the list of groups is done by worker threads, one per region
		List<Future<TargetSizeGroupPathSet>> regionSweepFutures = new ArrayList<Future<TargetSizeGroupPathSet>>();
		long freeSpace = -1;
		DateFormat df = DateFormat.getDateTimeInstance();
		long totalManagedSize = 0L;
		
		logger.info( "Beginning storage threshold eviction sweep at " + df.format(new Date()) );
		
		Set<Region> regions = getRegions();
		
		for(Region region: getRegions())
			if( ! region.isInitialized() )
			{
				logger.warn("Attempt to start an eviction sweep without all regions initialized.  If this happens once at startup, then it is okay.");
				return;
			}
		
		if(regions.isEmpty())
			return;
		
		Region firstRegion = null;
		// if maxUsedSpaceThreshold is > 0 then that will be used as the maximum
		// else a log message is generated and the eviction stops
		// To get the maximum used space requires a pass through the cache, and
		// perhaps a pass through the file system.
		// The freeSpace is either the actual free space on the device or
		// the maxUsedSpaceThreshold - totalManagedSize
		if(getMaxUsedSpaceThreshold() > 0)
		{
			for(Region region: getRegions())
			{
				firstRegion = (firstRegion == null ? region : firstRegion);
				totalManagedSize += region.getUsedSpace();
			}
			// how much free space foes the region's persistent storage think it has available
			freeSpace = firstRegion.getFreeSpace();
			
			// get the minimum of the persistent persistence device free space or the
			// "defined" maximum space minus the actual used space.
			// In other words the lesser of what we've defined as the minimum free space or the real device free space.
			freeSpace = Math.min(freeSpace, getMaxUsedSpaceThreshold() - totalManagedSize);
		}
		else
		{
			// get the first region linked to this eviction strategy
			firstRegion = getRegions().iterator().next();

			// this may or may not work because not all file systems support it.
			// regions over file systems that do not support getting free space
			// MUST report the value as 0
			freeSpace = firstRegion.getFreeSpace();
		}
		logRegionStatistics(firstRegion, freeSpace);
		
		// note that freeSpace could be a negative number
		// if it is precisely 0L and the max used space is not specified 
		// then we cannot continue
		if(freeSpace != 0L && getMaxUsedSpaceThreshold() > 0)
		{
			// for each Region
			for(Region region: getRegions())
			{
				// if the free space is less than the minimum then start the region sweeps
				// else don't
				// NOTE, it is expected that the region sweeps could be expensive so running
				// them should be minimized
				if( freeSpace < getMinFreeSpaceThreshold() )
				{
					// each region is told how many bytes we want to free up total
					// the candidates that it provides are all tacked onto a "master" list
					// which is used to determine what actually gets evicted
					RegionSweepTask task = new RegionSweepTask(region, getTargetFreeSpaceThreshold() - freeSpace);
					Future<TargetSizeGroupPathSet> future = getExecutor().submit(task);
					
					regionSweepFutures.add(future);
				}
			}
		}
		else
			logger.warn(
				"Managed regions do not support free space reporting and no maximum size was specified.  " + 
				" The eviction strategy is '" + this.getClass().getSimpleName() + "', which requires it.  No eviction will occur in these regions.");

		// Start the thread to do the actual evictions.
		// This thread will wait until all of the Futures have results available, which is the
		// same as saying when the region sweep tasks are complete, then it will evict across
		// all of the regions sufficient to free up the target space
		getExecutor().execute( new EvictorTask(regionSweepFutures, getTargetFreeSpaceThreshold() - freeSpace) );
		
		return;
	}

	private void logRegionStatistics(Region region, long freeSpace)
	{
		logger.info(
			"Region '" + region.getName() + 
			"' has " + freeSpace + 
			" bytes free, maximum used threshold is " + getMaxUsedSpaceThreshold() + 
			" bytes free, minimum threshold is " + getMinFreeSpaceThreshold() + 
			" bytes free, target threshold is " + getTargetFreeSpaceThreshold() + 
			" bytes free."
		);
	}
	
	@Override
	public SweepStatistics getLastSweepStatistics()
	{
		return lastSweepStatistics;
	}
	
	void setLastSweepStatistics(SweepStatistics sweepStatistics)
	{
		this.lastSweepStatistics = sweepStatistics;
	}
	
	@Override
	public StorageThresholdEvictionStrategyMemento createMemento()
	{
		StorageThresholdEvictionStrategyMemento memento = new StorageThresholdEvictionStrategyMemento();
		
		memento.setInitialized(isInitialized());
		memento.setName(getName());
		memento.setDelay(getDelay());
		memento.setInterval(getInterval());
		memento.setMinFreeSpaceThreshold(getMinFreeSpaceThreshold());
		memento.setTargetFreeSpaceThreshold(getTargetFreeSpaceThreshold());
		memento.setMaxUsedSpaceThreshold(getMaxUsedSpaceThreshold());
		
		return memento;
	}
	
	
	/**
	 * The worker task that collects the groups that may be evicted in one region.
	 * 
	 * @author VHAISWBECKEC
	 *
	 */
	class RegionSweepTask
	implements Callable<TargetSizeGroupPathSet>
	{
		private Region region;
		private long evictSize;
		
		RegionSweepTask(Region region, long evictSize)
		{
			this.region = region;
			this.evictSize = evictSize;
		}
		
		/**
		 * @see java.util.concurrent.Callable#call()
		 */
		public TargetSizeGroupPathSet call() 
		throws Exception
		{
			// maintains a Set of eviction candidates in oldest to newest order
			// and whose total size is less than the evictSize
			TargetSizeGroupPathSet evictionCandidates = new TargetSizeGroupPathSet(evictSize);
			logger.info(
				"Region '" + region.getName() + 
				" bytes free, minimum threshold is " + getMinFreeSpaceThreshold() + 
				" bytes free, target threshold is " + getTargetFreeSpaceThreshold() + " bytes free."
			);
			
			// special case if the Region implementation does not report free space
			if(evictSize > 0)
			{
				// scan leaf nodes first !!!
				// the group visitor does not provide ancestry information
				// DO NOT SCAN ANYTHING BUT LEAF NODES, otherwise the calculated sizes will be incorrect
				TargetSizeGroupPathSet regionEvictionCandidates = 
					GroupEvictionCandidateVisitor.createAndRun(region, false, true, evictSize);
				evictionCandidates.addAll( regionEvictionCandidates );
			}
			
			return evictionCandidates;
		}
	}
	
	/**
	 * A task that 
	 * 1.) Collects the result from all the regions and combines them
	 *     into a single list of groups to be evicted.  The collective
	 *     size of the groups on the list will be no bigger than the target
	 *     size.
	 * 2.) 
	 * 
	 * @author VHAISWBECKEC
	 */
	class EvictorTask
	implements Runnable
	{
		private List<Future<TargetSizeGroupPathSet>> regionTaskFutures;
		private TargetSizeGroupPathSet evictableGroups;
		
		EvictorTask(List<Future<TargetSizeGroupPathSet>> regionTaskFutures, long size)
		{
			this.regionTaskFutures = regionTaskFutures;
			evictableGroups = new TargetSizeGroupPathSet(size);
		}
		
		/**
		 * 
		 */
		public void run()
		{
			long start = System.currentTimeMillis();
			int groupsEvicted = 0;
			
			for(Future<TargetSizeGroupPathSet> future : this.regionTaskFutures)
			{
				try
				{
					// get() - Waits if necessary for the computation to complete, and then retrieves its result.
					TargetSizeGroupPathSet deletableGroupPaths = future.get();
					evictableGroups.addAll( deletableGroupPaths );
				} 
				catch (InterruptedException x)
				{
					logger.warn("InterruptedException caught in region eviction thread while getting result.", x);
				} 
				catch (ExecutionException x)
				{
					logger.warn("ExecutionException caught in region eviction thread while getting result. At least one region has not contributed to evictable list.", x);
				}
			}
			
			logger.info("There are " + evictableGroups.size() + " cache entries to evict.");
			if(evictableGroups.size() > 0)
			{
				for( GroupPath groupPath:evictableGroups )
				{
					try
					{
						Group parent = groupPath.getRegion().getGroup(groupPath.getPathName());
						if(parent == null)
							groupPath.getRegion().deleteChildGroup(groupPath.getGroup(), false);
						else
							parent.deleteChildGroup(groupPath.getGroup(), false);
							
						++groupsEvicted;
					} 
					catch (CacheException x)
					{
						logger.error("Unable to remove group '" + groupPath.getGroup().getName() + "'", x);
					}
				}
			}
			
			setLastSweepStatistics(new SweepStatistics(start, groupsEvicted) );
		}
	}
}
