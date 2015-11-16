package gov.va.med.imaging.exchange.webservices.exceptions;

public class WebservicesStudyIdParseException 
extends WebserviceIdParseException
{
	private static final long serialVersionUID = 3523114243156564572L;

	public WebservicesStudyIdParseException()
	{
		super();
	}

	public WebservicesStudyIdParseException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public WebservicesStudyIdParseException(String message)
	{
		super(message);
	}

	public WebservicesStudyIdParseException(Throwable cause)
	{
		super(cause);
	}

}
