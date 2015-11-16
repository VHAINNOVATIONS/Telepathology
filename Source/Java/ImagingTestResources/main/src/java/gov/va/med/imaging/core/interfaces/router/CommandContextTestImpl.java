/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 1, 2011
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
package gov.va.med.imaging.core.interfaces.router;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.Router;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.DataSourceProvider;
import gov.va.med.imaging.datasource.SiteResolutionDataSourceSpi;
import gov.va.med.imaging.datasource.TransactionLoggerDataSourceSpi;
import gov.va.med.imaging.datasource.VersionableDataSourceSpi;
import gov.va.med.imaging.exchange.business.ResolvedSite;

import java.lang.reflect.Method;

/**
 * @author vhaiswwerfej
 *
 */
public class CommandContextTestImpl 
implements CommandContext
{
	
	private final SiteResolutionDataSourceSpi siteResolution;
	
	public CommandContextTestImpl(SiteResolutionDataSourceSpi siteResolution)
	{
		this.siteResolution = siteResolution;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.router.CommandContext#getCommandFactory()
	 */
	@Override
	public CommandFactory getCommandFactory()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.router.CommandContext#getLocalSite()
	 */
	@Override
	public ResolvedSite getLocalSite()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.router.CommandContext#getProvider()
	 */
	@Override
	public DataSourceProvider getProvider()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.router.CommandContext#getResolvedArtifactSource(gov.va.med.RoutingToken)
	 */
	@Override
	public ResolvedArtifactSource getResolvedArtifactSource(
			RoutingToken routingToken) throws MethodException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.router.CommandContext#getResolvedArtifactSource(gov.va.med.RoutingToken, java.lang.Class, java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public ResolvedArtifactSource getResolvedArtifactSource(
			RoutingToken routingToken,
			Class<? extends VersionableDataSourceSpi> spi, Method method,
			Object[] parameters) throws MethodException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.router.CommandContext#getRouter()
	 */
	@Override
	public Router getRouter()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.router.CommandContext#getSiteResolver()
	 */
	@Override
	public SiteResolutionDataSourceSpi getSiteResolver()
	{
		return siteResolution;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.router.CommandContext#getTransactionLoggerService()
	 */
	@Override
	public TransactionLoggerDataSourceSpi getTransactionLoggerService()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.router.CommandContext#isCachingEnabled()
	 */
	@Override
	public boolean isCachingEnabled()
	{
		// TODO Auto-generated method stub
		return false;
	}

}
