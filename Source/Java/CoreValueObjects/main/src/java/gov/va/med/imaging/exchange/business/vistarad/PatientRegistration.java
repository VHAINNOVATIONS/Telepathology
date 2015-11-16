package gov.va.med.imaging.exchange.business.vistarad;

import gov.va.med.MockDataGenerationField;

public class PatientRegistration
{
	@MockDataGenerationField(pattern=MockDataGenerationField.ICN_PATTERN)
	private String patientIcn;
	
	@MockDataGenerationField(pattern=MockDataGenerationField.CPT_PATTERN)
	private String cptCode;
	
	public String getPatientIcn()
	{
		return patientIcn;
	}
	public void setPatientIcn(String patientIcn)
	{
		this.patientIcn = patientIcn;
	}
	public String getCptCode()
	{
		return cptCode;
	}
	public void setCptCode(String cptCode)
	{
		this.cptCode = cptCode;
	}
}
