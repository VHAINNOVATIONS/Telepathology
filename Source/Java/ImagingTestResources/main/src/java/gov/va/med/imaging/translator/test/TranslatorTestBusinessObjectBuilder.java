/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 20, 2008
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
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
package gov.va.med.imaging.translator.test;

import gov.va.med.PatientIdentifier;
import gov.va.med.imaging.business.TestSite;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.ImageAccessLogEvent;
import gov.va.med.imaging.exchange.ImageAccessLogEvent.ImageAccessLogEventType;
import gov.va.med.imaging.exchange.business.Image;
import gov.va.med.imaging.exchange.business.Patient;
import gov.va.med.imaging.exchange.business.Series;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.business.Patient.PatientSex;
import gov.va.med.imaging.exchange.enums.ObjectOrigin;
import gov.va.med.imaging.exchange.enums.StudyDeletedImageState;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;

import java.util.Date;
import java.util.Random;

/**
 * This tool creates test business objects for use in unit tests. This should NOT be used
 * in any production code
 * 
 * @author VHAISWWERFEJ
 *
 */
public class TranslatorTestBusinessObjectBuilder 
{
	
	private final static Random randomGenerator = new Random(System.currentTimeMillis());
	
	// identifier values used to create consistent fake data
	private final static String STUDY_IEN = "42";
	private final static String PATIENT_ICN = "655321";
	private final static String PATIENT_DFN = "123556";
	private final static int SERIES_IEN_START = 100;
	private final static int SERIES_COUNT = 3;
	private final static int SERIES_NUMBER_START = 1;
	private final static int IMAGE_COUNT = 50;
	private final static int IMAGE_IEN_START = 1000;
	private final static int IMAGE_UID_START = 2000;
	private final static int IMAGE_DISPLAY_START = 1;
	
	private static int seriesIen = SERIES_IEN_START;
	private static String getNextSeriesIen(){return "" + seriesIen++;}
	private static int seriesNumber = SERIES_NUMBER_START;
	private static String getNextSeriesNumber(){return "" + seriesNumber++;}
	private static int imageIen = IMAGE_IEN_START;
	private static String getNextImageIen(){return "" + imageIen++;}
	private static int imageUid = IMAGE_UID_START;
	private static String getNextImageUid(){return "" + imageUid++;}
	private static int getImageType(){return 42;}
	private static int dicomImageForDisplay = IMAGE_DISPLAY_START;
	private static String getNextDicomImageForDisplay(){return "" + dicomImageForDisplay++;}
	
	public static Site createSite()
	{
		return new TestSite("Test Site Name", "123", "Abbr");		
	}
	
	public static Study createStudy(Site site) 
	throws URNFormatException
	{
		return createStudy(site, null);
	}
	
	@SuppressWarnings("deprecation")
	public static Study createStudy(Site site, String consolidatedSiteNumber) 
	throws URNFormatException
	{
		Study study = Study.create(ObjectOrigin.DOD, site.getSiteNumber(), 
			STUDY_IEN, PatientIdentifier.icnPatientIdentifier(PATIENT_ICN), StudyLoadLevel.FULL, 
			StudyDeletedImageState.cannotIncludeDeletedImages);
		study.setCaptureBy("CapturedBy");
		study.setCaptureDate("200701141825");
		study.setDescription("Description");
		study.setEvent("Event");
		study.setImagePackage("image package");
		study.setImageType("Image type");
		study.setNoteTitle("Note title");
		study.setOrigin("DOD");
		//study.setPatientIcn(getPositiveRandomNumber() + "");
		study.setPatientName("Patient name");
		study.setProcedure("Procedure");
		study.setProcedureDate(new Date());
		study.setRadiologyReport("Report");
		study.setRpcResponseMsg("rpc response");
		study.setSiteAbbr(site.getSiteAbbr());
		study.setSiteName(site.getSiteName());
		study.setSpecialty("Specialty");
		study.setStudyClass("study class");
		study.setStudyUid("uid.123.study.456");
		study.addModality("CR");
		study.addModality("MR");
		if(consolidatedSiteNumber != null)
			study.setConsolidatedSiteNumber(consolidatedSiteNumber);
		
		Image firstImage = null;
		int seriesCount = SERIES_COUNT;
		for(int i = 0; i < seriesCount; i++)
		{
			Series series = createSeries(study, site, consolidatedSiteNumber);
			study.addSeries(series);
			study.setImageCount(study.getImageCount() + series.getImageCount());
			if(firstImage == null)
			{
				if(series.getImageCount() > 0)
				{
					firstImage = getFirstImage(series); 
				}
			}
		}
		if(firstImage != null)
		{
			study.setFirstImageIen(firstImage.getIen());		
			study.setFirstImage(firstImage);
		}
		return study;
	}
	
