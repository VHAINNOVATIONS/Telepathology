/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Apr 29, 2008
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
package gov.va.med.imaging.datasource;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.annotations.SPI;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import java.lang.reflect.Method;

/**
 * @author VHAISWBECKEC
 * 
 * This defines an interface that allows a Provider package some
 * influence over the routing of requests.  This interface defines
 * a "local" service provider.  A "local" service provider is one
 * that is not expected to access a remote data source and therefore
 * does not need URL and/or Site initialization.
 * Implementations of this class must implement a no-arg constructor.
 * 
 * Called by the Router before it determines the data source to send the request to.
 * Provider implementations may modify the content of the resolvedSite instance and
 * thereby redirect the call.
 * While there are no restrictions on how the redirect is made, it is strongly suggested that
 * Provider implementations restrict changes to the protocols that they implement because
 * there is no guarantee that dependent data source is installed.
 * 
 * An implementation of this interface should not modify the contents of the ResolvedSite 
 * instance if it does not wish to change the routing.
 * 
 * The order that implementations of this method are called is NOT guaranteed.  Multiple 
 * implementations whether in one or many Provider packages may be called in any order.
 * This is one more reason why a Provider should restrict implementations of this class
 * strictly to the protocols it implements.
 * 
 */
@SPI(description="This defines an interface that allows a Provider package some influence over the routing of requests")
public interface RoutingOverrideSpi
extends LocalDataSourceSpi
{
	/**
	 * 
	 * @param spi
	 * @param method
	 * @param parameters
	 * @param siteResolver
	 * @param resolvedArtifactSource
	 * @throws ConnectionException
	 * @throws MethodException
	 */
	public ResolvedArtifactSource resolve(
		ResolvedArtifactSource naturalDestination,
		Class<? extends VersionableDataSourceSpi> spi, 
		Method method, 
		Object[] parameters, 
		SiteResolutionDataSourceSpi siteResolver)
	throws ConnectionException, MethodException;
}
