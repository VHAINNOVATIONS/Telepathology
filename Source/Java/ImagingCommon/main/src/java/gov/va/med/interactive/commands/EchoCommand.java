/**
 * 
 */
package gov.va.med.interactive.commands;

import gov.va.med.interactive.Command;
import gov.va.med.interactive.CommandParametersDescription;
import gov.va.med.interactive.CommandProcessor;

/**
 * An example command that has parameters.
 * 
 * @author vhaiswbeckec
 *
 */
@SuppressWarnings("unchecked")
public class EchoCommand
extends Command
{
	private static final CommandParametersDescription[] commandParametersDescription = 
		new CommandParametersDescription[]
		{
			new CommandParametersDescription("shout", String.class, true)
		};
	/**
	 * The required static method that describes to the command factory what the parameters to the command are.
	 */
	public static CommandParametersDescription[] getCommandParametersDescription()
	{
		return commandParametersDescription;
	}

	/**
	 * The required constructor if the command takes any parameters.
	 * If the command takes no arguments then a no-arg constructor is sufficient.
	 * @param commandParameterValues
	 */
	public EchoCommand(String[] commandParameterValues)
	{
		super(commandParameterValues);
	}

	/**
	 * @see gov.va.med.interactive.Command#processCommand(gov.va.med.interactive.CommandProcessor, java.lang.Object)
	 */
	@Override
	public void processCommand(CommandProcessor processor, Object config)
	throws Exception
	{
		System.out.println(this.getCommandParameterValues()[0]);
	}

}
