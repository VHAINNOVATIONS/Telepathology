/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: April 13, 2006
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWTITTOC
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Hashtable;
import org.apache.log4j.Logger;

/**
 * Core Value Object class that contains information about a DICOM Application Entity.
 * This is based on the AE Security Matrix created in the Data Source.  This is used to
 * determine the permissions and flags assigned to a specific Application Entity.
 * 
 * @author Csaba Titton
 *
 */
public class DicomAE implements PersistentEntity, Serializable
{
	private static final long serialVersionUID = 4088963871460310856L;
	// Fields
	private int id;
	private String applicationName = null;		// max 128 char.: Unique (descriptive) DICOM Application Name
	private String remoteAETitle = null;		// max  16 char.: DICOM Application Entity Title (unique per local application instance)
	private String localAETitle = null;
	private String hostName = null;			// max  32 char.: the node this AE resides on (needed for remote SCPs only)
	private String port = null;				// max  10 char.: Port number of AE (needed for remote SCPs only)
	private String siteNumber = null;			// INSTITUTION file IEN of the site location where host of application reside
	
	private boolean forceReconciliation=false;	
	private String originIndex = null;
	
	private boolean rejectMessage = true;		// to remote AE on Store SCP IOD validation (default=true)
	private boolean warningMessage = true;		// to remote AE on duplicate/illegal UID (default=true)
	private boolean validateIODs = true;		// if true, validation will be performed
	private boolean relaxValidation = false;	// if true, minimum validation is enough for incoming objects to pass validation
	private boolean resourceError = true;		// to remote AE on DB, device. etc. access error (default=true)
	private ArrayList<ServiceAndRolePair> services = null;
	private String imagingService = null;

	//private String enabled;			// max   3 char.: ('Y', 'N')
	private int resultCode = 0;
	private String resultMessage = null;
	private boolean remoteAEValid = false;
	private String studyAssociation = null;// max   8 char.: 'PSPA', 'CSPA', 'UNKOWN' (for remote C-STORE SCUs only)
	private int studyTimeoutSeconds = 180;// max  5 digits

	private int dicomNResponseDelay = 24*3600;// one day in seconds
	private int dicomNRetriesLeft = 3;// this many as default retries with SC periodic command wake-up delay

	private static Logger logger = Logger.getLogger (DicomAE.class);

	public enum searchMode{REMOTE_AE, APP_NAME, SERVICE_AND_ROLE};
	
	private searchMode findMode = searchMode.REMOTE_AE;
	
	/**
	 * Explicit default constructor that is required because of the Dicom(URI) constructor. 
	 */
	public DicomAE()
	{
		super();
	}

	public DicomAE(String aeTitle, String siteNumber){
		this.remoteAETitle = aeTitle;
		this.siteNumber = siteNumber;
	}

	/**
	 * 
	 * @return Returns the aETitle.
	 */
	public String getApplicationName() {
		return applicationName;
	}
	/**
	 * @param title The appName to set.
	 */
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	
	/**
	 * 
	 * @return Returns the remoteAETitle.
	 */
	public String getRemoteAETitle() {
		return remoteAETitle;
	}
	/**
	 * @param title The remoteAETitle to set.
	 */
	public void setRemoteAETitle(String title) {
		this.remoteAETitle = title;
	}
	
	
	/**
	 * 
	 * @return Returns the hostName.
	 */
	public String getHostName() {
		return hostName;
	}
	/**
	 * @param hostName The hostName to set.
	 */
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	
	/**
	 * 
	 * @return Returns the port.
	 */
	public String getPort() {
		return port;
	}
	/**
	 * @param port The port to set.
	 */
	public void setPort(String port) {
		this.port = port;
	}
	
	/**
	 * 
	 * @return Returns the studyAssociation.
	 */
	public String getStudyAssociation() {
		return studyAssociation;
	}
	/**
	 * @param studyAssociation The studyAssociation to set.
	 */
	public void setStudyAssociation(String studyAssociation) {
		this.studyAssociation = studyAssociation;
	}
	
	/**
	 * 
	 * @return Returns the studyTimeoutSeconds.
	 */
	public int getStudyTimeoutSeconds() {
		return studyTimeoutSeconds;
	}
	/**
	 * @param studyTimeoutSeconds The studyTimeoutSeconds to set.
	 */
	public void setStudyTimeoutSeconds(int studyTimeoutSeconds) {
		this.studyTimeoutSeconds = studyTimeoutSeconds;
	}
		
	/**
	 * @return the location
	 */
	public String getSiteNumber() {
		return this.siteNumber;
	}
	/**
	 * @param location the location to set
	 */
	public void setSiteNumber(String siteNumber) {
		this.siteNumber = siteNumber;
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
	 * @return the resultCode
	 */
	public int getResultCode() {
		return resultCode;
	}

	/**
	 * @param resultCode the resultCode to set
	 */
	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
		if(resultCode > -1){
			this.setRemoteAEValid(true);
		}
		else{
			this.setRemoteAEValid(false);
		}
	}

