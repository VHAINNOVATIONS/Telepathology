/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: 
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

import gov.va.med.imaging.exchange.business.PersistentEntity;

import java.io.Serializable;

public class DicomCorrectInfo implements PersistentEntity, Serializable {

	private static final long serialVersionUID = -1115418512367151988L;
	private int id = -1;
	private int returnedStatus;
	private String returnedReason = null;
	private String filePath = null;
	private String failedReason = null;
	private String pid = null;
	private String patientName = null;
    private String nonDCMPatientName = null;
    private String accessionNumber = null;	
	private String caseNumb = null;
	private String GatewayLocation = null;
	private String imageUID = null;
	private String studyUID = null;
	private String serviceType = "";
	private String hostname = null;

	public DicomCorrectInfo(){

	}
	
	public DicomCorrectInfo(String path, String reason, String pid, String pName, String caseNumber,
							String imageUID, String studyUID, String hostname, 
							String nonDCMPatientName, String accessionNumber){
		this.filePath = path;
		this.failedReason = reason;
		this.pid = pid;
		this.patientName = pName;
		this.caseNumb = caseNumber;
		this.imageUID = imageUID;
		this.studyUID = studyUID;
		this.hostname = hostname;
		this.accessionNumber = accessionNumber;
		this.nonDCMPatientName = nonDCMPatientName;
	}

	
	public DicomCorrectInfo(String pid, String pName, String nonDCMPName, String caseNumb, String accession,
							String imageUID, String studyUID){
		this.pid = pid;	
		this.patientName = pName;
		this.nonDCMPatientName = nonDCMPName;
		this.caseNumb = caseNumb;
		this.accessionNumber = accession;
		this.imageUID = imageUID;
		this.studyUID = studyUID;
	}

	
    public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getFailedReason() {
		return failedReason;
	}
	public void setFailedReason(String failedReason) {
		this.failedReason = failedReason;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	public String getCaseNumb() {
		return caseNumb;
	}
	public void setCaseNumb(String caseNumb) {
		this.caseNumb = caseNumb;
	}
	public String getGatewayLocation() {
		return GatewayLocation;
	}
	public void setGatewayLocation(String gatewayLocation) {
		GatewayLocation = gatewayLocation;
	}
	public String getImageUID() {
		return imageUID;
	}
	public void setImageUID(String imageUID) {
		this.imageUID = imageUID;
	}
	public String getStudyUID() {
		return studyUID;
	}
	public void setStudyUID(String studyUID) {
		this.studyUID = studyUID;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public int getReturnedStatus() {
		return returnedStatus;
	}

	public void setReturnedStatus(int returnedStatus) {
		this.returnedStatus = returnedStatus;
	}

	public String getReturnedReason() {
		return returnedReason;
	}

	public void setReturnedReason(String returnedReason) {
		this.returnedReason = returnedReason;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	/**
	 * @return the nonDCMPatientName
	 */
	public String getNonDCMPatientName() {
		return nonDCMPatientName;
	}

	/**
	 * @param nonDCMPatientName the nonDCMPatientName to set
	 */
	public void setNonDCMPatientName(String nonDCMPatientName) {
		this.nonDCMPatientName = nonDCMPatientName;
	}

	/**
	 * @return the accessionNumber
	 */
	public String getAccessionNumber() {
		return accessionNumber;
	}

	/**
	 * @param accessionNumber the accessionNumber to set
	 */
	public void setAccessionNumber(String accessionNumber) {
		this.accessionNumber = accessionNumber;
	}	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DicomCorrectInfo [id=" + id + ", returnedStatus="
				+ returnedStatus + ", returnedReason=" + returnedReason
				+ ", filePath=" + filePath + ", failedReason=" + failedReason
				+ ", pid=" + pid + ", patientName=" + patientName
				+ ", nonDCMPatientName=" + nonDCMPatientName
				+ ", accessionNumber=" + accessionNumber + ", caseNumb="
				+ caseNumb + ", GatewayLocation=" + GatewayLocation
				+ ", imageUID=" + imageUID + ", studyUID=" + studyUID
				+ ", serviceType=" + serviceType + ", hostname=" + hostname + "]";
	}	
}
