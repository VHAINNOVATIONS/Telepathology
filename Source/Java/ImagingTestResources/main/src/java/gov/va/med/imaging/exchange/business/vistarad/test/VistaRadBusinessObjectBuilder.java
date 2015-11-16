/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 28, 2011
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
package gov.va.med.imaging.exchange.business.vistarad.test;

import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.business.vistarad.Exam;
import gov.va.med.imaging.exchange.business.vistarad.ExamImage;
import gov.va.med.imaging.exchange.business.vistarad.ExamImages;
import gov.va.med.imaging.exchange.enums.vistarad.ExamStatus;

/**
 * @author vhaiswwerfej
 *
 */
public class VistaRadBusinessObjectBuilder
{
	
	public static ExamImages createExamImages(String siteNumber, String consolidatedSiteNumber)
	throws URNFormatException
	{
		// need to put the consolidated site value into the right place so it gets picked out
		String rawHeader = "|||^^^^^^" + consolidatedSiteNumber;
		
		ExamImages examImages = new ExamImages(rawHeader, false);
		examImages.add(createExamImage(siteNumber));
		return examImages;		
	}
	
	public static Exam createExam()
	throws URNFormatException
	{
		Exam exam = Exam.create("660", "exam123", "pat123");
		exam.setCptCode("cpt");
		exam.setExamReport("report!");
		exam.setExamRequisitionReport("requisitionReport!");
		exam.setExamStatus(ExamStatus.INTERPRETED);
		exam.setModality("CT");
		exam.setPatientName("patName");
		exam.setPresentationStateData("asdf");
		exam.setRawHeaderLine1("raw1");
		exam.setRawHeaderLine2("raw2");
		exam.setSiteAbbr("abbr");
		exam.setSiteName("siteName");
		return exam;
	}
	
	public static ExamImage createExamImage()
	throws URNFormatException
	{
		return createExamImage(null);
	}
	
	public static ExamImage createExamImage(String siteNumber)
	throws URNFormatException
	{
		if(siteNumber == null)
			siteNumber = "660";
		ExamImage examImage = ExamImage.create(siteNumber, "image123", "exam123", "pat123");
		examImage.setAlienSiteNumber("alien123");
		examImage.setDiagnosticFilePath("\\diagnostic");
		examImage.setImageInCache(false);
		examImage.setPatientName("patName");
		return examImage;
	}

}
