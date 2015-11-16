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

public class ArtifactDescriptor implements PersistentEntity, Serializable
{
	private static final long serialVersionUID = 1L;

	//
	// Fields
	//
	private int id;
	private String artifactType;
	private String artifactFormat;
	private String fileExtension;
	private boolean isActive;
	private int retentionPolicyId;
	private RetentionPolicy retentionPolicy;

	//
	// Default Constructor
	//
	public ArtifactDescriptor()
	{
	}

	//
	// Additional Constructor(s)
	//
	public ArtifactDescriptor(int id, int retentionPolicyId, String artifactType, String artifactFormat, String fileExtension, boolean isActive) 
	{
		this.id = id;
		this.retentionPolicyId = retentionPolicyId;
		this.artifactType = artifactType;
		this.artifactFormat = artifactFormat;
		this.fileExtension = fileExtension;
		this.isActive = isActive;
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

	public String getArtifactType() {
		return artifactType;
	}

	public void setArtifactType(String artifactType) {
		this.artifactType = artifactType;
	}

	public String getArtifactFormat() {
		return artifactFormat;
	}

	public void setArtifactFormat(String artifactFormat) {
		this.artifactFormat = artifactFormat;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public int getRetentionPolicyId() {
		return retentionPolicyId;
	}

	public void setRetentionPolicyId(int retentionPolicyId) {
		this.retentionPolicyId = retentionPolicyId;
	}

	public RetentionPolicy getRetentionPolicy() {
		return retentionPolicy;
	}

	public void setRetentionPolicy(RetentionPolicy retentionPolicy) {
		this.retentionPolicy = retentionPolicy;
	}

	
	

}
