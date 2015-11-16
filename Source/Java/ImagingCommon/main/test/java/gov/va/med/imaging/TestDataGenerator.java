/**
 * 
 */
package gov.va.med.imaging;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import junit.framework.TestCase;

/**
 * A test data generator class that will build byte arrays, file and streams
 * for testing.  The data will always be ascending values from 0 to 255, and then
 * rolling back to 0.
 * 
 * @author VHAISWBECKEC
 *
 */
public class TestDataGenerator
{
	public static InputStream createSlowInputStream(int length, int maxBytesOnRead)
	{
		return new SlowInputStream(length, maxBytesOnRead);
	}
	
	public static ReadableByteChannel createFastReadableByteChannel(int length)
	{
		return new TestDataInputChannel(length, Math.min(length, 1024));
	}
	
	public static ReadableByteChannel createSlowReadableByteChannel(int length)
	{
		return new TestDataInputChannel(length, Math.min(length, 10));
	}
	
	public static byte[] createByteArray(int length)
	{
		byte[] retVal = new byte[length];
		
		for(int index=0; index < length; ++index)
			retVal[index] = (byte)index;
		
		return retVal;
	}
	
	public static File createFile(int length) 
	throws IOException
	{
		File tempDir = getTempDir();
		File tempFile = File.createTempFile("test_", ".data", tempDir);
		
		FileOutputStream outStream = new FileOutputStream(tempFile);
		
		for(int index=0; index<length; ++index)
			outStream.write((byte)index);
		
		outStream.close();
		
		return tempFile;
	}

	public static File createBlankDestinationFile() 
	throws IOException
	{
		File tempDir = getTempDir();
		File tempFile = File.createTempFile("test_", ".data", tempDir);
		
		return tempFile;
	}
	
	/**
	 * Create a writabe byte channel that will check that the values being written
	 * follow the test data generator pattern.
	 * 
	 * @return
	 */
	public static WritableByteChannel createTestDataValidatingWritableByteChannel()
	{
		return new ValidatingWritableByteChannel();
	}
	
	/**
	 * Validates for the test data pattern and writes no more than 10 bytes per call to 
	 * write().  This is useful for testing non-blocking channels.
	 * 
	 * @return
	 */
	public static WritableByteChannel createTestDataValidatingSlowWritableByteChannel()
	{
		return new ValidatingWritableByteChannel(10);
	}
	
	/**
	 * @return
	 */
	private static File tempDir = null;
	
	private static synchronized File getTempDir()
	{
		if(tempDir != null)
			return tempDir;
		
		String tempDirName = System.getenv("TEMP");;
		if(tempDirName == null)
			tempDirName = "/temp";

		tempDir = new File(tempDirName);
		if(! tempDir.exists())
			tempDir.mkdirs();
		
		return tempDir;
	}

	/**
	 * A method that simply validates that a byte array has
	 * valid test data as defined by this class (ascending values from 0 to 255).
	 * 
	 * @param source
	 * @return
	 */
	public static void assertValidTestData(byte[] source)
	{
		for(int index=0; index<source.length; ++index)
			TestCase.assertEquals( "Value at " + index + "is not as expected.", (byte)index, source[index] );
	}

	public static void assertValidTestData(File file) 
	throws IOException
	{
		assertValidTestData(new FileInputStream(file));
	}
	
	public static void assertValidTestData(InputStream inStream) 
	throws IOException
	{
		int index = 0;
		for(int value = inStream.read(); value >= 0; value = inStream.read())
		{
			TestCase.assertEquals( "Value at " + index + "is not as expected.", (byte)index, (byte)value );
			
			index++;
		}
	}
}

/**
 * @author VHAISWBECKEC
 * 
 * A test class that generates test data (ascending values from 0 to 255) and provides
 * only a limited number of bytes per read.
 * This is to simulate a network connection where the number of bytes available will
 * usually be less than the buffer sizes passed to the read.
 *
 */
