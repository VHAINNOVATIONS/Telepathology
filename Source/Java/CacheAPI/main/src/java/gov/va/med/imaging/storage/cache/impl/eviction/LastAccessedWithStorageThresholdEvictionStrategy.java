/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl.eviction;

import gov.va.med.imaging.storage.cache.EvictionStrategy;
import gov.va.med.imaging.storage.cache.EvictionTimer;

/**
 * @author VHAISWBECKEC
 *
 */
public class LastAccessedWithStorageThresholdEvictionStrategy 
extends LastAccessedEvictionStrategy 
implements EvictionStrategy
{
	public final static long minimumDelay = 1000;		// 1 second
	public final static long minimumInterval = 10000;	// 10 seconds
	public final static long defaultDelay = 10000;		// 10 seconds
	public final static long defaultInterval = 60000;	// 60 seconds
	
	public static LastAccessedWithStorageThresholdEvictionStrategy create(
			String name, 
			boolean initialized, 
			long maximumTimeSinceLastAccess, 
			long minFreeSpaceThreshold, 
			long targetFreeSpaceThreshold, 
			long delay, 
			long interval, 
			EvictionTimer timer)
	{
		return new LastAccessedWithStorageThresholdEvictionStrategy(
				name,
				initialized, 
				maximumTimeSinceLastAccess,
				minFreeSpaceThreshold,
				targetFreeSpaceThreshold,
				delay,
				interval,
				timer);
	}
	
	private final long delay;
	private final long interval;

	private final long minFreeSpaceThreshold;
	private final long targetFreeSpaceThreshold;

	protected LastAccessedWithStorageThresholdEvictionStrategy(
			String name, 
			boolean initialized, 
			long maximumTimeSinceLastAccess, 
			long minFreeSpaceThreshold, 
			long targetFreeSpaceThreshold, 
			long delay, 
			long interval, 
			EvictionTimer timer)
	{
		super(name, maximumTimeSinceLastAccess, initialized, timer);
		this.minFreeSpaceThreshold = minFreeSpaceThreshold;
		this.targetFreeSpaceThreshold = targetFreeSpaceThreshold;
		this.delay = delay;
		this.interval = interval;
	}

	public long getDelay()
	{
		return this.delay;
	}

	public long getInterval()
	{
		return this.interval;
	}

	public long getMinFreeSpaceThreshold()
	{
		return this.minFreeSpaceThreshold;
	}

	public long getTargetFreeSpaceThreshold()
	{
		return this.targetFreeSpaceThreshold;
	}

	private SweepStatistics lastSweepStatistics = new SweepStatistics(System.currentTimeMillis(), 0); 
	/**
	 * 
	 */
	@Override
	public void sweep()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public SweepStatistics getLastSweepStatistics()
	{
		return lastSweepStatistics;
	}

}
