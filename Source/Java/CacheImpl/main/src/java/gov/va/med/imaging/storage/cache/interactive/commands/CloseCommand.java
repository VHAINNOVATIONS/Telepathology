/**
 * 
 */
package gov.va.med.imaging.storage.cache.interactive.commands;

import gov.va.med.imaging.storage.cache.impl.CacheManagerImpl;
import gov.va.med.interactive.Command;
import gov.va.med.interactive.CommandProcessor;

/**
 * @author VHAISWBECKEC
 *
 */
public class CloseCommand 
extends Command<CacheManagerImpl>
{
	/**
	 * 
	 */
	public CloseCommand()
	{
		super();
	}

	/**
	 * @see gov.va.med.interactive.Command#processCommand(gov.va.med.interactive.CommandProcessor, gov.va.med.imaging.storage.cache.impl.CacheManagerImpl, gov.va.med.interactive.Command)
	 */
	@Override
	public void processCommand(CommandProcessor processor, CacheManagerImpl manager) 
	throws Exception
	{
		manager.setActiveCache(null);
	}
	
	
}
