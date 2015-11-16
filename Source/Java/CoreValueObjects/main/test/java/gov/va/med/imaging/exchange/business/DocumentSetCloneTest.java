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

import static org.junit.Assert.* ;

import java.util.Date;

import gov.va.med.OID;
import gov.va.med.WellKnownOID;
import gov.va.med.imaging.exchange.business.documents.DocumentSet;

import org.junit.Test;

/**
 * @author vhaiswwerfej
 *
 */
public class DocumentSetCloneTest
extends AbstractCloneTest
{
	
	@Test
	public void testCloneDocumentSet()
	{
		try
		{
			OID homeCommunityId = WellKnownOID.VA_DOCUMENT.getCanonicalValue();
			String repositoryId = "repo123";
			String documentSetId = "docSet123";
			String consolidatedSiteNumber = "con123";
			DocumentSet documentSet = new DocumentSet(homeCommunityId, repositoryId, documentSetId);
			documentSet.setConsolidatedSiteNumber(consolidatedSiteNumber);
			documentSet.setAcquisitionDate(new Date());
			documentSet.setAlienSiteNumber("alien123");
			documentSet.setClinicalType("clinicalType");
			documentSet.setErrorMessage("errorMessage");
			documentSet.setFirstImageIen("firstImageIen");
			documentSet.setPatientIcn("pat123");
			documentSet.setPatientName("patName");
			documentSet.setProcedureDate(new Date());
			documentSet.setRpcResponseMsg("rpcResponse");
			documentSet.setSiteAbbr("siteAbbr");
			documentSet.setSiteName("siteName");
			
			DocumentSet clonedDocumentSet = 
				documentSet.cloneWithConsolidatedSiteNumber(new TestSite());
			
			assertEquals(repositoryId, documentSet.getRepositoryId());
			assertEquals(consolidatedSiteNumber, clonedDocumentSet.getRepositoryId());
			assertEquals(consolidatedSiteNumber, documentSet.getConsolidatedSiteNumber());
			assertEquals(consolidatedSiteNumber, clonedDocumentSet.getConsolidatedSiteNumber());			
			
			String [] ignoreMethods = {"getRepositoryId"};
			compareObjects(documentSet, clonedDocumentSet, ignoreMethods);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			fail(ex.getMessage());
		}
	}

}
