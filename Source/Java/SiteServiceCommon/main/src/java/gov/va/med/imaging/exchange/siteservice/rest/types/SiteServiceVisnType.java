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
@XmlRootElement(name="visn")
public class SiteServiceVisnType
{
	
	private SiteServiceSitesType sites;
	
	private String visnName;
	private String visnNumber;
	
	public SiteServiceVisnType()
	{
		super();
	}
	
	@XmlElement(name = "sites")
	public SiteServiceSitesType getSites()
	{
		return sites;
	}
	
	public void setSites(SiteServiceSitesType sites)
	{
		this.sites = sites;
	}

	public String getVisnName()
	{
		return visnName;
	}

	public void setVisnName(String visnName)
	{
		this.visnName = visnName;
	}

	public String getVisnNumber()
	{
		return visnNumber;
	}

	public void setVisnNumber(String visnNumber)
	{
		this.visnNumber = visnNumber;
	}
}
