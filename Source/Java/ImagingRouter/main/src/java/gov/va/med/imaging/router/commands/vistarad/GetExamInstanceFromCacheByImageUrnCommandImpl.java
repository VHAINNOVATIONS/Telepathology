/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 5, 2010
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

import java.io.IOException;
import java.io.OutputStream;

import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.channels.CompositeIOException;
import gov.va.med.imaging.core.StreamImageFromCacheResponse;
import gov.va.med.imaging.core.interfaces.ImageMetadataNotification;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNotCachedException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.ImageFormatQualityList;
import gov.va.med.imaging.router.commands.CommonImageCacheFunctions;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * Retrieve an exam image (binary) data from the cache if it is in the cache.  If not in the cache
 * then throw an exception.
 * 
 * @author vhaiswwerfej
 *
 */
public class GetExamInstanceFromCacheByImageUrnCommandImpl
extends AbstractExamImageCommandImpl<Long>
{
	private static final long serialVersionUID = -6970689373226909755L;
	private final ImageURN imageUrn;
	private final ImageMetadataNotification imageMetadataNotification;
	private final OutputStream outStream;
	private final ImageFormatQualityList imageFormatQualityList;
	
	public GetExamInstanceFromCacheByImageUrnCommandImpl(ImageURN imageUrn, 
		ImageMetadataNotification imageMetadataNotification,
		OutputStream outStream, ImageFormatQualityList imageFormatQualityList)
	{
		this.imageFormatQualityList = imageFormatQualityList;
		this.imageMetadataNotification = imageMetadataNotification;
		this.imageUrn = imageUrn;
		this.outStream = outStream;
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

	/**
	 * @return the imageFormatQualityList
	 */
	public ImageFormatQualityList getImageFormatQualityList()
	{
		return imageFormatQualityList;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.commands.vistarad.AbstractExamCommandImpl#areClassSpecificFieldsEqual(java.lang.Object)
	 */
	@Override
	protected boolean areClassSpecificFieldsEqual(Object obj)
	{
		// Perform cast for subsequent tests
		final GetExamInstanceFromCacheByImageUrnCommandImpl other = (GetExamInstanceFromCacheByImageUrnCommandImpl) obj;
		
		boolean allEqual = true;
		allEqual = allEqual && areFieldsEqual(this.imageUrn, other.imageUrn);
		allEqual = allEqual && areFieldsEqual(this.outStream, other.outStream);
		allEqual = allEqual && areFieldsEqual(this.imageFormatQualityList, other.imageFormatQualityList);
		allEqual = allEqual && areFieldsEqual(this.imageMetadataNotification, other.imageMetadataNotification);
		
		return allEqual;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#callSynchronouslyInTransactionContext()
	 */
	@Override
	public Long callSynchronouslyInTransactionContext()
		throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();

		getLogger().info("RouterImpl.getExamInstanceFromCacheByUrn (" + imageUrn.toString() + ", " + getImageFormatQualityList().getAcceptString(true, true) + ")");
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
			getLogger().info("Image '" + imageUrn.toString() + "' caching enabled.");
			try
			{
				StreamImageFromCacheResponse response = 
					CommonImageCacheFunctions.streamImageFromCache(getCommandContext(), imageUrn, 
						getImageFormatQualityList(), outStream, getImageMetadataNotification());
				if((response != null) && (response.getBytesReturnedFromDataSource() > 0))
				{
					transactionContext.setItemCached(Boolean.TRUE);
					getLogger().info("Image '" + imageUrn.toString() + "' found in the cache and streamed to the destination.");					

					return new Long(response.getBytesReturnedFromDataSource());
					// new ImageMetadata(imageUrn, response.imageFormat, null, response.bytesReturnedFromDataSource, response.bytesReturnedFromDataSource);
				}
				getLogger().info("Did not get image [" + imageUrn.toString() + "] from cache");
				throw new ImageNotCachedException("Did not get image [" + imageUrn.toString() + "] from cache");
			}
			catch(CompositeIOException cioX) 
			{
				// if we know that no bytes have been written then we we can continue
				// otherwise we have to stop here and throw an error 
				if( cioX.isBytesWrittenKnown() && cioX.getBytesWritten() == 0 || cioX.getBytesWritten() == -1 )
				{
					String msg = "IO Exception when reading from cache, continuing with direct data source stream." + 
					cioX.getBytesWritten() + 
					" bytes were indicated to have been written." +
					"Caused by : [" + cioX.getMessage() +
					"] at " + getClass().getName() + ".callSynchronouslyInTransactionContext()";
					
					getLogger().warn(msg);					
					throw new MethodException(msg, cioX);
				}
				else
				{
					// exception occurred, we can't continue because the image may be partially written
					getLogger().error(cioX);
					throw new MethodException(
						"IO Exception when reading from cache, cannot continue because " + cioX.getBytesWritten() + 
						" bytes were known to have been written, continuing could result in corrupted image. " +
						"Caused by : [" + cioX.getMessage() +
						"] at " + getClass().getName() + ".callSynchronouslyInTransactionContext()"
					);
				}
			}
			catch(IOException ioX)
			{
				// exception occurred, we can't continue because the image may be partially written
				getLogger().error(ioX);
				throw new MethodException(
					"IO Exception when reading from cache, cannot continue because some bytes may be written, " + 
					"continuing could result in corrupted image. " +
					"Caused by : [" + ioX.getMessage() +
					"] at " + getClass().getName() + ".callSynchronouslyInTransactionContext()"
				);
			}
		}
		throw new ImageNotCachedException("Caching not available, did not get image [" + imageUrn.toString() + " from cache");
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#parameterToString()
	 */
	@Override
	protected String parameterToString()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(this.getImageUrn());
		sb.append(',');
		sb.append(this.getImageFormatQualityList());
		
		return sb.toString();
	}

}
