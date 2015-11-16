/**
 * License:
 * Copyright (c) 2009 Christopher Schultz
 * Free to use for any purpose for no fee. No guarantees. Credits and shout-outs are appreciated.
 * 
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Oct 26, 2010
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

package gov.va.med.imaging.httpFilterRecorder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.ServletInputStream;

class ServletTeeInputStream
extends ServletInputStream
{
	private InputStream _in;
	private OutputStream _out;
	private boolean _close;
	private boolean _isClosed = false;

	public ServletTeeInputStream(InputStream in, OutputStream out, boolean close)
	{
		_in = in;
		_out = out;
		_close = close;
	}

	@Override
	public int available() throws IOException
	{
		return _in.available();
	}

	@Override
	public void close() throws IOException
	{
		try
		{
			_in.close();
		}
		finally
		{
			if (_close)
				_out.close();
		}

		_isClosed = true;
	}

	@Override
	public void mark(int readlimit)
	{
		_in.mark(readlimit);
	}

	@Override
	public boolean markSupported()
	{
		return _in.markSupported();
	}

	@Override
	public void reset() throws IOException
	{
		_in.reset();
	}

	@Override
	public int read() throws IOException
	{
		int ch = _in.read();

		if (-1 != ch)
			_out.write(ch);

		return ch;
	}

	@Override
	public int read(byte[] b) throws IOException
	{
		return this.read(b, 0, b.length);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException
	{
		int n = _in.read(b, off, len);

		if (-1 != n)
			_out.write(b, off, n);

		return n;
	}

	@Override
	public long skip(long want) throws IOException
	{
		long total = 0;
		byte[] buffer = new byte[1024];

		while (0 < want)
		{
			int n;

			int wantThisTime = (1024 < want ? 1024 : (int) want);

			if (-1 != (n = _in.read(buffer, 0, wantThisTime)))
			{
				_out.write(buffer, 0, n);

				total += n;
				want -= n;
			}
		}

		return total;
	}

	public boolean isClosed()
	{
		return _isClosed;
	}
}