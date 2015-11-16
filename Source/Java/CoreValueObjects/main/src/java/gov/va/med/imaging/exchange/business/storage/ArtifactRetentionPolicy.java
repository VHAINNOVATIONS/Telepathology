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

public class ArtifactRetentionPolicy implements PersistentEntity, Serializable {
	private static final long serialVersionUID = 1L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	//
	// Fields
	//
	private int id;
	private int artifactInstanceFK;
	private String createdDateTime;
	private boolean isActive;
//	private boolean isSatisfied; -- removed 09/05/11
	private String satisfiedDateTime;
	private int artifactId;
	private Artifact artifact;
	private int retentionPolicyId;
	private RetentionPolicy retentionPolicy;

	private List<RetentionPolicyFulfillment> retentionPolicyFulfillments;

	//
	// Default Constructor
	//
	public ArtifactRetentionPolicy() {
		this.retentionPolicyFulfillments = new ArrayList<RetentionPolicyFulfillment>();
	}

	//
	// Additional Constructors
	//
	public ArtifactRetentionPolicy(int id, int artifactInstanceFK,
			String createdDateTime, boolean isActive, // boolean isSatisfied, removed 09/05/11
			String satisfiedDateTime, int artifactId, int retentionPolicyId) {
		this.id = id;
		this.artifactInstanceFK = artifactInstanceFK;
		this.createdDateTime = createdDateTime;
		this.isActive = isActive;
//		this.isSatisfied = isSatisfied;
		this.satisfiedDateTime = satisfiedDateTime;
		this.artifactId = artifactId;
		this.retentionPolicyId = retentionPolicyId;
	}

	public boolean checkIsSatisfied(String siteNumber) {
		// Get the list of provider ids to which the artifact is already written
		ArrayList<Integer> writtenProviderIds = new ArrayList<Integer>();
		for (ArtifactInstance instance : getArtifact().getArtifactInstances()) {
			if (!writtenProviderIds.contains(instance.getProviderId())) {
				writtenProviderIds.add(instance.getProviderId());
			}
		}
		// Get the list of provider ids to which it should be written for this
		// policy
		ArrayList<Integer> targetProviderIds = new ArrayList<Integer>();
		for (RetentionPolicyProviderMapping rppm : getRetentionPolicy()
				.getRetentionPolicyProviderMappings()) {
			if (rppm.getPlaceId() ==  StorageServerDatabaseConfiguration.getConfiguration().getPlace(siteNumber).getId()
					&& !targetProviderIds.contains(rppm.getProviderId())) {
				targetProviderIds.add(rppm.getProviderId());
			}
		}
		// compare the two lists
		return writtenProviderIds.containsAll(targetProviderIds);
	}

	public Artifact getArtifact() {
		return artifact;
	}

	public int getArtifactId() {
		return artifactId;
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

	public int getArtifactInstanceFK() {
		return artifactInstanceFK;
	}

	public String getCreatedDateTime() {
		return createdDateTime;
	}

	public RetentionPolicy getRetentionPolicy() {
		return retentionPolicy;
	}

	public List<RetentionPolicyFulfillment> getRetentionPolicyFulfillments() {
		return retentionPolicyFulfillments;
	}

	public int getRetentionPolicyId() {
		return retentionPolicyId;
	}

	public String getSatisfiedDateTime() {
		return satisfiedDateTime;
	}

	public boolean isActive() {
		return isActive;
	}

//	public boolean isSatisfied() {
//		return ((createdDateTime!=null) && (!createdDateTime.isEmpty()));
//	}

	public void setArtifact(Artifact artifact) {
		this.artifact = artifact;
		this.artifactId = artifact.getId();
	}

	public void setArtifactId(int artifactId) {
		this.artifactId = artifactId;
	}

	public void setArtifactInstanceFK(int artifactInstanceFK) {
		this.artifactInstanceFK = artifactInstanceFK;
	}

	public void setCreatedDateTime(String createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}

//	public void setIsSatisfied(boolean isSatisfied) {
//		this.isSatisfied = isSatisfied;
//	}

	public void setRetentionPolicy(RetentionPolicy retentionPolicy) {
		this.retentionPolicy = retentionPolicy;
		this.retentionPolicyId = retentionPolicy.getId();
	}

	public void setRetentionPolicyFulfillments(
			List<RetentionPolicyFulfillment> retentionPolicyFulfillments) {
		this.retentionPolicyFulfillments = retentionPolicyFulfillments;
	}

	public void setRetentionPolicyId(int retentionPolicyId) {
		this.retentionPolicyId = retentionPolicyId;
	}

	public void setSatisfiedDateTime(String satisfiedDateTime) {
		this.satisfiedDateTime = satisfiedDateTime;
	}
}
