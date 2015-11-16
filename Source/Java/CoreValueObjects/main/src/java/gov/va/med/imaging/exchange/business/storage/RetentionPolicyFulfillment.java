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

public class RetentionPolicyFulfillment implements PersistentEntity, Serializable
{
	private static final long serialVersionUID = 1L;

	//
	// Fields
	//
	private int id;
	private String createdDateTime;
	private int artifactInstanceId;
	private ArtifactInstance artifactInstance;
	private int artifactRetentionPolicyId;
	private ArtifactRetentionPolicy artifactRetentionPolicy;


	//
	// Default Constructor
	//
	public RetentionPolicyFulfillment()
	{
	}


	//
	// Additional Constructors
	//
	public RetentionPolicyFulfillment(int id, String createdDateTime,
			int artifactInstanceId, int artifactRetentionPolicyId) 
	{
		this.id = id;
		this.createdDateTime = createdDateTime;
		this.artifactInstanceId = artifactInstanceId;
		this.artifactRetentionPolicyId = artifactRetentionPolicyId;
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

	public String getCreatedDateTime() {
		return createdDateTime;
	}


	public void setCreatedDateTime(String creationDateTime) {
		this.createdDateTime = creationDateTime;
	}


	public int getArtifactInstanceId() {
		return artifactInstanceId;
	}


	public void setArtifactInstanceId(int artifactInstanceId) {
		this.artifactInstanceId = artifactInstanceId;
	}


	public ArtifactInstance getArtifactInstance() {
		return artifactInstance;
	}


	public void setArtifactInstance(ArtifactInstance artifactInstance) {
		this.artifactInstance = artifactInstance;
		this.artifactInstanceId = artifactInstance.getId();
	}


	public int getArtifactRetentionPolicyId() {
		return artifactRetentionPolicyId;
	}


	public void setArtifactRetentionPolicyId(int artifactRetentionPolicyId) {
		this.artifactRetentionPolicyId = artifactRetentionPolicyId;
	}


	public ArtifactRetentionPolicy getArtifactRetentionPolicy() {
		return artifactRetentionPolicy;
	}


	public void setArtifactRetentionPolicy(
			ArtifactRetentionPolicy artifactRetentionPolicy) {
		this.artifactRetentionPolicy = artifactRetentionPolicy;
		this.artifactRetentionPolicyId = artifactRetentionPolicy.getId();
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
