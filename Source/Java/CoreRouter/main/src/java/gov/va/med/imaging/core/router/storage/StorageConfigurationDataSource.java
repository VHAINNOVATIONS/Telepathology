package gov.va.med.imaging.core.router.storage;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.storage.ConfigurationDataSource;
import gov.va.med.imaging.exchange.business.storage.ProviderFactory;
import gov.va.med.imaging.exchange.business.storage.StorageServerDatabaseConfiguration;

import org.apache.log4j.Logger;

public class StorageConfigurationDataSource implements ConfigurationDataSource {

	private static final Logger logger = Logger.getLogger(StorageConfigurationDataSource.class);

	@Override
	public StorageServerDatabaseConfiguration getConfigurationFromDataSource(ProviderFactory providerFactory) 
	{
		try 
		{
			return StorageContext.getDataSourceRouter().getStorageServerDatabaseConfiguration(providerFactory);
		} 
		catch (MethodException e) 
		{
			logger.error(e.getMessage(), e);
		} 
		catch (ConnectionException e) 
		{
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}

}
