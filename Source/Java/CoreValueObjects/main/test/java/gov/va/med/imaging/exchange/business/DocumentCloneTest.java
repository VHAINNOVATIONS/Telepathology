/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 23, 2011
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswwerfej
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
package gov.va.med.imaging.exchange.business;

import java.util.Date;

import gov.va.med.ImageURNFactory;
import gov.va.med.MediaType;
import gov.va.med.WellKnownOID;
import gov.va.med.imaging.DocumentURN;
import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.exchange.business.documents.Document;

import org.junit.Test;

import static org.junit.Assert.* ;

/**
 * @author vhaiswwerfej
 *
 */
public class DocumentCloneTest
extends AbstractCloneTest
{
	
	@Test
	public void testCloneDocument()
	{
		try
		{
			String documentSetIen = "documentSet123";
			String homeCommunityId = WellKnownOID.VA_DOCUMENT.getCanonicalValue().toString();
			String repositoryId = "123";
			String documentId = "456";
			String consolidatedSiteNumber = "con123";
			String patientIcn = "pat123";
			
			ImageURN imageUrn = ImageURNFactory.create(repositoryId, documentId, 
					documentSetIen, patientIcn, null, ImageURN.class);
			DocumentURN documentUrn = DocumentURN.create(imageUrn);
			
			
			//GlobalArtifactIdentifier gai = GlobalArtifactIdentifierFactory.create(homeCommunityId, 
					//repositoryId, documentId);
			
			Document document = new Document(documentSetIen, documentUrn, new Date(), 123, "clinType", 123456, null);
			document.setConfidentialityCode(987);
			document.setConsolidatedSiteNumber(consolidatedSiteNumber);
			document.setDescription("description");
			document.setLanguageCode("language123");
			document.setMediaType(MediaType.APPLICATION_DOCX);
			document.setName("name");
			
			
			Document clonedDocument = document.cloneWithConsolidatedSiteNumber();
			assertEquals(documentUrn, document.getGlobalArtifactIdentifier());
			assertEquals(homeCommunityId, document.getGlobalArtifactIdentifier().getHomeCommunityId());			
			assertEquals(repositoryId, document.getGlobalArtifactIdentifier().getRepositoryUniqueId());
			assertEquals(repositoryId, document.getSiteNumber());
			assertEquals(repositoryId, document.getRepositoryId());
			assertEquals(documentId, document.getDocumentUrn().getDocumentId());
			assertEquals(homeCommunityId, clonedDocument.getGlobalArtifactIdentifier().getHomeCommunityId());			
			assertEquals(consolidatedSiteNumber, clonedDocument.getGlobalArtifactIdentifier().getRepositoryUniqueId());
			assertEquals(documentId, clonedDocument.getDocumentUrn().getDocumentId());
			assertEquals(consolidatedSiteNumber, clonedDocument.getSiteNumber());
			assertEquals(consolidatedSiteNumber, clonedDocument.getRepositoryId());
			
			assertEquals(consolidatedSiteNumber, document.getConsolidatedSiteNumber());
			assertEquals(consolidatedSiteNumber, clonedDocument.getConsolidatedSiteNumber());
			
			String [] ignoreMethods = {"getGlobalArtifactIdentifier", "getDocumentUrn", 
					"getSiteNumber", "getRepositoryId"};
			compareObjects(document, clonedDocument, ignoreMethods);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			fail(ex.getMessage());
		}
		
	}

}
