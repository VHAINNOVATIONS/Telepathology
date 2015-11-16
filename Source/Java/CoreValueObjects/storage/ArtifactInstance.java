package gov.va.med.imaging.business.storage.hibernate;

import java.util.Set;
import java.util.HashSet;
import java.io.Serializable;
import java.util.Date;

public class ArtifactInstance implements Serializable
{
	/** 
	 * This attribute maps to the column IEN in the ArtifactInstance table.
	 */
	protected int ien;

	/** 
	 * This attribute maps to the column CreatedTimestamp in the ArtifactInstance table.
	 */
	protected Date createdTimestamp;

	/** 
	 * This attribute maps to the column LastRetrievedTimestamp in the ArtifactInstance table.
	 */
	protected Date lastRetrievedTimestamp;

	/** 
	 * This attribute represents the foreign key relationship to the Artifact table.
	 */
	protected Artifact artifact;

	/** 
	 * This attribute represents the foreign key relationship to the Provider table.
	 */
	protected Provider provider;

	/**
	 * Method 'ArtifactInstance'
	 * 
	 */
	public ArtifactInstance()
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
