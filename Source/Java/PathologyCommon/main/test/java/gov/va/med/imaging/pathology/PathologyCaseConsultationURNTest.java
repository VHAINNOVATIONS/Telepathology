/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 25, 2012
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.va.med.PatientIdentifier;
import gov.va.med.PatientIdentifierType;
import gov.va.med.URNFactory;

import org.junit.Test;

/**
 * @author VHAISWWERFEJ
 *
 */
public class PathologyCaseConsultationURNTest
{
	
	@Test
	public void testToString()
	{
		try
		{
			PathologyCaseURN caseUrn = PathologyCaseURN.create("660", "CY", "12", "34", 
					new PatientIdentifier("123456V789", PatientIdentifierType.icn));
			PathologyCaseConsultationURN consultationUrn = PathologyCaseConsultationURN.create("ABC", caseUrn);
			
			//System.out.println(consultationUrn.toString());
			assertEquals("urn:vapathologycaseconsultation:660-ABC-CY-12-34-icn(123456V789)", consultationUrn.toString());
			assertEquals("ABC", consultationUrn.getConsultationId());
			
			PathologyCaseConsultationURN newConsultationUrn = URNFactory.create(consultationUrn.toString(), PathologyCaseConsultationURN.class);
			assertTrue(newConsultationUrn.equals(consultationUrn));
			assertEquals(consultationUrn.toString(), newConsultationUrn.toString());
			
			PathologyCaseConsultationURN newConsutlationUrn2 = 
					PathologyCaseConsultationURN.create("660", "ABC", "CY", "12", "34", new PatientIdentifier("123456V789", PatientIdentifierType.icn));
			assertTrue(newConsutlationUrn2.equals(consultationUrn));
			assertEquals(consultationUrn.toString(), newConsutlationUrn2.toString());
			
			assertEquals("660", consultationUrn.getOriginatingSiteId());
			assertEquals("ABC", consultationUrn.getConsultationId());
			assertEquals("CY", consultationUrn.getPathologyType());
			assertEquals("12", consultationUrn.getYear());
			assertEquals("34", consultationUrn.getNumber());
			assertEquals("icn(123456V789)", consultationUrn.getPatientId().toString());
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			fail(ex.getMessage());
		}
		
	}

}
