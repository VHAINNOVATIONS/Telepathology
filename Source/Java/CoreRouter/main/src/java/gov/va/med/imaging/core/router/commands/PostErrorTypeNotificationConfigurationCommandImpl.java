package gov.va.med.imaging.core.router.commands;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.BeanUtils;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl;
import gov.va.med.imaging.datasource.VeinsDataSourceSpi;
import gov.va.med.imaging.veins.ErrorTypeNotificationConfiguration;

public class PostErrorTypeNotificationConfigurationCommandImpl extends
		AbstractDataSourceCommandImpl<Boolean, VeinsDataSourceSpi> {
	private static final long serialVersionUID = 4759272349502375615L;
	private static final String SPI_METHOD_NAME = "updateErrorTypeNotificationConfiguration";
	private final ErrorTypeNotificationConfiguration config;
	private final RoutingToken routingToken;

	public PostErrorTypeNotificationConfigurationCommandImpl(RoutingToken routingToken,
			ErrorTypeNotificationConfiguration config) {
		super();
		this.routingToken = routingToken;
		this.config = config;
	}

	@Override
	public boolean equals(Object obj) {
		return BeanUtils.equals(this, obj);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getCommandResult(gov.va.med.imaging.datasource.VersionableDataSourceSpi)
	 */
	@Override
	protected Boolean getCommandResult(VeinsDataSourceSpi spi)
			throws ConnectionException, MethodException {
		spi.updateErrorTypeNotificationConfiguration(getConfig());
		return true;
	}

	public ErrorTypeNotificationConfiguration getConfig() {
		return config;
	}

	public RoutingToken getRoutingToken() {
		return routingToken;
	}

	/**
	 * @return the siteNumber
	 */
	public String getSiteNumber() {
		return getRoutingToken().getRepositoryUniqueId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiClass()
	 */
	@Override
	protected Class<VeinsDataSourceSpi> getSpiClass() {
		return VeinsDataSourceSpi.class;
	}

	@Override
	protected String getSpiMethodName() {
		return SPI_METHOD_NAME;
	}

	@Override
	protected Object[] getSpiMethodParameters() {
		return new Object[] { getConfig() };
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes() {
		return new Class<?>[] { ErrorTypeNotificationConfiguration.class };
	}

	@Override
	public int hashCode() {
		return BeanUtils.hashCode(this);
	}

	@Override
	protected String parameterToString() {
		return getConfig().toString();
	}

	@Override
	public String toString() {
		return BeanUtils.toString(this);
	}
}
