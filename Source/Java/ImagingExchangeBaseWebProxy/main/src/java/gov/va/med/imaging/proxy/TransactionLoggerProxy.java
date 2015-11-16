package gov.va.med.imaging.proxy;


/**
 * This interface is a simple proxy for the web access to TransactionLogger.
 * @author VHAISWBATESL1
 *
 */
public abstract interface TransactionLoggerProxy
{

	/**
	 * Execute a remote Transaction Logger request.
	 * @param methodName The name of the method to invoke.
	 * @param clazzes The classes of the parameters.
	 * @param params The method parameters.
	 * @return a generic Java Object for the user to puzzle over.
	 * @throws Exception if something goes wrong.
	 */
	public abstract Object call (String     methodName,
	                             Class<?>[] clazzes,
	                             Object[]   params) throws Exception;
	
} // abstract interface TransactionLoggerProxy
