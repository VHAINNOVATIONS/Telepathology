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

/**
 * @author vhaiswpeterb
 *
 */
public class ImageDIRRecord extends DicomDIRRecord {

	private String imageInstanceUID;
	private String instanceNumber;
	private String fileID;
	
	private String sopClassUid;
	private String transferSyntaxUid;
	private String imageNumber;
	private String numberOfFrames;
	
	public ImageDIRRecord(){
		super();
	}
	
	
	/**
	 * @return the imageInstanceUID
	 */
	public String getImageInstanceUID() {
		return imageInstanceUID;
	}
	/**
	 * @param imageInstanceUID the imageInstanceUID to set
	 */
	public void setImageInstanceUID(String imageInstanceUID) {
		this.imageInstanceUID = imageInstanceUID;
	}
	/**
	 * @return the fileID
	 */
	public String getFileID() {
		return fileID;
	}
	/**
	 * @param fileID the fileID to set
	 */
	public void setFileID(String fileID) {
		this.fileID = fileID;
	}
	/**
	 * @return the instanceNumber
	 */
	public String getInstanceNumber() {
		return instanceNumber;
	}
	/**
	 * @param instanceNumber the instanceNumber to set
	 */
	public void setInstanceNumber(String instanceNumber) {
		this.instanceNumber = instanceNumber;
	}


	public void setSopClassUid(String sopClassUid) {
		this.sopClassUid = sopClassUid;
	}


	public String getSopClassUid() {
		return sopClassUid;
	}


	public void setTransferSyntaxUid(String transferSyntaxUid) {
		this.transferSyntaxUid = transferSyntaxUid;
	}


	public String getTransferSyntaxUid() {
		return transferSyntaxUid;
	}


	public void setImageNumber(String imageNumber) {
		this.imageNumber = imageNumber;
	}


	public String getImageNumber() {
		return imageNumber;
	}


	public void setNumberOfFrames(String numberOfFrames) {
		this.numberOfFrames = numberOfFrames;
	}


	public String getNumberOfFrames() {
		return numberOfFrames;
	}
	
	
}
