/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 20, 2010
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswwerfej
  Description: 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+

 */
package gov.va.med.imaging.transactionlogger.datasource;

/**
 * @author vhaiswwerfej
 *
 */
public class TransactionLoggerStatistics
implements TransactionLoggerStatisticsMBean
{
	private long transactionsPurged = 0L;
	private long transactionsQueried = 0L;
	private long transactionsWritten = 0L;
	private long transactionWriteErrors = 0L;
	private long transactionReadErrors = 0L;
	private long transactionErrors = 0L;
	
	public TransactionLoggerStatistics()
	{
		super();
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactionlogger.datasource.TransactionLoggerStatisticsMBean#getTransactionsPurged()
	 */
	@Override
	public long getTransactionsPurged()
	{
		return transactionsPurged;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactionlogger.datasource.TransactionLoggerStatisticsMBean#getTransactionsQueried()
	 */
	@Override
	public long getTransactionsQueried()
	{
		return transactionsQueried;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactionlogger.datasource.TransactionLoggerStatisticsMBean#getTransactionsWritten()
	 */
	@Override
	public long getTransactionsWritten()
	{
		return transactionsWritten;
	}
	
	@Override
	public long getTransactionWriteErrors()
	{
		return transactionWriteErrors;
	}

	@Override
	public long getTransactionReadErrors()
	{
		return transactionReadErrors;
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactionlogger.datasource.TransactionLoggerStatisticsMBean#getTransactionErrors()
	 */
	@Override
	public long getTransactionErrors()
	{
		return transactionErrors;
	}

	public synchronized void incrementTransactionWritten()
	{
		transactionsWritten++;	
	}

	public synchronized void incrementTransactionsQueried()
	{
		transactionsQueried++;
	}
	
	public synchronized void increaseTransactionsPurged(long count)
	{
		transactionsPurged += count;
	}
	
	public synchronized void incrementTransactionReadErrors()
	{
		transactionReadErrors++;
	}
	
	public synchronized void incrementTransactionWriteErrors()
	{
		transactionWriteErrors++;
	}
	
	public synchronized void incrementTransactionErrors()
	{
		transactionErrors++;
	}
}
