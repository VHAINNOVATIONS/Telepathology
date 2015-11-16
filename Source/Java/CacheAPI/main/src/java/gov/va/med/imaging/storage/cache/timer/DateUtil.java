package gov.va.med.imaging.storage.cache.timer;

import java.util.Calendar;
import java.util.Date;

/**
 * A collection of utility functions to do Date calculations.
 *  
 * @author VHAISWBECKEC
 *
 */
public class DateUtil
{
	public static final long SECONDS_IN_MINUTE = 60L;
	public static final long SECONDS_IN_HOUR = 60L * SECONDS_IN_MINUTE;
	public static final long SECONDS_IN_DAY = 24L * SECONDS_IN_HOUR;
	public static final long SECONDS_IN_WEEK = 7L * SECONDS_IN_DAY;
	public static final long MILLISECONDS_IN_MINUTE = SECONDS_IN_MINUTE * 1000L;
	public static final long MILLISECONDS_IN_HOUR = SECONDS_IN_HOUR * 1000L;
	public static final long MILLISECONDS_IN_DAY = SECONDS_IN_DAY * 1000L;
	public static final long MILLISECONDS_IN_WEEK = SECONDS_IN_WEEK * 1000L;
	
	// an internal sequence of the order to increment fields in increasing order of magnitude
	private static final int[] incrementSequence = new int[]
	{
		Calendar.SECOND, Calendar.MINUTE, Calendar.HOUR_OF_DAY, Calendar.DAY_OF_MONTH, Calendar.MONTH, Calendar.YEAR, Calendar.ERA
	};
	
	private static int getIncrementSequenceIndex(int calendarFieldNumber)
	{
		for(int index=0; index < incrementSequence.length; ++index)
			if(incrementSequence[index] == calendarFieldNumber)
				return index;
		
		return 0;
	}
	
	/**
	 * 
	 * @param calendarField
	 * @param calendarFieldValue
	 * @param minimumCalendarFieldValue
	 * @return
	 */
	public static Date nextOccurenceOfCalenderField(int calendarField, int calendarFieldValue, int minimumCalendarFieldValue)
	{
		return nextOccurenceOfCalenderField(new Date(), calendarField, calendarFieldValue, minimumCalendarFieldValue);
	}
	
	/**
	 * The methods nextOccurenceOfHour, nextOccurenceOfDay, nextOccurenceOfMinute, etc ... are provided for
	 * convenience.  This method gets kinda' obscure and abstract and those convenience methods are recommended.
	 * 
	 * Get the next occurence of a date with the given field value after the given date.
	 * To make this a bit more concrete, think:
	 * 1.) get the next occurence of 11:00 AM after the current date - will return today at 11AM if the current time is before 11AM, else
	 *     it will return tomorrow at 11AM
	 * 2.) get the next occurence of the 10th of the month after the current date - will return the 10th of this month if the current date
	 *     is before the 10th, else the 10th of next month.
	 * 
	 * The Calendar fields of magnitude less than the field being set are always set to 0.  That is if you ask for the next 
	 * occurence of the 10th of the month you will get midnight (00:00:00) on the 10th.  If you ask for the next occurence of
	 * 11AM you will get 11:00:00.
	 * 
	 * Note that any field can cause a cascade in the fields of greater magnitude.  For example, asking for the next minute when 'then'
	 * is 2006Dec31:23:59:59 will return 2007:Jan01:00:00:00.
	 * 
	 * @param then
	 * @param calendarField
	 * @param calendarFieldValue
	 * @param minimumCalendarFieldValue
	 * @return
	 */
	public static Date nextOccurenceOfCalenderField(Date then, int calendarField, int calendarFieldValue, int minimumCalendarFieldValue)
	{
		Calendar thenCal = Calendar.getInstance();
		thenCal.setTime(then);
		int calendarFieldIncrementIndex = getIncrementSequenceIndex(calendarField);
		int nextSmallestCalendarField = calendarFieldIncrementIndex > 0 ? incrementSequence[calendarFieldIncrementIndex-1] : Calendar.MILLISECOND;
		
		calendarFieldValue = Math.max( thenCal.getActualMinimum(calendarField), Math.min(calendarFieldValue, thenCal.getActualMaximum(calendarField)) );
		
		// if the minimum increment is 0 then increment by the next smallest increment sequence
		if(minimumCalendarFieldValue == 0)
			thenCal.add(nextSmallestCalendarField, 1);
		else
			thenCal.add(calendarField, minimumCalendarFieldValue);
		
		Calendar resultCal = (Calendar)thenCal.clone();
		
		resultCal.set(calendarField, calendarFieldValue);
		
		// set the lower magnitude calendar fields to 0
		for(int index=calendarFieldIncrementIndex-1; index >= 0; --index)
			resultCal.set(incrementSequence[index], incrementSequence[index] == Calendar.DAY_OF_MONTH ? 1 : 0);
		resultCal.set(Calendar.MILLISECOND, 0);			// we don't deal with sub second resolution

		// if setting the calendar field has made the then date less than now, increment the next larger field
		for(int index=calendarFieldIncrementIndex+1; index < incrementSequence.length && resultCal.before(thenCal); ++index)
			resultCal.add(incrementSequence[index], 1);

		return resultCal.getTime();
	}

