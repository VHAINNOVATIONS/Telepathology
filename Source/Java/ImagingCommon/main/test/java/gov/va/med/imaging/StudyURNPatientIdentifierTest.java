/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jan 4, 2013
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
package gov.va.med.imaging;

import gov.va.med.PatientIdentifier;
import gov.va.med.PatientIdentifierType;
import gov.va.med.SERIALIZATION_FORMAT;
import gov.va.med.URNFactory;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author VHAISWWERFEJ
 *
 */
public class StudyURNPatientIdentifierTest
{
	
	@Test
	public void testParsingBhiePatientIdentifier()
	{
		try
		{
			StudyURN studyUrn =
					URNFactory.create("urn:bhiestudy:haims-e15506fe-454f-4298-b328-93c34fd9c3e3[1006184063V088473]", 
							StudyURN.class);
			
			assertTrue(studyUrn instanceof BhieStudyURN);
			BhieStudyURN bhieStudyUrn = (BhieStudyURN)studyUrn;
			assertNull(bhieStudyUrn.getPatientIdentifierType());
			assertSame(PatientIdentifierType.icn, bhieStudyUrn.getPatientIdentifierTypeOrDefault());
			assertEquals("1006184063V088473", bhieStudyUrn.getPatientId());
			assertEquals("urn:bhiestudy:haims-e15506fe-454f-4298-b328-93c34fd9c3e3", bhieStudyUrn.toString());
			assertEquals("urn:bhiestudy:haims-e15506fe-454f-4298-b328-93c34fd9c3e3", bhieStudyUrn.toString(SERIALIZATION_FORMAT.NATIVE));
			assertEquals("urn:vastudy:200-haims%2de15506fe%2d454f%2d4298%2db328%2d93c34fd9c3e3[1006184063V088473]", bhieStudyUrn.toString(SERIALIZATION_FORMAT.CDTP));
			assertEquals("urn:bhiestudy:haims-e15506fe-454f-4298-b328-93c34fd9c3e3[1006184063V088473]", bhieStudyUrn.toString(SERIALIZATION_FORMAT.RAW));
			assertEquals("urn:bhiestudy:haims-e15506fe-454f-4298-b328-93c34fd9c3e3%5b1006184063V088473%5d", bhieStudyUrn.toString(SERIALIZATION_FORMAT.VFTP));
			
		}
		catch(Exception ex)
		{
			fail(ex.getMessage());
		}		
	}
	
