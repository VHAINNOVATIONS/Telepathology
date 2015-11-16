/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 21, 2011
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
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

import gov.va.med.imaging.GUID;
import gov.va.med.imaging.exchange.business.TransactionContextLogEntrySnapshot;

/**
 * @author VHAISWWERFEJ
 *
 */
public class TransactionLogEntryObjectBuilder
{
	public static TransactionLogEntry createTransactionLogEntry()
	{
		return createTransactionLogEntry("");
	}
	
	public static TransactionLogEntry createTransactionLogEntry(String parentCommandId)
	{
		return createTransactionLogEntry(new GUID().toShortString(), parentCommandId);
	}
	
	public static TransactionLogEntry createTransactionLogEntry(String commandId, 
			String parentCommandId)
	{
		TransactionLogEntry entry = new TransactionContextLogEntrySnapshot(
				new Long(System.currentTimeMillis()), 
				new Long(100),
				"123456",
				"Test query",
				"no filter",
				"testCommandClassName",
				new Integer(5),
				new Long(0),
				new Long(0),
				new Long(0),
				new Long(0),
				"n/a",
				"localhost",					
				"200",
				"660",
				"John Smith",
				new GUID().toString(),
				"urn:123",
				Boolean.FALSE,
				"", 
				"",
				"",
				"vista",
				"404",
				"",
				"660",
				new Long(23),
				"0.9.6.0",
				"200",
				new Integer(3),
				Boolean.FALSE,
				commandId,
				parentCommandId,
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"");
		return entry;
	}

}
