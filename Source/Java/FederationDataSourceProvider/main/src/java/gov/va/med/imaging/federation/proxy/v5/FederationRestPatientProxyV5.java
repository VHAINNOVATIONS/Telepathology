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
package gov.va.med.imaging.federation.proxy.v5;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.Patient;
import gov.va.med.imaging.exchange.business.PatientMeansTestResult;
import gov.va.med.imaging.federation.proxy.v4.FederationRestPatientProxyV4;
import gov.va.med.imaging.federation.rest.endpoints.FederationPatientRestUri;
import gov.va.med.imaging.federation.rest.proxy.FederationRestGetClient;
import gov.va.med.imaging.federation.rest.translator.FederationRestTranslator;
import gov.va.med.imaging.federation.rest.types.FederationPatientMeansTestResultType;
import gov.va.med.imaging.federation.rest.types.FederationPatientType;
import gov.va.med.imaging.federation.rest.types.FederationStringArrayType;
import gov.va.med.imaging.federationdatasource.configuration.FederationConfiguration;
import gov.va.med.imaging.proxy.services.ProxyServices;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * @author vhaiswwerfej
 *
 */
public class FederationRestPatientProxyV5 
extends FederationRestPatientProxyV4
{	
	public FederationRestPatientProxyV5(ProxyServices proxyServices, 
			FederationConfiguration federationConfiguration)
	{
		super(proxyServices, federationConfiguration);
	}

	@Override
	protected String getDataSourceVersion()
	{
		return "5";
	}

	@Override
	public List<String> getPatientSitesVisited(RoutingToken routingToken,
			String patientIcn, boolean includeTrailingCharactersForSite200)
	throws ConnectionException, MethodException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		getLogger().info("getPatientSitesVisited, Transaction [" + transactionContext.getTransactionId() + "] initiated, patient '" + patientIcn + "' to '" + routingToken.toRoutingTokenString() + "'.");
		setDataSourceMethodAndVersion("getPatientSitesVisited");
		Map<String, String> urlParameterKeyValues = new HashMap<String, String>();
		urlParameterKeyValues.put("{routingToken}", routingToken.toRoutingTokenString());
		urlParameterKeyValues.put("{patientIcn}", patientIcn);
		urlParameterKeyValues.put("{includeTrailingCharactersForSite200}", includeTrailingCharactersForSite200 + "");
		
		String url = getWebResourceUrl(FederationPatientRestUri.patientVisitedPathV5, urlParameterKeyValues ); 
				
		FederationRestGetClient getClient = new FederationRestGetClient(url, MediaType.APPLICATION_XML_TYPE, federationConfiguration);
		FederationStringArrayType sites = getClient.executeRequest(FederationStringArrayType.class);
		getLogger().info("getPatientSitesVisited, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (sites == null ? "null" : (sites.getValues() == null ? "null" : sites.getValues().length)) + "] sites.");
		List<String> result = FederationRestTranslator.translate(FederationRestTranslator.translateStringArray(sites));
		getLogger().info("getPatientSitesVisited, Transaction [" + transactionContext.getTransactionId() + "] returned response of [" + (result == null ? "null" :  result.size()) + "] site number business objects.");
		return result;		
	}
	
	public Patient getPatientInformation(RoutingToken routingToken, String patientIcn)
	throws ConnectionException, MethodException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		getLogger().info("getPatientInformation, Transaction [" + transactionContext.getTransactionId() + "] initiated, patient '" + patientIcn + "' to '" + routingToken.toRoutingTokenString() + "'.");
		setDataSourceMethodAndVersion("getPatientInformation");
		Map<String, String> urlParameterKeyValues = new HashMap<String, String>();
		urlParameterKeyValues.put("{routingToken}", routingToken.toRoutingTokenString());
		urlParameterKeyValues.put("{patientIcn}", patientIcn);
		
		String url = getWebResourceUrl(FederationPatientRestUri.patientInformationPath, urlParameterKeyValues ); 
				
		FederationRestGetClient getClient = new FederationRestGetClient(url, MediaType.APPLICATION_XML_TYPE, federationConfiguration);
		FederationPatientType patient = getClient.executeRequest(FederationPatientType.class);
		getLogger().info("getPatientInformation, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (patient == null ? "null" : "not null") + "] patient.");
		Patient result = FederationRestTranslator.translate(patient);
		getLogger().info("getPatientInformation, Transaction [" + transactionContext.getTransactionId() + "] returned response of [" + (result == null ? "null" : "not null") + "] patient business object.");
		return result;	
	}
	
	public PatientMeansTestResult getPatientMeansTest(RoutingToken routingToken, String patientIcn)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		getLogger().info("getPatientMeansTest, Transaction [" + transactionContext.getTransactionId() + "] initiated, patient '" + patientIcn + "' to '" + routingToken.toRoutingTokenString() + "'.");
		setDataSourceMethodAndVersion("getPatientMeansTest");
		Map<String, String> urlParameterKeyValues = new HashMap<String, String>();
		urlParameterKeyValues.put("{routingToken}", routingToken.toRoutingTokenString());
		urlParameterKeyValues.put("{patientIcn}", patientIcn);
		
		String url = getWebResourceUrl(FederationPatientRestUri.patientMeansTestPath, urlParameterKeyValues ); 
				
		FederationRestGetClient getClient = new FederationRestGetClient(url, MediaType.APPLICATION_XML_TYPE, federationConfiguration);
		FederationPatientMeansTestResultType patientMeansTestResult = getClient.executeRequest(FederationPatientMeansTestResultType.class);
		getLogger().info("getPatientMeansTest, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (patientMeansTestResult == null ? "null" : "not null") + "] patient means test.");
		PatientMeansTestResult result = FederationRestTranslator.translate(patientMeansTestResult);
		getLogger().info("getPatientMeansTest, Transaction [" + transactionContext.getTransactionId() + "] returned response of [" + (result == null ? "null" : "not null") + "] patient means test business object.");
		return result;	
	}

}
