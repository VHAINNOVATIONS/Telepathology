package gov.va.med.imaging.exchange.business.storage;

public interface ConfigurationDataSource 
{
	StorageServerDatabaseConfiguration getConfigurationFromDataSource(ProviderFactory providerFactory);
}
