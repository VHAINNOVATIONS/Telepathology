/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 18, 2012
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
import gov.va.med.imaging.pathology.datasource.PathologyDataSourceSpi;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * @author VHAISWWERFEJ
 *
 */
public class GetPathologySiteTemplateCommandImpl
extends AbstractPathologyDataSourceCommandImpl<List<String>>
{
	private static final long serialVersionUID = 4315252017653286596L;
	
	private final RoutingToken routingToken;
	private final List<String> apSections;
	
	public GetPathologySiteTemplateCommandImpl(RoutingToken routingToken, List<String> apSections)
	{
		this.routingToken = routingToken;
		this.apSections = apSections;
	}

	public List<String> getApSections()
	{
		return apSections;
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
		return "getSiteTemplate";
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiMethodParameterTypes()
	 */
	@Override
	protected Class<?>[] getSpiMethodParameterTypes()
	{
		return new Class<?> [] {RoutingToken.class, List.class};
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiMethodParameters()
	 */
	@Override
	protected Object[] getSpiMethodParameters()
	{
		return new Object [] {getRoutingToken(), getApSections()};
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getCommandResult(gov.va.med.imaging.datasource.VersionableDataSourceSpi)
	 */
	@Override
	protected List<String> getCommandResult(PathologyDataSourceSpi spi)
			throws ConnectionException, MethodException
	{
		return spi.getSiteTemplate(getRoutingToken(), getApSections());
	}

	@Override
	protected List<String> postProcessResult(List<String> result)
	{
		TransactionContextFactory.get().setDataSourceEntriesReturned(result == null ? 0 : result.size());
		return result;
	}

}
