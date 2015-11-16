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
import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.exchange.enums.ObjectOrigin;
import gov.va.med.imaging.exchange.enums.ObjectStatus;

import org.junit.Test;
import static org.junit.Assert.* ;

/**
 * @author vhaiswwerfej
 *
 */
public class ImageCloneTest 
extends AbstractCloneTest
{
	
	@Test
	public void testImageClone()
	{
		try
		{
			String siteNumber = "660";
			String imageId = "image123";
			String studyId = "study123";
			String patientId = "pat123";
			String modality = "CT";
			String consolidatedSiteNumber = "con123";
			
			ImageURN imageUrn = ImageURNFactory.create(siteNumber, imageId, studyId, patientId, modality, ImageURN.class);
			Image image = Image.create(imageUrn);
			image.setAbsFilename("absFilename");
			image.setAbsLocation("M");
			image.setAlienSiteNumber("alien123");
			image.setBigFilename("bigFilename");
			image.setCaptureDate(new Date());
			image.setConsolidatedSiteNumber(consolidatedSiteNumber);
			image.setDescription("description");
			image.setDicomImageNumberForDisplay("dicomNumber");
			image.setDicomSequenceNumberForDisplay("dicomSequenceNumber");
			image.setDocumentDate(new Date());
			image.setErrorMessage("errorMessage");
			image.setFullFilename("fullFilename");
			image.setFullLocation("O");
			image.setImageClass("imageClass");
			image.setImageNumber("imageNumber");
			image.setImageStatus(ObjectStatus.DELETED);
			image.setImageUid("uid");
			image.setImageViewStatus(ObjectStatus.QA_REVIEWED);
			image.setImgType(123);
			image.setObjectOrigin(ObjectOrigin.VA);
			image.setPatientDFN("dfn");
			image.setPatientName("patName");
			image.setProcedure("procedure");
			image.setProcedureDate(new Date());
			image.setQaMessage("qaMesssage");
			image.setSensitive(true);
			image.setSiteAbbr("siteAbbr");
			
			Image clonedImage = image.cloneWithConsolidatedSiteNumber();
			assertEquals(imageUrn, image.getImageUrn());
			assertEquals(siteNumber, image.getSiteNumber());
			assertEquals(consolidatedSiteNumber, image.getConsolidatedSiteNumber());
			assertEquals(consolidatedSiteNumber, clonedImage.getConsolidatedSiteNumber());
			assertEquals(consolidatedSiteNumber, clonedImage.getSiteNumber());
			assertEquals(siteNumber, image.getRepositoryUniqueId());
			assertEquals(consolidatedSiteNumber, clonedImage.getRepositoryUniqueId());
			assertEquals(imageId, image.getIen());
			assertEquals(imageId, clonedImage.getIen());
			assertEquals(studyId, image.getStudyIen());
			assertEquals(studyId, clonedImage.getStudyIen());
			assertEquals(patientId, image.getPatientId());
			assertEquals(patientId, clonedImage.getPatientId());
			String [] ignoreMethods = {"getImageUrn", "getSiteNumber", "isEquivalent", 
					"isIncluding", "getRepositoryUniqueId", "getGlobalArtifactIdentifier"};
			compareObjects(image, clonedImage, ignoreMethods);
		
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			fail(ex.getMessage());
		}
	}

}
