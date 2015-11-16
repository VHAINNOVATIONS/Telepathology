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

import gov.va.med.imaging.core.interfaces.router.Command;
import gov.va.med.imaging.core.interfaces.router.CommandContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.apache.log4j.Logger;

/**
 * CommandCreatorProvider is an abstract base for creating commands as a provider.  This handles the actual searching for and creating
 * of the command. Implementations of this provider are responsible for specifying the packages their commands exist in and for assigning
 * the command context (creating an extension if desired)
 * 
 * @author VHAISWWERFEJ
 *
 */
public abstract class CommandCreatorProvider 
{
	private CommandContext baseCommandContext;

	public CommandCreatorProvider()
	{

	}

	/**
	 * Return an array of the package names supported by this provider. Ideally this list of packages is unique in a VISA deployment
	 * @return
	 */
	protected abstract String [] getCommandPackageNames();
	
	/**
	 * Return the command context to assign to any commands owned by this command creator provider. The assigned CommandContext should be 
	 * based on the baseCommandContext parameter. If no special changes need to be made to the CommandContext, this method can simply
	 * return baseCommandContext
	 *  
	 * @param baseCommandContext
	 * @return
	 */
	protected abstract CommandContext getCommandContext(CommandContext baseCommandContext);

	private final static Logger logger = Logger.getLogger(CommandCreatorProvider.class);
	protected Logger getLogger()
	{
		return logger;
	}
	
	/**
	 * Determines if the command is supported by this provider
	 * @param commandClassSemantics
	 * @param parameterTypes
	 * @param initArgs
	 * @return
	 */
	protected boolean isCommandSupported(CommandClassSemantics commandClassSemantics,
			Class<?>[] parameterTypes, 
			Object[] initArgs)
	{
		Constructor<?> constructor = findConstructor(commandClassSemantics, 
				parameterTypes, initArgs, false);
		return constructor != null ? true : false;
	}

	@SuppressWarnings("unchecked")
	protected <R extends Object> Command<R> createCommand(
			CommandClassSemantics commandClassSemantics,
			Class<?>[] parameterTypes, 
			Object[] initArgs)
	{
		getLogger().debug("Attempting to create command '" + commandClassSemantics.toString(true) + 
				"' with CommandCreatorProvider '" + this.getClass().getSimpleName());
		Constructor<?> selectedConstructor = 
			findConstructor(commandClassSemantics, parameterTypes, initArgs, true);
		if(selectedConstructor == null)
		{
			getLogger().debug("Cannot find Command using CommandCreatorProvider '" + this.getClass().getSimpleName() + 
					"' of type '" + commandClassSemantics.toString(true) + 
					"' failed because there were no constructors like '" + makeConstructorMessage(commandClassSemantics, parameterTypes) + 
			"'.");
		}
		else
		{
			try
			{
				Command<R> command = (Command<R>)selectedConstructor.newInstance(initArgs);
				command.setCommandContext(getCommandContext(getBaseCommandContext()));

				return command;
			} 
			catch (Exception x)
			{
				getLogger().warn("Request to construct Command of type '" + commandClassSemantics.toString(true) + 
						"' in CommandCreatorProvider '" + this.getClass().getSimpleName() + "' failed in the constructor.", x);
			} 
		}	

		return null;
	}
	
	private Constructor<?> findConstructor(CommandClassSemantics commandClassSemantics,
			Class<?>[] parameterTypes, 
			Object[] initArgs, boolean initializeClass)
	{
		if(commandClassSemantics != null)
		{
			try
			{
				Class<? extends Command<?>> commandClass = 
					findCommandClass( commandClassSemantics.toStringAsImplementation(), 
							commandClassSemantics.getCommandPackage(), initializeClass);

				Constructor<?> selectedConstructor = null;				
				Constructor<?>[] commandConstructors = commandClass.getConstructors();
				for(Constructor<?> constructor : commandConstructors)
				{
					if( Modifier.isPublic( constructor.getModifiers() ) )
					{
						if(evaluateConstructorParameters(constructor, parameterTypes))
						{
							if(selectedConstructor != null)
							{
								getLogger().error(
										"Request to construct Command of type '" + commandClassSemantics.toString(true) + 
										"' failed multiple constructors like '" + makeConstructorMessage(commandClassSemantics, parameterTypes) + 
								"' could not be differentiated.");
								return null;
							}
							selectedConstructor = constructor;
						}
					}
				}

				if(selectedConstructor == null)
				{
					getLogger().error(
							"Request to construct Command of type '" + commandClassSemantics.toString(true) + 
							"' failed because there were no constructors like '" + makeConstructorMessage(commandClassSemantics, parameterTypes) + 
					"'.");
				}
				else
				{
					return selectedConstructor;
				}
			} 
			catch (ClassNotFoundException x)
			{
				//getLogger().error("Request to construct Command of type '" + commandClassSemantics.toString() + "' failed because the class was not found.");
				getLogger().debug("Unable to construct Command of type '" + commandClassSemantics.toString() + "' using CommandCreatorProvider '" + this.getClass().getSimpleName() + "'.");
			}
		}

		return null;
	}

