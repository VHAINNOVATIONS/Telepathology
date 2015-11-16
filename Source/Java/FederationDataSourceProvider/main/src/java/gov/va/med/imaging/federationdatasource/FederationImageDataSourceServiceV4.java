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
package gov.va.med.imaging.federationdatasource;

import gov.va.med.imaging.AbstractImagingURN;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNotFoundException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.federation.proxy.IFederationProxy;
import gov.va.med.imaging.federation.proxy.v4.FederationRestImageProxyV4;
import gov.va.med.imaging.proxy.services.ProxyServiceType;
import gov.va.med.imaging.proxy.services.ProxyServices;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * @author vhaiswwerfej
 *
 */
public class FederationImageDataSourceServiceV4 
extends AbstractFederationImageDataSourceService
{
	private final static String DATASOURCE_VERSION = "4";
	
	private FederationRestImageProxyV4 proxy = null;

	/**
	 * @param resolvedArtifactSource
	 * @param protocol
	 * @throws UnsupportedOperationException
	 */
	public FederationImageDataSourceServiceV4(ResolvedArtifactSource resolvedArtifactSource, String protocol)
		throws UnsupportedOperationException
	{
		super(resolvedArtifactSource, protocol);
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federationdatasource.AbstractFederationImageDataSourceService#getDataSourceVersion()
	 */
	@Override
	public String getDataSourceVersion()
	{
		return DATASOURCE_VERSION;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federationdatasource.AbstractFederationImageDataSourceService#getFederationProxy()
	 */
	@Override
	protected IFederationProxy getFederationProxy() 
	throws ConnectionException
	{
		return getProxy();
	}
	
	private FederationRestImageProxyV4 getProxy()
	throws ConnectionException
	{
		if(proxy == null)
		{
			ProxyServices proxyServices = getFederationProxyServices();
			if(proxyServices == null)
				throw new ConnectionException("Did not receive any applicable services from IDS service for site [" + getSite().getSiteNumber() + "]");
			proxy = new FederationRestImageProxyV4(proxyServices, FederationDataSourceProvider.getFederationConfiguration());
		}
		return proxy;
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.PassthroughDataSource#isVersionCompatible()
	 */
	@Override
	public boolean isVersionCompatible() 
	{
		if(getFederationProxyServices() == null)			
			return false;
		ProxyServiceType serviceType = ProxyServiceType.metadata;
		try
		{
			
			getLogger().debug("Found FederationProxyServices, looking for '" + serviceType + "' service type at site [" + getSite().getSiteNumber() + "].");
			getFederationProxyServices().getProxyService(serviceType);
			getLogger().debug("Found service type '" + serviceType + "' at site [" + getSite().getSiteNumber() + "], returning true for version compatible.");
			return true;
		}
		catch(gov.va.med.imaging.proxy.exceptions.ProxyServiceNotFoundException psnfX)
		{
			getLogger().warn("Cannot find proxy service type '" + serviceType + "' at site [" + getSite().getSiteNumber() + "]");
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.ImageDataSource#getImageInformation(gov.va.med.imaging.AbstractImagingURN)
	 */
	@Override
	public String getImageInformation(AbstractImagingURN imagingUrn, boolean includeDeletedImages)
	throws MethodException, ConnectionException, ImageNotFoundException 
	{
		getLogger().info("getImageInformation(" + imagingUrn.toString() + "), TransactionContext (" + 
				TransactionContextFactory.get().getDisplayIdentity() + ").");		
		String result = getProxy().getImageInformation(imagingUrn, includeDeletedImages);
		TransactionContextFactory.get().setDataSourceBytesReceived(result == null ? 0L : result.length());
		getLogger().info("getImageInformation complete.");
		return result;	
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.ImageDataSource#getImageDevFields(gov.va.med.imaging.AbstractImagingURN, java.lang.String)
	 */
	@Override
	public String getImageDevFields(AbstractImagingURN imagingUrn, String flags)
	throws MethodException, ConnectionException, ImageNotFoundException 
	{
		getLogger().info("getImageDevFields(" + imagingUrn.toString() + "), TransactionContext (" + 
				TransactionContextFactory.get().getDisplayIdentity() + ").");
		String result = getProxy().getImageDevFields(imagingUrn, flags);
		TransactionContextFactory.get().setDataSourceBytesReceived(result == null ? 0L : result.length());
		getLogger().info("getImageDevFields complete.");
		return result;
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.ImageDataSource#getImageSystemGlobalNode(gov.va.med.imaging.AbstractImagingURN)
	 */
	@Override
	public String getImageSystemGlobalNode(AbstractImagingURN imagingUrn)
	throws MethodException, ConnectionException, ImageNotFoundException 
	{
		getLogger().info("getImageSystemGlobalNode(" + imagingUrn.toString() + "), TransactionContext (" + 
				TransactionContextFactory.get().getDisplayIdentity() + ").");
		String result = getProxy().getImageSystemGlobalNode(imagingUrn);
		TransactionContextFactory.get().setDataSourceBytesReceived(result == null ? 0L : result.length());
		getLogger().info("getImageSystemGlobalNode complete.");
		return result;
	}
	
	@Override
	protected boolean canGetTextFile()
	{
		return true;
	}
}
