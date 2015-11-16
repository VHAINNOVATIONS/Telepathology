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

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Represents a patient Reference entity in persistence (DB).
 * 
 * @author vhaiswtittoc
 *
 */
public class PatientRef implements PersistentEntity, Serializable, Comparable<PatientRef>
{	
	private static final long serialVersionUID = -5185851367113539916L;
	
	private int id;
	private String ien;					// Primary key in the DB
	private String enterprisePatientId;	// ICN, DFN or MRN
	private String assigningAuthority;	// (V)A, (D)oD, (I)HS
	private String creatingEntity;		// site/location within authority
	private String idType;				// (M)RN or (I)CN or (D)FN
	// here are Read Only attributes originated from non-VI domains; they have Getters only and they must have one underlying DB call
	// that refreshes them -- GetNonVIAttributes();
	private String patientName="n/a";	// "last^first^MI^prefixes^postfixes
	private String patientID="n/a";		// typically SS
	private String patientDOB="n/a";	// YYYYMMDD
	private String patientGender="n/a";	// "M", "F", "O"
	
	/**
	 * Create a new patient
	 * @param ePId Enterprise Patient ID
	 * @param authority Assigning Authority (V, D, I)
	 * @param entity Creating Entity (Site/station/division/location) within Authority
	 * @param idType ID Type (D for DFN)
	 */
	public PatientRef(String ePId, String authority, String entity, String idtype)
	{
		this.enterprisePatientId = ePId;
		this.assigningAuthority = authority;
		this.creatingEntity = entity;
		this.idType = idtype;
	}
	
	/**
	 * Returns the enterprise patient id
	 * @return
	 */
	public String getEnterprisePatientId() {
		return enterprisePatientId;
	}

	/**
	 * Returns the assigning authority
	 * @return
	 */
	public String getAssigningAuthority() {
		return assigningAuthority;
	}

	/**
	 * Returns the creating entity
	 * @return
	 */
	public String getCreatingEntity() {
		return creatingEntity;
	}

	/**
	 * Returns the type of the Enterprise ID
	 * @return
	 */
	public String getIdType() {
		return idType;
	}

	@Override
	public String toString() 
	{
		return this.enterprisePatientId + " (ID Type=" + this.idType + "; Assigning Authority=" + this.assigningAuthority +"; Creating Entity=" + this.creatingEntity + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((assigningAuthority == null) ? 0 : assigningAuthority.hashCode());
		result = prime * result
				+ ((creatingEntity == null) ? 0 : creatingEntity.hashCode());
		result = prime * result
				+ ((idType == null) ? 0 : idType.hashCode());
		result = prime * result
				+ ((enterprisePatientId == null) ? 0 : enterprisePatientId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final PatientRef other = (PatientRef) obj;
		if (assigningAuthority == null) {
			if (other.assigningAuthority != null)
				return false;
		} else if (!assigningAuthority.equals(other.assigningAuthority))
			return false;
		if (creatingEntity == null) {
			if (other.creatingEntity != null)
				return false;
		} else if (!creatingEntity.equals(other.creatingEntity))
			return false;
		if (idType == null) {
			if (other.idType != null)
				return false;
		} else if (!idType.equals(other.idType))
			return false;
		if (enterprisePatientId == null) {
			if (other.enterprisePatientId != null)
				return false;
		} else if (!enterprisePatientId.equals(other.enterprisePatientId))
			return false;
		return true;
	}	
	
	@Override
	public int compareTo(PatientRef that) 
	{
		int i = this.enterprisePatientId.compareTo(that.enterprisePatientId);
		if (i==0)
			i = this.idType.compareTo(that.idType);
		if (i==0)
			i = this.creatingEntity.compareTo(that.creatingEntity);
		if (i==0)
			i = this.assigningAuthority.compareTo(that.assigningAuthority);
		return i; 
	}
	
	/**
	 * Returns nonVI attributes
	 * @return
	 */
    public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Boolean getNonVIAttributes() {
		// DB call needed to fetch nonVI attributes
		return false;
	}

	public String getPatientName() {
		return patientName;
	}

	public String getPatientID() {
		return patientID;
	}

	public String getPatientDOB() {
		return patientDOB;
	}

	public String getPatientGender() {
		return patientGender;
	}

	public String getIEN()
	{
		return ien;
	}

	public void setIEN(String ien)
	{
		this.ien = ien;
	}

	public void setEnterprisePatientId(String enterprisePatientId)
	{
		this.enterprisePatientId = enterprisePatientId;
	}

}
