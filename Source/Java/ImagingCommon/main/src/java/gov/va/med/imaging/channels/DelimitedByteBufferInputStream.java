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
import java.io.InputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 * 
 * @author beckey
 * created: Sep 23, 2005 at 2:56:25 PM
 *
 * This class provides an InputStream over a ByteBuffer
 * with a specified delmiter.  When the delimiter is read
 * from the InputStream then this class returns a -1 (EOF).
 * 
 * The delimiter will be the last bytes read from this InputStream.
 */
public class DelimitedByteBufferInputStream
extends InputStream
{
	private ByteBuffer buffer;
	private byte[] delimiter;
	private boolean delimiterFound = false;
	
	/**
	 * The byteBuffer MUST BE FLIPPED before calling read() for the first time.
	 * 
	 * @param buffer
	 * @param delimiter
	 */
	public DelimitedByteBufferInputStream(ByteBuffer buffer, byte[] delimiter)
	{
		this.buffer =  buffer;
		this.delimiter = delimiter;
	}
	
	
	@Override
	public int read() 
	throws IOException
	{
		// this assurea that no matter how many times read() is called
		// it will always return -1 after the delimiter is found
		// buffered streams above this may repeatedly call read() even if
		// a -1 was returned
		if(delimiterFound)
			return -1;
		
		try
		{
			byte value = buffer.get();
			System.out.print((char)value);
			delimiterFound = delimiterFound || delimiterFound(value);
			
			return delimiterFound ? -1 : value;
		}
		catch (BufferUnderflowException buX)
		{
			// if there are no bytes available then return -1, we're done
			return -1;
		}
	}

	private byte[] fifo = null;
	private int fifoOffset = 0;
	private boolean delimiterFound(byte value)
	{
		if(fifo == null)
			fifo = new byte[delimiter.length];
		if(fifoOffset < delimiter.length-1)
			fifo[fifoOffset++] = value;
		else
		{
			for(int n=1; n < fifo.length; ++n)
				fifo[n-1] = fifo[n];
			fifo[fifo.length-1] = value;
		}

		for(int n=0; n < fifo.length; ++n)
			if( fifo[n] != delimiter[n] )
				return false;
		
		return true;
	}
}
