/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Aug 21, 2008
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Implementation of the Region interface containing one or more Site objects
 * 
 * @author VHAISWWERFEJ
 *
 */
public class RegionImpl 
implements Region, Serializable
{
	private static final long serialVersionUID = 9114784808273852712L;
	
	private String regionName;
	private String regionNumber;
	private List<Site> sites = null;
	
	public RegionImpl()
	{
		regionName = regionNumber = "";
		sites = new ArrayList<Site>();
	}
	
	public RegionImpl(String regionName, String regionNumber)
	{
		this.regionName = regionName;
		this.regionNumber = regionNumber;
		sites = new ArrayList<Site>();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.Region#getRegionName()
	 */
	@Override
	public String getRegionName() {
		return regionName;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.Region#getRegionNumber()
	 */
	@Override
	public String getRegionNumber() {
		return regionNumber;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.Region#setRegionName(java.lang.String)
	 */
	@Override
	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.Region#setRegionNumber(java.lang.String)
	 */
	@Override
	public void setRegionNumber(String regionNumber) {
		this.regionNumber = regionNumber;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.Region#getSites()
	 */
	@Override
	public List<Site> getSites() {
		return sites;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.Region#setSites(java.util.List)
	 */
	@Override
	public void setSites(List<Site> sites) 
	{
		this.sites = sites;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj.getClass() == RegionImpl.class) {
			RegionImpl s = (RegionImpl)obj;
			return this.regionNumber.equalsIgnoreCase(s.regionNumber);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() 
	{
		return this.regionName + " - " + this.regionNumber;
	}

	
}
