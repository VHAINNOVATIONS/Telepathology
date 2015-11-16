/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 8, 2010
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
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;

import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.channels.ByteStreamPump;
import gov.va.med.imaging.channels.ChecksumValue;
import gov.va.med.imaging.channels.exceptions.ChecksumFormatException;
import gov.va.med.imaging.core.annotations.routerfacade.RouterCommandExecution;
import gov.va.med.imaging.core.interfaces.exceptions.ChecksumComparisonFailedException;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNotFoundException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.ImageFormatQualityList;
import gov.va.med.imaging.exchange.business.ImageStreamResponse;
import gov.va.med.imaging.exchange.business.util.ExchangeUtil;
import gov.va.med.imaging.exchange.enums.ImageFormat;
import gov.va.med.imaging.exchange.enums.ImageQuality;
import gov.va.med.imaging.exchange.storage.cache.ImmutableInstance;
import gov.va.med.imaging.router.commands.CommonImageCacheFunctions;
import gov.va.med.imaging.storage.cache.InstanceWritableByteChannel;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import gov.va.med.imaging.storage.cache.exceptions.InstanceInaccessibleException;
import gov.va.med.imaging.storage.cache.exceptions.SimultaneousWriteException;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * @author vhaiswwerfej
 *
 */
@RouterCommandExecution(asynchronous=true, distributable=true)
public class PrefetchExamInstanceByImageUrnCommandImpl
extends AbstractExamImageCommandImpl<Void>
{
	private static final long serialVersionUID = -5458120354961385829L;

	private final ImageURN imageUrn;
	private final ImageFormatQualityList imageFormatQualityList;
	
	public PrefetchExamInstanceByImageUrnCommandImpl(ImageURN imageUrn, ImageFormatQualityList imageFormatQualityList)
	{
		this.imageFormatQualityList = imageFormatQualityList;
		this.imageUrn = imageUrn;
	}

	/**
	 * @return the imageUrn
	 */
	public ImageURN getImageUrn()
	{
		return imageUrn;
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
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#callSynchronouslyInTransactionContext()
	 */
	@Override
	public Void callSynchronouslyInTransactionContext()
		throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();

		getLogger().info("RouterImpl.prefetchExamInstanceByImageURN(" + imageUrn.toString() + ", " + getImageFormatQualityList().getAcceptString(true, true) + ")");
		
		// use this imageId to query the DOD
		String imageId = imageUrn.getImageId();
		String studyId = imageUrn.getStudyId();
		String siteNumber = imageUrn.getOriginatingSiteId();
		transactionContext.setServicedSource(imageUrn.toRoutingTokenString());
		// if caching is enabled we will try to use the cache
		// cacheThisInstance indicates both that we write to and read from the cache for this instance
		
		// if the Image URN was successfully parsed and caching is enabled
		// try to retrieve the instance from the cache
		if(!CommonImageCacheFunctions.isImageCached(getCommandContext(), getImageUrn(), getImageFormatQualityList()))
		{
			// the instance was not found in the cache
			// we try to grab the writable byte channel as soon as possible to lock other threads from writing to
			// it
			transactionContext.setItemCached(Boolean.FALSE);
			ImmutableInstance instance = null;
			InstanceWritableByteChannel instanceWritableChannel = null;
			OutputStream cacheOutStream = null;
			
			// JMW 9/5/08 - if the image comes back from the datasource, use the properties of the
			// image (format/quality) from the datasource to find it in the cache
			ImageFormat dataSourceImageFormat = null;
			ImageQuality dataSourceImageQuality = null;
			
			try
			{
				ImageStreamResponse datasourceResponse = streamImageFromDataSource(getImageUrn(), getImageFormatQualityList());
				ImageFormat imgFormat = datasourceResponse.getImageFormat();
				getLogger().info("Received response from data source, putting into cache");
				// set the data source image format and image quality here 
				// since it is now in the cache.
				// JMW 10/6/2008
				// moved this here, if we get the image from the DS, need to use the format/quality from the DS to put/get the image from the cache
				// only clear these values if there is a cache exception (error writing to the cache)
				dataSourceImageFormat = imgFormat;
				dataSourceImageQuality = datasourceResponse.getImageQuality();
				getLogger().debug("Attempting to create cache instance with format ["  + dataSourceImageFormat + "] and quality [" + dataSourceImageQuality + "]");
				if(ExchangeUtil.isSiteDOD(siteNumber))
				{
					instance = getCommandContext().getExtraEnterpriseCache().createImage(imageUrn, dataSourceImageQuality.name(), dataSourceImageFormat.getMimeWithEnclosedMime());
				}
				else
				{
					instance = getCommandContext().getIntraEnterpriseCacheCache().createImage(imageUrn, dataSourceImageQuality.name(), dataSourceImageFormat.getMimeWithEnclosedMime());
				}

				instanceWritableChannel = instance.getWritableChannel();
				cacheOutStream = Channels.newOutputStream(instanceWritableChannel);
				InputStream imageStream = datasourceResponse.getImageStream().getInputStream();
				
				if(cacheOutStream != null)
				{
					getLogger().info("Pumping stream into cache");
					ByteStreamPump pump = ByteStreamPump.getByteStreamPump(ByteStreamPump.TRANSFER_TYPE.NetworkToNetwork);
					// if the cacheStream is null the ByteStreamPump will ignore it
					pump.xfer(imageStream, cacheOutStream);
					
					
				}// not really sure what to do in the alternative here...
				
				if(imageStream != null)
				{
					// close the input stream
					imageStream.close();
				}
				cacheOutStream.close();
				
				assertCacheAndDatasourceChecksumEquality(instance,datasourceResponse);
			}
			catch(InstanceInaccessibleException iaX)
			{
				// special exception handling, another thread is requesting to write to the instance
				// just before we did.
				// Since the function of this command is to prefetch an image, and we now know that some other thread
				// is getting the image, just return.
				getLogger().info("InstanceInaccessibleException for image [" + imageUrn.toString() + "], prefetch is completing on the assumption tha the other thread will cache the image");
			}
			catch(SimultaneousWriteException swX)
			{
				getLogger().info("SimultaneousWriteException for image [" + imageUrn.toString() + "], prefetch is completing on the assumption tha the other thread will cache the image");
			}
			catch(CacheException cX)
			{
				// any kind of cache exceptions should be logged, but the image must still be retrieved from the data source
				// from here on if cacheOutStream is not null we'll write to it 
				getLogger().error(cX);
				instance = null;
				instanceWritableChannel= null;
				cacheOutStream = null;
				dataSourceImageFormat = null;
				dataSourceImageQuality = null;
			}
			catch(IOException ioX)
			{
				cacheOutStream = null;
				try{instanceWritableChannel.error();}catch(Throwable t){}
				getLogger().error(ioX);
			} 
			catch (ImageNotFoundException e)
            {
				//return null;
				throw e;
            }
			finally
			{
				// the instance absolutely positively must be closed
				if((instanceWritableChannel != null) && (instanceWritableChannel.isOpen()))
				{
					getLogger().error("Cache instance writable byte channel being closed with error on unknown exception");
					try{instanceWritableChannel.error();}catch(Throwable t){}
				}
			}
		}
		
		return (java.lang.Void)null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#parameterToString()
	 */
	@Override
	protected String parameterToString()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(getImageUrn());
		sb.append(',');
		sb.append(getImageFormatQualityList() == null ? "<null image format>" : getImageFormatQualityList().toString());
		
		return sb.toString();
	}

	/**
	 * 
	 * @param instance
	 * @param datasourceResponse
	 * @throws ChecksumComparisonFailedException
	 */
	private void assertCacheAndDatasourceChecksumEquality(
		ImmutableInstance instance, 
		ImageStreamResponse datasourceResponse)
	throws ChecksumComparisonFailedException
	{
		String responseChecksum = datasourceResponse.getProvidedImageChecksum();
		boolean noResponseChecksum = false;
		if(responseChecksum != null)
		{
			noResponseChecksum = (responseChecksum.equals("ok") || responseChecksum.equals("not ok"));
		}					
		String cacheChecksum = instance.getChecksumValue();
		if(responseChecksum != null && cacheChecksum != null)
		{
			try
			{
				ChecksumValue responseCV;
				if (noResponseChecksum) 
					responseCV = new ChecksumValue("");
				else 
					responseCV = new ChecksumValue(responseChecksum);
				ChecksumValue cacheCV = new ChecksumValue(cacheChecksum);

				if (noResponseChecksum) 
				{
					if (responseChecksum.equals("ok"))
						getLogger().info("Checksum for inStream '" + imageUrn + "' equals to data source cheksum.");
					else // "not ok"
					{
						getLogger().info("Checksum for inStream '" + imageUrn + "' IS Not Equal to data source checksum.");
						throw new ChecksumComparisonFailedException(imageUrn);
					}
				}
				else if (responseCV.getAlgorithm().equals(cacheCV.getAlgorithm())) 
				{
					if (! responseCV.equals(cacheCV) )
					{
						getLogger().error("Checksums for instance '" + imageUrn + "' ARE NOT EQUAL.");
						throw new ChecksumComparisonFailedException(imageUrn);
					}
					else
						getLogger().info("Checksums for instance '" + imageUrn + "' are equal.");
				}
				else
					getLogger().warn("Checksums not compared for instance '" + imageUrn + 
							"' because response algorithm is '" + responseCV.getAlgorithm() + 
							"' and cache algorithm is '" + cacheCV.getAlgorithm() + "'.");
			} 
			catch (ChecksumFormatException x)
			{
				getLogger().error("Invalidly formatted checksum value, either response header checksum '" + responseChecksum + 
						"' or cache calculated checksum '" + cacheChecksum + "'");
				throw new ChecksumComparisonFailedException(imageUrn);
			}
		}
	}
}