	/**
	 * For error and info messages, build a String representation of the
	 * constructor we are looking for.
	 *  
	 * @param commandClassSemantics
	 * @param parameterTypes
	 * @return
	 */
	private String makeConstructorMessage(
			CommandClassSemantics commandClassSemantics,
			Class<?>[] argTypes)
	{
		StringBuilder sb = new StringBuilder();

		sb.append(commandClassSemantics.toString());
		sb.append("(");
		boolean firstArg = true;
		for(Class<?> argType : argTypes)
		{
			if(!firstArg)
				sb.append(", ");
			sb.append(argType.getName());
			firstArg = false;
		}

		sb.append(")");

		return sb.toString();
	}

	/**
	 * @param stringAsImplementation
	 * @return
	 * @throws ClassNotFoundException 
	 */
	@SuppressWarnings("unchecked")
	private Class<? extends Command<?>> findCommandClass(String stringAsImplementation,
			String commandPackage, boolean initializeClass)
	throws ClassNotFoundException
	{		
		String [] commandPackageNames = getCommandPackageNames();
		if(commandPackage != null && commandPackage.length() > 0)
		{
			String packageAndCommandName = commandPackage + "." + stringAsImplementation;
			// if the package is specified then need to check if the package is part of the packages managed by
			// the current provider.  because all the commands are in the classpath, anyone can find them
			// but we want to be sure the "correct" provider finds it so they can assign the correct CommandContext
			
			for(String commandPackageName : commandPackageNames)
			{
				if(commandPackage.equals(commandPackageName))
				{			
					// if the command package was found in this provider, try to create the command (really should work)
					try
					{				
						getLogger().debug("Searching for command '" + packageAndCommandName + "' using specified package");
						Class<? extends Command<?>> commandClass = 
							(Class<? extends Command<?>>)Class.forName( packageAndCommandName, 
									initializeClass, this.getClass().getClassLoader() );

						return commandClass;
					} 
					catch (ClassNotFoundException cnfX){}
					catch (ClassCastException ccX){}
				}
			}
			// may not find command if this provider doesn't support that command
			getLogger().debug("Did not find command '" + packageAndCommandName + "' using specified package, cannot create command - check specified .");
		}
		else
		{
			// if package specified, don't brute force it
			for(String commandPackageName : commandPackageNames)
			{
				try
				{
					String packageAndCommandName = commandPackageName + "." + stringAsImplementation;
					Class<? extends Command<?>> commandClass = 
						(Class<? extends Command<?>>)Class.forName( packageAndCommandName, 
								initializeClass, this.getClass().getClassLoader() );
	
					return commandClass;
				} 
				catch (ClassNotFoundException cnfX){}
				catch (ClassCastException ccX){}
			}
		}

		throw new ClassNotFoundException("No valid command class named '" + stringAsImplementation + "' found in any of the command packages");
	}

	/**
	 * @param constructor
	 * @param parameterTypes
	 * @return
	 */
	private boolean evaluateConstructorParameters(
			Constructor<?> constructor,
			Class<?>[] parameterTypes)
	{
		Class<?>[] constructorParameterTypes = constructor.getParameterTypes();

		// if both parameters lists are null or zero length, return true
		if( (parameterTypes == null || parameterTypes.length == 0) && 
				(constructorParameterTypes == null || constructorParameterTypes.length == 0) )
			return true;

		// if the parameters list are null or zero length and the constructor parameter lists are not then return false
		if( (parameterTypes == null || parameterTypes.length == 0) && 
				(constructorParameterTypes != null && constructorParameterTypes.length > 0) )
			return false;

		// if the parameters list are not null or zero length and the constructor parameter lists are then return false
		if( (parameterTypes != null && parameterTypes.length > 0) && 
				(constructorParameterTypes == null || constructorParameterTypes.length == 0) )
			return false;

		// if the parameter type lists are different length, return null
		if( parameterTypes.length  != constructorParameterTypes.length )
			return false;

		// finally compare to see if the parameter lists are compatible
		for(int parameterIndex=0; parameterIndex < constructorParameterTypes.length; ++parameterIndex)
		{
			Class<?> parameterType = parameterTypes[parameterIndex];
			Class<?> constructorParameterType = constructorParameterTypes[parameterIndex];

			// if the parameterType is assignable to the constructor type then
			// we should not get a ClassCastException calling the constructor
			// otherwise we will and the constructor is incompatible with our parameters
			if( ! constructorParameterType.isAssignableFrom(parameterType) )
				return false;
		}

		return true;
	}

	public void setBaseCommandContext(CommandContext baseCommandContext) 
	{
		this.baseCommandContext = baseCommandContext;
	}

	public CommandContext getBaseCommandContext() {
		return baseCommandContext;
	}
}
