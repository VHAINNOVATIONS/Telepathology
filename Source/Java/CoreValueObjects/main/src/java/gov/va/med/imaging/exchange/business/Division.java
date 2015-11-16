package gov.va.med.imaging.exchange.business;

public class Division
{
	private String divisionIen;
	private String divisionCode;
	private String divisionName;
	
	public Division(String divisionIen, String divisionName, String divisionCode) 
	{
		this.divisionIen = divisionIen;
		this.divisionCode = divisionCode;
		this.divisionName = divisionName;
	}
	
	
	public void setDivisionIen(String divisionIen) {
		this.divisionIen = divisionIen;
	}
	public String getDivisionIen() {
		return divisionIen;
	}

	public void setDivisionCode(String divisionCode) {
		this.divisionCode = divisionCode;
	}
	public String getDivisionCode() {
		return divisionCode;
	}

	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
	}
	public String getDivisionName() {
		return divisionName;
	}

}
