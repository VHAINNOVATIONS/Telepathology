/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl.eviction;

/**
 * A simple value object that encapsulates statistics from an eviction sweep.
 * 
 * @author VHAISWBECKEC
 *
 */
public class SweepStatistics
{
	private long sweepTime;
	private int totalEvictedGroups;
	
	/**
	 * The defualt constructor is usually used to build a summary
	 * SweepStatistics instance.
	 */
	public SweepStatistics()
	{
		this.sweepTime = 0L;
		this.totalEvictedGroups = 0;
	}

	/**
	 * 
	 * @param sweepTime
	 * @param totalEvictedGroups
	 */
	public SweepStatistics(long sweepTime, int totalEvictedGroups)
	{
		super();
		this.sweepTime = sweepTime;
		this.totalEvictedGroups = totalEvictedGroups;
	}

	public long getSweepTime()
	{
		return this.sweepTime;
	}

	public int getTotalEvictedGroups()
	{
		return this.totalEvictedGroups;
	}
	
	/**
	 * Add the total evicted groups from the given SweepStatistics to this
	 * instance.  Set the sweep time to the minimum of this instance and the
	 * given instance.
	 * 
	 * @param sweepStatistics
	 */
	public void add(SweepStatistics sweepStatistics)
	{
		this.totalEvictedGroups += sweepStatistics.getTotalEvictedGroups();
		this.sweepTime = Math.min(this.getSweepTime(), sweepStatistics.getSweepTime());
	}
}
