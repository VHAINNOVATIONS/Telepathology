/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jul 10, 2012
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
package gov.va.med.imaging.pathology.commands;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.pathology.AbstractPathologySite;
import gov.va.med.imaging.pathology.datasource.PathologyDataSourceSpi;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

import java.util.List;

/**
 * @author VHAISWWERFEJ
 *
 */
public abstract class AbstractPathologyGetSitesCommandImpl
extends AbstractPathologyDataSourceCommandImpl<List<AbstractPathologySite>>
{
	private static final long serialVersionUID = -4058365522585841819L;
	
	private final RoutingToken routingToken;
	private final boolean reading;

	public AbstractPathologyGetSitesCommandImpl(RoutingToken routingToken, boolean reading)
	{
		super();
		this.routingToken = routingToken;
		this.reading = reading;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getRoutingToken()
	 */
	@Override
	public RoutingToken getRoutingToken()
	{
		return routingToken;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiMethodName()
	 */
	@Override
	protected String getSpiMethodName()
	{
		return "getSites";
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiMethodParameterTypes()
	 */
	@Override
	protected Class<?>[] getSpiMethodParameterTypes()
	{
		return new Class<?> [] {RoutingToken.class, boolean.class};
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiMethodParameters()
	 */
	@Override
	protected Object[] getSpiMethodParameters()
	{
		return new Object [] {getRoutingToken(), isReading()};
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getCommandResult(gov.va.med.imaging.datasource.VersionableDataSourceSpi)
	 */
	@Override
	protected List<AbstractPathologySite> getCommandResult(
			PathologyDataSourceSpi spi) 
	throws ConnectionException, MethodException
	{
		return spi.getSites(getRoutingToken(), isReading());
	}

	public boolean isReading()
	{
		return reading;
	}

	@Override
	protected List<AbstractPathologySite> postProcessResult(
			List<AbstractPathologySite> result)
	{
		TransactionContextFactory.get().setDataSourceEntriesReturned(result == null ? 0 : result.size());
		return result;
	}
}
