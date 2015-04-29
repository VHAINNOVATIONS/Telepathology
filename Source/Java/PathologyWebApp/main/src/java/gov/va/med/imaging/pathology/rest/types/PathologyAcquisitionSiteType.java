/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 13, 2012
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
package gov.va.med.imaging.pathology.rest.types;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author VHAISWWERFEJ
 *
 */
@XmlRootElement
public class PathologyAcquisitionSiteType
extends AbstractPathologySiteType
{
	
	private String primarySiteStationNumber;
	private String primarySiteAbbr;
	private String primarySiteName;
	
	public PathologyAcquisitionSiteType()
	{
		super();
	}
	
	public PathologyAcquisitionSiteType(String siteId, String siteName,
			String siteAbbr, boolean active, String primarySiteStationNumber, String primarySiteAbbr, String primarySiteName)
	{
		super(siteId, siteName, siteAbbr, active);
		this.primarySiteStationNumber = primarySiteStationNumber;
		this.primarySiteAbbr = primarySiteAbbr;
		this.primarySiteName = primarySiteName;
	}

	public String getPrimarySiteStationNumber()
	{
		return primarySiteStationNumber;
	}

	public void setPrimarySiteStationNumber(String primarySiteStationNumber)
	{
		this.primarySiteStationNumber = primarySiteStationNumber;
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

}
