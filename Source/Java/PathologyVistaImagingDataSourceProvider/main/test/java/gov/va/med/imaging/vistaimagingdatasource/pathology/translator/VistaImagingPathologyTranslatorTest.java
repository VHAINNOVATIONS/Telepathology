/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 15, 2012
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
package gov.va.med.imaging.vistaimagingdatasource.pathology.translator;

import gov.va.med.PatientIdentifier;
import gov.va.med.PatientIdentifierType;
import gov.va.med.imaging.business.TestSite;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.pathology.PathologyCase;
import gov.va.med.imaging.pathology.PathologyCaseSpecimen;
import gov.va.med.imaging.pathology.PathologyCaseURN;
import gov.va.med.imaging.pathology.PathologySnomedCode;
import gov.va.med.imaging.pathology.enums.PathologyField;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author VHAISWWERFEJ
 *
 */
public class VistaImagingPathologyTranslatorTest
{
	@Test
	public void testCaseTranslation()
	{
		try
		{
			String vistaResult = "1^46^Unreleased Reports\n" + 
				"CY 11 4^0^^PATIENT,ONEZEROSEVENTWO^1072^ROUTINE^NO^05/10/2011 14:27^In Progress^SLC^CY^11^4^9112345678V505029^3^TRADITIONAL^P1072^NO^0\n" +
				"CY 11 3^0^^PATIENT,ONEZEROSEVENTWO^1072^ROUTINE^NO^03/01/2011 16:28^In Progress^SLC^CY^11^3^9112345678V505029^1^TRADITIONAL^P1072^NO^1\n" +
				"CY 11 2^0^^PATIENT,ONEZEROSEVENTWO^1072^ROUTINE^NO^03/01/2011 16:27^In Progress^SLC^CY^11^2^9112345678V505029^1^TRADITIONAL^P1072^YES^0\n" +
				"CY 11 1^0^^PATIENT,ONEZEROSEVENTWO^1072^ROUTINE^NO^03/01/2011 16:26^In Progress^SLC^CY^11^1^9112345678V505029^1^TRADITIONAL^P1072^NO^0\n" +
				"CY 01 2^0^^PATIENT,ONEZEROSIXTHREE^1063^ROUTINE^NO^08/21/2001^In Progress^SLC^CY^01^2^9345678231V898292^1^TRADITIONAL^P1063^YES^0\n" +
				"CY 01 1^0^^PATIENT,ONEZEROSIXSIX^1066^ROUTINE^NO^08/21/2001^In Progress^SLC^CY^01^1^9512786523V998658^1^TRADITIONAL^P1066^NO^1";
			
			Site site = new TestSite("Salt Lake City", "660", "SLC");
			
			List<PathologyCase> cases = VistaImagingPathologyTranslator.translateLabCasesResult(site, vistaResult);
			System.out.println("got '" + cases.size() + "' cases");
			for(PathologyCase c : cases)
			{
				//System.out.println(c.getAccessionNumber() + ", " + c.getSpecimenTakenDate());
				assertNotNull(c.getSpecimenTakenDate());
				
				String accessionNumber = c.getPathologyCaseUrn().toStringAccessionNumber();
				if(isCaseSupposedToBeSensitive(accessionNumber))
				{
					assertTrue(c.isPatientSensitive());
				}
				else
				{
					assertFalse(c.isPatientSensitive());
				}
			}
			assertEquals(6, cases.size());
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			fail(ex.getMessage());
		}
	}
	
	private boolean isCaseSupposedToBeSensitive(String accessionNumber)
	{
		for(String sensitiveCaseAccessionNumber : sensitiveCases)
		{
			if(accessionNumber.equals(sensitiveCaseAccessionNumber))
				return true;
		}
		return false;
	}
	
	private static String [] sensitiveCases = new String[] {"CY 01 1", "CY 11 3"};
	
	@Test
	public void testSpecimenTranslation()
	{
		String result = "1^3^Specimen^Smear Prep^Stain/Procedure^# of Slides^Last Stain Date/Time\n(1) Bronchial Washing 1\n(2) Bronchial Washing 2\n(3) Bronchial Washing 3";
		
		List<PathologyCaseSpecimen> specimens = VistaImagingPathologyTranslator.translateSpecimens(result);
		assertEquals(3, specimens.size());
		for(PathologyCaseSpecimen specimen : specimens)
		{
			System.out.println(specimen.getSpecimen());
		}
		
		result = "1^1^Specimen^Smear Prep^Stain/Procedure^# of Slides^Last Stain Date/Time\nSPECIMEN 1^SMEAR PR^TRICYCLICS {Blood}^2^03/02/2012 12:13";
		specimens = VistaImagingPathologyTranslator.translateSpecimens(result);
		assertEquals(1, specimens.size());
		/*
		for(PathologyCaseSpecimen specimen : specimens)
		{
			System.out.println(specimen.getSpecimen());			
		}*/
	}
	
	@Test
	public void testConsultationsTranslation()
	{
		try
		{
			// longer date format
			String vistaResult = "1^2^Consult IEN^Type^Reservation Date^Interpreting Station^Site Abbreviation^Status\n18^INTERPRETATION^05/17/2012 10:40:20^660^SLC^PENDING\n19^CONSULTATION^05/17/2012 10:40:20^688^WAS^PENDING";
			PathologyCaseURN pathologyCaseUrn = PathologyCaseURN.create("660", "CY", "00", "1", 
					new PatientIdentifier("123456", PatientIdentifierType.icn));
			VistaImagingPathologyTranslator.translateConsultations(pathologyCaseUrn, vistaResult);
			
			// shorter date format
			vistaResult = "1^2^Consult IEN^Type^Reservation Date^Interpreting Station^Site Abbreviation^Status\n1^INTERPRETATION^05/10/2012 14:18^660^SLC^PENDING\n2^CONSULTATION^05/10/2012 14:23^^^PENDING";
			VistaImagingPathologyTranslator.translateConsultations(pathologyCaseUrn, vistaResult);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			fail(ex.getMessage());
		}
	}
	
