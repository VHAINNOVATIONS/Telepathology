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
package gov.va.med.imaging.conversion;

import gov.va.med.imaging.conversion.configuration.ImageConversionConfiguration;
import gov.va.med.imaging.core.interfaces.IImageConversionConfiguration;
import gov.va.med.imaging.core.interfaces.IImageLossyCompressionConfiguration;
import gov.va.med.imaging.dicom.utilities.api.reconstitution.impl.DicomObjectReconstitutionManager;
import gov.va.med.imaging.dicom.utilities.api.reconstitution.interfaces.DicomObjectReconstitutionFacade;
import gov.va.med.imaging.exchange.business.ImageFormatAllowableConversionList;
import gov.va.med.imaging.exchange.business.ModalityLossyCompressionParameters;
import gov.va.med.imaging.exchange.conversion.ImageConversion;
import gov.va.med.imaging.exchange.conversion.configuration.ImageLossyCompressionConfiguration;
import gov.va.med.imaging.exchange.conversion.interfaces.IImageConversion;

/**
 * Factory to create IImageConversion instances and IImageConversionConfiguration instances
 * 
 * @author VHAISWWERFEJ
 *
 */
public class ImageConversionFactory 
{	
	private static IImageConversion imageConversion = null;
	private static DicomObjectReconstitutionFacade dicomUtilities = null;
	private static IImageConversionConfiguration imageConversionConfiguration = null;
	private static IImageLossyCompressionConfiguration imageLossyCompressionConfiguration = null;
	
	/**
	 * Synchronized to be sure all configuration is done before allowing another thread
	 * access
	 * @return
	 */
	public static synchronized IImageConversion getImageConversion()
	{
		if(imageConversion == null)
		{
			imageConversion = new ImageConversion(getImageConversionConfiguration(), getImageLossyCompressionConfiguration());
			dicomUtilities = new DicomObjectReconstitutionManager();
			((ImageConversion)imageConversion).setDicomUtilities(dicomUtilities);
		}
		
		return imageConversion;
	}
	
	/**
	 * Synchronized function so the configuration is loaded before allowing another thread to have
	 * access to image conversion configuration
	 * @return
	 */
	public static synchronized IImageConversionConfiguration getImageConversionConfiguration()
	{
		if(imageConversionConfiguration == null)
		{
			imageConversionConfiguration = new ImageConversionConfiguration();
			imageConversionConfiguration.loadImageConversionConfigurationFromFile();
		}
		return imageConversionConfiguration;
	}

	/**
	 * Synchronized function so the configuration is loaded before allowing another thread to have
	 * access to image conversion configuration
	 * @return
	 */
	public static synchronized IImageLossyCompressionConfiguration getImageLossyCompressionConfiguration()
	{
		if(imageLossyCompressionConfiguration == null)
		{
			imageLossyCompressionConfiguration = new ImageLossyCompressionConfiguration();
			imageLossyCompressionConfiguration.loadImageLossyCompressionConfigurationFromFile();
		}
		return imageLossyCompressionConfiguration;
	}

	public static void main(String [] args)
	{
		IImageConversionConfiguration config = getImageConversionConfiguration();
		System.out.println("Got [" + config.getFormatConfigurations().size() + "] configs");
		System.out.println("Decompression enabled: " + (config.isDecompressionEnabled() ? "true" : "false")	);
		
		for(ImageFormatAllowableConversionList conversionList : config.getFormatConfigurations())
		{
			System.out.println(conversionList);
		}
		
		IImageLossyCompressionConfiguration lCConfig = getImageLossyCompressionConfiguration();
		System.out.println("Got [" + lCConfig.getImageLossyCompressionConfigList().size() + "] modality lossy compression configs");
		System.out.println("XChange compatibility mode enabled: " + (lCConfig.isExchangeCompatibilityModeEnabled() ? "true" : "false")	);
		
		for(ModalityLossyCompressionParameters mtyLossyCompParams : lCConfig.getImageLossyCompressionConfigList())
		{
			System.out.println(mtyLossyCompParams);
		}
		
	}
}
