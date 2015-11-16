/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 27, 2010
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
import java.util.zip.Checksum;

import javax.ws.rs.core.MediaType;

import gov.va.med.imaging.AbstractImagingURN;
import gov.va.med.imaging.SizedInputStream;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageConversionException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNearLineException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNotFoundException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityCredentialsExpiredException;
import gov.va.med.imaging.exchange.business.ImageFormatQualityList;
import gov.va.med.imaging.federation.rest.endpoints.FederationImageRestUri;
import gov.va.med.imaging.federation.rest.proxy.AbstractFederationRestImageProxy;
import gov.va.med.imaging.federation.rest.proxy.FederationRestGetClient;
import gov.va.med.imaging.federationdatasource.configuration.FederationConfiguration;
import gov.va.med.imaging.proxy.services.ProxyServiceType;
import gov.va.med.imaging.proxy.services.ProxyServices;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.transactioncontext.TransactionContextHttpHeaders;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * @author vhaiswwerfej
 *
 */
public class FederationRestImageProxyV4 
extends AbstractFederationRestImageProxy
{
	public FederationRestImageProxyV4(ProxyServices proxyServices, 
			FederationConfiguration federationConfiguration)
	{
		super(proxyServices, federationConfiguration);
	}
	
	public String getImageInformation(AbstractImagingURN imagingUrn, boolean includeDeletedImages)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();		
		getLogger().info("getImageInformation, Transaction [" + transactionContext.getTransactionId() + "] initiated, image Urn '" + imagingUrn.toString() + "'");
		setDataSourceMethodAndVersion("getImageInformation");
		Map<String, String> urlParameterKeyValues = new HashMap<String, String>();
		urlParameterKeyValues.put("{imageUrn}", imagingUrn.toString());
		urlParameterKeyValues.put("{includeDeletedImages}", includeDeletedImages + "");
		
		String url = getWebResourceUrl(FederationImageRestUri.imageInformationMethodPath, urlParameterKeyValues );
		FederationRestGetClient getClient = new FederationRestGetClient(url, MediaType.APPLICATION_XML_TYPE, federationConfiguration);
		String result = getClient.executeRequest(String.class);
		
		getLogger().info("getImageInformation, Transaction [" + transactionContext.getTransactionId() + "] returned response of length [" + (result == null ? "null" : result.length()) + "] bytes.");
		return result;
	}
	
	public String getImageSystemGlobalNode(AbstractImagingURN imagingUrn)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		setDataSourceMethodAndVersion("getImageSystemGlobalNode");
		getLogger().info("getImageSystemGlobalNode, Transaction [" + transactionContext.getTransactionId() + "] initiated, image Urn '" + imagingUrn.toString() + "'");
		Map<String, String> urlParameterKeyValues = new HashMap<String, String>();
		urlParameterKeyValues.put("{imageUrn}", imagingUrn.toString());
		
		String url = getWebResourceUrl(FederationImageRestUri.imageGlobalNodesMethodPath, urlParameterKeyValues );
		FederationRestGetClient getClient = new FederationRestGetClient(url, MediaType.APPLICATION_XML_TYPE, federationConfiguration);
		String result = getClient.executeRequest(String.class);
		
		getLogger().info("getImageSystemGlobalNode, Transaction [" + transactionContext.getTransactionId() + "] returned response of length [" + (result == null ? "null" : result.length()) + "] bytes.");
		return result;
	}
	
	public String getImageDevFields(AbstractImagingURN imagingUrn, String flags)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		setDataSourceMethodAndVersion("getImageDevFields");
		getLogger().info("getImageDevFields, Transaction [" + transactionContext.getTransactionId() + "] initiated, image Urn '" + imagingUrn.toString() + "'");
		Map<String, String> urlParameterKeyValues = new HashMap<String, String>();
		urlParameterKeyValues.put("{imageUrn}", imagingUrn.toString());
		//urlParameterKeyValues.put("{flags}", flags);
		
		String url = getWebResourceUrl(FederationImageRestUri.imageDevFieldsMethodPath + "?flags=" + flags, 
				urlParameterKeyValues );
		
		FederationRestGetClient getClient = new FederationRestGetClient(url, MediaType.APPLICATION_XML_TYPE, federationConfiguration);
		String result = getClient.executeRequest(String.class);
		
		getLogger().info("getImageDevFields, Transaction [" + transactionContext.getTransactionId() + "] returned response of length [" + (result == null ? "null" : result.length()) + "] bytes.");
		return result;
	}
	
	private ImageFormatQualityList currentImageFormatQualityList = null;
	
	
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
		return FederationImageRestUri.imageServicePath;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.proxy.ImagingProxy#addOptionalGetInstanceHeaders(org.apache.commons.httpclient.methods.GetMethod)
	 */
	@Override
	protected void addOptionalGetInstanceHeaders(GetMethod getMethod)
	{
		if(currentImageFormatQualityList != null)
		{
			String headerValue = currentImageFormatQualityList.getAcceptString(false, true); 
			getLogger().debug("Adding content type with sub type header value [" + headerValue + "]");
			getMethod.setRequestHeader(new Header(TransactionContextHttpHeaders.httpHeaderContentTypeWithSubType, 
				headerValue));
		}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.proxy.ImagingProxy#getInstanceRequestProxyServiceType()
	 */
	@Override
	protected ProxyServiceType getInstanceRequestProxyServiceType()
	{
		return ProxyServiceType.image;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.proxy.ImagingProxy#getTextFileRequestProxyServiceType()
	 */
	@Override
	protected ProxyServiceType getTextFileRequestProxyServiceType()
	{
		return ProxyServiceType.text;
	}

	/**
	 * Override to encode the study and image IDs in Base32
	 */
	@Override
	public SizedInputStream getInstance(
		String imageUrn, 
		ImageFormatQualityList requestFormatQualityList, 
		Checksum checksum, 
		boolean includeVistaSecurityContext) 
	throws ImageNearLineException, ImageNotFoundException, 
	SecurityCredentialsExpiredException, ImageConversionException, ConnectionException, MethodException
	{
		try
		{
			currentImageFormatQualityList = requestFormatQualityList;
			setDataSourceMethodAndVersion("getInstance");
			// don't need to base 32 encode the URN for version 4
			return super.getInstance(imageUrn, requestFormatQualityList, checksum, includeVistaSecurityContext);
		}
		finally
		{
			currentImageFormatQualityList = null;
		}
	}

	@Override
	protected String getDataSourceVersion()
	{
		return "4";
	}	
}
