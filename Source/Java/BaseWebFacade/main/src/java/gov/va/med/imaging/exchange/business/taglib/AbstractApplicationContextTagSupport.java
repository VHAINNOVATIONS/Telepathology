/**
 * 
 */
package gov.va.med.imaging.exchange.business.taglib;

import gov.va.med.imaging.BaseWebFacadeRouter;
import gov.va.med.imaging.core.FacadeRouterUtility;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.support.JspAwareRequestContext;
import org.springframework.web.servlet.support.RequestContext;

/**
 * @author vhaiswbeckec
 *
 * A root class, derivation TagSupport with the addition of
 * application context sensitive methods.  This class and 
 * AbstractApplicationContextTagSupport should be used as the
 * root of VIX tag handlers.
 */
public abstract class AbstractApplicationContextTagSupport
extends TagSupport
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
	public AbstractApplicationContextTagSupport()
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

	public Logger getLogger()
	{
		return this.logger;
	}
}
