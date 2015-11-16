/**
 * 
 */
package gov.va.med.imaging.monitor;

import java.io.Serializable;

/**
 * @author vhaiswbeckec
 *
 *
 * NOTE: do not implement .equals as anything other than the default Object implementation or
 * something that provides uniqueness for each instance. i.e. .equals is equivalent to ==
 */
public class DriveLimit
{
	public enum LimitType
	{
		NOTIFY_OVER("over"), NOTIFY_UNDER("under"), MONITOR("at");
		
		private String description;
		LimitType(String description)
		{
			this.description = description;
		}
		public String getDescription()
		{
			return description;
		}
	}
	
	public enum LimitStatistic
	{
		TOTAL_SPACE, FREE_SPACE, USED_SPACE, FREE_SPACE_PERCENTAGE, USED_SPACE_PERCENTAGE
	}

	/**
	 * 
	 * @param driveIdentifier
	 * @param limitType
	 * @param limitStatistic
	 * @param limit
	 * @return
	 * @throws DriveLimitInitializationException
	 */
	public static DriveLimit create(String driveIdentifier, LimitType limitType, LimitStatistic limitStatistic, long limit)
	throws DriveLimitInitializationException
	{
		if(limit < 0)
			throw new DriveLimitInitializationException("Limit must be a positive integer value");
		if(LimitStatistic.FREE_SPACE_PERCENTAGE == limitStatistic && limit > 100)
			throw new DriveLimitInitializationException("Limit for percentage statistics must be a positive integer value less than or equal to 100");
		if(LimitStatistic.USED_SPACE_PERCENTAGE == limitStatistic && limit > 100)
			throw new DriveLimitInitializationException("Limit for percentage statistics must be a positive integer value less than or equal to 100");
		
		return new DriveLimit(
				driveIdentifier == null ? null : driveIdentifier.substring(0,1), 
				limitType, limitStatistic, limit);
	}
	
	/**
	 * An all-string factory method to build instances from properties.
	 * 
	 * @param driveIdentifier
	 * @param limitTypeString
	 * @param limitStatisticString
	 * @param limitString
	 * @return
	 * @throws DriveLimitInitializationException
	 */
	public static DriveLimit create(String driveIdentifier, String limitTypeString, String limitStatisticString, String limitString)
	throws DriveLimitInitializationException
	{
		if(driveIdentifier == null)
			throw new DriveLimitInitializationException("Required parameter 'driveIdentifier' is null");
		if(limitTypeString == null)
			throw new DriveLimitInitializationException("Required parameter 'limitTypeString' is null");
		if(limitStatisticString == null)
			throw new DriveLimitInitializationException("Required parameter 'limitStatisticString' is null");
		if(limitString == null)
			throw new DriveLimitInitializationException("Required parameter 'limitString' is null");
		
		try
		{
			LimitType limitType = LimitType.valueOf(limitTypeString);
			LimitStatistic limitStatistic = LimitStatistic.valueOf(limitStatisticString);
			long limit = Long.parseLong(limitString);
			
			return create(driveIdentifier, limitType, limitStatistic, limit);
		} 
		catch (NumberFormatException x)
		{
			throw new DriveLimitInitializationException("The limit value '" + limitString + "' must be a positive integer.");
		}
		catch (IllegalArgumentException x)
		{
			throw new DriveLimitInitializationException(
				"Either '" + limitTypeString + "' is not a valid LimitType or '" + limitStatisticString + "' is not a valid LimitStatistic.");
		}
	}
	
	public static DriveLimit create(DriveLimitMemento memento)
	throws DriveLimitInitializationException
	{
		return create(memento.getDriveIdentifier(), memento.getLimitType(), memento.getLimitStatistic(), memento.getLimit());
	}	
	
	private String driveIdentifier;
	private LimitType limitType;
	private LimitStatistic limitStatistic;
	private long limit;
	
	private DriveLimit()
	{
	}
	
	private DriveLimit(String driveIdentifier, LimitType limitType, LimitStatistic limitStatistic, long limit)
	throws DriveLimitInitializationException
	{
		super();
		this.driveIdentifier = driveIdentifier.substring(0,1);		// the first char only
		this.limit = limit;
		this.limitType = limitType;
		this.limitStatistic = limitStatistic;
	}

	
	/**
	 * Accessors are provided predominantly for serialization/deserialization with XMLEncoder
	 * Normally the create() method should be used and the instances treated as being immutable.
	 * 
	 * @return
	 */
	public String getDriveIdentifier()
	{
		return this.driveIdentifier;
	}

