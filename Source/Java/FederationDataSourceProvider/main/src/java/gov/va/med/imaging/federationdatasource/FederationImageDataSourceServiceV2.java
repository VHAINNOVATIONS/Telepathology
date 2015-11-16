/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 16, 2009
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
import gov.va.med.imaging.federation.proxy.FederationProxyV2;
import gov.va.med.imaging.federation.proxy.IFederationProxy;
import gov.va.med.imaging.proxy.services.ProxyServices;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * @author vhaiswwerfej
 *
 */
public class FederationImageDataSourceServiceV2 
extends AbstractFederationImageDataSourceService
{
	private final static String DATASOURCE_VERSION = "2";
	
	private FederationProxyV2 proxy = null;
	
	/**
	 * @param resolvedArtifactSource
	 * @param protocol
	 * @throws UnsupportedOperationException
	 */
	public FederationImageDataSourceServiceV2(ResolvedArtifactSource resolvedArtifactSource, String protocol)
		throws UnsupportedOperationException
	{
		super(resolvedArtifactSource, protocol);
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

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federationdatasource.AbstractFederationImageDataSourceService#getDataSourceVersion()
	 */
	@Override
	public String getDataSourceVersion() 
	{
		return DATASOURCE_VERSION;
	}

	private FederationProxyV2 getProxy()
	throws ConnectionException
	{
		if(proxy == null)
		{
			ProxyServices proxyServices = getFederationProxyServices();
			if(proxyServices == null)
				throw new ConnectionException("Did not receive any applicable services from IDS service for site [" + getSite().getSiteNumber() + "]");
			proxy = new FederationProxyV2(proxyServices, FederationDataSourceProvider.getFederationConfiguration());
		}
		return proxy;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.ImageDataSource#getImageDevFields(gov.va.med.imaging.AbstractImagingURN, java.lang.String)
	 */
	@Override
	public String getImageDevFields(AbstractImagingURN imagingUrn, String flags)
	throws MethodException, ConnectionException, ImageNotFoundException 
	{
		getLogger().info("getImageDevFields(" + imagingUrn.toString() + "), TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		String result = getProxy().getImageDevFields(imagingUrn, flags);
		TransactionContextFactory.get().setDataSourceBytesReceived(result == null ? 0L : result.length());
		getLogger().info("getImageDevFields complete.");
		return result;
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
		String result = getProxy().getImageInformation(imagingUrn);
		TransactionContextFactory.get().setDataSourceBytesReceived(result == null ? 0L : result.length());
		getLogger().info("getImageInformation complete.");
		return result;		
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.ImageDataSource#getImageSystemGlobalNode(gov.va.med.imaging.AbstractImagingURN)
	 */
	@Override
	public String getImageSystemGlobalNode(AbstractImagingURN imagingUrn)
	throws MethodException, ConnectionException, ImageNotFoundException 
	{
		getLogger().info("getImageSystemGlobalNode(" + imagingUrn.toString() + "), TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
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
