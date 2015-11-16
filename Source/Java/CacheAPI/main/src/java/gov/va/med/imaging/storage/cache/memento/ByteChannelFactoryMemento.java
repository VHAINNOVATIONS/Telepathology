/**
 * 
 */
package gov.va.med.imaging.storage.cache.memento;


import java.io.Serializable;

public class ByteChannelFactoryMemento
implements Serializable
{
	private static final long serialVersionUID = 1L;
	private Long maxChannelOpenDuration;
	private Long sweepTime;
	private String checksumAlgorithmName;
	
	public ByteChannelFactoryMemento()
	{
		
	}
	
	public Long getMaxChannelOpenDuration()
	{
		return this.maxChannelOpenDuration;
	}

	public void setMaxChannelOpenDuration(Long maxChannelOpenDuration)
	{
		this.maxChannelOpenDuration = maxChannelOpenDuration;
	}

	public Long getSweepTime()
	{
		return this.sweepTime;
	}

	public void setSweepTime(Long sweepTime)
	{
		this.sweepTime = sweepTime;
	}

	public String getChecksumAlgorithmName()
	{
		return this.checksumAlgorithmName;
	}

	public void setChecksumAlgorithmName(String checksumAlgorithmName)
	{
		this.checksumAlgorithmName = checksumAlgorithmName;
	}
}