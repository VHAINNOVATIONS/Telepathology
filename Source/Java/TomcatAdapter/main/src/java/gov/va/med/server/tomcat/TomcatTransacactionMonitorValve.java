/**
 * 
 */
package gov.va.med.server.tomcat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletException;
import org.apache.catalina.Valve;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

/**
 * Configure this something like:
 *  <Valve className="gov.va.med.server.tomcat.TomcatTransacactionMonitorValve" transactionWarningTime="10000"/>
 * NOTE: the parent container must have its backgroundProcessorDelay attribute set to something reasonable
 * (e.g. <Engine defaultHost="660.med.va.gov" name="Catalina" backgroundProcessorDelay="5"> )
 * If the backgroundProcessorDelay is significantly more than the transactionWarningTime then the warnings will
 * be very late.  It is suggested that the backgroundProcessorDelay be less than the transactionWarningTime.
 * 
 * @author vhaiswbeckec
 *
 */
public class TomcatTransacactionMonitorValve
extends ValveBase
implements Valve
{
	private final List<ActiveTransaction> activeTransactions = 
		Collections.synchronizedList( new ArrayList<ActiveTransaction>() );
	private long transactionWarningTime = 10000;		// default to 10 seconds
	
	public long getTransactionWarningTime()
	{
		return this.transactionWarningTime;
	}

	public void setTransactionWarningTime(long transactionWarningTime)
	{
		this.transactionWarningTime = transactionWarningTime;
	}

	/**
	 * Run a periodic task in the parent Container background processing thread.
	 * 
	 * @see org.apache.catalina.Valve#backgroundProcess()
	 */
	@Override
	public void backgroundProcess()
	{
		long now = System.currentTimeMillis();
		// Make a copy so that we don't have to keep the mutex on the
		// active copy while we iterate and do our things.
		List<ActiveTransaction> transactions = new ArrayList<ActiveTransaction>(this.activeTransactions);
		
		if(getTransactionWarningTime() > 0)
			for(ActiveTransaction transaction : transactions)
			{
				if(now - transaction.getStartTime() > getTransactionWarningTime())
					issueWarning(transaction, now);
			}
	}

	/**
	 * @param transaction
	 */
	private void issueWarning(ActiveTransaction transaction, long now)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("A transaction has exceeded the configured maximum duration.\n");
		sb.append("Transaction has run for " );
		sb.append(now - transaction.getStartTime());
		sb.append(" milliseconds.\n");
		
		Thread thread = transaction.getWorker();
		sb.append("Thread ["); 
		sb.append( thread.getName() );
		sb.append( ']' );
		sb.append( thread.getState().toString() );
		sb.append( '\n' );
		for( StackTraceElement ste : thread.getStackTrace() )
		{
			sb.append(ste.getClassName());
			sb.append('.');
			sb.append(ste.getMethodName());
			sb.append(' ');
			sb.append(ste.getFileName());
			sb.append( '[');
			sb.append(ste.getLineNumber());
			sb.append(']');
		}
		
		System.err.println(sb.toString());
	}

	/**
	 * @see org.apache.catalina.Valve#getInfo()
	 */
	@Override
	public String getInfo()
	{
		return "Transaction monitoring valve, watches for long running transactions.";
	}

	/**
	 * @see org.apache.catalina.Valve#invoke(org.apache.catalina.connector.Request, org.apache.catalina.connector.Response)
	 */
	@Override
	public void invoke(Request request, Response response) 
	throws IOException, ServletException
	{
		long startTime = System.currentTimeMillis();
		Thread thread = Thread.currentThread();
		ActiveTransaction activeTransaction = new ActiveTransaction(startTime, thread);
		activeTransactions.add( activeTransaction );
		
		getNext().invoke(request, response);
		
		activeTransactions.remove( activeTransaction );
	}
	
	class ActiveTransaction
	{
		private final long startTime;
		private final Thread worker;
		
		/**
		 * @param startTime
		 * @param worker
		 */
		public ActiveTransaction(long startTime, Thread worker)
		{
			super();
			this.startTime = startTime;
			this.worker = worker;
		}

		public long getStartTime()
		{
			return this.startTime;
		}

		public Thread getWorker()
		{
			return this.worker;
		}
	}
}
