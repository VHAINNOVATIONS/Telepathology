/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jan 25, 2012
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

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractCommandImpl;
import gov.va.med.imaging.core.router.facade.InternalContext;
import gov.va.med.imaging.exchange.business.WelcomeMessage;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * This is a version of the GetWelcomeMessageCommand that caches the result of the welcome message in memory. This command will check
 * the cache before calling the data source command to get the welcome message.
 * 
 * @author vhaiswwerfej
 *
 */
public class GetCachedWelcomeMessageCommandImpl
extends AbstractCommandImpl<WelcomeMessage>
{
	private static final long serialVersionUID = 7384181991895608719L;
	
	private final RoutingToken routingToken;

	public GetCachedWelcomeMessageCommandImpl(RoutingToken routingToken)
	{
		super();
		this.routingToken = routingToken;
	}

	/**
	 * @return the routingToken
	 */
	public RoutingToken getRoutingToken()
	{
		return routingToken;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#callSynchronouslyInTransactionContext()
	 */
	@Override
	public WelcomeMessage callSynchronouslyInTransactionContext()
	throws MethodException, ConnectionException
	{
		// check cache for welcome message for routing token
		TransactionContext transactionContext = TransactionContextFactory.get();
		WelcomeMessage result = WelcomeMessageCache.getCachedWelcomeMessage(getRoutingToken());
		if(result != null)
		{
			transactionContext.setItemCached(true);
			getLogger().info("Welcome message for routing token '" + getRoutingToken().toRoutingTokenString() + "' found in cache.");
			return result;
		}
		else
		{
			transactionContext.setItemCached(false);
			getLogger().info("Welcome message for routing token '" + getRoutingToken().toRoutingTokenString() + "' NOT found in cache.");
			result = InternalContext.getRouter().getWelcomeMessageFromSite(getRoutingToken());
			WelcomeMessageCache.cacheWelcomeMessage(getRoutingToken(), result);
			return result;
		}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof GetCachedWelcomeMessageCommandImpl)
		{
			GetCachedWelcomeMessageCommandImpl that = (GetCachedWelcomeMessageCommandImpl)obj;
			return this.getRoutingToken().equals(that.getRoutingToken());
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#parameterToString()
	 */
	@Override
	protected String parameterToString()
	{
		return routingToken.toRoutingTokenString();
	}
}
