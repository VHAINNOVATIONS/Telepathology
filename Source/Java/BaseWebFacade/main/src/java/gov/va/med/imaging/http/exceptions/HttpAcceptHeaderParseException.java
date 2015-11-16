/*
 * Created on Apr 26, 2004
 * 
 * To change this generated comment go to Window>Preferences>Java>Code
 * Generation>Code Template
 */
package gov.va.med.imaging.http.exceptions;

/**
 * @author Chris Beckey
 */
public class HttpAcceptHeaderParseException 
extends HttpHeaderParseException
{
	
	/**
	 * 
	 */
	public HttpAcceptHeaderParseException()
	{
		super();
	}

	/**
	 * @param message
	 */
	public HttpAcceptHeaderParseException(String message)
	{
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public HttpAcceptHeaderParseException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public HttpAcceptHeaderParseException(Throwable cause)
	{
		super(cause);
	}

}
