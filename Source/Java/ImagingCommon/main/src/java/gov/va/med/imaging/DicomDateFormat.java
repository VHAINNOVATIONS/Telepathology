/**
 * 
 */
package gov.va.med.imaging;

import java.text.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A date formatter for DICOM date-time data elements (DT).
 * 
 * =============================================================================================================================================
 * From the DICOM Spec (06.05 pg.26):
 * "The Date Time common data type. Indicates a concatenated date-time ASCII string in the format: YYYYMMDDHHMMSS.FFFFFF&ZZZZ
 * The components of this string, from left to right, are 
 * YYYY = Year, 
 * MM = Month, 
 * DD = Day, 
 * HH = Hour, 
 * MM = Minute, 
 * SS = Second, 
 * FFFFFF = Fractional Second, 
 * & = “+” or “-”, and 
 * ZZZZ = Hours and Minutes of offset. 
 * &ZZZZ is an optional suffix for plus/minus offset from Coordinated Universal Time. A component that is omitted from the string is
 * termed a null component. Trailing null components of Date Time are ignored. Nontrailing null components are prohibited, given
 * that the optional suffix is not considered as a component.
 * Note: For reasons of backward compatibility with versions of this standard prior to V3.0, many existing DICOM Data
 * Elements use the separate DA and TM VRs. Standard and Private Data Elements defined in the future should use DT, when 
 * appropriate, to be more compliant with ANSI HISPP MSDS."
 * =============================================================================================================================================
 * 
 * NOTES
 * 1.) This class DOES NOT support the backward compatibility mentioned above for versions before 3.0.
 * 2.) The DICOM specification does not specify the calendar used for date interpretation.  This class
 * always uses a Gregorian calendar.
 * 3.) The timezone is defaulted to the default timezone for the VM if it is not specified.
 * 4.) DICOM uses 1-indexing for months. 
 * =============================================================================================================================================
 * 
 * @author VHAISWBECKEC
 */
