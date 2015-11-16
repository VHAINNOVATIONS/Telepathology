/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl.eviction;

import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import gov.va.med.imaging.storage.cache.EvictionStrategy;
import gov.va.med.imaging.storage.cache.EvictionTimer;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;

/**
 * An abstract class that may be used as the parent of eviction strategies that 
 * need to run periodically. The sweep() method will be called on a worker thread
 * of the timer task.  The sweep() method MUST return quickly, delegating to a
 * worker thread any task that takes significant time.  This class provides an executor
 * for worker threads if needed.
 * 
 * @author VHAISWBECKEC
 *
 */
public abstract class PeriodicSweepEvictionStrategy
extends SimpleEvictionStrategy
implements EvictionStrategy
{
	public final static long defaultDelay = 10000;		// 10 seconds
	public final static long defaultInterval = 60000;	// 60 seconds
	
	private final EvictionTimer evictionTimer;
	private SweepTask sweepTask;
	
	/**
	 * @param name
	 */
	protected PeriodicSweepEvictionStrategy(String name, EvictionTimer evictionTimer)
	{
		super(name);
		this.evictionTimer = evictionTimer;
	}

	public EvictionTimer getTimer()
	{
		return this.evictionTimer;
	}
	
	/**
	 * 
	 * @param initialized
	 * @throws CacheException
	 */
	@Override
	public void setInitialized(boolean initialized)
	throws CacheException
	{
		// if the state would not change, do nothing
		if(this.isInitialized() == initialized)
			return;
		
		if(initialized)
		{
			sweepTask = new SweepTask(this);
			if(getMaximumAge() > 0)
				getTimer().scheduleSweep(sweepTask, getMaximumAge());
			else
				getTimer().scheduleSweep(sweepTask, getDelay(), getInterval());
		}
		else
		{
			sweepTask.cancel();
			sweepTask = null;
		}
		
		super.setInitialized(initialized);
	}

	/**
	 * If a derived class overrides this method and returns a
	 * value greater than 0 than the sweep task will be scheduled using the 
	 * eviction timer's sweep interval map.  Otherwise the getDelay and getInterval
	 * are used to schedule the task.
	 * @return
	 */
	public long getMaximumAge()
	{
		return -1L;
	}
	
	/**
	 * Return the delay from initialization to the first run of the sweep.
	 * @see PeriodicSweepEvictionStrategy.getMaximumAge
	 * 
	 * @return
	 */
	public long getDelay()
	{
		return defaultDelay;
	}

	/**
	 * Return the interval between sweep runs.
	 * 
	 * @return
	 */
	public long getInterval()
	{
		return defaultInterval;
	}

	/**
	 * Run a periodic eviction sweep.
	 *
	 */
	public abstract void sweep();
	
	/**
	 * Get the most recent complete evition sweep statistics.
	 * @return
	 */
	public abstract SweepStatistics getLastSweepStatistics();

	/**
	 * Called to request that the eviction strategy stop any threads it may
	 * have started in the course of doing eviction sweeps.
	 */
	public synchronized void cancelSweeps()
	{
		// this will be called from the cancel() of the SweepTask
		if(executor != null)	// cancel execution of worker threads
			getExecutor().shutdownNow();
	}


	// ===================================================================================================================
	// The worker thread execution pool
	// Derived classes are not obligated to use this worker pool (or even use any threading
	// at all) but they usually do so that the sweep() method will return quickly.
	// ===================================================================================================================
	private ThreadGroup workerThreadGroup; 
	private ExecutorService executor;
	private ExecutorCompletionService<SweepStatistics> completionService;
	
	protected synchronized ExecutorService getExecutor()
	{
		if(executor == null)
		{
			workerThreadGroup = new ThreadGroup(getName());
			
			executor = Executors.newCachedThreadPool(
				new ThreadFactory()
				{
					private int serialNumber = 0;
					public Thread newThread(Runnable r)
					{
						Thread newThread = new Thread(workerThreadGroup, r, "TIN-" + serialNumber++);		// returned named threads so we can debug easier
						newThread.setDaemon(true);						// make it a daemon
						
						return newThread;
					}
				}
			);
			
			completionService = new ExecutorCompletionService<SweepStatistics>(executor);
		}
		
		return executor;
	}
	
	protected synchronized ExecutorCompletionService<SweepStatistics> getCompletionService()
	{
		getExecutor();
		return this.completionService;
	}	
}