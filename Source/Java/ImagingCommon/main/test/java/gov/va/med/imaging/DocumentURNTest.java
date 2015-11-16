package gov.va.med.imaging;

import java.lang.reflect.Method;
import gov.va.med.*;
import gov.va.med.GlobalArtifactIdentifierFactory.ConfigurationException;
import gov.va.med.exceptions.GlobalArtifactIdentifierFormatException;
import gov.va.med.imaging.exceptions.URNFormatException;
import junit.framework.TestCase;

public class DocumentURNTest 
extends TestCase
{
	public void testURNBuildingNegatives() 
	throws URNFormatException
	{
		DocumentURN urn1 = null;
		DocumentURN urn2 = null;

		urn1 = DocumentURN.create("A","A","A","A");
		urn2 = URNFactory.create("urn:vadoc:A-A-A-A-");
		System.out.println("urn1 = '" + urn1.toString() + "', urn2 is '" + urn2.toString() + "'");
		assertEquals(urn1, urn2);
		
		urn1 = DocumentURN.create("A","A","A","A");
		urn2 = URNFactory.create("urn:vadoc:A-A-A-A");
		System.out.println("urn1 = '" + urn1.toString() + "', urn2 is '" + urn2.toString() + "'");
		assertEquals(urn1, urn2);
		
		urn1 = DocumentURN.create("ABFFG", "1","871","VVBB");
		urn2 = URNFactory.create("urn:vadoc:ABFFG-1-871-VVBB-");
		System.out.println("urn1 = '" + urn1.toString() + "', urn2 is '" + urn2.toString() + "'");
		assertEquals(urn1, urn2);
		
		urn1 = DocumentURN.create("ABFFG", "1","871","VVBB");
		urn2 = URNFactory.create("urn:vadoc:ABFFG-1-871-VVBB");
		System.out.println("urn1 = '" + urn1.toString() + "', urn2 is '" + urn2.toString() + "'");
		assertEquals(urn1, urn2);
		
		urn1 = DocumentURN.create("ABFFG", "1","871","VVBB");
		urn2 = URNFactory.create("urn:vadoc:ABFFG-1-871-VVBB");
		System.out.println("urn1 = '" + urn1.toString() + "', urn2 is '" + urn2.toString() + "'");
		assertEquals(urn1, urn2);

		urn1 = DocumentURN.create("A","A","A","A");
		urn2 = URNFactory.create("urn:vadoc:A-A-A-A-");
		System.out.println("urn1 = '" + urn1.toString() + "', urn2 is '" + urn2.toString() + "'");
		assertEquals(urn1, urn2);
		
		urn1 = DocumentURN.create("A", "A","A","A");
		urn2 = URNFactory.create("urn:vadoc:A-A-A-A");
		System.out.println("urn1 = '" + urn1.toString() + "', urn2 is '" + urn2.toString() + ";");
		assertEquals(urn1, urn2);

		urn1 = DocumentURN.create("00000", "11111","222222","333333");
		urn2 = URNFactory.create("urn:vadoc:00000-11111-222222-333333-");
		System.out.println("urn1 = '" + urn1.toString() + "', urn2 is '" + urn2.toString() + "'");
		assertEquals(urn1, urn2);
		
		urn1 = DocumentURN.create("00000", "11111","222222","333333");
		urn2 = URNFactory.create("urn:vadoc:00000-11111-222222-333333");
		System.out.println("urn1 = [" + urn1.toString() + "], urn2 is [" + urn2.toString() + "]");
		assertEquals(urn1, urn2);

		urn1 = DocumentURN.create("000000000000000111111111111111999999999999999", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","222222222222222222222222223333333333333333","444444444444444444444444555555555555555555555");
		urn2 = URNFactory.create("urn:vadoc:000000000000000111111111111111999999999999999-AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA-222222222222222222222222223333333333333333-444444444444444444444444555555555555555555555-");
		System.out.println("urn1 = [" + urn1.toString() + "], urn2 is [" + urn2.toString() + "]");
		assertEquals(urn1, urn2);

		urn1 = DocumentURN.create("000000000000000111111111111111999999999999999", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","222222222222222222222222223333333333333333","444444444444444444444444555555555555555555555");
		urn2 = URNFactory.create("urn:vadoc:000000000000000111111111111111999999999999999-AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA-222222222222222222222222223333333333333333-444444444444444444444444555555555555555555555");
		System.out.println("urn1 = [" + urn1.toString() + "], urn2 is [" + urn2.toString() + "]");
		assertEquals(urn1, urn2);
		
		urn1 = DocumentURN.create("vaguid", "655321","739172","09876");
		urn2 = URNFactory.create("urn:vadoc:vaguid-655321-739172-09876-");
		System.out.println("urn1 = [" + urn1.toString() + "], urn2 is [" + urn2.toString() + "]");
		assertEquals(urn1, urn2);
		
		urn1 = DocumentURN.create("vaguid", "655321","739172","09876");
		urn2 = URNFactory.create("urn:vadoc:vaguid-655321-739172-09876");
		System.out.println("urn1 = [" + urn1.toString() + "], urn2 is [" + urn2.toString() + "]");
		assertEquals(urn1, urn2);
		
		urn1 = DocumentURN.create("000000","111111","222222","333333");
		urn2 = URNFactory.create("urn:vadoc:000000-111111-222222-333333-");
		System.out.println("urn1 = [" + urn1.toString() + "], urn2 is [" + urn2.toString() + "]");
		assertEquals(urn1, urn2);
		
		String urnString = "urn:vadoc:AAAAAA-BBBBBB-CCCCCC-DDDDDD-";
		urn1 = URNFactory.create(urnString);
		urn2 = DocumentURN.create("AAAAAA", "BBBBBB", "CCCCCC", "DDDDDD");
		System.out.println("urn String [" + urnString + "], URN representation [" + urn1.toString() + "]");
		assertNotSame(urnString, urn1.toString());
		assertEquals(urn1, urn2);
		
	}
	
	public void testURNBuildingPositives() 
	throws URNFormatException
	{
		DocumentURN urn1 = null;
		DocumentURN urn2 = null;
		
		urn1 = DocumentURN.create("655321", "111DFEA","48173","81756");
		URN urnX = URNFactory.create("urn:vadoc:655321-111DFEA-48173-81756-");
		assertTrue(urnX instanceof DocumentURN);
		urn2 = (DocumentURN)urnX;
		
		System.out.println("urn1 = [" + urn1.toString() + "], urn2 is [" + urn2.toString() + "]");
		assertEquals(urn1, urn2);

		try
		{
			urn1 = DocumentURN.create("655321", "111-DFEA","93810","ASDFIG");
			System.out.println("urn1 = '" + (urn1 == null ? "null" : urn1.toString()) + "'");
			fail();
		}
		catch(URNFormatException iufX)
		{}
		try
		{
			urn1 = DocumentURN.create("655-321", "111DFEA","93810","ASDFIG");
			System.out.println("urn1 = '" + (urn1 == null ? "null" : urn1.toString()) + "'");
			fail();
		}
		catch(URNFormatException iufX)
		{}
		try
		{
			urn1 = DocumentURN.create("655321", "111DFEA","93-810","ASDFIG");
			System.out.println("urn1 = '" + (urn1 == null ? "null" : urn1.toString()) + "'");
			fail();
		}
		catch(URNFormatException iufX)
		{}
		try
		{
			urn1 = DocumentURN.create("655321", "111DFEA","93810","ASD-FIG");
			System.out.println("urn1 = '" + (urn1 == null ? "null" : urn1.toString()) + "'");
			fail();
		}
		catch(URNFormatException iufX)
		{}
		try 
		{
			urn1 = DocumentURN.create("655321", "111DFEA","93810","ASD-FIG");
			System.out.println("urn1 = '" + (urn1 == null ? "null" : urn1.toString()) + "'");
			fail();
		}
		catch(URNFormatException iufX) 
		{}

	}
	
	/**
	 * @throws URNFormatException 
	 * 
	 */
	public void testTypeTranslation() 
	throws URNFormatException
	{
		DocumentURN documentUrn = URNFactory.create("urn:vadoc:655321-111DFEA-81736-5221G", DocumentURN.class);
		ImageURN imageUrn = ImageURN.create(documentUrn);
		DocumentURN documentUrn2 = DocumentURN.create(imageUrn);
		
		assertTrue(documentUrn.equals(documentUrn2));
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
		
		urn = URNFactory.create("urn:vadoc:nss?42*19-a-b-c");
		ts = urn.toString();
		tsInternal = urn.toString(SERIALIZATION_FORMAT.CDTP);
		tsNative = urn.toString(SERIALIZATION_FORMAT.NATIVE);
		System.out.println("RFC2121 = " + ts);
		System.out.println("Internal = " + tsInternal);
		System.out.println("Native = " + tsNative);
		assertEquals("urn:vadoc:nss%3f42*19-a-b-c", ts);
		assertEquals("urn:vadoc:nss%3f42%2a19-a-b-c", tsInternal);
		assertEquals("urn:vadoc:nss?42*19-a-b-c", tsNative);

		try{urn = URNFactory.create("urn:vadoc:nss?42*19-a-b-c[id1][id2]");}
		catch(URNFormatException urnfX){}

		urn = URNFactory.create("urn:vadoc:nss?42*19-a-b-c");
		ts = urn.toString();
		tsInternal = urn.toString(SERIALIZATION_FORMAT.CDTP);
		tsNative = urn.toString(SERIALIZATION_FORMAT.NATIVE);
		System.out.println("RFC2121 = " + ts);
		System.out.println("Internal = " + tsInternal);
		System.out.println("Native = " + tsNative);
		assertEquals("urn:vadoc:nss%3f42*19-a-b-c", ts);
		assertEquals("urn:vadoc:nss%3f42%2a19-a-b-c", tsInternal);
		assertEquals("urn:vadoc:nss?42*19-a-b-c", tsNative);

		try{urn = URNFactory.create("urn:vadoc:nss?42*19-a-b-c[id1][id2]");}
		catch(URNFormatException urnfX){}
	}
	
	// ======================================================================================
	// An aborted attempt to build generic test cases
	// It all works but not really worth the effort
	// ======================================================================================
	
	public void testGlobalArtifactIdentifier() 
	throws URNFormatException, GlobalArtifactIdentifierFormatException
	{
		GlobalArtifactIdentifier gaiOriginal = null;
		
		gaiOriginal = DocumentURN.create("655321", "111DFEA","48173","81756");
		
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
}
