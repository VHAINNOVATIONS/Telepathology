/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr 17, 2008
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
  Description: 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+

 */
package gov.va.med.imaging.business;

import gov.va.med.imaging.core.interfaces.LocalizedSite;
import gov.va.med.imaging.exchange.business.Site;

/**
 * @author VHAISWWERFEJ
 *
 */
public class LocalizedSiteTestImpl 
implements LocalizedSite 
{

	private Site site;
	private boolean alienSite;
	private boolean localSite;
	private boolean enabled = true;
	
	/**
	 * method needed for xml encoder
	 */
	public LocalizedSiteTestImpl()
	{
		site = null;
	}
	
	public LocalizedSiteTestImpl(Site site)
	{
		this.site = site;
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.LocalizedSite#getSite()
	 */
	@Override
	public Site getSite() 
	{
		return site;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.LocalizedSite#isAlienSite()
	 */
	@Override
	public boolean isAlienSite() {
		return alienSite;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.LocalizedSite#isLocalSite()
	 */
	@Override
	public boolean isLocalSite() 
	{
		return localSite;
	}

	public void setAlienSite(boolean alienSite) {
		this.alienSite = alienSite;
	}

	public void setLocalSite(boolean localSite) {
		this.localSite = localSite;
	}

	/** 
	 * method needed for xml encoder
	 * @param site the site to set
	 */
	public void setSite(Site site) {
		this.site = site;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	
}
