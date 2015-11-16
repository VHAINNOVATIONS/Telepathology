package gov.va.med.imaging.federation.pathology.rest.types;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PathologyFederationPatientInfoItemType 
{
	
	private String fieldNumber; // M patient file (#2) filed number
	private String fieldValue;  // actual patient data in that field
	
	public PathologyFederationPatientInfoItemType()
	{
		super();
	}

	public PathologyFederationPatientInfoItemType(String fieldNumber,
			String fieldValue) {
		super();
		this.fieldNumber = fieldNumber;
		this.fieldValue = fieldValue;
	}

	public String getFieldNumber() {
		return fieldNumber;
	}

	public void setFieldNumber(String fieldNumber) {
		this.fieldNumber = fieldNumber;
	}

	public String getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}

}
