package gov.va.med.imaging.business.storage.hibernate;

import java.util.Set;
import java.util.HashSet;
import java.io.Serializable;
import java.util.Date;

public class TransferStatistics implements Serializable
{
	/** 
	 * This attribute maps to the column IEN in the TransferStatistics table.
	 */
	protected int ien;

	/** 
	 * This attribute maps to the column StartTime in the TransferStatistics table.
	 */
	protected Date startTime;

	/** 
	 * This attribute maps to the column DurationInMilliseconds in the TransferStatistics table.
	 */
	protected int durationInMilliseconds;

	/** 
	 * This attribute represents whether the primitive attribute durationInMilliseconds is null.
	 */
	protected boolean durationInMillisecondsNull = true;

	/** 
	 * This attribute maps to the column SizeInBytes in the TransferStatistics table.
	 */
	protected int sizeInBytes;

	/** 
	 * This attribute represents whether the primitive attribute sizeInBytes is null.
	 */
	protected boolean sizeInBytesNull = true;

	/** 
	 * This attribute represents the foreign key relationship to the Place table.
	 */
	protected Place place;

	/** 
	 * This attribute represents the foreign key relationship to the Provider table.
	 */
	protected Provider provider;

	/**
	 * Method 'TransferStatistics'
	 * 
	 */
	public TransferStatistics()
	{
	}

	/**
	 * Method 'getIen'
	 * 
	 * @return int
	 */
	public int getIen()
	{
		return ien;
	}

	/**
	 * Method 'setIen'
	 * 
	 * @param ien
	 */
	public void setIen(int ien)
	{
		this.ien = ien;
	}

	/**
	 * Method 'getStartTime'
	 * 
	 * @return java.util.Date
	 */
	public java.util.Date getStartTime()
	{
		return startTime;
	}

	/**
	 * Method 'setStartTime'
	 * 
	 * @param startTime
	 */
	public void setStartTime(java.util.Date startTime)
	{
		this.startTime = startTime;
	}

	/**
	 * Method 'getDurationInMilliseconds'
	 * 
	 * @return int
	 */
	public int getDurationInMilliseconds()
	{
		return durationInMilliseconds;
	}

	/**
	 * Method 'setDurationInMilliseconds'
	 * 
	 * @param durationInMilliseconds
	 */
	public void setDurationInMilliseconds(int durationInMilliseconds)
	{
		this.durationInMilliseconds = durationInMilliseconds;
		this.durationInMillisecondsNull = false;
	}

	/** 
	 * Sets the value of durationInMillisecondsNull
	 */
	public void setDurationInMillisecondsNull(boolean durationInMillisecondsNull)
	{
		this.durationInMillisecondsNull = durationInMillisecondsNull;
	}

	/** 
	 * Gets the value of durationInMillisecondsNull
	 */
	public boolean isDurationInMillisecondsNull()
	{
		return durationInMillisecondsNull;
	}

	/**
	 * Method 'getSizeInBytes'
	 * 
	 * @return int
	 */
	public int getSizeInBytes()
	{
		return sizeInBytes;
	}

	/**
	 * Method 'setSizeInBytes'
	 * 
	 * @param sizeInBytes
	 */
	public void setSizeInBytes(int sizeInBytes)
	{
		this.sizeInBytes = sizeInBytes;
		this.sizeInBytesNull = false;
	}

	/** 
	 * Sets the value of sizeInBytesNull
	 */
	public void setSizeInBytesNull(boolean sizeInBytesNull)
	{
		this.sizeInBytesNull = sizeInBytesNull;
	}

	/** 
	 * Gets the value of sizeInBytesNull
	 */
	public boolean isSizeInBytesNull()
	{
		return sizeInBytesNull;
	}

	/**
	 * Method 'getPlace'
	 * 
	 * @return Place
	 */
	public Place getPlace()
	{
		return place;
	}

	/**
	 * Method 'setPlace'
	 * 
	 * @param place
	 */
	public void setPlace(Place place)
	{
		this.place = place;
	}

	/**
	 * Method 'getProvider'
	 * 
	 * @return Provider
	 */
	public Provider getProvider()
	{
		return provider;
	}

	/**
	 * Method 'setProvider'
	 * 
	 * @param provider
	 */
	public void setProvider(Provider provider)
	{
		this.provider = provider;
	}

}
