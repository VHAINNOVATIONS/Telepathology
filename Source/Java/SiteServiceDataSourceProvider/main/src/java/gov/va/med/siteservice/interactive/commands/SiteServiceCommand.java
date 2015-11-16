/**
 * 
 */
package gov.va.med.siteservice.interactive.commands;

import java.io.File;
import java.net.URI;
import gov.va.med.imaging.datasource.Provider;
import gov.va.med.interactive.Command;
import gov.va.med.interactive.CommandParametersDescription;
import gov.va.med.interactive.CommandProcessor;
import gov.va.med.siteservice.SiteResolutionProviderConfiguration;
import gov.va.med.siteservice.SiteServiceConfiguration;

/**
 * @author vhaiswbeckec
 *
 */
public class SiteServiceCommand
extends Command<SiteResolutionProviderConfiguration>
{
	private static final CommandParametersDescription<?>[] commandParametersDescription = 
		new CommandParametersDescription[]
		{
			new CommandParametersDescription<URI>("uri", URI.class, true), 
			new CommandParametersDescription<String>("siteCacheFile", String.class, false), 
			new CommandParametersDescription<String>("regionCacheFile", String.class, false), 
			new CommandParametersDescription<Integer>("refreshHour", Integer.class, false), 
			new CommandParametersDescription<Integer>("refreshMinimumDelay", Integer.class, false), 
			new CommandParametersDescription<Long>("refreshPeriod", Long.class, false), 
		};
	public static CommandParametersDescription<?>[] getCommandParametersDescription()
	{
		return commandParametersDescription;
	}
	
	private Provider provider;

	/**
	 * @param commandParameterValues
	 */
	public SiteServiceCommand(String[] commandParameterValues)
	{
		super(commandParameterValues);
	}
	
	/**
	 * @see gov.va.med.interactive.Command#processCommand(gov.va.med.interactive.CommandProcessor, java.lang.Object)
	 */
	@Override
	public void processCommand(CommandProcessor<SiteResolutionProviderConfiguration> processor, SiteResolutionProviderConfiguration config)
	throws Exception
	{
		SiteServiceConfiguration ssConfig = config.getSiteServiceConfiguration();
		if(ssConfig == null)
		{
			ssConfig = SiteServiceConfiguration.createDefault(new File("C:/VixConfig"));
			config.setSiteServiceConfiguration(ssConfig);
		}
		
		URI uri = this.getParameterValue("uri", URI.class);
		String siteCacheFile = 
			this.isParameterExists("siteCacheFile") ? 
				this.getParameterValue("siteCacheFile", String.class) : ssConfig.getSiteServiceCacheFileName(); 
				
		String regionCacheFile = 
			this.isParameterExists("regionCacheFile") ? 
				this.getParameterValue("regionCacheFile", String.class) : ssConfig.getRegionListCacheFileName();
				
		Integer refreshHour = 
			this.isParameterExists("refreshHour") ? 
				this.getParameterValue("refreshHour", Integer.class) : ssConfig.getRefreshHour();
				
		Integer refreshMinimumDelay = 
			this.isParameterExists("refreshMinimumDelay") ? 
				this.getParameterValue("refreshMinimumDelay", Integer.class) : ssConfig.getRefreshMinimumDelay();
				
		Long refreshPeriod = 
			this.isParameterExists("refreshPeriod") ? 
				this.getParameterValue("refreshPeriod", Long.class) : ssConfig.getRefreshPeriod();

		ssConfig.setRefreshHour(refreshHour.intValue());
		ssConfig.setRefreshMinimumDelay(refreshMinimumDelay.intValue());
		ssConfig.setRefreshPeriod(refreshPeriod.intValue());
		ssConfig.setRegionListCacheFileName(regionCacheFile);
		ssConfig.setSiteServiceCacheFileName(siteCacheFile);
		ssConfig.setSiteServiceUri(uri);
	}
}
