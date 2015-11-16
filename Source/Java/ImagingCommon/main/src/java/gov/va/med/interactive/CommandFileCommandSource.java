package gov.va.med.interactive;


import gov.va.med.interactive.commands.ExitCommand;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * This class:
 * 
 * @author VHAISWBECKEC
 *
 */
public class CommandFileCommandSource<M>
implements CommandSource<M>
{
	private final Logger logger = Logger.getLogger(this.getClass());
	private final String sourceUrl;
	private final String[] substitutionStrings;
	private final InputStream inStream;
	private CommandFactory<M> commandFactory;
	private CommandController<M> commandController;
	private boolean exitRequested;
	
	/**
	 * Creates a CommandFileCommandSource instance.
	 * This method opens the URL or resource before returning.
	 * If the URL or resource references an invalid resource then
	 * no CommandFileCommandSource is created and null is returned.
	 * 
	 * @param sourceUrl
	 * @param substitutionStrings
	 * @return
	 * @throws IOException
	 */
	public static CommandFileCommandSource<?> create(String sourceUrl, String[] substitutionStrings)
	throws IOException
	{
		InputStream inStream;
		
		try
		{
			try
			{
				URL url = new URL(sourceUrl);
				inStream = url.openStream();
				if(inStream == null)
					throw new IOException("Unable to open '" + sourceUrl + "' as a URL.");
				return new CommandFileCommandSource(inStream, sourceUrl, substitutionStrings);
			}
			catch (MalformedURLException x)
			{
				// if we can't open the stream as a URL then try it as a resource
				if(sourceUrl.startsWith(RESOURCE_PROTOCOL))
					sourceUrl = sourceUrl.substring(RESOURCE_PROTOCOL.length());
				inStream = CommandFileCommandSource.class.getClassLoader().getResourceAsStream(sourceUrl);
				if(inStream == null)
					throw new IOException("Unable to open '" + sourceUrl + "' as a resource.");
				return new CommandFileCommandSource(inStream, sourceUrl, substitutionStrings);
			}
		}
		catch(IOException ioX)
		{
			Logger.getLogger(CommandFileCommandSource.class).error("Unable to open command file '" + sourceUrl + "', " + ioX.getMessage());
			return null;
		}
	}
	/**
	 * Pass the command file and the remainder of the command line arguments.
	 * The command line arguments after the file name may be used as substitution
	 * strings.
	 */
	private CommandFileCommandSource(InputStream inStream, String sourceUrl, String[] substitutionStrings)
	{
		assert(inStream != null);
		this.inStream = inStream;
		this.sourceUrl = sourceUrl;
		this.substitutionStrings = substitutionStrings;
	}

	/**
	 * @return the inStream
	 */
	public InputStream getInStream()
	{
		return this.inStream;
	}
	/**
	 * @return the sourceUrl
	 */
	public String getSourceUrl()
	{
		return this.sourceUrl;
	}

	/**
	 * @return the substitutionStrings
	 */
	public String[] getSubstitutionStrings()
	{
		return this.substitutionStrings;
	}

	@Override
	public void commandQueueAvailable(){queueSavedCommands();}
	@Override
	public void commandQueueUnavailable(){}
	@Override
	public void managedObjectAvailable(){}
	@Override
	public void managedObjectUnavailable(){}
	
	public BlockingQueue<Command<M>> getCommandQueue(){return this.commandController.getCommandQueue();}
	@Override
	public void setCommandController(CommandController<M> commandController){this.commandController = commandController;}

	@Override
	public void setCommandFactory(CommandFactory<M> commandFactory){this.commandFactory = commandFactory;}

	@Override
	public CommandFactory<M> getCommandFactory(){return this.commandFactory;}
	
	/**
	 * @param commandLine
	 * @throws CommandLineParseException
	 */
	@Override
	public void pushCommands(String[] commandLine) 
	throws CommandLineParseException
	{}

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

	/**
	 * Read the command file, parse and add commands to the queue
	 */
	@Override
	public void run()
	{
		String[][] commandsText = getCommandsText();
		for(String[] commandText : commandsText)
		{
			if(commandText.length == 0)
				continue;
			
			String commandName = commandText[0];
			String[] commandParameters = null;

			if(commandText.length > 1)
			{
				commandParameters = new String[commandText.length-1];
				System.arraycopy(commandText, 1, commandParameters, 0, commandParameters.length);
			}
			
			try
			{
				Command<M> command = getCommandFactory().createCommand( commandName, commandParameters );
				getLogger().debug("Created command of type '" + command.getClass().getName() + "'.");
				queueCommand(command);
				if(command.exitAfterProcessing())
					setExitRequested(true);
			}
			catch (Exception x)
			{
				System.err.println("Unable to create a valid '" + commandName + "' command, " + x.getMessage());
			}
		}
		
		// if the user didn't ask for an exit, we'll add one for them
		if(! isExitRequested())
		{
			queueCommand(new ExitCommand());
			setExitRequested(true);
		}
	}

	// ===========================================================================================
	// local command queueing
	// ===========================================================================================
	
	private List<Command<M>> savedCommands = new ArrayList<Command<M>>(); 
	/**
	 * 
	 * @param commands
	 */
	private synchronized void queueCommand(Command<M> command)
	{
		if(getCommandQueue() != null)
		{
			getCommandQueue().add(command);
			if(command.exitAfterProcessing())
				setExitRequested(true);
		}
		else
		{
			synchronized (savedCommands)
			{
				savedCommands.add(command);
			}
		}
	}
	private void queueSavedCommands()
	{
		synchronized (savedCommands)
		{
			if(getCommandQueue() != null)
			{
				for(Command<M> command : savedCommands)
					queueCommand(command);
				
				savedCommands.clear();
			}
		}
	}
	
	// ===========================================================================================
	// File reading and parsing 
	// ===========================================================================================
	private static final String RESOURCE_PROTOCOL = "resource://";
	private String[][] commandsText;
	/**
	 * @return the commandsText
	 */
	public synchronized String[][] getCommandsText()
	{
		if(this.commandsText == null)
			this.commandsText = readCommandText(getInStream(), getSubstitutionStrings());
		
		return this.commandsText;
	}

	/**
	 * Do NOT call this from anywhere but the getCommandsText() method !!!
	 * @return 
	 */
	public static String[][] readCommandText(InputStream inStream, String[] substitutionStrings)
	{
		StreamTokenizer tokenizer = new StreamTokenizer( new InputStreamReader(inStream) );
		tokenizer.resetSyntax();
		tokenizer.whitespaceChars(0x0A, 0x0A);
		tokenizer.whitespaceChars(0x0D, 0x0D);
		tokenizer.wordChars(0x20, 0x20);		// <space>
		tokenizer.commentChar(0x21);			// !
		tokenizer.quoteChar(0x22);				// double quote
		tokenizer.wordChars(0x23, 0x2B);		// punctuation
		tokenizer.whitespaceChars(0x2C, 0x2C);	// comma
		tokenizer.wordChars(0x2D, 0x2F);		// punctuation
		tokenizer.wordChars(0x30, 0x39);		// 0 ... 9
		tokenizer.wordChars(0x3A, 0x40);		// punctuation
		tokenizer.wordChars(0x41, 0x5A);		// A ... Z
		tokenizer.wordChars(0x5B, 0x60);		// punctuation
		tokenizer.wordChars(0x61, 0x7A);		// a ... z
		tokenizer.wordChars(0x7B, 0x7E);		// punctuation
		tokenizer.eolIsSignificant(true);

		List<String[]> commands = new ArrayList<String[]>();
		try
		{
			List<String> command = new ArrayList<String>();
			while( StreamTokenizer.TT_EOF != tokenizer.nextToken() )
			{
				if( StreamTokenizer.TT_EOL == tokenizer.ttype)
				{
					String[] commandText = command.toArray(new String[command.size()]);
					doStringSubstitution(commandText, substitutionStrings);
					commands.add( commandText );
					
					command.clear();
				}
				else if( 0x22 == tokenizer.ttype )		// a quoted string
				{
					String token = tokenizer.sval == null ? "" : tokenizer.sval.trim(); 
					command.add(token);
				}
				else
				{
					String token = tokenizer.sval == null ? "" : tokenizer.sval.trim(); 
					command.add(token);
				}
			}
			// add the last line read
			String[] commandText = command.toArray(new String[command.size()]);
			doStringSubstitution(commandText, substitutionStrings);
			commands.add( commandText );
		}
		catch (IOException ioX)
		{
			Logger.getLogger(CommandFileCommandSource.class).error("Error reading command file ', " + ioX.getMessage());
			ioX.printStackTrace();
		}
		
		return commands.toArray(new String[commands.size()][]);
	}
	
	/**
	 * Substitute instances of %n with the command parameter values
	 * 
	 * @param commandText
	 * @param substitutionStrings
	 */
	protected static void doStringSubstitution(String[] commandText, String[] substitutionStrings)
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