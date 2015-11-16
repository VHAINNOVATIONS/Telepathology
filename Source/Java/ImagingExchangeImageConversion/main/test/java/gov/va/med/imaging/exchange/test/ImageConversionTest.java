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

package gov.va.med.imaging.exchange.test;

// import junit.framework.TestCase;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import gov.va.med.imaging.channels.ByteStreamPump;
import gov.va.med.imaging.exchange.conversion.exceptions.*;
import gov.va.med.imaging.exchange.conversion.interfaces.IImageConversion;
import gov.va.med.imaging.exchange.enums.ImageFormat;
import gov.va.med.imaging.exchange.enums.ImageQuality;
import gov.va.med.imaging.exchange.storage.ByteBufferBackedImageInputStream;
import gov.va.med.imaging.exchange.storage.ByteBufferBackedInputStream;

import org.springframework.core.io.ClassPathResource;

public class ImageConversionTest extends ImagingExchangeImageConversionTestBase {
	
	private IImageConversion imageConversion;

	private static String ViXTestDataFolder = "c:/tmp/TestData"; // needed only on build systems

	public ImageConversionTest() {
		super();
	}

	public ImageConversionTest(String name) {
		super(name);
	}

	protected void setUp() {
		imageConversion = (IImageConversion) springFactory
				.getBean("imageConversion");
		//ImageConversion imageConversion = new ImageConversion(appConfiguration);
		//imageConversion(appConfiguration);
		// imageCacheUri = appConfiguration.getCacheUri().toString() + ViXTestDataFolder;
		File cacheDir = new File(ViXTestDataFolder.toString());
		if (! cacheDir.exists())
		{
			cacheDir.mkdirs();
		}
	}

	public void testConvertThumbnailImages() {
		System.out.println("Starting Thumbnail Image Conversion Tests:");

		// convert VA TGA .abs file to .jpg file.
		ConvertThumbnailFile("  Convert VA TGA .abs file to .jpg ...",
				ImageQuality.THUMBNAIL, ImageFormat.JPEG, 
				"unitTestTGA.abs", "unitTestThumbnail1.jpg");

		// convert VA BMP .abs file to .jpg file.
		ConvertThumbnailFile("  Convert VA BMP .abs file to .jpg ...",
				ImageQuality.THUMBNAIL, ImageFormat.JPEG, 
				"unitTestBMP.abs", "unitTestThumbnail2.jpg");

		// convert VA TIFF .abs file to .jpg file.
		ConvertThumbnailFile("  Convert VA TIFF .abs file to .jpg ...",
				ImageQuality.THUMBNAIL, ImageFormat.JPEG, 
				"unitTestTIFF.abs", "unitTestThumbnail3.jpg");

		// convert DoD .jpg file to JPEG .abs file.
		ConvertThumbnailFile("  Convert DoD JPEG file to VA .jpg ...",
				ImageQuality.THUMBNAIL, ImageFormat.JPEG, 
				"DoDTN.jpg", "unitTestThumbnail4.jpg");

		ConvertThumbnailFile("  Convert VA TGA .abs file to .jpg ...",
				ImageQuality.THUMBNAIL, ImageFormat.JPEG, 
				"DM002745.ABS", "unitTestThumbnail5.jpg");

//		ConvertThumbnailFile("  Convert VA TGA .abs file to .jpg ...",
//		ImageQuality.THUMBNAIL, ImageFormat.JPEG, 
//		"DM002747.ABS", "unitTestThumbnail6.jpg");

		ConvertThumbnailFile("  Convert VA .abs file to .jpg ...",
				ImageQuality.THUMBNAIL, ImageFormat.JPEG, 
				"DM004764.ABS", "unitTestThumbnail7.jpg");
		
		ConvertThumbnailFile("  Convert VA .abs file to .jpg ...",
				ImageQuality.THUMBNAIL, ImageFormat.JPEG, 
				"DM000792.ABS", "unitTestThumbnail8.jpg");
		
		System.out.println("Thumbnail Image Conversion Tests DONE.");
	}

