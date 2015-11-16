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
public class TestWellKnownOID
extends TestCase
{

	/**
	 * Test method for {@link gov.va.med.WellKnownOID#get(java.lang.String)}.
	 */
	public void testGetString()
	{
		assertEquals(WellKnownOID.HAIMS_DOCUMENT, WellKnownOID.get("2.16.840.1.113883.3.42.10012.100001.206"));
		assertEquals(WellKnownOID.BHIE_RADIOLOGY, WellKnownOID.get("2.16.840.1.113883.3.42.10012.100001.207"));
		assertEquals(WellKnownOID.VA_DOCUMENT, WellKnownOID.get("2.16.840.1.113883.3.166"));
		assertEquals(WellKnownOID.VA_DOCUMENT, WellKnownOID.get("2.16.840.1.113883.6.233"));
		assertEquals(WellKnownOID.VA_RADIOLOGY_IMAGE, WellKnownOID.get("1.3.6.1.4.1.3768"));
		assertEquals(WellKnownOID.SNOMED, WellKnownOID.get("2.16.840.1.113883.6.96"));
		assertEquals(WellKnownOID.LOINC, WellKnownOID.get("2.16.840.1.113883.6.1"));
		assertEquals(WellKnownOID.HL7, WellKnownOID.get("2.16.840.1.113883.11.19465"));
		assertEquals(WellKnownOID.MHS, WellKnownOID.get("2.16.840.1.113883.3.42.10012.100001.205"));
	}

	/**
	 * Test method for {@link gov.va.med.WellKnownOID#get(gov.va.med.OID)}.
	 * @throws OIDFormatException 
	 */
	public void testGetOID() 
	throws OIDFormatException
	{
		assertEquals(WellKnownOID.HAIMS_DOCUMENT, WellKnownOID.get(OID.create("2.16.840.1.113883.3.42.10012.100001.206")));
		assertEquals(WellKnownOID.BHIE_RADIOLOGY, WellKnownOID.get(OID.create("2.16.840.1.113883.3.42.10012.100001.207")));
		assertEquals(WellKnownOID.VA_DOCUMENT, WellKnownOID.get(OID.create("2.16.840.1.113883.3.166")));
		assertEquals(WellKnownOID.VA_DOCUMENT, WellKnownOID.get(OID.create("2.16.840.1.113883.6.233")));
		assertEquals(WellKnownOID.VA_RADIOLOGY_IMAGE, WellKnownOID.get(OID.create("1.3.6.1.4.1.3768")));
		assertEquals(WellKnownOID.SNOMED, WellKnownOID.get(OID.create("2.16.840.1.113883.6.96")));
		assertEquals(WellKnownOID.LOINC, WellKnownOID.get(OID.create("2.16.840.1.113883.6.1")));
		assertEquals(WellKnownOID.HL7, WellKnownOID.get(OID.create("2.16.840.1.113883.11.19465")));
		assertEquals(WellKnownOID.MHS, WellKnownOID.get(OID.create("2.16.840.1.113883.3.42.10012.100001.205")));
	}

//	/**
//	 * Test method for {@link gov.va.med.WellKnownOID#getCanonicalValue()}.
//	 */
//	public void testGetCanonicalValue()
//	{
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link gov.va.med.WellKnownOID#getAllValues()}.
//	 */
//	public void testGetAllValues()
//	{
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link gov.va.med.WellKnownOID#isApplicable(java.lang.String)}.
//	 */
//	public void testIsApplicableString()
//	{
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link gov.va.med.WellKnownOID#isApplicable(gov.va.med.OID)}.
//	 */
//	public void testIsApplicableOID()
//	{
//		fail("Not yet implemented");
//	}

}
