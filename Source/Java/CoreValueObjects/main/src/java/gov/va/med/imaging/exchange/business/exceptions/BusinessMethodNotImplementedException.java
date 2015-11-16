package gov.va.med.imaging.exchange.business.exceptions;

import gov.va.med.imaging.core.interfaces.exceptions.MethodException;

public class BusinessMethodNotImplementedException extends MethodException
{

	public BusinessMethodNotImplementedException() 
	{
		super();
	}

	public BusinessMethodNotImplementedException(String message) 
	{
		super(message);
	}

}
