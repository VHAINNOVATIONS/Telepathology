/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Dec 14, 2010
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

package gov.va.med.imaging.core;

import gov.va.med.imaging.core.interfaces.router.Command;
import gov.va.med.imaging.core.interfaces.router.CommandFactory;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * @author vhaiswbeckec
 * @deprecated This is not actually used and will not be used, replaced in intention by CommandCreatorProvider
 *
 */
public class CommandFactoryProvider
implements CommandFactory
{
	private static String DEFAULT_NAME = "Base Command Factory Provider";
	private static double DEFAULT_VERSION = Byte.MAX_VALUE;				// by default, sort ourselves last
	private static String DEFAULT_INFO = "The command factory definition and base class.";
	
	private final Logger logger = Logger.getLogger(this.getClass());
	private ClassLoader providerClassLoader;
	private ServiceLoader<CommandFactoryProvider> providerServiceLoader;

	// ===========================================================================================
	// Instance Fields and Methods
	// ===========================================================================================
	private final String name;
	private final double version;
	private final String info;

	/**
	 * Create a Provider using the current class loader to load services.
	 */
	public CommandFactoryProvider()
	{
		this( DEFAULT_NAME, DEFAULT_VERSION, DEFAULT_INFO );
	}

	/**
	 * Sets the service class loader, which defines where the service providers will be
	 * loaded from.
	 * 
	 * @param serviceClassLoader
	 */
	public CommandFactoryProvider(ClassLoader serviceClassLoader)
	{
		this( DEFAULT_NAME, DEFAULT_VERSION, DEFAULT_INFO );
		this.setProviderClassLoader(serviceClassLoader);
	}
	
	/**
	 * 
	 * @param name
	 * @param version
	 * @param info
	 */
	protected CommandFactoryProvider(String name, double version, String info)
	{
		this.name = name;
		this.version = version;
		this.info = info;
	}

	public String getName()
	{
		return name;
	}

	public double getVersion()
	{
		return version;
	}

	public String getInfo()
	{
		return info;
	}
	
	/**
	 * @return the logger
	 */
	protected Logger getLogger()
	{
		return this.logger;
	}

	/**
	 * The ProviderClassLoader must be set before any calls to get service
	 * implementations else an IllegalStateException will be thrown.
	 * 
	 * @param classLoader
	 */
	public final synchronized void setProviderClassLoader(ClassLoader classLoader)
	{
		if (providerServiceLoader != null)
			throw new IllegalStateException(
				"The provider class loader must be set before any calls to acquire service implementations.");
		providerClassLoader = classLoader;
	}

	/**
	 * 
	 * @return
	 */
	public final synchronized boolean isProviderLoaderSet()
	{
		return providerServiceLoader != null;
	}
	
	/**
	 * 
	 * @return
	 */
	private final synchronized ServiceLoader<CommandFactoryProvider> getProviderLoader()
	{
		// This (next) line determines where the provider packages will be
		// loaded from.
		// Whatever ClassLoader has loaded this will determine the classpath
		// that
		// provider packages are found.
		// If the providerClassLoader has been set then that will be used as the
		// class loader.
		if (providerServiceLoader == null)
		{
			getLogger().info("ServiceLoader being created under context class loader ["
				+ (providerClassLoader == null ? "context class loader " + CommandFactoryProvider.class.getClassLoader().toString()
					: "specified class loader" + providerClassLoader.toString()) + "].");

			providerServiceLoader = 
				providerClassLoader == null ? 
					ServiceLoader.load(CommandFactoryProvider.class) : 
					ServiceLoader.load(CommandFactoryProvider.class, providerClassLoader);
		}
		return providerServiceLoader;
	}
	
	public final ClassLoader getProviderClassLoader()
	{
		return providerClassLoader;
	}
	
	/**
	 * @param dss
	 * @return
	 */
	public final SortedSet<CommandFactoryInfo> getAllInstalledCommandFactories()
	{
		// will contain the list of all applicable service implementations
		SortedSet<CommandFactoryInfo> services = new TreeSet<CommandFactoryInfo>();

		// for each known provider, see if it implements a service of the
		// requested type
		ServiceLoader<CommandFactoryProvider> loader = getProviderLoader();
		for (CommandFactoryProvider commandFactoryProvider : loader)
		{
			
		}
		
		return services.size() > 0 ? Collections.unmodifiableSortedSet(services) : null;
	}
	
	private Set<CommandFactoryInfo> commandFactories = null;

	/**
	 * Each derivative of this class should override this method to "advertise" its
	 * command factories.
	 * 
	 * @return
	 */
	public synchronized Set<CommandFactoryInfo> getCommandFactoryInfo()
	{
		if(commandFactories == null)
		{
			commandFactories = new HashSet<CommandFactoryInfo>();
			commandFactories.add(new CommandFactoryInfo(this, (byte)1, CommandFactoryImpl.class));
		}
		return commandFactories;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.router.CommandFactory#createCollectionCommand(java.lang.Class, java.lang.Class, java.lang.String, java.lang.Class<?>[], java.lang.Object[])
	 */
	@Override
	public <C extends Collection<R>, R> Command<C> createCollectionCommand(
		Class<C> collectionClass,
		Class<R> resultClass, 
		String commandClassName, 
		String commandPackage,
		Class<?>[] initArgTypes, 
		Object[] initArgs)
	throws IllegalArgumentException
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.router.CommandFactory#createCollectionCommand(java.lang.Class, java.lang.Class, java.lang.String, java.lang.Object[])
	 */
	/*
	@Override
	public <C extends Collection<R>, R> Command<C> createCollectionCommand(Class<C> collectionClass,
		Class<R> resultClass, String commandClassName, String commandPackage, Object[] initArgs) throws IllegalArgumentException
	{
		// TODO Auto-generated method stub
		return null;
	}*/

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.router.CommandFactory#createCommand(java.lang.Class, java.lang.String, java.lang.Class<?>[], java.lang.Object[])
	 */
	@Override
	public <R> Command<R> createCommand(
		Class<R> resultClass, 
		String commandClassName, 
		String commandPackage, 
		Class<?>[] initArgTypes,
		Object[] initArgs)
	{
		Set<CommandFactoryInfo> commandFactories = getAllInstalledCommandFactories();
		
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.router.CommandFactory#createCommand(java.lang.Class, java.lang.String, java.lang.Object[])
	 */
	/*
	@Override
	public <R> Command<R> createCommand(
		Class<R> resultClass, 
		String commandClassName, 
		String commandPackage, 
		Object[] initArgs)
	throws IllegalArgumentException
	{
		// TODO Auto-generated method stub
		return null;
	}*/

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.router.CommandFactory#createMapCollectionCommand(java.lang.Class, java.lang.Class, java.lang.Class, java.lang.String, java.lang.Class<?>[], java.lang.Object[])
	 */
	@Override
	public <C extends Map<K, V>, K, V> Command<C> createMapCollectionCommand(Class<C> collectionClass,
		Class<K> mapKeyClass, Class<V> mapValueClass, String commandClassName, String commandPackage, Class<?>[] initArgTypes,
		Object[] initArgs) throws IllegalArgumentException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <R> boolean isCommandSupported(Class<R> resultClass,
			String commandClassName, String commandPackage,
			Class<?>[] initArgTypes, Object[] initArgs)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <C extends Collection<R>, R> boolean isCollectionCommandSupported(
			Class<C> collectionClass, Class<R> resultClass,
			String commandClassName, String commandPackage,
			Class<?>[] initArgTypes, Object[] initArgs)
			throws IllegalArgumentException
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <C extends Map<K, V>, K, V> boolean isMapCollectionCommandSupported(
			Class<C> collectionClass, Class<K> mapKeyClass,
			Class<V> mapValueClass, String commandClassName,
			String commandPackage, Class<?>[] initArgTypes, Object[] initArgs)
			throws IllegalArgumentException
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.router.CommandFactory#createMapCollectionCommand(java.lang.Class, java.lang.Class, java.lang.Class, java.lang.String, java.lang.Object[])
	 */
	/*
	@Override
	public <C extends Map<K, V>, K, V> Command<C> createMapCollectionCommand(
		Class<C> collectionClass,
		Class<K> mapKeyClass, 
		Class<V> mapValueClass, 
		String commandClassName, 
		String commandPackage, 
		Object[] initArgs)
	throws IllegalArgumentException
	{
		// TODO Auto-generated method stub
		return null;
	}*/
}
