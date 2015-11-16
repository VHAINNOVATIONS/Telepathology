package gov.va.med.imaging.storage.cache.impl.eviction;

import gov.va.med.imaging.storage.cache.*;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import sun.security.action.GetLongAction;

/**
 * 
 * @author VHAISWBECKEC
 *
 */
public class LastAccessedEvictionStrategy
extends PeriodicSweepEvictionStrategy
implements EvictionStrategy, LastAccessedEvictionStrategyMBean
{
	public final static String maxTimePropertyKey = "maximumAge"; 
	public final static String initializedPropertyKey = "initialized";
	private static final long WORKER_WAIT_SECONDS = 120;
	
	private final long maximumTimeSinceLastAccess;
	private final DateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
	private final Logger log = Logger.getLogger(this.getClass());
	private SweepStatistics lastSweepStatistics = new SweepStatistics(System.currentTimeMillis(), 0);
	
	/**
	 * Factory method used by the interactive tool.
	 * 
	 * @param memento
	 * @param timer
	 * @return
	 */
	static LastAccessedEvictionStrategy create(Properties prop, EvictionTimer timer)
	{
		String name = (String)prop.get(SimpleEvictionStrategy.namePropertyKey);
		long maxTimeSinceLastAccess = ((Long)prop.get(maxTimePropertyKey)).longValue();
		boolean initialized = ((Boolean)prop.get(initializedPropertyKey)).booleanValue();
		
		return new LastAccessedEvictionStrategy(name, maxTimeSinceLastAccess, initialized, timer);
	}
	
	static LastAccessedEvictionStrategy create(LastAccessedEvictionStrategyMemento memento, EvictionTimer timer)
	{
		return new LastAccessedEvictionStrategy(memento.getName(), memento.getMaximumTimeSinceLastAccess(), memento.isInitialized(), timer);
	}
	
	static LastAccessedEvictionStrategy create(String name, Long maximumTimeSinceLastAccess, boolean initialized, EvictionTimer timer)
	{
		return new LastAccessedEvictionStrategy(name, maximumTimeSinceLastAccess, initialized, timer);
	}
	
	protected LastAccessedEvictionStrategy(String name, Long maximumTimeSinceLastAccess, boolean initialized, EvictionTimer timer)
	{
		super(name, timer);
		this.maximumTimeSinceLastAccess = maximumTimeSinceLastAccess.longValue();
	}
	
	/**
	 * Get the maximum age of a group as measured from now to its last access.
	 * 
	 * @see gov.va.med.imaging.storage.cache.impl.eviction.LastAccessedEvictionStrategyMBean#getMaximumTimeSinceLastAccess()
	 */
	@Override
	public long getMaximumAge()
	{
		return maximumTimeSinceLastAccess;
	}
	
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.eviction.LastAccessedEvictionStrategyMBean#getEvictedLastSweep()
	 */
	public int getEvictedLastSweep()
	{
		return getLastSweepStatistics().getTotalEvictedGroups();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.eviction.LastAccessedEvictionStrategyMBean#getLastRunDate()
	 */
	public String getLastRunDate()
	{
		return df.format( new Date(getLastSweepStatistics().getSweepTime()) );
	}

	
	/**
	 * Create a Memento that may be serialized and later used to restore the state of this class.
	 * 
	 * @return
	 */
	@Override
	public LastAccessedEvictionStrategyMemento createMemento()
	{
		return new LastAccessedEvictionStrategyMemento(getName(), isInitialized(), getMaximumAge());
	}

	// ==========================================================================================================
	//
	// ==========================================================================================================
	
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
	public void sweep()
	{
		log.info("Eviction sweep commencing, starting worker tasks.");
		
		long maxAge = getMaximumAge();		// the max period of a cached item before it gets deleted
															// this method is overriden in derived classes and should be called only
															// once per sweep
		Date now = new Date();								// the current date/time
		Date minLastAccess = new Date(now.getTime() - maxAge);		// anything accessed before this time should be deleted.
		LastAccessedDateJudge judge = new LastAccessedDateJudge(minLastAccess);

		//List<Future<SweepStatistics>> futures = new ArrayList<Future<SweepStatistics>>();
		for(Region region: getRegions())
		{
			getCompletionService().submit( new LastAccessedEvictionTask(judge, region) );
			//futures.add( getExecutor().submit( new LastAccessedEvictionTask(judge, region) ) );
		}
		SweepStatistics sweepStatistics = new SweepStatistics();
		//getExecutor().execute(new StatisticsCollectionTask(futures));
		for(int expectedResultsCount = getRegions().size(); expectedResultsCount > 0; --expectedResultsCount )
		{
			try 
			{
				Future<SweepStatistics> future = getCompletionService().poll(WORKER_WAIT_SECONDS, TimeUnit.SECONDS);
				if(future != null)
					sweepStatistics.add( future.get() );
				else
					log.error("Timed out waiting for LastAccessedEvictionTask worker thread.  Eviction sweep statistics are not reliable.");
			} 
			catch (InterruptedException e) 
			{
				log.error(e);
				break;
			} 
			catch (ExecutionException e) 
			{
				e.printStackTrace();
				log.error(e);
			}
		}
		
		log.info("Eviction sweep commencing, worker tasks started.");
	}
	
	// ==========================================================================================================
	//
	// ==========================================================================================================

	/**
	 * This is the class that actually does the work of deleting (evicting)
	 * files from the cache.
	 * 
	 * @author VHAISWBECKEC
	 *
	 */
	class LastAccessedEvictionTask
	implements Callable<SweepStatistics>
	{
		private final Region region;
		private final EvictionJudge<Group> judge;
		
		/**
		 * Starting with the specified region, delete anything older (i.e. last access before the minLastAccess)
		 * @param region
		 * @param minLastAccess
		 */
		LastAccessedEvictionTask(EvictionJudge<Group> judge, Region region)
		{ 
			this.judge = judge;
			this.region = region;
		}
		
		EvictionJudge<Group> getJudge()
		{
			return this.judge;
		}

		Region getRegion()
		{
			return this.region;
		}

		
		/* (non-Javadoc)
		 * @see java.util.concurrent.Callable#call()
		 */
		public SweepStatistics call() 
		throws Exception
		{
			int evicted = 0;
			long start = System.currentTimeMillis();
			log.info(Thread.currentThread().getName() + " beginning eviction sweep of region '" + getRegion().getName() + "'.");

			try
			{
				evicted = region.evaluateAndEvictChildGroups(getJudge());
			} 
			catch (ConcurrentModificationException cmX)
			{
				cmX.printStackTrace();
			}
			catch (CacheException e)
			{
				e.printStackTrace();
			}

			log.info(Thread.currentThread().getName() + " completed sweep, " + evicted + " groups evicted.");
			
			return new SweepStatistics(start, evicted);
		}
	}
	
	/**
	 * A class that waits for results from the region sweep tasks and then collects the statistics.
	 * 
	 * @author VHAISWBECKEC
	 */
	class StatisticsCollectionTask
	implements Runnable
	{
		private List<Future<SweepStatistics>> futures;
		StatisticsCollectionTask(List<Future<SweepStatistics>> futures)
		{
			this.futures = futures;
		}
		
		private List<Future<SweepStatistics>> getFutures()
		{
			return futures;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run()
		{
			SweepStatistics sweepStatistics = new SweepStatistics();
			
			for( Future<SweepStatistics> future : getFutures() )
			{
				//future.isDone();
				try
				{
					sweepStatistics.add( future.get() );
				} 
				catch (InterruptedException x)
				{
					log.error("Error getting results of region sweep, eviction statistics may not be accurate", x);
				} 
				catch (ExecutionException x)
				{
					log.error("Error getting results of region sweep, eviction statistics may not be accurate", x);
			
				}
			}
			
			setLastSweepStatistics(sweepStatistics);
		}
	}
	
	/**
	 * A simple EvictionJudge that just looks at the last accessed date
	 * @author vhaiswbeckec
	 *
	 */
	class LastAccessedDateJudge
	implements EvictionJudge<Group>
	{
		final long minLastAccessMilli;
		
		LastAccessedDateJudge(Date minLastAccess)
		{
			minLastAccessMilli = minLastAccess.getTime();
		}
		
		public boolean isEvictable(Group group) 
		throws CacheException
		{
			Date groupLastAccessed = group == null ? null : group.getLastAccessed();
			return groupLastAccessed == null ? false : (groupLastAccessed.getTime() < minLastAccessMilli);
		}
		
	}
}
