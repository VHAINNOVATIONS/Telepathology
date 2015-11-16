/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 14, 2008
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
package gov.va.med.imaging.federation.test;

import java.math.BigInteger;
import java.util.Random;

import gov.va.med.imaging.exchange.business.Image;
import gov.va.med.imaging.exchange.business.Series;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.federation.translator.FederationDatasourceTranslator;
import gov.va.med.imaging.federation.webservices.types.FederationInstanceType;
import gov.va.med.imaging.federation.webservices.types.FederationSeriesType;
import gov.va.med.imaging.federation.webservices.types.FederationSeriesTypeComponentInstances;
import gov.va.med.imaging.federation.webservices.types.FederationStudyType;
import gov.va.med.imaging.federation.webservices.types.FederationStudyTypeComponentSeries;
import gov.va.med.imaging.federation.webservices.types.FederationStudyTypeStudyModalities;
import gov.va.med.imaging.federation.webservices.types.ObjectOriginType;

/**
 * @author VHAISWWERFEJ
 *
 */
public class FederationTranslatorTest 
extends FederationTestBase 
{
	FederationDatasourceTranslator translator = new FederationDatasourceTranslator();
	
	Random randomGenerator = new Random(System.currentTimeMillis());
	
	public FederationTranslatorTest()
	{
		super();
	}
	
	public void testTransformStudy()
	{
		FederationStudyType federationStudy = createFederationStudyType();
		Study study = translator.transformStudy(federationStudy);
		compareStudy(study, federationStudy);
	}
	
	private void compareStudy(Study study, FederationStudyType federationStudy)
	{
		assertNotNull(study);
		assertNotNull(federationStudy);
		
		assertEquals(study.getCaptureBy(), federationStudy.getCapturedBy());
		assertEquals(study.getCaptureDate(), federationStudy.getCaptureDate());
		assertEquals(study.getDescription(), federationStudy.getDescription());
		assertEquals(study.getEvent(), federationStudy.getEvent());
		// not a necessary test, business resorts the studies based on date
		//assertEquals(study.getFirstImageIen(), federationStudy.getFirstImageIen() == null ? "" : federationStudy.getFirstImageIen());
		assertEquals(study.getImageCount(), federationStudy.getImageCount());
		assertEquals(study.getImagePackage(), federationStudy.getImagePackage());
		assertEquals(study.getImageType(), federationStudy.getImageType());
		
		//TODO: compare modalities in studies
		//assertEquals(studyModalityString, federationModalityString );
		assertEquals(study.getNoteTitle(), federationStudy.getNoteTitle());
		assertEquals(study.getObjectOrigin(), translator.transformObjectOrigin(federationStudy.getObjectOrigin()));
		assertEquals(study.getOrigin(), federationStudy.getOrigin());
		assertEquals(study.getPatientId(), federationStudy.getPatientIcn());
		assertEquals(study.getPatientName(), federationStudy.getPatientName());
		assertEquals(study.getProcedure(), federationStudy.getProcedureDescription());
		assertEquals(study.getProcedureDate(), translator.convertDICOMDateToDate(federationStudy.getProcedureDate()));
		assertEquals(study.getRadiologyReport(), federationStudy.getRadiologyReport());
		assertEquals(study.getRpcResponseMsg(), federationStudy.getRpcResponseMsg());
		assertEquals(study.getSiteAbbr(), federationStudy.getSiteAbbreviation());
		assertEquals(study.getSiteName(), federationStudy.getSiteName());
		assertEquals(study.getSiteNumber(), federationStudy.getSiteNumber());
		assertEquals(study.getSpecialty(), federationStudy.getSpecialtyDescription());
		assertEquals(study.getStudyClass(), federationStudy.getStudyClass());
		assertEquals(study.getStudyIen(), federationStudy.getStudyId());
		assertEquals(study.getStudyUid(), federationStudy.getDicomUid());
		
		assertEquals(study.getImageCount(), federationStudy.getImageCount());
		// compare first image
		// compare series
		
		int i = 0; 
		for(Series series : study.getSeries())
		{
			compareSeries(series, findSeries(federationStudy.getComponentSeries().getSeries(), series.getSeriesIen()));
			i++;
		}
		
	}
	
	private FederationSeriesType findSeries(FederationSeriesType [] series, String seriesIen)
	{
		for(int i = 0; i < series.length; i++)
		{
			if(series[i].getSeriesId().equals(seriesIen))
			{
				return series[i];
			}
		}
		return null;		
	}
	
	private FederationInstanceType findInstance(FederationInstanceType[] instances, String imageIen)
	{
		for(int i = 0; i < instances.length; i++)
		{
			if(instances[i].getImageId().equals(imageIen))
			{
				return instances[i];
			}
		}
		return null;
	}
	
	private void compareSeries(Series series, FederationSeriesType federationSeries)
	{
		assertNotNull(series);
		assertNotNull(federationSeries);
		
		System.out.println("Comparing series [" + series.getSeriesIen() + "]");
		assertEquals(series.getImageCount(), federationSeries.getImageCount());
		assertEquals(series.getModality(), federationSeries.getSeriesModality());
		assertEquals(series.getObjectOrigin(), translator.transformObjectOrigin(federationSeries.getObjectOrigin()));
		assertEquals(series.getSeriesIen(), federationSeries.getSeriesId());
		assertEquals(series.getSeriesNumber(), federationSeries.getDicomSeriesNumber() + "");
		assertEquals(series.getSeriesUid(), federationSeries.getDicomUid());
		
		for(Image image : series)
		{
			FederationInstanceType instance = findInstance(federationSeries.getComponentInstances().getInstance(), image.getIen());
			compareImages(image, instance);
		}
		// compare images
	}
	
	private void compareImages(Image image, FederationInstanceType federationInstance)
	{
		assertNotNull(image);
		assertNotNull(federationInstance);
		System.out.println("Comparing image [" + image.getIen() + "]");
		assertEquals(image.getAbsFilename(), federationInstance.getAbsImageFilename());
		assertEquals(image.getAbsLocation(), federationInstance.getAbsLocation());
		assertEquals(image.getBigFilename(), federationInstance.getBigImageFilename());
		assertEquals(image.getDescription(), federationInstance.getDescription());
		assertEquals(image.getDicomImageNumberForDisplay(), federationInstance.getDicomImageNumberForDisplay());
		assertEquals(image.getDicomSequenceNumberForDisplay(), federationInstance.getDicomSequenceNumberForDisplay());
		assertEquals(image.getFullFilename(), federationInstance.getFullImageFilename());
		assertEquals(image.getFullLocation(), federationInstance.getFullLocation());
		assertEquals(image.getIen(), federationInstance.getImageId());
		assertEquals(image.getImageClass(), federationInstance.getImageClass());
		assertEquals(image.getImageModality(), federationInstance.getImageModality());
		assertEquals(image.getImageUid(), federationInstance.getDicomUid());
		assertEquals(image.getImgType(), federationInstance.getImageType().intValue());
		assertEquals(image.getObjectOrigin(), translator.transformObjectOrigin(federationInstance.getObjectOrigin()));
		assertEquals(image.getPatientId(), federationInstance.getPatientIcn());
		assertEquals(image.getPatientName(), federationInstance.getPatientName());
		assertEquals(image.getProcedure(), federationInstance.getProcedure());
		assertEquals(image.getProcedureDate(), translator.convertDICOMDateToDate(federationInstance.getProcedureDate()));
		assertEquals(image.getQaMessage(), federationInstance.getQaMessage());
		assertEquals(image.getSiteAbbr(), federationInstance.getSiteAbbr());
		assertEquals(image.getSiteNumber(), federationInstance.getSiteNumber());
		assertEquals(image.getStudyIen(), federationInstance.getStudyId());
	}
	
	private FederationSeriesType createFederationSeriesType()
	{
		FederationSeriesType series = new FederationSeriesType();
		
		int imgCount = 20;
		
		series.setDescription("Series description");
		series.setDicomSeriesNumber(new Integer(42));
		series.setDicomUid("12");
		series.setImageCount(imgCount);
		series.setObjectOrigin(ObjectOriginType.DOD);
		series.setSeriesId("1");
		series.setSeriesModality("CT");
		
		FederationSeriesTypeComponentInstances componentInstances = new FederationSeriesTypeComponentInstances();
		FederationInstanceType [] instances = new FederationInstanceType[imgCount];
		for(int i = 0; i < imgCount; i++)
		{
			instances[i] = createFederationInstanceType();
		}
		componentInstances.setInstance(instances);
		series.setComponentInstances(componentInstances);
		return series;
	}
	
	private FederationInstanceType createFederationInstanceType()
	{
		FederationInstanceType instance = new FederationInstanceType();
		
		instance.setAbsImageFilename("\\\\server\\share\\filename.abs");
		instance.setAbsLocation("M");
		instance.setBigImageFilename("\\\\server\\share\\filename.big");
		instance.setDescription("Image description");
		instance.setDicomImageNumberForDisplay("1");
		instance.setDicomSequenceNumberForDisplay("1");
		instance.setDicomUid("43.random.uid");
		instance.setFullImageFilename("\\\\server\\share\\filename.tga");
		instance.setFullLocation("W");
		instance.setGroupId("12");
		instance.setImageClass("image class");
		instance.setImageId("99");
		instance.setImageModality("CT");
		instance.setImageNumber(new Integer(42));
		instance.setImageType(new BigInteger("99"));
		instance.setObjectOrigin(ObjectOriginType.VA);
		instance.setPatientIcn("655321");
		instance.setPatientName("Patient,Name");
		instance.setProcedure("Procedure");
		instance.setProcedureDate("200708140512");
		instance.setQaMessage("QA Messsage");
		instance.setSiteAbbr("SLC");
		instance.setSiteNumber("999");
		instance.setStudyId("987654321");		
		return instance;
	}

	private FederationStudyType createFederationStudyType()
	{
		FederationStudyType study = new FederationStudyType();
		study.setCaptureDate("100120071301");
		study.setCapturedBy("Capture User");
		study.setDescription("Study description");
		study.setDicomUid("123.dicom.uid.456");
		study.setEvent("CT");
		study.setImagePackage("RADIOLOGY");
		study.setImageType("IMAGE");
		study.setNoteTitle("Note title");
		study.setObjectOrigin(ObjectOriginType.VA);
		study.setOrigin("VA");
		study.setPatientIcn("655321");
		study.setPatientName("Patient,Name");
		study.setProcedureDate("100220071422");
		study.setProcedureDescription("Procedure Description");
		study.setRadiologyReport("Radiology Report");
		study.setRpcResponseMsg("1^Response");
		study.setSiteAbbreviation("SLC");
		study.setSiteName("Salt Lake City");
		study.setSiteNumber("660");
		study.setSpecialtyDescription("Specialty");
		study.setStudyClass("Study class");
		study.setStudyId("BFGT0");
		study.setStudyModalities(createFederationStudyModalities());
		study.setStudyPackage("Study package");
		study.setStudyType("Study type");
		
		int seriesCount = 5;
		study.setSeriesCount(seriesCount);
		
		FederationStudyTypeComponentSeries componentSeries = new FederationStudyTypeComponentSeries();
		FederationSeriesType [] series = new FederationSeriesType[seriesCount];
		FederationInstanceType firstImage = null;
		for(int i = 0; i < seriesCount; i++)
		{
			series[i] = createFederationSeriesType();
			if(firstImage == null)
			{
				if(series[i].getImageCount() > 0)
				{
					firstImage = series[i].getComponentInstances().getInstance(0);
				}
			}
		}
		componentSeries.setSeries(series);
		study.setComponentSeries(componentSeries);
		if(firstImage != null)
		{
			study.setFirstImage(firstImage);
			study.setFirstImageIen(firstImage.getImageId());
		}
		return study;
	}
	
	private FederationStudyTypeStudyModalities createFederationStudyModalities()
	{
		FederationStudyTypeStudyModalities modalities = new FederationStudyTypeStudyModalities();
		modalities.setModality(new String[] {"CT", "CR"});
		return modalities;
	}
	
	
}