	public void ConvertThumbnailFile(String message, ImageQuality quality, ImageFormat format,
								     String inFileName, String outFileName) {
	// good for Thumbnail conversions and single DCM image file conversion to Diag./Ref. DCM.JPG
		System.out.println(message + " started...");
		// String imageCache = appConfiguration.getCacheUri().toString() + ViXTestDataFolder;
		// TestCase.assertEquals("C:/ImageCache", appConfiguration.getImageCacheUri().toString());
		String targetImage = ViXTestDataFolder + "\\" + outFileName;
		ClassPathResource res = new ClassPathResource(inFileName); // this needs to be in main/test/resources
		File sourceFile1 = null;
		// File sourceFile2 = null;
		File targetFile1 = null;
		// File targetFile2 = null;
		FileInputStream sourceFileStream = null;
		FileOutputStream targetFileStream = null;
		int bytesOut=0;

		try {
			sourceFile1 = res.getFile(); 
			// TODO: this throws an exception until the referenced file is created
			//       and added as a resource to the project

			targetFile1 = new File(targetImage);
			if (targetFile1.exists()) {
				targetFile1.delete();
			}
			targetFile1.createNewFile();
			
			sourceFileStream = new FileInputStream(sourceFile1);
			targetFileStream = new FileOutputStream(targetFile1);
						
			ByteBufferBackedImageInputStream sourceStream = new ByteBufferBackedImageInputStream(sourceFileStream, sourceFileStream.available());
			
			ByteBufferBackedImageInputStream targetStream=imageConversion.ConvertImage(ImageFormat.TGA, format,
					quality, sourceStream.toBufferedObject(), null);
			
			if ((!targetStream.isReadable()) || (targetStream.getSize()<=0)) {
				assertTrue(false);
			}
			// funnel in stream to out stream
			ByteStreamPump bSP = ByteStreamPump.getByteStreamPump(ByteStreamPump.TRANSFER_TYPE.ByteArrayToFile);
			bytesOut = bSP.xfer(targetStream.getInputStream(), targetFileStream);
			// targetFileStream.flush();
			FileDescriptor fd = targetFileStream.getFD();
			fd.sync();
			targetFileStream.close();
			targetFileStream = null;
			
			// TODO: see if the thumbnail image was created correctly
			sourceStream.closeSafely();
			if (targetFileStream != null)
				targetFileStream.close();
			System.out.println(message + "[" + bytesOut + " bytes] --> " + targetImage + ((bytesOut>0)?" - SUCCESS":" - F a i l e d !!"));
		} 
		catch (IOException ex) {
			ex.printStackTrace();
		} 
		catch (ImageConversionInvalidInputException iciie) {
			iciie.printStackTrace();
		} 
		catch (ImageConversionIOException icioe) {
			icioe.printStackTrace();
		} 
		catch (ImageConversionCompressionException icce) {
			icce.printStackTrace();
		} 
		catch (ImageConversionDecompressionException icdce) {
			icdce.printStackTrace();
		}
		finally {
			assertTrue(bytesOut!=0);			
		}	
	}

	public void testConvertVADiagnosticImage() {
		System.out.println("Starting VA Diagnostic Image Conversion Tests:");
		// convert VA .dcm file to .dcmj2k (lossless) file, with HIS update
		ConvertVAImage("  Convert VA .dcm (and TXT) file to .dcmj2k (LL), with HIS update",
				ImageQuality.DIAGNOSTIC, ImageFormat.DICOM, ImageFormat.DICOMJPEG2000, 
				"unitTestDCM.dcm", "unitTestText.txt", "unitTestDIAG1.dcm.j2k");		
//		ConvertImageFile("  Convert VA .dcm file to .dcmj2k (LL), with HIS update",
//		ImageQuality.DIAGNOSTIC, ImageFormat.DICOM, ImageFormat.DICOMJPEG2000, 
//		"DM005320.DCM", "unitTestText.txt", "unitTestDIAG1x.dcm.j2k");		
		// test uncompressed
		ConvertVAImage("  Convert VA .tga and .txt files to .dcm raw (uncompressed), with HIS update",
				ImageQuality.DIAGNOSTICUNCOMPRESSED, ImageFormat.TGA, ImageFormat.DICOM,
				"unitTestTGA.big", "unitTestText.txt", "unitTestDIAG2raw.dcm");

		ConvertVAImage("  Convert VA .tga and .txt files to .dcm raw (uncompressed), with HIS update",
				ImageQuality.DIAGNOSTICUNCOMPRESSED, ImageFormat.TGA, ImageFormat.DICOM,
				"NCH00005617188.BIG", "NCH00005617188.txt", "unitTestNCHraw.dcm");

		ConvertVAImage("  Convert VA .tga and .txt files to .dcm raw (uncompressed), with HIS update",
				ImageQuality.DIAGNOSTIC, ImageFormat.TGA, ImageFormat.DICOM,
				"NCH00005617188.BIG", "NCH00005617188.txt", "unitTestNCHraw2.dcm");

		System.out.println("VA Diagnostic Image Conversion Tests DONE.");
	}

