/**
 * 
 */
package gov.va.med.imaging.datasource;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * The base class for service provider factories.
 * 
 * @author vhaiswbeckec
 *
 * @param <T>
 */
abstract class ServiceProviderFactory<T extends DataSourceSpi>
{
	private final Provider parentProvider;
	private final Class<? extends DataSourceSpi> spiType;

	ServiceProviderFactory(Provider parentProvider, Class<? extends DataSourceSpi> spiType)
	{
		if(parentProvider == null || spiType == null)
			throw new IllegalArgumentException("Both parent provider and SPI type must be provided to construct a ServiceProviderFactory.");
		
		this.parentProvider = parentProvider;
		this.spiType = spiType;
	}

	public Provider getParentProvider()
	{
		return this.parentProvider;
	}

	public Class<? extends DataSourceSpi> getSpiType()
	{
		return this.spiType;
	}

	public String getProductTypeName()
	{
		return getSpiType().getSimpleName();
	}
	
	/**
	 * @param url
	 * @param site
	 * @param implementingClass
	 * @param service 
	 * @return
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@SuppressWarnings("unchecked")
	protected T createInstanceUsingStaticCreateMethod(
		Class<?> implementingClass, 
		ProviderService service, 
		Class<?>[] createParameterTypes,
		Object[] createParameters) 
	throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		T dataSource;
		String createLogMessage = "create(";
		if (createParameterTypes != null)
			for (Class<?> parameterType : createParameterTypes)
				createLogMessage = createLogMessage + " " + parameterType.getName();
		createLogMessage = createLogMessage + ")";

		Provider.logger.debug(this.spiType.getSimpleName() + " instance, attempting instantiation using " + createLogMessage
			+ ".");

		Method factoryMethod = implementingClass.getDeclaredMethod("create", createParameterTypes);
		int modifiers = factoryMethod.getModifiers();
		if (!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers))
		{
			String msg = "Factory method create(URL, Site) exists but is not static and public so will not be used.";
			Provider.logger.warn(msg);
			throw new NoSuchMethodException(msg);
		}
		dataSource = (T) factoryMethod.invoke(null, createParameters);
		Provider.logger.info(this.spiType.getSimpleName() + " instance, instantiated using " + createLogMessage + ".");

		dataSource.setConfiguration(service.getProvider().getInstanceConfiguration());
		
		return dataSource;
	}

	/**
	 * 
	 * @param implementingClass
	 * @param service 
	 * @param constructorParameterTypes
	 * @param constructorParameters
	 * @return
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 */
	@SuppressWarnings("unchecked")
	protected T createInstanceUsingConstructor(
		Class<?> implementingClass, 
		ProviderService service, 
		Class<?>[] constructorParameterTypes,
		Object[] constructorParameters) 
	throws NoSuchMethodException, IllegalAccessException, 
	       InvocationTargetException, IllegalArgumentException, 
	       InstantiationException
	{
		T dataSource;
		String createLogMessage = "<ctor>(";
		if (constructorParameterTypes != null)
			for (Class<?> parameterType : constructorParameterTypes)
				createLogMessage = createLogMessage + " " + parameterType.getName();
		createLogMessage = createLogMessage + ")";

		Provider.logger.debug(this.spiType.getSimpleName() + " instance, attempting instantiation using " + createLogMessage
			+ ".");
		Constructor<?> constructor = implementingClass.getConstructor(constructorParameterTypes);
		int modifiers = constructor.getModifiers();
		if (!Modifier.isPublic(modifiers))
		{
			String msg = "constructor(URL, Site) exists but is not public so will not be used.";
			Provider.logger.warn(msg);
			throw new NoSuchMethodException(msg);
		}

		dataSource = (T) constructor.newInstance(constructorParameters);
		Provider.logger.debug(this.spiType.getSimpleName() + " instance, instantiated using " + createLogMessage + ".");
		
		dataSource.setConfiguration(service.getProvider().getInstanceConfiguration());

		return dataSource;
	}
}