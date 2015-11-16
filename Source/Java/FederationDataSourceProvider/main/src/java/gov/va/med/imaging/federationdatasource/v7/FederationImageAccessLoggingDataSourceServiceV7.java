/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr 23, 2013
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
package gov.va.med.imaging.federationdatasource.v7;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.ImagingLogEvent;
import gov.va.med.imaging.exchange.business.ImageAccessReason;
import gov.va.med.imaging.exchange.enums.ImageAccessReasonType;
import gov.va.med.imaging.federation.proxy.v7.FederationRestImageAccessLoggingProxyV7;
import gov.va.med.imaging.federationdatasource.v5.FederationImageAccessLoggingDataSourceServiceV5;
import gov.va.med.imaging.proxy.services.ProxyServiceType;
import gov.va.med.imaging.proxy.services.ProxyServices;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.federation.exceptions.FederationConnectionException;

import java.io.IOException;
import java.util.List;

/**
 * @author VHAISWWERFEJ
 *
 */
public class FederationImageAccessLoggingDataSourceServiceV7
extends FederationImageAccessLoggingDataSourceServiceV5
{
	private final static String DATASOURCE_VERSION = "7";
	private FederationRestImageAccessLoggingProxyV7 proxy = null;

	/**
	 * @param resolvedArtifactSource
	 * @param protocol
	 * @throws UnsupportedOperationException
	 */
	public FederationImageAccessLoggingDataSourceServiceV7(
			ResolvedArtifactSource resolvedArtifactSource, String protocol)
			throws UnsupportedOperationException
	{
		super(resolvedArtifactSource, protocol);
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federationdatasource.AbstractFederationImageAccessLoggingDataSourceService#getDataSourceVersion()
	 */
	@Override
	public String getDataSourceVersion() 
	{
		return DATASOURCE_VERSION;
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federationdatasource.FederationImageAccessLoggingDataSourceServiceV4#getProxyServiceType()
	 */
	@Override
	protected ProxyServiceType getProxyServiceType()
	{
		return ProxyServiceType.imageAccessLogging;
	}

	@Override
	protected FederationRestImageAccessLoggingProxyV7 getProxy()
	throws ConnectionException
	{
		if(proxy == null)
		{
			ProxyServices proxyServices = getFederationProxyServices();
			if(proxyServices == null)
				throw new ConnectionException("Did not receive any applicable services from IDS service for site [" + getSite().getSiteNumber() + "]");
			proxy = new FederationRestImageAccessLoggingProxyV7(proxyServices, getFederationConfiguration());
		}
		return proxy;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.ImageAccessLoggingSpi#getImageAccessReasons(gov.va.med.RoutingToken, java.util.List)
	 */
	@Override
	public List<ImageAccessReason> getImageAccessReasons(
			RoutingToken globalRoutingToken,
			List<ImageAccessReasonType> reasonTypes) 
	throws MethodException, ConnectionException
	{
		getLogger().info("getImageAccessReasons TransactionContext (" + 				
				TransactionContextFactory.get().getDisplayIdentity() + ").");
		try 
		{
			federationConnection.connect();			
		}
		catch(IOException ioX) 
		{
			getLogger().error("Error getting image access reasons", ioX);
			throw new FederationConnectionException(ioX);
		}
		List<ImageAccessReason> result = getProxy().getImageAccessReasons(globalRoutingToken, reasonTypes);
		getLogger().info("getImageAccessReasons got [" + (result == null ? "null" : result.size()) + "] image access reasons from site [" + getSite().getSiteNumber() + "]");			
		return result;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federationdatasource.FederationImageAccessLoggingDataSourceServiceV4#LogImagingLogEvent(gov.va.med.imaging.exchange.ImagingLogEvent)
	 */
	@Override
	public void LogImagingLogEvent(ImagingLogEvent logEvent)
	throws MethodException, ConnectionException
	{
		getLogger().info("LogImagingLogEvent TransactionContext (" + 				
				TransactionContextFactory.get().getDisplayIdentity() + ").");
		try 
		{
			federationConnection.connect();			
		}
		catch(IOException ioX) 
		{
			getLogger().error("Error logging imaging event", ioX);
			throw new FederationConnectionException(ioX);
		}
		getProxy().LogImagingLogEvent(logEvent);
		getLogger().info("LogImagingLogEvent completed to site [" + getSite().getSiteNumber() + "]");
	}

}
