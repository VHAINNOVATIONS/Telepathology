/**
 * 
 */
package gov.va.med.imaging.datasource;

import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.DataSourceExceptionHandler;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.datasource.annotations.SPI;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * @author VHAISWBECKEC
 * 
 * @see http://java.sun.com/javase/6/docs/api/java/util/ServiceLoader.html
 * 
 * "A service is a well-known set of interfaces and (usually abstract) classes.
 * A service provider is a specific implementation of a service. The classes in
 * a provider typically implement the interfaces and subclass the classes
 * defined in the service itself. Service providers can be installed in an
 * implementation of the Java platform in the form of extensions, that is, jar
 * files placed into any of the usual extension directories. Providers can also
 * be made available by adding them to the application's class path or by some
 * other platform-specific means. For the purpose of loading, a service is
 * represented by a single type, that is, a single interface or abstract class.
 * (A concrete class can be used, but this is not recommended.) A provider of a
 * given service contains one or more concrete classes that extend this service
 * type with data and code specific to the provider. The provider class is
 * typically not the entire provider itself but rather a proxy which contains
 * enough information to decide whether the provider is able to satisfy a
 * particular request together with code that can create the actual provider on
 * demand. The details of provider classes tend to be highly service-specific;
 * no single class or interface could possibly unify them, so no such type is
 * defined here. The only requirement enforced by this facility is that provider
 * classes must have a zero-argument constructor so that they can be
 * instantiated during loading. A service provider is identified by placing a
 * provider-configuration file in the resource directory META-INF/services. The
 * file's name is the fully-qualified binary name of the service's type. The
 * file contains a list of fully-qualified binary names of concrete provider
 * classes, one per line. Space and tab characters surrounding each name, as
 * well as blank lines, are ignored. The comment character is '#' ('\u0023',
 * NUMBER SIGN); on each line all characters following the first comment
 * character are ignored. The file must be encoded in UTF-8."
 * 
 * This class represents a "provider" for the VIX Facade Service Provider
 * Interface, where a provider implements some or all parts of VIX Data Sources.
 * Services that a provider may implement include: StudyGraphDataSource (such as
 * VistA, BIA or Federation). UserPreferenceDataSource (such as VistA)
 * 
 * Each provider has a name and a version number, and is configured in each
 * runtime it is installed in.
 * 
 */
