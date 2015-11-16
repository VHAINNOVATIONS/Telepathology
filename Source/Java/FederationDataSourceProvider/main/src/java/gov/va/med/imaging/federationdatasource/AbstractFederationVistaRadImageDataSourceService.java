/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jul 13, 2010
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

import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNearLineException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNotFoundException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.VistaRadDataSourceSpi;
import gov.va.med.imaging.datasource.VistaRadImageDataSourceSpi;
import gov.va.med.imaging.datasource.exceptions.UnsupportedServiceMethodException;
import gov.va.med.imaging.exchange.business.ImageFormatQualityList;
import gov.va.med.imaging.exchange.business.ImageStreamResponse;
import gov.va.med.imaging.exchange.business.vistarad.ExamImage;
import gov.va.med.imaging.exchange.storage.DataSourceInputStream;
import gov.va.med.imaging.federation.proxy.IFederationProxy;
import gov.va.med.imaging.proxy.exceptions.ProxyServiceNotFoundException;
import gov.va.med.imaging.proxy.services.ProxyServiceType;
import gov.va.med.imaging.url.vftp.VftpConnection;

/**
 * @author vhaiswwerfej
 *
 */
public abstract class AbstractFederationVistaRadImageDataSourceService
extends AbstractFederationImageDataSourceService
implements VistaRadImageDataSourceSpi
{
	private final VftpConnection federationConnection;
	
	/**
	 * @param resolvedArtifactSource
	 * @param protocol
	 * @throws UnsupportedOperationException
	 */
	public AbstractFederationVistaRadImageDataSourceService(ResolvedArtifactSource resolvedArtifactSource, String protocol)
	throws UnsupportedOperationException
	{
		super(resolvedArtifactSource, protocol);
		federationConnection = new VftpConnection(getMetadataUrl());
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.VistaRadImageDataSource#getImage(gov.va.med.imaging.exchange.business.vistarad.ExamImage, gov.va.med.imaging.exchange.business.ImageFormatQualityList)
	 */
	@Override
	public ImageStreamResponse getImage(ExamImage image,
			ImageFormatQualityList requestFormatQuality)
	throws MethodException, ConnectionException 
	{
		return getImage(image.getImageUrn(), requestFormatQuality);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.VistaRadImageDataSource#getImage(gov.va.med.imaging.ImageURN, gov.va.med.imaging.exchange.business.ImageFormatQualityList)
	 */
	@Override
	public ImageStreamResponse getImage(ImageURN imageUrn,
			ImageFormatQualityList requestFormatQuality)
	throws MethodException, ConnectionException 
	{
		return super.getImage(imageUrn, requestFormatQuality);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.VistaRadImageDataSource#getImageTXTFile(gov.va.med.imaging.exchange.business.vistarad.ExamImage)
	 */
	@Override
	public DataSourceInputStream getImageTXTFile(ExamImage image)
	throws MethodException, ConnectionException, ImageNotFoundException, ImageNearLineException 
	{
		return getImageTXTFile(image.getImageUrn());
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.VistaRadImageDataSource#getImageTXTFile(gov.va.med.imaging.ImageURN)
	 */
	@Override
	public DataSourceInputStream getImageTXTFile(ImageURN imageUrn)
	throws MethodException, ConnectionException, ImageNotFoundException, ImageNearLineException 
	{
		return super.getImageTXTFile(imageUrn);
	}
	
	/**
	 * Return the ProxyServiceType to check for to verify if version compatible is true
	 * @return
	 */
	protected abstract ProxyServiceType getVersionCompatibleProxyServiceType();
	

	@Override
	public boolean isVersionCompatible()
	{
		if(getFederationProxyServices() == null)
			return false;		
		try
		{
			getLogger().debug("Found FederationProxyServices, looking for '" + getVersionCompatibleProxyServiceType() + "' service type at site [" + getSite().getSiteNumber() + "].");
			getFederationProxyServices().getProxyService(getVersionCompatibleProxyServiceType());
			getLogger().debug("Found service type '" + getVersionCompatibleProxyServiceType() + "' at site [" + getSite().getSiteNumber() + "], returning true for version compatible.");
			return true;
		}
		catch(ProxyServiceNotFoundException psnfX)
		{
			getLogger().warn("Cannot find proxy service type '" + getVersionCompatibleProxyServiceType() + "' at site [" + getSite().getSiteNumber() + "]");
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federationdatasource.AbstractFederationImageDataSourceService#getFederationProxy()
	 */
	@Override
	protected IFederationProxy getFederationProxy() throws ConnectionException
	{
		throw new UnsupportedServiceMethodException(VistaRadImageDataSourceSpi.class, "getFederationProxy");
	}
	
	@Override
	protected boolean canGetTextFile()
	{
		return true;
	}
}
