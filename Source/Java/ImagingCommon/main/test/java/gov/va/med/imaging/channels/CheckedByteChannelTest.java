/**
 * 
 */
package gov.va.med.imaging.channels;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.zip.Adler32;

import junit.framework.TestCase;

/**
 * @author VHAISWBECKEC
 *
 */
public class CheckedByteChannelTest extends TestCase
{

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() 
	throws Exception
	{
		super.setUp();
	}

	public ReadableByteChannel getTestDataAsReadableByteChannel()
	{
		return Channels.newChannel(
			getClass().getClassLoader().getResourceAsStream("edvard_munch.gif")
		);
	}
	
	public WritableByteChannel getDrain() 
	throws IOException
	{
		File tempFile = File.createTempFile("sink", ".tmp");
		FileOutputStream outStream = new FileOutputStream(tempFile);
		
		tempFile.deleteOnExit();
		
		return outStream.getChannel();
	}

	public void testCheckedByteChannels() 
	throws IOException
	{
		ByteBuffer buffy = ByteBuffer.allocate(1024);
		CheckedReadableByteChannel readChannel = new CheckedReadableByteChannel(getTestDataAsReadableByteChannel(), new Adler32());
		CheckedWritableByteChannel writeChannel = new CheckedWritableByteChannel(getDrain(), new Adler32());
		while( readChannel.read(buffy) >= 0 )
		{
			buffy.flip();
			writeChannel.write(buffy);
			buffy.clear();
		}
		
		assertTrue(readChannel.getChecksum().getValue() != 0);
		assertTrue(writeChannel.getChecksum().getValue() != 0);
		
		assertEquals( readChannel.getChecksum().getValue(), writeChannel.getChecksum().getValue() );
		
		readChannel.close();
		writeChannel.close();
	}
	
	protected void tearDown() 
	throws Exception
	{
		super.tearDown();
	}
	
}
