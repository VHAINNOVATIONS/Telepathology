/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jan 7, 2009
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
package gov.va.med.imaging.facade.configuration;

import java.beans.XMLDecoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;

/**
 * @author vhaiswwerfej
 *
 */
public abstract class AbstractBaseFacadeConfiguration 
{
	protected final static Logger logger = 
		Logger.getLogger(AbstractBaseFacadeConfiguration.class);
	
	private boolean dirty;
	
	/**
	 * Indicates if the configuration has been updated on disk and this in memory configuration object may be
	 * considered dirty. This doesn't mean the configuration has to be refreshed, that is up to the developer/usage
	 * 
	 * @return
	 */
	protected boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	protected Logger getLogger()
	{
		return logger;
	}
	
	//private final String configurationFilename;
	
	public AbstractBaseFacadeConfiguration()
	{		
		//this.configurationFilename = filename;
	}
	
	public AbstractBaseFacadeConfiguration loadConfiguration()
	{
		AbstractBaseFacadeConfiguration config = loadConfigurationFromFile();
		if(config == null)
		{
			config = loadDefaultConfiguration();
			config.storeConfiguration();
		}
		return config;
	}		
	
	
	private AbstractBaseFacadeConfiguration loadConfigurationFromFile()
	{
		FileInputStream inputStream = null;
		try
		{
			File file = new File(getConfigurationFileName());
			if(file.exists())
			{			
				try
				{
					inputStream = new FileInputStream(file);
					XStream xstream = getXtream();
					AbstractBaseFacadeConfiguration configuration = 
						(AbstractBaseFacadeConfiguration)xstream.fromXML(inputStream);
					return configuration;
				}
				catch(com.thoughtworks.xstream.converters.ConversionException cX)
				{
					try 
					{
						inputStream.close();
						inputStream = null;
					}
					catch(Exception ex) {}
					inputStream = new FileInputStream(file);
					XMLDecoder decoder = null;
					try
					{
						decoder = new XMLDecoder(new FileInputStream(file.getAbsolutePath()));
						AbstractBaseFacadeConfiguration configuration = (AbstractBaseFacadeConfiguration)decoder.readObject();
						logger.info("Loaded configuration file [" + file.getAbsolutePath() + "]");
						return configuration;
					}
					finally
					{
						if(decoder != null)
							decoder.close();
					}
				}
			}
			else
			{
				logger.error("File [" + file.getAbsolutePath() + "] does not exist");
				return null;
			}
		}
		catch(FileNotFoundException fnfX)
		{
			logger.error("Error reading configuration, " + fnfX.getMessage(), fnfX);
			return null;
		}
		finally
		{
			if(inputStream != null)
			{
				try {inputStream.close();}
				catch(Exception ex) {}
			}
		}
	}
	
	public abstract AbstractBaseFacadeConfiguration loadDefaultConfiguration();
	
	
	public synchronized void storeConfiguration()
	{
		FileOutputStream output = null;
		try
		{
			String filename = getConfigurationFileName();
			output = new FileOutputStream(filename, false);
			XStream xstream = getXtream();
			xstream.toXML(this, output);
			logger.info("Stored configuration file [" + filename + "]");
		}
		catch(IOException ioX)
		{
			logger.error("Error storing configuration, " + ioX.getMessage(), ioX);
		}
		finally
		{
			if(output != null)
			{
				try {output.close();}
				catch(Exception ex) {}
			}
		}		
	}
	
	private XStream getXtream()
	{
		XStream xstream = new XStream();
		String [] omittedFields = getOmittedFields();
		if(omittedFields != null && omittedFields.length > 0)
		{
			for(String omittedField : omittedFields)
			{
				xstream.omitField(getClass(), omittedField);		
			}
		}
		
		xstream.registerConverter(new EncryptedConfigurationPropertyStringSingleValueConverter());
		return xstream;
	}
	
	/**
	 * Deletes the configuration file from disk (does not affect configurations in memory). 
	 * This method is primarily used for testing purposes, there are not any real reasons in production why a config file
	 * should be deleted. 
	 */
	public synchronized boolean deleteConfiguration()
	{
		String filename = getConfigurationFileName();
		logger.info("Deleting configuration file '" + filename + "'.");
		File file = new File(filename);
		if(file.exists())
		{
			if(!file.delete())
			{
				logger.error("Cannot delete file.");
				return false;				
			}
		}
		return true;
	}
	
