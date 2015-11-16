/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 2, 2008
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
package gov.va.med.imaging.conversion;

import gov.va.med.imaging.conversion.enums.ImageConversionSatisfaction;
import gov.va.med.imaging.core.interfaces.IImageConversionConfiguration;
import gov.va.med.imaging.core.interfaces.ImageStorageFacade;
import gov.va.med.imaging.core.interfaces.StorageCredentials;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageConversionException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNearLineException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNotFoundException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.ImageFormatAllowableConversionList;
import gov.va.med.imaging.exchange.business.ImageFormatQuality;
import gov.va.med.imaging.exchange.business.ImageFormatQualityList;
import gov.va.med.imaging.exchange.business.ImageStreamResponse;
import gov.va.med.imaging.exchange.conversion.exceptions.ImageConversionCompressionException;
import gov.va.med.imaging.exchange.conversion.exceptions.ImageConversionDecompressionException;
import gov.va.med.imaging.exchange.conversion.exceptions.ImageConversionIOException;
import gov.va.med.imaging.exchange.conversion.exceptions.ImageConversionInvalidInputException;
import gov.va.med.imaging.exchange.conversion.interfaces.IImageConversion;
import gov.va.med.imaging.exchange.enums.ImageFormat;
import gov.va.med.imaging.exchange.enums.ImageQuality;
import gov.va.med.imaging.exchange.enums.StorageProximity;
import gov.va.med.imaging.exchange.storage.ByteBufferBackedImageInputStream;
import gov.va.med.imaging.exchange.storage.ByteBufferBackedImageStreamResponse;
import gov.va.med.imaging.exchange.storage.ByteBufferBackedInputStream;
import gov.va.med.imaging.exchange.storage.ByteBufferBackedObject;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.vista.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/**
 * Image conversion utility does the work of determining what the stored image type is, what
 * it can be converted into and then actually calling the image conversion methods to 
 * convert the image. 
 * 
 * @author VHAISWWERFEJ
 *
 */
public class ImageConversionUtility 
{
	private final static Logger logger = Logger.getLogger(ImageConversionUtility.class);
	private final ImageStorageFacade storageFacade;
	private final ImageConversionSatisfaction conversionSatisfaction;
	private final boolean updateDicomHeaders;

	/**
	 * Create a new image conversion utility for converting a stored image into the format requested by the user
	 * 
	 * @param imageStorageFacade The storage facade knows how to open the file needed.
	 * @param conversionSatisfaction Image conversion satisfaction level for this image conversion utility
	 * @param updateDicomHeaders Determines if the DICOM header should be updated for images that are stored in DICOM
	 */
	public ImageConversionUtility(ImageStorageFacade imageStorageFacade, 
			ImageConversionSatisfaction conversionSatisfaction,
			boolean updateDicomHeaders)
	{
		super();
		this.storageFacade = imageStorageFacade;
		this.conversionSatisfaction = conversionSatisfaction;
		this.updateDicomHeaders = updateDicomHeaders;
	}

	private IImageConversion getImageConversion()
	{
		return ImageConversionFactory.getImageConversion();
	}

	private IImageConversionConfiguration getImageConfiguration()
	{
		return ImageConversionFactory.getImageConversionConfiguration();
	}
	
	/**
	 * Retrieves the image from the list of file paths using the specified format quality list and credentials
	 * @param files
	 * @param requestFormatQualityList The list of file formats to use when requesting the image
	 * @param convertFormatQualityList The list of file formats the image can be returned 
	 * 	in - if the image is requested in a format not included in this list, then the image must be converted
	 * @param txtFileAvailable This determines if the image conversion will attempt to download the TXT file, this value 
	 * 	has no impact if the TXT file is necessary to generate a DICOM image. This is only used if the TXT file might be 
	 * 	included in the result (not for conversion)
	 * @return A stream to the opened image
	 * @throws ImageNearLineException
	 * @throws ImageNotFoundException
	 * @throws ImageConversionException
	 * @throws ConnectionException
	 */
	public ImageStreamResponse getImage(List<ImageConversionFilePath> files, 
			ImageFormatQualityList requestFormatQualityList, 
			ImageFormatQualityList convertFormatQualityList,
			boolean txtFileAvailable)
	throws ImageNearLineException, ImageNotFoundException, ImageConversionException,
	ConnectionException, MethodException
	{
		return getImage(files, requestFormatQualityList, convertFormatQualityList,
				txtFileAvailable, null);
	}

	/**
	 * Retrieves the image from the list of file paths using the specified format quality list and credentials
	 * @param files
	 * @param requestFormatQualityList The list of file formats to use when requesting the image
	 * @param convertFormatQualityList The list of file formats the image can be returned 
	 * 	in - if the image is requested in a format not included in this list, then the image must be converted
	 * @param txtFileAvailable This determines if the image conversion will attempt to download the TXT file, this value 
	 * 	has no impact if the TXT file is necessary to generate a DICOM image. This is only used if the TXT file might be 
	 * 	included in the result (not for conversion)
	 * @param storageCredentials The credentials used to access the storage device for the files
	 * @return A stream to the opened image
	 * @throws ImageNearLineException
	 * @throws ImageNotFoundException
	 * @throws ImageConversionException
	 * @throws ConnectionException
	 */
	public ImageStreamResponse getImage(List<ImageConversionFilePath> files, 
			ImageFormatQualityList requestFormatQualityList, 
			ImageFormatQualityList convertFormatQualityList,
			boolean txtFileAvailable,
			StorageCredentials storageCredentials)
	throws ImageNearLineException, ImageNotFoundException, ImageConversionException,
	ConnectionException, MethodException
	{
		return getImage(files, requestFormatQualityList, convertFormatQualityList,
				txtFileAvailable, storageCredentials, null);
	}

