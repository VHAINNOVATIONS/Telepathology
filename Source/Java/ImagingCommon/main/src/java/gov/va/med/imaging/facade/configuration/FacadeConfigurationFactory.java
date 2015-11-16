/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr 15, 2010
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

import gov.va.med.imaging.facade.configuration.exceptions.CannotLoadConfigurationException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Factory for creating and managing configuration files.  All access to configuration files should be done through
 * this factory, if a configuration file is needed, it should be requested from this factory.  The first time
 * a configuration file is requested it is cached for future uses.  Subsequent requests for the configuration file
 * will get the cached copy.
 * <br><br>
 * When a configuration file is updated, clearConfiguration should be called which will force the new configuration
 * file to be loaded the next time it is requested.
 * 
 * @author vhaiswwerfej
 *
 */
public class FacadeConfigurationFactory
{
	private final static Logger logger = 
		Logger.getLogger(FacadeConfigurationFactory.class);
	
	
	private static FacadeConfigurationFactory configurationFactory = 
		new FacadeConfigurationFactory();
	
	/**
	 * Get the single instance of the configuration factory.
	 * @return
	 */
	public static FacadeConfigurationFactory getConfigurationFactory()
	{
		return configurationFactory;
	}
	
	private List<AbstractBaseFacadeConfiguration> configurations = new ArrayList<AbstractBaseFacadeConfiguration>();
	
	private FacadeConfigurationFactory()
	{
		super();
	}
	
	/**
	 * 
	 * @param <T> The type of the configuration file requesting
	 * @param configurationClass The class of the configuration file requesting
	 * @return The configuration file
	 * @throws CannotLoadConfigurationException If the configuration file cannot be loaded for some reason, this exception is thrown
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractBaseFacadeConfiguration> T getConfiguration(Class<T> configurationClass)
	throws CannotLoadConfigurationException
	{
		for(AbstractBaseFacadeConfiguration config : configurations)
		{
			if(config.getClass() == configurationClass)
			{
				return (T)config;
			}
		}
		
		T configuration = createAndLoadConfiguration(configurationClass);
		if(configuration != null) // should never be null, should throw exception if there is a problem
		{
			configuration = (T)configuration.loadConfiguration();
			synchronized(configurations)
			{
				configurations.add(configuration);
			}
		}
		return configuration;
	}
	
	private <T extends AbstractBaseFacadeConfiguration> T createAndLoadConfiguration(Class<T> configurationClass)
	throws CannotLoadConfigurationException
	{
		try
		{			
			// there must be a default constructor so the XmlEncoder/Decoder can work
			logger.info("Creating new configuration file of type '" + configurationClass.getName() + "'.");
			Constructor<T> constructor = configurationClass.getConstructor(new Class<?>[] {});
			return (T)constructor.newInstance(new Object[] {});		
		}
		catch(NoSuchMethodException nsmX)
		{
			String msg = "NoSuchMethodException while creating new instance of configuration '" + configurationClass.getName() + "'.";
			logger.error(msg, nsmX);
			throw new CannotLoadConfigurationException(msg, nsmX);
		}
		catch(InvocationTargetException itX)
		{
			String msg = "InvocationTargetException while creating new instance of configuration '" + configurationClass.getName() + "'.";
			logger.error(msg, itX);
			throw new CannotLoadConfigurationException(msg, itX);
		}
		catch(InstantiationException iX)
		{
			String msg = "InstantiationException while creating new instance of configuration '" + configurationClass.getName() + "'.";
			logger.error(msg, iX);
			throw new CannotLoadConfigurationException(msg, iX);
		}
		catch(IllegalAccessException iaX)
		{
			String msg = "IllegalAccessException while creating new instance of configuration '" + configurationClass.getName() + "'.";
			logger.error(msg, iaX);
			throw new CannotLoadConfigurationException(msg, iaX);
		}
	}
	
	/**
	 * Clear the configuration file from the factory cache. This method should be called after a configuration file has been modified on disk
	 * @param <T>
	 * @param configurationClass
	 */
	public <T extends AbstractBaseFacadeConfiguration> void clearConfiguration(Class<T> configurationClass)
	{
		synchronized(configurations)
		{
			for(AbstractBaseFacadeConfiguration config : configurations)
			{
				if(config.getClass() == configurationClass)
				{
					// mark the configuration as dirty so any existing configuration pointers are aware their 
					// configuration is old and can be refreshed (if desired)
					config.setDirty(true);
					configurations.remove(config);
					break;
				}
			}
		}
	}
	
	/**
	 * Get the number of configuration files in the factory cache, mainly used for debug purposes
	 * @return
	 */
	public int getConfigurationEntrySize()
	{
		return configurations.size();
	}

}
