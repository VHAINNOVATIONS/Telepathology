package gov.va.med.imaging.core.router.commands;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.BeanUtils;
import gov.va.med.imaging.BusinessKey;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl;
import gov.va.med.imaging.datasource.DurableQueueDataSourceSpi;
import gov.va.med.imaging.datasource.VeinsDataSourceSpi;
import gov.va.med.imaging.exchange.business.DurableQueueMessage;
import gov.va.med.imaging.veins.ErrorTypeNotificationConfiguration;

import java.util.List;

public class GetAllErrorTypeNotificationConfigurationCommandImpl
		extends
		AbstractDataSourceCommandImpl<List<ErrorTypeNotificationConfiguration>, VeinsDataSourceSpi> {
	private static final long serialVersionUID = 1239846162093L;
	private static final String SPI_METHOD_NAME = "findAllErrorTypeNotificationConfiguration";
	private final RoutingToken routingToken;

	public GetAllErrorTypeNotificationConfigurationCommandImpl(RoutingToken routingToken) {
		super();
		this.routingToken = routingToken;
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
	protected List<ErrorTypeNotificationConfiguration> getCommandResult(VeinsDataSourceSpi spi)
			throws ConnectionException, MethodException {
		return spi.findAllErrorTypeNotificationConfiguration();
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
		return new Object[] { };
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes() {
		return new Class<?>[] { };
	}

	@Override
	public int hashCode() {
		return BeanUtils.hashCode(this);
	}

	@Override
	protected String parameterToString() {
		return "";
	}

	@Override
	public String toString() {
		return BeanUtils.toString(this);
	}
}