	/**
	 * Retrieves the image from the list of file paths using the specified format quality list and credentials
	 * @param files
	 * @param requestFormatQualityList The list of file formats to use when requesting the image
	 * @param convertFormatQualityList The list of file formats the image can be returned 
	 * 	in - if the image is requested in a format not included in this list, then the image must be converted
	 * @param txtFileAvailable This determines if the image conversion will attempt to download the TXT file, this value 
	 * 	has no impact if the TXT file is necessary to generate a DICOM image. This is only used if the TXT file might be 
	 * 	included in the result (not for conversion)
	 * @param storageCredentials The credentials used to access the storage device for the files
	 * @param hisUpdate The HIS update from the database
	 * @return
	 * @throws ImageNearLineException
	 * @throws ImageNotFoundException
	 * @throws ImageConversionException
	 * @throws ConnectionException
	 */
	public ImageStreamResponse getImage(List<ImageConversionFilePath> files, 
			ImageFormatQualityList requestFormatQualityList,
			ImageFormatQualityList convertFormatQualityList,
			boolean txtFileAvailable,
			StorageCredentials storageCredentials, HashMap<String, String> hisUpdate)
	throws ImageNearLineException, ImageNotFoundException, ImageConversionException, 
	ConnectionException, MethodException
	{
		try
		{
			for(ImageConversionFilePath filePath : files)
			{
				ImageStreamResponse response = retrieveAndCompressImage(filePath, 
						requestFormatQualityList, convertFormatQualityList,
						txtFileAvailable, storageCredentials, hisUpdate);
				if(response != null)
					return response;
			}
			return null;
		}
		finally
		{
			if(storageFacade != null)
			{
				// be sure to clear the buffers when done with the conversion (always clear them)
				storageFacade.clearBuffers();
			}
		}
	}

