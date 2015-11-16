/**
 * 
 */
package gov.va.med.interactive;

import java.lang.reflect.Constructor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import gov.va.med.interactive.Command;
import gov.va.med.interactive.CommandProcessor;
import gov.va.med.interactive.commands.ExitCommand;

/**
 * The base class for a text based composite command.  The getCommandsText() method
 * should return a 2D array of commands.  The first element in each sub-array should be
 * the command name, the remainder of the sub-array elements are command args.
 * 
 * If the command text includes a String in the form "%n", where 'n' is a number the command
 * parameters are assumed to be substitution string values and are substituted for the "%n"
 * 
 * @author vhaiswbeckec
 *
 */
public abstract class CompositeCommand<M>
extends Command<M>
{
	/**
	 * @param commandParameterValues
	 */
	public CompositeCommand(String[] commandParameterValues)
	{
		super(commandParameterValues);
	}

	public CompositeCommand()
	{
		super();
	}
	
	/**
	 * @return the commandsText
	 */
	public abstract String[][] getCommandsText();

	Class<?>[] commandConstructorWithParameter = new Class<?>[]{String[].class};
	Class<?>[] commandConstructorNoArg = new Class<?>[]{};
	
	/**
	 * @see gov.va.med.interactive.Command#processCommand(gov.va.med.interactive.CommandProcessor, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void processCommand(CommandProcessor<M> processor, M managedObject)
	throws Exception
	{
		CommandFactory<M> commandFactory = this.getCommandFactory();
		String packageName = this.getClass().getPackage().getName();
		String[][] commandsText = getCommandsText();
		if( commandsText == null || commandsText.length < 1 )
		{
			System.out.println("Composite command has no child commands defined, ignoring.");
			return;
		}
		
		for(String[] commandText : commandsText)
		{
			if(commandText.length < 1)
				continue;
			
			//
			doStringSubstitution(commandText, getCommandParameterValues());
			
			String commandName = commandText[0];
			
			try
			{
				String[] parameters = new String[commandText.length-1];
				System.arraycopy(commandText, 1, parameters, 0, commandText.length-1);
				
				Command<M> command = parameters.length > 0 ? 
					commandFactory.createCommand(commandName, parameters) :
					commandFactory.createCommand(commandName, parameters);
				
				if(command != null)
					command.processCommand(processor, managedObject);
				else
					System.err.println("Failed to create command of type '" + commandName + "'.");
			}
			catch (Exception x)
			{
				System.err.println("Failed to create command of type '" + commandName + "'. " + x.getMessage());
				x.printStackTrace();
				
				processor.queueCommand(new ExitCommand());
			}
		}
	}

	/**
	 * Substitute instances of %n with the command parameter values
	 * 
	 * @param commandText
	 * @param substitutionStrings
	 */
	protected void doStringSubstitution(String[] commandText, String[] substitutionStrings)
	{
		if(commandText == null || substitutionStrings == null || substitutionStrings.length < 1)
			return;
		
		// a shortcut to avoid REGEX calls if no string substitution
		boolean subStringFound = false;
		for(String commandElement : commandText)
			if( commandElement != null && commandElement.indexOf('%') >= 0 )
			{
				subStringFound = true;
				break;
			}
		if(!subStringFound)
			return;
		
		// 
		for(int substitutionStringindex = substitutionStrings.length; substitutionStringindex >= 1; --substitutionStringindex)
		{
			String substitutionString = substitutionStrings[substitutionStringindex-1];	// -1 because of 0 versus 1 based indexing
			String substitutionRegex = "\\x25" + substitutionStringindex;					// make a regex of a percent sign and the index
			Pattern substitutionPattern = Pattern.compile(substitutionRegex);
			
			for(int commandIndex=0; commandIndex < commandText.length; ++commandIndex)
			{
				Matcher substitutionMatcher = substitutionPattern.matcher(commandText[commandIndex]);
				commandText[commandIndex] = substitutionMatcher.replaceAll(substitutionString);
			}
		}
	}
}
