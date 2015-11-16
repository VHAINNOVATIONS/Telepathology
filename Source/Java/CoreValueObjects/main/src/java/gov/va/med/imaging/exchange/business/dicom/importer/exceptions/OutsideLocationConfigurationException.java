package gov.va.med.imaging.exchange.business.dicom.importer.exceptions;

import gov.va.med.imaging.core.interfaces.exceptions.MethodException;

public class OutsideLocationConfigurationException extends MethodException
{
	private static final long serialVersionUID = 1L;

	public OutsideLocationConfigurationException()
    {
	    super();
    }

	public OutsideLocationConfigurationException(String message, Throwable cause)
    {
	    super(message, cause);
    }

	public OutsideLocationConfigurationException(String message)
    {
	    super(message);
    }

	public OutsideLocationConfigurationException(Throwable cause)
    {
	    super(cause);
    }
}



