package gov.va.med.imaging.exchange.conversion;

import gov.va.med.imaging.exchange.conversion.exceptions.ImageBufferException;

import java.nio.ByteBuffer;

/**
 * 
 * @deprecated No longer used
 *
 */
@Deprecated
public class ImageBuffers {
	
	private int numBuffers = 0;
	private ByteBuffer [] buffer = null;
	private int bufferSize[]=null;
	// buffer 1 -- temporary buffer to hold data
	// buffer 2 -- temporary buffer to hold data
	// buffer 3 -- temporary buffer to hold data; only used for reading in fake file!!

	/**
	 * This constructor method initializes the ImageBuffers object for given number of buffers.
	 * @param numBufs 1..n 
	 */
	public ImageBuffers(int numBufs){
		buffer = new ByteBuffer [numBufs];
		bufferSize = new int [numBufs];
		this.numBuffers = numBufs;
	}
	
	/**
	 * Returns number of buffers initialized.
	 * @return numBuffers
	 */
	public int getNumBuffers() {
		return this.numBuffers;
	}
	
	/**
	 * This method allocates the given buffer with given size.
	 * @param bIndex 1..n where n is max buffers allocated in class constructor
	 * @param byteSize
	 * @throws ImageBufferException on illegal input
	 */
	public void setBuffer(int bIndex, int byteSize)
    throws ImageBufferException {
       	if ((bIndex > this.numBuffers) || (bIndex<=0)){
        	throw new ImageBufferException("setBuffer -- Illegal buffer index = " + bIndex 
        									+ "; 1.." + this.numBuffers + " are valid!");
    	}
       	if (byteSize <= 0) {
        	throw new ImageBufferException("setBuffer -- Illegal byte size = " + byteSize);
    	}
       	bufferSize[bIndex-1] = byteSize;
	    buffer[bIndex-1] = ByteBuffer.allocate(byteSize);
    }
    
	/**
	 * This method returns the size of the given buffer.
	 * @param bIndex 1..n where n is max buffers allocated in class constructor
	 * @return byteSize
	 * @throws ImageBufferException on illegal index
	 */
	public int getBufferSize(int bIndex)
    throws ImageBufferException {
		if ((bIndex > this.numBuffers) || (bIndex<=0)){
	       	throw new ImageBufferException("getBufferSize -- Illegal buffer index = " + bIndex 
	       									+ "; 1.." + this.numBuffers + " are valid!");
		}
		return bufferSize[bIndex-1] ;
    }
	
	/**
	 * This method returns the given ByteBuffer.
	 * @param bIndex 1..n where n is max buffers allocated in class constructor
	 * @return ByteBuffer
	 * @throws ImageBufferException on illegal index
	 */
	public ByteBuffer getBuffer(int bIndex)
	throws ImageBufferException {
       	if ((bIndex > this.numBuffers) || (bIndex<=0)){
       		throw new ImageBufferException("getBuffer -- Illegal buffer index = " + bIndex 
	        								+ "; 1.." + this.numBuffers + " are valid!");
       	}
	    return buffer[bIndex-1];
    }
	
	/**
	 * This method returns a byte[] array on the given ByteBuffer.
	 * @param bIndex 1..n where n is max buffers allocated in class constructor
	 * @return byte[]
	 * @throws ImageBufferException on illegal index
	 */
	public byte[] getBufferArray(int bIndex)
    throws ImageBufferException {
    	if ((bIndex > this.numBuffers) || (bIndex<=0)){
    		throw new ImageBufferException("getBufferArray -- Illegal buffer index = " + bIndex 
    										+ "; 1.." + this.numBuffers + " are valid!");
	    }
    	return buffer[bIndex-1].array();
    }
    
	/**
	 * This method closes given ByteBuffer by clearing its position and mark, setting its size to 0.
	 * @param bIndex 1..n where n is max buffers allocated in class constructor
	 * @throws ImageBufferException on illegal index
	 */
	public void closeBuffer(int bIndex)
    throws ImageBufferException {
    	if ((bIndex > this.numBuffers) || (bIndex<=0)){
        	throw new ImageBufferException("closeBuffer -- Illegal buffer index = " + bIndex 
        									+ "; 1.." + this.numBuffers + " are valid!");
      	}
		if (buffer[bIndex-1]!=null) {
			buffer[bIndex-1].clear();
			bufferSize[bIndex-1]=0;
		}
	}

	/**
	 * This method closes all buffers by clearing position and mark, setting size to 0.
	 * @throws ImageBufferException on illegal index
	 */
	public void close() {
		if (buffer != null) {
 			for (int i=0; (i < this.numBuffers ); i++) {
				if (buffer[i]!=null) {
					buffer[i].clear();
					bufferSize[i]=0;
				}
			}
			buffer = null;
			bufferSize = null;
		}
		this.numBuffers=0;
	}

	// ============================== Private Memory Cache related method =====================================
	// 
	/**
	 * This method makes sure that the given byte array is stored in the buffer.
	 * @param bIndex 1..n where n is max buffers allocated in class constructor
	 * @return byteSize
	 * @throws ImageBufferException on illegal input
	 */
	public void setAndFillBuffer(int bIndex, byte[] data, int byteLength)
	throws ImageBufferException
	{
		if ((data==null) || (byteLength==0)) {
	       	throw new ImageBufferException("setAndFillBuffer -- filler byte array is null or 0 size");
		}
    	if ((bIndex > this.numBuffers) || (bIndex<=0)) {
	       	throw new ImageBufferException("setAndFillBuffer -- Illegal buffer index = " + bIndex 
					+ "; 1.." + this.numBuffers + " are valid!");			
		}
		// make sure content gets into buffer before proceeding -- wait up to 1 second
		final byte seed=(byte)0x95;	// taking advantage that uncompressed DICOM file rarely ends on this value
		setBuffer(bIndex, byteLength);
        buffer[bIndex-1].put(byteLength-1, seed);
        buffer[bIndex-1].rewind();
        buffer[bIndex-1].put(data, 0, byteLength);
        int loops=0;
		while ((buffer[bIndex-1].get(byteLength-1)==seed) && (loops < 10)) {
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
		    } finally {
		    	buffer[bIndex-1].rewind();
		    	loops++;
		    }
		}
		buffer[bIndex-1].rewind();
        if (loops>0) {
        	System.out.println("setAndfillBuffer(" + bIndex + ") -- Looped " + loops + " times! ");
		}
	}
}
