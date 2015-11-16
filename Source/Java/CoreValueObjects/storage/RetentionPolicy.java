package gov.va.med.imaging.business.storage.hibernate;

import java.util.Set;
import java.util.HashSet;
import java.io.Serializable;

public class RetentionPolicy implements Serializable
{
	/** 
	 * This attribute maps to the column IEN in the RetentionPolicy table.
	 */
	protected int ien;

	/** 
	 * This attribute maps to the column MinimumArchiveCopies in the RetentionPolicy table.
	 */
	protected int minimumArchiveCopies;

	/** 
	 * This attribute represents whether the primitive attribute minimumArchiveCopies is null.
	 */
	protected boolean minimumArchiveCopiesNull = true;

	/** 
	 * This attribute maps to the column MinimumOffsiteCopies in the RetentionPolicy table.
	 */
	protected int minimumOffsiteCopies;

	/** 
	 * This attribute represents whether the primitive attribute minimumOffsiteCopies is null.
	 */
	protected boolean minimumOffsiteCopiesNull = true;

	/** 
	 * This attribute maps to the column IsActive in the RetentionPolicy table.
	 */
	protected short isActive;

	/** 
	 * This attribute represents whether the primitive attribute isActive is null.
	 */
	protected boolean isActiveNull = true;

	/**
	 * Method 'RetentionPolicy'
	 * 
	 */
	public RetentionPolicy()
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
	 * Method 'getMinimumArchiveCopies'
	 * 
	 * @return int
	 */
	public int getMinimumArchiveCopies()
	{
		return minimumArchiveCopies;
	}

	/**
	 * Method 'setMinimumArchiveCopies'
	 * 
	 * @param minimumArchiveCopies
	 */
	public void setMinimumArchiveCopies(int minimumArchiveCopies)
	{
		this.minimumArchiveCopies = minimumArchiveCopies;
		this.minimumArchiveCopiesNull = false;
	}

	/** 
	 * Sets the value of minimumArchiveCopiesNull
	 */
	public void setMinimumArchiveCopiesNull(boolean minimumArchiveCopiesNull)
	{
		this.minimumArchiveCopiesNull = minimumArchiveCopiesNull;
	}

	/** 
	 * Gets the value of minimumArchiveCopiesNull
	 */
	public boolean isMinimumArchiveCopiesNull()
	{
		return minimumArchiveCopiesNull;
	}

	/**
	 * Method 'getMinimumOffsiteCopies'
	 * 
	 * @return int
	 */
	public int getMinimumOffsiteCopies()
	{
		return minimumOffsiteCopies;
	}

	/**
	 * Method 'setMinimumOffsiteCopies'
	 * 
	 * @param minimumOffsiteCopies
	 */
	public void setMinimumOffsiteCopies(int minimumOffsiteCopies)
	{
		this.minimumOffsiteCopies = minimumOffsiteCopies;
		this.minimumOffsiteCopiesNull = false;
	}

	/** 
	 * Sets the value of minimumOffsiteCopiesNull
	 */
	public void setMinimumOffsiteCopiesNull(boolean minimumOffsiteCopiesNull)
	{
		this.minimumOffsiteCopiesNull = minimumOffsiteCopiesNull;
	}

	/** 
	 * Gets the value of minimumOffsiteCopiesNull
	 */
	public boolean isMinimumOffsiteCopiesNull()
	{
		return minimumOffsiteCopiesNull;
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
