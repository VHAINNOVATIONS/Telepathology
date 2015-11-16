package gov.va.med.imaging.exchange.business.dicom.importer;

import gov.va.med.imaging.exchange.business.PersistentEntity;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ImagingLocation  implements PersistentEntity
{

    protected int id;
    protected String name;
    protected String creditMethod;
    
    public ImagingLocation() {}
	
    public ImagingLocation(int id, String name, String creditMethod) 
    {
		this.id = id;
		this.name = name;
		this.creditMethod = creditMethod;
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

    public String getCreditMethod() {
		return creditMethod;
	}

	public void setCreditMethod(String creditMethod) {
		this.creditMethod = creditMethod;
	}
}
