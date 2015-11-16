package gov.va.med.imaging.exchange.business.storage;

import gov.va.med.imaging.exchange.business.storage.exceptions.StorageConfigurationException;

public abstract class ProviderFactory 
{
	public abstract Provider createProvider(Provider provider) throws StorageConfigurationException;
}
