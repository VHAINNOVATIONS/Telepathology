/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jan 3, 2011
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
package gov.va.med.imaging.access;

import java.util.ArrayList;
import java.util.List;

import gov.va.med.imaging.access.TransactionLogEntry;
import gov.va.med.imaging.access.TransactionLogWriter;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;

/**
 * This is an implementation of the TransactionLogWriter which simply holds each transaction log entry
 * in a list and then provides a list.  This is intended to act as a bridge between the old way of accessing
 * the transaction log where the items were returned as a list and the new way which uses the TransactionLogWriter.
 * 
 * Take special care in using this implementation.  The problem with the old way of returning the list of entries
 * was the VIX would run out of memory if the number of entries was too large.  This implementation of 
 * TransactionLogWriter has the same problem and could potentially cause issues.  This implementation should
 * not be used when the possible data set of transaction log entries may be very large (> 1000)
 * 
 * @author vhaiswwerfej
 *
 */
public class TransactionLogWriterHolder 
implements TransactionLogWriter
{
	private final List<TransactionLogEntry> entries = 
		new ArrayList<TransactionLogEntry>();

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogWriter#writeTransactionLogEntry(gov.va.med.imaging.access.TransactionLogEntry)
	 */
	@Override
	public void writeTransactionLogEntry(TransactionLogEntry entry)
			throws MethodException
	{
		entries.add(entry);
	}
	
	public List<TransactionLogEntry> getEntries()
	{
		return entries;
	}

}
