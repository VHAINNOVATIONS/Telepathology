/**
 * License:
 * Copyright (c) 2009 Christopher Schultz
 * Free to use for any purpose for no fee. No guarantees. Credits and shout-outs are appreciated.
 */
package gov.va.med.imaging.httpFilterRecorder;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HttpConversationRecorderFilter
 * 
 * Dumps the complete HTTP request to stdout after the request has been
 * processed.
 * 
 * This should be the first filter in your filter chain so you can capture all
 * of the request body.
 * 
 * @author Chris Schultz (chris@xxxxxxxxxxxxxxxxxxxxxx)
 * @version 2009-02-20
 */
public class HttpConversationRecorderFilter
implements Filter
{
	private static final String DEFAULT_CHARSET = "ISO-8859-1";
	private static final String DEFAULT_URI_CHARSET = "UTF-8";

	private String defaultCharset = DEFAULT_CHARSET;
	private String uriCharset = DEFAULT_URI_CHARSET;

	private File baseDirectory = null;
	private String fileExtension = ".txt";
	private DateFormat df = new SimpleDateFormat("yyyyMMDD-hhmmss-SSSS");
	
	/**
	 * 
	 */
	public void init(FilterConfig config)
	throws ServletException
	{
		String charset = config.getInitParameter("default-charset");

		if (null != charset)
			defaultCharset = charset;

		charset = config.getInitParameter("default-uri-charset");

		if (null != charset)
			uriCharset = charset;
		
		String baseDirectoryName = config.getInitParameter("base-directory");
		if(baseDirectoryName != null)
		{
			this.baseDirectory = new File(baseDirectoryName);
			if( ! this.baseDirectory.exists() )
				this.baseDirectory.mkdirs();
			
			if( ! this.baseDirectory.isDirectory() )
			{
				baseDirectory = null;
				throw new ServletException("Base directory '" + baseDirectoryName + "' is not a directory. Logging will be sent to System.out");
			}
		}
	}

	/**
	 * @return the defaultCharset
	 */
	public String getDefaultCharset()
	{
		return this.defaultCharset;
	}

	/**
	 * @return the uriCharset
	 */
	public String getUriCharset()
	{
		return this.uriCharset;
	}

	public void destroy()
	{
	}

	/**
	 * 
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
	throws IOException, ServletException
	{
		if( request instanceof HttpServletRequest && response instanceof HttpServletResponse )
		{
			Throwable ex = null;
			HttpRequestRecorderWrapper req;

			req = new HttpRequestRecorderWrapper( (HttpServletRequest)request, getDefaultCharset(), getUriCharset() );

			try
			{
				chain.doFilter(req, response);
			}
			catch (Exception e)
			{
				// Save the exception for later. We still want to record
				// the request and possibly part of the response.
				//
				// Don't worry about catching java.lang.Error.
				ex = e;
			}

			// open a file if we have a base directory
			PrintStream out = null;
			if(baseDirectory != null)
			{
				File outFile = new File( df.format(new Date()) + Thread.currentThread().getName() + fileExtension );
				out = new PrintStream(outFile);
			}
			else
				out = System.out;
			
			req.dumpRequest(out);
			req.free();
			
			// close the stream if we are writing to a file
			if(baseDirectory != null)
				out.close();
			
			// Re-throw any exception that might have occurred
			if (null != ex)
			{
				if (ex instanceof ServletException)
					throw (ServletException) ex;
				if (ex instanceof IOException)
					throw (IOException) ex;
				if (ex instanceof RuntimeException)
					throw (RuntimeException) ex;
				if (ex instanceof Error)
					throw (Error) ex;

				// Should never get here
				throw new ServletException("Unexpected exception", ex);
			}
		}
		else
			chain.doFilter(request, response);
	}
}