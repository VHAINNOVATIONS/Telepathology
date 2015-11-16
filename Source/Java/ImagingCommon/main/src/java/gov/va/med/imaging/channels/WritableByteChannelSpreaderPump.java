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

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.zip.Checksum;

/**
 * @author beckey
 * created: Aug 11, 2005 at 4:17:47 PM
 *
 * This class copies a single input channel to multiple output
 * channels.  The input channel fills a buffer on the current
 * thread.  The output channels each run on a separate thread
 * and are assigned view buffers over the buffer allocated
 * for the input channel.
 * 
 * This class wraps an instance of WritableByteChannelSpreader and will
 * manage pushing the bytes through the channels.
 *  
 * There is opportunity for performance enhancement
 * in how the writer threads and view buffers are allocated.  
 * However, be forewarned that any changes in this area must 
 * be carefully tested for locking and synchronization issues.
 * 
 * To use this class to copy the content of one file to multiple
 * others, do something like:
 * 	try
 * 	{
 * 		ByteChannelSpreader cat = new ByteChannelSpreader(
 * 			"/home/beckey/Documents/webmail_addressbook.csv",
 * 			new String[] {
 * 				"/home/beckey/Documents/webmail_addressbook_copy1.csv"
 * 				"/home/beckey/Documents/webmail_addressbook_copy2.csv"
 * 			}
 * 		);
 * 		cat.copy();
 * 	}
 * 	catch (IOException X)
 * 	{
 *		X.printStackTrace();
 *	}
 *
 * To copy from one channel to multiple other channels, substitute channels
 * for file names in the above example.
 * Internally, all the constructors instantiate channels and the copy()
 * method itself operates only on channels.
 */
public class WritableByteChannelSpreaderPump
{
	public static final int DEFAULT_BUFFER_SIZE = 1024*256;
	public static final int DEFAULT_CRC_BUFFER_SIZE = 1024;
	
	private ReadableByteChannel inChannel = null;
	private WritableByteChannel[] outChannels = null;
	private int bufferSize = DEFAULT_BUFFER_SIZE;
	private FilePropertiesWritableByteChannelImpl typer = null;
	
	/**
	 * This is the preferred constructor
	 * 
	 * @param inChannel
	 * @param outChannels
	 * @throws IOException
	 */
	public WritableByteChannelSpreaderPump(
		ReadableByteChannel inChannel, 
		WritableByteChannel[] outChannels
	)
	throws IOException
	{
		this.inChannel = inChannel;
		this.outChannels = outChannels;
	}
	
	/**
	 * For old code that only has streams.
	 * @param inStream
	 * @param outStreams
	 * @throws IOException
	 */
	public WritableByteChannelSpreaderPump(
		InputStream inStream, 
		OutputStream[] outStreams
	)
	throws IOException
	{
		this.inChannel = Channels.newChannel(inStream);
		this.outChannels = new WritableByteChannel[outStreams.length];
		for(int index=0; index<outStreams.length; ++index)
			this.outChannels[index] = Channels.newChannel( outStreams[index] );
	}
	
	/**
	 * 
	 * @param file
	 * @param outStream
	 * @throws IOException
	 */
	public WritableByteChannelSpreaderPump(File file, OutputStream outStream)
	throws IOException
	{
		inChannel = (new FileInputStream(file)).getChannel();
		outChannels = new WritableByteChannel[] {Channels.newChannel(outStream)};
	}
	
	/**
	 * Copy the file to standard out.  This is here mostly for testing.
	 * @param file
	 * @throws IOException
	 */
	public WritableByteChannelSpreaderPump(File file)
	throws IOException
	{
		this(file, System.out);
	}
	
	/**
	 * 
	 * @param pathname
	 * @param outStream
	 * @throws IOException
	 */
	public WritableByteChannelSpreaderPump(String pathname, OutputStream outStream)
	throws IOException
	{
		this( new File(pathname), outStream );
	}

