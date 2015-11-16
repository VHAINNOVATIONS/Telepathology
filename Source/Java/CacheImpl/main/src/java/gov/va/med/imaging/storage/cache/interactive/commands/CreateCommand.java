/**
 * 
 */
package gov.va.med.imaging.storage.cache.interactive.commands;

import gov.va.med.imaging.storage.cache.Cache;
import gov.va.med.imaging.storage.cache.impl.CacheManagerImpl;
import gov.va.med.interactive.Command;
import gov.va.med.interactive.CommandParametersDescription;
import gov.va.med.interactive.CommandProcessor;

import java.net.URI;

public class CreateCommand 
extends Command<CacheManagerImpl>
{
	private static CommandParametersDescription[] commandParameters = new CommandParametersDescription[]
    {
 		new CommandParametersDescription(String.class, true), 	// cache name
 		new CommandParametersDescription(String.class, true),	// location URI
 		new CommandParametersDescription(String.class, false)	// prototype
 	};
 	
 	public static CommandParametersDescription[] getCommandParametersDescription()
 	{
 		return commandParameters;
 	}

	public CreateCommand (String[] commandParameterValues)
	{
		super(commandParameterValues);
	}

	public void processCommand(CommandProcessor commandProcessor, CacheManagerImpl cacheManager) 
	throws Exception
	{
		String[] parameters = this.getCommandParameterValues();
		
		String cacheName = parameters[0];
		URI locationUri = new URI(parameters[1]);
		String prototypeName = parameters.length >= 3 ? parameters[2] : null;
		
		Cache cache = cacheManager.createCache(cacheName, locationUri, prototypeName);
		
		cacheManager.setActiveCache(cache);
	}
}