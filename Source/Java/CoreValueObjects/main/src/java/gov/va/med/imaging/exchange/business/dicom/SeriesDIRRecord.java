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
public class SeriesDIRRecord extends DicomDIRRecord {

	
	private String seriesInstanceUID = null;
	private String seriesNumber = null;
	private String modality= null;
	private String facility = null;
	private String institutionAddress = null;
	private String seriesDescription = null;
	private Vector<ImageDIRRecord> images = null;
	
	
	/**
	 * 
	 */
	public SeriesDIRRecord() {
		super();
	}
	
	
	/**
	 * @return the seriesInstanceUID
	 */
	public String getSeriesInstanceUID() {
		return seriesInstanceUID;
	}
	/**
	 * @param seriesInstanceUID the seriesInstanceUID to set
	 */
	public void setSeriesInstanceUID(String seriesInstanceUID) {
		this.seriesInstanceUID = seriesInstanceUID;
	}
	/**
	 * @return the seriesNumber
	 */
	public String getSeriesNumber() {
		return seriesNumber;
	}
	/**
	 * @param seriesNumber the seriesNumber to set
	 */
	public void setSeriesNumber(String seriesNumber) {
		this.seriesNumber = seriesNumber;
	}
	/**
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
	 * @return the images
	 */
	public Vector<ImageDIRRecord> getImages() {
		return images;
	}
	/**
	 * @param images the images to set
	 */
	public void setImages(Vector<ImageDIRRecord> images) {
		this.images = images;
	}
	
	
	public void addImage(ImageDIRRecord record){
		if(this.images == null){
			this.images = new Vector<ImageDIRRecord>();
		}
		this.images.add(record);
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


	public void setSeriesDescription(String seriesDescription) {
		this.seriesDescription = seriesDescription;
	}


	public String getSeriesDescription() {
		return seriesDescription;
	}

	
	
}
