/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Oct 3, 2008
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
package gov.va.med.imaging.router.commands;

import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.channels.ByteStreamPump;
import gov.va.med.imaging.core.annotations.routerfacade.RouterCommandExecution;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractCommandImpl;
import gov.va.med.imaging.exchange.business.TransactionContextLogEntrySnapshot;
import gov.va.med.imaging.router.facade.ImagingContext;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A Command implementation for initiating a prefetch of a patient identification image.
 * The result of successful processing is a void, represented by the
 * "Object" class used as the generic type.  The result will always
 * be a null. 
 * 
 * @author VHAISWBECKEC
 *
 */
@RouterCommandExecution(asynchronous=true, distributable=true)
public class PrefetchPatientIdentificationImageCommandImpl 
extends AbstractCommandImpl<java.lang.Void>
{
	private static final long serialVersionUID = 1L;
	
	private final RoutingToken routingToken;
	private final PatientIdentifier patientIdentifier;

	/**
	 * @param command
	 * @param router
	 * @param asynchronousMethodProcessor
	 */
	public PrefetchPatientIdentificationImageCommandImpl(
		RoutingToken routingToken,
		PatientIdentifier patientIdentifier)
	{
		super();
		this.routingToken = routingToken;
		this.patientIdentifier = patientIdentifier;
	}

	public RoutingToken getRoutingToken()
	{
		return this.routingToken;
	}

	public String getSiteNumber()
    {
    	return getRoutingToken().getRepositoryUniqueId();
    }

	public PatientIdentifier getPatientIdentifier()
	{
		return patientIdentifier;
	}

	/**
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#callSynchronouslyInTransactionContext()
	 */
	@Override
	public java.lang.Void callSynchronouslyInTransactionContext() 
	throws MethodException
	{
		TransactionContext xactionCtx = TransactionContextFactory.get();
		
		getLogger().info("Asynchronous Command [" + this.getClass().getSimpleName() + "] is being processed.");
		OutputStream bitBucket = new NullOutputStream();
		
		xactionCtx.setChildRequestType("getPatientStudyList");
		InputStream inStream = getPatientIdentificationImage(getPatientIdentifier(), getSiteNumber());
		ByteStreamPump pump = ByteStreamPump.getByteStreamPump();
		try
		{
			pump.xfer(inStream, bitBucket);
		} 
		catch (Exception x)
		{
			throw new MethodException(x);
		}
		try		
		{
			getCommandContext().getTransactionLoggerService().writeLogEntry(new TransactionContextLogEntrySnapshot(xactionCtx));
		}
		catch(Exception x)
		{
			throw new MethodException(x);
		}
      
		getLogger().info("Asynchronous Command [" + this.getClass().getSimpleName() + "] - patient ID image stream obtained.");
		
		return (java.lang.Void)null;
	}

    public InputStream getPatientIdentificationImage(PatientIdentifier patientIdentifier, String siteNumber) 
    throws MethodException
    {
		TransactionContext transactionContext = TransactionContextFactory.get();
		transactionContext.setServicedSource(getRoutingToken().toRoutingTokenString());
		
		getLogger().info( "getPatientIdentificationImage - Transaction ID [" + transactionContext.getTransactionId() + "] from site [" + siteNumber + "] for patient [" + patientIdentifier + "].");
		try
		{
			InputStream patientIdImageStream = 
				ImagingContext.getRouter().getPatientIdentificationImage(
					getRoutingToken(), 
					patientIdentifier );
			getLogger().info( ((patientIdImageStream == null) ? "Did not find ID image" : "Found ID image") + 
					" for patient '" + patientIdentifier + "'.");
			
			return patientIdImageStream;
		}
		catch(ConnectionException cX)
		{
			throw new MethodConnectionException(cX);
		}
	}

	@Override
    protected String parameterToString()
    {
		StringBuffer sb = new StringBuffer();
		sb.append(getSiteNumber() == null ? "<null site number>" : getSiteNumber());
		sb.append(getPatientIdentifier() == null ? "<null patient id>" : getPatientIdentifier());

		return sb.toString();
    }
	
	private class NullOutputStream 
	extends OutputStream
	{
		@Override
		 public void write(int b) {}
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.patientIdentifier == null) ? 0 : this.patientIdentifier.hashCode());
		result = prime * result + ((this.routingToken == null) ? 0 : this.routingToken.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		final PrefetchPatientIdentificationImageCommandImpl other = (PrefetchPatientIdentificationImageCommandImpl) obj;
		if (this.patientIdentifier == null)
		{
			if (other.patientIdentifier != null)
				return false;
		}
		else if (!this.patientIdentifier.equals(other.patientIdentifier))
			return false;
		if (this.routingToken == null)
		{
			if (other.routingToken != null)
				return false;
		}
		else if (!this.routingToken.equals(other.routingToken))
			return false;
		return true;
	}


}
