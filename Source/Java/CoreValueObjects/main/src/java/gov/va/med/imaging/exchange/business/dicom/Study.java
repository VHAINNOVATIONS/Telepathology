/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr 10, 2008
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
package gov.va.med.imaging.exchange.business.dicom;

import gov.va.med.imaging.exchange.business.PersistentEntity;

import java.io.Serializable;
// import java.util.Date;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Represents a patient Reference entity in persistence (DB).
 * 
 * @author vhaiswtittoc
 *
 */
public class Study implements PersistentEntity, Serializable, Comparable<Study>
{	
	private static final long serialVersionUID = -5185851367113539916L;
	private int id;
	private String ien;			// Primary key in DB
	private String patientRefIEN;		// Foreign key to PatientRef record
	private String procedureRefIEN;		// Foreign key to ProcedureRef record
	private String studyIUID;			// actual UID
	private String originalStudyIUID;	// incoming UID, only if UID generation was forced 
	private String studyID;				// Max 16 chars
	private String description;			// Max 64 chars free text
	private String modalitiesInStudy;	// One or more Modalities separated by '\'
	private String studyDateTime;		// when study started (YYYYMMDD.HHMISS)
	private String studyDate;			// when study started (YYYYMMDD)
	private String studyTime;			// when study started (HHMISS)
	private String reasonForStudy;		// Max 64 chars free text
	private String acqComplete;			// 'A'-- acquiring; 'P' -- partially complete; 'C' -- completed
	private String originIX;			// ‘V’ = VA; ‘D’ = DOD, ‘F’ = FEE, 'O' = Other
	private String accessionNumber;		// blind copy from Proc Ref; in attach/updateStudy
	private String priority;			/// priority ‘S'[TAT], ‘R'[OUTINE], 'L'[OW], 'H'[IGH]
	private String referringPhysician; // Referring physician
	// here are the Read Only attributes; they have Getters only and must be set only internally by DB API call
	private String numberOfSeries;		// 
	private String numberOfSopInstances;// 
	private String LastAccessDateTime;  // set by all access methods (YYYYMMDD.HHMISS)
	private String LastUpdateDateTime;  // set by create/attach and update methods (YYYYMMDD.HHMISS)
	
	/**
	 * Create a new study
	 * @param sIUID actual UID (64)
	 * @param originalStudyIUID incoming UID if UID generation was forced, else null (96)
	 * @param studyID the study's ID string (max 16)
	 * @param description free text (64)
	 * @param modalitiesInStudy One or more Modalities separated by '\'
	 * @param studyDateTime when study started 
	 * @param reasonForStudy free text (64) 
	 * @param acqComplete 'A'-- acquiring; 'P' -- partially complete; 'C' -- completed
	 * @param originIX ‘V’ = VA; ‘N’ = NON-VA; ‘D’ = DOD, ‘F’ = FEE 
	 * @param prority Possible values: ‘STAT’, ‘ROUTINE’, 'LOW', 'MED', 'HIGH' 
	 * @param accessionNum same as in parent Proc Ref 
	 */
	public Study(String sIUID, String origSIUID, String sID, String desc, String mtysInStd, String sDT, String reason,
			     String isACQDone, String origIx, String pri, String accN)
	{
		this.studyIUID = sIUID;
		this.originalStudyIUID = origSIUID;
		this.studyID = sID;
		this.description = desc;
		this.modalitiesInStudy = mtysInStd;
		this.studyDateTime = sDT;
		this.reasonForStudy = reason;
		this.acqComplete = isACQDone;
		this.originIX = origIx;
		this.priority = pri;
		this.accessionNumber = accN;
	}
    public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getStudyIUID() {
		return studyIUID;
	}

	public void setStudyIUID(String studyIUID) {
		this.studyIUID = studyIUID;
	}

	public String getOriginalStudyIUID() {
		return originalStudyIUID;
	}

	public void setOriginalStudyIUID(String originalStudyIUID) {
		this.originalStudyIUID = originalStudyIUID;
	}

