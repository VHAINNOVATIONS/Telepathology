/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jul 8, 2009
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
import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.DocumentDataSourceSpi;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.business.ImageFormatQuality;
import gov.va.med.imaging.exchange.business.ImageFormatQualityList;
import gov.va.med.imaging.exchange.business.ImageStreamResponse;
import gov.va.med.imaging.exchange.enums.ImageFormat;
import gov.va.med.imaging.exchange.enums.ImageQuality;

import org.apache.log4j.Logger;

/**
 * This is an implementation of the DocumentDataSourceSpi with Federation as the backing
 * data storage.  This class is a derivation of the StudyGraphDataSourceSpi over Federation
 * with additional function to translate into Document semantics.
 * 
 * @author vhaiswwerfej
 *
 */
public class FederationDocumentDataSourceService 
extends FederationImageDataSourceService 
implements DocumentDataSourceSpi 
{
	/**
	 * @param resolvedArtifactSource
	 * @param protocol
	 * @throws UnsupportedOperationException
	 */
	public FederationDocumentDataSourceService(ResolvedArtifactSource resolvedArtifactSource, String protocol)
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
		ImageURN imageUrn;
		try
		{
			imageUrn = ImageURN.create(documentUrn);
		}
		catch (URNFormatException x)
		{
			throw new MethodException("Unexpected exception converting document URN '" + documentUrn.toString() + "' to image URN.", x);
		}
		ImageFormatQualityList docFormat = new ImageFormatQualityList();
		docFormat.add(new ImageFormatQuality(ImageFormat.ORIGINAL, ImageQuality.DIAGNOSTICUNCOMPRESSED));
		return this.getImage(imageUrn, docFormat);
	}

	/**
	 * 
	 */
	@Override
	public ImageStreamResponse getDocument(
			GlobalArtifactIdentifier gai)
	throws MethodException, ConnectionException
	{
		DocumentURN documentUrn = null;			
		if(gai instanceof DocumentURN)
			documentUrn = (DocumentURN)gai;
		
		ImageURN imageUrn;
		try
		{
			imageUrn = ImageURN.create(documentUrn);
		}
		catch (URNFormatException x)
		{
			Logger.getLogger(this.getClass()).error(x);
			throw new MethodException(x);
		}
		
		ImageFormatQualityList docFormat = new ImageFormatQualityList();
		docFormat.add(new ImageFormatQuality(ImageFormat.ORIGINAL, ImageQuality.DIAGNOSTICUNCOMPRESSED));
		return this.getImage(imageUrn, docFormat);
	}

	@Override
	protected boolean canGetTextFile()
	{
		return false;
	}
}
