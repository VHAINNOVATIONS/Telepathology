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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ArtifactInstance implements PersistentEntity, Serializable {

	private static final long serialVersionUID = 1L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	//
	// Fields
	//
	private int id;
	private String url;
	private String createdDateTime;
	private String lastAccessDateTime;
	private int artifactId;
	private Artifact artifact;
	private int providerId;
	private Provider provider;
	private Integer diskVolume; // Network Location pointer (for M, ptr to file #2005.2)
	private String filePath;		// file path on volume
	private String fileRef;		// file name without volume and/or path
	

	private List<RetentionPolicyFulfillment> retentionPolicyFulfillments;

	//
	// Default Constructor
	//
	public ArtifactInstance() {
		this.retentionPolicyFulfillments = new ArrayList<RetentionPolicyFulfillment>();
	}

	//
	// Additional Constructors
	//
	public ArtifactInstance(int id, String url, Integer diskVolume, String filePath, String fileRef,
			String creationDateTime, String lastAccessDateTime, int artifactId, int providerId) {
		this();
		this.id = id;
		this.url = url;
		this.diskVolume = diskVolume;
		this.filePath = filePath;
		this.fileRef = fileRef;
		this.createdDateTime = createdDateTime;
		this.lastAccessDateTime = lastAccessDateTime;
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

	public Artifact getArtifact() {
		return artifact;
	}

	public int getArtifactId() {
		return artifactId;
	}

	public String getCreatedDateTime() {
		return createdDateTime;
	}

	public String getLastAccessDateTime() {
		return lastAccessDateTime;
	}

	public Provider getProvider() {
		return provider;
	}

	public int getProviderId() {
		return providerId;
	}

	public List<RetentionPolicyFulfillment> getRetentionPolicyFulfillments() {
		return retentionPolicyFulfillments;
	}

	//
	// Properties
	//
	public String getUrl() {
		return url;
	}

	public void setArtifact(Artifact artifact) 
	{
		this.artifact = artifact;
		this.artifactId = artifact.getId();
	}

	public void setArtifactId(int artifactId) {
		this.artifactId = artifactId;
	}

	public void setCreationDateTime(String createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public void setLastAccessDateTime(String lastAccessDateTime) {
		this.lastAccessDateTime = lastAccessDateTime;
	}

	public void setProvider(Provider provider) 
	{
		this.provider = provider;
		this.providerId = provider.getId();
	}

	public void setProviderId(int providerId) 
	{
		this.providerId = providerId;
	}

	public void setRetentionPolicyFulfillments(
			List<RetentionPolicyFulfillment> retentionPolicyFulfillments) {
		this.retentionPolicyFulfillments = retentionPolicyFulfillments;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getDiskVolume() {
		return diskVolume;
	}

	public void setDiskVolume(Integer diskVolume) {
		this.diskVolume = diskVolume;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filPath) {
		this.filePath = filPath;
	}

	public String getFileRef() {
		return fileRef;
	}

	public void setFileRef(String fileRef) {
		this.fileRef = fileRef;
	}

}