	private ImageStreamResponse retrieveAndCompressImage(ImageConversionFilePath filePath, 
			ImageFormatQualityList requestFormatQualityList, 
			ImageFormatQualityList convertFormatQualityList,
			boolean txtFileAvailable,
			StorageCredentials storageCredentials, HashMap<String, String> hisUpdate)
	throws ImageNearLineException, ImageNotFoundException, ImageConversionException, 
	ConnectionException, MethodException
	{
		// just to be sure everything is clean
		//returnBuffers();
		try 
		{
			String imageFilename = filePath.getFilePath();
			logger.info("Opening input stream to image [" + imageFilename + "]");

			if(imageFilename.startsWith("."))
			{
				logger.error("Image starts with a '.', cannot access or retrieve canned images from accelerator");
				return null;
			}
			TransactionContext transactionContext = TransactionContextFactory.get();
			transactionContext.addDebugInformation("Opening image with path '" + imageFilename + "'.");
			ByteBufferBackedImageStreamResponse response = storageFacade.openImageStream(imageFilename, 
					storageCredentials, filePath.getStorageProximity(), requestFormatQualityList);
			ImageFormat storedFormat = response.getImageFormat();
			
			ImageQuality storedQuality = response.getImageQuality();
			if(storedQuality == null)
				storedQuality = filePath.getImageQuality();
			if(response.getImageQuality() == null)
				response.setImageQuality(storedQuality);
			
			
			if(storedFormat != null)
			{
				transactionContext.setDataSourceImageFormatReceived(storedFormat.toString());
			}
			if(storedQuality != null)
			{
				transactionContext.setDataSourceImageQualityReceived(storedQuality.toString());
			}

			// special case if we can't determine the format of the file, just want to return it if allowed
			if(storedFormat == ImageFormat.ORIGINAL)
			{
				logger.info("Unable to determine the format of file [" + imageFilename + "]");
				if(requestAllowsOriginalImageType(convertFormatQualityList))
				{
					logger.info("Unknown format is allowed because [ORIGINAL] is in request, returning ORIGINAL image format");
					// want to set the image quality in the response also...
					// do anything special if incorrect image quality? probably can't!
					if(response.getImageQuality() == null)
						response.setImageQuality(storedQuality);
					if((storedQuality != ImageQuality.THUMBNAIL) &&
							(response.getTxtStream() == null) && (!ImageFormat.isDICOMFormat(response.getImageFormat()))
							&& (txtFileAvailable))
					{
						ByteBufferBackedInputStream txtStream = 
							getTxtStreamHandleException(imageFilename, storageCredentials, hisUpdate);
						response.setTxtStream(txtStream);
						// can't do the DICOM header update since we don't know what the format of the image is
					}
					return response;
				}
			}

			Iterator<ImageFormatQuality> qualitiesIterator = convertFormatQualityList.iterator();
			boolean canCompressStoredFormat = canCompressFormat(storedFormat);


			// if satisfy top request is false then try to just return the output if possible
			// we would want to just return the data without converting it if it matches the 
			// requested formats
			//if(!isSatisfyTopRequest())
			if(conversionSatisfaction == ImageConversionSatisfaction.SATISFY_ANY_REQUEST)
			{
				logger.info("Checking to see if image in format [" + storedFormat + "] can be returned in current format");
				if(isStoredFormatInRequest(convertFormatQualityList, storedFormat, storedQuality))
				{
					logger.info("Image in format [" + storedFormat + "] and can be returned in this format, returning");
					if(response.getImageQuality() == null)
						response.setImageQuality(storedQuality);
					if(storedQuality != ImageQuality.THUMBNAIL)
					{
						setImageStreamResponseTextFileIfNecessary(response, storedFormat, imageFilename, 
								storageCredentials, hisUpdate, txtFileAvailable);					
						if((ImageFormat.isDICOMFormat(storedFormat)) && (updateDicomHeaders))
						{
							// if DICOM then try to update the DICOM header with the latest values
							return updateDICOMHeaderHandleExceptions(response, storedFormat, storedQuality);
						}
					}
					return response;
				}
			}
			else if(conversionSatisfaction == ImageConversionSatisfaction.SATISFY_ALLOWED_COMPRESSION)
			{
				// if the image is stored in a compressed format and that format is in the requested format list
				// then the image can be returned regardless of where it is in the list.
				if(storedFormat.isCompressed())
				{
					logger.info("Image is stored in format [" + storedFormat + "] which is compressed, checking to see if image can be returned in this format");
					if(isStoredFormatInRequest(convertFormatQualityList, storedFormat, storedQuality))
					{
						logger.info("Image in format [" + storedFormat + "] and can be returned in this format, returning");
						if(response.getImageQuality() == null)
							response.setImageQuality(storedQuality);
						if(storedQuality != ImageQuality.THUMBNAIL)
						{
							setImageStreamResponseTextFileIfNecessary(response, storedFormat, 
									imageFilename, storageCredentials, hisUpdate, txtFileAvailable);					
							if((ImageFormat.isDICOMFormat(storedFormat)) && (updateDicomHeaders))
							{
								// if DICOM then try to update the DICOM header with the latest values
								return updateDICOMHeaderHandleExceptions(response, storedFormat, storedQuality);
							}
						}
						return response;
					}
				}
			}
			ImageFormatQuality quality = getNextAllowableImageFormatQuality(storedFormat, qualitiesIterator);
			
			while(quality != null)
			{
				logger.info("Attempting to generate image of type '" + quality.getImageFormat() + "'.");
				if(((storedFormat == quality.getImageFormat()) || 
						(quality.getImageFormat() == ImageFormat.ORIGINAL))
						&& ((storedQuality == quality.getImageQuality()) || 
								(!canCompressStoredFormat)))
				{
					logger.info("Image format [" + storedFormat + "] matches request format, returning image");
					if(response.getImageQuality() == null)
						response.setImageQuality(quality.getImageQuality());
					if(quality.getImageQuality() != ImageQuality.THUMBNAIL)
					{
						setImageStreamResponseTextFileIfNecessary(response, storedFormat, 
								imageFilename, storageCredentials, hisUpdate, txtFileAvailable);					
						if((ImageFormat.isDICOMFormat(storedFormat)) && (updateDicomHeaders))
						{
							// if DICOM then try to update the DICOM header with the latest values
							return updateDICOMHeaderHandleExceptions(response, storedFormat, storedQuality);
						}
					}
					return response;
				}	

				// if the original file type is not in the right quality but the current conversion
				// is "original", then just return what we have. Original should always happen last
				// so this is a last resort and the user said they could handle the original format
				if(quality.getImageFormat() == ImageFormat.ORIGINAL)
				{
					logger.info("Image is in format [" + storedFormat + "] and [ORIGINAL] can be returned, returning stored image");
					if(response.getImageQuality() == null)
						response.setImageQuality(storedQuality);
					if(quality.getImageQuality() != ImageQuality.THUMBNAIL)
					{
						setImageStreamResponseTextFileIfNecessary(response, storedFormat, imageFilename, 
								storageCredentials, hisUpdate, txtFileAvailable);										
						if((ImageFormat.isDICOMFormat(storedFormat)) && (updateDicomHeaders))
						{
							// if DICOM then try to update the DICOM header with the latest values
							return updateDICOMHeaderHandleExceptions(response, storedFormat, storedQuality);
						}
					}
					return response;
				}

				// if we get to here, we have to do a conversion, put it into the buffer?
				
				// at this point we know we need to do some conversion, so make sure the image 
				// is in a buffer in case we need to reuse it.
				
				// no longer need to put into buffer, will be handled internally (right?)
				
				//response = storageFacade.putStreamIntoBuffer(imageFilename, response);	
				

				logger.info("Able to convert from [" + storedFormat + "] to [" + quality.getImageFormat() + "]");
				transactionContext.addDebugInformation("Converting from '" + storedFormat + "' to '" + quality.getImageFormat() + "'.");
				try
				{
					// if requested quality format is DICOM then we need the TXT file before we call compress and convert (to build the header)
					if(ImageFormat.isDICOMFormat(quality.getImageFormat()))
					{
						if(isDecompress(storedFormat, quality.getImageFormat()))
						{
							ImageStreamResponse convertedResponse = decompressImage(response, quality);
							if(convertedResponse != null)
							{
								return convertedResponse;
							}
						}
						else
						{
							// do conversion
							if(response.getTxtStream() == null)
							{
								ByteBufferBackedInputStream txtStream = 
									getTxtStreamHandleException(imageFilename, storageCredentials, hisUpdate);
								response.setTxtStream(txtStream);
							}
							ImageStreamResponse convertedResponse = compressAndConvertImage(response, quality);
							if(convertedResponse != null)
							{
								return convertedResponse;
							}
						}
					}
					else
					{
						// check to see if should do a decompression rather than compression
						if(isDecompress(storedFormat, quality.getImageFormat()))
						{
							// not creating a DICOM file so only doing compression, return TXT file after making the image
							ImageStreamResponse convertedResponse = decompressImage(response, quality);
							if(convertedResponse != null)
							{
								if((quality.getImageQuality() != ImageQuality.THUMBNAIL)
										&& (convertedResponse.getTxtStream() == null)
										&& (!ImageFormat.isDICOMFormat(response.getImageFormat())))
								{
									ByteBufferBackedInputStream txtStream = 
										getTxtStreamHandleException(imageFilename, storageCredentials, hisUpdate);
									convertedResponse.setTxtStream(txtStream);
								}
								return convertedResponse;
							}
						}
						else
						{							
							String modality = null;
							// only get the modality if the requested quality is not thumbnail
							if(quality.getImageQuality() != ImageQuality.THUMBNAIL)
							{
								// get modality code from TXT file for non-DICOM compression
								ByteBufferBackedInputStream txtStream = response.getTxtStream();
								if(txtStream == null)
									txtStream = getTxtStreamHandleException(imageFilename, storageCredentials, hisUpdate);
								modality = getTXTModality(txtStream); // get modality code and close partially consumed stream!
							}
							// not creating a DICOM file so only doing compression, return TXT file after making the image
							ImageStreamResponse convertedResponse = compressImage(imageFilename, response, quality, modality);
							if(convertedResponse != null)
							{
								if((quality.getImageQuality() != ImageQuality.THUMBNAIL)
										&& (convertedResponse.getTxtStream() == null)
										&& (!ImageFormat.isDICOMFormat(response.getImageFormat()))
										&& (txtFileAvailable))
								{
									ByteBufferBackedInputStream txtStream = 
										getTxtStreamHandleException(imageFilename, storageCredentials, hisUpdate);
									convertedResponse.setTxtStream(txtStream);
								}
								return convertedResponse;
							}
						}
					}
				}
				catch(ImageConversionDecompressionException icdX)
				{
					logger.error("Decompression error during converting image [" + imageFilename + "]", icdX);
					transactionContext.addDebugInformation("ImageConversionDecompressionException converting image, " + icdX.getMessage() + ".");
				}
				catch(ImageConversionCompressionException iccX)
				{
					logger.error("Compression error during converting image [" + imageFilename + "]", iccX);
					transactionContext.addDebugInformation("ImageConversionCompressionException converting image, " + iccX.getMessage() + ".");
				}
				catch(ImageConversionIOException icioX)
				{
					logger.error("IO error during converting image [" + imageFilename + "]", icioX);
					transactionContext.addDebugInformation("ImageConversionIOException converting image, " + icioX.getMessage() + ".");
				}
				catch(ImageConversionInvalidInputException iciiX)
				{
					logger.error("Input data error during converting image [" + imageFilename + "]", iciiX);
					transactionContext.addDebugInformation("ImageConversionInvalidInputException converting image, " + iciiX.getMessage() + ".");
				}
				catch(IOException ioX)
				{
					logger.error("IOException while converting image [" + imageFilename + "]", ioX);
					transactionContext.addDebugInformation("IOException converting image, " + ioX.getMessage() + ".");
				}

				logger.info("Not able to convert from [" + storedFormat + "] to [" + quality.getImageFormat() + "], trying next conversion option");
				// its not necessary to close the input stream, if we got here then we tried to do conversions
				// which means the image was buffered so the buffering handled closing the input streams
				logger.info("Re-opening image stream");
				// not really sure if this is necessary anymore
				response = storageFacade.openImageStream(imageFilename, 
						storageCredentials, StorageProximity.OFFLINE, requestFormatQualityList);
				storedFormat = response.getImageFormat();
				storedQuality = response.getImageQuality();
				if(storedQuality == null)
					storedQuality = filePath.getImageQuality();
				if(response.getImageQuality() == null)
					response.setImageQuality(storedQuality);
				quality = getNextAllowableImageFormatQuality(storedFormat, qualitiesIterator);
		
			}
			// if we got here then we couldn't do anything to the image to satisfy the request
			String msg = "Couldn't perform acceptable [" + storedFormat + 
			"] data conversion to match requested format(s) [" + convertFormatQualityList.getAcceptString(false) + "]";
			//logger.info(, throwing ImageConversionException");
			logger.error(msg);
			logger.info("Closing possibly still open image and TXT streams");
			if(response.getImageStream() != null)
				response.getImageStream().closeSafely();
			if(response.getTxtStream() != null)
				response.getTxtStream().closeSafely();
			throw new ImageConversionException(msg);
		}
		finally
		{
			//returnBuffers();
		}
	}

