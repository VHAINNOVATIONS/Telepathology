/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 21, 2012
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
package gov.va.med.imaging.federation.pathology.rest.types;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author VHAISWWERFEJ
 *
 */
@XmlRootElement
public class PathologyFederationAcquisitionSiteType
{
	private String siteId;
	private String siteName;
	private String siteAbbr;
	private boolean active;
	private String primarySiteId;
	private String primarySiteAbbr;
	private String primarySiteName;
	
	public PathologyFederationAcquisitionSiteType()
	{
		super();
	}

	public PathologyFederationAcquisitionSiteType(String siteId, String siteName,
			String siteAbbr, boolean active, String primarySiteId,
			String primarySiteName, String primarySiteAbbr)
	{
		super();
		this.siteId = siteId;
		this.siteName = siteName;
		this.siteAbbr = siteAbbr;
		this.active = active;
		this.primarySiteId = primarySiteId;
		this.primarySiteAbbr = primarySiteAbbr;
		this.primarySiteName = primarySiteName;
	}

	public String getPrimarySiteId()
	{
		return primarySiteId;
	}

	public void setPrimarySiteId(String primarySiteId)
	{
		this.primarySiteId = primarySiteId;
	}

	public String getPrimarySiteAbbr()
	{
		return primarySiteAbbr;
	}

	public void setPrimarySiteAbbr(String primarySiteAbbr)
	{
		this.primarySiteAbbr = primarySiteAbbr;
	}

	public String getPrimarySiteName()
	{
		return primarySiteName;
	}

	public void setPrimarySiteName(String primarySiteName)
	{
		this.primarySiteName = primarySiteName;
	}

	public String getSiteId()
	{
		return siteId;
	}

	public void setSiteId(String siteId)
	{
		this.siteId = siteId;
	}

	public String getSiteName()
	{
		return siteName;
	}

	public void setSiteName(String siteName)
	{
		this.siteName = siteName;
	}

	public String getSiteAbbr()
	{
		return siteAbbr;
	}

	public void setSiteAbbr(String siteAbbr)
	{
		this.siteAbbr = siteAbbr;
	}

	public boolean isActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

}
