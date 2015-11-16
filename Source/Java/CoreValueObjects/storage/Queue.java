package gov.va.med.imaging.business.storage.hibernate;

import java.util.Set;
import java.util.HashSet;
import java.io.Serializable;

public class Queue implements Serializable
{
	/** 
	 * This attribute maps to the column IEN in the Queue table.
	 */
	protected int ien;

	/** 
	 * This attribute maps to the column IsActive in the Queue table.
	 */
	protected short isActive;

	/** 
	 * This attribute represents whether the primitive attribute isActive is null.
	 */
	protected boolean isActiveNull = true;

	/**
	 * Method 'Queue'
	 * 
	 */
	public Queue()
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
	 * Method 'getIsActive'
	 * 
	 * @return short
	 */
	public short getIsActive()
	{
		return isActive;
	}

	/**
	 * Method 'setIsActive'
	 * 
	 * @param isActive
	 */
	public void setIsActive(short isActive)
	{
		this.isActive = isActive;
		this.isActiveNull = false;
	}

	/** 
	 * Sets the value of isActiveNull
	 */
	public void setIsActiveNull(boolean isActiveNull)
	{
		this.isActiveNull = isActiveNull;
	}

	/** 
	 * Gets the value of isActiveNull
	 */
	public boolean isIsActiveNull()
	{
		return isActiveNull;
	}

}
