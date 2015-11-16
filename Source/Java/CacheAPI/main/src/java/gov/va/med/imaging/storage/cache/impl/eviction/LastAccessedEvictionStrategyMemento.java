package gov.va.med.imaging.storage.cache.impl.eviction;

import java.io.Serializable;

/**
 * 
 * @author VHAISWBECKEC
 *
 */
public class LastAccessedEvictionStrategyMemento
extends SimpleEvictionStrategyMemento
implements Serializable
{
	private static final long serialVersionUID = -9046857505176378045L;
	private long maximumTimeSinceLastAccess;

	public LastAccessedEvictionStrategyMemento()
	{
		super();
	}
	
	public LastAccessedEvictionStrategyMemento(String name, boolean initialized, long maximumTimeSinceLastAccess)
	{
		super(name, initialized);
		setMaximumTimeSinceLastAccess(maximumTimeSinceLastAccess);
	}

	public long getMaximumTimeSinceLastAccess()
	{
		return this.maximumTimeSinceLastAccess;
	}

	public void setMaximumTimeSinceLastAccess(long maximumTimeSinceLastAccess)
	{
		this.maximumTimeSinceLastAccess = maximumTimeSinceLastAccess;
	}

}
