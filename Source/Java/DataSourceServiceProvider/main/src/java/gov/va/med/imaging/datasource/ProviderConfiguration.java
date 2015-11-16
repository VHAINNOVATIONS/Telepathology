/**
 * 
 */
package gov.va.med.imaging.datasource;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.NoSuchElementException;
import org.apache.log4j.Logger;
import com.thoughtworks.xstream.XStream;

/**
 * A class to manage configuration persistence in a standard fashion
 * that supports both XMLEncoder and XStream persistence.  This is largely
 * based upon but should replace the configuration management that is 
 * within the Provider class.
 * 
 * The mode property determines the persistence mechanism to use
 * XMLENCODER - uses the Java XMLEncoder
 * XSTREAM - uses the XStream encoding
 * HYBRID (default) - preferentially reads using XMLENCODER then tries XSTREAM,
 *          always writes using XSTREAM
 * 
 * The HYBRID mode is intended as a bridge until the configuration files are all
 * updated to use XStream persistence, after which usage of the freakish nightmare 
 * that is the XMLEncoder will be mercifully over. 
 * 
 * @author vhaiswbeckec
 *
 */
public class ProviderConfiguration<T>
{
	protected static Logger getLogger()
	{
		return Logger.getLogger(ProviderConfiguration.class);
	}
	
	private transient Logger logger = Logger.getLogger(this.getClass()); 
	private final String providerName;
	private final double providerVersion;
	
	/**
	 * 
	 * @param providerName
	 * @param providerVersion
	 * @param mode
	 */
	public ProviderConfiguration(String providerName, double providerVersion)
	{
		super();
		this.providerName = providerName;
		this.providerVersion = providerVersion;
	}

	public String getProviderName()
	{
		return this.providerName;
	}

	public double getProviderVersion()
	{
		return this.providerVersion;
	}

	/**
	 * Get the configuration directory. Usually, derived classes do not need to
	 * access the directory and just rely on the storeConfiguration)( and
	 * loadConfiguration() methods. This method is provided for exceptional
	 * cases.
	 */
	public File getConfigurationDirectory()
	{
		String configurationDirectoryName = System.getenv("vixconfig");
		if (configurationDirectoryName == null)
			configurationDirectoryName = System.getProperty("user.home");
		if (configurationDirectoryName == null)
			configurationDirectoryName = "/";

		File configurationDirectory = new File(configurationDirectoryName);
		if (!configurationDirectory.exists())
			configurationDirectory.mkdirs(); // make the directories if they
												// don't exist

		return configurationDirectory;
	}

	/**
	 * 
	 * @param providerName
	 * @param providerVersion
	 * @return
	 */
	public String getConfigurationFileName()
	{
		File configurationDirectory = getConfigurationDirectory();

		return configurationDirectory.getAbsolutePath() + "/" + 
			getProviderName() + "-" + 
			Double.toString(getProviderVersion()) + ".config";
	}

	/**
	 * 
	 * @param configurationFileName
	 * @return
	 * @throws IOException
	 */
	public File getConfigurationFile() 
	throws IOException
	{
		File configurationFile = new File(getConfigurationFileName());
		if (!configurationFile.exists())
			configurationFile.createNewFile();

		return configurationFile;
	}

	/**
	 * This method reads the first Object from the configuration file and
	 * returns it. This method does the class loader switching necessary.
	 * 
	 * @return
	 * @throws IOException 
	 */
	public T loadConfiguration() 
	throws IOException
	{
		ClassLoader loader = null;
		String componentIdentification = "<unknown>";

		try
		{
			try
			{
				// hold onto the previous loader
				loader = Thread.currentThread().getContextClassLoader();
				// set the current thread class loader to the class loader of the
				Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
				componentIdentification = "<unknown, after class loader switch>";
			}
			catch (Throwable t)
			{
				// errors caught here indicate that we don't have the permission
				// needed
				// to change ClassLoader, check and fix the java policy file.
				getLogger().error("Error loading configuration in '" + componentIdentification
					+ "', the java security policy may be set too tight.", t);
				return null;
			}
			try
			{
				return internalLoad();
			}
			catch(IOException ioX)
			{
				getLogger().error("Error loading configuration in '" + componentIdentification + "'.", ioX);
				throw ioX;
			}
			catch(com.thoughtworks.xstream.io.StreamException sX)
			{
				getLogger().error("Error parsing configuration in '" + componentIdentification + "'.", sX);
				throw new IOException(sX);
			}
		}
		finally
		{
			// set the current thread class loader back
			if (loader != null)
				Thread.currentThread().setContextClassLoader(loader);
		}
	}

