/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 21, 2011
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
package gov.va.med.imaging.federationdatasource.v5;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityException;
import gov.va.med.imaging.datasource.UserDataSourceSpi;
import gov.va.med.imaging.datasource.exceptions.UnsupportedProtocolException;
import gov.va.med.imaging.datasource.exceptions.UnsupportedServiceMethodException;
import gov.va.med.imaging.exchange.business.ApplicationTimeoutParameters;
import gov.va.med.imaging.exchange.business.Division;
import gov.va.med.imaging.exchange.business.ElectronicSignatureResult;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.UserInformation;
import gov.va.med.imaging.federation.proxy.FederationProxyUtilities;
import gov.va.med.imaging.federation.proxy.v5.FederationRestUserProxyV5;
import gov.va.med.imaging.federationdatasource.AbstractFederationDataSourceService;
import gov.va.med.imaging.proxy.services.ProxyServiceType;
import gov.va.med.imaging.proxy.services.ProxyServices;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.federation.exceptions.FederationConnectionException;
import gov.va.med.imaging.url.vftp.VftpConnection;

/**
 * @author VHAISWWERFEJ
 *
 */
public class FederationUserDataSourceServiceV5
extends AbstractFederationDataSourceService
implements UserDataSourceSpi
{
	protected final VftpConnection federationConnection;
	private ProxyServices federationProxyServices = null;
	
	private final static String DATASOURCE_VERSION = "5";
	private FederationRestUserProxyV5 proxy = null;
	public final static String SUPPORTED_PROTOCOL = "vftp";
	private final static String FEDERATION_PROXY_SERVICE_NAME = "Federation";
	
	private final static Logger logger = Logger.getLogger(FederationUserDataSourceServiceV5.class);

	public FederationUserDataSourceServiceV5(ResolvedArtifactSource resolvedArtifactSource, String protocol)
	throws UnsupportedOperationException
	{
		super(resolvedArtifactSource, protocol);
		federationConnection = new VftpConnection(getMetadataUrl());

		if(! (resolvedArtifactSource instanceof ResolvedSite) )
			throw new UnsupportedOperationException("The artifact source must be an instance of ResolvedSite and it is a '" + resolvedArtifactSource.getClass().getSimpleName() + "'.");
	}
	
	public static FederationUserDataSourceServiceV5 create(ResolvedArtifactSource resolvedArtifactSource, String protocol)
	throws ConnectionException, UnsupportedProtocolException
	{
		return new FederationUserDataSourceServiceV5(resolvedArtifactSource, protocol);
	}
	
	protected Logger getLogger()
	{
		return logger;
	}

	protected ProxyServiceType getProxyServiceType()
	{
		return ProxyServiceType.metadata;
	}
	
	@Override
	public List<String> getUserKeys(RoutingToken globalRoutingToken)
	throws MethodException, ConnectionException
	{
		getLogger().info("getUserKeys TransactionContext (" + 				
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
		List<String> result = getProxy().getUserKeys(globalRoutingToken);
		getLogger().info("getUserKeys got [" + (result == null ? "null" : result.size()) + "] user keys from site [" + getSite().getSiteNumber() + "]");			
		return result;
	}
	
	@Override
	public List<Division> getDivisionList(String accessCode,
			RoutingToken globalRoutingToken) 
	throws MethodException, ConnectionException
	{
		getLogger().info("getDivisionList TransactionContext (" + 				
				TransactionContextFactory.get().getDisplayIdentity() + ").");
		try 
		{
			federationConnection.connect();			
		}
		catch(IOException ioX) 
		{
			getLogger().error("Error getting division list", ioX);
			throw new FederationConnectionException(ioX);
		}
		List<Division> result = getProxy().getDivisionList(accessCode, globalRoutingToken);
		getLogger().info("getDivisionList got [" + (result == null ? "null" : result.size()) + "] divisions from site [" + getSite().getSiteNumber() + "]");			
		return result;
	}

	@Override
	public UserInformation getUserInformation(RoutingToken globalRoutingToken)
	throws MethodException, ConnectionException
	{
		getLogger().info("getUserInformation TransactionContext (" + 				
				TransactionContextFactory.get().getDisplayIdentity() + ").");
		try 
		{
			federationConnection.connect();			
		}
		catch(IOException ioX) 
		{
			getLogger().error("Error getting user information", ioX);
			throw new FederationConnectionException(ioX);
		}
		UserInformation result = getProxy().getUserInformation(globalRoutingToken);
		getLogger().info("getUserInformation got [" + (result == null ? "null" : "not null") + "] user information from site [" + getSite().getSiteNumber() + "]");			
		return result;
	}
	
	@Override
	public boolean isVersionCompatible() 
	throws SecurityException
	{
		if(getFederationProxyServices() == null)
			return false;		
		try
		{
			getLogger().debug("Found FederationProxyServices, looking for '" + getProxyServiceType() + "' service type at site [" + getSite().getSiteNumber() + "].");
			getFederationProxyServices().getProxyService(getProxyServiceType());
			getLogger().debug("Found service type '" + getProxyServiceType() + "' at site [" + getSite().getSiteNumber() + "], returning true for version compatible.");
			return true;
		}
		catch(gov.va.med.imaging.proxy.exceptions.ProxyServiceNotFoundException psnfX)
		{
			getLogger().warn("Cannot find proxy service type '" + getProxyServiceType() + "' at site [" + getSite().getSiteNumber() + "]");
			return false;
		}
	}
	
	protected ProxyServices getFederationProxyServices()
	{
		if(federationProxyServices == null)
		{
			federationProxyServices = 
				FederationProxyUtilities.getFederationProxyServices(getSite(), 
						getFederationProxyName(), getDataSourceVersion());
		}
		return federationProxyServices;
	}
	
	protected String getFederationProxyName()
	{
		return FEDERATION_PROXY_SERVICE_NAME;
	}
	
	public String getDataSourceVersion() 
	{
		return DATASOURCE_VERSION;
	}
	
	/**
	 * The artifact source must be checked in the constructor to assure that it is an instance
	 * of ResolvedSite.
	 * 
	 * @return
	 */
	protected ResolvedSite getResolvedSite()
	{
		return (ResolvedSite)getResolvedArtifactSource();
	}
	
	protected Site getSite()
	{
		return getResolvedSite().getSite();
	}
	
	protected FederationRestUserProxyV5 getProxy()
	throws ConnectionException
	{
		if(proxy == null)
		{
			ProxyServices proxyServices = getFederationProxyServices();
			if(proxyServices == null)
				throw new ConnectionException("Did not receive any applicable services from IDS service for site [" + getSite().getSiteNumber() + "]");
			proxy = new FederationRestUserProxyV5(proxyServices, 
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
		throw new UnsupportedServiceMethodException(UserDataSourceSpi.class, "verifyElectronicSignature");
	}
	
	@Override
	public ApplicationTimeoutParameters getApplicationTimeoutParameters(String siteId, String applicationName) 
	throws MethodException, ConnectionException
	{
		throw new UnsupportedServiceMethodException(UserDataSourceSpi.class, "getApplicationTimeoutParameters");
	}

}
