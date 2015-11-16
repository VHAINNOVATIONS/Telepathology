package gov.va.med.imaging;

import gov.va.med.StudyURNFactory;
import gov.va.med.URN;
import gov.va.med.URNFactory;
import gov.va.med.imaging.exceptions.StudyURNFormatException;
import gov.va.med.imaging.exceptions.URNFormatException;
import junit.framework.TestCase;

public class StudyURNTest 
extends TestCase
{
	private String[] VALID_URN_EXAMPLES = new String[]
	{
		"urn:vastudy:A-A-A",
		"urn:vastudy:ABFFG-1-1234",
		"urn:vastudy:655321-111DFEA-44456",
		"urn:vastudy:000000000000000111111111111111999999999999999-AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA-ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ",
		"urn:vastudy:660-250-1006170647V052871"
	};

	public void testStudyUrnBuilding() 
	throws URNFormatException
	{
		StudyURN studyURN1 = null;
		StudyURN studyURN2 = null;
		
		studyURN1 = StudyURN.create("655321", "111DFEA", "44456");
		studyURN2 = URNFactory.create("urn:vastudy:655321-111DFEA-44456");
		System.out.println("StudyURN1 = [" + studyURN1.toString() + "], StudyURN2 is [" + studyURN2.toString() + "]");
		assertEquals(studyURN1, studyURN2);

		try
		{
			studyURN1 = StudyURN.create("655321", "111-DFEA","44456");
			fail();
		}
		catch(StudyURNFormatException iufX)
		{}
		try
		{
			studyURN1 = StudyURN.create("655-321", "111DFEA","44456");
			fail();
		}
		catch(StudyURNFormatException iufX)
		{}
		try
		{
			studyURN1 = StudyURN.create("655321", "111DFEA","444-56");
			fail();
		}
		catch(StudyURNFormatException iufX)
		{}
		try
		{
			studyURN2 = URNFactory.create("urn:vaimage:655321-111D-FEA-44456");
			assertFalse(studyURN2 instanceof StudyURN);
		}
		catch(Exception x)
		{}
		
		try
		{
			studyURN2 = URNFactory.create("urn:vastudy:753.42.86");
			assertTrue(studyURN2 instanceof StudyURN);
		}
		catch(Exception x)
		{}

		String urnString = "urn:vastudy:660-250-1006170647V052871";
		studyURN2 = URNFactory.create(urnString);


		
		studyURN1 = StudyURN.create("ABFFG", "1","1234");
		studyURN2 = URNFactory.create("urn:vastudy:ABFFG-1-1234");
		assertTrue(studyURN2 instanceof StudyURN);
		System.out.println("StudyURN1 = [" + studyURN1.toString() + "], StudyURN2 is [" + studyURN2.toString() + "]");
		assertEquals(studyURN1, studyURN2);

		studyURN1 = StudyURN.create("A", "A","A");
		studyURN2 = URNFactory.create("urn:vastudy:A-A-A");
		assertTrue(studyURN2 instanceof StudyURN);
		System.out.println("StudyURN1 = [" + studyURN1.toString() + "], StudyURN2 is [" + studyURN2.toString() + "]");
		assertEquals(studyURN1, studyURN2);

		studyURN1 = StudyURN.create("00000", "11111","222222");
		studyURN2 = URNFactory.create("urn:vastudy:00000-11111-222222");
		assertTrue(studyURN2 instanceof StudyURN);
		System.out.println("StudyURN1 = [" + studyURN1.toString() + "], StudyURN2 is [" + studyURN2.toString() + "]");
		assertEquals(studyURN1, studyURN2);

		studyURN1 = StudyURN.create("000000000000000111111111111111999999999999999", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ");
		studyURN2 = URNFactory.create("urn:vastudy:000000000000000111111111111111999999999999999-AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA-ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ");
		assertTrue(studyURN2 instanceof StudyURN);
		System.out.println("StudyURN1 = [" + studyURN1.toString() + "], StudyURN2 is [" + studyURN2.toString() + "]");
		assertEquals(studyURN1, studyURN2);
			
	}
	
	public void testToStringVariations() 
	throws URNFormatException
	{
		StudyURN urn;
		String ts;
		String tsInternal;
		String tsNative;
		
		try
		{
			urn = URNFactory.create("urn:vastudy:660-nss?42*19-a[id1]");
			fail("Study URN in invalid format should have failed and did not.");
		}
		catch(URNFormatException urnfX){}

		try
		{
			urn = URNFactory.create("urn:vastudy:nss?42*19-a-b[id1]");
			fail("Study URN in invalid format should have failed and did not.");
		}
		catch(URNFormatException urnfX){}

		urn = URNFactory.create("urn:vastudy:660-1011^6949598.9048^1^191-12345", StudyURN.class);
		assertEquals("660", urn.getOriginatingSiteId());
		assertEquals("1011^6949598.9048^1^191", urn.getStudyId());
		assertEquals("12345", urn.getPatientId());
		
		ts = urn.toString();
		tsInternal = urn.toStringCDTP();
		tsNative = urn.toStringNative();
		assertEquals("urn:vastudy:660-1011%5e6949598.9048%5e1%5e191-12345", ts);
		assertEquals("urn:vastudy:660-1011%5e6949598.9048%5e1%5e191-12345", tsInternal);
		assertEquals("urn:vastudy:660-1011^6949598.9048^1^191-12345", tsNative);
		System.out.println("Factory parsed, RFC2141 = " + ts);
		System.out.println("Factory parsed, Internal = " + tsInternal);
		System.out.println("Factory parsed, Native = " + tsNative);

		urn = StudyURNFactory.create("660", "1011^6949598.9048^1^191", "12345", StudyURN.class);
		assertEquals("1011^6949598.9048^1^191", urn.getStudyId());
		assertEquals("660", urn.getOriginatingSiteId());
		assertEquals("12345", urn.getPatientId());
		
		urn = StudyURNFactory.create("660", "1011^6949598.9048^1^191", "12345", StudyURN.class);
		ts = urn.toString();
		tsInternal = urn.toStringCDTP();
		tsNative = urn.toStringNative();
		assertEquals("urn:vastudy:660-1011%5e6949598.9048%5e1%5e191-12345", ts);
		assertEquals("urn:vastudy:660-1011%5e6949598.9048%5e1%5e191-12345", tsInternal);
		assertEquals("urn:vastudy:660-1011^6949598.9048^1^191-12345", tsNative);
		System.out.println("StudyURN parsed, RFC2141 = " + ts);
		System.out.println("StudyURN parsed, Internal = " + tsInternal);
		System.out.println("StudyURN parsed, Native = " + tsNative);
		
		//RFC2141 = urn:vastudy:660-1011^6949598.9048^1^191-12345
		//Internal = urn:vastudy:660-1011^6949598.9048^1^191-12345
		//Native = urn:vastudy:660-1011^6949598.9048^1^191-12345

	}
	
	public void testStringification() 
	throws URNFormatException
	{
		for(String valid : VALID_URN_EXAMPLES)
		{
			URN original = URNFactory.create(valid);
			
			assertEquals(valid, original.toString());
		}
	}
	
	public void testStudyUrnComparison()
	throws URNFormatException
	{
		StudyURN studyUrn = 
			StudyURNFactory.create("660", "1234", "icn1234", StudyURN.class);
		StudyURN studyUrn2 = 
			StudyURNFactory.create("660", "1234", "icn1234", StudyURN.class);
		
		assertEquals(studyUrn, studyUrn2);
		assertTrue(studyUrn.equals(studyUrn2));
		
		StudyURN studyUrn3 = 
			StudyURNFactory.create("660", "098978", "icn1234", StudyURN.class);
		
		assertFalse("URN '" + studyUrn.toString() + "' is not equal to '" + studyUrn3.toString() + "'.", 
				studyUrn.equals(studyUrn3));
	}

}
