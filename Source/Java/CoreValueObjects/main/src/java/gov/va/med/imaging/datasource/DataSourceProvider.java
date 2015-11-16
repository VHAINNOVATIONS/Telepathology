/**
 * 
 */
package gov.va.med.imaging.datasource;

import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.DataSourceExceptionHandler;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;

import java.util.List;

/**
 * @author vhaiswbeckec
 *
 */
public interface DataSourceProvider
{
	/**
	 * Return description of the installed SPI implementations.
	 * 
	 * @return
	 */
	public String[] getInstalledServiceImplementationDescriptions();

	/**
	 * Return a description of the installed SPI.
	 *  
	 * @return
	 */
	public String[] getInstalledServiceDescriptions();

	/**
	 * Return description of the installed Provider implementations.
	 * 
	 * @return
	 */
	public String[] getInstalledProviderDescriptions();
	
	/**
	 * Find a SiteResolutionDataSourceSpi implementation.
	 * 
	 * @return - an implementation of SiteResolutionDataSourceSpi
	 * @throws ConnectionException - an error occurred connection the service to the host data source
	 * @throws NoValidServiceConstructorError - no valid service constructor was found
	 */
	public abstract SiteResolutionDataSourceSpi createSiteResolutionDataSource()
			throws ConnectionException;

	/**
	 * 
	 * @return
	 * @throws ConnectionException
	 */
	public abstract List<RoutingOverrideSpi> createRoutingOverrideServices()
			throws ConnectionException;

	/**
	 * Find a ImageDataSourceSpi implementation that can communicate with the given URL.
	 * Create an instance with the URL.
	 * 
	 * @param url
	 * @param site
	 * @return - an implementation of ImageDataSourceSpi
	 * @throws ConnectionException - an error occurred connection the service to the host data source
	 * @throws NoValidServiceConstructorError - no valid service constructor was found
	 */
	public abstract ImageDataSourceSpi createImageDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException;

	/**
	 * Find a StudyGraphDataSource implementation that can communicate with the given URL.
	 * Create an instance with the URL and the ISite.
	 * 
	 * @param url
	 * @param Site
	 * @return - an implementation of StudyGraphDataSourceSpi
	 * @throws ConnectionException - an error occurred connection the service to the host data source
	 * @throws NoValidServiceConstructorError - no valid service constructor was found
	 */
	public abstract StudyGraphDataSourceSpi createStudyGraphDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException;

	/**
	 * 
	 * @param url
	 * @param site
	 * @return
	 * @throws ConnectionException
	 * @throws NoValidServiceConstructorError
	 */
	public abstract ImageAccessLoggingSpi createImageAccessLoggingDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException;

	/**
	 * 
	 * @param url
	 * @param site
	 * @return
	 * @throws ConnectionException
	 * @throws NoValidServiceConstructorError
	 */
	public abstract DocumentDataSourceSpi createDocumentDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException;
	
