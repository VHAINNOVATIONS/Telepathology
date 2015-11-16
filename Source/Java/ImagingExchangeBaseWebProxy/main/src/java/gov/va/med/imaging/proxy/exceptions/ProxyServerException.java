/**
 * 
 */
package gov.va.med.imaging.proxy.exceptions;

/**
 * @author vhaiswbeckec
 *
 */
public class ProxyServerException 
extends ProxyException
{
	private static final long serialVersionUID = 1L;
	private int httpStatusCode;

	public ProxyServerException(String transactionId, int statusCode, String message)
	{
		super(transactionId, message);
		this.httpStatusCode = statusCode;
	}

	public int getHttpStatusCode()
	{
		return this.httpStatusCode;
	}
}
