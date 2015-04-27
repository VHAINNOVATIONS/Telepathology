/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 13, 2012
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

import java.util.List;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.pathology.PathologyCase;
import gov.va.med.imaging.pathology.datasource.PathologyDataSourceSpi;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * @author VHAISWWERFEJ
 *
 */
public class GetPathologyCasesCommandImpl
extends AbstractPathologyDataSourceCommandImpl<List<PathologyCase>>
{
	private static final long serialVersionUID = -7872560402056556739L;
	
	private final RoutingToken routingToken;
	private final boolean released; 
	private final int days;
	private final String requestingSiteId;
	
	public GetPathologyCasesCommandImpl(RoutingToken routingToken,
			boolean released, int days, String requestingSiteId)
	{
		super();
		this.routingToken = routingToken;
		this.released = released;
		this.days = days;
		this.requestingSiteId = requestingSiteId;
	}
	
	public GetPathologyCasesCommandImpl(RoutingToken routingToken,
			boolean released, int days)
	{
		this(routingToken, released, days, null);
	}

	@Override
	protected List<PathologyCase> postProcessResult(List<PathologyCase> result)
	{
		TransactionContextFactory.get().setDataSourceEntriesReturned(result == null ? 0 : result.size());
		return result;
	}

	public String getRequestingSiteId()
	{
		return requestingSiteId;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getRoutingToken()
	 */
	@Override
	public RoutingToken getRoutingToken()
	{
		return routingToken;
	}

	public boolean isReleased()
	{
		return released;
	}

	public int getDays()
	{
		return days;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiMethodName()
	 */
	@Override
	protected String getSpiMethodName()
	{
		return "getCases";
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiMethodParameterTypes()
	 */
	@Override
	protected Class<?>[] getSpiMethodParameterTypes()
	{
		return new Class<?>[]{RoutingToken.class, boolean.class, int.class, String.class};
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiMethodParameters()
	 */
	@Override
	protected Object[] getSpiMethodParameters()
	{
		return new Object[] {getRoutingToken(), isReleased(), getDays(), getRequestingSiteId()};
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getCommandResult(gov.va.med.imaging.datasource.VersionableDataSourceSpi)
	 */
	@Override
	protected List<PathologyCase> getCommandResult(PathologyDataSourceSpi spi)
			throws ConnectionException, MethodException
	{
		return spi.getCases(getRoutingToken(), isReleased(), getDays(), getRequestingSiteId());
	}

}
