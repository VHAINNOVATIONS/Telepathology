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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public class ByteBufferSourceReadableByteChannel
implements ReadableByteChannel
{
	private ByteBuffer buffer = null;
	private boolean open = true;
	
	/**
	 * 
	 * @param buffer
	 */
	public ByteBufferSourceReadableByteChannel(ByteBuffer buffer)
	{
		this.buffer = buffer;
	}
	
	/**
	 *  Reads a sequence of bytes from this channel into the given buffer.
	 *  An attempt is made to read up to r bytes from the channel, 
	 *  where r is the number of bytes remaining in the buffer, that is, 
	 *  dst.remaining(), at the moment this method is invoked.
	 *  Suppose that a byte sequence of length n is read, where 0 <= n <= r. 
	 *  This byte sequence will be transferred into the buffer so that the first 
	 *  byte in the sequence is at index p and the last byte is at index p + n - 1, 
	 *  where p is the buffer's position at the moment this method is invoked. 
	 *  Upon return the buffer's position will be equal to p + n; its limit will 
	 *  not have changed.
	 *  A read operation might not fill the buffer, and in fact it might not read 
	 *  any bytes at all. Whether or not it does so depends upon the nature and 
	 *  state of the channel. A socket channel in non-blocking mode, for example, cannot 
	 *  read any more bytes than are immediately available from the socket's input buffer; 
	 *  similarly, a file channel cannot read any more bytes than remain in the file. 
	 *  It is guaranteed, however, that if a channel is in blocking mode and there is at 
	 *  least one byte remaining in the buffer then this method will block until at least 
	 *  one byte is read.
	 *  This method may be invoked at any time. If another thread has already initiated 
	 *  a read operation upon this channel, however, then an invocation of this method will 
	 *  block until the first operation is complete. 
	 */
	public int read(ByteBuffer dst) throws IOException
	{
		if(buffer.remaining() <= 0)
			return -1;
		
		int bytesToRead = Math.min(dst.remaining(), buffer.remaining());
		byte[] tempBuffer = new byte[bytesToRead];
		buffer.get(tempBuffer);
		dst.put(tempBuffer);
		
		return bytesToRead;
	}

	public boolean isOpen()
	{
		return open;
	}

	public void close() throws IOException
	{
		open = false;
	}

}
