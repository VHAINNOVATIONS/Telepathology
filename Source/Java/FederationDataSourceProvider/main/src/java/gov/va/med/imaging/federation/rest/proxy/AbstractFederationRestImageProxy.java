/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 20, 2010
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
package gov.va.med.imaging.federation.rest.proxy;

import java.util.Map;

import org.apache.log4j.Logger;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.federation.proxy.AbstractFederationProxy;
import gov.va.med.imaging.federation.proxy.IFederationProxy;
import gov.va.med.imaging.federationdatasource.configuration.FederationConfiguration;
import gov.va.med.imaging.proxy.rest.RestProxyCommon;
import gov.va.med.imaging.proxy.services.ProxyServiceType;
import gov.va.med.imaging.proxy.services.ProxyServices;

/**
 * @author vhaiswwerfej
 *
 */
public abstract class AbstractFederationRestImageProxy 
extends AbstractFederationProxy 
implements IFederationProxy 
{	
	protected Logger getLogger()
	{
		return logger;
	}
	
	public AbstractFederationRestImageProxy(ProxyServices proxyServices, 
			FederationConfiguration federationConfiguration)
	{
		super(proxyServices, federationConfiguration);
	}
	
	protected abstract String getRestServicePath();
	protected abstract ProxyServiceType getProxyServiceType();
	
	protected String getWebResourceUrl(String methodUri, Map<String, String> urlParameterKeyValues)
	throws ConnectionException
	{
		StringBuilder url = new StringBuilder();
		url.append(proxyServices.getProxyService(getProxyServiceType()).getConnectionURL());
		//url.append("http://localhost:8080/FederationWebApp/restservices/");
		url.append(getRestServicePath());
		url.append("/");
		url.append(RestProxyCommon.replaceMethodUriWithValues(methodUri, urlParameterKeyValues));		
		
		return url.toString();
	}
}
