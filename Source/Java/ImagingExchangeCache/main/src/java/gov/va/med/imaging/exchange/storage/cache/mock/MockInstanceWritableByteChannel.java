package gov.va.med.imaging.exchange.storage.cache.mock;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.Checksum;

import gov.va.med.imaging.storage.cache.InstanceWritableByteChannel;

public class MockInstanceWritableByteChannel 
implements InstanceWritableByteChannel
{
	private boolean open = true;
	private Checksum checksum;
	private StackTraceElement[] instantiatingStackTrace = null;
	
	MockInstanceWritableByteChannel()
	{
		open = true;
		checksum = new MockChecksum();
		
		// trace the channel instantiation so we can tattle-tale later if its not closed
		instantiatingStackTrace = Thread.currentThread().getStackTrace();
	}
	
	@Override
	public void error() 
	throws IOException
	{
	}

	@Override
	public Checksum getChecksum()
	{
		return checksum;
	}

	@Override
	public int write(ByteBuffer src) 
	throws IOException
	{
		if(! isOpen())
			throw new java.io.IOException("Attempt to write to closed MockInstanceWritableByteChannel");
		
		int availableBytes = src.limit() - src.position();
		src.position( src.limit() );
		return availableBytes;
	}

	@Override
	public void close() 
	throws IOException
	{
		open = false;
	}

	@Override
	public boolean isOpen()
	{
		return open;
	}

	@Override
	public long getLastAccessedTime()
	{
		return System.currentTimeMillis();
	}

	@Override
	public StackTraceElement[] getInstantiatingStackTrace() 
	{
		return this.instantiatingStackTrace;
	}

}
