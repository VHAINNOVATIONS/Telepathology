/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 8, 2012
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
package gov.va.med;

/**
 * This object represents a patient identifier
 * 
 * @author VHAISWWERFEJ
 *
 */
public class PatientIdentifier
{
	private final String value;
	private final PatientIdentifierType patientIdentifierType;
	
	public static PatientIdentifier icnPatientIdentifier(String value)
	{
		return new PatientIdentifier(value, PatientIdentifierType.icn);
	}
	
	public static PatientIdentifier dfnPatientIdentifier(String value)
	{
		return new PatientIdentifier(value, PatientIdentifierType.dfn);
	}
	
	/**
	 * 
	 * @param value The raw string value
	 * @param patientIdentifierType The type this value represents
	 */
	public PatientIdentifier(String value,
			PatientIdentifierType patientIdentifierType)
	{
		super();
		this.value = value;
		this.patientIdentifierType = patientIdentifierType;
	}

	public String getValue()
	{
		return value;
	}

	public PatientIdentifierType getPatientIdentifierType()
	{
		return patientIdentifierType;
	}

	@Override
	public String toString()
	{
		return patientIdentifierType.name() + "(" + value + ")";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((patientIdentifierType == null) ? 0 : patientIdentifierType
						.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PatientIdentifier other = (PatientIdentifier) obj;
		if (patientIdentifierType != other.patientIdentifierType)
			return false;
		if (value == null)
		{
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	public static PatientIdentifier fromString(String value)
	{
		if(value == null)
			return null;
		int loc = value.indexOf("(");
		if(loc < 0)
		{
			return new PatientIdentifier(checkAndRemoveTrailingDelimiter(value), PatientIdentifierType.icn);
		}
		String identifierTypeName = value.substring(0, loc);
		PatientIdentifierType patientIdentifierType =
				PatientIdentifierType.valueOf(identifierTypeName);
		if(patientIdentifierType == null)
			return null;
		String idValue = checkAndRemoveTrailingDelimiter(value.substring(loc + 1));
		return new PatientIdentifier(idValue, patientIdentifierType);
		
	}
	
	private static String checkAndRemoveTrailingDelimiter(String value)
	{
		if(value == null)
			return null;
		if(value.endsWith(")"))
			return value.substring(0, value.length() - 1);
		return value;
	}
}
