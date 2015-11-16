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
import java.util.Date;

public class TransferStatistics implements PersistentEntity, Serializable
{

	private static final long serialVersionUID = 1L;

	//
	// Fields
	//
	private int id;
	private Date startDateTime;
	private long durationInMilliseconds;
	private long sizeInBytes;
	private int providerId;
	private Provider provider;
	private int placeId;
	private Place place;

	//
	// Default Constructor
	//
	public TransferStatistics()
	{
	}

	//
	// Additional Constructors
	//
	
	public TransferStatistics(int id, Date startDateTime,
			int durationInMilliseconds, int sizeInBytes, Provider provider,
			Place place) 
	{
		this.id = id;
		this.startDateTime = startDateTime;
		this.durationInMilliseconds = durationInMilliseconds;
		this.sizeInBytes = sizeInBytes;
		this.provider = provider;
		this.place = place;
	}

	public TransferStatistics(Date startDateTime, long durationInMilliseconds,
			long sizeInBytes, Provider provider, Place place) {
		this.startDateTime = startDateTime;
		this.durationInMilliseconds = durationInMilliseconds;
		this.sizeInBytes = sizeInBytes;
		setProvider(provider);
		setPlace(place);
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

	public Date getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(Date startDateTime) {
		this.startDateTime = startDateTime;
	}

	public long getDurationInMilliseconds() {
		return durationInMilliseconds;
	}

	public void setDurationInMilliseconds(long durationInMilliseconds) {
		this.durationInMilliseconds = durationInMilliseconds;
	}

	public long getSizeInBytes() {
		return sizeInBytes;
	}

	public void setSizeInBytes(long sizeInBytes) {
		this.sizeInBytes = sizeInBytes;
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
		this.providerId = provider.getId();
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
