/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Apr 11, 2008
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author VHAISWBECKEC
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */
package gov.va.med.imaging.exchange.business.taglib.patient;

import javax.servlet.jsp.JspException;

/**
 * @author VHAISWBECKEC
 *
 */
public class PatientIcn 
extends AbstractPatientPropertyTag
{
	private static final long serialVersionUID = 1L;
	private Boolean removeChecksum = Boolean.FALSE;

	/**
	 * @see gov.va.med.imaging.exchange.business.taglib.patient.AbstractPatientPropertyTag#getElementValue()
	 */
	@Override
	protected String getElementValue() 
	throws JspException
	{
		String patientIcn = getPatient().getPatientIcn();
		if(getRemoveChecksum().booleanValue() )
			return PatientIcn.removePatientIcnChecksum(patientIcn);
		return patientIcn;
	}

	public Boolean getRemoveChecksum()
    {
    	return removeChecksum;
    }

	public void setRemoveChecksum(Boolean removeChecksum)
    {
    	this.removeChecksum = removeChecksum;
    }
	
	/**
	 * A static method, accessible from JSP, that removes the checksum from a
	 * patient ICN.
	 * 
	 * @param patientIcn
	 * @return
	 */
	public static String removePatientIcnChecksum(String patientIcn)
	{
		int checksumDelimiterIndex = patientIcn.indexOf('V'); 
		if( checksumDelimiterIndex > 0 )
			patientIcn = patientIcn.substring(0, patientIcn.indexOf('V'));
		return patientIcn;
	}
}
