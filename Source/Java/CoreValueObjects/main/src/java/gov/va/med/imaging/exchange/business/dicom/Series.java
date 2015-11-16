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

/**
 * Represents a patient Reference entity in persistence (DB).
 * 
 * @author vhaiswtittoc
 *
 */
public class Series implements PersistentEntity, Serializable, Comparable<Series>
{	
	private static final long serialVersionUID = -5185851367113539916L;
	private int id;
	private String ien;					// Primary key in DB
	private String studyIEN;			// Foreign key to parent study
	private String seriesIUID;			// actual UID
	private String originalSeriesIUID;	// incoming UID, only if UID generation was forced 
	private String seriesNumber;		// Max 12 chars
	private String description;			// Max 64 chars free text
	private String modality;			// DICOM Modality code (Enumerated type) (2..16)
	private String bodyPart;			// DICOM body part examined -- Index (Enumerated type) (2..16)
	private String acqSite;				// location where series was created (2..32)
	private String seriesDateTime;		// when study started (YYYYMMDD.HHMISS)
	private String seriesCreator;		// person/device -- Max 64
	private String seriesCreatorDeviceModel; // Max 64
	private String frameOfReferenceUID;	// Max 64 chars free text
	private String laterality;			// ‘L’ or ‘R’
	private String spatialPosition;		// Possible values: 'HFP', 'HFS', 'HFDR', 'HFDL', 'FFDR', 'FFDL', 'FFP', 'FFS'
	private String sourceAETitle;		// max 16
	private String retrieveAETitle;		// max 16
	private String vIAcqEntryPoint;		// 10..25 like ‘DICOM Storage’,	‘Clinical Capture’, ‘Import API’,‘Import Reconciliation’
	private String iODViolationDetected;// 0..3, where 0 - VALID; 1 - NOT VALID; 2- NOT CHECKED; 3 - UNKNOWN  
	private String facility;
	private String institutionAddress;
	private String classIX;				// "CLIN","CLIN/ADMIN","ADMIN" or "ADMIN/CLIN"
	private String procedureEventIX;	// pointer to 2005.85
	private String specSubSpecIX;		// pointer to 2005.84 -- -ology/dept.
	private String tiuNoteReference;	// pointer to 8925 -- TIU Document file# -- an IEN, valid only for Consults

	// here are the Read Only attributes; they have Getters only and must be set only internally by DB API call
	private String numberOfSopInstances;// 
	private String LastUpdateDateTime;  // set by create/attach and update methods (YYYYMMDD.HHMISS)
	
