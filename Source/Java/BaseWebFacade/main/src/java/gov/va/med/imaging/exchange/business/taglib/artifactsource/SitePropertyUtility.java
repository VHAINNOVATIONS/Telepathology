/**
 * 
 */
package gov.va.med.imaging.exchange.business.taglib.artifactsource;

import gov.va.med.imaging.exchange.business.Site;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * @author vhaiswbeckec
 *
 */
public class SitePropertyUtility
{
	static AbstractSiteTag getParentSiteTag(Tag subject)
	{
		return (AbstractSiteTag)TagSupport.findAncestorWithClass(subject, AbstractSiteTag.class);
	}

	/**
	 * 
	 * @return
	 * @throws JspException
	 */
	static Site getSite(Tag subject) 
	throws JspException
	{
		AbstractSiteTag siteTag = SitePropertyUtility.getParentSiteTag(subject);
		if(siteTag == null)
			throw new JspException("A Site Property tag does not have an ancestor Site tag.");
		
		Site site = siteTag.getSite();
		
		if(site == null)
			throw new JspException("A Site Property tag was unable to get the ResolvedSite from its parent tag.");
		
		return site;
	}
	

}
