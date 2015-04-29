/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 20, 2012
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

import gov.va.med.imaging.rest.types.RestPatientIdentifierType;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author VHAISWWERFEJ
 *
 */
@XmlRootElement
public class PathologyFederationCaseType
{
	
	private String pathologyCaseId;
	private String accessionNumber;
	private int reserved;
	private String reservedBy;
	private String patientName;
	private RestPatientIdentifierType patientIdentifier;
	private String priority;
	private boolean slidesAvailable;
	private Date specimenTakenDate;
	private String status;
	private String siteAbbr;
	private int specimenCount;
	private String patientSsn;
	private String method;
	private boolean noteAttached;
	private boolean patientSensitive;
	private int numberOfImages;
	
	private PathologyFederationConsultationType [] consultations;
	
	public PathologyFederationCaseType()
	{
		super();
	}

	public PathologyFederationCaseType(String pathologyCaseId,
			String accessionNumber, int reserved, String reservedBy,
			String patientName, RestPatientIdentifierType patientIdentifier, String priority,
			boolean slidesAvailable, Date specimenTakenDate, String status,
			String siteAbbr, int specimenCount, String patientSsn,
			String method, boolean noteAttached,
			boolean patientSensitive, int numberOfImages)
	{
		super();
		this.pathologyCaseId = pathologyCaseId;
		this.accessionNumber = accessionNumber;
		this.reserved = reserved;
		this.reservedBy = reservedBy;
		this.patientName = patientName;
		this.patientIdentifier = patientIdentifier;
		this.priority = priority;
		this.slidesAvailable = slidesAvailable;
		this.specimenTakenDate = specimenTakenDate;
		this.status = status;
		this.siteAbbr = siteAbbr;
		this.specimenCount = specimenCount;
		this.patientSsn = patientSsn;
		this.method = method;
		this.noteAttached = noteAttached;
		this.patientSensitive = patientSensitive;
		this.numberOfImages = numberOfImages;
	}

	public String getPathologyCaseId()
	{
		return pathologyCaseId;
	}

	public void setPathologyCaseId(String pathologyCaseId)
	{
		this.pathologyCaseId = pathologyCaseId;
	}

	public String getAccessionNumber()
	{
		return accessionNumber;
	}

	public void setAccessionNumber(String accessionNumber)
	{
		this.accessionNumber = accessionNumber;
	}

	public int getReserved()
	{
		return reserved;
	}

	public void setReserved(int reserved)
	{
		this.reserved = reserved;
	}

	public String getReservedBy()
	{
		return reservedBy;
	}

	public void setReservedBy(String reservedBy)
	{
		this.reservedBy = reservedBy;
	}

	public String getPatientName()
	{
		return patientName;
	}

	public void setPatientName(String patientName)
	{
		this.patientName = patientName;
	}

	public RestPatientIdentifierType getPatientIdentifier()
	{
		return patientIdentifier;
	}

	public void setPatientIdentifier(RestPatientIdentifierType patientIdentifier)
	{
		this.patientIdentifier = patientIdentifier;
	}

	public String getPriority()
	{
		return priority;
	}

	public void setPriority(String priority)
	{
		this.priority = priority;
	}

	public boolean isSlidesAvailable()
	{
		return slidesAvailable;
	}

	public void setSlidesAvailable(boolean slidesAvailable)
	{
		this.slidesAvailable = slidesAvailable;
	}

	public Date getSpecimenTakenDate()
	{
		return specimenTakenDate;
	}

	public void setSpecimenTakenDate(Date specimenTakenDate)
	{
		this.specimenTakenDate = specimenTakenDate;
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

	public int getSpecimenCount()
	{
		return specimenCount;
	}

	public void setSpecimenCount(int specimenCount)
	{
		this.specimenCount = specimenCount;
	}

	public String getPatientSsn()
	{
		return patientSsn;
	}

	public void setPatientSsn(String patientSsn)
	{
		this.patientSsn = patientSsn;
	}

	public String getMethod()
	{
		return method;
	}

	public void setMethod(String method)
	{
		this.method = method;
	}

	public PathologyFederationConsultationType[] getConsultations()
	{
		return consultations;
	}

	public void setConsultations(PathologyFederationConsultationType[] consultations)
	{
		this.consultations = consultations;
	}

	public boolean isNoteAttached()
	{
		return noteAttached;
	}

	public void setNoteAttached(boolean noteAttached)
	{
		this.noteAttached = noteAttached;
	}

	public boolean isPatientSensitive()
	{
		return patientSensitive;
	}

	public void setPatientSensitive(boolean patientSensitive)
	{
		this.patientSensitive = patientSensitive;
	}

	public int getNumberOfImages()
	{
		return numberOfImages;
	}

	public void setNumberOfImages(int numberOfImages)
	{
		this.numberOfImages = numberOfImages;
	}

}
