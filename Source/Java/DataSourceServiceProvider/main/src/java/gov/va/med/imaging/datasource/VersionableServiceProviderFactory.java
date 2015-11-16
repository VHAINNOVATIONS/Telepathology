/**
 * 
 */
package gov.va.med.imaging.datasource;

import gov.va.med.imaging.DateUtil;
import gov.va.med.imaging.artifactsource.ArtifactSource;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.DataSourceExceptionHandler;
import gov.va.med.imaging.core.interfaces.exceptions.ConfigurationError;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.datasource.exceptions.NoValidServiceConstructorError;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import org.apache.log4j.Priority;

/**
 * 
 * @author vhaiswbeckec
 *
 * @param <T>
 */
class VersionableServiceProviderFactory<T extends VersionableDataSourceSpi>
extends ServiceProviderFactory<T>
{
	private final static int maximumConnectionExceptionRetryCount = 3;
	
	// the factory methods can look like either:
	// public static <S extends DataSourceSpi> S create(
	// Class<S> dataSourceClass, ResolvedArtifactSource artifactSource, String protocol
	// )
	// -or-
	// public static <S extends DataSourceSpi> S create(
	// ResolvedArtifactSource artifactSource, String protocol
	// )
	
	public final static Class<?>[] REQUIRED_TYPEDCREATE_METHOD_TYPES = new Class[]
	{ Class.class, ResolvedArtifactSource.class, String.class };
	public final static Class<?>[] REQUIRED_CREATE_METHOD_TYPES = new Class[]
	{ ResolvedArtifactSource.class, String.class };
	public final static Class<?>[] REQUIRED_CONSTRUCTOR_TYPES = new Class[]
	{ ResolvedArtifactSource.class, String.class };

	/**
	 * 
	 * @param parentProvider
	 * @param factoryServiceType
	 */
	VersionableServiceProviderFactory(Provider parentProvider, Class<? extends VersionableDataSourceSpi> spiType)
	{
		super(parentProvider, spiType);
	}

	public T createDataSource(
		ResolvedArtifactSource resolvedArtifactSource,
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException
	{
		if(resolvedArtifactSource == null || protocol == null)
			throw new IllegalArgumentException("Both the ResolvedArtifactSource and the protocol must be provided to create a data source.");
		
		// check the cache here to see if we know what protocol version that
		// this
		// URL speaks
		ProviderService cachedService = getCachedProviderService(resolvedArtifactSource, protocol);

		if (cachedService != null)
		{
			Provider.logger.debug("Found cached Service for " + getProductTypeName() + " provider using protocol '" + protocol + "' accessing '" + 
				(resolvedArtifactSource == null ? "no ResolvedArtifactSource" : resolvedArtifactSource.toString()) + 
				"'.");
			T dataSource = createDataSourceInstance(resolvedArtifactSource, protocol, cachedService);
			Provider.logger.info("Created DataSource provider accessing '" + 
				(resolvedArtifactSource == null ? "no ResolvedArtifactSource" : resolvedArtifactSource.toString()) + 
				"'.");

			return dataSource;
		}
		else
		{
			Provider.logger.info("Creating " + getProductTypeName() + " provider using protocol '" + protocol + "' accessing '"
				+ (resolvedArtifactSource == null ? "no ResolvedArtifactSource" : resolvedArtifactSource.toString()) + "'.");
			SortedSet<ProviderService> services = getParentProvider().findProviderServices(this.getSpiType(), protocol);
			Provider.logger.info(getProductTypeName() + ", the number of potential service implementations is "
				+ (services == null ? "none" : services.size()) + "'.");
			if (services == null)
			{
				Provider.logger.info("Applicable service implementations of type '"
					+ getProductTypeName() + "' for '" + resolvedArtifactSource.toString()
					+ "' are NOT available.");

				return null;
			}

			// for each of the Provider.Service (service implementations)
			// found
			for (ProviderService service : services)
			{
				T dataSource = createDataSourceInstance(resolvedArtifactSource, protocol, service);
				if (dataSource != null)
				{
					Provider.logger.info("Created " + getProductTypeName() + " provider accessing '"
						+ (resolvedArtifactSource == null ? "no URL" : resolvedArtifactSource.toString()) + "'.");

					// isVersionCompatible() is potentially an expensive
					// operation,
					// the Provider.Service caching is implemented to reduce
					// the need to call it.
					int retryCount = 0;
					while (retryCount < maximumConnectionExceptionRetryCount)
					{
						try
						{
							if (dataSource.isVersionCompatible())
							{
								Provider.logger.info("Created " + getProductTypeName()
									+ " provider accessing '" + (resolvedArtifactSource == null ? "no URL" : resolvedArtifactSource.toString())
									+ "' is version compatible.");
								// cache the Service mapped to the URL
								putProviderServiceToCache(resolvedArtifactSource, protocol, service);
								return dataSource;
							}
							// not versionCompatible, break from while loop
							break;
						}
						// can only handle connection based exceptions in
						// this way
						catch (ConnectionException cX)
						{
							retryCount++;
							boolean handled = false;
							if (dataSourceExceptionHandlers != null)
							{
								for (DataSourceExceptionHandler dseh : dataSourceExceptionHandlers)
								{
									if (dseh.isExceptionHandled(cX))
									{
										if (dseh.handleException(cX))
										{
											handled = true;
											break;
										}
									}
								}
							}
							// if the exception is not handled (no exception
							// handler)
							// then simply throw the exception, don't make
							// any retry attempts
							if (!handled)
								throw cX;
						}
					}
				}
				else
					Provider.logger.error("Failed to create " + 
						getProductTypeName() + " provider accessing '"
						+ (resolvedArtifactSource == null ? "no URL" : resolvedArtifactSource.toString()) + 
						"'.");
			}

			Provider.logger.info("Applicable service implementations of type '" +
				getProductTypeName() + 
				"' for '" + resolvedArtifactSource.toString() +
				"' are available but versions are incompatible.");

			return null;
		}
	}

	/**
	 * Create an instance of the data source of the requested type,
	 * connecting to the given URL. Create the instance using one of (in
	 * order of preference): public static create(URL, Site) public static
	 * create(URL) public ctor(URL, Site) public ctor(URL) public ctor()
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
	private T createDataSourceInstance(
		ResolvedArtifactSource resolvedArtifactSource,
		String protocol,
		ProviderService service)
	throws ConnectionException
	{
		Class<?> implementingClass = service.getImplementingClass();
		if(implementingClass == null)
			throw new IllegalArgumentException("The implementing class in '" + service.toString() + "' is null and must not be.");
		
		// a message fragment used in many logging messages
		String message = 
			"Creating instance of " + (implementingClass == null ? "null" : implementingClass.getName()) + ".\n" +
			"Using protocol " + protocol + ".\n" + 
			"Accessing " + (resolvedArtifactSource == null ? "null" : resolvedArtifactSource.toString()) + ".\n";

		T dataSource = null;
		
		try
		{
			// VersionableDataSourceSPI realizations must implement a
			// static create() method or a constructor with
			// a ResolvedArtifactSource and a String (protocol) parameter
			// the static create method may also have a parameter that indicates the service
			// type to create, this allows for dynamic proxy implementations of
			// data sources.
			try
			{
				dataSource = createInstanceUsingStaticCreateMethod(
					implementingClass, 
					service, 
					REQUIRED_TYPEDCREATE_METHOD_TYPES, 
					new Object[]{ service.getSpiType(), resolvedArtifactSource, protocol });
				
				Provider.logger.log( 
					(dataSource == null ? Priority.ERROR : Priority.DEBUG),
					dataSource == null ?
						"create(Class, ResolvedArtifactSource, String) returned null. This is a coding error and MUST be addressed." :
						"Successfully created instance of '" + dataSource.getClass().getName() + "' using create(Class, ResolvedArtifactSource, String)."
				);
			}
			catch (NoSuchMethodException nsmX1)
			{
				Provider.logger.debug("Unable to create data source provider using create(Class, ResolvedArtifactSource, String), continuing..." );
				try
				{
					dataSource = createInstanceUsingStaticCreateMethod(
						implementingClass, 
						service, 
						REQUIRED_CREATE_METHOD_TYPES, 
						new Object[]{ resolvedArtifactSource, protocol });
					
					Provider.logger.log( 
						(dataSource == null ? Priority.ERROR : Priority.DEBUG),
						dataSource == null ?
							"create(ResolvedArtifactSource, String) returned null. This is a coding error and MUST be addressed." :
							"Successfully created instance of '" + dataSource.getClass().getName() + "' using create(ResolvedArtifactSource, String)."
					);
				}
				catch(NoSuchMethodException nsmX2)
				{
					Provider.logger.debug("Unable to create data source provider using create(ResolvedArtifactSource, String), continuing..." );
					dataSource = createInstanceUsingConstructor(
						implementingClass, 
						service, 
						REQUIRED_CONSTRUCTOR_TYPES, 
						new Object[]{ resolvedArtifactSource, protocol });
					Provider.logger.debug( 
						"Successfully created instance of '" + dataSource.getClass().getName() + "' using <ctor>(ResolvedArtifactSource, String)."
					);
				}
			}
		}
		catch (java.lang.SecurityException e)
		{
			Provider.logger.error(e + message);
			throw new NoValidServiceConstructorError(implementingClass.getName(), e);
		}
		
		// This NoSuchMethodException instance is thrown only after all of
		// the potential instantiation methods have been tried.
		// In other words, there is no valid service factory method or
		// constructor.
		catch (NoSuchMethodException e)
		{
			Provider.logger.error(e + message);
			throw new NoValidServiceConstructorError(implementingClass.getName(), e);
		}
		catch (IllegalArgumentException e)
		{
			Provider.logger.error(e + message);
			throw new NoValidServiceConstructorError(implementingClass.getName(), e);
		}
		catch (InstantiationException e)
		{
			Provider.logger.error(e + message);
			throw new NoValidServiceConstructorError(implementingClass.getName(), e);
		}
		catch (IllegalAccessException e)
		{
			Provider.logger.error(e + message);
			throw new NoValidServiceConstructorError(implementingClass.getName(), e);
		}
		catch (InvocationTargetException e)
		{
			Provider.logger.error(e + message);
			// if there is a wrapped ConnectionException then unwrap it and
			// throw it
			// the constructors of a service implementation are expressly
			// permitted
			// to throw a ConnectionException
			if (e.getCause() != null && e.getCause() instanceof ConnectionException)
			{
				Provider.logger.error(e.getCause());
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
			Provider.logger.error(e);
			throw new NoValidServiceConstructorError(implementingClass.getName(), e);
		}
		
		return dataSource;
	}

	// =============================================================================
	// Service Version caching
	// =============================================================================
	
	// Retains the ResolvedArtifactSource (where the request is being sent) and the
	// protocol being used.  These are the keys used to select a SPI realization.
	private class ServiceCacheKey
	{
		private final ResolvedArtifactSource resolvedArtifactSource;
		private final String protocol;
		/**
		 * @param resolvedArtifactSource
		 * @param protocol
		 */
		public ServiceCacheKey(ResolvedArtifactSource resolvedArtifactSource, String protocol)
		{
			super();
			this.resolvedArtifactSource = resolvedArtifactSource;
			this.protocol = protocol;
		}
		public ResolvedArtifactSource getResolvedArtifactSource()
		{
			return this.resolvedArtifactSource;
		}
		public String getProtocol()
		{
			return this.protocol;
		}
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((protocol == null) ? 0 : protocol.hashCode());
			if(resolvedArtifactSource != null)
			{
				// use the details of the ArtifactSource to create the hashcode
				ArtifactSource artifactSource = resolvedArtifactSource.getArtifactSource();
				if(artifactSource != null)
				{
					result = prime
						* result 
						+ ((artifactSource.getHomeCommunityId() == null) ? 0
								: artifactSource.getHomeCommunityId().hashCode());
					result = prime
					* result 
					+ ((artifactSource.getRepositoryId() == null) ? 0
							: artifactSource.getRepositoryId().hashCode());
				}
			}
			return result;
		}
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ServiceCacheKey other = (ServiceCacheKey) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (protocol == null)
			{
				if (other.protocol != null)
					return false;
			}
			else if (!protocol.equals(other.protocol))
				return false;
			if (resolvedArtifactSource == null)
			{
				if (other.resolvedArtifactSource != null)
					return false;
			}
			else
			{
				if(other.resolvedArtifactSource == null)
					return false;
				if(resolvedArtifactSource.getArtifactSource() == null)
				{
					if(other.resolvedArtifactSource.getArtifactSource() != null)
						return false;
				}
				else
				{
					// use the details of the ArtifactSource to determine equality
					// both have ArtifactSources
					ArtifactSource thisArtifactsource = resolvedArtifactSource.getArtifactSource();
					ArtifactSource otherArtifactSource = other.resolvedArtifactSource.getArtifactSource();
					if(thisArtifactsource.getRepositoryId() == null)
					{
						if(otherArtifactSource.getRepositoryId() != null)
							return false;						
					}
					else if(!thisArtifactsource.getRepositoryId().equals(otherArtifactSource.getRepositoryId()))
						return false;
					
					if(thisArtifactsource.getHomeCommunityId() == null)
					{
						if(otherArtifactSource.getHomeCommunityId() != null)
							return false;
					}
					else if(!thisArtifactsource.getHomeCommunityId().equals(otherArtifactSource.getHomeCommunityId()))
						return false;
				}	
			}
			return true;
		}
		private VersionableServiceProviderFactory getOuterType()
		{
			return VersionableServiceProviderFactory.this;
		}
		
	};
	
	// Retains the Provider.Service (the service description) and the time that
	// it was cached.
	private class ServiceVersionCacheValue
	{
		private ProviderService service;
		private long putTime = System.currentTimeMillis();

		ServiceVersionCacheValue(ProviderService service)
		{
			this.service = service;
		}

		public ProviderService getService()
		{
			return service;
		}

		public long getPutTime()
		{
			return putTime;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (this.putTime ^ (this.putTime >>> 32));
			result = prime * result + ((this.service == null) ? 0 : this.service.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final ServiceVersionCacheValue other = (ServiceVersionCacheValue) obj;
			if (this.putTime != other.putTime)
				return false;
			if (this.service == null)
			{
				if (other.service != null)
					return false;
			}
			else if (!this.service.equals(other.service))
				return false;
			return true;
		}
	}

	private Map<ServiceCacheKey, ServiceVersionCacheValue> urlServiceVersionCache = 
		new HashMap<ServiceCacheKey, ServiceVersionCacheValue>();
	private static final long serviceCacheMaxAge = DateUtil.MILLISECONDS_IN_DAY;

	private ProviderService getCachedProviderService(
		ResolvedArtifactSource resolvedArtifactSource,
		String protocol)
	{
		ServiceCacheKey key = new ServiceCacheKey(resolvedArtifactSource, protocol);
		synchronized (urlServiceVersionCache)
		{
			ServiceVersionCacheValue cacheElement = urlServiceVersionCache.get(key);
			if (cacheElement == null)
				return null;

			// expire the cache element if it is too old
			if (System.currentTimeMillis() - cacheElement.getPutTime() > serviceCacheMaxAge)
			{
				urlServiceVersionCache.remove(key);
				return null;
			}
			return cacheElement.getService();
		}
	}

	private void putProviderServiceToCache(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol, 
		ProviderService service)
	{
		ServiceCacheKey key = new ServiceCacheKey(resolvedArtifactSource, protocol);
		
		synchronized (urlServiceVersionCache)
		{
			urlServiceVersionCache.put(key, new ServiceVersionCacheValue(service));
		}
	}
	
	/**
	 * 
	 */
	public void clearProviderServiceCache()
	{
		synchronized(urlServiceVersionCache)
		{
			urlServiceVersionCache.clear();
		}
	}
}