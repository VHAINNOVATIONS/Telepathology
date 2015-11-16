/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 27, 2010
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
package gov.va.med.imaging.federation.rest.types;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author vhaiswwerfej
 *
 */
@XmlRootElement
public class FederationImageAccessLogEventType
{
	private String imageId;
    private String patientIcn;
    private String reasonCode;
    private String reasonDescription;
    private String siteNumber;
    private String userSiteNumber;
    private FederationImageAccessLogEventTypeType eventType;
    
    public FederationImageAccessLogEventType()
    {
    	super();
    }

	public String getImageId()
	{
		return imageId;
	}

	public void setImageId(String imageId)
	{
		this.imageId = imageId;
	}

	public String getPatientIcn()
	{
		return patientIcn;
	}

	public void setPatientIcn(String patientIcn)
	{
		this.patientIcn = patientIcn;
	}

	public String getReasonCode()
	{
		return reasonCode;
	}

	public void setReasonCode(String reasonCode)
	{
		this.reasonCode = reasonCode;
	}

	public String getReasonDescription()
	{
		return reasonDescription;
	}

	public void setReasonDescription(String reasonDescription)
	{
		this.reasonDescription = reasonDescription;
	}

	public String getSiteNumber()
	{
		return siteNumber;
	}

	public void setSiteNumber(String siteNumber)
	{
		this.siteNumber = siteNumber;
	}

	public String getUserSiteNumber()
	{
		return userSiteNumber;
	}

	public void setUserSiteNumber(String userSiteNumber)
	{
		this.userSiteNumber = userSiteNumber;
	}

	public FederationImageAccessLogEventTypeType getEventType()
	{
		return eventType;
	}

	public void setEventType(FederationImageAccessLogEventTypeType eventType)
	{
		this.eventType = eventType;
	}
}
