/**
 * 
 */
package gov.va.med.imaging.exchange.business.taglib;

import gov.va.med.imaging.BaseWebFacadeRouter;
import gov.va.med.imaging.core.FacadeRouterUtility;
import gov.va.med.imaging.core.interfaces.Router;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.JspAwareRequestContext;
import org.springframework.web.servlet.support.RequestContext;

/**
 * A root class, derivation of BodyTagSupport with the addition of
 * application context sensitive methods.  This class and 
 * AbstractApplicationContextTagSupport should be used as the
 * root of VIX tag handlers.
 * In general this class is not directly extended, instead extend
 * one of: 
 * AbstractBusinessObjectCollectionTag, 
 * AbstractBusinessObjectTag,
 * AbstractBusinessObjectPropertyTag
 * 
 * @author vhaiswbeckec
 *
 */
public abstract class AbstractApplicationContextBodyTagSupport
extends BodyTagSupport
{
	/**
	 * {@link javax.servlet.jsp.PageContext} attribute for page-level
	 * {@link RequestContext} instance.
	 */
	public static final String REQUEST_CONTEXT_PAGE_ATTRIBUTE = "org.springframework.web.servlet.tags.REQUEST_CONTEXT";

	private Logger logger = Logger.getLogger(this.getClass());
	private RequestContext requestContext;
	
	/**
	 * 
	 */
	public AbstractApplicationContextBodyTagSupport()
	{
		super();
	}

	/**
	 * Return the current RequestContext.
	 */
	protected synchronized final RequestContext getRequestContext()
	{
		if(this.requestContext == null)
		{
			this.requestContext = (RequestContext) this.pageContext.getAttribute(REQUEST_CONTEXT_PAGE_ATTRIBUTE);
			if (this.requestContext == null)
			{
				this.requestContext = new JspAwareRequestContext(this.pageContext);
				this.pageContext.setAttribute(REQUEST_CONTEXT_PAGE_ATTRIBUTE, this.requestContext);
			}
		}
		
		return this.requestContext;
	}

	/**
	 * 
	 * @return
	 * @throws JspException 
	 */
	private BaseWebFacadeRouter router;
	
	protected synchronized BaseWebFacadeRouter getFacadeRouter() 
	throws JspException
	{
		if(router == null)
		{
			try
			{
				router = FacadeRouterUtility.getFacadeRouter(BaseWebFacadeRouter.class);
			} 
			catch (Exception x)
			{
				logger.error("Exception getting the facade router implementation.", x);
				throw new JspException(x);
			}
		}
		return router;
	}

	/**
	 * 
	 * @return
	 * @throws JspException
	 */
	public Router getRouter() 
	throws JspException
	{
    	WebApplicationContext webApplicationContext = getRequestContext().getWebApplicationContext();
    	
    	Object routerObj = webApplicationContext.getBean("coreRouter");
    	Router vixCore = null;
    	
    	try
        {
	        vixCore = (Router)routerObj;
        } 
    	catch (ClassCastException ccX)
        {
    		String msg = 
    			"Error casting object from context lookup of type [" +
	    		routerObj.getClass().getName() + ", " + routerObj.getClass().getClassLoader().hashCode() +
	    		"] to type [" + 
	    		Router.class.getName() + ", " + Router.class.getClassLoader().hashCode() + "]";
    		logger.error(msg);
    		throw new JspException(ccX);
        }
		
    	return vixCore;
	}
	
	public Logger getLogger()
	{
		return this.logger;
	}

}