public class Provider
extends Properties
implements DataSourceProvider
{
	private static final long serialVersionUID = 1L;
	static final transient Logger logger = Logger.getLogger(Provider.class);
	
	private ClassLoader providerClassLoader;
	private ServiceLoader<Provider> providerServiceLoader;
	private final ProviderDataSourceFactories providerServiceFactories = new ProviderDataSourceFactories(this);

	/**
	 * The ProviderClassLoader must be set before any calls to get service
	 * implementations else an IllegalStateException will be thrown.
	 * 
	 * @param classLoader
	 */
	public synchronized void setProviderClassLoader(ClassLoader classLoader)
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
	public synchronized boolean isProviderLoaderSet()
	{
		return providerServiceLoader != null;
	}
	
	/**
	 * 
	 * @return
	 */
	private synchronized ServiceLoader<Provider> getProviderLoader()
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
			logger.info("ServiceLoader being created under context class loader ["
				+ (providerClassLoader == null ? "context class loader " + Provider.class.getClassLoader().toString()
					: "specified class loader" + providerClassLoader.toString()) + "].");

			providerServiceLoader = providerClassLoader == null ? ServiceLoader.load(Provider.class) : ServiceLoader.load(
				Provider.class, providerClassLoader);
		}
		return providerServiceLoader;
	}
	
	public ClassLoader getProviderClassLoader()
	{
		return providerClassLoader;
	}

	/**
	 * Create a single instance of a LocalDataSource of the specified type. 
	 * 
	 * @param <S>
	 * @param spiType
	 * @return
	 * @throws ConnectionException
	 */
	public <S extends LocalDataSourceSpi> S createLocalDataSource(Class<S> spiType) 
	throws ConnectionException
	{
		LocalServiceProviderFactory<S> localDataSourceFactory = 
			this.providerServiceFactories.getOrCreateLocalServiceProviderFactory(spiType);
		if(localDataSourceFactory == null)
			throw new ConnectionException("Failed to create a data source factory producing instances of '" + spiType.getSimpleName() + "'.");
		S dataSource = localDataSourceFactory.createSingletonServiceInstance();

		return dataSource;
	}
	
	public <S extends LocalDataSourceSpi> S createLocalDataSource(Class<S> spiType, 
			ResolvedArtifactSource resolvedArtifactSource) 
	throws ConnectionException
	{
		LocalServiceProviderFactory<S> localDataSourceFactory = 
			this.providerServiceFactories.getOrCreateLocalServiceProviderFactory(spiType);
		if(localDataSourceFactory == null)
			throw new ConnectionException("Failed to create a data source factory producing instances of '" + spiType.getSimpleName() + "'.");
		S dataSource = localDataSourceFactory.createSingletonServiceInstance(resolvedArtifactSource);

		return dataSource;
	}

	/**
	 * Create a List of instances of LocalDataSourceSpi of the specified type.
	 * 
	 * @param <S>
	 * @param spiType
	 * @return
	 * @throws ConnectionException
	 */
	public <S extends LocalDataSourceSpi> List<S> createLocalDataSources(Class<S> spiType) 
	throws ConnectionException
	{
		LocalServiceProviderFactory<S> localDataSourceFactory = 
			this.providerServiceFactories.getOrCreateLocalServiceProviderFactory(spiType);
		if(localDataSourceFactory == null)
			throw new ConnectionException("Failed to create a data source factory producing instances of '" + spiType.getSimpleName() + "'.");
		List<S> dataSources = localDataSourceFactory.createServiceInstances();

		return dataSources;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.va.med.imaging.datasource.DataSourceProvider#createSiteResolutionDataSource()
	 */
	public SiteResolutionDataSourceSpi createSiteResolutionDataSource() 
	throws ConnectionException
	{
		return createLocalDataSource(SiteResolutionDataSourceSpi.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.va.med.imaging.datasource.DataSourceProvider#createRoutingOverrideServices()
	 */
	public List<RoutingOverrideSpi> createRoutingOverrideServices() 
	throws ConnectionException
	{
		return createLocalDataSources(RoutingOverrideSpi.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.va.med.imaging.datasource.DataSourceProvider#createImageDataSource(java.net.URL,
	 *      gov.va.med.imaging.core.interfaces.LocalizedSite)
	 */
	public ImageDataSourceSpi createImageDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException
	{
		return createVersionableDataSource(ImageDataSourceSpi.class, resolvedArtifactSource, protocol, dataSourceExceptionHandlers);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.va.med.imaging.datasource.DataSourceProvider#createStudyGraphDataSource(java.net.URL,
	 *      gov.va.med.imaging.core.interfaces.LocalizedSite)
	 */
	public StudyGraphDataSourceSpi createStudyGraphDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException
	{
		return createVersionableDataSource(StudyGraphDataSourceSpi.class, resolvedArtifactSource, protocol, dataSourceExceptionHandlers);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.va.med.imaging.datasource.DataSourceProvider#createImageAccessLoggingDataSource(java.net.URL,
	 *      gov.va.med.imaging.core.interfaces.LocalizedSite)
	 */
	public ImageAccessLoggingSpi createImageAccessLoggingDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException
	{
		return createVersionableDataSource(ImageAccessLoggingSpi.class, resolvedArtifactSource, protocol, dataSourceExceptionHandlers);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.va.med.imaging.datasource.DataSourceProvider#createDocumentDataSource(java.net.URL,
	 *      gov.va.med.imaging.core.interfaces.LocalizedSite)
	 */
	public DocumentDataSourceSpi createDocumentDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException
	{
		return createVersionableDataSource(DocumentDataSourceSpi.class, resolvedArtifactSource, protocol, dataSourceExceptionHandlers);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.va.med.imaging.datasource.DataSourceProvider#createDocumentDataSource(java.net.URL,
	 *      gov.va.med.imaging.core.interfaces.LocalizedSite)
	 */
	public DocumentSetDataSourceSpi createDocumentSetDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException
	{
		return createVersionableDataSource(DocumentSetDataSourceSpi.class, resolvedArtifactSource, protocol, dataSourceExceptionHandlers);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.va.med.imaging.datasource.DataSourceProvider#createUserPreferenceDataSource(java.net.URL,
	 *      gov.va.med.imaging.core.interfaces.LocalizedSite)
	 */
	public UserPreferenceDataSourceSpi createUserPreferenceDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException
	{
		return createVersionableDataSource(UserPreferenceDataSourceSpi.class, resolvedArtifactSource, protocol, dataSourceExceptionHandlers);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.va.med.imaging.datasource.DataSourceProvider#createPatientDataSource(java.net.URL,
	 *      gov.va.med.imaging.core.interfaces.LocalizedSite)
	 */
	public PatientDataSourceSpi createPatientDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException
	{
		return createVersionableDataSource(PatientDataSourceSpi.class, resolvedArtifactSource, protocol, dataSourceExceptionHandlers);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.va.med.imaging.datasource.DataSourceProvider#createDicomDataSource(java.net.URL,
	 *      gov.va.med.imaging.core.interfaces.LocalizedSite)
	 */
	public DicomDataSourceSpi createDicomDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException
	{
		return createVersionableDataSource(DicomDataSourceSpi.class, resolvedArtifactSource, protocol, dataSourceExceptionHandlers);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.va.med.imaging.datasource.DataSourceProvider#createDicomDataSource(java.net.URL,
	 *      gov.va.med.imaging.core.interfaces.LocalizedSite)
	 */
	public DicomStorageDataSourceSpi createDicomStorageDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException
	{
		return createVersionableDataSource(DicomStorageDataSourceSpi.class, resolvedArtifactSource, protocol, dataSourceExceptionHandlers);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.va.med.imaging.datasource.DataSourceProvider#createDicomDataSource(java.net.URL,
	 *      gov.va.med.imaging.core.interfaces.LocalizedSite)
	 */
	public DicomImporterDataSourceSpi createDicomImporterDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException
	{
		return createVersionableDataSource(DicomImporterDataSourceSpi.class, resolvedArtifactSource, protocol, dataSourceExceptionHandlers);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.va.med.imaging.datasource.DataSourceProvider#createDicomDataSource(java.net.URL,
	 *      gov.va.med.imaging.core.interfaces.LocalizedSite)
	 */
	public WorkListDataSourceSpi createWorkListDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException
	{
		return createVersionableDataSource(WorkListDataSourceSpi.class, resolvedArtifactSource, protocol, dataSourceExceptionHandlers);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.va.med.imaging.datasource.DataSourceProvider#createDicomDataSource(java.net.URL,
	 *      gov.va.med.imaging.core.interfaces.LocalizedSite)
	 */
	public StorageDataSourceSpi createStorageDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException
	{
		return createVersionableDataSource(StorageDataSourceSpi.class, resolvedArtifactSource, protocol, dataSourceExceptionHandlers);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.va.med.imaging.datasource.DataSourceProvider#createDicomDataSource(java.net.URL,
	 *      gov.va.med.imaging.core.interfaces.LocalizedSite)
	 */
	public DurableQueueDataSourceSpi createDurableQueueDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException
	{
		return createVersionableDataSource(DurableQueueDataSourceSpi.class, resolvedArtifactSource, protocol, dataSourceExceptionHandlers);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.va.med.imaging.datasource.DataSourceProvider#createDicomDataSource(java.net.URL,
	 *      gov.va.med.imaging.core.interfaces.LocalizedSite)
	 */
	public ServiceRegistrationDataSourceSpi createServiceRegistrationDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException
	{
		return createVersionableDataSource(ServiceRegistrationDataSourceSpi.class, resolvedArtifactSource, protocol, dataSourceExceptionHandlers);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.va.med.imaging.datasource.DataSourceProvider#createDicomDataSource(java.net.URL,
	 *      gov.va.med.imaging.core.interfaces.LocalizedSite)
	 */
	public VeinsDataSourceSpi createVeinsDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException
	{
		return createVersionableDataSource(VeinsDataSourceSpi.class, resolvedArtifactSource, protocol, dataSourceExceptionHandlers);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.va.med.imaging.datasource.DataSourceProvider#createDicomDataSource(java.net.URL,
	 *      gov.va.med.imaging.core.interfaces.LocalizedSite)
	 */
	public VistaRadDataSourceSpi createVistaRadDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException
	{
		return createVersionableDataSource(VistaRadDataSourceSpi.class, resolvedArtifactSource, protocol, dataSourceExceptionHandlers);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.va.med.imaging.datasource.DataSourceProvider#createVistaRadImageDataSource(java.net.URL,
	 *      gov.va.med.imaging.core.interfaces.LocalizedSite)
	 */
	@Override
	public VistaRadImageDataSourceSpi createVistaRadImageDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException
	{
		return createVersionableDataSource(VistaRadImageDataSourceSpi.class, resolvedArtifactSource, protocol, dataSourceExceptionHandlers);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.va.med.imaging.datasource.DataSourceProvider#createPassthroughDataSource()
	 */
	@Override
	public PassthroughDataSourceSpi createPassthroughDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException
	{
		return createVersionableDataSource(PassthroughDataSourceSpi.class, resolvedArtifactSource, protocol, dataSourceExceptionHandlers);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.va.med.imaging.datasource.DataSourceProvider#createUserAuthenticationDataSource(java.net.URL,
	 *      gov.va.med.imaging.core.interfaces.LocalizedSite)
	 */
	@Override
	public UserAuthenticationSpi createUserAuthenticationDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException
	{
		return createVersionableDataSource(UserAuthenticationSpi.class, resolvedArtifactSource, protocol, dataSourceExceptionHandlers);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.va.med.imaging.datasource.DataSourceProvider#createExternalPackageDataSource(java.net.URL,
	 *      gov.va.med.imaging.core.interfaces.LocalizedSite)
	 */
	@Override
	public ExternalPackageDataSourceSpi createExternalPackageDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException
	{
		return createVersionableDataSource(ExternalPackageDataSourceSpi.class, resolvedArtifactSource, protocol, dataSourceExceptionHandlers);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.va.med.imaging.datasource.DataSourceProvider#createStudyGraphDataSource(java.net.URL,
	 *      gov.va.med.imaging.core.interfaces.LocalizedSite)
	 */
	public TransactionLoggerDataSourceSpi createTransactionLoggerDataSource() 
	throws ConnectionException
	{
		return createLocalDataSource(TransactionLoggerDataSourceSpi.class);
	}

	@Override
	public ExternalSystemOperationsDataSourceSpi createExternalSystemOperationsDataSource(
			ResolvedArtifactSource resolvedArtifactSource, 
			String protocol,
			DataSourceExceptionHandler... dataSourceExceptionHandlers)
	throws ConnectionException
	{
		return createVersionableDataSource(ExternalSystemOperationsDataSourceSpi.class, resolvedArtifactSource, protocol, dataSourceExceptionHandlers);
	}

	@Override
	public PatientArtifactDataSourceSpi createPatientArtifactDataSource(
			ResolvedArtifactSource resolvedArtifactSource, String protocol,
			DataSourceExceptionHandler... dataSourceExceptionHandlers)
	throws ConnectionException
	{
		return createVersionableDataSource(
			PatientArtifactDataSourceSpi.class, 
			resolvedArtifactSource, 
			protocol, 
			dataSourceExceptionHandlers);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.va.med.imaging.datasource.DataSourceProvider#createDicomDataSource(java.net.URL,
	 *      gov.va.med.imaging.core.interfaces.LocalizedSite)
	 */
	public EventLoggingDataSourceSpi createEventLoggingDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException
	{
		EventLoggingDataSourceSpi dataSource = createVersionableDataSource(
			EventLoggingDataSourceSpi.class, 
			resolvedArtifactSource, 
			protocol, 
			dataSourceExceptionHandlers);
		
		return dataSource;
	}
	


	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.DataSourceProvider#createDicomQueryRetrieveDataSource(gov.va.med.imaging.artifactsource.ResolvedArtifactSource, java.lang.String, gov.va.med.imaging.core.interfaces.DataSourceExceptionHandler[])
	 */
	@Override
	public DicomQueryRetrieveDataSourceSpi createDicomQueryRetrieveDataSource(
		ResolvedArtifactSource resolvedArtifactSource, String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException
	{		
		return createVersionableDataSource(
			DicomQueryRetrieveDataSourceSpi.class, 
			resolvedArtifactSource, 
			protocol, 
			dataSourceExceptionHandlers
		);
	}

	
	@Override
	public DicomQueryRetrieveDataSourceSpi createLocalDicomQueryRetrieveDataSource(ResolvedArtifactSource resolvedArtifactSource)
	throws ConnectionException 
	{
		return createLocalDataSource(DicomQueryRetrieveDataSourceSpi.class, resolvedArtifactSource);
	}

	@Override
	public DicomStorageDataSourceSpi createLocalDicomStorageDataSource(
			ResolvedArtifactSource resolvedArtifactSource)
	throws ConnectionException 
	{
		return createLocalDataSource(DicomStorageDataSourceSpi.class, resolvedArtifactSource);
	}

	@Override
	public DicomApplicationEntityDataSourceSpi createLocalDicomApplicationEntityDataSource(
			ResolvedArtifactSource resolvedArtifactSource)
	throws ConnectionException 
	{
		return createLocalDataSource(DicomApplicationEntityDataSourceSpi.class, resolvedArtifactSource);
	}

	@Override
	public DicomDataSourceSpi createLocalDicomDataSource(
			ResolvedArtifactSource resolvedArtifactSource)
	throws ConnectionException 
	{
		return createLocalDataSource(DicomDataSourceSpi.class, resolvedArtifactSource);
	}

	@Override
	public DicomImporterDataSourceSpi createLocalDicomImporterDataSource(
			ResolvedArtifactSource resolvedArtifactSource)
	throws ConnectionException 
	{
		return createLocalDataSource(DicomImporterDataSourceSpi.class, resolvedArtifactSource);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.DataSourceProvider#createDicomQueryRetrieveDataSource(gov.va.med.imaging.artifactsource.ResolvedArtifactSource, java.lang.String, gov.va.med.imaging.core.interfaces.DataSourceExceptionHandler[])
	 */
	@Override
	public DicomApplicationEntityDataSourceSpi createDicomApplicationEntityDataSource(
		ResolvedArtifactSource resolvedArtifactSource, String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException
	{
		return createVersionableDataSource(
			DicomApplicationEntityDataSourceSpi.class, 
			resolvedArtifactSource, 
			protocol, 
			dataSourceExceptionHandlers
		);
	}
	
	@Override
	public UserDataSourceSpi createUserDataSource(
			ResolvedArtifactSource resolvedArtifactSource, String protocol,
			DataSourceExceptionHandler... dataSourceExceptionHandlers)
			throws ConnectionException
	{
		return createVersionableDataSource(
				UserDataSourceSpi.class, 
				resolvedArtifactSource, 
				protocol, 
				dataSourceExceptionHandlers
			);
	}

	/**
	 * Create an instance of the versionable data source of the type specified,
	 * capable of contacting the given data source over the specified protocol.
	 * 
	 * @param <S>
	 * @param spiType
	 * @return
	 * @throws ConnectionException
	 */
	public <S extends VersionableDataSourceSpi> S createVersionableDataSource(
		Class<S> spiType,
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers)
	throws ConnectionException
	{
		VersionableServiceProviderFactory<S> versionableDataSourceFactory = 
			this.providerServiceFactories.getOrCreateVersionableServiceProviderFactory(spiType);
		if(versionableDataSourceFactory == null)
			throw new ConnectionException("Failed to create a data source factory producing instances of '" + spiType.getSimpleName() + "'.");
		S dataSource = versionableDataSourceFactory.createDataSource(resolvedArtifactSource, protocol, dataSourceExceptionHandlers);

		return dataSource;
	}

	/**
	 * Find a service provider of the given type, over the given protocol and
	 * specific protocol version. For example:
	 * findProviderService("Service.StudyGraphDataSource", "vista", 1.0f) will
	 * return an Provider.Service instance
	 * 
	 * @param type -
	 *            if type is not a valid String form of a DataSourceServices
	 *            then a null pointer exception will be thrown
	 * @param protocol
	 * @param protocolVersion -
	 *            if the version is <= 0.0f the most first service
	 *            implementation of the type and protocol specified will be
	 *            returned. By default this is the highest numbered protocol
	 *            version.
	 * @return
	 */
	
	/**
	 * Return the service implementation where: 1.) the service implemented is
	 * of the specified type 2.) the service implemented communicates over the
	 * url's protocol 3.) the protocol version is the most recent
	 * 
	 * @param serviceType
	 * @param url
	 * @return
	 */
	public ProviderService findProviderServiceWithLatestProtocolVersion(
		Class<? extends DataSourceSpi> spiType,
		URL url)
	{
		logger.info("Searching installed providers for service [" + spiType.getSimpleName() + ", "
			+ url.toExternalForm() + "]");
		ServiceLoader<Provider> loader = getProviderLoader();
		for (Provider provider : loader)
		{
			logger.info("Searching provider [" + provider.getName() + "]");

			ProviderService service = provider.getService(spiType, url);
			if (service != null)
				return service;
		}
		return null;
	}

	/**
	 * Find a service provider of the given type, over the given protocol and
	 * specific protocol version. For example:
	 * findProviderService(Service.StudyGraphDataSource, "vista", 1.0f) will
	 * return an Provider.Service instance
	 * 
	 * @param serviceType
	 * @param protocol
	 * @param protocolVersion
	 * @return
	 */
	public ProviderService findProviderService(
		Class<? extends DataSourceSpi> spiType, 
		String protocol,
		float protocolVersion)
	{
		ServiceLoader<Provider> loader = getProviderLoader();
		for (Provider provider : loader)
		{
			ProviderService service = provider.getService(spiType, protocol, protocolVersion);
			if (service != null)
				return service;
		}
		return null;
	}

	/**
	 * Return a sorted set of all service implementations where: 1.) the service
	 * implemented is of the specified type 2.) the service implemented
	 * communicates over the url's protocol 3.) the protocol version is any
	 * value
	 * 
	 * @param serviceType
	 * @param url
	 * @return
	 */
	public SortedSet<ProviderService> findProviderServices(
		Class<? extends DataSourceSpi> spiType, 
		String protocol)
	{
		if (spiType == null)
			return null;

		logger.info("Searching installed providers for services [" + spiType.getSimpleName() + ", "
			+ protocol + "]");

		// will contain the list of all applicable service implementations
		SortedSet<ProviderService> applicableServices = new TreeSet<ProviderService>();

		// for each known provider, see if it implements a service of the
		// requested type
		ServiceLoader<Provider> loader = getProviderLoader();
		for (Provider provider : loader)
		{
			logger.debug("Searching provider [" + provider.getName() + 
				"] for service type [" + spiType.getSimpleName() + ", "
				+ protocol +"].");

			SortedSet<ProviderService> providerServices = provider.getServices(spiType, protocol);
			if (providerServices != null)
				applicableServices.addAll(providerServices);
		}
		return applicableServices.size() > 0 ? applicableServices : null;
	}

	/**
	 * 
	 * @param serviceType
	 * @return
	 */
	public SortedSet<ProviderService> findProviderLocalServices(Class<? extends DataSourceSpi> spiType)
	{
		if (spiType == null)
			return null;

		logger.info("Searching installed providers for services [" + spiType.getSimpleName() + "]");

		// will contain the list of all applicable service implementations
		SortedSet<ProviderService> applicableServices = new TreeSet<ProviderService>();

		// for each known provider, see if it implements a service of the
		// requested type
		ServiceLoader<Provider> loader = getProviderLoader();
		for (Provider provider : loader)
		{
			logger.info("Searching provider [" + provider.getName() + "] for LOCAL services of type "
				+ spiType.getSimpleName() + ".");

			SortedSet<ProviderService> providerServices = provider.getLocalServices(spiType);
			if (providerServices != null)
			{
				logger.info("Provider [" + provider.getName() + "] implements " + providerServices.size()
					+ " LOCAL services of type " + spiType.getSimpleName() + ".");
				applicableServices.addAll(providerServices);
				logger.info("There are now " + applicableServices.size()
					+ " cumulative implementions of LOCAL services type " + spiType.getSimpleName() + ".");
			}
			else
				logger.info("Provider [" + provider.getName() + "] implements no LOCAL services of type "
					+ spiType.getSimpleName() + ".");
		}
		return applicableServices;
	}

	// ===========================================================================================
	// Instance Fields and Methods
	// ===========================================================================================
	private final String name;
	private final double version;
	private final String info;

	/**
	 * Create a Provider using the current class loader to load services.
	 */
	public Provider()
	{
		this( "Core Abstract Provider", 1.0, "The abstract DataSourceProvider implementation.  Provides access to the realized providers." );
	}

	/**
	 * Sets the service class loader, which defines where the service providers will be
	 * loaded from.
	 * 
	 * @param serviceClassLoader
	 */
	public Provider(ClassLoader serviceClassLoader)
	{
		this( "Core Abstract Provider", 1.0, "The abstract DataSourceProvider implementation.  Provides access to the realized providers." );
		this.setProviderClassLoader(serviceClassLoader);
	}
	
	/**
	 * 
	 * @param name
	 * @param version
	 * @param info
	 */
	protected Provider(String name, double version, String info)
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

	// ===============================================================================
	// Provider Service
	// ===============================================================================

	/**
	 * Return the first implementation of a DataSourceServices over the protocol
	 * specified by the URL that a Provider provides. The Provider derivation
	 * should order the service implementations from newest to oldest version.
	 * 
	 * @see #getService(DataSourceServices, String, float)
	 * 
	 * @param type
	 * @param url
	 * @return
	 */
	public ProviderService getService(Class<? extends DataSourceSpi> spiType, URL url)
	{
		if (spiType == null || url == null)
			return null;

		String requestedProtocol = url.getProtocol();

		return getService(spiType, requestedProtocol, -1.0f);
	}

	/**
	 * Return a SortedSet of all services implemented by this provider of the
	 * specified type and communicating over the given url's protocol.
	 * 
	 * @param dataSourceServiceType
	 * @param requestedProtocol
	 * @return a list of applicable service implementations, an empty list if no
	 *         applicable services are found, null if either
	 *         dataSourceServiceType or requestedProtocol are null
	 */
	public SortedSet<ProviderService> getServices(
		Class<? extends DataSourceSpi> spiType, 
		String requestedProtocol)
	{
		SortedSet<ProviderService> applicableServices = new TreeSet<ProviderService>();

		if (spiType == null || requestedProtocol == null)
			return null;

		SortedSet<ProviderService> providerServices = getServices();
		if (providerServices == null) // if no services are defined by the
										// Provider then log a warning
			logger.warn("Provider '" + this.getName() + "." + this.getVersion()
				+ " defines no services, this is probably not intentional.");
		else
			for (ProviderService service : providerServices)
				if (spiType.equals(service.getSpiType()) && requestedProtocol.equals(service.getProtocol()))
					applicableServices.add(service);

		return applicableServices;
	}

	/**
	 * Return a SortedSet of all services implemented by this provider of the
	 * specified type with NO (null) protocol.
	 * 
	 * @param dataSourceServiceType
	 * @return
	 */
	public SortedSet<ProviderService> getLocalServices(Class<? extends DataSourceSpi> spiType)
	{
		SortedSet<ProviderService> applicableServices = new TreeSet<ProviderService>();

		if (spiType == null)
			return null;

		SortedSet<ProviderService> providerServices = getServices();
		
		 // if no services are defined by the Provider then log a warning
		if (providerServices == null || providerServices.size() == 0)
			logger.warn("Provider '" + this.getName() + "." + this.getVersion() + " defines no services, this is probably not intentional.");
		else
			for (ProviderService service : providerServices)
				if (spiType.equals(service.getSpiType()) && service.getProtocol() == null
					&& service.getProtocolVersion() == 0.0f)
					applicableServices.add(service);

		return applicableServices;
	}

	/**
	 * Return an instance of Provider.Service describing a service
	 * implementation applicable to the type, protocol and version. Type and
	 * protocol must not be null. If protocolVersion is <= 0.0 then the first
	 * service implementation implementing the type and protocol will be
	 * returned. The service Provider implementations should rely on the natural
	 * ordering of Provider.Service.
	 * 
	 * @param type
	 * @param url
	 * @param protocolVersion
	 * @return
	 */
	public ProviderService getService(Class<? extends DataSourceSpi> spiType, String requestedProtocol, float protocolVersion)
	{
		if (spiType == null || requestedProtocol == null)
			return null;

		for (ProviderService service : getServices())
		{
			if (spiType.equals(service.getSpiType()) && requestedProtocol.equals(service.getProtocol())
				&& protocolVersion <= 0.0f || protocolVersion == service.getProtocolVersion())
				return service;
		}

		return null;
	}

	/**
	 * A "local" service is one that does NOT require communication and
	 * therefore must NOT specify a protocol or a protocolVersion (other than
	 * 0.0F). Normal services, those that retrieve data from a remote box, will
	 * not be found using this method.
	 * 
	 * @param serviceType
	 * @return
	 */
	public ProviderService getLocalService(Class<? extends DataSourceSpi> spiType)
	{
		if (spiType == null)
			return null;

		for (ProviderService service : getServices())
		{
			if (spiType.equals(service.getSpiType()) && service.getProtocol() == null
				&& service.getProtocolVersion() == 0.0F)
				return service;
		}

		return null;
	}

	/**
	 * Get an unmodifiable Set of all services supported by this Provider. The
	 * service Provider implementations should rely on the natural ordering of
	 * Provider.Service.
	 * 
	 * This (the base Provider) does not provide any SPI implementations.
	 * 
	 * @return
	 */
	public SortedSet<ProviderService> getServices()
	{
		return (SortedSet<ProviderService>) null;
	}

	/**
	 * Gets a Set of all of the SPI classes that are currently installed,
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public final Set<Class<? extends DataSourceSpi>> getAllInstalledSpiTypes()
	{
		Set<Class<? extends DataSourceSpi>> spiTypes = new HashSet<Class<? extends DataSourceSpi>>(); 
		SortedSet<ProviderService> services = getAllInstalledServices(null);
		
		Class<? extends DataSourceSpi> lastSpiType = null;
		for(ProviderService service : services)
			if( lastSpiType == null || !lastSpiType.equals(service.getSpiType()) )
			{
				lastSpiType = (Class<? extends DataSourceSpi>)service.getSpiType();
				spiTypes.add(lastSpiType);
			}
		
		return spiTypes;
	}
	
	/**
	 * Get an unmodifiable Set of all services supported by all installed Provider.
	 * 
	 * @return
	 */
	public final SortedSet<ProviderService> getAllInstalledServices()
	{
		return getAllInstalledServices(null);
	}
	
	/**
	 * @param dss
	 * @return
	 */
	public final SortedSet<ProviderService> getAllInstalledServices(Class<? extends DataSourceSpi> spiType)
	{
		// will contain the list of all applicable service implementations
		SortedSet<ProviderService> services = new TreeSet<ProviderService>();

		// for each known provider, see if it implements a service of the
		// requested type
		ServiceLoader<Provider> loader = getProviderLoader();
		for (Provider provider : loader)
		{
			SortedSet<ProviderService> providerServices = provider.getServices();
			if (providerServices != null)
				for(ProviderService providerService : providerServices)
					// if the requested type is null, return all
					if(spiType == null || spiType.equals(providerService.getSpiType()))
						services.add(providerService);
		}
		
		return services.size() > 0 ? Collections.unmodifiableSortedSet(services) : null;
	}

	private ProviderConfiguration<Serializable> providerConfiguration = null;
	
	private synchronized ProviderConfiguration<Serializable> getProviderConfiguration()
	{
		if(providerConfiguration == null)
			providerConfiguration = new ProviderConfiguration<Serializable>(getName(), getVersion());
		
		return providerConfiguration;
	}
	
	protected Serializable loadConfiguration()
	{
		try
		{
			return getProviderConfiguration().loadConfiguration();
		}
		catch (IOException x)
		{
			logger.error(x);
			return null;
		}
	}

	protected void storeConfiguration(Serializable configuration)
	{
		try
		{
			getProviderConfiguration().store(configuration);
		}
		catch (IOException x)
		{
			logger.error(x);
		}
	}
	
	/**
	 * No configuration for this default implementation 
	 */
	public void storeConfiguration()
	{
		Serializable config = getInstanceConfiguration();
		if(config != null)
			storeConfiguration(config);		
		
		return;
	}
	
	protected Serializable getInstanceConfiguration()
	{
		return null;
	}
	
	protected String getConfigurationFileName(){return getProviderConfiguration().getConfigurationFileName();}

	/* (non-Javadoc)
	 * @see java.util.Hashtable#toString()
	 */
	@Override
	public synchronized String toString()
	{
		return this.getInfo();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.DataSourceProvider#getInstalledProviderDescriptions()
	 */
	@Override
	public String[] getInstalledProviderDescriptions()
	{
		// for each known provider, see if it implements a service of the
		// requested type
		ServiceLoader<Provider> loader = getProviderLoader();
		Set<Provider> providers = new HashSet<Provider>();
		
		for (Provider provider : loader)
			providers.add(provider);

		String[] result = new String[providers.size()];
		int index = 0;
		for (Provider provider : providers)
			result[index++] = provider.getInfo();
		
		return result;
	}

	/**
	 * @see gov.va.med.imaging.datasource.DataSourceProvider#getInstalledServiceProviderDescriptions()
	 */
	@Override
	public String[] getInstalledServiceImplementationDescriptions()
	{
		SortedSet<ProviderService> installedServices = getAllInstalledServices();
		String[] result = new String[installedServices.size()];
		int index=0;
		for( ProviderService server : installedServices )
			result[index++] = server.toString();
		
		return result;
	}
	
	/**
	 * @see gov.va.med.imaging.datasource.DataSourceProvider#getInstalledServiceDescriptions()
	 */
	@Override
	public String[] getInstalledServiceDescriptions()
	{
		Set<Class<? extends DataSourceSpi>> installedSpiTypes = getAllInstalledSpiTypes();
		String[] result;
		if(installedSpiTypes != null)
		{
			result = new String[installedSpiTypes.size()];
			int index=0;
			for( Class<? extends DataSourceSpi> spiClass : installedSpiTypes )
			{
				SPI spiAnnotation = spiClass.getAnnotation(SPI.class);
				result[index++] = spiClass.getName() + (spiAnnotation != null ? spiAnnotation.description() : "");
			}
		}
		else
			result = new String[0];
		
		return result;
	}
}
