/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr 17, 2008
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
package gov.va.med.imaging.conversion.test;

import gov.va.med.imaging.conversion.ImageConversionFactory;
import gov.va.med.imaging.conversion.configuration.ImageConversionConfiguration;
import gov.va.med.imaging.core.interfaces.IImageConversionConfiguration;
import gov.va.med.imaging.core.interfaces.IImageLossyCompressionConfiguration;
import gov.va.med.imaging.exchange.conversion.configuration.ImageLossyCompressionConfiguration;
import gov.va.med.imaging.exchange.conversion.interfaces.IImageConversion;
import junit.framework.TestCase;

/**
 * @author VHAISWWERFEJ
 *
 */
public class ImageConversionTest 
extends TestCase 
{
	
	
	public void testGetImageConfiguration()
	{
		IImageConversionConfiguration config = new ImageConversionConfiguration();
		if(config.loadImageConversionConfigurationFromFile())
		{
			assertNotNull(config);
		}
		else
		{
			assertTrue(config.saveImageConversionConfigurationToFile());			
		}
	}

	public void testGetImageLossyCompressionConfiguration()
	{
		IImageLossyCompressionConfiguration config = new ImageLossyCompressionConfiguration();
		if(config.loadImageLossyCompressionConfigurationFromFile())
		{
			assertNotNull(config);
		}
		else
		{
			assertTrue(config.saveImageLossyCompressionConfigurationToFile());			
		}
	}
	
	public void testImageConversionFactory()
	{
		IImageConversion imageConversion = ImageConversionFactory.getImageConversion();
		assertNotNull(imageConversion);
	}

}
