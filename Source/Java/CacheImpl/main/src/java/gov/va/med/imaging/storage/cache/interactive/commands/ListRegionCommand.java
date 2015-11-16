/**
 * 
 */
package gov.va.med.imaging.storage.cache.interactive.commands;

import gov.va.med.imaging.storage.cache.Region;
import gov.va.med.imaging.storage.cache.impl.CacheManagerImpl;
import gov.va.med.interactive.Command;
import gov.va.med.interactive.CommandProcessor;

/**
 * @author VHAISWBECKEC
 *
 */
public class ListRegionCommand 
extends Command<CacheManagerImpl>
{
	/**
	 * 
	 */
	public ListRegionCommand()
	{
		super();
	}

	/**
	 * @see gov.va.med.interactive.Command#processCommand(gov.va.med.interactive.CommandProcessor, gov.va.med.imaging.storage.cache.impl.CacheManagerImpl, gov.va.med.interactive.Command)
	 */
	public void processCommand(CommandProcessor processor, CacheManagerImpl manager) 
	throws Exception
	{
		for( Region region : manager.getActiveCache().getRegions() )
			System.out.println(region.toString());
	}
}
