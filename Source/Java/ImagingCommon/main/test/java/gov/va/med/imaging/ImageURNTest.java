package gov.va.med.imaging;

import java.io.*;
import java.util.regex.Matcher;
import gov.va.med.*;
import gov.va.med.GlobalArtifactIdentifierFactory.ConfigurationException;
import gov.va.med.exceptions.GlobalArtifactIdentifierFormatException;
import gov.va.med.imaging.exceptions.URNFormatException;
import junit.framework.TestCase;

public class ImageURNTest 
extends TestCase
{
	public void testRegularExpressions()
	{
		Matcher matcher;

		String[] invalidNss = new String[]
		{
			// too many dash delimited tokens, max is 5
			"200-rp02_0108_rp01-aaaf3c83-ce12-4390-904d-447e3d4f4811",
			// 
			"200-rp02_0108_rp01%2Daaaf3c83%2Dce12%2D4390%2D904d%2D447e3d4f4811"
		};
		
		for(String nss : invalidNss)
		{
			matcher = ImageURN.getNamespaceSpecificStringMatcher(nss);
			System.out.println( "NSS pattern is " + matcher.pattern().toString() );
			assertFalse("NSS '" + nss +"' should not be valid and is.", matcher.matches());
		}
		
		String[] validNss = new String[]
 		{
 			"200-rp02_0108_rp01-aaaf3c83-43-rrt",
 			"200-rp02_0108_rp01%2Daaaf3c83-45-42",
 			"200-rp02_0108_rp01-45-69",
 			"200-rp02_0108_rp01-99-54"
 		};
 		
 		for(String nss : validNss)
 		{
 			matcher = ImageURN.getNamespaceSpecificStringMatcher(nss);
 			System.out.println( "NSS pattern is " + matcher.pattern().toString() );
 			assertTrue("NSS '" + nss +"' should be valid and is not.", matcher.matches());
 		}
	}
	
	public void testDocumentUniqueIdBuildAndParse() 
	throws GlobalArtifactIdentifierFormatException
	{
		String instanceId = "instanceId";
		String studyId = "studyId";
		String patientId = "patientId";
		
		String documentUniqueId = ImageURN.buildDocumentUniqueId(instanceId, studyId, patientId);
		String[] components = ImageURN.parseDocumentUniqueId(documentUniqueId);
		
		assertEquals(instanceId, components[0]);
		assertEquals(studyId, components[1]);
		assertEquals(patientId, components[2]);
	}
	
	public void testImageURNBuildingNegatives() 
	throws URNFormatException
	{
		ImageURN imageUrn1 = null;
		ImageURN imageUrn2 = null;

		imageUrn1 = ImageURN.create("A","A","A","A");
		imageUrn2 = URNFactory.create("urn:vaimage:A-A-A-A-");
		System.out.println("imageUrn1 = '" + imageUrn1.toString() + "', imageUrn2 is '" + imageUrn2.toString() + "'");
		assertEquals(imageUrn1, imageUrn2);
		
		imageUrn1 = ImageURN.create("A","A","A","A");
		imageUrn2 = URNFactory.create("urn:vaimage:A-A-A-A");
		System.out.println("imageUrn1 = '" + imageUrn1.toString() + "', imageUrn2 is '" + imageUrn2.toString() + "'");
		assertEquals(imageUrn1, imageUrn2);
		
		imageUrn1 = ImageURN.create("ABFFG", "1","871","VVBB");
		imageUrn2 = URNFactory.create("urn:vaimage:ABFFG-1-871-VVBB-");
		System.out.println("imageUrn1 = '" + imageUrn1.toString() + "', imageUrn2 is '" + imageUrn2.toString() + "'");
		assertEquals(imageUrn1, imageUrn2);
		
		imageUrn1 = ImageURN.create("ABFFG", "1","871","VVBB");
		imageUrn2 = URNFactory.create("urn:vaimage:ABFFG-1-871-VVBB");
		System.out.println("imageUrn1 = '" + imageUrn1.toString() + "', imageUrn2 is '" + imageUrn2.toString() + "'");
		assertEquals(imageUrn1, imageUrn2);
		
		imageUrn1 = ImageURN.create("ABFFG", "1","871","VVBB","MMDD");
		imageUrn2 = URNFactory.create("urn:vaimage:ABFFG-1-871-VVBB-MMDD");
		System.out.println("imageUrn1 = '" + imageUrn1.toString() + "', imageUrn2 is '" + imageUrn2.toString() + "'");
		assertEquals(imageUrn1, imageUrn2);

		imageUrn1 = ImageURN.create("A","A","A","A");
		imageUrn2 = URNFactory.create("urn:vaimage:A-A-A-A-");
		System.out.println("imageUrn1 = '" + imageUrn1.toString() + "', imageUrn2 is '" + imageUrn2.toString() + "'");
		assertEquals(imageUrn1, imageUrn2);
		
		imageUrn1 = ImageURN.create("A", "A","A","A","A");
		imageUrn2 = URNFactory.create("urn:vaimage:A-A-A-A-A");
		System.out.println("imageUrn1 = '" + imageUrn1.toString() + "', imageUrn2 is '" + imageUrn2.toString() + ";");
		assertEquals(imageUrn1, imageUrn2);

		imageUrn1 = ImageURN.create("00000", "11111","222222","333333");
		imageUrn2 = URNFactory.create("urn:vaimage:00000-11111-222222-333333-");
		System.out.println("imageUrn1 = '" + imageUrn1.toString() + "', imageUrn2 is '" + imageUrn2.toString() + "'");
		assertEquals(imageUrn1, imageUrn2);
		
		imageUrn1 = ImageURN.create("00000", "11111","222222","333333","444444");
		imageUrn2 = URNFactory.create("urn:vaimage:00000-11111-222222-333333-444444");
		System.out.println("imageUrn1 = [" + imageUrn1.toString() + "], imageUrn2 is [" + imageUrn2.toString() + "]");
		assertEquals(imageUrn1, imageUrn2);

		imageUrn1 = ImageURN.create("000000000000000111111111111111999999999999999", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","222222222222222222222222223333333333333333","444444444444444444444444555555555555555555555");
		imageUrn2 = URNFactory.create("urn:vaimage:000000000000000111111111111111999999999999999-AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA-222222222222222222222222223333333333333333-444444444444444444444444555555555555555555555-");
		System.out.println("imageUrn1 = [" + imageUrn1.toString() + "], imageUrn2 is [" + imageUrn2.toString() + "]");
		assertEquals(imageUrn1, imageUrn2);

		imageUrn1 = ImageURN.create("000000000000000111111111111111999999999999999", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","222222222222222222222222223333333333333333","444444444444444444444444555555555555555555555","6666666666666666666666677777777777777777");
		imageUrn2 = URNFactory.create("urn:vaimage:000000000000000111111111111111999999999999999-AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA-222222222222222222222222223333333333333333-444444444444444444444444555555555555555555555-6666666666666666666666677777777777777777");
		System.out.println("imageUrn1 = [" + imageUrn1.toString() + "], imageUrn2 is [" + imageUrn2.toString() + "]");
		assertEquals(imageUrn1, imageUrn2);
		
		imageUrn1 = ImageURN.create("vaguid", "655321","739172","09876");
		imageUrn2 = URNFactory.create("urn:vaimage:vaguid-655321-739172-09876-");
		System.out.println("imageUrn1 = [" + imageUrn1.toString() + "], imageUrn2 is [" + imageUrn2.toString() + "]");
		assertEquals(imageUrn1, imageUrn2);
		
		imageUrn1 = ImageURN.create("vaguid", "655321","739172","09876","465789");
		imageUrn2 = URNFactory.create("urn:vaimage:vaguid-655321-739172-09876-465789");
		System.out.println("imageUrn1 = [" + imageUrn1.toString() + "], imageUrn2 is [" + imageUrn2.toString() + "]");
		assertEquals(imageUrn1, imageUrn2);
		
		imageUrn1 = ImageURN.create("000000","111111","222222","333333");
		imageUrn2 = URNFactory.create("urn:vaimage:000000-111111-222222-333333-");
		System.out.println("imageUrn1 = [" + imageUrn1.toString() + "], imageUrn2 is [" + imageUrn2.toString() + "]");
		assertEquals(imageUrn1, imageUrn2);
		
		String urnString = "urn:vaimage:AAAAAA-BBBBBB-CCCCCC-DDDDDD-";
		imageUrn1 = URNFactory.create(urnString);
		imageUrn2 = ImageURN.create("AAAAAA", "BBBBBB", "CCCCCC", "DDDDDD");
		System.out.println("urn String [" + urnString + "], URN representation [" + imageUrn1.toString() + "]");
		assertNotSame(urnString, imageUrn1.toString());
		assertEquals(imageUrn1, imageUrn2);
		
	}
	
	public void testImageURNBuildingPositives() 
	throws URNFormatException
	{
		ImageURN imageUrn1 = null;
		ImageURN imageUrn2 = null;
		
		imageUrn1 = ImageURN.create("655321", "111DFEA","48173","81756");
		URN urn2 = URNFactory.create("urn:vaimage:655321-111DFEA-48173-81756-");
		assertTrue(urn2 instanceof ImageURN);
		imageUrn2 = (ImageURN)urn2;
		
		System.out.println("imageUrn1 = [" + imageUrn1.toString() + "], imageUrn2 is [" + imageUrn2.toString() + "]");
		assertEquals(imageUrn1, imageUrn2);

		try
		{
			imageUrn1 = ImageURN.create("655321", "111-DFEA","93810","ASDFIG");
			System.out.println("imageUrn1 = '" + (imageUrn1 == null ? "null" : imageUrn1.toString()) + "'");
			fail();
		}
		catch(URNFormatException iufX)
		{}
		try
		{
			imageUrn1 = ImageURN.create("655-321", "111DFEA","93810","ASDFIG");
			System.out.println("imageUrn1 = '" + (imageUrn1 == null ? "null" : imageUrn1.toString()) + "'");
			fail();
		}
		catch(URNFormatException iufX)
		{}
		try
		{
			imageUrn1 = ImageURN.create("655321", "111DFEA","93810","ASD-FIG");
			System.out.println("imageUrn1 = '" + (imageUrn1 == null ? "null" : imageUrn1.toString()) + "'");
			fail();
		}
		catch(URNFormatException iufX)
		{}
		try 
		{
			imageUrn1 = ImageURN.create("655321", "111DFEA","93810","ASDFIG","456-789");
			System.out.println("imageUrn1 = '" + (imageUrn1 == null ? "null" : imageUrn1.toString()) + "'");
			fail();
		}
		catch(URNFormatException iufX) 
		{}
		
		String urnAsString = 
			"urn:bhieimage:rp02_0108_rp01-b84c243d-68ab-42ae-a125-7029777ea227[rp02_0108_rg01-67d2445e-4997-423b-9efb-3642e276c153][1008861107V475740]"; 
		try 
		{
			imageUrn1 = URNFactory.create(urnAsString);
			assertTrue(imageUrn1 instanceof ImageURN);
		}
		catch(URNFormatException iufX) 
		{
			fail("URN '" + urnAsString + "' should be a valid URN and is not.");
		}
	}
	
	/**
	 * 
	 * @throws URNFormatException
	 * @throws ConfigurationException
	 * @throws GlobalArtifactIdentifierFormatException 
	 */
	public void testGlobalArtifactIdentifier() 
	throws URNFormatException, GlobalArtifactIdentifierFormatException
	{
		GlobalArtifactIdentifier gaiOriginal = null;
		
		gaiOriginal = ImageURN.create("655321", "111DFEA","48173","81756");
		
		GlobalArtifactIdentifier gaiClone = GlobalArtifactIdentifierFactory.create(
			gaiOriginal.getHomeCommunityId(), gaiOriginal.getRepositoryUniqueId(), gaiOriginal.getDocumentUniqueId()
		);

		System.out.println(
			"Original type is '" + gaiOriginal.getClass().getSimpleName() + "', cloned type is '" +
			gaiClone.getClass().getSimpleName() + "'." + 
			"Instances " + (gaiOriginal.equals(gaiClone) ? "ARE" : "ARE NOT") + " equal.");

		assertEquals(
			"Original type is '" + gaiOriginal.getClass().getSimpleName() + "', cloned type is '" +
			gaiClone.getClass().getSimpleName() + "'.", 
			gaiOriginal, gaiClone
		);
	}
	
	private String[] validUrns = new String[]
	{
		"urn:vaimage:A-A-A-A",
		"urn:vaimage:655321-111DFEA-48173-81756",
	};
	
	public void testSerialization() 
	throws URNFormatException, IOException, ClassNotFoundException
	{
		for(String validUrn : validUrns)
			serializeCloneAndCompare(validUrn);
	}

	private void serializeCloneAndCompare(String urn) 
	throws URNFormatException, IOException, ClassNotFoundException
	{
		ImageURN original = null;
		ImageURN clone = null;

		original = URNFactory.create(urn);
		ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(outBytes);
		out.writeObject(original);
		out.close();
		
		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(outBytes.toByteArray()));
		clone = (ImageURN)in.readObject();
		
		assertEquals(original, clone);
	}
	
	/**
	 * @throws URNFormatException 
	 * 
	 */
	public void testToStringVariations() 
	throws URNFormatException
	{
		ImageURN urn;
		String ts;
		String tsInternal;
		String tsNative;
		
		urn = URNFactory.create("urn:vaimage:nss?42*19-a-b-c");
		ts = urn.toString();
		tsInternal = urn.toString(SERIALIZATION_FORMAT.CDTP);
		tsNative = urn.toString(SERIALIZATION_FORMAT.NATIVE);
		System.out.println("RFC2121 = " + ts);
		System.out.println("Internal = " + tsInternal);
		System.out.println("Native = " + tsNative);
		assertEquals("urn:vaimage:nss%3f42*19-a-b-c", ts);
		assertEquals("urn:vaimage:nss%3f42%2a19-a-b-c", tsInternal);
		assertEquals("urn:vaimage:nss?42*19-a-b-c", tsNative);

		try{urn = URNFactory.create("urn:vaimage:nss?42*19-a-b-c[id1][id2]");}
		catch(URNFormatException urnfX){}

		urn = URNFactory.create("urn:vaimage:nss?42*19-a-b-c");
		ts = urn.toString();
		tsInternal = urn.toString(SERIALIZATION_FORMAT.CDTP);
		tsNative = urn.toString(SERIALIZATION_FORMAT.NATIVE);
		System.out.println("RFC2121 = " + ts);
		System.out.println("Internal = " + tsInternal);
		System.out.println("Native = " + tsNative);
		assertEquals("urn:vaimage:nss%3f42*19-a-b-c", ts);
		assertEquals("urn:vaimage:nss%3f42%2a19-a-b-c", tsInternal);
		assertEquals("urn:vaimage:nss?42*19-a-b-c", tsNative);

		try{urn = URNFactory.create("urn:vaimage:nss?42*19-a-b-c[id1][id2]");}
		catch(URNFormatException urnfX){}
		
		urn = ImageURNFactory.create("660", "1234", "1011^6949598.9048^1^191", "12345", null, ImageURN.class);
		assertEquals("1011^6949598.9048^1^191", urn.getStudyId());
		assertEquals("660", urn.getOriginatingSiteId());
		assertEquals("1234", urn.getImageId());
		assertEquals("12345", urn.getPatientId());
		assertEquals(null, urn.getImageModality());
	}
	
	public void testBase32Encoding() 
	throws URNFormatException
	{
		ImageURN original = null;
		ImageURN clone = null;

		original = ImageURN.create("A","A","A","A");
		String urnAsString = original.toString(SERIALIZATION_FORMAT.PATCH83_VFTP);
		clone = URNFactory.create(urnAsString, SERIALIZATION_FORMAT.PATCH83_VFTP);
		
		assertEquals(original, clone);
	}
	
	public void testImageUrnComparison()
	throws URNFormatException
	{
		ImageURN imageUrn = 
			ImageURNFactory.create("660", 
					//Base32ConversionUtility.base32Decode("HA2TMMA5"), 
					"12334", // this id is not in the exam
					"1011^6949598.9048^1^191",
					"1006170647V052871", 
					null, ImageURN.class);
		ImageURN imageUrn2 = 
			ImageURNFactory.create("660", 
					//Base32ConversionUtility.base32Decode("HA2TMMA5"), 
					"12334", // this id is not in the exam
					"1011^6949598.9048^1^191",
					"1006170647V052871", 
					null, ImageURN.class);
		assertEquals(imageUrn, imageUrn2);
		
		assertTrue(imageUrn.equals(imageUrn2));
		
		ImageURN differentUrn = 
			ImageURNFactory.create("660", 
					//Base32ConversionUtility.base32Decode("HA2TMMA5"), 
					"abcde", // this id is not in the exam
					"1011^6949598.9048^1^191",
					"1006170647V052871", 
					null, ImageURN.class);
		assertNotSame(imageUrn, differentUrn);
		assertFalse("URN: '" + imageUrn.toString() + "' is not equal to '" + differentUrn.toString() + "'.",  
				imageUrn.equals(differentUrn));
	}
	
	public void testPatch83Compliance() 
	throws URNFormatException
	{
		ImageURN imageUrn = URNFactory.create(
				"urn:vaimage:660-GM2TEOA-GEYDCMK6GY4TOOJXHEZS4OBWG42V4MK6HA3A-1006170647V052871", 
				SERIALIZATION_FORMAT.PATCH83_VFTP
			);
        System.out.println("RFC2121: " + imageUrn.toString());
        
        //Here I am taking the resulting URN and creating the same URN               
        ImageURN newUrn = ImageURNFactory.create(
        	imageUrn.getOriginatingSiteId(), 
        	imageUrn.getImageId(), 
        	imageUrn.getStudyId(), 
        	imageUrn.getPatientId(), 
        	imageUrn.getImageModality(), 
        	ImageURN.class);
        System.out.println("RFC2121: " + newUrn.toString());

		assertEquals(newUrn, imageUrn);
		assertEquals(newUrn.toString(), imageUrn.toString());
	}
	
	/**
	 * The modality should not be included in the equals() or hashCode() calculation
	 */
	public void testModalityEquality()
	throws URNFormatException
	{
		ImageURN urn = URNFactory.create("urn:vaimage:660-rp02_0108_rp01-99-54");
		ImageURN urnWithModality = URNFactory.create("urn:vaimage:660-rp02_0108_rp01-99-54-CT");
		
		assertEquals(urn, urnWithModality);
		
		ImageURN imageUrn = ImageURNFactory.create("660", "123", "456", "pat123", "CT", ImageURN.class);
		ImageURN newUrn = ImageURNFactory.create("660", "123", "456", "pat123", "CT", ImageURN.class);
		assertEquals(imageUrn, newUrn);
		assertTrue(imageUrn.equals(newUrn));
		newUrn.setImageModality("MR");
		// despite different modality values, these two URNs should still be the same
		assertEquals(imageUrn, newUrn);
		assertTrue(imageUrn.equals(newUrn));
		newUrn.setImageModality(null);
		assertEquals(imageUrn, newUrn);
		assertTrue(imageUrn.equals(newUrn));
		newUrn.setPatientId("pat456");
		// just to be sure
		assertFalse(imageUrn.equals(newUrn));
		
		newUrn = ImageURNFactory.create("660", "123", "456", "pat123", null, ImageURN.class);
		assertEquals(imageUrn, newUrn);
		assertTrue(imageUrn.equals(newUrn));
		newUrn = ImageURNFactory.create("660", "123", "456", "pat123", "DX", ImageURN.class);
		assertEquals(imageUrn, newUrn);
		assertTrue(imageUrn.equals(newUrn));
		imageUrn = ImageURNFactory.create("660", "123", "456", "pat123", null, ImageURN.class);
		assertEquals(imageUrn, newUrn);
		assertTrue(imageUrn.equals(newUrn));
	}
}
