/**
 * 
 */
package gov.va.med.imaging.storage.cache.interactive.commands;

import gov.va.med.imaging.storage.cache.Cache;
import gov.va.med.imaging.storage.cache.EvictionStrategy;
import gov.va.med.imaging.storage.cache.impl.CacheManagerImpl;
import gov.va.med.imaging.storage.cache.impl.eviction.LastAccessedEvictionStrategyMemento;
import gov.va.med.interactive.Command;
import gov.va.med.interactive.CommandParametersDescription;
import gov.va.med.interactive.CommandProcessor;

/**
 * @author VHAISWBECKEC
 *
 */
public class TimeEvictionCommand 
extends Command<CacheManagerImpl>
{
	private static CommandParametersDescription[] commandParameters = new CommandParametersDescription[]
    {
		new CommandParametersDescription(String.class, true), 	// eviction strategy name
		new CommandParametersDescription(Long.class, true)		// lifespan in milliseconds
  	};
  	
 	public static CommandParametersDescription[] getCommandParametersDescription()
 	{
 		return commandParameters;
 	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.interactive.ValidCommandProcessor#processCommand(gov.va.med.imaging.storage.cache.interactive.CommandProcessor, gov.va.med.imaging.storage.cache.impl.CacheManagerImpl, gov.va.med.imaging.storage.cache.interactive.Command)
	 */
 	@Override
	public void processCommand(CommandProcessor processor, CacheManagerImpl manager) 
	throws Exception
	{
		String evictionStrategyName = getCommandParameterValues()[0];
		Long lifespan = Long.parseLong( getCommandParameterValues()[1] );
		
		Cache cache = manager.getActiveCache();
		
		LastAccessedEvictionStrategyMemento memento = new LastAccessedEvictionStrategyMemento();
		memento.setName(evictionStrategyName);
		memento.setMaximumTimeSinceLastAccess(lifespan);
		memento.setInitialized(true);
		
		EvictionStrategy evictionStrategy = manager.createEvictionStrategy(cache, memento);
		cache.addEvictionStrategy(evictionStrategy);
	}

}
