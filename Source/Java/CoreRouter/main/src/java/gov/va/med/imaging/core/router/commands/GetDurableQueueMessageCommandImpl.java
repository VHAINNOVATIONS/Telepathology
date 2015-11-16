package gov.va.med.imaging.core.router.commands;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.BeanUtils;
import gov.va.med.imaging.BusinessKey;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl;
import gov.va.med.imaging.exchange.business.DurableQueueMessage;
import gov.va.med.imaging.datasource.DurableQueueDataSourceSpi;

public class GetDurableQueueMessageCommandImpl
		extends
		AbstractDataSourceCommandImpl<DurableQueueMessage, DurableQueueDataSourceSpi> {
	private static final long serialVersionUID = 4273598234723484751L;
	private static final String SPI_METHOD_NAME = "peek";
	private final int queueId;
	private final RoutingToken routingToken;
	private final String messageGroupId;

	public GetDurableQueueMessageCommandImpl(RoutingToken routingToken,
			int queueId) {
		super();
		this.queueId = queueId;
		this.routingToken = routingToken;
		this.messageGroupId = null;
	}

	public GetDurableQueueMessageCommandImpl(RoutingToken routingToken,
			int queueId, String messageGroupId) {
		super();
		this.queueId = queueId;
		this.routingToken = routingToken;
		this.messageGroupId = messageGroupId;
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
	protected DurableQueueMessage getCommandResult(DurableQueueDataSourceSpi spi)
			throws ConnectionException, MethodException {

		DurableQueueMessage message;
		if (messageGroupId == null) {
			message = spi.peek(queueId);
		} else {
			message = spi.peek(queueId, messageGroupId);
		}
		getLogger().info(
				"Peeked message ["
						+ (message == null ? "null" : Integer.toString(message
								.getId())));
		return message;
	}

	@BusinessKey
	public int getQueueId() {
		return this.queueId;
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
		return new Object[] { getQueueId() };
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes() {
		if (messageGroupId == null) {
			return new Class<?>[] { int.class };
		} else {
			return new Class<?>[] { int.class, String.class };
		}
	}

	@Override
	public int hashCode() {
		return BeanUtils.hashCode(this);
	}

	@Override
	protected String parameterToString() {
		return Integer.toString(getQueueId());
	}

	@Override
	public String toString() {
		return BeanUtils.toString(this);
	}

	public String getMessageGroupId() {
		return messageGroupId;
	}
}
