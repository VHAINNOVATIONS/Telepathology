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
package gov.va.med.imaging.core.interfaces;

import gov.va.med.imaging.exchange.business.Site;

/**
 * A wrapper around a Site implementation that adds configuration information
 * about the Site, including whether the Site is local (same server) or 
 * alien (outside the enterprise WAN.)
 * 
 * @author VHAISWWERFEJ
 *
 */
public interface LocalizedSite 
{
	
	/**
	 * Get the Site that was resolved.
	 * 
	 * @return
	 */
	public abstract Site getSite();

	/**
	 * Local sites are determined during installation by a human.
	 * The core router may make some decisions about what kind of
	 * images it requests depending on whether a site is local.
	 * 
	 * @return
	 */
	public abstract boolean isLocalSite();

	/**
	 * Alien sites are sites that have been configured as requiring
	 * special handling as they are outside the enterprise.  The BIA
	 * is considered an alien site. The core router currently (21Feb2008) 
	 * does not make any routing decisions or modify data requests based 
	 * upon this flag, it is just informational. 
	 * 
	 * @return
	 */
	public abstract boolean isAlienSite();
	
	/**
	 * A site might be disabled by site resolution meaning no data should be loaded from that site.
	 * @return
	 */
	public abstract boolean isEnabled();

}
