/**
 * 
 */
package gov.va.med.siteservice.interactive.commands;

import gov.va.med.OID;
import gov.va.med.interactive.Command;
import gov.va.med.interactive.CommandParametersDescription;
import gov.va.med.interactive.CommandProcessor;
import gov.va.med.siteservice.SiteResolutionProviderConfiguration;
import gov.va.med.siteservice.siteprotocol.ProtocolPreference;

/**
 * @author vhaiswbeckec
 *
 */
public class AddProtocolPreferenceCommand
extends Command<SiteResolutionProviderConfiguration>
{
	private static final CommandParametersDescription<?>[] commandParametersDescription = 
		new CommandParametersDescription[]
		{
			new CommandParametersDescription<String>("homeCommunityId", String.class, true), 
			new CommandParametersDescription<String>("repositoryId", String.class, true), 
			new CommandParametersDescription<String[]>("protocols", String[].class, true),
			new CommandParametersDescription<Boolean>("local", Boolean.class, false), 
			new CommandParametersDescription<Boolean>("alien", Boolean.class, false),
			new CommandParametersDescription<Boolean>("enabled", Boolean.class, false), 
		};
	public static CommandParametersDescription<?>[] getCommandParametersDescription()
	{
		return commandParametersDescription;
	}
	
	/**
	 * @param commandParameterValues
	 */
	public AddProtocolPreferenceCommand(String[] commandParameterValues)
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
		OID homeCommunityOid = OID.create(homeCommunityId);
		String repositoryId = this.getParameterValue("repositoryId", String.class);
		String[] protocols = this.getParameterValue("protocols", String[].class);
		Boolean local = this.getParameterValue("local", Boolean.class);
		Boolean alien = this.getParameterValue("alien", Boolean.class);
		Boolean enabled = this.getParameterValue("enabled", Boolean.class);
		
		ProtocolPreference ppm = ProtocolPreference.create(
			homeCommunityOid, repositoryId, 
			local != null ? local.booleanValue() : false, alien != null ? alien.booleanValue() : false,
			enabled != null ? enabled.booleanValue() : true,
			protocols);
		
		config.getSiteProtocolPreferenceFactory().add(ppm);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append( "AddProtocolPreferenceCommand [" );
		for(String arg : getCommandParameterValues())
		{
			sb.append(arg);
			sb.append(',');
		}
		sb.append("]");
		
		return sb.toString();
	}
	
}
