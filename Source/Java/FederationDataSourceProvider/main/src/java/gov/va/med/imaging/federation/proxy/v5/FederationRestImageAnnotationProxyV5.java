/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 20, 2011
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
package gov.va.med.imaging.federation.proxy.v5;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.methods.GetMethod;

import gov.va.med.imaging.AbstractImagingURN;
import gov.va.med.imaging.ImageAnnotationURN;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.business.annotations.ImageAnnotation;
import gov.va.med.imaging.exchange.business.annotations.ImageAnnotationDetails;
import gov.va.med.imaging.exchange.business.annotations.ImageAnnotationSource;
import gov.va.med.imaging.federation.rest.endpoints.FederationImageAnnotationRestUri;
import gov.va.med.imaging.federation.rest.proxy.AbstractFederationRestImageProxy;
import gov.va.med.imaging.federation.rest.proxy.FederationRestGetClient;
import gov.va.med.imaging.federation.rest.proxy.FederationRestPostClient;
import gov.va.med.imaging.federation.rest.translator.FederationRestTranslator;
import gov.va.med.imaging.federation.rest.types.FederationImageAnnotationDetailsType;
import gov.va.med.imaging.federation.rest.types.FederationImageAnnotationType;
import gov.va.med.imaging.federationdatasource.configuration.FederationConfiguration;
import gov.va.med.imaging.proxy.services.ProxyServiceType;
import gov.va.med.imaging.proxy.services.ProxyServices;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * @author VHAISWWERFEJ
 *
 */
public class FederationRestImageAnnotationProxyV5
extends AbstractFederationRestImageProxy
{
	public FederationRestImageAnnotationProxyV5(ProxyServices proxyServices, 
			FederationConfiguration federationConfiguration)
	{
		super(proxyServices, federationConfiguration);
	}

	@Override
	protected String getRestServicePath()
	{
		return FederationImageAnnotationRestUri.imageAnnotationServicePath;
	}

	@Override
	protected ProxyServiceType getProxyServiceType()
	{
		return ProxyServiceType.metadata;
	}

	@Override
	protected String getDataSourceVersion()
	{
		return "5";
	}

	@Override
	protected void addOptionalGetInstanceHeaders(GetMethod getMethod)
	{
		// don't need to do anything here since image not actually loaded here
	}

	@Override
	protected ProxyServiceType getInstanceRequestProxyServiceType()
	{
		return ProxyServiceType.image;
	}

	@Override
	protected ProxyServiceType getTextFileRequestProxyServiceType()
	{
		return ProxyServiceType.text;
	}
	
	public List<ImageAnnotation> getImageAnnotations(AbstractImagingURN imagingUrn)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		getLogger().info("getImageAnnotations, Transaction [" + transactionContext.getTransactionId() + "] initiated, image '" + imagingUrn.toString() + "'.");
		setDataSourceMethodAndVersion("getImageAnnotations");
		Map<String, String> urlParameterKeyValues = new HashMap<String, String>();
		urlParameterKeyValues.put("{imagingUrn}", imagingUrn.toString());
		String url = getWebResourceUrl(FederationImageAnnotationRestUri.imageAnnotationsPath, urlParameterKeyValues);
		FederationRestGetClient getClient = new FederationRestGetClient(url, MediaType.APPLICATION_XML_TYPE, federationConfiguration);		
		FederationImageAnnotationType [] imageAnnotations = getClient.executeRequest(FederationImageAnnotationType[].class);
		getLogger().info("getImageAnnotations, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (imageAnnotations == null ? "null" :  "not null") + "] patient sensitivity webservice object.");
		List<ImageAnnotation> result = null;
		try
		{
			result = FederationRestTranslator.translate(imageAnnotations);
		}
		catch(URNFormatException urnfX)
		{
			logger.error("URNFormatException translating image annotations", urnfX);
			throw new MethodException(urnfX);
		}
		getLogger().info("getImageAnnotations, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (result == null ? "null" : result.size()) + "] image annotations.");
		
		return result;
	}
	
	public ImageAnnotationDetails getAnnotationDetails(
			AbstractImagingURN imagingUrn,
			ImageAnnotationURN imageAnnotationUrn) 
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		getLogger().info("getAnnotationDetails, Transaction [" + transactionContext.getTransactionId() + "] initiated, image annotation '" + imageAnnotationUrn.toString() + "'.");
		setDataSourceMethodAndVersion("getAnnotationDetails");
		Map<String, String> urlParameterKeyValues = new HashMap<String, String>();
		urlParameterKeyValues.put("{imagingUrn}", imagingUrn.toString());
		urlParameterKeyValues.put("{imageAnnotationUrn}", imageAnnotationUrn.toString());
		String url = getWebResourceUrl(FederationImageAnnotationRestUri.imageAnnotationDetailsPath, urlParameterKeyValues);
		FederationRestGetClient getClient = new FederationRestGetClient(url, MediaType.APPLICATION_XML_TYPE, federationConfiguration);
		
		FederationImageAnnotationDetailsType imageAnnotationDetails = 
			getClient.executeRequest(FederationImageAnnotationDetailsType.class);
		
		getLogger().info("getAnnotationDetails, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (imageAnnotationDetails == null ? "null" :  "not null") + "] annotation details webservice object.");
		ImageAnnotationDetails result = null;
		try
		{
			result = FederationRestTranslator.translate(imageAnnotationDetails);
		}
		catch(URNFormatException urnfX)
		{
			logger.error("URNFormatException translating image annotation details", urnfX);
			throw new MethodException(urnfX);
		}
		getLogger().info("getAnnotationDetails, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (result == null ? "null" : "not null") + "] image annotation details.");
		
		return result;
	}
	
	public ImageAnnotation storeImageAnnotationDetails(AbstractImagingURN imagingUrn,
			String annotationDetails, String annotationVersion, ImageAnnotationSource annotationSource)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		getLogger().info("storeImageAnnotationDetails, Transaction [" + transactionContext.getTransactionId() + "] initiated, image '" + imagingUrn.toString() + "'.");
		setDataSourceMethodAndVersion("storeImageAnnotationDetails");
		Map<String, String> urlParameterKeyValues = new HashMap<String, String>();
		urlParameterKeyValues.put("{imagingUrn}", imagingUrn.toString());
		urlParameterKeyValues.put("{version}", annotationVersion);
		urlParameterKeyValues.put("{source}", FederationRestTranslator.translate(annotationSource).name());
		String url = getWebResourceUrl(FederationImageAnnotationRestUri.storeImageAnnotationPath, urlParameterKeyValues);
		FederationRestPostClient postClient = new FederationRestPostClient(url, MediaType.APPLICATION_XML_TYPE, federationConfiguration);
		
		FederationImageAnnotationType imageAnnotation = 
			postClient.executeRequest(FederationImageAnnotationType.class, annotationDetails);
		
		getLogger().info("storeImageAnnotationDetails, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (imageAnnotation == null ? "null" :  "not null") + "] image annotation webservice object.");
		ImageAnnotation result = null;
		try
		{
			result = FederationRestTranslator.translate(imageAnnotation);
		}
		catch(URNFormatException urnfX)
		{
			logger.error("URNFormatException translating image annotation", urnfX);
			throw new MethodException(urnfX);
		}
		getLogger().info("storeImageAnnotationDetails, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (result == null ? "null" : "not null") + "] image annotation.");
		
		return result;
	}

}
