/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Dec 21, 2011
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
package gov.va.med.imaging.core.router.commands;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl;
import gov.va.med.imaging.datasource.SiteDataSourceSpi;
import gov.va.med.imaging.exchange.business.WelcomeMessage;

/**
 * @author VHAISWWERFEJ
 *
 */
public class GetWelcomeMessageBySiteCommandImpl
extends AbstractDataSourceCommandImpl<WelcomeMessage, SiteDataSourceSpi>
{
	private static final long serialVersionUID = -7005326028426585923L;

	private final RoutingToken routingToken;
	
	private static final String SPI_METHOD_NAME = "getWelcomeMessage";
	
	public GetWelcomeMessageBySiteCommandImpl(RoutingToken routingToken)
	{
		this.routingToken = routingToken;
	}

	@Override
	public RoutingToken getRoutingToken()
	{
		return routingToken;
	}

	@Override
	protected Class<SiteDataSourceSpi> getSpiClass()
	{
		return SiteDataSourceSpi.class;
	}

	@Override
	protected String getSpiMethodName()
	{
		return SPI_METHOD_NAME;
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes()
	{
		return new Class<?>[]{RoutingToken.class};
	}

	@Override
	protected Object[] getSpiMethodParameters()
	{
		return new Object[]{getRoutingToken()};
	}

	@Override
	protected String getSiteNumber()
	{
		return getRoutingToken().getRepositoryUniqueId();
	}

	@Override
	protected WelcomeMessage getCommandResult(SiteDataSourceSpi spi)
			throws ConnectionException, MethodException
	{
		return spi.getWelcomeMessage(getRoutingToken());
	}

}
