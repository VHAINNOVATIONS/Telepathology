/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 15, 2010
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
package gov.va.med.imaging.federation.proxy.v6;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import gov.va.med.HealthSummaryURN;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.HealthSummaryType;
import gov.va.med.imaging.federation.proxy.v5.FederationRestPatientProxyV5;
import gov.va.med.imaging.federation.rest.endpoints.FederationPatientRestUri;
import gov.va.med.imaging.federation.rest.proxy.FederationRestGetClient;
import gov.va.med.imaging.federation.rest.translator.FederationRestTranslator;
import gov.va.med.imaging.federation.rest.types.FederationHealthSummaryType;
import gov.va.med.imaging.federationdatasource.configuration.FederationConfiguration;
import gov.va.med.imaging.proxy.services.ProxyServiceType;
import gov.va.med.imaging.proxy.services.ProxyServices;
import gov.va.med.imaging.rest.types.RestCoreTranslator;
import gov.va.med.imaging.rest.types.RestStringType;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * @author vhaiswwerfej
 *
 */
public class FederationRestPatientProxyV6 
extends FederationRestPatientProxyV5
{	
	public FederationRestPatientProxyV6(ProxyServices proxyServices, 
			FederationConfiguration federationConfiguration)
	{
		super(proxyServices, federationConfiguration);
	}

	@Override
	protected String getDataSourceVersion()
	{
		return "6";
	}
	
	@Override
	protected ProxyServiceType getProxyServiceType()
	{
		return ProxyServiceType.patient;
	}
	
	public List<HealthSummaryType> getHealthSummaryTypes(
			RoutingToken routingToken) 
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		getLogger().info("getHealthSummaryTypes, Transaction [" + transactionContext.getTransactionId() + "] initiated to '" + routingToken.toRoutingTokenString() + "'.");
		setDataSourceMethodAndVersion("getHealthSummaryTypes");
		Map<String, String> urlParameterKeyValues = new HashMap<String, String>();
		urlParameterKeyValues.put("{routingToken}", routingToken.toRoutingTokenString());
		
		String url = getWebResourceUrl(FederationPatientRestUri.healthSummariesPath, urlParameterKeyValues ); 
				
		FederationRestGetClient getClient = new FederationRestGetClient(url, MediaType.APPLICATION_XML_TYPE, federationConfiguration);
		FederationHealthSummaryType[] healthSummary = getClient.executeRequest(FederationHealthSummaryType[].class);
		getLogger().info("getHealthSummaryTypes, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (healthSummary == null ? "null" : "not null") + "] health summaries.");
		List<HealthSummaryType> result = FederationRestTranslator.translate(healthSummary);
		getLogger().info("getHealthSummaryTypes, Transaction [" + transactionContext.getTransactionId() + "] returned response of [" + (result == null ? "null" : "not null") + "] health summaries business object.");
		return result;	
	}
	
	public String getHealthSummary(HealthSummaryURN healthSummaryUrn,
			String patientIcn) 
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		getLogger().info("getHealthSummary, Transaction [" + transactionContext.getTransactionId() + "] initiated for '" + healthSummaryUrn.toString() + "' for patient '" + patientIcn + "'.");
		setDataSourceMethodAndVersion("getHealthSummary");
		Map<String, String> urlParameterKeyValues = new HashMap<String, String>();
		urlParameterKeyValues.put("{healthSummaryId}", healthSummaryUrn.toString());
		urlParameterKeyValues.put("{patientIcn}", patientIcn);
		
		String url = getWebResourceUrl(FederationPatientRestUri.patientHealthSummaryPath, urlParameterKeyValues ); 
				
		FederationRestGetClient getClient = new FederationRestGetClient(url, MediaType.APPLICATION_XML_TYPE, federationConfiguration);
		RestStringType healthSummary = getClient.executeRequest(RestStringType.class);
		getLogger().info("getHealthSummary, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (healthSummary == null ? "null" : "not null") + "] patient health summary.");
		String result = RestCoreTranslator.translate(healthSummary);;
		getLogger().info("getHealthSummary, Transaction [" + transactionContext.getTransactionId() + "] returned response of [" + (result == null ? "null" : "not null") + "] patient health summary business object.");
		return result;	
	}
}
