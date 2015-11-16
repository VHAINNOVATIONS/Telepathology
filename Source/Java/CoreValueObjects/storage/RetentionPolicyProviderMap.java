package gov.va.med.imaging.business.storage.hibernate;

import java.util.Set;
import java.util.HashSet;
import java.io.Serializable;

public class RetentionPolicyProviderMap implements Serializable
{
	/** 
	 * This attribute maps to the column IEN in the RetentionPolicyProviderMap table.
	 */
	protected int ien;

	/** 
	 * This attribute maps to the column IsSynchronous in the RetentionPolicyProviderMap table.
	 */
	protected short isSynchronous;

	/** 
	 * This attribute represents whether the primitive attribute isSynchronous is null.
	 */
	protected boolean isSynchronousNull = true;

	/** 
	 * This attribute maps to the column IsOffsite in the RetentionPolicyProviderMap table.
	 */
	protected short isOffsite;

	/** 
	 * This attribute represents whether the primitive attribute isOffsite is null.
	 */
	protected boolean isOffsiteNull = true;

	/** 
	 * This attribute represents the foreign key relationship to the Place table.
	 */
	protected Place place;

	/** 
	 * This attribute represents the foreign key relationship to the Provider table.
	 */
	protected Provider provider;

	/** 
	 * This attribute represents the foreign key relationship to the RetentionPolicy table.
	 */
	protected RetentionPolicy retentionPolicy;

	/**
	 * Method 'RetentionPolicyProviderMap'
	 * 
	 */
	public RetentionPolicyProviderMap()
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
	 * Method 'getIsSynchronous'
	 * 
	 * @return short
	 */
	public short getIsSynchronous()
	{
		return isSynchronous;
	}

	/**
	 * Method 'setIsSynchronous'
	 * 
	 * @param isSynchronous
	 */
	public void setIsSynchronous(short isSynchronous)
	{
		this.isSynchronous = isSynchronous;
		this.isSynchronousNull = false;
	}

	/** 
	 * Sets the value of isSynchronousNull
	 */
	public void setIsSynchronousNull(boolean isSynchronousNull)
	{
		this.isSynchronousNull = isSynchronousNull;
	}

	/** 
	 * Gets the value of isSynchronousNull
	 */
	public boolean isIsSynchronousNull()
	{
		return isSynchronousNull;
	}

	/**
	 * Method 'getIsOffsite'
	 * 
	 * @return short
	 */
	public short getIsOffsite()
	{
		return isOffsite;
	}

	/**
	 * Method 'setIsOffsite'
	 * 
	 * @param isOffsite
	 */
	public void setIsOffsite(short isOffsite)
	{
		this.isOffsite = isOffsite;
		this.isOffsiteNull = false;
	}

	/** 
	 * Sets the value of isOffsiteNull
	 */
	public void setIsOffsiteNull(boolean isOffsiteNull)
	{
		this.isOffsiteNull = isOffsiteNull;
	}

	/** 
	 * Gets the value of isOffsiteNull
	 */
	public boolean isIsOffsiteNull()
	{
		return isOffsiteNull;
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
