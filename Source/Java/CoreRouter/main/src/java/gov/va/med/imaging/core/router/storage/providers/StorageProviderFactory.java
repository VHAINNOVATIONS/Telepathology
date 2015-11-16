package gov.va.med.imaging.core.router.storage.providers;

import gov.va.med.imaging.exchange.business.storage.Provider;
import gov.va.med.imaging.exchange.business.storage.ProviderFactory;
import gov.va.med.imaging.exchange.business.storage.exceptions.StorageConfigurationException;

public class StorageProviderFactory extends ProviderFactory
{
	private static final String RAID_PROVIDER = "RAID";
	private static final String JUKEBOX_PROVIDER = "JUKEBOX";
	
	@Override
	public Provider createProvider(Provider provider) throws StorageConfigurationException
	{
		if (provider.getProviderType().equals(RAID_PROVIDER))
		{
			return new RaidProvider(provider);
		}
		else if (provider.getProviderType().equals(JUKEBOX_PROVIDER))
		{
			return new JukeBoxProvider(provider);
		}
		else
		{
			throw new StorageConfigurationException("Unknown provider type: " + provider.getProviderType());
		}
	}
}
