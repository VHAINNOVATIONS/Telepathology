package gov.va.med.imaging.exchange.business;

import gov.va.med.imaging.BeanUtils;
import gov.va.med.imaging.BusinessKey;

import java.util.Calendar;
import java.util.Date;

/**
 * @author vhaiswgraver
 * 
 */
public class TimePeriod {
	Calendar endCalendar;
	Calendar startCalendar;

	public TimePeriod(Calendar startCalendar, Calendar endCalendar) {
		setStartCalendar(startCalendar);
		setEndCalendar(endCalendar);
	}

	public TimePeriod(Date startDateTime, Date endDateTime) {
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(startDateTime);
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(endDateTime);
		setStartCalendar(startCalendar);
		setEndCalendar(endCalendar);
	}

	public boolean after(Calendar cal) {
		return this.getStartCalendar().after(cal);
	}

	public boolean after(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return this.getStartCalendar().after(cal);
	}

	public boolean after(TimePeriod period) {
		return this.getStartCalendar().after(period.getEndCalendar());
	}

	private boolean areDatesValid(Calendar startCalendar, Calendar endCalendar) {
		if (startCalendar == null || endCalendar == null)
			return true;
		return startCalendar.compareTo(endCalendar) <= 0;
	}

	public boolean before(Calendar cal) {
		return this.getEndCalendar().before(cal);
	}

	public boolean before(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return this.getEndCalendar().before(cal);
	}

	public boolean before(TimePeriod period) {
		return this.getEndCalendar().before(period.getStartCalendar());
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new TimePeriod(getStartCalendar(), getEndCalendar());
	}

	public boolean contains(Calendar cal) {
		if (cal == null)
			return false;
		return this.getStartCalendar().compareTo(cal) <= 0
				&& this.getEndCalendar().compareTo(cal) >= 0;
	}

	public boolean contains(Date date) {
		if (date == null)
			return false;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return contains(cal);
	}

	public boolean contains(TimePeriod period) {
		if (period == null)
			return false;
		return this.contains(period.getStartCalendar())
				&& contains(period.getEndCalendar());
	}

	@Override
	public boolean equals(Object obj) {
		return BeanUtils.equals(this, obj);
	}

	@BusinessKey
	public Calendar getEndCalendar() {
		if (endCalendar == null)
			return null;
		// return a clone so dates can't be changed externally to make start
		// date after end date
		return (Calendar) endCalendar.clone();
	}
	
	public Date getEndDate() {
		if (endCalendar == null)
			return null;
		return endCalendar.getTime();
	}

	@BusinessKey
	public Calendar getStartCalendar() {
		if (startCalendar == null)
			return null;
		// return a clone so dates can't be changed externally to make start
		// date after end date
		return (Calendar) startCalendar.clone();
	}
	
	public Date getStartDate() {
		if (startCalendar == null)
			return null;
		return startCalendar.getTime();
	}

	@Override
	public int hashCode() {
		return BeanUtils.hashCode(this);
	}

	public boolean intersects(TimePeriod period) {
		return this.contains(period.getStartCalendar())
				|| this.contains(period.getEndCalendar())
				|| period.contains(this.getStartCalendar())
				|| period.contains(this.getEndCalendar());
	}

	public void setEndCalendar(Calendar endCalendar) {
		if (endCalendar == null) {
			// throw new Exception("The endCalendar may not be null");
		}
		if (!areDatesValid(this.getStartCalendar(), endCalendar)) {
			// throw new Exception("The start date may not be before the end
			// date of the TimePeriod");
		}
		// use clones so dates can't be changed externally to make start date
		// after end date
		this.endCalendar = (Calendar) endCalendar.clone();
	}
	
	public void setEndDate(Date endDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(endDate);
		setEndCalendar(cal);
	}

	public void setStartCalendar(Calendar startCalendar) {
		if (startCalendar == null) {
			// throw new Exception("The startCalendar may not be null");
		}
		if (!areDatesValid(startCalendar, this.getEndCalendar())) {
			// throw new Exception("The start date may not be before the end
			// date of the TimePeriod");
		}
		// use clones so dates can't be changed externally to make start date
		// after end date
		this.startCalendar = (Calendar) startCalendar.clone();
	}
	
	public void setStartDate(Date startDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		setStartCalendar(cal);
	}

	@Override
	public String toString() {
		return this.getStartCalendar().getTime().toString() + " - "
				+ this.getEndCalendar().getTime().toString();
	}
}
