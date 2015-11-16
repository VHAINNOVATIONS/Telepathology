/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Sep 30, 2008
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
package gov.va.med.imaging.core.router.commands;

import gov.va.med.imaging.core.interfaces.exceptions.CompositeMethodException;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractCommandImpl;
import gov.va.med.imaging.access.TransactionLogEntry;
//import gov.va.med.imaging.access.je.TransactionLogEntryImpl;

/**
 * A Command implementation for recording Transaction Log records.
 * The result of successful processing is a void, represented by the
 * "Object" class used as the generic type.  The result will always
 * be a null. 
 * 
 * @author VHAISWBECKEC
 *
 */
public class PostTransactionLogEntryCommandImpl
extends AbstractCommandImpl<java.lang.Void>
{
	private static final long serialVersionUID = 1L;   
	private final TransactionLogEntry entry;

	/**
	 * @param entry The TransactionLogEntry object which provides the values needed to store a TransactionLog record locally.
	 */
	public PostTransactionLogEntryCommandImpl(
			TransactionLogEntry entry)
	{
		super();
		this.entry = entry;
	}

	/**
	 * Store a TransactionLog record locally using values provided by a TransactionContext object or a TransactionLogEntry object.
	 */
	@Override
	public java.lang.Void callSynchronouslyInTransactionContext()
	throws MethodException
	{
		getLogger().info("Synchronous Command [" + this.getClass().getSimpleName() + "] - processing.");

		if (getEntry () != null)
			getLogger().info( "postLocalTransactionLogEntry - Transaction ID [" + getEntry().getTransactionId() + "]." );

		// The CompositeMethodException collects the exceptions from each
		// attempt. If some attempt succeeds then this instance is silently
		// discarded, else if all attempts fail this instance is thrown.
		CompositeMethodException compositeException = new CompositeMethodException();
		// keep track of whether we successfully completed the local call
		boolean success = false;

		try
		{
			if (getEntry () != null)
				getCommandContext ().getTransactionLoggerService ().writeLogEntry (getEntry ());
			else
				throw new MethodException ("Can't find a non-null context or entry.");
			success = true; 
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
			if (getEntry () != null)
				getLogger().error(
						"Failed To Write Log Entry - Transaction ID [" + getEntry ().getTransactionId() + "].\n" +
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

		return (java.lang.Void)null;
	}

	/**
	 * Get the TransactionLogEntry object used to locally store a TransactionLog record.
	 * @return the TransactionLogEntry object used to locally store a TransactionLog record.
	 */
	public TransactionLogEntry getEntry ()
	{
		return this.entry;
	}

	/**
	 * This is a non-idempotent command, the .equals() must always return false.
	 * 
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		return false;
	}


	@Override
	protected String parameterToString()
	{
		StringBuffer sb = new StringBuffer();

		sb.append(getEntry() == null ? "<null entry>" : getEntry().toString());

		return sb.toString();
	}


	/**
	 * Translate a TransactionContext into a DB-friendly storage object.
	 * Since TransactionContext is a Vista Realm Proxy Security thingy, it's
	 * not an object we want to Serialize and send over the wire.
	 * @param context The TransactionContext object to translate.
	 * @return the TransactionLogEntryImpl DB-friendly storage object.
	 */
	/***
   private TransactionLogEntryImpl createTransactionLogEntryImpl (TransactionContext context)
   {

      Long transactionStartTime = context.getStartTime ();
      Long transactionExecutionTime = transactionStartTime == 0L ? 
         0L : System.currentTimeMillis () - transactionStartTime;

      String siteNumber = context.getLoggerSiteNumber ();
      String requestType = context.getRequestType () + ": " + siteNumber + " <- " + context.getServicedSource ();

      TransactionLogEntryImpl entry = new TransactionLogEntryImpl (transactionStartTime,
                                                                   transactionExecutionTime,
                                                                   context.getPatientID (),
                                                                   requestType,
                                                                   context.getQueryFilter (),
                                                                   context.getCommandClassName (),
                                                                   context.getEntriesReturned (),
                                                                   context.getFacadeBytesSent (),
                                                                   context.getFacadeBytesReceived (),
                                                                   context.getDataSourceBytesSent (),
                                                                   context.getDataSourceBytesReceived (),
                                                                   context.getQuality (),
                                                                   context.getMachineName (),
                                                                   siteNumber,
                                                                   context.getOriginatingAddress (),
                                                                 //context.getFullName (),
                                                                   context.getLoggerFullName (),
                                                                   context.getTransactionId (),
                                                                   context.getUrn (),
                                                                   context.isItemCached (),
                                                                   context.getErrorMessage (),
                                                                   context.getModality (),
                                                                   context.getPurposeOfUse (),
                                                                   context.getDatasourceProtocol (),
                                                                   context.getResponseCode (),
                                                                   context.getExceptionClassName (),
                                                                   context.getRealm (),
                                                                   context.getTimeToFirstByte (),
                                                                   context.getVixSoftwareVersion (),
                                                                   context.getServicedSource ());

      return entry;

   } // createTransactionLogEntryImpl
	 ***/
}
