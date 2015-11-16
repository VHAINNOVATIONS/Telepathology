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
import java.util.Calendar;

public class ProviderAvailability implements PersistentEntity, Serializable {
	private static final long serialVersionUID = 1L;
	private static final int HOURS_PER_DAY = 24;
	private static final int MINUTES_PER_HOUR = 60;
	private static final int MINUTES_PER_DAY = HOURS_PER_DAY * MINUTES_PER_HOUR;

	//
	// Fields
	//
	private int id;
	private String startTime;
	private String endTime;
	private int providerId;
	private Provider provider;
	private int placeId;
	private Place place;
	private int startTime1;
	private int startTime2;
	private int endTime1;
	private int endTime2;

	//
	// Default Constructor
	//
	public ProviderAvailability() {
		calculateAvailabilityWindow();
	}

	//
	// Additional Constructor(s)
	//
	public ProviderAvailability(int id, int providerId, int placeId,
			String startTime, String endTime) {
		this.id = id;
		this.providerId = providerId;
		this.placeId = placeId;
		setStartTime(startTime);
		setEndTime(endTime);
	}

	//
	// Public Methods
	//
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
		calculateAvailabilityWindow();
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
		calculateAvailabilityWindow();
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
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
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getProviderId() {
		return providerId;
	}

	public void setProviderId(int providerId) {
		this.providerId = providerId;
	}

	public Calendar getNextAvailabilityStartDate(int placeId) {
		Calendar nextStart = Calendar.getInstance();
		int minutes = getMinutesInDay(nextStart);
		nextStart.set(Calendar.HOUR_OF_DAY, 0);
		nextStart.set(Calendar.MINUTE, 0);
		if (startTime1 < minutes) {
			nextStart.add(Calendar.DAY_OF_MONTH, 1);
		}
		nextStart.add(Calendar.MINUTE, startTime1);
		return nextStart;
	}

	public boolean isWithinAvailabilityWindow(Calendar calendar) {
		int targetTime = calendar.get(Calendar.HOUR_OF_DAY) * MINUTES_PER_HOUR;
		targetTime += calendar.get(Calendar.MINUTE);
		return isWithinAvailabilityWindow(targetTime, startTime1, endTime1)
				|| isWithinAvailabilityWindow(targetTime, startTime2, endTime2);
	}
	
	//
	// Private Methods
	//
	private int getMinutesInDay(Calendar calendar){
		int minutes = calendar.get(Calendar.HOUR_OF_DAY) * MINUTES_PER_HOUR;
		minutes += calendar.get(Calendar.MINUTE);
		return minutes;
	}

	private boolean isWithinAvailabilityWindow(int targetTime, int startTime, int endTime) {
		return startTime <= targetTime && targetTime <= endTime;
	}

	private void calculateAvailabilityWindow() {
		if (startTime == null || endTime == null) {
			startTime1 = 0;
			endTime1 = MINUTES_PER_DAY;
			startTime2 = 0;
			endTime2 = MINUTES_PER_DAY;
			return;
		}
		String[] timeParts;
		timeParts = gov.va.med.imaging.StringUtil.breakString(startTime, 2);
		this.startTime1 = Integer.parseInt(timeParts[0]) * MINUTES_PER_HOUR;
		this.startTime1 += Integer.parseInt(timeParts[1]);
		timeParts = gov.va.med.imaging.StringUtil.breakString(endTime, 2);
		this.endTime1 = Integer.parseInt(timeParts[0]) * MINUTES_PER_HOUR;
		this.endTime1 += Integer.parseInt(timeParts[1]);
		if (startTime1 > endTime1) {
			endTime2 = endTime1;
			endTime1 = MINUTES_PER_DAY;
			startTime2 = 0;
		}
	}
}
