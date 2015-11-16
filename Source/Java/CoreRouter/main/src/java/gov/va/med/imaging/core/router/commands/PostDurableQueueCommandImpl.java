package gov.va.med.imaging.core.router.commands;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.BeanUtils;
import gov.va.med.imaging.BusinessKey;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl;
import gov.va.med.imaging.datasource.DurableQueueDataSourceSpi;
import gov.va.med.imaging.exchange.business.DurableQueue;

public class PostDurableQueueCommandImpl extends
		AbstractDataSourceCommandImpl<Boolean, DurableQueueDataSourceSpi> {
	private static final long serialVersionUID = 4759272349502375615L;
	private static final String SPI_METHOD_NAME = "updateQueue";
	private final DurableQueue queue;
	private final RoutingToken routingToken;

	public PostDurableQueueCommandImpl(RoutingToken routingToken,
			DurableQueue queue) {
		super();
		this.routingToken = routingToken;
		this.queue = queue;
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
	protected Boolean getCommandResult(DurableQueueDataSourceSpi spi)
			throws ConnectionException, MethodException {
		spi.updateQueue(queue);
		return true;
	}

	public DurableQueue getQueue() {
		return queue;
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
		return new Object[] { getQueue() };
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes() {
		return new Class<?>[] { DurableQueue.class };
	}

	@Override
	public int hashCode() {
		return BeanUtils.hashCode(this);
	}

	@Override
	protected String parameterToString() {
		return getQueue().toString();
	}

	@Override
	public String toString() {
		return BeanUtils.toString(this);
	}
}
