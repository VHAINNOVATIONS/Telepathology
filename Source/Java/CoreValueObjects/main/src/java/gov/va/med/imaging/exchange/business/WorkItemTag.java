package gov.va.med.imaging.exchange.business;

public class WorkItemTag {
	private String key;
	private String value;
	
	public WorkItemTag(String key, String value) 
	{
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

}
