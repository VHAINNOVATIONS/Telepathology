/**
 * 
 */
package gov.va.med.imaging.transactioncontext;

/**
 * @author VHAISWBECKEC
 *
 */
public abstract class TransactionContextException 
extends Exception
{
	public TransactionContextException()
	{
		super();
	}

	public TransactionContextException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public TransactionContextException(String message)
	{
		super(message);
	}

	public TransactionContextException(Throwable cause)
	{
		super(cause);
	}

}
