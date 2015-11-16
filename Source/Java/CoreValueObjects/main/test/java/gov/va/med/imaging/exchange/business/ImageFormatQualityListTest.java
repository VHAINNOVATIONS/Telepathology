/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 21, 2011
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
package gov.va.med.imaging.exchange.business;

import java.util.ArrayList;
import java.util.List;

import gov.va.med.imaging.exchange.enums.ImageFormat;
import gov.va.med.imaging.exchange.enums.ImageQuality;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author vhaiswwerfej
 *
 */
public class ImageFormatQualityListTest
{

	@Test
	public void testPruningImageFormatQualityList()
	{
		ImageQuality imageQuality = ImageQuality.REFERENCE;
		
		ImageFormatQualityList imageFormatQualityList = 
			ImageFormatQualityList.createListFromFormatQuality(ImageFormat.JPEG, imageQuality);
		imageFormatQualityList.add(new ImageFormatQuality(ImageFormat.DICOM, imageQuality));
		imageFormatQualityList.add(new ImageFormatQuality(ImageFormat.TGA, imageQuality));
		imageFormatQualityList.add(new ImageFormatQuality(ImageFormat.BMP, imageQuality));
		
		List<ImageFormat> allowedFormats = new ArrayList<ImageFormat>();
		allowedFormats.add(ImageFormat.TGA);
		
		imageFormatQualityList.pruneToAllowedFormats(allowedFormats);
		
		assertEquals(1, imageFormatQualityList.size());
		//System.out.println(imageFormatQualityList.getAcceptString(true));
		assertTrue(imageFormatQualityList.contains(new ImageFormatQuality(ImageFormat.TGA, imageQuality)));
		assertFalse(imageFormatQualityList.contains(new ImageFormatQuality(ImageFormat.JPEG, imageQuality)));
		assertFalse(imageFormatQualityList.contains(new ImageFormatQuality(ImageFormat.DICOM, imageQuality)));
		assertFalse(imageFormatQualityList.contains(new ImageFormatQuality(ImageFormat.BMP, imageQuality)));
	}
	
	@Test
	public void testTranslatingStringToImageFormatQualityList()
	{
		ImageQuality imageQuality = ImageQuality.REFERENCE;
		
		ImageFormatQualityList imageFormatQualityList = 
			ImageFormatQualityList.createListFromFormatQuality(ImageFormat.JPEG, imageQuality);
		imageFormatQualityList.add(new ImageFormatQuality(ImageFormat.DICOM, imageQuality));
		imageFormatQualityList.add(new ImageFormatQuality(ImageFormat.DICOMJPEG2000, imageQuality));
		imageFormatQualityList.add(new ImageFormatQuality(ImageFormat.TGA, imageQuality));
		imageFormatQualityList.add(new ImageFormatQuality(ImageFormat.BMP, imageQuality));
		
		String imageFormatQualityListString = imageFormatQualityList.getAcceptString(true, true);
		ImageFormatQualityList newImageFormatQualityList = 
			ImageFormatQualityList.createListFromAcceptString(imageFormatQualityListString);
		
		assertEquals(imageFormatQualityList, newImageFormatQualityList);
		
	}
	
	@Test
	public void testTranslatingStringWithoutQualitiesToImageFormatQualityList()
	{
		// formats that do not have qualities are eliminated from the result
		String imageFormatQualityListString = "image/jpeg;q=.70,application/dicom,application/dicom/image/j2k;q=.90,image/x-targa,image/bmp;q=.70";
		ImageFormatQualityList imageFormatQualityList = 
			ImageFormatQualityList.createListFromAcceptString(imageFormatQualityListString);
		// should eliminate 2 items and retain 3
		assertEquals(3, imageFormatQualityList.size());
		ImageFormatQuality imageFormatQuality = 
			new ImageFormatQuality(ImageFormat.JPEG, ImageQuality.REFERENCE);
		assertTrue(imageFormatQualityList.contains(imageFormatQuality));
		imageFormatQuality = 
			new ImageFormatQuality(ImageFormat.DICOM, ImageQuality.REFERENCE);
		assertFalse(imageFormatQualityList.contains(imageFormatQuality));
		imageFormatQuality = 
			new ImageFormatQuality(ImageFormat.DICOMJPEG2000, ImageQuality.DIAGNOSTIC);
		assertTrue(imageFormatQualityList.contains(imageFormatQuality));
		imageFormatQuality = 
			new ImageFormatQuality(ImageFormat.TGA, ImageQuality.REFERENCE);
		assertFalse(imageFormatQualityList.contains(imageFormatQuality));		
		imageFormatQuality = 
			new ImageFormatQuality(ImageFormat.BMP, ImageQuality.REFERENCE);
		assertTrue(imageFormatQualityList.contains(imageFormatQuality));		
	}
}

