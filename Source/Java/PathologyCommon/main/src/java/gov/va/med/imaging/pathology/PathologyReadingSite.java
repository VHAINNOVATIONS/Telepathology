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

import gov.va.med.imaging.pathology.enums.PathologyReadingSiteType;

/**
 * @author VHAISWWERFEJ
 *
 */
public class PathologyReadingSite
extends AbstractPathologySite
{
	private final PathologyReadingSiteType readingSiteType;
	
	public PathologyReadingSite(String siteId, String siteName,
			String siteAbbr, boolean active, PathologyReadingSiteType readingSiteType)
	{
		super(siteId, siteName, siteAbbr, active);
		this.readingSiteType = readingSiteType;
	}

	public PathologyReadingSiteType getReadingSiteType()
	{
		return readingSiteType;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((readingSiteType == null) ? 0 : readingSiteType.hashCode());
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
		PathologyReadingSite other = (PathologyReadingSite) obj;
		if (readingSiteType != other.readingSiteType)
			return false;
		return true;
	}

}
