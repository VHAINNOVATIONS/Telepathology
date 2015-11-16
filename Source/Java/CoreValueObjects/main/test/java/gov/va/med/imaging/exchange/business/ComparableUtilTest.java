package gov.va.med.imaging.exchange.business;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

public class ComparableUtilTest 
extends TestCase
{
	public void testCompareStringStringBoolean()
	{
		assertTrue( ComparableUtil.compare(null, "hello", true) > 0 );
		assertTrue( ComparableUtil.compare("hello", null, true) < 0 );

		assertTrue( ComparableUtil.compare(null, "hello", false) > 0 );
		assertTrue( ComparableUtil.compare("hello", null, false) < 0 );
		
		assertTrue( ComparableUtil.compare((String)null, (String)null, true) == 0 );
		assertTrue( ComparableUtil.compare((String)null, (String)null, false) == 0 );
		
		assertTrue( ComparableUtil.compare("hello", "hello", true) == 0 );
		assertTrue( ComparableUtil.compare("hello", "hello", false) == 0 );
		
		assertTrue( ComparableUtil.compare("hello", "world", true) < 0 );
		assertTrue( ComparableUtil.compare("hello", "world", false) > 0 );
		
		assertTrue( ComparableUtil.compare("world", "hello", true) > 0 );
		assertTrue( ComparableUtil.compare("world", "hello", false) < 0 );
	}
	
	public void testComparedateDateBoolean() 
	throws ParseException
	{
		DateFormat df = new SimpleDateFormat("ddMMMyyyy");
		Date jan012007 = df.parse("01Jan2007");
		Date dec312007 = df.parse("31Dec2007");
		
		assertTrue( ComparableUtil.compare(null, jan012007, true) > 0 );
		assertTrue( ComparableUtil.compare(jan012007, null, true) < 0 );

		assertTrue( ComparableUtil.compare(null, jan012007, false) > 0 );
		assertTrue( ComparableUtil.compare(jan012007, null, false) < 0 );
		
		assertTrue( ComparableUtil.compare((Date)null, (Date)null, true) == 0 );
		assertTrue( ComparableUtil.compare((Date)null, (Date)null, false) == 0 );
		
		assertTrue( ComparableUtil.compare(jan012007, jan012007, true) == 0 );
		assertTrue( ComparableUtil.compare(jan012007, jan012007, false) == 0 );
		
		assertTrue( ComparableUtil.compare(jan012007, dec312007, true) < 0 );
		assertTrue( ComparableUtil.compare(jan012007, dec312007, false) > 0 );
		
		assertTrue( ComparableUtil.compare(dec312007, jan012007, true) > 0 );
		assertTrue( ComparableUtil.compare(dec312007, jan012007, false) < 0 );
		
	}
}
