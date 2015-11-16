package gov.va.med.imaging.core.router.commands;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.BeanUtils;
import gov.va.med.imaging.BusinessKey;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl;
import gov.va.med.imaging.exchange.business.DurableQueueMessage;
import gov.va.med.imaging.datasource.DurableQueueDataSourceSpi;

public class PostMoveDurableQueueMessageCommandImpl extends
		AbstractDataSourceCommandImpl<Boolean, DurableQueueDataSourceSpi> {
	private static final long serialVersionUID = 4273598234723484751L;
	private static final String SPI_METHOD_NAME = "moveMessage";
	private final int messageId;
	private final int targetQueueId;
	private final RoutingToken routingToken;

	public PostMoveDurableQueueMessageCommandImpl(RoutingToken routingToken,
			int messageId, int targetQueueId) {
		super();
		this.routingToken = routingToken;
		this.messageId = messageId;
		this.targetQueueId = targetQueueId;
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
		spi.moveMessage(getMessageId(), getTargetQueueId());
		return true;
	}

	public int getMessageId() {
		return messageId;
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
		return new Object[] { getMessageId(), getTargetQueueId() };
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes() {
		return new Class<?>[] { int.class, int.class };
	}

	@BusinessKey
	public int getTargetQueueId() {
		return this.targetQueueId;
	}

	@Override
	public int hashCode() {
		return BeanUtils.hashCode(this);
	}

	@Override
	protected String parameterToString() {
		return Integer.toString(getTargetQueueId());
	}

	@Override
	public String toString() {
		return BeanUtils.toString(this);
	}
}
