/**
 * 
 */
package gov.va.med.siteservice.interactive.commands;

import gov.va.med.interactive.Command;
import gov.va.med.interactive.CommandParametersDescription;
import gov.va.med.interactive.CommandProcessor;
import gov.va.med.siteservice.SiteResolutionProviderConfiguration;

/**
 * @author vhaiswbeckec
 *
 */
public class RemoveRepositoryCommand
extends Command<SiteResolutionProviderConfiguration>
{
	private static final CommandParametersDescription<?>[] commandParametersDescription = 
		new CommandParametersDescription[]
		{
			new CommandParametersDescription<String>("homeCommunityId", String.class, true), 
			new CommandParametersDescription<String>("repositoryId", String.class, true), 
		};

	public static CommandParametersDescription<?>[] getCommandParametersDescription()
	{
		return commandParametersDescription;
	}

	
	/**
	 * @param commandParameterValues
	 */
	public RemoveRepositoryCommand(String[] commandParameterValues)
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
		String homeCommunityId = this.getParameterValue("homeCommunityId", String.class);
		String repositoryId = this.getParameterValue("repositoryId", String.class);
		
		config.removeArtifactRepository(homeCommunityId, repositoryId);
	}
}
