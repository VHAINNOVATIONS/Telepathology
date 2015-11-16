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
public class ListCommand 
extends Command<CacheManagerImpl>
{
	/**
	 * 
	 */
	public ListCommand()
	{
		super();
	}

	/**
	 * @see gov.va.med.interactive.Command#processCommand(gov.va.med.interactive.CommandProcessor, gov.va.med.imaging.storage.cache.impl.CacheManagerImpl, gov.va.med.interactive.Command)
	 */
	public void processCommand(CommandProcessor processor, CacheManagerImpl manager) 
	throws Exception
	{
		for(Cache cache : manager.getKnownCaches())
			System.out.println(cache.toString());
	}
}