	/**
	 * @param metadataUrl
	 * @param resolvedSite
	 * @return
	 */
	public abstract DocumentSetDataSourceSpi createDocumentSetDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers)
	throws ConnectionException;
	   

	/**
	 * 
	 * @param url
	 * @param site
	 * @return
	 * @throws ConnectionException
	 * @throws NoValidServiceConstructorError
	 */
	public abstract UserPreferenceDataSourceSpi createUserPreferenceDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException;

	/**
	 * 
	 * @param url
	 * @param site
	 * @return
	 * @throws ConnectionException
	 */
	public abstract PatientDataSourceSpi createPatientDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException;
	
	/**
	 * 
	 * @param url
	 * @param site
	 * @return
	 * @throws ConnectionException
	 */
	public abstract DicomDataSourceSpi createDicomDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException;

	/**
	 * 
	 * @param url
	 * @param site
	 * @return
	 * @throws ConnectionException
	 */
	public abstract DicomStorageDataSourceSpi createDicomStorageDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException;

	/**
	 * 
	 * @param url
	 * @param site
	 * @return
	 * @throws ConnectionException
	 */
	public abstract DicomImporterDataSourceSpi createDicomImporterDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException;

	/**
	 * 
	 * @param url
	 * @param site
	 * @return
	 * @throws ConnectionException
	 */
	public abstract WorkListDataSourceSpi createWorkListDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException;

	/**
	 * 
	 * @param url
	 * @param site
	 * @return
	 * @throws ConnectionException
	 */
	public abstract DicomQueryRetrieveDataSourceSpi createDicomQueryRetrieveDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException;
	
	public abstract DicomQueryRetrieveDataSourceSpi createLocalDicomQueryRetrieveDataSource(ResolvedArtifactSource resolvedArtifactSource)
	throws ConnectionException;
	
	public abstract DicomStorageDataSourceSpi createLocalDicomStorageDataSource(ResolvedArtifactSource resolvedArtifactSource)
	throws ConnectionException;
	
	public abstract DicomApplicationEntityDataSourceSpi createLocalDicomApplicationEntityDataSource(ResolvedArtifactSource resolvedArtifactSource)
	throws ConnectionException;
	
	public abstract DicomDataSourceSpi createLocalDicomDataSource(ResolvedArtifactSource resolvedArtifactSource)
	throws ConnectionException;
	
	public abstract DicomImporterDataSourceSpi createLocalDicomImporterDataSource(ResolvedArtifactSource resolvedArtifactSource)
	throws ConnectionException;

	/**
	 * 
	 * @param url
	 * @param site
	 * @return
	 * @throws ConnectionException
	 */
	public abstract DicomApplicationEntityDataSourceSpi createDicomApplicationEntityDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException;

	
	/**
	 * 
	 * @param url
	 * @param site
	 * @return
	 * @throws ConnectionException
	 */
	public abstract StorageDataSourceSpi createStorageDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException;


	/**
	 * 
	 * @param url
	 * @param site
	 * @return
	 * @throws ConnectionException
	 */
	public abstract DurableQueueDataSourceSpi createDurableQueueDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException;


	/**
	 * 
	 * @param url
	 * @param site
	 * @return
	 * @throws ConnectionException
	 */
	public abstract ServiceRegistrationDataSourceSpi createServiceRegistrationDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException;


	/**
	 * 
	 * @param url
	 * @param site
	 * @return
	 * @throws ConnectionException
	 */
	public abstract VeinsDataSourceSpi createVeinsDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException;

	/**
	 * 
	 * @param url
	 * @param site
	 * @return
	 * @throws ConnectionException
	 */
	public abstract VistaRadDataSourceSpi createVistaRadDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException;

   /**
    * Find a VistARad Image data source implementation
    * @param url
    * @param site
    * @return
    * @throws ConnectionException
    */
   public abstract VistaRadImageDataSourceSpi createVistaRadImageDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
   throws ConnectionException;
   
   /**
    * Create a data source used to translate information from an external package into VistA Imaging data
    * @param url
    * @param site
    * @return
    * @throws ConnectionException
    */
   public abstract ExternalPackageDataSourceSpi createExternalPackageDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers)
   throws ConnectionException;

   /**
    * Find a TransactionLoggerDataSourceSpi implementation.
    *
    * @return - an implementation of TransactionLoggerDataSourceSpi.
    * @throws ConnectionException - an error occurred connecting the service to the host data source.
    * @throws NoValidServiceConstructorError - no valid service constructor was found.
    */
   public abstract TransactionLoggerDataSourceSpi createTransactionLoggerDataSource ()
   throws ConnectionException;
   
   /**
    * 
    * @param url
    * @param site
    * @param dataSourceExceptionHandlers
    * @return
    * @throws ConnectionException
    */
   public abstract PassthroughDataSourceSpi createPassthroughDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers)
   throws ConnectionException;
   
   /**
    * 
    * @param url
    * @param site
    * @param dataSourceExceptionHandlers
    * @return
    * @throws ConnectionException
    */
   public abstract UserAuthenticationSpi createUserAuthenticationDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers)
   throws ConnectionException;   
   
   /**
    * Creates any type of versionable data source and handles dataSourceExceptionHandlers
    * 
    * @param <S>
    * @param spiType
    * @param url
    * @param site
    * @param dataSourceExceptionHandlers
    * @return
    * @throws ConnectionException
    */
   public abstract <S extends VersionableDataSourceSpi> S createVersionableDataSource(
	   Class<S> spiType, 
	   ResolvedArtifactSource resolvedArtifactSource, 
	   String protocol,
	   DataSourceExceptionHandler ... dataSourceExceptionHandlers)
   throws ConnectionException;
   
   /**
    * Create any type of local data source.
    * @param <S>
    * @param spiType
    * @return
    * @throws ConnectionException
    */
   public abstract <S extends LocalDataSourceSpi> S createLocalDataSource(
	   Class<S> spiType)
   throws ConnectionException;
   
   public abstract <S extends LocalDataSourceSpi> S createLocalDataSource(Class<S> spiType, 
			ResolvedArtifactSource resolvedArtifactSource) 
	throws ConnectionException;
   
   public abstract ExternalSystemOperationsDataSourceSpi createExternalSystemOperationsDataSource(
		   ResolvedArtifactSource resolvedArtifactSource, 
		   String protocol,
		   DataSourceExceptionHandler... dataSourceExceptionHandlers)
   throws ConnectionException;
   
   public abstract PatientArtifactDataSourceSpi createPatientArtifactDataSource(
		   ResolvedArtifactSource resolvedArtifactSource,
		   String protocol,
		   DataSourceExceptionHandler... dataSourceExceptionHandlers)
   throws ConnectionException;
   
   public abstract EventLoggingDataSourceSpi createEventLoggingDataSource(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		DataSourceExceptionHandler... dataSourceExceptionHandlers) 
		throws ConnectionException;
	
	public abstract UserDataSourceSpi createUserDataSource(
			ResolvedArtifactSource resolvedArtifactSource, 
			String protocol,
			DataSourceExceptionHandler... dataSourceExceptionHandlers) 
	throws ConnectionException;

}
