package gov.va.med.imaging;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import junit.framework.TestCase;

public class DateUtilTest extends TestCase
{
	public static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd:HH:mm:ss");
	public static String getFormattedCalendar(Calendar cal)
	{
		return dateFormat.format(cal.getTime());
	}

	public void testTimeZone() 
	throws ParseException
	{
		TimeZone localTimeZone = TimeZone.getDefault();
		TimeZone newYorkTimeZone = TimeZone.getTimeZone("America/New_York");
		System.out.println( "Local timezone is '" + localTimeZone.getDisplayName() + "'." );
		System.out.println( "New York timezone is '" + newYorkTimeZone.getDisplayName() + "'." );
		
		DateFormat df = new SimpleDateFormat("ddMMMyyyy:hh:mm:ss");
		
		// Since 1966, most of the United States has observed Daylight Saving Time from 
		// 2:00 a.m. on the first Sunday of April to 2:00 a.m. on the last Sunday of October. 
		// Beginning in 2007, most of the U.S. will begin Daylight Saving Time at 2:00 a.m. on the 
		// second Sunday in March and revert to standard time on the first Sunday in November. 
		// In the U.S., each time zone switches at a different time.
		// In 2006 DST began on April 2 and ended on Oct 29
		// In 2007 DST bigins on March 11 and ends on Nov 4
		printIsInDST(newYorkTimeZone, df, "02Apr2006:00:59:59");		// one hour and one second before a ST/DST boundary
		printIsInDST(newYorkTimeZone, df, "02Apr2006:01:59:59");		// one second before a ST/DST boundary
		printIsInDST(newYorkTimeZone, df, "02Apr2006:02:00:01");		// one second after a ST/DST boundary, not really a valid time
		
		printIsInDST(newYorkTimeZone, df, "29Oct2006:00:59:59");		// one hour and one second before a DST/ST boundary
		printIsInDST(newYorkTimeZone, df, "29Oct2006:01:59:59");		// one second before a DST/ST boundary, but also 59:59 seconds after a DST/ST boundary
		printIsInDST(newYorkTimeZone, df, "29Oct2006:02:00:01");		// one second after a DST/ST boundary

		printIsInDST(newYorkTimeZone, df, "11Mar2007:00:59:59");		// one hour and one second before a ST/DST boundary
		printIsInDST(newYorkTimeZone, df, "11Mar2007:01:59:59");		// one second before a ST/DST boundary
		printIsInDST(newYorkTimeZone, df, "11Mar2007:02:00:01");		// one second after a ST/DST boundary, not really a valid time
		
		printIsInDST(newYorkTimeZone, df, "04Nov2007:00:59:59");		// one hour and one second before a DST/ST boundary
		printIsInDST(newYorkTimeZone, df, "04Nov2007:01:59:59");		// one second before a DST/ST boundary, but also 59:59 seconds after a DST/ST boundary
		printIsInDST(newYorkTimeZone, df, "04Nov2007:02:00:01");		// one second after a DST/ST boundary
	}
	
	private boolean printIsInDST(TimeZone tz, DateFormat df, String dateString) 
	throws ParseException
	{
		Date date = df.parse(dateString);		// one hour and one second before a DST/ST boundary
		
		boolean result = tz.inDaylightTime(date);
		System.out.println( dateString + " " + 
				(tz.inDaylightTime(date) ? "is" : "is not") +
				" in daylight time" );
		
		return result;
	}
	
