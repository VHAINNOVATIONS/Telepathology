package gov.va.med.imaging.exchange.business.dicom.importer;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class Series {

	private String uid;
    private String seriesDate;
    private String modality;
    private String seriesNumber;
    private String facility;
    private String institutionAddress;
    private String seriesDescription;
    
    
	private List<SopInstance> sopInstances = new ArrayList<SopInstance>();
    
    public String getUid() {
        return uid;
    }

    public void setUid(String value) {
        this.uid = value;
    }

    public String getSeriesDate() {
		return seriesDate;
	}

	public void setSeriesDate(String seriesDate) {
		this.seriesDate = seriesDate;
	}

	public String getModality() {
		if (modality == null || modality.trim().equals(""))
		{
			return "Unknown";
		}
		else
		{
			return modality;
		}
	}

	public void setModality(String modality) {
		this.modality = modality;
	}

    public List<SopInstance> getSopInstances() {
        return sopInstances;
    }

    public void setSopInstances(List<SopInstance> value) {
        this.sopInstances = value;
    }

	public void setSeriesNumber(String seriesNumber) {
		this.seriesNumber = seriesNumber;
	}

	public String getSeriesNumber() {
		return seriesNumber;
	}

	public Series() 
	{
		this.sopInstances = new ArrayList<SopInstance>();
	}

	public Series(String uid, String seriesDate, String modality,
			String seriesNumber) {
		this.uid = uid;
		this.seriesDate = seriesDate;
		this.modality = modality;
		this.seriesNumber = seriesNumber;
		this.sopInstances = new ArrayList<SopInstance>();
	}

	public void setFacility(String facility) {
		this.facility = facility;
	}

	public String getFacility() {
		return facility;
	}

	public void setInstitutionAddress(String institutionAddress) {
		this.institutionAddress = institutionAddress;
	}

	public String getInstitutionAddress() {
		return institutionAddress;
	}

	public void setSeriesDescription(String seriesDescription) {
		this.seriesDescription = seriesDescription;
	}

	public String getSeriesDescription() {
		return seriesDescription;
	}

	
	

}
