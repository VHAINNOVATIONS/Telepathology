/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Nov, 2009
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswlouthj
  Description: DICOM Study cache manager. Maintains the cache of study instances
  			   and expires old studies after 15 minutes. 

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

package gov.va.med.imaging.vistaimagingdatasource.dicom.storage;

import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.dicom.DicomCorrectEntry;
import gov.va.med.imaging.exchange.business.storage.exceptions.DeletionException;
import gov.va.med.imaging.exchange.business.storage.exceptions.RetrievalException;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

@Deprecated
public class DicomCorrectEntryDAOTests
{
//	@Test
//	public void testTranslateFindAll() 
//	{
//		StringBuilder builder = new StringBuilder();
//		builder.append("0``\r\n");
//		builder.append("FILEPATH|C:\\DICOMImage\\BP000255.dcm_0`\r\n");
//		builder.append("GATEWAY LOCATION|SALT LAKE CITY_0`\r\n");
//		builder.append("IMAGE UID|1.2.8.80004.66.3.4.563454685.354.55.5.889.1.5_0`\r\n");
//		builder.append("STUDY UID|1.2.8.80004.66.3.4.563454685.354.55.5.889_0`\r\n");
//		builder.append("SERVICE TYPE|RAD_0`\r\n");
//		builder.append("NEWNME|PATIENT,TWOZERO_0`\r\n");
//		builder.append("NEWSSN|000000020_0`\r\n");
//		builder.append("NEWCASE NO|112309-322_0`\r\n");
//		builder.append("NEW PROC IEN|177_0`\r\n");
//		builder.append("NEW PROCEDURE|CT ABDOMEN W/CONT_0`\r\n");
//		builder.append("FILEPATH|C:\\DICOMImage\\BP000255.dcm_1`\r\n");
//		builder.append("GATEWAY LOCATION|SALT LAKE CITY_1`\r\n");
//		builder.append("IMAGE UID|1.2.8.80004.66.3.4.563454685.354.55.5.889.1.5_1`\r\n");
//		builder.append("STUDY UID|1.2.8.80004.66.3.4.563454685.354.55.5.889_1`\r\n");
//		builder.append("SERVICE TYPE|RAD_1`\r\n");
//		builder.append("NEWNME|PATIENT,TWOZERO_1`\r\n");
//		builder.append("NEWSSN|000000020_1`\r\n");
//		builder.append("NEWCASE NO|112309-322_1`\r\n");
//		builder.append("NEW PROC IEN|177_1`\r\n");
//		builder.append("NEW PROCEDURE|CT ABDOMEN W/CONT_1`\r\n");
//		builder.append("FILEPATH|C:\\DICOMImage\\BP000255.dcm_2`\r\n");
//		builder.append("GATEWAY LOCATION|SALT LAKE CITY_2`\r\n");
//		builder.append("IMAGE UID|1.2.8.80004.66.3.4.563454685.354.55.5.889.1.5_2`\r\n");
//		builder.append("STUDY UID|1.2.8.80004.66.3.4.563454685.354.55.5.889_2`\r\n");
//		builder.append("SERVICE TYPE|RAD_2`\r\n");
//		builder.append("NEWNME|PATIENT,TWOZERO_2`\r\n");
//		builder.append("NEWSSN|000000020_2`\r\n");
//		builder.append("NEWCASE NO|112309-322_2`\r\n");
//		builder.append("NEW PROC IEN|177_2`\r\n");
//		builder.append("NEW PROCEDURE|CT ABDOMEN W/CONT_2`\r\n");
//
//		DicomCorrectEntryDAO dao = new DicomCorrectEntryDAO(null);
//		List<DicomCorrectEntry> entries = null;
//		try {
//			entries = dao.translateFindAll(builder.toString());
//		} catch (RetrievalException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (MethodException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		Assert.assertTrue(entries.size() == 3);
//		for (int i = 0; i < entries.size(); i++) {
//			Assert.assertTrue(entries.get(i).getFilePath().equals("C:\\DICOMImage\\BP000255.dcm_" + i));
//			Assert.assertTrue(entries.get(i).getGatewayLocation().equals("SALT LAKE CITY_" + i));
//			Assert.assertTrue(entries.get(i).getImageUID().equals("1.2.8.80004.66.3.4.563454685.354.55.5.889.1.5_" + i));
//			Assert.assertTrue(entries.get(i).getStudyUID().equals("1.2.8.80004.66.3.4.563454685.354.55.5.889_" + i));
//			Assert.assertTrue(entries.get(i).getServiceType().equals("RAD_" + i));
//			Assert.assertTrue(entries.get(i).getCorrectedName().equals("PATIENT,TWOZERO_" + i));
//			Assert.assertTrue(entries.get(i).getCorrectedSSN().equals("000000020_" + i));
//			Assert.assertTrue(entries.get(i).getCorrectedCaseNumber().equals("112309-322_" + i));
//			Assert.assertTrue(entries.get(i).getCorrectedProcedureIEN().equals("177_" + i));
//			Assert.assertTrue(entries.get(i).getCorrectedProcedureDescription().equals("CT ABDOMEN W/CONT_" + i));
//		}
//	}
//	
//	@Test
//	public void testTranslateDelete() throws Exception
//	{
//		DicomCorrectEntryDAO dao = new DicomCorrectEntryDAO(null);
//		dao.translateDelete("0");
//	}
//	
//	@Test (expected=DeletionException.class)
//	public void testTranslateDeleteWithException() throws DeletionException
//	{
//		DicomCorrectEntryDAO dao = new DicomCorrectEntryDAO(null);
//		dao.translateDelete("-1`Entity Not Found");
//	}

}