	public void testConvertVAReferenceImage() {
		System.out.println("Starting VA Reference Image Conversion Tests:");
		// convert VA .txt & .tga files to .dcmjpg (lossy) file, with HIS update
//		hm.put("0010,0010","IMAGPATIENT1007,1007"); // new Patient Name
		ConvertVAImage("  Convert VA .tga and .txt files to .dcmjpg (Lossy), with HIS update",
				ImageQuality.REFERENCE, ImageFormat.TGA, ImageFormat.DICOMJPEG,
				"unitTestTGA.big", "unitTestText.txt", "unitTestREF2.dcm.jpg");
		// Test non DICOM compliant ACR-NEMA 2.0 conversion too
		ConvertVAImage("  Convert VA .tga and .txt files to .dcmj2k (Lossy), without HIS update",
				ImageQuality.REFERENCE, ImageFormat.TGA, ImageFormat.DICOMJPEG2000,
				"EP026886.TGA", "EP026886.TXT", "unitTestREF2x.dcm.j2k");		
//				"DM005669.TGA", "DM005669.TXT", "unitTestREF2x.dcm.j2k");		
//				"W2061541ok.TGA", "W2061541.TXT", "unitTestWDC-NMRef.dcm.j2k");		
//			"MC000000007484.big", "MC000000007484.TXT", "unitTest-CRRef.dcm.j2k");		
		ConvertVAImage("  Convert VA .tga and .txt files to .dcmj2k (lossy), without HIS update",
		ImageQuality.REFERENCE, ImageFormat.TGA, ImageFormat.DICOMJPEG2000,
		"TAM00003254799.TGA", "TAM00003254799.txt", "unitTestRef2y.dcm.j2k");		
//		"TAM00121016210.TGA", "TAM00121016210.txt", "unitTestRef2y.dcm.j2k");		
//		ConvertVAImage("  Convert VA DCMJPG and .txt files to .dcmj2k (Lossy), with HIS update",
//		ImageQuality.REFERENCE, ImageFormat.DICOMJPEG, ImageFormat.DICOMJPEG2000,
//		"DM000788.DCM", "DM000788.TXT", "unitTestRef2x.dcm.j2k");		
		System.out.println("VA Reference Image Conversion Tests DONE.");
	}

