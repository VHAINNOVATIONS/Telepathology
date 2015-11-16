/**
 * 
 */
package gov.va.med;

import gov.va.med.exceptions.GlobalArtifactIdentifierFormatException;
import gov.va.med.imaging.DocumentSetURN;
import gov.va.med.imaging.DocumentURN;
import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.StudyURN;
import gov.va.med.imaging.exceptions.URNFormatException;
import java.lang.reflect.InvocationTargetException;
import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class TestGlobalArtifactIdentifierFactory
extends TestCase
{

	/**
	 * Test method for {@link gov.va.med.GlobalArtifactIdentifierFactory#create(java.lang.String, java.lang.String, java.lang.String)}.
	 * @throws URNFormatException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws NoSuchMethodException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 * @throws URNFormatException 
	 * @throws GlobalArtifactIdentifierFormatException 
	 */
	public void testCreate() 
	throws URNFormatException, GlobalArtifactIdentifierFormatException
	{
		GlobalArtifactIdentifier artifactIdentifier;
		
		ImageURN imageUrn = ImageURN.create("660", "655321", "100", "1111V11");
		assertNotNull( imageUrn );
		assertEquals("655321", imageUrn.getImageId());
		assertEquals("660", imageUrn.getOriginatingSiteId());
		assertEquals("1111V11", imageUrn.getPatientId());
		assertEquals("100", imageUrn.getStudyId());
		
		artifactIdentifier = 
			GlobalArtifactIdentifierFactory.create(
				imageUrn.getHomeCommunityId(), 
				imageUrn.getRepositoryUniqueId(), 
				imageUrn.getDocumentUniqueId());
		assertNotNull( artifactIdentifier );
		assertTrue(
			"Artifact Identifier is of type '" + artifactIdentifier.getClass().getSimpleName() + "'.", 
			artifactIdentifier instanceof ImageURN);
		assertEquals(imageUrn, artifactIdentifier);

		DocumentURN documentUrn = DocumentURN.create("660", "100", "1111V11", "655321");
		assertNotNull( documentUrn );
		artifactIdentifier = 
			GlobalArtifactIdentifierFactory.create(documentUrn.getHomeCommunityId(), documentUrn.getRepositoryUniqueId(), documentUrn.getDocumentUniqueId());
		assertNotNull( artifactIdentifier );
		assertTrue(
			"Artifact Identifier is of type '" + artifactIdentifier.getClass().getSimpleName() + "'.", 
			artifactIdentifier instanceof DocumentURN);
		assertEquals(documentUrn, artifactIdentifier);

		StudyURN studyUrn = StudyURN.create("660", "1111V11", "655321");
		assertNotNull( studyUrn );
		artifactIdentifier = GlobalArtifactIdentifierFactory.create(
			studyUrn.getHomeCommunityId(), 
			studyUrn.getRepositoryUniqueId(), 
			studyUrn.getDocumentUniqueId(),
			StudyURN.class);
		assertNotNull( artifactIdentifier );
		assertTrue(
			"Artifact Identifier is of type '" + artifactIdentifier.getClass().getSimpleName() + "'.", 
			artifactIdentifier instanceof StudyURN);
		assertEquals(studyUrn, artifactIdentifier);

		DocumentSetURN documentSetUrn = DocumentSetURN.create("660", "1111V11", "655321");
		assertNotNull( documentSetUrn );
		artifactIdentifier = GlobalArtifactIdentifierFactory.create(
			documentSetUrn.getHomeCommunityId(), 
			documentSetUrn.getRepositoryUniqueId(), 
			documentSetUrn.getDocumentUniqueId(),
			DocumentSetURN.class);
		assertNotNull( artifactIdentifier );
		assertTrue(
			"Artifact Identifier is of type '" + artifactIdentifier.getClass().getSimpleName() + "'.", 
			artifactIdentifier instanceof DocumentSetURN);
		assertEquals(documentSetUrn, artifactIdentifier);
	}

	public void testHomeCommunityIds() 
	throws GlobalArtifactIdentifierFormatException, URNFormatException
	{
		GlobalArtifactIdentifier gai = GlobalArtifactIdentifierFactory.create("2.16.840.1.113883.3.166", "660", "2772-2772-1006152719V948936");
		assertNotNull(gai);
		assertTrue(gai instanceof ImageURN);
		
		gai = GlobalArtifactIdentifierFactory.create("urn:gaid:2.16.840.1.113883.3.42.10012.100001.206-central-h0154fb19789cdc4e4db4d336139c3a89500114", SERIALIZATION_FORMAT.RFC2141 );
		assertNotNull(gai);
		assertTrue(gai instanceof GlobalArtifactIdentifierImpl);
		GlobalArtifactIdentifier gaiEmbedded = ((GlobalArtifactIdentifierImpl)gai).createFromEmbededGlobalArtifactIdentifier();
		assertNotNull(gaiEmbedded);
		
		URN urn = URNFactory.create("urn:gaid:2.16.840.1.113883.3.42.10012.100001.206-central-h0154fb19789cdc4e4db4d336139c3a89500114");
		assertNotNull(urn);
		assertTrue(urn instanceof GlobalArtifactIdentifierImpl);
		gaiEmbedded = ((GlobalArtifactIdentifierImpl)urn).createFromEmbededGlobalArtifactIdentifier();
		assertNotNull(gaiEmbedded);
	}
}