	/**
	 * This method parses an opened TXT file stream for DICOM Modality code.
	 * Before returning the modality code the partially read stream is closed.
	 * @param txtstream
	 * 			A sized InputStream referring to TXT stream and its length
	 * @throws ImageConversionIOException
	 */
	private String getTXTModality(ByteBufferBackedInputStream txtStream) {
		String mty="";
	    String textLine = "";
	    int bytesRead=0;
		if ((txtStream == null) || (!txtStream.isReadable()))
			return mty;
		try 
		{
			int fsize = txtStream.getSize();
			if (fsize > 0) {
		            BufferedReader buffer = new BufferedReader(new InputStreamReader(txtStream.getInputStream()));
		            do{// parse for "MODALITY=" or "Modality|1,1|" and take the next 2 bytes
		                do{ // get next line
		                	if (bytesRead >= fsize)
		                		return mty;
		                    textLine = buffer.readLine();
			                if(textLine == null) // EOF
		                        return mty;
			                bytesRead += textLine.length();
		                } while (textLine.equals(""));
		            } while( !((textLine.startsWith("MODALITY")) || (textLine.startsWith("0008,0060|Modality"))) );
		            if (textLine.startsWith("MODALITY"))
		            	mty=textLine.substring(textLine.indexOf("=")+1);
		            else
		            	mty=textLine.substring(textLine.lastIndexOf("|")+1);
			}
		} 
		catch (IOException ioX) {
			logger.error("Error extracting Modality code from TXT stream", ioX);
		}
		finally 
		{
			txtStream.closeSafely();
		}
		return mty;
	}
	
