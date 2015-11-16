package gov.va.med.imaging.business.storage.hibernate;

import java.util.Set;
import java.util.HashSet;
import java.io.Serializable;
import java.util.Date;

public class ProviderAvailability implements Serializable
{
	/** 
	 * This attribute maps to the column IEN in the ProviderAvailability table.
	 */
	protected int ien;

	/** 
	 * This attribute maps to the column StartTime in the ProviderAvailability table.
	 */
	protected Date startTime;

	/** 
	 * This attribute maps to the column EndTime in the ProviderAvailability table.
	 */
	protected Date endTime;

	/** 
	 * This attribute represents the foreign key relationship to the Place table.
	 */
	protected Place place;

	/** 
	 * This attribute represents the foreign key relationship to the Provider table.
	 */
	protected Provider provider;

	/**
	 * Method 'ProviderAvailability'
	 * 
	 */
	public ProviderAvailability()
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
	 * Method 'getEndTime'
	 * 
	 * @return java.util.Date
	 */
	public java.util.Date getEndTime()
	{
		return endTime;
	}

	/**
	 * Method 'setEndTime'
	 * 
	 * @param endTime
	 */
	public void setEndTime(java.util.Date endTime)
	{
		this.endTime = endTime;
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