	/**
	 * 
	 * @param configFile
	 * @return
	 */
	private T internalLoad()
	throws IOException
	{
		InputStream inStream = null;
		
		try
		{
			logger.info("Loading configuration from '" + getConfigurationFile() + "'.");
			inStream = new FileInputStream(getConfigurationFile());
			assert inStream != null;
			
			// switched the order to XStream first and then XMLDecoder, because the
			// config files are all being switched to XStream
			try
			{
				return loadUsingXStream(inStream);
			}
			catch(com.thoughtworks.xstream.converters.ConversionException cX)
			{
				// close and re-open config file
				try{inStream.close();}
				catch(Throwable t){}
				inStream = new FileInputStream(getConfigurationFile());

				return loadUsingXMLEncoder(inStream);
			}
			// NoSuchElementException is the exception that XMLDecoder throws when
			// it is asked to decode a string that XMLEncoder did not write, determined empirically
			//catch(NoSuchElementException nseX)		 
			//{
			//}
		}
		catch (IOException ioX)
		{
			throw ioX;
		}
		finally
		{
			try{inStream.close();}
			catch(Throwable t){}
		}
	}

	/**
	 * Store the configuration object to the configuration file. What the
	 * configuration object is, is up to the implementation classes.
	 * 
	 * @param configuration
	 */
	public void store(T configuration)
	throws IOException
	{
		FileOutputStream outStream = null;
		try
		{
			outStream = new FileOutputStream(getConfigurationFile());
			// always store in XStream now
			storeUsingXStream(outStream, configuration);
		}
		catch(IOException ioX)
		{
			throw ioX;
		}
		finally
		{
			try{outStream.close();}
			catch(Throwable t){}
		}
	}
	
	private void storeUsingXStream(OutputStream outStream, T configuration) 
	throws IOException
	{
		logger.info("Saving configuration '" + configuration.toString() + "' using XStream.");
		XStream xstream = new XStream();
		xstream.toXML(configuration, outStream);
		logger.info("Configuration saved using XStream.");
	}
	
	@SuppressWarnings("unchecked")
	private T loadUsingXStream(InputStream inStream)
	{
		logger.info("Loading configuration using XStream");
		XStream xstream = new XStream();
		T config = (T)xstream.fromXML(inStream);
		logger.info("Configuration loaded using XStream and is " + (config == null ? "NULL" : "NOT NULL") );
		
		return config;
	}
	
	private void storeUsingXMLEncoder(OutputStream outStream, T configuration) 
	throws IOException
	{
		logger.info("Saving configuration '" + configuration.toString() + "' using XMLEncoder.");

		XMLEncoder xmlEncoder = new XMLEncoder(outStream);
		xmlEncoder.writeObject(configuration);
		xmlEncoder.close();
	}
	
	@SuppressWarnings("unchecked")
	private T loadUsingXMLEncoder(InputStream inStream) 
	throws IOException
	{
		logger.info("Loading configuration using XMLDecoder");

		XMLDecoder xmlDecoder = new XMLDecoder(inStream);
		T config = (T)xmlDecoder.readObject();
		logger.info("Configuration loaded using XMLDecoder and is " + (config == null ? "NULL" : "NOT NULL") );
		xmlDecoder.close();
		return config;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Object> T serializeAndDeserializeByXStreamTest(T src)
	{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream(4096);
		XStream xstreamOut = new XStream();
		xstreamOut.toXML(src, outStream);

		ByteArrayInputStream inStream = new ByteArrayInputStream( outStream.toByteArray() );
		XStream xstreamIn = new XStream();
		T result = (T)xstreamIn.fromXML(inStream);
		return result;
	}

}
