/**
 * 
 */
package gov.va.med.imaging.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author VHAISWBECKEC
 *
 */
public class ExceptionUtilitiesTest extends TestCase
{
	public void testReformatException()
	{
		String testString = null;
		try
		{
			testString.startsWith("C");
		}
		catch (Exception e)
		{
			String formattedException = ExceptionUtilities.convertExceptionToString(e);
			Assert.assertNotNull(formattedException);
		}
	}

}
