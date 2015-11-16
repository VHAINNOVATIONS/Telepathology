/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 19, 2010
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
package gov.va.med.imaging.rest.exceptions;

import javax.xml.bind.annotation.XmlRootElement;

import gov.va.med.imaging.core.interfaces.exceptions.InsufficientPatientSensitivityException;

/**
 * Exception message contents for an InsufficientPatientSensitivityException
 * 
 * Note: This exception Message CANNOT extend RestExceptionMessage because that causes a problem in the XML marshaling
 * 
 * @author vhaiswwerfej
 *
 */
@XmlRootElement
public class RestInsufficientPatientSensitivityExceptionMessage 
{
	private int sensitiveErrorCode;
	private String patientIcn;
	private int allowedLevelCode;
	private String message;
	
	public RestInsufficientPatientSensitivityExceptionMessage()
	{
		super();
	}
	
	public RestInsufficientPatientSensitivityExceptionMessage(InsufficientPatientSensitivityException exception)
	{
		super();
		this.message =exception.getSensitiveValue().getWarningMessage(); 
		this.sensitiveErrorCode = exception.getSensitiveValue().getSensitiveLevel().getCode();
		// can't change the name of the field to match the patient identifier since that would cause issues with other servers
		this.patientIcn = exception.getPatientIdentifier().toString();
		this.allowedLevelCode = exception.getAllowedLevel().getCode();
	}

	public int getSensitiveErrorCode() {
		return sensitiveErrorCode;
	}

	public void setSensitiveErrorCode(int sensitiveErrorCode) {
		this.sensitiveErrorCode = sensitiveErrorCode;
	}

	public String getPatientIcn() {
		return patientIcn;
	}

	public void setPatientIcn(String patientIcn) {
		this.patientIcn = patientIcn;
	}

	public int getAllowedLevelCode() {
		return allowedLevelCode;
	}

	public void setAllowedLevelCode(int allowedLevelCode) {
		this.allowedLevelCode = allowedLevelCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
