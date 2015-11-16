package gov.va.med.imaging.channels;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import org.apache.log4j.Logger;

/**
 * The class that writes to the channel as something is reading from the 
 * input stream.  To use it, do something like:
 * 
 * // open the siphoning stream given a source stream, that is essentially wrapped, and a
 * // channel where the siphoned bytes will go
 *  
 * WriteChannelSiphoningInputStream siphon = 
 *   new WriteChannelSiphoningInputStream( inData.getTestDataAsInputStream(), siphoningChannel );
 *   
 * // read from the input stream like usual  
 * for(int bytesRead = siphon.read(temp, 32, 42); bytesRead != -1; bytesRead = siphon.read(temp, 32, 42) )
 * {
 * 	System.arraycopy(temp, 32, destination, byteCount, bytesRead);
 * 	byteCount += bytesRead;
 * }
 * // make sure that the stream is closed, else there is no guarantee that all bytes will
 * // be written to the siphon.  Note that flush() is not supported because there is no
 * // guarantee that the wrapped input stream supports it
 * siphon.close();
 * // all of the bytes read from the input stream will also have been written to the siphoned channel
 * 
 * @author vhaiswbeckec
 *
 */
public class WriteChannelSiphoningInputStream
extends InputStream
{
	private InputStream wrappedStream;
	private WritableByteChannel siphoningChannel;
	private final Logger logger = Logger.getLogger(this.getClass());
	
	public WriteChannelSiphoningInputStream(InputStream wrappedStream, WritableByteChannel siphoningChannel)
	{
		logger.debug("<ctor>");
		this.wrappedStream = wrappedStream;
		this.siphoningChannel = siphoningChannel;
	}

	/**
	 * @return the logger
	 */
	Logger getLogger()
	{
		return this.logger;
	}

	@Override
	public int available() 
	throws IOException
	{
		return wrappedStream.available();
	}

	/**
	 * This close DOES NOT wait for the siphon stream to complete all of its 
	 * writing before returning.
	 * @throws IOException
	 */
	@Override
	public void close() 
	throws IOException
	{
		wrappedStream.close();
		closeSiphoningChannel();
		logger.debug("asynchronous close.");
	}
	
	/**
	 * This close DOES wait for the siphon stream to complete all of its 
	 * writing before returning.
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public void synchronousClose(long timeout, TimeUnit unit) 
	throws IOException, InterruptedException
	{
		wrappedStream.close();
		closeSiphoningChannel();
		
		logger.debug("synchronous close, awaiting termination.");
		executor.awaitTermination(timeout, unit);
		logger.debug("synchronous close complete.");
	}
	
	@Override
	public int read() 
	throws IOException
	{
		int value = wrappedStream.read();
		
		if(value != -1)
		{
			if(markBuffer != null)
			{
				markBuffer.write((byte)value);
				if(markBuffer.isOverfilled())
					queueMarkBuffer();
				//logger.debug("Read 1 byte and written to mark buffer.");
			}
			else
			{
				ByteBuffer buffy = ByteBuffer.allocateDirect(1);
				buffy.put((byte)value);
				queueBuffer(buffy);
				//logger.debug("Read 1 byte and queued to siphoned stream.");
			}
		}
		
		return value;
	}

	@Override
	public int read(byte[] b, int off, int len) 
	throws IOException
	{
		int bytesRead = wrappedStream.read(b, off, len);
		
		if(bytesRead > 0)
		{
			if(markBuffer != null)
			{
				markBuffer.write(b, off, bytesRead);
				if(markBuffer.isOverfilled())
					queueMarkBuffer();
				//logger.debug("Read " + bytesRead + " bytes and written to mark buffer.");
			}
			else
			{
				ByteBuffer buffy = ByteBuffer.allocateDirect(bytesRead);
				buffy.put(b, off, bytesRead);
				queueBuffer(buffy);
				//logger.debug("Read " + bytesRead + " bytes and queued to siphon stream.");
			}
		}
		
		return bytesRead;
	}

	@Override
	public int read(byte[] b) throws IOException
	{
		int bytesRead = wrappedStream.read(b);
		if(bytesRead > 0)
		{
			if(markBuffer != null)
			{
				markBuffer.write(b, 0, bytesRead);
				if(markBuffer.isOverfilled())
					queueMarkBuffer();
				//logger.debug("Read " + bytesRead + " bytes and written to mark buffer.");
			}
			else
			{
				ByteBuffer buffy = ByteBuffer.allocateDirect(bytesRead);
				buffy.put(b, 0, bytesRead);
				queueBuffer(buffy);
				//logger.debug("Read " + bytesRead + " bytes and queued to siphon stream.");
			}
		}
		
		return bytesRead;
	}

	@Override
	public long skip(long n) 
	throws IOException
	{
		long skipped = wrappedStream.skip(n);
		return skipped;
	}
	
	// ==========================================
	// Support for mark() and reset()
	// ==========================================
	@Override
	public boolean markSupported()
	{
		return wrappedStream.markSupported();
	}

	private MarkBuffer markBuffer = null;
	@Override
	public synchronized void mark(int readlimit)
	{
		logger.debug("mark set.");
		if(closeTaskQueued)
			return;
		
		// if the mark buffer exists, queue its contents
		if(markBuffer != null)
			try{queueMarkBuffer();}
			catch (IOException x){x.printStackTrace();}
		
		wrappedStream.mark(readlimit);
		markBuffer = new MarkBuffer(readlimit);
		return;
	}

	@Override
	public synchronized void reset() 
	throws IOException
	{
		wrappedStream.reset();
		markBuffer = null;
		return;
	}

	/**
	 * @throws IOException 
	 * @throws IOException 
	 * 
	 */
	private void queueMarkBuffer() 
	throws IOException 
	{
		if(markBuffer == null)
			return;
		
		ByteBuffer buffy = ByteBuffer.allocateDirect(markBuffer.getMarkBufferBytesWritten());
		buffy.put(markBuffer.getMarkBuffer(), 0, markBuffer.getMarkBufferBytesWritten());
		queueBuffer(buffy);
		
		if(markBuffer.isOverfilled())
		{
			ByteBuffer remainderBuffy = ByteBuffer.allocate(markBuffer.getRemainderBufferBytesWritten());
			remainderBuffy.put(markBuffer.getRemainderBuffer(), 0, markBuffer.getRemainderBufferBytesWritten());
			queueBuffer(remainderBuffy);
		}
		
		markBuffer = null;
	}

	/**
	 * A class that is used to buffer bytes when the mark() and reset() methods
	 * are used.  This class is only needed from the time a mark() is called until
	 * either the reset() is called or the mark buffer is over filled 
	 * (isOverfilled returns true). 
	 */
	class MarkBuffer
	{
		private byte[] markBuffer;
		private int markBufferBytesWritten;
		private byte[] remainderBuffer;
		private int remainderBufferBytesWritten;
		
		MarkBuffer(int readLimit)
		{
			markBuffer = new byte[readLimit];
			this.markBufferBytesWritten = 0;
			this.remainderBuffer = null;
			this.remainderBufferBytesWritten = 0;
		}

		public int getBytesWritten()
		{
			return getMarkBufferBytesWritten() + getRemainderBufferBytesWritten();
		}
		
		public int getMarkBufferBytesWritten()
		{
			return this.markBufferBytesWritten;
		}
		
		public int getRemainderBufferBytesWritten()
		{
			return this.remainderBufferBytesWritten;
		}
		
		// returns bytes written to the mark buffer
		public int write(byte value) 
		throws IOException
		{
			return write(new byte[]{value});
		}
		
		public int write(byte[] values)
		{
			return write(values, 0, values.length);
		}
		
		public int write(byte[] values, int offset, int length)
		{
			// how much space is available in the mark buffer
			int markBufferBytesAvailable = markBuffer.length - markBufferBytesWritten;
			// how many bytes to write to the mark buffer
			int markBufferBytesToWrite = Math.min(markBufferBytesAvailable, length);
			// how many bytes left over to write to the remainder buffer
			int remainderBufferBytesToWrite = length - markBufferBytesToWrite;
			
			// write to the mark buffer
			System.arraycopy(values, offset, markBuffer, markBufferBytesWritten, markBufferBytesToWrite);
			markBufferBytesWritten+=markBufferBytesToWrite;
			
			if(remainderBufferBytesToWrite > 0)
			{
				remainderBuffer = new byte[remainderBufferBytesToWrite];
				System.arraycopy(values, offset+markBufferBytesToWrite, remainderBuffer, 0, remainderBufferBytesToWrite);
				remainderBufferBytesWritten=remainderBufferBytesToWrite;
			}
			
			return markBufferBytesToWrite + remainderBufferBytesToWrite;
		}
		
		public boolean isOverfilled()
		{
			return remainderBufferBytesWritten > 0;
		}
		
		public byte[] getMarkBuffer()
		{
			return markBuffer;
		}
		
		public byte[] getRemainderBuffer()
		{
			return remainderBuffer;
		}
	}
	
	// ==================================================
	// all the stuff that writes to the siphoning channel
	// ==================================================
	
	private static ThreadGroup siphonStreamThreadGroup = new ThreadGroup("SiphonStream");
	static ThreadGroup getSiphonStreamThreadGroup(){return siphonStreamThreadGroup;}
	private static ThreadFactory siphonStreamThreadFactory = new ThreadFactory()
	{
		private long serialNumber = Long.MIN_VALUE;
		@Override
		public Thread newThread(Runnable r)
		{
			return new Thread(getSiphonStreamThreadGroup(), r, getNextName());
		}
		private synchronized String getNextName()
		{
			if(serialNumber >= Long.MAX_VALUE)
				serialNumber = Long.MIN_VALUE;
			return "SiphonStream-" + Long.toHexString(serialNumber++);
		}
	};
	
	// DO NOT make this anything other than a single thread executor
	// else bytes will be written out of order and much synchronization
	// code will have to be written.
	//private java.util.concurrent.
	private ExecutorService executor = Executors.newSingleThreadExecutor(siphonStreamThreadFactory);
	private List<Throwable> siphonChannelExceptions = 
		Collections.synchronizedList( new ArrayList<Throwable>() );
	private boolean closeTaskQueued = false;
	
	/**
	 * @param buffy
	 */
	private void queueBuffer(ByteBuffer buffy)
	throws IOException
	{
		if(closeTaskQueued)
			throw new IOException("Unable to queue additional buffers to write to siphoning channel because close task is queued.");
		executor.execute( new SiphoningChannelWriteBufferTask(buffy, siphoningChannel) );
	}
	
	/**
	 * To close the siphoning channel, create a close channel task.
	 * Once this is done, no more queues can be added, else a 
	 * IOException will be thrown.
	 */
	private void closeSiphoningChannel()
	{
		if(closeTaskQueued)
			return;
		
		try{queueMarkBuffer();}
		catch (IOException x){x.printStackTrace();}
		
		executor.execute( new SiphoningChannelCloseTask(siphoningChannel) );
		closeTaskQueued = true;
		executor.shutdown();
	}
	
	public boolean isCompletedClose()
	{
		return executor.isTerminated();
	}
	
	public boolean isAnySiphonChannelExceptions()
	{
		return this.siphonChannelExceptions != null && this.siphonChannelExceptions.size() > 0;
	}
	
	public List<Throwable> getSiphonChannelExceptions()
	{
		return this.siphonChannelExceptions;
	}
	
	/**
	 * 
	 * @author vhaiswbeckec
	 *
	 */
	class SiphoningChannelWriteBufferTask
	implements Runnable
	{
		private ByteBuffer buffy;
		private WritableByteChannel siphoningChannel;
		
		/**
		 * @param buffy
		 * @param siphoningChannel
		 */
		public SiphoningChannelWriteBufferTask(ByteBuffer buffy, WritableByteChannel siphoningChannel)
		{
			this.buffy = buffy;
			this.siphoningChannel = siphoningChannel;
		}

		@Override
		public void run()
		{
			try
			{
				buffy.flip();
				//logger.debug("Writing " + buffy.remaining() + " bytes to siphoning stream.");
				while(buffy.remaining() > 0)
					siphoningChannel.write(buffy);
			}
			catch (IOException x)
			{
				getSiphonChannelExceptions().add(x);
			}
		}
	}
	
	/**
	 *
	 */
	class SiphoningChannelCloseTask
	implements Runnable
	{
		private WritableByteChannel siphoningChannel;

		/**
		 * @param siphoningChannel
		 */
		public SiphoningChannelCloseTask(WritableByteChannel siphoningChannel)
		{
			this.siphoningChannel = siphoningChannel;
		}

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run()
		{
			try
			{
				getLogger().debug("Closing siphoning stream.");
				this.siphoningChannel.close();
			}
			catch (IOException x)
			{
				getSiphonChannelExceptions().add(x);
			}
			finally
			{
				getLogger().debug("Siphoning stream closed, " + getSiphonChannelExceptions().size() + " exceptions occured");
				if(getSiphonChannelExceptions().size() > 0)
				{
					getLogger().warn("Siphoning stream exceptions");
					for(Throwable t : getSiphonChannelExceptions())
						getLogger().warn(t.getClass().getName() + "[" + t.getMessage() + "]");
				}
			}
		}
		
	}
}