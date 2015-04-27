/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 21, 2012
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
  Description: 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+

 */
package gov.va.med.imaging.pathology;

import gov.va.med.PatientIdentifier;
import gov.va.med.PatientIdentifierType;
import gov.va.med.URNFactory;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author VHAISWWERFEJ
 *
 */
public class PathologyCaseURNTest
{
	
	@Test
	public void testToString()
	{
		try
		{
			PathologyCaseURN urn = PathologyCaseURN.create("660", "CY", "12", "34", 
					new PatientIdentifier("123456V789", PatientIdentifierType.icn));
			assertEquals("urn:vapathologycase:660-CY-12-34-icn(123456V789)", urn.toString());
			assertEquals("CY 12 34", urn.toStringAccessionNumber());
			
			String urnString = urn.toString();
			
			PathologyCaseURN newCaseUrn = URNFactory.create(urnString, PathologyCaseURN.class);
			assertTrue(newCaseUrn.equals(urn));
			
			assertEquals(urn.toString(), newCaseUrn.toString());
			
			// test with DFN
			PathologyCaseURN dfnUrn = PathologyCaseURN.create("660", "CY", "12", "34", 
					new PatientIdentifier("123456V789", PatientIdentifierType.dfn));
			assertNotSame(urnString,  dfnUrn);
			assertEquals("urn:vapathologycase:660-CY-12-34-dfn(123456V789)", dfnUrn.toString());
			
			String dfnUrnString = dfnUrn.toString();
			PathologyCaseURN newCaseDfnUrn = URNFactory.create(dfnUrnString, PathologyCaseURN.class);
			assertTrue(newCaseDfnUrn.equals(dfnUrn));
			
			assertEquals(dfnUrn.toString(), newCaseDfnUrn.toString());
		}
		catch(Exception ex)
		{
			fail(ex.getMessage());
		}
		
	}

}
