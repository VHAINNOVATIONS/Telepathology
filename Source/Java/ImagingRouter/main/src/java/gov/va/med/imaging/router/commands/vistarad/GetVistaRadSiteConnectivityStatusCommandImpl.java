/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 14, 2009
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
package gov.va.med.imaging.router.commands.vistarad;

import java.net.URL;
import java.util.List;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractDataSourceExceptionHandler;
import gov.va.med.imaging.datasource.DataSourceProvider;
import gov.va.med.imaging.datasource.VistaRadDataSourceSpi;
import gov.va.med.imaging.exchange.enums.SiteConnectivityStatus;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * Command that gets the connectivity status of a remote site.  This command is functionaly the same
 * as the GetSiteConnectivityStatusCommandImpl except this one creates a VistARad SPI method
 * instead of a study graph SPI.  Creating the SPI makes isVersionCompatible which tests to see
 * if the VIX can communicate with the site.
 * 
 * @author vhaiswwerfej
 *
 */
public class GetVistaRadSiteConnectivityStatusCommandImpl 
extends AbstractDataSourceExceptionHandler<SiteConnectivityStatus> 
{
	private static final long serialVersionUID = -5300845279529252877L;
	
	private final RoutingToken routingToken;

	/**
	 * @param router
	 * @param accessibilityDate
	 * @param priority
	 * @param processingTargetCommencementDate
	 * @param processingDurationEstimate
	 */
	public GetVistaRadSiteConnectivityStatusCommandImpl(RoutingToken routingToken)
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
		return getRoutingToken().getRepositoryUniqueId();
	}


	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#callSynchronouslyInTransactionContext()
	 */
	@Override
	public SiteConnectivityStatus callSynchronouslyInTransactionContext()
	throws MethodException, ConnectionException 
	{
		TransactionContext context = TransactionContextFactory.get();
		getLogger().info( "isVistaRadSiteAvailable - Transaction ID [" + context.getTransactionId() + "], For site '" + getSiteNumber() + "'." );
		ResolvedArtifactSource resolvedArtifactSource;
	    resolvedArtifactSource = getCommandContext().getResolvedArtifactSource(getRoutingToken());
	    
	    if(resolvedArtifactSource == null)
	    {
	    	getLogger().error("Unable to find site with site number '" + getSiteNumber() + 
	    		"', verify this is a valid VA site number. Cannot retrieve data from this site.");
	    	return SiteConnectivityStatus.VIX_UNAVAILABLE;
	    }
        
		context.setServicedSource(getRoutingToken().toRoutingTokenString());
		List<URL> resolvedUrls = resolvedArtifactSource.getMetadataUrls();
		if(resolvedUrls == null || resolvedUrls.isEmpty())
		{
			getLogger().error("The site '" + getSiteNumber() + "' has no available interface URLs.\n" +
					"Please check that the protocol handlers are properly installed and that the \n" +
					"protocol preferences for the site specify valid protocols.");
			return SiteConnectivityStatus.VIX_UNAVAILABLE;
		}
		Exception lastException = null;
		// try each of the configured protocols in turn
		for(URL url : resolvedUrls )
		{
			try
			{
				VistaRadDataSourceSpi vistaRadResolver = getSpi(resolvedArtifactSource, url.getProtocol());
				if(vistaRadResolver != null)
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

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#parameterToString()
	 */
	@Override
	protected String parameterToString() 
	{
		return getRoutingToken().toString();
	}
	
	private VistaRadDataSourceSpi getSpi(ResolvedArtifactSource resolvedArtifactSource, String protocol)
	throws ConnectionException
	{
		if(canGenerateNewToken())
			return getProvider().createVersionableDataSource(VistaRadDataSourceSpi.class, resolvedArtifactSource, protocol, this);
		else
		{
			DataSourceProvider provider = getProvider();
			VistaRadDataSourceSpi s = provider.createVersionableDataSource(VistaRadDataSourceSpi.class, resolvedArtifactSource, protocol);
			return s;
			//return getProvider().createDataSource(getSpiClass(), url, resolvedSite);
		}
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
		final GetVistaRadSiteConnectivityStatusCommandImpl other = (GetVistaRadSiteConnectivityStatusCommandImpl) obj;
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
