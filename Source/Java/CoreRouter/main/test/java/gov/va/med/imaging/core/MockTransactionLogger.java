/**
 * 
 */
package gov.va.med.imaging.core;

import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;

import gov.va.med.imaging.access.TransactionLogEntry;
import gov.va.med.imaging.access.TransactionLogger;
import gov.va.med.imaging.exchange.enums.DatasourceProtocol;
import gov.va.med.imaging.exchange.enums.ImageQuality;
import gov.va.med.imaging.transactioncontext.TransactionContext;

/**
 * @author vhaiswbeckec
 *
 */
public class MockTransactionLogger 
implements TransactionLogger
{

   @Override
   public void dummyTransactionLoggerInterfaceMethod () {};
   
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogger#destroy()
	 */
//	@Override
	public void destroy()
	{

	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogger#findAllTransactionLogEntries()
	 */
//	@Override
	public Iterator<? extends TransactionLogEntry> findAllTransactionLogEntries()
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogger#findTransactionLogEntries(java.util.Date, java.util.Date, gov.va.med.imaging.exchange.enums.ImageQuality, java.lang.String, java.lang.String, gov.va.med.imaging.exchange.enums.DatasourceProtocol, java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
//	@Override
	public Enumeration<TransactionLogEntry> findTransactionLogEntries(
			Date startDate, Date endDate, ImageQuality imageQuality,
			String user, String modality,
			DatasourceProtocol datasourceProtocol, String errorMessage,
			String imageUrn, String transactionId, boolean forward)
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogger#findTransactionLogEntries(java.lang.String)
	 */
//	@Override
	public Enumeration<TransactionLogEntry> findTransactionLogEntries(
			String transactionId)
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogger#findTransactionLogEntries(java.lang.String)
	 */
//	@Override
	public Enumeration<TransactionLogEntry> findTransactionLogEntries(
			String fieldName, String fieldValue)
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogger#init()
	 */
//	@Override
	public void init()
	{

	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogger#writeLogEntry(gov.va.med.imaging.transactioncontext.TransactionContext)
	 */
//	@Override
	public void writeLogEntry(TransactionContext context)
	{
		System.out.println(context.toString());
	}

}
