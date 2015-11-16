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
package gov.va.med.imaging.exchange.business;

import java.io.Serializable;
import gov.va.med.imaging.exchange.enums.PatientSensitivityLevel;

/**
 * @author vhaiswwerfej
 *
 */
public class PatientSensitiveValue
implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private final String warningMessage;
	private final PatientSensitivityLevel sensitiveLevel;
	
	public PatientSensitiveValue(PatientSensitivityLevel sensitiveLevel, String warningMessage)
	{
		this.sensitiveLevel = sensitiveLevel;
		this.warningMessage = warningMessage;
	}

	/**
	 * @return the warningMessage
	 */
	public String getWarningMessage() {
		return warningMessage;
	}

	/**
	 * @return the sensitiveLevel
	 */
	public PatientSensitivityLevel getSensitiveLevel() {
		return sensitiveLevel;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() 
	{
		StringBuilder sb = new StringBuilder();
		sb.append(sensitiveLevel);
		if((warningMessage != null) && (warningMessage.length() > 0))
		{
			sb.append("\n");
			sb.append(warningMessage);
		}
		return sb.toString();
	}
}
