/**
 * 
 */
package gov.va.med.siteservice.exceptions;

/**
 * A generic exception that indicates that resolution of a RoutingToken
 * has failed because of an internal inconsistency or an unexpected
 * error.
 * This exception and its derivatives are not thrown if a RoutingToken
 * cannot be resolved because no mapping exists.
 * 
 * @author vhaiswbeckec
 *
 */
public class ArtifactSourceResolutionException
extends Exception
{
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public ArtifactSourceResolutionException()
	{
		super();
	}

	/**
	 * @param message
	 */
	public ArtifactSourceResolutionException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public ArtifactSourceResolutionException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ArtifactSourceResolutionException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
