/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 25, 2010
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

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author vhaiswwerfej
 *
 */
@XmlRootElement
public class FederationDocumentFilterType
extends FederationFilterType
{
	private String patientId;
	private String classCode;
	private String practiceSettingCode;
	private Date creationTimeFrom;
	private Date creationTimeTo;
	private Date serviceStartTimeFrom;
	private Date serviceStartTimeTo;
	private Date serviceStopTimeFrom;
	private Date serviceStopTimeTo;
	private String healthcareFacilityTypeCode;
	private String[] eventCodes;
	private String[] confidentialityCodes;
	private String author;
	private String formatCode;
	private String entryStatus;
	private int maxResultsCount;
	
	// Temporary fix for CVIX demo
	private String siteNumber;
	private boolean useAlternatePatientId;
	
	public FederationDocumentFilterType()
	{
		super();
	}

	public String getPatientId()
	{
		return patientId;
	}

	public void setPatientId(String patientId)
	{
		this.patientId = patientId;
	}

	public String getClassCode()
	{
		return classCode;
	}

	public void setClassCode(String classCode)
	{
		this.classCode = classCode;
	}

	public String getPracticeSettingCode()
	{
		return practiceSettingCode;
	}

	public void setPracticeSettingCode(String practiceSettingCode)
	{
		this.practiceSettingCode = practiceSettingCode;
	}

	public Date getCreationTimeFrom()
	{
		return creationTimeFrom;
	}

	public void setCreationTimeFrom(Date creationTimeFrom)
	{
		this.creationTimeFrom = creationTimeFrom;
	}

	public Date getCreationTimeTo()
	{
		return creationTimeTo;
	}

	public void setCreationTimeTo(Date creationTimeTo)
	{
		this.creationTimeTo = creationTimeTo;
	}

	public Date getServiceStartTimeFrom()
	{
		return serviceStartTimeFrom;
	}

	public void setServiceStartTimeFrom(Date serviceStartTimeFrom)
	{
		this.serviceStartTimeFrom = serviceStartTimeFrom;
	}

	public Date getServiceStartTimeTo()
	{
		return serviceStartTimeTo;
	}

	public void setServiceStartTimeTo(Date serviceStartTimeTo)
	{
		this.serviceStartTimeTo = serviceStartTimeTo;
	}

	public Date getServiceStopTimeFrom()
	{
		return serviceStopTimeFrom;
	}

	public void setServiceStopTimeFrom(Date serviceStopTimeFrom)
	{
		this.serviceStopTimeFrom = serviceStopTimeFrom;
	}

	public Date getServiceStopTimeTo()
	{
		return serviceStopTimeTo;
	}

	public void setServiceStopTimeTo(Date serviceStopTimeTo)
	{
		this.serviceStopTimeTo = serviceStopTimeTo;
	}

	public String getHealthcareFacilityTypeCode()
	{
		return healthcareFacilityTypeCode;
	}

	public void setHealthcareFacilityTypeCode(String healthcareFacilityTypeCode)
	{
		this.healthcareFacilityTypeCode = healthcareFacilityTypeCode;
	}

	public String[] getEventCodes()
	{
		return eventCodes;
	}

	public void setEventCodes(String[] eventCodes)
	{
		this.eventCodes = eventCodes;
	}

	public String[] getConfidentialityCodes()
	{
		return confidentialityCodes;
	}

	public void setConfidentialityCodes(String[] confidentialityCodes)
	{
		this.confidentialityCodes = confidentialityCodes;
	}

	public String getAuthor()
	{
		return author;
	}

	public void setAuthor(String author)
	{
		this.author = author;
	}

	public String getFormatCode()
	{
		return formatCode;
	}

	public void setFormatCode(String formatCode)
	{
		this.formatCode = formatCode;
	}

	public String getEntryStatus()
	{
		return entryStatus;
	}

	public void setEntryStatus(String entryStatus)
	{
		this.entryStatus = entryStatus;
	}

	public int getMaxResultsCount()
	{
		return maxResultsCount;
	}

	public void setMaxResultsCount(int maxResultsCount)
	{
		this.maxResultsCount = maxResultsCount;
	}

	public String getSiteNumber()
	{
		return siteNumber;
	}

	public void setSiteNumber(String siteNumber)
	{
		this.siteNumber = siteNumber;
	}

	public boolean isUseAlternatePatientId()
	{
		return useAlternatePatientId;
	}

	public void setUseAlternatePatientId(boolean useAlternatePatientId)
	{
		this.useAlternatePatientId = useAlternatePatientId;
	}
}
