/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 5, 2011
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswpeterb
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

import java.util.Vector;

/**
 * @author vhaiswpeterb
 *
 */
public class PatientDIRRecord extends DicomDIRRecord {

	private String patientName = null;
	private String patientID = null;
	private String patientSex = null;
	private String dob = null;
	
	private Vector<StudyDIRRecord> studies = null;
	
	
	/**
	 * 
	 */
	public PatientDIRRecord() {
		super();
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
	 * @return the patientID
	 */
	public String getPatientID() {
		return patientID;
	}
	/**
	 * @param patientID the patientID to set
	 */
	public void setPatientID(String patientID) {
		this.patientID = patientID;
	}
	/**
	 * @return the studies
	 */
	public Vector<StudyDIRRecord> getStudies() {
		return studies;
	}
	/**
	 * @param studies the studies to set
	 */
	public void setStudies(Vector<StudyDIRRecord> studies) {
		this.studies = studies;
	}
	
	public void addStudy(StudyDIRRecord record){
		if(this.studies == null){
			this.studies = new Vector<StudyDIRRecord>();
		}
		this.studies.add(record);
	}


	public void setDob(String dob) {
		this.dob = dob;
	}


	public String getDob() {
		return dob;
	}


	public void setPatientSex(String patientSex) {
		this.patientSex = patientSex;
	}


	public String getPatientSex() {
		return patientSex;
	}

	
}
