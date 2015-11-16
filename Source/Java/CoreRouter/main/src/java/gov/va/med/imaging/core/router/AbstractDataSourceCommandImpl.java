/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Sep 24, 2008
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author VHAISWBECKEC
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */
package gov.va.med.imaging.core.router;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.GUID;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.*;
import gov.va.med.imaging.core.interfaces.router.AsynchronousCommandResult;
import gov.va.med.imaging.core.interfaces.router.Command;
import gov.va.med.imaging.core.router.queue.ScheduledPriorityQueueElement;
import gov.va.med.imaging.datasource.DataSourceProvider;
import gov.va.med.imaging.datasource.VersionableDataSourceSpi;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * An abstract command implementation designed for use as a base class for commands that
 * directly call a datasource to do their work. This base class is most likely not suitable
 * for extension by commands that call other commands.
 * 
 * Derived classes need to implement the getResolvedSite and getCommandResult methods.
 * 
 * @author VHAISWLOUTHJ
 *
 */
public abstract class AbstractDataSourceCommandImpl<R, S extends VersionableDataSourceSpi>
extends AbstractDataSourceExceptionHandler<R>
implements Command<R>, Callable<AsynchronousCommandResult<R>>, ScheduledPriorityQueueElement, Serializable
{
	// 
	private static final long serialVersionUID = -853297943559433190L;
	// these properties are all associated with asynchronous processing
	protected Date accessibilityDate;
	//protected URL url = null;
	protected ResolvedArtifactSource resolvedArtifactSource;
	
	private final static int maximumSecurityTokenRetryAttemptCount = 3;
	
	/**
	 * Returns the RoutingToken realization that will be used to determine the
	 * SPI implementation to direct the request to.
	 * NOTE: the fact that the return value from this method is a single instance,
	 * not a collection, implies that a DataSourceCommand is targeted to a single
	 * SPI instance.
	 * 
	 * @return
	 */
	public abstract RoutingToken getRoutingToken();

	/**
	 * 
	 * @return
	 * @throws MethodException
	 * @throws ApplicationConfigurationException
	 */
	private List<URL> getMetadataSourceURLs() 
	throws MethodException, ApplicationConfigurationException
	{
		// 01May2008 CTB
		// Getting the resolvedSite now requires that we use the internal
		// getSite method that passes the method in the SPI to be called
		// and the method parameters.  This is to allow Redirection SPI
		// implementations to inspect the call and redirect it.
		resolvedArtifactSource = null;
		try
		{
			resolvedArtifactSource = getResolvedArtifactSource();
		}
		catch(Throwable t)
		{
			getLogger().error("Exception [" + t.getMessage() + "] getting site number with redirect handling, using default site resolution.", t);
			resolvedArtifactSource = getCommandContext().getResolvedArtifactSource(getRoutingToken());
		}
		
		if(resolvedArtifactSource == null)
		{
			throw new ApplicationConfigurationException("Cannot find resolved artifact source for site '" + getRoutingToken().toRoutingTokenString() + "'.");
		}
		
		List<URL> resolvedUrls = resolvedArtifactSource.getMetadataUrls();
		getLogger().info("Site number '" + getSiteNumber() + "' " +
			(resolvedArtifactSource == null ? "HAS NOT" : "HAS") + " been resolved and has " +
			(resolvedUrls == null || resolvedUrls.isEmpty() ? "0" : resolvedUrls.size()) +
			" available metadata URLs to try.");
		if(resolvedUrls == null || resolvedUrls.isEmpty())
		{
			throw new ApplicationConfigurationException(
					"The site '" + getSiteNumber() + "' has no available interface URLs.\n" +
					"Please check that the protocol handlers are properly installed and that the \n" +
					"protocol preferences for the site specify valid protocols."
			);
		}
		return resolvedUrls;
	}
	
	@Override
	public R callSynchronouslyInTransactionContext()
	throws MethodException 
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		getLogger().info(getSpiMethodName() + " for transaction '" + transactionContext.getTransactionId() + "'. to routingToken '" + getRoutingToken().toRoutingTokenString() + "'.");
		setTransactionContextFields();
		try 
		{
			// there may be multiple protocols available to contact the artifact source
			// the list of resolved URLS will be in the preferred order
			List<URL> metadataUrls = getMetadataSourceURLs();
			getLogger().info("Referenced site with  " +
				metadataUrls == null ? "<null>" : metadataUrls.size() +
				" URLs to attempt communication on.");
			CompositeMethodException compositeException = new CompositeMethodException();

			// try each of the configured protocols in turn
			for(URL metadataUrl : metadataUrls )
			{
				getLogger().info("Attempting data source creation to [" +
					metadataUrl == null ? "<null>" : metadataUrl.toString() + "].");
				String protocol = metadataUrl.getProtocol();
				try
				{
					S spi = getDataSourceSpi(protocol);
					if(spi != null)
					{
						getLogger().info("Data source of type " + spi.getClass().getSimpleName() + " created using [" +
							metadataUrl == null ? "<null>" : metadataUrl.toString() + "].");
						transactionContext.setDatasourceProtocol(protocol);
						R result = getCommandResultHandleCredentialsException(metadataUrl, spi);
						return postProcessResult(result);
					}
					else
						getLogger().info("Failed to create data source for [" +
							metadataUrl == null ? "<null>" : metadataUrl.toString() + 
							"].");
						
					/*
					//R result = getCommandResultHandleCredentialsException(interfaceUrls.getMetadataUrl());
					
					if(result != null)
						return postProcessResult(result);
					else
						// if a null result is returned this means the URL used could not create a data source for the site
						throw new ConnectionException("No data source available for method '" + getSpiMethodName() + 
								"' on URL '" + interfaceUrls.getMetadataUrl().toExternalForm() + "'.");
								*/
				}
				catch(UnsupportedOperationException uoX)
				{
					compositeException.addException(metadataUrl, new MethodConnectionException(new ConnectionException(uoX)));
					getLogger().error(
							"Failed to contact site'" + getSiteNumber() + "' using '" + metadataUrl.toExternalForm() + "'.\n" +
							"Exception details follow.", 
							uoX);
				}
				catch(ConnectionException cX)
				{
					compositeException.addException(metadataUrl, new MethodConnectionException(cX));
					getLogger().error(
							"Failed to contact site'" + getSiteNumber() + "' using '" + metadataUrl.toExternalForm() + "': " + (cX == null ? "" : cX.getMessage()));
				}
			}
			
			// if we get here then we were unable to connect to a data source
			// to satisfy the request
			String errorMsg =
				"Unsuccessfully tried all configured protocols for site '" + getSiteNumber() + "'.\n" +
				"Please check that the protocol handlers are properly installed and that the \n" +
				"protocol preferences for the site specify valid protocols.";
			getLogger().error(errorMsg);
			
			// JMW 9/29/2010 P104
			if(compositeException.size() == 0)
			{
				// if there were no exceptions but we got here that means all sources of data were attempted 
				// but none were compatible
				// add an exception manually here so the exception list is not empty
				compositeException.addException(new MethodException(errorMsg));
				
			}

			throw compositeException;
		}
		catch (ApplicationConfigurationException acX)
		{
			getLogger().error(acX);
			throw new MethodException(acX);
		}
	}

	/**
	 * Method that can be overridden to allow modifications or displaying of the result if desired
	 * @param result
	 * @return
	 */
	protected R postProcessResult(R result)
	{
		return result;
	}
	
	/**
	 * Get the name of the class of the SPI that is being used by this data source
	 * @return
	 */
	protected abstract Class<S> getSpiClass();
	
    /**
     * Return the name of the SPI method to be called.
     * @return
     */
    protected abstract String getSpiMethodName();

    /**
     * Return the parameter types of the SPI method to be called.
     * @return
     */
	protected abstract Class<?>[] getSpiMethodParameterTypes();
	
    /**
     * Return the parameters of the SPI method to be called.
     * @return
     */
	protected abstract Object[] getSpiMethodParameters();
    
	/**
	 * Get the ResolvedSite from the SPI method name, parameter types and parameters.
	 * This method allows the routing override provide implementations to inspect and 
	 * modify the resolved site.
	 * This method is declared final so that the derived classes must use it for site
	 * resolution, they may not do site resolution themselves.
	 * 
	 * @return
	 * @throws NoSuchMethodException
	 * @throws MethodException
	 */
	protected final ResolvedArtifactSource getResolvedArtifactSource() 
	throws NoSuchMethodException, MethodException 
	{
		Method spiMethod = getSpiClass().getDeclaredMethod(getSpiMethodName(), getSpiMethodParameterTypes());
		return getCommandContext().getResolvedArtifactSource(getRoutingToken(), getSpiClass(),  spiMethod, getSpiMethodParameters());
	}

    /**
     * Return the site number the data source should be called against.
     * @return
     */
    protected abstract String getSiteNumber();
	
	/**
	 * Retrieve an instance of the SPI being used by this data source
	 * @param url
	 * @return
	 * @throws ConnectionException
	 */
	protected S getDataSourceSpi(String protocol)
	throws ConnectionException
	{		
		if(canGenerateNewToken())
			return getProvider().createVersionableDataSource(getSpiClass(), resolvedArtifactSource, protocol, this);
		else
		{
			DataSourceProvider provider = getProvider();
			S s = provider.createVersionableDataSource(getSpiClass(), resolvedArtifactSource, protocol);
			return s;
			//return getProvider().createDataSource(getSpiClass(), url, resolvedSite);
		}
	}
	
	/**
	 * Method that can be overridden to allow setting transaction context properties before making call to the
	 * data source.
	 */
	protected void setTransactionContextFields()
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		transactionContext.setServicedSource(getRoutingToken().toRoutingTokenString());
	}
	
	/**
	 * Call the data source method to retrieve the result.  If there is a credentials expired exception
	 * attempt to handle it here and retry the same provider.
	 * @param url
	 * @return
	 * @throws ConnectionException
	 * @throws MethodException
	 */
	private R getCommandResultHandleCredentialsException(URL url, S spi)
	throws ConnectionException, MethodException
	{
		int securityExpiredCount = 0;
		
		while(securityExpiredCount < maximumSecurityTokenRetryAttemptCount)
		{
			try
			{
				return getCommandResult(spi);
			}
			catch(SecurityCredentialsExpiredException sceX)
			{
				securityExpiredCount++;
				getLogger().warn("Security credentials have expired, checking for method to acquire new credentials");
				if(canGenerateNewToken())
				{
					generateNewSecurityToken();
				}
				else
				{
					getLogger().warn("Security token was not locally generated, throwing error as MethodException.");
					throw new MethodException(sceX);
				}
			}
		}
		String msg = "Failed to retrieve data from URL '" + url.toExternalForm() + "' after '" + maximumSecurityTokenRetryAttemptCount + "' attempts with new securit tokens. No more attempts allowed";
		getLogger().warn(msg);
		throw new MethodException(msg);
	}

	/**
	 * 
	 * @param url
	 * @return 
	 * @throws ConnectionException
	 * @throws MethodException
	 * @throws SecurityCredentialsExpiredException
	 */
    protected abstract R getCommandResult(S spi) 
    throws ConnectionException, MethodException;    

	@Override
    public String toString()
    {
    	StringBuffer sb = new StringBuffer();
    	sb.append(getClass().getSimpleName());
    	sb.append("[");
    	sb.append(getClass().getSimpleName());
    	sb.append("(");
    	sb.append(parameterToString());
        sb.append(")]");
    	return sb.toString(); 
    }
    
	/**
	 * Builds a human-readable String of the parameter values.
	 * This method is intended for logging and debugging and is not
	 * part of the core processing.
	 * 
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#parameterToString()
	 */
	@Override
	protected String parameterToString() 
	{
		StringBuilder sb = new StringBuilder();
		if(getSpiMethodParameters() == null)
			sb.append("");
		
		else
			for(Object parameter : getSpiMethodParameters())
			{
				if(sb.length() > 0)
					sb.append(',');
				sb.append(parameter == null ? "<null>" : parameter.toString());
			}
		
		return sb.toString();
	}
    
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) 
	{
		// by default return false
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * new GUID().hashCode();
		return result;
	}
}