/**
 * 
 */
package gov.va.med.imaging.storage.cache.interactive.commands;

import gov.va.med.imaging.storage.cache.impl.CacheManagerImpl;
import gov.va.med.interactive.Command;
import gov.va.med.interactive.CommandProcessor;
import gov.va.med.server.ServerLifecycleEvent;

/**
 * @author VHAISWBECKEC
 *
 */
public class StopCommand 
extends Command<CacheManagerImpl>
{
	/**
	 * 
	 */
	public StopCommand()
	{
		super();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.interactive.ValidCommandProcessor#processCommand(gov.va.med.imaging.storage.cache.interactive.CommandProcessor, gov.va.med.imaging.storage.cache.impl.CacheManagerImpl, gov.va.med.imaging.storage.cache.interactive.Command)
	 */
	@Override
	public void processCommand(CommandProcessor processor, CacheManagerImpl manager) 
	throws Exception
	{
		manager.serverLifecycleEvent(new ServerLifecycleEvent(ServerLifecycleEvent.EventType.STOP));

	}

}
