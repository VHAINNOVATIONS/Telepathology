package gov.va.med.imaging;

import gov.va.med.URN;
import gov.va.med.URNFactory;
import gov.va.med.imaging.exceptions.StudyURNFormatException;
import gov.va.med.imaging.exceptions.URNFormatException;
import junit.framework.TestCase;

public class DocumentSetURNTest 
extends TestCase
{
	
	public void testDocumentSetURNBuilding() 
	throws URNFormatException
	{
		DocumentSetURN documentSetURN1 = null;
		DocumentSetURN documentSetURN2 = null;
		
		documentSetURN1 = DocumentSetURN.create("655321", "111DFEA", "44456");
		documentSetURN2 = URNFactory.create("urn:vadocset:655321-111DFEA-44456");
		System.out.println("DocumentSetURN1 = [" + documentSetURN1.toString() + "], DocumentSetURN2 is [" + documentSetURN2.toString() + "]");
		assertEquals(documentSetURN1, documentSetURN2);

		try
		{
			documentSetURN1 = DocumentSetURN.create("655321", "111-DFEA","44456");
			fail();
		}
		catch(URNFormatException iufX)
		{}
		try
		{
			documentSetURN1 = DocumentSetURN.create("655-321", "111DFEA","44456");
			fail();
		}
		catch(URNFormatException iufX)
		{}
		try
		{
			documentSetURN1 = DocumentSetURN.create("655321", "111DFEA","444-56");
			fail();
		}
		catch(URNFormatException iufX)
		{}
		try
		{
			documentSetURN2 = URNFactory.create("urn:vaimage:655321-111D-FEA-44456");
			assertFalse(documentSetURN2 instanceof DocumentSetURN);
		}
		catch(Exception x)
		{}
		try
		{
			documentSetURN2 = URNFactory.create("urn:vastudy:655321-111D-FEA-44456");
			assertFalse(documentSetURN2 instanceof DocumentSetURN);
		}
		catch(Exception x)
		{}

		documentSetURN1 = DocumentSetURN.create("ABFFG", "1","1234");
		documentSetURN2 = URNFactory.create("urn:vadocset:ABFFG-1-1234");
		assertTrue(documentSetURN2 instanceof DocumentSetURN);
		System.out.println("DocumentSetURN1 = [" + documentSetURN1.toString() + "], DocumentSetURN2 is [" + documentSetURN2.toString() + "]");
		assertEquals(documentSetURN1, documentSetURN2);

		documentSetURN1 = DocumentSetURN.create("A", "A","A");
		documentSetURN2 = URNFactory.create("urn:vadocset:A-A-A");
		assertTrue(documentSetURN2 instanceof DocumentSetURN);
		System.out.println("DocumentSetURN1 = [" + documentSetURN1.toString() + "], DocumentSetURN2 is [" + documentSetURN2.toString() + "]");
		assertEquals(documentSetURN1, documentSetURN2);

		documentSetURN1 = DocumentSetURN.create("00000", "11111","222222");
		documentSetURN2 = URNFactory.create("urn:vadocset:00000-11111-222222");
		assertTrue(documentSetURN2 instanceof DocumentSetURN);
		System.out.println("DocumentSetURN1 = [" + documentSetURN1.toString() + "], DocumentSetURN2 is [" + documentSetURN2.toString() + "]");
		assertEquals(documentSetURN1, documentSetURN2);

		documentSetURN1 = DocumentSetURN.create("000000000000000111111111111111999999999999999", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ");
		documentSetURN2 = URNFactory.create("urn:vadocset:000000000000000111111111111111999999999999999-AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA-ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ");
		assertTrue(documentSetURN2 instanceof DocumentSetURN);
		System.out.println("DocumentSetURN1 = [" + documentSetURN1.toString() + "], DocumentSetURN2 is [" + documentSetURN2.toString() + "]");
		assertEquals(documentSetURN1, documentSetURN2);

		
		documentSetURN1 = DocumentSetURN.create("vaguid", "655321","09876V12345");
		documentSetURN2 = URNFactory.create("urn:vadocset:vaguid-655321-09876V12345");
		assertTrue(documentSetURN2 instanceof DocumentSetURN);
		System.out.println("DocumentSetURN1 = [" + documentSetURN1.toString() + "], DocumentSetURN2 is [" + documentSetURN2.toString() + "]");
		assertEquals(documentSetURN1, documentSetURN2);
	}
	
	/**
	 * @throws URNFormatException 
	 * 
	 */
	public void testTypeTranslation() 
	throws URNFormatException
	{
		DocumentSetURN documentSetUrn = URNFactory.create("urn:vadocset:ABFFG-1-1234", DocumentSetURN.class);
		StudyURN studyUrn = StudyURN.create(documentSetUrn);
		DocumentSetURN documentSetUrn2 = DocumentSetURN.create(studyUrn);
		
		assertEquals(documentSetUrn, documentSetUrn2);
	}
	
	public void testToStringVariations() 
	throws URNFormatException
	{
		DocumentSetURN urn;
		String ts;
		String tsInternal;
		String tsNative;
		
		urn = URNFactory.create("urn:vadocset:nss?42*19-a-b");
		ts = urn.toString();
		tsInternal = urn.toStringCDTP();
		tsNative = urn.toStringNative();
		System.out.println("RFC2141 = " + ts);
		System.out.println("Internal = " + tsInternal);
		System.out.println("Native = " + tsNative);
		assertEquals("urn:vadocset:nss%3f42*19-a-b", ts);
		assertEquals("urn:vadocset:nss%3f42%2a19-a-b", tsInternal);
		assertEquals("urn:vadocset:nss?42*19-a-b", tsNative);

		try{urn = URNFactory.create("urn:vadocset:nss?42*19-a-b[id1]");}
		catch(URNFormatException urnfX){}

		urn = URNFactory.create("urn:vadocset:nss?42*19-a-b");
		ts = urn.toString();
		tsInternal = urn.toStringCDTP();
		tsNative = urn.toStringNative();
		System.out.println("RFC2141 = " + ts);
		System.out.println("Internal = " + tsInternal);
		System.out.println("Native = " + tsNative);
		assertEquals("urn:vadocset:nss%3f42*19-a-b", ts);
		assertEquals("urn:vadocset:nss%3f42%2a19-a-b", tsInternal);
		assertEquals("urn:vadocset:nss?42*19-a-b", tsNative);

		try{urn = URNFactory.create("urn:vadocset:nss?42*19-a-b[id1]");}
		catch(URNFormatException urnfX){}
	}
}