	/**
	 * Get the configuration directory.
	 * Usually, derived classes do not need to access the directory
	 * and just rely on the storeConfiguration)( and loadConfiguration()
	 * methods.  This method is provided for exceptional cases.
	 */
	private File getConfigurationDirectory()
	{
		String configurationDirectoryName = System.getenv("vixconfig");
		if(configurationDirectoryName == null)
			configurationDirectoryName = System.getProperty("user.home");
		if(configurationDirectoryName == null)
			configurationDirectoryName = "/";
		
		File configurationDirectory = new File(configurationDirectoryName);
		if(! configurationDirectory.exists())
			configurationDirectory.mkdirs();		// make the directories if they don't exist
		
		return configurationDirectory;
	}
	
	/**
	 * Get a reference to the configuration file.
	 * This method will create the configuration and parent directories if they do not exist.
	 * If the Provider does not have a configuration file then this method returns null.
	 * @return
	 * @throws IOException 
	 */
	/*
	private File getConfigurationFile() 
	throws IOException
	{
		String configurationFileName = getConfigurationFileName();
		if( configurationFileName == null)
			return null;
		File configurationFile = new File(configurationFileName);
		if(! configurationFile.exists())
			configurationFile.createNewFile();
		
		return configurationFile;
	}*/
	
	/**
	 * Build a filename in the standardized format from the
	 * provider name and version.  A Provider that does not
	 * have any persistent configuration must override this 
	 * method to return null.
	 * This method will assure that the parent directory exists
	 * before returning.  It will NOT create the configuration
	 * file if it does not exist.
	 * 
	 * The preferred store locations are (in order):
	 * 1.) The directory of the VIX configuration
	 * 2.) The user home directory
	 * 3.) The root directory
	 * 
	 * @return
	 */
	private String getConfigurationFileName()
	{
		File configurationDirectory = getConfigurationDirectory();	
		return configurationDirectory.getAbsolutePath() + "/" + this.getClass().getSimpleName() + ".config"; 
	}
	
	/**
	 * Override this method if you would like to have fields omitted from the XML output. Any field name listed here will not be included 
	 * when this configuration object is written to disk but it still can be read from disk if it exists in the XML
	 * @return
	 */
	private String [] getOmittedFields()
	{
		List<String> omittedFields = new ArrayList<String>();
		Class<?> clazz = this.getClass();
		
		Field [] fields = clazz.getDeclaredFields();
		
		//Field [] fields = clazz.getFields();
		for(Field field : fields)
		{
			if(field.isAnnotationPresent(HiddenConfigurationField.class))
			{
				omittedFields.add(field.getName());
			}
			else if(field.isAnnotationPresent(HiddenStringConfigurationField.class))
			{
				if(isStringFieldDefaultValue(field))
				{
					omittedFields.add(field.getName());
				}				
			}
			else if(field.isAnnotationPresent(HiddenBooleanConfigurationField.class))
			{
				if(isBooleanFieldDefaultValue(field))
					omittedFields.add(field.getName());
			}
			else if(field.isAnnotationPresent(HiddenIntConfigurationField.class))
			{
				if(isIntFieldDefaultValue(field))
					omittedFields.add(field.getName());
			}
		}
		return omittedFields.toArray(new String[omittedFields.size()]);
	}
	
	private boolean isIntFieldDefaultValue(Field field)
	{
		try
		{
			field.setAccessible(true);
			int value = field.getInt(this);
			return value == 0;
		}
		catch (IllegalArgumentException e)
		{
			logger.error("IllegalArgumentException reading property value, " + e.getMessage());
		} 
		catch (IllegalAccessException e)
		{
			logger.error("IllegalAccessException reading property value, " + e.getMessage());
		} 
		return false;
	}
	
	private boolean isBooleanFieldDefaultValue(Field field)
	{
		try
		{
			field.setAccessible(true);
			boolean value = field.getBoolean(this);
			return value == false;
		}
		catch (IllegalArgumentException e)
		{
			logger.error("IllegalArgumentException reading property value, " + e.getMessage());
		} 
		catch (IllegalAccessException e)
		{
			logger.error("IllegalAccessException reading property value, " + e.getMessage());
		} 
		return false;
	}
	
	private boolean isStringFieldDefaultValue(Field field)
	{
		try
		{
			field.setAccessible(true);
			Object value = field.get(this);
			return value == null;
		}
		catch (IllegalArgumentException e)
		{
			logger.error("IllegalArgumentException reading property value, " + e.getMessage());
		} 
		catch (IllegalAccessException e)
		{
			logger.error("IllegalAccessException reading property value, " + e.getMessage());
		} 
		return false;
	}
}
