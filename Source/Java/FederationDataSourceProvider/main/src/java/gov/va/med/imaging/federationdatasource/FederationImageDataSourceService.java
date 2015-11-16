/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 3, 2008
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
package gov.va.med.imaging.federationdatasource;

import gov.va.med.imaging.AbstractImagingURN;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNotFoundException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.federation.proxy.FederationProxy;
import gov.va.med.imaging.federation.proxy.IFederationProxy;
import gov.va.med.imaging.proxy.services.ProxyServices;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.federation.exceptions.FederationConnectionException;
import gov.va.med.imaging.url.federation.exceptions.FederationMethodException;

import java.io.IOException;

import javax.xml.rpc.ServiceException;

/**
 * @author VHAISWWERFEJ
 *
 */
public class FederationImageDataSourceService 
extends AbstractFederationImageDataSourceService
{	
	// Versioning fields
	private final static String DATASOURCE_VERSION = "0";	
	
	private FederationProxy proxy = null;
	
	/**
	 * @param resolvedArtifactSource
	 * @param protocol
	 * @throws UnsupportedOperationException
	 */
	public FederationImageDataSourceService(ResolvedArtifactSource resolvedArtifactSource, String protocol)
		throws UnsupportedOperationException
	{
		super(resolvedArtifactSource, protocol);
	}

	private FederationProxy getProxy()
	throws ConnectionException
	{
		if(proxy == null)
		{
			ProxyServices proxyServices = getFederationProxyServices();
			if(proxyServices == null)
				throw new ConnectionException("Did not receive any applicable services from IDS service for site [" + getSite().getSiteNumber() + "]");
			proxy = new FederationProxy(proxyServices, FederationDataSourceProvider.getFederationConfiguration());
		}
		return proxy;
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

	@Override
	public String getImageInformation(AbstractImagingURN imagingUrn, boolean includeDeletedImages)
	throws UnsupportedOperationException, MethodException, ConnectionException, 
		ImageNotFoundException 
	{
		getLogger().info("getImageInformation(" + imagingUrn.toString() + "), TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		try
		{
			String result = getProxy().getImageInformation(imagingUrn);
			getLogger().info("getImageInformation complete.");
			TransactionContextFactory.get().setDataSourceBytesReceived(result == null ? 0L : result.length());
			return result;
		}
		catch(ServiceException sX) {
			getLogger().error("Error getting study image information", sX);
			throw new FederationMethodException(sX);
		}
		catch(IOException ioX) 
		{
			getLogger().error("Error getting study image information", ioX);
			throw new FederationConnectionException(ioX);
		}
	}

	@Override
	public String getImageSystemGlobalNode(AbstractImagingURN imagingUrn)
	throws UnsupportedOperationException, MethodException, ConnectionException, 
		ImageNotFoundException 
	{
		getLogger().info("getImageSystemGlobalNode(" + imagingUrn.toString() + "), TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		try
		{
			String result = getProxy().getImageSystemGlobalNode(imagingUrn);
			getLogger().info("getImageSystemGlobalNode complete.");
			TransactionContextFactory.get().setDataSourceBytesReceived(result == null ? 0L : result.length());
			return result;
		}
		catch(ServiceException sX) {
			getLogger().error("Error getting image global nodes", sX);
			throw new FederationMethodException(sX);
		}
		catch(IOException ioX) 
		{
			getLogger().error("Error getting image global nodes", ioX);
			throw new FederationConnectionException(ioX);
		}
	}

	@Override
	public String getImageDevFields(AbstractImagingURN imagingUrn, String flags)
	throws UnsupportedOperationException, MethodException, ConnectionException, 
		ImageNotFoundException 
	{
		getLogger().info("getImageDevFields(" + imagingUrn.toString() + "), TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		try
		{
			String result = getProxy().getImageDevFields(imagingUrn, flags);
			getLogger().info("getImageDevFields complete.");
			TransactionContextFactory.get().setDataSourceBytesReceived(result == null ? 0L : result.length());
			return result;
		}
		catch(ServiceException sX) {
			getLogger().error("Error getting image dev fields", sX);
			throw new FederationMethodException(sX);
		}
		catch(IOException ioX) 
		{
			getLogger().error("Error getting image dev fields", ioX);
			throw new FederationConnectionException(ioX);
		}
	}

	@Override
	protected boolean canGetTextFile()
	{
		return true;
	}
}
