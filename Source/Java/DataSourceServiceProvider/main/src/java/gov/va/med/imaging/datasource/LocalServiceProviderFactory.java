/**
 * 
 */
package gov.va.med.imaging.datasource;

import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConfigurationError;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.datasource.exceptions.NoValidServiceConstructorError;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

/**
 * 
 * @author vhaiswbeckec
 *
 * @param <T>
 */
class LocalServiceProviderFactory<T extends LocalDataSourceSpi>
extends ServiceProviderFactory<T>
{
	public final static Class<?>[] REQUIRED_TYPEDCREATE_METHOD_TYPES = new Class[]
	{ Class.class };
	/**
	 * 
	 * @param factoryServiceType
	 */
	LocalServiceProviderFactory(Provider parentProvider, Class<? extends DataSourceSpi> spiType)
	{
		super(parentProvider, spiType);
	}

	/**
	 * Create an instance of a service that is expected to be the single
	 * implementation of a particular SPI.
	 * 
	 * @return
	 * @throws ConnectionException
	 */
	public T createSingletonServiceInstance()
	throws ConnectionException
	{
		return createSingletonServiceInstance(null);
	}
	
	public T createSingletonServiceInstance(ResolvedArtifactSource resolvedArtifactSource) 
	throws ConnectionException
	{
		List<T> servicesInstances = createServiceInstances(resolvedArtifactSource);

		if (servicesInstances != null && servicesInstances.size() > 1)
			throw new ConnectionException("Unable to determine which instance of "
				+ getProductTypeName() + " to create, multiple instances installed.");

		return servicesInstances == null || servicesInstances.size() == 0 ? null : servicesInstances.get(0);
	}
	
	@SuppressWarnings("boxing")
	public List<T> createServiceInstances() 
	throws ConnectionException
	{
		return createServiceInstances(null);
	}

	/**
	 * Create and return a List of LocalDataSourceSpi implementing
	 * instances.
	 * 
	 * @return
	 * @throws ConnectionException
	 */
	@SuppressWarnings("boxing")
	public List<T> createServiceInstances(ResolvedArtifactSource resolvedArtifactSource) 
	throws ConnectionException
	{
		List<T> serviceInstances = new ArrayList<T>();

		Provider.logger.info("Creating LOCAL " + getProductTypeName() + " services.");
		SortedSet<ProviderService> services = getParentProvider().findProviderLocalServices(getSpiType());
		Provider.logger.info(getProductTypeName() + ", the number of potential service implementations is '"
			+ (services == null ? "none" : services.size()) + "'.");
		if (services == null)
		{
			Provider.logger.info("Applicable LOCAL service implementations of type '"
				+ getProductTypeName() + "' are NOT available.");

			return null;
		}

		// for each of the Provider.Service (service implementations) found
		for (ProviderService service : services)
		{
			T dataSource = createDataSourceInstance(service, resolvedArtifactSource);
			if (dataSource != null)
			{
				Provider.logger.info("Created LOCAL " + getProductTypeName() + ".");

				serviceInstances.add(dataSource);
			}
		}

		if (serviceInstances.size() < 1)
			Provider.logger.error("Applicable LOCAL service implementations of type '"
				+ getProductTypeName()
				+ "' are available but instantiation failed for all implementations..  Services are NOT available.");
		else
			Provider.logger.info(serviceInstances.size() + " LOCAL service implementations of type '"
				+ getProductTypeName()
				+ "' have been instantiated.  Services are available.");

		return serviceInstances;
	}

	/**
	 * Create an instance of the data source of the requested type,
	 * connecting to the given URL. Create the instance using one of (in
	 * order of preference): public static create() public ctor()
	 * 
	 * @param url
	 * @param site
	 * @param service
	 * @return Returns an instance of the requested data source or throws an
	 *         exception.
	 * @throws ConnectionException
	 * @throws ConfigurationError -
	 *             if a service implementation is improperly implemented or
	 *             if a service implementation cannot be found or an
	 *             instance created then this method will throw a
	 *             ConfigurationError, which is an unchecked exception
	 */
	private T createDataSourceInstance(ProviderService service, ResolvedArtifactSource resolvedArtifactSource) 
	throws ConnectionException
	{
		Class<?> implementingClass = service.getImplementingClass();
		if(implementingClass == null)
			throw new IllegalArgumentException("The implementing class in '" + service.toString() + "' is null and must not be.");

		try
		{
			// DataSourceSPI realizations may or may not implement a
			// constructor with a Site parameter
			T dataSource = null;
			
			// the static create method may also have a parameter that indicates the service
			// type to create, this allows for dynamic proxy implementations of
			// data sources.
			try
			{
				dataSource = createInstanceUsingStaticCreateMethod(
					implementingClass, 
					service, 
					REQUIRED_TYPEDCREATE_METHOD_TYPES, 
					new Object[]{ service.getSpiType() });
			}
			catch (NoSuchMethodException nsmX1)
			{
				Class<?> [] createParameterTypes = null;
				Object [] createParameter = null;
				if(resolvedArtifactSource != null)
				{
					createParameterTypes = new Class [] {ResolvedArtifactSource.class};
					createParameter = new Object [] {resolvedArtifactSource};
				}
				else
				{
					createParameterTypes = new Class [] {};
					createParameter = new Object [] {};
				}
				try
				{
					dataSource = createInstanceUsingStaticCreateMethod(
						implementingClass,
						service, 
						createParameterTypes,
						createParameter);
				}
				catch (NoSuchMethodException nsmX2)
				{
					dataSource = createInstanceUsingConstructor(
						implementingClass, 
						service, 
						createParameterTypes, 
						createParameter);
				}
			}
			return dataSource;
		}
		catch (java.lang.SecurityException e)
		{
			Provider.logger.error("Exception when creating service '" + service.toString() + "', implementing class is '"
				+ (implementingClass == null ? "<unknown>" : implementingClass.getName()), e);
			throw new NoValidServiceConstructorError(implementingClass.getName(), e);
		}
		// This NoSuchMethodException instance is thrown only after all of
		// the
		// potential instantiation methods have been tried.
		// In other words, there is no valid service factory method or
		// constructor.
		catch (NoSuchMethodException e)
		{
			Provider.logger.error("Exception when creating service '" + service.toString() + "', implementing class is '"
				+ (implementingClass == null ? "<unknown>" : implementingClass.getName()), e);
			throw new NoValidServiceConstructorError(implementingClass.getName(), e);
		}
		catch (IllegalArgumentException e)
		{
			Provider.logger.error("Exception when creating service '" + service.toString() + "', implementing class is '"
				+ (implementingClass == null ? "<unknown>" : implementingClass.getName()), e);
			throw new NoValidServiceConstructorError(implementingClass.getName(), e);
		}
		catch (InstantiationException e)
		{
			Provider.logger.error("Exception when creating service '" + service.toString() + "', implementing class is '"
				+ (implementingClass == null ? "<unknown>" : implementingClass.getName()), e);
			throw new NoValidServiceConstructorError(implementingClass.getName(), e);
		}
		catch (IllegalAccessException e)
		{
			Provider.logger.error("Exception when creating service '" + service.toString() + "', implementing class is '"
				+ (implementingClass == null ? "<unknown>" : implementingClass.getName()), e);
			throw new NoValidServiceConstructorError(implementingClass.getName(), e);
		}
		catch (InvocationTargetException e)
		{
			Provider.logger.error("Exception when creating service '" + service.toString() + "', implementing class is '"
				+ (implementingClass == null ? "<unknown>" : implementingClass.getName()), e);
			// if there is a wrapped ConnectionException then unwrap it and
			// throw it
			// the constructors of a service implementation are expressly
			// permitted
			// to throw a ConnectionException
			if (e.getCause() != null && e.getCause() instanceof ConnectionException)
			{
				Provider.logger.error("above exception when creating service '" + service.toString()
					+ " caused by following exception", e.getCause());
				throw (ConnectionException) e.getCause();
			}
			// If the service failed to instantiate for some internal reason
			// that is not a ConnectionException then throw a
			// NoValidServiceConstructorException.
			throw new NoValidServiceConstructorError(implementingClass.getName(), e);
		}
		// a create method was defined but the return was not castable to
		// the
		// service type requested
		catch (ClassCastException e)
		{
			Provider.logger.error("Exception when creating service '" + service.toString() + "', implementing class is '"
				+ (implementingClass == null ? "<unknown>" : implementingClass.getName()), e);
			throw new NoValidServiceConstructorError(implementingClass.getName(), e);
		}
	}

}