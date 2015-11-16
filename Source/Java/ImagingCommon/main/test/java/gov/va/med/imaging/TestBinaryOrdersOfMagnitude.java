package gov.va.med.imaging;

import java.math.BigDecimal;
import java.math.BigInteger;

import junit.framework.TestCase;

public class TestBinaryOrdersOfMagnitude extends TestCase
{

	protected void setUp() throws Exception
	{
		super.setUp();
	}

	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	public final void testValueOfIgnoreCase()
	{
		assertTrue( BinaryOrdersOfMagnitude.KB == BinaryOrdersOfMagnitude.valueOfIgnoreCase("kb") );
		assertTrue( BinaryOrdersOfMagnitude.KB == BinaryOrdersOfMagnitude.valueOfIgnoreCase("kB") );
		assertTrue( BinaryOrdersOfMagnitude.KB == BinaryOrdersOfMagnitude.valueOfIgnoreCase("Kb") );
		assertTrue( BinaryOrdersOfMagnitude.KB == BinaryOrdersOfMagnitude.valueOfIgnoreCase("KB") );
		
		assertTrue( BinaryOrdersOfMagnitude.MB == BinaryOrdersOfMagnitude.valueOfIgnoreCase("mb") );
		assertTrue( BinaryOrdersOfMagnitude.MB == BinaryOrdersOfMagnitude.valueOfIgnoreCase("mB") );
		assertTrue( BinaryOrdersOfMagnitude.MB == BinaryOrdersOfMagnitude.valueOfIgnoreCase("Mb") );
		assertTrue( BinaryOrdersOfMagnitude.MB == BinaryOrdersOfMagnitude.valueOfIgnoreCase("MB") );
		
		try
		{
			BinaryOrdersOfMagnitude.valueOfIgnoreCase(" ");
			fail("An empty String should not have been matched to a BinaryOrdersOfMagnitude");
		}
		catch(IllegalArgumentException iaX){}
		
		try
		{
			BinaryOrdersOfMagnitude.valueOfIgnoreCase("junk");
			fail("The String 'junk' should not have been matched to a BinaryOrdersOfMagnitude");
		}
		catch(IllegalArgumentException iaX){}
	}

	public final void testGreatestMagnitudeLessThan()
	{
		assertTrue( BinaryOrdersOfMagnitude.B == BinaryOrdersOfMagnitude.greatestMagnitudeLessThan(1L) );
		assertTrue( BinaryOrdersOfMagnitude.B == BinaryOrdersOfMagnitude.greatestMagnitudeLessThan(1023L) );
		
		assertTrue( BinaryOrdersOfMagnitude.KB == BinaryOrdersOfMagnitude.greatestMagnitudeLessThan(BigInteger.valueOf(1024L)) );
		assertTrue( BinaryOrdersOfMagnitude.KB == BinaryOrdersOfMagnitude.greatestMagnitudeLessThan(1024L) );
		
	}

	public final void testFormatBigInteger()
	{
		assertEquals("1 KB", BinaryOrdersOfMagnitude.format(1024L, 0, false) );
		assertEquals("1 MB", BinaryOrdersOfMagnitude.format(1024L * 1024L, 0, false) );
	}
	
	public final void testParseString()
	{
		assertEquals(new BigDecimal(1024L), BinaryOrdersOfMagnitude.parse("1 KB") );
		assertEquals(new BigDecimal(1024L * 1024L), BinaryOrdersOfMagnitude.parse("1 MB") );
	}
}
