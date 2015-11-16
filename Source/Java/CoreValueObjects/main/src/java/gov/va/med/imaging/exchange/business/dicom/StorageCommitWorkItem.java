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

import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;
import gov.va.med.imaging.exchange.business.dicom.StorageCommitElement;
import gov.va.med.imaging.exchange.business.PersistentEntity;

/**
 * @author vhaiswtittoc
 *
 */
public class StorageCommitWorkItem implements PersistentEntity, Serializable {

	private static final long serialVersionUID = 4088963871460310856L;
	private int id;
	private String applicationName; // DICOM AE Sec MX app name
	private String transactionUID; // of the SC request
	private List<StorageCommitElement> sCElements = new ArrayList<StorageCommitElement>();
	private Long responseTimeStamp; // at or after which a response must be returned to sender SCU
	private String status; // RECEIVED, IN-PROGRESS, SUCCESS, FAILURE, SUCCESS SENT,  FAILURE SENT or  SENDING RESPONSE FAILED
	private String hostName=""; // tags the local node as the owner of the request for processing
	private int retriesLeft=3; // tags the local node as the owner of the request for processing -- 0..99
	private boolean doProcess=false; // if true Submit/Get requests instant lookup before RPC returns results
	private String moveAE=""; // DICOM Move AE of Vista where SC requester application can retrieve committed SOP Instances

	public StorageCommitWorkItem(){
		super();
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the applicationName
	 */
	public String getApplicationName() {
		return applicationName;
	}
	/**
	 * @param appName the applicationName to set
	 */
	public void setApplicationName(String appName) {
		this.applicationName = appName;
	}

	public void setTransactionUID(String taUID) {
		this.transactionUID = taUID;
	}


	public String getTransactionUID() {
		return transactionUID;
	}

	public List<StorageCommitElement> getStorageCommitElements()
	{
		return sCElements;
	}

	public void setElements(List<StorageCommitElement> sCElems)
	{
		this.sCElements = sCElems;
	}

	public Long getResponseTimeStamp() {
		return responseTimeStamp;
	}

	public void setResponseTimeStamp(Long responseTimeStamp) {
		this.responseTimeStamp = responseTimeStamp;
	}	

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public int getRetriesLeft() {
		return retriesLeft;
	}

	public void setRetriesLeft(int retriesLeft) {
		this.retriesLeft = retriesLeft;
	}

	public boolean isDoProcess() {
		return doProcess;
	}

	public void setDoProcess(boolean doProcess) {
		this.doProcess = doProcess;
	}
	
	public String getMoveAE() {
		return moveAE;
	}

	public void setMoveAE(String moveAE) {
		this.moveAE = moveAE;
	}
}