	public void testNextOccurenceOfHour()
	{
		Calendar defaultCalendar = Calendar.getInstance();
		
		Date next = null;
		next = DateUtil.nextOccurenceOfHour(1);
		defaultCalendar.setTime(next);
		System.out.println("The next occurence of 01:00 is [" + getFormattedCalendar(defaultCalendar) + "]" );
		
		next = DateUtil.nextOccurenceOfHour(0);
		defaultCalendar.setTime(next);
		System.out.println("The next occurence of 00:00 is [" + getFormattedCalendar(defaultCalendar) );
		
		next = DateUtil.nextOccurenceOfHour(23);
		defaultCalendar.setTime(next);
		System.out.println("The next occurence of 23:00 is [" + getFormattedCalendar(defaultCalendar) );
		
		next = DateUtil.nextOccurenceOfHour(12);
		defaultCalendar.setTime(next);
		System.out.println("The next occurence of 12:00 is [" + getFormattedCalendar(defaultCalendar) );
		
		next = DateUtil.nextOccurenceOfHour(6);
		defaultCalendar.setTime(next);
		System.out.println("The next occurence of 06:00 is [" + getFormattedCalendar(defaultCalendar) );
		
		next = DateUtil.nextOccurenceOfHour(99);
		defaultCalendar.setTime(next);
		System.out.println("The next occurence of 99:00 is [" + getFormattedCalendar(defaultCalendar) );

		next = DateUtil.nextOccurenceOfHour(20, 24);
		defaultCalendar.setTime(next);
		System.out.println("The next occurence of 20:00, greater than 24 hours from now, is [" + getFormattedCalendar(defaultCalendar) + "]" );

		next = DateUtil.nextOccurenceOfHour(5, 24);
		defaultCalendar.setTime(next);
		System.out.println("The next occurence of 05:00, greater than 24 hours from now, is [" + getFormattedCalendar(defaultCalendar) + "]" );
	}

	public void testNextOccurenceOfMinute()
	{
		Calendar defaultCalendar = Calendar.getInstance();
		
		Date next = null;
		next = DateUtil.nextMinute();
		System.out.println("The current time is [" + getFormattedCalendar(defaultCalendar) + "]" );
		defaultCalendar.setTime(next);
		System.out.println("The next minute (at least 10 seconds away) is [" + getFormattedCalendar(defaultCalendar) + "]" );
	}	
	
	public void testFromStaticDate() 
	throws ParseException
	{
		DateFormat df = new SimpleDateFormat("ddMMMyyyy:hh:mm:ss");
		Date jan01 = df.parse("01Jan2000:00:00:00");
		Date calculatedDate = null;
		Date comparisonDate = null;
		
		calculatedDate = DateUtil.nextOccurenceOfMinute(jan01, 10);
		comparisonDate = df.parse("01Jan2000:00:10:00"); 
		assertEquals("Calculated(" + df.format(calculatedDate) + ") != (" + df.format(comparisonDate) + ")",
				comparisonDate, calculatedDate );
		
		calculatedDate = DateUtil.nextOccurenceOfHour(jan01, 10);
		comparisonDate = df.parse("01Jan2000:10:00:00"); 
		assertEquals("Calculated(" + df.format(calculatedDate) + ") != (" + df.format(comparisonDate) + ")",
				comparisonDate, calculatedDate );
		
		calculatedDate = DateUtil.nextOccurenceOfDay(jan01, 10);
		comparisonDate = df.parse("10Jan2000:00:00:00"); 
		assertEquals("Calculated(" + df.format(calculatedDate) + ") != (" + df.format(comparisonDate) + ")",
				comparisonDate, calculatedDate );
		
		calculatedDate = DateUtil.nextOccurenceOfMonth(jan01, Calendar.FEBRUARY);
		comparisonDate = df.parse("01Feb2000:00:00:00"); 
		assertEquals("Calculated(" + df.format(calculatedDate) + ") != (" + df.format(comparisonDate) + ")",
				comparisonDate, calculatedDate );
			
		calculatedDate = DateUtil.nextOccurenceOfYear(jan01, 2010);
		comparisonDate = df.parse("01Jan2010:00:00:00"); 
		assertEquals("Calculated(" + df.format(calculatedDate) + ") != (" + df.format(comparisonDate) + ")",
				comparisonDate, calculatedDate );
			
	}
	
