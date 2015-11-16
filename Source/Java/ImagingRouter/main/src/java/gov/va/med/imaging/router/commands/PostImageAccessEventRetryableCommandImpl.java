/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Aug 24, 2010
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
package gov.va.med.imaging.router.commands;

import gov.va.med.RoutingToken;
import gov.va.med.RoutingTokenImpl;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.core.annotations.routerfacade.RouterCommandExecution;
import gov.va.med.imaging.core.interfaces.exceptions.CompositeMethodException;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractRetryableCommandImpl;
import gov.va.med.imaging.exchange.ImageAccessLogEvent;
import gov.va.med.imaging.exchange.business.util.ExchangeUtil;
import gov.va.med.imaging.router.facade.ImagingContext;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * @author vhaiswwerfej
 *
 */
@RouterCommandExecution(asynchronous=true, distributable=true)
public class PostImageAccessEventRetryableCommandImpl
extends AbstractRetryableCommandImpl<java.lang.Void>
{
	private static final long serialVersionUID = 1L;
	private final ImageAccessLogEvent event;
	
	/**
	 * 
	 * @param command
	 * @param router
	 * @param asynchronousMethodProcessor
	 * @param resultQueue
	 */
	public PostImageAccessEventRetryableCommandImpl(
		ImageAccessLogEvent event,
        int maximumRetries,
        long retryDelay)
    {
	    super(maximumRetries, retryDelay);
	    this.event = event;
    }
	
	private final static int defaultMaximumRetries = 3;
	private final static long defaultRetryDelay = 45000; // 45 seconds
	
	public PostImageAccessEventRetryableCommandImpl(ImageAccessLogEvent event)
	{
		this(event, defaultMaximumRetries, defaultRetryDelay);
	}

	/**
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public java.lang.Void callSynchronouslyInTransactionContext()
	throws MethodException
	{
		getLogger().info("Asynchronous Command [" + this.getClass().getSimpleName() + "] - processing.");
		
		TransactionContext xactionContext = TransactionContextFactory.get();
		getLogger().info("RouterImpl.logImageAccessEventRetryable (" + getEvent().getEventType().toString() + ") for " +
				"image [" + getEvent().getImageIen() + "] by ["
				//+ TransactionContextFactory.get().getTransactionId() + "].");
				//+ this.transactionId + "].");		
				+ xactionContext.getTransactionId() + "].");
		
		String siteNumberToLogEventTo = getEvent().getSiteNumber();

		// if the image requested is from the DOD, we need to log it in a VA
		// system, get the site number that the user was authenticated against and log
		// it to that system
		if (ExchangeUtil.isSiteDOD(siteNumberToLogEventTo))
		{
			// JMW 1/4/10 - was logging to realm site number if DoD image but that doesn't work if the
			// local site is station 200 (for CVIX) since station 200 doesn't have Imaging installed. So
			// use the event user site number, which at site VIX servers is the same as the VIX realm site number.
			// The user site number should be the authentication site which should always be a VA site number
			// if the image is DoD.  If the image is VA, then user site number will be 200 but that won't fall into
			// this situation
			siteNumberToLogEventTo = getEvent().getUserSiteNumber();			
		}
		RoutingToken routingToken = null;
		try
		{
			routingToken = RoutingTokenImpl.createVARadiologySite(siteNumberToLogEventTo);
		}
		catch (RoutingTokenFormatException x)
		{
			getLogger().error(x);
			throw new MethodException(x);
		}
		
		xactionContext.setServicedSource(routingToken.toRoutingTokenString());
		
		CompositeMethodException compositeException = new CompositeMethodException();
		// keep track of whether we successfully completed the remote call
		boolean success = false;
		try
		{
			ImagingContext.getRouter().postImageAccessEvent(
					routingToken, 
				getEvent());
			success = true;
		}
		catch(ConnectionException cX)
		{
			success = false;
			compositeException.addException(new MethodConnectionException(cX));
		}
		if(! success)
		{
			xactionContext.setErrorMessage(compositeException.getMessage());
			xactionContext.setExceptionClassName(compositeException.getClass().getSimpleName());
			
			throw compositeException;
		}
		
		return (java.lang.Void)null;
	}

	/**
	 * 
	 * @see gov.va.med.imaging.core.interfaces.router.commands.PostImageAccessEventCommand#getEvent()
	 */
	public ImageAccessLogEvent getEvent()
	{
		return this.event;
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
		sb.append(getEvent() == null ? "<null event>" : getEvent().toString());

		return sb.toString();
    }
}
