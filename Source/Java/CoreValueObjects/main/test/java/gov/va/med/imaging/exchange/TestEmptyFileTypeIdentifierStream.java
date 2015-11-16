/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 21, 2011
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
package gov.va.med.imaging.exchange;

import gov.va.med.imaging.core.interfaces.exceptions.ImageNotFoundException;
import gov.va.med.imaging.exchange.enums.ImageFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author vhaiswwerfej
 *
 */
public class TestEmptyFileTypeIdentifierStream
{
	private final static Logger logger = Logger.getLogger(TestEmptyFileTypeIdentifierStream.class);
	
	private String getFilesDirectory()
	{
		URL path = getClass().getResource("emptyImages");
		File f = new File(path.getFile());
		String imageDir = f.getAbsolutePath();
		imageDir = imageDir.replaceAll("%20", " ");
		return imageDir;
	}
	
	private File [] getEmptyFiles()
	{
		String imageDirectory = getFilesDirectory();
		File directory = new File(imageDirectory);
		return directory.listFiles();
	}
	
	@Test
	public void testEmptyFileThrowException()
	{
		testEmptyFileHandling(true);
	}
	
	@Test
	public void testEmptyFileNoException()
	{
		testEmptyFileHandling(false);
	}

	private void testEmptyFileHandling(boolean throwException)
	{
		File [] files = getEmptyFiles();
		assertNotSame("Did not find any images in image directory '" + getFilesDirectory() + "'", 
				0, files.length);
		int fileCount = 0;
		for(File file : files)
		{
			FileTypeIdentifierStream ftis = null;
			try
			{
				ftis = new FileTypeIdentifierStream(new FileInputStream(file));
				ImageFormat imageFormat = ftis.getImageFormat(throwException);
				if(throwException)
					fail("Empty file '" + file.getAbsolutePath() + "' should have thrown an exception and did not.");
				assertEquals("Empty file '" + file.getAbsolutePath() +"' did not get expected image format", 
						ImageFormat.ORIGINAL, imageFormat);
			}
			catch(ImageNotFoundException infX)
			{
				if(!throwException)
					fail("Empty file '" + file.getAbsolutePath() + "' should not have thrown an exception but it did");
			}
			catch(FileNotFoundException fnfX)
			{
				fail("FileNotFoundException: " + fnfX.getMessage());
			}			
			finally
			{
				if(ftis != null)
				{
					try
					{
						ftis.close();
					}
					catch(IOException ioX) {}
					
				}
			}
			fileCount++;			
		}
		logger.info("Tested '" + fileCount + "' files with throwException=" + throwException);
		
	}
}
