package gov.va.med.imaging.exchange.business.dicom.importer;

public class ReportParameters 
{
	private String reportTypeCode;
	private String reportStyleCode;
	private String startDate;
	private String endDate;
	
	public void setReportTypeCode(String reportTypeCode) 
	{
		this.reportTypeCode = reportTypeCode;
	}
	public String getReportTypeCode() 
	{
		return reportTypeCode;
	}
	
	public void setReportStyleCode(String reportStyleCode) 
	{
		this.reportStyleCode = reportStyleCode;
	}
	public String getReportStyleCode() 
	{
		return reportStyleCode;
	}
	
	public void setStartDate(String startDate) 
	{
		this.startDate = startDate;
	}
	public String getStartDate() 
	{
		return startDate;
	}
	
	public void setEndDate(String endDate) 
	{
		this.endDate = endDate;
	}
	public String getEndDate() 
	{
		return endDate;
	}

}
