package gov.va.med.imaging.storage.cache;

import gov.va.med.imaging.storage.cache.memento.EvictionTimerMemento;


/**
 * 
 * @author VHAISWBECKEC
 *
 */
public interface EvictionTimer
{
	/**
	 * Schedule eviction sweeps for age based eviction strategies.  The maximum age 
	 * parameter is used to calculate an interval for sweeps.  The interval MUST be 
	 * less than the maximum age but is otherwise up to the realization. 
	 * 
	 * @param sweepTask
	 * @param maximumAge
	 */
	public void scheduleSweep(EvictionTimerTask sweepTask, long maximumAge);
	
	/**
	 * Schedule an eviction sweep at the specified interval.  This type of
	 * scheduling is intended for eviction strategies that are not age-based
	 * but simply need to run periodically.  This is a simple pass-through to
	 * the Timer method of the same name.
	 */
	public void scheduleSweep(EvictionTimerTask sweepTask, long delay, long interval);

	/**
	 * 
	 */
	public void cancel();
	
	/**
	 * 
	 * @return
	 */
	public abstract EvictionTimerMemento createMemento();
}