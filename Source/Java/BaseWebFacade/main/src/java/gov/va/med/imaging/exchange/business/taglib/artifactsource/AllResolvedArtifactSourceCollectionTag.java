/**
 * 
 */
package gov.va.med.imaging.exchange.business.taglib.artifactsource;

import gov.va.med.imaging.BaseWebFacadeRouter;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.FacadeRouterUtility;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import java.util.Collection;
import javax.servlet.jsp.JspException;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.support.RequestContext;

/**
 * @author vhaiswbeckec
 *
 */
public class AllResolvedArtifactSourceCollectionTag
extends AbstractResolvedArtifactSourceCollectionTag
{
	private static final long serialVersionUID = 1L;

	/**
	 * {@link javax.servlet.jsp.PageContext} attribute for page-level
	 * {@link RequestContext} instance.
	 */
	public static final String REQUEST_CONTEXT_PAGE_ATTRIBUTE = "org.springframework.web.servlet.tags.REQUEST_CONTEXT";

	private Logger logger = Logger.getLogger(this.getClass());

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.taglib.artifactsource.AbstractArtifactSourceCollectionTag#getArtifactSources()
	 */
	@Override
	protected Collection<ResolvedArtifactSource> getResolvedArtifactSources() 
	throws JspException
	{
    	BaseWebFacadeRouter router;
		try
		{
			router = FacadeRouterUtility.getFacadeRouter(BaseWebFacadeRouter.class);
		} 
		catch (Exception x)
		{
			logger.error("Exception getting the facade router implementation.", x);
			throw new JspException(x);
		}
    	
    	try 
    	{
    		return router.getResolvedArtifactSourceList();
    	}
    	catch(MethodException mX)
    	{
    		logger.error(mX);
    		throw new JspException(mX);
    	}
    	catch(ConnectionException mX)
    	{
    		logger.error(mX);
    		throw new JspException(mX);
    	}
    }
}
