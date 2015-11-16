/**
 * 
 */
package gov.va.med;

import gov.va.med.imaging.exceptions.OIDFormatException;
import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class OIDTest
extends TestCase
{
	public void testValidOIDCreation() 
	throws OIDFormatException
	{
		OID oid;
		
		oid = OID.create("1");
		assertNotNull(oid);
		assertEquals("1", oid.toString());
		
		oid = OID.create("1.2");
		assertNotNull(oid);
		assertEquals("1.2", oid.toString());
		
		oid = OID.create("1.2.3.4");
		assertNotNull(oid);
		assertEquals("1.2.3.4", oid.toString());
	}
	
	public void testInvalidOIDCreation() 
	{
		OID oid;
		
		try
		{
			oid = OID.create("01");
			fail("Invalid OID format was accepted");
		}
		catch (OIDFormatException x){}
		try
		{
			oid = OID.create("1.02");
			fail("Invalid OID format was accepted");
		}
		catch (OIDFormatException x){}
		try
		{
			oid = OID.create("1.2.a");
			fail("Invalid OID format was accepted");
		}
		catch (OIDFormatException x){}
	}
	
	public void testEquality() 
	throws OIDFormatException
	{
		OID oid;
		OID noid;
		
		oid = OID.create("1");
		noid = OID.create("1");
		assertEquals(oid, noid);
		
		oid = OID.create("1.2");
		noid = OID.create("1.2");
		assertEquals(oid, noid);
		
		oid = OID.create("1.2.3.4");
		noid = OID.create("1.2.3.4");
		assertEquals(oid, noid);
		
		oid = OID.create("1");
		noid = OID.create("2");
		assertTrue(! oid.equals(noid) );
		
		oid = OID.create("1");
		noid = OID.create("1.2");
		assertTrue(! oid.equals(noid) );
		
	}
	
	public void testCompare() 
	throws OIDFormatException
	{
		OID oid;
		OID noid;
		
		oid = OID.create("1");
		noid = OID.create("1");
		assertEquals(0, oid.compareTo(noid));
		
		oid = OID.create("1");
		noid = OID.create("2");
		assertTrue(oid.compareTo(noid) < 0);
		
		oid = OID.create("1");
		noid = OID.create("1.2");
		assertTrue(oid.compareTo(noid) < 0);
		
		oid = OID.create("2");
		noid = OID.create("1");
		assertTrue(oid.compareTo(noid) > 0);
		
		oid = OID.create("1.2");
		noid = OID.create("1");
		assertTrue(oid.compareTo(noid) > 0);
	}	
	
	public void testAncestry() 
	throws OIDFormatException
	{
		OID oid;
		OID noid;
		
		oid = OID.create("1");
		noid = OID.create("1");
		assertFalse(oid.isAncestorOf(noid));
		
		oid = OID.create("1");
		noid = OID.create("2");
		assertFalse(oid.isAncestorOf(noid));
		
		oid = OID.create("1");
		noid = OID.create("1.2");
		assertTrue(oid.isAncestorOf(noid));
		
		oid = OID.create("1");
		noid = OID.create("1.2.3.4");
		assertTrue(oid.isAncestorOf(noid));
		
	}
}
