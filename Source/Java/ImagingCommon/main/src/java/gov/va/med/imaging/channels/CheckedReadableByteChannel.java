/**
 * 
 */
package gov.va.med.imaging.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.zip.Checksum;

/**
 * A ReadableByteChannel that calculates a Checksum while it is being read from.
 * This class iterates over a read-only buffer over the destination buffer, as such its
 * performance should be evaluated before using in a production situation. 
 * 
 * @author VHAISWBECKEC
 *
 */
public class CheckedReadableByteChannel 
implements ReadableByteChannel
{
	private ReadableByteChannel readableChannel;
	private Checksum checksum;
	
	public CheckedReadableByteChannel(ReadableByteChannel readableChannel, Checksum checksum)
	{
		this.readableChannel = readableChannel;
		this.checksum = checksum;
	}
	
	/* (non-Javadoc)
	 * @see java.nio.channels.ReadableByteChannel#read(java.nio.ByteBuffer)
	 */
	public int read(ByteBuffer dst) 
	throws IOException
	{
		int bytesRead = readableChannel.read(dst);
		
		ByteBuffer piggy = dst.asReadOnlyBuffer();
		piggy.flip();
		while( piggy.position() < piggy.limit() )
			checksum.update(piggy.get());
		
		return bytesRead;
	}

	/* (non-Javadoc)
	 * @see java.nio.channels.Channel#close()
	 */
	public void close() 
	throws IOException
	{
		readableChannel.close();
	}

	/* (non-Javadoc)
	 * @see java.nio.channels.Channel#isOpen()
	 */
	public boolean isOpen()
	{
		return readableChannel.isOpen();
	}

	public Checksum getChecksum()
	{
		return this.checksum;
	}
}
