/**
 * 
 */
package gov.va.med.imaging.vistaimagingdatasource;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.GlobalArtifactIdentifierFactory;
import gov.va.med.exceptions.GlobalArtifactIdentifierFormatException;
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

/**
 * This is an implementation of the DocumentDataSourceSpi with VistA as the backing
 * data storage.  This class is a derivation of the StudyGraphDataSourceSpi over VistA
 * with additional function to translate into Document semantics.
 * 
 * @author vhaiswbeckec
 *
 */
public class VistaImagingDocumentDataSourceService
extends VistaImageDataSourceService
implements DocumentDataSourceSpi
{
 

	/**
	 * @param resolvedArtifactSource
	 * @param protocol
	 */
	public VistaImagingDocumentDataSourceService(ResolvedArtifactSource resolvedArtifactSource, String protocol)
	{
		super(resolvedArtifactSource, protocol);
	}

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
	
	@Override
	public ImageStreamResponse getDocument(GlobalArtifactIdentifier gai)
	throws MethodException, ConnectionException
	{
		ImageURN imageUrn = null;
		try
		{
			imageUrn = GlobalArtifactIdentifierFactory.create(
				gai.getHomeCommunityId(), 
				gai.getRepositoryUniqueId(), 
				gai.getDocumentUniqueId(), 
				DocumentURN.class);
			//imageUrn = URNFactory.create(documentId, ImageURN.class);
		}
		//catch (URNFormatException x)
		//{
		//	throw new MethodException(
		//			"Unexpected exception converting document URN '" + 
		//			(imageUrn == null ? "<null>" : imageUrn.toString()) + 
		//			"' to image URN.", x);
		//}
		catch (GlobalArtifactIdentifierFormatException x)
		{
			throw new MethodException(
				"Unexpected exception building image URN from '" + 
				gai.toString() + "'.", x);
		}
		ImageFormatQualityList docFormat = new ImageFormatQualityList();
		docFormat.add(new ImageFormatQuality(ImageFormat.ORIGINAL, ImageQuality.DIAGNOSTICUNCOMPRESSED));
		return this.getImage(imageUrn, docFormat);
	}
}
