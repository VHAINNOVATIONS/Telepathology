package gov.va.med.imaging.exchange.business.storage;

public class Key 
{
	private int level;
	private String value;
	
	public Key(int level, String value) {
		this.level = level;
		this.value = value;
	}
	
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
