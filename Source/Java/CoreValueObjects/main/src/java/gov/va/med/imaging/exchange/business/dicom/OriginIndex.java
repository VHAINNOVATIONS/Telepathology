package gov.va.med.imaging.exchange.business.dicom;

import gov.va.med.imaging.exchange.business.PersistentEntity;

public class OriginIndex implements PersistentEntity
{
	public OriginIndex(String code, String description) {
		super();
		this.code = code;
		this.description = description;
	}
	private String code;
	private String description;
	
	@Override
	public void setId(int id) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setCode(String code) {
		this.code = code;
	}
	public String getCode() {
		return code;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDescription() {
		return description;
	}
}
