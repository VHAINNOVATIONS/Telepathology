package gov.va.med.imaging.business.storage.hibernate;

import java.util.Set;
import java.util.HashSet;
import java.io.Serializable;
import java.util.Date;

public class ArtifactTransaction implements Serializable
{
	/** 
	 * This attribute maps to the column IEN in the ArtifactTransaction table.
	 */
	protected int ien;

	/** 
	 * This attribute maps to the column Status in the ArtifactTransaction table.
	 */
	protected int status;

	/** 
	 * This attribute represents whether the primitive attribute status is null.
	 */
	protected boolean statusNull = true;

	/** 
	 * This attribute maps to the column TransactionTimestamp in the ArtifactTransaction table.
	 */
	protected Date transactionTimestamp;

	/** 
	 * This attribute represents the foreign key relationship to the Artifact table.
	 */
	protected Artifact artifact;

	/** 
	 * This attribute represents the foreign key relationship to the Provider table.
	 */
	protected Provider provider;

	/**
	 * Method 'ArtifactTransaction'
	 * 
	 */
	public ArtifactTransaction()
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
	 * Method 'getStatus'
	 * 
	 * @return int
	 */
	public int getStatus()
	{
		return status;
	}

	/**
	 * Method 'setStatus'
	 * 
	 * @param status
	 */
	public void setStatus(int status)
	{
		this.status = status;
		this.statusNull = false;
	}

	/** 
	 * Sets the value of statusNull
	 */
	public void setStatusNull(boolean statusNull)
	{
		this.statusNull = statusNull;
	}

	/** 
	 * Gets the value of statusNull
	 */
	public boolean isStatusNull()
	{
		return statusNull;
	}

	/**
	 * Method 'getTransactionTimestamp'
	 * 
	 * @return java.util.Date
	 */
	public java.util.Date getTransactionTimestamp()
	{
		return transactionTimestamp;
	}

	/**
	 * Method 'setTransactionTimestamp'
	 * 
	 * @param transactionTimestamp
	 */
	public void setTransactionTimestamp(java.util.Date transactionTimestamp)
	{
		this.transactionTimestamp = transactionTimestamp;
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
