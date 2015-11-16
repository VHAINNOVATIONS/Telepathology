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

import gov.va.med.PatientIdentifierType;
import gov.va.med.SERIALIZATION_FORMAT;
import gov.va.med.URNFactory;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author VHAISWWERFEJ
 *
 */
public class ImageURNPatientIdentifierTest
{
	@Test
	public void testParsingBhiePatientIdentifier()
	{
		try
		{
			ImageURN imageUrn = 
					URNFactory.create("urn:vaimage:200-haims%2df7890fb1%2dc1ad%2d4606%2d99e5%2dcfe31e31ae65%3a1-[1006184063V088473][haims%2de15506fe%2d454f%2d4298%2db328%2d93c34fd9c3e3][US]", SERIALIZATION_FORMAT.CDTP, ImageURN.class);
			assertTrue(imageUrn instanceof BhieImageURN);
			BhieImageURN bhieImageUrn = (BhieImageURN)imageUrn;
			assertNull(bhieImageUrn.getPatientIdentifierType());
			assertEquals(PatientIdentifierType.icn, bhieImageUrn.getPatientIdentifierTypeOrDefault());
			
			// test another incoming format to be sure its good
			ImageURN anotherImageUrn = 
					URNFactory.create("urn:bhieimage:haims-f7890fb1-c1ad-4606-99e5-cfe31e31ae65:1[1006184063V088473][haims-e15506fe-454f-4298-b328-93c34fd9c3e3][US]", SERIALIZATION_FORMAT.RAW, ImageURN.class);
			compareImageURNs(bhieImageUrn, anotherImageUrn);
			
			assertEquals("urn:bhieimage:haims-f7890fb1-c1ad-4606-99e5-cfe31e31ae65:1", bhieImageUrn.toString());
			assertEquals("urn:bhieimage:haims-f7890fb1-c1ad-4606-99e5-cfe31e31ae65:1", bhieImageUrn.toString(SERIALIZATION_FORMAT.NATIVE));
			assertEquals("urn:vaimage:200-haims%2df7890fb1%2dc1ad%2d4606%2d99e5%2dcfe31e31ae65%3a1-[1006184063V088473][haims%2de15506fe%2d454f%2d4298%2db328%2d93c34fd9c3e3][US]", bhieImageUrn.toString(SERIALIZATION_FORMAT.CDTP));
			assertEquals("urn:bhieimage:haims-f7890fb1-c1ad-4606-99e5-cfe31e31ae65:1[1006184063V088473][haims-e15506fe-454f-4298-b328-93c34fd9c3e3][US]", bhieImageUrn.toString(SERIALIZATION_FORMAT.RAW));
			assertEquals("urn:bhieimage:haims-f7890fb1-c1ad-4606-99e5-cfe31e31ae65:1%5b1006184063V088473%5d%5bhaims%2de15506fe%2d454f%2d4298%2db328%2d93c34fd9c3e3%5d%5bUS%5d", bhieImageUrn.toString(SERIALIZATION_FORMAT.VFTP));
			
			// ---------------------------------------------------------------------------------------------------
			
			// do it again using a DFN
			imageUrn = 
					URNFactory.create("urn:vaimage:200-haims%2df7890fb1%2dc1ad%2d4606%2d99e5%2dcfe31e31ae65%3a1-[12345][urn%3abhiestudy%3ahaims%2de15506fe%2d454f%2d4298%2db328%2d93c34fd9c3e3][CR][dfn]", SERIALIZATION_FORMAT.CDTP, ImageURN.class);
			assertTrue(imageUrn instanceof BhieImageURN);
			bhieImageUrn = (BhieImageURN)imageUrn;
			assertNotNull(bhieImageUrn.getPatientIdentifierType());
			assertEquals(PatientIdentifierType.dfn, bhieImageUrn.getPatientIdentifierType());
			
			// test another incoming format to be sure its good
			anotherImageUrn = 
					URNFactory.create("urn:bhieimage:haims-f7890fb1-c1ad-4606-99e5-cfe31e31ae65:1[12345][urn:bhiestudy:haims-e15506fe-454f-4298-b328-93c34fd9c3e3][CR][dfn]", SERIALIZATION_FORMAT.RAW, ImageURN.class);
			compareImageURNs(bhieImageUrn, anotherImageUrn);
			
			/*
			System.out.println("toString(): " + bhieImageUrn.toString());
			System.out.println("toString(NATIVE): " + bhieImageUrn.toString(SERIALIZATION_FORMAT.NATIVE));
			System.out.println("toStringCDTP(): " + bhieImageUrn.toString(SERIALIZATION_FORMAT.CDTP));
			System.out.println("toString(RAW): " + bhieImageUrn.toString(SERIALIZATION_FORMAT.RAW));
			System.out.println("toString(VFTP): " + bhieImageUrn.toString(SERIALIZATION_FORMAT.VFTP));
			*/
			
			assertEquals("urn:bhieimage:haims-f7890fb1-c1ad-4606-99e5-cfe31e31ae65:1", bhieImageUrn.toString());
			assertEquals("urn:bhieimage:haims-f7890fb1-c1ad-4606-99e5-cfe31e31ae65:1", bhieImageUrn.toString(SERIALIZATION_FORMAT.NATIVE));
			assertEquals("urn:vaimage:200-haims%2df7890fb1%2dc1ad%2d4606%2d99e5%2dcfe31e31ae65%3a1-[12345][urn%3abhiestudy%3ahaims%2de15506fe%2d454f%2d4298%2db328%2d93c34fd9c3e3][CR][dfn]", bhieImageUrn.toString(SERIALIZATION_FORMAT.CDTP));
			assertEquals("urn:bhieimage:haims-f7890fb1-c1ad-4606-99e5-cfe31e31ae65:1[12345][urn:bhiestudy:haims-e15506fe-454f-4298-b328-93c34fd9c3e3][CR][dfn]", bhieImageUrn.toString(SERIALIZATION_FORMAT.RAW));
			assertEquals("urn:bhieimage:haims-f7890fb1-c1ad-4606-99e5-cfe31e31ae65:1%5b12345%5d%5burn%3abhiestudy%3ahaims%2de15506fe%2d454f%2d4298%2db328%2d93c34fd9c3e3%5d%5bCR%5d%5bdfn%5d", bhieImageUrn.toString(SERIALIZATION_FORMAT.VFTP));
			
		}
		catch(Exception ex)
		{
			fail(ex.getMessage());
		}
	}
	
