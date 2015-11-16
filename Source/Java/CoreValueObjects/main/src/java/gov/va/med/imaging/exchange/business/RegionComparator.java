/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 18, 2011
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswwerfej
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
 * @author vhaiswwerfej
 *
 */
public class RegionComparator
implements java.util.Comparator<Region>
{
	@Override
	public int compare(Region r1, Region r2)
	{
		Integer r1Number = null;
		try
		{
			r1Number = Integer.parseInt(r1.getRegionNumber());
		}
		catch(NumberFormatException nfX)
		{
			r1Number = null;
		}
		Integer r2Number = null;
		try
		{
			r2Number = Integer.parseInt(r2.getRegionNumber());
		}
		catch(NumberFormatException nfX)
		{
			r2Number = null;
		}
		if(r1Number != null && r2Number != null)
		{
			return r1Number.compareTo(r2Number);
		}
		if(r1Number == null)
			return -1;
		if(r2Number == null)
			return 1;
		return 0;
	}
}
