/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl.eviction;

import java.io.Serializable;

/**
 * @author VHAISWBECKEC
 *
 */
public class StorageThresholdEvictionStrategyMemento 
extends SimpleEvictionStrategyMemento
implements Serializable
{
	private static final long serialVersionUID = 1L;

	private Long minFreeSpaceThreshold = 0L; 
	private Long targetFreeSpaceThreshold = 0L; 
	private Long maxUsedSpaceThreshold = 0L;
	private Long delay; 
	private Long interval;
	
	public StorageThresholdEvictionStrategyMemento()
	{
		super();
	}

	public StorageThresholdEvictionStrategyMemento(
		String name, 
		boolean initialized,
		long minFreeSpaceThreshold, 
		long targetFreeSpaceThreshold,
		long maxUsedSpaceThreshold,
		long delay, 
		long interval)
	{
		super(name, initialized);
		this.minFreeSpaceThreshold = minFreeSpaceThreshold;
		this.targetFreeSpaceThreshold = targetFreeSpaceThreshold;
		this.maxUsedSpaceThreshold = maxUsedSpaceThreshold;
		this.delay = delay;
		this.interval = interval;
	}

	public Long getDelay()
	{
		return this.delay;
	}

	public void setDelay(Long delay)
	{
		this.delay = delay;
	}

	public Long getInterval()
	{
		return this.interval;
	}

	public void setInterval(Long interval)
	{
		this.interval = interval;
	}

	public Long getMinFreeSpaceThreshold()
	{
		return this.minFreeSpaceThreshold;
	}

	public void setMinFreeSpaceThreshold(Long minFreeSpaceThreshold)
	{
		this.minFreeSpaceThreshold = minFreeSpaceThreshold;
	}

	public Long getTargetFreeSpaceThreshold()
	{
		return this.targetFreeSpaceThreshold;
	}

	public void setTargetFreeSpaceThreshold(Long targetFreeSpaceThreshold)
	{
		this.targetFreeSpaceThreshold = targetFreeSpaceThreshold;
	}

	public Long getMaxUsedSpaceThreshold()
	{
		return this.maxUsedSpaceThreshold;
	}

	public void setMaxUsedSpaceThreshold(Long maxUsedSpaceThreshold)
	{
		this.maxUsedSpaceThreshold = maxUsedSpaceThreshold;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(this.getClass().getSimpleName());
		sb.append("(");
		
		sb.append( "minFreeSpaceThreshold=" + getMinFreeSpaceThreshold() ); 
		sb.append(",");
		sb.append( "targetFreeSpaceThreshold=" + getTargetFreeSpaceThreshold() ); 
		sb.append(",");
		sb.append( "maxUsedSpaceThreshold=" + getMaxUsedSpaceThreshold() );
		sb.append(",");
		sb.append( "delay=" + getDelay() ); 
		sb.append(",");
		sb.append( "interval=" + getInterval() );
		
		sb.append(")");
		
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((this.delay == null) ? 0 : this.delay.hashCode());
		result = prime * result
				+ ((this.interval == null) ? 0 : this.interval.hashCode());
		result = prime
				* result
				+ ((this.maxUsedSpaceThreshold == null) ? 0
						: this.maxUsedSpaceThreshold.hashCode());
		result = prime
				* result
				+ ((this.minFreeSpaceThreshold == null) ? 0
						: this.minFreeSpaceThreshold.hashCode());
		result = prime
				* result
				+ ((this.targetFreeSpaceThreshold == null) ? 0
						: this.targetFreeSpaceThreshold.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final StorageThresholdEvictionStrategyMemento other = (StorageThresholdEvictionStrategyMemento) obj;
		if (this.delay == null)
		{
			if (other.delay != null)
				return false;
		} else if (!this.delay.equals(other.delay))
			return false;
		if (this.interval == null)
		{
			if (other.interval != null)
				return false;
		} else if (!this.interval.equals(other.interval))
			return false;
		if (this.maxUsedSpaceThreshold == null)
		{
			if (other.maxUsedSpaceThreshold != null)
				return false;
		} else if (!this.maxUsedSpaceThreshold
				.equals(other.maxUsedSpaceThreshold))
			return false;
		if (this.minFreeSpaceThreshold == null)
		{
			if (other.minFreeSpaceThreshold != null)
				return false;
		} else if (!this.minFreeSpaceThreshold
				.equals(other.minFreeSpaceThreshold))
			return false;
		if (this.targetFreeSpaceThreshold == null)
		{
			if (other.targetFreeSpaceThreshold != null)
				return false;
		} else if (!this.targetFreeSpaceThreshold
				.equals(other.targetFreeSpaceThreshold))
			return false;
		return true;
	}

}