	public void setDriveIdentifier(String driveIdentifier)
	{
		this.driveIdentifier = driveIdentifier;
	}

	public long getLimit()
	{
		return this.limit;
	}

	public void setLimit(long limit)
	{
		this.limit = limit;
	}

	public LimitStatistic getLimitStatistic()
	{
		return this.limitStatistic;
	}

	public void setLimitStatistic(LimitStatistic limitStatistic)
	{
		this.limitStatistic = limitStatistic;
	}

	public LimitType getLimitType()
	{
		return this.limitType;
	}

	public void setLimitType(LimitType limitType)
	{
		this.limitType = limitType;
	}

	public DriveLimitMemento getMemento()
	{
		return DriveLimitMemento.create(this);
	}
	
	/**
	 * 
	 * @param stats
	 * @return
	 */
	public boolean isOutOfLimit(DriveStatistics stats)
	{
		if(stats == null || stats.getDriveIdentifier() == null || ! stats.getDriveIdentifier().equalsIgnoreCase(this.driveIdentifier))
			return false;
		
		// MONITOR returns true if the drive identifier is the same
		if( LimitType.MONITOR==this.limitType )
			return true;
		
		if(LimitStatistic.TOTAL_SPACE == this.limitStatistic)
		{
			if(stats.getBytesTotal() < 0)
				return false;
			else 
				return 
					(LimitType.NOTIFY_OVER==this.limitType && stats.getBytesTotal() > limit) || 
					(LimitType.NOTIFY_UNDER==this.limitType && stats.getBytesTotal() < limit);
		}
		
		if(LimitStatistic.USED_SPACE == this.limitStatistic)
		{
			if(stats.getBytesUsed() < 0)
				return false;
			else 
				return 
					(LimitType.NOTIFY_OVER==this.limitType && stats.getBytesUsed() > limit) || 
					(LimitType.NOTIFY_UNDER==this.limitType && stats.getBytesUsed() < limit);
		}
		
		if(LimitStatistic.FREE_SPACE == this.limitStatistic)
		{
			if(stats.getBytesFree() < 0)
				return false;
			else 
				return 
					(LimitType.NOTIFY_OVER==this.limitType && stats.getBytesFree() > limit) || 
					(LimitType.NOTIFY_UNDER==this.limitType && stats.getBytesFree() < limit);
		}
		
		if(LimitStatistic.USED_SPACE_PERCENTAGE == this.limitStatistic)
		{
			return 
				(LimitType.NOTIFY_OVER==this.limitType && stats.getUsedPercentage() > limit) || 
				(LimitType.NOTIFY_UNDER==this.limitType && stats.getUsedPercentage() < limit);
		}
		
		if(LimitStatistic.FREE_SPACE_PERCENTAGE == this.limitStatistic)
		{
			if(stats.getBytesFree() < 0)
				return false;
			else 
				return 
					(LimitType.NOTIFY_OVER==this.limitType && stats.getFreePercentage() > limit) || 
					(LimitType.NOTIFY_UNDER==this.limitType && stats.getFreePercentage() < limit);
		}
		
		return false;
	}
	
	public String getExceedLimitMessage()
	{
		return LimitType.MONITOR == this.limitType ?
			this.driveIdentifier +  " MONITOR" :
			this.driveIdentifier + " is " + this.limitType.getDescription() + " the limit of " + this.limit + 
			(LimitStatistic.FREE_SPACE_PERCENTAGE == this.limitStatistic || LimitStatistic.USED_SPACE_PERCENTAGE == this.limitStatistic ?
					"%" : ""); 
	}

	@Override
	public int hashCode()
	{
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((this.driveIdentifier == null) ? 0 : this.driveIdentifier.hashCode());
		result = PRIME * result + (int) (this.limit ^ (this.limit >>> 32));
		result = PRIME * result + ((this.limitStatistic == null) ? 0 : this.limitStatistic.hashCode());
		result = PRIME * result + ((this.limitType == null) ? 0 : this.limitType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final DriveLimit other = (DriveLimit) obj;
		if (this.driveIdentifier == null)
		{
			if (other.driveIdentifier != null)
				return false;
		} else if (!this.driveIdentifier.equals(other.driveIdentifier))
			return false;
		if (this.limit != other.limit)
			return false;
		if (this.limitStatistic == null)
		{
			if (other.limitStatistic != null)
				return false;
		} else if (this.limitStatistic != other.limitStatistic)
			return false;
		if (this.limitType == null)
		{
			if (other.limitType != null)
				return false;
		} else if (this.limitType != other.limitType)
			return false;
		return true;
	}
	
}