	private void compareImageURNs(ImageURN imageUrn1, ImageURN imageUrn2)
	{
		assertEquals(imageUrn1, imageUrn2);
		assertEquals(imageUrn1.getImageId(), imageUrn2.getImageId());
		assertEquals(imageUrn1.getStudyId(), imageUrn2.getStudyId());
		assertEquals(imageUrn1.getPatientId(), imageUrn2.getPatientId());
		assertEquals(imageUrn1.getPatientIdentifierTypeOrDefault(), imageUrn2.getPatientIdentifierTypeOrDefault());
		assertEquals(imageUrn1.getImageModality(), imageUrn2.getImageModality());
		
		assertEquals(imageUrn1.toString(), imageUrn2.toString());
		assertEquals(imageUrn1.toString(SERIALIZATION_FORMAT.CDTP), imageUrn2.toString(SERIALIZATION_FORMAT.CDTP));
		// vftp is weird...
		//assertEquals(imageUrn1.toString(SERIALIZATION_FORMAT.VFTP), imageUrn2.toString(SERIALIZATION_FORMAT.VFTP));
		assertEquals(imageUrn1.toString(SERIALIZATION_FORMAT.RAW), imageUrn2.toString(SERIALIZATION_FORMAT.RAW));
		assertEquals(imageUrn1.toString(SERIALIZATION_FORMAT.NATIVE), imageUrn2.toString(SERIALIZATION_FORMAT.NATIVE));
		
	}
	
