package gov.va.med.imaging.core.router.commands;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.BeanUtils;
import gov.va.med.imaging.BusinessKey;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl;
import gov.va.med.imaging.datasource.DurableQueueDataSourceSpi;
import gov.va.med.imaging.exchange.business.DurableQueueMessage;

import java.util.List;

public class GetDurableQueueMessagesCommandImpl
		extends
		AbstractDataSourceCommandImpl<List<DurableQueueMessage>, DurableQueueDataSourceSpi> {
	private static final long serialVersionUID = 2068347295610633454L;
	private static final String SPI_METHOD_NAME = "getMessages";
	private final int queueId;
	private final RoutingToken routingToken;
	private final String messageGroupId;
	private final int startIndex;
	private final int numRecords;

	public GetDurableQueueMessagesCommandImpl(RoutingToken routingToken,
			int queueId, int startIndex, int numRecords) {
		super();
		this.queueId = queueId;
		this.routingToken = routingToken;
		this.messageGroupId = null;
		this.startIndex = startIndex;
		this.numRecords = numRecords;
	}

	public GetDurableQueueMessagesCommandImpl(RoutingToken routingToken,
			int queueId, String messageGroupId, int startIndex,
			int numRecords) {
		super();
		this.queueId = queueId;
		this.routingToken = routingToken;
		this.messageGroupId = messageGroupId;
		this.startIndex = startIndex;
		this.numRecords = numRecords;
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
	protected List<DurableQueueMessage> getCommandResult(
			DurableQueueDataSourceSpi spi) throws ConnectionException,
			MethodException {

		List<DurableQueueMessage> messages;
		if (messageGroupId == null) {
			messages = spi.getMessages(this.getQueueId(), this.getStartIndex(), this.getNumRecords());
		} else {
			messages = spi.getMessages(this.getQueueId(), this.getMessageGroupId(), this.getStartIndex(), this.getNumRecords());
		}
		return messages;
	}

	public String getMessageGroupId() {
		return messageGroupId;
	}

	public int getNumRecords() {
		return numRecords;
	}

	public int getStartIndex() {
		return startIndex;
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
}
