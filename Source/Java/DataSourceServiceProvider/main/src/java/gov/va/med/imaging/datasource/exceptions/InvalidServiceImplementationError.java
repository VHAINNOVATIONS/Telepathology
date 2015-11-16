package gov.va.med.imaging.datasource.exceptions;

import gov.va.med.imaging.core.interfaces.exceptions.ConfigurationError;

/**
 * Thrown when a service implementation is determined not to follow the
 * specification.  NOTE: this is an unchecked exception.
 * 
 * @author VHAISWBECKEC
 *
 */
public class InvalidServiceImplementationError 
extends ConfigurationError
{
	private static final long serialVersionUID = 1L;

	public InvalidServiceImplementationError(String componentName, String additionalInformation, Throwable cause)
    {
	    super(componentName, additionalInformation, cause);
    }

	public InvalidServiceImplementationError(String componentName, String additionalInformation)
    {
	    super(componentName, additionalInformation);
    }

	public InvalidServiceImplementationError(String componentName, Throwable cause)
    {
	    super(componentName, cause);
    }

	public InvalidServiceImplementationError(String componentName)
    {
	    super(componentName);
    }
}