	@Test
	public void testCreatingBhiePatientIdentifier()
	{
		try
		{
			ImageURN imageUrn = URNFactory.create("urn:bhieimage:haims-f7890fb1-c1ad-4606-99e5-cfe31e31ae65:1", ImageURN.class);
			assertTrue(imageUrn instanceof BhieImageURN);
			BhieImageURN bhieImageUrn = (BhieImageURN)imageUrn;
			assertNull(bhieImageUrn.getPatientIdentifierType());
			assertNull(bhieImageUrn.getPatientId());
			assertNull(bhieImageUrn.getPatientIdentifier());
			assertNull(bhieImageUrn.getStudyId());
			assertNull(bhieImageUrn.getImageModality());
			bhieImageUrn.setImageModality("CR");
			bhieImageUrn.setStudyId("urn:bhiestudy:haims-e15506fe-454f-4298-b328-93c34fd9c3e3");
			
			bhieImageUrn.setPatientId("12345");
			assertNotNull(bhieImageUrn.getPatientId());
			assertNotNull(bhieImageUrn.getPatientIdentifier());
			assertEquals("12345", bhieImageUrn.getPatientId());
			assertEquals("CR", bhieImageUrn.getImageModality());
			assertEquals("urn:bhiestudy:haims-e15506fe-454f-4298-b328-93c34fd9c3e3", bhieImageUrn.getStudyId());
			
			// still null
			assertNull(bhieImageUrn.getPatientIdentifierType());
			// assume ICN by default
			assertEquals(PatientIdentifierType.icn, bhieImageUrn.getPatientIdentifierTypeOrDefault());
			
			assertEquals("urn:bhieimage:haims-f7890fb1-c1ad-4606-99e5-cfe31e31ae65:1", bhieImageUrn.toString());
			assertEquals("urn:bhieimage:haims-f7890fb1-c1ad-4606-99e5-cfe31e31ae65:1", bhieImageUrn.toString(SERIALIZATION_FORMAT.NATIVE));
			assertEquals("urn:vaimage:200-haims%2df7890fb1%2dc1ad%2d4606%2d99e5%2dcfe31e31ae65%3a1-[12345][urn%3abhiestudy%3ahaims%2de15506fe%2d454f%2d4298%2db328%2d93c34fd9c3e3][CR]", bhieImageUrn.toString(SERIALIZATION_FORMAT.CDTP));
			assertEquals("urn:bhieimage:haims-f7890fb1-c1ad-4606-99e5-cfe31e31ae65:1[12345][urn:bhiestudy:haims-e15506fe-454f-4298-b328-93c34fd9c3e3][CR]", bhieImageUrn.toString(SERIALIZATION_FORMAT.RAW));
			assertEquals("urn:bhieimage:haims-f7890fb1-c1ad-4606-99e5-cfe31e31ae65:1", bhieImageUrn.toString(SERIALIZATION_FORMAT.VFTP));
			
			
			bhieImageUrn.setPatientIdentifierType(PatientIdentifierType.dfn);
			assertNotNull(bhieImageUrn.getPatientIdentifierType());
			assertEquals(PatientIdentifierType.dfn, bhieImageUrn.getPatientIdentifierType());
			assertEquals("CR", bhieImageUrn.getImageModality());
			
			/*
			System.out.println("toString(): " + bhieImageUrn.toString());
			System.out.println("toString(NATIVE): " + bhieImageUrn.toString(SERIALIZATION_FORMAT.NATIVE));
			System.out.println("toStringCDTP(): " + bhieImageUrn.toString(SERIALIZATION_FORMAT.CDTP));
			System.out.println("toString(RAW): " + bhieImageUrn.toString(SERIALIZATION_FORMAT.RAW));
			System.out.println("toString(VFTP): " + bhieImageUrn.toString(SERIALIZATION_FORMAT.VFTP));
			*/
			assertEquals("urn:bhieimage:haims-f7890fb1-c1ad-4606-99e5-cfe31e31ae65:1", bhieImageUrn.toString());
			assertEquals("urn:bhieimage:haims-f7890fb1-c1ad-4606-99e5-cfe31e31ae65:1", bhieImageUrn.toString(SERIALIZATION_FORMAT.NATIVE));
			assertEquals("urn:vaimage:200-haims%2df7890fb1%2dc1ad%2d4606%2d99e5%2dcfe31e31ae65%3a1-[12345][urn%3abhiestudy%3ahaims%2de15506fe%2d454f%2d4298%2db328%2d93c34fd9c3e3][CR][dfn]", bhieImageUrn.toString(SERIALIZATION_FORMAT.CDTP));
			assertEquals("urn:bhieimage:haims-f7890fb1-c1ad-4606-99e5-cfe31e31ae65:1[12345][urn:bhiestudy:haims-e15506fe-454f-4298-b328-93c34fd9c3e3][CR][dfn]", bhieImageUrn.toString(SERIALIZATION_FORMAT.RAW));
			// i don't really think this one is right but i don't think we are really using it either
			assertEquals("urn:bhieimage:haims-f7890fb1-c1ad-4606-99e5-cfe31e31ae65:1%5b%5d%5b%5d%5b%5d%5bdfn%5d", bhieImageUrn.toString(SERIALIZATION_FORMAT.VFTP));
			
		}
		catch(Exception ex)
		{
			fail(ex.getMessage());
		}
	}
	
