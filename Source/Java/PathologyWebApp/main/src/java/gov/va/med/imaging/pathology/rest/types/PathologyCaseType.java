/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 11, 2012
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

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author VHAISWWERFEJ
 *
 */
@XmlRootElement
public class PathologyCaseType
{
	private String caseId;
	private String accessionNumber;
	private int reservedState;
	private String reservedBy;
	private String hasNotes;
	private String patientName;
	private String patientId; // could be an ICN or a DFN, it should be an encoded value
	private String priority;
	private boolean slidesAvailable;
	private Date specimenTakenDate;
	private String reportStatus;
	private String siteAbbr;
	private String siteId;
	private int specimenCount;
	private String method;
	private String patientSsn;
	private boolean noteAttached;
	private boolean localOnly;
	private boolean patientSensitive;
	private int numberOfImages;
	
	//private PathologySlidesType slides;
    //private String consultationStatus;
	private PathologyConsultationsType consultations;
	
	public PathologyCaseType()
	{
		super();
	}
	
	public PathologyCaseType(String caseId, String accessionNumber,
			int reservedState, String reservedBy, String hasNotes,
			String patientName, String patientId, String priority,
			boolean slidesAvailable, Date specimenTakenDate, String reportStatus,
			String siteAbbr, String siteId, int specimenCount, String method,
			String patientSsn, boolean noteAttached, boolean localOnly,
			boolean patientSensitive, int numberOfImages)
	{
		super();
		this.caseId = caseId;
		this.accessionNumber = accessionNumber;
		this.reservedState = reservedState;
		this.reservedBy = reservedBy;
		this.hasNotes = hasNotes;
		this.patientName = patientName;
		this.patientId = patientId;
		this.priority = priority;
		this.slidesAvailable = slidesAvailable;
		this.specimenTakenDate = specimenTakenDate;
		this.reportStatus = reportStatus;
		this.siteAbbr = siteAbbr;
		this.siteId = siteId;
		this.specimenCount = specimenCount;
		this.method = method;
		this.patientSsn = patientSsn;
		this.noteAttached = noteAttached;
		this.localOnly = localOnly;
		this.patientSensitive = patientSensitive;
		this.numberOfImages = numberOfImages;
	}

	public String getCaseId()
	{
		return caseId;
	}

	public void setCaseId(String caseId)
	{
		this.caseId = caseId;
	}

	public String getAccessionNumber()
	{
		return accessionNumber;
	}

	public void setAccessionNumber(String accessionNumber)
	{
		this.accessionNumber = accessionNumber;
	}

	public String getPatientName()
	{
		return patientName;
	}

	public void setPatientName(String patientName)
	{
		this.patientName = patientName;
	}

	public String getPatientId()
	{
		return patientId;
	}

	public void setPatientId(String patientId)
	{
		this.patientId = patientId;
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

	public String getReportStatus()
	{
		return reportStatus;
	}

	public void setReportStatus(String reportStatus)
	{
		this.reportStatus = reportStatus;
	}

	public String getSiteAbbr()
	{
		return siteAbbr;
	}

	public void setSiteAbbr(String siteAbbr)
	{
		this.siteAbbr = siteAbbr;
	}

	public String getReservedBy()
	{
		return reservedBy;
	}

	public PathologyConsultationsType getConsultations()
	{
		return consultations;
	}

	public void setConsultations(PathologyConsultationsType consultations)
	{
		this.consultations = consultations;
	}

	public void setReservedBy(String reservedBy)
	{
		this.reservedBy = reservedBy;
	}
	public int getReservedState()
	{
		return reservedState;
	}
	public void setReservedState(int reservedState)
	{
		this.reservedState = reservedState;
	}
	public String getHasNotes()
	{
		return hasNotes;
	}
	public void setHasNotes(String hasNotes)
	{
		this.hasNotes = hasNotes;
	}
	public String getSiteId()
	{
		return siteId;
	}
	public void setSiteId(String siteId)
	{
		this.siteId = siteId;
	}
	public int getSpecimenCount()
	{
		return specimenCount;
	}
	public void setSpecimenCount(int specimenCount)
	{
		this.specimenCount = specimenCount;
	}
	public String getMethod()
	{
		return method;
	}
	public void setMethod(String method)
	{
		this.method = method;
	}

	public String getPatientSsn()
	{
		return patientSsn;
	}

	public void setPatientSsn(String patientSsn)
	{
		this.patientSsn = patientSsn;
	}

	public boolean isNoteAttached()
	{
		return noteAttached;
	}

	public void setNoteAttached(boolean noteAttached)
	{
		this.noteAttached = noteAttached;
	}

	public boolean isLocalOnly()
	{
		return localOnly;
	}

	public void setLocalOnly(boolean localOnly)
	{
		this.localOnly = localOnly;
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
