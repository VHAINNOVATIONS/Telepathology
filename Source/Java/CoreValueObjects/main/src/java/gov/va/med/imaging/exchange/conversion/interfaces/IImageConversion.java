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

package gov.va.med.imaging.exchange.conversion.interfaces;

import gov.va.med.imaging.exchange.conversion.exceptions.ImageConversionCompressionException;
import gov.va.med.imaging.exchange.conversion.exceptions.ImageConversionDecompressionException;
import gov.va.med.imaging.exchange.conversion.exceptions.ImageConversionIOException;
import gov.va.med.imaging.exchange.conversion.exceptions.ImageConversionInvalidInputException;
import gov.va.med.imaging.exchange.enums.ImageFormat;
import gov.va.med.imaging.exchange.enums.ImageQuality;
import gov.va.med.imaging.exchange.storage.ByteBufferBackedImageInputStream;
import gov.va.med.imaging.exchange.storage.ByteBufferBackedObject;

/**
 * 
 * @author VHAISWTITTOC
 *
 */
public interface IImageConversion 
{
	
	/**
	 * ConvertImage is bidirectional. VA Input thumbnails can be TGA, TIFF or JPEG,
	 * the DoD thumbnails are JPEG, final thumbnails are always in JPEG (lossy).
	 * VA input images are TGA+TXT or DICOM, DoD input images as well as all output
	 * images are DICOM (JPEG/J2K). DoD delivered images are always in DICOM format
	 * 
	 * @param sourceImageFormat
	 * 			DOWNSAMPLEDTGA, TGA, TIFF, JPEG, DICOMJPEG or DICOMJPEG2000
	 * @param targetImageFormat
	 * 			DOWNSAMPLEDTGA, TGA, DICOMJPEG or DICOMJPEG2000
	 * @param ImageQuality
	 * 			DIAGNOSTIC, REFERENCE, THUMBNAIL
	 * @param imageObject
	 * 			SizedInputStream of image (JPEG, TGA or DICOM), with bytesize
	 * @param txtObject
	 * 			Optional SizedInputStream of TXT file or null, with bytesize
	 * @return SizedInputStream
	 * 			Stream of image (JPEG, DICOM, maybe TGA), with bytesize
	 * @throws ImageConversionInvalidInputException	on invalid input parameters
	 * @throws ImageConversionIOException
	 * @throws ImageConversionCompressionException
	 * @throws ImageConversionDecompressionException
	 */
	public abstract ByteBufferBackedImageInputStream ConvertImage(ImageFormat sourceImageFormat,
									  ImageFormat targetImageFormat,
									  ImageQuality Quality,
									  ByteBufferBackedObject imageObject,
									  ByteBufferBackedObject txtObject)
	throws ImageConversionInvalidInputException, ImageConversionIOException,
		   ImageConversionCompressionException, ImageConversionDecompressionException;

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
	 * 			2 byte DICOM Modality code; needed for non-DICOM input if possible
	 * @return SizedInputStream
	 * 			Stream of image (JPEG, J2K, DICOMJPEG, DICOMJ2K), with bytesize
	 * @throws ImageConversionInvalidInputException	on invalid input parameters
	 * @throws ImageConversionCompressionException
	 */
	public abstract ByteBufferBackedImageInputStream CompressImage(
									  ImageQuality quality,
									  ImageFormat sourceImageFormat,
									  ImageFormat targetImageFormat,
									  ByteBufferBackedObject imageObject,
									  String modality)
	throws ImageConversionInvalidInputException, ImageConversionIOException,
	ImageConversionCompressionException;

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
	 * 			J2K, JPEG, DICOMJ2K and DICOMJPEG
	 * @param imageObject
	 * 			SizedInputStream of input image, with bytesize
	 * @return SizedInputStream
	 * 			Stream of image (TIFF, BMP, DICOM, maybe TGA), with bytesize
	 * @throws ImageConversionInvalidInputException	on invalid input parameters
	 * @throws ImageConversionDecompressionException
	 */
	public abstract ByteBufferBackedImageInputStream DecompressImage(
			  						  ImageFormat sourceFormat,
			  						  ImageFormat targetFormat,
			  						ByteBufferBackedObject imageObject)
	throws ImageConversionInvalidInputException, ImageConversionIOException,
	ImageConversionDecompressionException;
	
	/**
	 * UpdateVAImage returns a DICOM stream that is updated by VA TXT input stream. It does 
	 * not apply compression. VA Referential or Diagnostic quality Input is assumed (TGA+TXT
	 * or DICOM+TXT), the same quality is returned.
	 * 
	 * @param sourceImageFormat
	 * 			DOWNSAMPLEDTGA, TGA, DICOM
	 * @param sourceStream1
	 * 			SizedInputStream of image (TGA or DICOM), with bytesize
	 * @param sourceStream2
	 * 			SizedInputStream of TXT file, with bytesize
	 * @return SizedInputStream
	 * 			Stream of raw DICOM image, with bytesize
	 * @throws ImageConversionInvalidInputException	on invalid input parameters
	 * @throws ImageConversionIOException
	 */
	public ByteBufferBackedImageInputStream UpdateVAImage(
			ImageFormat sourceImageFormat,			// never null as input
			ByteBufferBackedObject imageObject,	// never null as input
			ByteBufferBackedObject txtObject)	// never null as input
	throws ImageConversionInvalidInputException, ImageConversionIOException;

}
