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
package gov.va.med.imaging.pathology.rest.types;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author VHAISWWERFEJ
 *
 */
@XmlRootElement
public class PathologyConsultationType
{
	private String consultationId;
	private String siteId;
	private String status;
	private String siteAbbr;	
	private String consultationType;
	
	public PathologyConsultationType()
	{
		super();
	}

	public PathologyConsultationType(String consultationId, String siteId, String status, 
			String siteAbbr, String consultationType)
	{
		super();
		this.consultationId = consultationId;
		this.siteId = siteId;
		this.status = status;
		this.siteAbbr = siteAbbr;
		this.consultationType = consultationType;
	}

	public String getSiteId()
	{
		return siteId;
	}

	public void setSiteId(String siteId)
	{
		this.siteId = siteId;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getSiteAbbr()
	{
		return siteAbbr;
	}

	public void setSiteAbbr(String siteAbbr)
	{
		this.siteAbbr = siteAbbr;
	}

	public String getConsultationId()
	{
		return consultationId;
	}

	public void setConsultationId(String consultationId)
	{
		this.consultationId = consultationId;
	}

	public String getConsultationType()
	{
		return consultationType;
	}

	public void setConsultationType(String consultationType)
	{
		this.consultationType = consultationType;
	}
}
