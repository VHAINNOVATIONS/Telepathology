/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr 27, 2010
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

import gov.va.med.imaging.channels.ChecksumValue;
import gov.va.med.imaging.exchange.storage.ByteBufferBackedImageInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;

import org.junit.Test;
import static org.junit.Assert.* ;

/**
 * @author vhaiswwerfej
 *
 */
public class ByteBufferBackedInputStreamTest 
extends AbstractByteBufferBackedInputStreamTest
{
	private final static int defaultBufferSize = 1024 * 16; // 16K buffer seemed to give optimal performance

	@Test
	public void runTests()
	{
		try
		{
			File[] files = getTestFiles();
			//testFile(files[0]);
			
			for(File file : files)
			{
				testFile(file);
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			fail(ex.getMessage());
		}
	}
	
	private File [] getTestFiles()
	{
		File imageDirectory = new File(getFilesDirectory());
		
		File[] files = imageDirectory.listFiles(new ImageFilter());
		return files;
	}
	
	private void testFile(File file)
	throws Exception
	{
		System.out.println("Testing file '" + file.getName() + "'.");
		testReadingFileFromStream(file);
		testReadingFileFromBuffer(file);
		testPartialReadingFileFromBuffer(file);
		testChecksum(file);
		System.out.println("Done test file '" + file.getName() + ".");
	}
	
	private void testReadingFileFromStream(File file)
	throws Exception
	{
		ByteBufferBackedImageInputStream imageBuffer = getBufferedBackInputStreamToFile(file);
		assertNotNull(imageBuffer);
		assertNotNull(imageBuffer.getImageFormat());
		assertFalse(imageBuffer.isBuffered());
		assertTrue(imageBuffer.isReadable());
		assertEquals(file.length(), imageBuffer.getSize());
		compareStreamToFile(imageBuffer.getInputStream(), file, imageBuffer.getSize());
		assertFalse(imageBuffer.isBuffered());
	}
	
	private void testReadingFileFromBuffer(File file)
	throws Exception
	{
		ByteBufferBackedImageInputStream imageBuffer = getBufferedBackInputStreamToFile(file);
		assertNotNull(imageBuffer);		
		assertNotNull(imageBuffer.getImageFormat());
		assertFalse(imageBuffer.isBuffered());
		assertTrue(imageBuffer.isReadable());
		assertEquals(file.length(), imageBuffer.getSize());
		compareStreamToFile(imageBuffer.getInputStreamFromBuffer(), file, imageBuffer.getSize());
		assertTrue(imageBuffer.isBuffered());
	}
	
	private void testPartialReadingFileFromBuffer(File file)
	throws Exception
	{
		ByteBufferBackedImageInputStream imageBuffer = getBufferedBackInputStreamToFile(file);
		assertNotNull(imageBuffer);		
		assertFalse(imageBuffer.isBuffered());
		assertTrue(imageBuffer.isReadable());
		assertEquals(file.length(), imageBuffer.getSize());
		compareStreamToFile(imageBuffer.getInputStreamFromBuffer(), file, imageBuffer.getSize() / 2);
		compareStreamToFile(imageBuffer.getInputStreamFromBuffer(), file, imageBuffer.getSize());		
		assertTrue(imageBuffer.isBuffered());
	}
	
	private void testChecksum(File file)
	throws Exception
	{
		ChecksumValue expectedChecksum = getExpectedChecksum(file);
		ByteBufferBackedImageInputStream imageBuffer = getBufferedBackInputStreamToFile(file);
		ChecksumValue calculatedChecksum = imageBuffer.getCalculatedChecksum();
		assertEquals(expectedChecksum, calculatedChecksum);
	}
	
	private ChecksumValue getExpectedChecksum(File file)
	throws Exception
	{
		CheckedInputStream cis = new CheckedInputStream(new FileInputStream(file), new Adler32());
		
		
		byte [] buf = new byte[defaultBufferSize];
		while(cis.read(buf) >= 0)
		{
			// must read entire file to update the checksum 
		}
		return new ChecksumValue(cis.getChecksum());
	}
	
	private void compareStreamToFile(InputStream input, File file, long bytesToRead)
	throws Exception
	{
		FileInputStream fis = null;;
		try
		{
			System.out.println("Comparing stream to file '" + file.getName() + "', reading '" + bytesToRead + "' bytes.");
			fis = new FileInputStream(file);
			int b1 = input.read();
			int b2 = fis.read();
			int count = 0;
			while((b1 > -1) && (count < bytesToRead))
			{
				if(b1 != b2)
					throw new Exception("Error comparing file '" + file.getName() + "', failed at byte [" + count + "].");
				count++;
				b1 = input.read();
				b2 = fis.read();
			}
			System.out.println("Comparison done, read '" + count + "' bytes.");
		}
		finally
		{
			if(fis != null)
			{
				try
				{
					fis.close();
				}
				catch(Exception ex){}
			}
			try
			{
				input.close();
			}
			catch(Exception ex) {}
		}
	}
	
	private ByteBufferBackedImageInputStream getBufferedBackInputStreamToFile(File file)
	throws Exception
	{
		return new ByteBufferBackedImageInputStream(new FileInputStream(file), (int)file.length());
	}
	
	private String getFilesDirectory()
	{
		URL path = getClass().getResource("images");
		File f = new File(path.getFile());
		String imageDir = f.getAbsolutePath();
		imageDir = imageDir.replaceAll("%20", " ");
		return imageDir;
	}

}
