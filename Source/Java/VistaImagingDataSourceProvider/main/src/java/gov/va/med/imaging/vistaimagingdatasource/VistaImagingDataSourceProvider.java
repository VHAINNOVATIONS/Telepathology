/**
 * 
 */
package gov.va.med.imaging.vistaimagingdatasource;

import gov.va.med.imaging.datasource.DicomApplicationEntityDataSourceSpi;
import gov.va.med.imaging.datasource.DicomDataSourceSpi;
import gov.va.med.imaging.datasource.DicomImporterDataSourceSpi;
import gov.va.med.imaging.datasource.DicomQueryRetrieveDataSourceSpi;
import gov.va.med.imaging.datasource.DicomStorageDataSourceSpi;
import gov.va.med.imaging.datasource.DocumentDataSourceSpi;
import gov.va.med.imaging.datasource.DocumentSetDataSourceSpi;
import gov.va.med.imaging.datasource.DurableQueueDataSourceSpi;
import gov.va.med.imaging.datasource.EventLoggingDataSourceSpi;
import gov.va.med.imaging.datasource.ExternalPackageDataSourceSpi;
import gov.va.med.imaging.datasource.ImageAccessLoggingSpi;
import gov.va.med.imaging.datasource.ImageDataSourceSpi;
import gov.va.med.imaging.datasource.PassthroughDataSourceSpi;
import gov.va.med.imaging.datasource.PatientArtifactDataSourceSpi;
import gov.va.med.imaging.datasource.PatientDataSourceSpi;
import gov.va.med.imaging.datasource.Provider;
import gov.va.med.imaging.datasource.ProviderService;
import gov.va.med.imaging.datasource.RoutingOverrideSpi;
import gov.va.med.imaging.datasource.ServiceRegistrationDataSourceSpi;
import gov.va.med.imaging.datasource.SiteDataSourceSpi;
import gov.va.med.imaging.datasource.StorageDataSourceSpi;
import gov.va.med.imaging.datasource.StudyGraphDataSourceSpi;
import gov.va.med.imaging.datasource.UserAuthenticationSpi;
import gov.va.med.imaging.datasource.UserAuthorizationDataSourceSpi;
import gov.va.med.imaging.datasource.UserDataSourceSpi;
import gov.va.med.imaging.datasource.VeinsDataSourceSpi;
import gov.va.med.imaging.datasource.VistaRadDataSourceSpi;
import gov.va.med.imaging.datasource.VistaRadImageDataSourceSpi;
import gov.va.med.imaging.datasource.WorkListDataSourceSpi;
import gov.va.med.imaging.vistaimagingdatasource.configuration.VistaImagingConfiguration;
import gov.va.med.imaging.vistaimagingdatasource.dicom.importer.VistaImagingDicomImporterDataSourceService;
import gov.va.med.imaging.vistaimagingdatasource.dicom.storage.VistaImagingDicomApplicationEntityDataSourceService;
import gov.va.med.imaging.vistaimagingdatasource.dicom.storage.VistaImagingDicomQueryRetrieveDataSourceService;
import gov.va.med.imaging.vistaimagingdatasource.dicom.storage.VistaImagingDicomStorageDataSourceService;
import gov.va.med.imaging.vistaimagingdatasource.storage.VistaImagingStorageDataSourceService;
import gov.va.med.imaging.vistaimagingdatasource.worklist.VistaImagingWorkListDataSourceService;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

/**
 * @author VHAISWBECKEC
 * 
 */
