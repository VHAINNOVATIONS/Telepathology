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

public class Artifact implements PersistentEntity, Serializable
{
	private static final long serialVersionUID = 1L;

	//
	// Fields
	//
	private int id;
	private String artifactToken;
	private long sizeInBytes;
	private String CRC;
	private String createdBy;
	private String createdDateTime;
	private String lastAccessDateTime;
	private int artifactDescriptorId;
	private ArtifactDescriptor artifactDescriptor;
	private int keyListId;
	private List<Key> keyList;
	private List<ArtifactInstance> artifactInstances = new ArrayList<ArtifactInstance>();
	private List<ArtifactRetentionPolicy> artifactRetentionPolicies = new ArrayList<ArtifactRetentionPolicy>();

	//
	// Default Constructor
	//
	public Artifact()
	{
		this.artifactInstances = new ArrayList<ArtifactInstance>();
		this.artifactRetentionPolicies = new ArrayList<ArtifactRetentionPolicy>();
	}

	//
	// Additional Constructors
	//
	public Artifact(int id, String artifactToken, int sizeInBytes, String cRC,
			String createdBy, String creationDateTime, String lastAccessDateTime,
			int artifactDescriptorId, int keyListId) 
	{
		this();
		this.id = id;
		this.artifactToken = artifactToken;
		this.sizeInBytes = sizeInBytes;
		this.CRC = cRC;
		this.createdBy = createdBy;
		this.createdDateTime = creationDateTime;
		this.lastAccessDateTime = lastAccessDateTime;
		this.artifactDescriptorId = artifactDescriptorId;
		this.keyListId = keyListId;
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

	public String getArtifactToken() {
		return artifactToken;
	}

	public void setArtifactToken(String artifactToken) {
		this.artifactToken = artifactToken;
	}

	public long getSizeInBytes() {
		return sizeInBytes;
	}

	public void setSizeInBytes(long sizeInBytes) {
		this.sizeInBytes = sizeInBytes;
	}

	public String getCRC() {
		return CRC;
	}

	public void setCRC(String cRC) {
		CRC = cRC;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(String createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public String getLastAccessDateTime() {
		return lastAccessDateTime;
	}

	public void setLastAccessDateTime(String lastAccessDateTime) {
		this.lastAccessDateTime = lastAccessDateTime;
	}

	public int getArtifactDescriptorId() {
		return artifactDescriptorId;
	}

	public void setArtifactDescriptorId(int artifactDescriptorId) {
		this.artifactDescriptorId = artifactDescriptorId;
	}

	public ArtifactDescriptor getArtifactDescriptor() {
		return artifactDescriptor;
	}

	public void setArtifactDescriptor(ArtifactDescriptor artifactDescriptor) {
		this.artifactDescriptor = artifactDescriptor;
		this.artifactDescriptorId = artifactDescriptor.getId();
	}

	public int getKeyListId() {
		return keyListId;
	}

	public void setKeyListId(int keyListId) {
		this.keyListId = keyListId;
	}

	public List<Key> getKeyList() 
	{
		// Sort it first, before returning
		List<Key> sortedList = new ArrayList<Key>();
		for (int i=1; i<=keyList.size(); i++)
		{
			for(Key key : keyList)
			{
				if (key.getLevel() == i) sortedList.add(key);
			}
		}
		return sortedList;
	}

	public void setKeyList(List<Key> keyList) {
		this.keyList = keyList;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<ArtifactInstance> getArtifactInstances() {
		return artifactInstances;
	}

	public void setArtifactInstances(List<ArtifactInstance> artifactInstances) {
		this.artifactInstances = artifactInstances;
	}

	public List<ArtifactRetentionPolicy> getArtifactRetentionPolicies() {
		return artifactRetentionPolicies;
	}

	public void setArtifactRetentionPolicies(
			List<ArtifactRetentionPolicy> artifactRetentionPolicies) {
		this.artifactRetentionPolicies = artifactRetentionPolicies;
	}
}
