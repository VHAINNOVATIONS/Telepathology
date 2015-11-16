package gov.va.med.imaging.exchange.business.dicom.importer;

import gov.va.med.imaging.exchange.business.PersistentEntity;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DiagnosticCode  implements PersistentEntity
{

    protected int id;
    protected String name;
    protected String description;
    
    public DiagnosticCode() {}
	
    public DiagnosticCode(int id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}
	
    public String getName() 
    {
        return name;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getDescription() 
    {
        return description;
    }
    public void setDescription(String value) 
    {
        this.description = value;
    }

	@Override
	public void setId(int id) 
	{
		this.id = id;
	}

	@Override
	public int getId() 
	{
		return this.id;
	}

}
