package gov.va.med.imaging.storage.cache.impl.eviction;

import gov.va.med.imaging.storage.cache.EvictionTimerTask;

/**
 * An EvictionTimerTask realization that just calls to the given eviction strategy at scheduled intervals.
 */
class SweepTask
extends EvictionTimerTask
{
	private long lastRunDate;

	/**
	 * 
	 */
	private PeriodicSweepEvictionStrategy periodicSweepStrategy; 
	SweepTask(PeriodicSweepEvictionStrategy periodicSweepStrategy)
	{
		this.periodicSweepStrategy = periodicSweepStrategy;
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.EvictionTimerTask#getEvictionStrategyName()
	 */
	@Override
	public String getEvictionStrategyName()
	{
		return getEvictionStrategy().getName();
	}

	public PeriodicSweepEvictionStrategy getEvictionStrategy()
	{
		return this.periodicSweepStrategy;
	}
	
	public long getLastRunDate()
	{
		return this.lastRunDate;
	}
	
	protected void setLastRunDate(long lastRunDate)
	{
		this.lastRunDate = lastRunDate;
	}
	
	/* (non-Javadoc)
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run()
	{
		setLastRunDate(System.currentTimeMillis());
		getEvictionStrategy().sweep();
	}

	@Override
	public boolean cancel()
	{
		getEvictionStrategy().cancelSweeps();
		return super.cancel();
	}

}