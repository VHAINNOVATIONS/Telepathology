/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: August 10, 2006
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWTITTOC
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
package gov.va.med.imaging.exchange.conversion;

import gov.va.med.imaging.SizedInputStream;
import gov.va.med.imaging.core.interfaces.IImageConversionConfiguration;
import gov.va.med.imaging.core.interfaces.IImageLossyCompressionConfiguration;
import gov.va.med.imaging.dicom.utilities.api.reconstitution.interfaces.DicomObjectReconstitutionFacade;
import gov.va.med.imaging.dicom.utilities.exceptions.GenericDicomReconstitutionException;
import gov.va.med.imaging.exchange.conversion.exceptions.ImageConversionCompressionException;
import gov.va.med.imaging.exchange.conversion.exceptions.ImageConversionDecompressionException;
import gov.va.med.imaging.exchange.conversion.exceptions.ImageConversionIOException;
import gov.va.med.imaging.exchange.conversion.exceptions.ImageConversionInvalidInputException;
import gov.va.med.imaging.exchange.conversion.interfaces.IImageConversion;
import gov.va.med.imaging.exchange.enums.ImageFormat;
import gov.va.med.imaging.exchange.enums.ImageQuality;
import gov.va.med.imaging.exchange.storage.ByteBufferBackedImageInputStream;
import gov.va.med.imaging.exchange.storage.ByteBufferBackedObject;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.aware.j2k.codec.engine.AwJ2k;
import com.aware.j2k.codec.engine.AwJ2kCriticalException;
import com.aware.j2k.codec.engine.AwJ2kException;
import com.aware.j2k.codec.engine.AwJ2kParameters;
import com.aware.j2k.codec.engine.AwOutputImageValue;

/**
 * @author VHAISWTITTOC
 * The Image Conversion class shields all DICOM and compression related details from the
 * rest of the system. It uses streams to communicate with VistA Imaging legacy and with
 * the outside world. It conforms to the VA-DOD El Paso NDAA Image Sharing projects's
 * requirements in term of image conversions and to ViX-to-ViX operations.
 * 
 * Note: Spring supplied singleton
 */