	/*
	 * ==================================================================================================================
	 * Convenience Methods
	 * For each calendar field (minute, hour, day of month, month, year) there are 4 convenience methods.
	 * 1.) Get the next occurence of the calendar
	 *     ex: get the next occurence of 11:00AM
	 * 2.) Get the next occurence of the calendar that is at least some increment in the future
	 *     ex: get the next occurence of 11:00AM that is at least two hours from now 
	 * 3.) Get the next occurence of the calendar after some given date
	 *     ex: Get the first occurence of 11:00AM after 11Dec2006:12:00:00
	 * 4.) Get the next occurence of the calendar after some given date that is at least some increment after the given date
	 *     ex: Get the first occurence of 11:00AM after 11Dec2006:12:00:00 that is at least one hour later
	 *     
	 * Ex for nextOccurenceOfHour: 
	 *   at 08:15:23 06Jan2006, nextOccurenceHour(9, 0) returns 09:00 06Jan2006 
	 *   at 08:15:23 06Jan2006, nextOccurenceHour(9, 1) returns 09:00 07Jan2006 
	 * ==================================================================================================================
	 */
	
	public static Date nextOccurenceOfMinute(int minute)
	{
		return nextOccurenceOfMinute(minute, 0);
	}
	public static Date nextOccurenceOfMinute(int minute, int minimumMinutes)
	{
		return nextOccurenceOfCalenderField(Calendar.MINUTE, minute, minimumMinutes);
	}
	public static Date nextOccurenceOfMinute(Date date, int minute)
	{
		return nextOccurenceOfMinute(date, minute, 0);
	}
	public static Date nextOccurenceOfMinute(Date date, int minute, int minimumMinutes)
	{
		return nextOccurenceOfCalenderField(date, Calendar.MINUTE, minute, minimumMinutes);
	}
	
	
	public static Date nextOccurenceOfHour(int hour)
	{
		return nextOccurenceOfHour(hour, 0);
	}
	public static Date nextOccurenceOfHour(int hour, int minimumHours)
	{
		return nextOccurenceOfCalenderField(Calendar.HOUR_OF_DAY, hour, minimumHours);
	}
	public static Date nextOccurenceOfHour(Date date, int hour)
	{
		return nextOccurenceOfHour(date, hour, 0);
	}
	public static Date nextOccurenceOfHour(Date date, int hour, int minimumHours)
	{
		return nextOccurenceOfCalenderField(date, Calendar.HOUR_OF_DAY, hour, minimumHours);
	}
	
	public static Date nextOccurenceOfDay(int dayOfMonth)
	{
		return nextOccurenceOfDay(dayOfMonth, 0);
	}
	public static Date nextOccurenceOfDay(int dayOfMonth, int minimumDays)
	{
		return nextOccurenceOfCalenderField(Calendar.DAY_OF_MONTH, dayOfMonth, minimumDays);
	}
	public static Date nextOccurenceOfDay(Date date, int dayOfMonth)
	{
		return nextOccurenceOfDay(date, dayOfMonth, 0);
	}
	public static Date nextOccurenceOfDay(Date date, int dayOfMonth, int minimumDays)
	{
		return nextOccurenceOfCalenderField(date, Calendar.DAY_OF_MONTH, dayOfMonth, minimumDays);
	}
	
