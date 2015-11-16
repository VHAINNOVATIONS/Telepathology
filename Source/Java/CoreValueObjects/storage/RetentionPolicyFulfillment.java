package gov.va.med.imaging.business.storage.hibernate;

import java.util.Set;
import java.util.HashSet;
import java.io.Serializable;
import java.util.Date;

public class RetentionPolicyFulfillment implements Serializable
{
	/** 
	 * This attribute maps to the column IEN in the RetentionPolicyFulfillment table.
	 */
	protected int ien;

	/** 
	 * This attribute maps to the column CreatedTimestamp in the RetentionPolicyFulfillment table.
	 */
	protected Date createdTimestamp;

	/** 
	 * This attribute represents the foreign key relationship to the ArtifactInstance table.
	 */
	protected ArtifactInstance artifactInstance;

	/** 
	 * This attribute represents the foreign key relationship to the ArtifactRetentionPolicy table.
	 */
	protected ArtifactRetentionPolicy artifactRetentionPolicy;

	/**
	 * Method 'RetentionPolicyFulfillment'
	 * 
	 */
	public RetentionPolicyFulfillment()
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
	 * Method 'getArtifactInstance'
	 * 
	 * @return ArtifactInstance
	 */
	public ArtifactInstance getArtifactInstance()
	{
		return artifactInstance;
	}

	/**
	 * Method 'setArtifactInstance'
	 * 
	 * @param artifactInstance
	 */
	public void setArtifactInstance(ArtifactInstance artifactInstance)
	{
		this.artifactInstance = artifactInstance;
	}

	/**
	 * Method 'getArtifactRetentionPolicy'
	 * 
	 * @return ArtifactRetentionPolicy
	 */
	public ArtifactRetentionPolicy getArtifactRetentionPolicy()
	{
		return artifactRetentionPolicy;
	}

	/**
	 * Method 'setArtifactRetentionPolicy'
	 * 
	 * @param artifactRetentionPolicy
	 */
	public void setArtifactRetentionPolicy(ArtifactRetentionPolicy artifactRetentionPolicy)
	{
		this.artifactRetentionPolicy = artifactRetentionPolicy;
	}

}
