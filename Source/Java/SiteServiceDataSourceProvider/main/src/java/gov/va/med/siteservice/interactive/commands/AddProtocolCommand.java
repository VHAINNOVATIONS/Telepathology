/**
 * 
 */
package gov.va.med.siteservice.interactive.commands;

import gov.va.med.interactive.Command;
import gov.va.med.interactive.CommandParametersDescription;
import gov.va.med.interactive.CommandProcessor;
import gov.va.med.siteservice.ProtocolServerConfiguration;
import gov.va.med.siteservice.SiteResolutionProviderConfiguration;

/**
 * @author vhaiswbeckec
 *
 */
public class AddProtocolCommand
extends Command<SiteResolutionProviderConfiguration>
{
	private static final CommandParametersDescription<?>[] commandParametersDescription = 
		new CommandParametersDescription[]
		{
			new CommandParametersDescription<String>("protocol", String.class, true), 
			new CommandParametersDescription<String>("application", String.class, true), 
			new CommandParametersDescription<String>("metadataPath", String.class, true), 
			new CommandParametersDescription<String>("imagePath", String.class, true), 
			new CommandParametersDescription<Boolean>("vista", Boolean.class, false), 
		};
	public static CommandParametersDescription<?>[] getCommandParametersDescription()
	{
		return commandParametersDescription;
	}
	
	/**
	 * @param commandParameterValues
	 */
	public AddProtocolCommand(String[] commandParameterValues)
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
		String protocol = this.getParameterValue("protocol", String.class);
		String application = this.getParameterValue("application", String.class);
		String metadataPath = this.getParameterValue("metadataPath", String.class);
		String imagePath = this.getParameterValue("imagePath", String.class);
		Boolean vista = this.getParameterValue("vista", Boolean.class);
		
		ProtocolServerConfiguration protocolConfiguration = new ProtocolServerConfiguration(
			vista == null ? false : vista.booleanValue(),
			application,
			metadataPath,
			imagePath
		);
		
		config.addProtocolConfiguration(protocol, protocolConfiguration );
	}
}
