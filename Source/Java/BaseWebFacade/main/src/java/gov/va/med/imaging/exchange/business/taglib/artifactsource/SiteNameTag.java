package gov.va.med.imaging.exchange.business.taglib.artifactsource;

import gov.va.med.imaging.exchange.business.Site;

import javax.servlet.jsp.JspException;

/**
 * 
 * @author VHAISWBECKEC
 *
 */
public class SiteNameTag 
extends AbstractSitePropertyTag
{
	private static final long serialVersionUID = 1L;

	@Override
    public String getElementValue() 
	throws JspException
    {
		Site s = getSite();
		if(s == null)
			return null;
		return s.getSiteName();
    }
}