	/**
	 * @return the resultMessage
	 */
	public String getResultMessage() {
		return resultMessage;
	}

	/**
	 * @param resultMessage the resultMessage to set
	 */
	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}

	/**
	 * @return the localAETitle
	 */
	public String getLocalAETitle() {
		return localAETitle;
	}

	/**
	 * @param localAETitle the localAETitle to set
	 */
	public void setLocalAETitle(String localAETitle) {
		this.localAETitle = localAETitle;
	}

	/**
	 * @return the remoteAEValid
	 */
	public boolean isRemoteAEValid() {
		return remoteAEValid;
	}

	/**
	 * @param remoteAEValid the remoteAEValid to set
	 */
	public void setRemoteAEValid(boolean remoteAEValid) {
		this.remoteAEValid = remoteAEValid;
	}

	/**
	 * @return the rejectMessage
	 */
	public boolean isRejectMessage() {
		return rejectMessage;
	}

	/**
	 * @param rejectMessage the rejectMessage to set
	 */
	public void setRejectMessage(boolean rejectMessage) {
		this.rejectMessage = rejectMessage;
	}

	/**
	 * @return the warningMessage
	 */
	public boolean isWarningMessage() {
		return warningMessage;
	}

	/**
	 * @param warningMessage the warningMessage to set
	 */
	public void setWarningMessage(boolean warningMessage) {
		this.warningMessage = warningMessage;
	}

	/**
	 * @return the validateIODs
	 */
	public boolean isValidateIODs() {
		return validateIODs;
	}

	/**
	 * @param validateIODs the validateIODs to set
	 */
	public void setValidateIODs(boolean validateIODs) {
		this.validateIODs = validateIODs;
	}

	/**
	 * @return the validateVRs
	 */
	public boolean isValidateVRs() {
		return validateIODs;
	}

	/**
	 * @return the relaxValidation
	 */
	public boolean isRelaxValidation() {
		return relaxValidation;
	}

	/**
	 * @param relaxValidation the relaxValidation to set
	 */
	public void setRelaxValidation(boolean relaxValidation) {
		this.relaxValidation = relaxValidation;
	}	
	
	public boolean isServiceAndRoleValid(String service, String role){
		if(services == null || services.isEmpty()){
			logger.error(this.getClass().getName()+": There are no DICOM Services assigned to this AE.");
			return false;
		}
		Iterator<ServiceAndRolePair> iter = this.services.iterator();
		while(iter.hasNext()){
			ServiceAndRolePair pair = (ServiceAndRolePair)iter.next();
			if(pair.getService().equalsIgnoreCase(service) && pair.getRole().equalsIgnoreCase(role)){
				return true;
			}
		}
		return false;		
	}
	
	public void addAEServiceAndRole(String service, String role){
		if(this.services == null){
			this.services = new ArrayList<ServiceAndRolePair>();
		}
		this.services.add(new ServiceAndRolePair(service, role));
	}

	/**
	 * @return the imagingService
	 */
	public String getImagingService() {
		return imagingService;
	}

	/**
	 * @param imagingService the imagingService to set
	 */
	public void setImagingService(String imagingService) {
		this.imagingService = imagingService;
	}

	/**
	 * @return the resourceError
	 */
	public boolean isResourceError() {
		return resourceError;
	}

	/**
	 * @param resourceError the resourceError to set
	 */
	public void setResourceError(boolean resourceError) {
		this.resourceError = resourceError;
	}

	public void setOriginIndex(String originIndex) {
		this.originIndex = originIndex;
	}

	public String getOriginIndex() {
		return originIndex;
	}

	public void setForceReconciliation(boolean forceReconciliation) {
		this.forceReconciliation = forceReconciliation;
	}

	public boolean isForceReconciliation() {
		return forceReconciliation;
	}



	private class ServiceAndRolePair{
		String service;
		String role;
		
		private ServiceAndRolePair(String service, String role){
			this.service = service;
			this.role = role;
		}
		
		/**
		 * @return the service
		 */
		public String getService() {
			return service;
		}
		/**
		 * @return the role
		 */
		public String getRole() {
			return role;
		}			
	}


	public int getDicomNResponseDelay() {
		return dicomNResponseDelay;
	}

	public void setDicomNResponseDelay(int dicomNResponseDelay) {
		this.dicomNResponseDelay = dicomNResponseDelay;
	}

	public int getDicomNRetriesLeft() {
		return dicomNRetriesLeft;
	}

	public void setDicomNRetriesLeft(int dicomNRetriesLeft) {
		this.dicomNRetriesLeft = dicomNRetriesLeft;
	}

	public searchMode getFindMode() {
		return findMode;
	}

	public void setFindMode(searchMode findMode) {
		this.findMode = findMode;
	}

}