	public void testFromStaticLeapYearDate() 
	throws ParseException
	{
		DateFormat df = new SimpleDateFormat("ddMMMyyyy:hh:mm:ss");
		Date feb28 = df.parse("28Feb2004:00:00:00");
		Date calculatedDate = null;
		Date comparisonDate = null;
		
		calculatedDate = DateUtil.nextOccurenceOfMinute(feb28, 10);
		comparisonDate = df.parse("28Feb2004:00:10:00"); 
		assertEquals("Calculated(" + df.format(calculatedDate) + ") != (" + df.format(comparisonDate) + ")",
				comparisonDate, calculatedDate );
		
		calculatedDate = DateUtil.nextOccurenceOfHour(feb28, 10);
		comparisonDate = df.parse("28Feb2004:10:00:00"); 
		assertEquals("Calculated(" + df.format(calculatedDate) + ") != (" + df.format(comparisonDate) + ")",
				comparisonDate, calculatedDate );
		
		calculatedDate = DateUtil.nextOccurenceOfDay(feb28, 29);
		comparisonDate = df.parse("29Feb2004:00:00:00"); 
		assertEquals("Calculated(" + df.format(calculatedDate) + ") != (" + df.format(comparisonDate) + ")",
				comparisonDate, calculatedDate );
		calculatedDate = DateUtil.nextOccurenceOfDay(feb28, 1);
		comparisonDate = df.parse("01Mar2004:00:00:00"); 
		assertEquals("Calculated(" + df.format(calculatedDate) + ") != (" + df.format(comparisonDate) + ")",
				comparisonDate, calculatedDate );
		
		calculatedDate = DateUtil.nextOccurenceOfMonth(feb28, Calendar.MARCH);
		comparisonDate = df.parse("01Mar2004:00:00:00"); 
		assertEquals("Calculated(" + df.format(calculatedDate) + ") != (" + df.format(comparisonDate) + ")",
				comparisonDate, calculatedDate );
			
		calculatedDate = DateUtil.nextOccurenceOfYear(feb28, 2010);
		comparisonDate = df.parse("01Jan2010:00:00:00"); 
		assertEquals("Calculated(" + df.format(calculatedDate) + ") != (" + df.format(comparisonDate) + ")",
				comparisonDate, calculatedDate );

		// non leap year Feb28th testing
		feb28 = df.parse("28Feb2003:00:00:00");		// a non-leap year
		calculatedDate = DateUtil.nextOccurenceOfDay(feb28, 29);
		comparisonDate = df.parse("28Mar2003:00:00:00"); 
		assertEquals("Calculated(" + df.format(calculatedDate) + ") != (" + df.format(comparisonDate) + ")",
				comparisonDate, calculatedDate );
		calculatedDate = DateUtil.nextOccurenceOfDay(feb28, 1);
		comparisonDate = df.parse("01Mar2003:00:00:00"); 
		assertEquals("Calculated(" + df.format(calculatedDate) + ") != (" + df.format(comparisonDate) + ")",
				comparisonDate, calculatedDate );
		
	}
	
	/**
	 * Testing for DST/ST boundary condition is problemetic.  The issue is that
	 * the formatted time, which we test against, could correlate to either of
	 * two instances of the comparison date.  That is, there are two 29Oct2006:01:01:01 and
	 * determining which one you get when you parse the string is unknown.
	 * BTW, Java may have an issue with DST changes as it looks like it is switching at
	 * 1AM and it should be switching at 2AM in the EST.
	 * 
	 * @throws ParseException
	 */
	public void XtestFromStaticDSTDate() 
	throws ParseException
	{
		DateFormat df = new SimpleDateFormat("ddMMMyyyy:hh:mm:ss");
		
		Date lastSecondOfDST = df.parse("29Oct2006:00:59:59");		// one second before a DST/ST boundary
		Date calculatedDate = null;
		Date comparisonDate = null;
		
		calculatedDate = DateUtil.nextOccurenceOfMinute(lastSecondOfDST, 10);
		comparisonDate = df.parse("29Oct2006:01:10:00"); 
		
		// must calculate on the formatted date, 'cause Java doesn't know which
		// instance of 29Oct2006:00:59:59 we're talking about
		assertEquals("Calculated(" + df.format(calculatedDate) + ") != (" + df.format(comparisonDate) + ") - string compare",
				df.format(comparisonDate), df.format(calculatedDate) );
		// the dates are not equal
		assertFalse("Calculated(" + df.format(calculatedDate) + ") != (" + df.format(comparisonDate) + ") Date compare",
				comparisonDate.equals(calculatedDate) );
		
	}