	/**
	 * 
	 * @param pathname
	 * @throws IOException
	 */
	public WritableByteChannelSpreaderPump(String pathname)
	throws IOException
	{
		this(pathname, System.out);
	}
	
	/**
	 * 
	 * @param inFilename
	 * @param outFilenames
	 * @throws IOException
	 */
	public WritableByteChannelSpreaderPump(
		String inFilename, 
		String[] outFilenames)
	throws IOException
	{
		inChannel = (new FileInputStream(inFilename)).getChannel();
		outChannels = new WritableByteChannel[outFilenames.length];
		for(int n=0; n < outFilenames.length; ++n)
			outChannels[n] = new FileOutputStream(outFilenames[n]).getChannel();
		
	}
	
	/**
	 * 
	 * @param inFile
	 * @param outFiles
	 * @throws IOException
	 */
	public WritableByteChannelSpreaderPump(File inFile, File[] outFiles)
	throws IOException
	{
		inChannel = (new FileInputStream(inFile)).getChannel();
		outChannels = new WritableByteChannel[outFiles.length];
		for(int n=0; n < outFiles.length; ++n)
			outChannels[n] = new FileOutputStream(outFiles[n]).getChannel();
	}

	/**
	 * 
	 * @return
	 */
	public int getBufferSize()
	{
		return bufferSize;
	}
	/**
	 * The bigger the better.  The buffer is allocated using
	 * "direct", so the buffer size may not show up in the JVM
	 * memory.
	 * 
	 * @param bufferSize
	 */
	public void setBufferSize(int bufferSize)
	{
		this.bufferSize = bufferSize;
	}
	
	/**
	 * Return the number of bytes read into the internal buffer.
	 * When the copy() method returns then this will reliably 
	 * indicate the length of the data.  Until then it is just
	 * a rough indication of the progress.
	 * 
	 * @return
	 */
	public int getLengthOfData()
	{
		return typer == null ? 0 : typer.getTotalBytesWritten();
	}
	
	/**
	 * Return the checksum calculator.
	 * Once all bytes have been streamed through (i.e. copy() is complete), 
	 * the .value() method will return the checksum of the bytes streamed.
	 * @return
	 */
	public Checksum getChecksum()
	{
		return typer == null ? null : typer.getChecksum();
	}
	
	public String getMimeType()
	{
		return typer == null ? null : typer.getMimeType();
	}

	/**
	 * Start the copy.  When this method completes the input
	 * channel will be completely drained and the contents
	 * sent to all of the output channels.
	 * 
	 * @throws IOException
	 */
	public long copy()
	throws IOException
	{
		return copy(inChannel, outChannels );
	}
	
	/**
	 * 
	 * @param sourceChannel
	 * @param destinationChannels
	 * @throws IOException
	 */
	private long copy(ReadableByteChannel sourceChannel, WritableByteChannel[] destinationChannels)
	throws IOException
	{
		typer = new FilePropertiesWritableByteChannelImpl();
		WritableByteChannelSpreader spreader = 
			new WritableByteChannelSpreader(destinationChannels, typer);
		return this.pumpReadableByteChannelToWritableChannel(sourceChannel, spreader);
	}

	/**
	 * @return the number of bytes moved through from the read channel
	 * @param clientChannel
	 * @param readChannel
	 * @throws IOException
	 */
	public long pumpReadableByteChannelToWritableChannel(
		ReadableByteChannel readChannel,
		WritableByteChannel clientChannel) 
	throws IOException
	{
		long bytesMoved = 0L;
		
		ByteBuffer buffer = ByteBuffer.allocateDirect(bufferSize);
		buffer.clear();
		while (readChannel.read (buffer) >= 0) 
		{
			bytesMoved += buffer.limit();
			buffer.flip(  );
			clientChannel.write (buffer);
			buffer.clear(  );
		}
		
		return bytesMoved;
	}
}
