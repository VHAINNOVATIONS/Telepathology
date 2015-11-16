/**
 * 
 */
package gov.va.med.imaging.protocol.vista;

import gov.va.med.PatientIdentifier;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.business.Image;
import gov.va.med.imaging.exchange.business.PatientSensitiveValue;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.enums.ObjectOrigin;
import gov.va.med.imaging.exchange.enums.PatientSensitivityLevel;
import gov.va.med.imaging.exchange.enums.StudyDeletedImageState;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.protocol.vista.exceptions.VistaParsingException;
import gov.va.med.imaging.url.vista.exceptions.VistaMethodException;
import gov.va.med.imaging.vistaimagingdatasource.VistaImage;
import java.util.List;
import java.util.SortedSet;
import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class TestVistaImagingTranslator
	extends TestCase
{

	/**
	 * Test method for {@link gov.va.med.imaging.protocol.vista.VistaImagingTranslator#convertStringToPatientSensitiveValue(java.lang.String, java.lang.String)}.
	 */
	public void testConvertStringToPatientSensitiveValue()
	{
		try
		{
			VistaImagingTranslator.convertStringToPatientSensitiveValue(null, "655321");
			fail("Simulated null response was not detected.");
		}
		catch (VistaMethodException x){}
		
		try
		{
			VistaImagingTranslator.convertStringToPatientSensitiveValue("-1", "655321");
			fail("Simulated error response was not detected.");
		}
		catch (VistaMethodException x){}
		
		String vistaResponse;
		
		try
		{
			vistaResponse = "0";
			PatientSensitiveValue patientSensitivity = 
				VistaImagingTranslator.convertStringToPatientSensitiveValue(vistaResponse, "655321");
			assertNotNull(patientSensitivity);
			assertEquals(PatientSensitivityLevel.NO_ACTION_REQUIRED, patientSensitivity.getSensitiveLevel());
		}
		catch (VistaMethodException x){fail(x.getMessage());}
		
		try
		{
			vistaResponse = "1";
			PatientSensitiveValue patientSensitivity = 
				VistaImagingTranslator.convertStringToPatientSensitiveValue(vistaResponse, "655321");
			assertNotNull(patientSensitivity);
			assertEquals(PatientSensitivityLevel.DISPLAY_WARNING, patientSensitivity.getSensitiveLevel());
		}
		catch (VistaMethodException x){fail(x.getMessage());}
		
		try
		{
			vistaResponse = "2";
			PatientSensitiveValue patientSensitivity = 
				VistaImagingTranslator.convertStringToPatientSensitiveValue(vistaResponse, "655321");
			assertNotNull(patientSensitivity);
			assertEquals(PatientSensitivityLevel.DISPLAY_WARNING_REQUIRE_OK, patientSensitivity.getSensitiveLevel());
		}
		catch (VistaMethodException x){fail(x.getMessage());}
		
		try
		{
			vistaResponse = "3";
			PatientSensitiveValue patientSensitivity = 
				VistaImagingTranslator.convertStringToPatientSensitiveValue(vistaResponse, "655321");
			assertNotNull(patientSensitivity);
			assertEquals(PatientSensitivityLevel.DISPLAY_WARNING_CANNOT_CONTINUE, patientSensitivity.getSensitiveLevel());
		}
		catch (VistaMethodException x){fail(x.getMessage());}
		
		try
		{
			vistaResponse = "4";
			PatientSensitiveValue patientSensitivity = 
				VistaImagingTranslator.convertStringToPatientSensitiveValue(vistaResponse, "655321");
			assertNotNull(patientSensitivity);
			assertEquals(PatientSensitivityLevel.ACCESS_DENIED, patientSensitivity.getSensitiveLevel());
		}
		catch (VistaMethodException x){fail(x.getMessage());}

	}

	/**
	 * Test method for {@link gov.va.med.imaging.protocol.vista.VistaImagingTranslator#createImageGroupFromImageLines(java.lang.String, gov.va.med.imaging.exchange.business.Study)}.
	 * @throws VistaParsingException 
	 * @throws URNFormatException 
	 */
	public void testParse_RPC_MAG_GET_STUDY_IMAGES() 
	throws VistaParsingException, URNFormatException
	{
		Study study = Study.create(ObjectOrigin.VA, "660", "12345", PatientIdentifier.icnPatientIdentifier("655321"), 
				StudyLoadLevel.FULL, StudyDeletedImageState.cannotIncludeDeletedImages);
		
		// DVB>S MAGIEN=4763
		// DVB>D GROUP^MAGGTIG(.MAGRY,MAGIEN,1)
		// DVB>ZW

		String vistaResponse = 
            "B2^4764^\\\\vhaiswimmixvi1\\image1$\\DM\\00\\47\\DM004764.DCM^\\\\vhaiswimmixvi1\\image1$\\DM\\00\\47\\DM004764.ABS^SPINE ENTIRE AP&LAT (#1)^3030508.0856^100^US^05/08/2003 08:56^^M^A^1^27^1^1^SLC^^^720^PATIENT,SEVENTWOZERO^CLIN^05/09/2003 09:49:15^^^";
		
		SortedSet<VistaImage> vistaImages = VistaImagingTranslator.createImageGroupFromImageLines(vistaResponse, study);
		assertNotNull(vistaImages);
		
		//String vistaResponse = 
		//	 "1^Class: CLIN -\f" + 
		//	 "Item~S2^Site^Note Title~~W0^Proc DT~S1^Procedure^# Img~S2^Short Desc^Pkg^Class^Type^Specialty^Event^Origin^Cap Dt~S1~W0^Cap by~~W0^Image ID~S2~W0\f" +
		//	 "1^WAS^NURSING NOTE^09/28/2001 00:01^NOTE^2^CONSULT NURSE MEDICAL WOUND SPEC INPT^NOTE^CLIN^CONSULT^NURSING^WOUND ASSESSMENT^VA^09/28/2001 01:35^IMAGPROVIDERONETWOSIX,ONETWOSIX^1752|1752^\\\\ISW-IMGGOLDBACK\\image1$\\DM\\00\\17\\DM001753.JPG^\\\\ISW-IMGGOLDBACK\\image1$\\DM\00\\17\\DM001753.ABS^CONSULT NURSE MEDICAL WOUND SPEC INPT^3010928^11^NOTE^09/28/2001^36^M^A^^^2^1^WAS^^^711^IMAGPATIENT1055,1055^CLIN^^^\f" +
		//	 "2^WAS^OPHTHALMOLOGY^08/20/2001 00:01^OPH^10^Ophthalmology^NOTE^CLIN^IMAGE^EYE CARE^^VA^08/20/2001 22:32^IMAGPROVIDERONETWOSIX,ONETWOSIX^1783|1783^\\\\ISW-IMGGOLDBACK\\image1$\\DM\\00\\17\\DM001784.DCM^\\\\ISW-IMGGOLDBACK\\image1$\\DM\\00\\17\\DM001784.ABS^Ophthalmology^3010820^11^OPH^08/20/2001^41^M^A^^^10^1^WAS^^^711^IMAGPATIENT1055,1055^CLIN^^^^";
	}

	/**
	 * Test the parsing of a response from  RPC MAG4 PAT GET IMAGES call
	 * @throws VistaParsingException 
	 */
	public void testParse_MAG4_PAT_GET_IMAGES() 
	throws VistaParsingException
	{
		// RPC MAG4 PAT GET IMAGES
		// S DFN=720
		// DVB>S DFN=720
		// DVB>D PGI^MAGSIXG1(.MAGOUT,DFN)
		// DVB>ZW
		String vistaResponse = 
			"1^All existing images\n" +
			"Item~S2^Site^Note Title~~W0^Proc DT~S1^Procedure^# Img~S2^Short Desc^Pkg^Class^Type^Specialty^Event^Origin^Cap Dt~S1~W0^Cap by~~W0^Image ID~S2~W0\n" + 
			"1^SLC^   ^05/09/2003 14:20^RAD NM^120^UNLISTED RADIOLOGIC PROCEDURE^RAD^CLIN^IMAGE^NUCLEAR MEDICINE^NUCLEAR MEDICINE SCAN^VA^05/09/2003 14:41:09^^5529^|5529^\\\\vhaiswimmixvi1\\image1$\\DM\00\\55\\DM005530.TGA^\\\\vhaiswimmixvi1\\image1$\\DM\00\\55\\DM005530.ABS^UNLISTED RADIOLOGIC PROCEDURE^3030509.142^11^RAD NM^05/09/2003 14:20^^M^A^^^120^1^SLC^^^720^PATIENT,SEVENTWOZERO^CLIN^05/09/2003 14:41:09^^^\n" + 
			"2^SLC^   ^05/08/2003 08:56^RAD US^1^SPINE ENTIRE AP&LAT^RAD^CLIN^IMAGE^RADIOLOGY^ULTRASOUND^VA^05/09/2003 09:49:15^^4764^|4764^\\\\vhaiswimmixvi1\\image1$\\DM\\00\\47\\DM004764.DCM^\\\\vhaiswimmixvi1\\image1$\\DM\\00\\47\\DM004764.ABS^SPINE ENTIRE AP&LAT^3030508.0856^100^RAD US^05/08/2003 08:56^^M^A^^^1^1^SLC^^^720^PATIENT,SEVENTWOZERO^CLIN^05/09/2003 09:49:15^^^\n" + 
			"3^SLC^   ^05/08/2003 08:54^RAD CR^4^CHEST SINGLE VIEW^RAD^CLIN^IMAGE^RADIOLOGY^COMPUTED RADIOGRAPHY^VA^05/08/2003 15:37:17^^4730^|4730^\\\\vhaiswimmixvi1\\image1$\\DM\\00\\47\\DM004731.DCM^\\\\vhaiswimmixvi1\\image1$\\DM\00\\47\\DM004731.ABS^CHEST SINGLE VIEW^3030508.0854^11^RAD CR^05/08/2003 08:54^^M^A^^^4^1^SLC^^^720^PATIENT,SEVENTWOZERO^CLIN^05/08/2003 15:37:17^^^\n" +
			"4^SLC^   ^05/08/2003 08:54^RAD CR^4^CHEST 2 VIEWS PA&LAT^RAD^CLIN^IMAGE^RADIOLOGY^COMPUTED RADIOGRAPHY^VA^05/08/2003 14:22:43^^3730^|3730^\\\\vhaiswimmixvi1\\image1$\\DM\\00\\37\\DM003731.TGA^\\\\vhaiswimmixvi1\\image1$\\DM\\00\\37\\DM003731.ABS^CHEST 2 VIEWS PA&LAT^3030508.0854^11^RAD CR^05/08/2003 08:54^^M^A^^^4^1^SLC^^\\vhaiswimmixvi1\\image1$\\DM\\00\\37\\DM003731.BIG^720^PATIENT,SEVENTWOZERO^CLIN^05/08/2003 14:22:43^^^\n" +
			"5^SLC^   ^05/08/2003 08:39^RAD NM^2^ECHOGRAM PELVIC B-SCAN &/OR REAL TIME W/IMAGING^RAD^CLIN^IMAGE^NUCLEAR MEDICINE^NUCLEAR MEDICINE SCAN^VA^05/09/2003 14:00:22^^5526^|5526^\\\\vhaiswimmixvi1\\image1$\\DM\\00\\55\\DM005527.DCM^\\\\vhaiswimmixvi1\\image1$\\DM\\00\\55\\DM005527.ABS^ECHOGRAM PELVIC B-SCAN &/OR REAL TIME W/IMAGING^3030508.0839^11^RAD NM^05/08/2003 08:39^^M^A^^^2^1^SLC^^^720^PATIENT,SEVENTWOZERO^CLIN^05/09/2003 14:00:22^^^\n" +
			"6^SLC^   ^05/08/2003 08:38^RAD MR^156^MAGNETIC IMAGE,BRAIN STEM^RAD^CLIN^IMAGE^RADIOLOGY^MAGNETIC RESONANCE SCAN^VA^05/09/2003 11:48:58^^4926^|4926^\\\\vhaiswimmixvi1\\image1$\\DM\\00\\49\\DM004927.TGA^\\\\vhaiswimmixvi1\\image1$\\DM\\00\\49\\DM004927.ABS^MAGNETIC IMAGE,BRAIN STEM^3030508.0838^11^RAD MR^05/08/2003 08:38^^M^A^^^156^1^SLC^^^720^PATIENT,SEVENTWOZERO^CLIN^05/09/2003 11:48:58^^^\n" + 
			"7^SLC^   ^05/08/2003 08:38^RAD MR^156^MAGNETIC IMAGE,ABDOMEN^RAD^CLIN^IMAGE^RADIOLOGY^MAGNETIC RESONANCE SCAN^VA^05/09/2003 10:28:24^^4769^|4769^\\\\vhaiswimmixvi1\\image1$\\DM\\00\\47\\DM004770.TGA^\\\\vhaiswimmixvi1\\image1$\\DM\\00\\47\\DM004770.ABS^MAGNETIC IMAGE,ABDOMEN^3030508.0838^11^RAD MR^05/08/2003 08:38^^M^A^^^156^1^SLC^^^720^PATIENT,SEVENTWOZERO^CLIN^05/09/2003 10:28:24^^^\n" + 
			"8^SLC^   ^05/08/2003 08:26^RAD CT^208^CT THORAX W/O CONT^RAD^CLIN^IMAGE^RADIOLOGY^COMPUTED TOMOGRAPHY^VA^05/09/2003 13:32:52^^5317^|5317^\\\\vhaiswimmixvi1\\image1$\\DM\\00\\53\\DM005318.DCM^\\\\vhaiswimmixvi1\\image1$\\DM\\00\\53\\DM005318.ABS^CT THORAX W/O CONT^3030508.0826^11^RAD CT^05/08/2003 08:26^^M^A^^^208^1^SLC^^^720^PATIENT,SEVENTWOZERO^CLIN^05/09/2003 13:32:52^^^\n" + 
			"9^SLC^   ^05/08/2003 08:26^RAD CT^206^CT HEAD W/O CONT^RAD^CLIN^IMAGE^RADIOLOGY^COMPUTED TOMOGRAPHY^VA^05/09/2003 12:42:29^^5105^|5105^\\\\vhaiswimmixvi1\\image1$\\DM\\00\\51\\DM005106.TGA^\\\\vhaiswimmixvi1\\image1$\\DM\\00\\51\\DM005106.ABS^CT HEAD W/O CONT^303058.0826^11^RAD CT^05/08/2003 08:26^^M^A^^^206^1^SLC^^^720^PATIENT,SEVENTWOZERO^CLIN^05/09/2003 12:42:29^^^";

		List<Image> images = 
			VistaImagingTranslator.createImagesForFirstImagesFromVistaGroupList(vistaResponse, 
					PatientIdentifier.icnPatientIdentifier("655321"), "660");
		assertEquals(9, images.size());
		
	}
	
	/**
	 * 
	 * @throws VistaParsingException
	 * @throws URNFormatException 
	 */
	public void testParse_MAGG_GROUP_IMAGES() 
	throws VistaParsingException, URNFormatException
	{
		// DVB>S MAGIEN=4763
		// DVB>D GROUP^MAGGTIG(.MAGRY,MAGIEN,1)
		// DVB>ZW
		 
		String vistaResponse = 
			"1^1\n" +
			"B2^4764^\\\\vhaiswimmixvi1\\image1$\\DM\\00\\47\\DM004764.DCM^\\\\vhaiswimmixvi1\\image1$\\DM\\00\\47\\DM004764.ABS^SPINE ENTIRE AP&LAT (#1)^3030508.0856^100^US^05/08/2003 08:56^^M^A^1^27^1^1^SLC^^^720^PATIENT,SEVENTWOZERO^CLIN^05/09/2003 09:49:15^^^";
		String siteId = "660";
		String decodedStudyIen = "42";
		String patientIcn = "655321";
		
		List<Image> images = VistaImagingTranslator.VistaImageStringListToImageList(vistaResponse, siteId, 
				decodedStudyIen, PatientIdentifier.icnPatientIdentifier(patientIcn));
	}
	
	public void testParse_MAG3_CPRS_TIU_NOTE() 
	throws VistaParsingException
	{
		// DVB>K ^TMP
		// DVB>D DT^DICRW
		// DVB>S TIUDA=481
		// DVB>D IMAGES^MAGGNTI(.MAGRY,TIUDA)
		// DVB>ZW

		String vistaResponse = 
			"12^12 Images for the selected TIU NOTE^481^PATIENT,ONEZEROTWOTHREE  OPHTHALMOLOGIST CONSULT NOTE  17 Mar 04@15:42:16^8811\n" +
			"B2^8800^\\\\vhaiswimmixvi1\\image1$\\DM00\\00\\00\\00\\88\\DM000000008800.DCM^\\\\vhaiswimmixvi1\\image1$\\DM00\\00\\00\\00\\88\\DM000000008800.ABS^OPHTHALMOLOGIST CONSULT NOTE^3040317^100^OT^03/17/2004^^M^A^^^1^1^SLC^^^1023^PATIENT,ONEZEROTWOTHREE^CLIN^03/17/2004 15:46:41^^^\n" +
			"B2^8801^\\\\vhaiswimmixvi1\\image1$\\DM00\\00\\00\\00\\88\\DM000000008801.DCM^\\\\vhaiswimmixvi1\\image1$\\DM00\\00\\00\\00\\88\\DM000000008801.ABS^OPHTHALMOLOGIST CONSULT NOTE^3040317^100^OT^03/17/2004^^M^A^^^1^1^SLC^^^1023^PATIENT,ONEZEROTWOTHREE^CLIN^03/17/2004 15:46:42^^^\n" +
			"B2^8802^\\\\vhaiswimmixvi1\\image1$\\DM00\\00\\00\\00\\88\\DM000000008802.DCM^\\\\vhaiswimmixvi1\\image1$\\DM00\\00\\00\\00\\88\\DM000000008802.ABS^OPHTHALMOLOGIST CONSULT NOTE^3040317^100^OT^03/17/2004^^M^A^^^1^1^SLC^^^1023^PATIENT,ONEZEROTWOTHREE^CLIN^03/17/2004 15:46:42^^^\n" +
			"B2^8803^\\\\vhaiswimmixvi1\\image1$\\DM00\\00\\00\\00\\88\\DM000000008803.DCM^\\\\vhaiswimmixvi1\\image1$\\DM00\\00\\00\\00\\88\\DM000000008803.ABS^OPHTHALMOLOGIST CONSULT NOTE^3040317^100^OT^03/17/2004^^M^A^^^1^1^SLC^^^1023^PATIENT,ONEZEROTWOTHREE^CLIN^03/17/2004 15:46:43^^^\n" + 
			"B2^8804^\\\\vhaiswimmixvi1\\image1$\\DM00\\00\\00\\00\\88\\DM000000008804.DCM^\\\\vhaiswimmixvi1\\image1$\\DM00\\00\\00\\00\\88\\DM000000008804.ABS^OPHTHALMOLOGIST CONSULT NOTE^3040317^100^OT^03/17/2004^^M^A^^^1^1^SLC^^^1023^PATIENT,ONEZEROTWOTHREE^CLIN^03/17/2004 15:46:43^^^\n" +
			"B2^8805^\\\\vhaiswimmixvi1\\image1$\\DM00\\00\\00\\00\\88\\DM000000008805.DCM^\\\\vhaiswimmixvi1\\image1$\\DM00\\00\\00\\00\\88\\DM000000008805.ABS^OPHTHALMOLOGIST CONSULT NOTE^3040317^100^OT^03/17/2004^^M^A^^^1^1^SLC^^^1023^PATIENT,ONEZEROTWOTHREE^CLIN^03/17/2004 15:46:43^^^\n" +
			"B2^8806^\\\\vhaiswimmixvi1\\image1$\\DM00\\00\\00\\00\\88\\DM000000008806.DCM^\\\\vhaiswimmixvi1\\image1$\\DM00\\00\\00\\00\\88\\DM000000008806.ABS^OPHTHALMOLOGIST CONSULT NOTE^3040317^100^OT^03/17/2004^^M^A^^^1^1^SLC^^^1023^PATIENT,ONEZEROTWOTHREE^CLIN^03/17/2004 15:46:43^^^\n" +
			"B2^8807^\\\\vhaiswimmixvi1\\image1$\\DM00\\00\\00\\00\\88\\DM000000008807.DCM^\\\\vhaiswimmixvi1\\image1$\\DM00\\00\\00\\00\\88\\DM000000008807.ABS^OPHTHALMOLOGIST CONSULT NOTE^3040317^100^OT^03/17/2004^^M^A^^^1^1^SLC^^^1023^PATIENT,ONEZEROTWOTHREE^CLIN^03/17/2004 15:46:44^^^\n" +
			"B2^8808^\\\\vhaiswimmixvi1\\image1$\\DM00\\00\\00\\00\\88\\DM000000008808.DCM^\\\\vhaiswimmixvi1\\image1$\\DM00\\00\\00\\00\\88\\DM000000008808.ABS^OPHTHALMOLOGIST CONSULT NOTE^3040317^100^OT^03/17/2004^^M^A^^^1^1^SLC^^^1023^PATIENT,ONEZEROTWOTHREE^CLIN^03/17/2004 15:46:44^^^\n" +
			"B2^8809^\\\\vhaiswimmixvi1\\image1$\\DM00\\00\\00\\00\\88\\DM000000008809.DCM^\\\\vhaiswimmixvi1\\image1$\\DM00\\00\\00\\00\\88\\DM000000008809.ABS^OPHTHALMOLOGIST CONSULT NOTE^3040317^100^OT^03/17/2004^^M^A^^^1^1^SLC^^^1023^PATIENT,ONEZEROTWOTHREE^CLIN^03/17/2004 15:46:44^^^\n" +
			"B2^8810^\\\\vhaiswimmixvi1\\image1$\\DM00\\00\\00\\00\\88\\DM000000008810.DCM^\\\\vhaiswimmixvi1\\image1$\\DM00\\00\\00\\00\\88\\DM000000008810.ABS^OPHTHALMOLOGIST CONSULT NOTE^3040317^100^OT^03/17/2004^^M^A^^^1^1^SLC^^^1023^PATIENT,ONEZEROTWOTHREE^CLIN^03/17/2004 15:46:45^^^\n" +
			"B2^8811^\\\\vhaiswimmixvi1\\image1$\\DM00\\00\\00\\00\\88\\DM000000008811.DCM^\\\\vhaiswimmixvi1\\image1$\\DM00\\00\\00\\00\\88\\DM000000008811.ABS^OPHTHALMOLOGIST CONSULT NOTE^3040317^100^OT^03/17/2004^^M^A^^^1^1^SLC^^^1023^PATIENT,ONEZEROTWOTHREE^CLIN^03/17/2004 15:46:45^^^";
		
		List<VistaImage> vistaImages = 
			VistaImagingTranslator.extractVistaImageListFromVistaResult(vistaResponse);
	}
	
	public void testParse_MAGG_CPRS_RAD_EXAM() 
	throws VistaParsingException
	{
		// DVB>S DATA="RPT^CPRS^711^RA^i7029271.8955-1^30^^^^^^^1"
		// DVB>D IMAGEC^MAGGTRAI(.MAGZRY,DATA)
		// DVB>ZW
			 		
		String vistaResponse = 
			"1^Images for the selected Radiology Exam^44^072897-30  CHEST SINGLE VIEW  2970728.1044^52\n" +
			"B2^52^\\\\vhaiswimmixvi1\\image1$\\IE000052.TGA^\\\\vhaiswimmixvi1\\image1$\\IE000052.ABS^X-RAY   CHEST SINGLE VIEW  7/28/97^2970728^3^[S]GEN. MED.^07/28/1997^1^M^A^^^1^1^SLC^^^711^PATIENT,SEVENONEONE^CLIN^03/26/1998 11:00^^^";
		
		List<VistaImage> vistaImages = 
			VistaImagingTranslator.extractVistaImageListFromVistaResult(vistaResponse);
	}
	
}
