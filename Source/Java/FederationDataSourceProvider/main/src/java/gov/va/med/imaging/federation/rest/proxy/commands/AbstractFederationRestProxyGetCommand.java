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

import javax.ws.rs.core.MediaType;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.federation.rest.proxy.FederationRestGetClient;
import gov.va.med.imaging.federationdatasource.configuration.FederationConfiguration;
import gov.va.med.imaging.proxy.services.ProxyServices;

/**
 * @author VHAISWWERFEJ
 *
 */
public abstract class AbstractFederationRestProxyGetCommand<R, T extends Object>
extends AbstractFederationRestProxyCommand<R, T>
{

	/**
	 * @param methodName
	 * @param proxyServices
	 * @param federationConfiguration
	 */
	public AbstractFederationRestProxyGetCommand(String methodName,
			ProxyServices proxyServices,
			FederationConfiguration federationConfiguration)
	{
		super(methodName, proxyServices, federationConfiguration);
	}

	@Override
	protected R executeRequest(String url, Class<R> webServiceResultClass) 
	throws MethodException, ConnectionException
	{
		FederationRestGetClient getClient = new FederationRestGetClient(url, MediaType.APPLICATION_XML_TYPE, federationConfiguration);
		R webServiceResult = getClient.executeRequest(webServiceResultClass);
		return webServiceResult;
	}

}
