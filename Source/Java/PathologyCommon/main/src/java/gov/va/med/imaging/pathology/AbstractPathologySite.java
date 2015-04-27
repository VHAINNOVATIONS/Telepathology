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
public abstract class AbstractPathologySite
{
	private final String siteId;
	private final String siteName;
	private final String siteAbbr;
	private final boolean active;
	
	public AbstractPathologySite(String siteId, String siteName,
			String siteAbbr, boolean active)
	{
		super();
		this.siteId = siteId;
		this.siteName = siteName;
		this.siteAbbr = siteAbbr;
		this.active = active;
	}
	
	public String getSiteId()
	{
		return siteId;
	}
	
	public String getSiteName()
	{
		return siteName;
	}
	
	public String getSiteAbbr()
	{
		return siteAbbr;
	}
	
	public boolean isActive()
	{
		return active;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (active ? 1231 : 1237);
		result = prime * result
				+ ((siteAbbr == null) ? 0 : siteAbbr.hashCode());
		result = prime * result + ((siteId == null) ? 0 : siteId.hashCode());
		result = prime * result
				+ ((siteName == null) ? 0 : siteName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractPathologySite other = (AbstractPathologySite) obj;
		if (active != other.active)
			return false;
		if (siteId == null)
		{
			if (other.siteId != null)
				return false;
		} else if (!siteId.equals(other.siteId))
			return false;
		return true;
	}	
}
