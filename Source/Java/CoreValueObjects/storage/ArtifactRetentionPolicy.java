package gov.va.med.imaging.business.storage.hibernate;

import java.util.Set;
import java.util.HashSet;
import java.io.Serializable;
import java.util.Date;

public class ArtifactRetentionPolicy implements Serializable
{
	/** 
	 * This attribute maps to the column IEN in the ArtifactRetentionPolicy table.
	 */
	protected int ien;

	/** 
	 * This attribute maps to the column ArtifactInstanceIEN in the ArtifactRetentionPolicy table.
	 */
	protected int artifactInstanceIEN;

	/** 
	 * This attribute represents whether the primitive attribute artifactInstanceIEN is null.
	 */
	protected boolean artifactInstanceIENNull = true;

	/** 
	 * This attribute maps to the column CreatedTimestamp in the ArtifactRetentionPolicy table.
	 */
	protected Date createdTimestamp;

	/** 
	 * This attribute maps to the column IsActive in the ArtifactRetentionPolicy table.
	 */
	protected short isActive;

	/** 
	 * This attribute represents whether the primitive attribute isActive is null.
	 */
	protected boolean isActiveNull = true;

	/** 
	 * This attribute maps to the column IsSatisfied in the ArtifactRetentionPolicy table.
	 */
	protected short isSatisfied;

	/** 
	 * This attribute represents whether the primitive attribute isSatisfied is null.
	 */
	protected boolean isSatisfiedNull = true;

	/** 
	 * This attribute maps to the column SatisfiedTimestamp in the ArtifactRetentionPolicy table.
	 */
	protected Date satisfiedTimestamp;

	/** 
	 * This attribute represents the foreign key relationship to the Artifact table.
	 */
	protected Artifact artifact;

	/** 
	 * This attribute represents the foreign key relationship to the RetentionPolicy table.
	 */
	protected RetentionPolicy retentionPolicy;

	/**
	 * Method 'ArtifactRetentionPolicy'
	 * 
	 */
	public ArtifactRetentionPolicy()
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
	 * Method 'getArtifactInstanceIEN'
	 * 
	 * @return int
	 */
	public int getArtifactInstanceIEN()
	{
		return artifactInstanceIEN;
	}

	/**
	 * Method 'setArtifactInstanceIEN'
	 * 
	 * @param artifactInstanceIEN
	 */
	public void setArtifactInstanceIEN(int artifactInstanceIEN)
	{
		this.artifactInstanceIEN = artifactInstanceIEN;
		this.artifactInstanceIENNull = false;
	}

	/** 
	 * Sets the value of artifactInstanceIENNull
	 */
	public void setArtifactInstanceIENNull(boolean artifactInstanceIENNull)
	{
		this.artifactInstanceIENNull = artifactInstanceIENNull;
	}

	/** 
	 * Gets the value of artifactInstanceIENNull
	 */
	public boolean isArtifactInstanceIENNull()
	{
		return artifactInstanceIENNull;
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
	 * Method 'getIsSatisfied'
	 * 
	 * @return short
	 */
	public short getIsSatisfied()
	{
		return isSatisfied;
	}

	/**
	 * Method 'setIsSatisfied'
	 * 
	 * @param isSatisfied
	 */
	public void setIsSatisfied(short isSatisfied)
	{
		this.isSatisfied = isSatisfied;
		this.isSatisfiedNull = false;
	}

	/** 
	 * Sets the value of isSatisfiedNull
	 */
	public void setIsSatisfiedNull(boolean isSatisfiedNull)
	{
		this.isSatisfiedNull = isSatisfiedNull;
	}

	/** 
	 * Gets the value of isSatisfiedNull
	 */
	public boolean isIsSatisfiedNull()
	{
		return isSatisfiedNull;
	}

	/**
	 * Method 'getSatisfiedTimestamp'
	 * 
	 * @return java.util.Date
	 */
	public java.util.Date getSatisfiedTimestamp()
	{
		return satisfiedTimestamp;
	}

	/**
	 * Method 'setSatisfiedTimestamp'
	 * 
	 * @param satisfiedTimestamp
	 */
	public void setSatisfiedTimestamp(java.util.Date satisfiedTimestamp)
	{
		this.satisfiedTimestamp = satisfiedTimestamp;
	}

	/**
	 * Method 'getArtifact'
	 * 
	 * @return Artifact
	 */
	public Artifact getArtifact()
	{
		return artifact;
	}

	/**
	 * Method 'setArtifact'
	 * 
	 * @param artifact
	 */
	public void setArtifact(Artifact artifact)
	{
		this.artifact = artifact;
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
