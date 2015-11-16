/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Oct 1, 2008
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author VHAISWBECKEC
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
package gov.va.med.imaging.core;

import gov.va.med.imaging.core.interfaces.router.Command;
import gov.va.med.imaging.core.interfaces.router.CommandContext;
import gov.va.med.imaging.core.interfaces.router.CommandFactory;
import java.util.Collection;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * @author VHAISWBECKEC
 *
 */
public class CommandFactoryImpl
implements CommandFactory
{
	private final CommandContext commandContext;
	private final Logger logger = Logger.getLogger(this.getClass());
	
	/**
	 * This constructor has been deprecated in favor of the single argument
	 * constructor (having a fully constructed command factory).
	 * @see #CommandFactoryImpl(CommandContext)
	 * @param router
	 */
	@Deprecated
	public CommandFactoryImpl(
		RouterImpl router)
	{
		this.commandContext = new CommandContextImpl(router, this);
	}
	
	/**
	 * The CommandContext should reference the CommandFactoryProvider as the command factory
	 * to provide access to all the installed command factories. 
	 *  
	 * @param commandContext
	 */
	public CommandFactoryImpl(CommandContext commandContext)
	{
		this.commandContext = commandContext;
	}
	
	// ==================================================================================================
	//
	/*
	private String [] getCommandPackageNames()
	{
		String [] commands = new String [CommandVocabulary.values().length];
		int count = 0;
		for(CommandVocabulary cv : CommandVocabulary.values())
		{
			commands[count] = cv.getPackageName() + ".";
			count++;
		}				
		return commands;
	}*/

	private CommandContext getCommandContext()
	{
		return this.commandContext;
	}
	
	/**
	 * @return the logger
	 */
	public Logger getLogger()
	{
		return this.logger;
	}

	/**
	 * 
	 * @param commandClassName
	 * @param parameterTypes
	 * @param parameters
	 * @return
	 */
	public <R extends Object> Command<R> createCommand(
		Class<R> resultClass,
		String commandClassName, 
		String commandPackage,
		Class<?>[] parameterTypes, 
		Object[] parameters)
	{
		CommandClassSemantics commandClassSemantics = null;
		try
		{
			commandClassSemantics = CommandClassSemantics.create(commandClassName, commandPackage);
		} 
		catch (CoreRouterSemanticsException x)
		{
			getLogger().error( "The command name '" + commandClassName + "' is not a valid command class name according to the defined semantics.", x );
			return (Command<R>)null;
		}
		
		return createCommand(
			commandClassSemantics, 
			parameterTypes, 
			parameters);
	}
	
	@Override
	public <R> boolean isCommandSupported(Class<R> resultClass,
			String commandClassName, String commandPackage,
			Class<?>[] parameterTypes, Object[] parameters)
	{
		CommandClassSemantics commandClassSemantics = null;
		try
		{
			commandClassSemantics = CommandClassSemantics.create(commandClassName, commandPackage);
		} 
		catch (CoreRouterSemanticsException x)
		{
			getLogger().error( "The command name '" + commandClassName + "' is not a valid command class name according to the defined semantics.", x );
			return false;
		}
		return isCommandSupported(commandClassSemantics, parameterTypes, parameters);
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.router.CommandFactory#createCollectionCommand(java.lang.Class, java.lang.Class, java.lang.String, java.lang.Class<?>[], java.lang.Object[])
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <C extends Collection<R>, R> Command<C> createCollectionCommand(
			Class<C> collectionClass, Class<R> resultClass,
			String commandClassName, String commandPackage,
			Class<?>[] initArgTypes, Object[] initArgs)
	throws IllegalArgumentException
	{
		Command<?> command = createNonTypesafeCommand(commandClassName, commandPackage,
				initArgTypes, initArgs);
		
		return (Command<C>)command;
	}
	
	@Override
	public <C extends Collection<R>, R> boolean isCollectionCommandSupported(
			Class<C> collectionClass, Class<R> resultClass,
			String commandClassName, String commandPackage,
			Class<?>[] initArgTypes, Object[] initArgs)
			throws IllegalArgumentException
	{
		CommandClassSemantics commandClassSemantics = null;
		try
		{
			commandClassSemantics = CommandClassSemantics.create(commandClassName, commandPackage);
		} 
		catch (CoreRouterSemanticsException x)
		{
			getLogger().error( "The command name '" + commandClassName + "' is not a valid command class name according to the defined semantics.", x );
			return false;
		}
		
		return isCommandSupported(
			commandClassSemantics, 
			initArgTypes, 
			initArgs);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <C extends Map<K,V>, K, V> Command<C> createMapCollectionCommand(
		Class<C> collectionClass,
		Class<K> mapKeyClass, Class<V> mapValueClass, 
		String commandClassName, 
		String commandPackage,
		Class<?>[] initArgTypes,
		Object[] initArgs) 
	throws IllegalArgumentException
	{
		Command<?> command = createNonTypesafeCommand(commandClassName, commandPackage,
				initArgTypes, initArgs);
		
		return (Command<C>)command;
	}

	@Override
	public <C extends Map<K, V>, K, V> boolean isMapCollectionCommandSupported(
			Class<C> collectionClass, Class<K> mapKeyClass,
			Class<V> mapValueClass, String commandClassName,
			String commandPackage, Class<?>[] initArgTypes, Object[] initArgs)
			throws IllegalArgumentException
	{
		CommandClassSemantics commandClassSemantics = null;
		try
		{
			commandClassSemantics = CommandClassSemantics.create(commandClassName, commandPackage);
		} 
		catch (CoreRouterSemanticsException x)
		{
			getLogger().error( "The command name '" + commandClassName + "' is not a valid command class name according to the defined semantics.", x );
			return false;
		}
		
		return isCommandSupported(
			commandClassSemantics, 
			initArgTypes, 
			initArgs);
	}

	/**
	 * 
	 * @param commandClassName
	 * @param initArgTypes
	 * @param initArgs
	 * @return
	 */
	private Command<?> createNonTypesafeCommand(String commandClassName, String commandPackage, 
			Class<?>[] initArgTypes, Object[] initArgs)
	{
		CommandClassSemantics commandClassSemantics = null;
		try
		{
			commandClassSemantics = CommandClassSemantics.create(commandClassName, commandPackage);
		} 
		catch (CoreRouterSemanticsException x)
		{
			getLogger().error( "The command name '" + commandClassName + "' is not a valid command class name according to the defined semantics.", x );
			return (Command<?>)null;
		}
		
		return createCommand(
			commandClassSemantics, 
			initArgTypes, 
			initArgs);
	}
	
	private static CommandCreator commandCreator = null;
	private synchronized static CommandCreator getCommandCreator(CommandContext commandContext)
	{
		if(commandCreator == null)
		{
			commandCreator = new CommandCreator(commandContext);
		}
		return commandCreator;
	}
	
	/**
	 * 
	 */
	private <R extends Object> Command<R> createCommand(
		CommandClassSemantics commandClassSemantics,
		Class<?>[] parameterTypes, 
		Object[] initArgs)
	{
		CommandCreator commandCreator = getCommandCreator(getCommandContext());
		return commandCreator.createCommand(commandClassSemantics, parameterTypes, initArgs);	
	}
	
	private boolean isCommandSupported(
			CommandClassSemantics commandClassSemantics,
			Class<?>[] parameterTypes, 
			Object[] initArgs)
	{
		CommandCreator commandCreator = getCommandCreator(getCommandContext());
		return commandCreator.isCommandSupported(commandClassSemantics, parameterTypes, initArgs);	
	}
}
