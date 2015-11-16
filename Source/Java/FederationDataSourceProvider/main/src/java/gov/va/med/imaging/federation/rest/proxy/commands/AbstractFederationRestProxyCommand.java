/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 21, 2012
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
package gov.va.med.imaging.federation.rest.proxy.commands;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.federation.rest.proxy.AbstractFederationRestProxy;
import gov.va.med.imaging.federationdatasource.configuration.FederationConfiguration;
import gov.va.med.imaging.proxy.services.ProxyServices;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

import java.util.Map;

/**
 * @author VHAISWWERFEJ
 *
 */
public abstract class AbstractFederationRestProxyCommand<R, T extends Object>
extends AbstractFederationRestProxy
{

	private final String methodName;
	
	/**
	 * @param proxyServices
	 * @param federationConfiguration
	 */
	public AbstractFederationRestProxyCommand(String methodName, ProxyServices proxyServices,
			FederationConfiguration federationConfiguration)
	{
		super(proxyServices, federationConfiguration);
		this.methodName = methodName;
	}
	
	public String getMethodName()
	{
		return methodName;
	}
	
	protected abstract String getMethodParametersDescription();
	
	protected abstract String getMethodUri();
	
	protected abstract Map<String, String> getUrlParametersKeyValues();
	
	protected abstract Class<R> getWebServiceResultClass();
	
	protected abstract R executeRequest(String url, Class<R> webServiceResultClass)
	throws MethodException, ConnectionException;
	
	protected abstract T translateWebServiceResult(R webServiceResult)
	throws MethodException;
	
	protected abstract String getTranslatedResultDescription(T result);

	public T execute()
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		String methodParameterDescription = getMethodParametersDescription();
		
		getLogger().info(getMethodName() + ", Transaction [" + transactionContext.getTransactionId() + "] " + (methodParameterDescription == null ? "." : methodParameterDescription));
		setDataSourceMethodAndVersion(getMethodName());
		Map<String, String> urlParameterKeyValues = getUrlParametersKeyValues();
		String url = getWebResourceUrl(getMethodUri(), urlParameterKeyValues);
		R webServiceResult = executeRequest(url, getWebServiceResultClass());
		getLogger().info(getMethodName() + ", Transaction [" + transactionContext.getTransactionId() + "] returned [" + (webServiceResult == null ? "null" :  "not null") + "] result webservice object.");
		T result = null;
		if(webServiceResult != null)
			result = translateWebServiceResult(webServiceResult);
		String translatedResultDescription = getTranslatedResultDescription(result);
		getLogger().info(getMethodName() + ", Transaction [" + transactionContext.getTransactionId() + "] " + (translatedResultDescription == null ? "." : translatedResultDescription));
		
		return result;
		
	}

}
