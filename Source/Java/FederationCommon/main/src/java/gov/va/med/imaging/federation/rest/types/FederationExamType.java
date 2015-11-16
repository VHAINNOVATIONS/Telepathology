/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 24, 2010
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
package gov.va.med.imaging.federation.rest.types;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author vhaiswwerfej
 *
 */
@XmlRootElement
public class FederationExamType 
{
	private String examId;
    private String patientIcn;
    private String siteNumber;
    private String patientName;
    private FederationExamStatusType examStatus;
    private String modality;
    private String cptCode;
    private String siteName;
    private String siteAbbr;
    private String radiologyReport = null;
    private String requisitionReport = null;
    private String presentationState = null;
    private String rawHeader1;
    private String rawHeader2;
    private String rawValue;
    private FederationExamImagesType examImages = null;
    
    public FederationExamType()
    {
    	super();
    }

	public String getExamId() {
		return examId;
	}

	public void setExamId(String examId) {
		this.examId = examId;
	}

	public String getPatientIcn() {
		return patientIcn;
	}

	public void setPatientIcn(String patientIcn) {
		this.patientIcn = patientIcn;
	}

	public String getSiteNumber() {
		return siteNumber;
	}

	public void setSiteNumber(String siteNumber) {
		this.siteNumber = siteNumber;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public FederationExamStatusType getExamStatus() {
		return examStatus;
	}

	public void setExamStatus(FederationExamStatusType examStatus) {
		this.examStatus = examStatus;
	}

	public String getModality() {
		return modality;
	}

	public void setModality(String modality) {
		this.modality = modality;
	}

	public String getCptCode() {
		return cptCode;
	}

	public void setCptCode(String cptCode) {
		this.cptCode = cptCode;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getSiteAbbr() {
		return siteAbbr;
	}

	public void setSiteAbbr(String siteAbbr) {
		this.siteAbbr = siteAbbr;
	}

	public String getRadiologyReport() {
		return radiologyReport;
	}

	public void setRadiologyReport(String radiologyReport) {
		this.radiologyReport = radiologyReport;
	}

	public String getRequisitionReport() {
		return requisitionReport;
	}

	public void setRequisitionReport(String requisitionReport) {
		this.requisitionReport = requisitionReport;
	}

	public String getPresentationState() {
		return presentationState;
	}

	public void setPresentationState(String presentationState) {
		this.presentationState = presentationState;
	}

	public String getRawHeader1() {
		return rawHeader1;
	}

	public void setRawHeader1(String rawHeader1) {
		this.rawHeader1 = rawHeader1;
	}

	public String getRawHeader2() {
		return rawHeader2;
	}

	public void setRawHeader2(String rawHeader2) {
		this.rawHeader2 = rawHeader2;
	}

	public String getRawValue() {
		return rawValue;
	}

	public void setRawValue(String rawValue) {
		this.rawValue = rawValue;
	}

	public FederationExamImagesType getExamImages() {
		return examImages;
	}

	public void setExamImages(FederationExamImagesType examImages) {
		this.examImages = examImages;
	}

}
