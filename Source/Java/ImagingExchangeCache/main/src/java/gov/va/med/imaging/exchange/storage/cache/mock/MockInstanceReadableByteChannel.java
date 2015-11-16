/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Oct 14, 2008
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author VHAISWBECKEC
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */
package gov.va.med.imaging.exchange.storage.cache.mock;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.Checksum;

import gov.va.med.imaging.storage.cache.InstanceReadableByteChannel;

/**
 * @author VHAISWBECKEC
 *
 */
public class MockInstanceReadableByteChannel 
implements InstanceReadableByteChannel
{
	private boolean open = true;
	private Checksum checksum;
	private int bytesRead = 0;
	private final static int MAX_BYTES_READ = 42;
	private StackTraceElement[] instantiatingStackTrace = null;
	
	MockInstanceReadableByteChannel()
    {
	    super();
		open = true;
		checksum = new MockChecksum();
		
		// trace the channel instantiation so we can tattle-tale later if its not closed
		instantiatingStackTrace = Thread.currentThread().getStackTrace();
    }

	/**
	 * @see gov.va.med.imaging.storage.cache.InstanceReadableByteChannel#error()
	 */
	@Override
	public void error() 
	throws IOException
	{

	}

	/**
	 * @see gov.va.med.imaging.storage.cache.InstanceReadableByteChannel#getChecksum()
	 */
	@Override
	public Checksum getChecksum()
	{
		return checksum;
	}

	/**
	 * @see java.nio.channels.ReadableByteChannel#read(java.nio.ByteBuffer)
	 */
	@Override
	public int read(ByteBuffer dst) 
	throws IOException
	{
		if(! isOpen())
			throw new java.io.IOException("Attempt to write to closed MockInstanceWritableByteChannel");
		
		int initialBytesRead = 0;
		for(; bytesRead < MAX_BYTES_READ; ++bytesRead)
			dst.put((byte)0x01);
		return bytesRead - initialBytesRead;
	}

	/**
	 * @see java.nio.channels.Channel#close()
	 */
	@Override
	public void close() throws IOException
	{
		this.open = false;
	}

	/**
	 * @see java.nio.channels.Channel#isOpen()
	 */
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
