package gov.va.med.imaging.core.router.storage;

import gov.va.med.imaging.core.router.storage.providers.StorageProviderFactory;
import gov.va.med.imaging.exchange.business.dicom.DicomServerConfiguration;
import gov.va.med.imaging.exchange.business.storage.StorageServerDatabaseConfiguration;
import gov.va.med.server.Authentication;
import gov.va.med.server.ServerAdapterImpl;
import gov.va.med.server.ServerLifecycleEvent;
import gov.va.med.server.ServerLifecycleListener;
import java.security.Principal;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

public class StorageLifecycleListener implements ServerLifecycleListener
{

	final Logger logger = Logger.getLogger(this.getClass());
	private static ApplicationContext springFactoryContext = null;

	public StorageLifecycleListener()
	{
		ServerAdapterImpl.getSingleton().addServerLifecycleListener(this);
	}

	@Override
	public void serverLifecycleEvent(ServerLifecycleEvent event)
	{
		if (event.getEventType().equals(ServerLifecycleEvent.EventType.START))
		{
			StorageServerDatabaseConfiguration.setConfigurationDataSource(new StorageConfigurationDataSource());
			StorageServerDatabaseConfiguration.setProviderFactory(new StorageProviderFactory());
		}
	}

}
