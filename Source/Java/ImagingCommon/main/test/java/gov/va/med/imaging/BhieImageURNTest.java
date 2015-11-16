package gov.va.med.imaging;

import java.util.regex.Matcher;
import gov.va.med.*;
import gov.va.med.GlobalArtifactIdentifierFactory.ConfigurationException;
import gov.va.med.exceptions.GlobalArtifactIdentifierFormatException;
import gov.va.med.imaging.exceptions.URNFormatException;
import junit.framework.TestCase;

public class BhieImageURNTest 
extends TestCase
{
	private String[] VALID_URN_EXAMPLES = new String[]
  	{
		"urn:bhieimage:4219-a-b-c"
  	};
	
	//public void testHaimsUrns() 
	//throws URNFormatException
	//{
	//	BhieImageURN urn = 
	//		URNFactory.create("urn:bhieimage:haims1_haims1_cb4d8fc601184948b06bd67331a19a5c@d024cb23-95e1-40ec-b495-a75c2aee54ff:1");
	//}
	
	public void testEqualityTest()
	throws URNFormatException
	{
		BhieImageURN urn1 = null;
		BhieImageURN urn2 = null;

		urn1 = BhieImageURN.create("A", "42", "655321");
		urn2 = BhieImageURN.create("A", "42", "655321");
		assertEquals("urn1 type is '" + urn1.getClass().getSimpleName() + "', urn2 type is '" + urn2.getClass().getSimpleName() + "'.", urn1, urn2);

		assertEquals(urn1.toString(), "42", urn1.getGroupId());
		assertEquals(urn1.toString(), "655321", urn1.getPatientId());
	}

	/**
	 * 
	 * @throws URNFormatException
	 */
	public void testBhieImageURNBuildingPositives() 
	throws URNFormatException
	{
		BhieImageURN bhieImageUrn1 = null;
		
		try
		{
			bhieImageUrn1 = BhieImageURN.create((String)null, (String)null, (String)null);
			fail("BhieImageURN1 = '" + (bhieImageUrn1 == null ? "null" : bhieImageUrn1.toString()) + "' should have failed to construct");
		}
		catch(URNFormatException iufX)
		{}
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
		
		gaiOriginal = BhieImageURN.create("A", "655321", "42");
		
		GlobalArtifactIdentifier gaiClone;
		try
		{
			gaiClone = gaiOriginal.clone();
			
			assertNotNull(gaiClone);
			
			assertEquals(
				"Original type is '" + gaiOriginal.getClass().getSimpleName() + "', cloned type is '" +
				gaiClone.getClass().getSimpleName() + "'.", 
				gaiOriginal, gaiClone
			);
			
			//GlobalArtifactIdentifier gaiClone = GlobalArtifactIdentifierFactory.create(
			//	gaiOriginal.getHomeCommunityId(), gaiOriginal.getRepositoryUniqueId(), gaiOriginal.getDocumentUniqueId(),
			//	BhieImageURN.class
			//);
		}
		catch (CloneNotSupportedException x)
		{
			fail(x.getMessage());
		}
	}
	
	public void testToStringVariations() 
	throws URNFormatException
	{
		BhieImageURN urn;
		BhieImageURN cloneUrn;
		String ts;
		String tsInternal;
		String tsNative;
		
		urn = URNFactory.create("urn:bhieimage:4219-a-b-c");
		ts = urn.toString();
		tsInternal = urn.toStringCDTP();
		tsNative = urn.toStringNative();
		System.out.println("RFC2141 = " + ts);
		System.out.println("Internal = " + tsInternal);
		System.out.println("Native = " + tsNative);
		assertEquals("urn:bhieimage:4219-a-b-c", ts);
		assertEquals("urn:vaimage:200-4219%2da%2db%2dc-", tsInternal);
		assertEquals("urn:bhieimage:4219-a-b-c", tsNative);
		cloneUrn = URNFactory.create(ts, SERIALIZATION_FORMAT.RFC2141);
		assertEquals(urn, cloneUrn);
		cloneUrn = URNFactory.create(tsInternal, SERIALIZATION_FORMAT.CDTP);
		assertEquals(urn, cloneUrn);
		cloneUrn = URNFactory.create(tsNative, SERIALIZATION_FORMAT.NATIVE);
		assertEquals(urn, cloneUrn);

		urn = URNFactory.create("urn:bhieimage:nss?42*19-a-b-c");
		ts = urn.toString();
		tsInternal = urn.toStringCDTP();
		tsNative = urn.toStringNative();
		System.out.println("RFC2141 = " + ts);
		System.out.println("Internal = " + tsInternal);
		System.out.println("Native = " + tsNative);
		assertEquals("urn:bhieimage:nss%3f42*19-a-b-c", ts);
		assertEquals("urn:vaimage:200-nss%3f42%2a19%2da%2db%2dc-", tsInternal);
		assertEquals("urn:bhieimage:nss?42*19-a-b-c", tsNative);
		cloneUrn = URNFactory.create(ts, SERIALIZATION_FORMAT.RFC2141);
		assertEquals(urn, cloneUrn);
		cloneUrn = URNFactory.create(tsInternal, SERIALIZATION_FORMAT.CDTP);
		assertEquals(urn, cloneUrn);
		cloneUrn = URNFactory.create(tsNative, SERIALIZATION_FORMAT.NATIVE);
		assertEquals(urn, cloneUrn);

		urn = URNFactory.create("urn:bhieimage:nss?42*19-a-b-c[id1][id2]");
		ts = urn.toString();
		tsInternal = urn.toString(SERIALIZATION_FORMAT.CDTP);
		tsNative = urn.toString(SERIALIZATION_FORMAT.NATIVE);
		System.out.println("RFC2141 = " + ts);
		System.out.println("Internal = " + tsInternal);
		System.out.println("Native = " + tsNative);
		assertEquals("urn:bhieimage:nss%3f42*19-a-b-c", ts);
		assertEquals("urn:vaimage:200-nss%3f42%2a19%2da%2db%2dc-[id1][id2]", tsInternal);
		assertEquals("urn:bhieimage:nss?42*19-a-b-c", tsNative);
		cloneUrn = URNFactory.create(tsInternal, SERIALIZATION_FORMAT.CDTP);
		assertEquals(urn, cloneUrn);
	}
	
	public void testStringification() 
	throws URNFormatException
	{
		BhieImageURN bhieImageUrn = URNFactory.create(
			"urn:bhieimage:rp02_0108_rp01-b84c243d-68ab-42ae-a125-7029777ea227",
			SERIALIZATION_FORMAT.NATIVE, 
			BhieImageURN.class);
		bhieImageUrn.setPatientId("1008861107V475740");
		bhieImageUrn.setGroupId("rp02_0108_rg01-67d2445e-4997-423b-9efb-3642e276c153");
		String stringified = bhieImageUrn.toString();
		String stringifiedAsVAInternal = bhieImageUrn.toString(SERIALIZATION_FORMAT.CDTP);
		String stringifiedAsBase32 = bhieImageUrn.toString(SERIALIZATION_FORMAT.PATCH83_VFTP);
		String stringifieidAsNative = bhieImageUrn.toString(SERIALIZATION_FORMAT.NATIVE);
		
		System.out.println("bhieImageUrn.toString(): " + stringified);
		System.out.println("bhieImageUrn.toStringAsVAInternal(): " + stringifiedAsVAInternal);
		System.out.println("bhieImageUrn.toStringAsBase32(): " + stringifiedAsBase32);
		System.out.println("base32CreatedImageUrn.toStringAsNative(): " + stringifieidAsNative);

		URN base32CreatedUrn = URNFactory.create(stringifiedAsBase32, SERIALIZATION_FORMAT.PATCH83_VFTP);
		assertTrue(base32CreatedUrn instanceof BhieImageURN);
		BhieImageURN base32BhieImageUrn = (BhieImageURN)base32CreatedUrn;
		System.out.println("base32CreatedImageUrn.getClass(): " + base32BhieImageUrn.getClass());
		System.out.println("base32CreatedImageUrn.toString(): " + base32BhieImageUrn.toString());
		System.out.println("toStringAsVAInternal(): " + base32BhieImageUrn.toStringCDTP());
		System.out.println("base32CreatedImageUrn.toStringAsNative(): " + base32BhieImageUrn.toString(SERIALIZATION_FORMAT.NATIVE));
		// Patch83 encoding will lose the patient and study IDs, the instance ID should equal to the native version
		assertEquals(bhieImageUrn.getInstanceId(), base32BhieImageUrn.getInstanceId());
	}
	
	public void testEscapingInAdditionalIdentifiers() 
	throws URNFormatException
	{
		ImageURN imageUrn = ImageURNFactory.create("200", "rp02_0108_rp01-64aaf5dc-9e8d-41d2-810a-8df8f9e08042", "rp02_0108_rg01-67d2445e-4997-423b-9efb-3642e276c153", "1008861107V475740", "CT", ImageURN.class);              
        String vaInternalString = imageUrn.toString(SERIALIZATION_FORMAT.CDTP);
        ImageURN urnFromVaInternal = URNFactory.create(vaInternalString, SERIALIZATION_FORMAT.CDTP, ImageURN.class);
		assertEquals(imageUrn, urnFromVaInternal);
	}
	
	public void testGettingParentStudyURN() 
	throws URNFormatException
	{
		BhieImageURN bhieImageUrn = null;
		BhieStudyURN studyUrn = null;
		
		bhieImageUrn = URNFactory.create(
			"urn:bhieimage:rp02_0108_rp01-b84c243d-68ab-42ae-a125-7029777ea227",
			SERIALIZATION_FORMAT.NATIVE, 
			BhieImageURN.class);
		studyUrn = bhieImageUrn.getParentStudyURN();
		assertNotNull(studyUrn);
		
		bhieImageUrn = URNFactory.create("urn:bhieimage:nss?42*19-a-b-c[id1][id2]");
		studyUrn = bhieImageUrn.getParentStudyURN();
		assertNotNull(studyUrn);
		
		bhieImageUrn = URNFactory.create("urn:bhieimage:nss?42*19-a-b-c");
		studyUrn = bhieImageUrn.getParentStudyURN();
		assertNotNull(studyUrn);
	}
	
	public void testCdtpSerialization() 
	throws URNFormatException
	{
		BhieImageURN bhieImageUrn = BhieImageURN.create("document", "study", "patient");
		String cdtpSerialized = bhieImageUrn.toString(SERIALIZATION_FORMAT.CDTP);
		assertEquals("urn:vaimage:200-document-[patient][study]", cdtpSerialized);
		BhieImageURN bhieImageUrnClone = URNFactory.create(cdtpSerialized, SERIALIZATION_FORMAT.CDTP, BhieImageURN.class);
		
		assertEquals("document", bhieImageUrnClone.getInstanceId());
		assertEquals("patient", bhieImageUrnClone.getPatientId());
		assertEquals("study", bhieImageUrnClone.getStudyId());
	}
	
	public void testRegularExpressions()
	{
		String stimulus = "200-ABCDEFG-HIJKLMN-866321V4567-CT";

		Matcher matcher = BhieImageURN.PATCH83_VFTP_NSS_PATTERN.matcher(stimulus);
		assertTrue( stimulus, matcher.matches() );
		assertEquals( "ABCDEFG", matcher.group(BhieImageURN.PATCH83_VFTP_NSS_REGEX_INSTANCE_INDEX) );
		assertEquals( "HIJKLMN", matcher.group(BhieImageURN.PATCH83_VFTP_NSS_REGEX_GROUP_INDEX) );
		assertEquals( "866321V4567", matcher.group(BhieImageURN.PATCH83_VFTP_NSS_REGEX_PATIENT_INDEX) );
		assertEquals( "CT", matcher.group(BhieImageURN.PATCH83_VFTP_NSS_REGEX_MODALITY_INDEX) );

		stimulus = "200-ABCDEFG-HIJKLMN-866321V4567";
		matcher = BhieImageURN.PATCH83_VFTP_NSS_PATTERN.matcher(stimulus);
		assertTrue( stimulus, matcher.matches() );
		assertEquals( "ABCDEFG", matcher.group(BhieImageURN.PATCH83_VFTP_NSS_REGEX_INSTANCE_INDEX) );
		assertEquals( "HIJKLMN", matcher.group(BhieImageURN.PATCH83_VFTP_NSS_REGEX_GROUP_INDEX) );
		assertEquals( "866321V4567", matcher.group(BhieImageURN.PATCH83_VFTP_NSS_REGEX_PATIENT_INDEX) );
		assertEquals( null, matcher.group(BhieImageURN.PATCH83_VFTP_NSS_REGEX_MODALITY_INDEX) );
		
		stimulus = "200-GEZDG-GQ2TM-ICN";
		matcher = BhieImageURN.PATCH83_VFTP_NSS_PATTERN.matcher(stimulus);
		assertTrue( stimulus, matcher.matches() );
		assertEquals( "GEZDG", matcher.group(BhieImageURN.PATCH83_VFTP_NSS_REGEX_INSTANCE_INDEX) );
		assertEquals( "GQ2TM", matcher.group(BhieImageURN.PATCH83_VFTP_NSS_REGEX_GROUP_INDEX) );
		assertEquals( "ICN", matcher.group(BhieImageURN.PATCH83_VFTP_NSS_REGEX_PATIENT_INDEX) );
		assertEquals( null, matcher.group(BhieImageURN.PATCH83_VFTP_NSS_REGEX_MODALITY_INDEX) );
		
		stimulus = "200-OJYDAMS7GAYTAOC7OJYDAMJNMI4DIYZSGQZWILJWHBQWELJUGJQWKLLBGEZDKLJXGAZDSNZXG5SWCMRSG4-OJYDAMS7GAYTAOC7OJTTAMJNGY3WIMRUGQ2WKLJUHE4TOLJUGIZWELJZMVTGELJTGY2DEZJSG43GGMJVGM-1008861107V475740";
		matcher = BhieImageURN.PATCH83_VFTP_NSS_PATTERN.matcher(stimulus);
		assertTrue( stimulus, matcher.matches() );
		assertEquals( "OJYDAMS7GAYTAOC7OJYDAMJNMI4DIYZSGQZWILJWHBQWELJUGJQWKLLBGEZDKLJXGAZDSNZXG5SWCMRSG4", matcher.group(BhieImageURN.PATCH83_VFTP_NSS_REGEX_INSTANCE_INDEX) );
		assertEquals( "OJYDAMS7GAYTAOC7OJTTAMJNGY3WIMRUGQ2WKLJUHE4TOLJUGIZWELJZMVTGELJTGY2DEZJSG43GGMJVGM", matcher.group(BhieImageURN.PATCH83_VFTP_NSS_REGEX_GROUP_INDEX) );
		assertEquals( "1008861107V475740", matcher.group(BhieImageURN.PATCH83_VFTP_NSS_REGEX_PATIENT_INDEX) );
		assertEquals( null, matcher.group(BhieImageURN.PATCH83_VFTP_NSS_REGEX_MODALITY_INDEX) );
		
		stimulus = "200-GEZDG-456";
		matcher = BhieImageURN.PATCH83_VFTP_NSS_PATTERN.matcher(stimulus);
		assertFalse( stimulus, matcher.matches() );
	}
}

