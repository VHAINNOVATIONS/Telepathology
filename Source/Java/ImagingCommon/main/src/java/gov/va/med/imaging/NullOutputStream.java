/**
 * 
 */
package gov.va.med.imaging;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Checksum;

/**
 * A Class that extends OutputStream and drops the bytes written to it to the bit bucket.
 * 
 * @author VHAISWBECKEC
 *
 */
public class NullOutputStream 
extends OutputStream
{
	private Checksum checksum;
	private long bytesWritten;
	
	/**
	 * 
	 */
	public NullOutputStream()
	{
	}

	/**
	 * Construct a NullOutputStream with a checksum, which is updated on
	 * each write()
	 * 
	 * @param checksum
	 */
	public NullOutputStream(Checksum checksum)
	{
		this.checksum = checksum;
		bytesWritten = 0;
	}
	
	/**
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(int b) 
	throws IOException
	{
		if(checksum != null)
			checksum.update(b);
		++bytesWritten;
	}

	@Override
	public void write(byte[] b, int off, int len) 
	throws IOException
	{
		if(checksum != null)
			checksum.update(b, off, len);
		
		bytesWritten += len;
	}

	@Override
	public void write(byte[] b) 
	throws IOException
	{
		if(checksum != null)
			checksum.update(b, 0, b.length);
		
		bytesWritten += b.length;
	}
	
	public long getBytesWritten()
	{
		return bytesWritten;
	}
}