	@Test
	public void testCreatingBhiePatientIdentifier()
	{
		try
		{
			StudyURN studyUrn = URNFactory.create("urn:bhiestudy:haims-e15506fe-454f-4298-b328-93c34fd9c3e3", 
							StudyURN.class);
			assertTrue(studyUrn instanceof BhieStudyURN);
			BhieStudyURN bhieStudyUrn = (BhieStudyURN)studyUrn;
			assertNull(bhieStudyUrn.getPatientIdentifierType());
			assertNull(bhieStudyUrn.getPatientId());
			assertNull(bhieStudyUrn.getPatientIdentifier());
			
			bhieStudyUrn.setPatientId("12345");
			
			assertEquals(PatientIdentifierType.icn, bhieStudyUrn.getPatientIdentifierTypeOrDefault());
			
			assertEquals("urn:bhiestudy:haims-e15506fe-454f-4298-b328-93c34fd9c3e3", bhieStudyUrn.toString());
			assertEquals("urn:bhiestudy:haims-e15506fe-454f-4298-b328-93c34fd9c3e3", bhieStudyUrn.toString(SERIALIZATION_FORMAT.NATIVE));
			assertEquals("urn:vastudy:200-haims%2de15506fe%2d454f%2d4298%2db328%2d93c34fd9c3e3[12345]", bhieStudyUrn.toString(SERIALIZATION_FORMAT.CDTP));
			assertEquals("urn:bhiestudy:haims-e15506fe-454f-4298-b328-93c34fd9c3e3[12345]", bhieStudyUrn.toString(SERIALIZATION_FORMAT.RAW));
			assertEquals("urn:bhiestudy:haims-e15506fe-454f-4298-b328-93c34fd9c3e3%5b12345%5d", bhieStudyUrn.toString(SERIALIZATION_FORMAT.VFTP));
			
			
			
			bhieStudyUrn.setPatientIdentifierType(PatientIdentifierType.dfn);
			assertNotNull(bhieStudyUrn.getPatientId());
			assertNotNull(bhieStudyUrn.getPatientIdentifier());
			assertNotNull(bhieStudyUrn.getPatientIdentifierType());
			assertEquals(PatientIdentifierType.dfn, bhieStudyUrn.getPatientIdentifierType());
			assertEquals("12345", bhieStudyUrn.getPatientId());
						
			assertEquals("urn:bhiestudy:haims-e15506fe-454f-4298-b328-93c34fd9c3e3", bhieStudyUrn.toString());
			assertEquals("urn:bhiestudy:haims-e15506fe-454f-4298-b328-93c34fd9c3e3", bhieStudyUrn.toString(SERIALIZATION_FORMAT.NATIVE));
			assertEquals("urn:vastudy:200-haims%2de15506fe%2d454f%2d4298%2db328%2d93c34fd9c3e3[12345][dfn]", bhieStudyUrn.toString(SERIALIZATION_FORMAT.CDTP));
			assertEquals("urn:bhiestudy:haims-e15506fe-454f-4298-b328-93c34fd9c3e3[12345][dfn]", bhieStudyUrn.toString(SERIALIZATION_FORMAT.RAW));
			assertEquals("urn:bhiestudy:haims-e15506fe-454f-4298-b328-93c34fd9c3e3%5b12345%5d%5bdfn%5d", bhieStudyUrn.toString(SERIALIZATION_FORMAT.VFTP));
			
			/*
			System.out.println("toString(): " + bhieStudyUrn.toString());
			System.out.println("toStringCDTP(): " + bhieStudyUrn.toString(SERIALIZATION_FORMAT.CDTP));
			System.out.println("toString(RAW): " + bhieStudyUrn.toString(SERIALIZATION_FORMAT.RAW));
			System.out.println("toString(VFTP): " + bhieStudyUrn.toString(SERIALIZATION_FORMAT.VFTP));
			*/
		}
		catch(Exception ex)
		{
			fail(ex.getMessage());
		}
	}
	
	@Test
	public void testCreatingStudyURNPatientIdentifier()
	{
		try
		{
			StudyURN studyUrn = StudyURN.create("660", "123", "456V789");
			assertNull(studyUrn.getPatientIdentifierType());
			assertEquals(PatientIdentifierType.icn, studyUrn.getPatientIdentifierTypeOrDefault());
			PatientIdentifier expectedPatientIdentifier = new PatientIdentifier("456V789", PatientIdentifierType.icn);
			assertEquals(expectedPatientIdentifier, studyUrn.getThePatientIdentifier());
			
			assertEquals("urn:vastudy:660-123-456V789", studyUrn.toString());
			assertEquals("urn:vastudy:660-123-456V789", studyUrn.toString(SERIALIZATION_FORMAT.NATIVE));
			assertEquals("urn:vastudy:660-123-456V789", studyUrn.toString(SERIALIZATION_FORMAT.CDTP));
			assertEquals("urn:vastudy:660-123-456V789", studyUrn.toString(SERIALIZATION_FORMAT.RAW));
			assertEquals("urn:vastudy:660-123-456V789", studyUrn.toString(SERIALIZATION_FORMAT.VFTP));
			
			studyUrn.setPatientIdentifierType(PatientIdentifierType.icn);
			assertEquals(PatientIdentifierType.icn, studyUrn.getPatientIdentifierType());
			
			assertEquals("urn:vastudy:660-123-456V789", studyUrn.toString());
			assertEquals("urn:vastudy:660-123-456V789", studyUrn.toString(SERIALIZATION_FORMAT.NATIVE));
			assertEquals("urn:vastudy:660-123-456V789[icn]", studyUrn.toString(SERIALIZATION_FORMAT.CDTP));
			assertEquals("urn:vastudy:660-123-456V789[icn]", studyUrn.toString(SERIALIZATION_FORMAT.RAW));
			assertEquals("urn:vastudy:660-123-456V789%5bicn%5d", studyUrn.toString(SERIALIZATION_FORMAT.VFTP));
			
			// DFN
			studyUrn = StudyURN.create("660", "123", "456");
			assertNull(studyUrn.getPatientIdentifierType());
			studyUrn.setPatientIdentifierType(PatientIdentifierType.dfn);
			assertEquals(PatientIdentifierType.dfn, studyUrn.getPatientIdentifierTypeOrDefault());
			expectedPatientIdentifier = new PatientIdentifier("456", PatientIdentifierType.dfn);
			assertEquals(expectedPatientIdentifier, studyUrn.getThePatientIdentifier());
			
			assertEquals("urn:vastudy:660-123-456", studyUrn.toString());
			assertEquals("urn:vastudy:660-123-456", studyUrn.toString(SERIALIZATION_FORMAT.NATIVE));
			assertEquals("urn:vastudy:660-123-456[dfn]", studyUrn.toString(SERIALIZATION_FORMAT.CDTP));
			assertEquals("urn:vastudy:660-123-456[dfn]", studyUrn.toString(SERIALIZATION_FORMAT.RAW));
			assertEquals("urn:vastudy:660-123-456%5bdfn%5d", studyUrn.toString(SERIALIZATION_FORMAT.VFTP));
			
			/*
			System.out.println("toString(): " + studyUrn.toString());
			System.out.println("toString(NATIVE): " + studyUrn.toString(SERIALIZATION_FORMAT.NATIVE));
			System.out.println("toStringCDTP(): " + studyUrn.toString(SERIALIZATION_FORMAT.CDTP));
			System.out.println("toString(RAW): " + studyUrn.toString(SERIALIZATION_FORMAT.RAW));
			System.out.println("toString(VFTP): " + studyUrn.toString(SERIALIZATION_FORMAT.VFTP));
			*/
		}
		catch(Exception ex)
		{
			fail(ex.getMessage());
		}
	}
	
