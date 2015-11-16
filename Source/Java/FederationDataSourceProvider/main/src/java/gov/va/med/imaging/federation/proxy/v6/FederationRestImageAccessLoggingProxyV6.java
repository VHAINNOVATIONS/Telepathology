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
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import gov.va.med.RoutingToken;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.ImagingLogEvent;
import gov.va.med.imaging.exchange.business.ImageAccessReason;
import gov.va.med.imaging.exchange.enums.ImageAccessReasonType;
import gov.va.med.imaging.federation.proxy.v5.FederationRestImageAccessLoggingProxyV5;
import gov.va.med.imaging.federation.rest.endpoints.FederationImageAccessLoggingUri;
import gov.va.med.imaging.federation.rest.proxy.RestPostClient;
import gov.va.med.imaging.federation.rest.translator.FederationRestTranslator;
import gov.va.med.imaging.federation.rest.types.FederationImageAccessReasonType;
import gov.va.med.imaging.federation.rest.types.FederationImageAccessReasonTypeHolderType;
import gov.va.med.imaging.federation.rest.types.FederationImagingLogEventType;
import gov.va.med.imaging.federationdatasource.configuration.FederationConfiguration;
import gov.va.med.imaging.proxy.services.ProxyServiceType;
import gov.va.med.imaging.proxy.services.ProxyServices;
import gov.va.med.imaging.rest.types.RestBooleanReturnType;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * @author VHAISWWERFEJ
 * @deprecated Version 6 of this interface is no longer used
 *
 */
public class FederationRestImageAccessLoggingProxyV6
extends FederationRestImageAccessLoggingProxyV5
{

	/**
	 * @param proxyServices
	 * @param federationConfiguration
	 */
	public FederationRestImageAccessLoggingProxyV6(ProxyServices proxyServices,
			FederationConfiguration federationConfiguration)
	{
		super(proxyServices, federationConfiguration);
	}
	
	@Override
	protected String getDataSourceVersion()
	{
		return "6";
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federation.rest.proxy.AbstractFederationRestImageProxy#getProxyServiceType()
	 */
	@Override
	protected ProxyServiceType getProxyServiceType()
	{
		return ProxyServiceType.imageAccessLogging;
	}

	public List<ImageAccessReason> getImageAccessReasons(
			RoutingToken globalRoutingToken,
			List<ImageAccessReasonType> reasonTypes) 
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		getLogger().info("getImageAccessReasons, Transaction [" + transactionContext.getTransactionId() + "] initiated, to site '" + globalRoutingToken.toRoutingTokenString() + "'");
		setDataSourceMethodAndVersion("getImageAccessReasons");
		Map<String, String> urlParameterKeyValues = new HashMap<String, String>();
		urlParameterKeyValues.put("{routingToken}", globalRoutingToken.toRoutingTokenString());
		
		FederationImageAccessReasonTypeHolderType reasonTypeHolder = FederationRestTranslator.translateReasonTypesToHolder(reasonTypes);
		String url = getWebResourceUrl(FederationImageAccessLoggingUri.reasonsPath, urlParameterKeyValues );
		RestPostClient postClient = new RestPostClient(url, MediaType.APPLICATION_XML_TYPE, federationConfiguration);
		FederationImageAccessReasonType [] reasons = 
			postClient.executeRequest(FederationImageAccessReasonType[].class, reasonTypeHolder);
		getLogger().info("getImageAccessReasons, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (reasons == null ? "null" : "not null") + "] image access reasons webservice objects.");
		try
		{
			List<ImageAccessReason> result = FederationRestTranslator.translate(reasons);		
			getLogger().info("getImageAccessReasons, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (result == null ? "null" : result.size()) + "] image access reasons.");
			return result;
		}
		catch(RoutingTokenFormatException rtfX)
		{
			throw new MethodException(rtfX);
		}
	}
	
	public void LogImagingLogEvent(ImagingLogEvent logEvent)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		getLogger().info("LogImagingLogEvent, Transaction [" + transactionContext.getTransactionId() + "] initiated, logEvent [" + logEvent.toString() + "]");
		setDataSourceMethodAndVersion("LogImagingLogEvent");
		Map<String, String> urlParameterKeyValues = new HashMap<String, String>();
		
		FederationImagingLogEventType federationLogEvent = FederationRestTranslator.translate(logEvent);		
		String url = getWebResourceUrl(FederationImageAccessLoggingUri.logImagingEvent, urlParameterKeyValues );
		RestPostClient postClient = new RestPostClient(url, MediaType.APPLICATION_XML_TYPE, federationConfiguration);
		RestBooleanReturnType result = 
			postClient.executeRequest(RestBooleanReturnType.class, federationLogEvent);
		getLogger().info("LogImagingLogEvent, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (result == null ? "null" : result.isResult()) + "] result for logging imaging event.");
	}
}
