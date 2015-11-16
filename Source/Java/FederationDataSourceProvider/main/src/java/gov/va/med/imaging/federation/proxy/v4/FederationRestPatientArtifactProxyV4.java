/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Oct 15, 2010
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
import gov.va.med.imaging.core.interfaces.exceptions.InsufficientPatientSensitivityException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.ArtifactResults;
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.exchange.translation.exceptions.TranslationException;
import gov.va.med.imaging.federation.rest.endpoints.FederationPatientArtifactRestUri;
import gov.va.med.imaging.federation.rest.proxy.AbstractFederationRestProxy;
import gov.va.med.imaging.federation.rest.proxy.FederationRestPostClient;
import gov.va.med.imaging.federation.rest.translator.FederationRestTranslator;
import gov.va.med.imaging.federation.rest.types.FederationArtifactResultsType;
import gov.va.med.imaging.federation.rest.types.FederationFilterType;
import gov.va.med.imaging.federationdatasource.configuration.FederationConfiguration;
import gov.va.med.imaging.proxy.services.ProxyServiceType;
import gov.va.med.imaging.proxy.services.ProxyServices;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * @author vhaiswwerfej
 *
 */
public class FederationRestPatientArtifactProxyV4
extends AbstractFederationRestProxy 
{
	
	public FederationRestPatientArtifactProxyV4(ProxyServices proxyServices, 
			FederationConfiguration federationConfiguration)
	{
		super(proxyServices, federationConfiguration);
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federation.rest.proxy.AbstractFederationRestProxy#getProxyServiceType()
	 */
	@Override
	protected ProxyServiceType getProxyServiceType() 
	{
		return ProxyServiceType.metadata;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federation.rest.proxy.AbstractFederationRestProxy#getRestServicePath()
	 */
	@Override
	protected String getRestServicePath() 
	{
		return FederationPatientArtifactRestUri.patientArtifactServicePath;
	}
	
	public ArtifactResults getPatientArtifacts(String patientIcn, StudyFilter filter, 
			RoutingToken routingToken, StudyLoadLevel studyLoadLevel, boolean includeRadiology, 
			boolean includeDocuments)
	throws InsufficientPatientSensitivityException, MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		getLogger().info("getPatientArtifacts, Transaction [" + transactionContext.getTransactionId() + "] initiated, patient '" + patientIcn + "' to '" + routingToken.toRoutingTokenString() + "'.");
		setDataSourceMethodAndVersion("getPatientArtifacts");
		Map<String, String> urlParameterKeyValues = new HashMap<String, String>();
		urlParameterKeyValues.put("{routingToken}", routingToken.toRoutingTokenString());
		urlParameterKeyValues.put("{patientIcn}", patientIcn);
		urlParameterKeyValues.put("{authorizedSensitiveLevel}", filter.getMaximumAllowedLevel().getCode() + "");
		urlParameterKeyValues.put("{studyLoadLevel}", FederationRestTranslator.translate(studyLoadLevel).name());
		urlParameterKeyValues.put("{includeRadiology}", includeRadiology + "");
		urlParameterKeyValues.put("{includeDocuments}", includeDocuments + "");
		
		String url = getWebResourceUrl(FederationPatientArtifactRestUri.patientArtifactsPath, urlParameterKeyValues ); 
		FederationFilterType federationFilter = FederationRestTranslator.translate(filter);
		FederationRestPostClient postClient = new FederationRestPostClient(url, MediaType.APPLICATION_XML_TYPE, federationConfiguration);
		FederationArtifactResultsType artifactResults = postClient.executeRequest(FederationArtifactResultsType.class, federationFilter);
		if(artifactResults == null)
		{
			getLogger().error("Got null FederationArtifactResultsType from Federation data source");			
			return null;
		}
		getLogger().info("getPatientArtifacts, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (artifactResults == null ? "null" : "not null") + "] FederationArtifactResultsType.");
		ArtifactResults result = null;
		try
		{
			result = FederationRestTranslator.translate(artifactResults, filter);
			transactionContext.addDebugInformation(result == null ? "null PatientArtifacts" : result.toString(true));
		}
		catch(TranslationException tX)
		{
			getLogger().error("Error translating artifact results into business objects, " + tX.getMessage(), tX);
			throw new MethodException(tX);			
		}
		getLogger().info("getPatientArtifacts, Transaction [" + transactionContext.getTransactionId() + "] returned response of [" + (result == null ? "null" :  "not null") + "] ArtifactResults business object.");
		return result;		
	}
	
	@Override
	protected String getDataSourceVersion()
	{
		return "4";
	}
}
