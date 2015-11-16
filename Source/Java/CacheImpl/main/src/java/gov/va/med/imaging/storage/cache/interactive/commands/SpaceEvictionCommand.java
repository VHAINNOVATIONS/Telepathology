/**
 * 
 */
package gov.va.med.imaging.storage.cache.interactive.commands;

import gov.va.med.imaging.storage.cache.Cache;
import gov.va.med.imaging.storage.cache.EvictionStrategy;
import gov.va.med.imaging.storage.cache.impl.CacheManagerImpl;
import gov.va.med.imaging.storage.cache.impl.eviction.StorageThresholdEvictionStrategyMemento;
import gov.va.med.interactive.Command;
import gov.va.med.interactive.CommandParametersDescription;
import gov.va.med.interactive.CommandProcessor;

/**
 * @author VHAISWBECKEC
 *
 */
public class SpaceEvictionCommand 
extends Command<CacheManagerImpl>
{
	private static CommandParametersDescription[] commandParameters = new CommandParametersDescription[]
	{
		new CommandParametersDescription(String.class, true), 	// eviction strategy name
		new CommandParametersDescription(Long.class, true),	// min freespace threshold in bytes
		new CommandParametersDescription(Long.class, true),	// target freespace threshold in bytes
		new CommandParametersDescription(Long.class, false)	// maximum used size
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
		String evictionStrategyName = getCommandParameterValues()[0];
		Long minFreeSpace = Long.parseLong( getCommandParameterValues()[1] );
		Long targetFreeSpace = Long.parseLong( getCommandParameterValues()[2] );
		
		Cache cache = manager.getActiveCache();
		
		StorageThresholdEvictionStrategyMemento memento = new StorageThresholdEvictionStrategyMemento(); 
		memento.setName(evictionStrategyName);
		memento.setMinFreeSpaceThreshold(minFreeSpace);
		memento.setTargetFreeSpaceThreshold(targetFreeSpace);
		memento.setDelay(1000L);
		memento.setInterval(10000L);
		
		EvictionStrategy evictionStrategy = manager.createEvictionStrategy(cache, memento);
		cache.addEvictionStrategy(evictionStrategy);
	}

}
