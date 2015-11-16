package gov.va.med.imaging.webapp;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

public class ImageExchangeApplicationContextServletContextListener 
implements ServletContextListener
{
	private ServletContext context = null;
	private static Logger log = Logger.getLogger(ImageExchangeApplicationContextServletContextListener.class);

	public void contextInitialized(ServletContextEvent event)
	{
		log.info("ImageExchangeApplicationContextServletContextListener.contextInitialized() begin.");
		context = event.getServletContext();
		String configLocation = context.getInitParameter(ContextLoader.CONFIG_LOCATION_PARAM);
		
		String[] contexts = configLocation.split("[ ,]");
		log.info("ImageExchangeApplicationContextServletContextListener.contextInitialized() loading from '" + configLocation + "'");
		
		ImageExchangeApplicationContext applicationContext = ImageExchangeApplicationContext.getSingleton(contexts);
		
		context.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);
		log.info("ImageExchangeApplicationContext available in servlet context attribute '" + WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE + "'.");
		log.info("ImageExchangeApplicationContextServletContextListener.contextInitialized() complete.");
	}
	
	public void contextDestroyed(ServletContextEvent event)
	{
		log.info("ImageExchangeApplicationContextServletContextListener.contextDestroyed() begin.");
		context.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, null);
		context = null;
		log.info("ImageExchangeApplicationContextServletContextListener.contextDestroyed() complete.");
	}

}
