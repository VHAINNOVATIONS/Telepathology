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

import gov.va.med.imaging.core.interfaces.exceptions.MethodException;

/**
 * Interface that defines the way transaction log entries are returned from the transaction log.
 * Each entry that matches the query parameters will use the writeTransactionLogEntry method.  This allows
 * each entry to be processed individually rather than as a large result set.  The problem with the large result 
 * set is if the set is too large, the VIX would run out of memory and throw exceptions.  By processing each
 * entry one at a time, this should be avoided.
 * 
 * By using this interface, the implementation may format the TransactionLogEntry in any way necessary
 * 
 * 
 * @author vhaiswwerfej
 *
 */
public interface TransactionLogWriter
{
	/**
	 * Returning/writing one entry
	 * 
	 * @param entry The current entry
	 * @throws MethodException
	 */
	public void writeTransactionLogEntry(TransactionLogEntry entry)
	throws MethodException;

}
