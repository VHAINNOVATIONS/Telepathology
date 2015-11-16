/**
 * 
 */
package gov.va.med.imaging.channels.exceptions;

/**
 * @author VHAISWBECKEC
 *
 */
public class ChecksumFormatException 
extends Exception
{
	private static final long serialVersionUID = 3121983358344182097L;

	public ChecksumFormatException()
	{
		super();
	}

	public ChecksumFormatException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public ChecksumFormatException(String message)
	{
		super(message);
	}

	public ChecksumFormatException(Throwable cause)
	{
		super(cause);
	}

}
