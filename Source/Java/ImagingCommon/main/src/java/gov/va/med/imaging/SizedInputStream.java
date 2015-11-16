package gov.va.med.imaging;

import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * @author VHAISWTITTOC
 *
 * SizedInputStream carries the byte size along an InputStream to support size for not file based streams.
 */

public class SizedInputStream
{
	private InputStream inStream = null;
	private int byteSize = 0;

	/**
	 * Construct a usable SizedInputStream.
	 * @param inStream
	 * @param byteSize
	 */
	public SizedInputStream(InputStream inStream, int byteSize)
    {
	    super();
	    this.inStream = inStream;
	    this.byteSize = byteSize;
    }
    
	public InputStream getInStream() {
		return inStream;
	}

	public void setInStream(InputStream inStream) {
		this.inStream = inStream;
	}

	public int getByteSize() {
		return byteSize;
	}

	public void setByteSize(int byteSize) {
		this.byteSize = byteSize;
	}

	public void close()
	throws IOException {
		if (inStream != null) {
			inStream.close();
		}
		byteSize = 0;
	}
}