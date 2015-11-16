/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Dec 01, 2007
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWLOUTHJ
  Description: 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+

 */
package gov.va.med.imaging.exchange.business.storage;

import gov.va.med.imaging.exchange.business.PersistentEntity;

import java.io.Serializable;

public class RetentionPolicyProviderMapping implements PersistentEntity, Serializable
{

	private static final long serialVersionUID = 1L;

	//
	// Fields
	//
	private int id;
	private boolean isSynchronous;
	private boolean isOffsite;
	private int providerId;
	private Provider provider;
	private int retentionPolicyId;
	private RetentionPolicy retentionPolicy;
	private int placeId;
	private Place place;

	//
	// Default Constructor
	//
	public RetentionPolicyProviderMapping()
	{
	}

	//
	// Additional Constructors
	//
	public RetentionPolicyProviderMapping(int id, int retentionPolicyId, int providerId, 
			int placeId, boolean isSynchronous, boolean isOffsite) 
	{
		this.id = id;
		this.providerId = providerId;
		this.retentionPolicyId = retentionPolicyId;
		this.placeId = placeId;
		this.isSynchronous = isSynchronous;
		this.isOffsite = isOffsite;
	}

	//
	// Properties
	//
    public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public boolean isSynchronous() {
		return isSynchronous;
	}

	public void setSynchronous(boolean isSynchronous) {
		this.isSynchronous = isSynchronous;
	}

	public boolean isOffsite() {
		return isOffsite;
	}

	public void setOffsite(boolean isOffsite) {
		this.isOffsite = isOffsite;
	}

	public int getProviderId() {
		return providerId;
	}

	public void setProviderId(int providerId) {
		this.providerId = providerId;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public int getRetentionPolicyId() {
		return retentionPolicyId;
	}

	public void setRetentionPolicyId(int retentionPolicyId) {
		this.retentionPolicyId = retentionPolicyId;
	}

	public RetentionPolicy getRetentionPolicy() {
		return retentionPolicy;
	}

	public void setRetentionPolicy(RetentionPolicy retentionPolicy) {
		this.retentionPolicy = retentionPolicy;
	}

	public int getPlaceId() {
		return placeId;
	}

	public void setPlaceId(int placeId) {
		this.placeId = placeId;
	}

	public Place getPlace() {
		return place;
	}

	public void setPlace(Place place) {
		this.place = place;
		this.placeId = place.getId();
	}

}
