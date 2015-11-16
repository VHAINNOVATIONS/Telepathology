/**
 * 
 */
package gov.va.med.imaging.access;

//import gov.va.med.imaging.transactioncontext.MockTransactionContext;
//import gov.va.med.imaging.transactioncontext.TransactionContext;

import java.io.IOException;
import java.io.OutputStream;
//import java.util.Iterator;

import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class XTestTransactionLog 
extends TestCase
{
	/**
	 * 
	 */
	public static final String TRANSACTION_LOG_IMPL = "gov.va.med.imaging.access.je.TransactionLogImpl";
//	private TransactionLogger transactionLogger;
	
    /* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() 
	throws Exception
	{
		super.setUp();
//		transactionLogger = createLoggerInstance();
	}

/***
	@SuppressWarnings("unchecked")
	private TransactionLogger createLoggerInstance() 
    throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
	    TransactionLogger logger;
	    Class<TransactionLogger> transactionLoggerClass = (Class<TransactionLogger>)Class.forName(TRANSACTION_LOG_IMPL);
	    logger = transactionLoggerClass.newInstance();
	    
//	    logger.init();
	    return logger;
    }
***/

	/**
	 * Just iterate through all of the log entries.
	 */
	public void testRead()
	{
		OutputStream out = new OutputStream()
		{
			@Override
			public void write(int b) throws IOException
			{
			}
		};

/***
	    for( Iterator<? extends TransactionLogEntry> logIterator = transactionLogger.findAllTransactionLogEntries();
    	logIterator.hasNext(); )
	    {
	    	TransactionLogEntry logEntry = logIterator.next();
	    	
	    	try
			{
				out.write( logEntry.toString().getBytes() );
			} 
	    	catch (IOException x)
			{
				x.printStackTrace();
			}
	    }
***/
	    
		try
		{
			out.close();
		} 
		catch (IOException x)
		{
			x.printStackTrace();
		}
	}

	public void testWrite()
	{
//		TransactionContext tc = new MockTransactionContext();
		
//		transactionLogger.writeLogEntry(tc);
	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() 
	throws Exception
	{

		super.tearDown();
	}
}

