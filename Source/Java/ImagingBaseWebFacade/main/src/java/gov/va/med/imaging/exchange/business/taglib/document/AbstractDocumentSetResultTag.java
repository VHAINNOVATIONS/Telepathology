/**
 * 
 */
package gov.va.med.imaging.exchange.business.taglib.document;

import javax.servlet.jsp.JspException;

import gov.va.med.imaging.ImagingBaseWebFacadeRouter;
import gov.va.med.imaging.core.FacadeRouterUtility;
import gov.va.med.imaging.exchange.business.documents.DocumentSetResult;
import gov.va.med.imaging.exchange.business.taglib.AbstractBusinessObjectTag;

/**
 * @author vhaiswbeckec
 *
 */
public abstract class AbstractDocumentSetResultTag
extends AbstractBusinessObjectTag<DocumentSetResult>
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 * @return
	 * @throws JspException 
	 */
	private ImagingBaseWebFacadeRouter router;
	
	protected synchronized ImagingBaseWebFacadeRouter getFacadeRouter() 
	throws JspException
	{
		if(router == null)
		{
			try
			{
				router = FacadeRouterUtility.getFacadeRouter(ImagingBaseWebFacadeRouter.class);
			} 
			catch (Exception x)
			{
				getLogger().error("Exception getting the facade router implementation.", x);
				throw new JspException(x);
			}
		}
		return router;
	}

}
