/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 25, 2010
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
package gov.va.med.imaging.federation.proxy.v4;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.DocumentFilter;
import gov.va.med.imaging.exchange.business.documents.DocumentSetResult;
import gov.va.med.imaging.exchange.translation.exceptions.TranslationException;
import gov.va.med.imaging.federation.rest.endpoints.FederationDocumentSetRestUri;
import gov.va.med.imaging.federation.rest.proxy.AbstractFederationRestProxy;
import gov.va.med.imaging.federation.rest.proxy.FederationRestPostClient;
import gov.va.med.imaging.federation.rest.translator.FederationRestTranslator;
import gov.va.med.imaging.federation.rest.types.FederationDocumentFilterType;
import gov.va.med.imaging.federation.rest.types.FederationDocumentSetResultType;
import gov.va.med.imaging.federationdatasource.configuration.FederationConfiguration;
import gov.va.med.imaging.proxy.services.ProxyServiceType;
import gov.va.med.imaging.proxy.services.ProxyServices;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * @author vhaiswwerfej
 *
 */
public class FederationRestDocumentSetProxyV4
extends AbstractFederationRestProxy 
{
	public FederationRestDocumentSetProxyV4(ProxyServices proxyServices, 
			FederationConfiguration federationConfiguration)
	{
		super(proxyServices, federationConfiguration);
	}


	@Override
	protected ProxyServiceType getProxyServiceType()
	{
		return ProxyServiceType.metadata;
	}

	@Override
	protected String getRestServicePath()
	{
		return FederationDocumentSetRestUri.documentSetServicePath;
	}

	public DocumentSetResult getDocumentSets(DocumentFilter filter, RoutingToken routingToken) 
	throws MethodException, ConnectionException 
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		getLogger().info("getDocumentSets, Transaction [" + transactionContext.getTransactionId() + "] initiated, patient '" + filter.getPatientId() + "' to '" + routingToken.toRoutingTokenString() + "'.");
		Map<String, String> urlParameterKeyValues = new HashMap<String, String>();
		urlParameterKeyValues.put("{routingToken}", routingToken.toRoutingTokenString());
		setDataSourceMethodAndVersion("getDocumentSets");
		String url = getWebResourceUrl(FederationDocumentSetRestUri.documentSetPath, urlParameterKeyValues );
		FederationDocumentFilterType federationDocumentFilter = FederationRestTranslator.translate(filter);
		FederationRestPostClient postClient = new FederationRestPostClient(url, MediaType.APPLICATION_XML_TYPE, federationConfiguration);
		FederationDocumentSetResultType documentResult = 
			postClient.executeRequest(FederationDocumentSetResultType.class, federationDocumentFilter);
		if(documentResult == null)
		{
			getLogger().error("Got null FederationDocumentSetResultType from Federation data source");			
			return null;
		}
		getLogger().info("getDocumentSets, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (documentResult == null ? "null" : "not null") + "] FederationDocumentSetResultType.");
		DocumentSetResult result = null;
		try
		{
			result = FederationRestTranslator.translate(documentResult);
		}
		catch(TranslationException tX)
		{
			getLogger().error("Error translating document sets into business objects, " + tX.getMessage(), tX);
			throw new MethodException(tX);			
		}
		getLogger().info("getDocumentSets, Transaction [" + transactionContext.getTransactionId() + "] returned response of [" + (result == null ? "null" :  "not null") + "] DocumentSetResult business object.");
		return result;		
	}

	@Override
	protected String getDataSourceVersion()
	{
		return "4";
	}
}
