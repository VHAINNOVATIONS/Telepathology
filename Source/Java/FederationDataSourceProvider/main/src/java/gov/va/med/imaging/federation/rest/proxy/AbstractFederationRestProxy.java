/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 15, 2010
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.SERIALIZATION_FORMAT;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.federation.proxy.IFederationProxy;
import gov.va.med.imaging.federationdatasource.configuration.FederationConfiguration;
import gov.va.med.imaging.proxy.services.ProxyServiceType;
import gov.va.med.imaging.proxy.services.ProxyServices;

import org.apache.commons.httpclient.methods.GetMethod;

/**
 * @author vhaiswwerfej
 *
 */
public abstract class AbstractFederationRestProxy 
extends AbstractFederationRestImageProxy 
implements IFederationProxy 
{	
	public AbstractFederationRestProxy(ProxyServices proxyServices, 
			FederationConfiguration federationConfiguration)
	{
		super(proxyServices, federationConfiguration);
	}
	
	@Override
	protected void addOptionalGetInstanceHeaders(GetMethod getMethod) 
	{
		// no image access from here
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
	
	private final static String utf8 = "UTF-8"; 
	protected String encodeGai(GlobalArtifactIdentifier gai)
	throws MethodException
	{
		return encodeString(gai.toString(SERIALIZATION_FORMAT.RAW));
	}
	
	protected String encodeString(String value)
	throws MethodException
	{
		try
		{
			return URLEncoder.encode(value, utf8);
		}
		catch(UnsupportedEncodingException ueX)
		{
			throw new MethodException(ueX);
		}
	}
}