	public static Date nextOccurenceOfMonth(int month)
	{
		return nextOccurenceOfMonth(month, 0);
	}
	public static Date nextOccurenceOfMonth(int month, int minimum)
	{
		return nextOccurenceOfCalenderField(Calendar.MONTH, month, minimum);
	}
	public static Date nextOccurenceOfMonth(Date date, int month)
	{
		return nextOccurenceOfMonth(date, month, 0);
	}
	public static Date nextOccurenceOfMonth(Date date, int month, int minimum)
	{
		return nextOccurenceOfCalenderField(date, Calendar.MONTH, month, minimum);
	}

	public static Date nextOccurenceOfYear(int year)
	{
		return nextOccurenceOfYear(year, 0);
	}
	public static Date nextOccurenceOfYear(int year, int minimum)
	{
		return nextOccurenceOfCalenderField(Calendar.YEAR, year, minimum);
	}
	public static Date nextOccurenceOfYear(Date date, int year)
	{
		return nextOccurenceOfYear(date, year, 0);
	}
	public static Date nextOccurenceOfYear(Date date, int year, int minimum)
	{
		return nextOccurenceOfCalenderField(date, Calendar.YEAR, year, minimum);
	}

	/*
	 * =============================================================================================
	 * Get next occurence of a Calendar field value.
	 * i.e. get the next even minute after the given date
	 * 
	 * NOTE: these differ from the above methods because those return the next instance of the
	 * numbered calendar field, i.e. return me the next Date when it is 11:00AM, rather than return 
	 * the next instance of an hour boundary, whcih is what these methods do.
	 * =============================================================================================
	 */
	public static Date next(Date then, int calendarField, int multiple, int minimalDelta)
	{
		int calendarFieldIncrementIndex = getIncrementSequenceIndex(calendarField);
		int nextSmallestCalendarField = calendarFieldIncrementIndex > 0 ? incrementSequence[calendarFieldIncrementIndex-1] : Calendar.MILLISECOND;
		
		Calendar calThen = Calendar.getInstance();
		calThen.setTime(then);
		
		calThen.add(nextSmallestCalendarField, minimalDelta);		// always at least a second late if its a minute, a minute of its an hour, etc ...
		
		Calendar calResult = (Calendar)calThen.clone();
		calResult.add(calendarField, multiple);
		for(int index=calendarFieldIncrementIndex-1; index >= 0; --index)
			calResult.set(incrementSequence[index], incrementSequence[index] == Calendar.DAY_OF_MONTH ? 1 : 0);
		calResult.set(Calendar.MILLISECOND, 0);			// we don't deal with sub second resolution
		
		if(calResult.before(calThen))
			calResult.add(calendarField, 1);

		return calResult.getTime();
	}
	
	public static Date next(Date then, int calendarField, int minimalDelta)
	{
		return next(then, calendarField, 1, minimalDelta);
	}

	public static Date nextSecond(Date then)
	{
		return next(then, Calendar.SECOND, 1, 100);
	}
	public static Date nextSecond()
	{
		return nextMinute(new Date());
	}
	public static Date nextMinute(Date then)
	{
		return next(then, Calendar.MINUTE, 1, 10);
	}
	public static Date nextMinute()
	{
		return nextMinute(new Date());
	}
	public static Date nextHour(Date then)
	{
		return next(then, Calendar.HOUR_OF_DAY, 1, 10);
	}
	public static Date nextHour()
	{
		return nextHour(new Date());
	}
	public static Date nextDay(Date then)
	{
		return next(then, Calendar.DAY_OF_MONTH, 1, 1);
	}
	public static Date nextDay()
	{
		return nextDay(new Date());
	}
	public static Date nextMonth(Date then)
	{
		return next(then, Calendar.MONTH, 1, 0);
	}
	public static Date nextMonth()
	{
		return nextMonth(new Date());
	}
	public static Date nextYear(Date then)
	{
		return next(then, Calendar.YEAR, 1, 0);
	}
	public static Date nextYear()
	{
		return nextYear(new Date());
	}
}
