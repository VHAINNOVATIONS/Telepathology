/**
 * 
 */
package gov.va.med.imaging.core;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.SortedSet;
import java.util.TreeSet;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.Router;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.router.CommandContext;
import gov.va.med.imaging.core.interfaces.router.CommandFactory;
import gov.va.med.imaging.datasource.DataSourceProvider;
import gov.va.med.imaging.datasource.SiteResolutionDataSourceSpi;
import gov.va.med.imaging.datasource.TransactionLoggerDataSourceSpi;
import gov.va.med.imaging.datasource.VersionableDataSourceSpi;
import gov.va.med.imaging.exchange.InterfaceURLs;
import gov.va.med.imaging.exchange.business.ResolvedSite;

/**
 * @author vhaiswbeckec
 *
 */
public class MockCommandContext 
implements CommandContext
{
	private static CommandFactory commandFactory;
	private static DataSourceProvider dataSourceProvider;
	private static Router router;
	private static SiteResolutionDataSourceSpi siteResolver;
	private static TransactionLoggerDataSourceSpi transactionLoggerService;
	
	private static boolean cachingEnabled = true;

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.router.CommandContext#getCommandFactory()
	 */
	@Override
	public CommandFactory getCommandFactory()
	{
		return commandFactory;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.router.CommandContext#getProvider()
	 */
	@Override
	public DataSourceProvider getProvider()
	{
		return dataSourceProvider;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.router.CommandContext#getRouter()
	 */
	@Override
	public Router getRouter()
	{
		return router;
	}

	@Override
	public ResolvedSite getLocalSite()
	{
		try
		{
			return new MockResolvedSite();
		}
		catch (MalformedURLException x)
		{
			x.printStackTrace();
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.router.CommandContext#getSiteResolver()
	 */
	@Override
	public SiteResolutionDataSourceSpi getSiteResolver()
	{
		return siteResolver;
	}

	@Override
	public ResolvedArtifactSource getResolvedArtifactSource(RoutingToken routingToken,
		Class spi, Method method, Object[] parameters) 
	throws MethodException
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.router.CommandContext#isCachingEnabled()
	 */
	@Override
	public boolean isCachingEnabled()
	{
		return MockCommandContext.cachingEnabled;
	}

	/**
	 * @param cachingEnabled the cachingEnabled to set
	 */
	protected static void setCachingEnabled(boolean cachingEnabled)
	{
		MockCommandContext.cachingEnabled = cachingEnabled;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.router.CommandContext#getTransactionLoggerService()
	 */
	@Override
	public TransactionLoggerDataSourceSpi getTransactionLoggerService() 
	{
		return transactionLoggerService;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.router.CommandContext#getResolvedArtifactSource(gov.va.med.RoutingToken)
	 */
	@Override
	public ResolvedArtifactSource getResolvedArtifactSource(RoutingToken routingToken) throws MethodException
	{
		try
		{
			return new MockResolvedSite();
		}
		catch (MalformedURLException x)
		{
			throw new MethodException(x);
		}
	}

}
