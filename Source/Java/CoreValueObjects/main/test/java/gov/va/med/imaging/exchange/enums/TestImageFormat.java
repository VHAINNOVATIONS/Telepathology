package gov.va.med.imaging.exchange.enums;

import junit.framework.TestCase;

/**
 * This test is more of a sanity check than a real unit test.
 * It exists simply to assure that any changes to mappings from
 * mime type to ImageFormat are intentional.
 * 
 * @author vhaiswbeckec
 *
 */
public class TestImageFormat 
extends TestCase
{

	public void testGetContentType()
	{
		assertEquals( "image/x-targa", ImageFormat.getContentType(ImageFormat.DOWNSAMPLEDTGA) );
		assertEquals( "image/x-targa", ImageFormat.getContentType(ImageFormat.TGA) );
		assertEquals( "image/tiff", ImageFormat.getContentType(ImageFormat.TIFF) );
		assertEquals( "image/bmp", ImageFormat.getContentType(ImageFormat.BMP) );
		assertEquals( "image/jpeg", ImageFormat.getContentType(ImageFormat.JPEG) );
		assertEquals( "application/dicom", ImageFormat.getContentType(ImageFormat.DICOM) );
		assertEquals( "application/dicom", ImageFormat.getContentType(ImageFormat.DICOMJPEG) );
		assertEquals( "application/dicom", ImageFormat.getContentType(ImageFormat.DICOMJPEG2000) );
	}

	/**
	 * This test is order specific with regard to the definition of the
	 * ImageFormat enum because valueOfMimeType() will return the first
	 * applicable value.
	 */
	public void testGetValueOfMimeType()
	{
		assertEquals( ImageFormat.TGA, ImageFormat.valueOfMimeType("image/x-targa") );
		assertEquals( ImageFormat.TIFF, ImageFormat.valueOfMimeType("image/tiff") );
		assertEquals( ImageFormat.BMP, ImageFormat.valueOfMimeType("image/bmp") );
		assertEquals( ImageFormat.JPEG, ImageFormat.valueOfMimeType("image/jpeg") );
		assertEquals( ImageFormat.DICOM, ImageFormat.valueOfMimeType("application/dicom") );
	}
}
