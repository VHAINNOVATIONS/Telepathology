/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr 10, 2008
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
package gov.va.med.imaging.datasource;

import java.util.Date;
import java.util.List;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.annotations.SPI;
import gov.va.med.imaging.access.TransactionLogEntry;
import gov.va.med.imaging.access.TransactionLogWriter;
import gov.va.med.imaging.exchange.enums.DatasourceProtocol;
import gov.va.med.imaging.exchange.enums.ImageQuality;


/**
 * @author VHAISWBATESL1
 */
@SPI(description="Defines the interface for writing and retrieving transaction logs")
public interface TransactionLoggerDataSourceSpi 
extends LocalDataSourceSpi
{
	/**
	 * Store a TransactionLog record using a TransactionLogEntry object.
	 * @param entry The TransactionLogEntry object to store a TransactionLog record.
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract void writeLogEntry(TransactionLogEntry entry) 
	throws MethodException, ConnectionException;


	/**
	 * Get a List of all the Transaction Log records.
	 * @return the List of all the TransactionLog records, if any.
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract void getAllLogEntries(TransactionLogWriter writer) 
	throws MethodException, ConnectionException;


	/**
	 * Get a List of Transaction Log records based on the passed in criteria.
	 * @param writer The transaction log writer to write the entries to
	 * @param startDate
	 * @param endDate
	 * @param imageQuality
	 * @param user
	 * @param modality
	 * @param datasourceProtocol
	 * @param errorMessage
	 * @param imageUrn
	 * @param transactionId
	 * @param forward indicates if the results should be sorted forwards (most recent last) or backwards (most recent first)
	 * @param startIndex The index of the first entry to return
	 * @param endIndex The index of the last entry to return
	 * @return the List of TransactionLog records meeting the query criteria, if any.
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract void getLogEntries(TransactionLogWriter writer,
			Date               startDate,
			Date               endDate, 
			ImageQuality       imageQuality, 
			String             user, 
			String             modality, 
			DatasourceProtocol datasourceProtocol,
			String             errorMessage,
			String             imageUrn,
			String             transactionId,
			Boolean            forward,
			Integer            startIndex,
			Integer            endIndex)
	throws MethodException, ConnectionException;


	/**
	 * Get a List of Transaction Log records for a field name with matching field value. 
	 * @param fieldName
	 * @param fieldValue
	 * @return the List of TransactionLog records meeting the query criteria, if any.
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract void getLogEntries (TransactionLogWriter writer,
			String fieldName,
			String fieldValue) 
	throws MethodException, ConnectionException;


	/**
	 * Get rid of old Transaction Log records.
	 * @param maxDaysAllowed The maximum number of days allowed before a TransactionLog record is deleted.
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract void purgeLogEntries (Integer maxDaysAllowed) 
	throws MethodException, ConnectionException;
}
