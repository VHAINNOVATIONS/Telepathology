package gov.va.med.imaging.exchange.business;

import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.storage.Place;
import gov.va.med.imaging.exchange.business.storage.StorageServerDatabaseConfiguration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

public class ServiceRegistration implements PersistentEntity {
	private int id;
	private String ipAddress;
	private Date lastStatusReceivedDateTime;
	private String lastStatusReceivedDateTimeString;
	private Place place;
	private int placeId;
	private Date registrationDateTime;
	private String registrationDateTimeString;
	private String serverName;
	private String serviceData;
	private String serviceId;
	private String serviceVersion;
	private ServiceStatus status;
	private int statusId;
	private String url;
	
	public ServiceRegistration(String serviceId, String version, ServiceStatus status, String siteNumber) throws MethodException {
		InetAddress ipAddress = null;
		try {
			ipAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException uhe) {
			throw new MethodException(uhe);
		}

		setIpAddress(ipAddress.getHostAddress());
		setServerName(ipAddress.getHostName());
		setPlace(StorageServerDatabaseConfiguration.getConfiguration()
				.getPlace(siteNumber));
		setServiceId(serviceId);
		setServiceVersion(version);
		setStatus(status);
	}

	public ServiceRegistration(String serviceId, String version, ServiceStatus status, String serviceData, String siteNumber) throws MethodException {
		this(serviceId, version, status, siteNumber);
		this.setServiceData(serviceData);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ServiceRegistration other = (ServiceRegistration) obj;
		if (id > 0 && other.id > 0 && id != other.id)
			return false;
		if (placeId == 0) {
			if (other.placeId != 0)
				return false;
		} else if (placeId != other.placeId)
			return false;
		if (serverName == null) {
			if (other.serverName != null)
				return false;
		} else if (!serverName.equals(other.serverName))
			return false;
		if (serviceId == null) {
			if (other.serviceId != null)
				return false;
		} else if (!serviceId.equals(other.serviceId))
			return false;
		if (serviceVersion == null) {
			if (other.serviceVersion != null)
				return false;
		} else if (!serviceVersion.equals(other.serviceVersion))
			return false;
		return true;
	}

	public int getId() {
		return id;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public Date getLastStatusReceivedDateTime() {
		return lastStatusReceivedDateTime;
	}

	public String getLastStatusReceivedDateTimeString() {
		return lastStatusReceivedDateTimeString;
	}

	public Place getPlace() {
		return place;
	}

	public int getPlaceId() {
		return placeId;
	}

	public Date getRegistrationDateTime() {
		return registrationDateTime;
	}

	public String getRegistrationDateTimeString() {
		return registrationDateTimeString;
	}

	public String getServerName() {
		return serverName;
	}

	public String getServiceData() {
		return serviceData;
	}

	public String getServiceId() {
		return serviceId;
	}

	public String getServiceVersion() {
		return serviceVersion;
	}

	public ServiceStatus getStatus() {
		return status;
	}

	public int getStatusId() {
		return statusId;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result
				+ ((ipAddress == null) ? 0 : ipAddress.hashCode());
		result = prime * result + ((place == null) ? 0 : place.hashCode());
		result = prime * result
				+ ((serverName == null) ? 0 : serverName.hashCode());
		result = prime * result
				+ ((serviceId == null) ? 0 : serviceId.hashCode());
		result = prime * result
				+ ((serviceVersion == null) ? 0 : serviceVersion.hashCode());
		return result;
	}

	public void setId(int ien) {
		this.id = ien;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public void setLastStatusReceivedDateTime(Date lastStatusReceivedDateTime) {
		this.lastStatusReceivedDateTime = lastStatusReceivedDateTime;
	}

	public void setLastStatusReceivedDateTimeString(
			String lastStatusReceivedDateTimeString) {
		this.lastStatusReceivedDateTimeString = lastStatusReceivedDateTimeString;
	}

	public void setPlace(Place place) {
		this.place = place;
		setPlaceId(place.getId());
	}

	public void setPlaceId(int placeId) {
		this.placeId = placeId;
	}

	public void setRegistrationDateTime(Date registrationDateTime) {
		this.registrationDateTime = registrationDateTime;
	}

	public void setRegistrationDateTimeString(String registrationDateTimeString) {
		this.registrationDateTimeString = registrationDateTimeString;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public void setServiceData(String serviceData) {
		this.serviceData = serviceData;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public void setServiceVersion(String serviceVersion) {
		this.serviceVersion = serviceVersion;
	}

	public void setStatus(ServiceStatus status) {
		this.status = status;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
