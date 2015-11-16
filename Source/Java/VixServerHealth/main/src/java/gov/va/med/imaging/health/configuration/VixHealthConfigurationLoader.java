/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 4, 2009
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
package gov.va.med.imaging.health.configuration;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.log4j.Logger;


/**
 * Loads and saves the VIX health configuration.  If the configuration file does not exist then
 * the default value is loaded. The default value will not be saved automatically.
 * 
 * There is a main function in this class used to create a new default configuration file.
 * 
 * @author vhaiswwerfej
 *
 */
public class VixHealthConfigurationLoader 
{
	private static final String VIX_HEALTH_CONFIG_FILENAME = "VIXHealthConfig.xml";
	private final static Logger logger = Logger.getLogger(VixHealthConfigurationLoader.class);
	
	private VixHealthConfiguration vixHealthConfiguration = new VixHealthConfiguration();
	
	private static String vixConfigurationDirectory = null;
	private final String realmSiteNumber;
	private final boolean listThreadPoolData;
	private final boolean listThreadData;
	private final boolean includeAwiv;
	private final boolean includeHdig;
	
	private static VixHealthConfigurationLoader loader = null;
	public synchronized static VixHealthConfigurationLoader getVixHealthConfigurationLoader()
	{
		if(loader == null)
		{
			loader = new VixHealthConfigurationLoader();
		}
		return loader;
	}
	
	private VixHealthConfigurationLoader()
	{
		super();		
		realmSiteNumber = null;
		this.listThreadData = false;
		this.listThreadPoolData = true;
		this.includeAwiv = false;
		this.includeHdig = false;
		loadConfiguration();
	}
	
	private VixHealthConfigurationLoader(String realmSiteNumber)
	{
		super();		
		this.realmSiteNumber = realmSiteNumber;
		this.listThreadData = false;
		this.listThreadPoolData = true;
		this.includeAwiv = false;
		this.includeHdig = false;
		loadConfiguration();
	}
	
	private VixHealthConfigurationLoader(String realmSiteNumber, boolean listThreadPoolData, 
			boolean listThreadData, boolean includeAwiv, boolean includeHdig)
	{
		super();
		this.realmSiteNumber = realmSiteNumber;		
		this.listThreadData = listThreadData;
		this.listThreadPoolData = listThreadPoolData;
		this.includeAwiv = includeAwiv;
		this.includeHdig = includeHdig;
		loadConfiguration();
	}

	/**
	 * @return the vixHealthConfiguration
	 */
	public VixHealthConfiguration getVixHealthConfiguration() {
		return vixHealthConfiguration;
	}

	/**
	 * @param vixHealthConfiguration the vixHealthConfiguration to set
	 */
	public void setVixHealthConfiguration(
			VixHealthConfiguration vixHealthConfiguration) {
		this.vixHealthConfiguration = vixHealthConfiguration;
	}

	private void loadDefaultConfiguration()
	{
		logger.info("Loading default VIX Health configurations");
		vixHealthConfiguration.loadDefaultConfigurations(this.realmSiteNumber, 
				this.listThreadPoolData, this.listThreadData,
				this.includeAwiv, this.includeHdig);		
	}
	
	/**
	 * Load the configuration, from the config file if one exists
	 * or from the default configuration.
	 */
	public void loadConfiguration()	
	{
		boolean fileLoadResult = false;
		synchronized (vixHealthConfiguration) 
		{
			try
			{
				fileLoadResult = loadConfigurationFromFile();
			}
			catch(VixHealthConfigurationException vhcX)
			{
				logger.error("Error loading from configuration file, " + vhcX.getMessage());
				fileLoadResult = false;
			}
			if(!fileLoadResult)
			{
				loadDefaultConfiguration();
			}
		}		
	}
	
	private boolean loadConfigurationFromFile()
	throws VixHealthConfigurationException
	{
		String filename = getConfigurationFilename();
		File file = new File(filename);
		if(file.exists())
		{
			XMLDecoder decoder = null;
			
			try
			{
				logger.info("Loading VIX Health configuration from file '" + filename + "'");
				decoder = new XMLDecoder(new FileInputStream(filename));
				vixHealthConfiguration =  (VixHealthConfiguration)decoder.readObject();
				return true;
			}
			catch(FileNotFoundException fnfX)
			{
				logger.error(fnfX.getMessage());
				return false;
			}
			finally
			{
				if(decoder != null)
					decoder.close();
			}			
		}
		else
		{
			return false;
		}		
	}
	
	private synchronized String getVixConfigurationDirectory() 
	throws VixHealthConfigurationException
	{
		String configDir = null;
		configDir = vixConfigurationDirectory;
		
		if (configDir == null)
		{
			configDir = System.getenv("vixconfig");
			if (configDir == null)
			{
				throw new VixHealthConfigurationException("The vixconfig has not been set.");
			}
			vixConfigurationDirectory = configDir;
		}
		return configDir;
	}
	
