/**
 * 
 */
package gov.va.med.imaging.monitor;

import java.io.Serializable;

/**
 * @author VHAISWBECKEC
 *
 */
public class DriveLimitMemento
implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String driveIdentifier;
	private String limitType;
	private String limitStatistic;
	private String limit;

	public static DriveLimitMemento create(DriveLimit driveLimit)
	{
		DriveLimitMemento memento = new DriveLimitMemento();
		
		memento.driveIdentifier = driveLimit.getDriveIdentifier();
		memento.limit = Long.toString( driveLimit.getLimit() );
		memento.limitStatistic = driveLimit.getLimitStatistic()==null ? null : driveLimit.getLimitStatistic().toString();
		memento.limitType = driveLimit.getLimitType()==null ? null : driveLimit.getLimitType().toString();
		
		return memento;
	}

	public DriveLimitMemento()
	{
		
	}
	
	public String getDriveIdentifier()
	{
		return this.driveIdentifier;
	}

	public void setDriveIdentifier(String driveIdentifier)
	{
		this.driveIdentifier = driveIdentifier;
	}

	public String getLimit()
	{
		return this.limit;
	}

	public void setLimit(String limit)
	{
		this.limit = limit;
	}

	public String getLimitStatistic()
	{
		return this.limitStatistic;
	}

	public void setLimitStatistic(String limitStatistic)
	{
		this.limitStatistic = limitStatistic;
	}

	public String getLimitType()
	{
		return this.limitType;
	}

	public void setLimitType(String limitType)
	{
		this.limitType = limitType;
	}
	
	
}
