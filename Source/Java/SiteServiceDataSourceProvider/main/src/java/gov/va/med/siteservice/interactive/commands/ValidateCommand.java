/**
 * 
 */
package gov.va.med.siteservice.interactive.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import gov.va.med.imaging.datasource.Provider;
import gov.va.med.interactive.Command;
import gov.va.med.interactive.CommandParametersDescription;
import gov.va.med.interactive.CommandProcessor;
import gov.va.med.siteservice.ProtocolServerConfiguration;
import gov.va.med.siteservice.SiteResolutionProvider;
import gov.va.med.siteservice.SiteResolutionProviderConfiguration;
import gov.va.med.siteservice.SiteServiceConfiguration;

/**
 * @author vhaiswbeckec
 *
 */
public class ValidateCommand
extends Command<SiteResolutionProviderConfiguration>
{
	/**
	 * @param commandParameterValues
	 */
	public ValidateCommand()
	{
		super();
	}

	/**
	 * @see gov.va.med.interactive.Command#processCommand(gov.va.med.interactive.CommandProcessor, java.lang.Object)
	 */
	@Override
	public void processCommand(CommandProcessor<SiteResolutionProviderConfiguration> processor, SiteResolutionProviderConfiguration config)
	throws Exception
	{
		List<String> errorList = new ArrayList<String>();
		
		for(Map.Entry<String, ProtocolServerConfiguration> protocolConfig : config.getProtocolConfiguration().entrySet() )
		{
			String protocol = protocolConfig.getKey();
			ProtocolServerConfiguration serverConfig = protocolConfig.getValue();
			if(protocol == null)
				errorList.add("Null protocol detected in protocol configuration.");
			if(serverConfig == null)
				errorList.add("Null server configuration mapped to '" + protocol + "' in protocol configuration.");
		}

		
		
		if(errorList.size() == 0)
			System.out.println("Configuration is valid, " + config.getProtocolConfiguration().size() + " protocols." );
		else
		{
			System.out.println("Configuration is NOT valid, because:");
			for(String error : errorList)
				System.out.println(error);
		}
	}
}
