package gov.va.med.imaging.exchange.business.dicom.importer;

import gov.va.med.imaging.exchange.business.PersistentEntity;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class StandardReport  implements PersistentEntity
{

    protected int id;
    protected String reportName;
    protected String reportText;
    protected String impression;
    
    public StandardReport(int id, String reportName,
			String reportText, String impression) 
    {
		super();
		this.id = id;
		this.reportName = reportName;
		this.reportText = reportText;
		this.impression = impression;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getReportText() {
		return reportText;
	}

	public void setReportText(String reportText) {
		this.reportText = reportText;
	}

	public String getImpression() {
		return impression;
	}

	public void setImpression(String impression) {
		this.impression = impression;
	}

	public StandardReport() {}
	
}
