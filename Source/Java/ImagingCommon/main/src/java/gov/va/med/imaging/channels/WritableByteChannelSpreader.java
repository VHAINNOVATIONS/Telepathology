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

import gov.va.med.imaging.channels.events.StorageByteChannelEventObservableImpl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.*;


/**
 * @author beckey
 * created: Sep 7, 2005 at 1:13:37 PM
 *
 */
public class WritableByteChannelSpreader
extends StorageByteChannelEventObservableImpl
implements WritableByteChannel
{
	/* ===================================================================================
	 * Static Members
	 * =================================================================================== */
	public static final int crcCalculationBufferSize = 1024;
	public static final int defaultExecutorServiceThreadCount = 10;
	private static ExecutorService clientServiceExecutor = null;

	/**
	 * A simple static method to write a buffer to multiple writable channels.
	 * This runs in a single thread and is intended as a convenience for writing to a cache
	 * and a destination nearly simultaneously.
	 * Use this method like a regular channel write, in particular the buffer pointers 
	 * must be properly set (flip() called) before calling this method. 
	 * 
	 * @param buffy
	 * @param writeChannels
	 * @throws IOException 
	 */
	public static void write(ByteBuffer buffy, WritableByteChannel[] writeChannels) 
	throws IOException
	{
		ByteBuffer[] bufferViews = new ByteBuffer[writeChannels.length];
		
		// NOTE: bufferViews[0] will be the source (real) buffer
		bufferViews[0] = buffy;
		for(int channelIndex=1; channelIndex<writeChannels.length; ++channelIndex)
			bufferViews[channelIndex] = buffy.duplicate();
		
		for(int channelIndex=0; channelIndex<writeChannels.length; ++channelIndex)
			writeChannels[channelIndex].write(bufferViews[channelIndex]);
	}
	
	/**
	 * If the environment provides an ExecutorService (i.e. a thread pool) then
	 * we can use it.  Once the ExecutorService is set it may not be changed.
	 *  
	 * @param executorService
	 */
	public static synchronized void setExecutorService(ExecutorService executorService)
	{
		if(clientServiceExecutor == null)
			clientServiceExecutor = executorService;
		else
			System.err.println("Attempt to set the WritableByteChannelSpreader ExecutorService after it has already been set.  Ignoring, just don't do it again.");
	}
	
	/**
	 * Get the ExecutorService to use for managing our worker threads, creating one if none
	 * has been set previously.  It is recommended that the environment in which this class
	 * is running create and set the ExecutorService prior to creating instances of this class.
	 * 
	 * @return
	 */
	private static synchronized ExecutorService getExecutorService()
	{
		if(clientServiceExecutor == null)
			clientServiceExecutor = Executors.newFixedThreadPool(defaultExecutorServiceThreadCount);
		
		return clientServiceExecutor;
	}

	/* ===================================================================================
	 * Constructors
	 * =================================================================================== */
	
	public WritableByteChannelSpreader(WritableByteChannel[] outChannels)
	{
		this(outChannels, null);
	}
	
	public WritableByteChannelSpreader(WritableByteChannel[] outChannels, Object notificationContext)
	{
		this(outChannels, null, notificationContext);
	}
	
	public WritableByteChannelSpreader(WritableByteChannel[] outChannels, WritableByteChannel sampleChannel)
	{
		this(outChannels, sampleChannel, null);
	}
	
	public WritableByteChannelSpreader(WritableByteChannel[] outChannels, WritableByteChannel sampleChannel, Object notificationContext)
	{
		super(notificationContext);
		this.outChannels = outChannels;
		this.sampleChannel = sampleChannel;
	}
	
	/* ===================================================================================
	 * Instance Members
	 * =================================================================================== */
	private WritableByteChannel[] outChannels = null;
	private WritableByteChannel sampleChannel = null;
	private int bytesWritten = 0;
    private CompletionService<Integer> completionService = null;
	
	/**
	 * Get the CompletionService instance to submit our worker tasks to.
	 * Note that the ExecutorService may be supplied to this class by a 
	 * calling class so that we can work from an existing thread pool.
	 * An instance of CompletionService is created for each instance of
	 * this class so that we can track our own results.
	 * 
	 * @return
	 */
	private synchronized CompletionService<Integer> getExecutorCompletionService()
	{
		if(completionService == null)
			completionService = new ExecutorCompletionService<Integer>(getExecutorService());

		return completionService;
	}
	
	public int getBytesWritten()
	{
		return bytesWritten;
	}

	/**
	 * Write the contents of the buffer to all of the WritableByteChannels
	 * which we were instantiated with.
	 */
	public int write(ByteBuffer srcBuffer) 
	throws IOException
	{
		for(int channelIndex=0; channelIndex<outChannels.length; ++channelIndex)
		{
			ChannelWriterCallable serviceCallable = 
				new ChannelWriterCallable(srcBuffer.asReadOnlyBuffer(), outChannels[channelIndex]);
			getExecutorCompletionService().submit(serviceCallable);
		}
		
		// while the write channels are busily writing to disk
		// write to the SampleChannel if one is specified
		//srcBuffer.flip();
		while(sampleChannel != null && srcBuffer.hasRemaining())
			this.bytesWritten += sampleChannel.write(srcBuffer);
		
		// wait for the ChannelWriterCallable instances to complete, we know that we must get
		// the same number of results as we submitted so can just iterate till
		// we get that number of results
		Integer firstResult = null;		// this will contain the number of bytes written
										// and should be the same for all ChannelWriterCallable
										// else we throw an exception
		for (int callableIndex = 0; callableIndex < outChannels.length; ++callableIndex)
		{
			try
			{
				Integer result = null;
				// Retrieves and removes the Future representing the next completed task, 
				// WAITING if none are yet present.
				Future<Integer> r = getExecutorCompletionService().take();
				
				// Waits if necessary for the computation to complete, 
				// and then retrieves its result.
				result = r.get();
				if(firstResult != null && !result.equals(firstResult) )
					throw new IOException("WritableByteChannels did not write an equal number of bytes");
				firstResult = result;
			}
			catch (InterruptedException X)
			{
				// this thread was interrupted while waiting for results
				X.printStackTrace();
				throw new IOException(X.getMessage());
			}
			catch (ExecutionException X)
			{
				// one of the worker threads threw an exception
				X.printStackTrace();
				throw new IOException(X.getMessage());
			}
		}
		
		// the buffer has been completely used up, reset everything to a fresh state
		//srcBuffer.clear();
		int bytesWritten = firstResult.intValue();
		this.notifyListenersWriteEvent(bytesWritten);
		return bytesWritten;
	}
	
	/**
	 * Close this WritableByteChannel and all output channels
	 */
	public void close() 
	throws IOException
	{
		for(int channelIndex=0; channelIndex<outChannels.length; ++channelIndex)
		{
			outChannels[channelIndex].close();
		}
		if(sampleChannel != null)
			sampleChannel.close();
		
		this.notifyListenersCloseEvent(0, 0, null, null );
	}

	/**
	 * Returns true if any of the WritabelByteChannel instances that we write to
	 * are open
	 */
	public boolean isOpen()
	{
		for(int channelIndex=0; channelIndex<outChannels.length; ++channelIndex)
			if(outChannels[channelIndex].isOpen())
				return true;

		return sampleChannel.isOpen();
	}
	
	/**
	 * 
	 * @author beckey
	 * created: Sep 8, 2005 at 1:26:19 PM
	 *
	 * This class implements the worker thread functionality for writing
	 * a buffer to one open channel.  There is one instance of this class
	 * for each device (channel) we are simultaneously writing to.
	 */
	class ChannelWriterCallable 
	implements Callable<Integer> 
	{
		private ByteBuffer buffer = null;
		private WritableByteChannel writableChannel = null;
		
		ChannelWriterCallable(ByteBuffer buffer, WritableByteChannel writableChannel)
		{
			this.buffer = buffer;
			this.writableChannel = writableChannel;
		}

		public Integer call()
		throws Exception
		{
			int bytesWritten = writableChannel.write(buffer);
			return new Integer(bytesWritten);
		}
	}
}
