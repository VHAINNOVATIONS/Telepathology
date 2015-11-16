package gov.va.med.imaging.exchange.enums;

import junit.framework.TestCase;

/**
 * This test is more of a sanity check than a real unit test.
 * It exists simply to assure that any changes to mappings from
 * image quality q-values type to ImageQuality are intentional.
 * 
 * @author vhaiswbeckec
 *
 */
public class TestImageQuality 
extends TestCase
{
	public void testGetImageQuality()
	{
		assertEquals( ImageQuality.DIAGNOSTIC, ImageQuality.getImageQuality(80) );
		assertEquals( ImageQuality.DIAGNOSTIC, ImageQuality.getImageQuality(99) );
		assertEquals( ImageQuality.DIAGNOSTIC, ImageQuality.getImageQuality(90) );
		assertEquals( ImageQuality.DIAGNOSTIC, ImageQuality.getImageQuality(85) );
		assertEquals( ImageQuality.DIAGNOSTIC, ImageQuality.getImageQuality(82) );
		assertEquals( ImageQuality.DIAGNOSTIC, ImageQuality.getImageQuality(94) );

		assertEquals( ImageQuality.REFERENCE, ImageQuality.getImageQuality(50) );
		assertEquals( ImageQuality.REFERENCE, ImageQuality.getImageQuality(79) );
		assertEquals( ImageQuality.REFERENCE, ImageQuality.getImageQuality(70) );
		assertEquals( ImageQuality.REFERENCE, ImageQuality.getImageQuality(65) );
		assertEquals( ImageQuality.REFERENCE, ImageQuality.getImageQuality(71) );

		assertEquals( ImageQuality.THUMBNAIL, ImageQuality.getImageQuality(1) );
		assertEquals( ImageQuality.THUMBNAIL, ImageQuality.getImageQuality(49) );
		assertEquals( ImageQuality.THUMBNAIL, ImageQuality.getImageQuality(42) );
		assertEquals( ImageQuality.THUMBNAIL, ImageQuality.getImageQuality(10) );
		assertEquals( ImageQuality.THUMBNAIL, ImageQuality.getImageQuality(19) );

		assertEquals( ImageQuality.DIAGNOSTICUNCOMPRESSED, ImageQuality.getImageQuality(100) );
	}
	

}
