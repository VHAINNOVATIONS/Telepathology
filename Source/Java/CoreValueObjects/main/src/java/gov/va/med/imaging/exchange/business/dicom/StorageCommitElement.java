/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr 5, 2012
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswtittoc
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
 * @author vhaiswtittoc
 *
 */
public class StorageCommitElement {

	private String sopClassUid;
	private String sopInstanceUID;
	private char commitStatus; // null, 'U' for uncomitted/not found or 'C' for committed
	private char failureReason; // ' ', or 'P', 'R', 'N', 'D'
/*	'P' for 0110H - Processing failure
		A general failure in processing the operation was encountered.
	'N' for 0112H - No such object instance
		The element was	not found in DB.
	'R' for 0213H - Resource limitation
		The SCP does not currently have enough resources to store the requested	SOP Instance(s).
	0122H - Referenced SOP Class not supported
		Storage Commitment has been requested for a SOP Instance with a SOP	Class that is not supported by the SCP.
	0119H - Class / Instance conflict
		The SOP Class of an element in the Referenced SOP Instance Sequence did	not correspond to the SOP class registered for this SOP Instance at the SCP.
	'D' for 0131H - Duplicate transaction UID
		The Transaction UID of the Storage Commitment Request is already in use. 
*/	
	public StorageCommitElement(){
		super();
		sopClassUid = null;
		sopInstanceUID = null;
		commitStatus = 'U';
		failureReason = ' ';
	}
	
	/**
	 * @return the sopInstanceUID
	 */
	public String getSopInstanceUID() {
		return sopInstanceUID;
	}
	/**
	 * @param sopInstanceUID the sopInstanceUID to set
	 */
	public void setSOPInstanceUID(String sopInstanceUID) {
		this.sopInstanceUID = sopInstanceUID;
	}

	public void setSopClassUid(String sopClassUid) {
		this.sopClassUid = sopClassUid;
	}


	public String getSopClassUid() {
		return sopClassUid;
	}

	public char getCommitStatus() {
		return commitStatus;
	}


	public void setCommitStatus(char commitStatus) {
		this.commitStatus = commitStatus;
	}


	public char getFailureReason() {
		return failureReason;
	}


	public void setFailureReason(char failureReason) {
		this.failureReason = failureReason;
	}



	
}
