package gov.va.med.imaging.exchange.business.dicom;

import gov.va.med.imaging.exchange.business.PersistentEntity;

public class DicomUid implements PersistentEntity
{
	public DicomUid(String type, String value) {
		super();
		this.setType(type);
		this.setValue(value);
	}
	private String type;
	private String value;
	
	@Override
	public void setId(int id) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
