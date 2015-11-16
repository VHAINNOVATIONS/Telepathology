/**
 * 
 */
package gov.va.med.imaging.transactioncontext;

/**
 * @author VHAISWBECKEC
 *
 */
public class InvalidTransactionContextMementoException 
extends TransactionContextException
{
	private static final long serialVersionUID = -7146680874750680208L;

	/**
	 * 
	 */
	public InvalidTransactionContextMementoException()
	{
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InvalidTransactionContextMementoException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public InvalidTransactionContextMementoException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public InvalidTransactionContextMementoException(Throwable cause)
	{
		super(cause);
	}
}
