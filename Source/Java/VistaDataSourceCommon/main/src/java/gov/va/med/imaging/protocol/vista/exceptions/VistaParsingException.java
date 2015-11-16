package gov.va.med.imaging.protocol.vista.exceptions;

import gov.va.med.imaging.core.interfaces.exceptions.MethodException;

/**
 * An exception thrown when an error has occurred parsing the response from a
 * Vista transaction.
 * 
 * @author VHAISWBECKEC
 *
 */
public class VistaParsingException 
extends MethodException
{
	private static final long serialVersionUID = 1L;

	public VistaParsingException()
	{
	}

	public VistaParsingException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public VistaParsingException(String message)
	{
		super(message);
	}

	public VistaParsingException(Throwable cause)
	{
		super(cause);
	}

}
