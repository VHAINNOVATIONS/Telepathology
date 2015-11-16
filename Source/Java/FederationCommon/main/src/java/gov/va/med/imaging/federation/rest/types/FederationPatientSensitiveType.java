package gov.va.med.imaging.federation.rest.types;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FederationPatientSensitiveType 
{
	private String warningMessage;
	private FederationPatientSensitivityLevelType sensitiveLevel;
	
	public FederationPatientSensitiveType()
	{
		
	}

	public String getWarningMessage() {
		return warningMessage;
	}

	public void setWarningMessage(String warningMessage) {
		this.warningMessage = warningMessage;
	}

	public FederationPatientSensitivityLevelType getSensitiveLevel() {
		return sensitiveLevel;
	}

	public void setSensitiveLevel(
			FederationPatientSensitivityLevelType sensitiveLevel) {
		this.sensitiveLevel = sensitiveLevel;
	}
}
