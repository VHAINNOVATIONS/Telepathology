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
package gov.va.med.imaging.core.interfaces.exceptions;

import gov.va.med.PatientIdentifier;
import gov.va.med.imaging.exchange.business.PatientSensitiveValue;
import gov.va.med.imaging.exchange.enums.PatientSensitivityLevel;

/**
 * Exception thrown when the patient requested has a sensitivity level higher
 * than the allowed level by the requestor. This requires the requestor to 
 * verify with the user they want to view the data, log the access and then
 * request again with a higher level. This exception must be created
 * using the static createInsufficientPatientSensitivityException method.
 * 
 * @author vhaiswwerfej
 *
 */
public class InsufficientPatientSensitivityException 
extends MethodException 
{
	private static final long serialVersionUID = 1322088022382089890L;
	
	/**
	 * The actual sensitivity value for the patient (including warning message details)
	 */
	private final PatientSensitiveValue sensitiveValue;
	private final PatientIdentifier patientIdentifier;
	/**
	 * The level the user is allowed to view
	 */
	private final PatientSensitivityLevel allowedLevel;
	
	protected InsufficientPatientSensitivityException(String msg,
			PatientSensitiveValue sensitiveValue, PatientIdentifier patientIdentifier,
			PatientSensitivityLevel allowedLevel) 
	{
		super(msg);
		this.sensitiveValue = sensitiveValue;
		this.patientIdentifier = patientIdentifier;
		this.allowedLevel = allowedLevel;		
	} 
	
	/**
	 * Create a new instance of an InsufficientPatientSensitivityException from an existing one
	 * @param ex
	 * @return
	 */
	public static InsufficientPatientSensitivityException createInsufficientPatientSensitivityException(
			InsufficientPatientSensitivityException ipsX)
	{
		return new InsufficientPatientSensitivityException(ipsX.getMessage(), ipsX.getSensitiveValue(), 
				ipsX.getPatientIdentifier(), ipsX.getAllowedLevel());
	}
	
	/**
	 * 
	 * @param sensitiveValue The actual sensitive value for the patient
	 * @param patientIcn The global patient identifier
	 * @param allowedLevel The sensitive level the user is allowed to view
	 * @return
	 */
	public static InsufficientPatientSensitivityException createInsufficientPatientSensitivityException(
			PatientSensitiveValue sensitiveValue, PatientIdentifier patientIdentifier,
			PatientSensitivityLevel allowedLevel)
	{
		String msg = "Cannot view patient '" + patientIdentifier + "' with allowed SL '" + 
			allowedLevel.getCode() + "', requires level '" + sensitiveValue.getSensitiveLevel().getCode() + "'.";
		return new InsufficientPatientSensitivityException(msg, sensitiveValue, patientIdentifier, allowedLevel);
	}

	/**
	 * Return the actual sensitive value for the patient
	 * 
	 * @return the sensitiveValue
	 */
	public PatientSensitiveValue getSensitiveValue() {
		return sensitiveValue;
	}

	public PatientIdentifier getPatientIdentifier()
	{
		return patientIdentifier;
	}

	/**
	 * Return the sensitive level the user is allowed to access 
	 * 
	 * @return the allowedLevel
	 */
	public PatientSensitivityLevel getAllowedLevel() {
		return allowedLevel;
	}
}
