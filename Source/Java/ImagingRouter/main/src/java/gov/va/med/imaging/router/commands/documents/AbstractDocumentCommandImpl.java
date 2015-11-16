/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 22, 2011
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
package gov.va.med.imaging.router.commands.documents;

import java.io.IOException;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.WellKnownOID;
import gov.va.med.imaging.core.interfaces.ImageMetadataNotification;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageConversionException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNearLineException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNotFoundException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.FileTypeIdentifierStream;
import gov.va.med.imaging.exchange.business.ImageStreamResponse;
import gov.va.med.imaging.exchange.business.documents.DocumentRetrieveResult;
import gov.va.med.imaging.exchange.storage.cache.RealizedCache;
import gov.va.med.imaging.router.commands.AbstractImagingCommandImpl;
import gov.va.med.imaging.router.facade.ImagingContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * @author vhaiswwerfej
 *
 */
public abstract class AbstractDocumentCommandImpl<R>
extends AbstractImagingCommandImpl<R>
{
	private static final long serialVersionUID = 1707175216726790841L;
	
	protected final GlobalArtifactIdentifier documentIdentifier;
	protected final ImageMetadataNotification imageMetadataNotification;
	
	protected AbstractDocumentCommandImpl(GlobalArtifactIdentifier documentIdentifier,
			ImageMetadataNotification imageMetadataNotification)
	{
		super();
		this.documentIdentifier = documentIdentifier;
		this.imageMetadataNotification = imageMetadataNotification;
	}
	
	protected AbstractDocumentCommandImpl(GlobalArtifactIdentifier documentIdentifier)
	{
		this(documentIdentifier, null);
	}
	
	protected RealizedCache getCache()
	{
		boolean cacheThisInstance = getDocumentIdentifier() != null && getCommandContext().isCachingEnabled();
		String homeCommunityId = getDocumentIdentifier().getHomeCommunityId();
		
		RealizedCache cache = null; 
		if(cacheThisInstance)
		{
			if((WellKnownOID.VA_DOCUMENT.isApplicable(homeCommunityId)) 
					|| (WellKnownOID.VA_RADIOLOGY_IMAGE.isApplicable(homeCommunityId)))
			{
				cache = getCommandContext().getIntraEnterpriseCacheCache();
			}
			else
			{
				cache = getCommandContext().getExtraEnterpriseCache();
			}
		}
		return cache;
	}

	/**
	 * 
	 * @param bytesReturned
	 * @return
	 * @throws MethodException
	 * @throws ImageConversionException
	 * @throws ImageNotFoundException
	 * @throws ImageNearLineException
	 */
	protected DocumentRetrieveResult streamFromDataSource()
	throws MethodException, ImageConversionException, ImageNotFoundException, ImageNearLineException
	{
		getLogger().info("Document '" + getDocumentIdentifier().toString() + "' getting document from source.");
		TransactionContextFactory.get().setItemCached(Boolean.FALSE);
		ImageStreamResponse streamResponse = null;
		try
		{
			streamResponse = streamDocumentFromDataSource();
			FileTypeIdentifierStream ftis = new FileTypeIdentifierStream(streamResponse.getImageStream().getInputStream());
			if((getImageMetadataNotification() != null) && (streamResponse != null))
			{
				getImageMetadataNotification().imageMetadata(streamResponse.getProvidedImageChecksum(), 
						streamResponse.getImageFormat(), streamResponse.getImageSize(), 
						streamResponse.getImageQuality());
			}
			return new DocumentRetrieveResult(getDocumentIdentifier(), ftis, null);
		}
		catch (ConnectionException cX)
		{
			getLogger().error(cX);
			throw new MethodException(cX);
		} 
		catch (ImageNotFoundException e)
        {
			throw e;
        }
	}
	
	/**
	 * 
	 * @param imageUrn
	 *            Unique identifier of the image to request from the data source
	 * @param qualityValue
	 * @param contentType
	 * @param outStream
	 * @return a String value indicating the checksum and checksum algorithm
	 * @see gov.va.med.imaging.ChecksumValue returns "ok" or "not ok" if
	 *      checksum was calculated for data source stream before compression
	 * @throws MethodException
	 * @throws IOException
	 * @throws ImageNearLineException
	 */
	private ImageStreamResponse streamDocumentFromDataSource() 
	throws MethodException, ImageConversionException, ConnectionException, ImageNearLineException, ImageNotFoundException
	{
		//StreamImageFromCacheResponse response = new StreamImageFromCacheResponse();
		//SizedInputStream sizedStream = null;
		//InputStream inStream = null;
		ImageStreamResponse documentResponse = null;
		//String documentId = documentUrn.getDocumentId();
		
		documentResponse = ImagingContext.getRouter().getDocumentFromDataSource(getDocumentIdentifier());
		if(documentResponse == null)
			throw new ImageNotFoundException("Document [" + getDocumentIdentifier().toString() + "] not found");

		if(documentResponse.getImageStream() == null)
			throw new MethodException("No input stream returned from data source for document [" + getDocumentIdentifier().toString() + "].");
		//inStream = sizedStream.getInStream();
		
		if(!documentResponse.getImageStream().isReadable())
			throw new MethodException("No input stream returned from data source for document [" + getDocumentIdentifier().toString() + "].");
		// at this point the image has been returned		
		
		//CommonImageCacheFunctions.cacheTXTFile(getCommandContext(), documentUrn, documentResponse.getTxtStream(), false);
		
		//ImageFormat curImgFormat = documentResponse.getImageFormat();
		//getLogger().info("Image returned from datasource in format [" + curImgFormat + "]");
		return documentResponse;		
		
	}

	public GlobalArtifactIdentifier getDocumentIdentifier()
	{
		return documentIdentifier;
	}

	public ImageMetadataNotification getImageMetadataNotification()
	{
		return imageMetadataNotification;
	}
}
