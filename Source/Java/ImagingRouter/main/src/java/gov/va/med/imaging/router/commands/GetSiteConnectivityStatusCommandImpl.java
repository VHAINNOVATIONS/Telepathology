/**
 * 
 */
package gov.va.med.imaging.router.commands;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractDataSourceExceptionHandler;
import gov.va.med.imaging.datasource.DataSourceProvider;
import gov.va.med.imaging.datasource.StudyGraphDataSourceSpi;
import gov.va.med.imaging.exchange.enums.SiteConnectivityStatus;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import java.net.URL;
import java.util.List;

/**
 * @author vhaiswbeckec
 *
 */
public class GetSiteConnectivityStatusCommandImpl
extends AbstractDataSourceExceptionHandler<SiteConnectivityStatus>
{
	private static final long serialVersionUID = 3845559383784181438L;
	private final RoutingToken routingToken;

	/**
	 * @param router
	 * @param accessibilityDate
	 * @param priority
	 * @param processingTargetCommencementDate
	 * @param processingDurationEstimate
	 */
	public GetSiteConnectivityStatusCommandImpl(RoutingToken routingToken)
	{
		super();
		this.routingToken = routingToken;
	}

	public RoutingToken getRoutingToken()
	{
		return this.routingToken;
	}

	public String getSiteNumber()
	{
		return this.getRoutingToken().getRepositoryUniqueId();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AsynchronousCommandProcessor#callInTransactionContext()
	 */
	@Override
	public SiteConnectivityStatus callSynchronouslyInTransactionContext()
	throws MethodException
	{
		TransactionContext context = TransactionContextFactory.get();
		getLogger().info( "isSiteAvailable - Transaction ID [" + context.getTransactionId() + "], For site '" + getSiteNumber() + "'." );
		ResolvedArtifactSource resolvedSite = getCommandContext().getResolvedArtifactSource(getRoutingToken());
		
		if(resolvedSite == null)
	    {
	    	getLogger().error("Unable to find site with site number '" + getSiteNumber() + 
	    		"', verify this is a valid VA site number. Cannot retrieve data from this site.");
	    	return SiteConnectivityStatus.VIX_UNAVAILABLE;
	    }
        
		context.setServicedSource( getRoutingToken().toRoutingTokenString() );
		List<URL> metadataUrls = resolvedSite.getMetadataUrls();
		if(metadataUrls == null || metadataUrls.isEmpty())
		{
			getLogger().error("The site '" + getSiteNumber() + "' has no available interface URLs.\n" +
					"Please check that the protocol handlers are properly installed and that the \n" +
					"protocol preferences for the site specify valid protocols.");
			return SiteConnectivityStatus.VIX_UNAVAILABLE;
		}
		Exception lastException = null;
		// try each of the configured protocols in turn
		for(URL url : metadataUrls )
		{
			try
			{
				StudyGraphDataSourceSpi studyResolver = getSpi(resolvedSite, url.getProtocol());
				if(studyResolver != null)
				{
					TransactionContextFactory.get().setDatasourceProtocol(url.getProtocol());
					return SiteConnectivityStatus.VIX_READY;					
				}
			}
			catch(ConnectionException cX)
			{
				getLogger().error(
						"Failed to contact site'" + getSiteNumber() + "' using '" + url.toExternalForm() + "'.\n" +
						"Exception details follow.", 
						cX);
				lastException = cX;
			}
		}
		if(lastException != null)
		{
			if(lastException.getClass() == ConnectionException.class)
			{
				getLogger().info("Unable to contact data source for site '" + getSiteNumber() + "' using ViX, returning Datasource Unavailable");
				return SiteConnectivityStatus.DATASOURCE_UNAVAILABLE;
			}
		}
		
		return SiteConnectivityStatus.VIX_UNAVAILABLE;
	}

	private StudyGraphDataSourceSpi getSpi(ResolvedArtifactSource resolvedSite, String protocol)
	throws ConnectionException
	{
		if(canGenerateNewToken())
			return getProvider().createVersionableDataSource(StudyGraphDataSourceSpi.class, resolvedSite, protocol, this);
		else
		{
			DataSourceProvider provider = getProvider();
			StudyGraphDataSourceSpi s = provider.createVersionableDataSource(StudyGraphDataSourceSpi.class, resolvedSite, protocol);
			return s;
			//return getProvider().createDataSource(getSpiClass(), url, resolvedSite);
		}
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AsynchronousCommandProcessor#parameterToString()
	 */
	@Override
	protected String parameterToString()
	{
		return this.getRoutingToken().toString();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
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
		final GetSiteConnectivityStatusCommandImpl other = (GetSiteConnectivityStatusCommandImpl) obj;
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
