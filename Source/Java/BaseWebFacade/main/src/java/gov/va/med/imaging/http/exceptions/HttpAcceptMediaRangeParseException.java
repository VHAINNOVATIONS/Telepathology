/*
 * Originally HttpAcceptMediaRangeParseException.java 
 * created on Nov 22, 2004 @ 3:13:00 PM
 * by Chris Beckey mailto:c.beckey@seetab.com
 *
 */
package gov.va.med.imaging.http.exceptions;

/**
 * @author Chris Beckey mailto:c.beckey@seetab.com
 * @since Nov 22, 2004 3:13:00 PM
 *
 * Add comments here
 */
public class HttpAcceptMediaRangeParseException extends HttpAcceptHeaderParseException
{

	/**
	 * 
	 */
	public HttpAcceptMediaRangeParseException()
	{
		super();
	}

	/**
	 * @param message
	 */
	public HttpAcceptMediaRangeParseException(String message)
	{
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public HttpAcceptMediaRangeParseException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public HttpAcceptMediaRangeParseException(Throwable cause)
	{
		super(cause);
	}

}
