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
public class StudyDIRRecord extends DicomDIRRecord {

	private String studyInstanceUID = null;
	private String accessionNumber = null;
	private String studyNumber = null;
	private String studyDate = null;
	private String studyTime = null;
	private String studyDescription = null;
	private String modalitiesInStudy = null;
	private String referringPhysician = null;
	private Vector<SeriesDIRRecord> series = null;
	

	public String getStudyDate() {
		return studyDate;
	}


	public void setStudyDate(String studyDate) {
		this.studyDate = studyDate;
	}


	public String getStudyDescription() {
		return studyDescription;
	}


	public void setStudyDescription(String studyDescription) {
		this.studyDescription = studyDescription;
	}


	/**
	 * 
	 */
	public StudyDIRRecord() {
		super();
	}

	
	/**
	 * @return the studyInstanceUID
	 */
	public String getStudyInstanceUID() {
		return studyInstanceUID;
	}
	/**
	 * @param studyInstanceUID the studyInstanceUID to set
	 */
	public void setStudyInstanceUID(String studyInstanceUID) {
		this.studyInstanceUID = studyInstanceUID;
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
	/**
	 * @return the studyNumber
	 */
	public String getStudyNumber() {
		return studyNumber;
	}
	/**
	 * @param studyNumber the studyNumber to set
	 */
	public void setStudyNumber(String studyNumber) {
		this.studyNumber = studyNumber;
	}
	/**
	 * @return the series
	 */
	public Vector<SeriesDIRRecord> getSeries() {
		return series;
	}
	/**
	 * @param series the series to set
	 */
	public void setSeries(Vector<SeriesDIRRecord> series) {
		this.series = series;
	}
	
	public void addSeries(SeriesDIRRecord record){
		if(this.series == null){
			this.series = new Vector<SeriesDIRRecord>();
		}
		this.series.add(record);
	}


	public void setModalitiesInStudy(String modalitiesInStudy) {
		this.modalitiesInStudy = modalitiesInStudy;
	}


	public String getModalitiesInStudy() {
		return modalitiesInStudy;
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
	
}
