/**
 * 
 */
package gov.va.med.siteservice.interactive.commands;

import gov.va.med.RoutingTokenImpl;
import gov.va.med.interactive.Command;
import gov.va.med.interactive.CommandParametersDescription;
import gov.va.med.interactive.CommandProcessor;
import gov.va.med.siteservice.ExternalArtifactSources;
import gov.va.med.siteservice.SiteResolutionProviderConfiguration;
import gov.va.med.siteservice.StaticExternalArtifactSources;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author vhaiswbeckec
 *
 */
public class AddIndirectionCommand
extends Command<SiteResolutionProviderConfiguration>
{
	private static final CommandParametersDescription<?>[] commandParametersDescription = 
		new CommandParametersDescription[]
		{
			new CommandParametersDescription<String>("referantHomeCommunityId", String.class, true), 
			new CommandParametersDescription<String>("referantRepositoryId", String.class, true), 
			new CommandParametersDescription<String>("referredHomeCommunityId", String.class, true), 
			new CommandParametersDescription<String>("referredRepositoryId", String.class, true), 
		};
	public static CommandParametersDescription<?>[] getCommandParametersDescription()
	{
		return commandParametersDescription;
	}

	/**
	 * @param commandParameterValues
	 */
	public AddIndirectionCommand(String[] commandParameterValues)
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
		String referantHomeCommunityId = this.getParameterValue("referantHomeCommunityId", String.class);
		String referantRepositoryId = this.getParameterValue("referantRepositoryId", String.class);
		String referredHomeCommunityId = this.getParameterValue("referredHomeCommunityId", String.class);
		String referredRepositoryId = this.getParameterValue("referredRepositoryId", String.class);
		
		RoutingTokenImpl referantRoutingToken = (RoutingTokenImpl)RoutingTokenImpl.create(referantHomeCommunityId, referantRepositoryId);
		RoutingTokenImpl referredRoutingToken = (RoutingTokenImpl)RoutingTokenImpl.create(referredHomeCommunityId, referredRepositoryId);
		ExternalArtifactSources externalArtifactRouting = config.getExternalArtifactSources();
		if(externalArtifactRouting == null)
			externalArtifactRouting = new StaticExternalArtifactSources();
		
		externalArtifactRouting.addIndirection(referantRoutingToken, referredRoutingToken);
	}

	/**
	 * @param queryUrlStrings
	 * @return
	 * @throws MalformedURLException 
	 */
	private URL[] createURLArray(String[] queryUrlStrings) 
	throws MalformedURLException
	{
		URL[] result = new URL[queryUrlStrings.length];
		int index = 0;
		for(String queryUrlString : queryUrlStrings)
			result[index++] = new URL(queryUrlString);
		
		return result;
	}
}
