/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jul 22, 2010
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

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.StudyURN;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.ImageFormatQualityList;
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.federation.rest.endpoints.FederationExternalSystemOperationsRestUri;
import gov.va.med.imaging.federation.rest.proxy.AbstractFederationRestProxy;
import gov.va.med.imaging.federation.rest.proxy.FederationRestGetClient;
import gov.va.med.imaging.federation.rest.proxy.FederationRestPostClient;
import gov.va.med.imaging.federation.rest.translator.FederationRestTranslator;
import gov.va.med.imaging.federation.rest.types.FederationFilterType;
import gov.va.med.imaging.federation.rest.types.FederationImageFormatQualitiesType;
import gov.va.med.imaging.federationdatasource.configuration.FederationConfiguration;
import gov.va.med.imaging.proxy.services.ProxyServiceType;
import gov.va.med.imaging.proxy.services.ProxyServices;
import gov.va.med.imaging.rest.types.RestBooleanReturnType;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

/**
 * @author vhaiswwerfej
 *
 */
public class FederationRestExternalSystemOperationProxyV4
extends AbstractFederationRestProxy
{

	public FederationRestExternalSystemOperationProxyV4(ProxyServices proxyServices, 
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
		return FederationExternalSystemOperationsRestUri.externalSystemOperationsServicePath;
	}
	
	public boolean initiateExamPrefetchOperation(StudyURN studyUrn) 
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		getLogger().info("initiateExamPrefetchOperation, Transaction [" + transactionContext.getTransactionId() + "] initiated, study URN '" + studyUrn.toString() + "'.");
		setDataSourceMethodAndVersion("initiateExamPrefetchOperation");
		Map<String, String> urlParameterKeyValues = new HashMap<String, String>();
		urlParameterKeyValues.put("{examId}", encodeGai(studyUrn));
		
		String url = getWebResourceUrl(FederationExternalSystemOperationsRestUri.prefetchExam, 
				urlParameterKeyValues );
		FederationRestGetClient getClient = new FederationRestGetClient(url, MediaType.APPLICATION_XML_TYPE, 
				federationConfiguration);
		RestBooleanReturnType result = getClient.executeRequest(RestBooleanReturnType.class);
		getLogger().info("initiateExamPrefetchOperation, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (result == null ? "null" : result.isResult()) + "] result.");
		return result.isResult();
	}
	
	public void refreshSiteServiceCache() 
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		getLogger().info("refreshSiteServiceCache, Transaction [" + transactionContext.getTransactionId() + "] initiated.");
		setDataSourceMethodAndVersion("refreshSiteServiceCache");
		Map<String, String> urlParameterKeyValues = new HashMap<String, String>();
		
		String url = getWebResourceUrl(FederationExternalSystemOperationsRestUri.refreshSiteServiceCache, 
				urlParameterKeyValues );
		FederationRestGetClient getClient = new FederationRestGetClient(url, MediaType.APPLICATION_XML_TYPE, 
				federationConfiguration);
		RestBooleanReturnType result = getClient.executeRequest(RestBooleanReturnType.class);
		getLogger().info("refreshSiteServiceCache, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (result == null ? "null" : result.isResult()) + "] result.");
	}
	
	public boolean prefetchPatientStudies(String patientIcn, StudyFilter filter, 
			RoutingToken routingToken, StudyLoadLevel studyLoadLevel)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		getLogger().info("prefetchPatientStudies, Transaction [" + transactionContext.getTransactionId() + "] initiated, patient '" + patientIcn + "' to '" + routingToken.toRoutingTokenString() + "'.");
		setDataSourceMethodAndVersion("prefetchPatientStudies");
		Map<String, String> urlParameterKeyValues = new HashMap<String, String>();
		urlParameterKeyValues.put("{routingToken}", routingToken.toRoutingTokenString());
		urlParameterKeyValues.put("{patientIcn}", patientIcn);
		urlParameterKeyValues.put("{authorizedSensitiveLevel}", filter.getMaximumAllowedLevel().getCode() + "");
		urlParameterKeyValues.put("{studyLoadLevel}", FederationRestTranslator.translate(studyLoadLevel).name());
		
		String url = getWebResourceUrl(FederationExternalSystemOperationsRestUri.prefetchStudiesPath, urlParameterKeyValues ); 
		FederationFilterType federationFilter = FederationRestTranslator.translate(filter);
		FederationRestPostClient postClient = new FederationRestPostClient(url, MediaType.APPLICATION_XML_TYPE, federationConfiguration);
		RestBooleanReturnType result = postClient.executeRequest(RestBooleanReturnType.class, federationFilter);
		getLogger().info("prefetchPatientStudies, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (result == null ? "null" : result.isResult()) + "] prefetch request result.");
		if(result == null)
		{
			return false;			
		}
		else
		{
			return result.isResult();
		}	
	}
	
	public boolean prefetchExamImage(ImageURN imageUrn, ImageFormatQualityList imageFormatQualityList,
			boolean includeTextFile)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		getLogger().info("prefetchExamImage, Transaction [" + transactionContext.getTransactionId() + "] initiated, image '" + 
				imageUrn.toString() + "' with quality list '" + imageFormatQualityList.getAcceptString(true, true) + "'.");
		setDataSourceMethodAndVersion("prefetchExamImage");
		Map<String, String> urlParameterKeyValues = new HashMap<String, String>();
		urlParameterKeyValues.put("{imageUrn}", encodeGai(imageUrn));
		urlParameterKeyValues.put("{includeTextFile}", includeTextFile + "");
		
		String url = getWebResourceUrl(FederationExternalSystemOperationsRestUri.prefetchExamImage, 
				urlParameterKeyValues );
		FederationImageFormatQualitiesType imageFormatQualityType = 
			FederationRestTranslator.translate(imageFormatQualityList);
		FederationRestPostClient postClient = new FederationRestPostClient(url, MediaType.APPLICATION_XML_TYPE, 
				federationConfiguration);
		RestBooleanReturnType result = postClient.executeRequest(RestBooleanReturnType.class, imageFormatQualityType);
		getLogger().info("prefetchExamImage, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (result == null ? "null" : result.isResult()) + "] prefetch request result.");
		if(result == null)
		{
			return false;			
		}
		else
		{
			return result.isResult();
		}
	}
	
	public boolean prefetchImage(ImageURN imageUrn, ImageFormatQualityList imageFormatQualityList)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		getLogger().info("prefetchImage, Transaction [" + transactionContext.getTransactionId() + "] initiated, image '" + 
				imageUrn.toString() + "' with quality list '" + imageFormatQualityList.getAcceptString(true, true) + "'.");
		setDataSourceMethodAndVersion("prefetchImage");
		Map<String, String> urlParameterKeyValues = new HashMap<String, String>();
		urlParameterKeyValues.put("{imageUrn}", encodeGai(imageUrn));
		FederationImageFormatQualitiesType imageFormatQualityType = 
			FederationRestTranslator.translate(imageFormatQualityList);		
		
		String url = getWebResourceUrl(FederationExternalSystemOperationsRestUri.prefetchImage, 
				urlParameterKeyValues ); 
		FederationRestPostClient postClient = new FederationRestPostClient(url, MediaType.APPLICATION_XML_TYPE, 
				federationConfiguration);
		RestBooleanReturnType result = postClient.executeRequest(RestBooleanReturnType.class, 
				imageFormatQualityType);
		getLogger().info("prefetchImage, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (result == null ? "null" : result.isResult()) + "] prefetch request result.");
		if(result == null)
		{
			return false;			
		}
		else
		{
			return result.isResult();
		}
	}
	
	public boolean prefetchGai(GlobalArtifactIdentifier gai)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		getLogger().info("prefetchGai, Transaction [" + transactionContext.getTransactionId() + "] initiated, GAI '" + 
				gai.toString() + "'.");
		setDataSourceMethodAndVersion("prefetchImage");
		Map<String, String> urlParameterKeyValues = new HashMap<String, String>();		
		urlParameterKeyValues.put("{gai}", encodeGai(gai));				
		
		String url = getWebResourceUrl(FederationExternalSystemOperationsRestUri.prefetchGai, 
				urlParameterKeyValues ); 
		FederationRestGetClient getClient = new FederationRestGetClient(url, MediaType.APPLICATION_XML_TYPE, 
				federationConfiguration);
		RestBooleanReturnType result = getClient.executeRequest(RestBooleanReturnType.class);
		getLogger().info("prefetchGai, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (result == null ? "null" : result.isResult()) + "] prefetch request result.");
		if(result == null)
		{
			return false;			
		}
		else
		{
			return result.isResult();
		}
	}
	
	@Override
	protected String getDataSourceVersion()
	{
		return "4";
	}
}
