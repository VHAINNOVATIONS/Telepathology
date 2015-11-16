/**
 * 
 */
package gov.va.med.exceptions;

/**
 * @author vhaiswbeckec
 *
 */
public class GlobalArtifactIdentifierFormatException
extends Exception
{
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public GlobalArtifactIdentifierFormatException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public GlobalArtifactIdentifierFormatException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public GlobalArtifactIdentifierFormatException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
