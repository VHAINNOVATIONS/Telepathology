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
 * Represents a patient who has been seen at a site.
 * 
 * @author vhaiswtittoc
 *
 */
public class ProcedureRef implements PersistentEntity, Serializable, Comparable<ProcedureRef>
{	
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String ien;					// Primary key in DB
	private String patientRefIEN;		// Foreign key to PatientRef record
	private String dicomAccessionNumber; // used only as matching entity on DICOM CStore reception loop

	private String procedureID;			// like Accession number
	private String assigningAuthority;	// (V)A, (D)oD, (I)HS
	private String creatingEntity;		// site/location within authority
	private String procedureIDType;		// File/Subscript for Vista proc record
//	private String procedureEventIX;	// pointer to 2005.85
//	private String specSubSpecIX;		// pointer to 2005.84 -- -ology/dept.
	private String packageIX;			// "RAD","LAB","MED","NOTE","CP","SUR","PHOTOID","NONE","CONS"
//	private String classIX;				// "CLIN","CLIN/ADMIN","ADMIN" or "ADMIN/CLIN"
	private String originIX;			// origin index term pointer -- must be at Study only	
//	private String procedure;
	private String procedureExamDateTime; // YYYYYMMDD.HHMISS
	// here are Read Only attributes originated from non-VI domains; they have Getters only and they must have one or two DB call
	// that refreshes them -- GetNonVIAttributes(); GetNonVIRadiologyReport()
	//      attributes populated by GetNonVIAttributes
	private String AccessionNumber="n/a";	//	
	private String procedureDescription="n/a";	//	
	private String procedureDateTime="n/a";	// 
	private String procedureCode="n/a";	//	
	private String procedureCodeTerminology="n/a";	// 
	private String codingAuthority="n/a";	//
	private String requestingPhysicianName="n/a";	//
//	private String confidentialityCode="n/a";	//
	private String specimenID="n/a";	//	
	private String specimenDescription="n/a";	//
	//      attribute populated by GetNonVIRadiologyReport()
	private String radiologyReport="n/a";	//	
	
	/**
	 * Create a new procedure Ref record in DB
	 * @param creatorId like accession number
	 * @param entity department
	 * @param idType 
	 */
	public ProcedureRef(String procedureID, String authority, String entity, String idtype, 	/* String ProcEventIx, String SpecSubSpecIx,*/ 
						String pacagekIx, 														/* String ClassIx, String originIX,*/ 
						String ProcExmaDatime)
	{
		this.procedureID = procedureID;
		this.assigningAuthority = authority;
		this.creatingEntity = entity;
		this.procedureIDType = idtype;
//		this.procedureEventIX = ProcEventIx;
//		this.specSubSpecIX = SpecSubSpecIx;
		this.packageIX = pacagekIx;
//		this.classIX = ClassIx;
//		this.originIX = originIX;
		this.procedureExamDateTime = ProcExmaDatime;
	}
	
    public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDicomAccessionNumber()
	{
		return dicomAccessionNumber;
	}

	public void setDicomAccessionNumber(String accessionNumber)
	{
		this.dicomAccessionNumber = accessionNumber;
	}	

	public String getProcedureID() {
		return procedureID;
	}

	public void setProcedureID(String ID) {
		this.procedureID = ID;
	}

	public String getAssigningAuthority() {
		return assigningAuthority;
	}

	public void setAssigningAuthority(String authority) {
		this.assigningAuthority = authority;
	}

	public String getCreatingEntity() {
		return creatingEntity;
	}

	public void setCreatingEntity(String creatingEntity) {
		this.creatingEntity = creatingEntity;
	}

	public String getProcedureIDType() {
		return procedureIDType;
	}

