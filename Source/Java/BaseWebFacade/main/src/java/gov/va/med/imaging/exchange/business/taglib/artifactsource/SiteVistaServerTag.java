package gov.va.med.imaging.exchange.business.taglib.artifactsource;

import javax.servlet.jsp.JspException;

/**
 * 
 * @author VHAISWBECKEC
 *
 */
public class SiteVistaServerTag
extends AbstractSitePropertyTag
{
	private static final long serialVersionUID = 1L;

	@Override
    public String getElementValue() 
	throws JspException
    {
		return getSite().getVistaServer();
    }
}
