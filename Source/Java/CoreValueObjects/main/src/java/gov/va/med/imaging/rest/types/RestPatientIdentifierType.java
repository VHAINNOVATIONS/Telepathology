package gov.va.med.imaging.rest.types;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RestPatientIdentifierType
{
	private String value;
	private String type;
	
	public RestPatientIdentifierType()
	{
		super();
	}
	
	public RestPatientIdentifierType(String value, String type)
	{
		super();
		this.value = value;
		this.type = type;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

}
