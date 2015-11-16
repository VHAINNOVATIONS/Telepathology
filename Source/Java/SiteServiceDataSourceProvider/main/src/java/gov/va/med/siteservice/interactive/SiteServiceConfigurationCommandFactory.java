/**
 * 
 */
package gov.va.med.siteservice.interactive;

import gov.va.med.imaging.datasource.Provider;
import gov.va.med.interactive.Command;
import gov.va.med.interactive.CommandListCommandFactory;
import gov.va.med.siteservice.SiteResolutionProviderConfiguration;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 
 * @author vhaiswbeckec
 *
 */
public class SiteServiceConfigurationCommandFactory
extends CommandListCommandFactory<SiteResolutionProviderConfiguration>
{
	private Provider provider;
	
	/**
	 * @param provider 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public SiteServiceConfigurationCommandFactory(Provider provider) 
	throws IOException, ClassNotFoundException, SecurityException, NoSuchMethodException, 
		IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		super();
		this.provider = provider;
	}

	/**
	 * @return the provider
	 */
	public Provider getProvider()
	{
		return this.provider;
	}

	/**
	 * @see gov.va.med.interactive.CommandListCommandFactory#getCommandPackageNames()
	 */
	@Override
	public String[] getCommandPackageNames()
	{
		return new String[]
		{
			"gov.va.med.interactive.commands",						// standard commands
			this.getClass().getPackage().getName() + ".commands"	// application specific commands
		};
	}

	/* (non-Javadoc)
	 * @see gov.va.med.interactive.CommandListCommandFactory#createCommand(java.lang.String, java.lang.String[])
	 */
	@Override
	public Command<SiteResolutionProviderConfiguration> createCommand(String commandText, String[] commandArgs)
	throws InstantiationException, IllegalAccessException, ClassCastException, SecurityException,
		NoSuchMethodException, IllegalArgumentException, InvocationTargetException
	{
		Command<SiteResolutionProviderConfiguration> command = super.createCommand(commandText, commandArgs);
		try
		{
			Method setProviderMethod = command.getClass().getMethod("setProvider", new Class<?>[]{Provider.class});
			setProviderMethod.invoke(command, new Object[]{getProvider()});
		}
		catch(Exception x){}
		return command;
	}

	/**
	 * @see gov.va.med.interactive.CommandListCommandFactory#createCommand(java.lang.String)
	 */
	@Override
	public Command<SiteResolutionProviderConfiguration> createCommand(String commandText) 
	throws InstantiationException, IllegalAccessException, ClassCastException, SecurityException, 
		NoSuchMethodException, IllegalArgumentException, InvocationTargetException
	{
		Command<SiteResolutionProviderConfiguration> command = super.createCommand(commandText);
		
		return command;
	}
	
	
}