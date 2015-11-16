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

import gov.va.med.imaging.exchange.enums.ImageFormat;
import gov.va.med.imaging.exchange.storage.ByteBufferBackedImageInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author vhaiswwerfej
 *
 */
public class TestByteBufferBackedImageInputStream
{
	
	@Test
	public void testDetectingEmptyImage()
	{
		testEmptyImageHandling(true);
	}
	
	@Test
	public void testNotDetectingEmptyImage()
	{
		testEmptyImageHandling(false);
	}
	
	public void testEmptyImageHandling(boolean detectEmptyImageStream)
	{
		String imageDirectory = getEmptyImagesDirectory();
		File directory = new File(imageDirectory);
		File [] files = directory.listFiles();
		assertNotSame(0, files.length);
		for(File file : files)
		{
			try
			{
				ByteBufferBackedImageInputStream bbbiis = 
					new ByteBufferBackedImageInputStream(new FileInputStream(file), 
							(int)file.length(), null, detectEmptyImageStream);
				if(detectEmptyImageStream)
				{
					assertTrue(bbbiis.isEmptyStream());
					assertNull(bbbiis.getImageFormat());
				}
				else
				{
					assertFalse(bbbiis.isEmptyStream());
					assertNotNull(bbbiis.getImageFormat());
					assertEquals(ImageFormat.ORIGINAL, bbbiis.getImageFormat());
				}
			}
			catch(FileNotFoundException fnfX)
			{
				fail(fnfX.getMessage());
			}
		}
	}
	
	private String getEmptyImagesDirectory()
	{
		URL path = getClass().getResource("emptyImages");
		File f = new File(path.getFile());
		String imageDir = f.getAbsolutePath();
		imageDir = imageDir.replaceAll("%20", " ");
		return imageDir;
	}

}
