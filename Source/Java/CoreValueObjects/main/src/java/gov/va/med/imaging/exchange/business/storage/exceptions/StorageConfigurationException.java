package gov.va.med.imaging.exchange.business.storage.exceptions;

import gov.va.med.imaging.core.interfaces.exceptions.MethodException;

public class StorageConfigurationException extends MethodException 
{

	public StorageConfigurationException() {
		super();
	}

	public StorageConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	public StorageConfigurationException(String message) {
		super(message);
	}

	public StorageConfigurationException(Throwable cause) {
		super(cause);
	}

}
