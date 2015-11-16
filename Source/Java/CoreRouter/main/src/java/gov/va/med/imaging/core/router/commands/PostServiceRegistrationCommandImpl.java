package gov.va.med.imaging.core.router.commands;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl;
import gov.va.med.imaging.datasource.ServiceRegistrationDataSourceSpi;
import gov.va.med.imaging.exchange.business.ServiceRegistration;

public class PostServiceRegistrationCommandImpl
		extends
		AbstractDataSourceCommandImpl<Boolean, ServiceRegistrationDataSourceSpi> {

	private static final long serialVersionUID = 8347193447213L;
	private static final String SPI_METHOD_NAME = "create";
	private RoutingToken routingToken;
	private final ServiceRegistration registration;

	public PostServiceRegistrationCommandImpl(RoutingToken routingToken,
			ServiceRegistration registration) {
		super();
		this.registration = registration;
		this.routingToken = routingToken;
	}

	@Override
	public boolean equals(Object obj) {
		return false;
	}

	@Override
	protected Boolean getCommandResult(ServiceRegistrationDataSourceSpi spi)
			throws ConnectionException, MethodException {
		spi.create(this.getServiceRegistration());
		return true;
	}

	public RoutingToken getRoutingToken() {
		return routingToken;
	}

	public ServiceRegistration getServiceRegistration() {
		return registration;
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
		return new Object[] { getServiceRegistration() };
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes() {
		return new Class<?>[] { ServiceRegistration.class };
	}
}
