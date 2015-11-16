/**
 * 
 */
package gov.va.med.imaging.storage.cache.interactive.commands;

import gov.va.med.imaging.storage.cache.Cache;
import gov.va.med.imaging.storage.cache.impl.CacheManagerImpl;
import gov.va.med.interactive.Command;
import gov.va.med.interactive.CommandParametersDescription;
import gov.va.med.interactive.CommandProcessor;

/**
 * @author VHAISWBECKEC
 *
 */
public class OpenCommand 
extends Command<CacheManagerImpl>
{
	private static CommandParametersDescription[] commandParameters = new CommandParametersDescription[]
    {
  		new CommandParametersDescription(String.class, true) 	// cache name
  	};
  	
  	public static CommandParametersDescription[] getCommandParametersDescription()
  	{
  		return commandParameters;
  	}

	public OpenCommand (String[] commandParameterValues)
	{
		super(commandParameterValues);
	}

	/**
	 * @see gov.va.med.imaging.storage.cache.interactive.ValidCommandProcessor#processCommand(gov.va.med.interactive.CommandProcessor, gov.va.med.imaging.storage.cache.impl.CacheManagerImpl, gov.va.med.interactive.Command)
	 */
	public void processCommand(CommandProcessor processor, CacheManagerImpl manager) 
	throws Exception
	{
		String cacheName = (String)getTypecheckedParameters()[0];
		
		Cache cache = manager.getCache(cacheName);
		
		if(cache != null)
			manager.setActiveCache(cache);
	}
}
