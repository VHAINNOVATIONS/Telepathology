package gov.va.med.imaging.business.storage.hibernate;

import java.util.Set;
import java.util.HashSet;
import java.io.Serializable;

public class ArtifactDescriptor implements Serializable
{
	/** 
	 * This attribute maps to the column IEN in the ArtifactDescriptor table.
	 */
	protected int ien;

	/** 
	 * This attribute maps to the column IsActive in the ArtifactDescriptor table.
	 */
	protected short isActive;

	/** 
	 * This attribute represents whether the primitive attribute isActive is null.
	 */
	protected boolean isActiveNull = true;

	/** 
	 * This attribute represents the foreign key relationship to the RetentionPolicy table.
	 */
	protected RetentionPolicy retentionPolicy;

	/**
	 * Method 'ArtifactDescriptor'
	 * 
	 */
	public ArtifactDescriptor()
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
	 * Method 'getRetentionPolicy'
	 * 
	 * @return RetentionPolicy
	 */
	public RetentionPolicy getRetentionPolicy()
	{
		return retentionPolicy;
	}

	/**
	 * Method 'setRetentionPolicy'
	 * 
	 * @param retentionPolicy
	 */
	public void setRetentionPolicy(RetentionPolicy retentionPolicy)
	{
		this.retentionPolicy = retentionPolicy;
	}

}
