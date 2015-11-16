package gov.va.med.imaging.exceptions;

/**
 * Indicates the a String is not in a valid Image URN format.
 * 
 * @author VHAISWBECKEC
 *
 */
public class ImageURNFormatException 
extends URNFormatException
{
	private static final long serialVersionUID = -6385185662744800097L;

	public ImageURNFormatException()
	{
	}

	public ImageURNFormatException(String message)
	{
		super(message);
	}

	public ImageURNFormatException(Throwable cause)
	{
		super(cause);
	}

	public ImageURNFormatException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
