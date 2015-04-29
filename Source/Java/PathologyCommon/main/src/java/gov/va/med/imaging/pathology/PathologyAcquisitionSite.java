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
package gov.va.med.imaging.pathology;

/**
 * @author VHAISWWERFEJ
 *
 */
public class PathologyAcquisitionSite
extends AbstractPathologySite
{
	private final String primarySiteStationNumber;
	private final String primarySiteAbbr;
	private final String primarySiteName;
	
	public PathologyAcquisitionSite(String siteId, String siteName,
			String siteAbbr, boolean active, String primarySiteStationNumber, String primarySiteName, String primarySiteAbbr)
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

	public String getPrimarySiteAbbr()
	{
		return primarySiteAbbr;
	}

	public String getPrimarySiteName()
	{
		return primarySiteName;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((primarySiteAbbr == null) ? 0 : primarySiteAbbr.hashCode());
		result = prime * result
				+ ((primarySiteName == null) ? 0 : primarySiteName.hashCode());
		result = prime
				* result
				+ ((primarySiteStationNumber == null) ? 0
						: primarySiteStationNumber.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PathologyAcquisitionSite other = (PathologyAcquisitionSite) obj;
		if (primarySiteStationNumber == null)
		{
			if (other.primarySiteStationNumber != null)
				return false;
		} else if (!primarySiteStationNumber.equals(other.primarySiteStationNumber))
			return false;
		return true;
	}

}
