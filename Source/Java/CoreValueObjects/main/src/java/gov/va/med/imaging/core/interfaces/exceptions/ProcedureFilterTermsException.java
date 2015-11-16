package gov.va.med.imaging.core.interfaces.exceptions;

public class ProcedureFilterTermsException extends Exception
{
	private static final long serialVersionUID = 1L;

	public ProcedureFilterTermsException()
	{
	}

	public ProcedureFilterTermsException(String message)
	{
		super(message);
	}

	public ProcedureFilterTermsException(Throwable cause)
	{
		super(cause);
	}

	public ProcedureFilterTermsException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
