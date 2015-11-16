package gov.va.med.imaging.exchange.business.storage.exceptions;

import gov.va.med.imaging.core.interfaces.exceptions.MethodException;

public class DeletionException extends MethodException 
{

	public DeletionException() {
		super();
	}

	public DeletionException(String message) {
		super(message);
	}

}
