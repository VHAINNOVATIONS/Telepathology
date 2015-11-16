/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 9, 2010
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
package gov.va.med.imaging.router.commands.vistarad;

import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.core.StreamImageFromCacheResponse;
import gov.va.med.imaging.core.interfaces.ImageMetadataNotification;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNotCachedException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.ImageFormatQuality;
import gov.va.med.imaging.exchange.business.ImageFormatQualityList;
import gov.va.med.imaging.exchange.enums.ImageFormat;
import gov.va.med.imaging.exchange.enums.ImageQuality;
import gov.va.med.imaging.router.commands.CommonImageCacheFunctions;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Get the text file associated with an exam image only if it is already in the cache.  If not in the cache
 * then throw an ImageNotCached exception
 * 
 * @author vhaiswwerfej
 *
 */
public class GetExamTextFileFromCacheByImageUrnCommandImpl
extends AbstractExamImageCommandImpl<Integer>
{
	private static final long serialVersionUID = -85725901700410123L;
	
	private final ImageURN imageUrn;
	private final ImageMetadataNotification imageMetadataNotification;
	private final OutputStream outStream;
	
	public GetExamTextFileFromCacheByImageUrnCommandImpl(ImageURN imageUrn, 
		ImageMetadataNotification imageMetadataNotification, OutputStream outStream)
	{
		this.imageUrn = imageUrn;
		this.imageMetadataNotification = imageMetadataNotification;
		this.outStream = outStream;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.commands.vistarad.AbstractExamCommandImpl#areClassSpecificFieldsEqual(java.lang.Object)
	 */
	@Override
	protected boolean areClassSpecificFieldsEqual(Object obj)
	{
		// Perform cast for subsequent tests
		final GetExamTextFileFromCacheByImageUrnCommandImpl other = (GetExamTextFileFromCacheByImageUrnCommandImpl) obj;
		
		boolean allEqual = true;
		allEqual = allEqual && areFieldsEqual(this.imageUrn, other.imageUrn);
		allEqual = allEqual && areFieldsEqual(this.imageMetadataNotification, other.imageMetadataNotification);
		allEqual = allEqual && areFieldsEqual(this.outStream, other.outStream);
		
		return allEqual;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#callSynchronouslyInTransactionContext()
	 */
	@Override
	public Integer callSynchronouslyInTransactionContext()
		throws MethodException, ConnectionException
	{
		int bytesReturned=0;
		ImageQuality txtQuality = ImageQuality.REFERENCE;
		ImageFormat txtFormat = ImageFormat.TEXT_DICOM;
		ImageFormatQualityList qualityList = new ImageFormatQualityList();
		qualityList.add(new ImageFormatQuality(txtFormat, txtQuality));
		TransactionContext transactionContext = TransactionContextFactory.get();

		getLogger().info("RouterImpl.getExamTxtFileFromCacheByImageURN(" + imageUrn.toString() + ")");
		if(outStream == null)
		{
			throw new MethodException("Outputstream is null");
		}
		
		// use this imageId to query the DOD
		String imageId = imageUrn.getImageId();
		String studyId = imageUrn.getStudyId();
		String siteNumber = imageUrn.getOriginatingSiteId();

		transactionContext.setServicedSource(imageUrn.toRoutingTokenString());

		// if caching is enabled we will try to use the cache
		// cacheThisInstance indicates both that we write to and read from the cache for this instance
		boolean cacheThisInstance = studyId != null  &&  imageId != null  && getCommandContext().isCachingEnabled();

		// if the Image URN was successfully parsed and caching is enabled
		// try to retrieve the instance from the cache
		if( cacheThisInstance ) 
		{
			getLogger().info("Exam image Text file'" + imageUrn.toString() + "' caching enabled");
			try
			{
				StreamImageFromCacheResponse response = 
					CommonImageCacheFunctions.streamImageFromCache(getCommandContext(),
							imageUrn, qualityList, outStream, getImageMetadataNotification());
				if(response != null)
				{
					bytesReturned = response.getBytesReturnedFromDataSource();
					if( bytesReturned > 0 )
					{
						transactionContext.setItemCached(Boolean.TRUE);
						getLogger().info("Exam image txt file '" + imageUrn.toString() + "' found in the cache and streamed to the destination.");
						return bytesReturned;
					}
				}
				getLogger().info("Did not get exam image txt file [" + imageUrn.toString() + "] from cache");
				throw new ImageNotCachedException("Did not get exam image txt file [" + imageUrn.toString() + "] from cache");
			}
			catch(IOException ioX)
			{
				// exception occured, we can't continue because the image may be partially written
				getLogger().error(ioX);
				throw new MethodException(
					"IO Exception when reading from cache, cannot continue because some bytes may be written, continuing could result in corrupted image." +
					"Caused by : [" + ioX.getMessage() +
					"] at " + getClass().getName() + ".callSynchronouslyInTransactionContext()."
				);
			}
		}
		
		throw new ImageNotCachedException("Caching not available, did not get exam image text file [" + imageUrn.toString() + " from cache");
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#parameterToString()
	 */
	@Override
	protected String parameterToString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(imageUrn.toString());
		sb.append(',');
		sb.append(outStream.toString()); 		
		sb.append(',');
		sb.append(imageMetadataNotification.toString());
		
		return sb.toString();
	}

	/**
	 * @return the imageUrn
	 */
	public ImageURN getImageUrn()
	{
		return imageUrn;
	}

	/**
	 * @return the imageMetadataNotification
	 */
	public ImageMetadataNotification getImageMetadataNotification()
	{
		return imageMetadataNotification;
	}

	/**
	 * @return the outStream
	 */
	public OutputStream getOutStream()
	{
		return outStream;
	}

}