	@Test
	public void testParsingDate()
	throws Exception
	{
		// method is private, using reflection to change that
		Method method =
			VistaImagingPathologyTranslator.class.getDeclaredMethod("parseDateString", new Class<?>[] {String.class});
		method.setAccessible(true); // make accessible (because I said so!)
		Date date = (Date)method.invoke(null, new Object[] {"12/07/2012"});
		assertNotNull(date);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);		
		assertEquals(11, calendar.get(Calendar.MONTH));
		assertEquals(7, calendar.get(Calendar.DAY_OF_MONTH));
		assertEquals(2012, calendar.get(Calendar.YEAR));
		
		date = (Date)method.invoke(null, new Object[] {"12/07/2012 13:45"});
		assertNotNull(date);
		calendar = Calendar.getInstance();
		calendar.setTime(date);		
		assertEquals(11, calendar.get(Calendar.MONTH));
		assertEquals(7, calendar.get(Calendar.DAY_OF_MONTH));
		assertEquals(2012, calendar.get(Calendar.YEAR));
		assertEquals(13, calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(45, calendar.get(Calendar.MINUTE));
		
		date = (Date)method.invoke(null, new Object[] {"12/07/2012 13:45:22"});
		assertNotNull(date);
		calendar = Calendar.getInstance();
		calendar.setTime(date);		
		assertEquals(11, calendar.get(Calendar.MONTH));
		assertEquals(7, calendar.get(Calendar.DAY_OF_MONTH));
		assertEquals(2012, calendar.get(Calendar.YEAR));
		assertEquals(13, calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(45, calendar.get(Calendar.MINUTE));
		assertEquals(22, calendar.get(Calendar.SECOND));
	}
	
	@Test
	public void testSnomedTranslation()
	throws Exception
	{
		Method method =
			VistaImagingPathologyTranslator.class.getDeclaredMethod("translateSnomedLine", new Class<?>[] {String.class});
		method.setAccessible(true); // make accessible (because I said so!)
		
		// 1|56000^LIVER^1|PROCEDURE^1341^ENDOSCOPIC BRUSH BIOPSY
		// 1|56000^LIVER^1|MORPHOLOGY^81703^HEPATOCELLULAR CARCINOMA^1|ETIOLOGY^3878^RIO BRAVO VIRUS
		// 1|56000^LIVER^2|MORPHOLOGY^67000^PLASMA MEMBRANE ALTERATION
		
		PathologySnomedCode snomedCode = 
			(PathologySnomedCode)method.invoke(null, new Object[] {"1|56000^LIVER^1|PROCEDURE^1341^ENDOSCOPIC BRUSH BIOPSY"});
		assertNotNull(snomedCode);
		assertEquals("1", snomedCode.getTissueId());
		assertEquals("LIVER", snomedCode.getTissue());
		assertEquals("56000", snomedCode.getTissueCode());
		assertEquals(PathologyField.procedure, snomedCode.getField());
		assertEquals("ENDOSCOPIC BRUSH BIOPSY", snomedCode.getSnomedValue());		
		assertEquals("1341", snomedCode.getSnomedCode());
		assertEquals("1", snomedCode.getSnomedId());
		assertNull(snomedCode.getEtiologyId());
		assertNull(snomedCode.getEtiologySnomedCode());
		assertNull(snomedCode.getEtiologySnomedValue());
		
		snomedCode = 
			(PathologySnomedCode)method.invoke(null, new Object[] {"1|56000^LIVER^1|MORPHOLOGY^81703^HEPATOCELLULAR CARCINOMA^1|ETIOLOGY^3878^RIO BRAVO VIRUS"});
		assertNotNull(snomedCode);
		assertEquals("1", snomedCode.getTissueId());
		assertEquals("LIVER", snomedCode.getTissue());
		assertEquals("56000", snomedCode.getTissueCode());
		assertEquals(PathologyField.morphology, snomedCode.getField());
		assertEquals("HEPATOCELLULAR CARCINOMA", snomedCode.getSnomedValue());		
		assertEquals("81703", snomedCode.getSnomedCode());
		assertEquals("1", snomedCode.getEtiologyId());
		assertEquals("1", snomedCode.getSnomedId());
		assertEquals("3878", snomedCode.getEtiologySnomedCode());
		assertEquals("RIO BRAVO VIRUS", snomedCode.getEtiologySnomedValue());
		
		snomedCode = 
			(PathologySnomedCode)method.invoke(null, new Object[] {"1|56000^LIVER^2|MORPHOLOGY^67000^PLASMA MEMBRANE ALTERATION"});
		assertNotNull(snomedCode);
		assertEquals("1", snomedCode.getTissueId());
		assertEquals("LIVER", snomedCode.getTissue());
		assertEquals("56000", snomedCode.getTissueCode());
		assertEquals(PathologyField.morphology, snomedCode.getField());
		assertEquals("PLASMA MEMBRANE ALTERATION", snomedCode.getSnomedValue());		
		assertEquals("67000", snomedCode.getSnomedCode());
		assertEquals("2", snomedCode.getSnomedId());
		assertNull(snomedCode.getEtiologyId());
		assertNull(snomedCode.getEtiologySnomedCode());
		assertNull(snomedCode.getEtiologySnomedValue());
	}
}

