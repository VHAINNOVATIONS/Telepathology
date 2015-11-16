package gov.va.med.imaging.business.storage.hibernate;

import java.util.Set;
import java.util.HashSet;
import java.io.Serializable;

public class Provider implements Serializable
{
	/** 
	 * This attribute maps to the column IEN in the Provider table.
	 */
	protected int ien;

	/** 
	 * This attribute maps to the column IsActive in the Provider table.
	 */
	protected short isActive;

	/** 
	 * This attribute represents whether the primitive attribute isActive is null.
	 */
	protected boolean isActiveNull = true;

	/** 
	 * This attribute maps to the column IsArchive in the Provider table.
	 */
	protected short isArchive;

	/** 
	 * This attribute represents whether the primitive attribute isArchive is null.
	 */
	protected boolean isArchiveNull = true;

	/** 
	 * This attribute maps to the column IsPrimaryStorage in the Provider table.
	 */
	protected short isPrimaryStorage;

	/** 
	 * This attribute represents whether the primitive attribute isPrimaryStorage is null.
	 */
	protected boolean isPrimaryStorageNull = true;

	/** 
	 * This attribute maps to the column IsWritable in the Provider table.
	 */
	protected short isWritable;

	/** 
	 * This attribute represents whether the primitive attribute isWritable is null.
	 */
	protected boolean isWritableNull = true;

	/** 
	 * This attribute represents the foreign key relationship to the Place table.
	 */
	protected Place place;

	/**
	 * Method 'Provider'
	 * 
	 */
	public Provider()
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

	/**
	 * Method 'getIsArchive'
	 * 
	 * @return short
	 */
	public short getIsArchive()
	{
		return isArchive;
	}

	/**
	 * Method 'setIsArchive'
	 * 
	 * @param isArchive
	 */
	public void setIsArchive(short isArchive)
	{
		this.isArchive = isArchive;
		this.isArchiveNull = false;
	}

	/** 
	 * Sets the value of isArchiveNull
	 */
	public void setIsArchiveNull(boolean isArchiveNull)
	{
		this.isArchiveNull = isArchiveNull;
	}

	/** 
	 * Gets the value of isArchiveNull
	 */
	public boolean isIsArchiveNull()
	{
		return isArchiveNull;
	}

	/**
	 * Method 'getIsPrimaryStorage'
	 * 
	 * @return short
	 */
	public short getIsPrimaryStorage()
	{
		return isPrimaryStorage;
	}

	/**
	 * Method 'setIsPrimaryStorage'
	 * 
	 * @param isPrimaryStorage
	 */
	public void setIsPrimaryStorage(short isPrimaryStorage)
	{
		this.isPrimaryStorage = isPrimaryStorage;
		this.isPrimaryStorageNull = false;
	}

	/** 
	 * Sets the value of isPrimaryStorageNull
	 */
	public void setIsPrimaryStorageNull(boolean isPrimaryStorageNull)
	{
		this.isPrimaryStorageNull = isPrimaryStorageNull;
	}

	/** 
	 * Gets the value of isPrimaryStorageNull
	 */
	public boolean isIsPrimaryStorageNull()
	{
		return isPrimaryStorageNull;
	}

	/**
	 * Method 'getIsWritable'
	 * 
	 * @return short
	 */
	public short getIsWritable()
	{
		return isWritable;
	}

	/**
	 * Method 'setIsWritable'
	 * 
	 * @param isWritable
	 */
	public void setIsWritable(short isWritable)
	{
		this.isWritable = isWritable;
		this.isWritableNull = false;
	}

	/** 
	 * Sets the value of isWritableNull
	 */
	public void setIsWritableNull(boolean isWritableNull)
	{
		this.isWritableNull = isWritableNull;
	}

	/** 
	 * Gets the value of isWritableNull
	 */
	public boolean isIsWritableNull()
	{
		return isWritableNull;
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

}
