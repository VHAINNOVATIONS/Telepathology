/*
 * Originally HttpQValueParseException.java 
 * created on Nov 19, 2004 @ 2:24:26 PM
 * by Chris Beckey mailto:c.beckey@seetab.com
 *
 */
package gov.va.med.imaging.http.exceptions;

/**
 * @author Chris Beckey mailto:c.beckey@seetab.com
 * @since Nov 19, 2004 2:24:26 PM
 *
 * Add comments here
 */
public class HttpQValueParseException 
extends HttpHeaderParseException
{

	/**
	 * 
	 */
	public HttpQValueParseException()
	{
		super();
	}

	/**
	 * @param message
	 */
	public HttpQValueParseException(String message)
	{
		super(message);

	}

	/**
	 * @param cause
	 */
	public HttpQValueParseException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public HttpQValueParseException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