	public void testNextCalendarFields() 
	throws ParseException
	{
		DateFormat df = new SimpleDateFormat("ddMMMyyyy:HH:mm:ss");
		
		validateDate(df, "01Apr2006:12:12:13", DateUtil.nextSecond( df.parse("01Apr2006:12:12:12") ) );
		validateDate(df, "01Apr2006:12:13:00", DateUtil.nextMinute( df.parse("01Apr2006:12:12:12") ) );
		validateDate(df, "01Apr2006:13:00:00", DateUtil.nextHour( df.parse("01Apr2006:12:12:12") ) );
		
		validateDate(df, "02Apr2006:00:00:00", DateUtil.nextDay( df.parse("01Apr2006:12:12:12") ) );
		validateDate(df, "01May2006:00:00:00", DateUtil.nextMonth( df.parse("01Apr2006:12:12:12") ) );
		//validateDate(df, "01Jan2007:00:00:00", DateUtil.nextYear( df.parse("01Apr2006:12:12:12") ) );
	}
	
	private void validateDate(DateFormat df, String stringifiedDate, Date calculatedDate) 
	throws ParseException
	{
		Date comparisonDate = df.parse(stringifiedDate);
		assertEquals(comparisonDate, calculatedDate);
	}
	
	public void testDicomDateFormatting() 
	throws ParseException
	{
		// An array of test data and expected results
		String[][] expectedResults = new String[][]
		{
			{"01/01/1990:01:00:00", "19900101010000.000000"},
			{"01/01/1990:01:00:00", "1990010101"},
			{"01/01/1990:01:00:00", "199001010100"},
			{"01/01/1990:01:00:00", "19900101010000"},
			{"01/01/1990:01:00:00", "19900101010000.000000"},
			{"01/01/1990:01:00:00", "19900101010000.0000"},
			// timezone testing is not working right, the DateFormat seems to be okay
			{"01/01/1990:01:00:00", "19900101010000.000000Z"},			// special case, tack the default timezone offset on
			{"01/01/1990:01:00:00", "19900101010000.0000Z"}				// special case, tack the default timezone offset on
		};
		// just a source of dates to test against
		DateFormat commonDf = new SimpleDateFormat("dd/MM/yyyy:hh:mm:ss");
		
		for(String[] expectedResult : expectedResults)
		{
			Date expectedDate = commonDf.parse(expectedResult[0]);
			
			System.out.println("Raw stimulus data '" + expectedResult[0] + "' comparing to '" + expectedResult[1] + "'.");
			// substitute the timezone offset
			if(expectedResult[1].endsWith("Z"))
			{
				int tzOffset = TimeZone.getDefault().getOffset(expectedDate.getTime());
				tzOffset /= (60 * 60 * 1000);		// put the timezone offset in terms of hours 
				String stringifiedOffset = Math.abs(tzOffset) < 10 ? 
						(tzOffset < 0 ? "-0" : "+0") + Math.abs(tzOffset) + "00" :
						(tzOffset < 0 ? "-" : "+") + Math.abs(tzOffset) + "00";
				
				expectedResult[1] = expectedResult[1].substring(0, expectedResult[1].length()-1) + stringifiedOffset;
			}
			System.out.println("Stimulus data '" + expectedResult[0] + "' comparing to '" + expectedResult[1] + "'.");
			
			DateFormat ddf = DateUtil.getDicomDateFormat();
			assertNotNull("No DICOM date format generated for '" + expectedResult[1] + "'", ddf);
			Date resultDate = ddf.parse(expectedResult[1]);
			
			
			assertEquals(expectedDate, resultDate); 
		}
	}
}
