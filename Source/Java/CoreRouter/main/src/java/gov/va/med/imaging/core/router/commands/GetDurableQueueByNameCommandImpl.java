package gov.va.med.imaging.core.router.commands;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.BeanUtils;
import gov.va.med.imaging.BusinessKey;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl;
import gov.va.med.imaging.exchange.business.DurableQueue;
import gov.va.med.imaging.datasource.DurableQueueDataSourceSpi;

public class GetDurableQueueByNameCommandImpl extends
		AbstractDataSourceCommandImpl<DurableQueue, DurableQueueDataSourceSpi> {
	private static final long serialVersionUID = 9127643675641L;
	private static final String SPI_METHOD_NAME = "getByName";
	private final RoutingToken routingToken;
	private final String queueName;

	public GetDurableQueueByNameCommandImpl(RoutingToken routingToken,
			String queueName) {
		super();
		this.routingToken = routingToken;
		this.queueName = queueName;
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
	protected DurableQueue getCommandResult(DurableQueueDataSourceSpi spi)
			throws ConnectionException, MethodException {
		DurableQueue result = spi.getByName(queueName);
		if (result == null) {
			getLogger().info("GetDurrableQueueByNameCommand result : null");
		} else {
			getLogger().info(
					"GetDurrableQueueByNameCommand result: "
							+ Integer.toString(result.getId()) + ", "
							+ queueName);
		}
		return result;
	}

	public String getQueueName() {
		return queueName;
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
	protected Class<DurableQueueDataSourceSpi> getSpiClass() {
		return DurableQueueDataSourceSpi.class;
	}

	@Override
	protected String getSpiMethodName() {
		return SPI_METHOD_NAME;
	}

	@Override
	protected Object[] getSpiMethodParameters() {
		return new Object[] { getQueueName() };
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes() {
		return new Class<?>[] { String.class };
	}

	@Override
	public int hashCode() {
		return BeanUtils.hashCode(this);
	}

	@Override
	protected String parameterToString() {
		return getQueueName();
	}

	@Override
	public String toString() {
		return BeanUtils.toString(this);
	}
}
