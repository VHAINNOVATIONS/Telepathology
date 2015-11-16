/**
 * Created on Sep 23, 2009
 */
package gov.va.med.imaging.exchange.business.dicom;

import java.io.Serializable;

/**
 *
 * @author vhaiswpeterb
 *
 */
public class UIDCheckInfo implements Serializable {

	private String pDFN;
	private String sAccessionNumber; // long acc#
	private String siteID;
	private String instrumentID;
	private String studyInstanceUID;
	private String seriesInstanceUID;
	private String sopInstanceUID;
	
	public UIDCheckInfo(String dfn, String accn, String siteId, String instrID, String studyUID, String serUID, String sopIUID){
		
		pDFN = dfn;
		sAccessionNumber = accn;
		siteID = siteId;
		instrumentID = instrID;
		studyInstanceUID = studyUID;
		seriesInstanceUID = serUID;
		sopInstanceUID = sopIUID;
	}
	
	
	public String getPatientDFN() {
		return pDFN;
	}
	public void setPDFN(String pdfn) {
		pDFN = pdfn;
	}
	public String getStudyAccessionNumber() {
		return sAccessionNumber;
	}
	public void setSAccessionNumber(String accessionNumber) {
		sAccessionNumber = accessionNumber;
	}
	public String getSiteID() {
		return siteID;
	}
	public void setSiteID(String siteId) {
		siteID = siteId;
	}
	public String getInstrumentID() {
		return instrumentID;
	}
	public void setInstrumentID(String instrID) {
		instrumentID = instrID;
	}
	public String getStudyInstanceUID() {
		return studyInstanceUID;
	}
	public void setStudyInstanceUID(String stdInstanceUID) {
		studyInstanceUID = stdInstanceUID;
	}
	public String getSeriesInstanceUID() {
		return seriesInstanceUID;
	}
	public void setSeriesInstanceUID(String serInstanceUID) {
		seriesInstanceUID = serInstanceUID;
	}
	public String getSOPInstanceUID() {
		return sopInstanceUID;
	}
	public void setSOPInstanceUID(String instanceUID) {
		sopInstanceUID = instanceUID;
	}
	
	
	
}
