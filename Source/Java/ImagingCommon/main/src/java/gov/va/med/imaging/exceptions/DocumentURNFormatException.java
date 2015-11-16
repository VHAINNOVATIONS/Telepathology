package gov.va.med.imaging.exceptions;

/**
 * Indicates the a String is not in a valid Image URN format.
 * 
 * @author VHAISWBECKEC
 *
 */
public class DocumentURNFormatException 
extends URNFormatException
{
	private static final long serialVersionUID = -6385185662744800097L;

	public DocumentURNFormatException()
	{
	}

	public DocumentURNFormatException(String message)
	{
		super(message);
	}

	public DocumentURNFormatException(Throwable cause)
	{
		super(cause);
	}

	public DocumentURNFormatException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