public class ImageConversion 
implements IImageConversion 
{
	private IImageConversionConfiguration imageConversionConfiguration = null;
	private IImageLossyCompressionConfiguration imageLossyCompressionConfiguration = null;

    private static final Logger LOGGER = Logger.getLogger (ImageConversion.class);

	private DicomObjectReconstitutionFacade dicomUtilities;
	
	public ImageConversion(IImageConversionConfiguration imageConversionConfiguration,
			IImageLossyCompressionConfiguration imageLossyCompressionConfiguration)
	{
		this.imageConversionConfiguration = imageConversionConfiguration;
		this.imageLossyCompressionConfiguration = imageLossyCompressionConfiguration;
	}
	
    /**
     * Using Spring Factory.  Set DicomObjectReconstitutionFacade.
     * @param dicomReconFacade represents the Spring Factory selected object
     */
    public void setDicomUtilities(DicomObjectReconstitutionFacade dicomReconFacade) 
    {
        this.dicomUtilities = dicomReconFacade;
    }
    
	/**
	 * ConvertImage is bidirectional. VA Input thumbnails can be TGA, TIFF or JPEG,
	 * the DoD thumbnails are JPEG, final thumbnails are always in JPEG (lossy).
	 * VA input images are TGA+TXT or DICOM, DoD input images as well as all output
	 * images are DICOM (JPEG/J2K). DoD delivered images are always in DICOM format
	 * 
	 * @param sourceImageFormat
	 * 			DOWNSAMPLEDTGA, TGA, TIFF, JPEG, DICOMJPEG or DICOMJPEG2000
	 * @param targetImageFormat
	 * 			DOWNSAMPLEDTGA, TGA, DICOMJPEG or DICOMJPEG2000, DICOM (for VA REF/DIAG TGA with TXT file) 
	 * @param ImageQuality
	 * 			DIAGNOSTIC, REFERENCE, THUMBNAIL
	 * @param sourceStream1
	 * 			SizedInputStream of image (JPEG, TGA or DICOM), with bytesize
	 * @param sourceStream2
	 * 			Optional SizedInputStream of TXT file or null, with bytesize
	 * @return SizedInputStream
	 * 			Stream of image (JPEG, DICOM, maybe TGA), with bytesize
	 * @throws ImageConversionInvalidInputException	on invalid input parameters
	 * @throws ImageConversionIOException
	 * @throws ImageConversionCompressionException
	 * @throws ImageConversionDecompressionException
	 */
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.conversion.interfaces.IImageConversion#ConvertImage(gov.va.med.imaging.exchange.enums.ImageFormat, gov.va.med.imaging.exchange.enums.ImageFormat, gov.va.med.imaging.exchange.enums.ImageQuality, gov.va.med.imaging.SizedInputStream, gov.va.med.imaging.SizedInputStream, gov.va.med.imaging.SizedInputStream)
	 */
	public ByteBufferBackedImageInputStream ConvertImage(
			ImageFormat sourceImageFormat,	// never null as input
			ImageFormat targetImageFormat,	// never null as input
			ImageQuality quality,			// never null as input
			ByteBufferBackedObject imageObject,	// never null as input
			ByteBufferBackedObject txtObject)	// can be null for thumbnails and DoD2VA only
//			HashMap theHISChanges)			// it was used for VA2DoD only, not for thumbnails
	throws ImageConversionInvalidInputException, ImageConversionIOException,
		   ImageConversionCompressionException, ImageConversionDecompressionException
    //
	// op-s and buffer handling:
	//
	// DoD2VA: NOOP for TN/REF/DIAG: sourceStreeam1 -> buf1 -> targetStream1
	//      or [op1: decompress] & [op2: DICOM sanitize]]
	//         (sS1 -> [buf1 -> [op1 ->] buf2) & (buf2 -> [op2 ->]] tS)        - dcm
    //
	// VA2DOD: [op1: (toDicom & update) or (update only)] & [op2: compress]
	//         (sS1[[+sS2] -> op1 -> buf1) & (op2 -> buf2] -> tS)              - dcmjxx
	//      or for thumbnails: sS1 -> buf1 [-> op2 ] -> tS                     - jpg
	{
        
        //ByteBufferBackedImageInputStream targetStream = null;
		ByteBufferBackedObject convertedObject = null;
        

		try 
		{
			// check input parameters for sanity
			if ( (sourceImageFormat == null) || (targetImageFormat == null) ||
				 (quality == null) || (imageObject == null)) 
			{
				String guiltList = "";
				if (sourceImageFormat == null) guiltList += "sourceImageFormat, ";
				if (targetImageFormat == null) guiltList += "targetImageFormat, ";
				if (quality == null) guiltList += "quality, ";
				if (imageObject == null) guiltList += "sizedSourceStream1, ";
				if (guiltList.length() > 2) guiltList = guiltList.substring(0, guiltList.length()-2);
		        LOGGER.error("ImageConversion has invalid (null) input parameter(s): " + guiltList);
				throw new ImageConversionInvalidInputException("Image Conversion has invalid (null) input parameter" + guiltList);
			}
			
			if ( (sourceImageFormat == ImageFormat.DICOMJPEG) ||
				 (sourceImageFormat == ImageFormat.DICOMJPEG2000) ) 
			{	
				// DoD2VA or fancy V2V conversion
				if ( (targetImageFormat != ImageFormat.DICOMJPEG) &&
					 (targetImageFormat != ImageFormat.DICOMJPEG2000) &&
					 (targetImageFormat != ImageFormat.DICOM) ) 
				{		// fancy V2V non-DICOM conversion
					return new ByteBufferBackedImageInputStream(dicomCompress(imageObject, 
							quality, targetImageFormat, true));	
				}
				else if ((sourceImageFormat == targetImageFormat) 
						&& (txtObject == null)) 
				{
					return new ByteBufferBackedImageInputStream(imageObject);
				}
				else {	// something to do
					
					if ( (txtObject != null)) 
					{	// V2V DICOM header update from TXT file			
						// txt in stream 2, DICOM in stream 1; output to buffer1
						convertedObject = updateDicomObject(imageObject, txtObject);
					} 		
					else 
					{
						convertedObject = imageObject;
					} // buffer1 has stream1
					if (sourceImageFormat == targetImageFormat) 
					{ 						
						// forward buffer1 to target stream
						//toTargetStream(targetStream, bufs, 1);
						// do nothing, targetStream already set from previous step
						return new ByteBufferBackedImageInputStream(convertedObject);
					} 
					else if ( ( (sourceImageFormat == ImageFormat.DICOMJPEG) &&
						   (targetImageFormat == ImageFormat.DICOMJPEG2000)) || 
						   ( (sourceImageFormat == ImageFormat.DICOMJPEG2000) &&
						     (targetImageFormat == ImageFormat.DICOMJPEG) ) )  
					{	// fancy V2V DICOM conversion
						// DICOM transfer syntax conversion (to lossy or lossless) 
						// with optional quality down grade -- REF or DIAG (no DIAGUC here!!!)
						// from buffer1 to targetStream/output using buffer 2
						return new ByteBufferBackedImageInputStream(dicomCompress(convertedObject, 
								quality, targetImageFormat, true));
					}
					else 
					{	// DoD2VA DICOM decompress to raw DICOM format (optional in future),
						// use buffer1, output to buffer2
						convertedObject = decompressDicomData(imageObject);
											
						// optionally, look for offending elements (icon image, etc.) in DICOM data 
						// and remove them (input from buffer2, output to tS]
						return new ByteBufferBackedImageInputStream(sanitizeDicomData(convertedObject)); // *** for now FAKE: pass through dicom data
					}
				}
			} 
			else 
			{													// VA2DoD or thumbnail			
				if (quality != ImageQuality.THUMBNAIL) 
				{
					// VA2DoD DICOM operations (on REFERENTIAL or DIAGNOSTIC images) 

					// 		-- TXT file cannot be null! (8/14/08 - cpt)
					if (txtObject == null) 
					{
						throw new ImageConversionInvalidInputException(
						"Image Conversion received null input parameter for TXT file stream (sizedSourceStream2 or sizedSourceStream2.getInStream())");
					}					
					if ((sourceImageFormat==ImageFormat.DICOM)) 
					{
						// DICOM input: update DICOM header only (from end of stream2), output to buffer1
						convertedObject = updateDicomObject(imageObject, txtObject);
					}
					else 
					{
						// reconstitute TGA and TXT files to DICOM and update header from end of TXT2,
						// output to buffer1 *** FAKE: from .DCM file
						convertedObject = createDicomObject(imageObject, txtObject); 
					}

					if ((quality==ImageQuality.DIAGNOSTICUNCOMPRESSED) ||	// bypass compression for Diagnostic with quality=100
						(imageConversionConfiguration.isNoLosslessCompression() &&
								(quality==ImageQuality.DIAGNOSTIC)) || 		// no compress if appconfig flag set 
						 (targetImageFormat == ImageFormat.DICOM)) 
					{		// non-compressed DICOM specifically asked 
					 	// funnel buffer1 to output
						// do nothing, targetStream already set
						return new ByteBufferBackedImageInputStream(convertedObject);
					}
					else 
					{
						return new ByteBufferBackedImageInputStream(dicomCompress(convertedObject, 
								quality, targetImageFormat, true));
					}
				}
				else 
				{		
					// Thumbnail operations
					if (sourceImageFormat != ImageFormat.JPEG) 
					{
						// compress TGA, TIFF or BMP to JPEG
						// sS1 input to buf1, output to Ts1 
						return thumbnailCompress(imageObject);
					} 
					else 
					{
						// no conversion to do: funnel input to output 				
						return new ByteBufferBackedImageInputStream(imageObject);
					}
				} 
			}	
		}
		catch (OutOfMemoryError oome) 
		{
	        LOGGER.error("ImageConversion Out of Memory error: ", oome);
			throw new ImageConversionIOException("ImageConversion Out of Memory error: ", oome);
		}
	}

	/**
	 * CompressImage is for ViX2ViX performance boost support over WAN lines when 
	 * DICOM conversion/update is not required. Expected Reference/Diagnostic quality
	 * input image types are BMP, TIFF, DICOM, TGA (mostly for Thumbnails). Thumbnail
	 * compression does the same conversion ConvertImage does, producing JPEG (lossy)
	 * output only.
	 * Already compressed inputs and failed J2K compression attempts throw exception.
	 * Note: LZW compressed TIFF images will fail.
	 * 
	 * @param ImageQuality
	 * 			THUMBNAIL, REFERENCE, DIAGNOSTIC  
	 * @param sourceImageFormat
	 * 			BMP, TIFF, TGA, JPEG, DICOM
	 * @param targetImageFormat
	 * 			JPEG (Thumbnail default), J2K (default), DICOMJPEG or DICOMJPEG2000
	 * @param imageObject
	 * 			SizedInputStream of image (JPEG, BMP, TIFF, DICOM, TGA), with bytesize
	 * @param modality
	 *  		DICOM modality type if available, else null
	 * @param modality
	 * 			2 byte DICOM Modality code; needed for non-DICOM input if possible
	 * @return SizedInputStream
	 * 			Stream of image (JPEG, J2K, DICOMJPEG, DICOMJ2K), with bytesize
	 * @throws ImageConversionInvalidInputException	on invalid input parameters
	 * @throws ImageConversionCompressionException
	 */
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.conversion.interfaces.IImageConversion#CompressImage(gov.va.med.imaging.exchange.enums.ImageQuality, gov.va.med.imaging.exchange.enums.ImageFormat, gov.va.med.imaging.SizedInputStream)
	 */
	public ByteBufferBackedImageInputStream CompressImage(
					  ImageQuality quality,			 // THUMBNAIL, REFERENCE, DIAGNOSTIC
					  ImageFormat sourceImageFormat, // BMP, TIFF, TGA, JPEG, DICOM
					  ImageFormat targetImageFormat, // JPEG, J2K, DICOMJPEG, DICOMJ2K
					  ByteBufferBackedObject imageObject,
					  String modality)				 // needed for non-DICOM input to guide compression Ratio/Quality
	throws ImageConversionInvalidInputException, ImageConversionIOException,
	ImageConversionCompressionException
	{       
		// check input parameters for sanity
		if ( (sourceImageFormat == null) || (targetImageFormat == null) ||
			 (quality == null) ||
			 (imageObject == null) ) {
			String guiltList = "";
			if (sourceImageFormat == null) guiltList += "sourceImageFormat, ";
			if (targetImageFormat == null) guiltList += "targetImageFormat, ";
			if (quality == null) guiltList += "quality, ";
			if (imageObject == null) guiltList += "sourceStream, ";
			if (guiltList.length() > 2) guiltList = guiltList.substring(0, guiltList.length()-2);
	        LOGGER.error("CompressImage has invalid (null) input parameter(s): " + guiltList);
			throw new ImageConversionInvalidInputException(
				"CompressImage has invalid (null) input parameter" + guiltList);
		}
		if (sourceImageFormat==targetImageFormat) 
		{ // do nothing: funnel input to output
			return new ByteBufferBackedImageInputStream(imageObject);
		}
		if (quality==ImageQuality.THUMBNAIL) 
		{	 // force JPEG (lossy) output
			return thumbnailCompress(imageObject);
		}

		if ((targetImageFormat!=ImageFormat.J2K) &&
			(targetImageFormat!=ImageFormat.DICOMJPEG2000) &&
			(targetImageFormat!=ImageFormat.JPEG) &&
			(targetImageFormat!=ImageFormat.DICOMJPEG)) 
		{
	        LOGGER.error("CompressImage got invalid targetImageFormat " + targetImageFormat.getType());
			throw new ImageConversionInvalidInputException(
				"CompressImage got invalid targetImageFormat " + targetImageFormat.getType());	
		}

		// DIAGNOSTIC or REFERENCE
		if ((sourceImageFormat==ImageFormat.BMP) ||
			(sourceImageFormat==ImageFormat.TGA) ||
			(sourceImageFormat==ImageFormat.TIFF)|| 
			(sourceImageFormat==ImageFormat.JPEG)) 
		{ 
			// make sure output is not DICOM
			if ((targetImageFormat==ImageFormat.DICOMJPEG) ||
				(targetImageFormat==ImageFormat.DICOMJPEG2000)) {
		        LOGGER.error("CompressImage cannot create DICOM compressed output for " + sourceImageFormat.getType() + " input!");
				throw new ImageConversionInvalidInputException(
					"CompressImage cannot create DICOM compressed output for " + sourceImageFormat.getType() + " input!");	
			}
			// Compress BMP, TGA, TIFF or JPEG input to J2K or JPEG format
			// do compress
			return Compress(imageObject, quality, targetImageFormat, modality, false);			
		}
		else if (sourceImageFormat==ImageFormat.DICOM) 
		{
			// send from buffer1 to compressor to buffer2 to tStream
			if ((targetImageFormat==ImageFormat.DICOMJPEG) ||
					(targetImageFormat==ImageFormat.DICOMJPEG2000)) 
			{
				return new ByteBufferBackedImageInputStream(dicomCompress(imageObject, quality, targetImageFormat, false));
			} 
			else 
			{ // JPEG or J2K output
				return Compress(imageObject, quality, targetImageFormat, modality, false);								
			}
		}
		else 
		{	// invalid input type
	        LOGGER.error("CompressImage got invalid sourceImageFormat " + sourceImageFormat.getType());
			throw new ImageConversionInvalidInputException(
				"CompressImage got invalid sourceImageFormat " + sourceImageFormat.getType());
		}        
	}

	/**
	 * DecompressImage is for ViX2ViX when client requests uncompressed data from 
	 * remote site. Only Diagnostic and Reference quality compressed data is expected.
	 * Expected input stream formats are J2K, JPEG, DICOMJ2K and DICOMJPEG.
	 * Expected output types are TIFF, TGA, BMP (for JPEG and J2K input) and DICOM 
	 * (for DICOMJ2K and DICOMJPEG).
	 * Uncompressed inputs and failed J2K decompression attempts throw exception.
	 * 
	 * @param sourceFormat
	 * 			J2K, JPEG, DICOMJ2K and DICOMJPEG
	 * @param targetFormat
	 * 			 BMP, TIFF, TGA, DICOM
	 * @param imageObject
	 * 			SizedInputStream of input image, with bytesize
	 * @return SizedInputStream
	 * 			Stream of image (TIFF, BMP, DICOM, maybe TGA), with bytesize
	 * @throws ImageConversionInvalidInputException	on invalid input parameters
	 * @throws ImageConversionDecompressionException
	 */
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.conversion.interfaces.IImageConversion#DecompressImage(gov.va.med.imaging.exchange.enums.ImageFormat, gov.va.med.imaging.exchange.enums.ImageFormat, gov.va.med.imaging.SizedInputStream)
	 */
	public ByteBufferBackedImageInputStream DecompressImage(
								ImageFormat sourceImageFormat, // JPEG, J2K, DICOMJPEG, DICOMJ2K
								ImageFormat targetImageFormat, // BMP, TIFF, TGA, DICOM
								ByteBufferBackedObject imageObject)
	throws ImageConversionInvalidInputException, ImageConversionIOException,
	ImageConversionDecompressionException
	{
		// check input parameters for sanity
		if ( (sourceImageFormat == null) || (targetImageFormat == null) ||
			 (imageObject == null)  ) 
		{
			String guiltList = "";
			if (sourceImageFormat == null) guiltList += "sourceImageFormat, ";
			if (targetImageFormat == null) guiltList += "targetImageFormat, ";
			if (imageObject == null) guiltList += "sourceStream, ";
			if (guiltList.length() > 2) guiltList = guiltList.substring(0, guiltList.length()-2);
	        LOGGER.error("DecompressImage has invalid (null) input parameter(s): " + guiltList);
			throw new ImageConversionInvalidInputException(
				"DecompressImage has invalid (null) input parameter" + guiltList);
		}
		if (sourceImageFormat==targetImageFormat) 
		{ // do nothing: funnel input to output
			return new ByteBufferBackedImageInputStream(imageObject);
		}
		
		if ((sourceImageFormat!=ImageFormat.J2K) &&
			(sourceImageFormat!=ImageFormat.DICOMJPEG2000) &&
			(sourceImageFormat!=ImageFormat.JPEG) &&
			(sourceImageFormat!=ImageFormat.DICOMJPEG)) 
		{
	        LOGGER.error("DecompressImage got invalid sourceImageFormat " + sourceImageFormat);
			throw new ImageConversionInvalidInputException(
				"DecompressImage got invalid sourceImageFormat " + sourceImageFormat);	
		}
		if ((targetImageFormat!=ImageFormat.BMP) &&
			(targetImageFormat!=ImageFormat.TIFF) &&
			(targetImageFormat!=ImageFormat.TGA) &&
			(targetImageFormat!=ImageFormat.DICOM)) 
		{
	        LOGGER.error("DecompressImage got invalid targetImageFormat " + targetImageFormat.getType());
			throw new ImageConversionInvalidInputException(
				"DecompressImage got invalid targetImageFormat " + targetImageFormat.getType());	
		}
		boolean badCompany=false;
		if (targetImageFormat==ImageFormat.DICOM) {
			badCompany = ((sourceImageFormat!=ImageFormat.DICOMJPEG2000) &&
					(sourceImageFormat!=ImageFormat.DICOMJPEG));
		}
		else { // BMP, TIFF, TGA input
			badCompany = ((sourceImageFormat!=ImageFormat.J2K) &&
					(sourceImageFormat!=ImageFormat.JPEG));
		}
		if (badCompany) {
	        LOGGER.error("DecompressImage got ImageFormat pair -- " + 
				sourceImageFormat + " in & " + targetImageFormat + " out!");
			throw new ImageConversionInvalidInputException(
				"DecompressImage got invalid ImageFormat pair -- " + 
				sourceImageFormat + " in & " + targetImageFormat + " out!");	
		}			
		return decompressStream(targetImageFormat, imageObject);			
	}

	/**
	 * UpdateVAImage returns a DICOM stream that is updated by VA TXT input stream. It does 
	 * not apply compression. VA Referential or Diagnostic quality Input is assumed (TGA+TXT
	 * or DICOM+TXT), the same quality is returned.
	 * 
	 * @param sourceImageFormat
	 * 			DOWNSAMPLEDTGA, TGA, DICOM, DICOMJPEG
	 * @param sourceStream1
	 * 			SizedInputStream of image (TGA or DICOM...), with bytesize
	 * @param sourceStream2
	 * 			SizedInputStream of TXT file, with bytesize
	 * @return SizedInputStream
	 * 			Stream of raw DICOM or DICOMJPEG image, with bytesize
	 * @throws ImageConversionInvalidInputException	on invalid input parameters
	 * @throws ImageConversionIOException
	 */	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.conversion.interfaces.IImageConversion#UpdateVAImage(gov.va.med.imaging.exchange.enums.ImageFormat, gov.va.med.imaging.exchange.enums.ImageFormat, gov.va.med.imaging.exchange.enums.ImageQuality, gov.va.med.imaging.SizedInputStream, gov.va.med.imaging.SizedInputStream, gov.va.med.imaging.SizedInputStream)
	 */
	public ByteBufferBackedImageInputStream UpdateVAImage(
			ImageFormat sourceImageFormat,			// never null as input
			ByteBufferBackedObject imageObject,	// never null as input
			ByteBufferBackedObject txtObject)	// never null as input
	throws ImageConversionInvalidInputException, ImageConversionIOException
    //
	// op-s and buffer handling:
	//
	// VA2xxx: op: (toDicom & update) or (update only)
	//        (sS1+sS2 -> op -> buf1 -> tS)              - dcm
	{
		try 
		{
			// check input parameters for sanity
			if ( (sourceImageFormat == null) || 
				 (imageObject == null) || 
				 (txtObject == null) ) {
				String guiltList = "";
				if (sourceImageFormat == null) guiltList += "sourceImageFormat, ";
				if (imageObject == null) guiltList += "sizedSourceStream1, ";				
				if (txtObject == null) guiltList += "sizedSourceStream2, ";
				if (guiltList.length() > 2) guiltList = guiltList.substring(0, guiltList.length()-2);
		        LOGGER.error("UpdateVAImage has invalid (null) input parameter(s): " + guiltList);
				throw new ImageConversionInvalidInputException("Image Conversion has invalid (null) input parameter" + guiltList);
			} 
			else if ( (sourceImageFormat != ImageFormat.DOWNSAMPLEDTGA) && (sourceImageFormat != ImageFormat.TGA) &&
					  (sourceImageFormat != ImageFormat.DICOM) && (sourceImageFormat != ImageFormat.DICOMJPEG) ) {
		        LOGGER.error("UpdateVAImage has invalid sourceImageFormat: " + sourceImageFormat.toString());
				throw new ImageConversionInvalidInputException("Image Conversion has sourceImageFormat: " + sourceImageFormat.toString());
			}
			
			if ( (sourceImageFormat == ImageFormat.DICOM) || (sourceImageFormat == ImageFormat.DICOMJPEG) ) 
			{
				// update DCM stream with TXT data
				return new ByteBufferBackedImageInputStream(updateDicomObject(imageObject, 
						txtObject));
			}
			else 
			{
				// reconstitute TGA and TXT files to DICOM and update header from end of TXT2,
				// output to buffer1 *** FAKE: from .DCM file
				return new ByteBufferBackedImageInputStream(createDicomObject(imageObject, 
						txtObject));
			}
		}
		catch (OutOfMemoryError oome) 
		{
	        LOGGER.error("UpdateVAImage Out of Memory error: ", oome);
			throw new ImageConversionIOException("UpdateVAImage Out of Memory error: ", oome);
		}		
	}


	// ===================================== Private (De)Compression methods =========================================
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.conversion.interfaces.IImageConversion#createDicomObject(gov.va.med.imaging.SizedInputStream, gov.va.med.imaging.SizedInputStream, gov.va.med.imaging.exchange.conversion.ImageBuffers)
	 */
	private ByteBufferBackedImageInputStream thumbnailCompress(ByteBufferBackedObject imageObject)
	throws ImageConversionIOException, ImageConversionCompressionException
	{
		// compress TGA, BMP, or TIFF to JPEG
		// sS1 input to buf1, output to Ts1 

        LOGGER.info("JPEG compress Thumbnail and stream it");
        AwJ2k codec = null;
        
	    try 
	    {
	        codec = new AwJ2k();
	        AwJ2k.AwJ2kEnableErrorProtectionMode(true);

	        codec.AwJ2kSetInputImage(imageObject.getBuffer().array(), imageObject.getSize());

	        codec.AwJ2kSetOutputType(AwJ2kParameters.AW_J2K_FORMAT_JPG);

	        AwOutputImageValue valueObj = codec.AwJ2kGetOutputImage();
	        // must copy out data from compressor buffer before leaving :-(
	        
	        ByteBuffer output = ByteBuffer.allocate(valueObj.getImageLength());
	        output.put(valueObj.getImage());
	        return new ByteBufferBackedImageInputStream(output, valueObj.getImageLength());
	    }
	    catch (AwJ2kException e) 
	    {
	        LOGGER.error("ThumbnailCompress - Compression Exception: " + e.getMessage()
	        			 + " (" + imageObject.getSize() + " bytes in)");
	    	throw new ImageConversionCompressionException("ThumbnailCompress - Compression Exception: ", e);
	    }
		catch (AwJ2kCriticalException  ce) {
	        LOGGER.error("ThumbnailCompress - critical exception " + ce.getMessage());
			throw new ImageConversionCompressionException("ThumbnailCompress - critical exception", ce);
		}
		catch (Exception e) {
	        LOGGER.error("ThumbnailCompress - generic exception " + e.getMessage());
			throw new ImageConversionCompressionException("ThumbnailCompress - generic exception", e);
		}
		finally 
		{
			try
			{
				if (codec!=null)
					codec.AwJ2kDestroy();
//				mutex.unlock();
			} 
			catch (AwJ2kException ex) 
			{
		        LOGGER.error("ThumbnailCompress - AWJ2K Destroy Exception: " + ex.getMessage()
	        			 + " (" + imageObject.getSize() + " bytes in)");
//				mutex.unlock();
		        throw new ImageConversionCompressionException("ThumbnailCompress - AWJ2K Destroy Exception: ", ex);
			}
		}
	}	
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.conversion.interfaces.IImageConversion#dicomCompress(gov.va.med.imaging.exchange.enums.ImageQuality, gov.va.med.imaging.exchange.enums.ImageFormat, gov.va.med.imaging.SizedInputStream, gov.va.med.imaging.exchange.conversion.ImageBuffers)
	 */
	private ByteBufferBackedObject dicomCompress(ByteBufferBackedObject imageObject,
			ImageQuality quality, ImageFormat targetFormat, boolean xChangeUse) 
	throws ImageConversionIOException, ImageConversionCompressionException 
	{
		// DICOM image compression (not thumbnail) to DICOMJPEG2000 or DICOMJPEG
		// from buffer1 to compressor to buffer2 to tStream

		LOGGER.info("Compress Dicom Object(" + imageObject.getSize() + ") and stream it");
		int autoLevel=-1; // Automatically determines the best number of compression levels for J2K
		float lossyCompressionRatio;
		int lossyJpgQuality;
		AwJ2k codec = null;

	    try 
	    {
    		String modality = getDicomModality(imageObject.getBuffer());
		    codec = new AwJ2k();
	        AwJ2k.AwJ2kEnableErrorProtectionMode(true);

	        codec.AwJ2kSetInputImage(imageObject.getBuffer().array(), imageObject.getSize());
	
	        if (targetFormat == ImageFormat.DICOMJPEG2000)
	        {
		        if (quality == ImageQuality.REFERENCE) 
		        {
		        	lossyCompressionRatio=
		        		imageLossyCompressionConfiguration.getModalityLossyJPEG2000Ratio(modality,
		        				imageObject.getSize(), xChangeUse);
		            codec.AwJ2kSetOutputJ2kRatio(lossyCompressionRatio);
		        }
		        else 
		        { // ImageQuality.DIAGNOSTIC
		            codec.AwJ2kSetOutputJ2kXform(AwJ2kParameters.AW_J2K_WV_TYPE_R53, autoLevel);
		            codec.AwJ2kSetOutputJ2kRatio(0); // lossless compression
		        }
		        codec.AwJ2kSetOutputType(AwJ2kParameters.AW_J2K_FORMAT_DCMJ2K);	
	        }
	        else 
	        {   // do regular JPEG
		        if (quality == ImageQuality.REFERENCE) 
		        {
		        	lossyJpgQuality=
		        		imageLossyCompressionConfiguration.getModalityLossyJPEGQuality(modality,
		        				imageObject.getSize(), xChangeUse);
		            codec.AwJ2kSetOutputJpegOptions(lossyJpgQuality);
		        }
		        else 
		        { // ImageQuality.DIAGNOSTIC
		            codec.AwJ2kSetOutputJpegOptions(-1); // lossless compression
		        }
		        codec.AwJ2kSetOutputType(AwJ2kParameters.AW_J2K_FORMAT_DCMJPG); // 0x0059	        	
	        }
	        AwOutputImageValue valueObj = codec.AwJ2kGetOutputImage();
	        // must copy out data from compressor buffer before leaving :-(
	        ByteBuffer output = ByteBuffer.allocate(valueObj.getImageLength());
	        output.put(valueObj.getImage());
	        return new ByteBufferBackedObject(output, valueObj.getImageLength());
	    }
	    catch (AwJ2kException ae) 
	    {
	        LOGGER.error("dicomCompress - Compression Exception: " + ae.getMessage());
	    	throw new ImageConversionCompressionException("dicomCompress - Compression Exception: ", ae);
	    }
		catch (AwJ2kCriticalException  ce) 
		{
	        LOGGER.error("dicomCompress - critical exception " + ce.getMessage());
			throw new ImageConversionCompressionException("dicomCompress - critical exception", ce);
		}
		catch (Exception e) 
		{
	        LOGGER.error("dicomCompress - generic exception " + e.getMessage());
			throw new ImageConversionCompressionException("dicomCompress - generic exception", e);
		}
		finally 
		{
			try
			{
				if (codec!=null)
					codec.AwJ2kDestroy();
			} 
			catch (AwJ2kException ex) 
			{
		        LOGGER.error("dicomCompress - AWJ2K Destroy Exception: " + ex.getMessage());
		        throw new ImageConversionCompressionException("dicomCompress - AWJ2K Destroy Exception: ", ex);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.conversion.interfaces.IImageConversion#Compress(gov.va.med.imaging.exchange.enums.ImageQuality, gov.va.med.imaging.exchange.enums.ImageFormat, gov.va.med.imaging.exchange.enums.ImageFormat, gov.va.med.imaging.SizedInputStream, gov.va.med.imaging.SizedInputStream, gov.va.med.imaging.exchange.conversion.ImageBuffers)
	 */
	private ByteBufferBackedImageInputStream Compress(ByteBufferBackedObject imageObject,
			ImageQuality quality, ImageFormat targetFormat, String modality, boolean xChangeUse) 
	throws ImageConversionIOException, ImageConversionCompressionException 
	{
		// Image compression (not thumbnail) of BMP, TGA, TIFF, JPEG, DICOM to J2K or JPEG
		// from buffer1 to compressor to buffer2 to tStream

		LOGGER.info("Compress Object(" + imageObject.getSize() + ") and stream it");
		int autoLevel=-1; // Automatically determines the best number of compression levels for J2K
		float lossyCompressionRatio;
		int lossyJpgQuality; // , bpp=0; // for downsizing bit depth
		AwJ2k codec = null;

	    try 
	    {
		    codec = new AwJ2k();
	        AwJ2k.AwJ2kEnableErrorProtectionMode(true);
	        codec.AwJ2kSetInputImage(imageObject.getBuffer().array(), imageObject.getSize());
	
	        if (targetFormat == ImageFormat.J2K)
	        {
		        if (quality == ImageQuality.REFERENCE) 
		        {
		        	lossyCompressionRatio=
		        		imageLossyCompressionConfiguration.getModalityLossyJPEG2000Ratio(modality,
		        				imageObject.getSize(), xChangeUse);
		            codec.AwJ2kSetOutputJ2kRatio(lossyCompressionRatio);
		        }
		        else 
		        { // ImageQuality.DIAGNOSTIC
		            codec.AwJ2kSetOutputJ2kXform(AwJ2kParameters.AW_J2K_WV_TYPE_R53, autoLevel);
		            codec.AwJ2kSetOutputJ2kRatio(0); // lossless compression
		        }
		        codec.AwJ2kSetOutputType(AwJ2kParameters.AW_J2K_FORMAT_J2K);	
	        }
	        else 
	        {   // do regular JPEG
		        if (quality == ImageQuality.REFERENCE) {
//                      // 02/18/09 - cpt - un-comment these 7 lines if bit depth reduction is desired!!!
//			        	bpp=codec.AwJ2kGetInputChannelBpp(0); 
//			        	if (bpp > 8) { // : render to 8 bit JPEG if source has more than 8 bits
//			        	//	codec.AwJ2kSetOutputComBitDepthScalingParameters(AwJ2kParameters.AW_J2K_SELECT_ALL_CHANNELS,
//			        	//			AwJ2kParameters.AW_J2K_BIT_DEPTH_SCALING_LINEAR_METHOD,
//			        	//			AwJ2kParameters.AW_J2K_BIT_DEPTH_SCALING_AUTO_CALC,
//			        	//			AwJ2kParameters.AW_J2K_BIT_DEPTH_SCALING_AUTO_CALC);
//			        		codec.AwJ2kSetOutputComBitDepth(AwJ2kParameters.AW_J2K_SELECT_ALL_CHANNELS, 8);
//			        	}
		        	lossyJpgQuality=
		        		imageLossyCompressionConfiguration.getModalityLossyJPEGQuality(modality,
		        				imageObject.getSize(), xChangeUse);
		            codec.AwJ2kSetOutputJpegOptions(lossyJpgQuality);
		        }
		        else 
		        { // ImageQuality.DIAGNOSTIC
		            codec.AwJ2kSetOutputJpegOptions(-1); // lossless compression
		        }
		        codec.AwJ2kSetOutputType(AwJ2kParameters.AW_J2K_FORMAT_JPG);	        	
	        }
	        AwOutputImageValue valueObj = codec.AwJ2kGetOutputImage();
	        // must copy out data from compressor buffer before leaving :-(
	        
	        ByteBuffer output = ByteBuffer.allocate(valueObj.getImageLength());
	        output.put(valueObj.getImage());
	        return new ByteBufferBackedImageInputStream(output, valueObj.getImageLength());
	    }
	    catch (AwJ2kException e) 
	    {
	        LOGGER.error("compress - Compression Exception: " + e.getMessage());
	    	throw new ImageConversionCompressionException("compress - Compression Exception: ", e);
	    }
		catch (AwJ2kCriticalException  ce) 
		{
	        LOGGER.error("compress - critical exception " + ce.getMessage());
			throw new ImageConversionCompressionException("compress - critical exception", ce);
		}
		catch (Exception e) 
		{
	        LOGGER.error("compress - generic exception " + e.getMessage());
			throw new ImageConversionCompressionException("compress - generic exception", e);
		}
		finally 
		{
			try
			{
				if (codec!=null)
					codec.AwJ2kDestroy();
			} 
			catch (AwJ2kException ex) 
			{
		        LOGGER.error("compress - AWJ2K Destroy Exception: " + ex.getMessage());
		        throw new ImageConversionCompressionException("compress - AWJ2K Destroy Exception: ", ex);
			}
		}
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.conversion.interfaces.IImageConversion#decompressDicomData(gov.va.med.imaging.SizedInputStream, gov.va.med.imaging.exchange.conversion.ImageBuffers)
	 */
	private ByteBufferBackedObject decompressDicomData(ByteBufferBackedObject dicomImageObject) 
	throws ImageConversionIOException, ImageConversionDecompressionException 
	{
		// decompress DCMJPG or DCMJ2K data in buffer1 to raw DICOM format (optional in future),
		// output to buffer2
        LOGGER.info("Decompress Dicom Stream");
	    AwJ2k codec = null;

		try 
		{
			codec = new AwJ2k();
	        AwJ2k.AwJ2kEnableErrorProtectionMode(true);	       

	        codec.AwJ2kSetInputImage(dicomImageObject.getBuffer().array(), 
	        		dicomImageObject.getSize());
	
	        codec.AwJ2kSetOutputType(AwJ2kParameters.AW_J2K_FORMAT_DCM);
	        AwOutputImageValue valueObj = codec.AwJ2kGetOutputImage();
	        
	        ByteBuffer output = ByteBuffer.allocate(valueObj.getImageLength());
	        output.put(valueObj.getImage());
	        return new ByteBufferBackedObject(output, valueObj.getImageLength());
	    }
	    catch (AwJ2kException e) {
	        LOGGER.error("decompressDicomStream - Decompression exception " + e.getMessage());
	    	throw new ImageConversionDecompressionException("decompressDicomStream - Decompression Exception: ", e);
	    }
		catch (AwJ2kCriticalException  ce) {
	        LOGGER.error("decompressDicomStream - critical exception " + ce.getMessage());
			throw new ImageConversionDecompressionException("decompressDicomStream - critical exception", ce);
		}
		catch (Exception e) {
	        LOGGER.error("decompressDicomStream - generic exception " + e.getMessage());
			throw new ImageConversionDecompressionException("decompressDicomStream - generic exception", e);
		}
		finally 
		{
			try
			{
				if (codec!=null)
					codec.AwJ2kDestroy();
			} 
			catch (AwJ2kException ex) 
			{
		        LOGGER.error("decompressDicomStream - AWJ2K Destroy Exception: " + ex.getMessage());
		        throw new ImageConversionDecompressionException("decompressDicomStream - AWJ2K Destroy Exception: ", ex);
			}
		}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.conversion.interfaces.IImageConversion#decompressStream(gov.va.med.imaging.SizedInputStream, gov.va.med.imaging.exchange.conversion.ImageBuffers)
	 */
	private ByteBufferBackedImageInputStream decompressStream(ImageFormat targetFormat, 
			ByteBufferBackedObject imageObject) 
	throws ImageConversionIOException, ImageConversionDecompressionException {
		// decompress J2K, JPEG objects to BMP, TIFF or TGA and DICOMJ2K and DICOMJPEG
		// objects to plain DICOM
		// using buffer1 for input and buffer2 for output
        LOGGER.info("DecompressStream");
	    AwJ2k codec = null;

	    try 
	    {
			codec = new AwJ2k();
	        AwJ2k.AwJ2kEnableErrorProtectionMode(true);

	        codec.AwJ2kSetInputImage(imageObject.getBuffer().array(), imageObject.getSize());

	        if (targetFormat==ImageFormat.DICOM) 
	        {
	        	codec.AwJ2kSetOutputType(AwJ2kParameters.AW_J2K_FORMAT_DCM);
	        } 
	        else if (targetFormat==ImageFormat.BMP) 
	        {
	        	codec.AwJ2kSetOutputType(AwJ2kParameters.AW_J2K_FORMAT_BMP);
	        } 
	        else if (targetFormat==ImageFormat.TIFF) 
	        {
	        	codec.AwJ2kSetOutputType(AwJ2kParameters.AW_J2K_FORMAT_TIF);
	        } 
	        else if (targetFormat==ImageFormat.TGA) 
	        {
	        	codec.AwJ2kSetOutputType(AwJ2kParameters.AW_J2K_FORMAT_TGA);
	        } 
	        else 
	        {
		        codec.AwJ2kDestroy();
		        LOGGER.error("decompressStream - invalid target format: " + targetFormat);
	        	throw new ImageConversionIOException("decompressStream - invalid target format: " + targetFormat);
	        }
	        AwOutputImageValue valueObj = codec.AwJ2kGetOutputImage();
	        
	        ByteBuffer output = ByteBuffer.allocate(valueObj.getImageLength());
	        output.put(valueObj.getImage(), 0, valueObj.getImageLength());
	        
	        return new ByteBufferBackedImageInputStream(output, valueObj.getImageLength());
	    }
	    catch (AwJ2kException e) 
	    {
	        LOGGER.error("decompressStream - Decompression Exception: ", e);
	        throw new ImageConversionDecompressionException("decompressStream - Decompression Exception: ", e);
	    }
		catch (AwJ2kCriticalException  ce) 
		{
	        LOGGER.error("decompressStream - critical exception " + ce.getMessage());
			throw new ImageConversionDecompressionException("decompressStream - critical exception", ce);
		}
		catch (Exception e) 
		{
	        LOGGER.error("decompressStream - generic exception " + e.getMessage());
			throw new ImageConversionDecompressionException("decompressStream - generic exception", e);
		}
		finally 
		{
			try
			{
				if (codec!=null)
					codec.AwJ2kDestroy();
			} 
			catch (AwJ2kException ex) 
			{
		        LOGGER.error("decompressStream - AWJ2K Destroy Exception: " + ex.getMessage());
		        throw new ImageConversionDecompressionException("decompressStream - AWJ2K Destroy Exception: ", ex);
			}
		}
	}

	// ===================================== Private Utility methods ==================================================
	
	
	// ============================== Private DICOM operation related methods =========================================
	
	private ByteBufferBackedObject sanitizeDicomData(ByteBufferBackedObject imageObject)
	throws ImageConversionIOException 
	{
		// look for offending elements (icon image, etc.) in DICOM data 
		// and remove them if necessary -- input from buffer2, output to tS
		
		// ************ start FAKE **********
		// let it through without sanity check
        LOGGER.info("No DICOM data sanitization: write to targetStream");
        return imageObject;
		// ************ end FAKE **********
	}

	private ByteBufferBackedObject updateDicomObject(ByteBufferBackedObject imageStream, 
			ByteBufferBackedObject textStream)
	throws ImageConversionIOException 
	{
		//	Reads sourceStream1, updates Header (direct call to ImagingDicomUtilities),
		//  finally funnels result DataSet to buffer1

		LOGGER.info("Update Dicom Stream (" + imageStream.getSize() + ") with HIS/TXT changes (" + textStream.getSize() + ")");
		byte[] data=null;

		try 
		{
// ************ FAKE **********
// ignore HasMap, let it through from stream to buffer1
//	    	setBuffer1(sourceStream.available());
//	        int sizeRead = sourceStream.read(getBuffer1Array(), 0, buf1Size());
//	        if (sizeRead != buf1Size())
//	        	throw new ImageConversionIOException("updateDicomObject - Error1 Reading Input Stream");
//	    }
//	    catch (IOException ioe) {
//        	throw new ImageConversionIOException("updateDicomObject - Error2 Reading Input Stream", ioe);
//	    }
// ************ FAKE **********
			
			SizedInputStream sizedDicomStream = new SizedInputStream(imageStream.openInputStreamToBuffer(), 
					imageStream.getSize());
			SizedInputStream sizedTextStream = new SizedInputStream(textStream.openInputStreamToBuffer(), 
					textStream.getSize());

			data = dicomUtilities.updateDicomStream(sizedDicomStream, sizedTextStream, false);
			
			ByteBuffer output = ByteBuffer.allocate(data.length);
			output.put(data);
			
			return new ByteBufferBackedObject(output, data.length);
		}
		catch (GenericDicomReconstitutionException gdre) 
		{
	        LOGGER.error("updateDicomObject - Error updating HIS data in DICOM Stream: " + gdre.getMessage());
			throw new ImageConversionIOException("updateDicomObject - Error updating HIS data in DICOM Stream", gdre);
		}
		catch (Exception e) 
		{
	        LOGGER.error("updateDicomObject - generic exception " + e.getMessage());
			throw new ImageConversionIOException("updateDicomObject - generic exception", e);
		}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.conversion.interfaces.IImageConversion#createDicomObject(gov.va.med.imaging.SizedInputStream, gov.va.med.imaging.SizedInputStream, gov.va.med.imaging.exchange.conversion.ImageBuffers)
	 */
	private ByteBufferBackedObject createDicomObject(ByteBufferBackedObject tgaStream, 
			ByteBufferBackedObject txtStream)
	throws ImageConversionIOException 
	{
		//	Reads sourceStream1&2 (TXT and TGA data), creates Dicom object (direct call to ImagingDicomUtilities),
		//  finally funnels result DataSet to buffer1

        LOGGER.info("Assemble Dicom Object Stream from Text (" + tgaStream.getSize() + 
        			") and TGA (" + txtStream.getSize() + ") streams");
		byte[] data=null;

		try 
		{
// ************ FAKE **********
//			// ignore input, read in a DCM file to buffer1 
//			// ClassPathResource resource = new ClassPathResource("unitTestDCM.dcm");
//			// String targetImage = resource.getPath(); // "c:\\ImageCache\\unitTestDCM.dcm";
//			
//			String imageCache = appConfiguration.getImageCacheUri().toString();
//			String sourceImage = imageCache + "\\" + "unitTestDCM.dcm";
//	
//			byte[] data = readImageFile(sourceImage);
// ************ FAKE **********
			
			SizedInputStream sizedtgaStream = new SizedInputStream(tgaStream.openInputStreamToBuffer(), tgaStream.getSize());
			SizedInputStream sizedTextStream = new SizedInputStream(txtStream.openInputStreamToBuffer(), txtStream.getSize());			
			
			data = dicomUtilities.assembleDicomStream(sizedTextStream, sizedtgaStream, false);
			
			ByteBuffer output = ByteBuffer.allocate(data.length);
			output.put(data);
			return new ByteBufferBackedObject(output, data.length);
		}
		catch (GenericDicomReconstitutionException gdre) 
		{
	        LOGGER.error("createDicomObject - Error creating DICOM Output Stream: " + gdre.getMessage());
			throw new ImageConversionIOException("createDicomObject - Error creating DICOM Output Stream: ", gdre);
		}
		catch (Exception e) 
		{
	        LOGGER.error("createDicomObject - generic exception " + e.getMessage());
			throw new ImageConversionIOException("createDicomObject - generic exception", e);
		}
	}
	// ============================== compression support methods =====================================
	// 
	/**
	 * This method parses a DICOM file for the 2 byte DICOM Modality code.
	 * @param imageBuffer
	 * @return returns the 2 byte string value of the DICOM tag (0008,0060) if tag found in first 1000 bytes of imageBuffer
	 */
	private String getDicomModality(ByteBuffer imageBuffer) 
	{    	
    	String mty="xy";
    	imageBuffer.rewind();
    	short short1=0, short2;
		for (int i=0; ((i < imageBuffer.capacity()-6) && (i < 1000));) {
			short2=imageBuffer.getShort();
			i+=2;
			if ((short1==0x0800) && (short2==0x6000)) {
				imageBuffer.getInt(); // skip 4 bytes
				short1=imageBuffer.getShort();
				mty=mty.replace('x', (char)(((short1 >> 8) & 0xFF)));
				mty=mty.replace('y', (char)((0xFF & short1)));
		    	imageBuffer.rewind();
				return mty;
			}
			short1=short2;
		}
    	imageBuffer.rewind();
    	return mty;   	
    }	
}
