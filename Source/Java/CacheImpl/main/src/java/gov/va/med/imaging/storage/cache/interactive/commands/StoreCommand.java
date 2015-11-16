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
public class StoreCommand 
extends Command<CacheManagerImpl>
{
	
	/**
	 * 
	 */
	public StoreCommand()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.interactive.ValidCommandProcessor#processCommand(gov.va.med.imaging.storage.cache.interactive.CommandProcessor, gov.va.med.imaging.storage.cache.impl.CacheManagerImpl, gov.va.med.imaging.storage.cache.interactive.Command)
	 */
	@Override
	public void processCommand(CommandProcessor processor, CacheManagerImpl manager) 
	throws Exception
	{
		Cache cache = manager.getActiveCache();
		manager.store(cache);
	}

}