	/**
	 * Save the current configuration to the default filename (in vixconfig)
	 */
	public void saveConfiguration()
	{
		synchronized (vixHealthConfiguration) 
		{			
			XMLEncoder encoder = null;
			try
			{
				String filename = getConfigurationFilename();
				logger.info("Saving VIX Health configuration to file '" + filename + "'");
				encoder = new XMLEncoder(new FileOutputStream(filename));
				encoder.writeObject(this.vixHealthConfiguration);
			}
			catch(FileNotFoundException fnfX)
			{
				logger.error(fnfX.getMessage());
			}
			catch(VixHealthConfigurationException vhcX)
			{
				logger.error(vhcX.getMessage());
			}
			finally
			{
				if(encoder != null)
				{
					encoder.close();
				}
			}			
		}
	}
	
	private String getConfigurationFilename()
	throws VixHealthConfigurationException
	{
		String fileSpec = getVixConfigurationDirectory();
		if (!fileSpec.endsWith("\\") || !fileSpec.endsWith("/"))
		{
			fileSpec += "/";
		}
		fileSpec += VIX_HEALTH_CONFIG_FILENAME;
		return fileSpec;
	}
	
	/**
	 * Create a default VIX Health configuration file. 
	 * Can either pass 0, 1 or 3 arguments. 
	 * 
	 * If 0 arguments passed
	 * then the realm site number will not be known and the utility
	 * will not be able to retrieve details about the realm.
	 * 
	 *  If 1 argument is passed it must be the realm site number. This
	 *  is required to retrieve health information about the realm.
	 *  
	 *  If 3 arguments are passed, the first must be the realm site number. 
	 *  The second is a boolean to list the thread pool data, the third parameter
	 *  is a boolean to list the thread data.
	 *  
	 *  If 4 arguments are passed, the first must be the realm site number. 
	 *  The second is a boolean to list the thread pool data, the third parameter
	 *  is a boolean to list the thread data. The fourth parameter is a boolean to
	 *  indicate if AWIV properties should be included
	 *  
	 * @param args
	 */
	public static void main(String [] args)
	{		
		VixHealthConfigurationLoader vixHealthConfigLoader = null;
		if(args.length == 1)
		{		
			// set realm site number
			String realmSiteNumber = args[0];
			System.out.println("Realm site number '" + realmSiteNumber + "' provided, creating health configuration.");
			vixHealthConfigLoader = new VixHealthConfigurationLoader(realmSiteNumber);
		}
		else if(args.length == 3)
		{
			String realmSiteNumber = args[0];
			boolean listThreadPoolData = Boolean.parseBoolean(args[1]);
			boolean listThreadData = Boolean.parseBoolean(args[2]);
			System.out.println("Realm site number '" + realmSiteNumber + "', list thread pool data ["+ listThreadPoolData + "], list thread data [" + listThreadData + "], creating health configuration.");
			vixHealthConfigLoader = new VixHealthConfigurationLoader(realmSiteNumber, 
					listThreadPoolData, listThreadData, false, false);
		}
		else if(args.length == 4)
		{
			String realmSiteNumber = args[0];
			boolean listThreadPoolData = Boolean.parseBoolean(args[1]);
			boolean listThreadData = Boolean.parseBoolean(args[2]);
			boolean includeAwiv = Boolean.parseBoolean(args[3]);
			System.out.println("Realm site number '" + realmSiteNumber + "', list thread pool data ["+ listThreadPoolData + "], list thread data [" + listThreadData + "], creating health configuration.");
			vixHealthConfigLoader = new VixHealthConfigurationLoader(realmSiteNumber, 
					listThreadPoolData, listThreadData, includeAwiv, false);
		}
		else if(args.length == 5)
		{
			String realmSiteNumber = args[0];
			boolean listThreadPoolData = Boolean.parseBoolean(args[1]);
			boolean listThreadData = Boolean.parseBoolean(args[2]);
			boolean includeAwiv = Boolean.parseBoolean(args[3]);
			boolean includeHdig = Boolean.parseBoolean(args[4]);
			System.out.println("Realm site number '" + realmSiteNumber + "', list thread pool data ["+ listThreadPoolData + "], list thread data [" + listThreadData + "], creating health configuration.");
			vixHealthConfigLoader = new VixHealthConfigurationLoader(realmSiteNumber, 
					listThreadPoolData, listThreadData, includeAwiv, includeHdig);
		}
		else
		{
			// no realm site number, won't configure to include realm information
			System.out.println("No arguments provided - creating default configuration.");
			System.out.println("Possible parameters are:");
			System.out.println("<realm Site Number> <include thread pool data> <include thread data> <includeAWIV> <includeHDIG>");
			vixHealthConfigLoader = new VixHealthConfigurationLoader();
		}
		vixHealthConfigLoader.saveConfiguration();		
	}
}