	public void setProcedureIDType(String IDType) {
		this.procedureIDType = IDType;
	}

//	public String getProcedureEventIX() {
//		return procedureEventIX;
//	}
//
//	public void setProcedureEventIX(String procedureEventIX) {
//		this.procedureEventIX = procedureEventIX;
//	}
//
//	public String getSpecSubSpecIX() {
//		return specSubSpecIX;
//	}
//
//	public void setSpecSubSpecIX(String specSubSpecIX) {
//		this.specSubSpecIX = specSubSpecIX;
//	}

	public String getPackageIX() {
		return packageIX;
	}

	public void setPackageIX(String packageIX) {
		this.packageIX = packageIX;
	}

//	public String getClassIX() {
//		return classIX;
//	}
//
//	public void setClassIX(String classIX) {
//		this.classIX = classIX;
//	}
//

	public String getOriginIX() {
		return originIX;
	}

	public void setOriginIX(String originIx) {
		this.originIX = originIx;
	}

	public String getProcedureExamDateTime() {
		return procedureExamDateTime;
	}

	public void setProcedureExamDateTime(String procedureExamDateTime) {
		this.procedureExamDateTime = procedureExamDateTime;
	}

	@Override
	public String toString() 
	{
		return this.procedureID + " (Procedure ID Type=" + this.procedureIDType + "; Creating Entity=" + this.creatingEntity + "; Assigning Authority=" + this.assigningAuthority + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((procedureID== null) ? 0 : procedureID.hashCode());
		result = prime * result + ((procedureIDType == null) ? 0 : procedureIDType.hashCode());
		result = prime * result + ((creatingEntity == null) ? 0 : creatingEntity.hashCode());
		result = prime * result + ((assigningAuthority == null) ? 0 : assigningAuthority.hashCode());
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
		final ProcedureRef other = (ProcedureRef) obj;
		if (procedureID == null) 
		{
			if (other.procedureID != null) return false;
		} 
		else if (!procedureID.equals(other.procedureID)) 
			return false;
		if (procedureIDType == null) {
			if (other.procedureIDType != null)
				return false;
		} else if (!procedureIDType.equals(other.procedureIDType))
			return false;
		if (creatingEntity == null) {
			if (other.creatingEntity != null)
				return false;
		} else if (!creatingEntity.equals(other.creatingEntity))
			return false;
		if (assigningAuthority == null) {
			if (other.assigningAuthority != null)
				return false;
		} else if (!assigningAuthority.equals(other.assigningAuthority))
			return false;

		return true;
	}

	@Override
	public int compareTo(ProcedureRef that) 
	{
		return this.procedureID.compareTo(that.procedureID);
	}

	/**
	 * Returns nonVI attributes but radiologyReport
	 * @return
	 */
	public Boolean getNonVIAttributes() {
		// DB call needed to fetch nonVI attributes
		return false;
	}
	public String getAccessionNumber() {
		return AccessionNumber;
	}

	public String getProcedureDescription() {
		return procedureDescription;
	}

	public String getProcedureDateTime() {
		return procedureDateTime;
	}

	public String getProcedureCode() {
		return procedureCode;
	}

	public String getSpecimenID() {
		return specimenID;
	}

	public String getSpecimenDescription() {
		return specimenDescription;
	}

	public String getProcedureCodeTerminology() {
		return procedureCodeTerminology;
	}

	public String getCodingAuthority() {
		return codingAuthority;
	}

	public String getRequestingPhysicianName() {
		return requestingPhysicianName;
	}

//	public String getConfidentialityCode() {
//		return confidentialityCode;
//	}
	public Boolean getNonVIRadiologyReport() {
		return false;
	}

	public String getRadiologyReport() {
		return radiologyReport;
	}

	public String getPatientRefIEN()
	{
		return patientRefIEN;
	}

	public void setPatientRefIEN(String patientRefIEN)
	{
		this.patientRefIEN = patientRefIEN;
	}

	public String getIEN()
	{
		return ien;
	}

	public void setIEN(String ien)
	{
		this.ien = ien;
	}



}
