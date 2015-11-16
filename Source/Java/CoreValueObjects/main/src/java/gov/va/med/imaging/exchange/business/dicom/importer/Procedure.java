package gov.va.med.imaging.exchange.business.dicom.importer;

import gov.va.med.imaging.exchange.business.PersistentEntity;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Procedure implements PersistentEntity
{

    private int id;
    private String name;
    private int imagingTypeId;
    private int imagingLocationId;
    private int hospitalLocationId;
    
    private List<ProcedureModifier> procedureModifiers = new ArrayList<ProcedureModifier>(); 

    public Procedure() {}
    public Procedure(String name, int id, int imagingTypeId, int imagingLocationId, int hospitalLocationId) 
    {
		this.name = name;
		this.id = id;
		this.imagingTypeId = imagingTypeId;
		this.imagingLocationId = imagingLocationId;
		this.hospitalLocationId = hospitalLocationId;
	}

    public int getId() 
    {
        return id;
    }
    public void setId(int value) 
    {
        this.id = value;
    }

    public int getImagingTypeId() 
    {
        return imagingTypeId;
    }
    public void setImagingTypeId(int value) 
    {
        this.imagingTypeId = value;
    }

    public String getName() 
    {
        return name;
    }
    public void setName(String value) 
    {
        this.name = value;
    }

	public List<ProcedureModifier> getProcedureModifiers() 
	{
		return procedureModifiers;
	}

	public void setProcedureModifiers(List<ProcedureModifier> procedureModifiers) 
	{
		this.procedureModifiers = procedureModifiers;
	}
	public void setImagingLocationId(int imagingLocationId) {
		this.imagingLocationId = imagingLocationId;
	}
	public int getImagingLocationId() {
		return imagingLocationId;
	}
	
	public int getHospitalLocationId() {
		return hospitalLocationId;
	}
	public void setHospitalLocationId(int hospitalLocationId) {
		this.hospitalLocationId = hospitalLocationId;
	}
    
}