	private boolean canCompressFormat(ImageFormat imageFormat)
	{
		if(imageFormat == null)
			return false;
		ImageFormatAllowableConversionList conversionList = getImageConfiguration().getFormatConfiguration(imageFormat);
		if(conversionList == null)
			return false;
		return conversionList.isCanCompress();
	}

	/**
	 * Retrieves a TXT stream from the storage facade.
	 * 
	 * @param imageFilename
	 * @param storageCredentials
	 * @param hisUpdate Hashmap containing name/value pairs of the HIS update of patient data.
	 * @return
	 */
	public ByteBufferBackedInputStream GetTxtStream(String imageFilename, StorageCredentials storageCredentials, 
			HashMap<String, String> hisUpdate)
	throws ImageNotFoundException, ImageNearLineException, ConnectionException, MethodException
	{
		logger.info("Opening stream to TXT file for image [" + imageFilename + "]");
		ByteBufferBackedInputStream txtStream = storageFacade.openTXTStream(imageFilename, 
				storageCredentials, null);
		logger.info("Got stream to TXT file for image [" + imageFilename + "]");
		return appendTxtStreamWithHisUpdate(txtStream, hisUpdate);
	}
	
	private ByteBufferBackedInputStream getTxtStreamHandleException(String imageFilename, 
			StorageCredentials storageCredentials, HashMap<String, String> hisUpdate)
	{
		try
		{
			return GetTxtStream(imageFilename, storageCredentials, hisUpdate);
		}
		catch(ImageNotFoundException infX)
		{
			logger.error("TXT File for image [" + imageFilename + "] not found", infX);
		}
		catch(ImageNearLineException inlX)
		{
			logger.error("TXT File for image [" + imageFilename + "] nearline exception", inlX);
		}
		catch(ConnectionException cX)
		{
			logger.error("TXT File for image [" + imageFilename + "] connection exception", cX);
		}
		catch(MethodException mX)
		{
			logger.error("TXT File for image [" + imageFilename + "] method exception", mX);
		}
		return null;
	}

