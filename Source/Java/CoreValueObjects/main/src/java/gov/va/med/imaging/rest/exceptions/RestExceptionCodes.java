/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 16, 2010
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

/**
 * Exception codes that correspond to VIX exceptions for used in REST interfaces
 * 
 * @author vhaiswwerfej
 *
 */
public class RestExceptionCodes 
{
	/**
	 * Error code for a MethodException
	 */
	public final static int restMethodExceptionCode = 600;
	/**
	 * Error code for a ConnectionException
	 */
	public final static int restConnectionExceptionCode = 601;
	/**
	 * Error code for an InvalidSecurityCredentialsException
	 */
	public final static int restInvalidSecurityCredentialsExceptionCode = 602;
	/**
	 * Error code for an InsufficientPatientSensitivityException
	 */
	public final static int restInsufficientPatientSensitivityCode = 603;
	
	/**
	 * Error code for a PatientNotFoundException
	 */
	public final static int restPatientNotFoundExceptionCode = 604;

}
