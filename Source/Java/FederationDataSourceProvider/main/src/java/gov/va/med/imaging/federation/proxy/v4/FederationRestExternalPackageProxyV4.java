/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 1, 2010
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

import gov.va.med.RoutingToken;
import gov.va.med.imaging.CprsIdentifier;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.translation.exceptions.TranslationException;
import gov.va.med.imaging.federation.rest.endpoints.FederationExternalPackageRestUri;
import gov.va.med.imaging.federation.rest.proxy.AbstractFederationRestProxy;
import gov.va.med.imaging.federation.rest.proxy.FederationRestPostClient;
import gov.va.med.imaging.federation.rest.translator.FederationRestTranslator;
import gov.va.med.imaging.federation.rest.types.FederationCprsIdentifierType;
import gov.va.med.imaging.federation.rest.types.FederationStudyType;
import gov.va.med.imaging.federationdatasource.configuration.FederationConfiguration;
import gov.va.med.imaging.proxy.services.ProxyServiceType;
import gov.va.med.imaging.proxy.services.ProxyServices;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import javax.ws.rs.core.MediaType;

/**
 * @author vhaiswwerfej
 *
 */
public class FederationRestExternalPackageProxyV4 
extends AbstractFederationRestProxy
{
	public FederationRestExternalPackageProxyV4(ProxyServices proxyServices, 
			FederationConfiguration federationConfiguration)
	{
		super(proxyServices, federationConfiguration);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federation.rest.proxy.AbstractFederationRestImageProxy#getProxyServiceType()
	 */
	@Override
	protected ProxyServiceType getProxyServiceType()
	{
		return ProxyServiceType.metadata;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federation.rest.proxy.AbstractFederationRestImageProxy#getRestServicePath()
	 */
	@Override
	protected String getRestServicePath()
	{
		return FederationExternalPackageRestUri.externalPackageServicePath;
	}
	
	public List<Study> getStudiesFromCprsIdentifier(RoutingToken routingToken, String patientIcn,
			CprsIdentifier cprsIdentifier)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		getLogger().info("getStudiesFromCprsIdentifier, Transaction [" + transactionContext.getTransactionId() + "] initiated, patient Icn '" + patientIcn + "' to '" + routingToken.toRoutingTokenString() + "'.");
		setDataSourceMethodAndVersion("getStudiesFromCprsIdentifier");
		Map<String, String> urlParameterKeyValues = new HashMap<String, String>();
		urlParameterKeyValues.put("{routingToken}", routingToken.toRoutingTokenString());
		urlParameterKeyValues.put("{patientIcn}", patientIcn);
		
		FederationCprsIdentifierType federationCprsIdentifier = FederationRestTranslator.translate(cprsIdentifier);
		
		String url = getWebResourceUrl(FederationExternalPackageRestUri.getStudyFromCprsMethodPath, urlParameterKeyValues );
		FederationRestPostClient postClient = new FederationRestPostClient(url, MediaType.APPLICATION_XML_TYPE, federationConfiguration);
		FederationStudyType[] studiesType = postClient.executeRequest(FederationStudyType[].class, federationCprsIdentifier);
		getLogger().info("getStudiesFromCprsIdentifier, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (studiesType == null ? "null" : studiesType.length) + "] study webservice objects.");
		try
		{
			SortedSet<Study> result = FederationRestTranslator.translate(studiesType);
			List<Study> studyResult = new ArrayList<Study>(result.size());
			studyResult.addAll(result);
			getLogger().info("getStudiesFromCprsIdentifier, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (studyResult == null ? "null" : studyResult.size()) + "] study business objects.");
			return studyResult;
		}
		catch(TranslationException tX)
		{
			getLogger().error("Error in getStudyFromCprsIdentifier", tX);
			throw new MethodException(tX);
		}
	}
	
	@Override
	protected String getDataSourceVersion()
	{
		return "4";
	}

}
