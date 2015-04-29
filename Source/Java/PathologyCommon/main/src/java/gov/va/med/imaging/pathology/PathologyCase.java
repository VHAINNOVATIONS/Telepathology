/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 7, 2012
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

import gov.va.med.PatientIdentifier;

import java.util.Date;
import java.util.List;

/**
 * @author VHAISWWERFEJ
 *
 */
public class PathologyCase
{
	
	//Accession #~~1^Lock~~2^Patient Name~~3^Patient ID #~~4^Priority~~5^Slide(s) Available~~6^Specimen Taken Date/Time~~7^Status~~8^Site~~9|LRDFN
	private final PathologyCaseURN pathologyCaseUrn;
	private final String accessionNumber;
	private final int reserved;
	private final String reservedBy;
	private final String patientName;
	private final PatientIdentifier patientIdentifier;
	private final String priority;
	private final boolean slidesAvailable;
	private final Date specimenTakenDate;
	private final String status;
	private final String siteAbbr;
	private final int specimenCount;
	private final String patientSsn;
	private final String method;
	private final boolean noteAttached;
	private final boolean patientSensitive;
	private final int numberOfImages; 
	
	//private String consultationStatus = null;
	private List<PathologyCaseConsultation> consultations = null;
	
	//private List<PathologySlide> slides = null;
		
	public PathologyCase(PathologyCaseURN pathologyCaseUrn, String accessionNumber, 
			int reserved, String reservedBy, String patientName,
			PatientIdentifier patientIdentifier, String priority, boolean slidesAvailable,
			Date specimenTakenDate, String status, String siteAbbr,
			int specimenCount, String patientSsn, String method,
			boolean noteAttached, boolean patientSensitive, int numberOfImages)
	{
		super();
		this.pathologyCaseUrn = pathologyCaseUrn;
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

	public String getAccessionNumber()
	{
		return accessionNumber;
	}

	public String getPatientName()
	{
		return patientName;
	}

	public PatientIdentifier getPatientIdentifier()
	{
		return patientIdentifier;
	}

	public String getPriority()
	{
		return priority;
	}

	public boolean isSlidesAvailable()
	{
		return slidesAvailable;
	}

	public Date getSpecimenTakenDate()
	{
		return specimenTakenDate;
	}

	public String getStatus()
	{
		return status;
	}

	public String getSiteAbbr()
	{
		return siteAbbr;
	}

	public PathologyCaseURN getPathologyCaseUrn()
	{
		return pathologyCaseUrn;
	}

	/*
	public List<PathologySlide> getSlides()
	{
		return slides;
	}

	public void setSlides(List<PathologySlide> slides)
	{
		this.slides = slides;
	}*/

	/*
	public String getConsultationStatus()
	{
		return consultationStatus;
	}

	public void setConsultationStatus(String consultationStatus)
	{
		this.consultationStatus = consultationStatus;
	}*/

	public String getReservedBy()
	{
		return reservedBy;
	}

	public List<PathologyCaseConsultation> getConsultations()
	{
		return consultations;
	}

	public void setConsultations(List<PathologyCaseConsultation> consultations)
	{
		this.consultations = consultations;
	}

	public int getReserved()
	{
		return reserved;
	}

	public int getSpecimenCount()
	{
		return specimenCount;
	}

	public String getPatientSsn()
	{
		return patientSsn;
	}

	public String getMethod()
	{
		return method;
	}

	public boolean isNoteAttached()
	{
		return noteAttached;
	}

	public boolean isPatientSensitive()
	{
		return patientSensitive;
	}

	public int getNumberOfImages()
	{
		return numberOfImages;
	}	
}
