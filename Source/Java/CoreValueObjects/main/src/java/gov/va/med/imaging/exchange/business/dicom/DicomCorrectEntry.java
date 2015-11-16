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

public class DicomCorrectEntry implements PersistentEntity, Serializable
{

	private static final long serialVersionUID = 3294230328652445098L;

	private int id = -1;
	private String failedReason = null;
	private String originalPatientId = null;
	private String originalPatientName = null;
	private String originalCaseNumber = null;
    private String originalNonDCMPatientName = null;
    private String originalAccessionNumber = null;
	private String dateEntered = null;
    private String filePath = null;
    private String gatewayLocation = null;
    private String imageUID = null;
    private String studyUID = null;
    private String serviceType = null;
    private String hostname = null;
    private String instrumentNickName = null;
    private String instrumentService = null;
    private String correctedName = "";
    private String correctedSSN = "";
    private String correctedCaseNumber = "";
    private String correctedProcedureIEN = "";
    private String correctedProcedureDescription = "";
    private String correctedDOB = "";
    private String correctedSex = "";
    private String correctedICN = "";
    private String correctedDFN = "";
    private String failedImportDetails = null;
    private String transferSyntaxUid = "";
    private boolean fileToBeDeleted = false;

	//
    // Properties
    //
    public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFailedReason() {
		return failedReason;
	}
	public void setFailedReason(String failedReason) {
		this.failedReason = failedReason;
	}
	public String getOriginalPatientId() {
		return originalPatientId;
	}
	public void setOriginalPatientId(String originalPatientId) {
		this.originalPatientId = originalPatientId;
	}
	public String getOriginalPatientName() {
		return originalPatientName;
	}
	public void setOriginalPatientName(String originalPatientName) {
		this.originalPatientName = originalPatientName;
	}
	public String getOriginalCaseNumber() {
		return originalCaseNumber;
	}
	public void setOriginalCaseNumber(String originalCaseNumber) {
		this.originalCaseNumber = originalCaseNumber;
	}
	public String getDateEntered() {
		return dateEntered;
	}
	public void setDateEntered(String dateEntered) {
		this.dateEntered = dateEntered;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getGatewayLocation() {
		return gatewayLocation;
	}
	public void setGatewayLocation(String gatewayLocation) {
		this.gatewayLocation = gatewayLocation;
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
	public String getCorrectedName() {
		return correctedName;
	}
	public void setCorrectedName(String correctedName) {
		this.correctedName = correctedName;
	}
	public String getCorrectedSSN() {
		return correctedSSN;
	}
	public void setCorrectedSSN(String correctedSSN) {
		this.correctedSSN = correctedSSN;
	}
	public String getCorrectedCaseNumber() {
		return correctedCaseNumber;
	}
	public void setCorrectedCaseNumber(String correctedCaseNumber) {
		this.correctedCaseNumber = correctedCaseNumber;
	}
	public String getCorrectedProcedureIEN() {
		return correctedProcedureIEN;
	}
	public void setCorrectedProcedureIEN(String correctedProcedureIEN) {
		this.correctedProcedureIEN = correctedProcedureIEN;
	}
	public String getCorrectedProcedureDescription() {
		return correctedProcedureDescription;
	}
	public void setCorrectedProcedureDescription(
			String correctedProcedureDescription) {
		this.correctedProcedureDescription = correctedProcedureDescription;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	/**
	 * @return the correctedDOB
	 */
	public String getCorrectedDOB() {
		return correctedDOB;
	}
	/**
	 * @param correctedDOB the correctedDOB to set
	 */
	public void setCorrectedDOB(String correctedDOB) {
		this.correctedDOB = correctedDOB;
	}
	/**
	 * @return the correctedSex
	 */
	public String getCorrectedSex() {
		return correctedSex;
	}
	/**
	 * @param correctedSex the correctedSex to set
	 */
	public void setCorrectedSex(String correctedSex) {
		this.correctedSex = correctedSex;
	}
	/**
	 * @return the correctedICN
	 */
	public String getCorrectedICN() {
		return correctedICN;
	}
	/**
	 * @param correctedICN the correctedICN to set
	 */
	public void setCorrectedICN(String correctedICN) {
		this.correctedICN = correctedICN;
	}
	/**
	 * @return the correctedDFN
	 */
	public String getCorrectedDFN() {
		return correctedDFN;
	}
	/**
	 * @param correctedDFN the correctedDFN to set
	 */
	public void setCorrectedDFN(String correctedDFN) {
		this.correctedDFN = correctedDFN;
	}
	public String getInstrumentNickName() {
		return instrumentNickName;
	}
	public void setInstrumentNickName(String instrumentNickName) {
		this.instrumentNickName = instrumentNickName;
	}
	/**
	 * @return the originalNonDCMPatientName
	 */
	public String getOriginalNonDCMPatientName() {
		return originalNonDCMPatientName;
	}
	/**
	 * @param originalNonDCMPatientName the originalNonDCMPatientName to set
	 */
	public void setOriginalNonDCMPatientName(String originalNonDCMPatientName) {
		this.originalNonDCMPatientName = originalNonDCMPatientName;
	}
	/**
	 * @return the originalAccessionNumber
	 */
	public String getOriginalAccessionNumber() {
		return originalAccessionNumber;
	}
	/**
	 * @param originalAccessionNumber the originalAccessionNumber to set
	 */
	public void setOriginalAccessionNumber(String originalAccessionNumber) {
		this.originalAccessionNumber = originalAccessionNumber;
	}
	/**
	 * @return the fileToBeDeleted
	 */
	public boolean isFileToBeDeleted() {
		return fileToBeDeleted;
	}
	/**
	 * @param fileToBeDeleted the fileToBeDeleted to set
	 */
	public void setFileToBeDeleted(boolean fileToBeDeleted) {
		this.fileToBeDeleted = fileToBeDeleted;
	}
	public void setFailedImportDetails(String failedImportDetails) {
		this.failedImportDetails = failedImportDetails;
	}
	public String getFailedImportDetails() {
		return failedImportDetails;
	}
	public void setTransferSyntaxUid(String transferSyntaxUid) {
		this.transferSyntaxUid = transferSyntaxUid;
	}
	public String getTransferSyntaxUid() {
		return transferSyntaxUid;
	}
	public void setInstrumentService(String instrumentService) {
		this.instrumentService = instrumentService;
	}
	public String getInstrumentService() {
		return instrumentService;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DicomCorrectEntry [id=" + id + ", failedReason=" + failedReason
				+ ", originalPatientId=" + originalPatientId
				+ ", originalPatientName=" + originalPatientName
				+ ", originalCaseNumber=" + originalCaseNumber
				+ ", originalNonDCMPatientName=" + originalNonDCMPatientName
				+ ", originalAccessionNumber=" + originalAccessionNumber
				+ ", dateEntered=" + dateEntered + ", filePath=" + filePath
				+ ", gatewayLocation=" + gatewayLocation + ", imageUID="
				+ imageUID + ", studyUID=" + studyUID + ", serviceType="
				+ serviceType + ", hostname=" + hostname
				+ ", instrumentNickName=" + instrumentNickName
				+ ", correctedName=" + correctedName + ", correctedSSN="
				+ correctedSSN + ", correctedCaseNumber=" + correctedCaseNumber
				+ ", correctedProcedureIEN=" + correctedProcedureIEN
				+ ", correctedProcedureDescription="
				+ correctedProcedureDescription + ", correctedDOB="
				+ correctedDOB + ", correctedSex=" + correctedSex
				+ ", correctedICN=" + correctedICN + ", correctedDFN="
				+ correctedDFN + ", fileToBeDeleted=" + fileToBeDeleted + "]";
	}
}
