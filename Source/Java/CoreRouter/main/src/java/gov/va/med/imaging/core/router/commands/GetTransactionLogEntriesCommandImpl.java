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
package gov.va.med.imaging.core.router.commands;

import gov.va.med.imaging.access.TransactionLogWriter;
import gov.va.med.imaging.core.interfaces.exceptions.CompositeMethodException;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractCommandImpl;
import gov.va.med.imaging.exchange.enums.DatasourceProtocol;
import gov.va.med.imaging.exchange.enums.ImageQuality;

import java.util.Date;

/**
 * @author vhaiswwerfej
 *
 */
public class GetTransactionLogEntriesCommandImpl
extends AbstractCommandImpl<java.lang.Void>
{
	private static final long serialVersionUID = 1L;
	private final TransactionLogWriter transactionLogWriter;
	private final Date startDate;
	private final Date endDate;
	private final ImageQuality imageQuality;
	private final String user;
	private final String modality;
	private final DatasourceProtocol datasourceProtocol;
	private final String errorMessage;
	private final String imageUrn;
	private final String transactionId;
	private final Boolean forward;
	private final Integer startIndex;
	private final Integer endIndex;
	private final String fieldName;
	private final String fieldValue;
	
	public GetTransactionLogEntriesCommandImpl(
			TransactionLogWriter transactionLogWriter,
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
	{
		super();
		this.transactionLogWriter = transactionLogWriter;
		this.startDate = startDate;
		this.endDate = endDate;
		this.imageQuality = imageQuality;
		this.user = user;
		this.modality = modality;
		this.datasourceProtocol = datasourceProtocol;
		this.errorMessage = errorMessage;
		this.imageUrn = imageUrn;
		this.transactionId = transactionId;
		this.forward = forward;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.fieldName = null;
		this.fieldValue = null;
	}
	
	public GetTransactionLogEntriesCommandImpl(
			TransactionLogWriter transactionLogWriter,
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
			String             fieldName,
			String             fieldValue,
			Integer            startIndex,
			Integer            endIndex)
	{
		super();
		this.transactionLogWriter = transactionLogWriter;
		this.startDate = startDate;
		this.endDate = endDate;
		this.imageQuality = imageQuality;
		this.user = user;
		this.modality = modality;
		this.datasourceProtocol = datasourceProtocol;
		this.errorMessage = errorMessage;
		this.imageUrn = imageUrn;
		this.transactionId = transactionId;
		this.forward = forward;
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}
	
	public GetTransactionLogEntriesCommandImpl(
			TransactionLogWriter transactionLogWriter,
			String             fieldName,
			String             fieldValue)
	{
		super();
		this.transactionLogWriter = transactionLogWriter;
		this.startDate = null;
		this.endDate = null;
		this.imageQuality = null;
		this.user = null;
		this.modality = null;
		this.datasourceProtocol = null;
		this.errorMessage = null;
		this.imageUrn = null;
		this.transactionId = null;
		this.forward = null;
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
		this.startIndex = null;
		this.endIndex = null;
	}

	public GetTransactionLogEntriesCommandImpl(TransactionLogWriter transactionLogWriter)
	{
		super();
		this.transactionLogWriter = transactionLogWriter;
		this.startDate = null;
		this.endDate = null;
		this.imageQuality = null;
		this.user = null;
		this.modality = null;
		this.datasourceProtocol = null;
		this.errorMessage = null;
		this.imageUrn = null;
		this.transactionId = null;
		this.forward = null;
		this.fieldName = null;
		this.fieldValue = null;
		this.startIndex = null;
		this.endIndex = null;
	}

	@Override
	public Void callSynchronouslyInTransactionContext() 
	throws MethodException, ConnectionException
	{
		getLogger ().info ("Synchronous Command [" + this.getClass().getSimpleName() + "] - processing.");

		getLogger ().info ("getTransactionLogEntryList.");

		// The CompositeMethodException collects the exceptions from each
		// attempt. If some attempt succeeds then this instance is silently
		// discarded, else if all attempts fail this instance is thrown.
		CompositeMethodException compositeException = new CompositeMethodException();
		// keep track of whether we successfully completed the local and/or remote call(s).
		boolean success = false;

		try
		{
			if (startDate          == null &&
					endDate            == null &&
					imageQuality       == null &&
					user               == null &&
					modality           == null &&
					datasourceProtocol == null &&
					errorMessage       == null &&
					imageUrn           == null &&
					transactionId      == null &&
					forward            == null &&
					fieldName          == null &&
					fieldValue         == null && 
					startIndex         == null && 
					endIndex           == null)
			{
				getCommandContext ().getTransactionLoggerService ().getAllLogEntries (transactionLogWriter);
				success = true;
			}
			else
				if (startDate          == null &&
						endDate            == null &&
						imageQuality       == null &&
						user               == null &&
						modality           == null &&
						datasourceProtocol == null &&
						errorMessage       == null &&
						imageUrn           == null &&
						transactionId      == null &&
						forward            == null &&
						fieldName          != null &&
						fieldValue         != null)
				{
					getCommandContext ().getTransactionLoggerService ().getLogEntries (transactionLogWriter,
							fieldName, fieldValue);
					success = true;
				}
				else
				{
					getCommandContext ().getTransactionLoggerService ().getLogEntries (transactionLogWriter, 
							startDate, endDate,
							imageQuality, user, modality, datasourceProtocol, errorMessage, imageUrn, transactionId, 
							forward, startIndex, endIndex);
					success = true;
				}
		}

		// ConnectionException instances are always collected into the
		// CompositeMethodException
		catch (ConnectionException cX)
		{
			compositeException.addException(new MethodConnectionException(cX));
		}

		// MethodExceptions are always logged regardless of whether we fail
		// here or retry the next protocol

		// a MethodException is immediately re-thrown if
		// FailoverOnMethodException is false
		catch (MethodException mX)
		{
			getLogger().error(
					"Failed To Get Log Entries.\n" +
					"Exception details follow.",
					mX);
			compositeException.addException(mX);
			if (! getCommandContext().getRouter().isFailoverOnMethodException())
				throw mX;
		}

		if(! success)
		{
			throw compositeException;
		}
		return null;
	}

	@Override
	public boolean equals(Object obj)
	{
		return false;
	}

	@Override
	protected String parameterToString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("GetTransactionLogEntriesCommandImpl");

		return sb.toString();
	}

}
