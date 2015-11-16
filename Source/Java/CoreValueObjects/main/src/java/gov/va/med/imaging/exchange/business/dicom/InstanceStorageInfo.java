/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: 
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

import java.io.Serializable;

/**
 * Core Value Object class containing information to fetch files from Storage.  Image
 * and related files may be stored one of two methods.  The common method is having
 * the UNC path to the files on VistA Imaging Image Shares.  The new method introduced in 
 * Patch 112 is an alternative that uses artifact keys instead of the UNC path.
 * 
 * This class is populated by the Data Source.  Then the class is passed to a Router 
 * command that returns a stream regardless how the image was stored.  This class makes 
 * the storage method neutral.
 * 
 * @author vhaiswpeterb
 *
 */
public class InstanceStorageInfo implements Serializable {

	private static final long serialVersionUID = -3641945488344514578L;
	
	private String objectIdentifier = null;
	private String type = null;
	private String artifactKey = null;
	private String objectStorageIdentifier = null;
	private String objectSupportedTextStorageIdentifier = null;
	private String networkIEN = null;
	private String networkIENUsername = null;
	private String networkIENPassword = null;
	private String sopClassUID = null;
	
	/**
	 * Constructor
	 */
	public InstanceStorageInfo(){
		
	}


	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}


	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}


	/**
	 * @return the storageLocationIdentifier
	 */
	public String getObjectStorageIdentifier() {
		return objectStorageIdentifier;
	}


	/**
	 * @param storageLocationIdentifier the storageLocationIdentifier to set
	 */
	public void setObjectStorageIdentifier(String storageLocationIdentifier) {
		this.objectStorageIdentifier = storageLocationIdentifier;
	}


	/**
	 * @return the objectSupportedTextStorageIdentifier
	 */
	public String getObjectSupportedTextStorageIdentifier() {
		return objectSupportedTextStorageIdentifier;
	}

	/**
	 * @param objectSupportedTextStorageIdentifier the objectSupportedTextStorageIdentifier to set
	 */
	public void setObjectSupportedTextStorageIdentifier(
			String objectSupportedTextStorageIdentifier) {
		this.objectSupportedTextStorageIdentifier = objectSupportedTextStorageIdentifier;
	}

	
	/**
	 * @return the objectIdentifier
	 */
	public String getObjectIdentifier() {
		return objectIdentifier;
	}


	/**
	 * @param objectIdentifier the objectIdentifier to set
	 */
	public void setObjectIdentifier(String objectIdentifier) {
		this.objectIdentifier = objectIdentifier;
	}


	/**
	 * @return the sopClassUID
	 */
	public String getSopClassUID() {
		
		return sopClassUID;
	}


	/**
	 * @param sopClassUID the sopClassUID to set
	 */
	public void setSopClassUID(String sopClassUID) {
		this.sopClassUID = sopClassUID;
	}


	/**
	 * @return the artifactKey
	 */
	public String getArtifactKey() {
		return artifactKey;
	}


	/**
	 * @param artifactKey the artifactKey to set
	 */
	public void setArtifactKey(String artifactKey) {
		this.artifactKey = artifactKey;
	}


	/**
	 * @return the networkIENUsername
	 */
	public String getNetworkIENUsername() {
		return networkIENUsername;
	}


	/**
	 * @param networkIENUsername the networkIENUsername to set
	 */
	public void setNetworkIENUsername(String networkIENUsername) {
		this.networkIENUsername = networkIENUsername;
	}

	/**
	 * @return the networkIENPassword
	 */
	public String getNetworkIENPassword() {
		return networkIENPassword;
	}


	/**
	 * @param networkIENPassword the networkIENPassword to set
	 */
	public void setNetworkIENPassword(String networkIENPassword) {
		this.networkIENPassword = networkIENPassword;
	}


	/**
	 * @return the networkIEN
	 */
	public String getNetworkIEN() {
		return networkIEN;
	}


	/**
	 * @param networkIEN the networkIEN to set
	 */
	public void setNetworkIEN(String networkIEN) {
		this.networkIEN = networkIEN;
	}


}
