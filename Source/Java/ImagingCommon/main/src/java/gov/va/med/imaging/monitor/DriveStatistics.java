package gov.va.med.imaging.monitor;

/**
 * A simple value object of collected drive statistics.
 * 
 * @author vhaiswbeckec
 *
 */
class DriveStatistics
{
	private String driveIdentifier;
	private long bytesFree;
	private long bytesTotal;
	
	public DriveStatistics(String driveIdentifier, long bytesFree, long bytesTotal)
	{
		super();
		this.driveIdentifier = driveIdentifier;
		this.bytesFree = bytesFree;
		this.bytesTotal = bytesTotal;
	}

	public long getBytesFree()
	{
		return this.bytesFree;
	}

	public long getBytesTotal()
	{
		return this.bytesTotal;
	}

	public long getBytesUsed()
	{
		return this.bytesTotal - this.bytesFree;
	}
	
	public String getDriveIdentifier()
	{
		return this.driveIdentifier;
	}

	/**
	 * not that we cannot calc percentages if the total bytes is not available
	 * for used space we define it as 100%, for free space we assume it is 0%
	 * @return
	 */
	public long getFreePercentage()
	{
		return getBytesTotal()==0L ? 
			0L : 
			(long)((float)(getBytesFree()) / (float)(getBytesTotal()) * 100.0);
	}
	
	public long getUsedPercentage()
	{
		return getBytesTotal()==0L ? 
			100L : 
			(long)((float)(getBytesUsed()) / (float)(getBytesTotal()) * 100.0);
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Drive ");
		sb.append(driveIdentifier);
		sb.append(": total bytes ");
		sb.append(getBytesTotal());
		
		sb.append(", bytes used ");
		sb.append(getBytesUsed());
		sb.append("(");
		sb .append(getUsedPercentage());
		sb.append("%)");
		
		sb.append(", bytes free ");
		sb.append(getBytesFree());
		sb.append("(");
		sb .append(getFreePercentage());
		sb.append("%)");
		
		return sb.toString();
	}
}