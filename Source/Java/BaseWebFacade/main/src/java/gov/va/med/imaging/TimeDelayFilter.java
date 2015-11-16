package gov.va.med.imaging;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Logger;

public class TimeDelayFilter 
implements Filter
{
	private Logger logger = Logger.getLogger(this.getClass());
	private long preDelay = 0L;
	private long postDelay = 0L;
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
	throws IOException, ServletException
	{
		if(preDelay > 0)
		{
			try
			{
				logger.info("preDelay sleeping for [" + preDelay + "] ms");
				Thread.sleep(preDelay);
			} 
			catch (InterruptedException e)
			{
				logger.error("InterruptedException in TimeDelayFilter is a bad thing, remove TimeDelayFilter from the chain.", e);
				throw new ServletException(e);
			}
		}
		
		chain.doFilter(request, response);
		
		if(postDelay > 0)
		{
			try
			{
				logger.info("postDelay sleeping for [" + postDelay + "] ms");
				Thread.sleep(postDelay);
			} 
			catch (InterruptedException e)
			{
				logger.error("InterruptedException in TimeDelayFilter is a bad thing, remove TimeDelayFilter from the chain.", e);
				throw new ServletException(e);
			}
		}
	}

	public void init(FilterConfig config) 
	throws ServletException
	{
		String preDelayInitParameter = config.getInitParameter("predelay");
		String postDelayInitParameter = config.getInitParameter("postdelay");
		
		try
		{
			if(preDelayInitParameter != null)
				preDelay = Long.parseLong( preDelayInitParameter );
			if(postDelayInitParameter != null)
				postDelay = Long.parseLong( postDelayInitParameter );
			
			if(preDelay < 0L)
				throw new ServletException("TimeDelayFilter predelay parameter must be a positive integer or zero.");
			if(postDelay < 0L)
				throw new ServletException("TimeDelayFilter postdelay parameter must be a positive integer or zero.");
		} 
		catch (NumberFormatException e)
		{
			logger.error(e);
			throw new ServletException(e);
		}
		logger.info("TimeDelayFilter initialized with predelay=" + preDelay + "' and postdelay=" + postDelay);
	}

	public void destroy()
	{
	}

}
