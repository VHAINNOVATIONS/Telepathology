package gov.va.med.imaging.exchange.business;

public class WorkItem implements PersistentEntity
{
	private int id;
	private String type;
	private String subtype;
	private String status;
	private String placeId;
	private int priority;
	private String message;
	private String createdDate;
	private String creatingUser = "";
	private String creatingUserDisplayName = "";
	private String creatingApplication = "";
	private String lastUpdateDate;
	private String updatingUser = "";
	private String updatingUserDisplayName = "";
	private String updatingApplication = "";
	private WorkItemTags tags;
	
	public WorkItem() 
	{
		tags = new WorkItemTags();
	}

	public WorkItem(String type, String subtype, String status, String placeId,
			int priority, String message, String creatingUser, String creatingApplication, 
			WorkItemTags tags) 
	{
		this.type = type;
		this.subtype = subtype;
		this.status = status;
		this.placeId = placeId;
		this.priority = priority;
		this.message = message;
		this.creatingUser = creatingUser;
		this.creatingApplication = creatingApplication;
		this.tags = tags;
	}

	public WorkItem(String type, String subtype, String status, String placeId,
			String message, String creatingUser, String creatingApplication) 
	{
		this.type = type;
		this.subtype = subtype;
		this.status = status;
		this.placeId = placeId;
		this.message = message;
		this.creatingUser = creatingUser;
		this.creatingApplication = creatingApplication;
		tags = new WorkItemTags();
	}

	public WorkItem(String type, String subtype, String status, String placeId, 
			String creatingUser, String creatingApplication) 
	{
		this.type = type;
		this.subtype = subtype;
		this.status = status;
		this.placeId = placeId;
		this.creatingUser = creatingUser;
		this.creatingApplication = creatingApplication;
		tags = new WorkItemTags();
	}
	
	public void addTag(String key, String value)
	{
		if (getTags() == null)
		{
			setTags(new WorkItemTags());
		}
		
		getTags().addTag(key, value);
		
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSubtype() {
		return subtype + "";
	}
	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}
	public String getStatus() {
		return status + "";
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPlaceId() {
		return placeId + "";
	}
	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public String getMessage() {
		return message + "";
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getCreatingUser() {
		return creatingUser + "";
	}
	public void setCreatingUser(String creatingUser) {
		this.creatingUser = creatingUser;
	}
	public String getCreatingApplication() {
		return creatingApplication + "";
	}
	public void setCreatingApplication(String creatingApplication) {
		this.creatingApplication = creatingApplication;
	}
	public String getLastUpdateDate() {
		return lastUpdateDate;
	}
	public void setLastUpdateDate(String lastUpdateDate) {
		this.lastUpdateDate= lastUpdateDate;
	}
	public String getUpdatingUser() {
		return updatingUser + "";
	}
	public void setUpdatingUser(String updatingUser) {
		this.updatingUser = updatingUser;
	}
	public String getUpdatingApplication() {
		return updatingApplication + "";
	}
	public void setUpdatingApplication(String updatingApplication) {
		this.updatingApplication = updatingApplication;
	}
	public WorkItemTags getTags() {
		return tags;
	}
	public void setTags(WorkItemTags tags) {
		this.tags = tags;
	}

	public void setCreatingUserDisplayName(String creatingUserDisplayName) {
		this.creatingUserDisplayName = creatingUserDisplayName;
	}

	public String getCreatingUserDisplayName() {
		return creatingUserDisplayName;
	}

	public void setUpdatingUserDisplayName(String updatingUserDisplayName) {
		this.updatingUserDisplayName = updatingUserDisplayName;
	}

	public String getUpdatingUserDisplayName() {
		return updatingUserDisplayName;
	}
	
	

}
