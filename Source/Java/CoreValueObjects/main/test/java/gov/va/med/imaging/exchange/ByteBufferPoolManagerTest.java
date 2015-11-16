/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Oct 21, 2008
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

import gov.va.med.imaging.SizedInputStream;
import gov.va.med.imaging.exchange.storage.DataSourceByteBufferPoolManager;
import gov.va.med.imaging.exchange.storage.KnownSizeByteBuffer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import junit.framework.TestCase;

/**
 * @author VHAISWWERFEJ
 *
 */
public class ByteBufferPoolManagerTest 
extends TestCase 
{
	public ByteBufferPoolManagerTest()
	{
		super();
	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() 
	throws Exception 
	{
		super.setUp();		
	}

	private String getFilesDirectory()
	{
		URL path = getClass().getResource("images");
		File f = new File(path.getFile());
		String imageDir = f.getAbsolutePath();
		imageDir = imageDir.replaceAll("%20", " ");
		return imageDir;
	}
	
	public void testBufferPoolManager()
	{
		String fileDirectory = getFilesDirectory();
		File directory = new File(fileDirectory);
		File[] files = directory.listFiles();
		for(File file : files)
		{
			TestFile(file);
		}
	}
	
	private DataSourceByteBufferPoolManager getBufferPoolManager()
	{
		return DataSourceByteBufferPoolManager.getByteBufferPoolManager();
	}
	
	private void TestFile(File file)
	{
		KnownSizeByteBuffer buffer = null;
		DataSourceByteBufferPoolManager manager = getBufferPoolManager();
		try
		{
			assertNotNull(file);			
			FileInputStream input = new FileInputStream(file);
			//ByteStreamPump pump = ByteStreamPump.getByteStreamPump();
			//pump.xfer(input, outStream);
			buffer = manager.readIntoBuffer(file.getName(), input);
			long fileLength = file.length();
			assertEquals("File length does not match known buffer size", buffer.getKnownSize(), fileLength);					
			TestBufferReads(buffer, fileLength);
			TestBufferReads(buffer, fileLength / 2);
			TestBufferReads(buffer, fileLength);
		}
		catch(Exception ex)
		{
			fail(ex.getMessage());
		}
		finally 
		{
			if(buffer != null)
				manager.releaseBuffer(buffer.getBuffer());		
		}
	}
	
	private void TestBufferReads(KnownSizeByteBuffer buffer, long bytesToRead)
	{
		SizedInputStream inputStream = getInputStream(buffer);
		assertNotNull(inputStream);
		assertNotNull(inputStream.getInStream());
		assertNotSame(0, inputStream.getByteSize());
		try
		{
			for(int i = 0; i < (bytesToRead); i++)
			{
				inputStream.getInStream().read();
			}
		}
		catch(IOException ioX)
		{
			fail(ioX.getMessage());
		}
		finally
		{
			try
			{
				inputStream.getInStream().close();
			}
			catch(IOException ioX)
			{
				fail(ioX.getMessage());
			}
		}		
	}
	
	private SizedInputStream getInputStream(KnownSizeByteBuffer buffer)
	{
		return getBufferPoolManager().openStreamToBuffer(buffer);
	}
	
	//private void Test
	

}
