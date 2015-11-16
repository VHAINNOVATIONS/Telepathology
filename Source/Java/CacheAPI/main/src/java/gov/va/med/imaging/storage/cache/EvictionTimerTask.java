package gov.va.med.imaging.storage.cache;

import java.util.TimerTask;

/**
 * A simple interface to make eviction strategy information available to the
 * timer task that runs it.
 * 
 * @author VHAISWBECKEC
 *
 */
public abstract class EvictionTimerTask
extends TimerTask
{
	public abstract String getEvictionStrategyName();
}
