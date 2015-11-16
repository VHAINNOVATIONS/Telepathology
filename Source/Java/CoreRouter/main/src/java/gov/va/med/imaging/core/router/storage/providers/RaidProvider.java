package gov.va.med.imaging.core.router.storage.providers;

import jcifs.smb.NtlmPasswordAuthentication;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.storage.StorageContext;
import gov.va.med.imaging.exchange.business.storage.NetworkLocationInfo;
import gov.va.med.imaging.exchange.business.storage.Provider;

import org.apache.log4j.Logger;

public class RaidProvider extends StorageCIFSProvider
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(RaidProvider.class);

	public RaidProvider(Provider provider)
	{
		super(provider);
	}

	@Override
	protected NetworkLocationInfo getCurrentWriteLocation()  throws MethodException, ConnectionException {
		return StorageContext.getDataSourceRouter().getCurrentWriteLocation(this);
	}
}