	public void ConvertVAImage(String message, ImageQuality quality, ImageFormat inFormat,
			ImageFormat outFormat, String inFileName1, String inFileName2, String outFileName) 
	{
		String diagnosticMessage = message + " from '" + inFileName1 + "', '" + inFileName2 + "' to '" + outFileName + "'.";
		
		// convert VA Diag/Ref. .dcm or .tga/.big + .txt files with HIS update to ll/lossy .dcmj2k/.dcmjpg file
		System.out.println(message + " started...");
		// String imageCache = appConfiguration.getCacheUri().toString() + ViXTestDataFolder;
		String targetImage = ViXTestDataFolder + "\\" + outFileName;
		ClassPathResource res1 = new ClassPathResource(inFileName1); // this needs to be in main/test/resources
		ClassPathResource res2 = new ClassPathResource(inFileName2); // this needs to be in main/test/resources
		File sourceFile1 = null;
		File sourceFile2 = null;
		File targetFile = null;
		FileInputStream sourceFileStream1 = null;
		FileInputStream sourceFileStream2 = null;
		FileOutputStream targetFileStream = null;
		int bytesOut=0;

		try {
			sourceFile1 = res1.getFile();
			sourceFile2 = res2.getFile();
			// TODO: these throw an exception until the referenced file is created
			//       and added as a resource to the project

			targetFile = new File(targetImage);
			if (targetFile.exists()) {
				targetFile.delete();
			}
			targetFile.createNewFile();
			
			sourceFileStream1 = new FileInputStream(sourceFile1);
			sourceFileStream2 = new FileInputStream(sourceFile2);
			targetFileStream = new FileOutputStream(targetFile);

			ByteBufferBackedImageInputStream sourceStream1 = new ByteBufferBackedImageInputStream(sourceFileStream1, sourceFileStream1.available());
			ByteBufferBackedInputStream sourceStream2 = new ByteBufferBackedInputStream(sourceFileStream2, sourceFileStream2.available());

			ByteBufferBackedImageInputStream targetStream=imageConversion.ConvertImage(inFormat, outFormat,
					quality, sourceStream1.toBufferedObject(), sourceStream2.toBufferedObject());

			if ((!targetStream.isReadable()) || (targetStream.getSize()<=0)) {
				fail(message + " [targetStream getInStream() returned null or getByteSize() returned non-positive integer.");
			}
			
			// funnel in stream to out stream
			ByteStreamPump bSP = ByteStreamPump.getByteStreamPump(ByteStreamPump.TRANSFER_TYPE.ByteArrayToFile);
			bytesOut = bSP.xfer(targetStream.getInputStream(), targetFileStream);
			// targetFileStream.flush();
			FileDescriptor fd = targetFileStream.getFD();
			fd.sync();
			targetFileStream.close();
			targetFileStream = null;
			
			sourceStream1.closeSafely();
			sourceStream2.closeSafely();
			if (targetFileStream != null)
				targetFileStream.close();
			System.out.println(message + "[" + bytesOut + " bytes] --> " + targetImage + ((bytesOut>0)?" - SUCCESS":" - F a i l e d !!"));
		} 
		catch (IOException ex) {
			ex.printStackTrace();
			fail(ex.getMessage() + diagnosticMessage);
			//assertNotNull(1);
		} 
		catch (ImageConversionInvalidInputException iciie) {
			iciie.printStackTrace();
			fail(iciie.getMessage() + diagnosticMessage);
			//assertNotNull(1);
		} 
		catch (ImageConversionIOException icioe) {
			icioe.printStackTrace();
			fail(icioe.getMessage() + diagnosticMessage);
			//assertNotNull(1);
			//icioe.printStackTrace();
		} 
		catch (ImageConversionCompressionException icce) {
			icce.printStackTrace();
			fail(icce.getMessage() + diagnosticMessage);
			//assertNotNull(1);
			//icce.printStackTrace();
		} 
		catch (ImageConversionDecompressionException icdce) {
			icdce.printStackTrace();
			fail(icdce.getMessage() + diagnosticMessage);
			//assertNotNull(1);
			//icdce.printStackTrace();
		} 
		catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage() + diagnosticMessage);
		}
		finally {
			assertTrue("Error occured (bytesOut == 0): " + diagnosticMessage, bytesOut!=0);			
		}	
	}

	public void testConvertDoDReferenceImage() {
		System.out.println("Starting DoD Reference Image Conversion Tests:");
		// convert DoD .dcmj2k (lossy) file to VA .dcm [& .txt] files
		ConvertDoDImage("  Convert DoD .dcmj2k (lossy) file to VA .dcm", ImageQuality.REFERENCE,
				"unitTestL10.dcm.j2k", "unitTestREF3.dcm", null); // "unitTestREF3.txt");
// test some DCMJPEG output
//		ConvertDoDImage("  Convert DCM files to .dcmjpg (Lossy)", ImageQuality.REFERENCE,
//				"MAG_EyeLens.dcm", "unitTestREF-color-jpg.dcm", null);
//		ConvertDoDImage("  Convert DCM files to .dcmjpg (Lossy)", ImageQuality.REFERENCE,
//				"ZELP0154907671.DCM", "unitTestREF-8bit-jpg.dcm", null);
//		ConvertDoDImage("  Convert DCM files to .dcmjpg (Lossy)", ImageQuality.REFERENCE,
//				"DM000779.dcm", "unitTestREF-8bit-MF-jpg.dcm", null);
		System.out.println("DoD Reference Image Conversion Tests DONE.");
	}

	public void testConvertDoDDiagnosticImage() {
		System.out.println("Starting DoD Diagnostic Image Conversion Tests:");
		// convert DoD .dcmj2k (lossless) file to VA .dcm file
		ConvertDoDImage("  Convert DoD .dcmj2k (lossless) file to VA .dcm", ImageQuality.DIAGNOSTIC,
				"unitTestLL.dcm.j2k", "unitTestDiag4.dcm", "unitTestDIAG4.txt");
		ConvertDoDImage("  Convert DoD .dcmj2k (lossless) file to VA .dcm", ImageQuality.DIAGNOSTIC,
				"200-CT2-90.dcm", "CPSDiag.dcm", null); // "CPSDIAG.txt");
		System.out.println("DoD Diagnostic Image Conversion Tests DONE.");
	}

	public void ConvertDoDImage(String message, ImageQuality quality, String inFileName, 
			String outFileName1, String outFileName2) {
		// convert DoD Diag/Ref. quality (ll/lossy) .dcmj2k/.dcmjpg file to VA .dcm file
		System.out.println(message + " started...");
		// String imageCache = appConfiguration.getCacheUri().toString() + ViXTestDataFolder;
		String targetImage = ViXTestDataFolder + "\\" + outFileName1;
//		String targetText = ViXTestDataFolder + "\\" + outFileName2;
		ClassPathResource res = new ClassPathResource(inFileName); // this needs to be in main/test/resources
		File sourceFile1 = null;
		// File sourceFile2 = null;
		File targetFile = null;
		FileInputStream sourceFileStream = null;
		FileOutputStream targetFileStream = null;
		int bytesOut=0;
		
		try {
			sourceFile1 = res.getFile();
			// TODO: this throws an exception until the referenced file is created
			//       and added as a resource to the project
			
			targetFile = new File(targetImage);
			if (targetFile.exists()) {
				targetFile.delete();
			}
			targetFile.createNewFile();

			sourceFileStream = new FileInputStream(sourceFile1);
			targetFileStream = new FileOutputStream(targetFile);

			ByteBufferBackedImageInputStream sourceStream = new ByteBufferBackedImageInputStream(sourceFileStream, sourceFileStream.available());

//			SizedInputStream targetStream=imageConversion.ConvertImage(ImageFormat.DICOMJPEG2000, ImageFormat.DICOMJPEG,
			ByteBufferBackedImageInputStream targetStream=imageConversion.ConvertImage(ImageFormat.DICOMJPEG2000, 
					ImageFormat.DICOM, quality, sourceStream.toBufferedObject(), null);
			if ((!targetStream.isReadable()) || (targetStream.getSize()<=0)) {
				assertTrue(false);
			}
			// funnel in stream to out stream
			ByteStreamPump bSP = ByteStreamPump.getByteStreamPump(ByteStreamPump.TRANSFER_TYPE.ByteArrayToFile);
			bytesOut = bSP.xfer(targetStream.getInputStream(), targetFileStream);
			// targetFileStream.flush();
			FileDescriptor fd1 = targetFileStream.getFD();
			fd1.sync();
			targetFileStream.close();
			targetFileStream = null;
						
			// TODO: see if the thumbnail image was created correctly
			sourceFileStream.close();
			if (targetFileStream != null)
				targetFileStream.close();
			System.out.println(message + "[" + bytesOut + " bytes] --> " + targetImage + ((bytesOut>0)?" - SUCCESS":" - F a i l e d !!"));
		} 
		catch (IOException ex) {
			ex.printStackTrace();
		} 
		catch (ImageConversionInvalidInputException iciie) {
			iciie.printStackTrace();
		} 
		catch (ImageConversionIOException icioe) {
			icioe.printStackTrace();
		} 
		catch (ImageConversionCompressionException icce) {
			icce.printStackTrace();
		} 
		catch (ImageConversionDecompressionException icdce) {
			icdce.printStackTrace();
		}
		finally {
			assertTrue(bytesOut!=0);			
		}	
	}
	
	public void testCompressImage() {
		System.out.println("Starting Compression Tests:");
		// compress various VA image files
		CompressFile("  Compress VA .bmp file to VA lossy .j2k", ImageQuality.REFERENCE,
				ImageFormat.BMP, ImageFormat.J2K, "unitTestBMP.abs", "XC", "unitTestBMPCompL10.j2k");
		CompressFile("  Compress VA .bmp file to VA lossless .j2k", ImageQuality.DIAGNOSTIC,
				ImageFormat.BMP, ImageFormat.J2K, "unitTestBMP.abs", "XC", "unitTestBMPCompLL.j2k");
		CompressFile("  Compress VA .TGA file to VA lossy .j2k", ImageQuality.REFERENCE,
				ImageFormat.TGA, ImageFormat.J2K, "unitTestTGA.tga", "IO", "unitTestTGACompL10.j2k");
		CompressFile("  Compress VA .TGA file to VA lossy .j2k", ImageQuality.REFERENCE,
				ImageFormat.TGA, ImageFormat.J2K, "MC000000007484.big", "CR", "unitTestCompL25.j2k");
		CompressFile("  Compress VA .TGA file to VA lossless .j2k", ImageQuality.DIAGNOSTIC,
				ImageFormat.TGA, ImageFormat.J2K, "unitTestTGA.big", "CR", "unitTestTGACompLL.j2k");
		CompressFile("  Compress VA .TIF file to VA lossy .j2k", ImageQuality.REFERENCE,
				ImageFormat.TIFF, ImageFormat.J2K, "unitTestTIFF.abs", "OT", "unitTestTIFFCompL10.j2k");
		CompressFile("  Compress VA .TIF file to VA lossless .j2k", ImageQuality.DIAGNOSTIC,
				ImageFormat.TIFF, ImageFormat.J2K, "unitTestTIFF.abs", "OT", "unitTestTIFFCompLL.j2k");
		CompressFile("  Compress VA .JPG file to VA lossy .j2k", ImageQuality.REFERENCE,
				ImageFormat.JPEG, ImageFormat.J2K, "unitTestJPEG.jpg", "XC", "unitTestJPEGCompL10.j2k");
		CompressFile("  Compress VA .JPG file to VA lossless .j2k", ImageQuality.DIAGNOSTIC,
				ImageFormat.JPEG, ImageFormat.J2K, "unitTestJPEG.jpg", "XC", "unitTestJPEGCompLL.j2k");
		CompressFile("  Compress VA .DCM file to VA lossy .dcmj2k", ImageQuality.REFERENCE,
				ImageFormat.DICOM, ImageFormat.J2K, "unitTestDCM.dcm", "", "unitTestDCMCompL10.j2k");
		CompressFile("  Compress VA .DCM file to VA lossless .dcmj2k", ImageQuality.DIAGNOSTIC,
				ImageFormat.DICOM, ImageFormat.J2K, "unitTestDCM.dcm", "", "unitTestDCMCompLL.j2k");
		CompressFile("  Compress VA .DCM file to VA lossy .j2k", ImageQuality.REFERENCE,
				ImageFormat.DICOM, ImageFormat.J2K, "unitTestDCM.dcm", "CR", "unitTestCompL10.j2k");
		CompressFile("  Compress VA .DCM file to VA lossless .j2k", ImageQuality.DIAGNOSTIC,
				ImageFormat.DICOM, ImageFormat.J2K, "unitTestDCM.dcm", "", "unitTestCompLL.j2k");
//      // 02/18/09 - cpt - un-comment these lines if bit depth reduction is implemented
//		CompressFile("  Compress VA .TGA file to VA lossy .jpg", ImageQuality.REFERENCE,
//				ImageFormat.TGA, ImageFormat.JPEG, "IE000404.TGA", "CT", "unitTestTGAComp.jpg");
//		CompressFile("  Compress VA .TGA file to VA lossy .jpg", ImageQuality.REFERENCE,
//				ImageFormat.TGA, ImageFormat.JPEG, "dm003684.tga", "CT", "unitTestTGAComp2.jpg");
//		CompressFile("  Compress VA .TGA file to VA lossy .jpg", ImageQuality.REFERENCE,
//				ImageFormat.TGA, ImageFormat.JPEG, "IE000182.TGA", "MR", "unitTestTGAComp3.jpg");
//		CompressFile("  Compress VA .TGA file to VA lossy .jpg", ImageQuality.REFERENCE,
//				ImageFormat.TGA, ImageFormat.JPEG, "IE000440.TGA", "NM", "unitTestTGAComp4.jpg");
//		CompressFile("  Compress VA .TGA file to VA lossy .jpg", ImageQuality.REFERENCE,
//				ImageFormat.TGA, ImageFormat.JPEG, "IE000464.TGA", "MR", "unitTestTGAComp5.jpg");
		
		System.out.println("Compression Tests DONE.");
	}

	public void CompressFile(String message, ImageQuality quality,
			ImageFormat inFormat, ImageFormat outFormat, String inFileName, String modality, String outFileName)
	{
		System.out.println(message + " started...");
		String targetImage = ViXTestDataFolder + "\\" + outFileName;
		ClassPathResource res = new ClassPathResource(inFileName); // this needs to be in main/test/resources
		File sourceFile = null;
		File targetFile = null;
		FileInputStream sourceFileStream = null;
		FileOutputStream targetFileStream = null;
		int bytesOut = 0;
		try
		{
			sourceFile = res.getFile(); // this throws an exception

			targetFile = new File(targetImage);
			if (targetFile.exists())
			{
				targetFile.delete();
			}
			targetFile.createNewFile();

			sourceFileStream = new FileInputStream(sourceFile);
			targetFileStream = new FileOutputStream(targetFile);

			ByteBufferBackedImageInputStream sourceStream = new ByteBufferBackedImageInputStream(
					sourceFileStream, sourceFileStream.available());

			ByteBufferBackedImageInputStream targetStream = imageConversion.CompressImage(
					  quality,	// THUMBNAIL, REFERENCE, DIAGNOSTIC
					  inFormat, // BMP, TIFF, TGA, DICOM
					  outFormat,// JPEG, J2K, DICOMJPEG, DICOMJ2K
					  sourceStream.toBufferedObject(),
					  modality);

			if ((!targetStream.isReadable()) || (targetStream.getSize() <= 0))
			{
				assertTrue(false);
			}
			// funnel in stream to out stream
			ByteStreamPump bSP = ByteStreamPump
					.getByteStreamPump(ByteStreamPump.TRANSFER_TYPE.ByteArrayToFile);
			bytesOut = bSP.xfer(targetStream.getInputStream(), targetFileStream);
			// targetFileStream.flush();
			FileDescriptor fd = targetFileStream.getFD();
			fd.sync();
			targetFileStream.close();
			targetFileStream = null;

			// TODO: see if image was created correctly
			sourceStream.closeSafely();
			if (targetFileStream != null)
				targetFileStream.close();
			System.out.println(message + "[" + bytesOut + " bytes] --> " +
				targetImage + ((bytesOut>0)?" - SUCCESS":" - F a i l e d !!"));
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		catch (ImageConversionInvalidInputException iciie)
		{
			iciie.printStackTrace();
		}
		catch (ImageConversionIOException icioe)
		{
			icioe.printStackTrace();
		}
		catch (ImageConversionCompressionException icce)
		{
			icce.printStackTrace();
		}
		finally {
			assertTrue(bytesOut!=0);			
		}	
	}

	public void testDecompressImage() {
		System.out.println("Starting Decompression Tests:");
		// decompress various compressed image files
		DecompressFile("  Decompress lossless compressed DCMJ2K file to DCM file",
				ImageFormat.DICOMJPEG2000, ImageFormat.DICOM, "unitTestLL.dcm.j2k", "unitTestLL.dcm");
		DecompressFile("  Decompress lossy compressed DCMJ2K file to DCM file",
				ImageFormat.DICOMJPEG2000, ImageFormat.DICOM, "unitTestL10.dcm.j2k", "unitTestL10.dcm");
		DecompressFile("  Decompress lossy compressed DCMJPG file to DCM file",
		ImageFormat.DICOMJPEG, ImageFormat.DICOM, "DM000779.DCM", "unitTestJPGL10.dcm");
//		ImageFormat.DICOMJPEG, ImageFormat.DICOM, "DVF00000021237.DCM", "unitTestJPGL10.dcm");
		DecompressFile("  Decompress lossless compressed J2K file to TGA file",
				ImageFormat.J2K, ImageFormat.TGA, "unitTestCompLL.j2k", "unitTestLL.tga");
		DecompressFile("  Decompress lossy compressed J2K file to TGA file",
				ImageFormat.J2K, ImageFormat.TGA, "unitTestCompL10.j2k", "unitTestL10.tga");
		DecompressFile("  Decompress lossless compressed J2K file to BMP file",
				ImageFormat.J2K, ImageFormat.BMP, "unitTestCompLL.j2k", "unitTestLL.bmp");
		DecompressFile("  Decompress lossy compressed J2K file to BMP file",
				ImageFormat.J2K, ImageFormat.BMP, "unitTestCompL10.j2k", "unitTestL10.bmp");
		DecompressFile("  Decompress lossless compressed J2K file to TIFF file",
				ImageFormat.J2K, ImageFormat.BMP, "unitTestCompLL.j2k", "unitTestLL.tif");
		DecompressFile("  Decompress lossy compressed J2K file to BMP file",
				ImageFormat.J2K, ImageFormat.BMP, "unitTestCompL10.j2k", "unitTestL10.tif");
		System.out.println("Decompression Tests DONE.");
	}
	
	public void DecompressFile(String message,
			ImageFormat inFormat, ImageFormat outFormat, String inFileName, String outFileName)
	{
		System.out.println(message + " started...");
		String targetImage = ViXTestDataFolder + "\\" + outFileName;
		ClassPathResource res = new ClassPathResource(inFileName); // this needs to be in main/test/resources
		File sourceFile = null;
		File targetFile = null;
		FileInputStream sourceFileStream = null;
		FileOutputStream targetFileStream = null;
		int bytesOut = 0;

		try
		{
			sourceFile = res.getFile(); // this throws an exception

			targetFile = new File(targetImage);
			if (targetFile.exists())
			{
				targetFile.delete();
			}
			targetFile.createNewFile();

			sourceFileStream = new FileInputStream(sourceFile);
			targetFileStream = new FileOutputStream(targetFile);

			ByteBufferBackedImageInputStream sourceStream = new ByteBufferBackedImageInputStream(
					sourceFileStream, sourceFileStream.available());

			ByteBufferBackedImageInputStream targetStream = imageConversion.DecompressImage(
					  inFormat, // JPEG, J2K, DICOMJPEG, DICOMJ2K
					  outFormat,//  BMP, TIFF, TGA, DICOM
					  sourceStream.toBufferedObject());

			if ((!targetStream.isReadable()) || (targetStream.getSize() <= 0))
			{
				assertTrue(false);
			}
			// funnel in stream to out stream
			ByteStreamPump bSP = ByteStreamPump
					.getByteStreamPump(ByteStreamPump.TRANSFER_TYPE.ByteArrayToFile);
			bytesOut = bSP.xfer(targetStream.getInputStream(), targetFileStream);
			// targetFileStream.flush();
			FileDescriptor fd = targetFileStream.getFD();
			fd.sync();
			targetFileStream.close();
			targetFileStream = null;

			// TODO: see if image was created correctly
			sourceStream.closeSafely();
			if (targetFileStream != null)
				targetFileStream.close();
			System.out.println(message + "[" + bytesOut + " bytes] --> " +
					targetImage + ((bytesOut>0)?" - SUCCESS":" - F a i l e d !!"));
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		catch (ImageConversionInvalidInputException iciie)
		{
			iciie.printStackTrace();
		}
		catch (ImageConversionIOException icioe)
		{
			icioe.printStackTrace();
		}
		catch (ImageConversionDecompressionException icce)
		{
			icce.printStackTrace();
		}
		finally {
			assertTrue(bytesOut!=0);			
		}	
	}
	
	public void testUpdateVAImage() {
		System.out.println("Starting VA Image Update Tests:");
		// convert VA .txt & .tga files to .dcmjpg (lossy) file, with HIS update
//		hm.put("0010,0010","IMAGPATIENT1007,1007"); // new Patient Name
		UpdateVAImage("  Convert VA .tga and .txt files to .dcm, with HIS update",
			ImageFormat.TGA,
			"unitTestTGA.big", "unitTestText.txt", "unitTestUDT1.dcm");
		// Test non DICOM compliant ACR-NEMA 2.0 conversion too
		UpdateVAImage("  Convert VA .tga and .txt files to .dcmj2k (Lossy), without HIS update",
				ImageFormat.TGA,
				"EP026886.TGA", "EP026886.TXT", "unitTestUDT2.dcm");		
//				"DM005669.TGA", "DM005669.TXT", "unitTestUDT2x.dcm");		
//				"W2061541ok.TGA", "W2061541.TXT", "unitTestWDC-NMUDT.dcm");		
		UpdateVAImage("  Convert VA .dcm (and TXT) file to .dcm, with HIS update",
				ImageFormat.DICOM,
				"unitTestDCM.dcm", "unitTestText.txt", "unitTestUDT3.dcm");		
		System.out.println("VA Image Update Tests DONE.");
	}

	public void UpdateVAImage(String message, ImageFormat inFormat,
			String inFileName1, String inFileName2, String outFileName) {
		// update VA Diag/Ref. .dcm or .tga/.big + .txt files with HIS update to uncompressed .dcm file
		System.out.println(message + " started...");
		// String imageCache = appConfiguration.getCacheUri().toString() + ViXTestDataFolder;
		String targetImage = ViXTestDataFolder + "\\" + outFileName;
		ClassPathResource res1 = new ClassPathResource(inFileName1); // this needs to be in main/test/resources
		ClassPathResource res2 = new ClassPathResource(inFileName2); // this needs to be in main/test/resources
		File sourceFile1 = null;
		File sourceFile2 = null;
		File targetFile = null;
		FileInputStream sourceFileStream1 = null;
		FileInputStream sourceFileStream2 = null;
		FileOutputStream targetFileStream = null;
		int bytesOut=0;

		try {
			sourceFile1 = res1.getFile();
			sourceFile2 = res2.getFile();
			// TODO: these throw an exception until the referenced file is created
			//       and added as a resource to the project

			targetFile = new File(targetImage);
			if (targetFile.exists()) {
				targetFile.delete();
			}
			targetFile.createNewFile();
			
			sourceFileStream1 = new FileInputStream(sourceFile1);
			sourceFileStream2 = new FileInputStream(sourceFile2);
			targetFileStream = new FileOutputStream(targetFile);

			ByteBufferBackedImageInputStream sourceStream1 = new ByteBufferBackedImageInputStream(sourceFileStream1, sourceFileStream1.available());
			ByteBufferBackedInputStream sourceStream2 = new ByteBufferBackedInputStream(sourceFileStream2, sourceFileStream2.available());

			ByteBufferBackedImageInputStream targetStream=imageConversion.UpdateVAImage(
					inFormat, sourceStream1.toBufferedObject(), sourceStream2.toBufferedObject());

			if ((!targetStream.isReadable()) || (targetStream.getSize()<=0)) {
				assertTrue(false);
			}
			// funnel in stream to out stream
			ByteStreamPump bSP = ByteStreamPump.getByteStreamPump(ByteStreamPump.TRANSFER_TYPE.ByteArrayToFile);
			bytesOut = bSP.xfer(targetStream.getInputStream(), targetFileStream);
			// targetFileStream.flush();
			FileDescriptor fd = targetFileStream.getFD();
			fd.sync();
			targetFileStream.close();
			targetFileStream = null;
			
			sourceStream1.closeSafely();
			sourceStream2.closeSafely();
			if (targetFileStream != null)
				targetFileStream.close();
			System.out.println(message + "[" + bytesOut + " bytes] --> " + targetImage + ((bytesOut>0)?" - SUCCESS":" - F a i l e d !!"));
		} 
		catch (IOException ex) {
			assertNotNull(1);
			ex.printStackTrace();
		} 
		catch (ImageConversionInvalidInputException iciie) {
			assertNotNull(1);
			iciie.printStackTrace();
		} 
		catch (ImageConversionIOException icioe) {
			assertNotNull(1);
			icioe.printStackTrace();
		} 
		finally {
			assertTrue(bytesOut!=0);			
		}	
	}

}