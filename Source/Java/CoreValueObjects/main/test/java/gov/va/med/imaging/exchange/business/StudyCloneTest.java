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

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import org.junit.Test;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.StudyURNFactory;
import gov.va.med.imaging.StudyURN;
import gov.va.med.imaging.exchange.enums.ObjectStatus;
import gov.va.med.imaging.exchange.enums.StudyDeletedImageState;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;

import static org.junit.Assert.* ;

/**
 * @author vhaiswwerfej
 *
 */
public class StudyCloneTest
extends AbstractCloneTest
{
	@Test
	public void testStudyClone()
	{
		String consolidatedSiteNumber = "con1";
		String siteNumber = "660";
		String studyId = "123";
		String patientIcn = "pat123";
		
		try
		{
			GlobalArtifactIdentifier gai = StudyURNFactory.create(siteNumber, studyId, patientIcn, StudyURN.class);
			
			Study study = Study.create(gai, StudyLoadLevel.STUDY_ONLY, StudyDeletedImageState.includesDeletedImages);
			study.setConsolidatedSiteNumber(consolidatedSiteNumber);
			study.setAlienSiteNumber("alien123");
			study.setCaptureBy("dr smith");
			study.setCaptureDate("2011-02-23");
			study.setCptCode("cpt");
			study.setDescription("description");
			study.setDocumentDate(new Date());
			study.setErrorMessage("error message");
			study.setEvent("event");
			study.setFirstImageIen("image123");
			study.setImageCount(23);
			study.setImagePackage("imgPackage");
			study.setImageType("imageType");
			study.setNoteTitle("note title");
			study.setOrigin("origin");
			study.setPatientName("patient Name");
			study.setProcedure("procedure");
			study.setProcedureDate(new Date());
			study.setProcedureDateString("2011-02-23");
			study.setRadiologyReport("report");
			study.setRpcResponseMsg("rpcResponse");
			study.setSensitive(true);
			study.setSiteAbbr("siteAbbr");
			study.setSiteName("siteName");
			study.setSpecialty("specialty");
			study.setStudyClass("studyClass");
			study.setStudyStatus(ObjectStatus.CONTROLLED);
			study.setStudyUid("uid123");
			study.setStudyViewStatus(ObjectStatus.IN_PROGRESS);
			Site testSite = new TestSite();
			Study clonedStudy = study.cloneWithConsolidatedSiteNumber(testSite);
			assertEquals(study.getStudyIen(), clonedStudy.getStudyIen());
			assertEquals(siteNumber, study.getSiteNumber());
			assertEquals(consolidatedSiteNumber, study.getConsolidatedSiteNumber());
			assertEquals(consolidatedSiteNumber, clonedStudy.getConsolidatedSiteNumber());
			assertEquals(consolidatedSiteNumber, clonedStudy.getSiteNumber());
			assertEquals(gai, clonedStudy.getAlternateArtifactIdentifier());
			assertEquals(gai, study.getGlobalArtifactIdentifier());
			compareStudies(study, clonedStudy);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			fail(ex.getMessage());	
		}
	}
	
	private void compareStudies(Study study1, Study study2)
	throws InvocationTargetException, IllegalAccessException
	{
		String [] ignoreMethods = {"getSiteNumber", "getSiteName", "getGlobalArtifactIdentifier", 
				"getChildSeries", "getValueAsInt", "getStudyUrn", "getAlternateArtifactIdentifier",
				"getValue", "getKeys"};
		compareObjects(study1, study2, ignoreMethods);		
	}
	
	

}
