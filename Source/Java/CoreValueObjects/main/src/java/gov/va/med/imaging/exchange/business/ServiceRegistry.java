package gov.va.med.imaging.exchange.business;

import java.util.ArrayList;
import java.util.List;

public class ServiceRegistry {
	private List<ServiceRegistration> registrations;

	public ServiceRegistry(){
		registrations = new ArrayList<ServiceRegistration>();
	}
	
	public List<ServiceRegistration> getRegistrations() {
		return registrations;
	}

	public void setRegistrations(List<ServiceRegistration> registrations) {
		this.registrations = registrations;
	}
}
