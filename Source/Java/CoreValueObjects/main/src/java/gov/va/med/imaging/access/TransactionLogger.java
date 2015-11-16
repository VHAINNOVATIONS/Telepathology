/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: May 27, 2008
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author VHAISWBECKEC
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */
package gov.va.med.imaging.access;

//import java.util.Date;
//import java.util.Enumeration;
//import java.util.Iterator;

//import gov.va.med.imaging.exchange.enums.DatasourceProtocol;
//import gov.va.med.imaging.exchange.enums.ImageQuality;
//import gov.va.med.imaging.transactioncontext.TransactionContext;

/**
 * @author VHAISWBECKEC
 *
 */
public interface TransactionLogger
{

   public abstract void dummyTransactionLoggerInterfaceMethod ();
   
//	public abstract void init();
	
//	public abstract void destroy();
	
//	public abstract void writeLogEntry(TransactionContext context);
	
	/**
	 * Implementations of this method should do the absolute minimum necessary
	 * to get an iterator over all of the log entries.  This method will only
	 * be called as a last-ditch attempt to get log data.
	 * @return
	 */
//	public abstract Iterator<? extends TransactionLogEntry> findAllTransactionLogEntries();
	
	/**
	 * @param startDate
	 * @param endDate
	 * @param imageQuality
	 * @param user
	 * @param modality
	 * @param datasourceProtocol
	 * @param errorMessage
	 * @param imageUrn
	 * @param transactionId
	 * @param forward
	 * @return
	 */
/***
	public abstract Enumeration<TransactionLogEntry> findTransactionLogEntries( 
		Date startDate, Date endDate, 
		ImageQuality imageQuality, 
		String user, 
		String modality, 
		DatasourceProtocol datasourceProtocol,
		String errorMessage,
		String imageUrn,
		String transactionId,
		boolean forward
	);
***/	
		
//	public abstract Enumeration<TransactionLogEntry> findTransactionLogEntries(String transactionId);
	
//    public abstract Enumeration<TransactionLogEntry> findTransactionLogEntries(String fieldName, String fieldValue);
}
