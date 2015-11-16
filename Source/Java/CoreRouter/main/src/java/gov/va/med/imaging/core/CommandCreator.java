/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 4, 2011
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
  Description: 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+

 */
package gov.va.med.imaging.core;

import java.util.ServiceLoader;

import org.apache.log4j.Logger;

import gov.va.med.imaging.core.interfaces.router.Command;
import gov.va.med.imaging.core.interfaces.router.CommandContext;

/**
 * CommandCreator creates commands using the CommandCreatorProviders that are installed.
 * 
 * @author VHAISWWERFEJ
 *
 */
public class CommandCreator 
{
	private final CommandContext baseCommandContext;
	private final static Logger logger = Logger.getLogger(CommandCreator.class);
	public Logger getLogger()
	{
		return logger;
	}
	
	public CommandCreator(CommandContext baseCommandContext)
	{
		this.baseCommandContext = baseCommandContext;
	}

	private static ServiceLoader<CommandCreatorProvider> commandCreatorLoader = null;
	
	private synchronized static ServiceLoader<CommandCreatorProvider> getCommandFactoryLoader(CommandContext commandContext)
	{
		if(commandCreatorLoader == null)
		{
			commandCreatorLoader = ServiceLoader.load(CommandCreatorProvider.class);
			for(CommandCreatorProvider commandCreator : commandCreatorLoader)
			{
				if(commandContext != null)
					commandCreator.setBaseCommandContext(commandContext);
			}
		}
		return commandCreatorLoader;
	}
	
	/**
	 * Create a command from any of the available command creator providers that support the command/
	 * 
	 * @param <R>
	 * @param commandClassSemantics
	 * @param parameterTypes
	 * @param initArgs
	 * @return
	 */
	public <R extends Object> Command<R> createCommand(
			CommandClassSemantics commandClassSemantics,
			Class<?>[] parameterTypes, 
			Object[] initArgs)
	{
		ServiceLoader<CommandCreatorProvider> serviceLoader = 
			getCommandFactoryLoader(baseCommandContext);
		for(CommandCreatorProvider commandCreator : serviceLoader)
		{
			Command<R> command = commandCreator.createCommand(commandClassSemantics, parameterTypes, initArgs);
			if(command != null)
				return command;
		}
		getLogger().fatal("Unable to create command '" + commandClassSemantics.toString() + "'.");
		return null;
	}
	
	/**
	 * Method to determine if the command can be created by any of the providers installed
	 * 
	 * @param commandClassSemantics
	 * @param parameterTypes
	 * @param initArgs
	 * @return
	 */
	public boolean isCommandSupported(
			CommandClassSemantics commandClassSemantics,
			Class<?>[] parameterTypes, 
			Object[] initArgs)
	{
		ServiceLoader<CommandCreatorProvider> serviceLoader = 
			getCommandFactoryLoader(baseCommandContext);
		for(CommandCreatorProvider commandCreator : serviceLoader)
		{
			boolean commandSupported = 
				commandCreator.isCommandSupported(commandClassSemantics, 
						parameterTypes, initArgs);
			if(commandSupported)
				return true;
		}
		return false;
	}
}
