/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr 8, 2009
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
package gov.va.med.imaging.exchange.business.vistarad;

import gov.va.med.PatientIdentifierType;
import gov.va.med.StudyURNFactory;
import gov.va.med.imaging.StudyURN;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.enums.vistarad.ExamStatus;

import java.io.Serializable;

/**
 * Represents a VistARad Exam instance. Contains a set of VistARad images.
 * 
 * An Exam may be partially or fully loaded depending on how the data was requested.
 * 
 * A fully loaded exam means the report, requisition report and images
 * are contained in the exam.  A partially loaded exam does not contain
 * all of these values
 * 
 * @author vhaiswwerfej
 *
 */
public class Exam 
implements Serializable
{
	private static final long serialVersionUID = -882058193432845092L;
	
	/*
	private final String siteNumber;
	private final String examId; // this should not be a URN!
	private final String patientIcn;
	*/
	private final StudyURN studyUrn;
	
	private ExamStatus examStatus;
	private String patientName;
	private ExamImages images = null;
	private String modality;
	private String cptCode;
	private String siteName;
	private String siteAbbr;
	
	// raw fields from RPC response - might become final since they should never be changed
	private String rawHeaderLine1;
	private String rawHeaderLine2;
	private String rawOutput;
	
	private String examReport;
	private String examRequisitionReport;
	
	private String presentationStateData;
	
	public static Exam create(String siteNumber, String examId, String patientIcn)
	throws URNFormatException
	{
		StudyURN studyUrn = StudyURNFactory.create(siteNumber, examId, patientIcn, StudyURN.class);
		studyUrn.setPatientIdentifierTypeIfNecessary(PatientIdentifierType.icn);
		return new Exam(studyUrn);
	}
	
	/**
	 * Default constructor for an Exam
	 * @param siteNumber
	 * @param examId
	 * @param patientIcn
	 */
	private Exam(StudyURN studyUrn) 
	{
		super();
		this.studyUrn = studyUrn;
		this.examReport = null;
		this.examRequisitionReport = null;
		this.presentationStateData = null;
		this.examStatus = ExamStatus.NOT_INTERPRETED;
	}
	
	/**
	 * Returns a StudyURN representing the exam, this is the identifier that
	 * should be used when identifying an exam outside of the VIX
	 * 
	 * @return
	 * @throws URNFormatException Occurs if the exam cannot be converted into a URN - should not happen
	 */
	public StudyURN getStudyUrn()
	{
		return this.studyUrn;
	}

	/**
	 * Returns the status of the exam as reported by the originating source of the exam
	 * 
	 * @return the examStatus
	 */
	public ExamStatus getExamStatus() {
		return examStatus;
	}

	/**
	 * @param examStatus the examStatus to set
	 */
	public void setExamStatus(ExamStatus examStatus) {
		this.examStatus = examStatus;
	}
	
	/**
	 * Count the number of images in the exam, if the exam is not fully loaded, then the
	 * image count will be 0 regardless of how many images might actually be in the exam
	 * 
	 * @return the imageCount
	 */
	public int getImageCount() 
	{
		//TODO: check on this logic, can we always give a valid image count?
		if(isImagesIncluded())
			return images.size();
		return 0; 
	}

	/**
	 * @return the patientName
	 */
	public String getPatientName() {
		return patientName;
	}

	/**
	 * @param patientName the patientName to set
	 */
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	/**
	 * @return the siteNumber
	 */
	public String getSiteNumber() 
	{
		return getStudyUrn().getOriginatingSiteId();
	}

	/**
	 * Unique identifier for the exam, this is not a globally unique identifier but the
	 * identifier retrieved from VistA.  This usually is an opaque object that contains the 
	 * patient DFN and radiology identifiers. Eventually this will become a Study IEN (group IEN)
	 * so it can be used with Study business objects. This field should be Base32 encoded by the VistA
	 * datasource and decoded only by the VistA data source. 
	 * 
	 * @return the examId
	 */
	public String getExamId() 
	{
		return getStudyUrn().getStudyId();
	}

	/**
	 * Unique identifier for the patient that owns this exam
	 * 
	 * @return the patientIcn
	 */
	public String getPatientIcn() 
	{
		return getStudyUrn().getPatientId();
	}

	/**
	 * Returns the map of images, this will be null if the images have not been loaded. Use isImagesIncluded()
	 * to determine if the images have been loaded before calling this method.
	 * 
	 * @return the images
	 */
	public ExamImages getImages() {
		return images;
	}

	/**
	 * @param images the images to set
	 */
	public void setImages(ExamImages images) {
		this.images = images;
	}

	/**
	 * This contains the raw output of the RPC response from VistA - this should not 
	 * be interpreted by the VIX, only passed back to VistARad.
	 * 
	 * @return the rawOutput
	 */
	public String getRawOutput() {
		return rawOutput;
	}

	/**
	 * @param rawOutput the rawOutput to set
	 */
	public void setRawOutput(String rawOutput) {
		this.rawOutput = rawOutput;
	}

	/**
	 * Exam modality
	 * 
	 * @return the modality
	 */
	public String getModality() {
		return modality;
	}

	/**
	 * @param modality the modality to set
	 */
	public void setModality(String modality) {
		this.modality = modality;
	}

	/**
	 * @return the cptCode
	 */
	public String getCptCode() {
		return cptCode;
	}

	/**
	 * @param cptCode the cptCode to set
	 */
	public void setCptCode(String cptCode) {
		this.cptCode = cptCode;
	}

	/**
	 * @return the siteName
	 */
	public String getSiteName() {
		return siteName;
	}

	/**
	 * @param siteName the siteName to set
	 */
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	/**
	 * @return the siteAbbr
	 */
	public String getSiteAbbr() {
		return siteAbbr;
	}

	/**
	 * @param siteAbbr the siteAbbr to set
	 */
	public void setSiteAbbr(String siteAbbr) {
		this.siteAbbr = siteAbbr;
	}

	/**
	 * @return the examReport
	 */
	public String getExamReport() {
		return examReport;
	}
	
	/**
	 * Determines if the report has been loaded into the exam
	 * @return
	 */
	public boolean isExamReportLoaded()
	{
		if(examReport == null)
			return false;
		return true;
	}

	/**
	 * @param examReport the examReport to set
	 */
	public void setExamReport(String examReport) {
		this.examReport = examReport;
	}

	/**
	 * @return the examRequisitionReport
	 */
	public String getExamRequisitionReport() {
		return examRequisitionReport;
	}

	/**
	 * @param examRequisitionReport the examRequisitionReport to set
	 */
	public void setExamRequisitionReport(String examRequisitionReport) {
		this.examRequisitionReport = examRequisitionReport;
	}			
	
	/**
	 * Determines if the exam requisition report has been loaded
	 * 
	 * @return
	 */
	public boolean isExamRequisitionReportLoaded()
	{
		if(examRequisitionReport == null)
			return false;
		return true;
	}
	
	/**
	 * Determines if the images have been loaded
	 * 
	 * @return
	 */
	public boolean isImagesIncluded()
	{
		if(images == null)
			return false;
		// if the exam images were loaded but the header indicates the metadata should not be cached, return false.
		// Certain cases of the image data (jukebox images) should not be cached since the VIX should always request an
		// updated copy of the data from the data source.
		return images.isCacheImageMetadata();
	}
	
	public boolean isPresentationStateLoaded()
	{
		if(presentationStateData == null)
			return false;
		return true;
	}
	
	/**
	 * Determines if the exam is fully loaded. Fully loaded means the report, requisition report and images
	 * are contained in the exam.
	 * 
	 * @return
	 */
	public boolean isLoaded()
	{
		if(isImagesIncluded() && isExamReportLoaded() 
				&& isExamRequisitionReportLoaded() && isPresentationStateLoaded())
			return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() 
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Exam (" + getExamId() + ") from site '" + getSiteNumber() + "'\n");
		if(isLoaded())
		{
			sb.append("\tExam is fully loaded, contains '" + getImageCount() + "' images");
		}
		else
		{
			sb.append("\tExam is not fully loaded");
		}
		return sb.toString();
	}

	/**
	 * This contains the first header line in raw form from the RPC response from VistA.
	 * This should not be interpreted by the VIX, only passed back to VistARad
	 * 
	 * @return the rawHeaderLine1
	 */
	public String getRawHeaderLine1() {
		return rawHeaderLine1;
	}

	/**
	 * @param rawHeaderLine1 the rawHeaderLine1 to set
	 */
	public void setRawHeaderLine1(String rawHeaderLine1) {
		this.rawHeaderLine1 = rawHeaderLine1;
	}

	/**
	 * This contains the second header line in raw form from the RPC response from VistA.
	 * This should not be interpreted by the VIX, only passed back to VistARad
	 * 
	 * @return the rawHeaderLine2
	 */
	public String getRawHeaderLine2() {
		return rawHeaderLine2;
	}

	/**
	 * @param rawHeaderLine2 the rawHeaderLine2 to set
	 */
	public void setRawHeaderLine2(String rawHeaderLine2) {
		this.rawHeaderLine2 = rawHeaderLine2;
	}

	/**
	 * @return the presentationStateData
	 */
	public String getPresentationStateData() {
		return presentationStateData;
	}

	/**
	 * @param presentationStateData the presentationStateData to set
	 */
	public void setPresentationStateData(String presentationStateData) {
		this.presentationStateData = presentationStateData;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
			+ ((this.studyUrn == null) ? 0 : this.studyUrn.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Exam other = (Exam) obj;
		if (this.studyUrn == null)
		{
			if (other.studyUrn != null)
				return false;
		}
		else if (!this.studyUrn.equalsGlobalArtifactIdentifier(other.studyUrn))
			return false;
		return true;
	}
}
