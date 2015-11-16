/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 3, 2008
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
package gov.va.med.imaging.federationdatasource;

import gov.va.med.imaging.datasource.*;
import gov.va.med.imaging.federation.proxy.FederationProxyUtilities;
import gov.va.med.imaging.federationdatasource.configuration.FederationConfiguration;
import gov.va.med.imaging.federationdatasource.v5.FederationDocumentDataSourceServiceV5;
import gov.va.med.imaging.federationdatasource.v5.FederationDocumentSetDataSourceServiceV5;
import gov.va.med.imaging.federationdatasource.v5.FederationExternalPackageDataSourceServiceV5;
import gov.va.med.imaging.federationdatasource.v5.FederationExternalSystemOperationDataSourceServiceV5;
import gov.va.med.imaging.federationdatasource.v5.FederationImageAccessLoggingDataSourceServiceV5;
import gov.va.med.imaging.federationdatasource.v5.FederationImageAnnotationDataSourceServiceV5;
import gov.va.med.imaging.federationdatasource.v5.FederationImageDataSourceServiceV5;
import gov.va.med.imaging.federationdatasource.v5.FederationPassthroughDataSourceServiceV5;
import gov.va.med.imaging.federationdatasource.v5.FederationPatientArtifactDataSourceServiceV5;
import gov.va.med.imaging.federationdatasource.v5.FederationPatientDataSourceServiceV5;
import gov.va.med.imaging.federationdatasource.v5.FederationStudyGraphDataSourceServiceV5;
import gov.va.med.imaging.federationdatasource.v5.FederationUserDataSourceServiceV5;
import gov.va.med.imaging.federationdatasource.v5.FederationVistaRadDataSourceServiceV5;
import gov.va.med.imaging.federationdatasource.v5.FederationVistaRadImageDataSourceServiceV5;
import gov.va.med.imaging.federationdatasource.v6.FederationPatientDataSourceServiceV6;
import gov.va.med.imaging.federationdatasource.v6.FederationUserDataSourceServiceV6;
import gov.va.med.imaging.federationdatasource.v7.FederationImageAccessLoggingDataSourceServiceV7;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.log4j.Logger;

/**
 * @author VHAISWWERFEJ
 * 
 */
public class FederationDataSourceProvider extends Provider
{
	private static final String PROVIDER_NAME = "FederationDataSource";
	private static final double PROVIDER_VERSION = 1.0d;
	private static final String PROVIDER_INFO = "Implements: \nStudyGraphDataSource, ImageDataSource SPI \n backed by a Federation data store.";

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(FederationDataSourceProvider.class);
	private static FederationConfiguration federationConfiguration = null;
	private final SortedSet<ProviderService> services;

	/**
	 * The public "nullary" constructor that is used by the ServiceLoader class
	 * to create instances.
	 */
	public FederationDataSourceProvider()
	{
		this(PROVIDER_NAME, PROVIDER_VERSION, PROVIDER_INFO);
	}
	
	/**
	 * A special constructor that is only used for creating a configuration
	 * file.
	 * 
	 * @param exchangeConfiguration
	 */
	private FederationDataSourceProvider(FederationConfiguration federationConfiguration) 
	{
		this();
		FederationDataSourceProvider.federationConfiguration = federationConfiguration;
	}

