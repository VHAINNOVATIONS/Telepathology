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

import static org.junit.Assert.*;
import gov.va.med.imaging.core.interfaces.router.CommandContext;
import gov.va.med.imaging.exchange.business.Image;
import gov.va.med.imaging.exchange.business.Series;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.business.StudySetResult;
import gov.va.med.imaging.router.commands.CommonStudyFunctions;
import gov.va.med.imaging.test.ObjectComparer;
import gov.va.med.imaging.translator.test.TranslatorTestBusinessObjectBuilder;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

/**
 * @author vhaiswwerfej
 *
 */
public class CommonStudyFunctionsTest
extends AbstractCommonFunctionsTest
{
	@Test	
	public void testUpdateStudySet()
	throws Exception
	{
		SortedSet<Study> studies = new TreeSet<Study>();
		Site site = getLocalSite();
		Site consolidatedSite = getConsolidatedSite();
		Study study = TranslatorTestBusinessObjectBuilder.createStudy(site, 
				consolidatedSite.getSiteNumber());		
		study.setConsolidatedSiteNumber(consolidatedSite.getSiteNumber());
		studies.add(study);
		StudySetResult studySetResult = StudySetResult.createFullResult(studies);
				
		CommandContext commandContext = getCommandContext();
		StudySetResult updatedStudySetResult = 
			CommonStudyFunctions.updateConsolidatedSitesInStudySetResult(studySetResult, 
					commandContext);
		
		assertEquals(1, studySetResult.getArtifactSize());
		assertEquals(studySetResult.getArtifactSize(), updatedStudySetResult.getArtifactSize());
		
		
		Study updatedStudy = updatedStudySetResult.getArtifacts().first();
		assertEquals(consolidatedSite.getSiteNumber(), updatedStudy.getSiteNumber());
		assertEquals(site.getSiteNumber(), study.getSiteNumber());
		assertNotSame(consolidatedSite.getSiteNumber(), study.getSiteNumber());
		
		assertEquals(consolidatedSite.getSiteName(), updatedStudy.getSiteName());
		assertEquals(site.getSiteName(), study.getSiteName());
		assertEquals(study.getGlobalArtifactIdentifier(), updatedStudy.getAlternateArtifactIdentifier());
		
		assertNotSame(0, study.getImageCount());
		Image firstImage = study.getFirstImage();
		Image updatedFirstImage = updatedStudy.getFirstImage();
		
		assertEquals(firstImage.getIen(), updatedFirstImage.getIen());
		assertEquals(site.getSiteNumber(), firstImage.getSiteNumber());
		assertEquals(consolidatedSite.getSiteNumber(), updatedFirstImage.getSiteNumber());
		
		String [] ignoreMethods = {"getSiteNumber", "getSiteName", 
				"getGlobalArtifactIdentifier", "getStudyUrn", 
				"getAlternateArtifactIdentifier", "getKeys", "getFirstImage"};
		ObjectComparer.compareObjects(study, updatedStudy, ignoreMethods);
		compareStudyImages(study, updatedStudy, site, consolidatedSite);
	}
	
	private void compareStudyImages(Study s1, Study s2, Site site, Site consolidatedSite)
	throws Exception
	{
		Iterator<Series> series1Iter = s1.iterator();
		Iterator<Series> series2Iter = s2.iterator();
		while(series1Iter.hasNext())
		{
			Series series1 = series1Iter.next();
			Series series2 = series2Iter.next();
			assertEquals(series1.getImageCount(), series2.getImageCount());
			Iterator<Image> image1Iter = series1.iterator();
			Iterator<Image> image2Iter = series2.iterator();
			while(image1Iter.hasNext())// && image2Iter.hasNext())
			{
				assertTrue(image2Iter.hasNext());
				image1Iter.next(); // still need to call this even if it isn't used (to move the iterator)
				Image i2 = image2Iter.next();
				
				// the image pointer in the old study gets updated so it points to the new image with the consolidated site number
				//assertEquals(site.getSiteNumber(), i1.getSiteNumber());
				
				assertEquals(consolidatedSite.getSiteNumber(), i2.getSiteNumber());
				
				//String [] ignoreMethods = {};
				//ObjectComparer.compareObjects(i1, i2, ignoreMethods);
			}
		}
	}
	
	

}
