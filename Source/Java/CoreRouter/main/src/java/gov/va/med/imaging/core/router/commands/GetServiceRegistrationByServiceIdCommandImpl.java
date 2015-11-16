package gov.va.med.imaging.core.router.commands;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl;
import gov.va.med.imaging.datasource.ServiceRegistrationDataSourceSpi;
import gov.va.med.imaging.exchange.business.ServiceRegistration;

import java.util.List;

public class GetServiceRegistrationByServiceIdCommandImpl
		extends
		AbstractDataSourceCommandImpl<List<ServiceRegistration>, ServiceRegistrationDataSourceSpi> {

	private static final long serialVersionUID = 8347193447213L;
	private static final String SPI_METHOD_NAME = "getByServiceId";
	private RoutingToken routingToken;
	private final String serviceId;

	public GetServiceRegistrationByServiceIdCommandImpl(RoutingToken routingToken, String serviceId) {
		super();
		this.serviceId = serviceId;
		this.routingToken = routingToken;
	}

	@Override
	public boolean equals(Object obj) {
		return false;
	}
	
	@Override
	protected List<ServiceRegistration> getCommandResult(ServiceRegistrationDataSourceSpi spi)
			throws ConnectionException, MethodException {
		return spi.getByServiceId(this.getServiceId());
	}
	
	public RoutingToken getRoutingToken() {
		return routingToken;
	}

	public String getServiceId() {
		return serviceId;
	}

	public String getSiteNumber() {
		return getRoutingToken().getRepositoryUniqueId();
	}

	@Override
	protected Class<ServiceRegistrationDataSourceSpi> getSpiClass() {
		return ServiceRegistrationDataSourceSpi.class;
	}

	@Override
	protected String getSpiMethodName() {
		return SPI_METHOD_NAME;
	}

	@Override
	protected Object[] getSpiMethodParameters() {
		return new Object[] { getServiceId() };
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes() {
		return new Class<?>[] { String.class };
	}
}