	/**
	 * Create a new series
	 * @param sIUID actual UID (64)
	 * @param originalSeriesIUID incoming UID if UID generation was forced, else null (96)
	 * @param seriesNumber the series' non-unique ID within the Study (max 12)
	 * @param description free text (64)
	 * @param modality DICOM Modality Code (2..16)
	 * @param bodypart DICOM enumerated Code (2..16) -- indexed
	 * @param acqSite location where series was created (2..16)
	 * @param seriesDateTime when study started 
	 * @param seriesCreator person/device -- Max 64
	 * @param seriesCreatorDeviceModel  Max 64
	 * @param frameOfReferenceUID  Max 64 chars free text
	 * @param laterality ‘L’ or ‘R’
	 * @param spatialPosition possible values: 'HFP', 'HFS', 'HFDR', 'HFDL', 'FFDR', 'FFDL', 'FFP', 'FFS'
	 * @param sourceAETitle max 16
	 * @param retrieveAETitle max 16
	 * @param vIAcqEntryPoint 10..25 like ‘DICOM Storage’,‘Clinical Capture’,‘Import API’ or‘Import Reconciliation’
	 * @param iODViolationDetected; // 0..3, where 0 - VALID; 1 - NOT VALID; 2- NOT CHECKED; 3 - UNKNOWN  
	 * @param classIx enum strings ("CLIN", "ADMIN", "CLIN/ADMIN", "ADMIN/CLIN");
	 * @param procEventIx  enumerated strings;  pointer to 2005.85
	 * @param specSubSpecIx enumerated strings ( -ology/dept.);  pointer to 2005.84
	 * @param tiuNoteReference for Consults pointer for related TIU Document
	 */
	public Series(String sIUID, String origSIUID, String serNum, String desc, String mty, String bodypart, String ascsite, String sDT,
			     String serCreator, String serCreDevModel, String frameOfRefUID, String laterality, String spatPos,
			     String srcAE, String retrAE, String vIAcqEntryPt, String IODViolationDetected,
			     String classIx, String procEventIx, String specSubSpecIx, String tiuNoteReference)
	{
		this.seriesIUID = sIUID;
		this.originalSeriesIUID = origSIUID;
		this.seriesNumber = serNum;
		this.description = desc;
		this.modality = mty;
		this.bodyPart = bodypart;
		this.acqSite = ascsite;
		this.seriesDateTime = sDT;
		this.seriesCreator = serCreator;
		this.seriesCreatorDeviceModel = serCreDevModel;
		this.frameOfReferenceUID = frameOfRefUID;
		this.laterality = laterality;
		this.spatialPosition = spatPos;
		this.sourceAETitle = srcAE;
		this.retrieveAETitle = retrAE;
		this.vIAcqEntryPoint = vIAcqEntryPt;
		this.iODViolationDetected = IODViolationDetected;
		this.classIX = classIx;
		this.procedureEventIX = procEventIx;
		this.specSubSpecIX = specSubSpecIx;
		this.tiuNoteReference = tiuNoteReference;
	}
	
	public String toString() 
	{
		return this.seriesIUID + " (origIUID=" + this.originalSeriesIUID + "; Desc=" + this.description + 
				"; Modality=" + this.modality + "; Bodypart=" + this.bodyPart + "; SeriesDateTime=" + this.seriesDateTime + 
				"; seriesCreator=" + this.seriesCreator + "; seriesCreatorDevmodel=" + this.seriesCreatorDeviceModel +
				"; frameOfRefUID=" + this.frameOfReferenceUID + "; laterality=" + this.laterality +
				"; SpatialPosition=" + this.spatialPosition + "; SourceAE=" + this.sourceAETitle +
				"; RetrieveAE=" + this.retrieveAETitle + "; VIAcqEntrypoint=" + this.vIAcqEntryPoint +
				"; IODViolationDetected=" + this.iODViolationDetected +
				"; classIX=" + this.classIX + "; procedureEventIX=" + this.procedureEventIX + 
				"; specSubSpecIX=" + this.specSubSpecIX + "; tiuNoteReference=" + this.tiuNoteReference + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((seriesIUID == null) ? 0 : seriesIUID.hashCode());
		result = prime * result
				+ ((originalSeriesIUID == null) ? 0 : originalSeriesIUID.hashCode());
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
		final Series other = (Series) obj;
		if (seriesIUID == null) {
			if (other.seriesIUID != null)
				return false;
		} else if (!seriesIUID.equals(other.seriesIUID))
			return false;
		if (originalSeriesIUID == null) {
			if (other.originalSeriesIUID != null)
				return false;
		} else if (!originalSeriesIUID.equals(other.originalSeriesIUID))
			return false;
		return true;
	}	
	
	@Override
	public int compareTo(Series that) 
	{
		return this.seriesIUID.compareTo(that.seriesIUID);
	}

    public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSeriesIUID() {
		return seriesIUID;
	}

	public void setSeriesIUID(String seriesIUID) {
		this.seriesIUID = seriesIUID;
	}

	public String getOriginalSeriesIUID() {
		return originalSeriesIUID;
	}

	public void setOriginalSeriesIUID(String originalSeriesIUID) {
		this.originalSeriesIUID = originalSeriesIUID;
	}

	public String getSeriesNumber() {
		return seriesNumber;
	}

