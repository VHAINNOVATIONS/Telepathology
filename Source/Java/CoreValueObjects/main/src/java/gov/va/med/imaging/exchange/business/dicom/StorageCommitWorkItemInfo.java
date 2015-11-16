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
public class StorageCommitWorkItemInfo {

	private String scWIID;
	private String status; // RECEIVED, IN-PROGRESS, SUCCESS, FAILURE, SUCCESS SENT,  FAILURE SENT or  SENDING RESPONSE FAILED
	private Long responseTimeStamp; // at or after which a response must be returned to sender SCU

	public StorageCommitWorkItemInfo(){
		super();
		scWIID = null;
		status = null;
		responseTimeStamp=0L;
	}

	public String getScWIID() {
		return scWIID;
	}

	public void setScWIID(String scWIID) {
		this.scWIID = scWIID;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getResponseTimeStamp() {
		return responseTimeStamp;
	}

	public void setResponseTimeStamp(Long responseTimeStamp) {
		this.responseTimeStamp = responseTimeStamp;
	}

}