	private ByteBufferBackedInputStream appendTxtStreamWithHisUpdate(ByteBufferBackedInputStream txtStream, 
			HashMap<String, String> hisUpdate)
	{		
		/*
		if((hisUpdate == null) || (hisUpdate.size() <= 0))
			return txtStream;
		 */
		int bytesRead = 0;

		StringBuilder sb = new StringBuilder();

		sb.append("$$BEGIN HIS UPDATE");
		sb.append("\n");

		String lastKey = "";
		int keyCount = 1;
		if(hisUpdate != null)
		{
			Set<Map.Entry<String, String>> entries = hisUpdate.entrySet();
			for(Entry<String, String> entry : entries)
			{
				String key = entry.getKey();
				if(key.equals(lastKey))
				{
					keyCount++;
				}

				String line = key + StringUtils.STICK + StringUtils.STICK + keyCount + ",1" + StringUtils.STICK + entry.getValue();
				lastKey = key;
				sb.append(line + "\n");
			}
		}
		sb.append("$$END HIS UPDATE\n");
		bytesRead = sb.length();
		InputStream hisStream = IOUtils.toInputStream(sb.toString());

		ByteBufferBackedInputStream sizedStream = null;
		if(txtStream == null)
		{
			logger.info("appendTxtStreamWithHisUpdate, txt stream is null, returning stream containing only HIS update");
			sizedStream = new ByteBufferBackedInputStream(hisStream, bytesRead);
		}
		else
		{
			logger.info("Combining HIS stream with TXT stream");
			InputStream textInputStream = txtStream.getInputStream();
			SequenceInputStream seqStream = new SequenceInputStream(textInputStream, hisStream);
			logger.info("HIS Stream has been joined with TXT stream");
			sizedStream = new ByteBufferBackedInputStream(seqStream, bytesRead + txtStream.getSize());			
		}
		return sizedStream;
	}

	/**
	 * Open the TXT file stream
	 * @param txtFilename The txt file to open
	 * @param storageCredentials the storage credentials used to access the image share
	 * @return The input stream to the file 
	 * @throws ImageNotFoundException Occurs if the file is not found or accessible
	 * @throws ImageNearLineException Occurs if the file is not accessible but is on a nearline storage device
	 */
	/*
	private SizedInputStream getTXTStream(String txtFilename, StorageCredentials storageCredentials)
	throws ImageNotFoundException, ImageNearLineException
	{		
		return SmbStorageUtility.openFileStream(txtFilename, storageCredentials, StorageProximity.OFFLINE);		
	}
	 */

	/**
	 * Returns the next allowable image format quality for the stored format.  
	 * @param storedFormat the format of the stored image
	 * @param qualitiesIterator The iterator of the qualities requested by the user
	 * @return An image format quality that was requested by the client and is allowable for the stored image format.
	 * Returns null if no format can be found that is allowed for the stored format
	 */
	private ImageFormatQuality getNextAllowableImageFormatQuality(ImageFormat storedFormat, 
			Iterator<ImageFormatQuality> qualitiesIterator)
	{
		logger.info("Finding next allowable conversion type for format [" + storedFormat + "]");
		ImageFormatAllowableConversionList formatAllowedList = getImageConfiguration().getFormatConfiguration(storedFormat);
		// if the list of allowable conversions is null, then no definitions for this format type
		// returning false to not allow the conversion, not sure if that is correct?
		if(formatAllowedList == null)
		{
			// JMW 2/21/2011 if the image format is not in the configuration file, then simply indicate it can
			// be converted into itself so if the user allows anything, it can be returned
			//logger.warn("List of allowed formats to convert to is null, returning null");
			logger.warn("List of allowed formats to convert '" + storedFormat + "' is null, not found in configuration file. Creating list only containing this format");
			formatAllowedList = new ImageFormatAllowableConversionList(storedFormat, false);
			// JMW 6/25/2013 - add this format as an allowed format to convert to (convert from self to self)
			formatAllowedList.add(storedFormat);
			//return null;
		}
		while(qualitiesIterator.hasNext())
		{
			ImageFormatQuality quality = qualitiesIterator.next();

			if((quality.getImageFormat() == ImageFormat.ORIGINAL) ||
					(formatAllowedList.isFormatConversionAllowed(quality.getImageFormat())))
			{
				logger.info("Found allowed and requested format [" + quality.getImageFormat() + "]");
				return quality;
			}
		}
		logger.info("Could not find any more formats to convert [" + storedFormat + "] into");
		return null;
	}

	/**
	 * Determines if the stored format is compressed and the target format is not compressed.
	 * 
	 * @param storedFormat
	 * @param targetFormat
	 * @return True if the stored format is compressed and target format is not compressed, false otherwise
	 */
	private boolean isDecompress(ImageFormat storedFormat, ImageFormat targetFormat)
	{
		if((storedFormat.isCompressed()) 
				&& (!targetFormat.isCompressed()))
		{
			return true;
		}
		return false;
	}

	private ByteBufferBackedImageStreamResponse decompressImage(ByteBufferBackedImageStreamResponse storedImage, 
			ImageFormatQuality quality)
	throws ImageConversionInvalidInputException, ImageConversionIOException, 
	ImageConversionDecompressionException, IOException
	{
		ImageFormat storedFormat = storedImage.getImageFormat();
		ImageFormat targetFormat = quality.getImageFormat();
		logger.info("decompress from [" + storedFormat + "] to [" + targetFormat + "]");		
		ImageQuality imageQuality = quality.getImageQuality();
		IImageConversion conversion = getImageConversion();

		ByteBufferBackedImageInputStream outStream = 
			conversion.DecompressImage(storedFormat, targetFormat, storedImage.getImageStream().toBufferedObject());
		return new ByteBufferBackedImageStreamResponse(outStream, imageQuality);
	}