class SlowInputStream
extends InputStream
{
	private int maxBytesOnRead = 10;
	
	private int length;
	public SlowInputStream(int length, int maxBytesOnRead)
	{
		this.length = length;
		this.maxBytesOnRead = maxBytesOnRead;
	}
	
	private int index;
	private byte getNextByte()
	{
		if(index >= this.length)
			return -1;
		return (byte)index++;
	}
	
	/* (non-Javadoc)
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() 
	throws IOException
	{
		return getNextByte();
	}

	@Override
	public int available() 
	throws IOException
	{
		return maxBytesOnRead;
	}

	@Override
	public int read(byte[] b) throws IOException
	{
		return this.read(b, 0, b.length);
	}
	
	private boolean eofReached = false;
	@Override
	public int read(byte[] b, int off, int len) throws IOException
	{
		if(eofReached)
			return -1;
		
		len = Math.min(b.length - off, len);		// do not overrun the buffer
		int maxBytesToReturn = Math.min(len, maxBytesOnRead);
		
		int bytesRead = 0;
		for(int index=off; index < off+maxBytesToReturn && !eofReached; ++index)
		{
			b[index] = getNextByte();
			bytesRead++;
			eofReached = (b[index] < 0);
		}
		
		return bytesRead;
	}
}

/**
 * A trivial ReadableByteChannel implementation that provides very few 
 * bytes per read.
 * 
 * @author VHAISWBECKEC
 *
 */
class TestDataInputChannel
implements ReadableByteChannel
{
	private int maxBytesOnRead = 10;
	private int length;
	private int totalBytesRead;
	private boolean open = true;
	private boolean eofReached = false;
	
	public TestDataInputChannel(int maxBytesOnRead, int length)
	{
		this.totalBytesRead = 0;
		this.maxBytesOnRead = maxBytesOnRead;
		this.length = length;
	}

	private int index;
	private byte getNextByte()
	{
		if(index >= this.length)
			return -1;
		return (byte)index++;
	}
	
	public int read(ByteBuffer dst) 
	throws IOException
	{
		if(eofReached)
			return -1;
		
		int bytesRead = 0;
		for(int index=0; 
			index < maxBytesOnRead && !eofReached && dst.position() < dst.limit(); 
			++index)
		{
			dst.put(getNextByte());
			++bytesRead;
			++totalBytesRead;
			eofReached = (totalBytesRead >= length);
		}
		
		return bytesRead;
	}

	public void close() 
	throws IOException
	{
		open = false;
	}

	public boolean isOpen()
	{
		return open;
	}
}

/**
 * @author VHAISWBECKEC
 * 
 * A writable byte channel that validates the data as it is being written to.
 * 
 * If bytesPerWrite is specified then this class does not write all of the available bytes 
 * on the every call to write.
 *
 */
class ValidatingWritableByteChannel 
implements WritableByteChannel
{
	private int channelIndex = 0;
	private boolean open = true;
	private int bytesPerWrite = Integer.MAX_VALUE;

	ValidatingWritableByteChannel()
	{
		
	}
	ValidatingWritableByteChannel(int bytesPerWrite)
	{
		
	}
	
	public int write(ByteBuffer src) 
	throws IOException
	{
		if(! isOpen())
			throw new ClosedChannelException();
		
		int bytesWritten = 0;
		while(src.remaining() > 0 && bytesWritten <= bytesPerWrite)
		{
			byte value = src.get();
			if( value != (byte)channelIndex)
				throw new StreamCorruptedException(
					"Test data at position " + channelIndex + " is not correct, expected = " + (byte)channelIndex + ", actual was " + value
				);
				
			channelIndex++;
			++bytesWritten;
		}
		
		return bytesWritten;
	}

	public void close() throws IOException
	{
		open = false;
	}

	public boolean isOpen()
	{
		return open;
	}
}
