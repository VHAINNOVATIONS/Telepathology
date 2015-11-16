/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 26, 2009
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWTITTOC
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
package gov.va.med.imaging.exchange.conversion.configuration;

import gov.va.med.imaging.core.interfaces.IImageLossyCompressionConfiguration;
import gov.va.med.imaging.core.interfaces.exceptions.ApplicationConfigurationException;
import gov.va.med.imaging.exchange.business.ModalityLossyCompressionParameters;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Image conversion configuration. Contains options for using in Image conversion.
 * 
 * @author VHAISWWERFEJ
 *
 */
public class ImageLossyCompressionConfiguration 
implements IImageLossyCompressionConfiguration
{
	/**
	 * TODO adjust big file border, J2K compression factors and JPG compress qualities
	 */
	private static int 	 averageImageSize = 1048576; // 1 MB
	private static float smallCompressRatio = 10.0f; // lossy J2K compression ratio for small images 
	private static float bigCompressRatio = 25.0f;   // lossy J2K compression ratio for large images 
	private static int	 smallCompressQuality = 75;  // lossy JPG compression quality for small images (0..100; 75 - JPG default)
	private static int	 bigCompressQuality = 50;    // lossy compression quality for large images 

	private static final String MODALITY_LOSSY_COMPRESSION_CONFIG_FILENAME = "ImageLossyCompressionConfig.xml";
	private static final  Logger logger = Logger.getLogger(ImageLossyCompressionConfiguration.class);

	protected boolean exchangeCompatibilityModeEnabled = true;
	
	protected List<ModalityLossyCompressionParameters> imageLossyCompressionConfigList = new ArrayList<ModalityLossyCompressionParameters>();
	
	private String vixConfigurationDirectory = null;
	private String imageLossyCompressionConfigurationFilespec = null;
	
	public ImageLossyCompressionConfiguration()
	{
		super();
	}
	
	public void init() 
	throws ApplicationConfigurationException
	{
		getImageLossyCompressionConfigurationFilespec();
		imageLossyCompressionConfigList.clear();
	}
	
	@Override
	public boolean loadImageLossyCompressionConfigurationFromFile() 
	{
		boolean success = false;
		try
		{
			success = this.loadAppConfigurationFromFile(this.getImageLossyCompressionConfigurationFilespec()); // uses read lock
		}
		catch (ApplicationConfigurationException ex) 
		{
			logger.error("Error in ImageLossyCompressionConfiguration.loadImageLossyCompressionConfigurationFromFile", ex);
		}
		return success;
	}	

	/**
	 * @param fileSpec - the file that contains the application configuration
	 * @return true if the configuration was successfully loaded from the file specified by appConfigurationFilespec
	 */
	private boolean loadAppConfigurationFromFile(String fileSpec) 
	{
		logger.info("Loading image compression ratio configuration from '" + fileSpec + "'.");
		
		boolean success = false;
		XMLDecoder xmlDecoder = null;
		ImageLossyCompressionConfiguration imageLossyCompressionConfiguration = null;

		if (fileSpec != null)
		{
			File configFile = new File(fileSpec);
			if (configFile.exists())
			{
				try
				{
					xmlDecoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(fileSpec))); // throws FileNotFoundException
					imageLossyCompressionConfiguration = (ImageLossyCompressionConfiguration) xmlDecoder.readObject();
					if (imageLossyCompressionConfiguration != null)
					{
						this.assignState(imageLossyCompressionConfiguration); // this method obtains a write lock
						logger.info("ImageLossyCompressionConfiguration: loaded Lossy Image Compression configuration from: " + fileSpec);
						success = true;
					}
				}
				catch (FileNotFoundException ex)
				{
					logger.error(ex.getMessage());
				}
				catch (ArrayIndexOutOfBoundsException ex)
				{
					logger.error("ImageLossyCompressionConfiguration.loadAppConfigurationFromFile: ArrayIndexOutOfBoundsException : " + ex.getMessage());
				}
				finally
				{
					if (xmlDecoder != null)
					{
						xmlDecoder.close();
					}
				}
			}
		}
		logger.info("Image lossy compression configuration from file '" + fileSpec + "' loaded.");
		return success;
	}
	
	/**
	 * @param imageLossyCompressionConfiguration - the AppConfiguration object that contains the state to use
	 * This public method takes the state of the passed appConfiguration param and overlays it onto the current instance
	 */
	private void assignState(ImageLossyCompressionConfiguration imageLossyCompressionConfiguration)
	{
		if (imageLossyCompressionConfiguration != null)
		{
			this.exchangeCompatibilityModeEnabled = imageLossyCompressionConfiguration.exchangeCompatibilityModeEnabled;			
			this.imageLossyCompressionConfigList.addAll(imageLossyCompressionConfiguration.imageLossyCompressionConfigList);
		}
	}
	
	private String getImageLossyCompressionConfigurationFilespec() 
	throws ApplicationConfigurationException
	{
		String fileSpec = null;		
		fileSpec = this.imageLossyCompressionConfigurationFilespec;
		

		if (fileSpec == null)
		{
			fileSpec = this.getVixConfigurationDirectory();
			// add the trailing file separator character if necessary
			if (!fileSpec.endsWith("\\") || !fileSpec.endsWith("/"))
			{
				fileSpec += "/";
			}
			fileSpec += MODALITY_LOSSY_COMPRESSION_CONFIG_FILENAME;

			this.setAppConfigurationFilespec(fileSpec);
		}
		
		return fileSpec;
	}
	
	private void setAppConfigurationFilespec(String fileSpec) 
	{
		this.imageLossyCompressionConfigurationFilespec = fileSpec;		
	}
	
	private String getVixConfigurationDirectory() 
	throws ApplicationConfigurationException
	{
		String configDir = null;
		configDir = this.vixConfigurationDirectory;		
		if (configDir == null)
		{
			configDir = System.getenv("vixconfig");
			if (configDir == null)
			{
				throw new ApplicationConfigurationException("The vixconfig has not been set.");
			}

			this.setVixConfigurationDirectory(configDir);
		}
		return configDir;
	}
	
	private void setVixConfigurationDirectory(String vixConfigurationDirectory) 
	{
		this.vixConfigurationDirectory = vixConfigurationDirectory;
	}
	
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.IImageLossyCompressionConfiguration#isDecompressionEnabled()
	 */
	@Override
	public boolean isExchangeCompatibilityModeEnabled() 
	{
		return this.exchangeCompatibilityModeEnabled;
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.IImageLossyCompressionConfiguration#setDecompressionEnabled(boolean)
	 */
	@Override
	public void setExchangeCompatibilityMode(boolean enabled) 
	{
		this.exchangeCompatibilityModeEnabled = enabled;
	}

	@Override
	public List<ModalityLossyCompressionParameters> getImageLossyCompressionConfigList() 
	{
		return this.imageLossyCompressionConfigList;		
	}

	public void setImageLossyCompressionConfigList(
			List<ModalityLossyCompressionParameters> imageLossyCompressionConfigList) 
	{
		this.imageLossyCompressionConfigList = imageLossyCompressionConfigList;
	}
	
	@Override
	public float getModalityLossyJPEG2000Ratio(String modality, int imageBytes, boolean xChangeUse) 
	{			
		float xCompatRatio= (imageBytes > averageImageSize) ? bigCompressRatio : smallCompressRatio;		

		if(xChangeUse && this.isExchangeCompatibilityModeEnabled())
		{
			logger.warn("JPEG2000 Compression Ratio is set to XChange default [" + xCompatRatio + "]");
			return xCompatRatio;
		}
		if(modality == null)
		{
			logger.warn("JPEG2000 Compression Ratio for null modality is set to XChange default [" + xCompatRatio + "]");
			return xCompatRatio;
		}
		logger.info("Searching for JPEG2000 Compression Ratio for modality Type [" + modality + "]");
		for(ModalityLossyCompressionParameters mlcp : imageLossyCompressionConfigList)
		{
			if(mlcp.getModality().equals(modality)) {
				logger.info("JPEG2000 Compression Ratio for modality Type [" + modality + 
					    "] is configured to [" + xCompatRatio + "]");
				return mlcp.getJ2kLossyRatio();
			}
		}
		logger.info("JPEG2000 Compression Ratio config setting for modality Type [" + modality + 
				    "] is not found; value set to XChange default [" + xCompatRatio + "]");
		return xCompatRatio;
	}

	@Override
	public int getModalityLossyJPEGQuality(String modality, int imageBytes, boolean xChangeUse) 
	{		
		int xCompatQuality= (imageBytes > averageImageSize) ? bigCompressQuality : smallCompressQuality;		

		if(xChangeUse && this.isExchangeCompatibilityModeEnabled())
		{
			logger.warn("JPEG Compression Quality is set to XChange default [" + xCompatQuality + "]");
			return xCompatQuality;
		}
		if(modality == null)
		{
			logger.warn("JPEG Compression Quality for null modality is set to XChange default [" + xCompatQuality + "]");
			return xCompatQuality;
		}
		logger.info("Searching for JPEG Compression Quality value for modality Type [" + modality + "]");
		int quality=0;					
		for(ModalityLossyCompressionParameters mlcp : imageLossyCompressionConfigList)
		{
			if(mlcp.getModality().equals(modality)) {
				quality = mlcp.getJpegQuality();
				// TODO if 0 received -- compute j2k equivalent quality setting for JPEG
				if (quality == 0) {
					quality = xCompatQuality; // temporarily use default setting for exchange
					logger.info("JPEG Compression Quality for modality Type [" + modality + 
						    "] is forced to XChange setting [" + quality + "]");
				} else {
					logger.info("JPEG Compression Quality for modality Type [" + modality + 
						    "] is configured to [" + quality + "]");
			
				}
			}
			return quality;
		}
		logger.info("Compression Quality config setting for modality Type [" + modality + 
			    "] is not found; value set to XChange default [" + xCompatQuality + "]");
		return xCompatQuality;
	}

	@Override
	public boolean saveImageLossyCompressionConfigurationToFile() {
		boolean success = false;
		XMLEncoder xmlEncoder = null;
		try
		{
			xmlEncoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(this.getImageLossyCompressionConfigurationFilespec())));
			xmlEncoder.writeObject(this);
			logger.info("ImageLossyCompressionConfiguration: configuration saved to: " + this.getImageLossyCompressionConfigurationFilespec());
			success = true;
		}
		catch (ApplicationConfigurationException ex)
		{
			logger.error(ex.getMessage());
		}
		catch (FileNotFoundException ex)
		{
			logger.error("ImageLossyCompressionConfiguration.saveAppConfigurationToFile: " + ex.getMessage());
		}
		finally
		{
			if (xmlEncoder != null)
			{
				xmlEncoder.close();
			}
		}
		return success;
	}
	
	public static void main(String [] args)
	{
		ImageLossyCompressionConfiguration config = new ImageLossyCompressionConfiguration();
		try
		{
			config.init();
		}
		catch(ApplicationConfigurationException acX)
		{
			acX.printStackTrace();
		}
		setBasicConfiguration(config);
		config.saveImageLossyCompressionConfigurationToFile();
	}
	
	private static void addAnEntry (ImageLossyCompressionConfiguration config, 
									String modality, float j2kRatio, int jpegQuality)
	{
		ModalityLossyCompressionParameters mlcp = 
			new ModalityLossyCompressionParameters(modality, j2kRatio, jpegQuality);
		config.imageLossyCompressionConfigList.add(mlcp);	
	}
	
	private static void setBasicConfiguration(ImageLossyCompressionConfiguration config)
	{
		config.setExchangeCompatibilityMode(true);
		
		// Create the default compression options allowed for each format
		// The order of items in these lists is NOT relevant, 
		// NO MODALITY code should be repeated!
		addAnEntry(config, "CR", 25.0f, 0); // Computed Radiography
		addAnEntry(config, "DX", 25.0f, 0); // Digital Radiography
		addAnEntry(config, "DG", 25.0f, 0); // DiaphanoGraphy
		addAnEntry(config, "RG", 25.0f, 0); // RadioGraphic img (conv. film/screen)
		addAnEntry(config, "MG", 25.0f, 0); // Mammography
		addAnEntry(config, "RF", 25.0f, 0); // Radio Fluoroscopy 
		addAnEntry(config, "XC", 25.0f, 0); // External-camera Photography
		addAnEntry(config, "CT", 10.0f, 0); // Computed Tomography
		addAnEntry(config, "XA", 10.0f, 0); // X-Ray Angiography
		addAnEntry(config, "MR", 10.0f, 0); // Magnetic Resonance
		addAnEntry(config, "OT", 10.0f, 0); // other
		addAnEntry(config, "PT", 10.0f, 0); // Positron emission Tomography (PET)
		addAnEntry(config, "US", 10.0f, 0); // UltraSound
		addAnEntry(config, "DD", 10.0f, 0); // Duplex Doppler
		addAnEntry(config, "NM",  8.0f, 0); // Nuclear Medicine
		addAnEntry(config, "ES", 10.0f, 0); // EndoScopy
		addAnEntry(config, "LS", 10.0f, 0); // Laser surface Scan
		addAnEntry(config, "PX", 10.0f, 0); // Panoramic X-Ray
		addAnEntry(config, "IO", 10.0f, 0); // Intra-oral Radiography
		addAnEntry(config, "CD", 10.0f, 0); // Color flow Doppler
		addAnEntry(config, "BI", 10.0f, 0); // Biomagnetic Imaging
		addAnEntry(config, "ST", 10.0f, 0); // Single-photon emission CT (SPECT)
		addAnEntry(config, "TG", 10.0f, 0); // Thermography
		addAnEntry(config, "GM", 10.0f, 0); // General Microscopy
		addAnEntry(config, "SM", 10.0f, 0); // Slide Microscopy
		// retired modalities
		addAnEntry(config, "DS", 10.0f, 0);	// Digital Subtraction Angiography --> XA
		addAnEntry(config, "AS", 10.0f, 0);	// AngioScopy --> XA
		addAnEntry(config, "CF", 10.0f, 0);	// Cine Fluorography --> RF
		addAnEntry(config, "VF", 10.0f, 0);	// Video Fluorography --> RF
		addAnEntry(config, "DF", 10.0f, 0);	// Digital Fluoroscopy --> RF
		addAnEntry(config, "LP", 10.0f, 0);	// LaParoscopy
		addAnEntry(config, "EC", 10.0f, 0);	// EchoCardiography --> XA
		addAnEntry(config, "MA", 10.0f, 0);	// Magnetic Resonance Angiography --> XA?
		addAnEntry(config, "MS", 10.0f, 0);	// Magnetic Resonance Spectroscopy
		addAnEntry(config, "FS", 10.0f, 0);	// Fundoscopy
		addAnEntry(config, "DM", 10.0f, 0);	// Digital Microscopy --> GM
		addAnEntry(config, "FA", 10.0f, 0);	// Fluorescein Angiography --> XA
		addAnEntry(config, "EC", 10.0f, 0);	// EchoCardiography
		addAnEntry(config, "CS", 10.0f, 0);	// CystoScopy
	}
}
