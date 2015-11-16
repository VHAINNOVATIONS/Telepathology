/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 26, 2012
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
package gov.va.med.imaging.router.commands;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.annotations.routerfacade.RouterCommandExecution;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractRetryableCommandImpl;
import gov.va.med.imaging.exchange.ImagingLogEvent;
import gov.va.med.imaging.router.facade.ImagingContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * @author VHAISWWERFEJ
 *
 */
@RouterCommandExecution(asynchronous=true, distributable=true)
public class PostImagingLogEventRetryableCommandImpl
extends AbstractRetryableCommandImpl<java.lang.Void>
{
	private static final long serialVersionUID = 4304522895767470223L;

	private final ImagingLogEvent imagingLogEvent;
	
	private final static int defaultMaximumRetries = 3;
	private final static long defaultRetryDelay = 45000; // 45 seconds
	
	/**
	 * @param maximumRetries
	 * @param retryDelay
	 */
	public PostImagingLogEventRetryableCommandImpl(ImagingLogEvent imagingLogEvent,
			int maximumRetries,
			long retryDelay)
	{
		super(maximumRetries, retryDelay);
		this.imagingLogEvent = imagingLogEvent;
	}

	public PostImagingLogEventRetryableCommandImpl(ImagingLogEvent imagingLogEvent)
	{
		this(imagingLogEvent, defaultMaximumRetries, defaultRetryDelay);
	}
	
	public ImagingLogEvent getImagingLogEvent()
	{
		return imagingLogEvent;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#callSynchronouslyInTransactionContext()
	 */
	@Override
	public Void callSynchronouslyInTransactionContext() 
	throws MethodException, ConnectionException
	{
		RoutingToken routingToken = getImagingLogEvent().getRoutingTokenToLogTo();
		TransactionContextFactory.get().setServicedSource(routingToken.toRoutingTokenString());
		ImagingContext.getRouter().postImagingLogEvent(getImagingLogEvent().getRoutingTokenToLogTo(), getImagingLogEvent());
		return (java.lang.Void)null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#parameterToString()
	 */
	@Override
	protected String parameterToString()
	{
		return getImagingLogEvent().toString();
	}

}
