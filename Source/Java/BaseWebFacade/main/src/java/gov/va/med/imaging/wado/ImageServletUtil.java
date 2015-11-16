package gov.va.med.imaging.wado;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * @author beckey
 * created: Apr 6, 2005 at 1:21:14 PM
 *
 * This class does ...
 */
public class ImageServletUtil
{
	public static final int DEFAULT_BUFFER_SIZE = 8192;

	/**
	 * Just copies from an InputStream to an OutputStream, this method
	 * leaves the Streams open when it completes.  It is the responsibility
	 * of the caller to close the Streams when this method returns.
	 */
	public static int pipeStream(InputStream inStream, OutputStream outStream) 
	throws IOException
	{
		return pipe(inStream, outStream, DEFAULT_BUFFER_SIZE);
	}
	
	/**
	 * Just copies from an InputStream to an OutputStream, this method
	 * leaves the Streams open when it completes.  It is the responsibility
	 * of the caller to close the Streams when this method returns.
	 * 
	 * @param inStream - the source Stream
	 * @param outStream - the destination Stream
	 * @param bufferSize - the size of the internal buffer
	 * @returns totalBytesPiped
	 * @throws IOException
	 */
	public static int pipe(InputStream inStream, OutputStream outStream, int bufferSize) 
	throws IOException
	{
		byte[] buffer = new byte[bufferSize];
		int totalBytesPiped=0;
		
		for( int bytesRead = inStream.read(buffer);
			  bytesRead > 0; 
			  bytesRead = inStream.read(buffer) )
		{
			outStream.write(buffer, 0, bytesRead);
			totalBytesPiped+=bytesRead;
		}
		return totalBytesPiped;
	}

	public static int pipe(ReadableByteChannel inChannel, OutputStream outStream) 
	throws IOException
	{
		return pipe(inChannel, Channels.newChannel(outStream), DEFAULT_BUFFER_SIZE);
	}
	
	public static int pipe(InputStream inStream, WritableByteChannel outChannel) 
	throws IOException
	{
		return pipe(Channels.newChannel(inStream), outChannel, DEFAULT_BUFFER_SIZE);
	}
	
	public static int pipe(ReadableByteChannel inChannel, WritableByteChannel outChannel) 
	throws IOException
	{
		return pipe(inChannel, outChannel, DEFAULT_BUFFER_SIZE);
	}
	
	public static int pipe(ReadableByteChannel inChannel, WritableByteChannel outChannel, int bufferSize) 
	throws IOException
	{
		int totalBytesPiped=0;
		int bytesWrote;
		
		if( bufferSize <= 0 )
			throw new IOException("Invalid buffer size specified");
		
		ByteBuffer buffy = ByteBuffer.allocate(bufferSize);
		
		buffy.clear();
		while (inChannel.read (buffy) >= 0) 
		{
			buffy.flip(  );
			bytesWrote=outChannel.write (buffy);
			buffy.clear(  );
			totalBytesPiped+=bytesWrote;
		}
		return totalBytesPiped;
	}
}
