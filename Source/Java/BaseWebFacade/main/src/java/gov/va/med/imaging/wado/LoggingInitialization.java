/*
 * Created on Jul 8, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package gov.va.med.imaging.wado;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.InputStream;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.*;

/**
 * @author Chris Beckey
 *
 * @web.servlet 
 *   name="LoggingInitializationServlet"
 *   display-name="Logging Initialization Servlet"
 *   description="Use Level parameter to set logging level (DEBUG, INFO)"
 *   load-on-startup="1"
 * 
 * @web.servlet-mapping 
 *   url-pattern="/LoggingInitialization"
 * 
 * @web.servlet-init-param 
 *   name = "defaultLoggingLevel"
 *   value = "DEBUG"
 *   description = "Must be one of (DEBUG,INFO)" 
 */
public class LoggingInitialization 
extends HttpServlet
{
	public final static String LevelParameterName = "LEVEL";
	
	/**
	 * Constructor of the object.
	 */
	public LoggingInitialization()
	{
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy()
	{
		super.destroy(); // Just puts "destroy" string in log
	}

	/**
	 * 
	 * @return
	 */
	private String getDefaultLoggingLevel()
	{
		String defaultLoggingLevel = getInitParameter("defaultLoggingLevel");
		
		if(defaultLoggingLevel == null)
			defaultLoggingLevel = "DEBUG";
		
		return defaultLoggingLevel;
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException
	{
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		out.println("  <BODY>");
		out.println("    Logging Configuration ... ");
		out.println("    ======= ============= === ");

		//streamLoggingProperties(response.getOutputStream());
		String myPath = 
			"http://" + request.getServerName() + ":" + request.getServerPort() + 
			request.getContextPath() + "/" + request.getServletPath();
		out.println("    ======= ============= === ");
		out.println("<a href=\"" + myPath + "?LEVEL=DEBUG\" method=\"POST\">DEBUG</a><br/>");
		out.println("<a href=\"" + myPath + "?LEVEL=INFO\" method=\"POST\">INFO</a><br/>");
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
	}

	/**
	 * The doPost method sets the logging configuration.  It can take either a single
	 * HTTP parameter 'LEVEL' which maps to a configuration file name, or a 
	 * properties set in the contnt of the POST request.
	 *
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException
	{
		String loggingLevel = request.getParameter(LevelParameterName);
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		if(loggingLevel != null)
		{
			setLoggingLevelByName( loggingLevel );
			
			out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
			out.println("<HTML>");
			out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
			out.println("  <BODY>");
			out.print("    Logging Level set to" + loggingLevel);
			out.println("  </BODY>");
			out.println("</HTML>");
		}
		else
		{
			this.setLoggingProperties( request.getInputStream() );
			
			out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
			out.println("<HTML>");
			out.println("  <HEAD><TITLE>Logging Initialization</TITLE></HEAD>");
			out.println("  <BODY>");
			out.println("    Logging Configuration has been updated.");
			out.println("  </BODY>");
			out.println("</HTML>");
		}
		
		out.flush();
	}

	/**
	 * Returns information about the servlet, such as 
	 * author, version, and copyright. 
	 *
	 * @return String information about this servlet
	 */
	public String getServletInfo()
	{
		return "Sets the logging configuration for the containing web application";
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occure
	 */
	public void init() 
	throws ServletException
	{
		//setLoggingLevel(getDefaultLoggingLevel());
	}

	/**
	 * 
	 * @param propertiesStream
	 * @throws IOException
	 */
	private void setLoggingProperties(InputStream propertiesStream)
	throws IOException
	{
		Properties configuration = new Properties();
		configuration.load(propertiesStream);
		
		PropertyConfigurator.configure(configuration);
	}
	
	/**
	 * 
	 * @param level
	 */
	private void setLoggingLevelByName(String level)
	{
		if(level == null)
			level = getDefaultLoggingLevel();
		
		PropertyConfigurator.configure(
			getServletContext().getRealPath("/") + "/" + level + ".lcf"
		);
	}
	
	/**
	 * 
	 * @param responseStream
	 */
	private void streamLoggingProperties(OutputStream responseStream)
	throws IOException
	{
		Logger logger = Logger.getRootLogger();
		ResourceBundle resources = logger.getResourceBundle();
		
		responseStream.write( resources.toString().getBytes() );
		
	}

}
