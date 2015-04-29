/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jul 10, 2012
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
 * In reality there is nothing special about this object and it is not specific to Pathology however it is initially being used by Pathology. 
 * These site objects do not necessarily correspond to entries in the site service.
 * 
 * @author VHAISWWERFEJ
 *
 */
public class PathologySite
{
	private final String siteId;
	private final String siteName;
	private final String stationNumber;
	private final String siteAbbr;
	
	public PathologySite(String siteId, String siteName, String stationNumber,
			String siteAbbr)
	{
		super();
		this.siteId = siteId;
		this.siteName = siteName;
		this.stationNumber = stationNumber;
		this.siteAbbr = siteAbbr;
	}

	public String getSiteId()
	{
		return siteId;
	}

	public String getSiteName()
	{
		return siteName;
	}

	public String getStationNumber()
	{
		return stationNumber;
	}

	public String getSiteAbbr()
	{
		return siteAbbr;
	}

}