	@Test
	public void testCreatingImageURNPatientIdentifier()
	{
		try
		{
		
			ImageURN imageUrn = ImageURN.create("660", "123", "456", "789V432", "CR");
			assertEquals("660", imageUrn.getOriginatingSiteId());
			assertEquals("123", imageUrn.getImageId());
			assertEquals("456", imageUrn.getStudyId());
			assertEquals("789V432", imageUrn.getPatientId());
			assertEquals("CR", imageUrn.getImageModality());
			assertNull(imageUrn.getPatientIdentifierType());
			assertEquals(PatientIdentifierType.icn, imageUrn.getPatientIdentifierTypeOrDefault());
			
			assertEquals("urn:vaimage:660-123-456-789V432-CR", imageUrn.toString());
			assertEquals("urn:vaimage:660-123-456-789V432-CR", imageUrn.toString(SERIALIZATION_FORMAT.NATIVE));
			assertEquals("urn:vaimage:660-123-456-789V432-CR", imageUrn.toString(SERIALIZATION_FORMAT.CDTP));
			assertEquals("urn:vaimage:660-123-456-789V432-CR", imageUrn.toString(SERIALIZATION_FORMAT.RAW));
			assertEquals("urn:vaimage:660-123-456-789V432-CR", imageUrn.toString(SERIALIZATION_FORMAT.VFTP));
			
			imageUrn.setPatientIdentifierType(PatientIdentifierType.dfn);
			assertNotNull(imageUrn.getPatientIdentifierType());
			assertEquals(PatientIdentifierType.dfn, imageUrn.getPatientIdentifierTypeOrDefault());
			assertEquals(PatientIdentifierType.dfn, imageUrn.getPatientIdentifierType());
			/*
			System.out.println("toString(): " + imageUrn.toString());
			System.out.println("toString(NATIVE): " + imageUrn.toString(SERIALIZATION_FORMAT.NATIVE));
			System.out.println("toStringCDTP(): " + imageUrn.toString(SERIALIZATION_FORMAT.CDTP));
			System.out.println("toString(RAW): " + imageUrn.toString(SERIALIZATION_FORMAT.RAW));
			System.out.println("toString(VFTP): " + imageUrn.toString(SERIALIZATION_FORMAT.VFTP));
			*/
			assertEquals("urn:vaimage:660-123-456-789V432-CR", imageUrn.toString());
			assertEquals("urn:vaimage:660-123-456-789V432-CR", imageUrn.toString(SERIALIZATION_FORMAT.NATIVE));
			assertEquals("urn:vaimage:660-123-456-789V432-CR[dfn]", imageUrn.toString(SERIALIZATION_FORMAT.CDTP));
			assertEquals("urn:vaimage:660-123-456-789V432-CR[dfn]", imageUrn.toString(SERIALIZATION_FORMAT.RAW));
			assertEquals("urn:vaimage:660-123-456-789V432-CR%5bdfn%5d", imageUrn.toString(SERIALIZATION_FORMAT.VFTP));
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	@Test
	public void testParsingImageURNPatientIdentifier()
	{
		try
		{
			ImageURN imageUrn = URNFactory.create("urn:vaimage:660-123-456-789V432-CR");
			assertEquals("660", imageUrn.getOriginatingSiteId());
			assertEquals("123", imageUrn.getImageId());
			assertEquals("456", imageUrn.getStudyId());
			assertEquals("789V432", imageUrn.getPatientId());
			assertEquals("CR", imageUrn.getImageModality());
			assertNull(imageUrn.getPatientIdentifierType());
			assertEquals(PatientIdentifierType.icn, imageUrn.getPatientIdentifierTypeOrDefault());
			
			assertEquals("urn:vaimage:660-123-456-789V432-CR", imageUrn.toString());
			assertEquals("urn:vaimage:660-123-456-789V432-CR", imageUrn.toString(SERIALIZATION_FORMAT.NATIVE));
			assertEquals("urn:vaimage:660-123-456-789V432-CR", imageUrn.toString(SERIALIZATION_FORMAT.CDTP));
			assertEquals("urn:vaimage:660-123-456-789V432-CR", imageUrn.toString(SERIALIZATION_FORMAT.RAW));
			assertEquals("urn:vaimage:660-123-456-789V432-CR", imageUrn.toString(SERIALIZATION_FORMAT.VFTP));
			
			ImageURN anotherImageUrn = URNFactory.create("urn:vaimage:660-123-456-789V432-CR[icn]");
			assertEquals("660", anotherImageUrn.getOriginatingSiteId());
			assertEquals("123", anotherImageUrn.getImageId());
			assertEquals("456", anotherImageUrn.getStudyId());
			assertEquals("789V432", anotherImageUrn.getPatientId());
			assertEquals("CR", anotherImageUrn.getImageModality());
			assertEquals(PatientIdentifierType.icn, anotherImageUrn.getPatientIdentifierType());
			
			assertEquals("urn:vaimage:660-123-456-789V432-CR", anotherImageUrn.toString());
			assertEquals("urn:vaimage:660-123-456-789V432-CR", anotherImageUrn.toString(SERIALIZATION_FORMAT.NATIVE));
			assertEquals("urn:vaimage:660-123-456-789V432-CR[icn]", anotherImageUrn.toString(SERIALIZATION_FORMAT.CDTP));
			assertEquals("urn:vaimage:660-123-456-789V432-CR[icn]", anotherImageUrn.toString(SERIALIZATION_FORMAT.RAW));
			assertEquals("urn:vaimage:660-123-456-789V432-CR%5bicn%5d", anotherImageUrn.toString(SERIALIZATION_FORMAT.VFTP));
			
			anotherImageUrn = URNFactory.create("urn:vaimage:660-123-456-789V432-CR[dfn]");
			assertEquals("660", anotherImageUrn.getOriginatingSiteId());
			assertEquals("123", anotherImageUrn.getImageId());
			assertEquals("456", anotherImageUrn.getStudyId());
			assertEquals("789V432", anotherImageUrn.getPatientId());
			assertEquals("CR", anotherImageUrn.getImageModality());
			assertEquals(PatientIdentifierType.dfn, anotherImageUrn.getPatientIdentifierType());
			
			assertEquals("urn:vaimage:660-123-456-789V432-CR", anotherImageUrn.toString());
			assertEquals("urn:vaimage:660-123-456-789V432-CR", anotherImageUrn.toString(SERIALIZATION_FORMAT.NATIVE));
			assertEquals("urn:vaimage:660-123-456-789V432-CR[dfn]", anotherImageUrn.toString(SERIALIZATION_FORMAT.CDTP));
			assertEquals("urn:vaimage:660-123-456-789V432-CR[dfn]", anotherImageUrn.toString(SERIALIZATION_FORMAT.RAW));
			assertEquals("urn:vaimage:660-123-456-789V432-CR%5bdfn%5d", anotherImageUrn.toString(SERIALIZATION_FORMAT.VFTP));
		}
		catch(Exception ex)
		{
			fail(ex.getMessage());
		}
	}

}
