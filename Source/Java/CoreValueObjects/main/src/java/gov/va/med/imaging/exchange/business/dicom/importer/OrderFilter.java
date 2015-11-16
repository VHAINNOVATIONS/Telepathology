package gov.va.med.imaging.exchange.business.dicom.importer;


public class OrderFilter 
{
	private String dfn;
	private String icn;
	private String orderType;
	private String startDate;
	private String endDate;
	private String siteId;
	
	public String getDfn() {
		return dfn;
	}
	public void setDfn(String dfn) {
		this.dfn = dfn;
	}
	public String getIcn() {
		return icn;
	}
	public void setIcn(String icn) {
		this.icn = icn;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}
	public String getSiteId() {
		return siteId;
	}
}
