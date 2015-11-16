package gov.va.med.imaging.core.router.commands;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.BeanUtils;
import gov.va.med.imaging.BusinessKey;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl;
import gov.va.med.imaging.exchange.business.DurableQueueMessage;
import gov.va.med.imaging.datasource.DurableQueueDataSourceSpi;

public class GetDurableQueueMessageCountCommandImpl extends
		AbstractDataSourceCommandImpl<Integer, DurableQueueDataSourceSpi> {
	private static final long serialVersionUID = 4273598234723484751L;
	private static final String SPI_METHOD_NAME = "getMessageCount";
	private final int queueId;
	private final RoutingToken routingToken;
	private final String messageGroupId;

	public GetDurableQueueMessageCountCommandImpl(RoutingToken routingToken,
			int queueId) {
		super();
		this.queueId = queueId;
		this.routingToken = routingToken;
		this.messageGroupId = null;
	}

	public GetDurableQueueMessageCountCommandImpl(RoutingToken routingToken,
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
	protected Integer getCommandResult(DurableQueueDataSourceSpi spi)
			throws ConnectionException, MethodException {

		int count;
		if (messageGroupId == null) {
			count = spi.getMessageCount(queueId);
		} else {
			count = spi.getMessageCount(queueId, messageGroupId);
		}
		return count;
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
