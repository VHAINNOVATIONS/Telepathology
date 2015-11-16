package gov.va.med.imaging.business.storage.hibernate;

import java.util.Set;
import java.util.HashSet;
import java.io.Serializable;

public class Place implements Serializable
{
	/** 
	 * This attribute maps to the column IEN in the Place table.
	 */
	protected int ien;

	/**
	 * Method 'Place'
	 * 
	 */
	public Place()
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

}
