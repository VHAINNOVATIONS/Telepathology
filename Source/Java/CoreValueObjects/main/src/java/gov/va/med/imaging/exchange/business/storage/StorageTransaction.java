/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Dec 01, 2007
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWLOUTHJ
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
package gov.va.med.imaging.exchange.business.storage;

import gov.va.med.imaging.exchange.business.PersistentEntity;

import java.io.Serializable;
import java.util.Date;

public class StorageTransaction implements PersistentEntity, Serializable
{

	private static final long serialVersionUID = 1L;
	
	public static final String READ_TRANSACTION = "R";
	public static final String WRITE_TRANSACTION = "W";
	public static final String CONFIGURATION_TRANSACTION = "C";

	public static final String SUCCESS = "1";
	public static final String FAILURE = "0";
	
	//
	// Fields
	//
	private int id;
	private String transactionType;
	private String status;
	private String message;
	private Date transactionDateTime;
	private String initApp;
	private int artifactId;
	private Artifact artifact;
	private int providerId;
	private Provider provider;

	//
	// Default Constructor
	//
	public StorageTransaction()
	{
	}

	//
	// Additional Constructors
	//
	public StorageTransaction(String transactionType, String status,
			String message, Date transactionDateTime, String initApp,
			Artifact artifact, Provider provider) {
		this.transactionType = transactionType;
		this.status = status;
		this.message = message;
		this.transactionDateTime = transactionDateTime;
		this.initApp = initApp;
		this.artifact = artifact;
		this.provider = provider;
	}

	public StorageTransaction(int id, String transactionType, String status,
			String message, Date transactionDateTime, String initApp,
			int artifactId, int providerId) 
	{
		this.id = id;
		this.transactionType = transactionType;
		this.status = status;
		this.message = message;
		this.transactionDateTime = transactionDateTime;
		this.initApp = initApp;
		this.artifactId = artifactId;
		this.providerId = providerId;
	}

	// 
	// Properties
	//
    public int getId() {
		return id;
	}
    
	public void setId(int id) {
		this.id = id;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getTransactionDateTime() {
		return transactionDateTime;
	}

	public void setTransactionDateTime(Date transactionDateTime) {
		this.transactionDateTime = transactionDateTime;
	}

	public String getInitApp() {
		return initApp;
	}

	public void setInitApp(String initApp) {
		this.initApp = initApp;
	}

	public int getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(int artifactId) {
		this.artifactId = artifactId;
	}

	public Artifact getArtifact() {
		return artifact;
	}

	public void setArtifact(Artifact artifact) {
		this.artifact = artifact;
		this.artifactId = artifact.getId();
	}

	public int getProviderId() {
		return providerId;
	}

	public void setProviderId(int providerId) {
		this.providerId = providerId;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
		this.providerId = provider.getId();
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
