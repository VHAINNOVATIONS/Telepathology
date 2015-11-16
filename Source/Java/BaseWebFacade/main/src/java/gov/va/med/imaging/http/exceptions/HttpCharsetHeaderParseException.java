/*
 * Originally HttpCharsetHeaderParseException.java 
 * created on Nov 18, 2004 @ 5:24:37 PM
 * by Chris Beckey mailto:c.beckey@seetab.com
 *
 */
package gov.va.med.imaging.http.exceptions;

/**
 * @author Chris Beckey mailto:c.beckey@seetab.com
 * @since Nov 18, 2004 5:24:37 PM
 *
 * Add comments here
 */
public class HttpCharsetHeaderParseException 
extends HttpHeaderParseException
{

	/**
	 * 
	 */
	public HttpCharsetHeaderParseException()
	{
		super();
	}

	/**
	 * @param message
	 */
	public HttpCharsetHeaderParseException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public HttpCharsetHeaderParseException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public HttpCharsetHeaderParseException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
