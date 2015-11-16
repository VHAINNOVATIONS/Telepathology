/*
 * Originally HttpHeaderParseException.java 
 * created on Nov 19, 2004 @ 3:52:25 PM
 * by Chris Beckey mailto:c.beckey@seetab.com
 *
 */
package gov.va.med.imaging.http.exceptions;

/**
 * @author Chris Beckey mailto:c.beckey@seetab.com
 * @since Nov 19, 2004 3:52:25 PM
 *
 * Add comments here
 */
public class HttpHeaderParseException 
extends Exception
{

	/**
	 * 
	 */
	public HttpHeaderParseException()
	{
		super();
	}

	/**
	 * @param message
	 */
	public HttpHeaderParseException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public HttpHeaderParseException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public HttpHeaderParseException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
