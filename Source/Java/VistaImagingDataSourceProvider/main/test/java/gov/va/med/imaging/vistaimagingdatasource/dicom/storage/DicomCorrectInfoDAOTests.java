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

import gov.va.med.imaging.exchange.business.dicom.DicomCorrectEntry;
import gov.va.med.imaging.exchange.business.dicom.DicomCorrectInfo;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

@Deprecated
public class DicomCorrectInfoDAOTests
{
//	@Test
//	public void testTranslateCreate() throws Exception
//	{
//		DicomCorrectInfo info = new DicomCorrectInfo();
//		Assert.assertTrue(info.getId() == 0);
//
//		StringBuilder builder = new StringBuilder();
//		builder.append("0``5");
//		DicomCorrectInfoDAO dao = new DicomCorrectInfoDAO(null);
//		info = dao.translateCreate(info, builder.toString());
//		
//		Assert.assertTrue(info.getId() == 5);
//		
//	}
}
