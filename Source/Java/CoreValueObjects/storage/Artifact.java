package gov.va.med.imaging.business.storage.hibernate;

import java.util.Set;
import java.util.HashSet;
import java.io.Serializable;
import java.util.Date;

public class Artifact implements Serializable
{
	/** 
	 * This attribute maps to the column IEN in the Artifact table.
	 */
	protected int ien;

	/** 
	 * This attribute maps to the column Size in the Artifact table.
	 */
	protected int size;

	/** 
	 * This attribute represents whether the primitive attribute size is null.
	 */
	protected boolean sizeNull = true;

	/** 
	 * This attribute maps to the column CreatedTimestamp in the Artifact table.
	 */
	protected Date createdTimestamp;

	/** 
	 * This attribute maps to the column LastRetrievedTimestamp in the Artifact table.
	 */
	protected Date lastRetrievedTimestamp;

	/** 
	 * This attribute represents the foreign key relationship to the ArtifactDescriptor table.
	 */
	protected ArtifactDescriptor artifactDescriptor;

	/**
	 * Method 'Artifact'
	 * 
	 */
	public Artifact()
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
	 * Method 'getSize'
	 * 
	 * @return int
	 */
	public int getSize()
	{
		return size;
	}

	/**
	 * Method 'setSize'
	 * 
	 * @param size
	 */
	public void setSize(int size)
	{
		this.size = size;
		this.sizeNull = false;
	}

	/** 
	 * Sets the value of sizeNull
	 */
	public void setSizeNull(boolean sizeNull)
	{
		this.sizeNull = sizeNull;
	}

	/** 
	 * Gets the value of sizeNull
	 */
	public boolean isSizeNull()
	{
		return sizeNull;
	}

	/**
	 * Method 'getCreatedTimestamp'
	 * 
	 * @return java.util.Date
	 */
	public java.util.Date getCreatedTimestamp()
	{
		return createdTimestamp;
	}

	/**
	 * Method 'setCreatedTimestamp'
	 * 
	 * @param createdTimestamp
	 */
	public void setCreatedTimestamp(java.util.Date createdTimestamp)
	{
		this.createdTimestamp = createdTimestamp;
	}

	/**
	 * Method 'getLastRetrievedTimestamp'
	 * 
	 * @return java.util.Date
	 */
	public java.util.Date getLastRetrievedTimestamp()
	{
		return lastRetrievedTimestamp;
	}

	/**
	 * Method 'setLastRetrievedTimestamp'
	 * 
	 * @param lastRetrievedTimestamp
	 */
	public void setLastRetrievedTimestamp(java.util.Date lastRetrievedTimestamp)
	{
		this.lastRetrievedTimestamp = lastRetrievedTimestamp;
	}

	/**
	 * Method 'getArtifactDescriptor'
	 * 
	 * @return ArtifactDescriptor
	 */
	public ArtifactDescriptor getArtifactDescriptor()
	{
		return artifactDescriptor;
	}

	/**
	 * Method 'setArtifactDescriptor'
	 * 
	 * @param artifactDescriptor
	 */
	public void setArtifactDescriptor(ArtifactDescriptor artifactDescriptor)
	{
		this.artifactDescriptor = artifactDescriptor;
	}

}
