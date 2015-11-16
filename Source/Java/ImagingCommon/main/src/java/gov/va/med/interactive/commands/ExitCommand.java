/**
 * 
 */
package gov.va.med.interactive.commands;

import gov.va.med.interactive.Command;
import gov.va.med.interactive.CommandProcessor;

/**
 * @author vhaiswbeckec
 *
 */
@SuppressWarnings("unchecked")
public class ExitCommand
extends Command
{
	/**
	 * @see gov.va.med.interactive.Command#processCommand(gov.va.med.interactive.CommandProcessor, java.lang.Object)
	 */
	@Override
	public void processCommand(CommandProcessor processor, Object managedObject)
	throws Exception
	{ 
		System.out.println("Bye");
	}

	@Override
	public boolean exitAfterProcessing()
	{
		return true;
	}
}
