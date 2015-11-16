/**
 * 
 */
package gov.va.med.imaging.proxy.exceptions;

/**
 * @author vhaiswbeckec
 *
 */
public class ProxyClientException 
extends ProxyException
{
	private static final long serialVersionUID = 1L;
	private int httpStatus;
	
	public ProxyClientException(int httpStatus, String message)
	{
		super(message);
		this.httpStatus = httpStatus;
	}

	public int getHttpStatus()
	{
		return this.httpStatus;
	}
}