public class DicomDateFormat
extends SimpleDateFormat
{
	private static final long serialVersionUID = -1381461230116366880L;

	// YYYYMMDDHHMMSS.FFFFFF+ZZZZ
	// The components of this string, from left to right, are 
	// YYYY = Year, 
	// MM = Month, 
	// DD = Day, 
	// HH = Hour, 
	// MM = Minute, 
	// SS = Second,
	// FFFFFF = milliseconds
	// ZZZZZ - timezone offset (first digit is + or -
	private final static String dicomDateFormat = "yyyyMMddHHmmss.SSSSSSZZZZZ";
	
	// The regulare expression pattern should read as:
	//	required   4 digits (the year)
	//	optionally 2 digit (the month of the year)
	//	optionally 2 digit (the day of the month)
	//	optionally 2 digits (for each of the hour, minute and seconds)
	//	optionally 1 decimal point followed by 4 or 6 digits (for the milliseconds)
	//	optionally 1 '+' or '-' followed by 4 digits, 2 for hours and 2 for minutes (for the offset from UCT)
	private final static String dicomDateRegex = "(\\d{4})(\\d{2})?(\\d{2})?(\\d{2})?(\\d{2})?(\\d{2})?(\\.(\\d{4}|\\d{6}))?(([+|-]\\d{4}))?";
	
	private static final int yearGroupIndex = 1;
	private static final int monthGroupIndex = 2;
	private static final int dayGroupIndex = 3;
	private static final int hourGroupIndex = 4;
	private static final int minuteGroupIndex = 5;
	private static final int secondGroupIndex = 6;
	private static final int millisecondGroupIndex = 8;
	private static final int timezoneGroupIndex = 9;
	
	// during processing it may be necessary to calculate a date to use for timezone
	// determination, this is NOT the DateFormat used to do our formatting or parsing
	private DateFormat utilityDateFormat = new SimpleDateFormat("yyyyMMddhhmmss.SSSSSS");

	// a NumberFormat used to build timezone formatted strings
	private NumberFormat timezoneNumberFormat = new DecimalFormat("+0000;-0000");
	
	/**
	 * Construct a DicomDateFormat instance using the default locale.
	 *
	 */
	public DicomDateFormat()
	{
		super(dicomDateFormat);
		super.setCalendar(new GregorianCalendar());
	}

	/**
	 * Construct a DicomDateFormat instance specifying a locale.
	 * 
	 * @param locale
	 */
	public DicomDateFormat(Locale locale)
	{
		super(dicomDateFormat, locale);
		super.setCalendar(new GregorianCalendar());
	}

	/**
	 * Method does nothing, the date format pattern is defined by the class.
	 * 
	 * @see java.text.SimpleDateFormat#applyLocalizedPattern(java.lang.String)
	 */
	@Override
	public void applyLocalizedPattern(String pattern) {}

	/**
	 * Method does nothing, the date format pattern is defined by the class.
	 * 
	 * @see java.text.SimpleDateFormat#applyPattern(java.lang.String)
	 */
	@Override
	public void applyPattern(String pattern){}

	/**
	 * Method does nothing, the Calendar is defined by the class as a Gregorian calendar instance.
	 * 
	 * @see java.text.DateFormat#setCalendar(java.util.Calendar)
	 */
	@Override
	public void setCalendar(Calendar newCalendar){}

	/**
	 * @throws ParseException 
	 * 
	 */
	@Override
	public Date parse(String dicomFormattedDate) 
	throws ParseException
	{
		String year = null;
		String month = null;
		String day = null;
		String hour = null;
		String minute = null;
		String second = null;
		String millisecond = null;
		String timezoneOffset = null;
		
		Pattern dicomPattern = Pattern.compile(dicomDateRegex);
		
		Matcher matcher = dicomPattern.matcher(dicomFormattedDate);
		if( ! matcher.matches() )
			throw new ParseException("'" + dicomFormattedDate + "' is not a valid DICOM date.", 0);

		year = matcher.group(yearGroupIndex);
		
		month  = matcher.group(monthGroupIndex);
		month = (month == null ? "01" : month);
		int monthInt = Integer.parseInt(month);
		if(monthInt < 1 || monthInt > 12)
			throw new ParseException("Month must be between 01 and 11", 3);
		
		day  = matcher.group(dayGroupIndex);
		day = (day == null ? "01" : day);
		
		hour  = matcher.group(hourGroupIndex);
		hour = (hour == null ? "00" : hour);
		
		minute = matcher.group(minuteGroupIndex);
		minute = (minute == null ? "00" : minute);
		
		second  = matcher.group(secondGroupIndex);
		second = (second == null ? "00" : second);
		
		millisecond  = matcher.group(millisecondGroupIndex);
		millisecond = (millisecond == null ? "000000" : millisecond);
		
		timezoneOffset  = matcher.group(timezoneGroupIndex);
		if(timezoneOffset == null)
		{
			Date date = utilityDateFormat.parse(year + month + day + hour + minute + second + "." + millisecond);
			
			// getOffset() returns the amount of time in milliseconds to add to UTC to get local time.
			int offset = TimeZone.getDefault().getOffset(date.getTime());
			
			int offsetMinutes = offset / (1000 * 60);
			int offsetHours = offsetMinutes / 60;
			offsetMinutes %= 60;
			
			timezoneOffset = timezoneNumberFormat.format(offsetHours * 100 + offsetMinutes);
		}
		
		//for(int groupIndex=0; groupIndex < matcher.groupCount(); ++groupIndex)
		//{
		//	String group = matcher.group(groupIndex);
		//	System.out.println("\tGroup [" + groupIndex + "] = '" + group + "'");
		//}
		String concatenatedTime = year + month + day + hour + minute + second + "." + millisecond + timezoneOffset;
		
		Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Parsing concatenated time '" + concatenatedTime + "'.");
		return super.parse(concatenatedTime);
	}

	/**
	 * @see java.text.SimpleDateFormat#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof DicomDateFormat && super.equals(obj);
	}

	/**
	 * This is simply a test driver for development.
	 * 
	 * @param argv
	 */
	public static void main(String [] argv)
	{
		DateFormat outputDf = new SimpleDateFormat("dd-MMM-yyyy @ hh:mm:ss.SSSS ZZZZZ");
		try
		{
			System.out.println( "Result is '" + outputDf.format(parseDate("2007")) ); // year
			System.out.println( "Result is '" + outputDf.format(parseDate("200712")) ); // year, month
			System.out.println( "Result is '" + outputDf.format(parseDate("20071230")) ); // year, month, day
			System.out.println( "Result is '" + outputDf.format(parseDate("2007123012")) ); // year, month, day, hour
			System.out.println( "Result is '" + outputDf.format(parseDate("200712301234")) ); // year, month, day, hour, minute
			System.out.println( "Result is '" + outputDf.format(parseDate("20071230123456")) ); // year, month, day, hour, minute, second
			System.out.println( "Result is '" + outputDf.format(parseDate("20071230123456.9999")) ); // year, month, day, hour, minute, second, millisecond
			System.out.println( "Result is '" + outputDf.format(parseDate("20071230123456.999999")) ); // year, month, day, hour, minute, second, millisecond
			
			// repeat all of the above with positive timezone
			System.out.println( "Result is '" + outputDf.format(parseDate("2007+0500")) ); // year
			System.out.println( "Result is '" + outputDf.format(parseDate("200712+0500")) ); // year, month
			System.out.println( "Result is '" + outputDf.format(parseDate("20071230+0500")) ); // year, month, day
			System.out.println( "Result is '" + outputDf.format(parseDate("2007123012+0500")) ); // year, month, day, hour
			System.out.println( "Result is '" + outputDf.format(parseDate("200712301234+0500")) ); // year, month, day, hour, minute
			System.out.println( "Result is '" + outputDf.format(parseDate("20071230123456+0500")) ); // year, month, day, hour, minute, second
			System.out.println( "Result is '" + outputDf.format(parseDate("20071230123456.9999+0500")) ); // year, month, day, hour, minute, second, millisecond
			System.out.println( "Result is '" + outputDf.format(parseDate("20071230123456.999999+0500")) ); // year, month, day, hour, minute, second, millisecond
			
			// repeat all of the above with negative timezone
			System.out.println( "Result is '" + outputDf.format(parseDate("2007-0500")) ); // year
			System.out.println( "Result is '" + outputDf.format(parseDate("200712-0500")) ); // year, month
			System.out.println( "Result is '" + outputDf.format(parseDate("20071230-0500")) ); // year, month, day
			System.out.println( "Result is '" + outputDf.format(parseDate("2007123012-0500")) ); // year, month, day, hour
			System.out.println( "Result is '" + outputDf.format(parseDate("200712301234-0500")) ); // year, month, day, hour, minute
			System.out.println( "Result is '" + outputDf.format(parseDate("20071230123456-0500")) ); // year, month, day, hour, minute, second
			System.out.println( "Result is '" + outputDf.format(parseDate("20071230123456.9999-0500")) ); // year, month, day, hour, minute, second, millisecond
			System.out.println( "Result is '" + outputDf.format(parseDate("20071230123456.999999-0500")) ); // year, month, day, hour, minute, second, millisecond
		} 
		catch(Exception x)
		{
			x.printStackTrace();
		}
	}
	private static Date parseDate(String dateString) 
	throws ParseException
	{
		DicomDateFormat df = new DicomDateFormat();
		
		System.out.println("Parsing '" + dateString + "'.");
		return df.parse(dateString);
	}
}
