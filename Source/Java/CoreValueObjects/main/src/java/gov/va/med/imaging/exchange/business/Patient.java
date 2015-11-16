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
package gov.va.med.imaging.exchange.business;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a patient who has been seen at a site.
 * 
 * @author VHAISWWERFEJ
 *
 */
public class Patient 
implements Serializable, Comparable<Patient>
{	
	private static final long serialVersionUID = -5185851367113539916L;

	/**
	 * 
	 * @param name
	 * @param icn
	 * @param veteranStatus
	 * @param patientSex
	 * @param dob
	 * @return
	 */
	public static Patient create(
		String name, String icn, String veteranStatus, 
		PatientSex patientSex, Date dob, String ssn, String dfn,
		Boolean sensitive)
	{
		return new Patient(name, icn, veteranStatus, patientSex, 
				dob, ssn, dfn, sensitive);
	}
	
	private String patientName;
	private String patientIcn;
	private String veteranStatus;
	private PatientSex patientSex;
	private Date dob;	
	private String ssn;
	private String dfn;
	private Boolean sensitive;
	
	/**
	 * Create a new patient
	 * @param name Patient Name
	 * @param icn Patient ICN
	 * @param veteranStatus The veteran status of the patient
	 * @param patientSex The sex of the patient
	 * @param dob The date of birth of the patient
	 */
	public Patient(String name, String icn, String veteranStatus, 
			PatientSex patientSex, Date dob, String ssn, String dfn,
			Boolean sensitive)
	{
		this.patientName = name;
		this.patientIcn = icn;
		this.veteranStatus = veteranStatus;
		this.patientSex = patientSex;
		this.dob = dob;
		this.ssn = ssn;
		this.dfn = dfn;
		this.sensitive = sensitive;
	}
	
	public Patient() 
	{
		super();
	}

	public String getSsn()
	{
		return ssn;
	}

	public String getDfn()
	{
		return dfn;
	}

	/**
	 * Returns the patient name
	 * @return
	 */
	public String getPatientName() {
		return patientName;
	}

	/**
	 * Returns the Patient ICN
	 * @return
	 */
	public String getPatientIcn() {
		return patientIcn;
	}

	/**
	 * Returns the patients veteran status
	 * @return
	 */
	public String getVeteranStatus() {
		return veteranStatus;
	}

	/**
	 * Returns the patient Sex
	 * @return
	 */
	public PatientSex getPatientSex() {
		return patientSex;
	}

	/**
	 * Returns the patients date of birth
	 * @return
	 */
	public Date getDob() {
		return dob;
	}

	public Boolean getSensitive()
	{
		return sensitive;
	}

	@Override
	public String toString() 
	{
		return this.patientName + " (" + this.patientIcn + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((patientIcn == null) ? 0 : patientIcn.hashCode());
		result = prime * result
				+ ((patientName == null) ? 0 : patientName.hashCode());
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
		final Patient other = (Patient) obj;
		if (patientIcn == null) {
			if (other.patientIcn != null)
				return false;
		} else if (!patientIcn.equals(other.patientIcn))
			return false;
		if (patientName == null) {
			if (other.patientName != null)
				return false;
		} else if (!patientName.equals(other.patientName))
			return false;
		return true;
	}	
	
	@Override
	public int compareTo(Patient that) 
	{
		return this.patientName.compareTo(that.patientName);
	}
	
	/**
	 * Returns the SSN in *****1234 format
	 * @return
	 */
	public String getFilteredSsn()
	{
		if(ssn != null)
		{
			if(ssn.length() > 4)
			{
				// there must be an easier way to do this...
				//123456789
				String last4 = ssn.substring(ssn.length() - 4);
				StringBuilder sb = new StringBuilder();
				for(int i = 0; i < (ssn.length() - 4); i++)
				{
					sb.append("*");
				}
				sb.append(last4);
				int len = sb.length();
				if(len > 9)
				{
					// remove the too many extra leading characters
					return sb.substring(len - 9);
				}
				return sb.toString();
			}
			// not longer than 4 characters for some reason
			return ssn;
		}
		return "";
	}	
	
	/**
	 * Returns true if the patient has an ICN
	 * @return
	 */
	public boolean isPatientIcnIncluded()
	{
		if(patientIcn == null || patientIcn.length() <= 0 || patientIcn.startsWith("-1"))
			return false;
		return true;
	}

	/**
	 * Enumeration to determine patient Sex
	 * @author VHAISWWERFEJ
	 *
	 */
	public enum PatientSex
	{
		Male, Female, Unknown;
		
		public static PatientSex valueOfPatientSex(String patientSex)
		{
			if("Male".equalsIgnoreCase(patientSex.trim()) || "M".equalsIgnoreCase(patientSex.trim()))
			{
				return Male;
			}
			else if("Female".equalsIgnoreCase(patientSex.trim()) || "F".equalsIgnoreCase(patientSex.trim()))
			{
				return Female;
			}
			return Unknown;
		}

		@Override
		public String toString() {
			if(this == Male)
				return "Male";
			else if(this == Female)
				return "Female";
			else
				return "Unkown";
		}

		public String toDicomString() {
			if(this == Male)
				return "M";
			else if(this == Female)
				return "F";
			else
				return "O";
		}
	}
}
