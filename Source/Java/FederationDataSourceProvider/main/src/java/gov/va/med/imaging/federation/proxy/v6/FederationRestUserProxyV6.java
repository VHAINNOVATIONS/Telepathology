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
package gov.va.med.imaging.federation.proxy.v6;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.ElectronicSignatureResult;
import gov.va.med.imaging.federation.proxy.v5.FederationRestUserProxyV5;
import gov.va.med.imaging.federation.rest.endpoints.FederationUserRestUri;
import gov.va.med.imaging.federation.rest.proxy.RestPostClient;
import gov.va.med.imaging.federation.rest.translator.FederationRestTranslator;
import gov.va.med.imaging.federation.rest.types.FederationElectronicSignatureResultType;
import gov.va.med.imaging.federationdatasource.configuration.FederationConfiguration;
import gov.va.med.imaging.proxy.services.ProxyServiceType;
import gov.va.med.imaging.proxy.services.ProxyServices;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * @author VHAISWWERFEJ
 *
 */
public class FederationRestUserProxyV6
extends FederationRestUserProxyV5
{

	/**
	 * @param proxyServices
	 * @param federationConfiguration
	 */
	public FederationRestUserProxyV6(ProxyServices proxyServices,
			FederationConfiguration federationConfiguration)
	{
		super(proxyServices, federationConfiguration);
	}
	
	@Override
	protected ProxyServiceType getProxyServiceType()
	{
		return ProxyServiceType.user;
	}

	@Override
	protected String getDataSourceVersion()
	{
		return "6";
	}
	
	public ElectronicSignatureResult verifyElectronicSignature(
			RoutingToken globalRoutingToken, String electronicSignature)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		getLogger().info("verifyElectronicSignature, Transaction [" + transactionContext.getTransactionId() + "] initiated.");
		setDataSourceMethodAndVersion("verifyElectronicSignature");
		Map<String, String> urlParameterKeyValues = new HashMap<String, String>();
		urlParameterKeyValues.put("{routingToken}", globalRoutingToken.toRoutingTokenString());
		String url = getWebResourceUrl(FederationUserRestUri.electronicSignature, urlParameterKeyValues);
		RestPostClient postClient = new RestPostClient(url, MediaType.APPLICATION_XML_TYPE, federationConfiguration);		
		FederationElectronicSignatureResultType esig = 
			postClient.executeRequest(FederationElectronicSignatureResultType.class, electronicSignature);
	
		getLogger().info("verifyElectronicSignature, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (esig == null ? "null" : "not null") + "] electronic signature result webservice object.");
		ElectronicSignatureResult result = FederationRestTranslator.translate(esig);		
		getLogger().info("verifyElectronicSignature, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (result == null ? "null" : result.isSuccess()) + "] electronic signature result.");
		return result;
	}

}
