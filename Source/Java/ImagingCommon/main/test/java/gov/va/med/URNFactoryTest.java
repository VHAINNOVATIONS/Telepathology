/**
 * 
 */
package gov.va.med;

import gov.va.med.imaging.*;
import gov.va.med.imaging.exceptions.URNFormatException;
import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class URNFactoryTest
	extends TestCase
{

	/**
	 * Test method for {@link gov.va.med.URNFactory#parse(java.lang.String)}.
	 */
	public void testParse()
	{
		try
		{
			URNComponents urnComponents = URNComponents.parse("urn:nid:1234");
			assertEquals("urn", urnComponents.getSchema());
			assertEquals("nid", urnComponents.getNamespaceIdentifier().getNamespace());
			assertEquals(null, urnComponents.getAdditionalIdentifers() );
			
			urnComponents = URNComponents.parse("urn:nid:1234[a]");
			assertEquals("urn", urnComponents.getSchema());
			assertEquals("nid", urnComponents.getNamespaceIdentifier().getNamespace());
			assertNotNull( urnComponents.getAdditionalIdentifers() );
			assertEquals(1, urnComponents.getAdditionalIdentifers().length);
			assertEquals("a", urnComponents.getAdditionalIdentifers()[0]);
			
			
			urnComponents = URNComponents.parse("urn:nid:1234[a][bhd-765]");
			assertEquals("urn", urnComponents.getSchema());
			assertEquals("nid", urnComponents.getNamespaceIdentifier().getNamespace());
			assertNotNull( urnComponents.getAdditionalIdentifers() );
			assertEquals(2, urnComponents.getAdditionalIdentifers().length);
			assertEquals("a", urnComponents.getAdditionalIdentifers()[0]);
			assertEquals("bhd-765", urnComponents.getAdditionalIdentifers()[1]);
		}
		catch (URNFormatException x)
		{
			fail(x.getMessage());
		}
	}

	public void testVASpecificURNs() 
	throws URNFormatException
	{
		String sitenumber = "660";
		String imageIen = "39";
		String studyIen = "39";
		String patientIcn = "1008861107V475740";
		String modality = "CR";

		StudyURN studyUrn = StudyURNFactory.create(sitenumber, studyIen, patientIcn, StudyURN.class);
		assertNotNull(studyUrn);
		assertEquals( studyIen,  studyUrn.getStudyId() );
		assertEquals( patientIcn,  studyUrn.getPatientId() );
		assertEquals( sitenumber,  studyUrn.getOriginatingSiteId() );
		
		ImageURN imageUrn = ImageURNFactory.create(sitenumber, imageIen, studyIen, patientIcn, modality, ImageURN.class);
		assertNotNull(imageUrn);
		assertEquals( imageIen,  imageUrn.getImageId() );
		assertEquals( studyIen,  imageUrn.getStudyId() );
		assertEquals( patientIcn,  imageUrn.getPatientId() );
		assertEquals( sitenumber,  imageUrn.getOriginatingSiteId() );
		
		ImageURN imageUrn2 = ImageURNFactory.create(sitenumber, imageIen, studyIen, patientIcn, null, ImageURN.class);
		assertNotNull(imageUrn2);
		assertEquals( imageIen,  imageUrn2.getImageId() );
		assertEquals( studyIen,  imageUrn2.getStudyId() );
		assertEquals( patientIcn,  imageUrn2.getPatientId() );
		assertEquals( sitenumber,  imageUrn2.getOriginatingSiteId() );
	}
	
	public void testValidBhieImageUrns() 
	throws URNFormatException
	{
		String[] validImageUrns = new String[]
		{
			"urn:bhieimage:rp02_0108_rp01-aaaf3c83-ce12-4390-904d-447e3d4f4811",
			"urn:bhieimage:rp02_0108_rp01-aaaf3c83-ce12-4390-904d-447e3d4f4811[1008861107V475740][39]"
		};
		
		for(String validUrn : validImageUrns)
		{
			BhieImageURN imageUrn = URNFactory.create(validUrn, BhieImageURN.class);
			assertNotNull(imageUrn);
			assertNotNull(imageUrn.getOriginatingSiteId());
			assertEquals("200", imageUrn.getOriginatingSiteId());
			assertNotNull(imageUrn.getNamespaceSpecificString());
			assertNotNull(imageUrn.getImageId());
			
			String vaInternalForm = imageUrn.toString(SERIALIZATION_FORMAT.CDTP);
			BhieImageURN internalVaForm = URNFactory.create(vaInternalForm, SERIALIZATION_FORMAT.CDTP, BhieImageURN.class);
			
			assertEquals(imageUrn, internalVaForm);
			
			//StudyURN.create(imageUrn.getOriginatingSiteId(), imageUrn.getStudyId(), imageUrn.getPatientIcn());
		}
	}
	
	public void testValidBhieImageUrnsWithAdditionalIdentifiers() 
	throws URNFormatException
	{
		String[] validImageUrns = new String[]
		{
			BhieImageURN.create("rp02_0108_rp01-aaaf3c83-ce12-4390-904d-447e3d4f4811", "1008861107V475740", "39").toStringCDTP()
		};
		
		for(String validUrn : validImageUrns)
		{
			BhieImageURN imageUrn = URNFactory.create(validUrn, BhieImageURN.class);
			assertNotNull(imageUrn);
			assertNotNull(imageUrn.getOriginatingSiteId());
			assertEquals("200", imageUrn.getOriginatingSiteId());
			assertNotNull(imageUrn.getNamespaceSpecificString());
			assertNotNull(imageUrn.getImageId());
			
			//StudyURN.create(imageUrn.getOriginatingSiteId(), imageUrn.getStudyId(), imageUrn.getPatientIcn());
		}
	}
	
	public void testValidImageUrns() 
	throws URNFormatException
	{
		String[] validUrns = new String[]
 		{
 			"urn:vaimage:660-8804-8799-1008861107V475740-CR",
 			"urn:vaimage:660-8804-8799-1008861107V475740"
 		};

		testValidUrns(validUrns, ImageURN.class, null);
	}
	
	public void testValidStudyUrns() 
	throws URNFormatException
	{
		String[] validUrns = new String[]
 		{
 			"urn:vastudy:660-8833-1008861107V475740"
 		};
		
 		testValidUrns(validUrns, StudyURN.class, null);
	}
	
	public void testValidDocumentSetUrns() 
	throws URNFormatException
	{
		String[] validUrns = new String[]
 		{
 			"urn:vadocset:655321-111DFEA-44456"
 		};
 		
 		testValidUrns(validUrns, DocumentSetURN.class, null);
	}
	
	public void testRawGAIDEncoding() 
	throws URNFormatException
	{
		URN urn = URNFactory.create(
			"urn:gaid:2.16.840.1.113883.3.42.10012.100001.206-central-h01afb8984dcbe3413cbb9c7943efd9a96e0114", 
			SERIALIZATION_FORMAT.RAW
		);
		System.out.println(urn.toString());
		assertTrue("null found in serialized format", urn.toString().indexOf("null") < 0);

		// urn:gaid:2.16.840.1.113883.3.42.10012.100001.206-central-null 
		
	}
	
	public void testValidDocumentUrns() 
	throws URNFormatException
	{
		String[] validUrns = new String[]
 		{
 			"urn:vadoc:A-B-C-D",
 			"urn:vadoc:ABFFG-1-871-VVBB",
 			"urn:vadoc:000000000000000111111111111111999999999999999-AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA-222222222222222222222222223333333333333333-444444444444444444444444555555555555555555555"
 		};
 		
 		testValidUrns(validUrns, DocumentURN.class, null);
	}
	
	/**
	 * Generic tester of valid URNs.  Simply checks that toString returns the original
	 * stringified URN.
	 * 
	 * @param <T>
	 * @param validUrns
	 * @param expectedType
	 * @throws URNFormatException
	 */
	private <T extends URN> void testValidUrns(String[] validUrns, Class<T> expectedType, UrnValidator validator) 
	throws URNFormatException
	{
		for(String validUrn : validUrns)
 		{
 			T urn = URNFactory.create(validUrn, expectedType);
 			assertNotNull(urn);
 			assertNotNull(urn.getNamespaceIdentifier());
 			assertNotNull(urn.getNamespaceSpecificString());

 			assertEquals("URN toString() does not return initial value.", validUrn, urn.toString());
 			
 			if(validator != null)
 				validator.validateUrn(urn);
 		}
	}
	
	interface UrnValidator
	{
		public void validateUrn(URN urn) throws URNFormatException;
	}
	
}
