package gov.va.med.imaging.exchange.business.storage.exceptions;

import gov.va.med.imaging.core.interfaces.exceptions.MethodException;

public class RetrievalException extends MethodException 
{

	public RetrievalException() {
		super();
	}

	public RetrievalException(String message) {
		super(message);
	}

}
