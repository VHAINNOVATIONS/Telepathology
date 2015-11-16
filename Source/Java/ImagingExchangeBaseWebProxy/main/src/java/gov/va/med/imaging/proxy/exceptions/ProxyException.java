/**
 * 
 */
package gov.va.med.imaging.proxy.exceptions;

/**
 * @author vhaiswbeckec
 * @deprecated No longer use this exception, use one of the business exceptions (ConnectionException, MethodException) or its derivatives
 *
 */
@Deprecated
public class ProxyException
extends Exception
{
	private static final long serialVersionUID = 1L;
	private final String transactionId;

	public ProxyException(String transactionId)
	{
		super();
		this.transactionId = transactionId;
	}

	public ProxyException(String transactionId, String message, Throwable cause)
	{
		super(message, cause);
		this.transactionId = transactionId;
	}

	public ProxyException(String transactionId, String message)
	{
		super(message);
		this.transactionId = transactionId;
	}

	public ProxyException(String transactionId, Throwable cause)
	{
		super(cause);
		this.transactionId = transactionId;
	}

	/**
	 * @return the transactionId
	 */
	public String getTransactionId()
	{
		return this.transactionId;
	}
}
