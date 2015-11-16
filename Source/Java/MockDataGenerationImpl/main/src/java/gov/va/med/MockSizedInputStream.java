/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Apr 30, 2010
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author vhaiswbeckec
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

package gov.va.med;

import java.io.IOException;
import java.io.InputStream;

public class MockSizedInputStream
extends InputStream
{
	private final InputStream inStream;
	private final long size;
	private final String mediaType;
	
	public MockSizedInputStream(InputStream inStream, long size, String mediaType)
	{
		super();
		this.inStream = inStream;
		this.size = size;
		this.mediaType = mediaType;
	}

	protected InputStream getInStream()
	{
		return this.inStream;
	}

	protected long getSize()
	{
		return this.size;
	}

	protected String getMediaType()
	{
		return this.mediaType;
	}

	@Override
	public int available() throws IOException
	{
		return inStream.available();
	}

	@Override
	public void close() throws IOException
	{
		inStream.close();
	}

	@Override
	public synchronized void mark(int readlimit)
	{
		inStream.mark(readlimit);
	}

	@Override
	public boolean markSupported()
	{
		return inStream.markSupported();
	}

	@Override
	public int read() throws IOException
	{
		return inStream.read();
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException
	{
		return inStream.read(b, off, len);
	}

	@Override
	public int read(byte[] b) throws IOException
	{
		return inStream.read(b);
	}

	@Override
	public synchronized void reset() throws IOException
	{
		inStream.reset();
	}

	@Override
	public long skip(long n) throws IOException
	{
		return inStream.skip(n);
	}
}