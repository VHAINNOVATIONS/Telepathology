package gov.va.med.imaging;

import gov.va.med.SERIALIZATION_FORMAT;
import gov.va.med.URN;
import gov.va.med.URNFactory;
import gov.va.med.imaging.exceptions.URNFormatException;
import junit.framework.TestCase;

public class BhieStudyURNTest
	extends TestCase
{
	private String[] VALID_URN_EXAMPLES = new String[]
	{
		"urn:bhiestudy:rp02-UJBJUYGBHCGFDUGGUYGUY",
		"urn:vastudy:200-ABFFG-1-1234",
		"urn:bhiestudy:655321-111DFEA-44456",
		"urn:bhiestudy:000000000000000111111111111111999999999999999-AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA-ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ" };

	public void testStudyUrnBuilding() throws URNFormatException
	{
		BhieStudyURN studyURN1 = null;
		BhieStudyURN studyURN2 = null;

		studyURN1 = BhieStudyURN.create("42", "655321");
	}

	public void testToStringVariations() 
	throws URNFormatException
	{
		BhieStudyURN urn;
		BhieStudyURN cloneUrn;
		String ts;
		String tsCDTP;
		String tsNative;
		
		urn = URNFactory.create("urn:bhiestudy:nss?42*19-a");
		assertTrue(urn instanceof BhieStudyURN);
		ts = urn.toString();
		tsCDTP = urn.toStringCDTP();
		tsNative = urn.toStringNative();
		//System.out.println("RFC2141 = " + ts);
		//System.out.println("Internal = " + tsInternal);
		//System.out.println("Native = " + tsNative);
		assertEquals("urn:bhiestudy:nss%3f42*19-a", ts);
		assertEquals("urn:vastudy:200-nss%3f42%2a19%2da", tsCDTP);
		assertEquals("urn:bhiestudy:nss?42*19-a", tsNative);

		urn = URNFactory.create("urn:bhiestudy:4219-a-b-c");
		ts = urn.toString();
		tsCDTP = urn.toString(SERIALIZATION_FORMAT.CDTP);
		tsNative = urn.toString(SERIALIZATION_FORMAT.NATIVE);
		//System.out.println("RFC2141 = " + ts);
		//System.out.println("Internal = " + tsInternal);
		//System.out.println("Native = " + tsNative);
		assertEquals("urn:bhiestudy:4219-a-b-c", ts);
		assertEquals("urn:vastudy:200-4219%2da%2db%2dc", tsCDTP);
		assertEquals("urn:bhiestudy:4219-a-b-c", tsNative);
		cloneUrn = URNFactory.create(ts);
		assertEquals(urn, cloneUrn);
		cloneUrn = URNFactory.create(tsCDTP, SERIALIZATION_FORMAT.CDTP);
		assertEquals(urn, cloneUrn);
		cloneUrn = URNFactory.create(tsNative, SERIALIZATION_FORMAT.NATIVE);
		assertEquals(urn, cloneUrn);

		urn = URNFactory.create("urn:bhiestudy:nss?42*19-a-b-c");
		ts = urn.toString();
		tsCDTP = urn.toStringCDTP();
		tsNative = urn.toStringNative();
		//System.out.println("RFC2141 = " + ts);
		//System.out.println("Internal = " + tsInternal);
		//System.out.println("Native = " + tsNative);
		assertEquals("urn:bhiestudy:nss%3f42*19-a-b-c", ts);
		assertEquals("urn:vastudy:200-nss%3f42%2a19%2da%2db%2dc", tsCDTP);
		assertEquals("urn:bhiestudy:nss?42*19-a-b-c", tsNative);
		cloneUrn = URNFactory.create(ts);
		assertEquals(urn, cloneUrn);
		cloneUrn = URNFactory.create(tsCDTP, SERIALIZATION_FORMAT.CDTP);
		assertEquals(urn, cloneUrn);
		cloneUrn = URNFactory.create(tsNative);
		assertEquals(urn, cloneUrn);
	}
	
	public void testToStringVariationsWithAdditionalIdentifiers() 
	throws URNFormatException
	{
		BhieStudyURN urn;
		BhieStudyURN cloneUrn;
		String ts;
		String tsCDTP;
		String tsNative;
		
		urn = URNFactory.create("urn:bhiestudy:nss?42*19-a-b-c[id1]");
		ts = urn.toString();
		tsCDTP = urn.toString(SERIALIZATION_FORMAT.CDTP);
		tsNative = urn.toString(SERIALIZATION_FORMAT.NATIVE);
		//System.out.println("RFC2141 = " + ts);
		//System.out.println("Internal = " + tsInternal);
		//System.out.println("Native = " + tsNative);
		assertEquals("urn:bhiestudy:nss%3f42*19-a-b-c", ts);
		assertEquals("urn:vastudy:200-nss%3f42%2a19%2da%2db%2dc[id1]", tsCDTP);
		assertEquals("urn:bhiestudy:nss?42*19-a-b-c", tsNative);
		cloneUrn = URNFactory.create(tsCDTP, SERIALIZATION_FORMAT.CDTP);
		assertEquals(urn, cloneUrn);
	}

	public void testStringifaction() throws URNFormatException
	{
		BhieStudyURN bhieStudyUrn = URNFactory.create(
			"urn:bhiestudy:rp02_0108_rg01-67d2445e-4997-423b-9efb-3642e276c153", 
			BhieStudyURN.class
		);
		bhieStudyUrn.setPatientId("1008861107V475740");
		String stringified = bhieStudyUrn.toString();
		String stringifiedAsVAInternal = bhieStudyUrn.toString(SERIALIZATION_FORMAT.CDTP);
		String stringifiedPatch83VFTP = bhieStudyUrn.toString(SERIALIZATION_FORMAT.PATCH83_VFTP);
		String stringifiedAsNative = bhieStudyUrn.toString(SERIALIZATION_FORMAT.NATIVE);

		//System.out.println("bhieStudyUrn.toString(): " + stringified);
		//System.out.println("bhieStudyUrn.toStringAsVAInternal(): " + stringifiedAsVAInternal);
		//System.out.println("bhieStudyUrn.toStringPatch83(): " + stringifiedPatch83VFTP);
		//System.out.println("bhieStudyUrn.toStringAsNative(): " + stringifiedAsNative);
		
		URN base32CreatedStudyUrn = URNFactory.create(stringifiedPatch83VFTP, SERIALIZATION_FORMAT.PATCH83_VFTP);
		//System.out.println("base32CreatedStudyUrn.getClass(): " + base32CreatedStudyUrn.getClass());
		//System.out.println("base32CreatedStudyUrn.toString(): " + base32CreatedStudyUrn.toString());
		//System.out.println("base32CreatedStudyUrn.toStringAsNative(): " + base32CreatedStudyUrn.toString(SERIALIZATION_FORMAT.NATIVE));
		assertEquals(bhieStudyUrn, base32CreatedStudyUrn);

		URN nativeCreatedUrn = URNFactory.create(stringifiedAsNative, SERIALIZATION_FORMAT.NATIVE);
		//System.out.println("nativeCreatedUrn.getClass(): " + nativeCreatedUrn.getClass());
		//System.out.println("nativeCreatedUrn.toString(): " + nativeCreatedUrn.toString());
		//System.out.println("nativeCreatedUrn.toStringAsNative(): " + nativeCreatedUrn.toString(SERIALIZATION_FORMAT.NATIVE));
		assertEquals(bhieStudyUrn, nativeCreatedUrn);
	}
}
