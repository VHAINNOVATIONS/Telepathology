/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 15, 2010
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
package gov.va.med.imaging.federation.rest.types;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author vhaiswwerfej
 *
 */
@XmlRootElement
public class FederationPatientType 
{
	private String patientName;
	private String patientIcn;
	private String veteranStatus;
	private FederationPatientSexType patientSex;
	private Date dob;
	private String ssn;
	private Boolean sensitive;
	
	public FederationPatientType()
	{
		super();
	}

	public FederationPatientType(String patientName, String patientIcn,
			String veteranStatus, FederationPatientSexType patientSex, Date dob,
			String ssn, Boolean sensitive) 
	{
		super();
		this.patientName = patientName;
		this.patientIcn = patientIcn;
		this.veteranStatus = veteranStatus;
		this.patientSex = patientSex;
		this.dob = dob;
		this.ssn = ssn;
		this.sensitive = sensitive;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getPatientIcn() {
		return patientIcn;
	}

	public void setPatientIcn(String patientIcn) {
		this.patientIcn = patientIcn;
	}

	public String getVeteranStatus() {
		return veteranStatus;
	}

	public void setVeteranStatus(String veteranStatus) {
		this.veteranStatus = veteranStatus;
	}

	public FederationPatientSexType getPatientSex() {
		return patientSex;
	}

	public void setPatientSex(FederationPatientSexType patientSex) {
		this.patientSex = patientSex;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public String getSsn()
	{
		return ssn;
	}

	public void setSsn(String ssn)
	{
		this.ssn = ssn;
	}

	public Boolean getSensitive()
	{
		return sensitive;
	}

	public void setSensitive(Boolean sensitive)
	{
		this.sensitive = sensitive;
	}

}
