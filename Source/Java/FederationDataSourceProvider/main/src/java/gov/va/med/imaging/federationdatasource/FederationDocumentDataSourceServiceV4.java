/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 23, 2010
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

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.imaging.DocumentURN;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.DocumentDataSourceSpi;
import gov.va.med.imaging.exchange.business.ImageFormatQuality;
import gov.va.med.imaging.exchange.business.ImageFormatQualityList;
import gov.va.med.imaging.exchange.business.ImageStreamResponse;
import gov.va.med.imaging.exchange.enums.ImageFormat;
import gov.va.med.imaging.exchange.enums.ImageQuality;
import gov.va.med.imaging.federation.proxy.IFederationProxy;
import gov.va.med.imaging.federation.proxy.v4.FederationRestDocumentProxyV4;
import gov.va.med.imaging.proxy.services.ProxyServices;

/**
 * @author vhaiswwerfej
 *
 */
public class FederationDocumentDataSourceServiceV4
extends FederationImageDataSourceServiceV4 
implements DocumentDataSourceSpi 
{
	private FederationRestDocumentProxyV4 proxy = null;
	
	
	/**
	 * @param resolvedArtifactSource
	 * @param protocol
	 * @throws UnsupportedOperationException
	 */
	public FederationDocumentDataSourceServiceV4(ResolvedArtifactSource resolvedArtifactSource, String protocol)
	throws UnsupportedOperationException
	{
		super(resolvedArtifactSource, protocol);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.DocumentDataSourceSpi#getDocument(gov.va.med.imaging.DocumentURN)
	 */
	@Override
	public ImageStreamResponse getDocument(DocumentURN documentUrn)
	throws MethodException, ConnectionException 
	{
		// no need to convert this into an ImageURN, the underlying function call can take a document URN
		// and converting this to an image URN changes the prefix which messes with the home community id
		/*
		ImageURN imageUrn;
		try
		{
			imageUrn = ImageURN.create(documentUrn);
		}
		catch (URNFormatException x)
		{
			throw new MethodException("Unexpected exception converting document URN '" + documentUrn.toString() + "' to image URN.", x);
		}*/
		ImageFormatQualityList docFormat = new ImageFormatQualityList();
		docFormat.add(new ImageFormatQuality(ImageFormat.ORIGINAL, ImageQuality.DIAGNOSTICUNCOMPRESSED));
		//return this.getImage(imageUrn, docFormat);
		return this.getImage(documentUrn, docFormat);
	}

	/**
	 * 
	 */
	@Override
	public ImageStreamResponse getDocument(
			GlobalArtifactIdentifier gai)
	throws MethodException, ConnectionException
	{		
		ImageFormatQualityList docFormat = new ImageFormatQualityList();
		docFormat.add(new ImageFormatQuality(ImageFormat.ORIGINAL, ImageQuality.DIAGNOSTICUNCOMPRESSED));
		return this.getImage(gai, docFormat);
	}
	
	private FederationRestDocumentProxyV4 getProxy()
	throws ConnectionException
	{
		if(proxy == null)
		{
			ProxyServices proxyServices = getFederationProxyServices();
			if(proxyServices == null)
				throw new ConnectionException("Did not receive any applicable services from IDS service for site [" + getSite().getSiteNumber() + "]");
			proxy = new FederationRestDocumentProxyV4(proxyServices, FederationDataSourceProvider.getFederationConfiguration());
		}
		return proxy;
	}

	@Override
	protected IFederationProxy getFederationProxy() 
	throws ConnectionException
	{
		return getProxy();
	}

	@Override
	protected boolean canGetTextFile()
	{
		return false;
	}
}
