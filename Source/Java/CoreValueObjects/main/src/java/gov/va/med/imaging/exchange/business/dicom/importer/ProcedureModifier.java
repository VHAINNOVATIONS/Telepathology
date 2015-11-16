package gov.va.med.imaging.exchange.business.dicom.importer;

import gov.va.med.imaging.exchange.business.PersistentEntity;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ProcedureModifier  implements PersistentEntity
{

    private int id;
    private int imagingTypeId;
    private String name;

    public ProcedureModifier() {}

    public ProcedureModifier(int id, int imagingTypeId, String name) 
    {
		this.id = id;
		this.imagingTypeId = imagingTypeId;
		this.name = name;
	}

	public int getId() 
    {
        return id;
    }

    public void setId(int value) 
    {
        this.id = value;
    }


    public String getName() 
    {
        return name;
    }

    public void setName(String value) 
    {
        this.name = value;
    }

	public int getImagingTypeId() 
	{
		return imagingTypeId;
	}

	public void setImagingTypeId(int imagingTypeId) 
	{
		this.imagingTypeId = imagingTypeId;
	}
    
    

}
