/**
 * 
 */
package gov.va.med.interactive;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author vhaiswbeckec
 * 
 * A CommandFactory implementation that determines the valid commands from a
 * CommandList class.  The CommandList class must be in the same package as the 
 * commands themselves.  The package name, where the CommandList and command class,
 * is returned by the getCommandPackageName() abstract method. 
 * 
 * The type parameter M specifies the class that is the subject of the commands,
 * an instance of which will be operated on by the commands.
 */
public abstract class CommandListCommandFactory<M> 
implements CommandFactory<M>
{
	// for commands loaded by package, this is the name of the class providing the command
	// list
	private final static String COMMAND_LIST = "PackageCommandList";
	
	final Map<CommandDescription, Class<Command<M>>> knownCommands;
	private Logger logger = Logger.getLogger(this.getClass());
	
	/**
	 * Derived classes must implement this method to indicate where to find
	 * the command packages.
	 * @return
	 */
	public abstract String[] getCommandPackageNames();
	
	/**
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	protected CommandListCommandFactory() 
	throws IOException, ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		this.knownCommands = new HashMap<CommandDescription, Class<Command<M>>>();
		
		loadCommandsFromPackageNames(getCommandPackageNames());
	}

	/**
	 * 
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@SuppressWarnings("unchecked")
	private void loadCommandsFromPackageNames(String[] packageNames) 
	throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		for(String commandPackageName : packageNames)
		{
			logger.info("Loading commands from package '" + commandPackageName + "'.");
			try
			{
				Class<? extends CommandList> commandProviderClass = 
					(Class<? extends CommandList>)Class.forName(commandPackageName + "." + COMMAND_LIST);
				logger.info("\tCommandList class is '" + commandProviderClass.getName() + "'.");
				
				CommandList commandList = commandProviderClass.newInstance();
				String[] commandClassNames = commandList.getCommandList();

				for(String command : commandClassNames)
				{
					logger.info("\tLoading command '" + command + "'.");
					try
					{
						String commandName = commandList.getCommandName(command);
						String commandHelp = commandList.getCommandHelpMessage(command);
						String commandShortcut = commandList.getCommandShortcut(command);
						Class<Command<M>> commandClass = (Class<Command<M>>)commandList.getCommandClass(command);
						
						CommandParametersDescription[] parametersDescription = null;
						try
						{
							Method getCommandParametersDescriptionMethod = 
								commandClass.getDeclaredMethod("getCommandParametersDescription", (Class<?>[])null);
							parametersDescription = (CommandParametersDescription[])
								getCommandParametersDescriptionMethod.invoke(null, (Object[])null);
							
							// populate the parameter descriptions from the properties file
							for(CommandParametersDescription parameterDescription : parametersDescription)
								if(parameterDescription.getDescription() == null)
								{
									String description = commandList.getCommandParameterDescription(command, parameterDescription.getParameterName());
									parameterDescription.setDescription(description);
								}
						}
						catch(NoSuchMethodException nsmX){}	// ignore, just means no args to the command
						
						CommandDescription commandDescription = 
							new CommandDescription(commandName, commandHelp, commandShortcut, parametersDescription);
						logger.info("Adding available command '" + commandDescription.toString() + "'.");
						
						knownCommands.put(commandDescription, commandClass);
					} 
					catch (ClassNotFoundException cnfX)
					{
						cnfX.printStackTrace();
					}
					catch (ClassCastException ccX)
					{
						ccX.printStackTrace();
					}
				}
			}
			catch(ClassNotFoundException cnfX)
			{
				logger.error("Unable to load command list from package '" + commandPackageName + "', command list class does not exist.");
			}
			catch (InstantiationException x)
			{
				logger.error("Unable to load command list from package '" + commandPackageName + "', unable to create command list class instance.");
			}
		}
	}

	/**
	 * 
	 * @param command
	 * @return
	 */
	private CommandDescription findCommandClass(String command)
	{
		if(command == null)
			return null;
		
		for(CommandDescription commandDesc : knownCommands.keySet())
		{
			if(command.equals(commandDesc.getCommandName()) || command.equals(commandDesc.shortcutKey) )
				return commandDesc;
		}
		
		return null;
	}

	/**
	 * 
	 */
	@Override
	public Command<M> createCommand(String command) 
	throws InstantiationException,
			IllegalAccessException, ClassCastException, SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			InvocationTargetException
	{
		return createCommand(command, (String[])null);
	}

	/**
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * 
	 */
	@Override
	public Command<M> createCommand(String commandText, String[] commandArgs) 
	throws InstantiationException, IllegalAccessException, ClassCastException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException
	{
		// by default, return a null if we cannot build a command processor
		Command<M> command = null;
		
		// first, find the CommandDescription by command name
		CommandDescription commandDescription = findCommandClass(commandText);
		if(commandDescription != null)
		{
			// get the class that implements the command
			Class<Command<M>> commandClass = knownCommands.get(commandDescription);
			
			// if no args, then use the default no-arg constructor and we're done
			if(commandArgs == null || commandArgs.length == 0)
			{
				command = (Command<M>)commandClass.newInstance();
				if(command != null) command.setCommandFactory(this);
			}
			else if(commandArgs != null && commandArgs.length > 0 && commandDescription.getCommandConstructorParameterTypes().length == 0)
			{
				throw new IllegalArgumentException("Command '" + commandText + "' has associated arguments but the command definition has no paremeters defined.");
			}
			// else if args are supplied, use the description of the constructor arguments
			// to convert the strings to the appropriate types
			else
			{
				Constructor<?> commandConstructor = commandClass.getConstructor(new Class<?>[]{String[].class});
				command = (Command)commandConstructor.newInstance( new Object[]{commandArgs});
				
				if(command != null) command.setCommandFactory(this);
			}
		}
		
		return command;
	}

	/**
	 * Generate a help message from the command descriptions.
	 * @return
	 */
	public String getHelpMessage()
	{
		StringBuilder sb = new StringBuilder();
		String lineSeperator = System.getProperty("line.separator");
		for(CommandDescription commandDescription : knownCommands.keySet())
		{
			sb.append( commandDescription.getCommandName() );
			sb.append('-');
			sb.append( commandDescription.getHelpMessage() );
			if(commandDescription.getCommandParametersDescription() != null)
				for( CommandParametersDescription parmDesc : commandDescription.getCommandParametersDescription() )
				{
					sb.append( lineSeperator );
					sb.append( '\t' );
					if( !parmDesc.isRequired() ) sb.append('[');
					sb.append('"');
					sb.append(parmDesc.getParameterName());
					sb.append('"');
					sb.append('(');
					sb.append( parmDesc.getParameterClass() == null ? "String" : parmDesc.getParameterClass().getSimpleName() );
					sb.append(')');
					if( !parmDesc.isRequired() ) sb.append(']');
					sb.append('-');
					sb.append(parmDesc.getDescription());
					//parmDesc.
				}
			sb.append( lineSeperator );
		}
		
		return sb.toString();
	}
	
	class CommandDescription
	{
		private final String commandName;
		private final String helpMessage;
		private final String shortcutKey;
		private final CommandParametersDescription[] commandParametersDescription;
		
		public CommandDescription(
			String commandName, 
			String helpMessage,
			String shortcutKey,
			CommandParametersDescription[] commandParametersDescription)
		{
			super();
			this.commandName = commandName;
			this.helpMessage = helpMessage;
			this.shortcutKey = shortcutKey;
			this.commandParametersDescription = commandParametersDescription;
		}

		String getCommandName()
		{
			return this.commandName;
		}

		String getHelpMessage()
		{
			return this.helpMessage;
		}

		String getShortcutKey()
		{
			return this.shortcutKey;
		}

		/**
		 * @return the commandParametersDescription
		 */
		public CommandParametersDescription[] getCommandParametersDescription()
		{
			return this.commandParametersDescription;
		}
		
		public Class<?>[] getCommandConstructorParameterTypes()
		{
			Class<?>[] constructorParameters = new Class<?>[getCommandParametersDescription().length];
			for(int parameterIndex=0; parameterIndex < getCommandParametersDescription().length; ++parameterIndex)
				constructorParameters[parameterIndex] = (getCommandParametersDescription()[parameterIndex]).getParameterClass();
			
			return constructorParameters;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(this.getClass().getSimpleName());
			sb.append('[');
			sb.append( this.getCommandName() );
			sb.append('-');
			sb.append( this.getCommandParametersDescription() );
			sb.append(']');
			return sb.toString();
		}
		
	}
}
