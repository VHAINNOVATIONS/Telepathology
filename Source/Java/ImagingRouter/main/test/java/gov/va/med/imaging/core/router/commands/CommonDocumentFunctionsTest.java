/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 1, 2011
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
package gov.va.med.imaging.core.router.commands;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import gov.va.med.imaging.exchange.business.documents.Document;
import gov.va.med.imaging.exchange.business.documents.DocumentSet;
import gov.va.med.imaging.exchange.business.documents.DocumentSetResult;
import gov.va.med.imaging.exchange.business.documents.test.DocumentBusinessObjectBuilder;

import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.router.commands.documents.CommonDocumentFunctions;
import gov.va.med.imaging.test.ObjectComparer;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author vhaiswwerfej
 *
 */
public class CommonDocumentFunctionsTest
extends AbstractCommonFunctionsTest
{
	
	@Test
	public void testUpdatingConsolidatedSiteInDocumentSet()
	throws Exception
	{
		DocumentSetResult documentSetResult = null;
		Site consolidatedSite = getConsolidatedSite();
		SortedSet<DocumentSet> documentSets = new TreeSet<DocumentSet>();
		DocumentSet documentSet = 
			DocumentBusinessObjectBuilder.createDocumentSet(consolidatedSite.getSiteNumber());
		documentSets.add(documentSet);
		String siteNumber = documentSet.getRepositoryId();
		documentSetResult = DocumentSetResult.createFullResult(documentSets);			
		
		DocumentSetResult updatedDocumentSetResult =
			CommonDocumentFunctions.updateConsolidatedSitesInDocumentSetResult(documentSetResult, 
				getCommandContext());
		
		DocumentSet updatedDocumentSet = updatedDocumentSetResult.getArtifacts().first();
		
		assertEquals(siteNumber, documentSet.getRepositoryId());
		assertEquals(consolidatedSite.getSiteNumber(), updatedDocumentSet.getRepositoryId());
		
		String [] ignoreMethods = {"getRepositoryId"};
		ObjectComparer.compareObjects(documentSet, updatedDocumentSet, ignoreMethods);
		compareDocuments(documentSet, updatedDocumentSet, siteNumber, consolidatedSite);
	}
	
	private void compareDocuments(DocumentSet ds1, DocumentSet ds2, 
			String siteNumber, Site consolidatedSite)
	throws Exception
	{
		Iterator<Document> document1Iter = ds1.iterator();
		Iterator<Document> document2Iter = ds2.iterator();
		while(document1Iter.hasNext())
		{
			Document doc1 = document1Iter.next();
			Document doc2 = document2Iter.next();
			assertEquals(siteNumber, doc1.getSiteNumber());
			assertEquals(siteNumber, doc1.getRepositoryId());
			assertEquals(consolidatedSite.getSiteNumber(), doc2.getSiteNumber());
			assertEquals(consolidatedSite.getSiteNumber(), doc2.getRepositoryId());
			assertEquals(doc1.getGlobalArtifactIdentifier().getDocumentUniqueId(), 
					doc2.getGlobalArtifactIdentifier().getDocumentUniqueId());
			assertEquals(doc1.getGlobalArtifactIdentifier().getHomeCommunityId(), 
					doc2.getGlobalArtifactIdentifier().getHomeCommunityId());
			assertEquals(doc1.getDocumentUrn().getDocumentId(),
					doc2.getDocumentUrn().getDocumentId());
			assertEquals(doc1.getDocumentUrn().getDocumentSetId(),
					doc2.getDocumentUrn().getDocumentSetId());
			assertEquals(doc1.getDocumentUrn().getImageId(),
					doc2.getDocumentUrn().getImageId());
			assertEquals(doc1.getDocumentUrn().getImageModality(),
					doc2.getDocumentUrn().getImageModality());
			assertEquals(doc1.getDocumentUrn().getPatientId(),
					doc2.getDocumentUrn().getPatientId());
			
			String [] ignoreMethods = {"getSiteNumber", "getRepositoryId", 
					"getGlobalArtifactIdentifier", "getDocumentUrn"};
			ObjectComparer.compareObjects(doc1, doc2, ignoreMethods);
		}
		
	}

}
