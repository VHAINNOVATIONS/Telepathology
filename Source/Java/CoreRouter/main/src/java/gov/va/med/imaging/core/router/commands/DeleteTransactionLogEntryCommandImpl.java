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

/**
 * A Command implementation for deleting Transaction Log records.
 * The result of successful processing is a void, represented by the
 * "Object" class used as the generic type.  The result will always
 * be a null. 
 * 
 * @author VHAISWBECKEC
 *
 */
public class DeleteTransactionLogEntryCommandImpl
extends AbstractCommandImpl<java.lang.Void>
{
	private static final long serialVersionUID = 1L;
	private final Integer maxDaysAllowed;
	
	/**
    * @param maxDaysAllowed The number of days of storage allowed before TransactionLog records purged. 
	 */
	public DeleteTransactionLogEntryCommandImpl(Integer maxDaysAllowed)
    {
	    super();
	    this.maxDaysAllowed = maxDaysAllowed;
    }
	
	/**
	 * Delete TransactionLog records in storage for more than "maxDaysAllowed".
	 */
	@Override
	public java.lang.Void callSynchronouslyInTransactionContext()
	throws MethodException
	{
		getLogger().info("Synchronous Command [" + this.getClass().getSimpleName() + "] - processing.");
		
      getLogger().info( "deleteTransactionLogEntry - Anything Over " + maxDaysAllowed + " Days Old.");

		// The CompositeMethodException collects the exceptions from each
		// attempt. If some attempt succeeds then this instance is silently
		// discarded, else if all attempts fail this instance is thrown.
		CompositeMethodException compositeException = new CompositeMethodException();
		// keep track of whether we successfully completed the local and/or remote call(s).
		boolean success = false;
		
		try
		{
		   getCommandContext ().getTransactionLoggerService ().purgeLogEntries (maxDaysAllowed);
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
			getLogger().error(
					"Failed To Purge Log Entries - Anything Over " + maxDaysAllowed + " Days Old.\n" +
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
		sb.append(maxDaysAllowed == null ? "<null maxDaysAllowed>" : maxDaysAllowed.toString());

		return sb.toString();
    }
}
