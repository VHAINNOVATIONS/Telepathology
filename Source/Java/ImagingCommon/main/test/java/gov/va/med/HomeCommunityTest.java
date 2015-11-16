/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Jan 18, 2011
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author vhaiswbeckec
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */

package gov.va.med;

import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.exceptions.OIDFormatException;
import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class HomeCommunityTest
	extends TestCase
{

	/**
	 * Test method for {@link gov.va.med.HomeCommunity#isWithinHomeCommunity(gov.va.med.OID)}.
	 * @throws OIDFormatException 
	 */
	public void testIsWithinHomeCommunityOID() 
	throws OIDFormatException
	{
		assertFalse( HomeCommunity.isWithinHomeCommunity(OID.create("1")) );
		assertFalse( HomeCommunity.isWithinHomeCommunity(OID.create("1.2")) );
		assertFalse( HomeCommunity.isWithinHomeCommunity(OID.create("3.4.5")) );
		assertFalse( HomeCommunity.isWithinHomeCommunity(OID.create("6.12.33.456.56")) );
		
		assertTrue( HomeCommunity.isWithinHomeCommunity(OID.create("2.16.840.1.113883.3.166")) );
		assertTrue( HomeCommunity.isWithinHomeCommunity(OID.create("2.16.840.1.113883.6.233")) );
		assertTrue( HomeCommunity.isWithinHomeCommunity(OID.create("1.3.6.1.4.1.3768")) );
	}

	/**
	 * Test method for {@link gov.va.med.HomeCommunity#isWithinHomeCommunity(gov.va.med.RoutingToken)}.
	 * @throws RoutingTokenFormatException 
	 * @throws OIDFormatException 
	 */
	public void testIsWithinHomeCommunityRoutingToken() 
	throws OIDFormatException, RoutingTokenFormatException
	{
		assertFalse( HomeCommunity.isWithinHomeCommunity(RoutingTokenImpl.createDoDDocumentSite("200")) );
		assertFalse( HomeCommunity.isWithinHomeCommunity(RoutingTokenImpl.createDoDDocumentSite("100")) );
		assertFalse( HomeCommunity.isWithinHomeCommunity(RoutingTokenImpl.createDoDRadiologySite("200")) );
		assertFalse( HomeCommunity.isWithinHomeCommunity(RoutingTokenImpl.createDoDRadiologySite("100")) );
		
		assertTrue( HomeCommunity.isWithinHomeCommunity(RoutingTokenImpl.createVADocumentSite("200")) );
		assertTrue( HomeCommunity.isWithinHomeCommunity(RoutingTokenImpl.createVARadiologySite("200")) );
		assertTrue( HomeCommunity.isWithinHomeCommunity(RoutingTokenImpl.createVADocumentSite("660")) );
		assertTrue( HomeCommunity.isWithinHomeCommunity(RoutingTokenImpl.createVARadiologySite("660")) );
		assertTrue( HomeCommunity.isWithinHomeCommunity(RoutingTokenImpl.createVADocumentSite("567")) );
		assertTrue( HomeCommunity.isWithinHomeCommunity(RoutingTokenImpl.createVARadiologySite("567")) );
	}

	/**
	 * Test method for {@link gov.va.med.HomeCommunity#isWithinHomeCommunity(java.lang.String)}.
	 * @throws OIDFormatException 
	 */
	public void testIsWithinHomeCommunityString() 
	throws OIDFormatException
	{
		assertFalse( HomeCommunity.isWithinHomeCommunity("1") );
		assertFalse( HomeCommunity.isWithinHomeCommunity("1.2") );
		assertFalse( HomeCommunity.isWithinHomeCommunity("3.4.5") );
		assertFalse( HomeCommunity.isWithinHomeCommunity("6.12.33.456.56") );
		
		assertTrue( HomeCommunity.isWithinHomeCommunity("2.16.840.1.113883.3.166") );
		assertTrue( HomeCommunity.isWithinHomeCommunity("2.16.840.1.113883.6.233") );
		assertTrue( HomeCommunity.isWithinHomeCommunity("1.3.6.1.4.1.3768") );
	}

}
