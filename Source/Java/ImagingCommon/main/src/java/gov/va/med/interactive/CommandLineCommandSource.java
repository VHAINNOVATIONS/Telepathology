package gov.va.med.interactive;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

/**
 * This class:
 * 1.) takes the application command line args and parses them to create commands and put
 *     them on the command queue.
 * 2.) continuously reads from the command line and put commands on the command queue.
 * 
 * If any commands on the given list (the app command line) are invalid then none of them will
 * be put on the command queue and the app will exit immediately
 * 
 * @author VHAISWBECKEC
 *
 */
public class CommandLineCommandSource<M>
implements CommandSource<M>
{
	private final Logger logger = Logger.getLogger(this.getClass());
	private PrintStream promptStream;
	private InputStream inStream;
	private CommandFactory<M> commandFactory;
	private CommandController<M> commandController;
	private boolean exitRequested;
	
	/**
	 * Get input from System.in and write prompts to System.out
	 */
	public CommandLineCommandSource() 
	{ 
		this(System.out, System.in);
	}

	/**
	 * Set the input stream for commands and the output stream for prompts.
	 * The prompt stream can be null if no prompts are needed.
	 * @param promptStream
	 * @param inStream
	 */
	public CommandLineCommandSource(PrintStream promptStream, InputStream inStream)
	{
		this.promptStream = promptStream;
		this.inStream = inStream;
	}

	/**
	 * @return the promptStream
	 */
	public PrintStream getPromptStream()
	{
		return this.promptStream;
	}

	/**
	 * @return the inStream
	 */
	public InputStream getInStream()
	{
		return this.inStream;
	}

	@Override
	public void commandQueueAvailable()
	{
		queueSavedCommands();
	}

	@Override
	public void commandQueueUnavailable()
	{
	}

	@Override
	public void managedObjectAvailable()
	{
	}

	@Override
	public void managedObjectUnavailable()
	{
	}
	
	public BlockingQueue<Command<M>> getCommandQueue()
	{
		return this.commandController.getCommandQueue();
	}

	/**
	 * @see gov.va.med.interactive.CommandSource#setCommandController(gov.va.med.interactive.CommandController)
	 */
	@Override
	public void setCommandController(CommandController<M> commandController)
	{
		this.commandController = commandController;
	}

	/**
	 * @param commandFactory the commandFactory to set
	 */
	@Override
	public void setCommandFactory(CommandFactory<M> commandFactory)
	{
		this.commandFactory = commandFactory;
	}

	@Override
	public CommandFactory<M> getCommandFactory()
	{
		return this.commandFactory;
	}
	
	/**
	 * @param commandLine
	 * @throws CommandLineParseException
	 */
	@Override
	public void pushCommands(String[] commandLine) 
	throws CommandLineParseException
	{
		List<Command<M>> commands = parseCommandLine(commandLine);
		if(commands != null)
			queueCommandList(commands);
	}

	public boolean isExitRequested()
	{
		return this.exitRequested;
	}

	private void setExitRequested(boolean exitRequested)
	{
		this.exitRequested = exitRequested;
	}
	
	public Logger getLogger()
	{
		return this.logger;
	}

	public void run()
	{
		// as long as the sky remains blue ...
		// or the user exits
		while(! isExitRequested())
		{
			try
			{
				String[] commandLine = readInteractiveCommandLine();
				
				List<Command<M>> commands = parseCommandLine(commandLine);
				if(commands != null)
					queueCommandList(commands);
			} 
			catch (CommandLineParseException x)
			{
				x.printStackTrace();
			} 
			catch (IOException x)
			{
				x.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param commands
	 */
	private synchronized void queueCommandList(List<Command<M>> commands)
	{
		if(getCommandQueue() != null)
		{
			for(Command<M> command : commands)
			{
				getCommandQueue().add(command);
				if(command.exitAfterProcessing())
					setExitRequested(true);
			}
		}
		else
			saveCommandsUntilQueueIsSet(commands);
	}

	private List<Command<M>> savedCommands = new ArrayList<Command<M>>(); 
	/**
	 * @param commands
	 */
	private void saveCommandsUntilQueueIsSet(List<Command<M>> commands)
	{
		synchronized (savedCommands)
		{
			savedCommands.addAll(commands);
		}
	}
	
	private void queueSavedCommands()
	{
		synchronized (savedCommands)
		{
			if(getCommandQueue() != null)
			{
				queueCommandList(savedCommands);
				savedCommands.clear();
			}
		}
	}
	
	/**
	 * 
	 */
	private LineNumberReader reader = null;
	private synchronized LineNumberReader getLineReader()
	{
		if(reader == null)
			reader = new LineNumberReader( new InputStreamReader(getInStream()) );
		return reader;
	}
	
	/**
	 * @return
	 * @throws IOException 
	 */
	private String[] readInteractiveCommandLine() 
	throws IOException
	{
		if(getPromptStream() != null)
			getPromptStream().print("Your command >");
		String line = getLineReader().readLine();
		
		// split on whitespace and return the array
		return line.split("[\\s]");
	}

	/**
	 * Parse the string array and try to make (a) command(s) from it.
	 * If balking queue is true then do not queue any commands until all have 
	 * been created successfully (i.e. are valid commands).
	 * 
	 * @param args
	 * @param balkingQueue
	 * @throws CommandLineParseException
	 */
	private List<Command<M>> parseCommandLine(String[] args) 
	throws CommandLineParseException
	{
		List<Command<M>> commands = new ArrayList<Command<M>>();
		boolean allCommandsValid = true;
		
		CommandText activeCommandText = null;
		for(int index=0; index < args.length; ++index)
		{
			String currentArg = args[index];
			
			if(currentArg.startsWith("<") && currentArg.endsWith(">"))
			{
				if(activeCommandText == null)
					throw new CommandLineParseException("Invalid command line syntax near '" + currentArg + "', no command to associate parameter with.");

				currentArg = currentArg.substring(1, currentArg.length() -1);
				activeCommandText.parameters.add(currentArg);
			}
			else
			{
				if(activeCommandText != null)
				{
					try
					{
						Command<M> command = getCommandFactory().createCommand(activeCommandText.getCommand(), activeCommandText.getParametersAsArray());
						getLogger().debug("Created command of type '" + command.getClass().getName() + "'.");
						commands.add(command);
					}
					catch (Exception x)
					{
						System.err.println("Unable to create a valid command from: " + activeCommandText.toString());
						allCommandsValid = false;
					}
				}
				
				activeCommandText = new CommandText(currentArg);
			}
		}
		
		if(activeCommandText != null)
		{
			try
			{
				Command<M> command = getCommandFactory().createCommand(activeCommandText.getCommand(), activeCommandText.getParametersAsArray());
				if(command != null)
				{
					getLogger().debug("Created command of type '" + command.getClass().getName() + "'.");
					commands.add(command);
				}
				else
				{

					allCommandsValid = false;
				}
			}
			catch (Exception x)
			{
				getLogger().error("Exception raised creating command from: " + activeCommandText.toString(), x);
				allCommandsValid = false;
			}
		}
		
		if(! allCommandsValid)
		{
			printCommandMenu();
			return null;
		}
		
		return commands;
	}
	
	private void printCommandMenu()
	{
		if(getPromptStream() != null)
		{
			getPromptStream().println( getCommandFactory().getHelpMessage() );
			getPromptStream().println("Command parameters must be delimited with '<' and '>'.");
			getPromptStream().println("Multiple commands may be given on one line (e.g. create <JunkCache> <file:///junk/cache> <TestWithEvictionPrototype> initialize enable store stop exit"); 
			getPromptStream().println("                                           (create a cache, initialize it, enable it, store the configuration, stop the cache and exit"); 
		}
	}
	
	/**
	 * A simple value object for collecting the command and its arguments together
	 * while we parse the command line.
	 */
	public class CommandText
	{
		private final String command;
		private final List<String> parameters;
		
		public CommandText(String command)
		{
			super();
			this.command = command;
			this.parameters = new ArrayList<String>();
		}

		public String getCommand()
		{
			return this.command;
		}

		public void addParameter(String parameter)
		{
			parameters.add(parameter);
		}

		public String[] getParametersAsArray()
		{
			return parameters.toArray(new String[parameters.size()]);
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			
			sb.append(getCommand());
			for(String parameter : this.parameters)
				sb.append(" " + parameter);
			
			return sb.toString();
		}
	}
}