package gov.va.med.siteservice;

import gov.va.med.ProtocolHandlerUtility;
import gov.va.med.imaging.datasource.Provider;
import gov.va.med.imaging.datasource.ProviderConfiguration;
import gov.va.med.imaging.datasource.ProviderService;
import gov.va.med.imaging.datasource.SiteResolutionDataSourceSpi;
import gov.va.med.interactive.CommandController;
import gov.va.med.interactive.CommandFactory;
import gov.va.med.siteservice.interactive.SiteServiceConfigurationCommandFactory;
import java.io.IOException;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * The Provider implementation that provides information on the Services in this
 * package.
 * 
 * @author VHAISWBECKEC
 * 
 */
public class SiteResolutionProvider 
extends Provider
{
	public static final String PROVIDER_NAME;
	public static final float PROVIDER_VERSION;
	private static final String PROVIDER_INFO;
	private static final String SITE_SERVICE_PROTOCOL;
	private static final float SITE_SERVICE_PROTOCOL_VERSION;

	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(SiteResolutionProvider.class);
	
	// static constructor to load constants and configuration
	static
	{
		PROVIDER_NAME = 
			Messages.getString("SiteResolutionProvider.providerName"); //$NON-NLS-1$
		PROVIDER_VERSION = Float.parseFloat(
			Messages.getString("SiteResolutionProvider.providerVersion") );
		PROVIDER_INFO = 
			Messages.getString("SiteResolutionProvider.providerDescription"); //$NON-NLS-1$
		
		SITE_SERVICE_PROTOCOL = Messages.getString("SiteResolutionProvider.siteServiceProtocol");
		SITE_SERVICE_PROTOCOL_VERSION = Float.parseFloat(
				Messages.getString("SiteResolutionProvider.siteServiceProtocolVersion") );
	}

	private static final ProviderConfiguration<SiteResolutionProviderConfiguration> providerConfiguration =
		new ProviderConfiguration<SiteResolutionProviderConfiguration>(
			PROVIDER_NAME, 
			PROVIDER_VERSION);
	public static ProviderConfiguration<SiteResolutionProviderConfiguration> getProviderConfiguration()
	{
		return providerConfiguration;
	}
	
	// ========================================================================================
	// Instance Members
	// ========================================================================================
	
	private final SortedSet<ProviderService> services;
	private SiteResolutionProviderConfiguration configuration;
	
	/**
	 * The public no-arg constructor that is used by the ServiceLoader class
	 * to create instances.
	 */
	public SiteResolutionProvider()
	{
		this(PROVIDER_NAME, PROVIDER_VERSION, PROVIDER_INFO);
	}
	
	/**
	 * @param name
	 * @param version
	 * @param info
	 */
	private SiteResolutionProvider(String name, double version, String info)
	{
		super(name, version, info);

		services = new TreeSet<ProviderService>();
		
		logger.info(
				"SiteResolutionProvider adding service [" + SiteResolutionDataSourceSpi.class.getSimpleName() + 
				"] " + SITE_SERVICE_PROTOCOL + " V" + SITE_SERVICE_PROTOCOL_VERSION + 
				" implemented by '" + SiteResolver.class.getName() + "'.");		
		services.add(
			new ProviderService(
				this, 
				SiteResolutionDataSourceSpi.class, 
				(byte)0,
				SiteResolver.class)
		);

		try
		{
			configuration = providerConfiguration.loadConfiguration();
			if(configuration != null)
			{
				logger.info("SiteResolutionProvider configuration successfully loaded.");
				//logger.info(getConfiguration().toString());
			}
			else
				logger.info("SiteResolutionProvider configuration not loaded.");
		}
		catch (IOException x)
		{
			logger.warn("SiteResolutionProvider configuration NOT loaded.", x);
		}
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public SiteResolutionProviderConfiguration getInstanceConfiguration()
    {
		return this.configuration;
    }
	
	/**
	 * This setter is only used for development testing and should not be used in production.
	 * @param configuration
	 */
	void setConfiguration(SiteResolutionProviderConfiguration configuration)
	{
		logger.info("Attempting to set SiteResolutionProvider configuration through accessor, this should only be done for testing.");
		if(this.configuration == null)
			this.configuration = configuration;
		else
			logger.error("Attempt to set SiteResolutionProvider configuration after it has already been set is being ignored.");
	}

	@Override
	public SortedSet<ProviderService> getServices()
	{
		return Collections.unmodifiableSortedSet(services);
	}

	// ======================================================================================
	// Configuration
	// ======================================================================================

	/**
	 */
	public static void main(String[] argv)
	{
		Logger.getRootLogger().setLevel(Level.ALL);
		ProtocolHandlerUtility.initialize(true);
		
		SiteResolutionProvider provider = new SiteResolutionProvider();
		try
		{
			CommandFactory<SiteResolutionProviderConfiguration> factory = 
				new SiteServiceConfigurationCommandFactory(provider);
			SiteResolutionProviderConfiguration config = provider.getInstanceConfiguration();
			if(config == null)
			{
				logger.warn("Unable to load configuration, creating default (blank) configuration.");
				config = new SiteResolutionProviderConfiguration();
				provider.setConfiguration(config);
			}
			
			gov.va.med.interactive.CommandController<SiteResolutionProviderConfiguration> commandController = 
				new CommandController<SiteResolutionProviderConfiguration>(
					config, 
					factory,
					argv
				);
			commandController.getCommandSource().pushCommands(argv);
			commandController.run();
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}

		System.exit(0);
	}
}
