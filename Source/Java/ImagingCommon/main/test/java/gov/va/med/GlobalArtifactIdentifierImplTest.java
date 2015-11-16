/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Dec 7, 2010
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author vhaiswbeckec
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */

package gov.va.med;

import gov.va.med.exceptions.GlobalArtifactIdentifierFormatException;
import gov.va.med.imaging.exceptions.URNFormatException;
import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class GlobalArtifactIdentifierImplTest
extends TestCase
{
	public void testConstructors() 
	throws URNFormatException
	{
		GlobalArtifactIdentifier gai = 
			GlobalArtifactIdentifierImpl.createFromGlobalArtifactIdentifiers("home", "repository", "document");
		assertNotNull(gai);
		System.out.println(gai.toString());
		assertEquals( "home", gai.getHomeCommunityId() );
		assertEquals( "repository", gai.getRepositoryUniqueId() );
		assertEquals( "document", gai.getDocumentUniqueId() );
	}
	
	public void testPaidConstructors() 
	throws URNFormatException, GlobalArtifactIdentifierFormatException
	{
		GlobalArtifactIdentifier gai = 
			PatientArtifactIdentifierImpl.createFromGlobalArtifactIdentifiers("home", "repository", "document", "patient");
		assertNotNull(gai);
		System.out.println(gai.toString());
		assertTrue(gai instanceof GlobalArtifactIdentifierImpl);
		assertTrue(gai instanceof PatientArtifactIdentifierImpl);
		assertEquals( "home", gai.getHomeCommunityId() );
		assertEquals( "repository", gai.getRepositoryUniqueId() );
		assertEquals( "document", gai.getDocumentUniqueId() );
		PatientArtifactIdentifierImpl pai = (PatientArtifactIdentifierImpl)gai;
		assertEquals( "patient", pai.getPatientIdentifier() );
		
		gai = GlobalArtifactIdentifierFactory.create("home", "repo", "docId");
		pai = PatientArtifactIdentifierImpl.create(gai, "patIcn");
		assertEquals("urn:gaid:home-repo-docId", gai.toString(SERIALIZATION_FORMAT.CDTP));
		assertEquals("urn:paid:home-repo-docId[patIcn]", pai.toString(SERIALIZATION_FORMAT.CDTP));
		assertEquals("urn:paid:home-repo-docId", pai.toString());

		URN urn = URNFactory.create(pai.toString(SERIALIZATION_FORMAT.CDTP), SERIALIZATION_FORMAT.CDTP);
		assertTrue(urn instanceof PatientArtifactIdentifierImpl);
		assertEquals("urn:paid:home-repo-docId", urn.toString());
		assertEquals("urn:paid:home-repo-docId[patIcn]", urn.toString(SERIALIZATION_FORMAT.CDTP));
	}
	
	public void testPaidConstructorsBorderCases()
	throws URNFormatException
	{
		GlobalArtifactIdentifier gai = 
			PatientArtifactIdentifierImpl.createFromGlobalArtifactIdentifiers("home", "repository", "document");
		assertNotNull(gai);
		assertEquals("document", gai.getDocumentUniqueId());
		assertNull( ((PatientArtifactIdentifierImpl)gai).getPatientIdentifier() );
		gai = PatientArtifactIdentifierImpl.createFromGlobalArtifactIdentifiers("home", "repository", "document", "patient");		
		assertNotNull(gai);
		assertEquals("patient", ((PatientArtifactIdentifierImpl)gai).getPatientIdentifier());
	}
	
	public void testGAIFactory()
	throws GlobalArtifactIdentifierFormatException, URNFormatException
	{
		GlobalArtifactIdentifier gai = 
			GlobalArtifactIdentifierFactory.create("urn:gaid:home-repository-document", SERIALIZATION_FORMAT.RFC2141, GlobalArtifactIdentifierImpl.class);
		assertNotNull(gai);
		assertTrue( gai instanceof GlobalArtifactIdentifier);
		assertEquals( "home", gai.getHomeCommunityId() );
		assertEquals( "repository", gai.getRepositoryUniqueId() );
		assertEquals( "document", gai.getDocumentUniqueId() );

		gai = PatientArtifactIdentifierImpl.create(gai, "patient");
		assertNotNull(gai);
		assertTrue( gai instanceof PatientArtifactIdentifierImpl);
		assertEquals( "home", gai.getHomeCommunityId() );
		assertEquals( "repository", gai.getRepositoryUniqueId() );
		assertEquals( "document", gai.getDocumentUniqueId() );
		assertEquals("patient", ((PatientArtifactIdentifierImpl)gai).getPatientIdentifier());
		
		String stringified = gai.toString(SERIALIZATION_FORMAT.CDTP);
		System.out.println(stringified);
		
		gai = 
			GlobalArtifactIdentifierFactory.create("urn:paid:home-repository-document[patient]", SERIALIZATION_FORMAT.RFC2141, PatientArtifactIdentifierImpl.class);
		assertNotNull(gai);
		assertTrue( gai instanceof PatientArtifactIdentifierImpl);
		assertEquals( "home", gai.getHomeCommunityId() );
		assertEquals( "repository", gai.getRepositoryUniqueId() );
		assertEquals( "document", gai.getDocumentUniqueId() );
		assertEquals("patient", ((PatientArtifactIdentifierImpl)gai).getPatientIdentifier());
		
	}
}
