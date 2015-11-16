/*
 * Originally HttpDate.java 
 * created on Nov 22, 2004 @ 1:07:14 PM
 * by Chris Beckey mailto:c.beckey@seetab.com
 *
 */
package gov.va.med.imaging.http;

/**
 * @author Chris Beckey mailto:c.beckey@seetab.com
 * @since Nov 22, 2004 1:07:14 PM
 *
 * HTTP applications have historically allowed three different formats
 * for the representation of date/time stamps:
 * 
 *	Sun, 06 Nov 1994 08:49:37 GMT  ; RFC 822, updated by RFC 1123
 *	Sunday, 06-Nov-94 08:49:37 GMT ; RFC 850, obsoleted by RFC 1036
 *	Sun Nov  6 08:49:37 1994       ; ANSI C's asctime() format
 *
 * The first format is preferred as an Internet standard and represents
 * a fixed-length subset of that defined by RFC 1123 [8] (an update to
 * RFC 822 [9]). The second format is in common use, but is based on the
 * obsolete RFC 850 [12] date format and lacks a four-digit year.
 * HTTP/1.1 clients and servers that parse the date value MUST accept
 * all three formats (for compatibility with HTTP/1.0), though they MUST
 * only generate the RFC 1123 format for representing HTTP-date values
 * in header fields. See section 19.3 for further information.
 * 
 * All HTTP date/time stamps MUST be represented in Greenwich Mean Time
 * (GMT), without exception. For the purposes of HTTP, GMT is exactly
 * equal to UTC (Coordinated Universal Time).
 *  
 * HTTP-date    = rfc1123-date | rfc850-date | asctime-date
 * rfc1123-date = wkday "," SP date1 SP time SP "GMT"
 * rfc850-date  = weekday "," SP date2 SP time SP "GMT"
 * asctime-date = wkday SP date3 SP time SP 4DIGIT
 * date1        = 2DIGIT SP month SP 4DIGIT
 * 				; day month year (e.g., 02 Jun 1982)
 * date2        = 2DIGIT "-" month "-" 2DIGIT
 * 				; day-month-year (e.g., 02-Jun-82)
 * date3        = month SP ( 2DIGIT | ( SP 1DIGIT ))
 * 				; month day (e.g., Jun  2)
 * time         = 2DIGIT ":" 2DIGIT ":" 2DIGIT
 * 				; 00:00:00 - 23:59:59
 * wkday        = "Mon" | "Tue" | "Wed" | "Thu" | "Fri" | "Sat" | "Sun"
 * weekday      = "Monday" | "Tuesday" | "Wednesday" | "Thursday" | "Friday" | "Saturday" | "Sunday"
 * month        = "Jan" | "Feb" | "Mar" | "Apr" | "May" | "Jun" | "Jul" | "Aug" | "Sep" | "Oct" | "Nov" | "Dec"
 * 
 */
public class HttpDate
{
	public HttpDate()
	{
	}
	
	public HttpDate parseHttpDate(String dateString)
	{
		return null;
	}

	/**
	 * Return a String representation of the date in RFC 1123 format.
	 * Example: Sun, 06 Nov 1994 08:49:37 GMT
	 */
	public String toString()
	{
		return null;
	}
}

/**
 * 
 * @author Chris Beckey mailto:c.beckey@seetab.com
 * @since Nov 22, 2004 1:22:37 PM
 *
 * A class to deal with Http month values.  This class assures that the 
 * month is valid and is formatted per RFC1123 
 */
class HttpDateMonth
{
	private static String[] monthList = {
		"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"
	};
	private String month;
	private int monthIndex;
	
	HttpDateMonth(String dateString)
	{
		setMonth(dateString);
	}
	
	/**
	 * Return a valid month name.
	 * @return
	 */
	String getMonth()
	{
		return month;
	}
	
	/**
	 * Return a zero based month index (that is Jan is 0, Dec is 11)
	 * 
	 * @return
	 */
	int getMonthIndex()
	{
		return monthIndex;
	}
	
	private void setMonth(String dateString)
	{
		for(monthIndex=0; monthIndex < monthList.length; ++monthIndex)
			if(monthList[monthIndex].equalsIgnoreCase(dateString))
			{
				month = monthList[monthIndex];
				// note that monthIndex is left pointing to the correct number
				break;
			}
	}
	
	public String toString()
	{
		return month;
	}
	
		
}