/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Jun 30, 2010
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author vhaiswbeckec
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */

package gov.va.med.interactive;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

/**
 * @author vhaiswbeckec
 *
 */
public abstract class ResourceBundleCommandList
{
	// The command parameter description property suffix
	protected static final String DESCRIPTION = "description";
	// The command shortcut property suffix
	protected static final String SHORTCUT = "shortcut";
	// The command help message property suffix
	protected static final String HELP_MESSAGE = "helpMessage";
	// The command name property suffix
	protected static final String COMMAND_NAME = "commandName";
	// The command class name property suffix
	protected static final String COMMAND_CLASS = "commandClass";
	
	public String getString(String key)
	{
		try
		{
			return getResourceBundle().getString(key);
		} 
		catch (MissingResourceException e)
		{
			return '!' + key + '!';
		}
	}
	
	/**
	 * @return
	 */
	protected abstract ResourceBundle getResourceBundle();

	private String[] commandNames = null;
	public synchronized String[] getCommandList()
	{
		if(commandNames == null)
		{
			String commandList = getString("commands");
			StringTokenizer st = new StringTokenizer(commandList, ",; \t|");
			commandNames = new String[st.countTokens()];
			for(int index=0; index < commandNames.length; ++index)
				commandNames[index] = st.nextToken();
		}		
		return commandNames;
	}
	
	public String getCommandName(String commandName)
	{
		return getString(commandName + "." + COMMAND_NAME);
	}
	
	public abstract Class<? extends Command<?>> getCommandClass(String commandName) 
	throws ClassNotFoundException;
	
	public String getCommandHelpMessage(String commandName)
	{
		return getString(commandName + "." + HELP_MESSAGE);
	}
	
	public String getCommandShortcut(String commandName)
	{
		return getString(commandName + "." + SHORTCUT);
	}
	
	public String getCommandParameterDescription(String commandName, String parameterName)
	{
		return getParameterString(commandName, parameterName, DESCRIPTION);
	}
	
	public String getParameterString(String commandName, String parameterName, String key)
	{
		return getString(commandName + "." + parameterName + "." + key);
	}
	
	public String getString(String commandName, String key)
	{
		return getString(commandName + "." + key);
	}
}
