package gov.va.med.imaging.exchange.webservices.exceptions;

public abstract class WebserviceIdParseException 
extends Exception
{
	public WebserviceIdParseException()
	{
		super();
	}

	public WebserviceIdParseException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public WebserviceIdParseException(String message)
	{
		super(message);
	}

	public WebserviceIdParseException(Throwable cause)
	{
		super(cause);
	}

}
