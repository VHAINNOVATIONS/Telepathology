/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr 10, 2008
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
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
// import java.util.Date;

/**
 * Represents a patient Reference entity in persistence (DB).
 * 
 * @author vhaiswtittoc
 *
 */
public class InstanceFile implements PersistentEntity, Serializable, Comparable<InstanceFile>
{	
	private static final long serialVersionUID = -5185851367113539916L;
	
	private int id;
	private String ien;					// primary key in the DB
	private String sopInstanceIEN;		// foreign key to the SOP Instance record in the DB
	private String artifactToken;		// Unique identifier of BLOB from Storage sub-system's Artifact record
	private String isOriginal;			// is this the file entry for the original DICOM object ('Y' or 'N')
	private String isConfidential;		// is this instance confidential to display? ('Y' or 'N')
	private String deleteDateTime;		// when object was deleted (YYYYMMDD.HHMISS)
	private String deletedBy;			// person who deleted object
	private String deleteReason;		// (10..60 ch)
	private String imageType;			// Identifies image characteristics ORIGINAL/DERIVED, PRIMARY/SECONDARY, etc.
	private String derivationDesc;		// non compression related derivation explanation if object is derived
	private String compressionRatio;	// decimal string ("xx.x"), optionally ‘\’ separated for multiple values
	private String compressionMethod;	// ISO notations, optionally ‘\’ separated for multiple values
	private int artifactFileId; 		// added 09/13/11 ...
// read-only field(s)
	private String status;				// ‘ACTIVE’, ‘INACTIVE’ 

	
	public InstanceFile() 
	{
	}

	/**
	 * Create a new study
	 * @param aToken artifact Token from storage subsystem
	 * @param isOrigl is this the file entry for the original DICOM object
	 * @param delDT
	 * @param delBy
	 * @param imgType
	 * @param delReason
	 * @param derivDesc
	 * @param comprRatio
	 * @param comprMethod
	 * @param artifactFileId; // added 09/13/11 ...

	 */
	public InstanceFile(String aToken, String isOrigl, String isConfid, String delDT, String delBy,
						 String delReason, String imgType, String derivDesc, String comprRatio,
						 String comprMethod, int artifactFileId)
	{
		this.artifactToken = aToken;
		this.isOriginal = isOrigl;
		this.isConfidential = isConfid;
		this.deleteDateTime = delDT;
		this.deletedBy = delBy;
		this.imageType = imgType;
		this.deleteReason = delReason;
		this.derivationDesc = derivDesc;
		this.compressionRatio = comprRatio;
		this.compressionMethod = comprMethod;
		this.artifactFileId = artifactFileId;

	}
	
	public String toString() 
	{
		return  this.artifactToken + "; isOriginal=" + this.isOriginal + "; saveDateTime=" + this.deleteDateTime +
				"; deletedBy=" + this.deletedBy + "; delReason=" + this.deleteReason +
				"; imageType=" + this.imageType + "; derivationDesc=" + this.derivationDesc +
				"; compressionRatio=" + this.compressionRatio + "; compressionMethod=" + this.compressionMethod +
				"; artifactFileId=" + this.artifactFileId + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
			+ ((artifactToken == null) ? 0 : artifactToken.hashCode());
		return result;
	}

	@Override
	public int compareTo(InstanceFile that) 
	{
		return this.artifactToken.compareTo(that.artifactToken);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final InstanceFile other = (InstanceFile) obj;
		if (artifactToken == null) {
			if (other.artifactToken != null)
				return false;
		} else if (!artifactToken.equals(other.artifactToken))
			return false;
		return true;
	}	
	
    public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getArtifactToken() {
		return artifactToken;
	}

	public void setArtifactToken(String aToken) {
		artifactToken = aToken;
	}

	public String getIsOriginal() {
		return isOriginal;
	}

	public void setIsOriginal(String isOriginal) {
		this.isOriginal = isOriginal;
	}

	public String getIsConfidential() {
		return isConfidential;
	}

	public void setIsConfidential(String isConfidential) {
		this.isConfidential = isConfidential;
	}

	public String getDeleteDateTime() {
		return deleteDateTime;
	}

	public void setDeleteDateTime(String deleteDateTime) {
		this.deleteDateTime = deleteDateTime;
	}

	public String getDeletedBy() {
		return deletedBy;
	}

	public void setDeletedBy(String deletedBy) {
		this.deletedBy = deletedBy;
	}

	public String getDeleteReason() {
		return deleteReason;
	}

	public void setDeleteReason(String deleteReason) {
		this.deleteReason = deleteReason;
	}

	public String getImageType() {
		return imageType;
	}

	public void setImageType(String imgType) {
		this.imageType = imgType;
	}

	public String getDerivationDesc() {
		return derivationDesc;
	}

	public void setDerivationDesc(String derivationDesc) {
		this.derivationDesc = derivationDesc;
	}

	public String getCompressionRatio() {
		return compressionRatio;
	}

	public void setCompressionRatio(String compressionRatio) {
		this.compressionRatio = compressionRatio;
	}

	public String getCompressionMethod() {
		return compressionMethod;
	}

	public void setCompressionMethod(String compressionMethod) {
		this.compressionMethod = compressionMethod;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String stat) {
		this.status=stat;
	}

	public String getIEN()
	{
		return ien;
	}

	public void setIEN(String sopInstanceIEN)
	{
		this.ien = sopInstanceIEN;
	}

	public String getSOPInstanceIEN()
	{
		return sopInstanceIEN;
	}

	public void setSOPInstanceIEN(String sopInstIEN)
	{
		this.sopInstanceIEN = sopInstIEN;
	}

	public int getArtifactFileId() {
		return artifactFileId;
	}

	public void setArtifactFileId(int artifactFileId) {
		this.artifactFileId = artifactFileId;
	}
}
