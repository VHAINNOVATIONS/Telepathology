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

import gov.va.med.imaging.exchange.business.vistarad.ExamImage;

import org.junit.Test;
import static org.junit.Assert.* ;

/**
 * @author vhaiswwerfej
 *
 */
public class ExamImageCloneTest
extends AbstractCloneTest
{
	@Test
	public void testCloneExamImage()
	{
		
		try
		{
			String consolidatedSiteNumber = "con123";
			String siteNumber = "660";
			String imageId = "123";
			String examId = "exam123";
			String patientIcn = "pat123";
			ExamImage examImage = ExamImage.create(siteNumber, imageId, examId, patientIcn);			
			examImage.setAlienSiteNumber("alien123");
			examImage.setDiagnosticFilePath("diagFilePath");
			examImage.setImageInCache(false);
			examImage.setPatientName("patName");
			
			ExamImage clonedExamImage = examImage.cloneWithConsolidatedSiteNumber(consolidatedSiteNumber);
			
			assertEquals(siteNumber, examImage.getSiteNumber());
			assertEquals(consolidatedSiteNumber, clonedExamImage.getSiteNumber());
			assertEquals(imageId, examImage.getImageId());
			assertEquals(imageId, clonedExamImage.getImageId());
			assertEquals(examId, examImage.getExamId());
			assertEquals(examId, clonedExamImage.getExamId());
			assertEquals(patientIcn, examImage.getPatientIcn());
			assertEquals(patientIcn, clonedExamImage.getPatientIcn());
			
			String [] ignoreMethods = {"getSiteNumber", "getImageUrn", "getRepositoryUniqueId",
					"isEquivalent", "isIncluding"};
			compareObjects(examImage, clonedExamImage, ignoreMethods);
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			fail(ex.getMessage());
		}
		
	}

}
