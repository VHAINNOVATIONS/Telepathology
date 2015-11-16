package gov.va.med.imaging.exchange.business;

public class WorkList implements PersistentEntity
{
	private int id;
	private String description;
	
	
	public WorkList() 
	{
	}	
	public WorkList(int id, String description) 
	{
		this.id = id;
		this.description = description;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
