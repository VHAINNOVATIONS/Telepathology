/**
 * 
 */
package gov.va.med.siteservice.interactive.commands;

import gov.va.med.imaging.datasource.Provider;
import gov.va.med.interactive.Command;
import gov.va.med.interactive.CommandParametersDescription;
import gov.va.med.interactive.CommandProcessor;
import gov.va.med.siteservice.SiteResolutionProvider;
import gov.va.med.siteservice.SiteResolutionProviderConfiguration;
import gov.va.med.siteservice.SiteServiceConfiguration;

/**
 * @author vhaiswbeckec
 *
 */
public class ClearCommand
extends Command<SiteResolutionProviderConfiguration>
{
	private static final CommandParametersDescription<?>[] commandParametersDescription = 
		new CommandParametersDescription[]
		{
			new CommandParametersDescription<Boolean>("siteService", Boolean.class, false), 
			new CommandParametersDescription<Boolean>("protocol", Boolean.class, false), 
			new CommandParametersDescription<Boolean>("protocolPreference", Boolean.class, false), 
			new CommandParametersDescription<Boolean>("repository", Boolean.class, false), 
		};
	public static CommandParametersDescription<?>[] getCommandParametersDescription()
	{
		return commandParametersDescription;
	}
	
	/**
	 * @param commandParameterValues
	 */
	public ClearCommand(String[] commandParameterValues)
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
		Boolean siteService = this.getParameterValue("siteService", Boolean.class);
		Boolean protocol = this.getParameterValue("protocol", Boolean.class);
		Boolean protocolPreference = this.getParameterValue("protocolPreference", Boolean.class);
		Boolean repository = this.getParameterValue("repository", Boolean.class);
		
		if(siteService.booleanValue())
			config.setSiteServiceConfiguration(
				SiteServiceConfiguration.createDefault(
					SiteResolutionProvider.getProviderConfiguration().getConfigurationDirectory()
				)
			);
		if(protocol.booleanValue() && config.getProtocolConfiguration() != null)
			config.getProtocolConfiguration().clear();
		if(protocolPreference.booleanValue() && config.getSiteProtocolPreferenceFactory() != null)
			config.getSiteProtocolPreferenceFactory().clear();
		if(repository.booleanValue() && config.getExternalArtifactSources() != null)
			config.getExternalArtifactSources().clear();
		
	}
}