public class VistaImagingDataSourceProvider 
extends Provider
{
	private static final String PROVIDER_NAME = "VistaImagingDataSource";
	private static final double PROVIDER_VERSION = 1.0d;
	private static final String PROVIDER_INFO = 
		"Implements: \n" + 
		"StudyGraphDataSource, ImageDataSource, and ImageAccessLoggingDataSource SPI \n" + 
		"backed by a VistA data store.";

	private static final long serialVersionUID = 1L;
	
	private static VistaImagingConfiguration vistaConfiguration = null;
	private final static Logger logger = Logger.getLogger(VistaImagingDataSourceProvider.class);
	
	private final SortedSet<ProviderService> services;

	/**
	 * The public "nullary" constructor that is used by the ServiceLoader class
	 * to create instances.
	 */
	public VistaImagingDataSourceProvider()
	{
		this(PROVIDER_NAME, PROVIDER_VERSION, PROVIDER_INFO);
	}
	
	/**
	 * A special constructor that is only used for creating a configuration
	 * file.
	 * 
	 * @param vistaConfiguration
	 */
	public VistaImagingDataSourceProvider(VistaImagingConfiguration vistaConfiguration)
	{
		this();
		VistaImagingDataSourceProvider.vistaConfiguration = vistaConfiguration;
	}

	/**
	 * @param name
	 * @param version
	 * @param info
	 */
	private VistaImagingDataSourceProvider(String name, double version, String info)
	{
		super(name, version, info);

		services = new TreeSet<ProviderService>();
		services.add(
			new ProviderService(
				this, 
				StudyGraphDataSourceSpi.class, 
				VistaImagingStudyGraphDataSourceService.SUPPORTED_PROTOCOL, 
				1.0F, 
				VistaImagingStudyGraphDataSourceService.class)
		);
		services.add(
			new ProviderService(
				this, 
				ImageDataSourceSpi.class,
				VistaImageDataSourceService.SUPPORTED_PROTOCOL,
				1.0F,
				VistaImageDataSourceService.class)
		);
		services.add(
			new ProviderService(
				this, 
				ImageDataSourceSpi.class,
				VistaImageDataSourceServiceV0.SUPPORTED_PROTOCOL,
				0.9F,
				VistaImageDataSourceServiceV0.class)
		);
		/*
		 // JMW 10/5/2010 disable this version since it will only be functional with P119 (to support deleted images)
		services.add(
				new Provider.Service(
				this, 
					DataSourceServices.ImageDataSource,
					VistaImageDataSourceServiceV2.SUPPORTED_PROTOCOL,
					2.0F,
					VistaImageDataSourceServiceV2.class)
			);*/
		services.add(
			new ProviderService(
				this, 
				ImageAccessLoggingSpi.class,
				VistaImagingImageAccessLoggingDataSourceService.SUPPORTED_PROTOCOL,
				1.0F,
				VistaImagingImageAccessLoggingDataSourceService.class)
		);
		services.add(
			new ProviderService(
				this, 
				StudyGraphDataSourceSpi.class,
				VistaImagingStudyGraphDataSourceServiceV0.SUPPORTED_PROTOCOL,
				0.9F,
				VistaImagingStudyGraphDataSourceServiceV0.class)
		);
		services.add(
				new ProviderService(
					this, 
					StudyGraphDataSourceSpi.class,
					VistaImagingStudyGraphDataSourceService.SUPPORTED_PROTOCOL,
					2.0F,
					VistaImagingStudyGraphDataSourceServiceV1.class)
			);
		services.add(
				new ProviderService(
					this, 
					StudyGraphDataSourceSpi.class,
					VistaImagingStudyGraphDataSourceServiceV2.SUPPORTED_PROTOCOL,
					3.0F,
					VistaImagingStudyGraphDataSourceServiceV2.class)
			);
		services.add(
			new ProviderService(
				this, 
				PatientDataSourceSpi.class,
				VistaImagingPatientDataSourceService.SUPPORTED_PROTOCOL,
				1.0F,
				VistaImagingPatientDataSourceService.class)
		);
		services.add(
			new ProviderService(
				this, 
				DicomDataSourceSpi.class,
				VistaImagingDicomDataSourceService.SUPPORTED_PROTOCOL,
				1.0F,
				VistaImagingDicomDataSourceService.class)
			);
		services.add(
			new ProviderService(
				this, 
				DicomDataSourceSpi.class,
				(byte)1, 
				VistaImagingDicomDataSourceService.class)
			);

		services.add(
				new ProviderService(
					this, 
					DicomApplicationEntityDataSourceSpi.class,
					VistaImagingDicomApplicationEntityDataSourceService.SUPPORTED_PROTOCOL,
					1.0F,
					VistaImagingDicomApplicationEntityDataSourceService.class)
				);
		services.add(
			new ProviderService(
				this, 
				DicomApplicationEntityDataSourceSpi.class,
				(byte)1, 
				VistaImagingDicomApplicationEntityDataSourceService.class)
			);
		services.add(
				new ProviderService(
					this, 
					DicomQueryRetrieveDataSourceSpi.class,
					VistaImagingDicomQueryRetrieveDataSourceService.SUPPORTED_PROTOCOL,
					1.0F,
					VistaImagingDicomQueryRetrieveDataSourceService.class)
				);
		
		services.add(
			new ProviderService(
				this, 
				DicomQueryRetrieveDataSourceSpi.class,
				(byte)1, 
				VistaImagingDicomQueryRetrieveDataSourceService.class)
			);

		services.add(
				new ProviderService(
					this, 
					DicomStorageDataSourceSpi.class,
					VistaImagingDicomStorageDataSourceService.SUPPORTED_PROTOCOL,
					1.0F,
					VistaImagingDicomStorageDataSourceService.class)
				);
		
		services.add(
			new ProviderService(
				this, 
				DicomStorageDataSourceSpi.class,
				(byte)1, 
				VistaImagingDicomStorageDataSourceService.class)
			);
				
		services.add(
				new ProviderService(
					this, 
					DicomImporterDataSourceSpi.class,
					VistaImagingDicomImporterDataSourceService.SUPPORTED_PROTOCOL,
					1.0F,
					VistaImagingDicomImporterDataSourceService.class)
				);
		services.add(
			new ProviderService(
				this, 
				DicomImporterDataSourceSpi.class,
				(byte)1, 
				VistaImagingDicomImporterDataSourceService.class)
			);

		services.add(
				new ProviderService(
					this, 
					WorkListDataSourceSpi.class,
					VistaImagingWorkListDataSourceService.SUPPORTED_PROTOCOL,
					1.0F,
					VistaImagingWorkListDataSourceService.class)
				);

		services.add(
				new ProviderService(
					this, 
					StorageDataSourceSpi.class,
					VistaImagingStorageDataSourceService.SUPPORTED_PROTOCOL,
					1.0F,
					VistaImagingStorageDataSourceService.class)
				);
				
		services.add(
				new ProviderService(
					this, 
					DurableQueueDataSourceSpi.class,
					VistaImagingDurableQueueDataSourceService.SUPPORTED_PROTOCOL,
					1.0F,
					VistaImagingDurableQueueDataSourceService.class)
				);
				
		services.add(
				new ProviderService(
					this, 
					ServiceRegistrationDataSourceSpi.class,
					VistaImagingServiceRegistrationDataSourceService.SUPPORTED_PROTOCOL,
					1.0F,
					VistaImagingServiceRegistrationDataSourceService.class)
				);
				
		services.add(
				new ProviderService(
					this, 
					VeinsDataSourceSpi.class,
					VistaImagingVeinsDataSourceService.SUPPORTED_PROTOCOL,
					1.0F,
					VistaImagingVeinsDataSourceService.class)
				);
				
		/*
		 // no longer supported by Patch 104
		services.add(
			new ProviderService(
				this, 
				VistaRadDataSourceSpi.class,
				AbstractBaseVistaImagingVistaRadService.SUPPORTED_PROTOCOL,
				0.9F,
				VistaImagingVistaRadDataSourceServiceV0.class)
			);
			*/
		
		services.add(
			new ProviderService(
				this, 
				VistaRadDataSourceSpi.class,
				VistaImagingVistaRadDataSourceService.SUPPORTED_PROTOCOL,
				1.0F,
				VistaImagingVistaRadDataSourceService.class)
			);
		/*
		 // no longer supported by Patch 104
		services.add(
			new ProviderService(
				this, 
				VistaRadImageDataSourceSpi.class,
				AbstractBaseVistaRadImageDataSourceService.SUPPORTED_PROTOCOL,
				0.9F,
				VistaImagingVistaRadImageDataSourceServiceV0.class)
			);
		*/
		services.add(
			new ProviderService(
				this, 
				VistaRadImageDataSourceSpi.class,
				AbstractBaseVistaRadImageDataSourceService.SUPPORTED_PROTOCOL,
				1.0F,
				VistaImagingVistaRadImageDataSourceServiceV1.class)
		);
		services.add(
			new ProviderService(
				this, 
				VistaRadImageDataSourceSpi.class,
				VistaImagingVistaRadImageDataSourceServiceV2.SUPPORTED_PROTOCOL,
				2.0F,
				VistaImagingVistaRadImageDataSourceServiceV2.class)
		);
		services.add(
			new ProviderService(
				this, 
				VistaRadImageDataSourceSpi.class,
				VistaImagingVistaRadImageDataSourceServiceV3.SUPPORTED_PROTOCOL,
				3.0F,
				VistaImagingVistaRadImageDataSourceServiceV3.class)
		);
		
		services.add(
			new ProviderService(
				this, 
				ExternalPackageDataSourceSpi.class,
				AbstractBaseVistaImagingExternalPackageDataSourceService.SUPPORTED_PROTOCOL,
				0.9F,
				VistaImagingExternalPackageDataSourceServiceV0.class)
			);
		
		services.add(
			new ProviderService(
				this, 
				ExternalPackageDataSourceSpi.class,
				AbstractBaseVistaImagingExternalPackageDataSourceService.SUPPORTED_PROTOCOL,
				1.0F,
				VistaImagingExternalPackageDataSourceService.class)
			);
		services.add(
				new ProviderService(
					this, 
					ExternalPackageDataSourceSpi.class,
					AbstractBaseVistaImagingExternalPackageDataSourceService.SUPPORTED_PROTOCOL,
					2.0F,
					VistaImagingExternalPackageDataSourceServiceV2.class)
				);			

		// document set and document binary data source services
		services.add(
			new ProviderService(
				this, 
				DocumentSetDataSourceSpi.class,
				VistaImagingStudyGraphDataSourceService.SUPPORTED_PROTOCOL,
				1.0F,
				VistaImagingDocumentSetDataSourceService.class)
			);
		services.add(
			new ProviderService(
				this, 
				DocumentDataSourceSpi.class,
				VistaImageDataSourceService.SUPPORTED_PROTOCOL,
				1.0F,
				VistaImagingDocumentDataSourceService.class)
			);
		services.add(
			new ProviderService(
				this, 
				PassthroughDataSourceSpi.class,
				VistaImagingPassthroughDataSourceService.SUPPORTED_PROTOCOL,
				1.0F,
				VistaImagingPassthroughDataSourceService.class)
			);
		services.add(
			new ProviderService(
				this, 
				UserAuthorizationDataSourceSpi.class,
				VistaImagingUserAuthorizationDataSourceService.SUPPORTED_PROTOCOL,
				1.0F,
				VistaImagingUserAuthorizationDataSourceService.class)
			);
		services.add(
				new ProviderService(
					this,
					PatientArtifactDataSourceSpi.class,
					VistaImagingStudyGraphDataSourceService.SUPPORTED_PROTOCOL,
					3.0F,
					VistaImagingPatientArtifactDataSourceServiceV3.class)
				);
		services.add(
			new ProviderService(
				this,
				PatientArtifactDataSourceSpi.class,
				VistaImagingStudyGraphDataSourceService.SUPPORTED_PROTOCOL,
				2.0F,
				VistaImagingPatientArtifactDataSourceServiceV2.class)
			);
		services.add(
			new ProviderService(
				this,
				PatientArtifactDataSourceSpi.class,
				VistaImagingStudyGraphDataSourceService.SUPPORTED_PROTOCOL,
				1.0F,
				VistaImagingPatientArtifactDataSourceServiceV1.class)
			);
		services.add(
				new ProviderService(
					this, 
					EventLoggingDataSourceSpi.class,
					VistaImagingEventLoggingDataSourceService.SUPPORTED_PROTOCOL,
					1.0F,
					VistaImagingEventLoggingDataSourceService.class)
				);
		services.add(
				new ProviderService(
					this,
					UserDataSourceSpi.class,
					VistaImagingUserDataSourceService.SUPPORTED_PROTOCOL,
					1.0F,
					VistaImagingUserDataSourceService.class)
				);		
		services.add(
				new ProviderService(
					this,
					VistaRadDataSourceSpi.class,
					VistaImagingVistaRadDataSourceServiceV2.SUPPORTED_PROTOCOL,
					2.0F,
					VistaImagingVistaRadDataSourceServiceV2.class)
				);
		services.add(
				new ProviderService(
					this,
					VistaRadDataSourceSpi.class,
					VistaImagingVistaRadDataSourceServiceV3.SUPPORTED_PROTOCOL,
					3.0F,
					VistaImagingVistaRadDataSourceServiceV3.class)
				);
		services.add(
				new ProviderService(
					this,
					SiteDataSourceSpi.class,
					VistaImagingSiteDataSourceService.SUPPORTED_PROTOCOL,
					1.0F,
					VistaImagingSiteDataSourceService.class)
				);
		services.add(
				new ProviderService(
					this,
					UserDataSourceSpi.class,
					VistaImagingUserDataSourceServiceV2.SUPPORTED_PROTOCOL,
					2.0F,
					VistaImagingUserDataSourceServiceV2.class)
				);
		services.add(
				new ProviderService(
					this, 
					PatientDataSourceSpi.class,
					VistaImagingPatientDataSourceServiceV2.SUPPORTED_PROTOCOL,
					2.0F,
					VistaImagingPatientDataSourceServiceV2.class)
			);
		services.add(
			new ProviderService(
				this, 
				UserAuthenticationSpi.class,
				VistaImagingUserAuthenticationDataSourceService.SUPPORTED_PROTOCOL,
				1.0F,
				VistaImagingUserAuthenticationDataSourceService.class)
			);
		
			
		// VistaDelegateRedirector is a "local" service, it has no protocol
		// or protocol version and is instantiated with the null-arg
		// constructor
		services.add(
			new ProviderService(
				this, 
				RoutingOverrideSpi.class,
				(byte)1, 
				VistaImagingDelegateRedirector.class)
			);
		
		// load the ExchangeConfiguration if it exists
		synchronized(VistaImagingDataSourceProvider.class)
	    {
			try
			{
				if(vistaConfiguration == null)
					vistaConfiguration = (VistaImagingConfiguration)loadConfiguration();
			}
			catch(ClassCastException ccX)
			{
				logger.error("Unable to load configuration because the configuration file is invalid.", ccX);
			}
	    }
	}
	
	/**
	 * 
	 */
	@Override
	public void storeConfiguration()
    {
	    storeConfiguration(getVistaConfiguration());
    }
	
	/**
	 * A package level method for SPI implementation to get the
	 * Configuration.
	 * 
	 * @return
	 */
	static VistaImagingConfiguration getVistaConfiguration()
	{
		if(vistaConfiguration == null)
			logger.error("VistaConfiguration is null, possibly called before VistaDataSourceProvider was instantiated.");
		
		return vistaConfiguration;
	}

	@Override
	public SortedSet<ProviderService> getServices()
	{
		return Collections.unmodifiableSortedSet(services);
	}
	
	public static void main(String [] args)
	{
		System.out.println("Creating vista datasource configuration file");		
		VistaImagingConfiguration vistaConfiguration = VistaImagingConfiguration.createDefaultConfiguration();		
		VistaImagingDataSourceProvider provider = new VistaImagingDataSourceProvider(vistaConfiguration);
		provider.storeConfiguration();
		System.out.println("Configuration file saved to '" + provider.getConfigurationFileName() + "'.");
	}
}
