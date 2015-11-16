/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Oct 6, 2008
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
package gov.va.med.imaging.exchange.business;

import gov.va.med.imaging.BaseWebFacadeRouter;
import gov.va.med.imaging.core.FacadeRouterUtility;
import gov.va.med.imaging.core.interfaces.router.CommandFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * An abstract servlet that provides common web application functionality
 * for Imaging servlets such as:
 * 1.) getting router references
 * 2.) getting logger references
 * 3.) getting spring application context references
 * 
 * @author VHAISWBECKEC
 *
 */
public class AbstractBaseServlet 
extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	public static final String defaultCoreRouterBeanName = "coreRouter";
	public static final String defaultCommandFactoryBeanName = "commandFactory";
	
	private WebApplicationContext webApplicationContext;
	private CommandFactory commandFactory;
	private BaseWebFacadeRouter router;
	private String coreRouterBeanName = defaultCoreRouterBeanName;
	private String commandFactoryBeanName = defaultCommandFactoryBeanName;
	private Logger logger = Logger.getLogger(this.getClass());

	/**
	 * 
	 */
	public AbstractBaseServlet()
	{
	}

	/**
	 * 
	 * @return
	 */
	protected synchronized WebApplicationContext getWebApplicationContext()
	throws ServletException
	{
		if(webApplicationContext == null)
		{
			ServletContext servletContext = this.getServletContext();
			webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
			
			if(webApplicationContext == null)
			{
				String msg = 
					"Unable to acquire a reference to the web application context.\n" +
					"A Spring Web Application Context must be provided for this servlet and its derivatives.";
				logger.error(msg);
				throw new ServletException(msg);
			}
		}
		return webApplicationContext;
	}
	
	/**
	 * 
	 * @return
	 */
	protected synchronized BaseWebFacadeRouter getRouter()
	throws ServletException
	{
    	BaseWebFacadeRouter router;
		try
		{
			router = FacadeRouterUtility.getFacadeRouter(BaseWebFacadeRouter.class);
		} 
		catch (Exception x)
		{
			logger.error("Exception getting the facade router implementation.", x);
			return null;
		}
		
		return router;
	}

	protected synchronized CommandFactory getCommandFactory()
	throws ServletException
	{
		if(commandFactory == null)
		{
			try
            {
				commandFactory = (CommandFactory)getWebApplicationContext().getBean(commandFactoryBeanName);
            } 
			catch (NoSuchBeanDefinitionException nsbdX)
            {
				String msg = 
					"Unable to acquire a reference to the CommandFactory implementation.\n" +
					"The Spring Web Application Context must provide a bean named '" + commandFactoryBeanName + 
					"' a reference to a CommandFactory implementation and it does not.";
				logger.error(msg);
				throw new ServletException(msg);
            }
			catch (BeansException e)
            {
				String msg = 
					"Unable to acquire a reference to the CommandFactory implementation.\n" +
					"The Spring Web Application Context provides a bean named '" + commandFactoryBeanName + 
					"' but the bean could not be instantiated.";
				logger.error(msg);
				throw new ServletException(msg);
            }
			catch (ClassCastException ccX)
            {
				String msg = 
					"Unable to acquire a reference to the CommandFactory implementation.\n" +
					"The Spring Web Application Context provides a bean named '" + commandFactoryBeanName + 
					"' but the referenced object does not implement CommandFactory.";
				logger.error(msg);
				throw new ServletException(msg);
            }
		}
		
		return commandFactory;
	}

	protected Logger getLogger()
	{
		return logger;
	}

	/**
	 * The CoreRouterBeanName is the name of the bean in the Spring Web Application Context
	 * that is a reference to a Router implementation.
	 * 
	 * @return
	 */
	protected String getCoreRouterBeanName()
    {
    	return coreRouterBeanName;
    }

	protected void setCoreRouterBeanName(String coreRouterBeanName)
    {
    	this.coreRouterBeanName = coreRouterBeanName;
    }

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() 
	throws ServletException
	{
		// calling getRouter will force exceptions immediately if the router or
		// spring context are not available
		getRouter();
	}
	
}
