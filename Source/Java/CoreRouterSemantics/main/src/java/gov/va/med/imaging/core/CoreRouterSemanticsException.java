/**
 * 
 */
package gov.va.med.imaging.core;

/**
 * An exception thrown when a violation of the core router semantics or grammar
 * is detected.  This "should" never be thrown at runtime if the classes and methods
 * are correctly annotated and compilation was successful.
 * 
 * @author vhaiswbeckec
 *
 */
public class CoreRouterSemanticsException 
extends Exception
{
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public CoreRouterSemanticsException()
	{
	}

	/**
	 * @param message
	 */
	public CoreRouterSemanticsException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public CoreRouterSemanticsException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CoreRouterSemanticsException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
