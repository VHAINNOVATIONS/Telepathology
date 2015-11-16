/*
 * Copyright (c) 2005, United States Veterans Administration
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list 
 * of conditions and the following disclaimer in the documentation and/or other 
 * materials provided with the distribution.
 * Neither the name of the United States Veterans Administration nor the names of its 
 * contributors may be used to endorse or promote products derived from this software 
 * without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES 
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED 
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR 
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH 
 * DAMAGE.
 */
package gov.va.med.imaging.channels;

import gov.va.med.imaging.ClonableAdler32;
import gov.va.med.imaging.channels.events.StorageByteChannelEventObservable;
import gov.va.med.imaging.channels.events.StorageByteChannelEventObservableImpl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.WritableByteChannel;
import java.util.zip.Checksum;

/**
 * @author beckey
 * created: Sep 8, 2005 at 1:57:27 PM
 *
 * This class does ...
 */
public class FilePropertiesWritableByteChannelImpl
extends StorageByteChannelEventObservableImpl
implements WritableByteChannel, StorageByteChannelEventObservable, FilePropertiesWritableByteChannel
{
	private static final int defaultSampleBufferSize = 2048;

	private boolean open = true;		// keep track of whether we're supposed to act like we're open
	private int totalBytesWritten = 0;	// the total number of bytes that pass through this channel
	private byte[] sampleBuffer = null; 	// the internal buffer we use to sample the contents
	private Checksum checksum = null;	// An instance of a Checksum class

	public FilePropertiesWritableByteChannelImpl()
	{
		this(defaultSampleBufferSize);
	}
	public FilePropertiesWritableByteChannelImpl(int bufferSize)
	{
		this(bufferSize, null);
	}
	public FilePropertiesWritableByteChannelImpl(Object notificationContext)
	{
		this(defaultSampleBufferSize, notificationContext);
	}
	public FilePropertiesWritableByteChannelImpl(int bufferSize, Object notificationContext)
	{
		super(notificationContext);
		sampleBuffer = new byte[bufferSize];
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.impl.FilePropertiesWritableByteChannel#getChecksum()
	 */
	public synchronized Checksum getChecksum()
	{
		if(checksum == null)
			checksum = new ClonableAdler32();
		return checksum;
	}

	public void setChecksum(Checksum checksum)
	{
		if(this.checksum == null)
			this.checksum = checksum;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.impl.FilePropertiesWritableByteChannel#getTotalBytesWritten()
	 */
	public int getTotalBytesWritten()
	{
		if(isOpen())
			return 0;
		return totalBytesWritten;
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.impl.FilePropertiesWritableByteChannel#getMimeType()
	 */
	public String getMimeType()
	{
		if(isOpen())
			return null;
		
		return null;
	}
	
	/**
	 *  Writes a sequence of bytes to this channel from the given buffer.  
	 *  An attempt is made to write up to r bytes to the channel, where r is the number of bytes 
	 *  remaining in the buffer, that is, dst.remaining(), at the moment this method is invoked.
	 *  Suppose that a byte sequence of length n is written, where 0 <= n <= r. This byte sequence 
	 *  will be transferred from the buffer starting at index p, where p is the buffer's position 
	 *  at the moment this method is invoked; the index of the last byte written will be p + n - 1. 
	 *  Upon return the buffer's position will be equal to p + n; its limit will not have changed.
	 *  Unless otherwise specified, a write operation will return only after writing all of the r 
	 *  requested bytes. Some types of channels, depending upon their state, may write only some of 
	 *  the bytes or possibly none at all. A socket channel in non-blocking mode, for example, cannot 
	 *  write any more bytes than are free in the socket's output buffer.
	 *  This method may be invoked at any time. If another thread has already initiated a write 
	 *  operation upon this channel, however, then an invocation of this method will block until the 
	 *  first operation is complete.
	 *   
	 * @see java.nio.channels.WritableByteChannel#write(java.nio.ByteBuffer)
	 */
	public int write(ByteBuffer src) 
	throws IOException
	{
		int bytesWrittenThisInvocation = 0;
		
		// in reality we don't care whether we've been closed or not
		// but we have to act like a real WritableByteChannel
		if(!open)
			throw new ClosedChannelException();
		
		while(src.hasRemaining())
		{
			// determine how many bytes we can read without getting a buffer underflow
			int readableBytes = Math.min(src.remaining(), sampleBuffer.length);
			
			// get all the bytes we can into out byte array
			src.get(sampleBuffer, 0, readableBytes);
			
			// do the calculations on our byte array
			getChecksum().update(sampleBuffer, 0, sampleBuffer.length);
			
			// keep a running total of bytes written on this invocation only
			bytesWrittenThisInvocation += readableBytes;
		}
		
		// keep a running total of all bytes written
		totalBytesWritten += bytesWrittenThisInvocation;
		
		notifyListenersWriteEvent(bytesWrittenThisInvocation);
		return bytesWrittenThisInvocation;
	}

	/* (non-Javadoc)
	 * @see java.nio.channels.Channel#isOpen()
	 */
	public boolean isOpen()
	{
		return open;
	}

	/* (non-Javadoc)
	 * @see java.nio.channels.Channel#close()
	 */
	public void close() throws IOException
	{
		open = false;
		notifyListenersCloseEvent( 0, getTotalBytesWritten(), getChecksum(), getMimeType() );
	}

}