	private ByteBufferBackedImageStreamResponse compressImage(String imageFilename, 
			ByteBufferBackedImageStreamResponse storedImage, ImageFormatQuality quality, String modality)
	throws ImageConversionDecompressionException, ImageConversionIOException, 
	ImageConversionInvalidInputException, ImageConversionCompressionException,
	IOException
	{
		ImageFormat storedFormat = storedImage.getImageFormat();
		ImageFormat targetFormat = quality.getImageFormat();
		logger.info("compress from [" + storedFormat + "] to [" + targetFormat + "]");
			
		ImageQuality imageQuality = quality.getImageQuality();
		IImageConversion conversion = getImageConversion();

		ByteBufferBackedImageInputStream outStream = conversion.CompressImage(imageQuality, storedFormat, 
				targetFormat, storedImage.getImageStream().toBufferedObject(), modality);
		return new ByteBufferBackedImageStreamResponse(outStream, imageQuality);
	}

	/**
	 * This updates the DICOM header of an existing image that was not converted. This ensures the header
	 * for the image is correct and up to date.
	 * @param storedImage
	 * @param storedFormat
	 * @param imageQuality
	 * @return
	 * @throws ImageConversionDecompressionException
	 * @throws ImageConversionIOException
	 * @throws ImageConversionInvalidInputException
	 * @throws ImageConversionCompressionException
	 */
	private ByteBufferBackedImageStreamResponse updateDICOMHeader(ByteBufferBackedImageStreamResponse storedImage, 
			ImageFormat storedFormat, ImageQuality imageQuality)
	throws ImageConversionDecompressionException, ImageConversionIOException, 
	ImageConversionInvalidInputException, ImageConversionCompressionException,
	IOException
	{
		// JMW 4/18/20111 P104 - Laurel Bridge 3.3.22c fix makes it possible for us to
		// update the header in DICOM JPG images properly - they are no longer excluded
		
		// JMW 12/23/2010 p104 - the image conversion code has trouble updating DICOM JPG image headers
		// avoid issues and just don't bother updating them
		/*
		if(storedFormat == ImageFormat.DICOMJPEG)
		{
			logger.warn("Image stored in DICOMJPEG format, not able to update DICOM header for this format, returning image as stored.");
			return storedImage;
		}*/
		
		logger.info("Update header of stored format [" + storedFormat + "]");		
		IImageConversion conversion = getImageConversion();
		ByteBufferBackedImageInputStream outStream = 
			conversion.UpdateVAImage(storedFormat, storedImage.getImageStream().toBufferedObject(), 
					storedImage.getTxtStream().toBufferedObject());		
		return new ByteBufferBackedImageStreamResponse(outStream, imageQuality);
	}
	
	/**
	 * This method updates the DICOM image and handles any exceptions. This should always return the image although it might
	 * be the original image unchanged 
	 * 
	 * @param storedImage
	 * @param storedFormat
	 * @param imageQuality
	 * @return
	 */
	private ImageStreamResponse updateDICOMHeaderHandleExceptions(ByteBufferBackedImageStreamResponse storedImage,
			ImageFormat storedFormat, ImageQuality imageQuality)
	{
		// if DICOM then try to update the DICOM header with the latest values
		try
		{
			logger.info("Image is in DICOM format, updating DICOM header with TXT file and HIS updates");
			ImageStreamResponse updatedResponse = updateDICOMHeader(storedImage, storedFormat, imageQuality);
			if(updatedResponse == null)
				logger.error("Response from updating DICOM header was null, this should NEVER happen!");
			else
				return updatedResponse;
			// if got to here then the result was null (shouldn't happen), 
			// but the current response object should still contain the image and text 
			// file (since it was buffered), so it shouldn't have to do anything
		}
		catch(IOException ioX)
		{
			logger.error("IOException updating DICOM header, " + ioX.getMessage() + ". Will return image as stored.");
			// exception occurred but the current response object should still contain the image and text 
			// file (since it was buffered), so it shouldn't have to do anything, we can just return that
		}
		catch(ImageConversionDecompressionException icdX)
		{
			logger.error("ImageConversionDecompressionException updating DICOM header, " + icdX.getMessage() + ". Will return image as stored.");
			// exception occurred but the current response object should still contain the image and text 
			// file (since it was buffered), so it shouldn't have to do anything, we can just return that
		}
		catch(ImageConversionCompressionException iccX)
		{
			logger.error("ImageConversionCompressionException updating DICOM header, " + iccX.getMessage() + ". Will return image as stored.");
			// exception occurred but the current response object should still contain the image and text 
			// file (since it was buffered), so it shouldn't have to do anything, we can just return that
		}
		catch(ImageConversionInvalidInputException iciiX)
		{
			logger.error("ImageConversionInvalidInputException updating DICOM header, " + iciiX.getMessage() + ". Will return image as stored.", iciiX);
			// exception occurred but the current response object should still contain the image and text 
			// file (since it was buffered), so it shouldn't have to do anything, we can just return that
		}
		catch(ImageConversionIOException icioX)
		{
			logger.error("ImageConversionIOException updating DICOM header, " + icioX.getMessage() + ". Will return image as stored.");
			// exception occurred but the current response object should still contain the image and text 
			// file (since it was buffered), so it shouldn't have to do anything, we can just return that
		}
		return storedImage;
	}
	
