/**
 * 
 */
package gov.va.med.siteservice.interactive.commands;

import gov.va.med.OID;
import gov.va.med.RoutingTokenImpl;
import gov.va.med.imaging.artifactsource.ArtifactSourceImpl;
import gov.va.med.interactive.Command;
import gov.va.med.interactive.CommandAnnotation;
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
@CommandAnnotation(name="addRepository", shortcut="r")
public class AddRepositoryCommand
extends Command<SiteResolutionProviderConfiguration>
{
	private static final CommandParametersDescription<?>[] commandParametersDescription = 
		new CommandParametersDescription[]
		{
			new CommandParametersDescription<String>("homeCommunityId", String.class, true), 
			new CommandParametersDescription<String>("repositoryId", String.class, true), 
			new CommandParametersDescription<String[]>("queryUrls", String[].class, true), 
			new CommandParametersDescription<String[]>("retrieveUrls", String[].class, true), 
		};
	public static CommandParametersDescription<?>[] getCommandParametersDescription()
	{
		return commandParametersDescription;
	}

	/**
	 * @param commandParameterValues
	 */
	public AddRepositoryCommand(String[] commandParameterValues)
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
		String[] queryUrlStrings = this.getParameterValue("queryUrls", String[].class);
		String[] retrieveUrlStrings = this.getParameterValue("retrieveUrls", String[].class);
		
		OID homeCommunityOid = OID.create(homeCommunityId);
		URL[] queryUrls = createURLArray(queryUrlStrings); 
		URL[] retrieveUrls = createURLArray(retrieveUrlStrings); 
		
		RoutingTokenImpl rt = (RoutingTokenImpl)RoutingTokenImpl.create(homeCommunityId, repositoryId);
		ArtifactSourceImpl value = 
			new ArtifactSourceImpl(homeCommunityOid, repositoryId, queryUrls, retrieveUrls);
		ExternalArtifactSources externalArtifactRouting = config.getExternalArtifactSources();
		if(externalArtifactRouting == null)
			externalArtifactRouting = new StaticExternalArtifactSources();
		
		externalArtifactRouting.add(rt, value);
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
		{
			result[index] = new URL(queryUrlString);
			index++;
		}
		return result;
	}
}