	/**
	 * @param name
	 * @param version
	 * @param info
	 */
	private FederationDataSourceProvider(String name, double version, String info)
	{
		super(name, version, info);

		services = new TreeSet<ProviderService>();
		// version 1 and version 2 are no longer available because they do not handle the Base32 conversions properly (version 3 handles this correctly)
		/*
		services.add(
			new Provider.Service(
				DataSourceServices.StudyGraphDataSource,
				FederationStudyGraphDataSourceService.SUPPORTED_PROTOCOL,
				1.0F,
				FederationStudyGraphDataSourceService.class)
		);
		services.add(
			new Provider.Service(
				DataSourceServices.StudyGraphDataSource,
				FederationStudyGraphDataSourceServiceV2.SUPPORTED_PROTOCOL,
				2.0F,
				FederationStudyGraphDataSourceServiceV2.class)
		);*/
		services.add(
			new ProviderService(
				this, 
				StudyGraphDataSourceSpi.class,
				FederationStudyGraphDataSourceServiceV3.SUPPORTED_PROTOCOL,
				3.0F,
				FederationStudyGraphDataSourceServiceV3.class)
		);
		/*
		services.add(
			new Provider.Service(
				DataSourceServices.ImageDataSource,
				FederationImageDataSourceService.SUPPORTED_PROTOCOL,
				1.0F,
				FederationImageDataSourceService.class)
		);
		services.add(
			new Provider.Service(
				DataSourceServices.ImageDataSource,
				FederationImageDataSourceServiceV2.SUPPORTED_PROTOCOL,
				2.0F,
				FederationImageDataSourceServiceV2.class)
		);*/
		services.add(
			new ProviderService(
				this, 
				ImageDataSourceSpi.class,
				AbstractFederationImageDataSourceService.SUPPORTED_PROTOCOL,
				3.0F,
				FederationImageDataSourceServiceV3.class)
		);
		/*
		services.add(
			new Provider.Service(
				DataSourceServices.ImageAccessLoggingDataSource,
				FederationImageAccessLoggingDataSourceService.SUPPORTED_PROTOCOL,
				1.0F,
				FederationImageAccessLoggingDataSourceService.class)
		);
		services.add(
			new Provider.Service(
				DataSourceServices.ImageAccessLoggingDataSource,
				FederationImageAccessLoggingDataSourceServiceV2.SUPPORTED_PROTOCOL,
				2.0F,
				FederationImageAccessLoggingDataSourceServiceV2.class)
		);*/
		services.add(
			new ProviderService(
				this, 
				ImageAccessLoggingSpi.class,
				FederationImageAccessLoggingDataSourceServiceV3.SUPPORTED_PROTOCOL,
				3.0F,
				FederationImageAccessLoggingDataSourceServiceV3.class)
		);
		/*
		services.add(
			new Provider.Service(
				DataSourceServices.PatientDataSource,
				FederationPatientDataSourceService.SUPPORTED_PROTOCOL,
				1.0F,
				FederationPatientDataSourceService.class)
		);
		services.add(
			new Provider.Service(
				DataSourceServices.PatientDataSource,
				FederationPatientDataSourceServiceV2.SUPPORTED_PROTOCOL,
				2.0F,
				FederationPatientDataSourceServiceV2.class)
		);*/
		services.add(
			new ProviderService(
				this, 
				PatientDataSourceSpi.class,
				FederationPatientDataSourceServiceV3.SUPPORTED_PROTOCOL,
				3.0F,
				FederationPatientDataSourceServiceV3.class)
		);
		/*
		 // version 2 and 3 of the ExternalPackageDataSource for Federation is no longer used because
		 // it contains a bug that will only retrieve some of the possible images associated with a 
		 // CPRS identifier, to prevent this bug from occuring, these data sources are disabled and
		 // version 4 must be used
		services.add(
			new Provider.Service(
				DataSourceServices.ExternalPackageDataSource,
				FederationExternalPackageDataSourceServiceV2.SUPPORTED_PROTOCOL,
				2.0F,
				FederationExternalPackageDataSourceServiceV2.class)
		);
		services.add(
			new Provider.Service(
				DataSourceServices.ExternalPackageDataSource,
				FederationExternalPackageDataSourceServiceV3.SUPPORTED_PROTOCOL,
				3.0F,
				FederationExternalPackageDataSourceServiceV3.class)
		);*/
		// version 1 of the Document and DocumentSet data source for Federation are disabled because
		// they do not support base32 conversions properly (see comment above about versions 1 and 2)
		/*
		services.add(
			new Provider.Service(
				DataSourceServices.DocumentDataSource,
				FederationDocumentDataSourceService.SUPPORTED_PROTOCOL,
				1.0F,
				FederationDocumentDataSourceService.class)
		);
		services.add(
			new Provider.Service(
				DataSourceServices.DocumentSetDataSource,
				FederationDocumentSetDataSourceService.SUPPORTED_PROTOCOL,
				1.0F,
				FederationDocumentSetDataSourceService.class)
		);*/
		services.add(
			new ProviderService(
				this, 
				DocumentSetDataSourceSpi.class,
				FederationStudyGraphDataSourceServiceV3.SUPPORTED_PROTOCOL,
				3.0F,
				FederationDocumentSetDataSourceServiceV3.class)
		);
		services.add(
				new ProviderService(
					this, 
					DocumentDataSourceSpi.class,
					AbstractFederationImageDataSourceService.SUPPORTED_PROTOCOL,
					3.0F,
					FederationDocumentDataSourceServiceV3.class)
			);		
		services.add(
			new ProviderService(
				this, 
				VistaRadDataSourceSpi.class,
				AbstractFederationVistaRadDataSourceService.SUPPORTED_PROTOCOL,
				3.0F,
				FederationVistaRadDataSourceServiceV3.class)
		);
		services.add(
			new ProviderService(
				this, 
				VistaRadImageDataSourceSpi.class,
				AbstractFederationImageDataSourceService.SUPPORTED_PROTOCOL,
				3.0F,
				FederationVistaRadImageDataSourceServiceV3.class)
		);
		services.add(
			new ProviderService(
				this, 
				PassthroughDataSourceSpi.class,
				AbstractFederationPassthroughDataSourceService.SUPPORTED_PROTOCOL,
				3.0F,
				FederationPassthroughDataSourceServiceV3.class)
		);
		services.add(
			new ProviderService(
				this,
				PatientArtifactDataSourceSpi.class,
				FederationStudyGraphDataSourceServiceV3.SUPPORTED_PROTOCOL,
				3.0F,
				FederationPatientArtifactDataSourceServiceV3.class)
		);
		
		// JMW disable REST web services for now, enable for Patch 104
		///*
		services.add(
				new ProviderService(
					this, 
					DocumentSetDataSourceSpi.class,
					FederationDocumentSetDataSourceServiceV4.SUPPORTED_PROTOCOL,
					4.0F,
					FederationDocumentSetDataSourceServiceV4.class)
			);
			
			services.add(
				new ProviderService(
					this, 
					DocumentDataSourceSpi.class,
					AbstractFederationImageDataSourceService.SUPPORTED_PROTOCOL,
					4.0F,
					FederationDocumentDataSourceServiceV4.class)
			);
		services.add(
				new ProviderService(
					this, 
					StudyGraphDataSourceSpi.class,
					FederationStudyGraphDataSourceServiceV4.SUPPORTED_PROTOCOL,
					4.0F,
					FederationStudyGraphDataSourceServiceV4.class)
			);		
		
		services.add(
				new ProviderService(
					this, 
					PatientDataSourceSpi.class,
					FederationPatientDataSourceServiceV4.SUPPORTED_PROTOCOL,
					4.0F,
					FederationPatientDataSourceServiceV4.class)
			);
		
		services.add(
				new ProviderService(
					this, 
					StudyGraphDataSourceSpi.class,
					FederationStudyGraphDataSourceServiceV4.SUPPORTED_PROTOCOL,
					4.0F,
					FederationStudyGraphDataSourceServiceV4.class)
			);
		
		services.add(
				new ProviderService(
					this, 
					PassthroughDataSourceSpi.class,
					AbstractFederationPassthroughDataSourceService.SUPPORTED_PROTOCOL,
					4.0F,
					FederationPassthroughDataSourceServiceV4.class)
			);
		
		services.add(
				new ProviderService(
					this, 
					VistaRadDataSourceSpi.class,
					AbstractFederationVistaRadDataSourceService.SUPPORTED_PROTOCOL,
					4.0F,
					FederationVistaRadDataSourceServiceV4.class)
			);
		
		services.add(
				new ProviderService(
					this, 
					ImageAccessLoggingSpi.class,
					FederationImageAccessLoggingDataSourceServiceV4.SUPPORTED_PROTOCOL,
					4.0F,
					FederationImageAccessLoggingDataSourceServiceV4.class)
			);
		
		services.add(
				new ProviderService(
					this, 
					ImageDataSourceSpi.class,
					AbstractFederationImageDataSourceService.SUPPORTED_PROTOCOL,
					4.0F,
					FederationImageDataSourceServiceV4.class)
			);
		
		services.add(
				new ProviderService(
					this, 
					ExternalPackageDataSourceSpi.class,
					FederationExternalPackageDataSourceServiceV4.SUPPORTED_PROTOCOL,
					4.0F,
					FederationExternalPackageDataSourceServiceV4.class)
			);
					
		services.add(
				new ProviderService(
					this, 
					VistaRadImageDataSourceSpi.class,
					AbstractFederationImageDataSourceService.SUPPORTED_PROTOCOL,
					4.0F,
					FederationVistaRadImageDataSourceServiceV4.class)
			);				
		
		services.add(
				new ProviderService(
					this, 
					ExternalSystemOperationsDataSourceSpi.class,
					AbstractFederationExternalSystemOperationDataSourceService.SUPPORTED_PROTOCOL,
					4.0F,
					FederationExternalSystemOperationDataSourceServiceV4.class)
			);	
		services.add(
			new ProviderService(
				this,
				PatientArtifactDataSourceSpi.class,
				FederationPatientArtifactDataSourceServiceV4.SUPPORTED_PROTOCOL,
				4.0F,
				FederationPatientArtifactDataSourceServiceV4.class)
			);
		
		// version 5 services
		services.add(
				new ProviderService(
					this,
					DocumentDataSourceSpi.class,
					FederationDocumentDataSourceServiceV5.SUPPORTED_PROTOCOL,
					5.0F,
					FederationDocumentDataSourceServiceV5.class)
				);
		services.add(
				new ProviderService(
					this,
					DocumentSetDataSourceSpi.class,
					FederationDocumentSetDataSourceServiceV5.SUPPORTED_PROTOCOL,
					5.0F,
					FederationDocumentSetDataSourceServiceV5.class)
				);
		services.add(
				new ProviderService(
					this,
					ExternalPackageDataSourceSpi.class,
					FederationExternalPackageDataSourceServiceV5.SUPPORTED_PROTOCOL,
					5.0F,
					FederationExternalPackageDataSourceServiceV5.class)
				);
		services.add(
				new ProviderService(
					this,
					ImageAccessLoggingSpi.class,
					FederationImageAccessLoggingDataSourceServiceV5.SUPPORTED_PROTOCOL,
					5.0F,
					FederationImageAccessLoggingDataSourceServiceV5.class)
				);
		services.add(
				new ProviderService(
					this,
					ImageAnnotationDataSourceSpi.class,
					FederationImageAnnotationDataSourceServiceV5.SUPPORTED_PROTOCOL,
					5.0F,
					FederationImageAnnotationDataSourceServiceV5.class)
				);
		services.add(
				new ProviderService(
					this,
					ImageDataSourceSpi.class,
					FederationImageDataSourceServiceV5.SUPPORTED_PROTOCOL,
					5.0F,
					FederationImageDataSourceServiceV5.class)
				);
		services.add(
				new ProviderService(
					this,
					PassthroughDataSourceSpi.class,
					FederationPassthroughDataSourceServiceV5.SUPPORTED_PROTOCOL,
					5.0F,
					FederationPassthroughDataSourceServiceV5.class)
				);
		services.add(
				new ProviderService(
					this,
					PatientArtifactDataSourceSpi.class,
					FederationPatientArtifactDataSourceServiceV5.SUPPORTED_PROTOCOL,
					5.0F,
					FederationPatientArtifactDataSourceServiceV5.class)
				);
		services.add(
				new ProviderService(
					this,
					PatientDataSourceSpi.class,
					FederationPatientDataSourceServiceV5.SUPPORTED_PROTOCOL,
					5.0F,
					FederationPatientDataSourceServiceV5.class)
				);
		services.add(
				new ProviderService(
					this,
					StudyGraphDataSourceSpi.class,
					FederationStudyGraphDataSourceServiceV5.SUPPORTED_PROTOCOL,
					5.0F,
					FederationStudyGraphDataSourceServiceV5.class)
				);
		services.add(
				new ProviderService(
					this,
					UserDataSourceSpi.class,
					FederationUserDataSourceServiceV5.SUPPORTED_PROTOCOL,
					5.0F,
					FederationUserDataSourceServiceV5.class)
				);
		services.add(
				new ProviderService(
					this,
					VistaRadDataSourceSpi.class,
					FederationVistaRadDataSourceServiceV5.SUPPORTED_PROTOCOL,
					5.0F,
					FederationVistaRadDataSourceServiceV5.class)
				);
		services.add(
				new ProviderService(
					this,
					VistaRadImageDataSourceSpi.class,
					FederationVistaRadImageDataSourceServiceV5.SUPPORTED_PROTOCOL,
					5.0F,
					FederationVistaRadImageDataSourceServiceV5.class)
				);
		services.add(
				new ProviderService(
					this, 
					ExternalSystemOperationsDataSourceSpi.class,
					AbstractFederationExternalSystemOperationDataSourceService.SUPPORTED_PROTOCOL,
					5.0F,
					FederationExternalSystemOperationDataSourceServiceV5.class)
			);	
		
		// version 6 services
		services.add(
				new ProviderService(
					this,
					PatientDataSourceSpi.class,
					FederationPatientDataSourceServiceV6.SUPPORTED_PROTOCOL,
					6.0F,
					FederationPatientDataSourceServiceV6.class)
				);
		services.add(
				new ProviderService(
					this,
					UserDataSourceSpi.class,
					FederationUserDataSourceServiceV6.SUPPORTED_PROTOCOL,
					6.0F,
					FederationUserDataSourceServiceV6.class)
				);
services.add(
				new ProviderService(
					this, 
					ImageAccessLoggingSpi.class,
					FederationImageAccessLoggingDataSourceServiceV7.SUPPORTED_PROTOCOL,
					7.0F,
					FederationImageAccessLoggingDataSourceServiceV7.class)
			);	
			
				
		// load the FederationConfiguration if it exists
		synchronized(FederationDataSourceProvider.class)
	    {
			try
			{
				if(federationConfiguration == null)
				{
					federationConfiguration = (FederationConfiguration)loadConfiguration();
					if(federationConfiguration != null)
					{
						FederationProxyUtilities.configureFederationCertificate(federationConfiguration);
					}
				}
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
	    storeConfiguration(getFederationConfiguration());
    }
	
	/**
	 * A package level method for SPI implementation to get the
	 * Configuration.
	 * 
	 * @return
	 */
	static FederationConfiguration getFederationConfiguration()
	{
		if(federationConfiguration == null)
			logger.error("FederationConfiguration is null, possibly called before FederationDataSourceProvider was instantiated.");
		
		return federationConfiguration;
	}

	@Override
	public SortedSet<ProviderService> getServices()
	{
		return Collections.unmodifiableSortedSet(services);
	}
	
	/**
	 * Main function to create a Federation Data Source configuration file. 
	 * The truststore password and the keystore password must be provided, all other parameters are optional.
	 * Required Arguments:
	 * 	-truststorePassword <truststore password>
	 *  -keystorePassword <keystore password>
	 * 
	 * Optional arguments include:
	 *  -keystoreUrl <keystore URL>
	 *  -truststoreUrl <truststore URL>
	 *  -federationSslProtocol <Federation SSL protocol>
	 * 
	 * @param args Arguments shown above
	 */
	public static void main(String [] args)
	{
		System.out.println("Creating exchange datasource configuration file");
		
		String truststorePassword = null;
		String keystorePassword = null;
		String truststoreUrl = null;
		String keystoreUrl = null;
		String federationSslProtocol = null;
		int metadataTimeout = 0;
		boolean addCompression = true;
		for(int i = 0; i < args.length; i++)
		{
			if("-truststorePassword".equals(args[i]))
			{
				truststorePassword = args[++i];
			}
			else if("-keystorePassword".equals(args[i]))
			{
				keystorePassword = args[++i];
			}
			else if("-keystoreUrl".equals(args[i]))
			{
				keystoreUrl = args[++i];
			}
			else if("-truststoreUrl".equals(args[i]))
			{
				truststoreUrl = args[++i];
			}
			else if("-federationSslProtocol".equals(args[i]))
			{
				federationSslProtocol = args[++i];
			}
			else if("-metadataTimeout".equalsIgnoreCase(args[i]))
			{
				metadataTimeout = Integer.parseInt(args[++i]);
			}
			else if("-addCompression".equalsIgnoreCase(args[i]))
			{
				addCompression = Boolean.parseBoolean(args[++i]);
			}
		}
		FederationConfiguration fedConfiguration = FederationConfiguration.createConfiguration(keystoreUrl, 
				keystorePassword, truststoreUrl, truststorePassword, federationSslProtocol);
		if(metadataTimeout > 0)		
			fedConfiguration.setMetadataTimeoutMs(metadataTimeout);
		fedConfiguration.setAddCompressionForImageRequests(addCompression);
		FederationDataSourceProvider provider = new FederationDataSourceProvider(fedConfiguration);
		provider.storeConfiguration();
		System.out.println("Configuration file saved to '" + provider.getConfigurationFileName() + "'.");
	}
}
