package gov.va.med.imaging.core.router.commands;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl;
import gov.va.med.imaging.datasource.ServiceRegistrationDataSourceSpi;
import gov.va.med.imaging.exchange.business.ServiceRegistration;

public class GetServiceRegistrationCommandImpl
		extends
		AbstractDataSourceCommandImpl<ServiceRegistration, ServiceRegistrationDataSourceSpi> {

	private static final long serialVersionUID = 8347193447213L;
	private static final String SPI_METHOD_NAME = "getById";
	private RoutingToken routingToken;
	private final int id;

	public GetServiceRegistrationCommandImpl(RoutingToken routingToken, int id) {
		super();
		this.id = id;
		this.routingToken = routingToken;
	}

	@Override
	public boolean equals(Object obj) {
		return false;
	}

	@Override
	protected ServiceRegistration getCommandResult(
			ServiceRegistrationDataSourceSpi spi) throws ConnectionException,
			MethodException {
		return spi.getById(this.getId());
	}

	public RoutingToken getRoutingToken() {
		return routingToken;
	}

	public int getId() {
		return id;
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
		return new Object[] { getId() };
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes() {
		return new Class<?>[] { int.class };
	}
}
