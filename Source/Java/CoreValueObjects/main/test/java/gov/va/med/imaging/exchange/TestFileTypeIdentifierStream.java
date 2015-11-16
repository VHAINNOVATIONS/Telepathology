/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Oct 16, 2008
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
package gov.va.med.imaging.exchange;

import gov.va.med.imaging.exchange.enums.ImageFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import junit.framework.TestCase;

/**
 * Unit testing for the FileTypeIdentifierStream. This test looks in the image folder of the test
 * resources and identifies each image in the folder.  Then based on the file extension given to the
 * file determines if the file was identified correctly. This means that files in this folder need
 * non-standard file extensions such as dcmj2k to indicate a DICOM wrapped JPEG 2000 image. While not
 * optimal, this allows the ultimate flexibility.  When new "problem" images appear, they can simply
 * be dropped into this images folder and immediately are added to the test without any configuration
 * changes.
 * 
 * @author VHAISWWERFEJ
 *
 */
public class TestFileTypeIdentifierStream 
extends TestCase 
{
	// known file extensions and formats, to add a new format, add it here and the result
	// in the getExpectedImageFormat function
	private final static String FILE_EXTENSION_DCM = "dcm";
	private final static String FILE_EXTENSION_DCMJ2K = "dcmj2k";
	private final static String FILE_EXTENSION_DCMJPG = "dcmjpg";
	private final static String FILE_EXTENSION_DCMPDF = "dcmpdf";
	private final static String FILE_EXTENSION_J2K = "j2k";
	private final static String FILE_EXTENSION_JPEG = "jpg";
	private final static String FILE_EXTENSION_PDF = "pdf";
	private final static String FILE_EXTENSION_TGA = "tga";
	private final static String FILE_EXTENSION_TIFF = "tiff";
	private final static String FILE_EXTENSION_BMP = "bmp";
	private final static String FILE_EXTENSION_AVI = "avi";
	private final static String FILE_EXTENSION_WAV = "wav";
	private final static String FILE_EXTENSION_DOC = "doc";
	private final static String FILE_EXTENSION_HTML = "html";
	private final static String FILE_EXTENSION_RTF = "rtf";
	private final static String FILE_EXTENSION_MP3 = "mp3";
	private final static String FILE_EXTENSION_MPG = "mpg";
	private final static String FILE_EXTENSION_PNG = "png";
	private final static String FILE_EXTENSION_GIF = "gif";
	private final static String FILE_EXTENSION_XLS = "xls";
	private final static String FILE_EXTENSION_DOCX = "docx";
	private final static String FILE_EXTENSION_XML = "xml";
	
	public TestFileTypeIdentifierStream()
	{
		super();
	}
	
	private String getFilesDirectory()
	{
		URL path = getClass().getResource("images");
		File f = new File(path.getFile());
		String imageDir = f.getAbsolutePath();
		imageDir = imageDir.replaceAll("%20", " ");
		return imageDir;
	}
	
	public void testImageIdentification()
	{
		String imageDirectory = getFilesDirectory();
		File directory = new File(imageDirectory);
		File[] files = directory.listFiles();
		assertNotSame("Did not find any images in image directory", 0, files.length);
		for(File file : files)
		{
			try
			{
				System.out.println("Testing format for file [" + file.getAbsolutePath() + "]");
				ImageFormat expectedFormat = getExpectedImageFormat(file);
				assertNotNull("Expected format for file [" + file.getAbsolutePath() + "] is null", expectedFormat);
				FileTypeIdentifierStream inputStream = 
					new FileTypeIdentifierStream(new FileInputStream(file));
				ImageFormat imageFormat = inputStream.getImageFormat();
				inputStream.close();
				assertEquals("Expected image format [" + expectedFormat + "] not equal to image format [" + imageFormat + "] for file [" + file.getAbsolutePath() + "]", 
						expectedFormat, imageFormat);
			}
			catch(FileNotFoundException fnfX)
			{
				fail("FileNotFoundException: " + fnfX.getMessage());
			}
			catch(IOException ioX)
			{
				fail("IO Exception: " + ioX.getMessage());
			}
		}
	}	
	
	/**
	 * Determines the expected image format based on the file extension.
	 * @param file
	 * @return
	 */
	private ImageFormat getExpectedImageFormat(File file)
	{
		String filename = file.getName().toLowerCase();
		
		if(filename.endsWith(FILE_EXTENSION_DCM))
		{
			return ImageFormat.DICOM;
		}
		else if(filename.endsWith(FILE_EXTENSION_DCMJ2K))
		{
			return ImageFormat.DICOMJPEG2000;
		}
		else if(filename.endsWith(FILE_EXTENSION_DCMJPG))
		{
			return ImageFormat.DICOMJPEG;
		}
		else if(filename.endsWith(FILE_EXTENSION_DCMPDF))
		{
			return ImageFormat.DICOMPDF;	
		}
		else if(filename.endsWith(FILE_EXTENSION_J2K))
		{
			return ImageFormat.J2K;
		}
		else if(filename.endsWith(FILE_EXTENSION_PDF))
		{
			return ImageFormat.PDF;
		}
		else if(filename.endsWith(FILE_EXTENSION_TGA))
		{
			return ImageFormat.TGA;
		}
		else if(filename.endsWith(FILE_EXTENSION_TIFF))
		{
			return ImageFormat.TIFF;
		}
		else if(filename.endsWith(FILE_EXTENSION_JPEG))
		{
			return ImageFormat.JPEG;
		}
		else if(filename.endsWith(FILE_EXTENSION_BMP))
		{
			return ImageFormat.BMP;
		}
		else if(filename.endsWith(FILE_EXTENSION_AVI))
		{
			return ImageFormat.AVI;
		}
		else if(filename.endsWith(FILE_EXTENSION_WAV))
		{
			return ImageFormat.WAV;
		}
		else if(filename.endsWith(FILE_EXTENSION_DOC))
		{
			return ImageFormat.DOC;
		}
		else if(filename.endsWith(FILE_EXTENSION_MP3))
		{
			return ImageFormat.MP3;
		}
		else if(filename.endsWith(FILE_EXTENSION_HTML))
		{
			return ImageFormat.HTML;
		}
		else if(filename.endsWith(FILE_EXTENSION_RTF))
		{
			return ImageFormat.RTF;
		}
		else if(filename.endsWith(FILE_EXTENSION_MPG))
		{
			return ImageFormat.MPG;
		}
		else if(filename.endsWith(FILE_EXTENSION_PNG))
		{
			return ImageFormat.PNG;
		}
		else if(filename.endsWith(FILE_EXTENSION_GIF))
		{
			return ImageFormat.GIF;
		}
		else if(filename.endsWith(FILE_EXTENSION_XLS))
		{
			return ImageFormat.XLS;
		}
		else if(filename.endsWith(FILE_EXTENSION_DOCX))
		{
			return ImageFormat.DOCX;
		}
		else if(filename.endsWith(FILE_EXTENSION_XML))
			return ImageFormat.XML;
		return null;
	}

}
