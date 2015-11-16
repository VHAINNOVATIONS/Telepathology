package gov.va.med;

import java.io.IOException;
import java.io.InputStream;

/**
 * 
 */
public class NetworkSimulatingInputStream
extends InputStream
{
	public enum DELAY_MODE
	{
		NONE(0, 0),									// no delays 
		MEDIUM_LATENCY(30000, 0), 					// long (30 second) delay on the first read
		HIGH_LATENCY(125000, 0),					// really long (125 second) delay on the first read
		LOW_BANDWIDTH(0, 10), 						// delay (10 ms) on every read
		MEDIUM_LATENCY_LOW_BANDWIDTH(30000, 10);	// kinda' obvious
		
		private final long latency;
		private final long readDelay;
		
		DELAY_MODE(long latency, long readDelay){this.latency=latency; this.readDelay=readDelay;}
		long getLatency(){return this.latency;}
		long getReadDelay(){return this.readDelay;}
	}
	
	public enum EXCEPTION_MODE
	{
		RELIABLE(0.0),		// no exceptions thrown 
		ONE_PERCENT(0.01), 	// throw an ioException 1% of the time
		TEN_PERCENT(0.1), 	// throw an ioException 10% of the time
		FIFTY_PERCENT(0.5), 	// throw an ioException 10% of the time
		ALWAYS(1.0);		// throw an IOException on every call
		
		private final double exceptionProbability;
		EXCEPTION_MODE(double exceptionProbability){this.exceptionProbability = exceptionProbability;}
		double getExceptionProbability(){return this.exceptionProbability;}
	}
	
	private final InputStream wrappedStream;
	private final DELAY_MODE delayMode;
	private final EXCEPTION_MODE exceptionMode;
	private boolean firstByteRead = false;
	
	public NetworkSimulatingInputStream(
		InputStream wrappedStream, 
		DELAY_MODE delayMode, 
		EXCEPTION_MODE exceptionMode)
	{
		this.wrappedStream = wrappedStream;
		this.delayMode = delayMode;
		this.exceptionMode = exceptionMode;
	}
	
	/**
	 * 
	 * @return
	 */
	private boolean shouldThrowException()
	{
		return Math.random() < this.exceptionMode.getExceptionProbability();
	}
	
	private long getLatency()
	{
		return this.delayMode.getLatency();
	}
	
	private long getReadDelay()
	{
		return this.delayMode.getReadDelay();
	}
	
	@Override
	public int available() 
	throws IOException
	{
		if(shouldThrowException())
			throw new IOException("Artificially generated IOException");
		
		return wrappedStream.available();
	}
	
	@Override
	public void close() 
	throws IOException
	{
		if(shouldThrowException())
			throw new IOException("Artificially generated IOException");
		wrappedStream.close();
	}
	
	@Override
	public synchronized void mark(int readlimit)
	{
		wrappedStream.mark(readlimit);
	}
	
	@Override
	public boolean markSupported()
	{
		return wrappedStream.markSupported();
	}
	
	@Override
	public int read() 
	throws IOException
	{
		if(shouldThrowException())
			throw new IOException("Artificially generated IOException");
		
		int result = wrappedStream.read();
		if(firstByteRead)
			try{Thread.sleep(getReadDelay());}catch(InterruptedException iX){}
		else
			try{Thread.sleep(getLatency());}catch(InterruptedException iX){}
			
		firstByteRead = true;
		return result;
	}
	
	@Override
	public int read(byte[] b, int off, int len) 
	throws IOException
	{
		if(shouldThrowException())
			throw new IOException("Artificially generated IOException");
		
		int result = wrappedStream.read(b, off, len);
		if(firstByteRead)
			try{Thread.sleep(result * getReadDelay());}catch(InterruptedException iX){}
		else
			try{Thread.sleep(getLatency());}catch(InterruptedException iX){}

		firstByteRead = true;
		return result;
	}
	
	@Override
	public int read(byte[] b) 
	throws IOException
	{
		if(shouldThrowException())
			throw new IOException("Artificially generated IOException");
		
		int result = wrappedStream.read(b);
		if(firstByteRead)
			try{Thread.sleep(result * getReadDelay());}catch(InterruptedException iX){}
		else
			try{Thread.sleep(getLatency());}catch(InterruptedException iX){}

		firstByteRead = true;
		return result;
	}
	
	@Override
	public synchronized void reset() 
	throws IOException
	{
		if(shouldThrowException())
			throw new IOException("Artificially generated IOException");
		
		wrappedStream.reset();
	}
	
	@Override
	public long skip(long n) 
	throws IOException
	{
		if(shouldThrowException())
			throw new IOException("Artificially generated IOException");
		
		return wrappedStream.skip(n);
	}
}