	@Test
	public void testParsingStudyURNPatientIdentifier()
	{
		try
		{
			StudyURN studyUrn = URNFactory.create("urn:vastudy:660-123-456V789", StudyURN.class);
			assertNull(studyUrn.getPatientIdentifierType());
			assertEquals(PatientIdentifierType.icn, studyUrn.getDefaultPatientIdentifierType());
			assertEquals("456V789", studyUrn.getPatientId());
			
			studyUrn = URNFactory.create("urn:vastudy:660-123-456V789[icn]", StudyURN.class);
			assertNotNull(studyUrn.getPatientIdentifierType());
			assertEquals(PatientIdentifierType.icn, studyUrn.getPatientIdentifierType());
			assertEquals("456V789", studyUrn.getPatientId());
			
			studyUrn = URNFactory.create("urn:vastudy:660-123-456[dfn]", StudyURN.class);
			assertNotNull(studyUrn.getPatientIdentifierType());
			assertEquals(PatientIdentifierType.dfn, studyUrn.getPatientIdentifierType());
			assertEquals("456", studyUrn.getPatientId());
		}
		catch(Exception ex)
		{
			fail(ex.getMessage());
		}
	}
	
	/*
	@Test
	public void testCreatingHaimsPatientIdentifier()
	{
		try
		{
			//urn:paid:2.16.840.1.113883.3.198-2.16.840.1.113883.3.198.1-45678901211022010134659
			GlobalArtifactIdentifier gaid =  GlobalArtifactIdentifierFactory.create("2.16.840.1.113883.3.198", "2.16.840.1.113883.3.198.1", "45678901211022010134659");
			PatientArtifactIdentifier paid = PatientArtifactIdentifierImpl.create(gaid, "1006184063V088473");
			
			System.out.println("toString(): " + paid.toString());
			System.out.println("toString(NATIVE): " + paid.toString(SERIALIZATION_FORMAT.NATIVE));
			System.out.println("toStringCDTP(): " + paid.toString(SERIALIZATION_FORMAT.CDTP));
			System.out.println("toString(RAW): " + paid.toString(SERIALIZATION_FORMAT.RAW));
			System.out.println("toString(VFTP): " + paid.toString(SERIALIZATION_FORMAT.VFTP));
		}
		catch(Exception ex)
		{
			fail(ex.getMessage());
		}
	}*/

}
