/**
 * 
 */
package gov.va.med.siteservice.interactive.commands;

import gov.va.med.imaging.datasource.Provider;
import gov.va.med.interactive.Command;
import gov.va.med.interactive.CommandParametersDescription;
import gov.va.med.interactive.CommandProcessor;
import gov.va.med.siteservice.SiteResolutionProviderConfiguration;

/**
 * @author vhaiswbeckec
 *
 */
public class ListCommand
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
	 * 
	 */
	public ListCommand()
	{
		super();
	}

	/**
	 * @param commandParameterValues
	 */
	public ListCommand(String[] commandParameterValues)
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
		Boolean external = this.getParameterValue("external", Boolean.class);
		
		if(siteService.booleanValue())
			System.out.println( config.getSiteServiceConfiguration().toString() );
		if(protocol.booleanValue())
			System.out.println( config.getProtocolConfiguration().toString() );
		if(protocolPreference.booleanValue())
			System.out.println( config.getSiteProtocolPreferenceFactory().toString() );
		if(external.booleanValue())
			System.out.println( config.getExternalArtifactSources().toString() );
		
	}
}
