/**
 * Created on Sep 22, 2009
 */
package gov.va.med.imaging.exchange.business.dicom;

import java.io.Serializable;

/**
 *
 *
 *
 * @author vhaiswpeterb
 *
 */
public class PatientStudyInfo implements Serializable {

	
	private String patientDFN;
	private String patientName;
	private String patientID;
	private String patientBirthDate;
	private String patientSex;
	private String patientICN;
	private String studyAccessionNumber;
	private String studyImagingService; // RAD or CON
	private String siteID;

	public PatientStudyInfo() {}
	
	public PatientStudyInfo( String studyAccessionNumber, String studyImagingService, 
			String patientDFN, String patientName, String patientID, 
			String patientBirthDate, String patientSex, String patientICN) {
		this.patientDFN=patientDFN;
		this.patientName=patientName;
		this.patientID=patientID;
		this.patientBirthDate=patientBirthDate;
		this.patientSex=patientSex;
		this.patientICN=patientICN;
		this.studyAccessionNumber = studyAccessionNumber;
		this.studyImagingService = studyImagingService;
	}
	public String getPatientDFN() {
		return patientDFN;
	}
	public void setPatientDFN(String patientDFN) {
		this.patientDFN = patientDFN;
	}
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	public String getPatientID() {
		return patientID;
	}
	public void setPatientID(String patientID) {
		this.patientID = patientID;
	}
	public String getPatientBirthDate() {
		return patientBirthDate;
	}
	public void setPatientBirthDate(String patientBirthDate) {
		this.patientBirthDate = patientBirthDate;
	}
	public String getPatientSex() {
		return patientSex;
	}
	public void setPatientSex(String patientSex) {
		this.patientSex = patientSex;
	}
	public String getPatientICN() {
		return patientICN;
	}
	public void setPatientICN(String patientICN) {
		this.patientICN = patientICN;
	}
	public String getStudyAccessionNumber() {
		return studyAccessionNumber;
	}
	public void setStudyAccessionNumber(String studyAccessionNumber) {
		this.studyAccessionNumber = studyAccessionNumber;
	}
	public String getStudyImagingService() {
		return studyImagingService;
	}
	public void setStudyImagingService(String studyImagingService) {
		this.studyImagingService = studyImagingService;
	}

	public String getSiteID()
	{
		return siteID;
	}

	public void setSiteID(String siteID)
	{
		this.siteID = siteID;
	}
}
