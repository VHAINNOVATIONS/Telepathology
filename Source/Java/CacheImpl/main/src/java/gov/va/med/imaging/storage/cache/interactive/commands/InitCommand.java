/**
 * 
 */
package gov.va.med.imaging.storage.cache.interactive.commands;

import gov.va.med.imaging.storage.cache.Cache;
import gov.va.med.imaging.storage.cache.impl.CacheManagerImpl;
import gov.va.med.interactive.Command;
import gov.va.med.interactive.CommandProcessor;

/**
 * @author VHAISWBECKEC
 *
 */
public class InitCommand 
extends Command<CacheManagerImpl>
{
	public InitCommand()
	{
		super();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.interactive.ValidCommandProcessor#processCommand(gov.va.med.imaging.storage.cache.interactive.CommandProcessor, gov.va.med.imaging.storage.cache.impl.CacheManagerImpl, gov.va.med.imaging.storage.cache.interactive.Command)
	 */
	public void processCommand(CommandProcessor processor, CacheManagerImpl manager) 
	throws Exception
	{
		Cache cache = manager.getActiveCache();
		if(cache == null)
		{
			getLogger().info("Unable to initialize cache, active cache must be set through an open or create command.");
		}
		else
		{
			getLogger().info("Initializing '" + cache + "'.");
			manager.initialize(cache);
		}
	}

}