	public static Series createSeries(Study study, Site site) 
	throws URNFormatException
	{
		return createSeries(study, site, null);
	}
	
	public static Series createSeries(Study study, Site site, String consolidatedSiteNumber) 
	throws URNFormatException
	{
		Series series = Series.create(ObjectOrigin.DOD, getNextSeriesIen(), getNextSeriesNumber());
		series.setModality("CR");
		series.setSeriesUid("uid.series.123.456");
		//System.out.println("created series with number [" + series.getSeriesNumber() + "]");
		int imgCount = IMAGE_COUNT;
		for(int i = 0; i < imgCount; i++)
		{
			Image image = createImage(study, site, series, consolidatedSiteNumber);
			series.addImage(image);
		}
		return series;
	}
	
	public static Image createImage(Study study, Site site, Series series) 
	throws URNFormatException
	{
		return createImage(study, site, series, null);
	}
	
	public static Image createImage(Study study, Site site, Series series, 
			String consolidatedSiteNumber) 
	throws URNFormatException
	{
		Image image = Image.create(site.getSiteNumber(), getNextImageIen(), study.getStudyIen(), 
				PatientIdentifier.icnPatientIdentifier(PATIENT_ICN), series.getModality());		
		image.setAbsFilename("\\\\server\\share\\file.abs");
		image.setAbsLocation("A");
		image.setBigFilename("\\\\server\\share\\file.big");
		image.setDescription("Image description");
		image.setDicomImageNumberForDisplay(getNextDicomImageForDisplay());
		//image.setDicomSequenceNumberForDisplay(getPositiveRandomNumber() + "");
		image.setDicomSequenceNumberForDisplay(series.getSeriesNumber());
		image.setFullFilename("\\\\server\\share\\file.tga");
		image.setFullLocation("M");
		//image.setGroupIen(series.getSeriesIen());
		image.setImageClass("image class");
		//image.setImageModality("CR");
		image.setImageNumber(image.getDicomImageNumberForDisplay());
		image.setImageUid(getNextImageUid());
		image.setImgType(getImageType());
		image.setObjectOrigin(ObjectOrigin.DOD);
		image.setPatientDFN(PATIENT_DFN);
		image.setPatientName("Patient name");
		image.setProcedure("Procedure");
		image.setProcedureDate(new Date());
		image.setQaMessage("qa message");
		image.setSiteAbbr(site.getSiteAbbr());
		//image.setSiteNumber(site.getSiteNumber());		
		if(consolidatedSiteNumber != null)
			image.setConsolidatedSiteNumber(consolidatedSiteNumber);
		return image;
	}
	
	private static Image getFirstImage(Series series)
	{
		if(series.getImageCount() <= 0)
			return null;
		for(Image image : series)
		{
			return image;
		}
		return null;
	}
	
	public static Patient createPatient()
	{
		return new Patient("patName", "icn", "retired", 
				PatientSex.Male, new Date(), "123456789", null, null);
	}
	
	public static ImageAccessLogEvent createImageAccessLogEvent()
	{
		ImageAccessLogEvent imageAccessLogEvent =
			new ImageAccessLogEvent("imageIen", "dfn", "icn", "siteNumber", 
					System.currentTimeMillis(), "reason", "description", 
					ImageAccessLogEventType.IMAGE_COPY, false, "660");
		
		return imageAccessLogEvent;
	}
}