	public String getStudyID() {
		return studyID;
	}

	public void setStudyID(String studyID) {
		this.studyID = studyID;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getModalitiesInStudy() {
		return modalitiesInStudy;
	}

	public void setModalitiesInStudy(String modalitiesInStudy) {
		this.modalitiesInStudy = modalitiesInStudy;
	}

	public String getStudyDateTime() {
		return studyDateTime;
	}

	public void setStudyDateTime(String studyDateTime) {
		this.studyDateTime = studyDateTime;
	}

	public String getReasonForStudy() {
		return reasonForStudy;
	}

	public void setReasonForStudy(String reasonForStudy) {
		this.reasonForStudy = reasonForStudy;
	}

	public String getAcqComplete() {
		return acqComplete;
	}

	public void setIsAcqComplete(String acqComplete) {
		this.acqComplete = acqComplete;
	}

	public String getOriginIX() {
		return originIX;
	}

	public void setOriginIX(String originIX) {
		this.originIX = originIX;
	}

	public String getPriority() {
		return priority;
	}

	public void setPrority(String priority) {
		this.priority = priority;
	}

	public String getNumberOfSeries() {
		return numberOfSeries;
	}

	public String getNumberOfSopInstances() {
		return numberOfSopInstances;
	}

	public String getLastAccessDateTime() {
		return LastAccessDateTime;
	}

	public String getLastUpdateDateTime() {
		return LastUpdateDateTime;
	}

	public String toString() 
	{
		return this.studyIUID + " (origIUID=" + this.originalStudyIUID + "; ID=" + this.studyID + 
				"; Desc=" + this.description + "; Modalities=" + this.modalitiesInStudy + 
				"; StudyDateTime=" + this.studyDateTime + "; reason=" + this.reasonForStudy +
				"; ACQDone?=" + this.acqComplete + "; originIX=" + this.originIX +
				"; priority=" + this.priority + "; accessionNum=" + this.accessionNumber + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((studyIUID == null) ? 0 : studyIUID.hashCode());
		result = prime * result
				+ ((originalStudyIUID == null) ? 0 : originalStudyIUID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Study other = (Study) obj;
		if (studyIUID == null) {
			if (other.studyIUID != null)
				return false;
		} else if (!studyIUID.equals(other.studyIUID))
			return false;
		if (originalStudyIUID == null) {
			if (other.originalStudyIUID != null)
				return false;
		} else if (!originalStudyIUID.equals(other.originalStudyIUID))
			return false;
		return true;
	}	
	
	@Override
	public int compareTo(Study that) 
	{
		return this.studyIUID.compareTo(that.studyIUID);
	}

	public String getPatientRefIEN()
	{
		// TODO Auto-generated method stub
		return patientRefIEN;
	}

	public void setPatientRefIEN(String patientRefIEN)
	{
		this.patientRefIEN = patientRefIEN;
	}

	public String getProcedureRefIEN()
	{
		// TODO Auto-generated method stub
		return procedureRefIEN;
	}

	public void setProcedureRefIEN(String procedureRefIEN)
	{
		this.procedureRefIEN = procedureRefIEN;
	}

	public String getIEN()
	{
		return ien;
	}

	public void setIEN(String studyIEN)
	{
		this.ien = studyIEN;
	}
	public void setStudyDate(String studyDate) {
		this.studyDate = studyDate;
	}
	public String getStudyDate() {
		return studyDate;
	}
	public void setStudyTime(String studyTime) {
		this.studyTime = studyTime;
	}
	public String getStudyTime() {
		return studyTime;
	}
	public void setReferringPhysician(String referringPhysician) {
		this.referringPhysician = DicomUtils.reformatDicomName(referringPhysician);
	}
	public String getReferringPhysician() {
		return referringPhysician;
	}

	public String getAccessionNumber() {
		return accessionNumber;
	}

	public void setAccessionNumber(String accessionNumber) {
		this.accessionNumber = accessionNumber;
	}
}
