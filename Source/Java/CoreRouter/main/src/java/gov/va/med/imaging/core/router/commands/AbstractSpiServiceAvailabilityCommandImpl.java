/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 17, 2011
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
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractDataSourceExceptionHandler;
import gov.va.med.imaging.datasource.DataSourceProvider;
import gov.va.med.imaging.datasource.VersionableDataSourceSpi;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

import java.net.URL;
import java.util.List;

/**
 * This command determines if an SPI is available and there is a way to use it to communicate
 * with a location identified by a routing token.
 * 
 * @author VHAISWWERFEJ
 *
 */
public abstract class AbstractSpiServiceAvailabilityCommandImpl<E extends VersionableDataSourceSpi>
extends AbstractDataSourceExceptionHandler<Boolean>
{
	private static final long serialVersionUID = 4236272158963151131L;
	private final RoutingToken routingToken;
	private final Class<E> spiType;

	/**
	 * @param router
	 * @param accessibilityDate
	 * @param priority
	 * @param processingTargetCommencementDate
	 * @param processingDurationEstimate
	 */
	public AbstractSpiServiceAvailabilityCommandImpl(RoutingToken routingToken,
			Class<E> spiType)
	{
		super();
		this.routingToken = routingToken;
		this.spiType = spiType;
	}

	public RoutingToken getRoutingToken()
	{
		return this.routingToken;
	}

	public Class<E> getSpiType()
	{
		return spiType;
	}

	public String getSiteNumber()
	{
		return this.getRoutingToken().getRepositoryUniqueId();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AsynchronousCommandProcessor#callInTransactionContext()
	 */
	@Override
	public Boolean callSynchronouslyInTransactionContext()
	throws MethodException
	{
		TransactionContext context = TransactionContextFactory.get();
		getLogger().info("Determining if server '" + getSpiType().getSimpleName() + "' is available at '" + getRoutingToken().toRoutingTokenString() + "'.");
		ResolvedArtifactSource resolvedSite = getCommandContext().getResolvedArtifactSource(getRoutingToken());
		
		if(resolvedSite == null)
	    {
	    	getLogger().error("Unable to find site with site number '" + getSiteNumber() + 
	    		"', verify this is a valid VA site number. Cannot access data from this site.");
	    	return false;
	    }
        
		context.setServicedSource( getRoutingToken().toRoutingTokenString() );
		List<URL> metadataUrls = resolvedSite.getMetadataUrls();
		if(metadataUrls == null || metadataUrls.isEmpty())
		{
			getLogger().error("The site '" + getSiteNumber() + "' has no available interface URLs.\n" +
					"Please check that the protocol handlers are properly installed and that the \n" +
					"protocol preferences for the site specify valid protocols.");
			return false;
		}
		Exception lastException = null;
		// try each of the configured protocols in turn
		TransactionContext transactionContext = TransactionContextFactory.get();
		for(URL url : metadataUrls )
		{
			try
			{
				E spiResolver = getSpi(resolvedSite, url.getProtocol());
				if(spiResolver != null)
				{
					transactionContext.setDatasourceProtocol(url.getProtocol());
					getLogger().info("Able to use SPI '" + getSpiType().getSimpleName() + "' to access '" +  getRoutingToken().toRoutingTokenString() + "'.");
					transactionContext.addDebugInformation("Able to use SPI '" + getSpiType().getSimpleName() + "' to access '" +  getRoutingToken().toRoutingTokenString() + "'.");
					return true;					
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
				return false;
			}
		}
		getLogger().info("Cannot use SPI '" + getSpiType().getSimpleName() + "' to access '" +  getRoutingToken().toRoutingTokenString() + "'.");
		transactionContext.addDebugInformation("Cannot use SPI '" + getSpiType().getSimpleName() + "' to access '" +  getRoutingToken().toRoutingTokenString() + "'.");
		return false;
	}

	private E getSpi(ResolvedArtifactSource resolvedSite, String protocol)
	throws ConnectionException
	{
		if(canGenerateNewToken())
			return getProvider().createVersionableDataSource(getSpiType(), resolvedSite, protocol, this);
		else
		{
			DataSourceProvider provider = getProvider();
			E s = 
				provider.createVersionableDataSource(getSpiType(), resolvedSite, protocol);
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

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		final AbstractSpiServiceAvailabilityCommandImpl<E> other = (AbstractSpiServiceAvailabilityCommandImpl<E>) obj;
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
