/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 10, 2012
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
package gov.va.med.imaging.federationdatasource.v6;

import java.io.IOException;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.ElectronicSignatureResult;
import gov.va.med.imaging.federation.proxy.v6.FederationRestUserProxyV6;
import gov.va.med.imaging.federationdatasource.v5.FederationUserDataSourceServiceV5;
import gov.va.med.imaging.proxy.services.ProxyServiceType;
import gov.va.med.imaging.proxy.services.ProxyServices;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.federation.exceptions.FederationConnectionException;

/**
 * @author VHAISWWERFEJ
 *
 */
public class FederationUserDataSourceServiceV6
extends FederationUserDataSourceServiceV5
{
	
	private final static String DATASOURCE_VERSION = "6";
	private FederationRestUserProxyV6 proxy = null;

	@Override
	public String getDataSourceVersion() 
	{
		return DATASOURCE_VERSION;
	}

	/**
	 * @param resolvedArtifactSource
	 * @param protocol
	 * @throws UnsupportedOperationException
	 */
	public FederationUserDataSourceServiceV6(
			ResolvedArtifactSource resolvedArtifactSource, String protocol)
	throws UnsupportedOperationException
	{
		super(resolvedArtifactSource, protocol);
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federationdatasource.v5.FederationUserDataSourceServiceV5#getProxyServiceType()
	 */
	@Override
	protected ProxyServiceType getProxyServiceType()
	{
		return ProxyServiceType.user;
	}

	protected FederationRestUserProxyV6 getProxy()
	throws ConnectionException
	{
		if(proxy == null)
		{
			ProxyServices proxyServices = getFederationProxyServices();
			if(proxyServices == null)
				throw new ConnectionException("Did not receive any applicable services from IDS service for site [" + getSite().getSiteNumber() + "]");
			proxy = new FederationRestUserProxyV6(proxyServices, 
					getFederationConfiguration());
		}
		return proxy;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.UserDataSourceSpi#verifyElectronicSignature(gov.va.med.RoutingToken, java.lang.String)
	 */
	@Override
	public ElectronicSignatureResult verifyElectronicSignature(
			RoutingToken globalRoutingToken, String electronicSignature)
	throws MethodException, ConnectionException
	{
		getLogger().info("verifyElectronicSignature TransactionContext (" + 				
				TransactionContextFactory.get().getDisplayIdentity() + ").");
		try 
		{
			federationConnection.connect();			
		}
		catch(IOException ioX) 
		{
			getLogger().error("Error getting user keys", ioX);
			throw new FederationConnectionException(ioX);
		}
		ElectronicSignatureResult result = getProxy().verifyElectronicSignature(globalRoutingToken, electronicSignature);
		getLogger().info("verifyElectronicSignature got [" + (result == null ? "null" : result.isSuccess()) + "] electronic signature result site [" + getSite().getSiteNumber() + "]");			
		return result;
	}
}
