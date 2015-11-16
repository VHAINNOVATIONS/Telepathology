/**
 * 
 */
package gov.va.med.imaging.core.interfaces.router;

import java.lang.reflect.Method;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.Router;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.*;
import gov.va.med.imaging.exchange.business.ResolvedSite;

/**
 * This interface defines the context available to a Command.
 * THe CommandFactory implementation MUST provide an implementation of
 * this interface to each command that it creates.
 * 
 * @author vhaiswbeckec
 *
 */
public interface CommandContext
{

	/**
	 * @return the router
	 */
	public abstract Router getRouter();

	/**
	 * 
	 * @return
	 */
	public abstract DataSourceProvider getProvider();

	/**
	 * @return the siteResolver
	 */
	public abstract SiteResolutionDataSourceSpi getSiteResolver();
	
	/**
	 * @return the commandFactory
	 */
	public abstract CommandFactory getCommandFactory();
	
	/**
	 * @return the transactionLoggerService
	 */
	public abstract TransactionLoggerDataSourceSpi getTransactionLoggerService();
	
	/**
	 * @return - true if caching is enabled and commands should attempt to cache instances
	 */
	public abstract boolean isCachingEnabled();
	
	/**
	 * This will return a ResolvedSite instance representing the local site that
	 * this VIX is configured to represent as configured in the AppConfiguration 
	 * or null if this VIX does not represent a Site.
	 * 
	 * @return
	 */
	public abstract ResolvedSite getLocalSite();
	
	/**
	 * @param siteNumber
	 * @return
	 * @throws MethodException
	 */
	//public ResolvedSite getSite(String siteNumber) 
	//throws MethodException;
	
	/**
	 * This method DOES NOT respect the routing override services and MUST NOT
	 * be used except for site resolution expressed externally. Use:
	 * getArtifactSource(RoutingToken, spi, method, parameters)
	 * for normal site resolution within a Command.
	 * 
	 * @param siteNumber
	 * @return
	 * @throws MethodException
	 */
	public ResolvedArtifactSource getResolvedArtifactSource(RoutingToken routingToken) 
	throws MethodException;
	
	/**
	 * @param spi
	 * @param method
	 * @param parameters
	 * @return
	 * @throws MethodException 
	 */
	//public ResolvedSite getSite(
	//	String siteNumber, 
	//	Class<? extends VersionableDataSourceSpi> spi, 
	//	Method method, 
	//	Object[] parameters) 
	//throws MethodException;

	/**
	 * This method should be used by the Command implementations to resolve the RoutingToken to 
	 * a ResolvedArtifactSource.  This method will make the calls to the routingOverrideServices if
	 * they are in place.
	 * 
	 * @param routingToken
	 * @param spi
	 * @param method
	 * @param parameters
	 * @return
	 * @throws MethodException
	 */
	public ResolvedArtifactSource getResolvedArtifactSource(
		RoutingToken routingToken, 
		Class<? extends VersionableDataSourceSpi> spi, 
		Method method, 
		Object[] parameters) 
	throws MethodException;

}