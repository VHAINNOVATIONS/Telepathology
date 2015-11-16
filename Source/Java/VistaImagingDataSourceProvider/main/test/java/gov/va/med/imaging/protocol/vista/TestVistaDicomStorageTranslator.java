package gov.va.med.imaging.protocol.vista;

import gov.va.med.imaging.exchange.business.dicom.InstanceFile;
import gov.va.med.imaging.exchange.business.dicom.SOPInstance;
import gov.va.med.imaging.exchange.business.dicom.Series;
import gov.va.med.imaging.exchange.business.dicom.Study;
import gov.va.med.imaging.vistaimagingdatasource.dicom.storage.VistaImagingDicomStorageUtility;
import junit.framework.TestCase;

public class TestVistaDicomStorageTranslator 
extends TestCase
{
	public void testTranslatePatientStudyLookupResults() {
		System.out.println("OK");
	}

	public void testTranslateUIDCheckResults()
	{
	}

	public void testTranslateFindPatientRef()
	{
	}

	public void testTranslateFindProcedureRef()
	{
	}

	public void testTranslateFindStudy()
	{
	}

	public void testTranslateFindSeries()
	{
	}

	public void testTranslateStorePatientRef()
	{
	}

	public void testTranslateAttachProcedureRef()
	{
	}

//	public void testTranslateAttachStudy()
//	{
//		Study study = new Study("","","","","","","","","","");
//		String testString="0~~50";
//		
//		VistaImagingDicomStorageUtility.translateAttachStudy(study, testString);
//		
//		assertEquals("50", study.getIEN());
//	}
//
//	public void testTranslateAttachSeries()
//	{
//		Series series = new Series("","","","","","","","","","","","","","","","","","");
//		String testString="0~~50";
//		
//		VistaImagingDicomStorageUtility.translateAttachSeries(series, testString);
//		
//		assertEquals("50", series.getIEN());
//	}
//
//	public void testTranslateAttachSOPInstance()
//	{
//		SOPInstance sopInstance = new SOPInstance("","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","");
//		String testString="0~~50";
//		
//		VistaImagingDicomStorageUtility.translateAttachSOPInstance(sopInstance, testString);
//		
//		assertEquals("50", sopInstance.getIEN());
//	}
//
//	public void testTranslateAttachInstanceFile()
//	{
//		InstanceFile instanceFile = new InstanceFile("","","","","","","","","","","","","","","","");
//		String testString="0~~50";
//		
//		VistaImagingDicomStorageUtility.translateAttachInstanceFile(instanceFile, testString);
//		
//		assertEquals("50", instanceFile.getIEN());
//	}
}
