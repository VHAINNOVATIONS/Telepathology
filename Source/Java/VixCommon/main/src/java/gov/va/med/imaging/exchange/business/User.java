/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 21, 2011
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
package gov.va.med.imaging.exchange.business;

/**
 * This represents a user of the system (not a patient). This information is from a specific
 * repository in a home community and might be different in different repositories and communities
 * 
 * @author VHAISWWERFEJ
 *
 */
public interface User
{
	/**
	 * Get the unique (at the site) identifier for the user.  In VistA this is the user DUZ
	 * @return
	 */
	public abstract String getUserId();
	
	/**
	 * Get the name of the user
	 * @return
	 */
	public abstract String getName();
	
	/**
	 * Get the title the user holds
	 * @return
	 */
	public abstract String getTitle();
	
	/**
	 * Get the service the user is a part of
	 * @return
	 */
	public abstract String getService();
}
