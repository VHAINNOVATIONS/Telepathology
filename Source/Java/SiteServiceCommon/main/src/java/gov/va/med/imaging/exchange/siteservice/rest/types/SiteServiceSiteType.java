/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 5, 2012
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
package gov.va.med.imaging.exchange.siteservice.rest.types;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author VHAISWWERFEJ
 *
 */
@XmlRootElement(name="site")
public class SiteServiceSiteType
{
	private String siteNumber;
	private String siteName;
	private String visnNumber;
	private String siteAbbr;
	
	private boolean siteUserAuthenticatable;
	private boolean sitePatientLookupable;
	
	private SiteServiceSiteConnectionsType siteConnections;
	
	public SiteServiceSiteType()
	{
		super();
	}

	public String getSiteNumber()
	{
		return siteNumber;
	}

	public void setSiteNumber(String siteNumber)
	{
		this.siteNumber = siteNumber;
	}

	public String getSiteName()
	{
		return siteName;
	}

	public void setSiteName(String siteName)
	{
		this.siteName = siteName;
	}

	public String getVisnNumber()
	{
		return visnNumber;
	}

	public void setVisnNumber(String visnNumber)
	{
		this.visnNumber = visnNumber;
	}

	public String getSiteAbbr()
	{
		return siteAbbr;
	}

	public void setSiteAbbr(String siteAbbr)
	{
		this.siteAbbr = siteAbbr;
	}

	public boolean isSiteUserAuthenticatable()
	{
		return siteUserAuthenticatable;
	}

	public void setSiteUserAuthenticatable(boolean siteUserAuthenticatable)
	{
		this.siteUserAuthenticatable = siteUserAuthenticatable;
	}

	public boolean isSitePatientLookupable()
	{
		return sitePatientLookupable;
	}

	public void setSitePatientLookupable(boolean sitePatientLookupable)
	{
		this.sitePatientLookupable = sitePatientLookupable;
	}

	/**
	 * @return the siteConnections
	 */
	@XmlElement(name = "connections")
	public SiteServiceSiteConnectionsType getSiteConnections()
	{
		return siteConnections;
	}

	/**
	 * @param siteConnections the siteConnections to set
	 */
	public void setSiteConnections(SiteServiceSiteConnectionsType siteConnections)
	{
		this.siteConnections = siteConnections;
	}
}
