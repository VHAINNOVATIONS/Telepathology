/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 15, 2012
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

import java.util.Date;

/**
 * @author VHAISWWERFEJ
 *
 */
public class PathologyCaseConsultation
{
	
	private final PathologyCaseConsultationURN pathologyCaseConsultationUrn;
	private final String type;
	private final Date reservationDate;
	private final String interpretingStation;
	private final String siteAbbr;
	private final String status;
	
	public PathologyCaseConsultation(
			PathologyCaseConsultationURN pathologyCaseConsultationUrn,
			String type, Date reservationDate, String interpretingStation,
			String siteAbbr, String status)
	{
		super();
		this.pathologyCaseConsultationUrn = pathologyCaseConsultationUrn;
		this.type = type;
		this.reservationDate = reservationDate;
		this.interpretingStation = interpretingStation;
		this.siteAbbr = siteAbbr;
		this.status = status;
	}
	
	public PathologyCaseConsultationURN getPathologyCaseConsultationUrn()
	{
		return pathologyCaseConsultationUrn;
	}
	
	public String getType()
	{
		return type;
	}
	
	public Date getReservationDate()
	{
		return reservationDate;
	}
	
	public String getInterpretingStation()
	{
		return interpretingStation;
	}
	
	public String getSiteAbbr()
	{
		return siteAbbr;
	}
	
	public String getStatus()
	{
		return status;
	}
}
