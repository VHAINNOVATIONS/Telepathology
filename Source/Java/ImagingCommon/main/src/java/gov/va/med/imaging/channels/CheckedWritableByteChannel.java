/**
 * 
 */
package gov.va.med.imaging.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.zip.Checksum;

/**
 * A WritableByteChannel that calculates a Checksum while it is being written to.
 * This class iterates over a read-only buffer over the source buffer, as such its
 * performance should be evaluated before using in a production situation. 
 * 
 * @author VHAISWBECKEC
 *
 */
public class CheckedWritableByteChannel 
implements WritableByteChannel
{
	private WritableByteChannel writableChannel;
	private Checksum checksum;
	
	public CheckedWritableByteChannel(WritableByteChannel writableChannel, Checksum checksum)
	{
		this.writableChannel = writableChannel;
		this.checksum = checksum;
	}
	
	/* (non-Javadoc)
	 * @see java.nio.channels.WritableByteChannel#write(java.nio.ByteBuffer)
	 */
	public int write(ByteBuffer src) 
	throws IOException
	{
		ByteBuffer piggy = src.asReadOnlyBuffer();
		while( piggy.position() < piggy.limit() )
			checksum.update(piggy.get());
		
		return writableChannel.write(src);
	}

	/* (non-Javadoc)
	 * @see java.nio.channels.Channel#close()
	 */
	public void close() throws IOException
	{
		writableChannel.close();
	}

	/* (non-Javadoc)
	 * @see java.nio.channels.Channel#isOpen()
	 */
	public boolean isOpen()
	{
		return writableChannel.isOpen();
	}

	public Checksum getChecksum()
	{
		return this.checksum;
	}
}
