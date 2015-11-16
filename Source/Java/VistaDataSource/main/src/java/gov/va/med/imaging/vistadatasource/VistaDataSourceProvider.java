/**
 * 
 */
package gov.va.med.imaging.vistadatasource;

import gov.va.med.imaging.datasource.*;
import gov.va.med.imaging.vistadatasource.configuration.VistaDataSourceConfiguration;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

/**
 * @author VHAISWBECKEC
 * 
 */
public class VistaDataSourceProvider 
extends Provider
{
	private static final String PROVIDER_NAME = "VistaDataSource";
	private static final double PROVIDER_VERSION = 1.0d;
	private static final String PROVIDER_INFO = 
		"Implements: \n" + 
		"PatientDataSource SPI \n" + 
		"backed by a VistA data store.";

	private static final long serialVersionUID = 1L;	
	
	private final SortedSet<ProviderService> services;
	private static VistaDataSourceConfiguration vistaConfiguration = null;
	private final static Logger logger = Logger.getLogger(VistaDataSourceProvider.class);

	/**
	 * The public "nullary" constructor that is used by the ServiceLoader class
	 * to create instances.
	 */
	public VistaDataSourceProvider()
	{
		this(PROVIDER_NAME, PROVIDER_VERSION, PROVIDER_INFO);
	}	
	
	public VistaDataSourceProvider(VistaDataSourceConfiguration vistaConfiguration)
	{
		this();
		VistaDataSourceProvider.vistaConfiguration = vistaConfiguration;
	}

	/**
	 * @param name
	 * @param version
	 * @param info
	 */
	private VistaDataSourceProvider(String name, double version, String info)
	{
		super(name, version, info);

		services = new TreeSet<ProviderService>();		
		services.add(
			new ProviderService(
				this,
				PatientDataSourceSpi.class,
				VistaPatientDataSourceService.SUPPORTED_PROTOCOL,
				1.0F,
				VistaPatientDataSourceService.class)
		);
		services.add(
			new ProviderService(
				this,
				UserAuthorizationDataSourceSpi.class,
				VistaUserAuthorizationDataSourceProvider.SUPPORTED_PROTOCOL,
				1.0F,
				VistaUserAuthorizationDataSourceProvider.class)
		);
		services.add(
			new ProviderService(
				this,
				UserAuthenticationSpi.class,
				VistaUserAuthenticationDataSourceProvider.SUPPORTED_PROTOCOL,
				1.0F,
				VistaUserAuthenticationDataSourceProvider.class)
		);
		services.add(
				new ProviderService(
					this,
					SiteDataSourceSpi.class,
					VistaSiteDataSourceService.SUPPORTED_PROTOCOL,
					1.0F,
					VistaSiteDataSourceService.class)
			);
		
			
		// VistaDelegateRedirector is a "local" service, it has no protocol
		// or protocol version and is instantiated with the null-arg
		// constructor
		services.add(
			new ProviderService(
				this,
				RoutingOverrideSpi.class,
				(byte)1, 
				VistaDelegateRedirector.class)
			);
		
		// load the ExchangeConfiguration if it exists
			synchronized(VistaDataSourceProvider.class)
		    {
				try
				{
					if(vistaConfiguration == null)
						vistaConfiguration = (VistaDataSourceConfiguration)loadConfiguration();
				}
				catch(ClassCastException ccX)
				{
					logger.error("Unable to load configuration because the configuration file is invalid.", ccX);
				}
		    }
		
	}
	

	@Override
	public SortedSet<ProviderService> getServices()
	{
		return Collections.unmodifiableSortedSet(services);
	}
	
	/**
	 * 
	 */
	@Override
	public void storeConfiguration()
    {
	    storeConfiguration(getVistaConfiguration());
    }
	
	/**
	 * A package level method for SPI implementation to get the
	 * Configuration.
	 * 
	 * @return
	 */
	static VistaDataSourceConfiguration getVistaConfiguration()
	{
		if(vistaConfiguration == null)
			logger.error("VistaDataSourceConfiguration is null, possibly called before VistaDataSourceProvider was instantiated.");
		
		return vistaConfiguration;
	}
	
	public static void main(String [] args)
	{
		System.out.println("Creating vista datasource configuration file");
		VistaDataSourceConfiguration vistaConfiguration = null;
		if(args == null || args.length <= 0)
		{
			vistaConfiguration = VistaDataSourceConfiguration.createDefaultConfiguration();
		}
		else
		{
			boolean internalTestEnvironment = false;
			try
			{
				internalTestEnvironment = Boolean.parseBoolean(args[0]);
			}
			catch(Exception ex)
			{
				logger.error("Error parsing '" + args[0] + "', " + ex.getMessage());
			}
			vistaConfiguration = new VistaDataSourceConfiguration();
			vistaConfiguration.setInternalTestEnvironment(internalTestEnvironment);
		}
				
		VistaDataSourceProvider provider = new VistaDataSourceProvider(vistaConfiguration);
		provider.storeConfiguration();
		System.out.println("Configuration file saved to '" + provider.getConfigurationFileName() + "'.");
	}
}
