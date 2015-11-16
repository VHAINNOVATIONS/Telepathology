/**
 * 
 */
package gov.va.med.imaging.dicom.common;

/**
 * @author vhaiswpeterb
 *
 */
public class DicomFileMetaInfo {
	
	String implementationClassUID = null;
	String implementationVersionName = null;
	String sourceAET = null;
	String transfersyntaxUID = null;

	
	/**
	 * @return the implementationClassUID
	 */
	public String getImplementationClassUID() {
		return implementationClassUID;
	}
	/**
	 * @param implementationClassUID the implementationClassUID to set
	 */
	public void setImplementationClassUID(String implementationClassUID) {
		this.implementationClassUID = implementationClassUID;
	}
	/**
	 * @return the implementationVersionName
	 */
	public String getImplementationVersionName() {
		return implementationVersionName;
	}
	/**
	 * @param implementationVersionName the implementationVersionName to set
	 */
	public void setImplementationVersionName(String implementationVersionName) {
		this.implementationVersionName = implementationVersionName;
	}
	/**
	 * @return the sourceAET
	 */
	public String getSourceAET() {
		return sourceAET;
	}
	/**
	 * @param sourceAET the sourceAET to set
	 */
	public void setSourceAET(String sourceAET) {
		this.sourceAET = sourceAET;
	}
	/**
	 * @return the transfersyntaxUID
	 */
	public String getTransfersyntaxUID() {
		return transfersyntaxUID;
	}
	/**
	 * @param transfersyntaxUID the transfersyntaxUID to set
	 */
	public void setTransfersyntaxUID(String transfersyntaxUID) {
		this.transfersyntaxUID = transfersyntaxUID;
	}
}
