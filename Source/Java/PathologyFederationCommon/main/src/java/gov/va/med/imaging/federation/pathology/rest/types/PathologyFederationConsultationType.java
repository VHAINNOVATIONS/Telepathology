/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 25, 2012
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

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author VHAISWWERFEJ
 *
 */
@XmlRootElement
public class PathologyFederationConsultationType
{
	private String consultationId;
	private String type;
	private Date reservationDate;
	private String interpretingStation;
	private String siteAbbr;
	private String status;
	
	public PathologyFederationConsultationType()
	{
		super();
	}

	public PathologyFederationConsultationType(String consultationId,
			String type, Date reservationDate, String interpretingStation,
			String siteAbbr, String status)
	{
		super();
		this.consultationId = consultationId;
		this.type = type;
		this.reservationDate = reservationDate;
		this.interpretingStation = interpretingStation;
		this.siteAbbr = siteAbbr;
		this.status = status;
	}

	public String getConsultationId()
	{
		return consultationId;
	}

	public void setConsultationId(String consultationId)
	{
		this.consultationId = consultationId;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public Date getReservationDate()
	{
		return reservationDate;
	}

	public void setReservationDate(Date reservationDate)
	{
		this.reservationDate = reservationDate;
	}

	public String getInterpretingStation()
	{
		return interpretingStation;
	}

	public void setInterpretingStation(String interpretingStation)
	{
		this.interpretingStation = interpretingStation;
	}

	public String getSiteAbbr()
	{
		return siteAbbr;
	}

	public void setSiteAbbr(String siteAbbr)
	{
		this.siteAbbr = siteAbbr;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

}
