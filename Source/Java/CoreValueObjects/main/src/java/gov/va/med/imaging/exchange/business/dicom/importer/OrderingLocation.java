package gov.va.med.imaging.exchange.business.dicom.importer;

import gov.va.med.imaging.exchange.business.PersistentEntity;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class OrderingLocation  implements PersistentEntity
{

    protected int id;
    protected String name;
    
    public OrderingLocation() {}
	
    public OrderingLocation(int id, String name) {
		this.id = id;
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

}
