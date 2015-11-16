/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jul 28, 2009
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
package gov.va.med.imaging.federation.proxy;

import gov.va.med.imaging.proxy.ids.IDSOperation;
import gov.va.med.imaging.proxy.ids.IDSService;
import gov.va.med.imaging.proxy.services.AbstractProxyService;
import gov.va.med.imaging.proxy.services.ProxyService;
import gov.va.med.imaging.proxy.services.ProxyServiceType;

/**
 * Creates a ProxyService based on the results of an IDS query
 * 
 * @author vhaiswwerfej
 *
 */
public class FederationProxyService 
extends AbstractProxyService
implements ProxyService 
{
	private final static String  defaultFederationProtocol = "https";
	private final static String defaultFederationUsername = null;
	private final static String defaultFederationPassword = null;
	private final static int defaultFederationSslPort = 8443;
	
	public FederationProxyService(IDSService idsServce, IDSOperation idsOperation, String host)
	{
		this.applicationPath = idsServce.getApplicationPath();
		this.host = host;
		this.port = defaultFederationSslPort;
		this.operationPath = idsOperation.getOperationPath();
		this.proxyServiceType = ProxyServiceType.getProxyServiceTypeFromIDSOperation(idsOperation);
		this.uid = defaultFederationUsername;
		this.credentials = defaultFederationPassword;
		this.protocol = defaultFederationProtocol;
	}
}
