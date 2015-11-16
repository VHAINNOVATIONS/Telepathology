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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;

public class BufferedTeeReader
extends BufferedReader
{
	private Writer _out;
	private boolean _close;
	private boolean _isClosed = false;

	// TODO: Allow this to be overridden
	private String _lineSeparator = System.getProperty("line.separator");

	public BufferedTeeReader(BufferedReader in, Writer out, boolean close)
	{
		super(in);

		_out = out;
		_close = close;
	}

	@Override
	public int read() throws IOException
	{
		int c = super.read();

		if (-1 != c)
			_out.write(c);

		return c;
	}

	@Override
	public int read(char[] buffer) throws IOException
	{
		// Calling super.read(buffer) results in
		// this.read(buffer, 0, buffer.length) being called,
		// which will double the amount of output generated.

		// int n = super.read(buffer);
		//
		// if(-1 != n)
		// _out.write(buffer, 0, n);
		//
		// return n;

		return this.read(buffer, 0, buffer.length);
	}

	@Override
	public int read(char[] buffer, int off, int len) throws IOException
	{
		int n = super.read(buffer, off, len);

		if (-1 != n)
			_out.write(buffer, off, n);

		return n;
	}

	@Override
	public int read(CharBuffer buffer) throws IOException
	{
		int n = super.read(buffer);

		if (-1 != n)
			_out.append(buffer, 0, n);

		return n;
	}

	@Override
	public String readLine() throws IOException
	{
		String line = super.readLine();

		if (null != line)
		{
			_out.write(line);
			_out.write(_lineSeparator);
		}

		return line;
	}

	@Override
	public long skip(long want) throws IOException
	{
		long total = 0;
		char[] buffer = new char[1024];

		while (0 < want)
		{
			int n;

			int wantThisTime = (1024 < want ? 1024 : (int) want);

			if (-1 != (n = super.read(buffer, 0, wantThisTime)))
			{
				_out.write(buffer, 0, n);

				total += n;
				want -= n;
			}
		}

		return total;
	}

	@Override
	public boolean ready() throws IOException
	{
		return super.ready();
	}

	// TODO: return false?
	@Override
	public boolean markSupported()
	{
		return super.markSupported();
	}

	@Override
	public void mark(int readAheadLimit) throws IOException
	{
		super.mark(readAheadLimit);
	}

	@Override
	public void reset() throws IOException
	{
		super.reset();
	}

	@Override
	public void close() throws IOException
	{
		try
		{
			super.close();
		}
		finally
		{
			if (_close)
				_out.close();
		}

		_isClosed = true;
	}

	public boolean isClosed()
	{
		return _isClosed;
	}
}