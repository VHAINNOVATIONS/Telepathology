/**
 * 
 */
package gov.va.med.imaging.storage.cache.interactive.commands;

import gov.va.med.imaging.storage.cache.Cache;
import gov.va.med.imaging.storage.cache.Region;
import gov.va.med.imaging.storage.cache.impl.CacheManagerImpl;
import gov.va.med.interactive.Command;
import gov.va.med.interactive.CommandParametersDescription;
import gov.va.med.interactive.CommandProcessor;

/**
 * @author VHAISWBECKEC
 *
 */
public class RegionCommand 
extends Command<CacheManagerImpl>
{
	private static CommandParametersDescription[] commandParameters = new CommandParametersDescription[]
	{
		new CommandParametersDescription(String.class, true), 	// region name
		new CommandParametersDescription(String.class, true),	// eviction strategy name
		new CommandParametersDescription(String.class, false)	// another, optional, eviction strategy name
	};
	
 	public static CommandParametersDescription[] getCommandParametersDescription()
 	{
 		return commandParameters;
 	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.interactive.ValidCommandProcessor#processCommand(gov.va.med.imaging.storage.cache.interactive.CommandProcessor, gov.va.med.imaging.storage.cache.impl.CacheManagerImpl, gov.va.med.imaging.storage.cache.interactive.Command)
	 */
	public void processCommand(CommandProcessor processor, CacheManagerImpl manager) 
	throws Exception
	{
		String regionName = getCommandParameterValues()[0];
		int evictionStrategyNameCount = getCommandParameterValues().length - 1;
		
		String[] evictionStrategyNames = new String[evictionStrategyNameCount];
		for(int index=1; index < evictionStrategyNameCount; ++index)
			evictionStrategyNames[index-1] = getCommandParameterValues()[index];
		
		Cache cache = manager.getActiveCache();
		
		Region region = manager.createRegion( cache, regionName, evictionStrategyNames );
		cache.addRegion(region);
	}

}
