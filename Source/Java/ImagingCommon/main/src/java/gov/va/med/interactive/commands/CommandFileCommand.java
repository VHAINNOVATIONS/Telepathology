/**
 * 
 */
package gov.va.med.interactive.commands;

import gov.va.med.interactive.CommandParametersDescription;
import gov.va.med.interactive.CompositeCommand;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;


/**
 * The base class for a file based composite command, the file should be formatted
 * as a CSV with double-quote delimiters.
 * The first arg to the command is the file name, the remainder of the args
 * are passed to the superclass (CompositeCommand).
 * 
 * @author vhaiswbeckec
 *
 */
public class CommandFileCommand<M>
extends CompositeCommand<M>
{
	private static final CommandParametersDescription<?>[] commandParametersDescription = 
		new CommandParametersDescription[]
		{
			new CommandParametersDescription<String>("url", String.class, true), 
			new CommandParametersDescription<String>("substitution", String.class, false, true), 
		};
	public static CommandParametersDescription<?>[] getCommandParametersDescription()
	{
		return commandParametersDescription;
	}
	
	private static String[] stripFirstElement(String[] commandParameterValues)
	{
		if(commandParameterValues.length <= 1)
			return new String[0];
		
		String[] stringSubstitution = new String[commandParameterValues.length-1];
		System.arraycopy(commandParameterValues, 1, stringSubstitution, 0, stringSubstitution.length);
		
		return stringSubstitution;
	}
	
	private String sourceUrl;
	
	/**
	 * @param commandParameterValues
	 */
	public CommandFileCommand(String[] commandParameterValues)
	{
		super( stripFirstElement(commandParameterValues) );
		sourceUrl = commandParameterValues[0];
	}

	private String[][] commandsText;
	/**
	 * @return the commandsText
	 */
	@Override
	public synchronized String[][] getCommandsText()
	{
		if(this.commandsText == null)
			readCommandText();
		
		return this.commandsText;
	}

	private static final String RESOURCE_PROTOCOL = "resource://";
	/**
	 * Do NOT call this from anywhere but the getCommandsText() method !!!
	 */
	private void readCommandText()
	{
		InputStream textSourceStream = null;
		
		try
		{
			try
			{
				URL url = new URL(sourceUrl);
				textSourceStream = url.openStream();
			}
			catch (MalformedURLException x)
			{
				// if we can't open the stream as a URL then try it as a resource
				if(sourceUrl.startsWith(RESOURCE_PROTOCOL))
					sourceUrl = sourceUrl.substring(RESOURCE_PROTOCOL.length());
				textSourceStream = this.getClass().getClassLoader().getResourceAsStream(sourceUrl);
			}
		}
		catch(IOException ioX)
		{
			Logger.getLogger(this.getClass()).error("Unable to open command file '" + sourceUrl + "', " + ioX.getMessage());
		}
		
		if(textSourceStream != null)
		{
			StreamTokenizer tokenizer = new StreamTokenizer(new InputStreamReader(textSourceStream));
			tokenizer.resetSyntax();
			tokenizer.whitespaceChars(0x0A, 0x0A);
			tokenizer.whitespaceChars(0x0D, 0x0D);
			tokenizer.wordChars(0x21, 0x21);		// !
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
						commands.add( command.toArray(new String[command.size()]) );
						command.clear();
					}
					else
					{
						String token = tokenizer.sval == null ? "" : tokenizer.sval.trim(); 
						command.add(token);
					}
				}
				// add the last line read
				commands.add( command.toArray(new String[command.size()]) );
				
				this.commandsText = commands.toArray(new String[commands.size()][]);
			}
			catch (IOException ioX)
			{
				Logger.getLogger(this.getClass()).error("Error reading command file '" + sourceUrl + "', " + ioX.getMessage());
				ioX.printStackTrace();
			}
		}
		else
			Logger.getLogger(this.getClass()).error("Error opening command file '" + sourceUrl + "'.");
	}
}
