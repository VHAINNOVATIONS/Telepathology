/**
 * 
 */
package gov.va.med.imaging;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * @author VHAISWBECKEC
 *
 */
public class TimerFilter
implements Filter
{
	private Logger logger = Logger.getLogger(this.getClass()); 
	public final static String outputFilename = "TimerFilter.csv";
	public final static String headerLine = "request-identifier,request-time,response-time,duration";
	private Writer writer;
	private ExecutorService executor;
	private String dateFormat = "yyyy-MM-dd:hh:mm:ss:SSSS";

	private Writer getWriter()
	{
		File outFile;
		boolean writeHeaderLine = false;
		
		File dir = new File(System.getenv("vixconfig"));
		dir.mkdirs();
		outFile = new File(dir, outputFilename);
		logger.debug("writing to '" + outFile.getAbsolutePath() + "'.");
		
		if(! outFile.exists())
			writeHeaderLine = true;
		
		OutputStreamWriter writer = null;
		
		try
		{
			writer = new OutputStreamWriter( new FileOutputStream(outFile, true) );
		} 
		catch (FileNotFoundException x)
		{
			writer = new OutputStreamWriter( System.out );
		}
		
		if(writeHeaderLine)
			try
			{
				writer.write(headerLine + "\n");
				writer.flush();
			} 
			catch (IOException x)
			{
				logger.error("Error writing header line to timer file", x);
			}
		
		return writer;
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig config) throws ServletException
	{
		writer = getWriter();
		executor = Executors.newSingleThreadExecutor(new ThreadFactory()
		{
			public Thread newThread(Runnable r)
			{
				Thread thread = new Thread(r);
				thread.setName("timer-filter-log-writer");
				//thread.setPriority(Thread.NORM_PRIORITY-1);
				
				return thread;
			}
			
		});
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
	throws IOException, ServletException
	{
		ThreadLocal<Long> requestTime = new ThreadLocal<Long>();
		requestTime.set( new Long(System.currentTimeMillis()) );
		
		chain.doFilter(request, response);
		
		String requestIdentifier = null;
		
		if(request instanceof HttpServletRequest)
		{
			HttpServletRequest httpRequest = (HttpServletRequest)request;
			String imageUrn = httpRequest.getParameter("imageURN"); // was imageUrn - DKB
			if (imageUrn != null) // image request
			{
				boolean vaImage = imageUrn.indexOf("vaimage") >= 0;
				boolean dodImage = imageUrn.indexOf("dodimage") >= 0;
				
				requestIdentifier = httpRequest.getRequestURI() + 
					(vaImage ? "-vaimage" : null) +
					(dodImage ? "-dodimage" : null);
			}
			else // web service request
			{
				requestIdentifier = request.getRemoteHost() + ":" + request.getRemotePort();				
			}
		}
		else // TODO: When is this ever being executed? DKB
		{
			requestIdentifier = request.getRemoteHost() + ":" + request.getRemotePort();
		}
		writeTimerLog(((Long)requestTime.get()).longValue(), System.currentTimeMillis(), requestIdentifier);
		
		requestTime.remove();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy()
	{
		try
		{
			try
			{
				executor.shutdown();
				executor.awaitTermination(10, TimeUnit.SECONDS);
			} 
			catch (InterruptedException x)
			{
				x.printStackTrace();
			}
			writer.flush();
			writer.close();
		} 
		catch (IOException x)
		{
			x.printStackTrace();
		}
	}

	private void writeTimerLog(long requestTime, long responseTime, String requestIdentifier)
	{
		logger.debug("writeTimerLog(" + requestTime + "," + responseTime + "," + requestIdentifier + ")");
		
		TimerLogEntry logEntry = new TimerLogEntry(requestTime, responseTime, requestIdentifier);
		
		TimerLogEntryTask task = new TimerLogEntryTask(logEntry);
		executor.submit(task);
	}
	
	class TimerLogEntry
	{
		private long requestTime;
		private long responseTime;
		private String requestIdentifier;
		
		TimerLogEntry(long requestTime, long responseTime, String requestIdentifier)
		{
			this.requestTime = requestTime;
			this.responseTime = responseTime;
			this.requestIdentifier = requestIdentifier;
		}

		public long getRequestTime()
		{
			return this.requestTime;
		}

		public long getResponseTime()
		{
			return this.responseTime;
		}

		public String getRequestIdentifier()
		{
			return this.requestIdentifier;
		}
	}
	
	class TimerLogEntryTask
	implements Runnable
	{
		private TimerLogEntry entry;
		TimerLogEntryTask(TimerLogEntry entry)
		{
			this.entry = entry;
		}
		
		public void run()
		{
			DateFormat df = new SimpleDateFormat(dateFormat);
			StringBuffer sb = new StringBuffer();
			sb.append(entry.getRequestIdentifier());
			sb.append(',');
			sb.append( df.format(entry.getRequestTime()) );
			sb.append(',');
			sb.append( df.format(entry.getResponseTime()) );
			sb.append(',');
			sb.append( entry.getResponseTime() - entry.getRequestTime() );
			
			System.out.println("Writing TimerLogEntry(" + sb.toString() + ")");
			
			try
			{
				writer.write(sb.toString());
				writer.write("\n");
				writer.flush();
			} 
			catch (IOException x)
			{
				x.printStackTrace();
			}
		}
	}
}