	public void setSeriesNumber(String seriesNumber) {
		this.seriesNumber = seriesNumber;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getModality() {
		return modality;
	}

	public void setModality(String modality) {
		this.modality = modality;
	}

	public String getBodyPart() {
		return bodyPart;
	}

	public void setBodyPart(String bodyPart) {
		this.bodyPart = bodyPart;
	}

	public String getAcqSite() {
		return acqSite;
	}

	public void setAcqSite(String acqSite) {
		this.acqSite = acqSite;
	}

	public String getSeriesDateTime() {
		return seriesDateTime;
	}

	public void setSeriesDateTime(String seriesDateTime) {
		this.seriesDateTime = seriesDateTime;
	}

	public String getSeriesCreator() {
		return seriesCreator;
	}

	public void setSeriesCreator(String seriesCreator) {
		this.seriesCreator = seriesCreator;
	}

	public String getSeriesCreatorDeviceModel() {
		return seriesCreatorDeviceModel;
	}

	public void setSeriesCreatorDeviceModel(String seriesCreatorDeviceModel) {
		this.seriesCreatorDeviceModel = seriesCreatorDeviceModel;
	}

	public String getFrameOfReferenceUID() {
		return frameOfReferenceUID;
	}

	public void setFrameOfReferenceUID(String frameOfReferenceUID) {
		this.frameOfReferenceUID = frameOfReferenceUID;
	}

	public String getLaterality() {
		return laterality;
	}

	public void setLaterality(String laterality) {
		this.laterality = laterality;
	}

	public String getSpatialPosition() {
		return spatialPosition;
	}

	public void setSpatialPosition(String spatialPosition) {
		this.spatialPosition = spatialPosition;
	}

	public String getSourceAETitle() {
		return sourceAETitle;
	}

	public void setSourceAETitle(String sourceAETitle) {
		this.sourceAETitle = sourceAETitle;
	}

	public String getRetrieveAETitle() {
		return retrieveAETitle;
	}

	public void setRetrieveAETitle(String retrieveAETitle) {
		this.retrieveAETitle = retrieveAETitle;
	}

	public String getVIAcqEntryPoint() {
		return vIAcqEntryPoint;
	}

	public void setVIAcqEntryPoint(String acqEntryPoint) {
		vIAcqEntryPoint = acqEntryPoint;
	}

	public String getNumberOfSopInstances() {
		return numberOfSopInstances;
	}

	public String getLastUpdateDateTime() {
		return LastUpdateDateTime;
	}

	public String getStudyIEN()
	{
		// TODO Auto-generated method stub
		return studyIEN;
	}

	public String getIEN()
	{
		return ien;
	}

	public void setIEN(String ien)
	{
		this.ien = ien;
	}

	public void setStudyIEN(String studyIEN)
	{
		this.studyIEN = studyIEN;
	}

	public String getIODViolationDetected() {
		return iODViolationDetected;
	}

	public void setIODViolationDetected(String iODViolationDetected) {
		this.iODViolationDetected = iODViolationDetected;
	}

	public void setFacility(String facility) {
		this.facility = facility;
	}

	public String getFacility() {
		return facility;
	}

	public void setInstitutionAddress(String institutionAddress) {
		this.institutionAddress = institutionAddress;
	}

	public String getInstitutionAddress() {
		return institutionAddress;
	}
	public String getProcedureEventIX() {
		return procedureEventIX;
	}

	public void setProcedureEventIX(String procedureEventIX) {
		this.procedureEventIX = procedureEventIX;
	}

	public String getSpecSubSpecIX() {
		return specSubSpecIX;
	}

	public void setSpecSubSpecIX(String specSubSpecIX) {
		this.specSubSpecIX = specSubSpecIX;
	}
	public String getClassIX() {
		return classIX;
	}

	public void setClassIX(String classIX) {
		this.classIX = classIX;
	}

	public String getTiuNoteReference() {
		return tiuNoteReference;
	}

	public void setTiuNoteReference(String tiuNoteReference) {
		this.tiuNoteReference = tiuNoteReference;
	}
}
