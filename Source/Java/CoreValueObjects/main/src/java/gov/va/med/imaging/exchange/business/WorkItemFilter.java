package gov.va.med.imaging.exchange.business;

public class WorkItemFilter {
	private String type;
	private String subtype = "";
	private String status = "";
	private String placeId = "";
	private String itemPriority = "";
	private String shortCircuitTagName = "";
	private String maximumNumberOfItemsToReturn = "";
	
	private WorkItemTags tags = new WorkItemTags();

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSubtype() {
		return subtype;
	}
	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPlaceId() {
		return placeId;
	}
	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}
	public String getItemPriority() {
		return itemPriority;
	}
	public void setItemPriority(String itemPriority) {
		this.itemPriority = itemPriority;
	}
	public WorkItemTags getTags() {
		return tags;
	}
	public void setTags(WorkItemTags tags) {
		this.tags = tags;
	}
	public void setShortCircuitTagName(String shortCircuitTagName) {
		this.shortCircuitTagName = shortCircuitTagName;
	}
	public String getShortCircuitTagName() {
		return shortCircuitTagName;
	}
	public void setMaximumNumberOfItemsToReturn(
			String maximumNumberOfItemsToReturn) {
		this.maximumNumberOfItemsToReturn = maximumNumberOfItemsToReturn;
	}
	public String getMaximumNumberOfItemsToReturn() {
		return maximumNumberOfItemsToReturn;
	}	
}
