/**
 * 
 */
package gov.va.med.imaging.channels;

import java.util.List;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * A class that is used to push bytes from an input stream to an output stream.
 * Based upon the type of streams (network, memory, or file) the buffer size is
 * optimized. 
 * Use the getByteStreamPump(TRANSFER_TYPE) static method to get an optimized
 * byte stream pump.
 * Once the reference to the ByteStreamPump is obtained, call either:
 * xfer(InputStream inStream, OutputStream outStream) or,
 * xfer(InputStream inStream, OutputStream[] outStream)
 * to transfer the input stream content to the output stream(s).
 * e.g.
 * ByteStreamPump bsp = ByteStreamPump.getByteStreamPump(TRANSFER_TYPE.FileToNetwork);
 * bsp.xfer(inFile, outResponse);
 * 
 * @see ByteChannelPump
 * 
 * @author VHAISWBECKEC
 *
 */
public class ByteStreamPump
extends AbstractBytePump
{
	// =================================================================================================
	// Buffer Pool Management
	// =================================================================================================
	private static List<byte[]> bufferPool = new ArrayList<byte[]>();
	
	private static byte[] getBuffer(BUFFER_SIZE bufferSize)
	{
		int size = bufferSize.getSize();
		synchronized(bufferPool)
		{
			for(byte[] buffer : bufferPool)
			{
				if(buffer.length == size)
				{
					bufferPool.remove(buffer);
					return buffer;
				}
			}
			return new byte[size];
		}
	}
	
	private static byte[] getBuffer(String name)
	{
		BUFFER_SIZE bufferSize = getBufferSize(name, MEDIUM.StreamToStream);
		return getBuffer(bufferSize);
	}
	
	// when a BytePump has completed a xfer it:
	// 1.) releases the buffer
	// 2.) reports its usage so that we can adjust the buffer size
	private static void bufferUsageComplete(
			byte[] buffer, 
			String name, 
			int smallestChunkSize, int largestChunkSize,
			int totalBytesRead, int readCount,
			long elapsedTime)
	{
		synchronized(bufferPool)
		{
			bufferPool.add(buffer);
		}
		
		if(name != null)
			adjustBufferUsage(name, MEDIUM.StreamToStream, smallestChunkSize, largestChunkSize, totalBytesRead, readCount, elapsedTime);
	}
	
	// ============================================================================================================
	// Factory Methods
	// ============================================================================================================
	public static ByteStreamPump getByteStreamPump()
	{
		return getByteStreamPump((String)null);
	}

	public static ByteStreamPump getByteStreamPump(Class<?> clazz)
	{
		return getByteStreamPump(clazz.getName());
	}

	public static ByteStreamPump getByteStreamPump(TRANSFER_TYPE transferType)
	{
		return getByteStreamPump(transferType.toString());
	}

	public static ByteStreamPump getByteStreamPump(String name)
	{
		return new ByteStreamPump(name);
	}

	// ============================================================================================================
	// Instance Constructors, Fields, and Methods
	// ============================================================================================================
	private ByteStreamPump(String name)
	{
		super(name);
	}

	/**
	 * Read all bytes from the input stream and write them to the output stream
	 * 
	 * @param inStream
	 * @param outStream
	 * @return
	 * @throws IOException, IllegalArgumentException
	 */
	public int xfer(InputStream inStream, OutputStream outStream) 
	throws IOException, IllegalArgumentException
	{
		if(outStream == null)
			throw new IllegalArgumentException("OutputStream must be non-null");
		
		return xfer(inStream, new OutputStream[]{outStream});
	}
	
	/**
	 * Read all bytes from the input stream and write to all output streams
	 * 
	 * @param inStream
	 * @param outStreams
	 * @return
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public int xfer(InputStream inStream, OutputStream[] outStreams) 
	throws IOException, IllegalArgumentException
	{
		if(inStream == null)
			throw new IllegalArgumentException("InputStream must be non-null");

		if(outStreams == null)
			throw new IllegalArgumentException("OutputStream must be non-null");
		
		byte[] buffy = ByteStreamPump.getBuffer(getName());
		
		int totalBytesRead = 0;
		int readCounts = 0;
		int smallestChunkSize = Integer.MAX_VALUE;
		int largestChunkSize = 0;
		long startTime = System.currentTimeMillis();

		boolean anyIoExceptions = false;
		IOException[] streamExceptions = new IOException[outStreams.length];
		for(int i=0; i<streamExceptions.length; ++i)
			streamExceptions[i] = null;
		
		for(int bytesRead=inStream.read(buffy); bytesRead > 0; bytesRead=inStream.read(buffy))
		{
			++readCounts;
			totalBytesRead += bytesRead;
			
			smallestChunkSize = Math.min(smallestChunkSize, bytesRead);
			largestChunkSize = Math.max(largestChunkSize, bytesRead);
			
			// write to each of the streams unless an exception has occured on that stream previously
			for(int outStreamIndex = 0; outStreamIndex < outStreams.length; ++outStreamIndex)
			{
				OutputStream outStream = outStreams[outStreamIndex];
				if(outStream != null && streamExceptions[outStreamIndex] == null)
				{
					try
					{
						outStream.write(buffy, 0, bytesRead);
					}
					catch(IOException ioX)
					{
						streamExceptions[outStreamIndex] = ioX;
						anyIoExceptions = true;
					}
				}
			}
		}
		
		bufferUsageComplete(buffy, getName(), 
				smallestChunkSize, 
				largestChunkSize,
				totalBytesRead,
				readCounts, 
				System.currentTimeMillis() - startTime);

		if(anyIoExceptions)
			throw CompositeIOException.create(streamExceptions, totalBytesRead);
		
		return totalBytesRead;
	}
}
