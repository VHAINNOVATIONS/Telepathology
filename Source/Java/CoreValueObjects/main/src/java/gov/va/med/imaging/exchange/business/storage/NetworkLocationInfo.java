package gov.va.med.imaging.exchange.business.storage;

import gov.va.med.imaging.core.interfaces.StorageCredentials;
import gov.va.med.imaging.exchange.business.PersistentEntity;

import java.io.Serializable;

public class NetworkLocationInfo implements PersistentEntity, Serializable, StorageCredentials
{

	private static final long serialVersionUID = 1L;

	private int id;
	private String writeLocationIEN;
	private String physicalPath;
	private String username;
	private String password;
	private int placeId;
	private Place place;
	
	public NetworkLocationInfo(String writeLocationIEN,
			String physicalPath) 
	{
		this.writeLocationIEN = writeLocationIEN;
		this.physicalPath = physicalPath;
	}

	public NetworkLocationInfo(String writeLocationIEN,
			Place place,
			String physicalPath,
			String username,
			String password)
	{
		this(writeLocationIEN, physicalPath);
		this.setPlace(place);
		this.setUsername(username);
		this.setPassword(password);
	}

	public int getId() {
		return id;
	}

	public String getNetworkLocationIEN() {
		return writeLocationIEN;
	}

	public String getPassword() {
		return password;
	}
	
	public String getPhysicalPath() {
		return physicalPath;
	}

	public Place getPlace() {
		return place;
	}

	public int getPlaceId() {
		return placeId;
	}

	public String getUsername() {
		return username;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPlace(Place place) {
		this.place = place;
		if(place != null){
			this.placeId = place.getId();
		}
	}

	public void setPlaceId(int placeId) {
		this.placeId = placeId;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
