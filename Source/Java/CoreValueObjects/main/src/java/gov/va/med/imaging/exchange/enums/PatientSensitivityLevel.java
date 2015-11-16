/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 1, 2009
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswwerfej
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
package gov.va.med.imaging.exchange.enums;

/**
 * @author vhaiswwerfej
 *
 */
public enum PatientSensitivityLevel 
{
	DATASOURCE_FAILURE(-1, "RPC/API failed", false),
	NO_ACTION_REQUIRED(0, "No display/action required", false),
	DISPLAY_WARNING(1, "Display warning message", false),
	DISPLAY_WARNING_REQUIRE_OK(2, "Display warning message, require OK to continue. Log access", true),
	DISPLAY_WARNING_CANNOT_CONTINUE(3, "Access to record denied, Accessing own Patient file record", false),
	ACCESS_DENIED(4, "Access to Patient file (#2) records denied, SSN not defined", false);
	
	final int code;
	final String description;
	final boolean loggingRequired;
	
	PatientSensitivityLevel(int code, String description, boolean loggingRequired)
	{
		this.code = code;
		this.description = description;
		this.loggingRequired = loggingRequired;
	}
	
	public static PatientSensitivityLevel getPatientSensitivityLevel(int code)
	{
		for(PatientSensitivityLevel level : PatientSensitivityLevel.values())
		{
			if(level.code == code)
				return level;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() 
	{
		return code + ":" + description;
	}

	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the loggingRequired
	 */
	public boolean isLoggingRequired() {
		return loggingRequired;
	}	
}
