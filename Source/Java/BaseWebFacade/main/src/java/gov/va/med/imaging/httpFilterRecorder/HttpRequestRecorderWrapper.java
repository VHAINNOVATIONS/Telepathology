package gov.va.med.imaging.httpFilterRecorder;

/**
 * License:
 * Copyright (c) 2009 Christopher Schultz
 * Free to use for any purpose for no fee. No guarantees. Credits and shout-outs are appreciated.
 */

import java.io.*;
import java.util.Enumeration;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * 
 */
class HttpRequestRecorderWrapper
extends HttpServletRequestWrapper
{
	private ServletTeeInputStream inputTeeStream;
	private ByteArrayOutputStream inputStreamBuffer;

	private BufferedTeeReader inputTeeReader;
	private StringWriter inputReaderBuffer;

	private boolean usingReader;
	private final String defaultCharset;
	private final String uriCharset;

	public HttpRequestRecorderWrapper(HttpServletRequest request, String defaultCharset, String uriCharset)
	{
		super(request);
		this.defaultCharset = defaultCharset;
		this.uriCharset = uriCharset;
	}

	/**
	 * @return the uriCharset
	 */
	public String getUriCharset()
	{
		return this.uriCharset;
	}

	/**
	 * @return the defaultCharset
	 */
	public String getDefaultCharset()
	{
		return this.defaultCharset;
	}

	/**
	 * @param usingReader the usingReader to set
	 */
	public void setUsingReader(boolean usingReader)
	{
		this.usingReader = usingReader;
	}

	public boolean isUsingReader()
	{
		return usingReader;
	}

	@Override
	public synchronized BufferedReader getReader() 
	throws IOException
	{
		if (null == inputTeeReader)
		{
			BufferedReader in = super.getReader();
			inputReaderBuffer = new StringWriter();
			inputTeeReader = new BufferedTeeReader(in, inputReaderBuffer, true);
			usingReader = true;
		}

		return inputTeeReader;
	}

	@Override
	public ServletInputStream getInputStream() 
	throws IOException
	{
		if (null == inputTeeStream)
		{
			ServletInputStream in = super.getInputStream();
			inputStreamBuffer = new ByteArrayOutputStream();
			inputTeeStream = new ServletTeeInputStream(in, inputStreamBuffer, true);
		}

		return inputTeeStream;
	}

	public byte[] getRequestBody()
	{
		if (isUsingReader())
		{
			String charset = getCharacterEncoding();

			if (null == charset)
				charset = defaultCharset;

			try
			{
				return inputReaderBuffer.toString().getBytes(charset);
			}
			catch (UnsupportedEncodingException uee)
			{
				throw new InternalError("Unrecognized character set: " + charset);
			}
		}
		else
		{
			return inputStreamBuffer.toByteArray();
		}
	}

	public void free()
	{
		inputTeeStream = null;
		inputTeeReader = null;

		if (null != inputStreamBuffer)
		{
			inputStreamBuffer.reset(); // empty buffer
			inputStreamBuffer = null;
		}

		if (null != inputReaderBuffer)
		{
			inputReaderBuffer.getBuffer().setLength(0); // empty buffer
			inputReaderBuffer = null;
		}
	}

	public void dumpRequest(PrintStream out) 
	throws IOException
	{
		out.println("======> dumping HTTP request <=============");

		//
		// Wow, the first line of an HTTP request is tough to reconstruct
		//
		out.print(getMethod());
		out.print(" ");
		out.print(getRequestURI());

		String query = getQueryString();
		if (null != query)
		{
			out.print("?");
			out.print(query);
		}

		String protocol = getProtocol();
		if (null != protocol)
		{
			out.print(" ");
			out.print(protocol);
		}

		out.println();

		//
		// Now, dump all the headers
		//
		for (Enumeration i = getHeaderNames(); i.hasMoreElements();)
		{
			String headerName = (String) i.nextElement();

			out.print(headerName);
			out.print(": ");

			for (Enumeration j = getHeaders(headerName); j.hasMoreElements();)
			{
				out.print((String) j.nextElement());

				if (j.hasMoreElements())
					out.print(",");
			}

			out.println();
		}

		// Print the end-of-header newline
		out.println();

		//
		// If Content-Type is application/x-www-form-urlencoded
		// then we have to reconstruct the parameters from the parameter
		// map.
		//

		// Trigger the fetching of request parameters
		getParameter("");

		// See Servlet Specification SRV.3.1.1 for details.
		if ("POST".equals(getMethod()) && "application/x-www-form-urlencoded".equals(getContentType()))
		{
			// In this case, the servlet container is supposed to
			// consume the request body and parse it as POST form data.
			//
			// We will need to reconstruct the POST string using all
			// parameters read plus the query string to figure out which
			// came from where.

			String postQueryString = RequestUtils.getPostQueryString(this, uriCharset);

			out.print(postQueryString);
		}
		else
		{
			//
			// Consume any remaining request body
			//
			if (isUsingReader())
			{
				int leftover = 0;

				// Stealthily avoid reading from a closed reader
				if (!inputTeeReader.isClosed())
				{
					try
					{
						while (-1 != inputTeeReader.read())
							leftover++;

						inputTeeReader.close();
					}
					catch (IOException ioe)
					{
						System.err.println("WARN: reader already closed?");
					}
				}

				if (0 < leftover)
					System.out.println("====> Filter read " + leftover
						+ "unread characters from the request reader");
			}
			else
			{
				int leftover = 0;

				// Stealthily avoid reading from a closed reader
				if (!inputTeeStream.isClosed())
				{
					try
					{
						while (-1 != inputTeeStream.read())
							leftover++;

						inputTeeStream.close();
					}
					catch (IOException ioe)
					{
						System.err.println("WARN: stream already closed? " + ioe);
					}
				}

				if (0 < leftover)
					System.out.println("====> Filter read " + leftover + "unread bytes from the request stream");
			}

			//
			// Dump the request body
			//
			byte[] body = getRequestBody();

			String charset = getCharacterEncoding();
			if (null == charset)
				charset = defaultCharset;

			if (null != body && 0 < body.length)
				out.write(body);
		}

		out.flush();
	}
}