/**
 * 
 */
package gov.va.med.imaging;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import junit.framework.TestCase;

/**
 * @author VHAISWBECKEC
 *
 */
public class DicomDateFormatTest extends TestCase
{
	private static final int stimulusDataIndex = 0;
	private static final int resultsDataIndex = 1;
	private static final String[][] testData = new String[][]
    {
		{"19000101", "01-Jan-1900@00:00:00.0000 -0500"},
		{"2007", "01-Jan-2007@00:00:00.0000 -0500"},
		{"200712", "01-Dec-2007@00:00:00.0000 -0500"},
		{"20071230", "30-Dec-2007@00:00:00.0000 -0500"},
		{"2007123012", "30-Dec-2007@12:00:00.0000 -0500"},
		{"200712301234", "30-Dec-2007@12:34:00.0000 -0500"},
		{"20071230123456", "30-Dec-2007@12:34:56.0000 -0500"},
		{"20071230123456.9999", "30-Dec-2007@12:34:56.9999 -0500"},
		{"20071230123456.999999", "30-Dec-2007@12:34:56.999999 -0500"},
		{"2007+0500", "31-Dec-2006@14:00:00.0000 -0500"},				// 01Jan2007 midnight somewhere in Asia is 31Dec2006 2PM in EST
		{"200712+0500", "30-Nov-2007@14:00:00.0000 -0500"},				// 01Dec2007 midnight somewhere in Asia is 30Nov2007 2PM in EST
		{"20071230+0500", "29-Dec-2007@14:00:00.0000 -0500"},			// 30Dec2007 midnight somewhere in Asia is 29Dec2007 2PM in EST
		{"2007123012+0500", "30-Dec-2007@02:00:00.0000 -0500"},			// 30Dec2007 noon somewhere in Asia is 30Dec2007 2AM in EST
		{"200712301234+0500", "30-Dec-2007@02:34:00.0000 -0500"},		// 30Dec2007 12:34 somewhere in Asia is 30Dec2007 2:34AM in EST
		{"20071230123456+0500", "30-Dec-2007@02:34:56.0000 -0500"}		// 30Dec2007 12:34:56 somewhere in Asia is 30Dec2007 2:34:56AM in EST
		
    };
	
	private DicomDateFormat dicomDf = new DicomDateFormat();
	private DateFormat resultDf = new SimpleDateFormat("dd-MMM-yyyy@HH:mm:ss.S ZZZZZ");
	
	/**
	 * Test method for {@link gov.va.med.imaging.DicomDateFormat#parse(java.lang.String)}.
	 */
	public void testParseString()
	{
		// If the VM has a default other than GMT-5 (i.e. EST) we need to switch to GMT-5 for the tests to work
		TimeZone.setDefault(TimeZone.getTimeZone("GMT-5"));
		
		for(String[] testSample : testData)
		{
			String stimulus = testSample[stimulusDataIndex];
			String desiredResult = testSample[resultsDataIndex];
			
			try
			{
				Date dicomDate = dicomDf.parse(stimulus);
				Date resultsDate = resultDf.parse(desiredResult);
				
				assertEquals("Failure translating '" + dicomDate + "'", dicomDate, resultsDate);
			} 
			catch (ParseException x)
			{
				fail(x.getMessage());
			}
			
		}
	}

//	private void sumpin()
//	{
//		System.out.println( "Result is '" + outputDf.format(parseDate("2007")) ); // year
//		System.out.println( "Result is '" + outputDf.format(parseDate("200712")) ); // year, month
//		System.out.println( "Result is '" + outputDf.format(parseDate("20071230")) ); // year, month, day
//		System.out.println( "Result is '" + outputDf.format(parseDate("2007123012")) ); // year, month, day, hour
//		System.out.println( "Result is '" + outputDf.format(parseDate("200712301234")) ); // year, month, day, hour, minute
//		System.out.println( "Result is '" + outputDf.format(parseDate("20071230123456")) ); // year, month, day, hour, minute, second
//		System.out.println( "Result is '" + outputDf.format(parseDate("20071230123456.9999")) ); // year, month, day, hour, minute, second, millisecond
//		System.out.println( "Result is '" + outputDf.format(parseDate("20071230123456.999999")) ); // year, month, day, hour, minute, second, millisecond
//		
//		// repeat all of the above with positive timezone
//		System.out.println( "Result is '" + outputDf.format(parseDate("2007+0500")) ); // year
//		System.out.println( "Result is '" + outputDf.format(parseDate("200712+0500")) ); // year, month
//		System.out.println( "Result is '" + outputDf.format(parseDate("20071230+0500")) ); // year, month, day
//		System.out.println( "Result is '" + outputDf.format(parseDate("2007123012+0500")) ); // year, month, day, hour
//		System.out.println( "Result is '" + outputDf.format(parseDate("200712301234+0500")) ); // year, month, day, hour, minute
//		System.out.println( "Result is '" + outputDf.format(parseDate("20071230123456+0500")) ); // year, month, day, hour, minute, second
//		System.out.println( "Result is '" + outputDf.format(parseDate("20071230123456.9999+0500")) ); // year, month, day, hour, minute, second, millisecond
//		System.out.println( "Result is '" + outputDf.format(parseDate("20071230123456.999999+0500")) ); // year, month, day, hour, minute, second, millisecond
//		
//		// repeat all of the above with negative timezone
//		System.out.println( "Result is '" + outputDf.format(parseDate("2007-0500")) ); // year
//		System.out.println( "Result is '" + outputDf.format(parseDate("200712-0500")) ); // year, month
//		System.out.println( "Result is '" + outputDf.format(parseDate("20071230-0500")) ); // year, month, day
//		System.out.println( "Result is '" + outputDf.format(parseDate("2007123012-0500")) ); // year, month, day, hour
//		System.out.println( "Result is '" + outputDf.format(parseDate("200712301234-0500")) ); // year, month, day, hour, minute
//		System.out.println( "Result is '" + outputDf.format(parseDate("20071230123456-0500")) ); // year, month, day, hour, minute, second
//		System.out.println( "Result is '" + outputDf.format(parseDate("20071230123456.9999-0500")) ); // year, month, day, hour, minute, second, millisecond
//		System.out.println( "Result is '" + outputDf.format(parseDate("20071230123456.999999-0500")) ); // year, month, day, hour, minute, second, millisecond
//		
//	}
}
