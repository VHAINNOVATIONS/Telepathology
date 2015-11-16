package gov.va.med.imaging.vistaimagingdatasource.dicom.storage;


import gov.va.med.imaging.exchange.business.dicom.UIDCheckInfo;
import gov.va.med.imaging.exchange.business.dicom.UIDCheckResult;
import gov.va.med.imaging.url.vista.StringUtils;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class VistaImagingDicomStorageUtilityTests extends TestCase{
	
	protected static final String FIELD_SEPARATOR1 = StringUtils.TILDE; // P34 initialization RPCs result separator


	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testNoUIDErrors(){
		
		String returnValue = "0";
		String studyUID = "1.2.4445.321.555.1";
		String serUID = "1.2.4445.321.555.1.1";
		String sopUID = "1.2.4445.321.555.1.1.1";
		UIDCheckInfo info = new UIDCheckInfo("777", "010203-123", "660", "4", studyUID, serUID, sopUID);
		UIDCheckResult result = VistaImagingDicomStorageUtility.translateUIDCheckResults(returnValue, info.getSeriesInstanceUID(), FIELD_SEPARATOR1);
		
		assertFalse(result.isDuplicateUID());
		assertFalse(result.isFatalError());
		assertFalse(result.isIllegalUID());
		assertFalse(result.isSOPInstanceResend());
	}

	@Test
	public void testDuplicateUIDErrors(){
		
		String returnValue = "1~Duplicate UID~1.2.4445.321.1212.555.1.1";
		String studyUID = "1.2.4445.321.555.1";
		String serUID = "1.2.4445.321.555.1.1";
		String sopUID = "1.2.4445.321.555.1.1.1";
		UIDCheckInfo info = new UIDCheckInfo("777", "010203-123", "660", "4", studyUID, serUID, sopUID);
		UIDCheckResult result = VistaImagingDicomStorageUtility.translateUIDCheckResults(returnValue, info.getSeriesInstanceUID(), FIELD_SEPARATOR1);
		
		assertTrue(result.isDuplicateUID());
		assertFalse(result.isFatalError());
		assertFalse(result.isIllegalUID());
		assertFalse(result.isSOPInstanceResend());
		assertTrue(result.getCorrectedUID().equals("1.2.4445.321.1212.555.1.1"));
	}

	@Test
	public void testRESENDUID(){
		
		String returnValue = "2~RESEND";
		String studyUID = "1.2.4445.321.555.1";
		String serUID = "1.2.4445.321.555.1.1";
		String sopUID = "1.2.4445.321.555.1.1.1";
		UIDCheckInfo info = new UIDCheckInfo("777", "010203-123", "660", "4", studyUID, serUID, sopUID);
		UIDCheckResult result = VistaImagingDicomStorageUtility.translateUIDCheckResults(returnValue, info.getSeriesInstanceUID(), FIELD_SEPARATOR1);
		
		assertFalse(result.isDuplicateUID());
		assertFalse(result.isFatalError());
		assertFalse(result.isIllegalUID());
		assertTrue(result.isSOPInstanceResend());
	}
	
	@Test
	public void testIllegalUID(){
		
		String returnValue = "3~Illegal UID Replacement~1.2.840.113754.1.4.66046732.217113383130306150800";
		String studyUID = "1.2.4445.321.555.1";
		String serUID = "1.2.4445.321.555.1.1";
		String sopUID = "1.2.4445.321.555.1.1.1";
		UIDCheckInfo info = new UIDCheckInfo("777", "010203-123", "660", "4", studyUID, serUID, sopUID);
		UIDCheckResult result = VistaImagingDicomStorageUtility.translateUIDCheckResults(returnValue, info.getSeriesInstanceUID(), FIELD_SEPARATOR1);
		
		assertFalse(result.isDuplicateUID());
		assertFalse(result.isFatalError());
		assertTrue(result.isIllegalUID());
		assertFalse(result.isSOPInstanceResend());
		assertTrue(result.getCorrectedUID().equals("1.2.840.113754.1.4.66046732.217113383130306150800"));
	}
	
	@Test
	public void testFatalError(){
		
		String returnValue = "-1~Fatal Error";
		String studyUID = "1.2.4445.321.555.1";
		String serUID = "1.2.4445.321.555.1.1";
		String sopUID = "1.2.4445.321.555.1.1.1";
		UIDCheckInfo info = new UIDCheckInfo("777", "010203-123", "660", "4", studyUID, serUID, sopUID);
		UIDCheckResult result = VistaImagingDicomStorageUtility.translateUIDCheckResults(returnValue, info.getSeriesInstanceUID(), FIELD_SEPARATOR1);
		
		assertFalse(result.isDuplicateUID());
		assertTrue(result.isFatalError());
		assertFalse(result.isIllegalUID());
		assertFalse(result.isSOPInstanceResend());
	}



}