	private void setImageStreamResponseTextFileIfNecessary(ByteBufferBackedImageStreamResponse storedImage,
			ImageFormat storedFormat, String imageFilename, StorageCredentials storageCredentials, 
			HashMap<String, String> hisUpdate, boolean txtFileAvailable)
	{
		if((storedImage.getTxtStream() == null) && (txtFileAvailable))
		{
			boolean getTextFile = false;
			if(ImageFormat.isDICOMFormat(storedFormat))
			{
				if(updateDicomHeaders)
				{
					getTextFile = true;
				}
			}
			else
			{
				getTextFile = true;
			}
			if(getTextFile)
			{
				ByteBufferBackedInputStream txtStream = getTxtStreamHandleException(imageFilename, 
						storageCredentials, hisUpdate);
				storedImage.setTxtStream(txtStream);
			}
		}	
	}

	/**
	 * This method is for compressing and converting images into DICOM
	 * @param storedImage
	 * @param quality
	 * @return
	 * @throws ImageConversionDecompressionException
	 * @throws ImageConversionIOException
	 * @throws ImageConversionInvalidInputException
	 * @throws ImageConversionCompressionException
	 * @throws IOException
	 */
	private ImageStreamResponse compressAndConvertImage(ByteBufferBackedImageStreamResponse storedImage, 
			ImageFormatQuality quality)
	throws ImageConversionDecompressionException, ImageConversionIOException, 
	ImageConversionInvalidInputException, ImageConversionCompressionException,
	IOException
	{		
		ImageFormat storedFormat = storedImage.getImageFormat();
		ImageFormat targetFormat = quality.getImageFormat();
		logger.info("compress and convert from [" + storedFormat + "] to [" + targetFormat + "]");
		
		if(storedFormat == targetFormat)
		{
			// the stored format is DICOM
			if(updateDicomHeaders)
			{
				logger.info("Stored format is the same as target format, updating DICOM header and returning image.");
				// if DICOM then try to update the DICOM header with the latest values
				return updateDICOMHeaderHandleExceptions(storedImage, storedFormat, quality.getImageQuality());
			}
			logger.info("Stored format is the same as target format, returning input.");
			return storedImage;
		}
		
		else if (targetFormat == ImageFormat.ORIGINAL)
		{
			logger.info("Target format is ORIGINAL, returning input");
			return storedImage;
		}
		ImageQuality imageQuality = quality.getImageQuality();
		IImageConversion conversion = getImageConversion();
		ByteBufferBackedObject txtObject = null;
		if(storedImage.getTxtStream() != null)
			txtObject = storedImage.getTxtStream().toBufferedObject();
		if(imageQuality == ImageQuality.THUMBNAIL)
		{
			
			ByteBufferBackedImageInputStream outStream = conversion.ConvertImage(storedFormat, targetFormat, 
					imageQuality, storedImage.getImageStream().toBufferedObject(), txtObject);
			return new ByteBufferBackedImageStreamResponse(outStream, imageQuality);
		}
		else
		{
			ImageFormat convertFormat = targetFormat;
			/*
			 // JMW 9/4/2009 - no longer do this - VRad needs DICOM without doing J2K, requestor should know what they want!
			if((targetFormat == ImageFormat.DICOM)
					|| (targetFormat == ImageFormat.DICOMJPEG)
					|| (targetFormat == ImageFormat.DICOMJPEG2000))
			{
				logger.info("Request was for a DICOM type, changing to DICOM JPEG 2000");
				convertFormat = ImageFormat.DICOMJPEG2000;
			}*/
			ByteBufferBackedImageInputStream outStream = conversion.ConvertImage(storedFormat, convertFormat, 
					imageQuality, storedImage.getImageStream().toBufferedObject(), txtObject);
			return new ByteBufferBackedImageStreamResponse(outStream, imageQuality);
		}
	}

	private boolean requestAllowsOriginalImageType(ImageFormatQualityList requestFormatQualityList)
	{
		if(requestFormatQualityList == null)
			return false;
		for(ImageFormatQuality quality : requestFormatQualityList)
		{
			if(quality.getImageFormat() == ImageFormat.ORIGINAL)
				return true;
		}
		return false;
	}

	private boolean isStoredFormatInRequest(ImageFormatQualityList requestFormatQualityList, 
			ImageFormat storedFormat, ImageQuality storedQuality)
	{
		if(requestFormatQualityList == null)
			return false;
		for(ImageFormatQuality quality : requestFormatQualityList)
		{
			// JMW 1/26/2009 - change to this function, used to compare format and quality directly
			// now, if format is the same and storedQuality >= quality, then return true
			// quality must be better than or equal to to be true.			
			if(quality.getImageFormat() == storedFormat)
			{
				if(quality.getImageQuality().getCanonical() <= storedQuality.getCanonical())
					return true;
			}		
		}
		return false;
	}


	/*
	private StorageProximity getStorageProximityFromVistaLocation(String imgLocation)
	{
		if(IMAGE_LOCATION_MAGNETIC.equals(imgLocation))
		{
			return StorageProximity.ONLINE;
		}
	}
	 */
}
